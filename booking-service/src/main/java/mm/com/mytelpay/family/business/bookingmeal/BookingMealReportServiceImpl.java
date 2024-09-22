package mm.com.mytelpay.family.business.bookingmeal;

import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.AccountReportDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.*;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.CanteenResourceDTO;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repo.BookingMealRepository;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class BookingMealReportServiceImpl extends BookingBaseBusiness implements BookingMealReportService {

    public BookingMealReportServiceImpl() {
        logger = LogManager.getLogger(BookingMealReportServiceImpl.class);
    }

    @Autowired
    private BookingMealRepository bookingMealRepository;

    @Autowired
    public ExcelCommon excelCommon;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    private static final String SHEET = "Booking meal report";

    private static final String[] HEADERS_REPORT = {"No", "User", "Phone", "Meal type", "Order Time", "Meal Time", "Canteen", "Unit", "Status"};

    private static final String[] HEADERS_REPORT_FEE = {"No", "User", "Phone", "Unit", "Canteen", "Fee", "Refund", "Total Fee"};

    private static final String SHEET_FEE = "Booking meal fee report";

    @Override
    public ResponseEntity<ByteArrayResource> exportExcelReportBookingMeal(ReportBookingMealReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<ReportOrderMealResDTO> list = getReportBookingMealForExport(request);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Booking_Meal_Report_" + currentDateTime + ".xlsx";
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET); // Create sheet with sheet name
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < HEADERS_REPORT.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS_REPORT[col]);
        }
        writeToFileReportExcel(list, sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private void writeToFileReportExcel(List<ReportOrderMealResDTO> resDTOList, Sheet sheet) {
        int rowIdx = 1;
        int rowNumber = 1;
        for (ReportOrderMealResDTO dto : resDTOList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(dto.getUserName());
            row.createCell(2).setCellValue(dto.getPhone());
            row.createCell(3).setCellValue(String.valueOf(dto.getMealType()));
            row.createCell(4).setCellValue(Util.convertLocalDateTimeToString(dto.getOrderTime()));
            row.createCell(5).setCellValue(Util.convertLocalDateToString(dto.getMealTime()));
            row.createCell(6).setCellValue(dto.getCanteenName());
            row.createCell(7).setCellValue(dto.getUnit());
            row.createCell(8).setCellValue(String.valueOf(dto.getStatus()));
        }
    }

    @Override
    public PageImpl<ReportOrderMealResDTO> getListBookingMealReport(ReportBookingMealReqDTO request, HttpServletRequest httpServletRequest) {
        Page<ReportOrderMealResDTO> responses = getReportBookingMeal(request);
        return new PageImpl<>(responses.getContent(), responses.getPageable(), responses.getTotalElements());
    }

    private Page<ReportOrderMealResDTO> getReportBookingMeal(ReportBookingMealReqDTO request) {
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());

        try {
            LocalDate fromTime = StringUtils.isBlank(request.getFromDate()) ? null : Util.convertToLocalDate(request.getFromDate());
            LocalDate toTime = StringUtils.isBlank(request.getToDate()) ? null : Util.convertToLocalDate(request.getToDate());
            if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
            }
            Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
            MealDetailStatus status = !Objects.equals(request.getStatus(), "") ? MealDetailStatus.valueOf(request.getStatus()) : null;
            MealType type = !Objects.equals(request.getMeal(), "") ? MealType.valueOf(request.getMeal()) : null;

            Page<ReportOrderMealResDTO> responses = bookingMealRepository.filter(accountId,
                    status,
                    request.getCanteenId(),
                    type,
                    request.getUnitId(),
                    fromTime,
                    toTime,
                    pageable);

            List<ReportOrderMealResDTO> list = responses.getContent();
            if (list.isEmpty()) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS);
            }
            setCanteenAndUnit(request, accountReportDTO, list);
            logger.info("Found:{} booking meal", responses.getTotalElements());
            return responses;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        } catch (Exception e) {
            logger.error("Get List booking meal unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    private List<ReportOrderMealResDTO> getReportBookingMealForExport(ReportBookingMealReqDTO request) {
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());

        try {
            LocalDate fromTime = StringUtils.isBlank(request.getFromDate()) ? null : Util.convertToLocalDate(request.getFromDate());
            LocalDate toTime = StringUtils.isBlank(request.getToDate()) ? null : Util.convertToLocalDate(request.getToDate());
            if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
            }
            MealDetailStatus status = !Objects.equals(request.getStatus(), "") ? MealDetailStatus.valueOf(request.getStatus()) : null;
            MealType type = !Objects.equals(request.getMeal(), "") ? MealType.valueOf(request.getMeal()) : null;

            List<ReportOrderMealResDTO> list = bookingMealRepository.exportReport(accountId,
                    status,
                    request.getCanteenId(),
                    type,
                    request.getUnitId(),
                    fromTime,
                    toTime);

            if (list.isEmpty()) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS);
            }

            setCanteenAndUnit(request, accountReportDTO, list);
            logger.info("Found:{} booking meal ", list.size());
            return list;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        } catch (Exception e) {
            logger.error("Get List booking meal unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    private void setCanteenAndUnit(ReportBookingMealReqDTO request, List<AccountReportDTO> accountReportDTO, List<ReportOrderMealResDTO> list) {
        if (!list.isEmpty()) {
            List<String> canteenIds = list.stream().map(ReportOrderMealResDTO::getCanteenId).collect(Collectors.toList());
            List<CanteenResourceDTO> canteenResourceDTOS = new ArrayList<>();
            try {
                canteenResourceDTOS = resourceRestTemplate.getListCanteenByIds(canteenIds, request.getRequestId(), perRequestContextDto.getBearToken());
            } catch (Exception e) {
                logger.error("--> Error when get list canteen {}", e.getMessage());
            }
            List<UnitDTO> unitDTOS = accountRestTemplate.getListUnit(perRequestContextDto.getBearToken());
            for (ReportOrderMealResDTO res : list) {

                Optional<AccountReportDTO> account = accountReportDTO.stream()
                        .filter(acc -> acc.getAccountId().equals(res.getUserId()))
                        .findFirst();
                res.setPhone(account.map(AccountReportDTO::getPhone).orElse(null));
                res.setUserName(account.map(AccountReportDTO::getFullName).orElse(null));

                Optional<CanteenResourceDTO> canteenResourceDTO = canteenResourceDTOS.stream()
                        .filter(c -> c.getId().equals(res.getCanteenId()))
                        .findFirst();
                res.setCanteenName(canteenResourceDTO.map(CanteenResourceDTO::getName).orElse(null));

                Optional<UnitDTO> unitDTO = unitDTOS.stream().filter(u -> u.getId().equals(res.getUnitId())).findFirst();
                res.setUnit(unitDTO.map(UnitDTO::getName).orElse(null));
            }
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcelReportFeeBookingMeal(ReportFeeBookingMealReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<ReportFeeOrderMealResDTO> list = getReportFeeBookingMealForExport(request);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Booking_Fee_Report_" + currentDateTime + ".xlsx";
        Workbook workbook = excelCommon.getWorkbook(fileName);
        // Create sheet
        Sheet sheet = workbook.createSheet(SHEET_FEE); // Create sheet with sheet name
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < HEADERS_REPORT_FEE.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS_REPORT_FEE[col]);
        }
        writeToFileFeeReportExcel(list, sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private void writeToFileFeeReportExcel(List<ReportFeeOrderMealResDTO> resDTOList, Sheet sheet) {
        int rowIdx = 1;
        int rowNumber = 1;
        for (ReportFeeOrderMealResDTO dto : resDTOList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(dto.getUserName());
            row.createCell(2).setCellValue(dto.getPhone());
            row.createCell(3).setCellValue(dto.getUnit());
            row.createCell(4).setCellValue(dto.getCanteenName());
            row.createCell(5).setCellValue(dto.getFee());
            row.createCell(6).setCellValue(dto.getRefund());
            row.createCell(7).setCellValue(dto.getTotalFee());
        }
    }

    @Override
    public PageImpl<ReportFeeOrderMealResDTO> getListBookingFeeMealReport(ReportFeeBookingMealReqDTO request, HttpServletRequest httpServletRequest) {
        Page<ReportFeeOrderMealResDTO> response = getReportFeeBookingMeal(request);
        return new PageImpl<>(response.getContent(), response.getPageable(), response.getTotalElements());
    }

    private Page<ReportFeeOrderMealResDTO> getReportFeeBookingMeal(ReportFeeBookingMealReqDTO request) {
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());

        try {
            LocalDate fromTime = null;
            LocalDate toTime = null;
            Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
            if (Objects.nonNull(request.getFromDate()) && !request.getFromDate().isBlank()) {
                fromTime = Util.convertToLocalDate(request.getFromDate());
            }
            if (Objects.nonNull(request.getToDate()) && !request.getToDate().isBlank()) {
                toTime = Util.convertToLocalDate(request.getToDate());
            }
            if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
            }
            MealType type = !Objects.equals(request.getMeal(), "") ? MealType.valueOf(request.getMeal()) : null;

            Page<ReportFeeOrderMealResDTO> responses = bookingMealRepository.reportFeeBookingMeal(accountId,
                    request.getCanteenId(),
                    type,
                    request.getUnitId(),
                    fromTime,
                    toTime,
                    pageable);

            List<ReportFeeOrderMealResDTO> list = responses.getContent();
            if (list.isEmpty()) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS);
            }
            setCanteenAndUnitAndFee(request.getRequestId(), accountReportDTO, list);

            logger.info("Found:{} booking meal.", responses.getTotalElements());
            return responses;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        } catch (Exception e) {
            logger.error("Get report fee booking meal unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    private void setCanteenAndUnitAndFee(String requestId, List<AccountReportDTO> accountReportDTO, List<ReportFeeOrderMealResDTO> list) {
        if (!list.isEmpty()) {
            List<String> canteenIds = list.stream().map(ReportFeeOrderMealResDTO::getCanteenId).collect(Collectors.toList());
            List<CanteenResourceDTO> canteenResourceDTOS = new ArrayList<>();
            try {
                canteenResourceDTOS = resourceRestTemplate.getListCanteenByIds(canteenIds, requestId, perRequestContextDto.getBearToken());
            } catch (Exception e) {
                logger.error("--> Error when get list canteen {}", e.getMessage());
            }
            List<UnitDTO> unitDTOS = accountRestTemplate.getListUnit(perRequestContextDto.getBearToken());
            for (ReportFeeOrderMealResDTO res : list) {

                Optional<AccountReportDTO> account = accountReportDTO.stream()
                        .filter(acc -> acc.getAccountId().equals(res.getUserId()))
                        .findFirst();
                res.setPhone(account.map(AccountReportDTO::getPhone).orElse(null));
                res.setUserName(account.map(AccountReportDTO::getFullName).orElse(null));

                Optional<CanteenResourceDTO> canteenResourceDTO = canteenResourceDTOS.stream()
                        .filter(c -> c.getId().equals(res.getCanteenId()))
                        .findFirst();
                res.setCanteenName(canteenResourceDTO.map(CanteenResourceDTO::getName).orElse(null));

                Optional<UnitDTO> unitDTO = unitDTOS.stream().filter(u -> u.getId().equals(res.getUnitId())).findFirst();
                res.setUnit(unitDTO.map(UnitDTO::getName).orElse(null));

                res.setRefund(res.getStatus().equals(MealDetailStatus.CANCEL) ? res.getFee() : 0);

                res.setTotalFee(res.getFee() - res.getRefund());
            }
        }
    }

    private List<ReportFeeOrderMealResDTO> getReportFeeBookingMealForExport(ReportFeeBookingMealReqDTO request) {
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());

        try {
            LocalDate fromTime = StringUtils.isBlank(request.getFromDate()) ? null : Util.convertToLocalDate(request.getFromDate());
            LocalDate toTime = StringUtils.isBlank(request.getToDate()) ? null : Util.convertToLocalDate(request.getToDate());
            if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
            }
            MealType type = !Objects.equals(request.getMeal(), "") ? MealType.valueOf(request.getMeal()) : null;

            List<ReportFeeOrderMealResDTO> list = bookingMealRepository.reportFeeBookingMealExport(accountId,
                    request.getCanteenId(),
                    type,
                    request.getUnitId(),
                    fromTime,
                    toTime);
            if (list.isEmpty()) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS);
            }

            setCanteenAndUnitAndFee(request.getRequestId(), accountReportDTO, list);
            logger.info("Found:{} booking meal.", list.size());
            return list;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        } catch (Exception e) {
            logger.error("Get report fee booking meal unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }
}
