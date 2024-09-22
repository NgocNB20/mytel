package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.menu.dto.MenuFilterDbDto;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

    @Query(value = "SELECT c FROM Menu c WHERE c.id = :id")
    Optional<Menu> getMenuById(String id);

    @Modifying
    @Query("DELETE FROM Menu u WHERE u.mealId IN :mealIds")
    void deleteByMealIds(List<String> mealIds);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.menu.dto.MenuFilterDbDto(" +
            "u.day," +
            "u.name," +
            "u.id," +
            "u.type," +
            "u.price," +
            "u.createdAt," +
            "v.id," +
            "v.name," +
            "v.type," +
            "v.createdAt," +
            "z.id," +
            "z.fileName," +
            "z.url) " +
            "FROM Meal u join Menu t on u.id = t.mealId join Food v on t.foodIds = v.id left join FileAttach z on v.id = z.objectId " +
            "WHERE (:day IS NULL OR u.day = :day) AND (:canteenId IS NULL OR u.canteenId = :canteenId)")
    List<MenuFilterDbDto> filterMenu(Day day,String canteenId);

    @Modifying
    @Query("DELETE FROM Menu u WHERE u.foodIds = :foodId")
    void deleteByFoodId(String foodId);
}
