package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.car.dto.CarFilterResDTO;
import mm.com.mytelpay.family.business.car.dto.CarReportDTO;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    @Query(value = "SELECT c FROM Car c WHERE c.id = :id")
    Optional<Car> getCarById(String id);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.car.dto.CarFilterResDTO(c.id, c.name, c.carType, c.licensePlate, c.model, c.status, c.createdAt)" +
            " FROM Car c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:carType IS NULL OR c.carType = :carType) " +
            "AND (:status IS NULL OR c.status = :status) ")
    Page<CarFilterResDTO> filterCar(CarType carType, String name, Status status, Pageable pageable);

    @Query(value = "select case when (count(c) > 0) then true else false end " +
            "from Car c " +
            "where c.licensePlate = :licensePlate")
    boolean existsLicensePlate(String licensePlate);

    @Query(value = "SELECT c " +
            " FROM Car c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:carType IS NULL OR c.carType = :carType) " +
            "AND (:status IS NULL OR c.status = :status) ORDER BY c.createdAt DESC ")
    List<Car> listExportCar(CarType carType, String name, Status status);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.car.dto.CarReportDTO(c.id , c.name , c.licensePlate)" +
            " FROM Car c " +
            " where ( c.id IN :carId)")
    List<CarReportDTO> getListCarDTO (List<String> carId);

    @Query(value = "SELECT c " +
            " FROM Car c" +
            " WHERE " +
            " (c.status = 'ACTIVE') " +
            " AND (c.id NOT IN :ids) ORDER BY c.createdAt DESC " +
            "")
    List<Car> getListCarForAssign(List<String> ids);
    
    List<Car> findAllByStatusOrderByCreatedAtDesc(Status status);
    
    @Query(value = "select case when (count(c) > 0) then true else false end " +
            "from Car c " +
            "where c.id = :id "
            + "AND c.name = :name "
            + "AND c.carType = :carType "
            + "AND c.licensePlate = :licensePlate ")
    boolean isExistsCar(String id, String name, CarType carType, String licensePlate);
}
