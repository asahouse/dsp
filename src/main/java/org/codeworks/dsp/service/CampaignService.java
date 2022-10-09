package org.codeworks.dsp.service;

import org.codeworks.dsp.model.dto.SubmitCampaign;
import org.codeworks.dsp.model.entities.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * 广告活动业务接口
 * Created by Luis on 2016/9/9.
 */
public interface CampaignService {

    Campaign createCampaign(Integer advId, SubmitCampaign campaign, Boolean isAdmin);

    Campaign updateCampaign(Integer advId, SubmitCampaign campaign, Boolean isAdmin);

    Page<Campaign> getCampaignsAtAdvId(Integer advId, Map<String, Object> params, Pageable page);

    Page<Campaign> getCampaigns(Map<String, Object> params, Pageable page);

    Campaign getCampaign(Integer id, Integer advId);

    Campaign getCampaign(Integer id);

    Campaign getCampaignWithAll(Integer id, Integer advId);

    Long countCampaign(Map<String, Object> params);

    Long countTotal();
}
