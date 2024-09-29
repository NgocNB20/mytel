package mm.com.mytelpay.family.model.entities;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Attendance {

    private String accountId;
    private LocalDate date;
    private LocalTime timeScan;
    private String status;


    boolean save(Attendance attendanceRequest, LocalTime timeStart, LocalTime timeEnd) {
        if (!checkTimeBeforeInsert(attendanceRequest.timeScan)) {
            return false;
        }

        List<Attendance> listDB = new ArrayList<>();
        if (listDB.size() == 0) {
            listDB.add(attendanceRequest);
            return true;
        }
        if (attendanceRequest.timeScan.isAfter(timeStart)) {
            for (int i = 1 ; i < listDB.size() ; i++) {
                if (listDB.get(i).status .equals("Approval")) {
                    attendanceRequest.setStatus("Pending");
                    saveDB(attendanceRequest);   //new record
                    return true;
                }
            }
            Attendance attendanceSecond = listDB.get(1);
            attendanceSecond.setTimeScan(attendanceRequest.getTimeScan());
            saveDB(attendanceSecond);    // update record
            return true;
        }
        return true;
    }


        Attendance findFistAsc (){
            return new Attendance();
        }

        Attendance findFistDesc (){
            return new Attendance();
        }

        void saveDB(Attendance attendance) {

        }

        void getCurrentUser() {
        String roleName = "";
        }

        boolean checkTimeBeforeInsert(LocalTime timeScan) {
            Attendance attendance = findFistDesc();
            return timeScan.isAfter(attendance.getTimeScan());
        }
    }



/*id: nguười user h*/
