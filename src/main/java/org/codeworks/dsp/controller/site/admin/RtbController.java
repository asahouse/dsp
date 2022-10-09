package org.codeworks.dsp.controller.site.admin;

import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.service.BesApiService;
import org.codeworks.dsp.service.RtbCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/25.
 */
@RequestMapping("/v2/admin/rtb")
@RestController("/v2/admin/RtbController")
public class RtbController extends AbstractController {

    @Autowired
    private RtbCallService rtbCallService;

    @Autowired
    private AdvertiserRepository advertiserRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private BesApiService besApiService;

    @PostMapping(path="syncAdvertiser")
    public Response syncAdvertiser(@RequestParam ArrayList<Integer> ids) {
        List<Advertiser> advertisers = advertiserRepository.findByIdIn(ids);
        rtbCallService.syncAdvertiser(advertisers);
        return Response.ok();
    }

    @PostMapping(path="syncCampaign")
    public Response syncCampaign(@RequestParam ArrayList<Integer> ids) {
        List<Campaign> campaigns = campaignRepository.findByIdIn(ids);
        rtbCallService.syncCampaign(campaigns);
        return Response.ok();
    }

    @PostMapping(path="syncMaterial")
    public Response syncMaterial(@RequestParam ArrayList<Integer> ids) {
        List<Material> materials = materialRepository.findByIdIn(ids);
        rtbCallService.syncMaterials(materials);
        return Response.ok();
    }
}
