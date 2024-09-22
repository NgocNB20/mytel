package mm.com.mytelpay.family.business.car.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

import java.util.List;

@Data
public class CarEditReqDTO extends BaseRequest {

    @NotBlank
    private String id;

    @SizeRegex(max = 100)
    private String name;

    @EnumRegex(enumClass = CarType.class)
    private String carType;

    @SizeRegex(max = 100)
    private String model;

    @SizeRegex(max = 20)
    private String licensePlate;

    private String color;

    @EnumRegex(enumClass = Status.class)
    private String status;

    private List<Long> fileDeletes;

}
