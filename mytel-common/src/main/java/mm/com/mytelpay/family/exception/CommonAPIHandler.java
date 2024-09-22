package mm.com.mytelpay.family.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.exception.error.BookingEx;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.ErrorCommon;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.logging.RequestUtils;
import mm.com.mytelpay.family.utils.Translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.hibernate.QueryException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.crypto.BadPaddingException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

@RestControllerAdvice
public class CommonAPIHandler {

    public static final Logger logger = LogManager.getLogger(CommonAPIHandler.class.getSimpleName());

    public CommonAPIHandler() {
    }

    @ExceptionHandler({Exception.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorExceptionMessage handleAllException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), "999", "Internal server error", new Object[0][]);
    }

    @ExceptionHandler({IndexOutOfBoundsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorExceptionMessage outException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), "999", "Wrong convert data", new Object[0][]);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, BadPaddingException.class, JsonProcessingException.class, HttpMessageNotReadableException.class,
            IllegalArgumentException.class, IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorExceptionMessage validateException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), "999", "Wrong request", new Object[0][]);
    }

    @ExceptionHandler({BindException.class, QueryException.class, InvalidDataAccessApiUsageException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorExceptionMessage handleBindException (Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), "999", "Wrong request");
    }

    @ExceptionHandler({RequestEx.class, BusinessEx.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorExceptionMessage handleException(ErrorCommon ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), ex.getErrorCode(), Translator.toLocale(ex.getMessage()), null);
    }

    @ExceptionHandler({ValidationException.class, BookingEx.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorExceptionMessage inputRequired(ErrorCommon ex, WebRequest request) {
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), ex.getErrorCode(), ex.getMessage(), new Object[0][]);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, SizeLimitExceededException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorExceptionMessage handleUploadFileException (Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return new ErrorExceptionMessage(RequestUtils.currentRequestId(), CommonException.File.FILE_SIZE_EXCEEDS_LIMIT, Translator.toLocale(CommonException.File.FILE_SIZE_EXCEEDS_LIMIT), new Object[0][]);
    }

}
