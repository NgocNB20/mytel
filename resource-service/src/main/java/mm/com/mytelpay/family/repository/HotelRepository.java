package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.hotel.dto.HotelExportResDTO;
import mm.com.mytelpay.family.business.hotel.dto.HotelFilterResDTO;
import mm.com.mytelpay.family.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {

    @Query(value = "SELECT h" +
            " FROM Hotel h WHERE (:hotelName IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :hotelName,'%'))) " +
            "AND (:hotelCode IS NULL OR LOWER(h.code) LIKE LOWER(CONCAT('%', :hotelCode,'%'))) " +
            "AND (COALESCE(:rating, -1) = -1 OR h.rating = :rating) " +
            "AND (COALESCE(:provinceId, '') = '' OR h.provinceId = :provinceId) " +
            "AND (COALESCE(:districtId, '') = '' OR h.districtId = :districtId) " +
            "ORDER BY h.createdAt DESC")
    Page<Hotel> getListFilterHotel(String hotelCode, String hotelName, Integer rating, String provinceId, String districtId, Pageable pageable);

    @Query(value = "SELECT h" +
            " FROM Hotel h WHERE (:hotelName IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :hotelName,'%'))) " +
            "AND (:hotelCode IS NULL OR LOWER(h.code) LIKE LOWER(CONCAT('%', :hotelCode,'%'))) " +
            "AND (COALESCE(:rating, -1) = -1 OR h.rating = :rating) " +
            "AND (COALESCE(:provinceId, '') = '' OR h.provinceId = :provinceId) " +
            "AND (COALESCE(:districtId, '') = '' OR h.districtId = :districtId) " +
            "AND (h.rolesAllow IS NULL OR h.rolesAllow = '' OR (" +
            "FIND_IN_SET(:endUserRole, h.rolesAllow) > 0 " +
            "OR FIND_IN_SET(:adminRole, h.rolesAllow) > 0 " +
            "OR FIND_IN_SET(:chefRole, h.rolesAllow) > 0 " +
            "OR FIND_IN_SET(:driverRole, h.rolesAllow) > 0 " +
            "OR FIND_IN_SET(:driverManagerRole, h.rolesAllow) > 0 " +
            "OR FIND_IN_SET(:directorRole, h.rolesAllow) > 0)) " +
            "ORDER BY h.createdAt DESC")
    Page<Hotel> getListFilterHotel(String hotelCode, String hotelName, Integer rating, String provinceId, String districtId,
                                   String endUserRole, String adminRole, String chefRole, String driverRole, String driverManagerRole,
                                   String directorRole, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.hotel.dto.HotelFilterResDTO(" +
            "h.id, h.name, h.phone, h.address, h.rating, h.description)" +
            " FROM Hotel h WHERE (h.id IN :ids) ORDER BY h.createdAt DESC")
    List<HotelFilterResDTO> findByHotelId(List<String> ids);

    List<Hotel> findByDistrictId(String districtId);

    Optional<Hotel> findByCode(String code);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.hotel.dto.HotelExportResDTO( " +
            "h.code, h.name, h.phone, h.address, h.rolesAllow, h.rating, d.code, p.code, h.maxPrice, h.maxPlusPrice, h.description)" +
            " FROM Hotel h LEFT JOIN Province p ON h.provinceId = p.id LEFT JOIN District d ON h.districtId = d.id " +
            "WHERE (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:hotelCode IS NULL OR LOWER(h.code) LIKE LOWER(CONCAT('%', :hotelCode,'%'))) " +
            "AND (COALESCE(:provinceId, '') = '' OR h.provinceId = :provinceId) " +
            "AND (COALESCE(:districtId, '') = '' OR h.districtId = :districtId) " +
            "AND (COALESCE(:rating, -1) = -1 OR h.rating = :rating) ORDER BY h.createdAt desc ")
    List<HotelExportResDTO> listExportExcelHotel(String name, String hotelCode, String provinceId, String districtId, Integer rating);

}
