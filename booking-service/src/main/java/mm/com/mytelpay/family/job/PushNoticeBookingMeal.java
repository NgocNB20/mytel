package mm.com.mytelpay.family.job;

import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.business.notification.NoticeService;
import mm.com.mytelpay.family.business.notification.NotificationReqDTO;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.model.BookingMeal;
import mm.com.mytelpay.family.model.BookingMealDetail;
import mm.com.mytelpay.family.repo.BookingMealDetailRepository;
import mm.com.mytelpay.family.repo.BookingMealRepository;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PushNoticeBookingMeal {
    private final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    @Autowired
    private BookingMealRepository bookingMealRepository;

    @Autowired
    private BookingMealDetailRepository bookingMealDetailRepository;

    @Autowired
    private AccountRestTemplate accountRestTemplate;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    public ApplicationSettingCommonService applicationSettingCommonService;

    @Scheduled(cron = "0 0 21 * * *")
    public void pushNoticeBreakfastForEU() {
        logger.info("Start job push notice  breakfast");
        sendNoticeAndMealType(MealType.BREAKFAST, NoticeTemplate.SEND_NOTIFICATION_TO_EU_HAVE_BREAKFAST, NoticeTemplate.SEND_NOTIFICATION_TO_EU_HAVE_BREAKFAST_DEFAULT);
        logger.info("End job push notice  breakfast");
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void pushNoticeLunchForEU() {
        logger.info("Start job push notice  lunch");
        sendNoticeAndMealType(MealType.LUNCH, NoticeTemplate.SEND_NOTIFICATION_TO_EU_HAVE_LUNCH, NoticeTemplate.SEND_NOTIFICATION_TO_EU_HAVE_LUNCH_DEFAULT);
        logger.info("End job push notice  breakfast");
    }

    @Scheduled(cron = "0 0 13 * * *")
    public void pushNoticeDinnerForEU() {
        logger.info("Start job push notice  dinner");
        sendNoticeAndMealType(MealType.DINNER, NoticeTemplate.SEND_NOTIFICATION_TO_EU_HAVE_DINNER, NoticeTemplate.SEND_NOTIFICATION_TO_EU_HAVE_DINNER_DEFAULT);
        logger.info("End job push notice  dinner");
    }

    private void sendNoticeAndMealType(MealType mealType, String template, String templateDefault) {
        LocalDate day = LocalDate.now();
        if (mealType.equals(MealType.BREAKFAST)) {
            day = day.plusDays(1);
        }
        List<BookingMealDetail> mealDetails = bookingMealDetailRepository.getBookingMealDetailByMealDayAndMealType(day, mealType, MealDetailStatus.PENDING);
        if (Objects.isNull(mealDetails) || mealDetails.isEmpty()) return;
        List<String> bookingMealIds = mealDetails.stream().map(BookingMealDetail::getBookingMealId).collect(Collectors.toList());
        List<BookingMeal> bookingMealList = bookingMealRepository.getAccountIdByBookingMealIds(bookingMealIds);
        List<String> accountId = bookingMealList.stream().map(BookingMeal::getAccountId).collect(Collectors.toList());
        List<AccountDeviceResDTO> accountDeviceResDTOS = accountRestTemplate.getListAccountDeviceRestDTO(accountId);

        Map<String, List<AccountDeviceResDTO>> mapAccountIdAndDevice = new HashMap<>();
        accountDeviceResDTOS.forEach(a -> {
            if (!mapAccountIdAndDevice.containsKey(a.getAccountId())) {
                mapAccountIdAndDevice.put(a.getAccountId(), new ArrayList<>());
            }
            mapAccountIdAndDevice.get(a.getAccountId()).add(a);
        });

        Map<String, List<AccountDeviceResDTO>> listMap = new HashMap<>();
        for (BookingMealDetail b : mealDetails) {
            if (!listMap.containsKey(b.getId())) {
                listMap.put(b.getId(), new ArrayList<>());
            }
            List<AccountDeviceResDTO> deviceResDTOS = new ArrayList<>();
            bookingMealList.forEach(bm -> {
                if (bm.getId().equals(b.getBookingMealId())) {
                    deviceResDTOS.addAll(mapAccountIdAndDevice.get(bm.getAccountId()));
                }
            });
            listMap.get(b.getId()).addAll(deviceResDTOS);
        }

        NotificationReqDTO noticeDTO = applicationSettingCommonService.getNoticeByKey(String.valueOf(template), null, null);
        for (Map.Entry<String, List<AccountDeviceResDTO>> entry : listMap.entrySet()) {
            noticeService.sendNotification(null, noticeDTO == null ? null : noticeDTO.getValue(), String.valueOf(templateDefault), null, entry.getValue(), Payload.BOOKING_MEAL, entry.getKey());
        }
    }
}
