package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.resttemplate.dto.AccInfoBasicDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.HotelDTO;
import mm.com.mytelpay.family.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterBookingHotelResDTO {

    private String id;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer members;

    private BookingStatus status;

    private LocalDateTime createdAt;

    private HotelDTO hotelInfo;

    private AccInfoBasicDTO accountInfo;

    public FilterBookingHotelResDTO(String id, LocalDateTime startDate, LocalDateTime endDate, Integer members, BookingStatus status, LocalDateTime createdAt, String hotelId, String accountId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.members = members;
        this.status = status;
        this.createdAt = createdAt;
        this.hotelInfo = HotelDTO.builder().id(hotelId).build();
        this.accountInfo = AccInfoBasicDTO.builder().accountId(accountId).build();
    }
}
