package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportBookingHotelResDTO {

    private String id;

    private String accountId;

    private String fullName;

    private String phone;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer member;

    private String hotelId;

    private String hotelName;

    private Integer bookingFee;

    private Integer serviceFee;

    private BookingStatus status;

    private LocalDateTime createAt;

    private List<FileResponse> bookingInvoice;
    public ReportBookingHotelResDTO(String id, String accountId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createAt, Integer member,
                                    String hotelId, Integer bookingFee, Integer serviceFee, BookingStatus status) {
        this.id = id;
        this.accountId = accountId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createAt = createAt;
        this.member = member;
        this.hotelId = hotelId;
        this.bookingFee = bookingFee;
        this.serviceFee = serviceFee;
        this.status = status;
    }
}
