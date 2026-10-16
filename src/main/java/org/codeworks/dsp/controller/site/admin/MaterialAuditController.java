package org.codeworks.dsp.controller.site.admin;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.AuditMaterial;
import org.codeworks.dsp.model.dto.AuditMaterials;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.views.creatives.AuditList;
import org.codeworks.dsp.service.AuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员广告物料审核接口
 * <p>
 * 提供运营管理端对广告主提交的广告物料进行人工审核、批量合规审批与驳回流转。
 * 审核通过后将触发与底层 RTB 引擎或外部 ADX 的物料信息同步。
 * </p>
 *
 * @author Luis, benjaminkc
 */
@Api(tags = "管理员物料审核接口", description = "提供创意物料的待审列表查询、批量审批通过与驳回流转")
@RequestMapping("/v2/admin/material")
@RestController("/v2/admin/MaterialAuditController")
public class MaterialAuditController extends AbstractController {

    @Autowired
    private AuditService auditService;

    /**
     * 分页查询待审核物料列表
     *
     * @param params 过滤参数
     * @param page 分页配置
     * @return 包含广告主信息的待审物料分页列表
     */
    @ApiOperation(value = "分页查询审核物料列表", notes = "获取待审或已审物料，关联广告主资质与活动上下文")
    @JsonView(AuditList.class)
    @GetMapping(path = "/audit")
    public Response auditList(@ApiParam(value = "过滤参数Map") @RequestParam Map<String, Object> params,
                             @ApiParam(value = "分页参数") Pageable page) {
        Page<Material> materials = auditService.getMaterialsWithAdv(params, page);
        return Response.ok("list", materials.getContent())
                .add("total", materials.getTotalElements());
    }

    /**
     * 批量执行物料审核
     *
     * @param auditMaterials 审核操作载体列表（含物料ID、审核结果、拒绝原因等）
     * @return 操作成功响应
     */
    @ApiOperation(value = "批量审核物料", notes = "对勾选的一组物料提交审核判定（通过或拒绝，附带拒绝理由）")
    @PostMapping(path = "audit")
    public Response audit(@ApiParam(value = "物料审核结果列表", required = true) @RequestBody List<AuditMaterial> auditMaterials) {
        auditService.auditMaterials(auditMaterials);
        return Response.ok();
    }
}
