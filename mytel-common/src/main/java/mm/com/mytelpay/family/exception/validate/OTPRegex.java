package mm.com.mytelpay.family.exception.validate;

import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.regex.Pattern;

@Documented
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Constraint(
        validatedBy = {OTPValidator.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface OTPRegex {
    String message() default "OTP is not match regex";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class OTPValidator implements ConstraintValidator<OTPRegex, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) cxt).getConstraintViolationCreationContexts().get(0).getPath());
        if (value == null || value.trim().isEmpty()) {
            String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_IS_REQUIRED));
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, message);
        } else {
            String regex = Constants.OTP_REGEX;
            if (Pattern.matches(regex, value.trim())) {
                return true;
            } else {
                String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
            }
        }
    }
}

