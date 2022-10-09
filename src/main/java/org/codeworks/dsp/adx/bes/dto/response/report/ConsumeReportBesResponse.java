package org.codeworks.dsp.adx.bes.dto.response.report;

import org.codeworks.dsp.adx.bes.dto.BesConsumeReport;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;

import java.io.Serializable;
import java.util.List;

/**
 * bes消费报告响应
 * Created by Luis on 2016/9/13.
 */
public class ConsumeReportBesResponse extends BaseBesResponse implements Serializable {

    private List<BesConsumeReport> response;

    public List<BesConsumeReport> getResponse() {
        return response;
    }

    public void setResponse(List<BesConsumeReport> response) {
        this.response = response;
    }
}
