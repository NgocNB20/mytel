package mm.com.mytelpay.family.business.district;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.district.dto.DIstrictImportError;
import mm.com.mytelpay.family.business.district.dto.DistrictFilterReqDTO;
import mm.com.mytelpay.family.business.district.dto.DistrictFilterResDTO;
import mm.com.mytelpay.family.business.district.dto.DistrictImportResDTO;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.District;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repository.DistrictRepository;
import mm.com.mytelpay.family.repository.ProvinceRepository;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DistrictExcelServiceImpl extends FamilyBaseBusiness implements DistrictExcelService {
    private static final String DISTRICT_NAME_HEADER = "District Name";
    private static final String DESCRIPTION_HEADER = "Description";
    private static final String[] HEADER_ERROR = {"No", "Code", DISTRICT_NAME_HEADER, "ProvinceCode", DESCRIPTION_HEADER, "Message"};
    private static final String[] HEADER_DISTRICT = {"No", "Code", DISTRICT_NAME_HEADER, "Province Name", DESCRIPTION_HEADER};
    private static final String[] HEADER_IMPORT = {"No", "Code", DISTRICT_NAME_HEADER, "Province Code", DESCRIPTION_HEADER};
    private static final String SHEET = "District";
    private static final String EXCEL_DISTRICT = "District-";
    private static final String EXCEL_DISTRICT_TEMPLATE = "Template_District-";
    private static final String EXCEL_ERROR = "ImportResult-";

    @Autowired
    DistrictRepository districtRepository;
    @Autowired
    ProvinceRepository provinceRepository;

    @Override
    public ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException {
        List<District> districts = new ArrayList<>();
        List<DistrictImportResDTO> districtImportResDTOS = new ArrayList<>();
        List<DIstrictImportError> importErrors = new ArrayList<>();
        if (inputFile.isEmpty()) {
            throw new BusinessEx(ResourceErrorCode.Car.IMPORT_FAIL, null);
        }
        boolean isXls = Objects.requireNonNull(inputFile.getOriginalFilename()).endsWith("xls");
        try (InputStream file = inputFile.getInputStream()) {
            Workbook workbook;
            Sheet sheet;
            if (isXls) {
                workbook = new HSSFWorkbook(file);
                sheet = workbook.getSheetAt(0);
            } else {
                workbook = new XSSFWorkbook(file);
                sheet = workbook.getSheetAt(0);
            }
            checkHeader(sheet);
            Iterator<Row> rows = sheet.iterator();
            DataFormatter formatter = new DataFormatter();
            if (sheet.getPhysicalNumberOfRows() > Constants.MAX_LIMIT_200_IMPORT_EXCEL)
                throw new BusinessEx(null, CommonException.File.LIMIT_ROW_IMPORT, null);

            List<String> codes = new ArrayList<>();
            while (rows.hasNext()) {
                readRowExcel(districtImportResDTOS, importErrors, rows, formatter, codes);
            }
            for (DistrictImportResDTO dto : districtImportResDTOS)
                districts.add(mapperDistrictImportResDTOToDistrict(dto));

            districtRepository.saveAll(districts);
            insertActionLog(null, perRequestContextDto.getCurrentAccountId(), ActionType.IMPORT_DISTRICT, null);
            return exportExcelError(importErrors);
        } catch (IOException e) {
            throw new BusinessEx(null, ResourceErrorCode.District.IMPORT_FAIL, null);
        }
    }

    private void readRowExcel(List<DistrictImportResDTO> districtImportResDTOS, List<DIstrictImportError> importErrors, Iterator<Row> rows, DataFormatter formatter, List<String> codes) {
        Row currentRow = rows.next();
        if (currentRow.getRowNum() >= 1) {
            Iterator<Cell> cellInRow = currentRow.iterator();
            DistrictImportResDTO districtImport = new DistrictImportResDTO();
            String errorMessage = "";
            while (cellInRow.hasNext()) {
                Cell cell = cellInRow.next();
                if (cell != null) {
                    int cellIndex = cell.getColumnIndex();
                    String cellVal = Objects.requireNonNull(ExcelCommon.getDataFromCell(formatter, cell)).trim();
                    switch (cellIndex) {
                        case 0:
                            districtImport.setId(cellVal);
                            break;
                        case 1:
                            districtImport.setCode(cellVal);
                            errorMessage = checkCode(codes, errorMessage, cellVal);
                            break;
                        case 2:
                            districtImport.setName(cellVal);
                            errorMessage = checkName(errorMessage, cellVal);
                            break;
                        case 3:
                            districtImport.setProvinceCode(cellVal);
                            errorMessage = checkProvinceCode(errorMessage, cellVal);
                            break;
                        case 4:
                            districtImport.setDescription(cellVal);
                            errorMessage = checkDescription(errorMessage, cellVal);
                            break;
                        default:
                            break;
                    }
                }
            }
            errorMessage = checkValidDistrict(errorMessage, districtImport);
            if (StringUtils.isBlank(errorMessage)) {
                districtImportResDTOS.add(districtImport);
                importErrors.add(new DIstrictImportError(districtImport, "Success,"));
                codes.add(districtImport.getCode());
            } else importErrors.add(new DIstrictImportError(districtImport, errorMessage));
        }
    }

    private String checkDescription(String errorMessage, String cellVal) {
        if (cellVal.length() > 255) errorMessage += "Description is invalid,";
        return errorMessage;
    }

    private String checkProvinceCode(String errorMessage, String cellVal) {
        if (cellVal.length() > 50) {
            errorMessage += "ProvinceCode is invalid,";
        }
        Optional<Province> province = provinceRepository.getProvinceByCode(cellVal);
        if (province.isEmpty()) errorMessage += "Province does not exists,";
        return errorMessage;
    }

    private String checkName(String errorMessage, String cellVal) {
        if (cellVal.length() > 100) {
            errorMessage += "Name is invalid,";
        }
        return errorMessage;
    }

    private String checkCode(List<String> codes, String errorMessage, String cellVal) {
        if (cellVal.length() > 50) {
            errorMessage += "Code is invalid,";
        }
        if (codes.contains(cellVal)) {
            errorMessage += "Code was exists,";
        }
        Optional<District> districtByCode = districtRepository.findByCode(cellVal);
        if (districtByCode.isPresent()) errorMessage += "Code was exists,";
        return errorMessage;
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(DistrictFilterResDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<DistrictFilterReqDTO> list = districtRepository.filter(request.getName(),
                request.getCode(),
                StringUtils.isBlank(request.getProvinceId()) ? null : request.getProvinceId().trim());
        return exportExcelDistrict(list);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadTemplate(HttpServletRequest httpServletRequest) throws IOException {
        List<DistrictImportResDTO> districts = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            DistrictImportResDTO district = new DistrictImportResDTO();
            district.setId(UUID.randomUUID().toString());
            district.setCode("Code 00" + i);
            district.setName("District " + i);
            district.setProvinceCode("ProvinceCode 00" + i);
            district.setDescription("Description district " + i);
            districts.add(district);
        }
        return exportExcelTemplate(districts);
    }

    private void checkHeader(Sheet sheet) {
        for (int col = 0; col < sheet.getRow(0).getPhysicalNumberOfCells(); col++) {
            try {
                if (!HEADER_IMPORT[col].equals(sheet.getRow(0).getCell(col).toString())) {
                    throw new BusinessEx(null, ResourceErrorCode.District.IMPORT_FAIL, null);
                }
            } catch (Exception e) {
                throw new BusinessEx(null, ResourceErrorCode.District.IMPORT_FAIL, null);
            }
        }
    }

    private ResponseEntity<ByteArrayResource> exportExcelError(List<DIstrictImportError> importErrors) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = EXCEL_ERROR + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADER_ERROR.length, HEADER_ERROR);
        int rowIdx = 1;
        int rowNumberIndex = 1;
        for (DIstrictImportError importError : importErrors) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumberIndex++);
            row.createCell(1).setCellValue(importError.getDto().getCode());
            row.createCell(2).setCellValue(importError.getDto().getName());
            row.createCell(3).setCellValue(importError.getDto().getProvinceCode());
            row.createCell(4).setCellValue(importError.getDto().getDescription());
            String error = importError.getRes().substring(0, importError.getRes().length() - 1);
            row.createCell(5).setCellValue(error);
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private District mapperDistrictImportResDTOToDistrict(DistrictImportResDTO dto) {
        District district = new District();
        district.setId(UUID.randomUUID().toString());
        district.setName(dto.getName());
        district.setDescription(dto.getDescription());
        district.setCode(dto.getCode());
        Optional<Province> province = provinceRepository.getProvinceByCode(dto.getProvinceCode());
        if (province.isEmpty()) {
            throw new BusinessEx(ResourceErrorCode.Province.NOT_FOUND, null);
        }
        district.setProvinceId(province.get().getId());
        return district;
    }

    private ResponseEntity<ByteArrayResource> exportExcelDistrict(List<DistrictFilterReqDTO> districts) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = EXCEL_DISTRICT + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADER_DISTRICT.length, HEADER_DISTRICT);
        int rowIdx = 1;
        int rowNumber = 1;
        for (DistrictFilterReqDTO district : districts) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(district.getCode());
            row.createCell(2).setCellValue(district.getName());
            row.createCell(3).setCellValue(district.getProvinceName());
            row.createCell(4).setCellValue(district.getDescription());
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private ResponseEntity<ByteArrayResource> exportExcelTemplate(List<DistrictImportResDTO> districts) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = EXCEL_DISTRICT_TEMPLATE + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADER_IMPORT.length, HEADER_IMPORT);
        int rowIdx = 1;
        int rowNumber = 1;
        for (DistrictImportResDTO district : districts) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(district.getCode());
            row.createCell(2).setCellValue(district.getName());
            row.createCell(3).setCellValue(district.getProvinceCode());
            row.createCell(4).setCellValue(district.getDescription());
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private String checkValidDistrict(String errorMessage, DistrictImportResDTO district) {
        if (StringUtils.isBlank(district.getCode())) errorMessage = "Code is empty," + errorMessage;
        if (StringUtils.isBlank(district.getName())) errorMessage += "Name is empty,";
        if (StringUtils.isBlank(district.getProvinceCode())) errorMessage += "Province code is empty,";
        return errorMessage;
    }

}
