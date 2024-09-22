package mm.com.mytelpay.family.business.bookinghotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.AccountRoleResDto;
import mm.com.mytelpay.family.business.bookinghotel.dto.*;
import mm.com.mytelpay.family.business.notification.NoticeService;
import mm.com.mytelpay.family.business.notification.NotificationReqDTO;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccInfoBasicDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.HotelDTO;
import mm.com.mytelpay.family.enums.*;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.BookingHotel;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repo.BookingHotelRepository;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class BookingHotelServiceImpl extends BookingBaseBusiness implements BookingHotelService {

    public BookingHotelServiceImpl() {
        logger = LogManager.getLogger(BookingHotelServiceImpl.class);
    }

    @Autowired
    private BookingHotelRepository bookingHotelRepository;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    @Autowired
    private BookingHotelTransaction bookingHotelTransaction;

    @Autowired
    private NoticeService noticeService;

    @Value("${time.delay.booking}")
    private String timeDelayBookingHotel;

    @Override
    public boolean create(BookingHotelCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String accountId = perRequestContextDto.getCurrentAccountId();
        String bearAuth = perRequestContextDto.getBearToken();
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), bearAuth);

        validateInputCreate(request, accountDTO, bearAuth);

        BookingHotel bookingHotel = mapper.map(request, BookingHotel.class);
        bookingHotel.setFromTime(request.getFrom());
        bookingHotel.setToTime(request.getTo());
        bookingHotel.setAccountId(accountId);
        bookingHotel.setHotelId(request.getHotelId());
        bookingHotel.setBookingStatus(BookingStatus.PENDING);

        BookingHotel hotelSave = bookingHotelTransaction.createBookingHotel(bookingHotel);

        List<AccountDeviceResDTO> accountDeviceResDTO = accountRestTemplate.getAccountDeviceResDTO(bookingHotel.getAccountId(), null, RoleType.ADMIN, request.getRequestId(), bearAuth);
        NotificationReqDTO noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_ADMIN_NOTIFICATION_APPROVE_BOOKING_HOTEL, request.getRequestId(), bearAuth);
        noticeService.sendNotification(request.getRequestId(), noticeDTO == null ? null : noticeDTO.getValue(), NoticeTemplate.SEND_ADMIN_NOTIFICATION_APPROVE_BOOKING_HOTEL_DEFAULT, accountDTO, accountDeviceResDTO, Payload.BOOKING_HOTEL, hotelSave.getId());

        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_BOOKING_HOTEL,null, data);
        return true;
    }

    private void validateInputCreate(BookingHotelCreateReqDTO request, AccountDTO accountDTO, String bearAuth) {
        if (request.getFrom().isAfter(request.getTo())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
        }
        if (request.getFrom().isBefore(LocalDateTime.now())){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_THAN_CURRENT_DATE, null);
        }
        if (Math.abs(Duration.between(request.getFrom(), request.getTo()).toHours()) < Integer.parseInt(timeDelayBookingHotel)){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingHotel.TIME_BOOKING_INVALID, null);
        }
        //check hotel
        HotelDTO hotelDTO = resourceRestTemplate.getHotelInfo(request.getHotelId(), request.getRequestId(), bearAuth);
        List<String> roles = accountDTO.getRoles().stream().map(AccountRoleResDto::getCode).collect(Collectors.toList());
        if (roles.stream().noneMatch(hotelDTO.getRolesAllow()::contains)){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingHotel.NOT_AUTHORIZED_BOOKING_HOTEL, null);
        }
        if (bookingHotelRepository.checkExistsTime(accountDTO.getAccountId(), request.getFrom(), request.getTo(), Integer.valueOf(timeDelayBookingHotel))){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.BOOKING_TIME_IS_EXISTS, null);
        }
    }

    @Override
    public BookingHotelDetailResDTO getDetail(BookingHotelDetailReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BookingHotel bookingHotel = getBookingHotel(request.getBookingId(), request.getRequestId());
        String bearAuth = perRequestContextDto.getBearToken();
        String accountIdLogin = perRequestContextDto.getCurrentAccountId();

//        check role account
        AccountDTO accountLogin = accountRestTemplate.getAccountInfo(accountIdLogin, request.getRequestId(), bearAuth);
        List<String> roleCode = accountLogin.getRoles().stream().map(AccountRoleResDto::getCode).collect(Collectors.toList());
        if (!roleCode.contains(RoleType.ADMIN.toString()) && !accountIdLogin.equals(bookingHotel.getAccountId())){
            throw new BusinessEx(BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
        }

        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(bookingHotel.getAccountId(), request.getRequestId(), bearAuth);
        HotelDTO hotelDTO = resourceRestTemplate.getHotelInfo(bookingHotel.getHotelId(), request.getRequestId(), bearAuth);
        hotelDTO.setId(bookingHotel.getHotelId());
        BookingHotelDetailResDTO resDTO = mapper.map(bookingHotel, BookingHotelDetailResDTO.class);
        resDTO.setCusInfo(accountDTO);
        resDTO.setHotelInfo(hotelDTO);
        List<FileAttach> fileAttaches = fileRepository.getFileAttachByObjectIdAndObjectType(bookingHotel.getId(), ObjectType.BOOKING_HOTEL);
        resDTO.setBookingInvoice(objectMapper.convertValue(fileAttaches, new TypeReference<>() {}));
        return resDTO;
    }

    private BookingHotel getBookingHotel(String bookingId, String requestId) {
        return bookingHotelRepository.findById(bookingId).orElseThrow(() -> {
            throw new BusinessEx(requestId, BookingErrorCode.BookingHotel.BOOKING_HOTEL_NOT_FOUND, null);
        });
    }

    @Override
    public PageImpl<FilterBookingHotelResDTO> filterForEU(FilterBookingHotelReqDTO request, HttpServletRequest httpServletRequest) {
        String bearAuth = perRequestContextDto.getBearToken();
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        return getCommonResponseBookingHotels(request, bearAuth, accountDTO.getAccountId());
    }

    @Override
    public PageImpl<FilterBookingHotelResDTO> filterForAdmin(FilterBookingHotelReqDTO request, HttpServletRequest httpServletRequest) {
        String bearAuth = perRequestContextDto.getBearToken();
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        if (Objects.nonNull(accountDTO)) {
            List<String> roles = accountDTO.getRoles().stream().map(AccountRoleResDto::getCode).collect(Collectors.toList());
            if (!roles.contains(RoleType.ADMIN.toString())) {
                throw new BusinessEx(BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
            }
        }
        return getCommonResponseBookingHotels(request, bearAuth, null);
    }

    @Override
    public boolean cancel(CancelBookingHotelReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String accountId = perRequestContextDto.getCurrentAccountId();
        String bearAuth = perRequestContextDto.getBearToken();
        accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), bearAuth);
        BookingHotel bookingHotel = getBookingHotel(request.getBookingId(), request.getRequestId());
        if (!accountId.equals(bookingHotel.getAccountId()) || !bookingHotel.getBookingStatus().equals(BookingStatus.PENDING)){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
        }

        bookingHotel.setReason(request.getReason());
        bookingHotel.setBookingStatus(BookingStatus.CANCEL);
        bookingHotelTransaction.updateBookingHotel(bookingHotel, null, accountId, BookingHistoryStatus.CANCEL);
        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), null, ActionType.CANCEL_BOOKING_HOTEL,null, data);
        return true;
    }

    @Override
    public boolean approveOrReject(ApproveRejectBookingHotelReqDTO request, MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        validateInputApproveReject(request, file);

        String accountId = perRequestContextDto.getCurrentAccountId();
        String bearAuth = perRequestContextDto.getBearToken();
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), bearAuth);
        BookingHotel bookingHotel = getBookingHotel(request.getBookingId(), request.getRequestId());
        List<String> roles = accountDTO.getRoles().stream().map(AccountRoleResDto::getCode).collect(Collectors.toList());
        if (!roles.contains(RoleType.ADMIN.toString()) || !bookingHotel.getBookingStatus().equals(BookingStatus.PENDING)){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
        }
        String data = objectMapper.writeValueAsString(request);

        String keyNotice = null;
        String noticeDefault = null;
        if (Objects.equals(request.getType(), UpdateBookingType.APPROVE.toString())){
            checkFeeHotel(request, bearAuth, bookingHotel);
            bookingHotel.setBookingStatus(BookingStatus.APPROVED);
            bookingHotel.setFeeBooking(request.getFeeBooking());
            bookingHotel.setFeeService(request.getFeeService());
            updateFile(file, bookingHotel.getId(), ObjectType.BOOKING_HOTEL);
            bookingHotelTransaction.updateBookingHotel(bookingHotel, null, accountId, BookingHistoryStatus.ACCEPTED);
            keyNotice = NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_HOTEL;
            noticeDefault = NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_HOTEL_DEFAULT;
            insertActionLog(request.getRequestId(), null, ActionType.APPROVED_BOOKING_HOTEL,null, data);
        } else if (Objects.equals(request.getType(), UpdateBookingType.REJECT.toString())){
            bookingHotel.setReason(request.getReason());
            bookingHotel.setBookingStatus(BookingStatus.CANCEL);
            bookingHotelTransaction.updateBookingHotel(bookingHotel, null, accountId, BookingHistoryStatus.REJECTED);
            keyNotice = NoticeTemplate.EU_SEND_NOTIFICATION_REJECT_BOOKING_HOTEL;
            noticeDefault = NoticeTemplate.EU_SEND_NOTIFICATION_REJECT_BOOKING_HOTEL_DEFAULT;
            insertActionLog(request.getRequestId(), null, ActionType.REJECT_BOOKING_HOTEL,null, data);
        }
        if (!bookingHotel.getAccountId().equals(accountId)){
            NotificationReqDTO noticeDTO = applicationSettingCommonService.getNoticeByKey(keyNotice, request.getRequestId(), bearAuth);
            List<AccountDeviceResDTO> accountDeviceResDTO = accountRestTemplate.getAccountDeviceResDTO(bookingHotel.getAccountId(), null, null, request.getRequestId(), bearAuth);
            noticeService.sendNotification(request.getRequestId(), noticeDTO == null ? null : noticeDTO.getValue(), noticeDefault, accountDTO, accountDeviceResDTO, Payload.BOOKING_HOTEL, request.getBookingId());
        }
        return true;
    }

    private void checkFeeHotel(ApproveRejectBookingHotelReqDTO request, String bearAuth, BookingHotel bookingHotel) {
        HotelDTO hotelDTO = resourceRestTemplate.getHotelInfo(bookingHotel.getHotelId(), request.getRequestId(), bearAuth);
        if (request.getFeeBooking() > hotelDTO.getMaxPrice()){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingHotel.FEE_BOOKING_INVALID, null);
        }
        if (request.getFeeService() > hotelDTO.getMaxPlusPrice()){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingHotel.FEE_SERVICE_INVALID, null);
        }
    }

    @Override
    public boolean checkHotelInBooking(SimpleRequest request, HttpServletRequest httpServletRequest) {
        List<BookingHotel> bookingHotels = bookingHotelRepository.getListByHotelId(request.getId());
        return ObjectUtils.isNotEmpty(bookingHotels);
    }

    private void validateInputApproveReject(ApproveRejectBookingHotelReqDTO request, MultipartFile file) {
        if (Objects.equals(request.getType(), UpdateBookingType.APPROVE.toString())){
            if (ObjectUtils.isEmpty(request.getFeeBooking())){
                throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.feeBooking");
            }
            if (ObjectUtils.isEmpty(request.getFeeService())){
                throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.feeService");
            }
            if (ObjectUtils.isEmpty(file) || Objects.requireNonNull(file.getOriginalFilename()).isEmpty()){
                throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.file");
            }
        }else if (Objects.equals(UpdateBookingType.REJECT.toString(), request.getType()) && StringUtils.isEmpty(request.getReason())){
            throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.reason");
        }
    }

    @NotNull
    private PageImpl<FilterBookingHotelResDTO> getCommonResponseBookingHotels(FilterBookingHotelReqDTO request, String bearAuth, String accountId) {
        try {
            Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
            LocalDate fromTime = StringUtils.isBlank(request.getFrom()) ? LocalDate.now() : Util.convertToLocalDate(request.getFrom());
            LocalDate toTime = StringUtils.isBlank(request.getTo()) ? LocalDate.now() : Util.convertToLocalDate(request.getTo());

            Page<FilterBookingHotelResDTO> responses = bookingHotelRepository.filter(
                    StringUtils.isBlank(request.getStatus()) ? null : BookingStatus.valueOf(request.getStatus()),
                    fromTime,
                    toTime,
                    accountId,
                    pageable);
            List<FilterBookingHotelResDTO> lstBooking = responses.getContent();

            if (!lstBooking.isEmpty()) {
                setInfoAccountAndHotel(request, bearAuth, accountId, responses);
            }

            logger.info("Found:{} booking hotel.", responses.getTotalElements());
            return new PageImpl<>(responses.getContent(), pageable, responses.getTotalElements());
        } catch (Exception e) {
            logger.error("Get List booking hotel unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    private void setInfoAccountAndHotel(FilterBookingHotelReqDTO request, String bearAuth, String accountId, Page<FilterBookingHotelResDTO> responses) {
        List<String> hotelIds = responses.getContent().stream().map(FilterBookingHotelResDTO::getHotelInfo)
                .map(HotelDTO::getId).distinct().collect(Collectors.toList());
        List<HotelDTO> hotelDTOList = resourceRestTemplate.getListHotelByIds(hotelIds, request.getRequestId(), bearAuth);
        Map<String, HotelDTO> hotelDTOMap = new HashMap<>();
        for (HotelDTO hotel : hotelDTOList) {
            if (!hotelDTOMap.containsKey(hotel.getId()))
                hotelDTOMap.put(hotel.getId(), hotel);
        }

        Map<String, AccInfoBasicDTO> accountDTOMap = new HashMap<>();
        if (StringUtils.isEmpty(accountId)){
            List<String> accountIds = responses.getContent().stream().map(FilterBookingHotelResDTO::getAccountInfo).map(AccInfoBasicDTO::getAccountId).distinct().collect(Collectors.toList());
            List<AccInfoBasicDTO> accountDTOList = accountRestTemplate.getListAccountsInfo(accountIds, request.getRequestId(), bearAuth);
            for (AccInfoBasicDTO accountDTO : accountDTOList) {
                if (!accountDTOMap.containsKey(accountDTO.getAccountId()))
                    accountDTOMap.put(accountDTO.getAccountId(), accountDTO);
            }
        }
        for (FilterBookingHotelResDTO res : responses) {
            if (Objects.nonNull(hotelDTOMap.get(res.getHotelInfo().getId()))) {
                res.setHotelInfo(hotelDTOMap.get(res.getHotelInfo().getId()));
            }
            if (Objects.nonNull(accountDTOMap.get(res.getAccountInfo().getAccountId()))) {
                res.setAccountInfo(accountDTOMap.get(res.getAccountInfo().getAccountId()));
            }else {
                res.setAccountInfo(null);
            }
        }
    }
}
