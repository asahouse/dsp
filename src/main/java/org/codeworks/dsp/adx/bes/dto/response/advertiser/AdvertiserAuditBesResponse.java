package org.codeworks.dsp.adx.bes.dto.response.advertiser;

import org.codeworks.dsp.adx.bes.dto.BesAdvertiserAuditState;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/17.
 */
public class AdvertiserAuditBesResponse extends BaseBesResponse implements Serializable {

    private List<BesAdvertiserAuditState> response;

    public List<BesAdvertiserAuditState> getResponse() {
        return response;
    }

    public void setResponse(List<BesAdvertiserAuditState> response) {
        this.response = response;
    }
}
