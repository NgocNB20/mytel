package mm.com.mytelpay.family.business.bookinghotel;

import com.fasterxml.jackson.core.type.TypeReference;
import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.AccountReportDTO;
import mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelReqDTO;
import mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelResDTO;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.HotelDTO;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repo.BookingHotelRepository;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class BookingHotelReportServiceImpl extends BookingBaseBusiness implements BookingHotelReportService {

    public BookingHotelReportServiceImpl() {
        logger = LogManager.getLogger(BookingHotelReportServiceImpl.class);
    }

    @Autowired
    private BookingHotelRepository bookingHotelRepository;

    @Autowired
    public ExcelCommon excelCommon;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    private static final String SHEET = "Booking Hotel Report";

    private static final String[] HEADERS_REPORT = {"No", "User", "Phone", "Start date", "End date", "Member", "Hotel", "Booking Fee", "Service Fee", "Status", "Booking Invoice"};


    @Override
    public ResponseEntity<ByteArrayResource> exportExcelReportBookingHotel(ReportBookingHotelReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        List<ReportBookingHotelResDTO> responses = getReportBookingHotelForExport(request);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Booking_Hotel_Report_" + currentDateTime + ".xlsx";
        Workbook workbook = excelCommon.getWorkbook(fileName);
        // Create sheet
        Sheet sheet = workbook.createSheet(SHEET); // Create sheet with sheet name
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < HEADERS_REPORT.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS_REPORT[col]);
        }
        writeToFileReportExcel(responses , sheet);
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        excelCommon.autosizeColumn(sheet , numberOfColumn);
        return excelCommon.createOutputFile(workbook, fileName);
    }

    private void writeToFileReportExcel(List<ReportBookingHotelResDTO> resDTOList, Sheet sheet){
        int rowIdx = 1;
        int rowNumber = 1;
        for (ReportBookingHotelResDTO dto: resDTOList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowNumber++);
            row.createCell(1).setCellValue(dto.getFullName());
            row.createCell(2).setCellValue(dto.getPhone());
            row.createCell(3).setCellValue(Util.convertLocalDateTimeToString(dto.getStartDate()));
            row.createCell(4).setCellValue(Util.convertLocalDateTimeToString(dto.getEndDate()));
            row.createCell(5).setCellValue(Objects.isNull(dto.getMember()) ? "" : dto.getMember().toString());
            row.createCell(6).setCellValue(dto.getHotelName());
            row.createCell(7).setCellValue(dto.getBookingFee() != null ? dto.getBookingFee() : 0);
            row.createCell(8).setCellValue(dto.getServiceFee() != null ? dto.getServiceFee() : 0);
            row.createCell(9).setCellValue(String.valueOf(dto.getStatus()));
            row.createCell(10).setCellValue(ObjectUtils.isNotEmpty(dto.getBookingInvoice()) ?
                    dto.getBookingInvoice().stream().map(FileResponse::getUrl).collect(Collectors.toList()).toString() : "");
        }
    }

    @Override
    public PageImpl<ReportBookingHotelResDTO> getListBookingHotelReport(ReportBookingHotelReqDTO request, HttpServletRequest httpServletRequest) {
        Page<ReportBookingHotelResDTO> responses = getReportBookingHotel(request);
        return new PageImpl<>(responses.getContent(), responses.getPageable(), responses.getTotalElements());
    }

    private Page<ReportBookingHotelResDTO> getReportBookingHotel(ReportBookingHotelReqDTO request) {
        accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(),perRequestContextDto.getBearToken());
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());

        try {
            LocalDate fromTime = null;
            LocalDate toTime = null;
            Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
            if (Objects.nonNull(request.getFrom()) && !request.getFrom().isBlank()) {
                fromTime = Util.convertToLocalDate(request.getFrom());
            }
            if (Objects.nonNull(request.getTo()) && !request.getTo().isBlank()) {
                toTime = Util.convertToLocalDate(request.getTo());
            }
            if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
            }
            BookingStatus status = !Objects.equals(request.getStatus(), "") ? BookingStatus.valueOf(request.getStatus()) : null;

            Page<ReportBookingHotelResDTO> responses = bookingHotelRepository.getListBookingReport(accountId,
                    status,
                    request.getHotelId(),
                    fromTime,
                    toTime,
                    pageable);

            List<ReportBookingHotelResDTO> list = responses.getContent();
            if (list.isEmpty()) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS);
            }

            List<String> hotelIds = responses.getContent().stream().map(ReportBookingHotelResDTO::getHotelId).distinct().collect(Collectors.toList());

            List<HotelDTO> hotelDTOList = resourceRestTemplate.getListHotelByIds(hotelIds, request.getRequestId(), perRequestContextDto.getBearToken());
            for (ReportBookingHotelResDTO res : list) {

                Optional<AccountReportDTO> account = accountReportDTO.stream()
                        .filter(acc -> acc.getAccountId().equals(res.getAccountId()))
                        .findFirst();
                res.setPhone(account.map(AccountReportDTO::getPhone).orElse(null));
                res.setFullName(account.map(AccountReportDTO::getFullName).orElse(null));
                //set hotel name
                res.setHotelName(hotelDTOList.stream().filter(h -> res.getHotelId().equals(h.getId())).findFirst().orElse(new HotelDTO()).getName());
            }
            logger.info("Found:{} booking hotel.", responses.getTotalElements());
            return responses;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        } catch (Exception e) {
            logger.error("Get List booking hotel unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    private List<ReportBookingHotelResDTO> getReportBookingHotelForExport(ReportBookingHotelReqDTO request) {
        accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(),perRequestContextDto.getBearToken());
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());

        try {
            LocalDate fromTime = null;
            LocalDate toTime = null;
            if (Objects.nonNull(request.getFrom()) && !request.getFrom().isBlank()) {
                fromTime = Util.convertToLocalDate(request.getFrom());
            }
            if (Objects.nonNull(request.getTo()) && !request.getTo().isBlank()) {
                toTime = Util.convertToLocalDate(request.getTo());
            }
            if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
            }
            BookingStatus status = !Objects.equals(request.getStatus(), "") ? BookingStatus.valueOf(request.getStatus()) : null;

            List<ReportBookingHotelResDTO> responses = bookingHotelRepository.getListBookingReport(accountId,
                    status,
                    request.getHotelId(),
                    fromTime,
                    toTime);


            if(!responses.isEmpty()) {

                List<String> hotelIds = responses.stream().map(ReportBookingHotelResDTO::getHotelId).distinct().collect(Collectors.toList());

                List<HotelDTO> hotelDTOList = resourceRestTemplate.getListHotelByIds(hotelIds, request.getRequestId(), perRequestContextDto.getBearToken());
                for(ReportBookingHotelResDTO res : responses) {

                    Optional<AccountReportDTO> account = accountReportDTO.stream()
                            .filter(acc -> acc.getAccountId().equals(res.getAccountId()))
                            .findFirst();
                    res.setPhone(account.map(AccountReportDTO::getPhone).orElse(null));
                    res.setFullName(account.map(AccountReportDTO::getFullName).orElse(null));
                    //set hotel name
                    res.setHotelName(hotelDTOList.stream().filter(h -> res.getHotelId().equals(h.getId())).findFirst().orElse(new HotelDTO()).getName());
                    List<FileAttach> fileAttaches = fileRepository.getFileAttachByObjectIdAndObjectType(res.getId(), ObjectType.BOOKING_HOTEL);
                    res.setBookingInvoice(objectMapper.convertValue(fileAttaches, new TypeReference<>() {
                    }));
                }
            }
            logger.info("Found:{} booking hotel.", responses.size());
            return responses;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        } catch (Exception e) {
            logger.error("Get List booking hotel unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }
}
