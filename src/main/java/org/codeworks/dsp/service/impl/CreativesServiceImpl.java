package org.codeworks.dsp.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.exception.CreativesException;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Materials;
import org.codeworks.dsp.model.entities.*;
import org.codeworks.dsp.model.entities.Material.Review;
import org.codeworks.dsp.service.CreativesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 创意业务接口实现
 * Created by Luis on 2016/8/29.
 */
@Service
public class CreativesServiceImpl implements CreativesService {

    @Autowired
    private AdvertiserRepository advRepo;

    @Autowired
    private MaterialRepository materialRepo;

    @Autowired
    private CampaignRepository campaignRepository;

    @Override
    public void createMaterials(Integer advId, Integer cId, Materials materials) {
        Optional<Campaign> campaignOpt = campaignRepository.findWithAllByIdAndAdvertiserId(cId, advId);
        if (campaignOpt.isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            List<Material> materialList = materials.getMaterials();
            materialList.forEach(m -> {
                m.setAdvertiser(new Advertiser(advId));
                m.setReview(Review.pending);
                m.setCampaign(campaignOpt.get());
                m.setCreateDate(now);
            });
            materialRepo.save(materialList);
        } else
            throw new CreativesException(ErrorCodes.no_such_category);
    }

    @Override
    public List<Material> updateMaterials(Integer advId, Integer cId, Materials materials) {

        List<Material> results = new ArrayList<>();

        Optional<Campaign> campaignOpt = campaignRepository.findWithAllByIdAndAdvertiserId(cId, advId);
        Optional<Advertiser> adverOpt = advRepo.getById(advId);

        if (campaignOpt.isPresent() || adverOpt.isPresent()) {

            List<Material> materialList = materials.getMaterials();
            for (int i = 0; i < materialList.size(); i++) {
                Material material = materialList.get(i);

                Optional<Material> dbMaterialOpt = materialRepo.findByIdAndCampaignId(material.getId(), cId);
                if (!dbMaterialOpt.isPresent())
                    throw new CreativesException(ErrorCodes.no_such_material, new Object[]{String.format("index:%d", i)});

                Material dbMaterial = dbMaterialOpt.get();

                if (dbMaterial.getReview().equals(Review.processing))
                    throw new CreativesException(ErrorCodes.prohibit_modify_material, new Object[]{String.format("index:%d", i)});

                if (dbMaterial.getReview().equals(Review.ineligibleForAdx))
                    dbMaterial.setReview(Review.updatePending);
                else if (dbMaterial.getReview().equals(Review.ineligibleForAdmin)
                        || dbMaterial.getReview().equals(Review.eligible))
                    dbMaterial.setReview(Review.pending);

                BeanUtils.copyProperties(material, dbMaterial,
                        "id", "url", "review", "width", "height", "tradeId", "type", "createDate");

                dbMaterial.setCampaign(campaignOpt.get());
                dbMaterial.setAdvertiser(adverOpt.get());

                materialRepo.save(dbMaterial);
                results.add(dbMaterial);
            }
        } else
            throw new CreativesException(ErrorCodes.no_such_category);

        return results;
    }

    @Override
    public Page<Material> getMaterials(Integer advId, Integer cId, Map<String, Object> params, Pageable page) {
        Optional<Campaign> campaignOpt = campaignRepository.findWithAllByIdAndAdvertiserId(cId, advId);
        if (campaignOpt.isPresent()) {
            Predicate predicate = buildMaterialPredicate(cId, params);
            return materialRepo.findAll(predicate, page);
        } else
            throw new CreativesException(ErrorCodes.no_such_category);
    }

    @Override
    public Material getMaterial(Integer id, Integer advId, Integer cId) {
        Optional<Material> materialOpt = materialRepo.findByIdAndAdvertiserIdAndCampaignId(id, advId, cId);
        if (materialOpt.isPresent()) {
            return materialOpt.get();
        } else
            throw new CreativesException(ErrorCodes.no_such_material, new Object[]{""});
    }

    @Override
    public List<Material> getMaterials(List<Integer> ids){
        return materialRepo.findByIdIn(ids);
    }

    private Predicate buildMaterialPredicate(Integer cId, Map<String, Object> params) {
        BooleanBuilder builder = new BooleanBuilder();
        QMaterial qMaterial = QMaterial.material;

        builder.and(qMaterial.campaign.id.eq(cId));
        if (params.containsKey("status"))
            builder.and(qMaterial.status.eq(Integer.valueOf((String) params.get("status"))));
        if (params.containsKey("review"))
            builder.and(qMaterial.review.eq(Integer.valueOf((String) params.get("review"))));
        if (params.containsKey("type"))
            builder.and(qMaterial.type.eq(Integer.valueOf((String) params.get("type"))));
        if (params.containsKey("size")) {
            String[] size = ((String) params.get("size")).split("\\*");
            builder.and(qMaterial.width.eq(Integer.valueOf(size[0])));
            builder.and(qMaterial.height.eq(Integer.valueOf(size[1])));
        }
        if (params.containsKey("name"))
            builder.and(qMaterial.name.likeIgnoreCase(String.format("%%s%%", params.get("name"))));
        if (params.containsKey("landingUrl"))
            builder.and(qMaterial.landingUrl.likeIgnoreCase(String.format("%%s%%", params.get("landingUrl"))));

        return builder;
    }
}
