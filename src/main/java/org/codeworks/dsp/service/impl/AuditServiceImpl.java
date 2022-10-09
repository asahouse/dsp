package org.codeworks.dsp.service.impl;

import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.dto.AuditMaterial;
import org.codeworks.dsp.model.dto.AuditMaterial.AuditReview;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.entities.Material.Review;
import org.codeworks.dsp.service.AuditService;
import org.codeworks.dsp.service.BesApiService;
import org.codeworks.dsp.service.specification.MaterialSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 审核业务接口实现
 * Created by Luis on 2016/9/23.
 */
@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private MaterialRepository materialRepo;

    @Autowired
    private BesApiService besApiService;

    @Override
    public Page<Material> getMaterialsWithAdv(Map<String, Object> params, Pageable page) {
        return materialRepo.findAll(MaterialSpecification.auditList(params), page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMaterials(List<AuditMaterial> auditMaterials) {

        List<Material> deliverMaterials = new ArrayList<>();
        List<Material> rejectMaterials = new ArrayList<>();

        // 分拣物料
        pickMaterials(auditMaterials, deliverMaterials, rejectMaterials);

        // 执行送审
        if (deliverMaterials.size() > 0) {
            postAuditMaterial(deliverMaterials);
        }

        // 执行退回
        if (rejectMaterials.size() > 0){
            rejectMaterial(rejectMaterials);
        }
    }

    private void pickMaterials(List<AuditMaterial> rawMaterials, List<Material> deliverMaterials, List<Material> rejectMaterials) {
        for (AuditMaterial auditMaterial : rawMaterials) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", auditMaterial.getId());

            Optional<Material> dbMaterialOpt = Optional.ofNullable(materialRepo.findOne(MaterialSpecification.findOne(params)));

            if (!dbMaterialOpt.isPresent()) continue;

            Material dbMaterial = dbMaterialOpt.get();

            // 修改了创意行业代码,设置新的
            if (auditMaterial.getTradeId() != null)
                dbMaterial.setTradeId(auditMaterial.getTradeId());

            // 分配退回
            if (auditMaterial.getReview().equals(AuditReview.ineligible)) {
                rejectMaterials.add(dbMaterial);
                continue;
            }

            // 物料所属活动缺少SiteId,进行退回
            if (dbMaterial.getCampaign().getAnalysisSiteId() <= 0) {
                rejectMaterials.add(dbMaterial);
                continue;
            }

            // 分配送审
            if (auditMaterial.getReview().equals(AuditReview.eligible)) {
                deliverMaterials.add(dbMaterial);
                continue;
            }

        }
    }

    private void postAuditMaterial(List<Material> deliverMaterials){
        if (deliverMaterials.size() <= 0) return;

        //发送百度送审
        besApiService.addCreatives(deliverMaterials.stream().filter(
            m -> m.getReview().equals(Review.pending)).collect(Collectors.toList()));

        besApiService.updateCreatives(deliverMaterials.stream().filter(
            m -> m.getReview().equals(Review.updatePending)).collect(Collectors.toList()));

        //变更状态
        List<Material> updateList = deliverMaterials.stream().map(m ->{
            Material material = new Material();
            BeanUtils.copyProperties(m, material);
            material.setReview(Review.processing);
            return material;
        }).collect(Collectors.toList());

        //执行更新
        updateMaterialsReview(updateList);
    }

    private void rejectMaterial(List<Material> rejectMaterials){
        if (rejectMaterials.size() <= 0) return;

        //更新状态
        List<Material> updateList = rejectMaterials.stream().map(m -> {
            Material material = new Material();
            BeanUtils.copyProperties(m, material);
            material.setReview(Review.ineligibleForAdmin);
            return material;
        }).collect(Collectors.toList());

        //执行更新
        updateMaterialsReview(updateList);
    }

    private void updateMaterialsReview(List<Material> materials) {
        if (materials.size() > 0)
            materialRepo.save(materials);
    }


}