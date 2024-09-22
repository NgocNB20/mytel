package mm.com.mytelpay.family.business.bookingmeal;

import mm.com.mytelpay.family.business.bookingmeal.dto.ReportBookingMealReqDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.ReportFeeBookingMealReqDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.ReportFeeOrderMealResDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.ReportOrderMealResDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BookingMealReportService {

    ResponseEntity<ByteArrayResource> exportExcelReportBookingMeal(ReportBookingMealReqDTO request , HttpServletRequest httpServletRequest) throws IOException;

    PageImpl<ReportOrderMealResDTO> getListBookingMealReport(ReportBookingMealReqDTO request , HttpServletRequest httpServletRequest);

    ResponseEntity<ByteArrayResource> exportExcelReportFeeBookingMeal(ReportFeeBookingMealReqDTO request , HttpServletRequest httpServletRequest) throws IOException;

    PageImpl<ReportFeeOrderMealResDTO> getListBookingFeeMealReport(ReportFeeBookingMealReqDTO request , HttpServletRequest httpServletRequest);

}
