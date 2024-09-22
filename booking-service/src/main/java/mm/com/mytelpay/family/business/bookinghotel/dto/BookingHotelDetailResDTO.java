package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.resttemplate.dto.HotelDTO;
import mm.com.mytelpay.family.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingHotelDetailResDTO {

    private String id;

    private Integer member;

    private LocalDateTime fromTime;

    private LocalDateTime toTime;

    private String reason;

    private BookingStatus bookingStatus;

    private LocalDateTime createdAt;

    private Integer feeBooking;

    private Integer feeService;

    private String note;

    private AccountDTO cusInfo;

    private HotelDTO hotelInfo;

    private List<FileResponse> bookingInvoice;

}
