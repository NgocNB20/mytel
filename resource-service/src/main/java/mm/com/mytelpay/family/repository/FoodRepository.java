package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.food.dto.FoodFilterResDTO;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.model.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {

    @Query(value = "SELECT c FROM Food c WHERE c.id = :id ")
    Optional<Food> getFoodById(String id);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.food.dto.FoodFilterResDTO ( " +
            "it.id ," +
            "it.name ," +
            "it.type ," +
            "it.createdAt ) " +
            "FROM Food it " +
            "WHERE (:name IS NULL OR LOWER(it.name) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            "AND (:type IS NULL OR it.type = :type) " +
            "")
    Page<FoodFilterResDTO> filterFood(@Param("type") FoodType type,
                                      @Param("name") String name,
                                      Pageable pageable);

    @Query(value = "SELECT u FROM Food u WHERE u.id IN :ids")
    List<Food> findByListId(Set<String> ids);
}
