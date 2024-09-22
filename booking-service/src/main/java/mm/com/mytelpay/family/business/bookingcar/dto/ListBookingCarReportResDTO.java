package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.enums.DirectionType;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListBookingCarReportResDTO extends BaseRequest {
    private String id;
    private String user;
    private String phone;
    private CarBookingType tripType;
    private String original;
    private String destination;
    private Integer members;
    private BookingStatus statusBooking;
    private LocalDateTime createdAt;
    private String accountId;
    private String driverId;
    private String driverName;
    private String phoneDriver;
    private String carId;
    private String carName;
    private String licensePlate;
    private CarBookingDetailStatus status;
    private DirectionType type;
    private String reason;

    public ListBookingCarReportResDTO(String id, CarBookingType tripType, String original, String destination, Integer members, BookingStatus statusBooking, LocalDateTime createdAt, String accountId, String driverId, String carId, CarBookingDetailStatus status, DirectionType type, String reason) {
        this.id = id;
        this.tripType = tripType;
        this.original = original;
        this.destination = destination;
        this.members = members;
        this.statusBooking = statusBooking;
        this.createdAt = createdAt;
        this.accountId = accountId;
        this.driverId = driverId;
        this.carId = carId;
        this.status = status;
        this.type = type;
        this.reason = reason;
    }
}
