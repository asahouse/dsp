package org.codeworks.dsp.rtbCall.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.codeworks.dsp.model.entities.*;
import org.codeworks.dsp.rtbCall.api.base.BaseRtbApi;
import org.codeworks.dsp.rtbCall.api.base.RtbApi;
import org.codeworks.dsp.rtbCall.dto.material.*;
import org.codeworks.dsp.rtbCall.dto.request.DeleteRtbRequest;
import org.codeworks.dsp.rtbCall.dto.request.SyncRtbRequest;
import org.codeworks.dsp.rtbCall.dto.response.RtbResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/10/24.
 */
@Component
public class MaterialRtbApi extends BaseRtbApi implements RtbApi<RtbMaterial, Material> {

    @Override
    public RtbResponse<RtbMaterial> sync(Material material) throws JsonProcessingException {
        return sync(Collections.singletonList(material));
    }

    @Override
    public RtbResponse<RtbMaterial> sync(List<Material> materials) throws JsonProcessingException{
        SyncRtbRequest req = new SyncRtbRequest(convert(materials));
        ResponseEntity<RtbResponse> resp = post(rtbConstant.getSYNC_MATERIAL_URL(), mapper.writeValueAsString(req.getRtbs()), RtbResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    @Override
    public RtbResponse<RtbMaterial> delete(List<Integer> ids) throws JsonProcessingException{
        DeleteRtbRequest req = new DeleteRtbRequest(ids);
        ResponseEntity<RtbResponse> resp = delete(rtbConstant.getSYNC_MATERIAL_URL(), mapper.writeValueAsString(req.getIds()), RtbResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    @Override
    public List<RtbMaterial> convert(List<Material> entities){
        if (!Optional.ofNullable(entities).isPresent() || entities.isEmpty()) return new ArrayList<>();

        return entities.stream().map(material -> {
            RtbMaterial rtb = new RtbMaterial();

            rtb.setId(material.getId());
            rtb.setWidth(material.getWidth());
            rtb.setHeight(material.getHeight());
            rtb.setTradeId(material.getTradeId());
            rtb.setLandingPage(material.getLandingUrl());

            if (Optional.ofNullable(material.getAdvertiser()).isPresent()){
                Advertiser advertiser = material.getAdvertiser();
                rtb.setAdvId(advertiser.getId());
            }

            if (Optional.ofNullable(material.getCampaign()).isPresent()){
                Campaign campaign = material.getCampaign();
                rtb.setCampaignId(campaign.getId());
            }

            return rtb;
        }).collect(Collectors.toList());
    }

}
