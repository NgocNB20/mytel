package mm.com.mytelpay.family.business.district;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.model.Hotel;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.district.dto.*;

import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.District;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.repository.DistrictRepository;
import mm.com.mytelpay.family.repository.HotelRepository;
import mm.com.mytelpay.family.repository.ProvinceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class DistrictServiceIml extends FamilyBaseBusiness implements DistrictService {
    @Autowired
    DistrictRepository districtRepository;
    @Autowired
    ProvinceRepository provinceRepository;
    @Autowired
    HotelRepository hotelRepository;

    @Override
    public boolean create(DistrictReqDTO request, HttpServletRequest httpServletRequest) {
        Optional<Province> provinceOptional = provinceRepository.getProvinceById(request.getProvinceId());
        if (provinceOptional.isEmpty())
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Province.NOT_FOUND, null);
        districtRepository.findByCode(request.getCode()).ifPresent(district -> {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.District.DUPLICATE_CODE, null);
        });
        District district = new District();
        district.setId(UUID.randomUUID().toString());
        district.setName(request.getName().trim());
        district.setDescription(Objects.nonNull(request.getDescription()) ? request.getDescription().trim() : null);
        district.setProvinceId(provinceOptional.get().getId());
        district.setCode(request.getCode().trim());
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.CREATE_DISTRICT, request.toString());
        districtRepository.save(district);
        return true;
    }

    @Override
    public boolean edit(DistrictEditReqDTO request, HttpServletRequest httpServletRequest) {
        District district = districtRepository.getDistrictById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.District.NOT_FOUND, null);
        });
        if (!district.getCode().equals(request.getCode())) {
            districtRepository.findByCode(request.getCode ()).ifPresent(districtEdit -> {
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.District.DUPLICATE_CODE, null);
            });
        }
        District districtEdit = mapperDistrictRequestToDistrict(request, district);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.UPDATE_DISTRICT, request.toString());
        districtRepository.save(districtEdit);
        return true;
    }

    @Override
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) {
        Optional<District> districtOptional = districtRepository.getDistrictById(request.getId());
        if (districtOptional.isEmpty())
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.District.NOT_FOUND, null);
        List<Hotel> hotels = hotelRepository.findByDistrictId(districtOptional.get().getId());
        if (!hotels.isEmpty())
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.District.RELATED_HOTEL, null);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.DELETE_PROVINCE, request.toString());
        districtRepository.deleteById(request.getId());
        return true;
    }

    @Override
    public DistrictResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        District district = districtRepository.getDistrictById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.District.NOT_FOUND, null);
        });
        Optional<Province> province = provinceRepository.getProvinceById(district.getProvinceId());
        DistrictResDTO response = new DistrictResDTO();
        response.setId(district.getId());
        response.setName(district.getName());
        response.setCode(district.getCode());
        response.setDescription(district.getDescription());
        response.setProvinceName(province.orElse(new Province()).getName());
        response.setProvinceCode(province.orElse(new Province()).getCode());
        response.setProvinceId(province.orElse(new Province()).getId());
        return response;
    }

    @Override
    public Page<DistrictFilterReqDTO> getList(DistrictFilterResDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<DistrictFilterReqDTO> requestPage = districtRepository.filter(
                request.getName(),
                request.getCode(),
                StringUtils.isBlank(request.getProvinceId()) ? null : request.getProvinceId().trim(),
                pageable
        );
        logger.info("Found:{} district.", requestPage.getTotalElements());
        if (requestPage.getContent().isEmpty()) {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
        }
        return new PageImpl<>(requestPage.getContent(), pageable, requestPage.getTotalElements());
    }

    private District mapperDistrictRequestToDistrict(DistrictEditReqDTO request, District district) {
        district.setName(request.getName().trim());
        district.setDescription(Objects.nonNull(request.getDescription()) ? request.getDescription().trim() : null);
        district.setCode(request.getCode().trim());
        return district;
    }
}
