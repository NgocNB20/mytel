package mm.com.mytelpay.family.business.car;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.car.dto.*;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.Car;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.CarRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountSettingDTO;
import mm.com.mytelpay.family.utils.AESencryptionUtil;
import mm.com.mytelpay.family.utils.ObjectMapperUtil;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.web.client.RestClientException;

@Log4j2
@AllArgsConstructor
@Service
public class CarServiceImpl extends FamilyBaseBusiness implements CarService {

    public CarServiceImpl() {
        logger = LogManager.getLogger(CarServiceImpl.class);
    }

    @Autowired
    private CarRepository carRepository;

    @Value("${external.bookingCar.checkCar}")
    private String checkCarOnTripUrl;

    @Autowired
    private AccountRestTemplate accountRestTemplate;
        
    @Override
    public CarDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        try {
            Car car = getCarInDb(request.getRequestId(), request.getId());
            CarDetailResDTO response = mapper.map(car, CarDetailResDTO.class);
            List<FileAttach> fileAttach = fileService.findImageByObjectIdAndType(car.getId(), ObjectType.CAR);
            List<FileResponse> listResponse = mapper.map(fileAttach, new TypeToken<List<FileResponse>>() {
            }.getType());
            response.setFile(listResponse);
            
            CarQRDataResDTO qrData = CarQRDataResDTO.builder()
                        .id(car.getId())
                        .name(car.getName())
                        .carType(car.getCarType())
                        .licensePlate(car.getLicensePlate())
                        .build();
            
            response.setQrString(AESencryptionUtil.encryptAES(qrData));
            return response;
        } catch (BusinessEx e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Get car id:{} fail", request.getId(), ex);
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Override
    public Page<CarFilterResDTO> getList(CarFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<CarFilterResDTO> responses = carRepository.filterCar(
                StringUtils.isBlank(request.getCarType()) ? null : CarType.valueOf(request.getCarType()),
                request.getName(),
                StringUtils.isBlank(request.getStatus()) ? null : Status.valueOf(request.getStatus()),
                pageable);
        
        responses.forEach(CarFilterResDTO::generateQRString);
        logger.info("Found:{} car.", responses.getTotalElements());
        if (responses.getContent().isEmpty()) {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.NO_DATA_FOUND, null);
        }
        return new PageImpl<>(responses.getContent(), pageable, responses.getTotalElements());
    }

    @Override
    @SneakyThrows
    @Transactional
    public boolean create(CarCreateReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        //check exits license plate
        checkLicensePlate(null, request.getLicensePlate(), request.getRequestId());
        //save car and actionLog
        addCar(request, files);
        return true;
    }

    @Override
    @SneakyThrows
    @Transactional
    public boolean edit(CarEditReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        //check car
        Car carInDb = getCarInDb(request.getRequestId(), request.getId());

        //update car
        updateCar(request, files, carInDb);
        return true;
    }

    @Override
    @SneakyThrows
    @Transactional
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        //check in db
        getCarInDb(request.getRequestId(), request.getId());

