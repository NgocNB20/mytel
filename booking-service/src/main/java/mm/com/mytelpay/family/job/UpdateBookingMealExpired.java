package mm.com.mytelpay.family.job;

import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.model.BookingMealDetail;
import mm.com.mytelpay.family.repo.BookingMealDetailRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UpdateBookingMealExpired {
    private final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    @Autowired
    private BookingMealDetailRepository bookingMealDetailRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateBookingMealDetail() {
        logger.info("Start scanning and update booking meal expired");
        LocalDate date = LocalDate.now();
        List<BookingMealDetail> bookingMealDetails = bookingMealDetailRepository.getListBKMealDetailExpired(date, MealDetailStatus.PENDING);
        bookingMealDetails.forEach( b -> b.setStatus(MealDetailStatus.EXPIRED));
        if (!bookingMealDetails.isEmpty()){
            bookingMealDetailRepository.saveAll(bookingMealDetails);
        }
        logger.info("End scanning and update booking meal expired");
    }
}
