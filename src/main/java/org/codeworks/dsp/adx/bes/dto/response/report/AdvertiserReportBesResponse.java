package org.codeworks.dsp.adx.bes.dto.response.report;

import org.codeworks.dsp.adx.bes.dto.BesAdvertiserReport;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/11/24.
 */
public class AdvertiserReportBesResponse extends BaseBesResponse implements Serializable {

    private List<BesAdvertiserReport> response;

    public List<BesAdvertiserReport> getResponse() {
        return response;
    }

    public void setResponse(List<BesAdvertiserReport> response) {
        this.response = response;
    }
}
