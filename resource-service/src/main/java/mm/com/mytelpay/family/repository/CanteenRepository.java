package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.canteen.dto.CanteenFilterResDTO;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.model.Canteen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanteenRepository extends JpaRepository<Canteen, String> {


    @Query(value = "SELECT c FROM Canteen c WHERE c.id = :id and c.status = :status ")
    Optional<Canteen> getCanteenById(String id, Status status);

    @Query(value = "SELECT c FROM Canteen c WHERE c.id = :id ")
    Optional<Canteen> getCanteenByIdNoStatus(String id);

    Optional<Canteen> findByCode(String code);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.canteen.dto.CanteenFilterResDTO ( " +
            "m.id ," +
            "m.code," +
            "m.name, " +
            "m.unitId," +
            "m.seats ," +
            "m.address , " +
            "m.description ) " +
            "FROM Canteen m " +
            "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            "AND (:unitId IS NULL OR m.unitId = :unitId) " +
            "AND (:code IS NULL OR LOWER(m.code) LIKE LOWER(CONCAT('%',:code,'%'))) " +
            "ORDER BY m.createdAt DESC")
    Page<CanteenFilterResDTO> filterCanteen(String name,
                                            String unitId,
                                            String code,
                                            Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.canteen.dto.CanteenFilterResDTO ( " +
            "m.id ," +
            "m.code," +
            "m.name, " +
            "m.unitId," +
            "m.seats ," +
            "m.address , " +
            "m.description ) " +
            "FROM Canteen m " +
            "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            "AND (:unitId IS NULL OR m.unitId = :unitId) " +
            "AND (:code IS NULL OR LOWER(m.code) LIKE LOWER(CONCAT('%',:code,'%'))) " +
            "ORDER BY m.createdAt DESC")
    List<CanteenFilterResDTO> getCanteenExport(String name,
                                               String unitId,
                                               String code);

    @Query(value = "SELECT c FROM Canteen c WHERE c.id in :ids")
    List<Canteen> findAllByIdIn(List<String> ids);

    List<Canteen> findByUnitId(String unitId);

    Optional<Canteen> findCanteenByUnitId(String unitId);

    Optional<Canteen> findById(String canteenId);
}