        //check car on booking car
        if (checkCarOnTrip(request.getId(), request.getRequestId(), perRequestContextDto.getBearToken())){
            throw new BusinessEx(request.getRequestId(), ResourceErrorCode.Car.CAR_IN_TRIP, null);
        }
        //insert actionLog, delete file,car
        deleteCar(request);
        return true;
    }

    @Override
    public List<CarReportDTO> getListCarReport(CarReportReqDTO carReportReqDTO, HttpServletRequest httpServletRequest) {
        return carRepository.getListCarDTO(carReportReqDTO.getCarId());
    }

    private Car getCarInDb(String requestId, String id) {
        return carRepository.getCarById(id).orElseThrow(() -> {
            logger.error("Car id:{} not found", id);
            throw new BusinessEx(requestId, ResourceErrorCode.Car.NOT_FOUND, null);
        });
    }

    private Car mapperCarRequestToCar(CarEditReqDTO request, MultipartFile[] files, Car car) {
        if (StringUtils.isNotBlank(request.getName())) {
            car.setName(request.getName());
        }

        if (StringUtils.isNotBlank(request.getColor())) {
            car.setColor(request.getColor());
        }

        if (StringUtils.isNotBlank(request.getModel())) {
            car.setModel(request.getModel());
        }

        if (StringUtils.isNotBlank(request.getLicensePlate())) {
            checkLicensePlate(car.getLicensePlate(), request.getLicensePlate(), request.getRequestId());
            car.setLicensePlate(request.getLicensePlate());
        }

        if (StringUtils.isNotBlank(request.getCarType())) {
            car.setCarType(CarType.valueOf(request.getCarType()));
        }

        if (StringUtils.isNotBlank(request.getStatus())) {
            car.setStatus(Status.valueOf(request.getStatus()));
        }

        if (request.getFileDeletes() != null) {
            deleteFileByIds(request.getFileDeletes(), request.getId(), ObjectType.CAR);
        }

        if (files != null) {
            createArrayFile(files, car.getId(), ObjectType.CAR);
        }

        return car;
    }

    public void checkLicensePlate(String name, String newName, String requestId) {
        if (!newName.equals(name) && carRepository.existsLicensePlate(newName)) {
            logger.error("LicensePlate:{} is used", requestId);
            throw new BusinessEx(requestId, ResourceErrorCode.Car.LICENSE_PLATE_IS_USED, null);
        }
    }

    @SneakyThrows
    @Transactional
    public void addCar(CarCreateReqDTO request, MultipartFile[] files) {
        Car carRequest = mapper.map(request, Car.class);
        carRequest.setId(UUID.randomUUID().toString());
        carRequest.setCarType(CarType.valueOf(request.getCarType()));
        Car carSave = carRepository.save(carRequest);
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_CAR, objectMapper.writeValueAsString(request));
        this.createArrayFile(files, carSave.getId(), ObjectType.CAR);
    }

    @SneakyThrows
    @Transactional
    public void updateCar(CarEditReqDTO request, MultipartFile[] files, Car carInDb) {
        insertActionLog(request.getRequestId(), null, ActionType.EDIT_CAR, objectMapper.writeValueAsString(request));
        Car car = mapperCarRequestToCar(request, files, carInDb);
        carRepository.save(car);
    }

    @SneakyThrows
    @Transactional
    public void deleteCar(SimpleRequest request) {
        carRepository.deleteById(request.getId());
        deleteFile(request.getId(), ObjectType.CAR);
        insertActionLog(request.getRequestId(), null, ActionType.DELETE_CAR, objectMapper.writeValueAsString(request));
    }

    @Override
    public List<CarDetailResDTO> getListCarForAssign(CarAssignReqDTO request, HttpServletRequest httpServletRequest) {
        List<CarDetailResDTO> response = new ArrayList<>();
        List<Car> carList;
        if (!request.getIds().isEmpty()) {
            carList = carRepository.getListCarForAssign(request.getIds());
        } else {
            carList = carRepository.findAllByStatusOrderByCreatedAtDesc(Status.ACTIVE);
        }
        if (!carList.isEmpty()) {
            for (Car c : carList) {
                SimpleRequest req = new SimpleRequest(c.getId(), request.getRequestId());
                response.add(getDetail(req, httpServletRequest));
            }
        }
        return response;
    }

    public boolean checkCarOnTrip(String carId, String requestId, String bearerAuth) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqEntity = new HttpEntity<>(new SimpleRequest(carId, requestId), headers);

            ResponseEntity<CommonResponseDTO> response = restTemplate.exchange(bookingServiceBaseUrl.concat(checkCarOnTripUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            if (ObjectUtils.isEmpty(response)) {
                return false;
            }
            return (boolean) Objects.requireNonNull(response.getBody()).getResult();
        } catch (RestClientException e) {
            return false;
        }
    }

    @SneakyThrows
    @Override
    public CarQRDataResDTO validQRData(CarQRReqDTO qrRequest) {
        
        AccountSettingDTO accountSetting = accountRestTemplate.getAccountSettings(perRequestContextDto.getCurrentAccountId(), qrRequest.getRequestId(), perRequestContextDto.getBearToken());

        logger.info("=============================================================");
        logger.info("accountsettings" + ObjectMapperUtil.toJsonString(accountSetting));
        logger.info("=============================================================");
//        if(!accountSetting.getCarRegistrationActive() || !Util.isMorthanOneDayFrom(accountSetting.getCarRegistrationTime())){
//            throw new BusinessEx(qrRequest.getRequestId(), ResourceErrorCode.Car.CAR_REGISTRATION_NOT_ACTIVE, null);
//        }
        
         if(!accountSetting.getCarRegistrationActive()){
            throw new BusinessEx(qrRequest.getRequestId(), ResourceErrorCode.Car.CAR_REGISTRATION_NOT_ACTIVE, null);
        }
        
        CarQRDataResDTO car = AESencryptionUtil.decryptAES(qrRequest.getQrData(), CarQRDataResDTO.class);
        
        if(Objects.isNull(car)){
            throw new BusinessEx(qrRequest.getRequestId(), ResourceErrorCode.Car.INVALID_QR, null);
        }

        if(carRepository.isExistsCar(car.getId(), car.getName(), car.getCarType(),
                                        car.getLicensePlate())){
            return car;
        } else {
            throw new BusinessEx(qrRequest.getRequestId(), ResourceErrorCode.Car.INVALID_QR, null);
        }
                        
    }
}
