package org.codeworks.dsp.rtbCall;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/23.
 */
@Component
public class RtbConstant implements Serializable{

    @Value("${rtb.api.accept}")
    public final String ACCEPT = "application/json;charset=utf-8";

    @Value("${rtb.api.contentType}")
    public final String CONTENT_TYPE = "application/json;charset=utf-8";

    @Value("${rtb.api.version}")
    public String VERSION;

    public String serverUrl;

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }




    public String getSYNC_MATERIAL_URL() {
        return serverUrl + "/v1/index/material";
    }

    public String getSYNC_CAMPAIGN_URL() {
        return serverUrl + "/v1/index/campaign";
    }

    public String getSYNC_ADVERTISER_URL() {
        return serverUrl + "/v1/index/advertiser";
    }


}
