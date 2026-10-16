package org.codeworks.dsp.controller.site.campaign;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.dto.SubmitCampaign;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Advertiser.Review;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.campaign.EditView;
import org.codeworks.dsp.model.views.campaign.ListView;
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
 * 广告活动（Campaign）管理接口
 * <p>
 * 提供广告主自助创建、修改、分页查询以及获取广告活动详情的能力。
 * 具备广告主归属权限越权防护校验与资质审核通过状态检查。
 * </p>
 *
 * @author Luis, benjaminkc
 */
@Api(tags = "广告活动管理接口", description = "提供广告主维度的广告活动创建、更新、列表查询与详情获取")
@RequestMapping("/v2/campaign")
@RestController("/v2/campaign/CampaignController")
public class CampaignController extends AbstractController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private AdvertiserRepository advRepo;

    /**
     * 创建广告活动
     *
     * @param advId 目标广告主ID
     * @param loginAdv 当前登录会话中的广告主/管理员实体
     * @param campaign 广告活动提交数据载体
     * @param result 数据校验结果
     * @return 创建成功的广告活动实体响应
     */
    @ApiOperation(value = "创建广告活动", notes = "广告主创建新的广告活动，非管理员仅可为自身账号创建，且广告主资质必须已通过审核")
    @JsonView(ListView.class)
    @PostMapping
    public Response add(@ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                        @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                        @ApiParam(value = "广告活动创建参数", required = true) @Validated(Create.class) @RequestBody SubmitCampaign campaign,
                        BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));
        Advertiser currentAdv = advRepo.getById(advId).orElse(loginAdv);
        if (!currentAdv.getReview().equals(Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        return Response.ok("campaign", campaignService.createCampaign(advId, campaign, false));
    }

    /**
     * 更新广告活动
     *
     * @param advId 目标广告主ID
     * @param loginAdv 当前登录会话中的广告主/管理员实体
     * @param campaign 广告活动更新参数载体
     * @param result 数据校验结果
     * @return 更新后的广告活动实体响应
     */
    @ApiOperation(value = "更新广告活动", notes = "更新指定广告主旗下的广告活动设置，包含排期、预算与定向配置")
    @PutMapping
    public Response update(@ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                           @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                           @ApiParam(value = "广告活动更新参数", required = true) @Validated(Update.class) @RequestBody SubmitCampaign campaign,
                           BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));
        Advertiser currentAdv = advRepo.getById(advId).orElse(loginAdv);
        if (!currentAdv.getReview().equals(Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        return Response.ok("campaign", campaignService.updateCampaign(advId, campaign, false));
    }

    /**
     * 分页查询指定广告主的广告活动列表
     *
     * @param advId 目标广告主ID
     * @param loginAdv 当前登录会话中的广告主/管理员实体
     * @param params 附加查询参数（状态、名称等过滤项）
     * @param page 分页配置参数
     * @return 分页广告活动列表及总数量
     */
    @ApiOperation(value = "分页查询广告活动列表", notes = "查询指定广告主名下的所有广告活动，支持动态多条件组合过滤与分页")
    @JsonView(ListView.class)
    @GetMapping
    public Response list(@ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                         @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                         @ApiParam(value = "过滤条件参数Map") @RequestParam Map<String, Object> params,
                         @ApiParam(value = "分页参数") Pageable page) {
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));
        Page<Campaign> campaigns = campaignService.getCampaignsAtAdvId(advId, params, page);
        return Response.ok("list", campaigns.getContent()).add("total", campaigns.getTotalElements());
    }

    /**
     * 获取指定广告活动详情（含排期、频控与定向维度完整数据）
     *
     * @param id 广告活动ID
     * @param advId 广告主ID
     * @param loginAdv 当前登录实体
     * @return 完整的广告活动视图对象
     */
    @ApiOperation(value = "获取广告活动详情", notes = "根据ID和广告主ID获取广告活动的全部关联明细数据（用于编辑页面展示）")
    @JsonView(EditView.class)
    @GetMapping(path = "/{id}")
    public Response get(@ApiParam(value = "广告活动ID", required = true) @PathVariable Integer id,
                        @ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                        @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv) {
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));
        return Response.ok("campaign", campaignService.getCampaignWithAll(id, advId));
    }
}
