package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.BookingHotelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingHotelHistoryRepository extends JpaRepository<BookingHotelHistory, String> {

}
