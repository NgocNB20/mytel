package mm.com.mytelpay.family.business.function;

import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.function.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.entities.Function;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Log4j2
@Service
public class FunctionServiceImpl extends AccountBaseBusiness implements FunctionService {

    @Autowired
    private PerRequestContextDto perRequestContextDto;

    public FunctionServiceImpl() {
        logger = LogManager.getLogger(FunctionServiceImpl.class);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean addFunction(FunctionAddReqDTO request, HttpServletRequest httpServletRequest) {
        this.validateNewFunction(request);
        try {
            String parentId = StringUtils.isEmpty(request.getParentId()) ? null : request.getParentId();
            List<String> trimmedEndPoints = request.getEndPoints().stream()
                    .map(String::trim)
                    .collect(Collectors.toList());
            Function newFunction = new Function();
            newFunction.setCode(request.getCode());
            newFunction.setName(request.getName());
            newFunction.setEndPoints(String.join(",", trimmedEndPoints));
            newFunction.setParentId(parentId);
            insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.CREATE_FUNCTION, objectMapper.writeValueAsString(request));
            functionRepository.save(newFunction);
            return true;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean updateFunction(FunctionEditReqDTO request, HttpServletRequest httpServletRequest) {
        try {
            Function function = functionRepository.findById(request.getId()).orElseThrow(() -> {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.FUNCTION_NOT_EXISTED, null);
            });
            insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.EDIT_FUNCTION, objectMapper.writeValueAsString(request));
            functionRepository.save(mapperFunctionRequestToFunction(request, function));
            return true;
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean deleteFunction(SimpleRequest request, HttpServletRequest httpServletRequest) {
        Function function = functionRepository.findById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.FUNCTION_NOT_EXISTED, null);
        });
        try {
            List<Function> childrenFunctions = functionRepository.findAllByParentId(function.getId());
            if (CollectionUtils.isNotEmpty(childrenFunctions)) {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.CANNOT_DELETE_THIS_FUNCTION_BECAUSE_IT_HAS_OTHER_FUNCTIONS_THAT_ARE_USED_AS_THE_PARENT, null);
            }
            roleHasFunctionRepository.deleteByFunctionId(function.getId());
            functionRepository.delete(function);
            return true;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    public Page<FunctionFilterResDTO> getList(FunctionFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<FunctionFilterResDTO> responses = functionRepository.filterFunction(request.getName(), request.getCode(), request.getParentId(), pageable);

        if(CollectionUtils.isEmpty(responses.getContent())){
            throw new BusinessEx(request.getRequestId() , AccountErrorCode.Role.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS , null);
        }
        List<FunctionFilterResDTO> list = responses.getContent();
        return new PageImpl<>(list , pageable , responses.getTotalElements());
    }

    @Override
    public FunctionDetailDto getDetail(String id, HttpServletRequest httpServletRequest) {
        Optional<Function> optionalFunction = functionRepository.findById(id);
        if (optionalFunction.isEmpty()) {
            return new FunctionDetailDto();
        }
        Function function = optionalFunction.get();
        String endPointsString = function.getEndPoints();
        List<String> endpointsList = StringUtils.isEmpty(endPointsString)
                ? Collections.emptyList()
                : Arrays.stream(endPointsString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        return FunctionDetailDto.builder()
                .id(function.getId())
                .code(function.getCode())
                .name(function.getName())
                .parentId(function.getParentId())
                .endPoints(endpointsList)
                .build();
    }

    private void validateNewFunction(FunctionAddReqDTO request) {
        if (isExistedFunctionCode(request.getCode())){
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.CODE_EXISTED, null);
        }
        if (StringUtils.isNotEmpty(request.getParentId()) && !isValidFunctionId(request.getParentId())){
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.FUNCTION_ID_NOT_EXISTED, null);
        }
    }

    private boolean isExistedFunctionCode(String code) {
        return functionRepository.findFirstByCode(code) != null;
    }

    private boolean isValidFunctionId(String id) {
        return functionRepository.findById(id).isPresent();
    }

    private Function mapperFunctionRequestToFunction(FunctionEditReqDTO request, Function function) {
        if (StringUtils.isNotBlank(request.getName())) {
            function.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getCode())) {
            if (!StringUtils.equals(function.getCode(), request.getCode()) && isExistedFunctionCode(request.getCode())) {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.CODE_EXISTED, null);
            }
            else {
                function.setCode(request.getCode());
            }
        }
        if (StringUtils.isNotBlank(request.getParentId())) {
            if (StringUtils.equals(request.getParentId(), function.getId())) {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.PARENT_ID_INVALID, null);
            }
            else if (isValidFunctionId(request.getParentId())) {
                function.setParentId(request.getParentId());
            }
            else {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Function.FUNCTION_ID_NOT_EXISTED, null);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getEndPoints())) {
            List<String> trimmedEndPoints = request.getEndPoints().stream()
                    .map(String::trim)
                    .collect(Collectors.toList());
            function.setEndPoints(String.join(",", trimmedEndPoints));
        }
        return function;
    }

}
