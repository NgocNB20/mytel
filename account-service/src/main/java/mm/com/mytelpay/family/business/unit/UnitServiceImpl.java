package mm.com.mytelpay.family.business.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.unit.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.entities.Unit;
import mm.com.mytelpay.family.repo.UnitRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;


@Service
public class UnitServiceImpl extends AccountBaseBusiness implements UnitService {

    public UnitServiceImpl() {
        logger = LogManager.getLogger(UnitServiceImpl.class);
    }

    @Autowired
    private PerRequestContextDto perRequestContextDto;

    @Autowired
    private UnitRepository unitRepository;

    @Override
    public Page<UnitFilterResDTO> filter(UnitFilterReqDTO request, HttpServletRequest httpServletRequest) {
        try {
            Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
            Page<UnitFilterResDTO> units = unitRepository.filterUnit(
                    request.getName(),
                    request.getCode()
                    , pageable);
            logger.info("Found:{} units.", units.getTotalElements());
            if (units.getContent().isEmpty()) {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.EMPTY, null);
            }
            return new PageImpl<>(units.getContent(), pageable, units.getTotalElements());
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    public List<UnitFilterResDTO> getAll(HttpServletRequest httpServletRequest) {
        return unitRepository.getAllWithoutPagination();
    }

    @Override
    public UnitDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            Unit unit = unitRepository.getUnitById(request.getId()).orElseThrow(() -> {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.NOT_FOUND, null);
            });
            return mapper.map(unit, UnitDetailResDTO.class);

        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @SneakyThrows
    public boolean create(UnitCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Optional<Unit> unitInDb = unitRepository.getUnitByCode(request.getCode());
        if (unitInDb.isPresent()) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.IS_EXIST, null);
        }
        Unit unitRequest = mapper.map(request, Unit.class);
        unitRepository.save(unitRequest);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.CREATE_UNIT, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @SneakyThrows
    public boolean edit(UnitEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Unit unitInDb = unitRepository.getUnitById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.NOT_FOUND, null);
        });
        if (!unitInDb.getCode().equals(request.getCode()) && unitRepository.findUnitByCode(request.getCode()).isPresent()) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.IS_EXIST, null);
        }
        unitRepository.save(mapperUnitRequestToUnit(request, unitInDb));
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.EDIT_UNIT, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @SneakyThrows
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) {
        if (unitRepository.getUnitById(request.getId()).isEmpty()) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.NOT_FOUND, null);
        }
        if (unitRepository.existsUserInUnit(request.getId())){
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.CANNOT_DELETE, null);
        }
        unitRepository.deleteById(request.getId());
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.DELETE_UNIT, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    public UnitForCanteenDTO isUnitIdExisted(UnitForCanteenReqDTO reqDTO) {
        UnitForCanteenDTO unitForCanteenDTO = new UnitForCanteenDTO();
        Optional<Unit> unit = unitRepository.findById(reqDTO.getId());
        if (unit.isEmpty()){
            throw new BusinessEx(reqDTO.getRequestId() , AccountErrorCode.Unit.NOT_FOUND, null);
        }else {
            unitForCanteenDTO.setId(unit.get().getId());
            unitForCanteenDTO.setName(unit.get().getName());
            unitForCanteenDTO.setCode(unit.get().getCode());
        }
        return unitForCanteenDTO;
    }

    @Override
    public List<UnitForCanteenDTO> getListUnit(UnitInfoReqDTO reqDTO) {
        return unitRepository.getListUnit(reqDTO.getUnitId());
    }

    @Override
    public UnitForChefDTO getUnitForChef(UnitForChefReqDTO reqDTO) {
        UnitForChefDTO unit = new UnitForChefDTO();
        if (!ObjectUtils.isEmpty(accountRepository.findById(reqDTO.getAccountId()))) {
            Optional<Account> account = accountRepository.findById(reqDTO.getAccountId());
            unit.setUnitId(account.map(Account::getUnitId).orElse(null));
        } else {
            throw new BusinessEx(reqDTO.getRequestId(), AccountErrorCode.Unit.EMPTY, null);
        }
        return unit;
    }

    private Unit mapperUnitRequestToUnit(UnitEditReqDTO request, Unit unit) {
        if (StringUtils.isNotBlank(request.getName())) {
            unit.setName(request.getName());
        }
        if (Objects.nonNull(request.getCode())) {
            unit.setCode(request.getCode());
        }
        return unit;
    }
}
