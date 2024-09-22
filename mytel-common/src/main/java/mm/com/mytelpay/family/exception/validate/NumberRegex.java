package mm.com.mytelpay.family.exception.validate;

import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.RequestEx;
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
        validatedBy = {NumberValidator.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberRegex {
    String message() default "Input ${fieldName} is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class NumberValidator implements ConstraintValidator<NumberRegex, String> {

    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) cxt).getConstraintViolationCreationContexts().get(0).getPath());
        String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));

        if (value == null || value.equals("")) {
            return true;
        } else {
            String regex = "\\d+";
            if (Pattern.matches(regex, value.trim())) {
                return true;
            } else {
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
            }
        }
    }
}
