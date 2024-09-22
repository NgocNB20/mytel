package mm.com.mytelpay.family.business.car.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
public class CarCreateReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    @EnumRegex(enumClass = CarType.class)
    private String carType;

    @NotBlank
    @SizeRegex(max = 100)
    private String model;

    @NotBlank
    @SizeRegex(max = 20)
    private String licensePlate;
}
