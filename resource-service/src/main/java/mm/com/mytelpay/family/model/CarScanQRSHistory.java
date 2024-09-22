/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "CAR_QR_SCAN_HISTORY")
@Data
public class CarScanQRSHistory extends BaseModel{
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;
     
    @Column(name = "USER_ID", columnDefinition = "VARCHAR(255)")
    String userId;
    
    @Column(name = "USER_NAME", columnDefinition = "VARCHAR(255)")
    String userName;
    
    @Column(name = "USER_EMAIL", columnDefinition = "VARCHAR(255)")
    String userEmail;
    
    @Column(name = "USER_MSISDN", columnDefinition = "VARCHAR(255)")
    private String userMsisdn;
    
    @Column(name = "CAR_ID", columnDefinition = "VARCHAR(255)")
    private String carId;
    
    @Column(name = "CAR_NAME", columnDefinition = "VARCHAR(255)")
    private String carName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "CAR_TYPE", columnDefinition = "VARCHAR(36)")
    private CarType carType;
    
    @Column(name = "CAR_LICENSE_PLATE", columnDefinition = "VARCHAR(255)")
    private String carLicensePlate;
}
