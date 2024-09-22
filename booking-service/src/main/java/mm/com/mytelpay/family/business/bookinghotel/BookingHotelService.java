package mm.com.mytelpay.family.business.bookinghotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.bookinghotel.dto.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface BookingHotelService {

    boolean create(BookingHotelCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    BookingHotelDetailResDTO getDetail(BookingHotelDetailReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    PageImpl<FilterBookingHotelResDTO> filterForEU(FilterBookingHotelReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    PageImpl<FilterBookingHotelResDTO> filterForAdmin(FilterBookingHotelReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean cancel(CancelBookingHotelReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean approveOrReject(ApproveRejectBookingHotelReqDTO request, MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean checkHotelInBooking(SimpleRequest request, HttpServletRequest httpServletRequest);

}
