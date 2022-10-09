package org.codeworks.dsp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 公共功能
 * Created by Luis on 2016/8/24.
 */
public abstract class AbstractController {

    @Autowired
    private MessageSource messageSource;

    protected String getMessage(MessageSourceResolvable messageSourceResolvable) {
        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    protected String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
