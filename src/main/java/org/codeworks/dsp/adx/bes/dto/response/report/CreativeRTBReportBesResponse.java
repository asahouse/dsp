package org.codeworks.dsp.adx.bes.dto.response.report;

import org.codeworks.dsp.adx.bes.dto.BesCreativeRTBReport;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/11/24.
 */
public class CreativeRTBReportBesResponse extends BaseBesResponse implements Serializable {

    private List<BesCreativeRTBReport> response;

    public List<BesCreativeRTBReport> getResponse() {
        return response;
    }

    public void setResponse(List<BesCreativeRTBReport> response) {
        this.response = response;
    }
}
