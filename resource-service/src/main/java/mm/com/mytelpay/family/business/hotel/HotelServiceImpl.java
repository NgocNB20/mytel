package mm.com.mytelpay.family.business.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.district.dto.DistrictHotelFilterMap;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.hotel.dto.*;
import mm.com.mytelpay.family.business.province.dto.ProvinceHotelFilterMap;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.BookingRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountRoleResDto;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.District;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.model.Hotel;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.DistrictRepository;
import mm.com.mytelpay.family.repository.HotelRepository;
import mm.com.mytelpay.family.repository.ProvinceRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class HotelServiceImpl extends FamilyBaseBusiness implements HotelService {

    public HotelServiceImpl() {
        logger = LogManager.getLogger(HotelServiceImpl.class);
    }

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private BookingRestTemplate bookingRestTemplate;
    @Autowired
    private HotelValidators hotelValidators;
    @Autowired
    private AccountRestTemplate accountRestTemplate;

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean createHotel(HotelCreateReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        hotelValidators.validateRequestCreateHotel(request);

        Hotel hotelRequest = mapper.map(request, Hotel.class);
        if (CollectionUtils.isNotEmpty(request.getRolesAllow())) {
            hotelRequest.setRolesAllow(request.getRolesAllow().stream().distinct().collect(Collectors.joining(",")));
        }
        Hotel hotelSave = hotelRepository.save(hotelRequest);
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_HOTEL, objectMapper.writeValueAsString(request));
        createArrayFile(files, hotelSave.getId(), ObjectType.HOTEL);
        return true;
    }

    @Override
    public ResponseEntity<CommonResponseDTO> getListForCms(HotelFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<Hotel> hotels = hotelRepository.getListFilterHotel(request.getCode(), request.getName(), request.getRating(), request.getProvinceId(), request.getDistrictId(), pageable);
        if (hotels.getContent().isEmpty()) {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
        }
        List<HotelFilterResDTO> resultHotels = getHotelFilterResDTOS(hotels);
        logger.info("Found:{} hotel.", resultHotels.size());
        commonResponse.setResult(new PageImpl<>(resultHotels, pageable, hotels.getTotalElements()));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Override
    public ResponseEntity<CommonResponseDTO> getListForApp(HotelFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        AccountDTO accountInfoEU = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());
        List<String> rolesOfUser = accountInfoEU.getRoles().stream().map(AccountRoleResDto::getCode).collect(Collectors.toList());
        String endUserRole = rolesOfUser.contains(RoleType.END_USER.toString()) ? RoleType.END_USER.toString() : null;
        String adminRole = rolesOfUser.contains(RoleType.ADMIN.toString()) ? RoleType.ADMIN.toString() : null;
        String chefRole = rolesOfUser.contains(RoleType.CHEF.toString()) ? RoleType.CHEF.toString() : null;
        String driverRole = rolesOfUser.contains(RoleType.DRIVER.toString()) ? RoleType.DRIVER.toString() : null;
        String driverManagerRole = rolesOfUser.contains(RoleType.DRIVER_MANAGER.toString()) ? RoleType.DRIVER_MANAGER.toString() : null;
        String directorRole = rolesOfUser.contains(RoleType.DIRECTOR.toString()) ? RoleType.DIRECTOR.toString() : null;

        Page<Hotel> hotels = hotelRepository.getListFilterHotel(request.getCode(), request.getName(), request.getRating(), request.getProvinceId(), request.getDistrictId(),
                endUserRole, adminRole, chefRole, driverRole, driverManagerRole, directorRole, pageable);

        List<HotelFilterResDTO> resultHotels = getHotelFilterResDTOS(hotels);
        logger.info("Found:{} hotel.", resultHotels.size());
        commonResponse.setResult(new PageImpl<>(resultHotels, pageable, hotels.getTotalElements()));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @NotNull
    private List<HotelFilterResDTO> getHotelFilterResDTOS(Page<Hotel> hotels) {
        List<Hotel> hotelList = hotels.getContent();
        List<String> hotelIds = hotelList.stream().map(Hotel::getId).collect(Collectors.toList());
        Map<String, ProvinceHotelFilterMap> provinceHotelMaps = provinceRepository.getProvincesByHotelIds(hotelIds).stream().collect(Collectors.toMap(
                ProvinceHotelFilterMap::getHotelId, provinceHotelFilterMap -> provinceHotelFilterMap
        ));
        Map<String, DistrictHotelFilterMap> districtHotelMaps = districtRepository.getDistrictByHotelIds(hotelIds).stream().collect(Collectors.toMap(
                DistrictHotelFilterMap::getHotelId, districtHotelFilterMap -> districtHotelFilterMap
        ));
        Map<String, List<FileAttach>> fileMaps = fileService.findImagesByObjectIdsAndType(hotelIds, ObjectType.HOTEL).stream().collect(
                Collectors.groupingBy(FileAttach::getObjectId, HashMap::new, Collectors.toCollection(ArrayList::new))
        );
        List<HotelFilterResDTO> resultHotels = new ArrayList<>();
        for (Hotel hotel : hotelList) {
            resultHotels.add(mapHotelToHotelResponse(hotel, provinceHotelMaps, districtHotelMaps, fileMaps));
        }
        return resultHotels;
    }

    private HotelFilterResDTO mapHotelToHotelResponse(Hotel hotel, Map<String, ProvinceHotelFilterMap> provinceHotelMaps, Map<String, DistrictHotelFilterMap> districtHotelMaps, Map<String, List<FileAttach>> fileMaps) {
        HotelFilterResDTO hotelResponse = mapper.map(hotel, HotelFilterResDTO.class);
        String hotelId = hotel.getId();
        ProvinceHotelFilterMap provinceHotelFilterMap = provinceHotelMaps.get(hotelId);
        if (provinceHotelFilterMap != null) {
            hotelResponse.setProvinceName(provinceHotelFilterMap.getProvinceName());
            hotelResponse.setProvinceName(provinceHotelFilterMap.getProvinceName());
        }
        DistrictHotelFilterMap districtHotelFilterMap = districtHotelMaps.get(hotelId);
        if (districtHotelFilterMap != null) {
            hotelResponse.setDistrictCode(districtHotelFilterMap.getDistrictCode());
            hotelResponse.setDistrictName(districtHotelFilterMap.getDistrictName());
        }

        List<FileAttach> fileAttach = fileMaps.get(hotelId);
        if (CollectionUtils.isNotEmpty(fileAttach)) {
            List<FileResponse> listResponse = mapper.map(fileAttach, new TypeToken<List<FileResponse>>() {
            }.getType());
            hotelResponse.setFile(listResponse);
        }
        return hotelResponse;
    }

    @Override
    public List<HotelFilterResDTO> getHotelsByIds(GetHotelsByIdsReqDTO request, HttpServletRequest httpServletRequest) {
        return hotelRepository.findByHotelId(request.getIds());
    }
    @Override
    public HotelDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        Hotel hotel = hotelRepository.findById(request.getId()).orElseThrow(() -> {
            logger.error("Hotel id:{} not found", request.getId());
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Hotel.NOT_FOUND, null);
        });
        HotelDetailResDTO response = mapper.map(hotel, HotelDetailResDTO.class);

        List<String> roleAllows = StringUtils.isEmpty(hotel.getRolesAllow())
                ? Collections.emptyList()
                : Arrays.stream(hotel.getRolesAllow().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        response.setRolesAllow(roleAllows);
        Optional<Province> provinceOfHotel = provinceRepository.findById(hotel.getProvinceId());
        provinceOfHotel.ifPresent(province -> {
            response.setProvinceCode(province.getCode());
            response.setProvinceName(province.getName());
        });

        Optional<District> districtOfHotel = districtRepository.findById(hotel.getDistrictId());
        districtOfHotel.ifPresent(district -> {
            response.setDistrictCode(district.getCode());
            response.setDistrictName(district.getName());
        });

        List<FileAttach> fileAttach = fileService.findImageByObjectIdAndType(hotel.getId(), ObjectType.HOTEL);
        List<FileResponse> listResponse = mapper.map(fileAttach, new TypeToken<List<FileResponse>>() {
        }.getType());
        response.setFile(listResponse);

        return response;
    }

    @Override
    @Transactional
    public boolean editHotel(HotelEditReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Hotel hotelInDb = hotelRepository.findById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Hotel.NOT_FOUND, null);
        });

        insertActionLog(request.getRequestId(), null, ActionType.EDIT_HOTEL, objectMapper.writeValueAsString(request));
        hotelRepository.save(mapperHotelRequestToHotel(request, files, hotelInDb));
        return true;
    }

    @Override
    @Transactional
    public void deleteHotel(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            Optional<Hotel> hotel = hotelRepository.findById(request.getId());
            if (hotel.isEmpty()){
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Hotel.NOT_FOUND, null);
            }
            if (Boolean.TRUE.equals(bookingRestTemplate.checkExistedBookedHotel(request.getId(), request.getRequestId(), perRequestContextDto.getBearToken()))) {
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Hotel.HOTEL_BOOKED_CANNOT_BE_DELETED, null);
            }

            insertActionLog(request.getRequestId(), null, ActionType.DELETE_HOTEL, objectMapper.writeValueAsString(request));
            hotelRepository.deleteById(request.getId());
            deleteFile(request.getId(), ObjectType.HOTEL);
        } catch (BusinessEx ex){
            throw ex;
        } catch (Exception e){
            logger.error("Delete hotel id:{} fail", request.getId());
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Hotel.DELETE_FAIL, null);
        }
    }

    private Hotel mapperHotelRequestToHotel(HotelEditReqDTO request, MultipartFile[] files, Hotel hotel) {
        if (StringUtils.isNotBlank(request.getName())) {
            hotel.setName(request.getName());
        }

        if (StringUtils.isNotBlank(request.getPhone())) {
            hotel.setPhone(request.getPhone());
        }

        if (StringUtils.isNotBlank(request.getCode()) && !StringUtils.equals(request.getCode(), hotel.getCode())) {
            hotelValidators.validateDuplicateHotelCode(request.getCode(), request.getRequestId());
            hotel.setCode(request.getCode());
        }

        if (StringUtils.isNotBlank(request.getProvinceId())) {
            hotelValidators.validateNotFoundProvinceId(request.getProvinceId(), request.getRequestId());
            hotel.setProvinceId(request.getProvinceId());
        }

        if (StringUtils.isNotBlank(request.getDistrictId())) {
            hotelValidators.validateNotFoundDistrictId(request.getDistrictId(), request.getRequestId());
            hotelValidators.validateDistrictIsInProvince(request.getDistrictId(), request.getProvinceId(), request.getRequestId());
            hotel.setDistrictId(request.getDistrictId());
        }

        if (StringUtils.isNotBlank(request.getAddress())) {
            hotel.setAddress(request.getAddress());
        }

        if (StringUtils.isNotBlank(request.getPhone())) {
            hotel.setPhone(request.getPhone());
        }

        if (StringUtils.isNotBlank(request.getDescription())) {
            hotel.setDescription(request.getDescription());
        }

        if (StringUtils.isNotBlank(request.getMaxPrice())) {
            hotel.setMaxPrice(Integer.valueOf(request.getMaxPrice()));
        }

        if (StringUtils.isNotBlank(request.getMaxPlusPrice())) {
            hotel.setMaxPlusPrice(Integer.valueOf(request.getMaxPlusPrice()));
        }

        if (request.getRating() != null) {
            hotel.setRating(request.getRating());
        }

        if (CollectionUtils.isNotEmpty(request.getRolesAllow())) {
            hotelValidators.validateRoleAllows(request.getRolesAllow());
            hotel.setRolesAllow(request.getRolesAllow().stream().distinct().collect(Collectors.joining(",")));
        }

        if (request.getFileDeletes() != null) {
            deleteFileByIds(request.getFileDeletes(), request.getId(), ObjectType.HOTEL);
        }

        if (files != null) {
            createArrayFile(files, hotel.getId(), ObjectType.HOTEL);
        }

        return hotel;
    }
}
