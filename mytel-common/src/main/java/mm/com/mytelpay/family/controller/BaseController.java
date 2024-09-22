package mm.com.mytelpay.family.controller;

import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.utils.Translator;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

public abstract class BaseController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    public CommonResponseDTO generateDefaultResponse(String requestId, Object result, Object... objects) {
        CommonResponseDTO commonResponse = new CommonResponseDTO("00000", Translator.toLocale("00000"), result);
        commonResponse.setRequestId(requestId);
        return commonResponse;
    }
}
