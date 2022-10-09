package org.codeworks.dsp.service.impl;

import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.CategoryDictionaryRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.rtbCall.api.AdvertiserRtbApi;
import org.codeworks.dsp.rtbCall.api.CampaignRtbApi;
import org.codeworks.dsp.rtbCall.api.MaterialRtbApi;
import org.codeworks.dsp.rtbCall.dto.advertiser.RtbAdvertiser;
import org.codeworks.dsp.rtbCall.dto.campaign.RtbCampaign;
import org.codeworks.dsp.rtbCall.dto.material.RtbMaterial;
import org.codeworks.dsp.rtbCall.dto.response.RtbResponse;
import org.codeworks.dsp.rtbCall.exception.RtbApiException;
import org.codeworks.dsp.service.RtbCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/12/22.
 */
@Service
public class RtbCallServiceImpl implements RtbCallService{

    @Value("${rtb.api.active}")
    private boolean isActive;

    @Autowired
    private AdvertiserRepository adverRepo;

    @Autowired
    private MaterialRepository materialRepo;

    @Autowired
    private CategoryDictionaryRepository cdRepo;

    @Autowired
    private CampaignRepository campaignRepo;

    @Autowired
    private MaterialRtbApi materialRtbApi;

    @Autowired
    private AdvertiserRtbApi advertiserRtbApi;

    @Autowired
    private CampaignRtbApi campaignRtbApi;





    @Override
    public void syncMaterials(List<Material> materials) {
        if (isActive){
            if (!Optional.ofNullable(materials).isPresent()
                    || !materials.stream()
                    .filter(m -> !m.getReview().equals(Material.Review.eligible))
                    .collect(Collectors.toList()).isEmpty())
                throw new RtbApiException(ErrorCodes.rtb_material_error_verify);

            try{
                RtbResponse resp = materialRtbApi.sync(materials);
                if (resp.getStatus() == HttpStatus.OK.value()){
                    materials.stream().forEach(m -> {
                        m.setSync(Material.Sync.yet);
                        materialRepo.save(m);
                    });
                }
            }catch (Exception ex) {
                //JsonProcessingException
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }
        }
    }

    @Override
    public void deleteMaterials(List<Material> materials) {
        if (isActive){
            List<Integer> ids = materials.stream().map(m -> m.getId()).collect(Collectors.toList());
            if (ids.isEmpty()) return;

            try {
                RtbResponse<RtbMaterial> resp = materialRtbApi.delete(ids);
                if (resp.getStatus() == HttpStatus.OK.value()){
                    materials.stream().forEach(material -> {
                        material.setSync(Material.Sync.not);
                        materialRepo.save(material);
                    });
                }
            }catch (Exception ex) {
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }
        }
    }

    @Override
    public void syncAdvertiser(List<Advertiser> advertisers) {
        if (isActive){
            if (!Optional.ofNullable(advertisers).isPresent()
                    || !advertisers.stream()
                    .filter(m -> !m.getReview().equals(Advertiser.Review.eligible))
                    .collect(Collectors.toList()).isEmpty())
                throw new RtbApiException(ErrorCodes.rtb_advertiser_error_verify);

            try{
                RtbResponse resp = advertiserRtbApi.sync(advertisers);
                if (resp.getStatus() == HttpStatus.OK.value()){
                    advertisers.stream().forEach(adv -> {
                        adv.setSync(Advertiser.Sync.yet);
                        adverRepo.save(adv);
                    });
                }
            }catch (Exception ex) {
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }
        }
    }

    @Override
    public void deleteAdvertiser(List<Advertiser> advertisers) {
        if (isActive){
            List<Integer> ids = advertisers.stream().map(adv -> adv.getId()).collect(Collectors.toList());
            if (ids.isEmpty()) return;

            try {
                RtbResponse<RtbAdvertiser> resp = advertiserRtbApi.delete(ids);
                if (resp.getStatus() == HttpStatus.OK.value()){
                    advertisers.stream().forEach(advertiser -> {
                        advertiser.setSync(Advertiser.Sync.not);
                        adverRepo.save(advertiser);
                    });
                }
            }catch (Exception ex) {
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }
        }
    }

    @Override
    public void syncCampaign(List<Campaign> campaigns) {
        if (isActive){
            if (!Optional.ofNullable(campaigns).isPresent())
                throw new RtbApiException(ErrorCodes.no_such_campaign);

            try{
                RtbResponse resp = campaignRtbApi.sync(campaigns);
                if (resp.getStatus() == HttpStatus.OK.value()){
                    campaigns.stream().forEach(c -> {
                        c.setSync(Campaign.Sync.yet);
                        campaignRepo.save(c);
                    });
                }
            }catch (Exception ex) {
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }

        }
    }

    @Override
    public void deleteCampaign(List<Campaign> campaignses) {
        if (isActive){
            List<Integer> ids = campaignses.stream().map(m -> m.getId()).collect(Collectors.toList());
            if (ids.isEmpty()) return;

            try {
                RtbResponse<RtbCampaign> resp = campaignRtbApi.delete(ids);
                if (resp.getStatus() == HttpStatus.OK.value()){
                    campaignses.stream().forEach(campaign -> {
                        campaign.setSync(Campaign.Sync.not);
                        campaignRepo.save(campaign);
                    });
                }
            }catch (Exception ex) {
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }
        }
    }

    @Override
    public void scheduleSyncCampaignsOnAllowDate() {
        if (isActive){

            List<Campaign> campaigns = campaignRepo.findByStartDateBeforeAndEndDateAfterAndSync(
                    LocalDate.now(), LocalDate.now(), Campaign.Sync.not.toValue());

            try{
                if (Optional.ofNullable(campaigns).isPresent() && !campaigns.isEmpty()){
                    achieveAndInputCategoryDictionary(campaigns);
                    campaignRtbApi.sync(campaigns);
                }
            }catch (Exception ex) {
                throw new RtbApiException(ErrorCodes.rtb_sync_error);
            }


        }
    }


    private void achieveAndInputCategoryDictionary(List<Campaign> campaigns){
        campaigns.stream().forEach(campaign -> {
            campaign.getCampaignObjectives().stream().forEach(co -> {
                Optional<CategoryDictionary> cdOpt = cdRepo.findById(co.getCategoryDictionaryId());

                if (cdOpt.isPresent()) {
                    CategoryDictionary cd = cdOpt.get();
                    co.setCategoryDictionary(cd);
                }
            });
        });
    }
}
