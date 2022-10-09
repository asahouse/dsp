package org.codeworks.dsp.controller.site.admin;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.AuditMaterial;
import org.codeworks.dsp.model.dto.AuditMaterials;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.views.creatives.AuditList;
import org.codeworks.dsp.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物料审核接口
 * Created by Luis on 2016/9/23.
 */
@RequestMapping("/v2/admin/material")
@RestController("/v2/admin/MaterialAuditController")
public class MaterialAuditController extends AbstractController {

    @Autowired
    private AuditService auditService;

    @JsonView(AuditList.class)
    @GetMapping(path = "/audit")
    public Response auditList(@RequestParam Map<String, Object> params, Pageable page) {
        Page<Material> materials = auditService.getMaterialsWithAdv(params, page);
        return Response.ok("list", materials.getContent())
                .add("total", materials.getTotalElements());
    }

    @PostMapping(path = "audit")
    public Response audit(@RequestBody List<AuditMaterial> auditMaterials) {
        auditService.auditMaterials(auditMaterials);
        return Response.ok();
    }
}
