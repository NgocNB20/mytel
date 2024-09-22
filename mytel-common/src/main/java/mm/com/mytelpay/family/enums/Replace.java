package mm.com.mytelpay.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Replace {
    USERNAME("username"), NO_OF_POINT("noOfPoint"), CHEF_NAME("chefName"), PHONE_NUMBER("phoneNumber");

    private final String value;
}
