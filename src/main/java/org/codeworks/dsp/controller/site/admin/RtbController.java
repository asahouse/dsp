package org.codeworks.dsp.controller.site.admin;

import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.service.BesApiService;
import org.codeworks.dsp.service.RtbCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * RTB 基础数据同步控制器
 * <p>
 * 提供向 RTB 竞价核心引擎手动批量同步广告主资质、广告活动配置以及创意物料状态的功能。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "RTB 数据同步接口", description = "提供将广告主、广告活动及创意物料手动推送到 RTB 竞价端的数据同步服务")
@RequestMapping("/v2/admin/rtb")
@RestController("/v2/admin/RtbController")
public class RtbController extends AbstractController {

    @Autowired
    private RtbCallService rtbCallService;

    @Autowired
    private AdvertiserRepository advertiserRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private BesApiService besApiService;

    /**
     * 手动同步广告主信息至 RTB 引擎
     *
     * @param ids 待同步的广告主 ID 列表
     * @return 操作成功响应
     */
    @ApiOperation(value = "同步广告主至 RTB", notes = "将指定的广告主信息批量推送到 RTB 出价服务")
    @PostMapping(path = "syncAdvertiser")
    public Response syncAdvertiser(@ApiParam(value = "广告主ID列表", required = true) @RequestParam ArrayList<Integer> ids) {
        List<Advertiser> advertisers = advertiserRepository.findByIdIn(ids);
        rtbCallService.syncAdvertiser(advertisers);
        return Response.ok();
    }

    /**
     * 手动同步广告活动至 RTB 引擎
     *
     * @param ids 待同步的广告活动 ID 列表
     * @return 操作成功响应
     */
    @ApiOperation(value = "同步广告活动至 RTB", notes = "将指定的广告活动排期与定向配置推送到 RTB 引擎")
    @PostMapping(path = "syncCampaign")
    public Response syncCampaign(@ApiParam(value = "广告活动ID列表", required = true) @RequestParam ArrayList<Integer> ids) {
        List<Campaign> campaigns = campaignRepository.findByIdIn(ids);
        rtbCallService.syncCampaign(campaigns);
        return Response.ok();
    }

    /**
     * 手动同步创意物料至 RTB 引擎
     *
     * @param ids 待同步的创意物料 ID 列表
     * @return 操作成功响应
     */
    @ApiOperation(value = "同步创意物料至 RTB", notes = "将审核通过的物料信息推送到 RTB 竞价检索引擎")
    @PostMapping(path = "syncMaterial")
    public Response syncMaterial(@ApiParam(value = "物料ID列表", required = true) @RequestParam ArrayList<Integer> ids) {
        List<Material> materials = materialRepository.findByIdIn(ids);
        rtbCallService.syncMaterials(materials);
        return Response.ok();
    }
}
