package org.codeworks.dsp.controller.site.admin;

import com.baidu.statistics.dataapi.om.profile.GetDataCollectionRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataRequestRequisite;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.service.AnalysisService;
import org.codeworks.dsp.service.StatisticsService;
import org.codeworks.dsp.utils.EhcacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminkc on 16/12/9.
 */
@RequestMapping("/v2/admin/analysis")
@RestController("/v2/admin/AnalysisController")
public class AnalysisController extends AbstractController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private EhcacheUtil ehcacheUtil;

    @GetMapping(path = "baidu-sites")
    public Response baiduSites(){
        return Response.ok("data", statisticsService.achieveBaiduSites());
    }

    @PostMapping(path = "baidu-data")
    public Response baiduData(@RequestBody GetDataRequest body, BindingResult result){

        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        return Response.ok("data", statisticsService.achieveBaiduData(body));
    }

    @PostMapping(path = "baidu-datas")
    public Response baiduDatas(@RequestBody GetDataCollectionRequest body, BindingResult result){

        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        return Response.ok("data", statisticsService.achieveBaiduDatas(body));
    }

    @GetMapping(path = "baidu-remove")
    public Response baiduClear(){
        ehcacheUtil.removeAll();
        return Response.ok();
    }

    @GetMapping(path = "baidu-removekey")
    public Response baiduClear(@RequestParam String key, @RequestParam String cache){
        ehcacheUtil.remove(cache, key);
        return Response.ok();
    }
}
