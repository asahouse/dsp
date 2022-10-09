package org.codeworks.dsp.service;

import org.codeworks.dsp.model.dto.AuditMaterial;
import org.codeworks.dsp.model.dto.AuditMaterials;
import org.codeworks.dsp.model.entities.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 审核业务接口
 * Created by Luis on 2016/9/23.
 */
public interface AuditService {

    Page<Material> getMaterialsWithAdv(Map<String, Object> params, Pageable page);

    void auditMaterials(List<AuditMaterial> auditMaterials);
}
