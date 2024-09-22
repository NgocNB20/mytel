package mm.com.mytelpay.family.business.bookingcar;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.notification.NoticeService;
import mm.com.mytelpay.family.business.notification.NotificationReqDTO;
import mm.com.mytelpay.family.business.resttemplate.MapboxRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccInfoBasicDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.*;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.BookingCar;
import mm.com.mytelpay.family.model.BookingCarDetail;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.model.RoleApprove;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repo.BookingCarDetailRepository;
import mm.com.mytelpay.family.repo.BookingCarRepository;
import mm.com.mytelpay.family.repo.RoleApproveRepository;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class BookingCarServiceImpl extends BookingBaseBusiness implements BookingCarService {

    @Autowired
    private BookingCarRepository bookingCarRepository;

    @Autowired
    private BookingCarDetailRepository bookingCarDetailRepository;

    @Autowired
    private RoleApproveRepository roleApproveRepository;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    @Value("${time.delay.booking}")
    private String timeDelayBookingCar;

    @Autowired
    private BookingCarTransaction bookingCarTransaction;

    @Autowired
    private MapboxRestTemplate mapboxRestTemplate;

    @Override
    public PageImpl<FilterBookingCarResDTO> getList(FilterBookingCarReqDTO request, HttpServletRequest httpServletRequest) {
        accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());

        CarBookingType typeBookingSearch = StringUtils.isBlank(request.getCarTypeBooking()) ? null : CarBookingType.valueOf(request.getCarTypeBooking());
        BookingStatus bookingStatusSearch = StringUtils.isBlank(request.getStatus()) ? null : BookingStatus.valueOf(request.getStatus());

        LocalDateTime fromTime = StringUtils.isBlank(request.getFrom()) ? LocalDateTime.now().with(LocalTime.MIN) : Util.convertToLocalDateTime(request.getFrom());
        LocalDateTime toTime = StringUtils.isBlank(request.getTo()) ? LocalDateTime.now() : Util.convertToLocalDateTime(request.getTo());

        Page<FilterBookingCarResDTO> responses = bookingCarRepository.filterBookingCar(
                typeBookingSearch,
                bookingStatusSearch,
                perRequestContextDto.getCurrentAccountId(),
                fromTime,
                toTime,
                pageable);

        List<String> fileIds = responses.getContent().stream().map(FilterBookingCarResDTO::getId).collect(Collectors.toList());
        Map<String, FileResponse> listMap = getStringFileResponseMap(fileIds);
        responses.getContent().forEach(r -> r.setFile(listMap.get(r.getId())));

        logger.info("Found:{} booking car", responses.getTotalElements());
        return new PageImpl<>(responses.getContent(), pageable, responses.getTotalElements());
    }

    @NotNull
    private Map<String, FileResponse> getStringFileResponseMap(List<String> fileIds) {
        List<FileAttach> fileAttaches = fileRepository.getFileAttachByObjectIdInAndObjectType(fileIds, ObjectType.BOOKING_CAR);
        Map<String, FileResponse> listMap = new HashMap<>();
        for (FileAttach f:fileAttaches) {
            if(!listMap.containsKey(f.getObjectId())){
                listMap.put(f.getObjectId(), new FileResponse());
            }
            listMap.put(f.getObjectId(), mapper.map(f, FileResponse.class));
        }
        return listMap;
    }

    @Override
    public boolean bookingCar(BookingCarReqDTO request, MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        String bearerAuth = perRequestContextDto.getBearToken();
        String actionLogData = objectMapper.writeValueAsString(request);
        String accountId = perRequestContextDto.getCurrentAccountId();

        AccountDTO account = accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), bearerAuth);
        if (!Status.ACTIVE.equals(account.getStatus())){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_INACTIVE, null);
        }
        List<String> roleIds = account.getRoles().stream().map(AccountRoleResDto::getRoleId).collect(Collectors.toList());

        //Check EU has role isAssign
        RoleApprove roleIsAssigns = roleApproveRepository.findFirstByBookingTypeAndIsAssign(BookingType.BOOKING_CAR, true);

        validateBookingCar(request, accountId);

        BookingCar bookingCar = mapper.map(request, BookingCar.class);
        bookingCar.setUnitId(account.getUnitId());
        bookingCar.setAccountId(accountId);

        List<RoleApprove> roleApproveList = roleApproveRepository.findAllByBookingTypeOrderByLevelDesc(BookingType.BOOKING_CAR);

        RoleApprove getRoleReceiveNotice;
        NotificationReqDTO noticeDTO;
        String noticeDefault;
        //check EU has role approve
        RoleApprove roleApproveEU = roleApproveList.stream()
                .filter(r -> roleIds.contains(r.getRoleId()))
                .findFirst().orElse(null);
        if (roleApproveEU == null) {
            bookingCar.setBookingStatus(BookingStatus.PENDING);
            bookingCar.setApproveLevel(0);
            getRoleReceiveNotice = roleApproveRepository.findFirstByBookingTypeOrderByLevelAsc(BookingType.BOOKING_CAR);
            noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR, request.getRequestId(), bearerAuth);
            noticeDefault = NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR_DEFAULT;
        } else {
            bookingCar.setBookingStatus(BookingStatus.APPROVING);
            if (roleApproveEU.getLevel() > roleIsAssigns.getLevel()) {
                bookingCar.setApproveLevel(roleIsAssigns.getLevel() - 1);
            } else {
                bookingCar.setApproveLevel(roleApproveEU.getLevel());
            }
            getRoleReceiveNotice = roleIsAssigns;
            noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_NOTIFICATION_TO_DRIVER_MANAGEMENT, request.getRequestId(), bearerAuth);
            noticeDefault = NoticeTemplate.SEND_NOTIFICATION_TO_DRIVER_MANAGEMENT_DEFAULT;
        }
        List<AccountDeviceResDTO> accountDeviceResDTO = accountRestTemplate.getAccountDeviceResDTO(account.getAccountId(), getRoleReceiveNotice.getRoleId(), null, request.getRequestId(), bearerAuth);
        BookingCar bookingCarSave = bookingCarTransaction.createBookingCarAndDetail(bookingCar, file);
        insertActionLog(request.getRequestId(), account.getAccountId(), ActionType.CREATE_BOOKING_CAR, account.getMsisdn(), actionLogData);
        noticeService.sendNotification(request.getRequestId(), noticeDTO == null ? null : noticeDTO.getValue(), noticeDefault, account, accountDeviceResDTO, Payload.BOOKING_CAR, bookingCarSave.getId());
        return true;
    }

    private void validateBookingCar(BookingCarReqDTO request, String accountId) {
        // Validate time request
        if (CarBookingType.TWO_WAY.toString().equals(request.getTypeBooking()) && Objects.isNull(request.getTimeReturn())) {
            throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.timeReturn");
        }
        if (request.getTimeStart().isBefore(LocalDateTime.now().plusHours(Long.parseLong(timeDelayBookingCar)))) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.TIME_START_INVALID, null);
        } else if (CarBookingType.TWO_WAY.toString().equals(request.getTypeBooking()) && Objects.nonNull(request.getTimeReturn()) && (request.getTimeStart().isEqual(request.getTimeReturn())
                || request.getTimeStart().isAfter(request.getTimeReturn()))) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.TIME_RETURN_INVALID, null);
        }

        // Validate original and destination request
        if (request.getDestination().trim().equalsIgnoreCase(request.getOriginal().trim())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.ORIGINAL_DIFFERENT_DESTINATION, null);
        }

        boolean timeExists;
        if (CarBookingType.TWO_WAY.toString().equals(request.getTypeBooking())) {
            timeExists = bookingCarRepository.checkExistsTimeWithTwoWay(accountId, request.getTimeStart(), request.getTimeReturn(), Integer.valueOf(timeDelayBookingCar));
        } else {
            timeExists = bookingCarRepository.checkExistsTimeWithOneWayOrFull(accountId, request.getTimeStart(), Integer.valueOf(timeDelayBookingCar));
        }
        if (timeExists) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.BOOKING_TIME_IS_EXISTS, null);
        }
    }

    public boolean approve(ApproveBookingCarReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        String bearerAuth = perRequestContextDto.getBearToken();

        BookingCar bookingCar = bookingCarRepository.findByBookingId(request.getBookingId()).orElseThrow(() -> {
            logger.error("Booking car request found");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_NOT_FOUND, null);
        });

        RoleApprove roleApprove = roleApproveRepository.findFirstByBookingTypeAndAndRoleId(BookingType.BOOKING_CAR, request.getRoleId());
        if (ObjectUtils.isEmpty(roleApprove)) {
            logger.error("Role approve booking car not found");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.ROLE_APPROVE_NOT_FOUND, null);
        }

        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), bearerAuth);
        if (!Status.ACTIVE.equals(accountDTO.getStatus())){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_INACTIVE, null);
        }
        List<String> roleIds = accountDTO.getRoles().stream().map(AccountRoleResDto::getRoleId).collect(Collectors.toList());
        if (!roleIds.contains(roleApprove.getRoleId())) {
            logger.error("Account has no permissions");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
        }

        List<RoleApprove> roleApproveList = roleApproveRepository.findAllByBookingTypeOrderByLevelAsc(BookingType.BOOKING_CAR);

        //check role level has been approved
        if (roleApproveList.indexOf(roleApprove) != bookingCar.getApproveLevel()) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.INVALID_ROLE_APPROVE, null);
        }

        // Final approve
        if ((roleApproveList.get(roleApproveList.size() - 1).getLevel() == bookingCar.getApproveLevel() + 1)) {
            bookingCar.setBookingStatus(BookingStatus.APPROVED);
            bookingCar.setApproveLevel(roleApproveList.size());
        } else {
            bookingCar.setBookingStatus(BookingStatus.APPROVING);
            bookingCar.setApproveLevel(bookingCar.getApproveLevel() + 1);
        }

        AccountDTO accountInfoEU = accountRestTemplate.getAccountInfo(bookingCar.getAccountId(), request.getRequestId(), bearerAuth);
        if (!Status.ACTIVE.equals(accountInfoEU.getStatus())){
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_INACTIVE, null);
        }
        // Assign driver and car
        if (Boolean.TRUE.equals(roleApprove.getIsAssign())) {
            checkRoleEU(bookingCar, roleApprove, accountInfoEU, roleApproveList);
            assignDriverAndCar(request, bookingCar);
        }
        bookingCar.setLastUpdatedAt(LocalDateTime.now());
        String actionLogData = objectMapper.writeValueAsString(request);
        CarBookingDetailStatus carBookingDetailStatus = CarBookingDetailStatus.valueOf(bookingCar.getBookingStatus().toString());
        bookingCarRepository.save(bookingCar);
        bookingCarDetailRepository.updateStatusBookingDetail(bookingCar.getId(), carBookingDetailStatus);
        bookingCarTransaction.saveBookingCarHistory(bookingCar, request.getRoleId(), accountDTO.getAccountId(), BookingHistoryStatus.ACCEPTED);
        insertActionLog(request.getRequestId(), accountDTO.getAccountId(), ActionType.APPROVED_BOOKING_CAR, accountDTO.getMsisdn(), actionLogData);

        //send notice
        sendNoticeApprove(request, bearerAuth, bookingCar, roleApproveList, accountInfoEU);
        return true;
    }

    private void checkRoleEU(BookingCar bookingCar, RoleApprove roleApprove, AccountDTO accountDTO, List<RoleApprove> roleApproveList) {
        List<String> roleIdsEU = accountDTO.getRoles().stream().map(AccountRoleResDto::getRoleId).collect(Collectors.toList());
        Collections.reverse(roleApproveList);
        //get the Role Approve with the highest level the user has ( if it has )
        RoleApprove roleApproveEU = roleApproveList.stream()
                .filter(r -> roleIdsEU.contains(r.getRoleId()))
                .findFirst().orElse(null);

        //check if EU is the role approve with the biggest level
        if (roleApproveEU != null && Objects.equals(roleApproveEU.getLevel(), roleApproveList.get(0).getLevel())) {
            bookingCar.setBookingStatus(BookingStatus.APPROVED);
            bookingCar.setApproveLevel(roleApproveList.size());
        }
        //check if EU has a bigger role approve than assigner
        else if (roleApproveEU != null && roleApproveEU.getLevel() > roleApprove.getLevel()) {
            bookingCar.setBookingStatus(BookingStatus.APPROVING);
            bookingCar.setApproveLevel(roleApproveEU.getLevel());
        }
    }

    private void sendNoticeApprove(ApproveBookingCarReqDTO request, String bearerAuth, BookingCar bookingCar, List<RoleApprove> roleApproveList, AccountDTO accountBookingDTO) {
        if (bookingCar.getBookingStatus().equals(BookingStatus.APPROVED)) {
            //send notice to user reporting success
            List<AccountDeviceResDTO> accountDeviceResDTO = accountRestTemplate.getAccountDeviceResDTO(bookingCar.getAccountId(), null, null, request.getRequestId(), bearerAuth);
            NotificationReqDTO noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR_SUCCESS, request.getRequestId(), bearerAuth);
            noticeService.sendNotification(request.getRequestId(), noticeDTO == null ? null : noticeDTO.getValue(), NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_SUCCESS_BOOKING_CAR_DEFAULT
                    , accountBookingDTO, accountDeviceResDTO, Payload.BOOKING_CAR, request.getBookingId());

            //send notice to driver
            List<AccountDeviceResDTO> listDrivers = new ArrayList<>();
            List<BookingCarDetail> bookingCarDetails = bookingCarDetailRepository.findByBookingCarId(bookingCar.getId());
            if (Objects.nonNull(bookingCarDetails)) {
                bookingCarDetails.forEach(b ->
                    listDrivers.addAll(accountRestTemplate.getAccountDeviceResDTO(b.getDriverId(), null, null, request.getRequestId(), bearerAuth)));
            }
            NotificationReqDTO noticeToDriver = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_NOTIFICATION_TO_DRIVER, request.getRequestId(), bearerAuth);
            noticeService.sendNotification(request.getRequestId(), noticeToDriver == null ? null : noticeToDriver.getValue(), NoticeTemplate.SEND_NOTIFICATION_TO_DRIVER_DEFAULT,
                    accountBookingDTO, listDrivers, Payload.BOOKING_CAR, request.getBookingId());

        } else {
            //send notice to account has role approve the next
            Collections.reverse(roleApproveList);
            RoleApprove roleApproveNext = roleApproveList.get(bookingCar.getApproveLevel());
            NotificationReqDTO noticeDTO;
            String noticeDefault;
            if (Boolean.TRUE.equals(roleApproveNext.getIsAssign())) {
                noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.SEND_NOTIFICATION_TO_DRIVER_MANAGEMENT, request.getRequestId(), bearerAuth);
                noticeDefault = NoticeTemplate.SEND_NOTIFICATION_TO_DRIVER_MANAGEMENT_DEFAULT;
            } else {
                noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR, request.getRequestId(), bearerAuth);
                noticeDefault = NoticeTemplate.EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR_DEFAULT;
            }
            List<AccountDeviceResDTO> accountDeviceResDTO = accountRestTemplate.getAccountDeviceResDTO(bookingCar.getAccountId(), roleApproveList.get(bookingCar.getApproveLevel()).getRoleId(), null, request.getRequestId(), bearerAuth);
            noticeService.sendNotification(request.getRequestId(), noticeDTO == null ? null : noticeDTO.getValue(), noticeDefault, accountBookingDTO, accountDeviceResDTO, Payload.BOOKING_CAR, request.getBookingId());
        }
    }

    private void assignDriverAndCar(ApproveBookingCarReqDTO request, BookingCar bookingCar) {
        //validate driverId and carId
        if (StringUtils.isBlank(request.getDriverOutboundId())) {
            throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.driverOutboundId");
        }
        if (StringUtils.isBlank(request.getCarOutboundId())) {
            throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.carOutboundId");
        }

        if (bookingCar.getTypeBooking().equals(CarBookingType.TWO_WAY)) {
            if (StringUtils.isBlank(request.getDriverReturnId())) {
                throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.driverReturnId");
            }
            if (StringUtils.isBlank(request.getCarReturnId())) {
                throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.carReturnId");
            }
        }

        //assign driver and car
        List<BookingCarDetail> bookingCarDetails = bookingCarDetailRepository.findByBookingCarId(bookingCar.getId());
        bookingCarDetails.forEach(b -> {
            if (b.getType().equals(DirectionType.RETURN)) {
                b.setDriverId(request.getDriverReturnId());
                b.setCarId(request.getCarReturnId());
            } else {
                b.setDriverId(request.getDriverOutboundId());
                b.setCarId(request.getCarOutboundId());
            }
        });
        bookingCarDetailRepository.saveAll(bookingCarDetails);
    }

    @Override
    public List<BookingCarResDTO> getDetailBookingCar(SimpleRequest request, HttpServletRequest httpServletRequest) {
        AccountDTO account = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());

        BookingCar bookingCar = bookingCarRepository.findByBookingId(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_NOT_FOUND, null);
        });
        List<BookingCarResDTO> response = bookingCarRepository.getDetail(bookingCar.getId());
        if (!response.isEmpty()) {
            for (BookingCarResDTO res : response) {
                addInfoBooking(res, request);
            }
        } else {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_DETAIL_NOT_FOUND, null);
        }
        if (!checkIsValidRole(RoleType.DRIVER, account)) {
            List<String> fileIds = response.stream().map(BookingCarResDTO::getBookingId).collect(Collectors.toList());
            Map<String, FileResponse> listMap = getStringFileResponseMap(fileIds);
            response.forEach(r -> r.setFile(listMap.get(r.getBookingId())));
        }

        return response;
    }

    private void addInfoBooking(BookingCarResDTO res, SimpleRequest request) {
        if (StringUtils.isNotBlank(res.getAccountId())) {
            AccountDTO userInfo = accountRestTemplate.getAccountInfoOrNull(res.getAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
            if (Objects.nonNull(userInfo)) res.setUserInfo(mapper.map(userInfo, AccInfoBasicDTO.class));
        }
        if (StringUtils.isNotBlank(res.getDriverId())) {
            AccountDTO driverInfo = accountRestTemplate.getAccountInfoOrNull(res.getDriverId(), request.getRequestId(), perRequestContextDto.getBearToken());
            if(Objects.nonNull(driverInfo)) res.setDriverInfo(mapper.map(driverInfo, AccInfoBasicDTO.class));
        }
        if (StringUtils.isNotBlank(res.getCarId())) {
            res.setCarInfo(resourceRestTemplate.getCarInfo(res.getCarId(), request.getRequestId(), perRequestContextDto.getBearToken()));
        }
    }

    @Override
    public boolean reject(RejectBookingCarReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BookingCar bookingCar = getBookingCarById(request.getBookingId(), request.getRequestId());
        // Validate data
        if (bookingCar.getBookingStatus().equals(BookingStatus.APPROVED)
                || bookingCar.getBookingStatus().equals(BookingStatus.CANCEL)
                || bookingCar.getBookingStatus().equals(BookingStatus.DONE)) {
            logger.error("Invalidate reject booking car");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.INVALID_REJECT_BOOKING, null);
        }
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        // check roleId
        RoleApprove roleApprove = roleApproveRepository.findFirstByBookingTypeAndAndRoleId(BookingType.BOOKING_CAR, request.getRoleId());
        if (ObjectUtils.isEmpty(roleApprove)) {
            logger.error("Role reject booking car not found");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.ROLE_REJECT_NOT_FOUND, null);
        }
        List<String> roleIds = accountDTO.getRoles().stream().map(AccountRoleResDto::getRoleId).collect(Collectors.toList());
        if (!roleIds.contains(roleApprove.getRoleId())) {
            logger.error("Invalidate Role reject");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.INVALID_ROLE_APPROVE, null);
        }
        if ((bookingCar.getApproveLevel() + 1) != (roleApprove.getLevel())) { // check roleId does have permission reject ?
            logger.error("Invalidate Role level reject");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.INVALID_ROLE_REJECT, null);
        }

        bookingCar.setReason(request.getReason());
        bookingCarTransaction.updateDataReject(bookingCar, request.getRoleId(), perRequestContextDto.getCurrentAccountId());

        // Send notification
        //1. Send notify to user
        NotificationReqDTO noticeDTO = applicationSettingCommonService.getNoticeByKey(NoticeTemplate.EU_SEND_NOTIFICATION_REJECT_BOOKING_CAR, request.getRequestId(), perRequestContextDto.getBearToken());
        List<AccountDeviceResDTO> accountDeviceResDTO = accountRestTemplate.getAccountDeviceResDTO(bookingCar.getAccountId(), null, null, request.getRequestId(), perRequestContextDto.getBearToken());
        String actionLogData = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.REJECT_BOOKING_CAR, accountDTO.getMsisdn(), actionLogData);
        noticeService.sendNotification(request.getRequestId(), noticeDTO == null ? null : noticeDTO.getValue(), NoticeTemplate.EU_SEND_NOTIFICATION_REJECT_BOOKING_CAR_DEFAULT
                , accountDTO, accountDeviceResDTO, Payload.BOOKING_CAR, request.getBookingId());
        return true;
    }

    @Override
    public boolean update(UpdateBookingCarReqDTO request, HttpServletRequest httpServletRequest) {
        BookingCarDetail bookingDetail = bookingCarDetailRepository.findById(request.getBookingDetailId()).orElseThrow(
                () -> new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_DETAIL_NOT_FOUND, null)
        );

        BookingCar bookingCar = bookingCarRepository.findByBookingId(bookingDetail.getBookingCarId()).orElseThrow(
                () -> new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_NOT_FOUND, null)
        );

        validateBeforeUpdateBookingCar(request, bookingDetail, bookingCar);

        bookingDetail.setStatus(request.getStatus());

        if (bookingDetail.getStatus().equals(CarBookingDetailStatus.STARTED)) {
            if (bookingCar.getBookingStatus().equals(BookingStatus.APPROVED)) {
                bookingCar.setBookingStatus(BookingStatus.STARTED);
            }
        } else if (bookingDetail.getStatus().equals(CarBookingDetailStatus.DONE)) {
            List<BookingCarDetail> bookingCarDetails = bookingCarDetailRepository.findByBookingCarId(bookingCar.getId());
            if (CollectionUtils.isEmpty(bookingCarDetails)) {
                logger.error("List booking car detail empty");
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_DETAIL_NOT_FOUND, null);
            }

            boolean isDone = true;
            for (BookingCarDetail d : bookingCarDetails) {
                if (!d.getStatus().equals(CarBookingDetailStatus.DONE)) {
                    isDone = false;
                    break;
                }
            }

            if (isDone) {
                bookingCar.setBookingStatus(BookingStatus.DONE);
            }
        }
        bookingCarTransaction.saveBookingCarAndDetail(bookingCar, Collections.singletonList(bookingDetail));
        return true;
    }

    private void validateBeforeUpdateBookingCar(UpdateBookingCarReqDTO request, BookingCarDetail bookingDetail, BookingCar bookingCar) {
        if (!StringUtils.equals(perRequestContextDto.getCurrentAccountId(), bookingDetail.getDriverId())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.DONT_HAVE_PERMISSION, null);
        }

        if (request.getStatus().equals(CarBookingDetailStatus.STARTED) && !LocalDate.now().isEqual(bookingDetail.getTimeStart().toLocalDate())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.CANNOT_START_THE_TRIP_TODAY, null);
        }

        if (request.getStatus().equals(CarBookingDetailStatus.DONE) && LocalDate.now().isBefore(bookingDetail.getTimeStart().toLocalDate())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.CANNOT_COMPLETE_THE_TRIP_TODAY, null);
        }

        if (!bookingCar.getBookingStatus().equals(BookingStatus.APPROVED) && !bookingCar.getBookingStatus().equals(BookingStatus.STARTED)) {
            logger.error("Booking car not has Approved or Started");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.HAS_NOT_BEEN_ACCEPTED_YET, null);
        }

        if (bookingDetail.getStatus().equals(CarBookingDetailStatus.DONE)) {
            logger.error("Booking car is Done");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.CANNOT_UPDATE_THE_TRIP_ANYMORE, null);
        }

        if (!bookingDetail.getStatus().equals(CarBookingDetailStatus.STARTED) && request.getStatus().equals(CarBookingDetailStatus.DONE)) {
            logger.error("Booking car not has started");
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.CANNOT_FINISH_THE_TRIP_WHEN_IT_HAS_NOT_STARTED, null);
        }
        validateBookignReturn(request, bookingDetail, bookingCar);
    }

    private void validateBookignReturn(UpdateBookingCarReqDTO request, BookingCarDetail bookingDetail, BookingCar bookingCar) {
        if (DirectionType.RETURN.equals(bookingDetail.getType())) {
            Optional<BookingCarDetail> outboundBookingCarDetailOpt = bookingCarDetailRepository.findByBookingCarId(bookingCar.getId())
                    .stream()
                    .filter(bc -> DirectionType.OUTBOUND.equals(bc.getType()))
                    .findFirst();

            if (outboundBookingCarDetailOpt.isPresent()) {
                BookingCarDetail outboundBookingCarDetail = outboundBookingCarDetailOpt.get();
                CarBookingDetailStatus status = outboundBookingCarDetail.getStatus();
                if (status != null && !CarBookingDetailStatus.DONE.equals(status)) {
                    throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.THE_RETURN_TRIP_CANNOT_BE_UPDATED_UNTIL_THE_OUTBOUND_TRIP_HAS_NOT_BEEN_COMPLETED, null);
                }
            }
        }
    }

    private BookingCar getBookingCarById(String bookingCarId, String requestId) {
        return bookingCarRepository.findByBookingId(bookingCarId).orElseThrow(() -> {
            logger.error("Booking car not found");
            throw new BusinessEx(requestId, BookingErrorCode.BookingCar.BOOKING_CAR_NOT_FOUND, null);

        });
    }

    @Override
    public PageImpl<GetListReqBookingCarResDTO> getListReq(GetListReqBookingCarReqDTO request, HttpServletRequest httpServletRequest) {
        Page<GetListReqBookingCarResDTO> responses = handleDataForGetListReq(request);
        logger.info("Found:{} booking car.", responses.getTotalElements());

        return getGetListReqBookingCarResDTOS(responses);
    }

    @NotNull
    public PageImpl<GetListReqBookingCarResDTO> getGetListReqBookingCarResDTOS(Page<GetListReqBookingCarResDTO> responses) {
        List<String> fileIds = responses.getContent().stream().map(GetListReqBookingCarResDTO::getId).collect(Collectors.toList());
        Map<String, FileResponse> listMap = getStringFileResponseMap(fileIds);
        responses.getContent().forEach(r -> r.setFile(listMap.get(r.getId())));

        return new PageImpl<>(responses.getContent(), responses.getPageable(), responses.getTotalElements());
    }

    @Override
    public PageImpl<GetListReqBookingCarResDTO> getListAssignedForDriver(GetListReqBookingCarReqDTO request, HttpServletRequest httpServletRequest) {
        AccountDTO account = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        if (!checkIsValidRole(RoleType.DRIVER, account)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.DONT_HAVE_PERMISSION, null);
        }

        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        BookingStatus bookingStatusSearch = StringUtils.isBlank(request.getStatus()) ? null : BookingStatus.valueOf(request.getStatus());
        LocalDateTime fromTime = StringUtils.isBlank(request.getFrom()) ? null : Util.convertToLocalDateTime(request.getFrom());
        LocalDateTime toTime = StringUtils.isBlank(request.getTo()) ? null : Util.convertToLocalDateTime(request.getTo());

        Page<GetListReqBookingCarResDTO> responses = getListBookingCarForDriver(bookingStatusSearch, fromTime, toTime, pageable);
        logger.info("Found:{} booking car.", responses.getTotalElements());
        return new PageImpl<>(responses.getContent(), responses.getPageable(), responses.getTotalElements());
    }

    private Page<GetListReqBookingCarResDTO> handleDataForGetListReq(GetListReqBookingCarReqDTO request) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<GetListReqBookingCarResDTO> res;
        BookingStatus bookingStatusSearch = StringUtils.isBlank(request.getStatus()) ? null : BookingStatus.valueOf(request.getStatus());
        LocalDateTime fromTime = StringUtils.isBlank(request.getFrom()) ? null : Util.convertToLocalDateTime(request.getFrom());
        LocalDateTime toTime = StringUtils.isBlank(request.getTo()) ? null : Util.convertToLocalDateTime(request.getTo());
        AccountDTO account = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());

        if (request.isForBooking()) {
            res = getListBookingCarForEndUser(bookingStatusSearch, fromTime, toTime, pageable);
        } else {
            res = getListBookingCarForNotEndUser(bookingStatusSearch, fromTime, toTime, pageable, account, request.getRequestId());
        }
        if (!res.getContent().isEmpty()) {
            List<String> accountIds = res.getContent().stream().map(GetListReqBookingCarResDTO::getAccountInfo).map(AccInfoBasicDTO::getAccountId).distinct().collect(Collectors.toList());
            Map<String, AccInfoBasicDTO> accountDTOMap = new HashMap<>();
            List<AccInfoBasicDTO> accountDTOList = accountRestTemplate.getListAccountsInfo(accountIds, request.getRequestId(), perRequestContextDto.getBearToken());
            for (AccInfoBasicDTO accountDTO : accountDTOList) {
                if (!accountDTOMap.containsKey(accountDTO.getAccountId()))
                    accountDTOMap.put(accountDTO.getAccountId(), accountDTO);
            }
            for (GetListReqBookingCarResDTO dt : res.getContent()) {
                dt.setAccountInfo(accountDTOMap.get(dt.getAccountInfo().getAccountId()));
            }
        }
        return res;
    }

    private Page<GetListReqBookingCarResDTO> getListBookingCarForEndUser(BookingStatus bookingStatusSearch, LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable) {
        return bookingCarRepository.getListBookingCarForEU(
                bookingStatusSearch, fromTime,
                toTime, perRequestContextDto.getCurrentAccountId(),
                pageable);
    }

    private Page<GetListReqBookingCarResDTO> getListBookingCarForDriver(BookingStatus bookingStatusSearch, LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable) {
        return bookingCarRepository.getListAssignedForDriver(
                bookingStatusSearch, fromTime,
                toTime, perRequestContextDto.getCurrentAccountId(),
                pageable);
    }

    private Page<GetListReqBookingCarResDTO> getListBookingCarForNotEndUser(BookingStatus bookingStatusSearch, LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable, AccountDTO account, String requestId) {
        List<RoleApprove> roleApproveList = roleApproveRepository.findAllByBookingTypeOrderByLevelAsc(BookingType.BOOKING_CAR);
        List<String> roleIds = account.getRoles().stream().map(AccountRoleResDto::getRoleId).collect(Collectors.toList());
        Collections.reverse(roleApproveList);
        RoleApprove roleApproveEU = roleApproveList.stream()
                .filter(r -> roleIds.contains(r.getRoleId()))
                .findFirst().orElse(null);
        String unitId = null;
        if (roleApproveEU == null) {
            logger.error("Invalid role get booking car request");
            throw new BusinessEx(requestId, BookingErrorCode.BookingCar.INVALID_ROLE_EU, null);
        }
        if (roleApproveEU.getLevel() == 1) {
            unitId = account.getUnitId();
        }
        return bookingCarRepository.getListBookingCarReq(
                bookingStatusSearch, fromTime,
                toTime, unitId, pageable);
    }

    @Override
    public boolean verifyQrBookingCar(VerifyQrBookingCarReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        AccountDTO account = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        if (!checkIsValidRole(RoleType.DRIVER, account)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.ONLY_DRIVER_CAN_COMPLETE_THE_TRIP, null);
        }
        BookingType bookingType = BookingType.valueOf(request.getType());
        if (bookingType == BookingType.BOOKING_CAR) {
            UpdateBookingCarReqDTO updateBookingCarReqDTO = new UpdateBookingCarReqDTO(request.getBookingDetailId(), CarBookingDetailStatus.STARTED);
            updateBookingCarReqDTO.setRequestId(request.getRequestId());
            updateBookingCarReqDTO.setCurrentTime(request.getCurrentTime());
            String data = objectMapper.writeValueAsString(request);
            insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.VERIFY_QR, null, data);
            return this.update(updateBookingCarReqDTO, httpServletRequest);
        }
        return true;
    }

    @Override
    public boolean completeBookingCar(CompleteBookingReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        AccountDTO account = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        if (!checkIsValidRole(RoleType.DRIVER, account)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.DONT_HAVE_PERMISSION, null);
        }
        UpdateBookingCarReqDTO updateBookingCarReqDTO = new UpdateBookingCarReqDTO(request.getBookingDetailId(), CarBookingDetailStatus.DONE);
        updateBookingCarReqDTO.setRequestId(request.getRequestId());
        updateBookingCarReqDTO.setCurrentTime(request.getCurrentTime());
        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.COMPLETE_BOOKING_CAR,null, data);
        return this.update(updateBookingCarReqDTO, httpServletRequest);
    }

    @Override
    public boolean cancel(BookingCarCancel bookingCarCancel, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String bearerAuth = perRequestContextDto.getBearToken();
        String accountId = perRequestContextDto.getCurrentAccountId();
        AccountDTO account = accountRestTemplate.getAccountInfo(accountId, bookingCarCancel.getRequestId(), bearerAuth);
        List<String> roleIds = account.getRoles().stream().map(AccountRoleResDto::getRoleId).collect(Collectors.toList());
        if (!account.getStatus().equals(Status.ACTIVE)) {
            throw new BusinessEx(bookingCarCancel.getRequestId(), BookingErrorCode.Account.WRONG_INFO, null);
        }
        BookingCar bookingCar = bookingCarRepository.findByIdAndAccountId(bookingCarCancel.getBookingId(), account.getAccountId()).orElseThrow(() -> {
            throw new BusinessEx(bookingCarCancel.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
        });

        List<RoleApprove> roleApproveList = roleApproveRepository.findAllByBookingTypeOrderByLevelAsc(BookingType.BOOKING_CAR);
        RoleApprove roleApproveEU = roleApproveList.stream()
                .filter(r -> roleIds.contains(r.getRoleId()))
                .findFirst().orElse(null);
        if (roleApproveEU == null && !bookingCar.getBookingStatus().equals(BookingStatus.PENDING)) {
            throw new BusinessEx(bookingCarCancel.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
        } else if (roleApproveEU != null) {
            RoleApprove roleIsAssigns = roleApproveRepository.findFirstByBookingTypeAndIsAssign(BookingType.BOOKING_CAR, true);
            if (roleApproveEU.getLevel() > roleIsAssigns.getLevel() && bookingCar.getApproveLevel() > roleIsAssigns.getLevel()) {
                throw new BusinessEx(bookingCarCancel.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
            }
            if (roleApproveEU.getLevel() < bookingCar.getApproveLevel()) {
                throw new BusinessEx(bookingCarCancel.getRequestId(), BookingErrorCode.Account.ACCOUNT_HAS_NO_PERMISSIONS, null);
            }
        }
        String actionLogData = objectMapper.writeValueAsString(bookingCar);
        bookingCar.setBookingStatus(BookingStatus.CANCEL);
        bookingCar.setReason(bookingCarCancel.getReason());
        bookingCar.setApproveLevel(roleApproveList.size() + 1);
        List<BookingCarDetail> bookingCarDetails = bookingCarDetailRepository.findByBookingCarId(bookingCarCancel.getBookingId());
        for (BookingCarDetail bookingCarDetail : bookingCarDetails) {
            bookingCarDetail.setStatus(CarBookingDetailStatus.CANCEL);
        }
        bookingCarTransaction.saveBookingCarAndDetail(bookingCar, bookingCarDetails);
        insertActionLog(bookingCarCancel.getRequestId(), account.getAccountId(), ActionType.CANCEL_BOOKING_CAR, account.getMsisdn(), actionLogData);
        return true;
    }

    @Override
    public BookingAssignDriverDTO dataAssignDriver(AssignReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        BookingAssignDriverDTO response = new BookingAssignDriverDTO();
        List<BookingCarDetail> bookingDetailsOutbound;
        List<BookingCarDetail> bookingDetailsReturn;
        if (StringUtils.isNotBlank(reqDTO.getTimeStart())) {
            bookingDetailsOutbound = getBookingCarDetails(reqDTO.getTimeStart(), reqDTO.getTimeReturn());
            // list assigned
            Set<String> driverIdsOutbound = bookingDetailsOutbound.stream()
                    .map(BookingCarDetail::getDriverId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            driverIdsOutbound.addAll(bookingCarDetailRepository.listAccountIdByBookingIds(bookingDetailsOutbound.stream()
                    .map(BookingCarDetail::getBookingCarId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList())));
            // get list drivers without driverIds
            response.setLstDriverOutbound(accountRestTemplate.getListDriverAssign(driverIdsOutbound, reqDTO.getRequestId(), perRequestContextDto.getBearToken()));
        }
        if (StringUtils.isNotBlank(reqDTO.getTimeReturn())) {
            bookingDetailsReturn = getBookingCarDetails(reqDTO.getTimeReturn(), reqDTO.getTimeReturn());
            // list assigned
            Set<String> driverIdsReturn = bookingDetailsReturn.stream()
                    .map(BookingCarDetail::getDriverId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            driverIdsReturn.addAll(bookingCarDetailRepository.listAccountIdByBookingIds(bookingDetailsReturn.stream()
                    .map(BookingCarDetail::getBookingCarId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList())));
            // get list drivers without driverIds
            response.setLstDriverReturn(accountRestTemplate.getListDriverAssign(driverIdsReturn, reqDTO.getRequestId(), perRequestContextDto.getBearToken()));
        }
        return response;
    }

    @Override
    public BookingAssignCarDTO dataAssignCar(AssignReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        BookingAssignCarDTO response = new BookingAssignCarDTO();
        List<BookingCarDetail> bookingDetailsOutbound;
        List<BookingCarDetail> bookingDetailsReturn;
        if (StringUtils.isNotBlank(reqDTO.getTimeStart())) {
            bookingDetailsOutbound = getBookingCarDetails(reqDTO.getTimeStart(), reqDTO.getTimeReturn());
            Set<String> carIdsOutbound = bookingDetailsOutbound.stream()
                    .map(BookingCarDetail::getCarId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            // get list drivers without driverIds
            response.setLstCarOutbound(resourceRestTemplate.getListCarAssign(carIdsOutbound, reqDTO.getRequestId(),perRequestContextDto.getBearToken()));
        }
        if (StringUtils.isNotBlank(reqDTO.getTimeReturn())) {
            bookingDetailsReturn = getBookingCarDetails(reqDTO.getTimeReturn(), reqDTO.getTimeReturn());
            // list assigned
            Set<String> carIdsReturn = bookingDetailsReturn.stream()
                    .map(BookingCarDetail::getCarId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            // get list drivers without driverIds
            response.setLstCarReturn(resourceRestTemplate.getListCarAssign(carIdsReturn, reqDTO.getRequestId(),perRequestContextDto.getBearToken()));
        }
        return response;
    }

    private List<BookingCarDetail> getBookingCarDetails(String timeStartInput, String timeReturnInput) {
        List<BookingCarDetail> bookingDetailsOutbound;
        LocalDateTime timeStart = Util.convertToLocalDateTime(timeStartInput);
        bookingDetailsOutbound = bookingCarDetailRepository.findBookingOutboundApprovedOrStarted(timeStart);
        if (StringUtils.isNotBlank(timeReturnInput)) {
            LocalDate timeReturn = Util.convertToLocalDate(timeReturnInput);
            List<BookingCarDetail> detailListRemove = new ArrayList<>();
            for (BookingCarDetail detail:bookingDetailsOutbound) {
                if (detail.getTimeStart().toLocalDate().isAfter(timeStart.toLocalDate()) && detail.getTimeStart().toLocalDate().isAfter(timeReturn)){
                    detailListRemove.add(detail);
                }
            }
            bookingDetailsOutbound.removeAll(detailListRemove);
        }
        return bookingDetailsOutbound;
    }

    @Override
    public boolean checkCarOnTrip(SimpleRequest request, HttpServletRequest httpServletRequest) {
        return bookingCarDetailRepository.checkCarOnTrip(request.getId());
    }

    @Override
    public List<BookingCar> checkListReq(SimpleRequest request, HttpServletRequest httpServletRequest) {
        LocalDateTime fromTime = LocalDateTime.now();
        accountRestTemplate.getAccountInfo(request.getId(), request.getRequestId(), perRequestContextDto.getBearToken());
        return bookingCarRepository.getListBookingCar(fromTime, request.getId());
    }

    @Override
    public List<BookingCar> checkListRequestOfDriver(SimpleRequest request, HttpServletRequest httpServletRequest) {
        LocalDateTime fromTime = LocalDateTime.now();
        accountRestTemplate.getAccountInfo(request.getId(), request.getRequestId(), perRequestContextDto.getBearToken());
        return bookingCarRepository.getListBookingCarOfDriver(fromTime, request.getId());
    }

    @Override
    public boolean rejectRequestUser(SimpleRequest request, HttpServletRequest httpServletRequest) {
        List<BookingCar> bookingCars = checkListReq(request, httpServletRequest);
        if (!bookingCars.isEmpty()) {
            for (BookingCar bkc : bookingCars) {
                bkc.setBookingStatus(BookingStatus.CANCEL);
            }
        }
        bookingCarRepository.saveAll(bookingCars);
        return true;
    }

    @Override
    public MapboxDirectionDTO calculatorDistance(CalculatorDistanceReqDTO request,
        HttpServletRequest httpServletRequest) {
      return mapboxRestTemplate.getDirectionDriving(request, request.getRequestId());
    }


    @Transactional
    @Override
    public boolean updateFuelEst(UpdateFuelEstBookingCarReqDTO request, HttpServletRequest httpServletRequest) {

      BookingCar bookingCar = bookingCarRepository.findByBookingId(request.getBookingId()).orElseThrow(
          () -> new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.BOOKING_CAR_NOT_FOUND, null)
      );
      bookingCar.setFuelEstimate(request.getFuelEstimate());
      bookingCarRepository.save(bookingCar);
      return true;
    }
}
