package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.ActionType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "ACTION_LOG")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Data
public class ActionLog {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION_TYPE", columnDefinition = "VARCHAR(50)")
    private ActionType actionType;

    @Column(name = "MSISDN", columnDefinition = "VARCHAR(12)")
    private String msisdn;

    @Column(name = "REQUEST_TIME", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime requestTime;

    @Column(name = "REQUEST_ID", columnDefinition = "VARCHAR(255)")
    private String requestId;

    @Lob
    @Column(name = "data", columnDefinition="BLOB")
    private String data;

    @CreationTimestamp
    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;
}