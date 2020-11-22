package com.kakaopay.shinch.spread.controller;

import com.google.common.collect.Lists;
import com.kakaopay.shinch.spread.exception.NoContentException;
import com.kakaopay.shinch.spread.exception.SpreadConditionException;
import com.kakaopay.shinch.spread.exception.SpreadConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    //204
    @ExceptionHandler(NoContentException.class)
    public void noContent(HttpServletResponse response, NoContentException ex) {
        response.setStatus(HttpStatus.NO_CONTENT.value());
        log.warn("NO_CONTENT : {}", ex.getMessage());
    }

    //400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void badRequest(HttpServletResponse response, HttpMessageNotReadableException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("ERROR Handler", ex);
    }

    //400
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public List<String> badRequest(HttpServletResponse response, ServletRequestBindingException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("ERROR Handler", ex);
        List<String> errors = Lists.newArrayList();
        errors.add(ex.getMessage());
        return errors;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public List<String> badRequest(HttpServletResponse response, MethodArgumentTypeMismatchException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("ERROR Handler", ex);
        List<String> errors = Lists.newArrayList();
        errors.add(ex.getName() + " 형식이 잘못 되었습니다.");
        return errors;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public List<String> badRequest(HttpServletResponse response, MethodArgumentNotValidException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        List<String> errors = Lists.newArrayList();
        for ( ObjectError errorObj : ex.getBindingResult().getAllErrors() ) {
            errors.add(errorObj.getDefaultMessage());
        }
        log.error("ERROR Handler", ex);

        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public List<String> badRequest(HttpServletResponse response, ConstraintViolationException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("ERROR Handler", ex);
        List<String> errors = Lists.newArrayList();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations ) {
            errors.add(violation.getMessage());
        }
        return errors;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public List<String> badRequest(HttpServletResponse response, HttpMediaTypeNotAcceptableException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("ERROR Handler", ex);
        List<String> errors = Lists.newArrayList();
        errors.add(ex.getMessage());
        return errors;
    }

    @ExceptionHandler(SpreadConditionException.class)
    @ResponseBody
    public List<String> badRequest(HttpServletResponse response, SpreadConditionException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("ERROR Handler", ex);
        List<String> errors = Lists.newArrayList();
        errors.add(ex.getMessage());
        return errors;
    }

    //401 Unauthorized
    //402 Payment Required
    //403 Forbidden
    //404
    @ExceptionHandler(NoHandlerFoundException.class)
    public void notFound(HttpServletResponse response, HttpServletRequest request, NoHandlerFoundException ex) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        if ( request.getRequestURI().startsWith("/api/") ) {
            log.error("ERROR Handler", ex);
        }
    }
    //405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void methodNotAllowed(HttpServletResponse response, HttpRequestMethodNotSupportedException ex) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        log.error("ERROR Handler", ex);
    }
    //406 Not Acceptable
    //407 Proxy Authentication Required
    //408 Request Timeout
    //409 Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String dbDuplicateError(HttpServletResponse response, DataIntegrityViolationException ex) {
        response.setStatus(HttpStatus.CONFLICT.value());
        log.error("ERROR Handler", ex);
        return ex.getMessage();
    }

    @ExceptionHandler(SpreadConflictException.class)
    public String spreadConflictError(HttpServletResponse response, SpreadConflictException ex) {
        response.setStatus(HttpStatus.CONFLICT.value());
        log.error("ERROR Handler", ex);
        return ex.getMessage();
    }
    //410 Gone
    //411 Length Required
    //412 Precondition Failed
    //413 Request Entity Too Large
    //414 Request-URI Too Long
    //415
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public void unsupportedMediaType(HttpServletResponse response, HttpMediaTypeNotSupportedException ex) {
        response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        log.error("ERROR Handler", ex);
    }

    //416 Requested range not satisfiable
    //417 Expectation Failed

    //500 Internal server error
    @ExceptionHandler(Exception.class)
    public void internalServerError(HttpServletResponse response, Exception ex) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        log.error("ERROR Handler", ex);
    }

    //501 Not implemented
    //502 Bad Gateway
    //503 Service Unavailable
    //504 Gateway Timeout
    //505 HTTP Version Not Supported

}
