package org.codeworks.dsp.exception;

/**
 * Created by benjaminkc on 16/12/11.
 */
public class BaiduStatisticsException extends RuntimeException implements Errors{

    private ErrorCodes errorCodes;

    private boolean isLog = false;

    private Object[] arguments;

    public BaiduStatisticsException(ErrorCodes errorCodes) {
        super();
        this.errorCodes = errorCodes;
    }

    public BaiduStatisticsException(ErrorCodes errorCodes, boolean isLog) {
        super();
        this.errorCodes = errorCodes;
        this.isLog = isLog;
    }
    public BaiduStatisticsException(ErrorCodes errorCodes, Object[] arguments) {
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
