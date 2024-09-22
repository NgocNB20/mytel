package mm.com.mytelpay.family.business.canteen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.canteen.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.Canteen;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.CanteenRepository;
import mm.com.mytelpay.family.repository.MealRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class CanteenServiceImpl extends FamilyBaseBusiness implements CanteenService {

    public CanteenServiceImpl() {
        logger = LogManager.getLogger(CanteenServiceImpl.class);
    }

    @Autowired
    private CanteenRepository canteenRepository;

    @Autowired
    private MealRepository mealRepository;

    @Override
    public Page<CanteenFilterResDTO> filter(CanteenFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<CanteenFilterResDTO> canteens = canteenRepository.filterCanteen(
                StringUtils.isBlank(request.getName()) ? null : request.getName(),
                StringUtils.isBlank(request.getUnitId()) ? null : request.getUnitId(),
                StringUtils.isBlank(request.getCode()) ? null : request.getCode()
                , pageable);
        addUnitNameForCanteen(canteens);
        logger.info("Found:{} canteens.", canteens.getTotalElements());
        return new PageImpl<>(canteens.getContent(), pageable, canteens.getTotalElements());
    }

    @Override
    public CanteenDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            CanteenDetailResDTO canteenDetailResDTO = new CanteenDetailResDTO();
            Canteen canteen = getCanteenById(request.getId(), request.getRequestId());
            return mapCanteenToCanteenDeatail(canteen, canteenDetailResDTO);
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Get canteen id:{} fail", request.getId(), ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean create(CanteenCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Canteen canteenRequest = new Canteen();
        mapCreateCanteenRequestToCanteen(request, canteenRequest);
        canteenRepository.save(canteenRequest);
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_CANTEEN, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean edit(CanteenEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Canteen canteenInDb = getCanteenById(request.getCanteenId(), request.getRequestId());
        canteenRepository.save(mapperCanteenRequestToCanteen(request, canteenInDb));
        insertActionLog(request.getRequestId(), null, ActionType.EDIT_CANTEEN, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Canteen canteenInDb = getCanteenById(request.getId(), request.getRequestId());
        if (!ObjectUtils.isEmpty(mealRepository.getMealByCanteenId(request.getId()))) {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Canteen.DELETED_FAIL, null);
        } else {
            canteenRepository.delete(canteenInDb);
        }
        insertActionLog(request.getRequestId(), null, ActionType.DELETE_CANTEEN, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    public List<CanteenFilterResDTO> getListByIds(CanteenGetIdsResDTO canteenGetIdsResDTO, HttpServletRequest httpServletRequest) {
        List<Canteen> list = canteenRepository.findAllByIdIn(canteenGetIdsResDTO.getIds());
        List<CanteenFilterResDTO> canteenFilterResDTOS = new ArrayList<>();
        if (Objects.nonNull(list)){
            canteenFilterResDTOS = objectMapper.convertValue(list, new TypeReference<>() {
            });
        }
        return canteenFilterResDTOS;
    }

    @Override
    public CanteenForChefDTO getCanteenForChef(CanteenForChefReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        CanteenForChefDTO canteenForChefDTO = new CanteenForChefDTO();
        if (!ObjectUtils.isEmpty(canteenRepository.findCanteenByUnitId(reqDTO.getUnitId()))) {
            Canteen canteen = canteenRepository.findCanteenByUnitId(reqDTO.getUnitId())
                    .orElseThrow(() -> {
                        throw new BusinessEx(reqDTO.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
                    });

            canteenForChefDTO.setCanteenId(canteen.getId());
        } else {
            throw new BusinessEx(reqDTO.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
        }
        return canteenForChefDTO;
    }

    @Override
    public checkCanteenForChefResDTO checkCanteenId(CheckCanteenForChefReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        if (canteenRepository.findById(reqDTO.getCanteenId()).isEmpty()) {
            throw new BusinessEx(reqDTO.getRequestId(), ResourceErrorCode.Canteen.NOT_FOUND, null);
        }
        checkCanteenForChefResDTO res = new checkCanteenForChefResDTO();
        Canteen canteen = canteenRepository.getCanteenByIdNoStatus(reqDTO.getCanteenId()).get();
        res.setCanteenId(canteen.getId());
        res.setCanteenName(canteen.getName());
        return res;
    }

    @Override
    public List<GetAllCanteenResDTO> getAllCanteen(HttpServletRequest httpServletRequest) {
        List<Canteen> canteens = new ArrayList<>();
        canteens = canteenRepository.findAll();
        List<GetAllCanteenResDTO> resDTOS = new ArrayList<>();
        for (Canteen canteen: canteens) {
            GetAllCanteenResDTO getAllCanteenResDTO = new GetAllCanteenResDTO();
            getAllCanteenResDTO.setCanteenId(canteen.getId());
            getAllCanteenResDTO.setCanteenName(canteen.getName());
            resDTOS.add(getAllCanteenResDTO);
        }
        return resDTOS;
    }

    public List<UnitDTO> getAllUnit() {
        return getAllUnit(perRequestContextDto.getBearToken());
    }
    private Canteen getCanteenById(String id, String requestId) {
        return canteenRepository.findById(id).orElseThrow(() -> {
            logger.error("Canteen id:{} not found", id);
            throw new BusinessEx(requestId, ResourceErrorCode.Canteen.NOT_FOUND, null);
        });
    }

    public UnitInfoReqDTO getUnitId (Page<CanteenFilterResDTO> canteens){
        List<String> unitId = new ArrayList<>();
        for (CanteenFilterResDTO cantennFilter: canteens.getContent()) {
            unitId.add(cantennFilter.getUnitId());
        }
        UnitInfoReqDTO unitIdInfo = new UnitInfoReqDTO();
        unitIdInfo.setUnitId(unitId);
        return unitIdInfo;
    }

    public void addUnitNameForCanteen ( Page<CanteenFilterResDTO> canteens){
        UnitInfoReqDTO unitInfoReq = getUnitId(canteens);
        List<UnitDTO> unitDTOS;
        unitDTOS = getListUnit(unitInfoReq.getUnitId(),unitInfoReq.getRequestId(), perRequestContextDto.getBearToken());
        for (CanteenFilterResDTO canteenFilter: canteens.getContent()) {
            for (UnitDTO unitDTO: unitDTOS) {
                if(unitDTO.getId().equals(canteenFilter.getUnitId())){
                    canteenFilter.setUnit(unitDTO.getName());
                }
            }
        }
    }
    private Canteen mapperCanteenRequestToCanteen(CanteenEditReqDTO request, Canteen canteen) {
        if ("0".equals(request.getNumberSeats())){
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Canteen.NUMBERSEAT, null);
        }
        if (!StringUtils.isEmpty(request.getCode())) {
            if (!request.getCode().equals(canteen.getCode()) && !ObjectUtils.isEmpty(canteenRepository.findByCode(request.getCode()))) {
                throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Canteen.IS_EXIST, null);
            } else {
                canteen.setCode(request.getCode());
            }
        }
        if (StringUtils.isNotBlank(request.getName())) {
            canteen.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getNumberSeats())) {
            canteen.setSeats(Integer.valueOf(request.getNumberSeats()));
        }
        canteen.setAddress(request.getAddress());
        canteen.setDescription(request.getDescription());
        return canteen;
    }

    private void mapCreateCanteenRequestToCanteen(CanteenCreateReqDTO request, Canteen canteen) {
        UnitReqDTO unitReqDTO = new UnitReqDTO();
        unitReqDTO.setId(request.getUnit());
        if ("0".equals(request.getNumberSeats())){
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Canteen.NUMBERSEAT, null);
        }
        if (!ObjectUtils.isEmpty(canteenRepository.findByCode(request.getCode()))) {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Canteen.IS_EXIST, null);
        }
        UnitDTO unitDTO = getUnitInfo(unitReqDTO.getId(), unitReqDTO.getRequestId(), perRequestContextDto.getBearToken());
        if (ObjectUtils.isEmpty(unitDTO)) {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Unit.NOT_FOUND, null);
        }
        if (!ObjectUtils.isEmpty(canteenRepository.findByUnitId(request.getUnit()))) {
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Unit.UNIT_ALREADY_EXISTS_CANTEEN, null);
        }
        canteen.setName(request.getName());
        canteen.setCode(request.getCode());
        canteen.setUnitId(request.getUnit());
        canteen.setSeats(Integer.valueOf(request.getNumberSeats()));
        canteen.setAddress(request.getAddress());
        canteen.setDescription(request.getDescription());
    }

    private CanteenDetailResDTO mapCanteenToCanteenDeatail(Canteen canteen, CanteenDetailResDTO canteenDetailResDTO){
        canteenDetailResDTO.setId(canteen.getId());
        canteenDetailResDTO.setCode(canteen.getCode());
        canteenDetailResDTO.setName(canteen.getName());
        canteenDetailResDTO.setSeats(canteen.getSeats());
        canteenDetailResDTO.setAddress(canteen.getAddress());
        canteenDetailResDTO.setDescription(canteen.getDescription());
        canteenDetailResDTO.setUnitId(canteen.getUnitId());
        List<UnitDTO> unitDTOS = getAllUnit();
        for (UnitDTO unitDTO: unitDTOS) {
            if(canteen.getUnitId().equals(unitDTO.getId())){
                canteenDetailResDTO.setUnitName(unitDTO.getName());
            }
        }
        return canteenDetailResDTO;
    }
}
