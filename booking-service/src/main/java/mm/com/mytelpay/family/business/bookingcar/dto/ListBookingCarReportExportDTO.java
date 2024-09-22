package mm.com.mytelpay.family.business.bookingcar.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListBookingCarReportExportDTO extends BaseRequest {
    private String id;

    private String user;

    private String phone;

    private CarBookingType tripType;

    private String original;

    private String destination;

    private Integer members;

    private String reason;

    private String accountId;

    private String file;

    private BookingStatus statusBooking;

    private LocalDateTime createdTime;

    private String driverIdOutbound;

    private String driverNameOutbound;

    private String phoneDriverOutbound;

    private String carIdOutbound;

    private String carNameOutbound;

    private String licensePlateOutbound;

    private CarBookingDetailStatus statusOutbound;

    private String driverIdReturn;

    private String driverNameReturn;

    private String phoneDriverReturn;

    private String carIdReturn;

    private String carNameReturn;

    private String licensePlateReturn;

    private CarBookingDetailStatus statusReturn;

    public ListBookingCarReportExportDTO(String id, CarBookingType tripType, String original, String destination, Integer members, String reason, String accountId, BookingStatus statusBooking, LocalDateTime createdTime) {
        this.id = id;
        this.tripType = tripType;
        this.original = original;
        this.destination = destination;
        this.members = members;
        this.reason = reason;
        this.accountId = accountId;
        this.statusBooking = statusBooking;
        this.createdTime = createdTime;
    }
}
