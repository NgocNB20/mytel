/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.resttemplate.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountSettingDTO {
    
    private String accountId;

    private Boolean carRegistrationActive;

    private LocalDateTime carRegistrationTime;
}
