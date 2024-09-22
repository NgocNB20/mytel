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

@Documented
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Constraint(
        validatedBy = {SizeRegexValidate.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeRegex {

    String message() default "MaxLength is not match regex";

    Class<?>[] groups() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    Class<? extends Payload>[] payload() default {};

}

class SizeRegexValidate implements ConstraintValidator<SizeRegex, Object> {

    private int min;
    private int max;

    public void initialize(SizeRegex constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    public boolean isValid(Object o, ConstraintValidatorContext cxt) {
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) cxt).getConstraintViolationCreationContexts().get(0).getPath());
        String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));
        if (o == null || o.toString().isEmpty()) {
            return true;
        }
        if (String.class.isAssignableFrom(o.getClass())) {
            int length = ((String) o).trim().length();
            if (length > max || length < min) {
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
            }
        }

        if (Number.class.isAssignableFrom(o.getClass())) {
            double value = ((Number) o).doubleValue();
            if (value < min || value > max) {
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
            }
        }

        return true;
    }
}