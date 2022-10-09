package org.codeworks.dsp.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.codeworks.dsp.adx.bes.api.AdvertiserBesApi;
import org.codeworks.dsp.adx.bes.api.CreativeBesApi;
import org.codeworks.dsp.adx.bes.api.ReportBesApi;
import org.codeworks.dsp.adx.bes.dto.BesAdvertiser;
import org.codeworks.dsp.adx.bes.dto.BesAdvertiserAuditState;
import org.codeworks.dsp.adx.bes.dto.BesCreativeAuditState;
import org.codeworks.dsp.adx.bes.dto.response.advertiser.AdvertiserAuditBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.advertiser.AdvertiserListBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeAuditBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeListBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeModifiedTradeBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.AdvertiserReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.ConsumeReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.CreativeRTBReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.RtbReportBesResponse;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.service.BesApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * bes接口业务
 * Created by Luis on 2016/9/14.
 */
@Service
public class BesApiServiceImpl implements BesApiService {

    @Value("${bes.api.active}")
    private boolean isActive;

    @Autowired
    private AdvertiserRepository advRepo;

    @Autowired
    private MaterialRepository materialRepo;

    @Autowired
    private AdvertiserBesApi advertiserBesApi;

    @Autowired
    private CreativeBesApi creativeBesApi;

    @Autowired
    private ReportBesApi reportBesApi;


    @Override
    public Boolean addAdvertiser(Advertiser adv) {
        if (isActive) {
            AdvertiserListBesResponse res = advertiserBesApi.add(adv);
            return !res.getResponse().isEmpty();
        }else
            return false;
    }

    @Override
    public Boolean updateAdvertiser(Advertiser adv) {
        if (isActive) {
            AdvertiserListBesResponse res = advertiserBesApi.update(adv);
            return !res.getResponse().isEmpty();
        }else
            return false;
    }

    @Override
    public void addCreatives(List<Material> materialList) {
        if (isActive) {
            if (materialList.size() <= 10)
                creativeBesApi.add(materialList);
            else {
                int total = materialList.size();
                int pageSize = 10;
                int totalPages = (int) Math.ceil(total / pageSize);
                for (int i = 0; i < totalPages; i++) {
                    creativeBesApi.add(materialList.stream().limit(pageSize).skip(i * pageSize).collect(Collectors.toList()));
                }
            }
        }
    }

    @Override
    public void updateCreatives(List<Material> materialList) {
        if (isActive) {
            if (materialList.size() <= 10) {
                creativeBesApi.update(narrowMaterials(materialList));
            } else {
                int total = materialList.size();
                int totalPages = (int) Math.ceil(total / 10);
                List<Material> materials = narrowMaterials(materialList);
                for (int i = 0; i < totalPages; i++) {
                    creativeBesApi.update(materials.stream().limit(10).skip(i * total).collect(Collectors.toList()));
                }
            }
        }
    }

