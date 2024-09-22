package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.unit.dto.UnitFilterResDTO;
import mm.com.mytelpay.family.business.unit.dto.UnitForCanteenDTO;
import mm.com.mytelpay.family.model.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {

    @Query(value = "SELECT c FROM Unit c WHERE c.id = :id ")
    Optional<Unit> getUnitById(String id);

    Unit findFirstById(String id);

    @Query(value = "SELECT c FROM Unit c WHERE c.code = :code ")
    Optional<Unit> getUnitByCode(String code);

    Optional<Unit> findUnitByCode(String code);
    Integer countUnitByCode(String code);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.unit.dto.UnitFilterResDTO ( " +
            "m.id ," +
            "m.name ," +
            "m.code  ) " +
            "FROM Unit m " +
            "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            "AND (:code IS NULL OR LOWER(m.code) LIKE LOWER(CONCAT('%',:code,'%'))) ")
    Page<UnitFilterResDTO> filterUnit(@Param("name") String name,
                                      @Param("code") String code,
                                      Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.unit.dto.UnitFilterResDTO (m.id, m.name, m.code) FROM Unit m ORDER BY m.createdAt DESC")
    List<UnitFilterResDTO> getAllWithoutPagination();

    @Query(value = "SELECT CASE WHEN (COUNT(acc) > 0) THEN TRUE ELSE FALSE END " +
            "FROM Unit u JOIN Account acc ON acc.unitId = u.id " +
            "WHERE u.id = :id AND acc.status = 'ACTIVE'")
    boolean existsUserInUnit(String id);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.unit.dto.UnitForCanteenDTO(" +
            "m.id, " +
            "m.name, " +
            "m.code )" +
            "FROM Unit m " +
            "WHERE (coalesce(:unitId) IS NULL OR m.id IN (:unitId))")
    List<UnitForCanteenDTO> getListUnit (List<String> unitId);
    Optional<Unit> findById(String id);
}
