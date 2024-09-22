package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.resttemplate.dto.AccInfoBasicDTO;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.enums.DirectionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCarResDTO {

    private String id;

    private String bookingId;

    private DirectionType type;

    private CarBookingType tripType;

    private String original;

    private String destination;

    private LocalDateTime createdAt;

    private LocalDateTime departureDate;

    private String note;

    private BookingStatus status;

    private CarBookingDetailStatus detailStatus;

    private Integer members;

    private Integer approveLevel;

    private String accountId;

    private String driverId;

    private String carId;

    private String reason;

    private AccInfoBasicDTO userInfo;

    private AccInfoBasicDTO driverInfo;

    private CarDTO carInfo;

    private FileResponse file;

    private Double fuelEstimate;

    private Double distance;

    public BookingCarResDTO(String id, String bookingId, DirectionType type, CarBookingType tripType, String original, String destination, LocalDateTime createdAt, LocalDateTime departureDate, String note, BookingStatus status, Integer members, Integer approveLevel
        , String accountId, String driverId, String carId, String reason, CarBookingDetailStatus detailStatus, Double fuelEstimate, Double distance
    ) {
        this.id = id;
        this.bookingId = bookingId;
        this.type = type;
        this.tripType = tripType;
        this.original = original;
        this.destination = destination;
        this.createdAt = createdAt;
        this.departureDate = departureDate;
        this.note = note;
        this.status = status;
        this.members = members;
        this.approveLevel = approveLevel;
        this.reason = reason;
        this.accountId = accountId;
        this.driverId = driverId;
        this.carId = carId;
        this.detailStatus = detailStatus;
        this.fuelEstimate = fuelEstimate;
        this.distance = distance;
    }
}