    @Override
    public Optional<RtbReportBesResponse> callRtbReport(LocalDate startDate, LocalDate endDate){
        if (isActive){
            return Optional.ofNullable(reportBesApi.rtb(startDate, endDate));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ConsumeReportBesResponse> callConsumeReport(LocalDate startDate, LocalDate endDate){
        if (isActive){
            return Optional.ofNullable(reportBesApi.consume(startDate, endDate));
        }
        return Optional.empty();
    }

    @Override
    public Optional<AdvertiserReportBesResponse> callAdvertiserReport(LocalDate startDate, LocalDate endDate){
        if (isActive){
            return Optional.ofNullable(reportBesApi.advertiser(startDate, endDate));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CreativeRTBReportBesResponse> callCreativeRTBReport(LocalDate startDate, LocalDate endDate){
        if (isActive){
            return Optional.ofNullable(reportBesApi.creativeRTB(startDate, endDate));
        }
        return Optional.empty();
    }

    @Override
    public List<Integer> scheduleCheckAndUpdateAdvertiser(){
        List<Integer> resultIds = new ArrayList<>();
        if (isActive) {
            List<Advertiser> advertisers = advRepo.findByReviewIs(Advertiser.Review.processing.toValue());
            if (advertisers.isEmpty()) return resultIds;

            AdvertiserAuditBesResponse resp =
                    advertiserBesApi.queryQualification(advertisers.stream().map(m -> m.getId()).collect(Collectors.toSet()));

            if (Optional.ofNullable(resp.getResponse()).isPresent()) {

                resultIds = resp.getResponse().stream()
                        .filter(m -> m.getState().equals(BesAdvertiserAuditState.State.approved))
                        .map(m -> m.getAdvertiserId()).collect(Collectors.toList());

                if (!resultIds.isEmpty())
                    advRepo.updateReview(Advertiser.Review.eligible, resultIds);

                resp.getResponse().stream().filter(m -> m.getState().equals(BesAdvertiserAuditState.State.denied))
                        .forEach(m -> {
                            if (Optional.ofNullable(m).isPresent() && !m.getRefuseReason().isEmpty()) {
                                String refuseReason = StringUtils.join(m.getRefuseReason(), "\n");
                                advRepo.updateRefuseReason(refuseReason, m.getAdvertiserId());
                                advRepo.updateReview(Advertiser.Review.ineligible, m.getAdvertiserId());
                            }
                        });
            }
        }
        return resultIds;
    }

    @Override
    public List<Integer> scheduleCheckAndUpdateCreatives(){
        List<Integer> resultIds = new ArrayList<>();
        if (isActive) {
            List<Material> materials = materialRepo.findByReviewIs(Material.Review.processing.toValue());
            if (materials.isEmpty()) return resultIds;

            CreativeAuditBesResponse resp =
                    creativeBesApi.queryAuditState(materials.stream().map(m -> m.getId()).collect(Collectors.toSet()));

            if (Optional.ofNullable(resp.getResponse()).isPresent()) {

                resultIds = resp.getResponse().stream()
                        .filter(m -> m.getState().equals(BesCreativeAuditState.State.approved))
                        .map(m -> m.getCreativeId()).collect(Collectors.toList());

                if (!resultIds.isEmpty())
                    materialRepo.updateReview(Material.Review.eligible, resultIds);

                resp.getResponse().stream().filter(m -> m.getState().equals(BesCreativeAuditState.State.denied))
                        .forEach(m -> {
                            if (Optional.of(m).isPresent() && !m.getRefuseReason().isEmpty()) {
                                String refuseReason = StringUtils.join(m.getRefuseReason(), "\n");
                                materialRepo.updateRefuseReason(refuseReason, m.getCreativeId());
                                materialRepo.updateReview(Material.Review.ineligibleForAdx, m.getCreativeId());
                            }
                        });

            }
        }
        return resultIds;
    }

    @Override
    public List<Integer> scheduleCheckModifiedCreatives(){
        List<Integer> resultIds = new ArrayList<>();
        if (isActive){
            CreativeModifiedTradeBesResponse resp =
                    creativeBesApi.getModifiedTradeMaterials(LocalDate.now().minusDays(7), LocalDate.now());

            if (Optional.ofNullable(resp.getResponse()).isPresent()){

                resp.getResponse().stream().forEach(m -> {
                    Optional<Material> materialOpt = Optional.ofNullable(materialRepo.findOne(m.getCreativeId()));
                    if (materialOpt.isPresent()
                            && materialOpt.get().getTradeId().equals(m.getOriCreativeTradeId()))
                        materialRepo.updateTradeId(m.getModifiedCreativeTradeId(), m.getCreativeId());
                });

                resultIds = resp.getResponse().stream()
                        .map(m -> m.getCreativeId()).collect(Collectors.toList());
            }
        }
        return resultIds;
    }

    @Override
    public List<Integer> scheduleSendAdvertiser(){
        List<Integer> result = new ArrayList<>();
        if (isActive){
            List<Advertiser> submits = advRepo.findByReviewIs(Advertiser.Review.waitingSubmit.toValue());
            List<Advertiser> modifies = advRepo.findByReviewIs(Advertiser.Review.waitingModify.toValue());

            AdvertiserListBesResponse submitsRes = advertiserBesApi.add(submits);
            AdvertiserListBesResponse modifyRes = advertiserBesApi.update(modifies);

            List<BesAdvertiser> combine = submitsRes.getResponse();
            combine.addAll(modifyRes.getResponse());

            Set<Integer> unduplicate = combine.stream().map(BesAdvertiser::getAdvertiserId).collect(Collectors.toSet());
            unduplicate.forEach(id -> {
                advRepo.updateReview(Advertiser.Review.processing, id);
            });

            return unduplicate.stream().collect(Collectors.toList());
        }else
            return result;
    }

    private List<Material> narrowMaterials(List<Material> materials) {
        //更新只更新landingPageUrl
        return materials.stream().map(m -> {
            Material material = new Material();
            material.setId(m.getId());
            material.setLandingUrl(m.getLandingUrl());
            return material;
        }).collect(Collectors.toList());
    }
}
