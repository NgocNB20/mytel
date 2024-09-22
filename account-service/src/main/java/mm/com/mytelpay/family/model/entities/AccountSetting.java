/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.model.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "ACCOUNT_CONFIG")
public class AccountSetting extends BaseModel{
    
     @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;
    
    @Column(name = "CAR_REGISTRATION", columnDefinition = "BOOLEAN")
    private Boolean carRegistrationActive = false;
    
    @Column(name = "CAR_REGISTRATION_TIME", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime carRegistrationTime;
}
