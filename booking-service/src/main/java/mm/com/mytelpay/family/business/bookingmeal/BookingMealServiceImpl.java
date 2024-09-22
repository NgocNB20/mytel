package mm.com.mytelpay.family.business.bookingmeal;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.AccountReportDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.*;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.*;
import mm.com.mytelpay.family.enums.*;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.job.SetMealIdForBookingMeal;
import mm.com.mytelpay.family.model.BookingMeal;
import mm.com.mytelpay.family.model.BookingMealDetail;
import mm.com.mytelpay.family.repo.BookingMealDetailRepository;
import mm.com.mytelpay.family.repo.BookingMealRepository;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class BookingMealServiceImpl extends BookingBaseBusiness implements BookingMealService {

    public BookingMealServiceImpl() {
        logger = LogManager.getLogger(BookingMealServiceImpl.class);
    }

    @Autowired
    private BookingMealRepository bookingMealRepository;

    @Autowired
    private BookingMealDetailRepository bookingMealDetailRepository;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;

    @Autowired
    private BookingMealTransaction bookingMealTransaction;

    @Autowired
    private BookingMealValidators bookingMealValidators;

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean create(BookingMealCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String accountId = perRequestContextDto.getCurrentAccountId();
        String bearerAuth = perRequestContextDto.getBearToken();
        AccountDTO account = accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), bearerAuth);
        if (ObjectUtils.isEmpty(request.getWeekdays())){
            request.setWeekdays(Collections.singletonList(Day.ALL.toString()));
        }
        if (!Status.ACTIVE.equals(account.getStatus())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Account.ACCOUNT_INACTIVE, null);
        }
        account.getRoles().forEach(a -> {
            if (a.getCode().equals(RoleType.CHEF.toString())) {
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.DONT_HAVE_PERMISSION, null);
            }
        });
        BookingMeal bookingMeal = mapper.map(request, BookingMeal.class);
        bookingMeal.setAccountId(accountId);
        bookingMeal.setUnitId(account.getUnitId());

        LocalDate fromTime = StringUtils.isBlank(request.getFrom()) ? LocalDate.now() : Util.convertToLocalDate(request.getFrom());
        LocalDate toTime = StringUtils.isBlank(request.getTo()) ? LocalDate.now() : Util.convertToLocalDate(request.getTo());
        bookingMeal.setFromTime(fromTime);
        bookingMeal.setToTime(toTime);
        long numDays = ChronoUnit.DAYS.between(fromTime, toTime) + 1;

        //validate date
        validateDateInput(request, fromTime, toTime);

        //create bookingMealDetail
        List<BookingMealDetail> bookingMealDetails = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        if (fromTime.equals(toTime)) {
            dates.add(fromTime);
            Map<MealType, Integer> mapPrice = resourceRestTemplate.getPriceMeal(Constants.CONFIG_PRICE_MEAL, request.getRequestId(), bearerAuth);
            for (String m : request.getMealType()) {
                bookingMealDetails.addAll(createMealDetails(Collections.singletonList(fromTime), MealType.valueOf(m), mapPrice));
            }
        } else {
            createBookingMealDetails(request, fromTime, numDays, bookingMealDetails, dates);
        }

        //remove booking existing
        removeBookingMealExisting(bookingMealDetails, dates, accountId);

        //calculate Total Amount
        int totalAmount = getTotalAmount(request, account, bookingMealDetails);

        updateMealIdForBookingMealDetails(request.getRequestId(), request.getCanteenId(), bookingMealDetails);

        String data = objectMapper.writeValueAsString(request);
        bookingMealTransaction.saveAndDeductionMoney(bearerAuth, bookingMeal, bookingMealDetails, totalAmount);
        insertActionLog(request.getRequestId(), accountId, ActionType.CREATE_BOOKING_HOTEL, null, data);
        return true;
    }

    private void updateMealIdForBookingMealDetails(String requestId, String canteenId, List<BookingMealDetail> bookingMealDetails) {
        List<GetListMenuResDTO> listMenuResDTOS = resourceRestTemplate.getListMeal(canteenId, requestId);

        if (listMenuResDTOS.isEmpty()){
            return;
        }
        Map<String, Map<MealType, String>> menuMap =SetMealIdForBookingMeal.mapMealType(listMenuResDTOS);
        bookingMealDetails.forEach(b -> {
            if (Objects.nonNull(menuMap.get(b.getMealDay().getDayOfWeek().toString()))){
                b.setMealId(menuMap.get(b.getMealDay().getDayOfWeek().toString()).get(b.getType()));
            }
        });
    }

    private int getTotalAmount(BookingMealCreateReqDTO request, AccountDTO account, List<BookingMealDetail> bookingMealDetails) {
        int totalAmount = 0;
        for (BookingMealDetail b : bookingMealDetails) {
            totalAmount += b.getFee();
        }
        //check if EU have enough money
        if (account.getBalance() < totalAmount) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.NOT_ENOUGH_MONEY, null);
        }
        return totalAmount;
    }

    private void removeBookingMealExisting(List<BookingMealDetail> bookingMealDetails, List<LocalDate> dates, String accountId) {
        List<BookingMealDetail> mealDetailsInDB = bookingMealDetailRepository.findAllByMealDayIn(dates, accountId);
        List<BookingMealDetail> mealDetailsIsExist = new ArrayList<>();
        for (BookingMealDetail b : mealDetailsInDB) {
            for (BookingMealDetail bdb : bookingMealDetails) {
                if (b.getMealDay().equals(bdb.getMealDay()) && b.getType().equals(bdb.getType())) {
                    mealDetailsIsExist.add(bdb);
                }
            }
        }
        bookingMealDetails.removeAll(mealDetailsIsExist);
        if (bookingMealDetails.isEmpty()) {
            throw new BusinessEx(null, BookingErrorCode.BookingMeal.ORDER_ALREADY_CREATED, null);
        }
    }

    private void createBookingMealDetails(BookingMealCreateReqDTO request, LocalDate fromTime, long numDays, List<BookingMealDetail> bookingMealDetails, List<LocalDate> dates) {
        List<LocalDate> dayBreakfast = new ArrayList<>();
        List<LocalDate> dayLunch = new ArrayList<>();
        List<LocalDate> dayDinner = new ArrayList<>();
        for (int i = 0; i < numDays; i++) {
            LocalDate date = fromTime.plusDays(i);
            dates.add(date);
            if (!request.getWeekdays().contains(date.getDayOfWeek().toString()) && !request.getWeekdays().contains(Day.ALL.toString())) {
                continue;
            }
            createMealsInDay(request, dayBreakfast, dayLunch, dayDinner, date);
        }

        Map<MealType, Integer> mapPrice = resourceRestTemplate.getPriceMeal(Constants.CONFIG_PRICE_MEAL, request.getRequestId(), perRequestContextDto.getBearToken());

        if (request.getMealType().contains(MealType.BREAKFAST.toString())) {
            bookingMealDetails.addAll(createMealDetails(dayBreakfast, MealType.BREAKFAST, mapPrice));
        }
        if (request.getMealType().contains(MealType.LUNCH.toString())) {
            bookingMealDetails.addAll(createMealDetails(dayLunch, MealType.LUNCH, mapPrice));
        }
        if (request.getMealType().contains(MealType.DINNER.toString())) {
            bookingMealDetails.addAll(createMealDetails(dayDinner, MealType.DINNER, mapPrice));
        }
    }

    private void createMealsInDay(BookingMealCreateReqDTO request, List<LocalDate> dayBreakfast, List<LocalDate> dayLunch, List<LocalDate> dayDinner, LocalDate date) {
        if (date.isEqual(LocalDate.now())) {
            createMealsToDay(request, dayBreakfast, dayLunch, dayDinner, date);
        } else {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                dayBreakfast.add(date);
                dayLunch.add(date);
            }
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY && date.getDayOfWeek() != DayOfWeek.SATURDAY) {
                dayBreakfast.add(date);
                dayDinner.add(date);
                dayLunch.add(date);
            }
        }
    }

    private void createMealsToDay(BookingMealCreateReqDTO request, List<LocalDate> dayBreakfast, List<LocalDate> dayLunch, List<LocalDate> dayDinner, LocalDate date) {
        LocalTime timeNow = LocalTime.now();
        if (request.getMealType().contains(MealType.BREAKFAST.toString())) {
            LocalTime deadlineToBookingMeal = bookingMealValidators.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_BOOKING_MEAL_KEY, MealType.BREAKFAST, Constants.DEFAULT_BREAKFAST_BOOKING_OVER_DUE_TIME);
            if (!timeNow.isAfter(deadlineToBookingMeal)) {
                dayBreakfast.add(date);
            }
        }
        if (request.getMealType().contains(MealType.LUNCH.toString())) {
            LocalTime deadlineToBookingMeal = bookingMealValidators.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_BOOKING_MEAL_KEY, MealType.LUNCH, Constants.DEFAULT_LUNCH_BOOKING_OVER_DUE_TIME);
            if (!timeNow.isAfter(deadlineToBookingMeal)) {
                dayLunch.add(date);
            }
        }
        if (request.getMealType().contains(MealType.DINNER.toString())) {
            LocalTime deadlineToBookingMeal = bookingMealValidators.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_BOOKING_MEAL_KEY, MealType.DINNER, Constants.DEFAULT_DINNER_BOOKING_OVER_DUE_TIME);
            if (!timeNow.isAfter(deadlineToBookingMeal)) {
                dayDinner.add(date);
            }
        }
    }

    private List<BookingMealDetail> createMealDetails(List<LocalDate> listDay, MealType mealType, Map<MealType, Integer> mapPrice) {
        List<BookingMealDetail> bookingMealDetails = new ArrayList<>();
        try{
            for (LocalDate localDate : listDay) {
                BookingMealDetail detail = new BookingMealDetail();
                detail.setMealDay(localDate);
                detail.setStatus(MealDetailStatus.PENDING);
                detail.setType(mealType);
                if (detail.getType() == MealType.BREAKFAST) {
                    detail.setFee(mapPrice.get(MealType.BREAKFAST));
                } else if (detail.getType() == MealType.LUNCH) {
                    detail.setFee(mapPrice.get(MealType.LUNCH));
                } else if (detail.getType() == MealType.DINNER) {
                    detail.setFee(mapPrice.get(MealType.DINNER));
                }
                bookingMealDetails.add(detail);
            }
        } catch (Exception e){
            logger.error("Create booking meal detail fail: ", e);
            throw new BusinessEx(BookingErrorCode.BookingMeal.CREATE_BOOKING_MEAL_DETAIL_FAIL, null);
        }
        return bookingMealDetails;
    }

    private void validateDateInput(BookingMealCreateReqDTO request, LocalDate fromTime, LocalDate toTime) {
        CanteenResourceDTO canteenResourceDTO = resourceRestTemplate.getCanteenInfo(request.getCanteenId(), request.getRequestId(), perRequestContextDto.getBearToken());
        if (Objects.isNull(canteenResourceDTO) || ObjectUtils.isEmpty(canteenResourceDTO)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Resource.CANTEEN_NOT_FOUND, null);
        }
        if (ChronoUnit.DAYS.between(LocalDate.now(), fromTime) + 1 > Constants.LIMIT_DAY_ORDER || ChronoUnit.DAYS.between(LocalDate.now(), toTime) + 1 > Constants.LIMIT_DAY_ORDER) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.NUMBER_BOOKING_EXCEEDS_LIMIT, null);
        }
        if (fromTime.isBefore(LocalDate.now())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_THAN_CURRENT_DATE, null);
        }
        if (fromTime.isAfter(toTime)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
        }
        if (fromTime.equals(toTime)) {
            validateFromEqualsToTime(request, fromTime);
        }else if (!request.getWeekdays().contains(Day.ALL.toString())){
            List<String> dayList = new ArrayList<>();
            while (!fromTime.isAfter(toTime)) {
                dayList.add(fromTime.getDayOfWeek().toString());
                fromTime= fromTime.plusDays(1);
            }
            for (String day:request.getWeekdays()) {
                if (!dayList.contains(day)){
                    throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.DAY_BOOKING_INVALID, null);
                }
            }
        }
    }

    private void validateFromEqualsToTime(BookingMealCreateReqDTO request, LocalDate fromTime) {
        if (!ObjectUtils.isEmpty(request.getWeekdays()) && !request.getWeekdays().contains(fromTime.getDayOfWeek().toString())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.DAY_BOOKING_INVALID, null);
        }

        this.validateTimeBooking(request, fromTime);

        if (fromTime.getDayOfWeek() == DayOfWeek.SATURDAY && request.getMealType().contains(MealType.DINNER.toString())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.NOT_ODER_DINNER_SATURDAY, null);
        }
        if (fromTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.NOT_ODER_SUNDAY, null);
        }
    }

    private void validateTimeBooking(BookingMealCreateReqDTO request, LocalDate fromTime) {
        if (fromTime.equals(LocalDate.now())) {
            LocalTime timeNow = LocalTime.now();

            if (request.getMealType().contains(MealType.BREAKFAST.toString())) {
                LocalTime deadlineToBookingMeal = bookingMealValidators.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_BOOKING_MEAL_KEY, MealType.BREAKFAST, Constants.DEFAULT_BREAKFAST_BOOKING_OVER_DUE_TIME);
                if (timeNow.isAfter(deadlineToBookingMeal)) {
                    throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.BREAKFAST_OVER_DUE_TIME, null);
                }
            } else if (request.getMealType().contains(MealType.LUNCH.toString())) {
                LocalTime deadlineToBookingMeal = bookingMealValidators.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_BOOKING_MEAL_KEY, MealType.LUNCH, Constants.DEFAULT_LUNCH_BOOKING_OVER_DUE_TIME);
                if (timeNow.isAfter(deadlineToBookingMeal)) {
                    throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.LUNCH_OVER_DUE_TIME, null);
                }
            } else if (request.getMealType().contains(MealType.DINNER.toString())) {
                LocalTime deadlineToBookingMeal = bookingMealValidators.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_BOOKING_MEAL_KEY, MealType.DINNER, Constants.DEFAULT_DINNER_BOOKING_OVER_DUE_TIME);
                if (timeNow.isAfter(deadlineToBookingMeal)) {
                    throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.DINNER_OVER_DUE_TIME, null);
                }
            }
        }
    }

    @Override
    public PageImpl<FilterBookingMealResDTO> getListForEU(FilterBookingMealEUReqDTO request, HttpServletRequest httpServletRequest) {
        String bearerAuth = perRequestContextDto.getBearToken();
        String accountId = perRequestContextDto.getCurrentAccountId();
        accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), bearerAuth);
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        LocalDate fromTime = StringUtils.isBlank(request.getFrom()) ? null : Util.convertToLocalDate(request.getFrom());
        LocalDate toTime = StringUtils.isBlank(request.getTo()) ? null : Util.convertToLocalDate(request.getTo());
        if (Objects.nonNull(fromTime) && Objects.nonNull(toTime) && fromTime.isAfter(toTime)) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCommon.FROM_DATE_LEST_THAN_TO_DATE, null);
        }
        Page<BookingMealDetail> bookingMealDetails = bookingMealDetailRepository.filterBookingMeal(fromTime, toTime, accountId, pageable);
        List<LocalDate> dates = bookingMealDetails.getContent().stream().map(BookingMealDetail::getMealDay).collect(Collectors.toList());
        List<FilterBookingMealDetailDTO> bookingMealDetailList = bookingMealDetailRepository.getDetailByMealDayIn(dates, accountId);

        List<String> listBookingMealIds = bookingMealDetailList.stream().map(FilterBookingMealDetailDTO::getBookingMealId).distinct().collect(Collectors.toList());
        List<String> listCanteenIds = bookingMealRepository.getCanteenIdsByBookingMealIds(listBookingMealIds);

        List<CanteenResourceDTO> listCanteen = resourceRestTemplate.getListCanteenByIds(listCanteenIds, request.getRequestId(), bearerAuth);
        Map<String, CanteenResourceDTO> canteenMap = new HashMap<>();
        for (CanteenResourceDTO canteen : listCanteen) {
            canteenMap.put(canteen.getId(), canteen);
        }

        List<FilterBookingMealResDTO> filterBookingMealResDTOS = getFilterBookingMealResDTOS(dates, bookingMealDetailList, listCanteen, canteenMap);
        return new PageImpl<>(filterBookingMealResDTOS, pageable, bookingMealDetails.getTotalElements());
    }

    @NotNull
    private List<FilterBookingMealResDTO> getFilterBookingMealResDTOS(List<LocalDate> dates, List<FilterBookingMealDetailDTO> bookingMealDetailList, List<CanteenResourceDTO> listCanteen, Map<String, CanteenResourceDTO> canteenMap) {
        Map<String, List<FilterBookingMealDetailDTO>> listMap = new LinkedHashMap<>();
        for (LocalDate date : dates) {
            if (!listMap.containsKey(date.toString())) {
                listMap.put(date.toString(), new ArrayList<>());
            }
        }

        for (FilterBookingMealDetailDTO b : bookingMealDetailList) {
            if (!listMap.containsKey(b.getMealDay().toString())) {
                listMap.put(b.getMealDay().toString(), new ArrayList<>());
            }
            FilterBookingMealDetailDTO dto = mapper.map(b, FilterBookingMealDetailDTO.class);
            dto.setMealType(b.getMealType());
            if (!listCanteen.isEmpty() && Objects.nonNull(canteenMap.get(dto.getCanteenId()))) {
                dto.setCanteenName(canteenMap.get(dto.getCanteenId()).getName());
                dto.setCanteenAddress(canteenMap.get(dto.getCanteenId()).getAddress());
            }
            if (listMap.containsKey(b.getMealDay().toString()) && listMap.get(b.getMealDay().toString()).contains(dto)) {
                continue;
            }
            listMap.get(b.getMealDay().toString()).add(dto);
        }
        List<FilterBookingMealResDTO> filterBookingMealResDTOS = new ArrayList<>();
        for (Map.Entry<String, List<FilterBookingMealDetailDTO>> entry : listMap.entrySet()) {
            FilterBookingMealResDTO dto = new FilterBookingMealResDTO();
            dto.setMealDay(Util.convertLocalDateToString(LocalDate.parse(entry.getKey())));
            dto.setListOrderMeal(entry.getValue());
            filterBookingMealResDTOS.add(dto);
        }
        return filterBookingMealResDTOS;
    }

    @Override
    public BookingMealDetailResDTO getDetail(BookingMealDetailReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        String brearAuth = perRequestContextDto.getBearToken();
        BookingMealDetail bookingMealDetail = getBookingMealDetail(request.getId(), request.getRequestId());
        BookingMeal bookingMeal = getBookingMeal(bookingMealDetail.getBookingMealId(), request.getRequestId());
        CanteenResourceDTO canteenResourceDTO = resourceRestTemplate.getCanteenInfo(bookingMeal.getCanteenId(), request.getRequestId(), brearAuth);

        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(bookingMeal.getAccountId(), request.getRequestId(), brearAuth);
        BookingMealDetailResDTO bookingMealDetailResDTO = new BookingMealDetailResDTO(bookingMealDetail);
        bookingMealDetailResDTO.setReason(bookingMealDetail.getReason());
        bookingMealDetailResDTO.setCanteenName(canteenResourceDTO.getName());
        bookingMealDetailResDTO.setCanteenAddress(canteenResourceDTO.getAddress());
        bookingMealDetailResDTO.setFee(bookingMealDetail.getFee());
        bookingMealDetailResDTO.setAccount(accountDTO);
        bookingMealDetailResDTO.setReason(bookingMealDetail.getReason());

        GetMealByIdDTO meal = resourceRestTemplate.getMealDetailById(bookingMealDetail.getMealId(), request.getRequestId(), brearAuth);
        bookingMealDetailResDTO.setMeal(meal);
        return bookingMealDetailResDTO;
    }

    @Override
    public PageImpl<ReportOrderMealResDTO> filter(ReportBookingMealReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        List<AccountReportDTO> accountReportDTO = getAccountReportDTOS(request.getPhone(), request.getRequestId());
        List<String> accountId = accountReportDTO.stream().map(AccountReportDTO::getAccountId).collect(Collectors.toList());
        try {
            Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
            LocalDate fromTime = Util.convertToLocalDate(request.getFromDate());
            LocalDate toTime = Util.convertToLocalDate(request.getToDate());

            Page<ReportOrderMealResDTO> responses = bookingMealRepository.filter(accountId,
                    MealDetailStatus.valueOf(request.getStatus()),
                    request.getCanteenId(),
                    MealType.valueOf(request.getMeal()),
                    request.getUnitId(),
                    fromTime,
                    toTime,
                    pageable);
            logger.info("Found:{} booking meal.", responses.getTotalElements());
            return new PageImpl<>(responses.getContent(), pageable, responses.getTotalElements());
        } catch (Exception e) {
            logger.error("Get List booking hotel unsuccessfully", e);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @Transactional
    public boolean verifyQrBookingMeal(VerifyQrBookingMealReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BookingMealDetail bookingMealDetail = getBookingMealDetail(request.getBookingDetailId(), request.getRequestId());
        checkCanteenIdOfBookingMealAndChef(bookingMealDetail);
        if (!MealDetailStatus.PENDING.equals(bookingMealDetail.getStatus())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.QR_IS_EXPIRED, null);
        }
        if (!LocalDate.now().isEqual(bookingMealDetail.getMealDay())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingMeal.CANNOT_SCAN_QR_TODAY, null);
        }
        String timeScanQrMealKey =  "time_scan_qr_meal";
        String timeScanQrMealJsonValue = applicationSettingCommonService.getMessageByKey(timeScanQrMealKey, request.getRequestId(), perRequestContextDto.getBearToken(), null);
        bookingMealValidators.validateTimeScanQr(timeScanQrMealJsonValue, bookingMealDetail);

        bookingMealDetail.setStatus(MealDetailStatus.DONE);
        bookingMealDetailRepository.save(bookingMealDetail);
        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.VERIFY_QR,null, data);
        return true;
    }

    @Override
    public boolean cancelOrderMeal(CancelOrderMealReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BookingMealDetail bookingMealDetail = getBookingMealDetail(request.getBookingDetailId(), request.getRequestId());
        BookingMeal bookingMeal = getBookingMeal(bookingMealDetail.getBookingMealId(), request.getRequestId());
        if (!Util.isRightCurrentAccountId(bookingMeal.getAccountId(), perRequestContextDto.getCurrentAccountId())) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.DONT_HAVE_PERMISSION, null);
        }
        Integer yearOfMealDate = bookingMealDetail.getMealDay().getYear();
        List<LocalDate> holidaysInYear = resourceRestTemplate.getPublicHolidaysInYear(yearOfMealDate, perRequestContextDto.getBearToken());

        bookingMealValidators.validateTimeBeforeCancel(holidaysInYear, bookingMealDetail);

        bookingMealTransaction.cancelOderMeal(perRequestContextDto.getBearToken(), bookingMealDetail, request.getReason());
        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.CANCEL_BOOKING_MEAL, null, data);
        return true;
    }

    private BookingMealDetail getBookingMealDetail(String bookingDetailId, String requestId) {
        return bookingMealDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new BusinessEx(requestId, BookingErrorCode.BookingMeal.BOOKING_MEAL_DETAIL_NOT_FOUND, null));
    }

    private BookingMeal getBookingMeal(String bookingMealId, String requestId) {
        return bookingMealRepository.findById(bookingMealId)
                .orElseThrow(() -> new BusinessEx(requestId, BookingErrorCode.BookingMeal.BOOKING_MEAL_NOT_FOUND, null));
    }

    @Override
    public BookingMealFilterDTO getTotalBookingMeal(BookingMealFilterReqDTO reqDTO) {
        BookingMealFilterDTO totalBookingMeal = new BookingMealFilterDTO();
        List<BookingMealFilterResDTO> getBookingMeal = getBookingMeal(reqDTO);
        totalBookingMeal.setBookingMealFilterResDTOS(getBookingMeal);
        int totalOrder = 0;
        for (BookingMealFilterResDTO bookingMeal : getBookingMeal) {
            totalOrder += bookingMeal.getTotalOrder();
        }
        totalBookingMeal.setTotalOrder(totalOrder);
        return totalBookingMeal;
    }

    @Override
    public PageImpl<BookingMealViewListDTO> listOrderBookingMeal(BookingMealViewListReqDTO request) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        CanteenForChefDTO canteenForChefDTO = getCanteenForChefDTO();
        String phone = StringUtils.isBlank(request.getPhoneNumber()) ? null : request.getPhoneNumber();
        List<String> listAccountIdsByPhone = getListAccountId(phone, request);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate from = validFromToDate(formatter, request.getFrom());
        LocalDate to = validFromToDate(formatter, request.getTo());
        checkToTime(from, to);
        MealDetailStatus status = StringUtils.isBlank(request.getStatus()) ? null : MealDetailStatus.valueOf(request.getStatus());
        MealType mealType = StringUtils.isBlank(request.getMealType()) ? null : MealType.valueOf(request.getMealType());
        Page<BookingMealViewListDTO> bookingMealViewListDTOS = bookingMealDetailRepository.getOrderMealList(from, to, status, mealType, listAccountIdsByPhone,canteenForChefDTO.getCanteenId(), pageable);
        if (bookingMealViewListDTOS.isEmpty()) {
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        setUserNameAndPhoneForBookingMeal(bookingMealViewListDTOS);
        return new PageImpl<>(bookingMealViewListDTOS.getContent(), bookingMealViewListDTOS.getPageable(), bookingMealViewListDTOS.getTotalElements());
    }

    private CanteenForChefDTO getCanteenForChefDTO() {
        String bearAuth = perRequestContextDto.getBearToken();
        AccountDTO accountDTO = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), null, bearAuth);
        CanteenForChefDTO canteenId = new CanteenForChefDTO();
        canteenId.setCanteenId(accountDTO.getCanteenId());
        return canteenId;
    }

    private void checkCanteenIdOfBookingMealAndChef (BookingMealDetail bookingMealDetail){
        BookingMeal bookingMeal = bookingMealRepository.findById(bookingMealDetail.getBookingMealId()).orElseThrow(() -> {
            throw new BusinessEx(null, BookingErrorCode.BookingMeal.BOOKING_MEAL_NOT_FOUND, null);
        });
        String canteenIdOfBookingMeal = bookingMeal.getCanteenId();
        String bearerAuth = perRequestContextDto.getBearToken();
        String accountId = perRequestContextDto.getCurrentAccountId();
        AccountDTO accountDTO =  accountRestTemplate.getAccountInfo(accountId, null, bearerAuth);
        String canteenIdOfChef = accountDTO.getCanteenId();
        if(!canteenIdOfBookingMeal.equals(canteenIdOfChef)){
            throw new BusinessEx(null, BookingErrorCode.BookingMeal.NOT_SCAN_QR, null);
        }
    }

    private void setUserNameAndPhoneForBookingMeal(Page<BookingMealViewListDTO> bookingMealViewListDTOS) {
        List<String> accountIds = bookingMealViewListDTOS.stream().map(BookingMealViewListDTO::getAccountId).distinct().collect(Collectors.toList());
        List<AccountReportDTO> accountReportDTOS = accountRestTemplate.getListAccountReport(null, accountIds, perRequestContextDto.getBearToken());
        bookingMealViewListDTOS.forEach(bookingMealViewListDTO ->
            accountReportDTOS.forEach(accountReportDTO -> {
                if (accountReportDTO.getAccountId().equals(bookingMealViewListDTO.getAccountId())) {
                    bookingMealViewListDTO.setUserName(accountReportDTO.getFullName());
                    bookingMealViewListDTO.setUserPhone(accountReportDTO.getPhone());
                    bookingMealViewListDTO.setAvtUrl(accountReportDTO.getAvtUrl());
                }
            }));
    }


    private LocalDate validFromToDate(DateTimeFormatter formatter, String reqFrom) {
        LocalDate date = null;
        if (!StringUtils.isBlank(reqFrom)) {
            try {
                date = LocalDate.parse(reqFrom, formatter);
            } catch (DateTimeParseException e) {
                throw new BusinessEx(null, BookingErrorCode.BookingCommon.FROM_DATE_INVALID, null);
            }
        }
        return date;
    }

    private void checkToTime(LocalDate from, LocalDate to) {
        if (Objects.nonNull(from) && Objects.nonNull(to) && to.isBefore(from)) {
            throw new BusinessEx(null, BookingErrorCode.BookingCar.TIME_RETURN_INVALID, null);
        }
    }

    private List<String> getListAccountId(String phone, BookingMealViewListReqDTO request) {
        List<String> listAccountId = new ArrayList<>();
        getListAccountIds(request.getRequestId(), listAccountId, phone);
        if (CollectionUtils.isEmpty(listAccountId)) {
            listAccountId = null;
        }
        return listAccountId;
    }

    public List<BookingMealFilterResDTO> getBookingMeal(BookingMealFilterReqDTO request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        CanteenForChefDTO canteenForChefDTO = getCanteenForChefDTO();
        LocalDate from = validFromToDate(formatter, request.getFrom());
        LocalDate to = validFromToDate(formatter, request.getTo());
        checkToTime(from, to);
        MealDetailStatus status = StringUtils.isBlank(request.getStatus()) ? null : MealDetailStatus.valueOf(request.getStatus());
        return bookingMealDetailRepository.getTotalBookingMeal(from, to, status, canteenForChefDTO.getCanteenId());
    }

}
