package org.codeworks.dsp.adx.bes.dto.request;

import java.io.Serializable;

/**
 * 百度请求授权对象
 * Created by Luis on 2016/9/12.
 */
public class AuthHeader implements Serializable {

    private Integer dspId;

    private String token;

    public AuthHeader() {
    }

    public AuthHeader(Integer dspId, String token) {
        this.dspId = dspId;
        this.token = token;
    }

    public Integer getDspId() {
        return dspId;
    }

    public void setDspId(Integer dspId) {
        this.dspId = dspId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
