package org.codeworks.dsp.controller.site.admin;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.dto.SubmitCampaign;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.campaign.EditView;
import org.codeworks.dsp.model.views.campaign.ListView;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.service.CampaignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员后台广告活动管理接口
 * <p>
 * 提供系统运营人员/超级管理员对全平台所有广告活动的直接创建、特权修改、全景分页查询与详情检索功能。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "管理员广告活动管理接口", description = "提供平台管理员层级对全量广告活动的创建、变更、全景查询与详情获取")
@RequestMapping("/v2/admin/campaign")
@RestController("/v2/admin/CampaignAuditController")
public class CampaignManageController extends AbstractController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private AdvertiserRepository advRepo;

    /**
     * 管理员代创建广告活动
     *
     * @param advId 广告主ID
     * @param loginAdv 当前登录会话对象
     * @param campaign 活动提交表单实体
     * @param result 参数校验结果
     * @return 创建成功的广告活动实体响应
     */
    @ApiOperation(value = "管理员代建广告活动", notes = "管理员协助指定广告主直接创建广告活动，绕过普通广告主越权判定，但需保证广告主已通过资质审核")
    @JsonView(ListView.class)
    @PostMapping
    public Response add(@ApiParam(value = "所属广告主ID", required = true) @RequestParam Integer advId,
                        @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                        @ApiParam(value = "广告活动表单参数", required = true) @Validated(Create.class) @RequestBody SubmitCampaign campaign,
                        BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        Advertiser currentAdv = advRepo.getById(advId).orElse(loginAdv);
        if (!currentAdv.getReview().equals(Advertiser.Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        return Response.ok("campaign", campaignService.createCampaign(advId, campaign, true));
    }

    /**
     * 管理员特权修改广告活动
     *
     * @param advId 广告主ID
     * @param loginAdv 当前登录会话对象
     * @param campaign 活动表单实体
     * @param result 参数校验结果
     * @return 修改后的活动实体
     */
    @ApiOperation(value = "管理员修改广告活动", notes = "管理员修改全平台任意广告主的广告活动参数")
    @PutMapping
    public Response update(@ApiParam(value = "所属广告主ID", required = true) @RequestParam Integer advId,
                           @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                           @ApiParam(value = "广告活动表单参数", required = true) @Validated(Update.class) @RequestBody SubmitCampaign campaign,
                           BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        Advertiser currentAdv = advRepo.getById(advId).orElse(loginAdv);
        if (!currentAdv.getReview().equals(Advertiser.Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        return Response.ok("campaign", campaignService.updateCampaign(advId, campaign, true));
    }

    /**
     * 管理员全平台分页查询广告活动
     *
     * @param params 过滤参数
     * @param page 分页参数
     * @return 全量广告活动分页列表
     */
    @ApiOperation(value = "管理员全景查询广告活动", notes = "支持跨广告主检索系统内全部广告活动列表")
    @JsonView(ListView.class)
    @GetMapping
    public Response list(@ApiParam(value = "过滤条件Map") @RequestParam Map<String, Object> params,
                         @ApiParam(value = "分页参数") Pageable page) {
        Page<Campaign> campaigns = campaignService.getCampaigns(params, page);
        return Response.ok("list", campaigns.getContent()).add("total", campaigns.getTotalElements());
    }

    /**
     * 管理员获取活动详情
     *
     * @param id 活动ID
     * @param advId 广告主ID
     * @return 广告活动完整实体
     */
    @ApiOperation(value = "管理员获取活动详情", notes = "获取指定广告活动的完整配置与关联定向详情")
    @JsonView(EditView.class)
    @GetMapping(path = "/{id}")
    public Response get(@ApiParam(value = "广告活动ID", required = true) @PathVariable Integer id,
                        @ApiParam(value = "所属广告主ID", required = true) @RequestParam Integer advId) {
        return Response.ok("campaign", campaignService.getCampaignWithAll(id, advId));
    }
}
