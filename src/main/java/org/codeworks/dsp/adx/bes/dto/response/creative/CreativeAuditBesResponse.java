package org.codeworks.dsp.adx.bes.dto.response.creative;

import org.codeworks.dsp.adx.bes.dto.BesCreativeAuditState;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * bes创意检查状态响应
 * Created by Luis on 2016/9/13.
 */
public class CreativeAuditBesResponse extends BaseBesResponse implements Serializable {

    private List<BesCreativeAuditState> response;

    public List<BesCreativeAuditState> getResponse() {
        return response;
    }

    public void setResponse(List<BesCreativeAuditState> response) {
        this.response = response;
    }
}
