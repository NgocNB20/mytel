package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.BookingCarHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingCarHistoryRepository extends JpaRepository<BookingCarHistory, String> {

}
