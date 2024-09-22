package mm.com.mytelpay.family.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.bookingcar.dto.AccountReportDTO;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.common.StorageService;
import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.config.firebase.NotificationService;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.FileAccess;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.ActionLog;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.repo.ActionLogRepository;
import mm.com.mytelpay.family.repo.FileRepository;
import mm.com.mytelpay.family.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class BookingBaseBusiness extends BaseBusiness {

    @Autowired
    public ModelMapper mapper;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public ActionLogRepository actionLogRepository;

    @Autowired
    public NotificationService notificationService;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public PerRequestContextDto perRequestContextDto;

    @Autowired
    public ApplicationSettingCommonService applicationSettingCommonService;

    @Autowired
    public AccountRestTemplate accountRestTemplate;

    @Qualifier("S3")
    @Autowired
    public StorageService storageService;

    @Autowired
    public FileRepository fileRepository;

    @Value("${amazonProperties.basePathPublic}")
    public String urlPublic;

    @Value("${amazonProperties.basePath}")
    public String url;

    public void insertActionLog(String requestId, String accountId, ActionType actionType, String msisdn, String data) {
        ActionLog actionLog = new ActionLog();
        actionLog.setId(UUID.randomUUID().toString());
        actionLog.setRequestId(requestId);
        actionLog.setAccountId(accountId);
        actionLog.setActionType(actionType);
        actionLog.setMsisdn(msisdn);
        actionLog.setData(data);
        actionLog.setRequestTime(LocalDateTime.now());
        actionLogRepository.save(actionLog);
    }

    @Transactional
    public void createFile(MultipartFile file, String id, ObjectType type) {
        FileAttach fileAttach = new FileAttach();
        uploadAndCreateFile(file, id, type, fileAttach);
    }

    public boolean checkIsValidRole(RoleType roleType, AccountDTO account) {
        if (account == null || CollectionUtils.isEmpty(account.getRoles())) {
            return false;
        }
        return account.getRoles().stream()
                .anyMatch(role -> roleType.toString().equals(role.getCode()));
    }

    @Transactional
    public void updateFile(MultipartFile file, String id, ObjectType type) {
        FileAttach fileAttach = fileRepository.getFirstByObjectIdAndObjectType(id, type).orElse(new FileAttach());
        uploadAndCreateFile(file, id, type, fileAttach);
    }

    private void uploadAndCreateFile(MultipartFile file, String id, ObjectType type, FileAttach fileAttach) {
        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isBlank()) {
                fileAttach.setFileName(storageService.upload(file, type, FileAccess.PUBLIC));
                fileAttach.setObjectId(id);
                fileAttach.setObjectType(type);
                fileAttach.setUrl(storageService.getUrl() + fileAttach.getFileName());
                fileRepository.save(fileAttach);
                logger.info("Insert file: {} success", fileAttach.getFileName());
            }
        } catch (Exception e) {
            storageService.delete(fileAttach.getFileName());
            logger.error("Insert file fail", e);
            throw new BusinessEx(BookingErrorCode.FileAttach.CREATE_FILE_FAIL, null);
        }
    }

    public List<AccountReportDTO> getAccountReportDTOS(String phoneNumber, String requestId) {
        String phone = StringUtils.isBlank(phoneNumber) ? null : phoneNumber;
        return accountRestTemplate.listAccountReportDTO(phone, requestId, perRequestContextDto.getBearToken());
    }

    public void getListAccountIds(String requestId, List<String> listAccountId, String phone) {
        if (!StringUtils.isEmpty(phone)) {
            if (!phone.matches(Constants.NUMBER_REGEX)) {
                throw new BusinessEx(requestId, BookingErrorCode.BookingCar.PHONE_IS_INVALID, null);
            }
            List<AccountReportDTO> accountReportDTO = accountRestTemplate.listAccountReportDTO(phone, requestId, perRequestContextDto.getBearToken());
            for (AccountReportDTO accountId : accountReportDTO) {
                listAccountId.add(accountId.getAccountId());
            }
        }
    }

}

