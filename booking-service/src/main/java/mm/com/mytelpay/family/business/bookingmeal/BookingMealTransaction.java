package mm.com.mytelpay.family.business.bookingmeal;

import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.model.BookingMeal;
import mm.com.mytelpay.family.model.BookingMealDetail;
import mm.com.mytelpay.family.repo.BookingMealDetailRepository;
import mm.com.mytelpay.family.repo.BookingMealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingMealTransaction extends BookingBaseBusiness {
    @Autowired
    private BookingMealRepository bookingMealRepository;

    @Autowired
    private BookingMealDetailRepository bookingMealDetailRepository;

    @Transactional
    public void saveAndDeductionMoney(String bearerAuth, BookingMeal bookingMeal, List<BookingMealDetail> bookingMealDetails, int totalAmount) {
        BookingMeal bookingMealSave = bookingMealRepository.save(bookingMeal);
        logger.info("Insert booking meal success");
        bookingMealDetails.forEach(meal -> meal.setBookingMealId(bookingMealSave.getId()));
        bookingMealDetailRepository.saveAll(bookingMealDetails);
        logger.info("Insert booking meal detail success");
        accountRestTemplate.updateBalance(totalAmount, BalanceActionType.ORDER_MEAL, bearerAuth);
    }

    @Transactional
    public void cancelOderMeal(String bearerAuth, BookingMealDetail bookingMealDetail, String reason) {
        accountRestTemplate.updateBalance(bookingMealDetail.getFee(), BalanceActionType.REFUND, bearerAuth);
        bookingMealDetail.setStatus(MealDetailStatus.CANCEL);
        bookingMealDetail.setReason(reason);
        bookingMealDetailRepository.save(bookingMealDetail);
        logger.info("Update booking meal detail success");
    }

}
