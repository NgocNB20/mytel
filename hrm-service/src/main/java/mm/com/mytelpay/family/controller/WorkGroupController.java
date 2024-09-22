package mm.com.mytelpay.family.controller;

import lombok.RequiredArgsConstructor;
import mm.com.mytelpay.family.dto.WorkGroupDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repository.WorkGroupRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/public/workgroup")
@RequiredArgsConstructor
public class WorkGroupController {
    private final WorkGroupRepository repository;

    @GetMapping
    public List<WorkGroupDTO> get () {
        List<WorkGroupDTO> workGroupDTOS = repository.getWorkGroup();
        for (int i = 0; i < workGroupDTOS.size() ; i++) {
            WorkGroupDTO w = workGroupDTOS.get(i);
                w.setType("1111111111111111");

        }
        return workGroupDTOS;
    }

    public static void main(String[] args) {
        ExcelCommon common = new ExcelCommon();
        LocalDate start = LocalDate.of(2024,9,1);
        LocalDate end = LocalDate.of(2024,9,30);
        long countDay = Duration.between(start.atStartOfDay(),end.atStartOfDay()).toDays();
        // Tạo một workbook mới (file Excel)
        Workbook workbook = new XSSFWorkbook(); // Dùng XSSFWorkbook cho định dạng .xlsx

        // Tạo một sheet mới trong workbook
        Sheet sheet = workbook.createSheet("Employees");

        // Tạo hàng đầu tiên (header row)
        Row headerRow = sheet.createRow(0);

        // Tạo các ô trong hàng đầu tiên (header)
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Name");

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Department");

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("Salary");
        int init=0;
        while (init<countDay) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd EEE");
            String formattedMonth = start.plusDays(init).format(formatter);
            headerRow.createCell(init+3).setCellValue(formattedMonth);
            init++;
        }
        for (int rowIndex = 1 ; rowIndex <= 5 ; rowIndex++) {
            int initCell=0;
            Row dataRow = sheet.createRow(rowIndex);
            dataRow.createCell(0).setCellValue("John Doe");
            dataRow.createCell(1).setCellValue("Sales");
            dataRow.createCell(2).setCellValue(5000);
            while (initCell<countDay) {
                dataRow.createCell(initCell+3).setCellValue(checkDailyDay(start.plusDays(initCell)));
                initCell++;
            }
        }



        // Đường dẫn đến folder
        String folderPath = "C:/Users/HLC/Desktop/test/"; // Thay đổi thành đường dẫn của bạn
        File directory = new File(folderPath);

        // Kiểm tra folder tồn tại, nếu không thì tạo mới
        if (!directory.exists()) {
            directory.mkdirs();
        }
        common.autosizeColumn(sheet,(int)(countDay+3));
        // Tạo một file Excel và ghi dữ liệu vào
        try (FileOutputStream fileOut = new FileOutputStream(folderPath+"employees.xlsx")) {
            workbook.write(fileOut); // Ghi workbook vào file
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File Excel đã được tạo thành công!");
    }
    private static String checkDailyDay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd EEE");
        Random random = new Random();
        int randomInt = random.nextInt(100);
        return  date.format(formatter)+ " "+randomInt;
    }
}
