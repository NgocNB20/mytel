package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.meal.dto.MealDetailDbDto;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, String> {

    @Query(value = "SELECT c FROM Meal c WHERE c.id = :id ")
    Optional<Meal> getMealById(String id);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.meal.dto.MealDetailDbDto( " +
            "u.id ," +
            "u.name ," +
            "u.day ," +
            "u.type ," +
            "u.createdAt ," +
            "u.price ," +
            "c.id," +
            "c.name," +
            "v.id, " +
            "v.name," +
            "v.type," +
            "v.createdAt," +
            "z.id," +
            "z.fileName," +
            "z.url)" +
            "FROM Meal u LEFT JOIN Canteen c On u.canteenId = c.id " +
            "LEFT JOIN Menu t ON u.id = t.mealId " +
            "LEFT JOIN Food v ON t.foodIds = v.id " +
            "LEFT JOIN FileAttach z ON v.id = z.objectId" +
            " WHERE u.id = :id")
    List<MealDetailDbDto> getDetailMeal(String id);

    Optional<Meal> getMealByCanteenId(String canteenId);

    List<Meal> findByCreatedBy(String createdBy);

    @Query(value = "SELECT u FROM Meal u WHERE u.createdBy = :createdBy AND u.day = :day")
    List<Meal> findByCreatedByAndDay(String createdBy, Day day);

    @Modifying
    @Query("UPDATE Meal u SET u.name = :name WHERE u.id IN :mealIds")
    void updateName(List<String> mealIds, String name);
}
