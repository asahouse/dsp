package org.codeworks.dsp.controller.site.admin;

import org.apache.commons.collections.map.HashedMap;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.rtbMQ.*;
import org.codeworks.dsp.service.CampaignService;
import org.codeworks.dsp.service.RtbDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RTB 实时竞价数据统计与分析大盘接口
 * <p>
 * 提供消耗统计（按日/按时）、曝光点击监控、引擎出价状态统计（处理量/丢弃/无匹配/报价/竞得）
 * 以及细分到广告主与广告活动的多维度统计与分析能力。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "RTB 数据分析看板接口", description = "提供 RTB 维度消耗趋势、曝光/点击监控及竞价状态统计查询")
@RequestMapping("/v2/admin/rtbData")
@RestController("/v2/admin/RtbDataController")
public class RtbDataController extends AbstractController {

    private static DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dtf_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private RtbDataService rtbDataService;

    @Autowired
    private CampaignRepository campaignRepository;

    /**
     * 按日期区间查询 RTB 消耗统计
     *
     * @param start 起始日期(yyyy-MM-dd)
     * @param end 截止日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 按日聚合消耗统计
     */
    @ApiOperation(value = "按日期区间查询RTB消耗", notes = "按日聚合查询指定时间跨度内全平台的真实消耗金额")
    @GetMapping(path = "consume/range")
    public Response consume(@ApiParam(value = "起始日期(yyyy-MM-dd)", required = true) @RequestParam String start,
                            @ApiParam(value = "截止日期(yyyy-MM-dd)", required = true) @RequestParam String end,
                            @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbConsume> rtb = rtbDataService.getRtbConsumeDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbConsume>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbConsume::getCompressDate));

        Map<LocalDate, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(date -> {
            Double totalConsume = map.get(date).stream().mapToDouble(RtbConsume::getConsume).sum();

            Map<String, Object> item = new HashedMap();
            item.put("consume", totalConsume);
            result.put(date, item);
        });
        return Response.ok("content", result);
    }

    /**
     * 查询某天内按小时聚合的 RTB 消耗统计
     *
     * @param day 目标日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 按小时聚合消耗统计
     */
    @ApiOperation(value = "按小时查询指定日消耗", notes = "查询某指定自然日内 0-23 小时的分时消耗统计趋势")
    @GetMapping(path = "consume/date")
    public Response consume(@ApiParam(value = "目标日期(yyyy-MM-dd)", required = true) @RequestParam String day,
                            @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbConsume> rtb = rtbDataService.getRtbConsumeDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbConsume>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbConsume::getCompressHour));

        Map<Integer, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(hour -> {
            Double totalConsume = map.get(hour).stream().mapToDouble(RtbConsume::getConsume).sum();

            Map<String, Object> item = new HashedMap();
            item.put("consume", totalConsume);
            result.put(hour, item);
        });

        return Response.ok("content", result);
    }

    /**
     * 按日期区间查询监控数据(曝光与点击)
     *
     * @param start 起始日期(yyyy-MM-dd)
     * @param end 截止日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 按日聚合的曝光与点击数
     */
    @ApiOperation(value = "按区间查询曝光与点击统计", notes = "按日统计指定周期内的曝光量与点击量")
    @GetMapping(path = "monitor/range")
    public Response monitor(@ApiParam(value = "起始日期(yyyy-MM-dd)", required = true) @RequestParam String start,
                            @ApiParam(value = "截止日期(yyyy-MM-dd)", required = true) @RequestParam String end,
                            @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbMoniter> rtb = rtbDataService.getRtbMoniterDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbMoniter>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbMoniter::getCompressDate));

        Map<LocalDate, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(date -> {
            Integer totalImp = map.get(date).stream().mapToInt(RtbMoniter::getImp).sum();
            Integer totalClick = map.get(date).stream().mapToInt(RtbMoniter::getClick).sum();

            Map<String, Object> item = new HashedMap();
            item.put("imp", totalImp);
            item.put("click", totalClick);
            result.put(date, item);
        });
        return Response.ok("content", result);
    }

    /**
     * 查询某天内按小时聚合的曝光与点击数据
     *
     * @param day 目标日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 分时曝光与点击统计
     */
    @ApiOperation(value = "按小时查询指定日曝光点击", notes = "查询某自然日 24 小时各时段的曝光与点击分布")
    @GetMapping(path = "monitor/date")
    public Response monitor(@ApiParam(value = "目标日期(yyyy-MM-dd)", required = true) @RequestParam String day,
                            @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbMoniter> rtb = rtbDataService.getRtbMoniterDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbMoniter>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbMoniter::getCompressHour));

        Map<Integer, Map<String, Object> > result = new HashedMap();
        map.keySet().stream().forEach(hour -> {
            Integer totalImp = map.get(hour).stream().mapToInt(RtbMoniter::getImp).sum();
            Integer totalClick = map.get(hour).stream().mapToInt(RtbMoniter::getClick).sum();

            Map<String, Object> item = new HashedMap();
            item.put("imp", totalImp);
            item.put("click", totalClick);
            result.put(hour, item);
        });
        return Response.ok("content", result);
    }

    /**
     * 按日期区间查询 RTB 引擎处理状态明细
     *
     * @param start 起始日期(yyyy-MM-dd)
     * @param end 截止日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 按日聚合的请求处理、解析错误、丢弃、无匹配、出价及竞得数
     */
    @ApiOperation(value = "按区间查询RTB引擎状态统计", notes = "按日统计请求处理总量、丢包数、未命中数、出价数及竞得数")
    @GetMapping(path = "rtbStats/range")
    public Response rtbStats(@ApiParam(value = "起始日期(yyyy-MM-dd)", required = true) @RequestParam String start,
                            @ApiParam(value = "截止日期(yyyy-MM-dd)", required = true) @RequestParam String end,
                            @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbStats> rtb = rtbDataService.getRtbStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbStats>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbStats::getCompressDate));

        Map<LocalDate, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(date -> {
            Integer totalHandle = map.get(date).stream().mapToInt(RtbStats::getHandle).sum();
            Integer totalParseError = map.get(date).stream().mapToInt(RtbStats::getParseError).sum();
            Integer totalDrop = map.get(date).stream().mapToInt(RtbStats::getDrop).sum();
            Integer totalNoMatch = map.get(date).stream().mapToInt(RtbStats::getNoMatch).sum();
            Integer totalOffer = map.get(date).stream().mapToInt(RtbStats::getOffer).sum();
            Integer totalWin = map.get(date).stream().mapToInt(RtbStats::getWin).sum();

            Map<String, Object> item = new HashedMap();
            item.put("handle", totalHandle);
            item.put("parseError", totalParseError);
            item.put("drop", totalDrop);
            item.put("noMatch", totalNoMatch);
            item.put("offer", totalOffer);
            item.put("win", totalWin);
            result.put(date, item);
        });
        return Response.ok("content", result);
    }

    /**
     * 按小时查询指定自然日内 RTB 引擎状态明细
     *
     * @param day 目标日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 分时处理状态汇总
     */
    @ApiOperation(value = "按小时查询指定日RTB状态", notes = "查询某自然日各小时 RTB 出价引擎的健康度与处理吞吐统计")
    @GetMapping(path = "rtbStats/date")
    public Response rtbStats(@ApiParam(value = "目标日期(yyyy-MM-dd)", required = true) @RequestParam String day,
                            @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbStats> rtb = rtbDataService.getRtbStatsDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbStats>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbStats::getCompressHour));

        Map<Integer, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(hour -> {
            Integer totalHandle = map.get(hour).stream().mapToInt(RtbStats::getHandle).sum();
            Integer totalParseError = map.get(hour).stream().mapToInt(RtbStats::getParseError).sum();
            Integer totalDrop = map.get(hour).stream().mapToInt(RtbStats::getDrop).sum();
            Integer totalNoMatch = map.get(hour).stream().mapToInt(RtbStats::getNoMatch).sum();
            Integer totalOffer = map.get(hour).stream().mapToInt(RtbStats::getOffer).sum();
            Integer totalWin = map.get(hour).stream().mapToInt(RtbStats::getWin).sum();

            Map<String, Object> item = new HashedMap();
            item.put("handle", totalHandle);
            item.put("parseError", totalParseError);
            item.put("drop", totalDrop);
            item.put("noMatch", totalNoMatch);
            item.put("offer", totalOffer);
            item.put("win", totalWin);
            result.put(hour, item);
        });
        return Response.ok("content", result);
    }

    /**
     * 按日期区间查询广告活动表现大盘
     *
     * @param start 起始日期(yyyy-MM-dd)
     * @param end 截止日期(yyyy-MM-dd)
     * @param pageRequest 分页参数
     * @return 全量广告活动按日表现汇总
     */
    @ApiOperation(value = "按区间查询全量活动表现", notes = "按日汇总曝光、点击、竞价与竞得指标")
    @GetMapping(path = "campaignStats/range")
    public Response campaignStats(@ApiParam(value = "起始日期(yyyy-MM-dd)", required = true) @RequestParam String start,
                                  @ApiParam(value = "截止日期(yyyy-MM-dd)", required = true) @RequestParam String end,
                                  @ApiParam(value = "分页参数") Pageable pageRequest) {

        Page<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbCampaignStats>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressDate));

        Map<LocalDate, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(date -> {
            Integer totalImp = map.get(date).stream().mapToInt(RtbCampaignStats::getImp).sum();
            Integer totalClick = map.get(date).stream().mapToInt(RtbCampaignStats::getClick).sum();
            Integer totalOffer = map.get(date).stream().mapToInt(RtbCampaignStats::getOffer).sum();
            Integer totalWin = map.get(date).stream().mapToInt(RtbCampaignStats::getWin).sum();

            Map<String, Object> item = new HashedMap();
            item.put("imp", totalImp);
            item.put("click", totalClick);
            item.put("offer", totalOffer);
            item.put("win", totalWin);
            result.put(date, item);
        });

        return Response.ok("content", result);
    }

    /**
     * 按日期区间分广告主查询活动表现
     *
     * @param start 起始日期
     * @param end 截止日期
     * @return 按广告主聚合的表现统计
     */
    @ApiOperation(value = "按区间分广告主统计活动表现", notes = "按广告主维度分组汇总活动指标")
    @GetMapping(path = "campaignStats/range/adv")
    public Response campaignStats(@ApiParam(value = "起始日期(yyyy-MM-dd)", required = true) @RequestParam String start,
                                  @ApiParam(value = "截止日期(yyyy-MM-dd)", required = true) @RequestParam String end) {
        List<Campaign> campaigns = (List<Campaign>) campaignRepository.findAll();

        Map<Integer, Object> result = new HashedMap();
        Map<Advertiser, List<Campaign>> advMap =
                campaigns.stream().collect(Collectors.groupingBy(Campaign::getAdvertiser));

        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date));
        Map<LocalDate, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressDate));

        advMap.keySet().stream().forEach(adv -> {
            List<Campaign> camps = advMap.get(adv);

            if (!camps.isEmpty()) {
                Map<LocalDate, Object> singleAdvInfoMap = new HashedMap();
                Set<Integer> campIds = camps.stream().map(Campaign::getId).collect(Collectors.toSet());

                map.keySet().stream().forEach(date -> {
                    List<RtbCampaignStats> advStats = map.get(date).stream()
                            .filter(s -> campIds.contains(s.getCampaignId()))
                            .collect(Collectors.toList());

                    if (!advStats.isEmpty()) {
                        Map<String, Object> item = new HashedMap();
                        item.put("imp", advStats.stream().mapToInt(RtbCampaignStats::getImp).sum());
                        item.put("click", advStats.stream().mapToInt(RtbCampaignStats::getClick).sum());
                        item.put("offer", advStats.stream().mapToInt(RtbCampaignStats::getOffer).sum());
                        item.put("win", advStats.stream().mapToInt(RtbCampaignStats::getWin).sum());
                        singleAdvInfoMap.put(date, item);
                    }
                });

                result.put(adv.getId(), singleAdvInfoMap);
            }
        });

        return Response.ok("content", result);
    }

    /**
     * 按日期区间查询单个广告主的所有活动表现
     *
     * @param start 起始日期
     * @param end 截止日期
     * @param advId 目标广告主ID
     * @return 单广告主按日活动表现
     */
    @ApiOperation(value = "按区间查询单广告主活动表现", notes = "获取指定广告主名下活动在指定时间范围内的指标表现")
    @GetMapping(path = "campaignStats/range/adv/{advId}")
    public Response campaignStats(
            @ApiParam(value = "起始日期(yyyy-MM-dd)", required = true) @RequestParam String start,
            @ApiParam(value = "截止日期(yyyy-MM-dd)", required = true) @RequestParam String end,
            @ApiParam(value = "广告主ID", required = true) @PathVariable Integer advId) {

        List<Campaign> campaigns = campaignRepository.findByAdvertiserId(advId);

        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date));
        Map<LocalDate, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressDate));

        Map<LocalDate, Object> singleAdvInfoMap = new HashedMap();

        if (!campaigns.isEmpty()) {
            Set<Integer> campIds = campaigns.stream().map(Campaign::getId).collect(Collectors.toSet());

            map.keySet().stream().forEach(date -> {
                List<RtbCampaignStats> advStats = map.get(date).stream()
                        .filter(s -> campIds.contains(s.getCampaignId()))
                        .collect(Collectors.toList());

                if (!advStats.isEmpty()) {
                    Map<String, Object> item = new HashedMap();
                    item.put("imp", advStats.stream().mapToInt(RtbCampaignStats::getImp).sum());
                    item.put("click", advStats.stream().mapToInt(RtbCampaignStats::getClick).sum());
                    item.put("offer", advStats.stream().mapToInt(RtbCampaignStats::getOffer).sum());
                    item.put("win", advStats.stream().mapToInt(RtbCampaignStats::getWin).sum());
                    singleAdvInfoMap.put(date, item);
                }
            });
        }

        return Response.ok("content", singleAdvInfoMap);
    }

    /**
     * 按小时查询指定日的全量活动表现
     *
     * @param day 目标日期
     * @param pageRequest 分页参数
     * @return 分时全量活动指标
     */
    @ApiOperation(value = "按小时查询全量活动表现", notes = "某自然日内 24 小时的全平台活动表现")
    @GetMapping(path = "campaignStats/date")
    public Response campaignStats(@ApiParam(value = "目标日期(yyyy-MM-dd)", required = true) @RequestParam String day,
                                  @ApiParam(value = "分页参数") Pageable pageRequest) {
        Page<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbCampaignStats>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbCampaignStats::getCompressHour));

        Map<Integer, Map<String, Object>> result = new HashedMap();
        map.keySet().stream().forEach(hour -> {
            Integer totalImp = map.get(hour).stream().mapToInt(RtbCampaignStats::getImp).sum();
            Integer totalClick = map.get(hour).stream().mapToInt(RtbCampaignStats::getClick).sum();
            Integer totalOffer = map.get(hour).stream().mapToInt(RtbCampaignStats::getOffer).sum();
            Integer totalWin = map.get(hour).stream().mapToInt(RtbCampaignStats::getWin).sum();

            Map<String, Object> item = new HashedMap();
            item.put("imp", totalImp);
            item.put("click", totalClick);
            item.put("offer", totalOffer);
            item.put("win", totalWin);
            result.put(hour, item);
        });

        return Response.ok("content", result);
    }

    /**
     * 按小时分广告主查询指定日活动表现
     *
     * @param day 目标日期
     * @return 分时分广告主指标
     */
    @ApiOperation(value = "按小时分广告主查询活动表现", notes = "按广告主分组并按 24 小时展现统计指标")
    @GetMapping(path = "campaignStats/date/adv")
    public Response campaignStats(@ApiParam(value = "目标日期(yyyy-MM-dd)", required = true) @RequestParam String day) {
        List<Campaign> campaigns = (List<Campaign>) campaignRepository.findAll();

        Map<Integer, Object> result = new HashedMap();
        Map<Advertiser, List<Campaign>> advMap =
                campaigns.stream().collect(Collectors.groupingBy(Campaign::getAdvertiser));

        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithData(LocalDate.parse(day, dtf_date));
        Map<Integer, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressHour));

        advMap.keySet().stream().forEach(adv -> {
            List<Campaign> camps = advMap.get(adv);

            if (!camps.isEmpty()) {
                Map<Integer, Object> singleAdvInfoMap = new HashedMap();
                Set<Integer> campIds = camps.stream().map(Campaign::getId).collect(Collectors.toSet());

                map.keySet().stream().forEach(hour -> {
                    List<RtbCampaignStats> advStats = map.get(hour).stream()
                            .filter(s -> campIds.contains(s.getCampaignId()))
                            .collect(Collectors.toList());

                    if (!advStats.isEmpty()) {
                        Map<String, Object> item = new HashedMap();
                        item.put("imp", advStats.stream().mapToInt(RtbCampaignStats::getImp).sum());
                        item.put("click", advStats.stream().mapToInt(RtbCampaignStats::getClick).sum());
                        item.put("offer", advStats.stream().mapToInt(RtbCampaignStats::getOffer).sum());
                        item.put("win", advStats.stream().mapToInt(RtbCampaignStats::getWin).sum());
                        singleAdvInfoMap.put(hour, item);
                    }
                });

                result.put(adv.getId(), singleAdvInfoMap);
            }
        });

        return Response.ok("content", result);
    }

    /**
     * 按小时查询单个广告主的指定日活动表现
     *
     * @param day 目标日期
     * @param advId 广告主ID
     * @return 单广告主分时统计指标
     */
    @ApiOperation(value = "按小时查询单广告主活动表现", notes = "获取指定广告主在某一天的 24 小时分时表现明细")
    @GetMapping(path = "campaignStats/date/adv/{advId}")
    public Response campaignStats(@ApiParam(value = "目标日期(yyyy-MM-dd)", required = true) @RequestParam String day,
                                  @ApiParam(value = "广告主ID", required = true) @PathVariable Integer advId) {
        List<Campaign> campaigns = campaignRepository.findByAdvertiserId(advId);

        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithData(LocalDate.parse(day, dtf_date));
        Map<Integer, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressHour));

        Map<Integer, Object> singleAdvInfoMap = new HashedMap();

        if (!campaigns.isEmpty()) {
            Set<Integer> campIds = campaigns.stream().map(Campaign::getId).collect(Collectors.toSet());

            map.keySet().stream().forEach(hour -> {
                List<RtbCampaignStats> advStats = map.get(hour).stream()
                        .filter(s -> campIds.contains(s.getCampaignId()))
                        .collect(Collectors.toList());

                if (!advStats.isEmpty()) {
                    Map<String, Object> item = new HashedMap();
                    item.put("imp", advStats.stream().mapToInt(RtbCampaignStats::getImp).sum());
                    item.put("click", advStats.stream().mapToInt(RtbCampaignStats::getClick).sum());
                    item.put("offer", advStats.stream().mapToInt(RtbCampaignStats::getOffer).sum());
                    item.put("win", advStats.stream().mapToInt(RtbCampaignStats::getWin).sum());
                    singleAdvInfoMap.put(hour, item);
                }
            });
        }

        return Response.ok("content", singleAdvInfoMap);
    }
}
