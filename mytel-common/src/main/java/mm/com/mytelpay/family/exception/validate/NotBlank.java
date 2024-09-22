package mm.com.mytelpay.family.exception.validate;

import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.Objects;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validatedBy = {NotBlankValidator.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {
    String message() default "Input ${fieldName} is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class NotBlankValidator implements ConstraintValidator<NotBlank, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext ctx) {
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) ctx).getConstraintViolationCreationContexts().get(0).getPath().getLeafNode());
        String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_IS_REQUIRED));
        if (o instanceof String && ((String) o).trim().isBlank()) {
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, message);
        }
        if (o instanceof MultipartFile && Objects.equals(((MultipartFile) o).getOriginalFilename(), "")) {
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, message);
        }
        if (o instanceof ArrayList && ((ArrayList<?>) o).isEmpty()) {
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, message);
        }
        if (Objects.isNull(o)) {
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, message);
        }
        return true;
    }
}
