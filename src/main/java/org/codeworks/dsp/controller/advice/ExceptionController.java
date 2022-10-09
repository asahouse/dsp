package org.codeworks.dsp.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codeworks.dsp.exception.Errors;
import org.codeworks.dsp.model.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * 错误处理
 * Created by Luis on 2016/5/30.
 */
@ControllerAdvice
public class ExceptionController {

    private static final Logger log = LogManager.getLogger(ExceptionController.class);

    @Autowired
    private MessageSource messageSource;

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public Response runtimeEx(RuntimeException ex) {
        if (ex instanceof Errors) {
            Errors e = (Errors) ex;
            if (e.getIsLog())
                logException(ex);
            if (e.getArguments() == null)
                return Response.error(messageSource.getMessage(
                        e.getErrorCodes().getCode(), null, LocaleContextHolder.getLocale()));
            else
                return Response.error(messageSource.getMessage(
                        e.getErrorCodes().getCode(), e.getArguments(), LocaleContextHolder.getLocale()));
        } else {
            logException(ex);
            return Response.error();
        }
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response missingParameterEx(MissingServletRequestParameterException ex) {
        return Response.badRequest(String.format("%s: %s", BAD_REQUEST
                .getReasonPhrase(), ex.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public Response notFoundEx(NoHandlerFoundException ex) {
        return Response.resp(NOT_FOUND, String.format("%s: %s", HttpStatus.NOT_FOUND
                .getReasonPhrase(), ex.getMessage()));
    }

    private void logException(RuntimeException ex) {
        try (StringWriter sw = new StringWriter()) {
            ex.printStackTrace(new PrintWriter(sw));
            log.error(sw.toString());
        } catch (IOException e) {
            log.error(e);
        }
    }
}
