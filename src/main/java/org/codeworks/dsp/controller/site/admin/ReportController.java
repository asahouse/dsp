package org.codeworks.dsp.controller.site.admin;

import org.apache.commons.lang3.ArrayUtils;
import org.codeworks.dsp.adx.bes.dto.response.report.AdvertiserReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.ConsumeReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.CreativeRTBReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.RtbReportBesResponse;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.service.BesApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/10/25.
 */
@RequestMapping("/v2/admin/report")
@RestController("/v2/admin/ReportController")
public class ReportController extends AbstractController {

    @Autowired
    private BesApiService besApiService;

    @GetMapping(path="rtbReport")
    public Response callRtbReport(){
        Optional<RtbReportBesResponse> report = besApiService.callRtbReport(LocalDate.now(), LocalDate.now().plusDays(2));
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @GetMapping(path="consumeReport")
    public Response callConsumeReport(){
        Optional<ConsumeReportBesResponse> report = besApiService.callConsumeReport(LocalDate.now(), LocalDate.now().plusDays(2));
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @GetMapping(path="advertiserReport")
    public Response callAdvertiserReport(){
        Optional<AdvertiserReportBesResponse> report = besApiService.callAdvertiserReport(LocalDate.now(), LocalDate.now().plusDays(2));
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @GetMapping(path="creativeRTBReport")
    public Response callCreativeRTBReport(){
        Optional<CreativeRTBReportBesResponse> report = besApiService.callCreativeRTBReport(LocalDate.now(), LocalDate.now().plusDays(2));
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }
}
