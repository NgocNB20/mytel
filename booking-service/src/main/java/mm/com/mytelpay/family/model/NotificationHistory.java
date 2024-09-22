package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.Payload;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "NOTIFICATION_HISTORY")
@Data
public class NotificationHistory {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "IS_READ", columnDefinition = "NUMERIC(1)")
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAY_LOAD", columnDefinition = "VARCHAR(36)")
    private Payload payload;

    @Column(name = "ID_PAY_LOAD", columnDefinition = "VARCHAR(36)")
    private String idPayload;

    @CreationTimestamp
    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;
}
