package mm.com.mytelpay.family.business.car;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.car.dto.CarCreateReqDTO;
import mm.com.mytelpay.family.business.car.dto.CarExportReqDTO;
import mm.com.mytelpay.family.business.car.dto.CarImportResultResDTO;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.Car;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repository.CarRepository;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
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
public class CarExcelServiceImpl extends FamilyBaseBusiness implements CarExcelService {

    private static final String CAR_TYPE_HEADER = "CarType";
    private static final String MODEL_HEADER = "Model";
    private static final String LICENSE_PLATE_HEADER = "License_Plate";
    public CarExcelServiceImpl() {
        logger = LogManager.getLogger(CarExcelServiceImpl.class);
    }

    @Autowired
    private CarRepository carRepository;

    private static final String[] HEADER_EXPORT = {"No", "Name", CAR_TYPE_HEADER, MODEL_HEADER, LICENSE_PLATE_HEADER, "Status"};

    private static final String[] HEADER_IMPORT = {"No", "Name", CAR_TYPE_HEADER, MODEL_HEADER, LICENSE_PLATE_HEADER, "Message"};

    private static final String[] HEADER = {"No", "Name", CAR_TYPE_HEADER, MODEL_HEADER, LICENSE_PLATE_HEADER};
    private static final String SHEET = "Car";

    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_NAME = 1;
    private static final int COLUMN_INDEX_CAR_TYPE = 2;
    private static final int COLUMN_INDEX_MODEL = 3;
    private static final int COLUMN_INDEX_LICENSE_PLATE = 4;
    private static final int COLUMN_INDEX_STATUS = 5;
    private static final int COLUMN_INDEX_RESULT = 5;

    @Override
    public ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException {
        List<Car> res = new ArrayList<>();
        List<CarImportResultResDTO> resImport = new ArrayList<>();
        List<String> licensePlates = new ArrayList<>();
        if (inputFile.isEmpty()){
            throw new BusinessEx(ResourceErrorCode.Car.IMPORT_FAIL, null);
        }
        boolean isXls = Objects.requireNonNull(inputFile.getOriginalFilename()).endsWith("xls");
        try {
            InputStream file = inputFile.getInputStream();
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
            Iterator<Row> rowIterator = sheet.iterator();
            DataFormatter formatter = new DataFormatter();
            if (sheet.getPhysicalNumberOfRows() > Constants.MAX_LIMIT_200_IMPORT_EXCEL) {
                throw new BusinessEx(null, CommonException.File.LIMIT_ROW_IMPORT, null);
            }
            while (rowIterator.hasNext()) {
                readRowExcel(res, resImport, licensePlates, rowIterator, formatter);
            }
        } catch (IOException e) {
            throw new BusinessEx(ResourceErrorCode.Car.IMPORT_FAIL, null);
        }
        carRepository.saveAll(res);
        insertActionLog(null, perRequestContextDto.getCurrentAccountId(), ActionType.IMPORT_CAR, null);
        return downloadResultImport(resImport);
    }

