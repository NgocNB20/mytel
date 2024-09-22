package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.enums.BookingType;
import mm.com.mytelpay.family.model.RoleApprove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleApproveRepository extends JpaRepository<RoleApprove, String> {

    RoleApprove findFirstByBookingTypeAndAndRoleId(BookingType bookingType, String roleId);

    List<RoleApprove> findAllByBookingTypeOrderByLevelAsc(BookingType bookingType);

    List<RoleApprove> findAllByBookingTypeOrderByLevelDesc(BookingType bookingType);

    @Query(value = "SELECT c " +
            " FROM RoleApprove c " +
            "WHERE (:bookingType IS NULL OR c.bookingType = :bookingType)" +
            "AND (:isAssign IS NULL OR c.isAssign = :isAssign)" +
            "AND (:level IS NULL OR c.level = :level)" +
            "AND (:roleId IS NULL OR c.roleId = :roleId)" +
            "")
    List<RoleApprove> filterRoleApprove(BookingType bookingType, Boolean isAssign, Integer level, String roleId);

    RoleApprove findFirstByBookingTypeOrderByLevelAsc(BookingType bookingType);

    RoleApprove findFirstByBookingTypeAndIsAssign(BookingType bookingType, boolean isAssign);
}
