package mm.com.mytelpay.family.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.models.BaseModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "ACCOUNT")
public class Account extends BaseModel {

    @Id
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "KEYCLOAK_ID", columnDefinition = "VARCHAR(50)")
    private String keycloakID;

    @NotNull
    @Column(name = "MSISDN", columnDefinition = "VARCHAR(100)")
    private String msisdn;

    @Column(name = "EMAIL", columnDefinition = "VARCHAR(100)")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", columnDefinition = "VARCHAR(36)")
    private Status status = Status.ACTIVE;

    @Column(name = "UNIT_ID", columnDefinition = "VARCHAR(36)")
    private String unitId;

    @Column(name = "APPROVER_ID", columnDefinition = "VARCHAR(36)")
    private String approverId;

    @Column (name = "CANTEEN_ID", columnDefinition = "VARCHAR(100)")
    private String canteenId;
    
    @JsonIgnore
    @Transient
    private List<Role> roles;

}
