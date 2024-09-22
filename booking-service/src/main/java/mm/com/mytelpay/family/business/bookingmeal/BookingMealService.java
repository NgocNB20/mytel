package mm.com.mytelpay.family.business.bookingmeal;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.bookingmeal.dto.*;
import org.springframework.data.domain.PageImpl;

import javax.servlet.http.HttpServletRequest;

public interface BookingMealService {

    boolean create(BookingMealCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    PageImpl<FilterBookingMealResDTO> getListForEU(FilterBookingMealEUReqDTO reqDTO, HttpServletRequest httpServletRequest);

    BookingMealDetailResDTO getDetail(BookingMealDetailReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    PageImpl<ReportOrderMealResDTO> filter(ReportBookingMealReqDTO reqDTO, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean verifyQrBookingMeal(VerifyQrBookingMealReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean cancelOrderMeal(CancelOrderMealReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    BookingMealFilterDTO getTotalBookingMeal(BookingMealFilterReqDTO reqDTO);

    PageImpl<BookingMealViewListDTO> listOrderBookingMeal(BookingMealViewListReqDTO request);
}
