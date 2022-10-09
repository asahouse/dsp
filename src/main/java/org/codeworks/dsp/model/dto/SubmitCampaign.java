package org.codeworks.dsp.model.dto;

import org.codeworks.dsp.model.entities.Campaign;

import java.util.List;

/**
 * Created by benjaminkc on 16/10/21.
 */
public class SubmitCampaign{

    private Campaign campaign;

    private List<SubmitCampaignObjective> campaignObjectives;


    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public List<SubmitCampaignObjective> getCampaignObjectives() {
        return campaignObjectives;
    }

    public void setCampaignObjectives(List<SubmitCampaignObjective> campaignObjectives) {
        this.campaignObjectives = campaignObjectives;
    }
}
