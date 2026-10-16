package org.codeworks.dsp.controller.site.admin;

import org.apache.commons.lang3.ArrayUtils;
import org.codeworks.dsp.adx.bes.dto.response.report.AdvertiserReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.ConsumeReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.CreativeRTBReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.RtbReportBesResponse;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.service.BesApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 报表中心管理接口（ADX / 百度 BES 对账与报表拉取）
 * <p>
 * 提供从外部 ADX（如百度 BES）拉取 RTB 竞价报表、消耗报表、广告主报表以及物料级竞价报表的能力。
 * 针对空日期参数提供智能默认回退机制（默认拉取近 7 天历史报表）。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "统计报表接口", description = "提供 ADX 平台 RTB 竞价对账报表、消耗明细报表与广告主表现报表拉取")
@RequestMapping("/v2/admin/report")
@RestController("/v2/admin/ReportController")
public class ReportController extends AbstractController {

    @Autowired
    private BesApiService besApiService;

    /**
     * 智能计算与补齐日期区间（默认获取过去 7 天至昨天）
     */
    private LocalDate[] resolveDates(LocalDate startDate, LocalDate endDate) {
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now().minusDays(1);
        return new LocalDate[]{start, end};
    }

    /**
     * 拉取 RTB 竞价报表
     *
     * @param startDate 起始日期（yyyy-MM-dd）
     * @param endDate 截止日期（yyyy-MM-dd）
     * @return 报表行内容集合响应
     */
    @ApiOperation(value = "拉取 RTB 竞价报表", notes = "从 BES API 拉取指定时间范围的 RTB 竞价统计对账报表")
    @GetMapping(path = "rtbReport")
    public Response callRtbReport(
            @ApiParam(value = "起始日期(yyyy-MM-dd)") @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "截止日期(yyyy-MM-dd)") @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDate[] dates = resolveDates(startDate, endDate);
        Optional<RtbReportBesResponse> report = besApiService.callRtbReport(dates[0], dates[1]);
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 拉取消耗报表
     *
     * @param startDate 起始日期
     * @param endDate 截止日期
     * @return 消耗统计行集合
     */
    @ApiOperation(value = "拉取消耗统计报表", notes = "从 BES API 拉取指定时间跨度内的广告活动及广告主真实资金消耗明细")
    @GetMapping(path = "consumeReport")
    public Response callConsumeReport(
            @ApiParam(value = "起始日期(yyyy-MM-dd)") @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "截止日期(yyyy-MM-dd)") @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDate[] dates = resolveDates(startDate, endDate);
        Optional<ConsumeReportBesResponse> report = besApiService.callConsumeReport(dates[0], dates[1]);
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 拉取广告主维度表现报表
     *
     * @param startDate 起始日期
     * @param endDate 截止日期
     * @return 广告主表现数据
     */
    @ApiOperation(value = "拉取广告主表现报表", notes = "拉取各广告主在外部 ADX 侧的展示量、点击量与花费汇总")
    @GetMapping(path = "advertiserReport")
    public Response callAdvertiserReport(
            @ApiParam(value = "起始日期(yyyy-MM-dd)") @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "截止日期(yyyy-MM-dd)") @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDate[] dates = resolveDates(startDate, endDate);
        Optional<AdvertiserReportBesResponse> report = besApiService.callAdvertiserReport(dates[0], dates[1]);
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 拉取创意物料级别 RTB 报表
     *
     * @param startDate 起始日期
     * @param endDate 截止日期
     * @return 创意物料表现数据
     */
    @ApiOperation(value = "拉取创意级 RTB 报表", notes = "拉取细粒度到 Material 创意维度的展示与竞得明细")
    @GetMapping(path = "creativeRTBReport")
    public Response callCreativeRTBReport(
            @ApiParam(value = "起始日期(yyyy-MM-dd)") @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "截止日期(yyyy-MM-dd)") @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDate[] dates = resolveDates(startDate, endDate);
        Optional<CreativeRTBReportBesResponse> report = besApiService.callCreativeRTBReport(dates[0], dates[1]);
        return Response.ok("content", report.isPresent() ? report.get().getResponse() : ArrayUtils.EMPTY_STRING_ARRAY);
    }
}
