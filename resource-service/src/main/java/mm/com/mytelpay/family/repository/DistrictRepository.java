package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.district.dto.DistrictFilterReqDTO;
import mm.com.mytelpay.family.business.district.dto.DistrictHotelFilterMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import mm.com.mytelpay.family.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    @Query(value = "SELECT d FROM District  d WHERE d.id = :id ")
    Optional<District> getDistrictById(String id);

    Optional<District> findByCode(String code);

    Optional<District> findByName(String name);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.district.dto.DistrictFilterReqDTO(" +
            "d.id , " +
            "d.code , " +
            "d.name ," +
            "p.id, " +
            "p.name ," +
            "p.code, " +
            "d.description)" +
            " FROM  District d LEFT JOIN Province p ON d.provinceId = p.id " +
            " WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%',:name,'%')))" +
            " AND (:code IS NULL OR LOWER(d.code) LIKE LOWER(CONCAT('%',:code,'%')))" +
            " AND (:provinceId IS NULL OR d.provinceId = :provinceId)" +
            " ORDER BY d.createdAt DESC")
    Page<DistrictFilterReqDTO> filter(String name, String code, String provinceId, Pageable pageable);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.district.dto.DistrictFilterReqDTO(" +
            "d.id , " +
            "d.code , " +
            "d.name ," +
            "p.id, " +
            "p.name ," +
            "p.code, " +
            "d.description)" +
            " FROM  District d LEFT JOIN Province p ON d.provinceId = p.id " +
            " WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%',:name,'%')))" +
            " AND (:code IS NULL OR LOWER(d.code) LIKE LOWER(CONCAT('%',:code,'%')))" +
            " AND (:provinceId IS NULL OR d.provinceId = :provinceId)" +
            " ORDER BY d.createdAt DESC")
    List<DistrictFilterReqDTO> filter(String name, String code, String provinceId);

    @Query(value = "select new mm.com.mytelpay.family.business.district.dto.DistrictHotelFilterMap(h.id, d.code, d.name) " +
            "from District d inner join Hotel h on d.id = h.districtId" +
            " where h.id in :hotelIds")
    List<DistrictHotelFilterMap> getDistrictByHotelIds(List<String> hotelIds);

}
