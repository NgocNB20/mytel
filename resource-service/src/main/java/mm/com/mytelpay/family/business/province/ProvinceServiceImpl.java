package mm.com.mytelpay.family.business.province;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.province.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.DistrictRepository;
import mm.com.mytelpay.family.repository.ProvinceRepository;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class ProvinceServiceImpl extends FamilyBaseBusiness implements ProvinceService {

    private static final String[] HEADER_ERROR = { "No", "Code", "Name","Description", "Message" };

    private static final String[] HEADERS_REPORT_PROVINCE = {"No", "Code", "Name", "Description"};

    private static final String SHEET = "Province";

    private static final String SHEET_REPORT_PROVINCE = "Report Province";

    private final HashMap<Integer , String> hmError = new HashMap<>();
    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Override
    public Province createProvince(ProvinceCreateReqDTO request, HttpServletRequest httpServletRequest) {
        provinceRepository.findProvinceByName(request.getName()).ifPresent((province -> {
            throw new BusinessEx(request.getName() , ResourceErrorCode.Province.NAME_IS_EXISTS, null);
        }));

        provinceRepository.getProvinceByCode(request.getCode()).ifPresent(province -> {
            throw new BusinessEx(request.getCode() , ResourceErrorCode.Province.CODE_IS_EXISTS, null);
        });

        Province province = new Province();
        province.setName(request.getName().trim());
        province.setCode(request.getCode().trim());
        province.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        province.setId(UUID.randomUUID().toString());
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.CREATE_PROVINCE, request.toString());
        return provinceRepository.save(province);
    }

    @Override
    public Province editProvince(ProvinceEditReqDTO request, HttpServletRequest httpServletRequest) {
        Province province = provinceRepository.getProvinceById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getId(), ResourceErrorCode.Province.NOT_FOUND, null);
        });

        if (!province.getName().equals(request.getName())) {
            provinceRepository.findProvinceByName(request.getName()).ifPresent((p -> {
                throw new BusinessEx(request.getName() , ResourceErrorCode.Province.NAME_IS_EXISTS, null);
            }));
            province.setName(request.getName().trim());
        }
        if(!province.getCode().equals(request.getCode())){
            provinceRepository.getProvinceByCode(request.getCode()).ifPresent(p -> {
                throw new BusinessEx(request.getName() , ResourceErrorCode.Province.CODE_IS_EXISTS, null);
            });
            province.setCode(request.getCode().trim());
        }
        province.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.UPDATE_PROVINCE, request.toString());
        return provinceRepository.save(province);
    }

    @Override
    public void deleteProvince(SimpleRequest simpleRequest, HttpServletRequest httpServletRequest) {
        Province province = provinceRepository.getProvinceById(simpleRequest.getId()).orElseThrow(() ->{
            throw new BusinessEx(simpleRequest.getRequestId(), ResourceErrorCode.Province.NOT_FOUND, null);
        });
        if (provinceRepository.checkProvinceInUsed(simpleRequest.getId())) {
            throw new BusinessEx(simpleRequest.getRequestId(), ResourceErrorCode.Province.IS_USED, null);
        }
        insertActionLog(simpleRequest.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.DELETE_PROVINCE, simpleRequest.toString());
        provinceRepository.deleteById(province.getId());
    }

    @Override
    public ProvinceResDTO getDetail(SimpleRequest simpleRequest, HttpServletRequest httpServletRequest) {
        Province province = provinceRepository.getProvinceById(simpleRequest.getId()).orElseThrow(() ->{
            throw new BusinessEx(simpleRequest.getRequestId(), ResourceErrorCode.Province.NOT_FOUND, null);
        });
        return mapper.map(province , ProvinceResDTO.class);
    }

    @Override
    public Page<ProvinceFilterReqDTO> getList(ProvinceFilerResDTO response, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(response.getPageIndex(), response.getPageSize() , response.getSortBy() , response.getSortOrder());
        Page<ProvinceFilterReqDTO> responsePage = provinceRepository.filerProvince(
                response.getName(),
                response.getCode(),
                pageable
        );
        logger.info("Found:{} province.", responsePage.getTotalElements());
        if (responsePage.getContent().isEmpty()) {
            throw new BusinessEx(response.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
        }
        return new PageImpl<>(responsePage.getContent(),pageable, responsePage.getTotalElements());
    }

    @Override
    public ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException {
        List<Province> provinces = new ArrayList<>();
        List<ProvinceImportErrorDTO> resImport = new ArrayList<>();

        Set<String> nameProvinces = new HashSet<>();

        Set<String> codeProvinces = new HashSet<>();
        boolean isXls = inputFile != null && Objects.requireNonNull(inputFile.getOriginalFilename()).endsWith("xls");
        if (Objects.isNull(inputFile)) {
            throw new BusinessEx(null, ResourceErrorCode.Province.IMPORT_FAIL, null);
        }
        InputStream file = inputFile.getInputStream();
        Workbook workbook;
        if (isXls) {
            workbook = new HSSFWorkbook(file);
        } else {
            workbook = new XSSFWorkbook(file);
        }
        Sheet sheet = workbook.getSheetAt(0);

        if (sheet.getPhysicalNumberOfRows() > Constants.MAX_LIMIT_200_IMPORT_EXCEL) {
            throw new BusinessEx(null, CommonException.File.LIMIT_ROW_IMPORT, null);
        }

        checkHeader(sheet);
        Iterator<Row> rows = sheet.iterator();
        int rowNumber = 0;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            Iterator<Cell> cellInRow = currentRow.iterator();
            Province province = new Province();
            DataFormatter formatter = new DataFormatter();
            String errorMessage = "";
            while (cellInRow.hasNext()) {
                Cell currentCell = cellInRow.next();
                int cellIdx = currentCell.getColumnIndex();
                String value = ExcelCommon.getDataFromCellString(formatter, currentCell);
                switch (cellIdx) {
                    case 0:
                        province.setId(UUID.randomUUID().toString());
                        break;
                    case 1:
                        errorMessage = checkProvinceCode(errorMessage, value);
                        province.setCode(value);
                        break;
                    case 2:
                        errorMessage = checkProvinceName(errorMessage, value);
                        province.setName(value);
                        break;
                    case 3:
                        errorMessage = checkDescription(errorMessage, value);
                        province.setDescription(value);
                        break;

                    default:
                        break;
                }
                hmError.put(rowNumber , errorMessage);
            }
            errorMessage = checkValidProvince(errorMessage, province, nameProvinces, codeProvinces);
            if (errorMessage.isBlank()) {
                errorMessage += "Success ";
                provinces.add(province);
            }
            resImport.add(new ProvinceImportErrorDTO(province , errorMessage));
        }
        provinceRepository.saveAll(provinces);
        insertActionLog(null, perRequestContextDto.getCurrentAccountId(), ActionType.IMPORT_PROVINCE, null);
        return exportExcelError(resImport);
    }

    private String checkDescription(String errorMessage, String value) {
        if (value.length() > 255) {
            errorMessage += "Description no more than 255 characters , ";
        }
        return errorMessage;
    }

    private String checkProvinceName(String errorMessage, String value) {
        Optional<Province> provinceCheckName = provinceRepository.findProvinceByName(value);
        if (StringUtils.isBlank(value)) {
            errorMessage += "Name is empty, ";
        }
        if (provinceCheckName.isPresent()) {
            errorMessage += "Name was exists, ";
        }
        if (value.length() > 100) {
            errorMessage += "Name no more than 100 characters, ";
        }
        return errorMessage;
    }

    private String checkProvinceCode(String errorMessage, String value) {
        Optional<Province> provinceCheckCode = provinceRepository.getProvinceByCode(value);
        if (StringUtils.isBlank(value)) {
            errorMessage += "Code is empty, ";
        }
        if (provinceCheckCode.isPresent()) {
            errorMessage += "Code was exists, ";
        }
        if (value.length() > 50) {
            errorMessage += "code no more than 50 characters, ";
        }
        return errorMessage;
    }

    private void checkHeader(Sheet sheet){
        for (int col = 0; col < sheet.getRow(0).getPhysicalNumberOfCells(); col++) {
            try{
                if (!HEADERS_REPORT_PROVINCE[col].equals(sheet.getRow(0).getCell(col).toString())){
                    throw new BusinessEx(null, ResourceErrorCode.Province.IMPORT_FAIL, null);
                }
            }catch (Exception e){
                throw new BusinessEx(null, ResourceErrorCode.Province.IMPORT_FAIL, null);
            }
        }
    }

    private String checkValidProvince(String errorMessage, Province province, Set<String> nameProvinces, Set<String> codeProvinces) {
        if (StringUtils.isBlank(errorMessage)) {
            if (StringUtils.isEmpty(province.getName()) || province.getName().isBlank()) {
                errorMessage += "Province name is null, ";
            } else if (nameProvinces.contains(province.getName())) {
                errorMessage += "Province name is used, ";
            } else {
                nameProvinces.add(province.getName());
            }

            if (Objects.isNull(province.getCode()) || province.getCode().isBlank()) {
                errorMessage += "Province code is null, ";
            } else if (codeProvinces.contains(province.getCode())) {
                errorMessage += "Province code is used, ";
            } else {
                codeProvinces.add(province.getCode());
            }
        }
        return errorMessage;
    }


    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(ProvinceFilerResDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<ProvinceFilterReqDTO> list = provinceRepository.filerProvince(
                request.getName(),
                request.getCode()
        );
        logger.info("Found:{} province.", list.size());
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Report_Province_" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        // Create sheet
        Sheet sheet = workbook.createSheet(SHEET_REPORT_PROVINCE); // Create sheet with sheet name
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < HEADERS_REPORT_PROVINCE.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS_REPORT_PROVINCE[col]);
        }
        writeToFileReportProvinceExcel(list , sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet , numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcelTemplate(HttpServletRequest httpServletRequest) throws IOException {
        List<ProvinceFilterReqDTO> listProvinceExample = generateListProvinceExample();
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_NAME_EXCEL);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Report_Province_Template" + currentDateTime + EXCEL_TYPE;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        // Create sheet
        Sheet sheet = workbook.createSheet(SHEET_REPORT_PROVINCE); // Create sheet with sheet name
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < HEADERS_REPORT_PROVINCE.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS_REPORT_PROVINCE[col]);
        }
        writeToFileReportProvinceExcel(listProvinceExample , sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet , numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private List<ProvinceFilterReqDTO> generateListProvinceExample() {
        List<ProvinceFilterReqDTO> list = new ArrayList<>();
        for(int i = 5; i >= 0; i-- ) {
            Province province = new Province();
            province.setId(String.valueOf(UUID.randomUUID()));
            province.setName("Province "+i);
            province.setCode("00"+i);
            province.setDescription("Example Description "+i);
            list.add(mapper.map(province, ProvinceFilterReqDTO.class));
        }
       return list;
    }

    private void writeToFileReportProvinceExcel(List<ProvinceFilterReqDTO> resDTOList, Sheet sheet){
        int rowIdx = 1;
        int rowNumber = 1;
        for (ProvinceFilterReqDTO dto: resDTOList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(dto.getCode());
            row.createCell(2).setCellValue(dto.getName());
            row.createCell(3).setCellValue(dto.getDescription());
        }
    }

    public ResponseEntity<ByteArrayResource> exportExcelError(List<ProvinceImportErrorDTO> provinceImportErrors) throws IOException {
        String filename = "Province_Result.xlsx";
        Workbook workbook = excelCommon.getWorkbook(filename);
        Sheet sheet = workbook.createSheet(SHEET);
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < HEADER_ERROR.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADER_ERROR[col]);
        }
        int rowIdx = 1;
        int rowNumber = 1;
        for (ProvinceImportErrorDTO importError : provinceImportErrors) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber ++);
            row.createCell(1).setCellValue(importError.getProvince().getCode());
            row.createCell(2).setCellValue(importError.getProvince().getName());
            row.createCell(3).setCellValue(importError.getProvince().getDescription());
            String error = !importError.getRes().isEmpty() ? importError.getRes().substring(0, importError.getRes().length() -1) : "";
            row.createCell(4).setCellValue(error);
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet , numberOfColumn);
        return excelCommon.createOutputFile(workbook, filename);
    }
}
