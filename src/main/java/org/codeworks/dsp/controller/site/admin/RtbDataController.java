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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/12/22.
 */
@RequestMapping("/v2/admin/rtbData")
@RestController("/v2/admin/RtbDataController")
public class RtbDataController extends AbstractController {

    private static DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dtf_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private RtbDataService rtbDataService;

    @Autowired
    private CampaignRepository campaignRepository;

    @GetMapping(path = "consume/range")
    public Response consume(@RequestParam String start, @RequestParam String end, Pageable pageRequest){
        Page<RtbConsume> rtb = rtbDataService.getRtbConsumeDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbConsume>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbConsume::getCompressDate));

        Map<LocalDate, Map<String, Object> > result = new HashedMap();
        map.keySet().stream().forEach(date -> {
            Double totalConsume = map.get(date).stream().mapToDouble(RtbConsume::getConsume).sum();

            Map<String, Object> item = new HashedMap();
            item.put("consume", totalConsume);
            result.put(date, item);
        });
        return Response.ok("content", result);
    }
    @GetMapping(path = "consume/date")
    public Response consume(@RequestParam String day, Pageable pageRequest){
        Page<RtbConsume> rtb = rtbDataService.getRtbConsumeDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbConsume>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbConsume::getCompressHour));

        Map<Integer, Map<String, Object> > result = new HashedMap();
        map.keySet().stream().forEach(hour -> {
            Double totalConsume = map.get(hour).stream().mapToDouble(RtbConsume::getConsume).sum();

            Map<String, Object> item = new HashedMap();
            item.put("consume", totalConsume);
            result.put(hour, item);
        });

        return Response.ok("content", result);
    }

    @GetMapping(path = "monitor/range")
    public Response monitor(@RequestParam String start, @RequestParam String end, Pageable pageRequest){
        Page<RtbMoniter> rtb = rtbDataService.getRtbMoniterDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbMoniter>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbMoniter::getCompressDate));

        Map<LocalDate, Map<String, Object> > result = new HashedMap();
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
    @GetMapping(path = "monitor/date")
    public Response monitor(@RequestParam String day, Pageable pageRequest){
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

    @GetMapping(path = "rtbStats/range")
    public Response rtbStats(@RequestParam String start, @RequestParam String end, Pageable pageRequest){
        Page<RtbStats> rtb = rtbDataService.getRtbStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbStats>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbStats::getCompressDate));

        Map<LocalDate, Map<String, Object> > result = new HashedMap();
        map.keySet().stream().forEach(date -> {
            Integer totalHandle =  map.get(date).stream().mapToInt(RtbStats::getHandle).sum();
            Integer totalParseError =  map.get(date).stream().mapToInt(RtbStats::getParseError).sum();
            Integer totalDrop =  map.get(date).stream().mapToInt(RtbStats::getDrop).sum();
            Integer totalNoMatch =  map.get(date).stream().mapToInt(RtbStats::getNoMatch).sum();
            Integer totalOffer =  map.get(date).stream().mapToInt(RtbStats::getOffer).sum();
            Integer totalWin =  map.get(date).stream().mapToInt(RtbStats::getWin).sum();

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
    @GetMapping(path = "rtbStats/date")
    public Response rtbStats(@RequestParam String day, Pageable pageRequest){
        Page<RtbStats> rtb = rtbDataService.getRtbStatsDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbStats>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbStats::getCompressHour));


        Map<Integer, Map<String, Object> > result = new HashedMap();
        map.keySet().stream().forEach(hour -> {
            Integer totalHandle =  map.get(hour).stream().mapToInt(RtbStats::getHandle).sum();
            Integer totalParseError =  map.get(hour).stream().mapToInt(RtbStats::getParseError).sum();
            Integer totalDrop =  map.get(hour).stream().mapToInt(RtbStats::getDrop).sum();
            Integer totalNoMatch =  map.get(hour).stream().mapToInt(RtbStats::getNoMatch).sum();
            Integer totalOffer =  map.get(hour).stream().mapToInt(RtbStats::getOffer).sum();
            Integer totalWin =  map.get(hour).stream().mapToInt(RtbStats::getWin).sum();

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




    @GetMapping(path = "campaignStats/range")
    public Response campaignStats(@RequestParam String start, @RequestParam String end, Pageable pageRequest){

        Page<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date), pageRequest);
        Map<LocalDate, List<RtbCampaignStats>> map =
                rtb.getContent().stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressDate));

        Map<LocalDate, Map<String, Object> > result = new HashedMap();
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

    @GetMapping(path = "campaignStats/range/adv")
    public Response campaignStats(@RequestParam String start, @RequestParam String end){
        List<Campaign> campaigns = (List<Campaign>) campaignRepository.findAll();

        //广告主id / (日期 / 字段键值汇总)
        Map<Integer, Object> result = new HashedMap();

        Map<Advertiser, List<Campaign>> advMap =
                campaigns.stream().collect(Collectors.groupingBy(Campaign::getAdvertiser));


        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date));
        Map<LocalDate, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressDate));


        advMap.keySet().stream().forEach(adv ->{
            List<Campaign> camps = advMap.get(adv);

            if (!camps.isEmpty()){
                Map<LocalDate, Object> singleAdvInfoMap = new HashedMap();

                map.keySet().stream().forEach(date -> {

                    Map<Integer, List<RtbCampaignStats>> campMapGroup =  map.get(date).stream().collect(
                            Collectors.groupingBy(RtbCampaignStats::getCampaignId));

                    campMapGroup.keySet().stream().forEach(id -> {
                        Integer totalImp = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getImp).sum();
                        Integer totalClick = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getClick).sum();
                        Integer totalOffer = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getOffer).sum();
                        Integer totalWin = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getWin).sum();

                        Map<String, Object> item = new HashedMap();
                        item.put("imp", totalImp);
                        item.put("click", totalClick);
                        item.put("offer", totalOffer);
                        item.put("win", totalWin);

                        Long exist = camps.stream().filter(c -> id == c.getId()).count();
                        if (exist>0)
                            singleAdvInfoMap.put(date, item);
                    });
                });

                result.put(adv.getId(), singleAdvInfoMap);
            }
        });

        return Response.ok("content", result);
    }

    @GetMapping(path = "campaignStats/range/adv/{advId}")
    public Response campaignStats(
            @RequestParam String start, @RequestParam String end, @PathVariable Integer advId){

        List<Campaign> campaigns = campaignRepository.findByAdvertiserId(advId);

        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithRange(
                LocalDate.parse(start, dtf_date), LocalDate.parse(end, dtf_date));
        Map<LocalDate, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressDate));

        Map<LocalDate, Object> singleAdvInfoMap = new HashedMap();

        if (!campaigns.isEmpty()){

            map.keySet().stream().forEach(date -> {

                Map<Integer, List<RtbCampaignStats>> campMapGroup =  map.get(date).stream().collect(
                        Collectors.groupingBy(RtbCampaignStats::getCampaignId));

                campMapGroup.keySet().stream().forEach(id -> {
                    Integer totalImp = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getImp).sum();
                    Integer totalClick = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getClick).sum();
                    Integer totalOffer = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getOffer).sum();
                    Integer totalWin = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getWin).sum();

                    Map<String, Object> item = new HashedMap();
                    item.put("imp", totalImp);
                    item.put("click", totalClick);
                    item.put("offer", totalOffer);
                    item.put("win", totalWin);

                    Long exist = campaigns.stream().filter(c -> id == c.getId()).count();
                    if (exist>0)
                        singleAdvInfoMap.put(date, item);
                });
            });
        }

        return Response.ok("content", singleAdvInfoMap);
    }



    @GetMapping(path = "campaignStats/date")
    public Response campaignStats(@RequestParam String day, Pageable pageRequest){
        Page<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithData(
                LocalDate.parse(day, dtf_date), pageRequest);
        Map<Integer, List<RtbCampaignStats>> map =
                rtb.getContent().stream().collect(
                        Collectors.groupingBy(RtbCampaignStats::getCompressHour));

        Map<Integer, Map<String, Object> > result = new HashedMap();
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

    @GetMapping(path = "campaignStats/date/adv")
    public Response campaignStats(@RequestParam String day){
        List<Campaign> campaigns = (List<Campaign>) campaignRepository.findAll();

        //广告主id / (小时 / 字段键值汇总)
        Map<Integer, Object> result = new HashedMap();

        Map<Advertiser, List<Campaign>> advMap =
                campaigns.stream().collect(Collectors.groupingBy(Campaign::getAdvertiser));


        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithData(LocalDate.parse(day, dtf_date));
        Map<Integer, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressHour));


        advMap.keySet().stream().forEach(adv ->{
            List<Campaign> camps = advMap.get(adv);

            if (!camps.isEmpty()){
                Map<Integer, Object> singleAdvInfoMap = new HashedMap();

                map.keySet().stream().forEach(hour -> {

                    Map<Integer, List<RtbCampaignStats>> campMapGroup =  map.get(hour).stream().collect(
                            Collectors.groupingBy(RtbCampaignStats::getCampaignId));

                    campMapGroup.keySet().stream().forEach(id -> {
                        Integer totalImp = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getImp).sum();
                        Integer totalClick = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getClick).sum();
                        Integer totalOffer = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getOffer).sum();
                        Integer totalWin = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getWin).sum();

                        Map<String, Object> item = new HashedMap();
                        item.put("imp", totalImp);
                        item.put("click", totalClick);
                        item.put("offer", totalOffer);
                        item.put("win", totalWin);

                        Long exist = camps.stream().filter(c -> id == c.getId()).count();
                        if (exist>0)
                            singleAdvInfoMap.put(hour, item);
                    });
                });

                result.put(adv.getId(), singleAdvInfoMap);
            }
        });

        return Response.ok("content", result);
    }

    @GetMapping(path = "campaignStats/date/adv/{advId}")
    public Response campaignStats(@RequestParam String day, @PathVariable Integer advId){
        List<Campaign> campaigns = campaignRepository.findByAdvertiserId(advId);

        List<RtbCampaignStats> rtb = rtbDataService.getRtbCampaignStatsDatasWithData(LocalDate.parse(day, dtf_date));
        Map<Integer, List<RtbCampaignStats>> map =
                rtb.stream().collect(Collectors.groupingBy(RtbCampaignStats::getCompressHour));

        Map<Integer, Object> singleAdvInfoMap = new HashedMap();

        if (!campaigns.isEmpty()){

            map.keySet().stream().forEach(hour -> {

                Map<Integer, List<RtbCampaignStats>> campMapGroup =  map.get(hour).stream().collect(
                        Collectors.groupingBy(RtbCampaignStats::getCampaignId));

                campMapGroup.keySet().stream().forEach(id -> {
                    Integer totalImp = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getImp).sum();
                    Integer totalClick = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getClick).sum();
                    Integer totalOffer = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getOffer).sum();
                    Integer totalWin = campMapGroup.get(id).stream().mapToInt(RtbCampaignStats::getWin).sum();

                    Map<String, Object> item = new HashedMap();
                    item.put("imp", totalImp);
                    item.put("click", totalClick);
                    item.put("offer", totalOffer);
                    item.put("win", totalWin);

                    Long exist = campaigns.stream().filter(c -> id == c.getId()).count();
                    if (exist>0)
                        singleAdvInfoMap.put(hour, item);
                });
            });
        }

        return Response.ok("content", singleAdvInfoMap);
    }
}
