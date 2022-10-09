package org.codeworks.dsp.adx.bes.dto.response.creative;

import org.codeworks.dsp.adx.bes.dto.BesCreative;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * bes创意列表响应
 * Created by Luis on 2016/9/13.
 */
public class CreativeListBesResponse extends BaseBesResponse implements Serializable {

    private List<BesCreative> response;

    public List<BesCreative> getResponse() {
        return response;
    }

    public void setResponse(List<BesCreative> response) {
        this.response = response;
    }
}
