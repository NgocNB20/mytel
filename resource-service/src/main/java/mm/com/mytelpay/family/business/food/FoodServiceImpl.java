package mm.com.mytelpay.family.business.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.food.dto.*;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.model.Food;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.FoodRepository;
import mm.com.mytelpay.family.repository.MenuRepository;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class FoodServiceImpl extends FamilyBaseBusiness implements FoodService {

    public FoodServiceImpl() {
        logger = LogManager.getLogger(FoodServiceImpl.class);
    }

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public Page<FoodFilterResDTO> filter(FoodFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<FoodFilterResDTO> items = foodRepository.filterFood(
                StringUtils.isBlank(request.getType()) ? null : FoodType.valueOf(request.getType())
                , request.getName()
                , pageable);
        logger.info("Found:{} items.", items.getTotalElements());

        List<FoodFilterResDTO> resDTOS = items.getContent();
        List<String> ids = resDTOS.stream().map(FoodFilterResDTO::getId).collect(Collectors.toList());
        List<FileAttach> fileAttaches = fileRepository.getFileAttachByObjectIdInAndObjectType(ids, ObjectType.FOOD);

        Map<String, List<FileResponse>> listMap = new HashMap<>();
        for (FileAttach f:fileAttaches) {
            if(!listMap.containsKey(f.getObjectId())){
                listMap.put(f.getObjectId(), new ArrayList<>());
            }
            listMap.get(f.getObjectId()).add(mapper.map(f, FileResponse.class));
        }

        resDTOS.forEach(r -> r.setFiles(listMap.get(r.getId())));

        commonResponse.setResult(new PageImpl<>(resDTOS, pageable, items.getTotalElements()));
        return new PageImpl<>(items.getContent(), pageable, items.getTotalElements());
    }

    @Override
    public FoodDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        Food food = getFoodById(request.getId(), request.getRequestId());
        FoodDetailResDTO response = mapper.map(food, FoodDetailResDTO.class);
        List<FileAttach> fileAttach = fileService.findImageByObjectIdAndType(food.getId(), ObjectType.FOOD);
        List<FileResponse> listResponse = mapper.map(fileAttach, new TypeToken<List<FileResponse>>() {
        }.getType());
        response.setFiles(listResponse);
        return response;
    }

    private Food getFoodById(String foodId, String requestId) {
        return foodRepository.getFoodById(foodId).orElseThrow(() -> {
            throw new BusinessEx(requestId, ResourceErrorCode.Food.NOT_FOUND, null);
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean create(FoodCreateArrayReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        List<Food> foods = new ArrayList<>();
        String accountId = perRequestContextDto.getCurrentAccountId();
        validateCreateFood(request);
        request.getFoods().forEach(f -> {
            Food foodRequest = mapper.map(f, Food.class);
            foodRequest.setCreatedBy(accountId);
            Food foodSave = foodRepository.save(foodRequest);
            this.createFile(f.getFile(), foodSave.getId(), ObjectType.FOOD);
            foods.add(foodSave);
        });
        insertActionLog(request.getRequestId(), null, ActionType.CREATE_FOOD, objectMapper.writeValueAsString(foods));
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean edit(FoodEditReqDTO request, MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Food foodInDb = getFoodById(request.getId(), request.getRequestId());
        foodRepository.save(mapperItemRequestToItem(request, file, foodInDb));
        insertActionLog(request.getRequestId(), null, ActionType.EDIT_FOOD, objectMapper.writeValueAsString(request));
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        getFoodById(request.getId(), request.getRequestId());
        menuRepository.deleteByFoodId(request.getId());
        foodRepository.deleteById(request.getId());
        deleteFile(request.getId(), ObjectType.FOOD);
        insertActionLog(request.getRequestId(), null, ActionType.DELETE_FOOD, objectMapper.writeValueAsString(request));
        return true;
    }

    private Food mapperItemRequestToItem(FoodEditReqDTO request, MultipartFile file, Food food) {
        if (StringUtils.isNotBlank(request.getName())) {
            food.setName(request.getName());
        }
        if (Objects.nonNull(request.getType())) {
            food.setType(FoodType.valueOf(request.getType()));
        }
        if (file != null) {
            FileAttach fileAttach = fileRepository.findFirstByObjectIdAndAndObjectType(request.getId(), ObjectType.FOOD);
            if (Objects.nonNull(fileAttach)){
                updateFile(file, fileAttach);
            }else {
                createFile(file, food.getId(), ObjectType.FOOD);
            }
        }
        return food;
    }

    private void validateCreateFood(FoodCreateArrayReqDTO reqDTO){
        for (FoodCreateReqDTO f: reqDTO.getFoods()) {
            if (StringUtils.isBlank(f.getName())) {
                throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.name");
            }
            if (f.getName().length() > 255){
                throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, "parameter.invalid.name");
            }
            if (StringUtils.isBlank(f.getType())) {
                throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.type");
            }
            try {
                FoodType.valueOf(f.getType());
            } catch (Exception e) {
                throw new RequestEx(CommonException.Request.INPUT_INVALID, "parameter.invalid.type");
            }
            checkFile(f.getFile());
        }
    }

    private void checkFile(MultipartFile file){
        String regex = "^(jpeg|jpg|png)$";
        if (file.isEmpty() || Objects.requireNonNull(file.getOriginalFilename()).isBlank()) {
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.file");
        } else {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            assert extension != null;
            if (!Pattern.matches(regex, extension.trim())) {
                throw new RequestEx(CommonException.Request.INPUT_INVALID, "parameter.invalid.file");
            }
        }
    }
}
