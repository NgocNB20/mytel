package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.dto.WorkGroupDTO;
import mm.com.mytelpay.family.model.entities.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkGroupRepository extends JpaRepository<WorkGroup,String> {

    @Query("SELECT new mm.com.mytelpay.family.dto.WorkGroupDTO(wg.id,wg.location,wg.type) FROM WorkGroup wg")
    List<WorkGroupDTO> getWorkGroup();
}
