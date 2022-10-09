package org.codeworks.dsp.adx.bes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * bes api常量
 * Created by Luis on 2016/9/12.
 */
@Component
public class BaiduEsConstant implements Serializable{

    @Value("${bes.api.accept}")
    public final String ACCEPT = "application/json;charset=utf-8";

    @Value("${bes.api.contentType}")
    public final String CONTENT_TYPE = "application/json;charset=utf-8";

    @Value("${bes.dspId}")
    public int DSPID;

    @Value("${bes.token}")
    public String TOKEN;

    @Value("${bes.api.serverURL}")
    public String SERVER_URL;

    @Value("${bes.api.version}")
    public String VERSION;

    public String getCREATIVE_GETALL_URL() {
        return SERVER_URL + "/" + VERSION + "/creative/getAll";
    }

    public String getCREATIVE_GET_URL() {
        return SERVER_URL + "/" + VERSION + "/creative/get";
    }

    public String getCREATIVE_ADD_URL() {
        return SERVER_URL + "/" + VERSION + "/creative/add";
    }

    public String getCREATIVE_UPDATE_URL() {
        return SERVER_URL + "/" + VERSION + "/creative/update";
    }

    public String getCREATIVE_QUERY_AUDIT_STATE_URL() {
        return SERVER_URL + "/" + VERSION + "/creative/queryAuditState";
    }

    public String getCREATIVE_GET_TRADE_MODIFIED_URL() {
        return SERVER_URL + "/" + VERSION + "/creative/getTradeModified";
    }

    public String getADVERTISER_GETALL_URL() {
        return SERVER_URL + "/" + VERSION + "/advertiser/getAll";
    }

    public String getADVERTISER_GET_URL() {
        return SERVER_URL + "/" + VERSION + "/advertiser/get";
    }

    public String getADVERTISER_ADD_URL() {
        return SERVER_URL + "/" + VERSION + "/advertiser/add";
    }

    public String getADVERTISER_UPDATE_URL() {
        return SERVER_URL + "/" + VERSION + "/advertiser/update";
    }

    public String getADVERTISER_QUERY_QUALIFICATION_URL() {
        return SERVER_URL + "/" + VERSION + "/advertiser/queryQualification";
    }

    public String getREPORT_RTB_URL() {
        return SERVER_URL + "/" + VERSION + "/report/rtb";
    }

    public String getREPORT_CONSUME_URL() {
        return SERVER_URL + "/" + VERSION + "/report/consume";
    }

    public String getREPORT_ADVERTISER_URL() {
        return SERVER_URL + "/" + VERSION + "/report/advertiser";
    }

    public String getREPORT_CREATIVERTB_URL() {
        return SERVER_URL + "/" + VERSION + "/report/creativeRTB";
    }
}
