package mm.com.mytelpay.family.business.account.service;

import lombok.SneakyThrows;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.account.NewAccountBuilder;
import mm.com.mytelpay.family.business.account.dto.AccountExportDTO;
import mm.com.mytelpay.family.business.account.dto.AccountImportErrorReqDTO;
import mm.com.mytelpay.family.business.account.dto.AccountImportReqDTO;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterResDTO;
import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.AccountInfo;
import mm.com.mytelpay.family.model.entities.AccountRole;
import mm.com.mytelpay.family.model.entities.Unit;
import mm.com.mytelpay.family.repo.UnitRepository;
import mm.com.mytelpay.family.transaction.AccountTransactionService;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.PasswordUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AccountExcelImpl extends AccountBaseBusiness implements AccountExcelService {
    private static final String PHONE_HEADER = "Phone";
    private static final String EMAIL_HEADER = "Email";
    private static final String UNIT_CODE_HEADER = "UnitCode";
    private static final String EXCEL_SUFFIX = ".xlsx";

    static String[] headers = {"No", "Name", PHONE_HEADER, EMAIL_HEADER, "UnitId", UNIT_CODE_HEADER, "UnitName", "Status"};
    static String[] headersTemplate = {"No", "Name", PHONE_HEADER, EMAIL_HEADER, UNIT_CODE_HEADER, "Password"};
    static String[] headersError = {"No", "Name", PHONE_HEADER, EMAIL_HEADER, UNIT_CODE_HEADER, "PassWord", "Message"};
    static String sheetName = "User";
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    PasswordUtils passwordUtils;
    @Autowired
    private ApplicationSettingCommonService applicationSettingCommonService;
    @Autowired
    private PerRequestContextDto perRequestContextDto;
    @Autowired
    private AccountTransactionService accountTransactionService;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd_HH_mm";

    @Override
    public ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile) {
        List<AccountImportReqDTO> accountImportReqDTOS = new ArrayList<>();
        List<AccountImportErrorReqDTO> accountImportErrorReqDTOS = new ArrayList<>();
        List<Unit> units = unitRepository.findAll();
        List<Account> accounts = accountRepository.findAll();
        boolean checkFile = checkFileExcel(inputFile);
        Set<String> validPhoneSet = new HashSet<>();
        Set<String> validEmailSet = new HashSet<>();
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
            readRowExcel(rows, validPhoneSet, validEmailSet, accountImportErrorReqDTOS, units, accounts);
            saveImportedUserToDb(accountImportErrorReqDTOS);
            return exportExcelError(accountImportErrorReqDTOS);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }

    }

    private void readRowExcel(Iterator<Row> rows, Set<String> validPhoneSet, Set<String> validEmailSet, List<AccountImportErrorReqDTO> accountImportErrorReqDTOS, List<Unit> units, List<Account> accounts) {
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            AccountImportReqDTO accountImportReqDTO = new AccountImportReqDTO();
            int cellIdx = 0;
            StringBuilder errorMessage = new StringBuilder();
            for (int cellNum = 0; cellNum < currentRow.getLastCellNum(); cellNum++) {
                Cell currentCell = currentRow.getCell(cellNum, Row.CREATE_NULL_AS_BLANK);
                DataFormatter dataFormatter = new DataFormatter();
                String cellValue = StringUtils.trim(dataFormatter.formatCellValue(currentCell));
                switch (cellIdx) {
                    case 1:
                        checkName(errorMessage, cellValue);
                        accountImportReqDTO.setName(cellValue);
                        break;
                    case 2:
                        checkPhone(errorMessage, validPhoneSet, cellValue, accounts);
                        accountImportReqDTO.setPhone(cellValue);
                        break;
                    case 3:
                        checkEmail(errorMessage, validEmailSet, cellValue, accounts);
                        accountImportReqDTO.setEmail(cellValue);
                        break;
                    case 4:
                        checkUnitCode(errorMessage, cellValue, units);
                        accountImportReqDTO.setUnitCode(cellValue);
                        break;
                    case 5:
                        checkPass(errorMessage, cellValue);
                        accountImportReqDTO.setPassword(cellValue);
                        break;
                    default:
                        break;
                }
                cellIdx++;
            }
            if (StringUtils.isEmpty(errorMessage)) {
                validPhoneSet.add(accountImportReqDTO.getPhone());
                validEmailSet.add(accountImportReqDTO.getEmail());
            }
            accountImportErrorReqDTOS.add(new AccountImportErrorReqDTO(accountImportReqDTO, errorMessage.toString()));
        }
    }

    private void checkLimitRowExcel(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > Constants.MAX_LIMIT_200_IMPORT_EXCEL) {
            throw new BusinessEx(null, CommonException.File.LIMIT_ROW_IMPORT, null);
        }
    }

    private boolean checkFileExcel(MultipartFile inputFile) {
        if (ObjectUtils.isEmpty(inputFile) && Objects.isNull(inputFile.getOriginalFilename())) {
            throw new RequestEx(null, AccountErrorCode.ACCOUNT.IMPORT_FAIL, null);
        }
        return Objects.requireNonNull(inputFile.getOriginalFilename()).endsWith("xls");
    }

    private void checkHeader(Sheet sheet) {
        for (int col = 0; col < sheet.getRow(0).getPhysicalNumberOfCells(); col++) {
            try {
                if (!headersTemplate[col].equals(sheet.getRow(0).getCell(col).toString())) {
                    throw new BusinessEx(null, AccountErrorCode.ACCOUNT.IMPORT_FAIL, null);
                }
            } catch (Exception e) {
                throw new BusinessEx(null, AccountErrorCode.ACCOUNT.IMPORT_FAIL, null);
            }
        }
    }


    private StringBuilder checkName(StringBuilder errorMessage, String name) {
        if (StringUtils.isEmpty(name)) {
            errorMessage.append(" Name is Empty,");
        }
        if (name.length() > 100) {
            errorMessage.append(" Name is invalid,");
        }
        return errorMessage;
    }

    private StringBuilder checkPhone(StringBuilder errorMessage, Set<String> phoneOrEmailSet, String phone, List<Account> accounts) {
        if (StringUtils.isEmpty(phone)) {
            errorMessage.append(" Phone is Empty,");
        } else {
            for (Account account : accounts) {
                if (account.getMsisdn().equals(phone)) {
                    errorMessage.append(" Phone number already exists,");
                    break;
                }
            }
            if (!phone.matches(Constants.PHONE_NUMBER_PATTERN)) {
                errorMessage.append(" Phone is invalid,");
            }
        }
        if (phoneOrEmailSet.contains(phone)) {
            errorMessage.append(" Phone number already exists,");
        }
        return errorMessage;
    }

    private StringBuilder checkEmail(StringBuilder errorMessage, Set<String> phoneOrEmailSet, String email, List<Account> accounts) {
        if (StringUtils.isEmpty(email)) {
            errorMessage.append(" Email is Empty,");
        } else {
            for (Account account : accounts) {
                if (account.getEmail().equals(email)) {
                    errorMessage.append(" Email already exists,");
                    break;
                }
            }
            if (!email.matches(Constants.CHECK_EMAIL_PATTERN)) {
                errorMessage.append(" Email is invalid,");
            }
        }
        if (phoneOrEmailSet.contains(email)) {
            errorMessage.append(" Email already exists,");
        }
        return errorMessage;
    }

    private StringBuilder checkUnitCode(StringBuilder errorMessage, String unitCode, List<Unit> units) {
        boolean checkUnitCode = false;
        if (StringUtils.isEmpty(unitCode)) {
            errorMessage.append(" UnitCode is Empty,");
        } else {
            for (Unit unit : units) {
                if (unit.getCode().equals(unitCode)) {
                    checkUnitCode = true;
                    break;
                }
            }
            if (!checkUnitCode) {
                errorMessage.append(" Unit does not exists,");
            }
        }
        if (unitCode.length() > 50) {
            errorMessage.append(" UnitCode is invalid,");
        }
        return errorMessage;
    }

    private StringBuilder checkPass(StringBuilder errorMessage, String passWord) {
        if (StringUtils.isEmpty(passWord)) {
            errorMessage.append(" Password is Empty,");
        }
        if (!passWord.matches(Constants.PASSWORD_PATTERN)) {
            errorMessage.append(" Password is invalid,");
        }
        return errorMessage;
    }

    @Override
    @SneakyThrows
    public ResponseEntity<ByteArrayResource> exportExcel(AccountExportDTO request) {
        Status status = null;
        if (StringUtils.isNotBlank(request.getStatus())) {
            status = Status.valueOf(request.getStatus());
        }
        List<AccountFilterResDTO> accountFilterResDTOS = accountRepository.getListAccount(request.getName(), request.getEmail(), request.getMsisdn(), request.getUnitCode(), status);
        DateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "User-" + currentDateTime + EXCEL_SUFFIX;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(AccountExcelImpl.sheetName);
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < headers.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headers[col]);
        }
        int rowIdx = 1;
        int rowNumber = 1;
        for (AccountFilterResDTO dto : accountFilterResDTOS) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(dto.getName());
            row.createCell(2).setCellValue(dto.getMsisdn());
            row.createCell(3).setCellValue(dto.getEmail());
            row.createCell(4).setCellValue(dto.getUnitId());
            row.createCell(5).setCellValue(dto.getUnitCode());
            row.createCell(6).setCellValue(dto.getUnitName());
            row.createCell(7).setCellValue(String.valueOf(dto.getStatus()));
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);

        return excelCommon.createOutputFile(workbook, fileName);
    }

    @Override
    @SneakyThrows
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Template_Import_User-" + currentDateTime + EXCEL_SUFFIX;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(AccountExcelImpl.sheetName);
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < headersTemplate.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headersTemplate[col]);
        }
        List<Unit> units = unitRepository.findAll();
        Sheet sheetUnit = workbook.createSheet("Unit");
        int length = units.size();
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Row row = sheetUnit.createRow(i);
            row.createCell(0).setCellValue(units.get(i).getCode());
            data.add(units.get(i).getCode());
        }
        int secondRow = 1;
        int rowNumber = 1;
        for (int i = 1; i <= 5; i++) {
            Row row = sheet.createRow(secondRow++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue("Peter Dan");
            row.createCell(2).setCellValue("9521321321");
            row.createCell(3).setCellValue("peter_dan@gmail.com");
            row.createCell(4).setCellValue("IT");
            row.createCell(5).setCellValue("123456");
        }
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(data.toArray(new String[0]));
        CellRangeAddressList addressList = new CellRangeAddressList(1, sheet.getLastRowNum(), 2, 2);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        for (Row row : sheet) {
            Cell cell = row.getCell(4);
            if (cell == null) {
                cell = row.createCell(4);
            }
            cell.setCellStyle(excelCommon.getCellStyle(workbook));
            cell.setAsActiveCell();
            sheet.addValidationData(validation);
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);

        return excelCommon.createOutputFile(workbook, fileName);
    }

    public void saveImportedUserToDb(List<AccountImportErrorReqDTO> accountImports) {
        String endUserRoleId = null;
        Optional<String> optionalEndUserRole = roleRepository.findRoleIdUsingRoleCode(RoleType.END_USER);
        if (optionalEndUserRole.isPresent()) {
            endUserRoleId = optionalEndUserRole.get();
        }
        List<Account> accounts = new ArrayList<>();
        List<AccountInfo> accountInfos = new ArrayList<>();
        List<AccountRole> accountRoles = new ArrayList<>();
        String defaultMessage = NoticeTemplate.IMPORT_USER_NOTIFY_SMS_DEFAULT_MESSAGE;
        String message =  applicationSettingCommonService.getMessageByKey(NoticeTemplate.SEND_NOTIFICATION_USER_ADDED, null, perRequestContextDto.getBearToken(), defaultMessage);
        for (AccountImportErrorReqDTO accountImportReq: accountImports) {
            if (StringUtils.isNotEmpty(accountImportReq.getMessage())) continue;

            AccountImportReqDTO accountImportReqDTO = accountImportReq.getAccountImportReqDTO();
            if (createNewAccountAndAssignInfo(accountImportReq, endUserRoleId, accounts, accountInfos, accountRoles, perRequestContextDto.getCurrentAccountId())) {
                sendSmsNotifyAfterAddNewUser(accountImportReqDTO.getRequestId(), accountImportReqDTO.getPhone(), accountImportReqDTO.getPassword(), message);
            }
        }
        accountTransactionService.saveImportedAccountsToDB(accounts, accountInfos, accountRoles);
    }

    @Override
    public void sendSmsNotifyAfterAddNewUser(String requestId, String phoneOfUser, String password, String message) {
        message = message.replace("{phone}", phoneOfUser).replace("{password}", password);
        super.sendSMS(phoneOfUser, message);
    }

    public boolean createNewAccountAndAssignInfo(AccountImportErrorReqDTO accountImportReq, String endUserRoleId, List<Account> accounts, List<AccountInfo> accountInfos, List<AccountRole> accountRoles, String currentAccountId) {
        AccountImportReqDTO accountImportReqDTO = accountImportReq.getAccountImportReqDTO();
        NewAccountBuilder newAccountBuilder = new NewAccountBuilder(unitRepository);
        Account newAccount = newAccountBuilder.newAccountBuilder(accountImportReqDTO);
        String accountId = newAccount.getId();
        AccountInfo newAccountInfo = NewAccountBuilder.newAccountInfoBuider(accountImportReqDTO ,accountId);
        try (Response response = keyCloakService.createKeycloakUser(accountImportReqDTO.getPhone(), accountImportReqDTO.getEmail(), accountImportReqDTO.getName(), accountImportReqDTO.getPassword())) {
            if (response.getStatus() != 201) {
                logger.error("Create Keycloak account failed when import user: {}", accountImportReqDTO.getPhone());
                accountImportReq.setMessage(" Create Keycloak account failed ");
                return false;
            }
            String newKeycloakUserId = keyCloakService.getKeycloakUserIdByUsername(accountImportReqDTO.getPhone());
            newAccount.setKeycloakID(newKeycloakUserId);
            newAccount.setApproverId(currentAccountId);
        }
        accounts.add(newAccount);
        accountInfos.add(newAccountInfo);
        accountImportReq.setMessage(" Success ");
        if (endUserRoleId != null) {
            AccountRole newAccountRole = new AccountRole(accountId, endUserRoleId);
            accountRoles.add(newAccountRole);
        }
        return true;
    }

    public ResponseEntity<ByteArrayResource> exportExcelError(List<AccountImportErrorReqDTO> accountImportErrorReqDTOS) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "User_Error-" + currentDateTime + EXCEL_SUFFIX;
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(AccountExcelImpl.sheetName);
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < headersError.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headersError[col]);
        }
        int rowIdx = 1;
        int rowNumber = 1;
        for (AccountImportErrorReqDTO importError : accountImportErrorReqDTOS) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(importError.getAccountImportReqDTO().getName());
            row.createCell(2).setCellValue(importError.getAccountImportReqDTO().getPhone());
            row.createCell(3).setCellValue(importError.getAccountImportReqDTO().getEmail());
            row.createCell(4).setCellValue(importError.getAccountImportReqDTO().getUnitCode());
            row.createCell(5).setCellValue(importError.getAccountImportReqDTO().getPassword());
            String error = importError.getMessage().substring(0, importError.getMessage().length() - 1);
            row.createCell(6).setCellValue(error);
        }
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

}

