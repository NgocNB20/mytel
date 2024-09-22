package mm.com.mytelpay.family.business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.canteen.dto.UnitDTO;
import mm.com.mytelpay.family.business.canteen.dto.UnitInfoReqDTO;
import mm.com.mytelpay.family.business.canteen.dto.UnitReqDTO;
import mm.com.mytelpay.family.business.common.StorageService;
import mm.com.mytelpay.family.business.file.FileService;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.FileAccess;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.ActionLog;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repository.ActionLogRepository;
import mm.com.mytelpay.family.repository.FileRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class FamilyBaseBusiness extends BaseBusiness {
    @Autowired
    public ModelMapper mapper;

    @Autowired
    public ObjectMapper objectMapper;

    @Value("${amazonProperties.basePathPublic}")
    public String urlPublic;

    @Value("${amazonProperties.basePath}")
    public String url;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${external.account.url}")
    private String accountServiceBaseUrl;

    @Value("${external.unit.checkUnit}")
    private String getUnitInfo;

    @Qualifier("S3")
    @Autowired
    public StorageService storageService;

    @Autowired
    public FileRepository fileRepository;

    @Autowired
    public FileService fileService;

    @Autowired
    public ExcelCommon excelCommon;

    @Value("${external.unit.getListUnit}")
    private String getListUnit;

    @Value("${external.unit.getAllUnit}")
    private String getAllUnit;
    @Value("${external.booking.url}")
    public String bookingServiceBaseUrl;

    @Autowired
    public PerRequestContextDto perRequestContextDto;

    public static final String EXCEL_TYPE = ".xlsx";

    public static final String DATE_FORMAT_NAME_EXCEL = "yyyy-MM-dd_HH_mm";

    @Transactional
    public void createArrayFile(MultipartFile[] files, String id, ObjectType type) {
        List<FileAttach> fileAttaches = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                FileAttach fileAttach = new FileAttach();
                if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isBlank()) {
                    fileAttach.setFileName(storageService.upload(file, type, FileAccess.PUBLIC));
                    fileAttach.setObjectId(id);
                    fileAttach.setObjectType(type);
                    fileAttach.setUrl(storageService.getUrl() + fileAttach.getFileName());
                    fileAttaches.add(fileAttach);
                }
            }
            fileRepository.saveAll(fileAttaches);
        } catch (Exception e) {
            fileAttaches.forEach(f -> storageService.delete(f.getFileName()));
        }
    }

    @Transactional
    public void createFile(MultipartFile file, String id, ObjectType type) {
        FileAttach fileAttach = new FileAttach();
        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isBlank()) {
                fileAttach.setFileName(storageService.upload(file, type, FileAccess.PUBLIC));
                fileAttach.setObjectId(id);
                fileAttach.setObjectType(type);
                fileAttach.setUrl(storageService.getUrl() + fileAttach.getFileName());
            }
            fileRepository.save(fileAttach);
        } catch (Exception e) {
            storageService.delete(fileAttach.getFileName());
        }
    }

    @Transactional
    public void updateFile(MultipartFile file, FileAttach fileAttach) {
        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isBlank()) {
                fileAttach.setFileName(storageService.upload(file, fileAttach.getObjectType(), FileAccess.PUBLIC));
                fileAttach.setUrl(storageService.getUrl() + fileAttach.getFileName());
            }
            fileRepository.save(fileAttach);
        } catch (Exception e) {
            storageService.delete(fileAttach.getFileName());
        }
    }

    public void deleteFile(String objectId, ObjectType objectType) {
        try {
            List<FileAttach> fileAttaches = fileService.findImageByObjectIdAndType(objectId, objectType);
            if (!fileAttaches.isEmpty()) {
                fileRepository.deleteAllByObjectIdAndObjectType(objectId, objectType);
                fileAttaches.forEach(f -> storageService.delete(f.getFileName()));
            }
        } catch (Exception e) {
            logger.info("deleteFile fail");
        }
    }

    public void deleteFileByIds(List<Long> ids, String objectId, ObjectType objectType) {
        try {
            List<FileAttach> fileAttaches = fileRepository.getFileAttachByIdInAndObjectIdAndObjectType(ids, objectId, objectType);
            if (!fileAttaches.isEmpty()) {
                fileRepository.deleteFileAttachByIdInAndObjectIdAndObjectType(ids, objectId, objectType);
                fileAttaches.forEach(f -> storageService.delete(f.getFileName()));
            }
        } catch (Exception e) {
            logger.info("deleteFile fail");
        }
    }

    @Autowired
    public ActionLogRepository actionLogRepository;

    public ActionLog insertActionLog(String requestId, String accountId, ActionType actionType, String data) {
        ActionLog actionLog = new ActionLog();
        actionLog.setId(UUID.randomUUID().toString());
        actionLog.setRequestId(requestId);
        actionLog.setAccountId(accountId);
        actionLog.setActionType(actionType);
        actionLog.setData(data);
        actionLog.setRequestTime(LocalDateTime.now());
        return actionLogRepository.save(actionLog);
    }

    public UnitDTO getUnitInfo(String id, String requestId, String bearerAuth) {
        UnitDTO unitDTO;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<UnitReqDTO> reqEntity = new HttpEntity<>(new UnitReqDTO(id), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getUnitInfo), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Unit.NOT_FOUND, null);
            }
            unitDTO = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), UnitDTO.class);
            if (ObjectUtils.isEmpty(unitDTO)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Unit.NOT_FOUND, null);
            }
        } catch (Exception e) {
            throw new BusinessEx(requestId, ResourceErrorCode.Unit.NOT_FOUND, null);
        }
        return unitDTO;
    }

    public List<UnitDTO> getListUnit(List<String> unitId, String requestId, String bearerAuth) {
        List<UnitDTO> unitDTOS;
        try {
            if (ObjectUtils.isEmpty(unitId)) {
                unitId = null;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<UnitInfoReqDTO> entity = new HttpEntity<>(new UnitInfoReqDTO(unitId), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getListUnit), HttpMethod.POST, entity, CommonResponseDTO.class);
            if (Objects.isNull(commonResponse.getBody()) || Objects.isNull(Objects.requireNonNull(commonResponse.getBody()).getResult())) {
                throw new BusinessEx(requestId, ResourceErrorCode.Unit.NOT_FOUND, null);
            }
            unitDTOS = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {});
            return unitDTOS;
        } catch (Exception e) {
            throw new BusinessEx(requestId, ResourceErrorCode.Unit.NOT_FOUND, null);
        }
    }

    public List<UnitDTO> getAllUnit(String bearerAuth) {
        List<UnitDTO> unitDTOS;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(bearerAuth);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Object> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getAllUnit), HttpMethod.GET, entity, Object.class);
        unitDTOS = objectMapper.convertValue(commonResponse.getBody(), new TypeReference<>() {
        });
        return unitDTOS;
    }

    public void createHeaderExcel(Sheet sheet, int length, String[] headerImport) {
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headerImport[col]);
        }
    }
}

