package mm.com.mytelpay.family.exception.validate;

import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.Objects;
import java.util.regex.Pattern;

@Documented
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Constraint(
        validatedBy = {FileExcelValidator.class}
)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileExcelRegex {
    String message() default "The file is not match regex";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class FileExcelValidator implements ConstraintValidator<FileExcelRegex, MultipartFile> {

    public boolean isValid(MultipartFile value, ConstraintValidatorContext cxt) {
        String regex = "^(xlsx|xls)$";
        String fieldName = String.valueOf(((ConstraintValidatorContextImpl) cxt).getConstraintViolationCreationContexts().get(0).getPath().getLeafNode());
        if (value.isEmpty() || Objects.requireNonNull(value.getOriginalFilename()).isBlank()){
            String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_IS_REQUIRED));
            throw new RequestEx(CommonException.Request.INPUT_IS_REQUIRED, message);
        }else{
            String extension = FilenameUtils.getExtension(value.getOriginalFilename());
            assert extension != null;
            if (!Pattern.matches(regex, extension.trim())) {
                String message = Util.replaceMessageCode(fieldName, Translator.toLocale(CommonException.Request.INPUT_INVALID));
                throw new RequestEx(CommonException.Request.INPUT_INVALID, message);
            }
        }
        return true;
    }
}