    private void readRowExcel(List<Car> res, List<CarImportResultResDTO> resImport, List<String> licensePlates, Iterator<Row> rowIterator, DataFormatter formatter) {
        Row row = rowIterator.next();
        if (row.getRowNum() >= 1) {
            Iterator<Cell> cellIterator = row.cellIterator();
            if (cellIterator.hasNext()) {
                CarCreateReqDTO car = new CarCreateReqDTO();
                String errorMessage = "";
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell != null) {
                        String valueTemp;
                        int cellIndex = cell.getColumnIndex();
                        valueTemp = ExcelCommon.getDataFromCell(formatter, cell);
                        switch (cellIndex) {
                            case 1:
                                errorMessage = checkName(errorMessage, valueTemp);
                                car.setName(valueTemp);
                                break;
                            case 2:
                                errorMessage = checkCarType(errorMessage, valueTemp);
                                car.setCarType(valueTemp);
                                break;
                            case 3:
                                errorMessage = checkModel(errorMessage, valueTemp);
                                car.setModel(valueTemp);
                                break;
                            case 4:
                                errorMessage = checkLicensePlate(errorMessage, valueTemp);
                                car.setLicensePlate(valueTemp);
                                break;
                            default:
                                break;
                        }
                    }
                }
                errorMessage = validateFinal(res, licensePlates, car, errorMessage);
                resImport.add(new CarImportResultResDTO(car, errorMessage));
            }
        }
    }


    private String checkErrorMessage(String errorMessage, String message){
        if (!errorMessage.contains(message)){
            errorMessage += message;
        }
        return errorMessage;
    }

    @NotNull
    private String validateFinal(List<Car> res, List<String> licensePlates, CarCreateReqDTO car, String errorMessage) {
        errorMessage = checkEmptyCar(errorMessage, car);
        if (StringUtils.isBlank(errorMessage)) {
            if (licensePlates.contains(car.getLicensePlate())){
                errorMessage = checkErrorMessage(errorMessage, "License plate is used, ");
            }else {
                licensePlates.add(car.getLicensePlate());
                res.add(mapper.map(car, Car.class));
                errorMessage += " Success ";
            }
        }
        return errorMessage;
    }

    private String checkEmptyCar(String errorMessage, CarCreateReqDTO car){
        if (ObjectUtils.isEmpty(car.getName())) {
            errorMessage = checkErrorMessage(errorMessage, "Name is Empty, ");
        }
        if (ObjectUtils.isEmpty(car.getModel())) {
            errorMessage = checkErrorMessage(errorMessage, "Model is Empty, ");
        }
        if (ObjectUtils.isEmpty(car.getLicensePlate())) {
            errorMessage = checkErrorMessage(errorMessage, "LicensePlate is Empty, ");
        }
        if (ObjectUtils.isEmpty(car.getCarType())) {
            errorMessage = checkErrorMessage(errorMessage, "CarType is Empty, ");
        }
        return errorMessage;
    }

    private void checkHeader(Sheet sheet){
        for (int col = 0; col < sheet.getRow(0).getPhysicalNumberOfCells(); col++) {
            try{
                if (!HEADER[col].equals(sheet.getRow(0).getCell(col).toString())){
                    throw new BusinessEx(null, ResourceErrorCode.Car.IMPORT_FAIL, null);
                }
            }catch (Exception e){
                throw new BusinessEx(null, ResourceErrorCode.Car.IMPORT_FAIL, null);
            }
        }
    }

    private String checkName(String errorMessage, String name){
        if (StringUtils.isEmpty(name)) {
            errorMessage = checkErrorMessage(errorMessage, "Name is Empty, ");
        }
        if (name.length() > 100) {
            errorMessage += "Name is in invalid, ";
        }
        return errorMessage;
    }

    private String checkCarType(String errorMessage, String carType){
        if (StringUtils.isEmpty(carType)) {
            errorMessage = checkErrorMessage(errorMessage, "CarType is Empty, ");
        }
        try{
            CarType.valueOf(carType);
        }catch (Exception e){
            errorMessage += "CarType is invalid, ";
        }
        return errorMessage;
    }

    private String checkModel(String errorMessage, String model){
        if (StringUtils.isEmpty(model)) {
            errorMessage = checkErrorMessage(errorMessage, "Model is Empty, ");
        }
        if (model.length() > 100) {
            errorMessage += "Model is in invalid, ";
        }
        return errorMessage;
    }

    private String checkLicensePlate(String errorMessage, String licensePlate){
        if (StringUtils.isEmpty(licensePlate)) {
            errorMessage = checkErrorMessage(errorMessage, "LicensePlate is Empty, ");
        }
        if (licensePlate.length() > 20) {
            errorMessage += "LicensePlate is in invalid, ";
        }
        if (carRepository.existsLicensePlate(licensePlate)) {
            errorMessage = checkErrorMessage(errorMessage, "License plate is used, ");
        }
        return errorMessage;
    }


    private ResponseEntity<ByteArrayResource> downloadResultImport(List<CarImportResultResDTO> responses) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(Constants.EXCEL_DATE_FORMATTER);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Result-Import-Car-" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADER_IMPORT.length, HEADER_IMPORT);
        int rowIdx = 1;
        int rowNumber = 1;
        for (CarImportResultResDTO response : responses) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(COLUMN_INDEX_ID).setCellValue(rowNumber++);
            row.createCell(COLUMN_INDEX_NAME).setCellValue(response.getCar().getName());
            row.createCell(COLUMN_INDEX_CAR_TYPE).setCellValue(response.getCar().getCarType() == null ? "" : response.getCar().getCarType());
            row.createCell(COLUMN_INDEX_MODEL).setCellValue(response.getCar().getModel());
            row.createCell(COLUMN_INDEX_LICENSE_PLATE).setCellValue(response.getCar().getLicensePlate());
            row.createCell(COLUMN_INDEX_RESULT).setCellValue(StringUtils.isBlank(response.getRes()) ? "Success " : response.getRes().substring(0, response.getRes().length() - 2));
        }
        // Auto resize column width
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        // Create file excel
        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(CarExportReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<Car> cars = carRepository.listExportCar(
                StringUtils.isBlank(request.getCarType()) ? null : CarType.valueOf(request.getCarType()),
                request.getName(),
                StringUtils.isBlank(request.getStatus()) ? null : Status.valueOf(request.getStatus())
        );

        DateFormat dateFormatter = new SimpleDateFormat(Constants.EXCEL_DATE_FORMATTER);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Export-Car-" + currentDateTime + EXCEL_TYPE;

        // Create Workbook
        Workbook workbook = excelCommon.getWorkbook(fileName);
        // Create sheet
        Sheet sheet = workbook.createSheet(SHEET); // Create sheet with sheet name
        createHeaderExcel(sheet, HEADER_EXPORT.length, HEADER_EXPORT);
        int rowIdx = 1;
        int rowNumber = 1;
        for (Car car : cars) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(COLUMN_INDEX_ID).setCellValue(rowNumber++);
            row.createCell(COLUMN_INDEX_NAME).setCellValue(car.getName());
            row.createCell(COLUMN_INDEX_CAR_TYPE).setCellValue(car.getCarType() == null ? "" : car.getCarType().toString());
            row.createCell(COLUMN_INDEX_MODEL).setCellValue(car.getModel());
            row.createCell(COLUMN_INDEX_LICENSE_PLATE).setCellValue(car.getLicensePlate());
            row.createCell(COLUMN_INDEX_STATUS).setCellValue(car.getStatus() == null ? "" : car.getStatus().toString());
        }
        // Auto resize column width
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        // Create file excel
        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadTemplate(HttpServletRequest httpServletRequest) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(Constants.EXCEL_DATE_FORMATTER);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Template-Import-Car-" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADER_EXPORT.length - 1, HEADER_EXPORT);

        Sheet sheetCarType = workbook.createSheet(CAR_TYPE_HEADER);
        int length = CarType.values().length;
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < length; i++){
            Row row = sheetCarType.createRow(i);
            row.createCell(0).setCellValue(CarType.values()[i].toString());
            data.add(CarType.values()[i].toString());
        }

        int rowIdx = 1;
        for (int i = 1; i <= 5; i++) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue("Name_Example_" + i);
            row.createCell(2).setCellValue(data.get(i));
            row.createCell(3).setCellValue("model_Example_" + i);
            row.createCell(4).setCellValue("30H-Example" + i);
        }

        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(data.toArray(new String[0]));
        CellRangeAddressList addressList = new CellRangeAddressList(1, sheet.getLastRowNum(), 2, 2);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        for (Row row : sheet) {
            Cell cell = row.getCell(2);
            if (cell == null) {
                cell = row.createCell(2);
            }
            cell.setCellStyle(excelCommon.getCellStyle(workbook));
            cell.setAsActiveCell();
            sheet.addValidationData(validation);
        }

        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);

        sheet.addValidationData(validation);

        return excelCommon.createOutputFile(workbook, fileName);
    }
}
