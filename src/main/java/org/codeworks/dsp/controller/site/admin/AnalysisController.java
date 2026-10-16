package org.codeworks.dsp.controller.site.admin;

import com.baidu.statistics.dataapi.om.profile.GetDataCollectionRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataRequestRequisite;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.service.AnalysisService;
import org.codeworks.dsp.service.StatisticsService;
import org.codeworks.dsp.utils.EhcacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 外部站点分析与百度统计数据接口
 * <p>
 * 提供从百度统计数据 API 同步站点列表、指标明细与聚合指标，并支持本地二级缓存维护与清理。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "站点分析与百度统计接口", description = "提供百度统计数据拉取、站点查询及本地统计缓存清除功能")
@RequestMapping("/v2/admin/analysis")
@RestController("/v2/admin/AnalysisController")
public class AnalysisController extends AbstractController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private EhcacheUtil ehcacheUtil;

    /**
     * 获取百度统计绑定的站点列表
     *
     * @return 站点列表数据
     */
    @ApiOperation(value = "获取百度统计站点列表", notes = "从百度统计开放平台同步当前账号下配置的所有网站站点元数据")
    @GetMapping(path = "baidu-sites")
    public Response baiduSites() {
        return Response.ok("data", statisticsService.achieveBaiduSites());
    }

    /**
     * 获取单站点百度统计数据
     *
     * @param body 查询参数请求体
     * @param result 数据校验结果
     * @return 统计明细数据
     */
    @ApiOperation(value = "获取单站点统计数据", notes = "根据指定指标、时间范围和维度请求百度统计单站点分析明细")
    @PostMapping(path = "baidu-data")
    public Response baiduData(@ApiParam(value = "统计查询请求参数", required = true) @RequestBody GetDataRequest body, BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        return Response.ok("data", statisticsService.achieveBaiduData(body));
    }

    /**
     * 获取多站点集合百度统计数据
     *
     * @param body 多站点集合请求体
     * @param result 数据校验结果
     * @return 集合统计明细
     */
    @ApiOperation(value = "获取多站点集合统计数据", notes = "批量获取多个站点聚合指标的百度统计分析结果")
    @PostMapping(path = "baidu-datas")
    public Response baiduDatas(@ApiParam(value = "多站点查询请求体", required = true) @RequestBody GetDataCollectionRequest body, BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        return Response.ok("data", statisticsService.achieveBaiduDatas(body));
    }

    /**
     * 清空本地统计缓存
     *
     * @return 操作成功响应
     */
    @ApiOperation(value = "清空所有统计缓存", notes = "清空本地 Ehcache 缓存中的全部百度统计临时数据")
    @PostMapping(path = "baidu-remove")
    public Response baiduClear() {
        ehcacheUtil.removeAll();
        return Response.ok();
    }

    /**
     * 按键清空指定缓存项
     *
     * @param key 缓存 Key
     * @param cache 缓存分片名称
     * @return 操作成功响应
     */
    @ApiOperation(value = "删除指定统计缓存项", notes = "从指定的 Ehcache 区域中移除对应 key 的统计缓存条目")
    @PostMapping(path = "baidu-removekey")
    public Response baiduClear(@ApiParam(value = "缓存键", required = true) @RequestParam String key,
                              @ApiParam(value = "缓存分片名称", required = true) @RequestParam String cache) {
        ehcacheUtil.remove(cache, key);
        return Response.ok();
    }
}
