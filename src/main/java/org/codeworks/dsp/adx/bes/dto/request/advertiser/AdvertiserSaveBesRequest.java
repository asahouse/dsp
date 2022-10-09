package org.codeworks.dsp.adx.bes.dto.request.advertiser;

import org.codeworks.dsp.adx.bes.dto.BesAdvertiser;
import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;

import java.io.Serializable;
import java.util.List;

/**
 * bes创建/更新广告主请求
 * Created by Luis on 2016/9/12.
 */
public class AdvertiserSaveBesRequest extends BaseBesRequest implements Serializable {

    private List<BesAdvertiser> request;

    public AdvertiserSaveBesRequest() {
    }

    public AdvertiserSaveBesRequest(List<BesAdvertiser> request) {
        this.request = request;
    }

    public List<BesAdvertiser> getRequest() {
        return request;
    }

    public void setRequest(List<BesAdvertiser> request) {
        this.request = request;
    }
}
