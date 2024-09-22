package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.province.dto.ProvinceFilterReqDTO;
import mm.com.mytelpay.family.business.province.dto.ProvinceHotelFilterMap;
import mm.com.mytelpay.family.model.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
    @Query(value = "SELECT p FROM Province p WHERE p.id = :id")
    Optional<Province> getProvinceById(String id);

    Optional<Province> findProvinceByName(String name);

    Optional<Province> getProvinceByCode(String code);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.province.dto.ProvinceFilterReqDTO(p.id , p.name , p.description , p.code)" +
            " FROM  Province  p WHERE ((:name IS NULL) or :name = '' OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :name,'%')))) " +
            " AND ((:code IS NULL) OR :code = '' OR (LOWER(p.code) LIKE LOWER(CONCAT('%', :code,'%'))))" )
    Page<ProvinceFilterReqDTO> filerProvince(String name, String code , Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.province.dto.ProvinceFilterReqDTO(p.id , p.name , p.description , p.code)" +
            " FROM  Province  p WHERE ((:name IS NULL) or :name = '' OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :name,'%')))) " +
            " AND ((:code IS NULL) OR :code = '' OR (LOWER(p.code) LIKE LOWER(CONCAT('%', :code,'%')))) " +
            "ORDER BY p.createdAt DESC" )
    List<ProvinceFilterReqDTO> filerProvince(String name, String code);

    @Query(value = "select case when (count(d) > 0) then true else false end " +
            "from Province p inner join District d on p.id = d.provinceId" +
            " where p.id = :id")
    boolean checkProvinceInUsed(String id);

    @Query(value = "select new mm.com.mytelpay.family.business.province.dto.ProvinceHotelFilterMap(h.id, p.code, p.name) " +
            "from Province p inner join Hotel h on p.id = h.provinceId" +
            " where h.id in :hotelIds")
    List<ProvinceHotelFilterMap> getProvincesByHotelIds(List<String> hotelIds);

}
