package mm.com.mytelpay.family.business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.account.dto.AccountIdDTO;
import mm.com.mytelpay.family.business.account.dto.BookingCarDTO;
import mm.com.mytelpay.family.business.common.StorageService;
import mm.com.mytelpay.family.business.file.FileService;
import mm.com.mytelpay.family.business.resttemplate.BookingRestTemplate;
import mm.com.mytelpay.family.config.keycloak.KeyCloakService;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.FileAccess;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.ActionLog;
import mm.com.mytelpay.family.model.entities.FileAttach;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.repo.*;
import mm.com.mytelpay.family.repo.file.FileRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AccountBaseBusiness extends BaseBusiness {

    @Autowired
    public ModelMapper mapper;

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public AccountInfoRepository accountInfoRepository;
    @Autowired
    public AccountRoleRepository accountRoleRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    public FunctionRepository functionRepository;
    @Autowired
    public RoleHasFunctionRepository roleHasFunctionRepository;
    @Autowired
    public KeyCloakService keyCloakService;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public ActionLogRepository actionLogRepository;

    @Autowired
    public ExcelCommon excelCommon;

    @Autowired
    public FileRepository fileRepository;

    @Autowired
    public FileService fileService;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${amazonProperties.basePathPublic}")
    public String urlPubic;

    @Value("${amazonProperties.basePath}")
    public String url;

    @Value("${external.booking.url}")
    private String bookingServiceBaseUrl;
    @Value("${external.booking.checkReq}")
    private String checkReq;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Qualifier("S3")
    @Autowired
    public StorageService storageService;

    @Autowired
    public BookingRestTemplate bookingRestTemplate;

    public ActionLog insertActionLog(String requestId, String accountId, ActionType actionType,
                                     Boolean isVerifiedOTP, String msisdn, String data) {
        ActionLog actionLog = new ActionLog();
        actionLog.setRequestId(requestId);
        actionLog.setAccountId(accountId);
        actionLog.setActionType(actionType);
        actionLog.setIsVerifiedOtp(isVerifiedOTP);
        actionLog.setMsisdn(msisdn);
        actionLog.setData(data);
        actionLog.setRequestTime(LocalDateTime.now());
        return actionLogRepository.save(actionLog);
    }

    public ActionLog insertActionLog(String requestId, String accountId, ActionType actionType, String data) {
        ActionLog actionLog = new ActionLog();
        actionLog.setRequestId(requestId);
        actionLog.setAccountId(accountId);
        actionLog.setActionType(actionType);
        actionLog.setData(data);
        actionLog.setRequestTime(LocalDateTime.now());
        return actionLogRepository.save(actionLog);
    }

    public ActionLog findActionById(String id) {
        Optional<ActionLog> actionLogOptional = actionLogRepository.findById(id);
        if (actionLogOptional.isEmpty())
            return null;
        else
            return actionLogOptional.get();
    }

    public ActionLog updateActionLog(ActionLog actionLog, String accountId) {
        actionLog.setIsVerifiedOtp(true);
        actionLog.setAccountId(accountId);
        return actionLogRepository.save(actionLog);
    }

    public void deleteFile(String objectId, ObjectType objectType) {
        try {
            List<FileAttach> fileAttaches = fileService.findImageByObjectIdAndType(objectId, objectType);
            if (!fileAttaches.isEmpty()) {
                fileRepository.deleteAllByObjectIdAndObjectType(objectId, objectType);
                fileAttaches.forEach(f -> storageService.delete(f.getFileName()));
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            logger.info("deleteFile fail");
        }
    }

    public void createFile(MultipartFile[] files, String id, ObjectType type) {
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
            logger.error(ExceptionUtils.getStackTrace(e));
            fileAttaches.forEach(f -> storageService.delete(f.getFileName()));
        }
    }

    public void createSingleFile(MultipartFile file, String id, ObjectType type) {
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
            logger.error(ExceptionUtils.getStackTrace(e));
            storageService.delete(fileAttach.getFileName());
        }
    }

    public List<BookingCarDTO> checkReqBookingCarOfDriver(String accountId, String bearerAuth){
        List<BookingCarDTO> bookingCarDTOS;
        HttpHeaders headers =  new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(bearerAuth);
        HttpEntity<AccountIdDTO> entity = new HttpEntity<>(new AccountIdDTO(accountId), headers);
        ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(bookingServiceBaseUrl.concat(checkReq), HttpMethod.POST, entity , CommonResponseDTO.class);
        bookingCarDTOS = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<List<BookingCarDTO>>() {});
        return bookingCarDTOS;
    }
}

