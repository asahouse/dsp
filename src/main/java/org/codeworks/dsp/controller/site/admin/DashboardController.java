package org.codeworks.dsp.controller.site.admin;

import org.apache.commons.collections.map.HashedMap;
import org.codeworks.dsp.adx.bes.dto.response.report.ConsumeReportBesResponse;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.rtbMQ.RtbConsume;
import org.codeworks.dsp.model.entities.rtbMQ.RtbMoniter;
import org.codeworks.dsp.service.AdvertiserService;
import org.codeworks.dsp.service.BesApiService;
import org.codeworks.dsp.service.CampaignService;
import org.codeworks.dsp.service.RtbDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 管理员工作台 Dashboard 聚合看板接口
 * <p>
 * 聚合全平台广告主总量、活动总量、今日新增活动数、近 3 天外部消耗、近 7 天曝光/点击与花费趋势。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "管理员工作台大盘接口", description = "提供系统概览看板数据，包括核心指标、近7天消耗及投放趋势")
@RequestMapping("/v2/admin/dashboard")
@RestController("/v2/admin/DashboardController")
public class DashboardController extends AbstractController {

    @Autowired
    private AdvertiserService advService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private BesApiService besApiService;

    @Autowired
    private RtbDataService rtbDataService;

    /**
     * 获取管理工作台核心指标看板数据
     *
     * @return 包含广告主总量、活动总数、当日活动数、消耗统计与曝光/点击趋势的数据对象
     */
    @ApiOperation(value = "获取工作台看板指标", notes = "聚合全平台广告主数、活动数、近3天消耗及近7日曝光/点击/花费趋势统计")
    @GetMapping
    public Response dashboardInfos() {

        //目前广告主总量
        Long totalAdv = advService.countAll();

        //全部活动数
        Long totalCampaign = campaignService.countTotal();

        //当天活动数
        Long totalCampaignNow = this.achieveCountCampaignTotal(LocalDateTime.now());

        //3天内的消费总数
        Number threeDaysConsume = this.achieveThreeDayConsume(LocalDate.now().minusDays(3),LocalDate.now());

        PageRequest pageRequest = new PageRequest(0, 20);

        //广告主消费
        Page<RtbConsume> consumes = rtbDataService.getRtbConsumeDatasWithRange(
                LocalDate.now().minusDays(7), LocalDate.now(), pageRequest);
        Map<LocalDate, List<RtbConsume>> consumesMap =
                consumes.getContent().stream().collect(Collectors.groupingBy(RtbConsume::getCompressDate));
        Map<LocalDate, Map<String, Object> > consumeResult = new HashedMap();
        consumesMap.keySet().stream().forEach(date -> {
            Double totalConsume = consumesMap.get(date).stream().mapToDouble(RtbConsume::getConsume).sum();

            Map<String, Object> item = new HashedMap();
            item.put("consume", totalConsume);
            consumeResult.put(date, item);
        });

        //监测信息(曝光,点击)
        Page<RtbMoniter> monitors = rtbDataService.getRtbMoniterDatasWithRange(
                LocalDate.now().minusDays(7), LocalDate.now(), pageRequest);
        Map<LocalDate, List<RtbMoniter>> monitorsMap =
                monitors.getContent().stream().collect(Collectors.groupingBy(RtbMoniter::getCompressDate));
        Map<LocalDate, Map<String, Object> > monitorsResult = new HashedMap();
        monitorsMap.keySet().stream().forEach(date -> {
            Integer totalImp = monitorsMap.get(date).stream().mapToInt(RtbMoniter::getImp).sum();
            Integer totalClick = monitorsMap.get(date).stream().mapToInt(RtbMoniter::getClick).sum();

            Map<String, Object> item = new HashedMap();
            item.put("imp", totalImp);
            item.put("click", totalClick);
            monitorsResult.put(date, item);
        });


        Map<String, Object> result = new HashMap<>();
        result.put("totalCampaign", totalCampaign);
        result.put("totalAdv", totalAdv);
        result.put("totalCampaignNow", totalCampaignNow);
        result.put("threeDaysConsume", threeDaysConsume);
        result.put("monitors", monitorsResult);
        result.put("consumes", consumeResult);

        return Response.ok().add("result", result);
    }

    private Long achieveCountCampaignTotal(LocalDateTime dateTime){
        Map<String, Object> timeReq = new HashMap<>();

        LocalDateTime fromTime = dateTime.withHour(0).withSecond(0).withMinute(0).withNano(0);
        LocalDateTime toTime = dateTime.withHour(23).withSecond(59).withMinute(59).withNano(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        timeReq.put("createTime between", dtf.format(fromTime)+"#"+dtf.format(toTime));
        Long campaignCount = campaignService.countCampaign(timeReq);

        return campaignCount;
    }

    private Double achieveThreeDayConsume(LocalDate fromDate, LocalDate toDate){
        Optional<ConsumeReportBesResponse> res = besApiService.callConsumeReport(fromDate, toDate);

        if (res.isPresent() && !res.get().getResponse().isEmpty()){
            return res.get().getResponse().get(0).getCost();
        }else
            return 0.00;
    }
}
