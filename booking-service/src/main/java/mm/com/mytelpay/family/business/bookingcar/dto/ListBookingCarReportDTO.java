package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListBookingCarReportDTO extends BaseRequest {
    private String id;
    private CarBookingType tripType;
    private String original;
    private String destination;
    private Integer members;
    private BookingStatus status;
    private LocalDateTime createdAt;

    private String msisdn;

    private String name;
    private String accountId;

    private FileResponse file;

    public ListBookingCarReportDTO(String id, CarBookingType tripType, String original, String destination, Integer members, BookingStatus status, LocalDateTime createdAt, String accountId) {
        this.id = id;
        this.tripType = tripType;
        this.original = original;
        this.destination = destination;
        this.members = members;
        this.status = status;
        this.createdAt = createdAt;
        this.accountId = accountId;

    }
}
