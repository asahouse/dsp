package org.codeworks.dsp.controller.site.creatives;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Materials;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.creatives.ListView;
import org.codeworks.dsp.model.views.creatives.Single;
import org.codeworks.dsp.service.CreativesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 广告物料（Creative / Material）管理接口
 * <p>
 * 提供广告主物料的批量创建、批量修改、列表查询以及单条详情获取。
 * 包含越权控制与广告主资质审核有效性检查。
 * </p>
 *
 * @author Luis, benjaminkc
 */
@Api(tags = "广告物料管理接口", description = "提供广告主创意物料的创建、修改、分页查询与详情获取")
@RequestMapping("/v2/creatives/material")
@RestController("/v2/creatives/MaterialController")
public class MaterialController extends AbstractController {

    @Autowired
    private CreativesService creativesService;

    @Autowired
    private AdvertiserRepository advRepo;

    /**
     * 批量创建广告物料
     *
     * @param advId 广告主ID
     * @param cId 关联的广告活动ID
     * @param loginAdv 当前登录广告主
     * @param materials 待创建的物料列表
     * @param result 数据校验结果
     * @return 操作成功响应
     */
    @ApiOperation(value = "批量创建物料", notes = "在指定的广告活动下批量创建创意物料（图片/Flash等），创建后进入审核流")
    @PostMapping
    public Response add(@ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                        @ApiParam(value = "广告活动ID", required = true) @RequestParam Integer cId,
                        @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                        @ApiParam(value = "物料集合对象", required = true) @Validated(Create.class) @RequestBody Materials materials,
                        BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));
        Advertiser currentAdv = advRepo.getById(advId).orElse(loginAdv);
        if (!currentAdv.getReview().equals(Advertiser.Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        creativesService.createMaterials(advId, cId, materials);
        return Response.ok();
    }

    /**
     * 批量更新广告物料
     *
     * @param advId 广告主ID
     * @param cId 关联的广告活动ID
     * @param loginAdv 当前登录广告主
     * @param materials 待更新的物料列表
     * @param result 数据校验结果
     * @return 操作成功响应
     */
    @ApiOperation(value = "批量更新物料", notes = "批量更新指定广告活动下的创意物料配置信息")
    @PutMapping
    public Response update(@ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                           @ApiParam(value = "广告活动ID", required = true) @RequestParam Integer cId,
                           @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                           @ApiParam(value = "物料集合对象", required = true) @Validated(Update.class) @RequestBody Materials materials,
                           BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));
        Advertiser currentAdv = advRepo.getById(advId).orElse(loginAdv);
        if (!currentAdv.getReview().equals(Advertiser.Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        creativesService.updateMaterials(advId, cId, materials);
        return Response.ok();
    }

    /**
     * 分页查询物料列表
     *
     * @param advId 广告主ID
     * @param cId 广告活动ID
     * @param loginAdv 当前登录广告主
     * @param params 附加查询参数
     * @param page 分页参数
     * @return 物料分页列表与总数
     */
    @ApiOperation(value = "分页查询物料列表", notes = "根据广告活动和广告主查询创意物料分页列表")
    @JsonView(ListView.class)
    @GetMapping
    public Response list(@ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                         @ApiParam(value = "广告活动ID", required = true) @RequestParam Integer cId,
                         @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                         @ApiParam(value = "附加过滤参数") @RequestParam Map<String, Object> params,
                         @ApiParam(value = "分页参数") Pageable page) {
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));

        Page<Material> materials = creativesService.getMaterials(advId, cId, params, page);
        return Response.ok("list", materials.getContent())
                .add("total", materials.getTotalElements());
    }

    /**
     * 获取单条物料详情
     *
     * @param id 物料ID
     * @param cId 广告活动ID
     * @param advId 广告主ID
     * @param loginAdv 当前登录广告主
     * @return 物料实体数据
     */
    @ApiOperation(value = "获取物料详情", notes = "根据物料ID、活动ID和广告主ID获取单一物料的详细配置与审核状态")
    @JsonView(Single.class)
    @GetMapping(path = "/{id}")
    public Response get(@ApiParam(value = "物料ID", required = true) @PathVariable Integer id,
                        @ApiParam(value = "广告活动ID", required = true) @RequestParam Integer cId,
                        @ApiParam(value = "广告主ID", required = true) @RequestParam Integer advId,
                        @ApiParam(hidden = true) @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv) {
        if (!loginAdv.getIsAdmin() && !loginAdv.getId().equals(advId))
            return Response.resp(org.springframework.http.HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()));

        return Response.ok("material", creativesService.getMaterial(id, advId, cId));
    }
}
