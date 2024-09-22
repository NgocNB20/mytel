package mm.com.mytelpay.family.models.dto;


import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ExcelCommon {
    public static final String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String TYPE_XLS = "application/vnd.ms-excel";

    public static boolean hasExcelFormat(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessEx(null, CommonException.Request.INPUT_IS_REQUIRED, "parameter.invalid.excelRequired");
        } else {
            if (!(TYPE_XLSX.equals(file.getContentType()) || TYPE_XLS.equals(file.getContentType()))) {
                throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
            }
        }
        return true;
    }


    public static String getDataFromCell(DataFormatter formatter, Cell cell) {
//        try catch
        String data = formatter.formatCellValue(cell).trim();
        return data.length() == 0 ? null : data;
    }

    public static String getDataFromCellString(DataFormatter formatter, Cell cell) {
//        try catch
        String data = formatter.formatCellValue(cell).trim();
        return data.length() == 0 ? "" : data;
    }

    // Create workbook
    public Workbook getWorkbook(String excelFilePath) throws IOException {
        Workbook workbook = null;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    // Create CellStyle for header
    public CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        return cellStyle;
    }


    // Auto resize column width
    public void autosizeColumn(Sheet sheet, int lastColumn) {
        Workbook workbook = sheet.getWorkbook();

        if (workbook instanceof HSSFWorkbook) {
            HSSFSheet hssfSheet = (HSSFSheet) sheet;
            for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
                hssfSheet.autoSizeColumn(columnIndex);
            }
        } else if (workbook instanceof XSSFWorkbook) {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
                xssfSheet.autoSizeColumn(columnIndex);
            }
        } else {
            throw new IllegalArgumentException("Workbook type not supported");
        }
    }

    // Create output file
    public ResponseEntity<ByteArrayResource> createOutputFile(Workbook workbook, String fileName) throws IOException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
            workbook.write(stream);
            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),
                    header, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    public CellStyle getCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("@"));
        style.setLocked(true);
        return style;
    }

}

