package org.codeworks.dsp.adx.bes.dto.response.report;

import org.codeworks.dsp.adx.bes.dto.BesRtbReport;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * bes rtb报告响应
 * Created by Luis on 2016/9/13.
 */
public class RtbReportBesResponse extends BaseBesResponse implements Serializable {

    private List<BesRtbReport> response;

    public List<BesRtbReport> getResponse() {
        return response;
    }

    public void setResponse(List<BesRtbReport> response) {
        this.response = response;
    }
}
