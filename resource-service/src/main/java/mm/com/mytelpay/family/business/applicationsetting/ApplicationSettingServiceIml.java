package mm.com.mytelpay.family.business.applicationsetting;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.applicationsetting.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.ApplicationSetting;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.ApplicationSettingRepository;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;
import static mm.com.mytelpay.family.utils.Util.isValidJson;

@Service
public class ApplicationSettingServiceIml extends FamilyBaseBusiness implements ApplicationSettingService {

    @Autowired
    private ApplicationSettingRepository applicationSettingRepository;

    @Override
    public boolean create(AppSettingCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        checkKey(null, request.getKey(), request.getRequestId());
        ApplicationSetting applicationSetting = mapper.map(request, ApplicationSetting.class);
        applicationSettingRepository.save(applicationSetting);
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_TEMPLATE, objectMapper.writeValueAsString(request));
        return true;
    }

    public void checkKey(String oldKey, String newKey, String requestId) {
        if (!newKey.equals(oldKey) && applicationSettingRepository.existsKeyInApplicationSetting(newKey)) {
            logger.error("Key:{} is used", requestId);
            throw new BusinessEx(requestId, ResourceErrorCode.ApplicationSetting.KEY_IS_USED, null);
        }
    }

    @Override
    public boolean edit(AppSettingEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        ApplicationSetting applicationSetting = getApplicationSetting(request.getRequestId(), request.getId());
        applicationSettingRepository.save(mapperCarRequestToCar(request, applicationSetting));
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_TEMPLATE, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        getApplicationSetting(request.getRequestId(), request.getId());
        applicationSettingRepository.deleteById(request.getId());
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_TEMPLATE, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    public AppSettingDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        ApplicationSetting applicationSetting = getApplicationSetting(request.getRequestId(), request.getId());
        return mapper.map(applicationSetting, AppSettingDetailResDTO.class);
    }

    private ApplicationSetting getApplicationSetting(String requestId, String id) {
        return applicationSettingRepository.getAppSettingById(id).orElseThrow(() -> {
            throw new BusinessEx(requestId, ResourceErrorCode.ApplicationSetting.NOT_FOUND, null);
        });
    }

    @Override
    public Page<AppSettingFilterResDTO> getList(AppSettingFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<AppSettingFilterResDTO> responses = applicationSettingRepository.filterAppSetting(
                request.getKey(),
                StringUtils.isBlank(request.getStatus()) ? null : Status.valueOf(request.getStatus()),
                pageable);
        if (responses.getContent().isEmpty()) {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
        }
        logger.info("Found:{} template.", responses.getTotalElements());
        return new PageImpl<>(responses.getContent(), pageable, responses.getTotalElements());
    }

    @Override
    public AppSettingDetailResDTO getByKey(AppSettingGetKeyDTO request, HttpServletRequest httpServletRequest) {
        ApplicationSetting applicationSetting = applicationSettingRepository.getAppSettingByName(request.getKey()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.ApplicationSetting.NOT_FOUND, null);
        });
        return mapper.map(applicationSetting, AppSettingDetailResDTO.class);
    }

    @Override
    public List<LocalDate> getPublicHolidaysInYear(Integer year) {
        String publicHolidayKey = "public_holiday_" + year;
        String publicHolidayValueJson = applicationSettingRepository.getValueAppSettingByName(publicHolidayKey);
        if (StringUtils.isEmpty(publicHolidayValueJson)) {
            return Collections.emptyList();
        }
        return getHolidayListFromValueJson(publicHolidayValueJson);
    }

    public List<LocalDate> getHolidayListFromValueJson(String publicHolidayValueJson) {
        List<LocalDate> holidayList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        JSONObject jsonObject = new JSONObject(publicHolidayValueJson);
        for (String key : jsonObject.keySet()) {
            String value = jsonObject.getString(key);
            LocalDate holidayDate = LocalDate.parse(value, formatter);
            holidayList.add(holidayDate);
        }
        return holidayList;
    }


    @Override
    public AppSettingDetailResDTO getListReason(AppSettingGetKeyDTO request, HttpServletRequest httpServletRequest) {
        ApplicationSetting applicationSetting = applicationSettingRepository.getAppSettingByName(request.getKey()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.ApplicationSetting.NOT_FOUND, null);
        });
        AppSettingDetailResDTO res = mapper.map(applicationSetting, AppSettingDetailResDTO.class);
        String bsJson = applicationSetting.getValue();
        String lang = Util.getLanguage(httpServletRequest);
        if (StringUtils.isNotEmpty(bsJson) && isValidJson(bsJson) && StringUtils.isNotBlank(lang)) {
            JSONObject exploreObject = new JSONObject(bsJson);
            try {
                exploreObject.getJSONObject(lang);
            } catch (JSONException e) {
                res.setValue(exploreObject.getJSONArray(lang).toString());
            }
        }
        return res;
    }
    private ApplicationSetting mapperCarRequestToCar(AppSettingEditReqDTO request, ApplicationSetting applicationSetting) {
        if (StringUtils.isNotBlank(request.getKey())) {
            checkKey(applicationSetting.getKey(), request.getKey(), request.getRequestId());
            applicationSetting.setKey(request.getKey());
        }
        if (StringUtils.isNotBlank(request.getValue())) {
            applicationSetting.setValue(request.getValue());
        }
        if (request.getStatus() != null) {
            applicationSetting.setStatus(Status.valueOf(request.getStatus()));
        }
        applicationSetting.setDescription(request.getDescription());
        return applicationSetting;
    }

}
