package org.codeworks.dsp.rtbCall.exception;

import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.exception.Errors;

/**
 * Created by benjaminkc on 16/10/23.
 */
public class RtbApiException extends RuntimeException implements Errors {
    private ErrorCodes errorCodes;

    private boolean isLog = false;

    private Object[] arguments;

    public RtbApiException(String message) {
        super(message);
    }

    public RtbApiException(ErrorCodes errorCodes) {
        super();
        this.errorCodes = errorCodes;
    }

    public RtbApiException(ErrorCodes errorCodes, boolean isLog) {
        super();
        this.errorCodes = errorCodes;
        this.isLog = isLog;
    }

    public RtbApiException(ErrorCodes errorCodes, Object[] arguments) {
        super();
        this.errorCodes = errorCodes;
        this.arguments = arguments;
    }


    @Override
    public ErrorCodes getErrorCodes() {
        return errorCodes;
    }

    @Override
    public boolean getIsLog() {
        return isLog;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

}
