package org.codeworks.dsp.adx.bes.api;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codeworks.dsp.adx.bes.dto.BesCreative;
import org.codeworks.dsp.adx.bes.dto.BesCreative.AdViewType;
import org.codeworks.dsp.adx.bes.dto.BesCreative.Type;
import org.codeworks.dsp.adx.bes.dto.request.creative.CreativeGetAllBesRequest;
import org.codeworks.dsp.adx.bes.dto.request.creative.CreativeGetBesRequest;
import org.codeworks.dsp.adx.bes.dto.request.creative.CreativeSaveBesRequest;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeAuditBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeListBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeModifiedTradeBesResponse;
import org.codeworks.dsp.adx.bes.exception.BaiduEsApiException;
import org.codeworks.dsp.model.entities.Material;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 创意接口
 * Created by Luis on 2016/9/12.
 */
@Component
public class CreativeBesApi extends BaseBesApi {

    public CreativeListBesResponse add(Material material) {
        return add(Collections.singletonList(material));
    }

    public CreativeListBesResponse add(List<Material> materials) {
        return save(baiduEsConstant.getCREATIVE_ADD_URL(), materials);
    }

    public CreativeListBesResponse update(Material material) {
        return update(Collections.singletonList(material));
    }

    public CreativeListBesResponse update(List<Material> materials) {
        return save(baiduEsConstant.getCREATIVE_UPDATE_URL(), materials);
    }

    public CreativeListBesResponse getAll(LocalDate startDate, LocalDate endDate) {
        CreativeGetAllBesRequest req = new CreativeGetAllBesRequest(startDate, endDate);
        ResponseEntity<CreativeListBesResponse> resp = post(baiduEsConstant.getCREATIVE_GETALL_URL(), req, CreativeListBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public CreativeListBesResponse get(Set<Integer> materialIds) {
        CreativeGetBesRequest req = new CreativeGetBesRequest(materialIds);
        ResponseEntity<CreativeListBesResponse> resp = post(baiduEsConstant.getCREATIVE_GET_URL(), req, CreativeListBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public CreativeAuditBesResponse queryAuditState(Set<Integer> materialIds) {
        if (materialIds.size() > 100)
            throw new BaiduEsApiException("query maximum 100 materials at a time");
        CreativeGetBesRequest req = new CreativeGetBesRequest(materialIds);
        ResponseEntity<CreativeAuditBesResponse> resp =
                post(baiduEsConstant.getCREATIVE_QUERY_AUDIT_STATE_URL(), req, CreativeAuditBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    public CreativeModifiedTradeBesResponse getModifiedTradeMaterials(LocalDate startDate, LocalDate endDate) {
        if (startDate.plusDays(7).isAfter(endDate))
            throw new BaiduEsApiException("query maximum range is 7 days");
        CreativeGetAllBesRequest req = new CreativeGetAllBesRequest(startDate, endDate);
        ResponseEntity<CreativeModifiedTradeBesResponse> resp =
                post(baiduEsConstant.getCREATIVE_GET_TRADE_MODIFIED_URL(), req, CreativeModifiedTradeBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    private CreativeListBesResponse save(String url, List<Material> materials) {
        if (materials.size() > 10)
            throw new BaiduEsApiException("maximum 10 materials at a time");
        CreativeSaveBesRequest req = new CreativeSaveBesRequest(convertMaterials(materials));
        ResponseEntity<CreativeListBesResponse> resp = post(url, req, CreativeListBesResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    private List<BesCreative> convertMaterials(List<Material> materials) {
        return materials.stream().map(m -> {
            BesCreative besCreative = new BesCreative();
            besCreative.setAdvertiserId(m.getAdvertiser().getId());
            besCreative.setCreativeId(m.getId());
            besCreative.setAdviewType(AdViewType.web);
            besCreative.setType(Type.image);
            if (!FilenameUtils.isExtension(m.getUrl().toLowerCase(), "png"))
                besCreative.setCreativeUrl(m.getUrl());
            else {
                try {
                    URI pngURI = new URI(m.getUrl());
                    byte[] pngBytes = FileUtils.readFileToByteArray(new File(pngURI));
                    besCreative.setBinaryData(pngBytes);
                } catch (URISyntaxException e) {
                } catch (IOException e) {
                }
            }
            besCreative.setLandingPage(m.getLandingUrl());
            besCreative.setMonitorUrls(new ArrayList<>());
            besCreative.setWidth(m.getWidth());
            besCreative.setHeight(m.getHeight());
            besCreative.setCreativeTradeId(m.getTradeId());
            return besCreative;
        }).collect(Collectors.toList());
    }

}
