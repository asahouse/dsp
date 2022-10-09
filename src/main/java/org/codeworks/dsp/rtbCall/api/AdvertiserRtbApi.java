package org.codeworks.dsp.rtbCall.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.rtbCall.api.base.BaseRtbApi;
import org.codeworks.dsp.rtbCall.api.base.RtbApi;
import org.codeworks.dsp.rtbCall.dto.advertiser.RtbAdvertiser;
import org.codeworks.dsp.rtbCall.dto.request.DeleteRtbRequest;
import org.codeworks.dsp.rtbCall.dto.request.SyncRtbRequest;
import org.codeworks.dsp.rtbCall.dto.response.RtbResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/10/27.
 */
@Component
public class AdvertiserRtbApi extends BaseRtbApi implements RtbApi<RtbAdvertiser, Advertiser> {

    @Override
    public RtbResponse<RtbAdvertiser> sync(Advertiser advertiser) throws JsonProcessingException{
        return sync(Collections.singletonList(advertiser));
    }

    @Override
    public RtbResponse<RtbAdvertiser>  sync(List<Advertiser> advertisers) throws JsonProcessingException {
        SyncRtbRequest req = new SyncRtbRequest(convert(advertisers));
        ResponseEntity<RtbResponse> resp = post(rtbConstant.getSYNC_ADVERTISER_URL(), mapper.writeValueAsString(req.getRtbs()), RtbResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    @Override
    public RtbResponse<RtbAdvertiser>  delete(List<Integer> ids) throws JsonProcessingException{
        DeleteRtbRequest req = new DeleteRtbRequest(ids);
        ResponseEntity<RtbResponse> resp = delete(rtbConstant.getSYNC_ADVERTISER_URL(), mapper.writeValueAsString(req.getIds()), RtbResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    @Override
    public List<RtbAdvertiser> convert(List<Advertiser> entities){
        if (!Optional.ofNullable(entities).isPresent() || entities.isEmpty()) return new ArrayList<>();

        return entities.stream().map(advertiser -> {
            RtbAdvertiser rtb = new RtbAdvertiser();
                rtb.setId(advertiser.getId());
                rtb.setBalance(advertiser.getBalance());
            return rtb;
        }).collect(Collectors.toList());
    }
}
