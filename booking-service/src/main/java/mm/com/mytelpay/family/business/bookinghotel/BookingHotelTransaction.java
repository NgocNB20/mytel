package mm.com.mytelpay.family.business.bookinghotel;

import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.enums.BookingHistoryStatus;
import mm.com.mytelpay.family.model.BookingHotel;
import mm.com.mytelpay.family.model.BookingHotelHistory;
import mm.com.mytelpay.family.repo.BookingHotelHistoryRepository;
import mm.com.mytelpay.family.repo.BookingHotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingHotelTransaction extends BookingBaseBusiness {

    @Autowired
    private BookingHotelRepository bookingHotelRepository;

    @Autowired
    private BookingHotelHistoryRepository bookingHotelHistoryRepository;

    @Transactional
    public BookingHotel createBookingHotel(BookingHotel hotel){
        BookingHotel bookingHotel = bookingHotelRepository.save(hotel);
        saveBookingHotelHistory(bookingHotel, null, bookingHotel.getAccountId(), BookingHistoryStatus.CREATED);
        logger.info("insert booking hotel success");
        return bookingHotel;
    }

    @Transactional
    public void saveBookingHotelHistory(BookingHotel bookingHotel, String roleId, String approveId, BookingHistoryStatus bookingHistoryStatus) {
        BookingHotelHistory bookingHotelHistory = new BookingHotelHistory();
        bookingHotelHistory.setBookingHotelId(bookingHotel.getId());
        bookingHotelHistory.setApproveId(approveId);
        bookingHotelHistory.setRoleId(roleId);
        bookingHotelHistory.setStatus(bookingHistoryStatus);
        bookingHotelHistoryRepository.save(bookingHotelHistory);
        logger.info("Insert booking hotel history success");
    }

    @Transactional
    public void updateBookingHotel(BookingHotel hotel, String roleId, String accountId, BookingHistoryStatus bookingHistoryStatus){
        BookingHotel bookingHotel = bookingHotelRepository.save(hotel);
        saveBookingHotelHistory(bookingHotel, roleId, accountId, bookingHistoryStatus);
        logger.info("insert booking hotel success");
    }

}
