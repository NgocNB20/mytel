package mm.com.mytelpay.family.business.bookingcar;

import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.enums.*;
import mm.com.mytelpay.family.model.BookingCar;
import mm.com.mytelpay.family.model.BookingCarDetail;
import mm.com.mytelpay.family.model.BookingCarHistory;
import mm.com.mytelpay.family.repo.BookingCarDetailRepository;
import mm.com.mytelpay.family.repo.BookingCarHistoryRepository;
import mm.com.mytelpay.family.repo.BookingCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingCarTransaction extends BookingBaseBusiness {

    @Autowired
    private BookingCarRepository bookingCarRepository;

    @Autowired
    private BookingCarHistoryRepository bookingCarHistoryRepository;

    @Autowired
    private BookingCarDetailRepository bookingCarDetailRepository;

    @Transactional
    public BookingCar createBookingCarAndDetail(BookingCar bookingCar, MultipartFile file) {
        if (!bookingCar.getTypeBooking().equals(CarBookingType.TWO_WAY)){
            bookingCar.setTimeReturn(null);
        }
        BookingCar bookingCarSave = bookingCarRepository.save(bookingCar);
        logger.info("insert booking car success");
        createFile(file, bookingCarSave.getId(), ObjectType.BOOKING_CAR);
        saveBookingCarHistory(bookingCar, null, bookingCarSave.getAccountId(), BookingHistoryStatus.CREATED);

        List<BookingCarDetail> bookingCarDetails = new ArrayList<>();
        if (bookingCar.getTypeBooking().equals(CarBookingType.TWO_WAY)) {
            bookingCarDetails.add(createBookingDetail(bookingCar, DirectionType.OUTBOUND));
            bookingCarDetails.add(createBookingDetail(bookingCar, DirectionType.RETURN));
        } else {
            bookingCarDetails.add(createBookingDetail(bookingCar, DirectionType.OUTBOUND));
        }
        bookingCarDetailRepository.saveAll(bookingCarDetails);
        logger.info("insert booking car detail success");
        return bookingCarSave;
    }

    private BookingCarDetail createBookingDetail(BookingCar bookingCar, DirectionType type) {
        BookingCarDetail detail = new BookingCarDetail();
        detail.setBookingCarId(bookingCar.getId());
        detail.setStatus(CarBookingDetailStatus.valueOf(bookingCar.getBookingStatus().toString()));
        detail.setOriginal(bookingCar.getOriginal());
        detail.setDestination(bookingCar.getDestination());
        if (type.equals(DirectionType.RETURN)) {
            detail.setOriginal(bookingCar.getDestination());
            detail.setDestination(bookingCar.getOriginal());
            detail.setTimeStart(bookingCar.getTimeReturn());
        } else {
            detail.setTimeStart(bookingCar.getTimeStart());
        }
        detail.setType(type);
        detail.setCreatedAt(LocalDateTime.now());
        return detail;
    }

    @Transactional
    public void saveBookingCarHistory(BookingCar bookingCar, String roleId, String approveId, BookingHistoryStatus bookingHistoryStatus) {
        BookingCarHistory bookingCarHistory = new BookingCarHistory();
        bookingCarHistory.setBookingCarId(bookingCar.getId());
        bookingCarHistory.setStatus(bookingHistoryStatus);
        bookingCarHistory.setRoleId(roleId);
        bookingCarHistory.setApproveId(approveId);
        bookingCarHistoryRepository.save(bookingCarHistory);
        logger.info("Insert booking car history success");
    }

    @Transactional
    public void updateDataReject(BookingCar bookingCar, String roleId, String approveId) {
        bookingCar.setLastUpdatedAt(LocalDateTime.now());
        bookingCar.setBookingStatus(BookingStatus.CANCEL);
        bookingCarRepository.save(bookingCar);
        List<BookingCarDetail> bookingCarDetails = bookingCarDetailRepository.findByBookingCarId(bookingCar.getId());
        if (!bookingCarDetails.isEmpty()) {
            for (BookingCarDetail detail : bookingCarDetails) {
                detail.setStatus(CarBookingDetailStatus.CANCEL);
            }
            bookingCarDetailRepository.saveAll(bookingCarDetails);
        }
        saveBookingCarHistory(bookingCar, roleId, approveId, BookingHistoryStatus.REJECTED);
    }

    @Transactional
    public void saveBookingCarAndDetail(BookingCar bookingCar, List<BookingCarDetail> bookingCarDetails) {
        bookingCarRepository.save(bookingCar);
        logger.info("insert booking car success");
        bookingCarDetailRepository.saveAll(bookingCarDetails);
        logger.info("insert booking car detail success");
    }
}
