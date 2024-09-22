package mm.com.mytelpay.family.job;

import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.notification.NoticeService;
import mm.com.mytelpay.family.business.notification.NotificationReqDTO;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.logging.RequestUtils;
import mm.com.mytelpay.family.model.BookingCar;
import mm.com.mytelpay.family.model.BookingCarDetail;
import mm.com.mytelpay.family.model.BookingHotel;
import mm.com.mytelpay.family.repo.BookingCarDetailRepository;
import mm.com.mytelpay.family.repo.BookingCarRepository;
import mm.com.mytelpay.family.repo.BookingHotelRepository;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AutoCancelRequest extends BookingBaseBusiness {

    @Autowired
    private BookingCarRepository bookingCarRepository;

    @Autowired
    private BookingCarDetailRepository bookingCarDetailRepository;

    @Autowired
    private BookingHotelRepository bookingHotelRepository;

    @Autowired
    private NoticeService noticeService;

    @Scheduled(cron = "0 0 0 * * *")
    public void jobCancelBookingCar() {
        logger.info("Start auto-cancellation of expired car bookings");
        LocalDateTime startDate = LocalDateTime.now();
        List<BookingCar> bookingCarsExpired = bookingCarRepository.findBookingCarExpired(startDate);
        if (bookingCarsExpired.isEmpty()) {
            return;
        }

        List<String> accountIds = bookingCarsExpired.stream().map(BookingCar::getAccountId).distinct().collect(Collectors.toList());
        Map<String, List<AccountDeviceResDTO>> mapAccountIdAndDevice = createMapAccountDevice(accountIds);

        NotificationReqDTO noticeCar = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_NOTICE_BOOKING_CAR_EXPIRED, null, null);
        NotificationReqDTO noticeCarNotStarted = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_NOTICE_BOOKING_CAR_NOT_STARTED, null, null);
        String noticeCarDefault = NoticeTemplate.SEND_NOTICE_BOOKING_CAR_EXPIRED_DEFAULT;
        String noticeCarApprovedDefault = NoticeTemplate.SEND_NOTICE_BOOKING_CAR_NOT_STARTED_EXPIRED_DEFAULT;

        List<BookingCarDetail> bookingCarDetails = new ArrayList<>();
        for (BookingCar bookingCar : bookingCarsExpired) {
            bookingCar.setBookingStatus(BookingStatus.CANCEL);
            bookingCar.setApproveLevel(0);

            if (BookingStatus.APPROVED.equals(bookingCar.getBookingStatus())) {
                bookingCar.setReason(NoticeTemplate.BOOKING_EXPIRED_REASON_NOT_STARTED);
                noticeService.sendNotification(null, noticeCar == null ? null : noticeCar.getValue(), noticeCarDefault
                        , null, mapAccountIdAndDevice.get(bookingCar.getAccountId()), Payload.BOOKING_CAR, bookingCar.getId());
            } else {
                bookingCar.setReason(NoticeTemplate.BOOKING_EXPIRED_REASON);
                noticeService.sendNotification(null, noticeCarNotStarted == null ? null : noticeCarNotStarted.getValue(), noticeCarApprovedDefault
                        , null, mapAccountIdAndDevice.get(bookingCar.getAccountId()), Payload.BOOKING_CAR, bookingCar.getId());
            }

            List<BookingCarDetail> bookingCarDetail = bookingCarDetailRepository.findByBookingCarId(bookingCar.getId());
            for (BookingCarDetail detail : bookingCarDetail) {
                detail.setStatus(CarBookingDetailStatus.CANCEL);
            }
            bookingCarDetails.addAll(bookingCarDetail);

        }
        bookingCarRepository.saveAll(bookingCarsExpired);
        bookingCarDetailRepository.saveAll(bookingCarDetails);
        insertActionLog(RequestUtils.currentRequestId(), null, ActionType.CANCEL_BOOKING_CAR, null, "Cancel " + bookingCarsExpired.size() + " booking car");
        logger.info("End job auto-cancellation of expired car bookings");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void jobCancelBookingHotel() {
        logger.info("Start auto-cancellation of expired hotel bookings");
        LocalDateTime startDate = LocalDateTime.now();
        List<BookingHotel> bookingHotelsExpired = bookingHotelRepository.getListBookingExpired(startDate);
        if (bookingHotelsExpired.isEmpty()) {
            return;
        }
        List<String> accountIds = bookingHotelsExpired.stream().map(BookingHotel::getAccountId).distinct().collect(Collectors.toList());
        Map<String, List<AccountDeviceResDTO>> mapAccountIdAndDevice = createMapAccountDevice(accountIds);

        NotificationReqDTO notice = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_NOTICE_BOOKING_HOTEL_EXPIRED, null, null);
        String noticeDefault = NoticeTemplate.SEND_NOTICE_BOOKING_HOTEL_EXPIRED_DEFAULT;
        for (BookingHotel bookingHotel : bookingHotelsExpired) {
            bookingHotel.setBookingStatus(BookingStatus.CANCEL);
            bookingHotel.setReason(NoticeTemplate.BOOKING_EXPIRED_REASON);
            noticeService.sendNotification(null, notice == null ? null : notice.getValue(), noticeDefault
                    , null, mapAccountIdAndDevice.get(bookingHotel.getAccountId()), Payload.BOOKING_HOTEL, bookingHotel.getId());
        }
        bookingHotelRepository.saveAll(bookingHotelsExpired);
        logger.info("End job auto-cancellation of expired hotel bookings");
    }

    @NotNull
    private Map<String, List<AccountDeviceResDTO>> createMapAccountDevice(List<String> accountIds) {
        List<AccountDeviceResDTO> accountDeviceResDTOS = accountRestTemplate.getListAccountDeviceRestDTO(accountIds);
        Map<String, List<AccountDeviceResDTO>> mapAccountIdAndDevice = new HashMap<>();
        for (AccountDeviceResDTO a : accountDeviceResDTOS) {
            if (!mapAccountIdAndDevice.containsKey(a.getAccountId())) {
                mapAccountIdAndDevice.put(a.getAccountId(), new ArrayList<>());
            }
            mapAccountIdAndDevice.get(a.getAccountId()).add(a);
        }
        return mapAccountIdAndDevice;
    }
}
