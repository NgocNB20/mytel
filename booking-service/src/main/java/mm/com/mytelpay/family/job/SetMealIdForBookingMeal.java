package mm.com.mytelpay.family.job;

import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.GetListMenuResDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.MealResDTO;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.model.BookingMeal;
import mm.com.mytelpay.family.model.BookingMealDetail;
import mm.com.mytelpay.family.repo.BookingMealDetailRepository;
import mm.com.mytelpay.family.repo.BookingMealRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SetMealIdForBookingMeal {
    private final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    @Autowired
    private BookingMealDetailRepository bookingMealDetailRepository;

    @Autowired
    private BookingMealRepository bookingMealRepository;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    @Scheduled(cron = "0 0 0 * * *")
    public void setMenuForBookingMeal() {
        logger.info("Start scanning and updating bookings with mealId null");
        List<BookingMealDetail> bookingMealDetails = bookingMealDetailRepository.getListMealIdNull();
        bookingMealDetails = updateMealIdForBookingMealDetails(bookingMealDetails);
        if (!bookingMealDetails.isEmpty()){
            bookingMealDetailRepository.saveAll(bookingMealDetails);
        }
        logger.info("End scanning and updating bookings with mealId null");
    }

    private List<BookingMealDetail> updateMealIdForBookingMealDetails(List<BookingMealDetail> bookingMealDetails) {
        if (Objects.isNull(bookingMealDetails) || bookingMealDetails.isEmpty()){
            return new ArrayList<>();
        }
        List<String> bookingMealIds = bookingMealDetails.stream().map(BookingMealDetail::getBookingMealId).distinct().collect(Collectors.toList());
        List<BookingMeal> bookingMeals = bookingMealRepository.findAllById(bookingMealIds);
        List<String> canteenIds = bookingMeals.stream().map(BookingMeal::getCanteenId).distinct().collect(Collectors.toList());
        if (canteenIds.isEmpty()){
            return new ArrayList<>();
        }

        Map<String, List<GetListMenuResDTO>> mapMenuByCanteenIds = new HashMap<>();
        canteenIds.forEach(c ->{
            if (!mapMenuByCanteenIds.containsKey(c)) {
                mapMenuByCanteenIds.put(c, new ArrayList<>());
            }
        });

        for (Map.Entry<String, List<GetListMenuResDTO>> entry : mapMenuByCanteenIds.entrySet()) {
            List<GetListMenuResDTO> listMenuResDTOS = resourceRestTemplate.getListMeal(entry.getKey(), null);
            if (Objects.nonNull(listMenuResDTOS)){
                mapMenuByCanteenIds.get(entry.getKey()).addAll(listMenuResDTOS);
            }
        }

        Map<String, List<BookingMealDetail>> mapBKMEalDetailAndCanteen = new HashMap<>();
        for (BookingMeal bookingMeal : bookingMeals) {
            String canteenId = bookingMeal.getCanteenId();
            List<BookingMealDetail> mealDetails = new ArrayList<>();

            for (BookingMealDetail bookingMealDetail : bookingMealDetails) {
                if (bookingMealDetail.getBookingMealId().equals(bookingMeal.getId())) {
                    mealDetails.add(bookingMealDetail);
                }
            }
            mapBKMEalDetailAndCanteen.put(canteenId, mealDetails);
        }

        return setMealForBookingMealDetails(mapMenuByCanteenIds, mapBKMEalDetailAndCanteen);
    }

    @NotNull
    private List<BookingMealDetail> setMealForBookingMealDetails(Map<String, List<GetListMenuResDTO>> mapMenuByCanteenIds, Map<String, List<BookingMealDetail>> mapBKMEalDetailAndCanteen) {
        List<BookingMealDetail> newBookingMealDetailList = new ArrayList<>();
        for (Map.Entry<String, List<BookingMealDetail>> entry : mapBKMEalDetailAndCanteen.entrySet()){
            List<GetListMenuResDTO> listMenuResDTOS = mapMenuByCanteenIds.get(entry.getKey());
            Map<String, Map<MealType, String>> menuMap = mapMealType(listMenuResDTOS);

            List<BookingMealDetail> newBookingMealDetails = entry.getValue();
            newBookingMealDetails.forEach(b -> {
                if (Objects.nonNull(menuMap.get(b.getMealDay().getDayOfWeek().toString()))){
                    b.setMealId(menuMap.get(b.getMealDay().getDayOfWeek().toString()).get(b.getType()));
                }
            });
            newBookingMealDetailList.addAll(newBookingMealDetails);
        }
        return newBookingMealDetailList;
    }

    public static Map<String, Map<MealType, String>> mapMealType(List<GetListMenuResDTO> listMenuResDTOS) {
        Map<String, Map<MealType, String>> menuMap = new HashMap<>();
        for (GetListMenuResDTO menu : listMenuResDTOS) {
            EnumMap<MealType, String> mealMap = new EnumMap<>(MealType.class);
            for (MealResDTO meal : menu.getMenu()) {
                mealMap.put(meal.getMealType(), meal.getId());
            }
            menuMap.put(menu.getDay().toString(), mealMap);
        }
        return menuMap;
    }

}
