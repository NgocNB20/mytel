package mm.com.mytelpay.family.exception.validate;

import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Documented
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Constraint(
        validatedBy = {EnumValidator.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumRegex {

    String message() default "The Enum is not match regex";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();

}

class EnumValidator implements ConstraintValidator<EnumRegex, Object> {

    private List<String> acceptedValues;

    @Override
    public void initialize(EnumRegex enumRegex) {
        acceptedValues = Stream.of(enumRegex.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cxt) {
        if (Objects.isNull(value)) {
            return true;
        }
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) cxt).getConstraintViolationCreationContexts().get(0).getPath().getLeafNode());
        String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));
        if (value instanceof List) {
            List<?> values = (List<?>) value;
            for (Object enumValue : values) {
                if (enumValue != null && !acceptedValues.contains(enumValue)){
                    throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
                }
            }
        }
        if (value instanceof String){
            if (StringUtils.isBlank((CharSequence) value)) return true;
            else if (!acceptedValues.contains(value)){
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);

            }
        }
        return true;
    }

}
