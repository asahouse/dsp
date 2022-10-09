package org.codeworks.dsp.adx.bes.dto.response.advertiser;

import org.codeworks.dsp.adx.bes.dto.BesAdvertiser;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * bes广告主列表响应
 * Created by Luis on 2016/9/12.
 */
public class AdvertiserListBesResponse extends BaseBesResponse implements Serializable {

    private List<BesAdvertiser> response;

    public List<BesAdvertiser> getResponse() {
        return response;
    }

    public void setResponse(List<BesAdvertiser> response) {
        this.response = response;
    }
}
