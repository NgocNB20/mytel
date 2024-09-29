package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyStatistic {
    private static final Map<String,String> message = new HashMap<>();
    static {
        message.put("NORMAL","normal");
        message.put("ABSENT","absent");
        message.put("LATE","late");
        message.put("MISSING","missing");
        message.put("EARLY","");
    }
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private String resultClockIn;
    private String resultClockOut;
    private LocalTime shiftClockIn;  // 08:00
    private LocalTime shiftClockOut; // 17:30

    public DailyStatistic(LocalDateTime clockIn, LocalDateTime clockOut, LocalTime shiftClockIn, LocalTime shiftClockOut) {
        this.clockIn = clockIn;
        this.clockOut = clockOut;
        this.shiftClockIn = shiftClockIn;
        this.shiftClockOut = shiftClockOut;

        // Tối ưu logic kiểm tra thời gian clockIn
        if (clockIn==null) {
            this.resultClockIn="absent";
            this.resultClockOut="absent";
        } else {
            this.resultClockIn = formatLateMessage(clockIn,shiftClockIn);
            if (clockOut==null) {
                this.resultClockOut="missing";
            } else {
                this.resultClockOut = formatEarlyMessage(clockOut,shiftClockOut);
            }

        }

    }
    private  String formatLateMessage(LocalDateTime clockIn, LocalTime shiftClockIn) {

        Duration duration = Duration.between(shiftClockIn, clockIn.toLocalTime());

        long minutesLate = duration.toMinutes();

        if (minutesLate <= 0) {
            return "Normal";
        }

        // Tính giờ và phút
        long hoursLate = minutesLate / 60;
        long remainingMinutesLate = minutesLate % 60;

        // Tạo thông báo
        if (hoursLate > 0) {
            return String.format("In %d hours %d minutes late", hoursLate, remainingMinutesLate);
        } else {
            return String.format("In %d minutes late", remainingMinutesLate);
        }
    }

    private String formatEarlyMessage(LocalDateTime clockOut, LocalTime shiftClockOut) {

        // Tính toán sự chênh lệch giữa giờ thực tế clockOut và giờ kết thúc ca shiftClockOut
        Duration duration = Duration.between(clockOut.toLocalTime(), shiftClockOut);

        long minutesEarly = duration.toMinutes();

        // Nếu không đi sớm (tức là minutesEarly <= 0), trả về "Normal"
        if (minutesEarly <= 0) {
            return "Normal";
        }

        // Tính giờ và phút
        long hoursEarly = minutesEarly / 60;
        long remainingMinutesEarly = minutesEarly % 60;

        // Tạo thông báo
        if (hoursEarly > 0) {
            return String.format("Out %d hours %d minutes early", hoursEarly, remainingMinutesEarly);
        } else {
            return String.format("Out %d minutes early", remainingMinutesEarly);
        }
    }


    public static void main(String[] args) {
        LocalDateTime clockIn = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 43, 0));
        LocalDateTime clockOut = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 01, 0));
        LocalTime shiftClockIn = LocalTime.of(8, 30, 0);
        LocalTime shiftClockOut = LocalTime.of(17, 30, 0);
        DailyStatistic statistic = new DailyStatistic(clockIn, null, shiftClockIn, shiftClockOut);
        System.out.println(statistic);

    }

}

