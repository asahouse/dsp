package org.codeworks.dsp.adx.bes.api;

import org.codeworks.dsp.adx.bes.dto.BesAdvertiser;
import org.codeworks.dsp.adx.bes.dto.request.advertiser.AdvertiserGetAllBesRequest;
import org.codeworks.dsp.adx.bes.dto.request.advertiser.AdvertiserGetBesRequest;
import org.codeworks.dsp.adx.bes.dto.request.advertiser.AdvertiserSaveBesRequest;
import org.codeworks.dsp.adx.bes.dto.response.advertiser.AdvertiserAuditBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.advertiser.AdvertiserListBesResponse;
import org.codeworks.dsp.adx.bes.exception.BaiduEsApiException;
import org.codeworks.dsp.model.entities.Advertiser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 广告主接口
 * Created by Luis on 2016/9/12.
 */
@Component
public class AdvertiserBesApi extends BaseBesApi {

    public AdvertiserListBesResponse add(Advertiser adv) {
        return add(Collections.singletonList(adv));
    }

    public AdvertiserListBesResponse add(List<Advertiser> advs) {
        return save(baiduEsConstant.getADVERTISER_ADD_URL(), advs);
    }

    public AdvertiserListBesResponse update(Advertiser adv) {
        return update(Collections.singletonList(adv));
    }

    public AdvertiserListBesResponse update(List<Advertiser> advs) {
        return save(baiduEsConstant.getADVERTISER_UPDATE_URL(), advs);
    }

    public AdvertiserListBesResponse getAll(LocalDate startDate, LocalDate endDate) {
        AdvertiserGetAllBesRequest req = new AdvertiserGetAllBesRequest(startDate, endDate);
        ResponseEntity<AdvertiserListBesResponse> resp = post(baiduEsConstant.getADVERTISER_GETALL_URL(), req, AdvertiserListBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public AdvertiserListBesResponse get(Set<Integer> advIds) {
        AdvertiserGetBesRequest req = new AdvertiserGetBesRequest(advIds);
        ResponseEntity<AdvertiserListBesResponse> resp = post(baiduEsConstant.getADVERTISER_GET_URL(), req, AdvertiserListBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public AdvertiserAuditBesResponse queryQualification(Set<Integer> advIds){
        AdvertiserGetBesRequest req = new AdvertiserGetBesRequest(advIds);
        ResponseEntity<AdvertiserAuditBesResponse> resp = post(baiduEsConstant.getADVERTISER_QUERY_QUALIFICATION_URL(), req, AdvertiserAuditBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    private AdvertiserListBesResponse save(String url, List<Advertiser> advs) {
        if (advs.size() > 5)
            throw new BaiduEsApiException("maximum 5 advertiser at a time");
        AdvertiserSaveBesRequest req = new AdvertiserSaveBesRequest(convertAdvs(advs));
        ResponseEntity<AdvertiserListBesResponse> resp = post(url, req, AdvertiserListBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    private List<BesAdvertiser> convertAdvs(List<Advertiser> advs) {
        return advs.stream().map(adv -> {
            BesAdvertiser besAdv = new BesAdvertiser();
            besAdv.setAdvertiserId(adv.getId());
            besAdv.setAdvertiserLiteName(adv.getQualification().getCompanyName());
            besAdv.setAdvertiserName(adv.getQualification().getLicence());
            besAdv.setSiteName(adv.getQualification().getSiteName());
            besAdv.setSiteUrl(adv.getQualification().getOfficialSite());
            return besAdv;
        }).collect(Collectors.toList());
    }
}
