package org.codeworks.dsp.service;

import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;

import java.util.List;

/**
 * Created by benjaminkc on 16/10/24.
 */
public interface RtbCallService {

    void syncMaterials(List<Material> materials);

    void deleteMaterials(List<Material> materials);

    void syncAdvertiser(List<Advertiser> advertisers);

    void deleteAdvertiser(List<Advertiser> advertisers);

    void syncCampaign(List<Campaign> campaigns);

    void deleteCampaign(List<Campaign> campaigns);

    void scheduleSyncCampaignsOnAllowDate();
}
