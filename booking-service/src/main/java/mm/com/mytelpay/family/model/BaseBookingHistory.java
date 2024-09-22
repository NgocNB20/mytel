package mm.com.mytelpay.family.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.BookingHistoryStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseBookingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "ROLE_ID", columnDefinition = "VARCHAR(36)")
    private String roleId;

    @Column(name = "APPROVE_STATUS", columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private BookingHistoryStatus status;

    @Column(name = "APPROVE_ID", columnDefinition = "VARCHAR(36)")
    private String approveId;

    @CreationTimestamp
    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;

}
