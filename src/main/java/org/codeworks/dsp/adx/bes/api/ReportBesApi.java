package org.codeworks.dsp.adx.bes.api;

import org.codeworks.dsp.adx.bes.dto.request.report.ReportDateRangeBesRequest;
import org.codeworks.dsp.adx.bes.dto.response.report.AdvertiserReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.ConsumeReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.CreativeRTBReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.RtbReportBesResponse;
import org.codeworks.dsp.adx.bes.exception.BaiduEsApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 报告接口
 * Created by Luis on 2016/9/13.
 */
@Component
public class ReportBesApi extends BaseBesApi {

    public RtbReportBesResponse rtb(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new BaiduEsApiException("endDate can not before startDate");

        if (startDate.plusDays(10).isBefore(endDate))
            throw new BaiduEsApiException("query maximum range is 10 days");

        ReportDateRangeBesRequest req = new ReportDateRangeBesRequest(startDate, endDate);
        ResponseEntity<RtbReportBesResponse> resp = post(baiduEsConstant.getREPORT_RTB_URL(), req, RtbReportBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public ConsumeReportBesResponse consume(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new BaiduEsApiException("endDate can not before startDate");

        if (startDate.plusDays(10).isBefore(endDate))
            throw new BaiduEsApiException("query maximum range is 10 days");

        ReportDateRangeBesRequest req = new ReportDateRangeBesRequest(startDate, endDate);
        ResponseEntity<ConsumeReportBesResponse> resp = post(baiduEsConstant.getREPORT_CONSUME_URL(), req, ConsumeReportBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public AdvertiserReportBesResponse advertiser(LocalDate startDate, LocalDate endDate){
        if (startDate.isAfter(endDate))
            throw new BaiduEsApiException("endDate can not before startDate");

        if (startDate.plusDays(10).isBefore(endDate))
            throw new BaiduEsApiException("query maximum range is 10 days");

        ReportDateRangeBesRequest req = new ReportDateRangeBesRequest(startDate, endDate);
        ResponseEntity<AdvertiserReportBesResponse> resp = post(baiduEsConstant.getREPORT_ADVERTISER_URL(), req, AdvertiserReportBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public CreativeRTBReportBesResponse creativeRTB(LocalDate startDate, LocalDate endDate){
        if (startDate.isAfter(endDate))
            throw new BaiduEsApiException("endDate can not before startDate");

        if (startDate.plusDays(10).isBefore(endDate))
            throw new BaiduEsApiException("query maximum range is 10 days");

        ReportDateRangeBesRequest req = new ReportDateRangeBesRequest(startDate, endDate);
        ResponseEntity<CreativeRTBReportBesResponse> resp = post(baiduEsConstant.getREPORT_CREATIVERTB_URL(), req, CreativeRTBReportBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }
}
