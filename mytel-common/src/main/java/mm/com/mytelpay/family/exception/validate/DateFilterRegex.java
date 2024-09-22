package mm.com.mytelpay.family.exception.validate;

import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Documented
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Constraint(
        validatedBy = {DateFilterValidator.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFilterRegex {
    String message() default "Date is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
class DateFilterValidator implements ConstraintValidator<DateFilterRegex, String> {
    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext constraintValidatorContext) {
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) constraintValidatorContext).getConstraintViolationCreationContexts().get(0).getPath());
        if (StringUtils.isNotBlank(dateStr)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
            try {
                LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
            }
        }
        return true;
    }
}
