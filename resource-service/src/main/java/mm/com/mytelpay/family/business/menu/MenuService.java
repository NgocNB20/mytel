package mm.com.mytelpay.family.business.menu;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.menu.dto.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface MenuService {

    boolean create(MenuCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(MenuEditReqDto request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest);

    Page<MenuFilterResDto> filter(MenuFilterReqDto request, HttpServletRequest httpServletRequest);

    MenuDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);
}
