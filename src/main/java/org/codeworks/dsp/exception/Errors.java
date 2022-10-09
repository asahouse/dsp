package org.codeworks.dsp.exception;

/**
 * Created by Luis on 2016/8/25.
 */
public interface Errors {

    ErrorCodes getErrorCodes();

    boolean getIsLog();

    Object[] getArguments();
}
