package org.codeworks.dsp.exception;

/**
 * 广告活动模块异常
 * Created by Luis on 2016/8/25.
 */
public class CampaignException extends RuntimeException implements Errors {

    private ErrorCodes errorCodes;

    private boolean isLog = false;

    private Object[] arguments;

    public CampaignException(ErrorCodes errorCodes) {
        super();
        this.errorCodes = errorCodes;
    }

    public CampaignException(ErrorCodes errorCodes, boolean isLog) {
        super();
        this.errorCodes = errorCodes;
        this.isLog = isLog;
    }

    public CampaignException(ErrorCodes errorCodes, Object[] arguments) {
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
