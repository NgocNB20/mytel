/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.CarType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarQRDataResDTO {
    private String id;

    private String name;

    private CarType carType;

    private String licensePlate;
}
