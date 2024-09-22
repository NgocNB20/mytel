package mm.com.mytelpay.family.business.menu;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.menu.dto.*;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountRoleResDto;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.*;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class MenuServiceImpl extends FamilyBaseBusiness implements MenuService {

    public MenuServiceImpl() {
        logger = LogManager.getLogger(MenuServiceImpl.class);
    }

    public static final int NUMBER_OF_MEAL_SATURDAY = 2;
    public static final int NUMBER_OF_MEAL_DAY = 3;
    public static final int NUMBER_OF_FOOD = 5;

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private AccountRestTemplate accountRestTemplate;

    @Autowired
    private CanteenRepository canteenRepository;

    @Autowired
    private ApplicationSettingRepository applicationSettingRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean create(MenuCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(),
                request.getRequestId(), perRequestContextDto.getBearToken());
        String accountId = accountDTO.getAccountId();

        Set<String> foodIds = new HashSet<>();
        request.getListMeal().forEach(
                u -> {
                    List<String> foodIdsRequest = u.getListFoodId();
                    if (CollectionUtils.isEmpty(foodIdsRequest))
                        throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.invalid.listFood");
                    if (foodIdsRequest.size() > NUMBER_OF_FOOD)
                        throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Menu.CHOOSE_MAXIMUM_FOOD, null);
                    foodIds.addAll(foodIdsRequest);
                }
        );
        checkFoodsExist(request.getRequestId(), foodIds);

        if (Day.ALL.name().equals(request.getDay()))
            checkMealCreatedAllDay(request.getRequestId(), accountId);
        if (!Day.ALL.name().equals(request.getDay()))
            checkMealCreatedOneDay(request.getRequestId(), accountId, Day.valueOf(request.getDay()));

        Set<MealType> mealTypes = request.getListMeal().stream().map(u -> MealType.valueOf(u.getMealType())).collect(Collectors.toSet());
        if (Day.SATURDAY.name().equals(request.getDay()) && (mealTypes.size() != NUMBER_OF_MEAL_SATURDAY || !mealTypes.containsAll(List.of(MealType.BREAKFAST, MealType.LUNCH))))
                {throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Menu.REQUIRE_TWO_MEAL, null);
        }

        if (!Day.SATURDAY.name().equals(request.getDay()) && (mealTypes.size() != NUMBER_OF_MEAL_DAY))
                {throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Menu.REQUIRE_THREE_MEAL, null);
        }

        List<String> canteenIds = new ArrayList<>();
        canteenIds.add(accountDTO.getCanteenId());

        Map<MealType, Integer> mapPrice = getPriceByMealType();
        Map<MealType, Set<String>> mapFoodIds = getFoodIdsByMealType(request.getListMeal());
        List<Meal> mealsInit = new ArrayList<>();
        List<Menu> menusInit = new ArrayList<>();
        List<MealListDto> mealListReq = request.getListMeal();
        List<String> days = Arrays.stream(Day.values()).map(Enum::name).collect(Collectors.toList());
        days.remove(Day.ALL.name());
        if (Day.ALL.name().equals(request.getDay())) {
            days.forEach(u -> mealsInit.addAll(createMealByDay(Day.valueOf(u), request.getName(), mealListReq, canteenIds, accountId, mapPrice)));
        } else
            mealsInit.addAll(createMealByDay(Day.valueOf(request.getDay()), request.getName(), mealListReq, canteenIds, accountId, mapPrice));
        List<Meal> mealsCreated = mealRepository.saveAll(mealsInit);
        mealsCreated.forEach(u -> menusInit.addAll(createMenusByMealType(u.getId(), request.getName(), u.getType(), mapFoodIds)));
        menuRepository.saveAll(menusInit);
        insertActionLog(request.getRequestId(), accountId, ActionType.CREATE_MENU, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean edit(MenuEditReqDto request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String accountId = perRequestContextDto.getCurrentAccountId();

        if (Day.ALL.name().equals(request.getDay())) {
            throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_INVALID, "parameter.invalid.day");
        }
        List<MealListUpdateDto> mealListUpdate = request.getListMeal();
        if (mealListUpdate == null || mealListUpdate.isEmpty())
            throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.listMeal");

        Set<MealType> mealTypeSet = new HashSet<>();
        List<MealType> mealTypeList = new ArrayList<>();
        request.getListMeal().forEach(u -> {
            try {
                mealTypeSet.add(MealType.valueOf(u.getMealType()));
                mealTypeList.add(MealType.valueOf(u.getMealType()));
            } catch (Exception ex) {
                throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.invalid.listMeal");
            }
        });
        if (mealTypeSet.size() != mealTypeList.size() || (Day.SATURDAY.name().equals(request.getDay()) && mealTypeSet.contains(MealType.DINNER))) {
            throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_INVALID, "parameter.invalid.listMeal");
        }

        Set<String> foodIds = new HashSet<>();
        request.getListMeal().forEach(u -> {
            if (u.getListFoodId() == null || u.getListFoodId().isEmpty()) {
                throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.listFood");
            }
            if (u.getListFoodId().size() > NUMBER_OF_FOOD)
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Menu.CHOOSE_MAXIMUM_FOOD, null);
            foodIds.addAll(u.getListFoodId());
        });
        checkFoodsExist(request.getRequestId(), foodIds);

        List<Meal> mealsExist = mealRepository.findByCreatedByAndDay(accountId, Day.valueOf(request.getDay()));
        if (mealsExist.isEmpty())
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Menu.NOT_CREATED_FOR_ONE, null);

        updateMenu(request.getName(), request.getListMeal(), mealsExist);
        insertActionLog(request.getRequestId(), accountId, ActionType.EDIT_MENU, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            menuRepository.deleteById(request.getId());
            insertActionLog(request.getRequestId(), null, ActionType.DELETE_MENU, objectMapper.writeValueAsString(request));
            return true;
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Delete menu id:{} fail", request.getId(), ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    public Page<MenuFilterResDto> filter(MenuFilterReqDto request, HttpServletRequest httpServletRequest) {
        String bearAuth = perRequestContextDto.getBearToken();
        String canteenId = request.getCanteenId();
        if (StringUtils.isNotBlank(bearAuth)){
            AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(),
                    request.getRequestId(), bearAuth);

            List<String> roles = accountDTO.getRoles().stream().map(AccountRoleResDto::getCode).collect(Collectors.toList());
            if (roles.contains(RoleType.CHEF.name())) {
                canteenId = getCanteen(request.getRequestId(), accountDTO.getUnitId()).get(0).getId();
            } else {
                if (StringUtils.isBlank(canteenId))
                    throw new BusinessEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.canteenId");
            }
        }
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        String dayRequest = request.getDay();
        if (Day.ALL.name().equals(dayRequest)) dayRequest = null;
        List<MenuFilterDbDto> val = menuRepository.filterMenu(StringUtils.isBlank(dayRequest) ? null : Day.valueOf(dayRequest), canteenId);
        List<MenuFilterResDto> res = convert(val);

        logger.info("Found:{} menus.", res.size());
        return new PageImpl<>(res, pageable, res.size());
    }

    @Override
    public MenuDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            Menu menu = menuRepository.getMenuById(request.getId()).orElseThrow(() -> {
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Meal.NOT_FOUND, null);
            });
            return mapper.map(menu, MenuDetailResDTO.class);

        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Get canteen id:{} fail", request.getId(), ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    /**
     * create list of meals by day
     */
    public List<Meal> createMealByDay(Day day, String name, List<MealListDto> mealListDtos, List<String> canteenIds, String createdBy, Map<MealType, Integer> map) {
        List<Meal> meals = new ArrayList<>();
        mealListDtos.forEach(u -> canteenIds.forEach(t -> {
            Meal meal = new Meal();
            meal.setName(name);
            meal.setDay(day);
            meal.setPrice(map.get(MealType.valueOf(u.getMealType())));
            meal.setType(MealType.valueOf(u.getMealType()));
            meal.setCanteenId(t);
            meal.setCreatedBy(createdBy);
            meals.add(meal);
        }));
        if (Day.SATURDAY.equals(day)) {
            return meals.stream().filter(t -> !MealType.DINNER.equals(t.getType())).collect(Collectors.toList());
        }
        return meals;
    }

    /**
     * Create list of menus by mealType
     */
    public List<Menu> createMenusByMealType(String mealId, String name, MealType mealType, Map<MealType, Set<String>> map) {
        List<Menu> menus = new ArrayList<>();
        Set<String> foodIds = map.get(mealType);
        foodIds.forEach(u -> {
            Menu menu = new Menu();
            menu.setName(name);
            menu.setMealId(mealId);
            menu.setFoodIds(u);
            menus.add(menu);
        });
        return menus;
    }

    /**
     * Get list of foodIds by mealType
     */
    public Map<MealType, Set<String>> getFoodIdsByMealType(List<MealListDto> listMeal) {
        EnumMap<MealType, Set<String>> map = new EnumMap<>(MealType.class);
        listMeal.forEach(u -> map.put(MealType.valueOf(u.getMealType()), new HashSet<>(u.getListFoodId())));
        return map;
    }

    /**
     * Get price by mealType
     */
    public Map<MealType, Integer> getPriceByMealType() throws JsonProcessingException {
        EnumMap<MealType, Integer> map = new EnumMap<>(MealType.class);
        Optional<ApplicationSetting> val = applicationSettingRepository.findByKey(Constants.CONFIG_PRICE_MEAL);
        if (val.isEmpty()) {
            throw new BusinessEx(ResourceErrorCode.Menu.NOT_FOUND, null);
        }
        String value = val.get().getValue();
        List<PriceMealDto> priceMealDtos = Arrays.asList(objectMapper.readValue(value, PriceMealDto[].class));
        priceMealDtos.forEach(u -> map.put(u.getMealType(), u.getPrice()));
        return map;
    }

    /**
     * Check meal is created for all day
     */
    public void checkMealCreatedAllDay(String requestId, String accountId) {
        List<Meal> meals = mealRepository.findByCreatedBy(accountId);
        Set<Day> dayExists = meals.stream().map(Meal::getDay).collect(Collectors.toSet());
        if (!dayExists.isEmpty())
            throw new BusinessEx(requestId, ResourceErrorCode.Menu.MENU_CREATED, null);
    }

    /**
     * Check meal is created for a day
     */
    public void checkMealCreatedOneDay(String requestId, String accountId, Day day) {
        List<Meal> meals = mealRepository.findByCreatedByAndDay(accountId, day);
        if (!meals.isEmpty()) throw new BusinessEx(requestId, ResourceErrorCode.Menu.MENU_CREATED, null);
    }

    /**
     * Check list of foodIds exist
     */
    public void checkFoodsExist(String requestId, Set<String> foodIds) {
        List<String> foodExistIds = foodRepository.findByListId(foodIds).stream().map(Food::getId).collect(Collectors.toList());
        if (foodExistIds.size() < foodIds.size())
            throw new BusinessEx(requestId, ResourceErrorCode.Food.NOT_FOUND, null);
    }

    /**
     * Get canteenId by user login
     */
    public List<Canteen> getCanteen(String requestId, String unitId) {
        List<Canteen> canteenList = canteenRepository.findByUnitId(unitId);
        if (canteenList.isEmpty()) throw new BusinessEx(requestId, ResourceErrorCode.Canteen.NOT_FOUND, null);
        return canteenList;
    }

    /**
     * Update menu
     */
    public void updateMenu(String name, List<MealListUpdateDto> mealsUpdate, List<Meal> mealsExist) {
        EnumMap<MealType, Set<String>> mapMealType = new EnumMap<>(MealType.class);
        mealsUpdate.forEach(u -> mapMealType.put(MealType.valueOf(u.getMealType()), new HashSet<>(u.getListFoodId())));
        List<Menu> menusUpdate = new ArrayList<>();
        List<String> mealIdsNeedToDelete = new ArrayList<>();
        List<String> mealIdsNeedToUpdate = new ArrayList<>();
        mealsExist.forEach(u -> {
            mealIdsNeedToUpdate.add(u.getId());
            if (mapMealType.containsKey(u.getType())) {
                Set<String> foodIds = mapMealType.get(u.getType());
                mealIdsNeedToDelete.add(u.getId());
                foodIds.forEach(
                        t -> {
                            Menu menu = new Menu();
                            menu.setName(name);
                            menu.setMealId(u.getId());
                            menu.setFoodIds(t);
                            menusUpdate.add(menu);
                        }
                );
            }
        });
        if (!name.equals(mealsExist.get(0).getName()))
            mealRepository.updateName(mealIdsNeedToUpdate, name);
        menuRepository.deleteByMealIds(mealIdsNeedToDelete);
        menuRepository.saveAll(menusUpdate);
    }

    /**
     * Convert List<MenuFilterDbDto> to List<MenuFilterResDto>
     */
    public List<MenuFilterResDto> convert(List<MenuFilterDbDto> val) {
        List<MenuFilterResDto> res = new ArrayList<>();
        Map<Day, List<MenuFilterDbDto>> map = val.stream()
                .collect(Collectors.groupingBy(MenuFilterDbDto::getDay));

        for (Map.Entry<Day, List<MenuFilterDbDto>> entry : map.entrySet()) {
            MenuFilterResDto menuDto = new MenuFilterResDto();
            menuDto.setDay(entry.getKey());
            menuDto.setName(entry.getValue().get(0).getName());
            List<MealFilterResDto> menu = new ArrayList<>();

            EnumMap<MealType, HashSet<FoodFilterResDto>> mapMeal = new EnumMap<>(MealType.class);
            Map<String, HashSet<FileFilterResDto>> mapFood = new HashMap<>();

            entry.getValue().forEach(u -> {
                MealFilterResDto mealDto = MealFilterResDto.builder()
                        .id(u.getMealId())
                        .mealType(u.getType())
                        .price(u.getPrice())
                        .createdAt(u.getCreatedAt())
                        .build();
                if (!menu.contains(mealDto)) menu.add(mealDto);

                FileFilterResDto fileDto = FileFilterResDto.builder()
                        .id(u.getFileId())
                        .fileName(u.getFileName())
                        .url(u.getUrl())
                        .build();
                if (mapFood.containsKey(u.getFoodId())) mapFood.get(u.getFoodId()).add(fileDto);
                else {
                    HashSet<FileFilterResDto> filesNew = new HashSet<>();
                    filesNew.add(fileDto);
                    mapFood.put(u.getFoodId(), filesNew);
                }

                FoodFilterResDto foodDto = FoodFilterResDto.builder()
                        .id(u.getFoodId())
                        .name(u.getFoodName())
                        .type(u.getFoodType())
                        .createdAt(u.getFoodCreatedAt())
                        .build();
                if (mapMeal.containsKey(u.getType())) mapMeal.get(u.getType()).add(foodDto);
                else {
                    HashSet<FoodFilterResDto> foodsNew = new HashSet<>();
                    foodsNew.add(foodDto);
                    mapMeal.put(u.getType(), foodsNew);
                }
            });
            Collections.sort(menu);
            menuDto.setMenu(menu);
            mapMeal.forEach((key, value) -> value.forEach(w -> w.setFiles(mapFood.get(w.getId()))));
            menuDto.getMenu().forEach(t -> t.setFoods(mapMeal.get(t.getMealType())));
            res.add(menuDto);
        }
        Collections.sort(res);
        return res;
    }
}
