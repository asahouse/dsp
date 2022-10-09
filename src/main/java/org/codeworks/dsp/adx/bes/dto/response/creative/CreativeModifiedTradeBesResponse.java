package org.codeworks.dsp.adx.bes.dto.response.creative;

import org.codeworks.dsp.adx.bes.dto.BesCreativeTradeModified;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * bes创意行业修正响应
 * Created by Luis on 2016/9/13.
 */
public class CreativeModifiedTradeBesResponse extends BaseBesResponse implements Serializable {

    private List<BesCreativeTradeModified> response;

    public List<BesCreativeTradeModified> getResponse() {
        return response;
    }

    public void setResponse(List<BesCreativeTradeModified> response) {
        this.response = response;
    }
}
