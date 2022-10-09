package org.codeworks.dsp.exception;

/**
 * 创意模块异常
 * Created by Luis on 2016/8/25.
 */
public class CreativesException extends RuntimeException implements Errors {

    private ErrorCodes errorCodes;

    private boolean isLog = false;

    private Object[] arguments;

    public CreativesException(ErrorCodes errorCodes) {
        super();
        this.errorCodes = errorCodes;
    }

    public CreativesException(ErrorCodes errorCodes, boolean isLog) {
        super();
        this.errorCodes = errorCodes;
        this.isLog = isLog;
    }

    public CreativesException(ErrorCodes errorCodes, Object[] arguments) {
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
