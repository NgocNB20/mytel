package mm.com.mytelpay.family.business.bookingcar;

import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.enums.DirectionType;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.BookingCarDetail;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repo.BookingCarDetailRepository;
import mm.com.mytelpay.family.repo.BookingCarRepository;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class BookingCarReportServiceIml extends BookingBaseBusiness implements BookingCarReportService {
    private static final String[] HEADERS = {"No", "User booking", "Phone booking", "Trip type", "Original", "Destination", "Member", "Reason", "Status booking", "Created time", "Driver name", "Driver phone", "Car name", "License plate", "Status", "Driver name", "Driver phone", "Car name", "License plate", "Status", "Url"};
    private static final String SHEET = "Booking report";
    @Autowired
    public ExcelCommon excelCommon;
    @Autowired
    private BookingCarRepository bookingCarRepository;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    @Autowired
    BookingCarDetailRepository bookingCarDetailRepository;

    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(ListAccountReportReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<ListBookingCarReportExportDTO> resDTOList = getListBookingCarReportExcel(request);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Booking_Car_Report_" + currentDateTime + ".xlsx";
        Workbook workbook = excelCommon.getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(SHEET);
        Row header = sheet.createRow(0);
        CellRangeAddress mergedCellOutbound = new CellRangeAddress(0, 0, 9, 13);
        header.createCell(9).setCellValue("OUTBOUND");
        sheet.addMergedRegion(mergedCellOutbound);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment((short) HorizontalAlignment.CENTER.ordinal());
        header.getCell(9).setCellStyle(style);
        CellRangeAddress mergedCellReturn = new CellRangeAddress(0, 0, 14, 18);
        header.createCell(14).setCellValue("RETURN");
        sheet.addMergedRegion(mergedCellReturn);
        style.setAlignment((short) HorizontalAlignment.CENTER.ordinal());
        header.getCell(14).setCellStyle(style);
        Row headerRow = sheet.createRow(1);
        for (int col = 0; col < HEADERS.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS[col]);
        }
        writeToFileExcel(resDTOList, sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet, numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private void writeToFileExcel(List<ListBookingCarReportExportDTO> resDTOList, Sheet sheet) {
        int rowIdx = 2;
        int rowNumber = 1;
        for (ListBookingCarReportExportDTO dto : resDTOList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(dto.getUser());
            row.createCell(2).setCellValue(dto.getPhone());
            row.createCell(3).setCellValue(String.valueOf(dto.getTripType()));
            row.createCell(4).setCellValue(dto.getOriginal());
            row.createCell(5).setCellValue(dto.getDestination());
            row.createCell(6).setCellValue(dto.getMembers());
            row.createCell(7).setCellValue(dto.getReason());
            row.createCell(8).setCellValue(String.valueOf(dto.getStatusBooking()));
            row.createCell(9).setCellValue(String.valueOf(dto.getCreatedTime()));
            row.createCell(10).setCellValue(dto.getDriverNameOutbound());
            row.createCell(11).setCellValue(dto.getPhoneDriverOutbound());
            row.createCell(12).setCellValue(dto.getCarNameOutbound());
            row.createCell(13).setCellValue(dto.getLicensePlateOutbound());
            row.createCell(14).setCellValue(String.valueOf(dto.getStatusOutbound()));
            row.createCell(15).setCellValue(dto.getDriverNameReturn());
            row.createCell(16).setCellValue(dto.getPhoneDriverReturn());
            row.createCell(17).setCellValue(dto.getCarNameReturn());
            row.createCell(18).setCellValue(dto.getLicensePlateReturn());
            String statusReturn = StringUtils.isEmpty(String.valueOf(dto.getStatusReturn())) ? "" : String.valueOf(dto.getStatusReturn());
            if (!statusReturn.equals("null")) {
                row.createCell(19).setCellValue(statusReturn);
            }
            row.createCell(20).setCellValue(dto.getFile());
        }
    }

    public List<ListBookingCarReportExportDTO> getListBookingCarReportExcel(ListAccountReportReqDTO request) {
        List<String> driverIds = new ArrayList<>();
        List<String> carIds = new ArrayList<>();
        List<ListBookingCarReportExportDTO> resDTOList = addDriverIdAndCarIdBookingCarReportExcel((request));
        getListAccountIdAndCarId(driverIds, carIds, resDTOList);
        List<String> listDriverIds = distinctDriverId(driverIds);
        List<String> listCarIds = distinctCarId(carIds);
        AccountReportReqDTO accountReportReqDTO = new AccountReportReqDTO();
        accountReportReqDTO.setAccountId(listDriverIds);
        List<AccountReportDTO> accountReportDTOS = accountReport(accountReportReqDTO);
        CarRportReqDTO carRportReqDTO = new CarRportReqDTO();
        carRportReqDTO.setCarId(listCarIds);
        List<CarReportDTO> carReportDTO = getListCarReport(carRportReqDTO);
        setInfoDriverAndCar(resDTOList, accountReportDTOS, carReportDTO);

        List<String> fileIds = resDTOList.stream().map(ListBookingCarReportExportDTO::getId).collect(Collectors.toList());
        Map<String, FileResponse> listMap = getStringFileResponseMap(fileIds);
        for (ListBookingCarReportExportDTO dto : resDTOList) {
            if (Objects.nonNull(listMap.get(dto.getId()))) {
                dto.setFile(listMap.get(dto.getId()).getUrl());
            }
        }
        return resDTOList;
    }

    private void getListAccountIdAndCarId(List<String> accountIds, List<String> carIds, List<ListBookingCarReportExportDTO> resDTOList) {
        for (ListBookingCarReportExportDTO dto : resDTOList) {
            accountIds.add(dto.getAccountId());
            accountIds.add(dto.getDriverIdOutbound());
            accountIds.add(dto.getDriverIdReturn());
            carIds.add(dto.getCarIdReturn());
            carIds.add(dto.getCarIdOutbound());
        }
    }

    private void setInfoDriverAndCar(List<ListBookingCarReportExportDTO> resDTOList, List<AccountReportDTO> accountReportDTOS, List<CarReportDTO> carReportDTO) {
        resDTOList.forEach(reportExportDTO -> {
            setInfoAccount(reportExportDTO, accountReportDTOS);
            setInfoCar(reportExportDTO, carReportDTO);
        });
    }

    private void setInfoAccount(ListBookingCarReportExportDTO reportExportDTO, List<AccountReportDTO> accountReportDTOS) {
        accountReportDTOS.forEach(accountReportDTO -> {
            if (accountReportDTO.getAccountId().equals(reportExportDTO.getAccountId())) {
                reportExportDTO.setUser(accountReportDTO.getFullName());
                reportExportDTO.setPhone(accountReportDTO.getPhone());
            }
            if (accountReportDTO.getAccountId().equals(reportExportDTO.getDriverIdOutbound())) {
                reportExportDTO.setDriverNameOutbound(accountReportDTO.getFullName());
                reportExportDTO.setPhoneDriverOutbound(accountReportDTO.getPhone());
            }
            if (accountReportDTO.getAccountId().equals(reportExportDTO.getDriverIdReturn())) {
                reportExportDTO.setDriverNameReturn(accountReportDTO.getFullName());
                reportExportDTO.setPhoneDriverReturn(accountReportDTO.getPhone());
            }
        });
    }

    private void setInfoCar(ListBookingCarReportExportDTO reportExportDTO, List<CarReportDTO> carReportDTO) {
        carReportDTO.forEach(listCarReportDTO -> {
            if (listCarReportDTO.getId().equals(reportExportDTO.getCarIdOutbound())) {
                reportExportDTO.setCarNameOutbound(listCarReportDTO.getName());
                reportExportDTO.setLicensePlateOutbound(listCarReportDTO.getLicensePlate());
            }
            if (listCarReportDTO.getId().equals(reportExportDTO.getCarIdReturn())) {
                reportExportDTO.setCarNameReturn(listCarReportDTO.getName());
                reportExportDTO.setLicensePlateReturn(listCarReportDTO.getLicensePlate());
            }
        });
    }

    public List<ListBookingCarReportExportDTO> addDriverIdAndCarIdBookingCarReportExcel(ListAccountReportReqDTO request) {
        List<ListBookingCarReportExportDTO> resDTOList = getListBookingCarReportResDTOS((request));
        if (CollectionUtils.isEmpty(resDTOList)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        for (ListBookingCarReportExportDTO dto : resDTOList) {
            String bookingCarId = dto.getId();
            List<BookingCarDetail> carDetails = bookingCarDetailRepository.findByBookingCarId(bookingCarId);
            checkBookingCarType(dto, carDetails);
        }
        return resDTOList;
    }

    private void checkBookingCarType(ListBookingCarReportExportDTO dto, List<BookingCarDetail> carDetails) {
        if (dto.getTripType().equals(CarBookingType.ONE_WAY) || dto.getTripType().equals(CarBookingType.DRIVER_FOLLOW)) {
            for (BookingCarDetail bookingCarDetail : carDetails) {
                dto.setDriverIdOutbound(bookingCarDetail.getDriverId());
                dto.setCarIdOutbound(bookingCarDetail.getCarId());
                dto.setStatusOutbound(bookingCarDetail.getStatus());
            }
        } else {
            for (BookingCarDetail bookingCarDetail : carDetails) {
                if (bookingCarDetail.getType().equals(DirectionType.OUTBOUND) || bookingCarDetail.getType().equals(DirectionType.DEFAULT)) {
                    dto.setDriverIdOutbound(bookingCarDetail.getDriverId());
                    dto.setCarIdOutbound(bookingCarDetail.getCarId());
                    dto.setStatusOutbound(bookingCarDetail.getStatus());
                } else {
                    dto.setDriverIdReturn(bookingCarDetail.getDriverId());
                    dto.setCarIdReturn(bookingCarDetail.getCarId());
                    dto.setStatusReturn(bookingCarDetail.getStatus());
                }
            }
        }
    }


    public List<ListBookingCarReportExportDTO> getListBookingCarReportResDTOS(ListAccountReportReqDTO request) {
        List<String> listAccountId = new ArrayList<>();
        LocalDateTime fromTime;
        LocalDateTime toTime;

        listAccountId = getListAccountIds(request, listAccountId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        String fromDate = request.getFromDate();
        String toDate = request.getToDate();
        fromTime = checkFromTime(request, formatter, fromDate);
        toTime = checkToTime(request, formatter, toDate);
        if (toTime != null && fromTime != null && toTime.isBefore(fromTime)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.TIME_RETURN_INVALID, null);
        }
        CarBookingType bookingType = StringUtils.isBlank(request.getBookingType()) ? null : CarBookingType.valueOf(request.getBookingType());
        BookingStatus bookingStatus = StringUtils.isBlank(request.getStatus()) ? null : BookingStatus.valueOf(request.getStatus());
        String driverId = StringUtils.isBlank(request.getDriverId()) ? null : request.getDriverId();

        return bookingCarRepository.getListBookingReportReportExport(bookingStatus, driverId, bookingType, fromTime, toTime, listAccountId);
    }

    private LocalDateTime checkToTime(ListAccountReportReqDTO request, DateTimeFormatter formatter, String toDate) {
        LocalDateTime toTime = null;
        if (!StringUtils.isBlank(toDate)) {
            try {
                LocalDate localDate = LocalDate.parse(toDate, formatter);
                toTime = LocalDateTime.of(localDate, LocalTime.MIN);
            } catch (DateTimeParseException e) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.TO_DATE_INVALID, null);
            }
        }
        return toTime;
    }

    private LocalDateTime checkFromTime(ListAccountReportReqDTO request,  DateTimeFormatter formatter, String fromDate) {
        LocalDateTime fromTime = null;
        if (!StringUtils.isBlank(fromDate)) {
            try {
                LocalDate localDate = LocalDate.parse(fromDate, formatter);
                fromTime = LocalDateTime.of(localDate, LocalTime.MIN);
            } catch (DateTimeParseException e) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_INVALID, null);
            }
        }
        return fromTime;
    }

    @Nullable
    private List<String> getListAccountIds(ListAccountReportReqDTO request, List<String> listAccountId) {
        String phone = StringUtils.isBlank(request.getPhone()) ? null : request.getPhone();
        getListAccountIds(request.getRequestId(), listAccountId, phone);
        if (listAccountId.isEmpty()) {
            listAccountId = null;
        }
        return listAccountId;
    }

    private List<CarReportDTO> getListCarReport(CarRportReqDTO carRportReqDTO) {
        List<String> id = carRportReqDTO.getCarId();
        return resourceRestTemplate.carReportDTOS(id, perRequestContextDto.getBearToken());
    }

    private List<AccountReportDTO> accountReport(AccountReportReqDTO accountReportReqDTO) {
        List<String> id = accountReportReqDTO.getAccountId();
        return accountRestTemplate.getListAccountReport(accountReportReqDTO.getRequestId(), id, perRequestContextDto.getBearToken());
    }

    private List<String> distinctCarId(List<String> carIds) {
        return carIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> distinctDriverId(List<String> driverIds) {
        return driverIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public PageImpl<ListBookingCarReportDTO> getListAccountReport(ListAccountReportReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        List<String> listAccountId = new ArrayList<>();
        listAccountId = getListAccountIds(request, listAccountId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        String fromDate = request.getFromDate();
        LocalDateTime fromTime = checkFromTime(request, formatter, fromDate);
        String toDate = request.getToDate();
        LocalDateTime toTime = checkToTime(request, formatter, toDate);
        if (toTime != null && fromTime != null && toTime.isBefore(fromTime)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.TIME_RETURN_INVALID, null);
        }
        BookingStatus bookingStatus = StringUtils.isBlank(request.getStatus()) ? null : BookingStatus.valueOf(request.getStatus());
        CarBookingType bookingType = StringUtils.isBlank(request.getBookingType()) ? null : CarBookingType.valueOf(request.getBookingType());
        String driverId = StringUtils.isBlank(request.getDriverId()) ? null : request.getDriverId();
        Page<ListBookingCarReportDTO> listAccountReportDTOS = bookingCarRepository.getListAccountReport(bookingStatus, driverId, bookingType, fromTime, toTime, listAccountId, pageable);
        if (listAccountReportDTOS.isEmpty()) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        List<String> accountIds = listAccountReportDTOS.stream().map(ListBookingCarReportDTO::getAccountId).collect(Collectors.toList());
        List<String> distinctAccountId = accountIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        AccountReportReqDTO accountReportReqDTO = new AccountReportReqDTO();
        accountReportReqDTO.setAccountId(distinctAccountId);
        List<AccountReportDTO> accountReportDTOS = accountReport(accountReportReqDTO);
        listAccountReportDTOS.forEach(listAccountReportDTO ->
                accountReportDTOS.forEach(accountReport -> {
                    if (accountReport.getAccountId().equals(listAccountReportDTO.getAccountId())) {
                        listAccountReportDTO.setName(accountReport.getFullName());
                        listAccountReportDTO.setMsisdn(accountReport.getPhone());
                    }
                })
        );

        List<String> fileIds = listAccountReportDTOS.getContent().stream().map(ListBookingCarReportDTO::getId).collect(Collectors.toList());
        Map<String, FileResponse> listMap = getStringFileResponseMap(fileIds);
        listAccountReportDTOS.getContent().forEach(r -> r.setFile(listMap.get(r.getId())));

        return new PageImpl<>(listAccountReportDTOS.getContent(), listAccountReportDTOS.getPageable(), listAccountReportDTOS.getTotalElements());
    }

    @NotNull
    private Map<String, FileResponse> getStringFileResponseMap(List<String> fileIds) {
        List<FileAttach> fileAttaches = fileRepository.getFileAttachByObjectIdInAndObjectType(fileIds, ObjectType.BOOKING_CAR);
        Map<String, FileResponse> listMap = new HashMap<>();
        for (FileAttach f : fileAttaches) {
            if (!listMap.containsKey(f.getObjectId())) {
                listMap.put(f.getObjectId(), new FileResponse());
            }
            listMap.put(f.getObjectId(), mapper.map(f, FileResponse.class));
        }
        return listMap;
    }

}
