package mm.com.mytelpay.family.business.meal;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.meal.dto.*;

import javax.servlet.http.HttpServletRequest;

public interface MealService {

    MealDetailResDto getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean create(MealCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(MealEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest);

}
