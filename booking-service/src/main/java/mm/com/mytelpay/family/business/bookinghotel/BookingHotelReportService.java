package mm.com.mytelpay.family.business.bookinghotel;

import mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelReqDTO;
import mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelResDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BookingHotelReportService {

    ResponseEntity<ByteArrayResource> exportExcelReportBookingHotel(ReportBookingHotelReqDTO request , HttpServletRequest httpServletRequest) throws IOException;

    PageImpl<ReportBookingHotelResDTO> getListBookingHotelReport(ReportBookingHotelReqDTO request , HttpServletRequest httpServletRequest);
}
