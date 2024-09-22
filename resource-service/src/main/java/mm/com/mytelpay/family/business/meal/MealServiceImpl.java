package mm.com.mytelpay.family.business.meal;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.meal.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.Meal;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.MealRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class MealServiceImpl extends FamilyBaseBusiness implements MealService {

    public MealServiceImpl() {
        logger = LogManager.getLogger(MealServiceImpl.class);
    }

    @Autowired
    private MealRepository mealRepository;

    @Override
    public MealDetailResDto getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        List<MealDetailDbDto> val = mealRepository.getDetailMeal(request.getId());

        MealDetailDbDto mealDefault = val.get(0);
        MealDetailResDto res = new MealDetailResDto();
        res.setId(mealDefault.getId());
        res.setName(mealDefault.getName());
        res.setCanteenName(mealDefault.getCanteenName());
        res.setDay(mealDefault.getDay());
        res.setCreatedAt(mealDefault.getCreatedAt());
        res.setPrice(mealDefault.getPrice());
        res.setType(mealDefault.getType());

        Set<FoodDetailDto> foods = new HashSet<>();
        Map<String, HashSet<FileDetailDto>> mapFood = new HashMap<>();

        val.forEach(u -> {
            FoodDetailDto a = FoodDetailDto.builder()
                    .id(u.getFoodId())
                    .name(u.getFoodName())
                    .type(u.getFoodType())
                    .createdAt(u.getFoodCreatedAt())
                    .build();
            foods.add(a);

            FileDetailDto b = FileDetailDto.builder().id(u.getFileId()).fileName(u.getFileName()).url(u.getUrl()).build();
            if (mapFood.containsKey(u.getFoodId())) mapFood.get(u.getFoodId()).add(b);
            else {
                HashSet<FileDetailDto> filesNew = new HashSet<>();
                filesNew.add(b);
                mapFood.put(u.getFoodId(), filesNew);
            }

        });
        foods.forEach(t -> t.setFiles(mapFood.get(t.getId())));
        res.setFoods(foods);
        return res;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean create(MealCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        try {
            Meal mealRequest = mapper.map(request, Meal.class);
            mealRepository.save(mealRequest);
            insertActionLog(request.getRequestId(), null, ActionType.CREATE_MEAL, objectMapper.writeValueAsString(request));
            return true;
        } catch (Exception ex) {
            logger.error("Create canteen fail", ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean edit(MealEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        try {
            Meal mealInDb = mealRepository.getMealById(request.getId()).orElseThrow(() -> {
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Meal.NOT_FOUND, null);
            });
            mealRepository.save(mapperMealRequestToMeal(request, mealInDb));
            insertActionLog(request.getRequestId(), null, ActionType.EDIT_MEAL, objectMapper.writeValueAsString(request));
            return true;
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Update meal id:{} fail", request.getId(), ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            mealRepository.deleteById(request.getId());
            insertActionLog(request.getRequestId(), null, ActionType.DELETE_MEAL, objectMapper.writeValueAsString(request));
            return true;
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Delete meal id:{} fail", request.getId(), ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    private Meal mapperMealRequestToMeal(MealEditReqDTO request, Meal meal) {
        if (StringUtils.isNotBlank(request.getName())) {
            meal.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getCanteenId())) {
            meal.setName(request.getCanteenId());
        }
        if (Objects.nonNull(request.getDay())) {
            meal.setDay(null);
        }
        if (Objects.nonNull(request.getType())) {
            meal.setType(MealType.valueOf(request.getType()));
        }
        if (Objects.nonNull(request.getPrice())) {
            meal.setPrice(request.getPrice().intValue());
        }
        return meal;
    }
}
