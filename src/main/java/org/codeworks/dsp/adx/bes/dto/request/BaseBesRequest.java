package org.codeworks.dsp.adx.bes.dto.request;

import java.io.Serializable;

/**
 * bes请求基础对象
 * Created by Luis on 2016/9/12.
 */
public class BaseBesRequest implements Serializable {

    private AuthHeader authHeader;

    public AuthHeader getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(AuthHeader authHeader) {
        this.authHeader = authHeader;
    }
}
