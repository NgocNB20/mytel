package mm.com.mytelpay.family.business.bookingcar;

import mm.com.mytelpay.family.business.bookingcar.dto.ListAccountReportReqDTO;
import mm.com.mytelpay.family.business.bookingcar.dto.ListBookingCarReportDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BookingCarReportService {
    ResponseEntity<ByteArrayResource> exportExcel (ListAccountReportReqDTO request , HttpServletRequest httpServletRequest) throws IOException;

    PageImpl<ListBookingCarReportDTO> getListAccountReport (ListAccountReportReqDTO listAccountReportReqDTO , HttpServletRequest httpServletRequest);
}
