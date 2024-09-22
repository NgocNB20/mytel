package mm.com.mytelpay.family.business.hotel;

import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.hotel.dto.HotelCreateReqDTO;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BookingEx;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.District;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.repository.DistrictRepository;
import mm.com.mytelpay.family.repository.HotelRepository;
import mm.com.mytelpay.family.repository.ProvinceRepository;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HotelValidators extends FamilyBaseBusiness {

    public HotelValidators() {
        logger = LogManager.getLogger(HotelValidators.class);
    }

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;

    public void validateRequestCreateHotel(HotelCreateReqDTO request) {
        this.validateRoleAllows(request.getRolesAllow());
        this.validateDuplicateHotelCode(request.getCode(), request.getRequestId());
        this.validateNotFoundDistrictId(request.getDistrictId(), request.getRequestId());
        this.validateNotFoundProvinceId(request.getProvinceId(), request.getRequestId());
        this.validateDistrictIsInProvince(request.getDistrictId(), request.getProvinceId(), request.getRequestId());
    }

    public void validateDuplicateHotelCode(String hotelCode, String requestId) {
        hotelRepository.findByCode(hotelCode).ifPresent(hotel -> {
            throw new BusinessEx(requestId, ResourceErrorCode.Hotel.CODE_IS_EXISTS);
        });
    }

    public void validateRoleAllows(List<String> roleAllows) {
        boolean containsInvalidRole = roleAllows.stream()
                .anyMatch(role -> !isValidRole(role));
        if (containsInvalidRole) {
            String fieldName = "rolesAllows";
            String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));
            throw new BookingEx(CommonException.Request.INPUT_INVALID, message);
        }
    }

    public boolean isValidRole(String roleCode) {
        try {
            RoleType.valueOf(roleCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void validateNotFoundProvinceId(String provinceId, String requestId) {
        Optional<Province> province = provinceRepository.findById(provinceId);
        if(province.isEmpty()) {
            throw new BusinessEx(requestId, ResourceErrorCode.Province.NOT_FOUND);
        }
    }

    public void validateNotFoundDistrictId(String districtId, String requestId) {
        Optional<District> district = districtRepository.findById(districtId);
        if(district.isEmpty()) {
            throw new BusinessEx(requestId, ResourceErrorCode.District.NOT_FOUND);
        }
    }

    public void validateDistrictIsInProvince(String districtId, String provinceId, String requestId) {
        District district = districtRepository.findById(districtId).orElseThrow(() -> new BusinessEx(requestId, ResourceErrorCode.District.NOT_FOUND));
        if (!StringUtils.equals(provinceId, district.getProvinceId())) {
            throw new BusinessEx(requestId, ResourceErrorCode.District.NOT_PART_OF_PROVINCE);
        }
    }

}
