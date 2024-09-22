package mm.com.mytelpay.family.business.hotel;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.hotel.dto.HotelExportReqDTO;
import mm.com.mytelpay.family.business.hotel.dto.HotelExportResDTO;
import mm.com.mytelpay.family.business.hotel.dto.HotelImportDTO;
import mm.com.mytelpay.family.business.hotel.dto.HotelImportResultResDTO;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.District;
import mm.com.mytelpay.family.model.Hotel;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.repository.DistrictRepository;
import mm.com.mytelpay.family.repository.HotelRepository;
import mm.com.mytelpay.family.repository.ProvinceRepository;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HotelExcelServiceImpl extends FamilyBaseBusiness implements HotelExcelService {

    public HotelExcelServiceImpl() {
        logger = LogManager.getLogger(HotelExcelServiceImpl.class);
    }

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    private static final String[] HEADERS = {"No", "Name", "Code", "Province Code", "District Code", "Address",
            "Phone", "Description", "Roles Allow", "Rating", "Max Price", "Max Plus Price", "Message"};
    private static final String SHEET = "Hotel";

    private static final int COLUMN_INDEX_NO = 0;
    private static final int COLUMN_INDEX_NAME = 1;
    private static final int COLUMN_INDEX_CODE = 2;
    private static final int COLUMN_INDEX_PROVINCE_CODE = 3;
    private static final int COLUMN_INDEX_DISTRICT_CODE = 4;
    private static final int COLUMN_INDEX_ADDRESS = 5;
    private static final int COLUMN_INDEX_PHONE = 6;
    private static final int COLUMN_INDEX_DESCRIPTION = 7;
    private static final int COLUMN_INDEX_ROLE_ALLOW = 8;
    private static final int COLUMN_INDEX_RATING = 9;
    private static final int COLUMN_INDEX_MAX_PRICE = 10;
    private static final int COLUMN_INDEX_MAX_PLUS_PRICE = 11;

    @Override
    public ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException {
        List<HotelImportResultResDTO> resImport = new ArrayList<>();
        List<Hotel> hotels = hotelRepository.findAll();
        List<District> districts = districtRepository.findAll();
        List<Province> provinces = provinceRepository.findAll();
        boolean checkFile = checkFileExcel(inputFile);
        Set<String> validCode = new HashSet<>();
        try {
            InputStream file = inputFile.getInputStream();
            Workbook workbook;
            if (checkFile) {
                workbook = new HSSFWorkbook(file);
            } else {
                workbook = new XSSFWorkbook(file);
            }
            Sheet sheet = workbook.getSheetAt(0);
            checkHeader(sheet);
            Iterator<Row> rows = sheet.iterator();
            checkLimitRowExcel(sheet);
            if (rows.hasNext()) rows.next();
            readRowExcel(rows, validCode, resImport, hotels, provinces, districts, sheet);
            return downloadResultImport(resImport);
        } catch (FileNotFoundException e){
            return null;
        } catch (IOException e) {
            throw new BusinessEx(ResourceErrorCode.Hotel.IMPORT_FAIL, null);
        }
    }

    private void readRowExcel(Iterator<Row> rows, Set<String> validCode, List<HotelImportResultResDTO> resImport, List<Hotel> hotels, List<Province> provinces, List<District> districts, Sheet sheet){
        List<Hotel> res = new ArrayList<>();
        while (rows.hasNext()){
            Row currentRow = rows.next();
            Hotel hotel = new Hotel();
            HotelImportDTO hotelImportDTO = new HotelImportDTO();
            int cellIdx = 0;
            String errorMessage = "";
            for (int cellNum = 0 ;  cellNum < sheet.getRow(0).getPhysicalNumberOfCells(); cellNum++){
                Cell currentCell = currentRow.getCell(cellNum, Row.CREATE_NULL_AS_BLANK);
                DataFormatter dataFormatter = new DataFormatter();
                String cellValue = StringUtils.trim(dataFormatter.formatCellValue(currentCell));
                switch (cellIdx){
                    case 1:
                        errorMessage = checkName(errorMessage, cellValue, hotel);
                        hotelImportDTO.setName(cellValue);
                        break;
                    case 2:
                        errorMessage = checkCode(errorMessage, cellValue, hotels, validCode, hotel);
                        hotelImportDTO.setCode(cellValue);
                        break;
                    case 3:
                        errorMessage = checkProvinceCode(errorMessage , cellValue, hotel, provinces);
                        hotelImportDTO.setProvinceCode(cellValue);
                        break;
                    case 4:
                        errorMessage = checkDistrictCode(errorMessage, cellValue, hotel, districts);
                        hotelImportDTO.setDistrictCode(cellValue);
                        break;
                    case 5:
                        errorMessage = checkAddress(errorMessage, cellValue, hotel);
                        hotelImportDTO.setAddress(cellValue);
                        break;
                    case 6:
                        errorMessage = checkPhone(errorMessage, cellValue, hotel);
                        hotelImportDTO.setPhone(cellValue);
                        break;
                    case 7:
                        errorMessage = checkDescription(errorMessage, cellValue, hotel);
                        hotelImportDTO.setDescription(cellValue);
                        break;
                    case 8:
                        errorMessage = checkRoleAllow(errorMessage, cellValue, hotel);
                        hotelImportDTO.setRolesAllow(cellValue);
                        break;
                    case 9:
                        errorMessage = checkRating(errorMessage, cellValue, hotel);
                        hotelImportDTO.setRating(cellValue);
                        break;
                    case 10:
                        errorMessage = checkMaxPrice(errorMessage, cellValue , hotel);
                        hotelImportDTO.setMaxPrice(cellValue);
                        break;
                    case 11:
                        errorMessage = checkMaxPlusPrice(errorMessage, cellValue , hotel);
                        hotelImportDTO.setMaxPlusPrice(cellValue);
                        break;
                    default:
                        break;
                }
                cellIdx++;
            }
            if(StringUtils.isEmpty(errorMessage)){
                res.add(hotel);
                errorMessage += "Success ";
                validCode.add(hotel.getCode());
            }
            resImport.add(new HotelImportResultResDTO(hotelImportDTO, errorMessage));
        }
        hotelRepository.saveAll(res);
    }

    private String checkMaxPlusPrice(String errorMessage, String maxPlusPrice, Hotel hotel){
        if (StringUtils.isEmpty(maxPlusPrice)) {
            errorMessage += " Max Plus Price is Empty,";
        }else {
            if(!maxPlusPrice.matches(Constants.POSITIVE_NUMBER_REGEX) || !checkValidPrice(maxPlusPrice)){
                errorMessage += " Max Plus Price is invalid,";
            }
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setMaxPlusPrice(Integer.valueOf(maxPlusPrice));
        }
        return errorMessage;
    }
    private String checkMaxPrice(String errorMessage, String maxPrice, Hotel hotel){
        if (StringUtils.isEmpty(maxPrice)) {
            errorMessage += " Max Price is Empty,";
        }else {
            if(!maxPrice.matches(Constants.POSITIVE_NUMBER_REGEX) || !checkValidPrice(maxPrice)){
                errorMessage += " Max Price is invalid,";
            }
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setMaxPrice(Integer.valueOf(maxPrice));
        }
        return errorMessage;
    }

    private boolean checkValidPrice(String priceString) {
        int maxLengthOfPrice = 9;
        return priceString.length() <= maxLengthOfPrice;
    }

    private String checkRating(String errorMessage, String rating, Hotel hotel){
        if (StringUtils.isEmpty(rating)) {
            errorMessage += " Rating is Empty,";
        }else {
            if(!rating.matches(Constants.REGEX_RATING)){
                errorMessage += " Rating can only receive values from 1 to 5,";
            }
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setRating(Integer.valueOf(rating));
        }
        return errorMessage;
    }
    private String checkRoleAllow(String errorMessage, String roleAllow, Hotel hotel){
        List<String> listRoleAllow = new ArrayList<>();
        if (StringUtils.isEmpty(roleAllow)) {
            errorMessage += " Roles allow is empty,";
        }else {
            StringBuilder errorBuilder = new StringBuilder();
            String[] roleAllows = roleAllow.split(",");
            for (String role : roleAllows) {
                String element = role.trim();
                listRoleAllow.add(element);
            }
            for (String checkRole : listRoleAllow) {
                try {
                    RoleType.valueOf(checkRole);
                } catch (IllegalArgumentException e) {
                    errorBuilder.append(" Roles allow is invalid,");
                    break;
                }

            }
            errorMessage += errorBuilder.toString();
        }
        if (StringUtils.isEmpty(errorMessage)){
            String role = String.join(",", listRoleAllow);
            hotel.setRolesAllow(role);
        }
        return errorMessage;
    }
    private String checkDescription(String errorMessage, String description, Hotel hotel){
        if(description.length() > 255){
            errorMessage += " Description cannot be more than 255 characters,";
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setDescription(description);
        }
        return errorMessage;
    }
    private String checkPhone(String errorMessage, String phone, Hotel hotel){
        int phoneLength = phone.length();
        int minPhoneLength = 8;
        int maxPhoneLength = 15;
        if (StringUtils.isEmpty(phone)) {
            errorMessage += " Phone is empty,";
        }
        else if(minPhoneLength > phoneLength || maxPhoneLength < phoneLength){
            errorMessage += " Phone no less than " + minPhoneLength + " characters and no more than " + maxPhoneLength + " characters,";
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setPhone(phone);
        }
        return errorMessage;
    }
    private String checkAddress(String errorMessage, String address, Hotel hotel){
        if (StringUtils.isEmpty(address)) {
            errorMessage += " Address is empty,";
        }
        else if(address.length() > 255) {
            errorMessage += " Address cannot be more than 255 characters,";
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setAddress(address);
        }
        return errorMessage;
    }
    private String checkDistrictCode(String errorMessage, String districtCode, Hotel hotel, List<District> districts){

        if (StringUtils.isEmpty(districtCode)) {
            errorMessage += " District code is Empty,";
        }else {
            boolean check = false;
            StringBuilder errorBuilder = new StringBuilder();
            for (District district: districts){
                if(districtCode.equals(district.getCode())){
                    if(StringUtils.isNotEmpty(hotel.getProvinceId()) && StringUtils.equals(hotel.getProvinceId(), district.getProvinceId())){
                        hotel.setDistrictId(district.getId());
                    } else {
                        errorBuilder.append(" District is not part of province,");
                    }
                    check = true;
                    break;
                }
            }
            errorMessage += errorBuilder.toString();
            if(!check){
                errorMessage += " District code does not exists,";
            }
        }
        return errorMessage;
    }
    private String checkProvinceCode(String errorMessage, String provinceCode, Hotel hotel, List<Province> provinces) {

        if (StringUtils.isEmpty(provinceCode)) {
            errorMessage += " Province code is Empty,";
        }else {
            boolean check = false;
            for (Province province: provinces) {
                if(provinceCode.equals(province.getCode())){
                    hotel.setProvinceId(province.getId());
                    check = true;
                    break;
                }
            }
            if(!check){
                errorMessage += " Province code does not exists,";
            }
        }
        return errorMessage;
    }
    private String checkCode(String errorMessage, String code, List<Hotel> hotels, Set<String> validCode, Hotel hotel){
        if (StringUtils.isEmpty(code)) {
            errorMessage += " Code is Empty,";
        }
        else {
            if (code.length() > 50) {
                errorMessage += " Code cannot be more than 50 characters,";
            }
            if(validCode.contains(code)){
                errorMessage += " code was exists,";
            }
            StringBuilder errorBuilder = new StringBuilder();
            for (Hotel checkCodeHotel: hotels) {
                if(code.equals(checkCodeHotel.getCode())){
                    errorBuilder.append(" code was exists,");
                    break;
                }
            }
            errorMessage += errorBuilder.toString();
        }

        if (StringUtils.isEmpty(errorMessage)){
            hotel.setCode(code);
        }
        return errorMessage;
    }

    private String checkName(String errorMessage, String name, Hotel hotel) {
        if (StringUtils.isEmpty(name)) {
            errorMessage += " Name is Empty,";
        }
        if (name.length() > 100) {
            errorMessage += " Name cannot be more than 100 characters,";
        }
        if (StringUtils.isEmpty(errorMessage)){
            hotel.setName(name);
        }
        return errorMessage;
    }
    private void checkLimitRowExcel(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > Constants.MAX_LIMIT_200_IMPORT_EXCEL) {
            throw new BusinessEx(null, CommonException.File.LIMIT_ROW_IMPORT, null);
        }
    }

    private void checkHeader(Sheet sheet) {
        for (int col = 0; col < sheet.getRow(0).getPhysicalNumberOfCells(); col++) {
            try {
                if (!HEADERS[col].equals(sheet.getRow(0).getCell(col).toString())) {
                    throw new BusinessEx(null, ResourceErrorCode.Hotel.IMPORT_FAIL, null);
                }
            } catch (Exception e) {
                throw new BusinessEx(null, ResourceErrorCode.Hotel.IMPORT_FAIL, null);
            }
        }
    }

    private boolean checkFileExcel(MultipartFile inputFile) {
        if (ObjectUtils.isEmpty(inputFile) && Objects.isNull(inputFile.getOriginalFilename())) {
            throw new RequestEx(null, ResourceErrorCode.Hotel.IMPORT_FAIL, null);
        }
        return Objects.requireNonNull(inputFile.getOriginalFilename()).endsWith("xls");
    }



    private ResponseEntity<ByteArrayResource> downloadResultImport(List<HotelImportResultResDTO> hotels) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Hotel_Error" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADERS.length, HEADERS);
        int rowIdx = 1;
        int rowNumber = 1;
        for (HotelImportResultResDTO hotel : hotels) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(hotel.getHotelImportDTO().getName());
            row.createCell(2).setCellValue(hotel.getHotelImportDTO().getCode());
            row.createCell(3).setCellValue(hotel.getHotelImportDTO().getProvinceCode());
            row.createCell(4).setCellValue(hotel.getHotelImportDTO().getDistrictCode());
            row.createCell(5).setCellValue(hotel.getHotelImportDTO().getAddress());
            row.createCell(6).setCellValue(hotel.getHotelImportDTO().getPhone());
            row.createCell(7).setCellValue(hotel.getHotelImportDTO().getDescription());
            row.createCell(8).setCellValue(hotel.getHotelImportDTO().getRolesAllow());
            row.createCell(9).setCellValue(hotel.getHotelImportDTO().getRating());
            row.createCell(10).setCellValue(hotel.getHotelImportDTO().getMaxPrice());
            row.createCell(11).setCellValue(hotel.getHotelImportDTO().getMaxPlusPrice());
            String error = hotel.getRes().substring(0, hotel.getRes().length() - 1);
            row.createCell(12).setCellValue(error);
        }
        // Auto resize column width
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        // Create file excel
        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(HotelExportReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<HotelExportResDTO> hotels = hotelRepository.listExportExcelHotel(request.getName(), request.getCode(), request.getProvinceId(), request.getDistrictId(), request.getRating());

        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Hotel-" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);

        Sheet sheet = workbook.createSheet(SHEET); // Create sheet with sheet name
        writeToFileReportHotelExcel(hotels, sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);

        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportTemplate(HttpServletRequest httpServletRequest) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Import_Hotel_Template" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        writeToFileTemplateHotelExcel(sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);

        return excelCommon.createOutputFile(workbook, fileName);
    }

    private void writeToFileReportHotelExcel(List<HotelExportResDTO> hotels, Sheet sheet){
        createHeaderExcel(sheet, HEADERS.length, HEADERS);

        int rowIdx = 1;
        for (HotelExportResDTO hotel : hotels) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(COLUMN_INDEX_NO).setCellValue(rowIdx++);
            row.createCell(COLUMN_INDEX_NAME).setCellValue(hotel.getName());
            row.createCell(COLUMN_INDEX_CODE).setCellValue(hotel.getCode());
            row.createCell(COLUMN_INDEX_PROVINCE_CODE).setCellValue(hotel.getProvinceCode());
            row.createCell(COLUMN_INDEX_DISTRICT_CODE).setCellValue(hotel.getDistrictCode());
            row.createCell(COLUMN_INDEX_PHONE).setCellValue(hotel.getPhone());
            row.createCell(COLUMN_INDEX_DESCRIPTION).setCellValue(hotel.getDescription());
            row.createCell(COLUMN_INDEX_ADDRESS).setCellValue(hotel.getAddress());
            row.createCell(COLUMN_INDEX_ROLE_ALLOW).setCellValue(hotel.getRolesAllow());
            row.createCell(COLUMN_INDEX_RATING).setCellValue(hotel.getRating() == null ? "" : hotel.getRating().toString());
            row.createCell(COLUMN_INDEX_MAX_PRICE).setCellValue(hotel.getMaxPrice() == null ? "" : hotel.getMaxPrice().toString());
            row.createCell(COLUMN_INDEX_MAX_PLUS_PRICE).setCellValue(hotel.getMaxPlusPrice() == null ? "" : hotel.getMaxPlusPrice().toString());
        }
    }

    private void writeToFileTemplateHotelExcel(Sheet sheet){
        createHeaderExcel(sheet, HEADERS.length - 1 , HEADERS);

        int rowIdx = 1;
        int rowNumber = 1;
        for (int i =0 ; i<5; i++) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(COLUMN_INDEX_NAME).setCellValue("Hotel One");
            row.createCell(COLUMN_INDEX_CODE).setCellValue("Code" + i);
            row.createCell(COLUMN_INDEX_PROVINCE_CODE).setCellValue("Code123");
            row.createCell(COLUMN_INDEX_DISTRICT_CODE).setCellValue("Code123");
            row.createCell(COLUMN_INDEX_PHONE).setCellValue("0561238323");
            row.createCell(COLUMN_INDEX_DESCRIPTION).setCellValue("Hotel of province");
            row.createCell(COLUMN_INDEX_ADDRESS).setCellValue("1A province");
            row.createCell(COLUMN_INDEX_ROLE_ALLOW).setCellValue("ADMIN");
            row.createCell(COLUMN_INDEX_RATING).setCellValue("5");
            row.createCell(COLUMN_INDEX_MAX_PRICE).setCellValue("100000");
            row.createCell(COLUMN_INDEX_MAX_PLUS_PRICE).setCellValue("50000");
        }
    }

}
