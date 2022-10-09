package org.codeworks.dsp.exception;

/**
 * Created by benjaminkc on 16/10/19.
 */
public class CategoryException extends RuntimeException implements Errors {

    private ErrorCodes errorCodes;

    private boolean isLog = false;

    private Object[] arguments;

    public CategoryException(ErrorCodes errorCodes) {
        super();
        this.errorCodes = errorCodes;
    }

    public CategoryException(ErrorCodes errorCodes, boolean isLog) {
        super();
        this.errorCodes = errorCodes;
        this.isLog = isLog;
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
