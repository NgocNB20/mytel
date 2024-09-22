package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterBookingCarResDTO {

    private String id;

    private CarBookingType type;

    private String original;

    private String destination;

    private LocalDateTime departureDate;

    private LocalDateTime returnDate;

    private BookingStatus status;

    private Integer members;

    private LocalDateTime createdAt;

    private FileResponse file;

    public FilterBookingCarResDTO(String id, CarBookingType type, String original, String destination,
      LocalDateTime departureDate, LocalDateTime returnDate, BookingStatus status, Integer members, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.original = original;
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.status = status;
        this.members = members;
        this.createdAt = createdAt;
    }
}
