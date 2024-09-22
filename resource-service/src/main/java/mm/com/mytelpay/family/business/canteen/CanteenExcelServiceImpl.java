package mm.com.mytelpay.family.business.canteen;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.canteen.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.Canteen;
import mm.com.mytelpay.family.repository.CanteenRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CanteenExcelServiceImpl extends FamilyBaseBusiness implements CanteenExcelService {

    public CanteenExcelServiceImpl() {
        logger = LogManager.getLogger(CanteenExcelServiceImpl.class);
    }

    @Autowired
    private CanteenRepository canteenRepository;
    private static final String SEATS_HEADER = "Seats";
    private static final String ADDRESS_HEADER = "Address";
    private static final String DESCRIPTION_HEADER = "Description";

    private static final String[] HEADER_TEMPLATE = {"No", "Code", "Name", "Unit", SEATS_HEADER, ADDRESS_HEADER, DESCRIPTION_HEADER};
    private static final String[] HEADERS = {"No", "Code", "Name", "Unit", SEATS_HEADER, ADDRESS_HEADER, DESCRIPTION_HEADER, "Message"};
    private static final String[] HEADER_IMPORT = {"No", "Code", "Name", "Unit", SEATS_HEADER, ADDRESS_HEADER, DESCRIPTION_HEADER};
    private static final String SHEET = "Canteen";

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) {
        List<Canteen> listAllCanteen = canteenRepository.findAll();
        List<Canteen> canteens = new ArrayList<>();
        List<CanteenImportResultResDTO> resImport = new ArrayList<>();
        List<UnitDTO> unitDTOS = getAllUnit(perRequestContextDto.getBearToken());
        Set<String> validCode = new HashSet<>();
        Set<String> validUnit = new HashSet<>();
        boolean checkFile = checkFileExcel(inputFile);
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
            readRowExcel(rows, canteens, unitDTOS, resImport, listAllCanteen, validCode, validUnit);
            canteenRepository.saveAll(canteens);
            return downloadResultImport(resImport);
        } catch (IOException e) {
            throw new BusinessEx(ResourceErrorCode.Canteen.IMPORT_FAIL, null);
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(CanteenFilterReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<CanteenFilterResDTO> canteens = getListCanteenExport(request);
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Canteen-" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADERS.length, HEADERS);
        int rowIdx = 1;
        int rowNumber = 1;
        for (CanteenFilterResDTO canteen : canteens) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(canteen.getCode());
            row.createCell(2).setCellValue(canteen.getName());
            row.createCell(3).setCellValue(canteen.getUnit());
            row.createCell(4).setCellValue(canteen.getNumberSeats());
            row.createCell(5).setCellValue(canteen.getAddress());
            row.createCell(6).setCellValue(canteen.getDescription());
        }
        // Auto resize column width
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        // Create file excel
        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadTemplate(HttpServletRequest httpServletRequest) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Template_Canteen-" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADER_TEMPLATE.length, HEADER_TEMPLATE);
        int secondRow = 1;
        int rowNumber = 1;
        for (int i = 0; i <= 5; i++) {
            Row row = sheet.createRow(secondRow++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue("Canteen code");
            row.createCell(2).setCellValue("Canteen name");
            row.createCell(3).setCellValue("IT");
            row.createCell(4).setCellValue("5");
            row.createCell(5).setCellValue("Address canteen");
            row.createCell(6).setCellValue("Description canteen");
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private ResponseEntity<ByteArrayResource> downloadResultImport(List<CanteenImportResultResDTO> canteens) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Canteen_Error-" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        createHeaderExcel(sheet, HEADERS.length, HEADERS);

        int rowIdx = 1;
        int rowNumber = 1;
        for (CanteenImportResultResDTO canteenError : canteens) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(canteenError.getCanteenInportDTO().getCode());
            row.createCell(2).setCellValue(canteenError.getCanteenInportDTO().getName());
            row.createCell(3).setCellValue(canteenError.getCanteenInportDTO().getUnit());
            row.createCell(4).setCellValue(canteenError.getCanteenInportDTO().getSeat());
            row.createCell(5).setCellValue(canteenError.getCanteenInportDTO().getAddress());
            row.createCell(6).setCellValue(canteenError.getCanteenInportDTO().getDescription());
            String error = canteenError.getMessage().substring(0, canteenError.getMessage().length() - 1);
            row.createCell(7).setCellValue(error);
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private void readRowExcel(Iterator<Row> rows, List<Canteen> canteens, List<UnitDTO> unitDTOS, List<CanteenImportResultResDTO> resImport, List<Canteen> listAllCanteen, Set<String> validCode, Set<String> validUnit) {
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            Canteen canteen = new Canteen();
            CanteenInportDTO canteenInportDTO = new CanteenInportDTO();
            int cellIdx = 0;
            String errorMessage = "";
            for (int cellNum = 0; cellNum < currentRow.getLastCellNum(); cellNum++) {
                Cell currentCell = currentRow.getCell(cellNum, Row.CREATE_NULL_AS_BLANK);
                DataFormatter dataFormatter = new DataFormatter();
                String cellValue = StringUtils.trim(dataFormatter.formatCellValue(currentCell));
                switch (cellIdx) {
                    case 1:
                        errorMessage = checkCode(errorMessage, cellValue, listAllCanteen, validCode, canteen);
                        canteenInportDTO.setCode(cellValue);
                        break;
                    case 2:
                        errorMessage = checkName(errorMessage, cellValue, canteen);
                        canteen.setName(cellValue);
                        canteenInportDTO.setName(cellValue);
                        break;
                    case 3:
                        errorMessage = checkUnit(errorMessage, cellValue, unitDTOS, canteen, validUnit);
                        canteenInportDTO.setUnit(cellValue);
                        break;
                    case 4:
                        errorMessage = checkSeat(errorMessage, cellValue, canteen);
                        canteenInportDTO.setSeat(cellValue);
                        break;
                    case 5:
                        canteen.setAddress(cellValue);
                        canteenInportDTO.setAddress(cellValue);
                        break;
                    case 6:
                        canteen.setDescription(cellValue);
                        canteenInportDTO.setDescription(cellValue);
                        break;
                    default:
                        break;
                }
                cellIdx++;
            }
            if (StringUtils.isEmpty(errorMessage)) {
                canteens.add(canteen);
                validCode.add(canteen.getCode());
                validUnit.add(canteen.getUnitId());
                errorMessage = " Success ";
            }
            resImport.add(new CanteenImportResultResDTO(canteenInportDTO, errorMessage));
        }
    }

    private boolean checkFileExcel(MultipartFile inputFile) {
        if (ObjectUtils.isEmpty(inputFile) && Objects.isNull(inputFile.getOriginalFilename())) {
            throw new RequestEx(null, ResourceErrorCode.Canteen.IMPORT_FAIL, null);
        }
        return Objects.requireNonNull(inputFile.getOriginalFilename()).endsWith("xls");
    }

    private void checkLimitRowExcel(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > Constants.MAX_LIMIT_200_IMPORT_EXCEL) {
            throw new BusinessEx(null, CommonException.File.LIMIT_ROW_IMPORT, null);
        }
    }

    private String checkSeat(String errorMessage, String seat, Canteen canteen) {
        boolean checkNumberSeats = true;
        if (StringUtils.isEmpty(seat)) {
            errorMessage += " Seat is Empty,";
            checkNumberSeats = false;
        }
        if (!seat.matches(Constants.NUMBER_SEAT_REGEX) && !StringUtils.isEmpty(seat)) {
            errorMessage += " Seat is invalid,";
            checkNumberSeats = false;
        }
        if (checkNumberSeats) {
            canteen.setSeats(Integer.valueOf(seat));
        }
        return errorMessage;
    }

    private String checkUnit(String errorMassage, String unit, List<UnitDTO> unitDTOS, Canteen canteen, Set<String> validUnit) {
        String unitId = "";
        int countUnit = 0;
        if (StringUtils.isEmpty(unit)) {
            errorMassage += " Unit is Empty,";
        } else {
            StringBuilder errorMessageBuilder = new StringBuilder(errorMassage);
            for (UnitDTO unitDTO : unitDTOS) {
                if (unitDTO.getCode().equals(unit)) {
                    unitId = unitDTO.getId();
                    countUnit++;
                    if (!ObjectUtils.isEmpty(canteenRepository.findByUnitId(unitId))) {
                        errorMessageBuilder.append(" Unit already exists canteen,");
                    } else {
                        canteen.setUnitId(unitId);
                    }
                    break;
                }
            }
            if (countUnit == 0) {
                errorMessageBuilder.append(" Unit does not exist,");
            }
            errorMassage = errorMessageBuilder.toString();
        }
        if (validUnit.contains(unitId)) {
            errorMassage += " Unit already exists canteen,";
        }
        return errorMassage;
    }

    private String checkName(String errorMessage, String name, Canteen canteen) {
        boolean checkName = true;
        if (StringUtils.isEmpty(name)) {
            errorMessage += " Name is Empty,";
            checkName = false;
        }
        if (name.length() > 100) {
            errorMessage += " Name is invalid,";
            checkName = false;
        }
        if (checkName) {
            canteen.setName(name);
        }
        return errorMessage;
    }

    private String checkCode(String errorMessage, String cellValue, List<Canteen> listAllCanteen, Set<String> validCode, Canteen checkCodeCanteen) {
        boolean checkCode = true;
        if (validCode.contains(cellValue)) {
            errorMessage += " Code was exists,";
            checkCode = false;
        }
        if (StringUtils.isEmpty(cellValue)) {
            errorMessage += " Code is Empty,";
            checkCode = false;
        } else {
            StringBuilder errorMessageBuilder = new StringBuilder(errorMessage);
            for (Canteen canteen : listAllCanteen) {
                if (cellValue.equals(canteen.getCode())) {
                    errorMessageBuilder.append(" Code was exists,");
                    checkCode = false;
                    break;
                }
            }
            errorMessage = errorMessageBuilder.toString();
        }
        if (cellValue.length() > 50) {
            errorMessage += " Code is invalid,";
            checkCode = false;
        }
        if (checkCode) {
            checkCodeCanteen.setCode(cellValue);
        }
        return errorMessage;
    }

    private void checkHeader(Sheet sheet) {
        for (int col = 0; col < sheet.getRow(0).getPhysicalNumberOfCells(); col++) {
            try {
                if (!HEADER_IMPORT[col].equals(sheet.getRow(0).getCell(col).toString())) {
                    throw new BusinessEx(null, ResourceErrorCode.Canteen.IMPORT_FAIL, null);
                }
            } catch (Exception e) {
                throw new BusinessEx(null, ResourceErrorCode.Canteen.IMPORT_FAIL, null);
            }
        }
    }

    public List<CanteenFilterResDTO> getListCanteenExport(CanteenFilterReqDTO request) {
        String name = StringUtils.isBlank(request.getName()) ? null : request.getName();
        String unitId = StringUtils.isBlank(request.getUnitId()) ? null : request.getUnitId();
        String code = StringUtils.isBlank(request.getCode()) ? null : request.getCode();
        List<CanteenFilterResDTO> listCanteenReport = canteenRepository.getCanteenExport(name, unitId, code);
        addUnitNameForCanteen(listCanteenReport);
        return listCanteenReport;
    }

    public UnitInfoReqDTO getUnitId(List<CanteenFilterResDTO> canteens) {
        List<String> unitId = new ArrayList<>();
        for (CanteenFilterResDTO cantennFilter : canteens) {
            unitId.add(cantennFilter.getUnitId());
        }
        UnitInfoReqDTO unitIdInfo = new UnitInfoReqDTO();
        unitIdInfo.setUnitId(unitId);
        return unitIdInfo;
    }

    public void addUnitNameForCanteen(List<CanteenFilterResDTO> canteens) {
        UnitInfoReqDTO unitInfoReq = getUnitId(canteens);
        List<UnitDTO> unitDTOS;
        unitDTOS = getListUnit(unitInfoReq.getUnitId(), unitInfoReq.getRequestId(), perRequestContextDto.getBearToken());
        for (CanteenFilterResDTO canteenFilter : canteens) {
            for (UnitDTO unitDTO : unitDTOS) {
                if (unitDTO.getId().equals(canteenFilter.getUnitId())) {
                    canteenFilter.setUnit(unitDTO.getName());
                }
            }
        }
    }
}
