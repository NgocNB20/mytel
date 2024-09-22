package mm.com.mytelpay.family.business.bookingcar;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.model.BookingCar;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BookingCarService {

    PageImpl<FilterBookingCarResDTO> getList(FilterBookingCarReqDTO request, HttpServletRequest httpServletRequest);

    List<BookingCarResDTO> getDetailBookingCar(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean bookingCar(BookingCarReqDTO request, MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean approve(ApproveBookingCarReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean reject(RejectBookingCarReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean update(UpdateBookingCarReqDTO request, HttpServletRequest httpServletRequest);

    PageImpl<GetListReqBookingCarResDTO> getListReq(GetListReqBookingCarReqDTO request, HttpServletRequest httpServletRequest);

    boolean cancel (BookingCarCancel bookingCarCancel, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    PageImpl<GetListReqBookingCarResDTO> getListAssignedForDriver(GetListReqBookingCarReqDTO request, HttpServletRequest httpServletRequest);

    boolean verifyQrBookingCar(VerifyQrBookingCarReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean completeBookingCar(CompleteBookingReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    BookingAssignDriverDTO dataAssignDriver (AssignReqDTO reqDTO, HttpServletRequest httpServletRequest);

    BookingAssignCarDTO dataAssignCar (AssignReqDTO reqDTO, HttpServletRequest httpServletRequest);

    boolean checkCarOnTrip(SimpleRequest request, HttpServletRequest httpServletRequest);

    List<BookingCar> checkListReq(SimpleRequest request, HttpServletRequest httpServletRequest);

    List<BookingCar> checkListRequestOfDriver(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean rejectRequestUser(SimpleRequest request, HttpServletRequest httpServletRequest);

    MapboxDirectionDTO calculatorDistance(CalculatorDistanceReqDTO request, HttpServletRequest httpServletRequest);

    boolean updateFuelEst(UpdateFuelEstBookingCarReqDTO request, HttpServletRequest httpServletRequest);
}
