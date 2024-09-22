package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.resttemplate.dto.AccInfoBasicDTO;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetListReqBookingCarResDTO {

    private String id;

    private CarBookingType tripType;

    private String original;

    private String destination;

    private LocalDateTime departureDate;

    private LocalDateTime returnDate;

    private BookingStatus status;

    private Integer approveLevel;

    private Integer members;

    private LocalDateTime createdAt;

    private FileResponse file;

    private AccInfoBasicDTO accountInfo;

    private Double distance;

    public GetListReqBookingCarResDTO(String id, CarBookingType tripType, String original, String destination, LocalDateTime departureDate, LocalDateTime returnDate, BookingStatus status, LocalDateTime createdAt
    ,String userIdRequest, Integer approveLevel, Integer members, Double distance) {
        this.id = id;
        this.tripType = tripType;
        this.original = original;
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.status = status;
        this.createdAt = createdAt;
        this.accountInfo = AccInfoBasicDTO.builder().accountId(userIdRequest).build();
        this.approveLevel = approveLevel;
        this.members = members;
        this.distance = distance;
    }
}
