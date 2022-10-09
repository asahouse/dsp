package org.codeworks.dsp.adx.bes.api;

import org.codeworks.dsp.adx.bes.BaiduEsConstant;
import org.codeworks.dsp.adx.bes.dto.request.AuthHeader;
import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;
import org.codeworks.dsp.adx.bes.dto.response.BaseBesResponse;
import org.codeworks.dsp.adx.bes.exception.BaiduEsApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * bes api基础接口
 * Created by Luis on 2016/9/12.
 */
public abstract class BaseBesApi {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    protected BaiduEsConstant baiduEsConstant;

    protected <S extends BaseBesRequest, T extends BaseBesResponse> ResponseEntity<T> post(String url, S request, Class<T> responseType) {
        AuthHeader authHeader = new AuthHeader(baiduEsConstant.DSPID, baiduEsConstant.TOKEN);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", baiduEsConstant.ACCEPT);
        headers.add("Content-Type", baiduEsConstant.CONTENT_TYPE);
        request.setAuthHeader(authHeader);
        HttpEntity<S> req = new HttpEntity<>(request, headers);
        return restTemplate.exchange(url, HttpMethod.POST, req, responseType);
    }

    protected <T extends BaseBesResponse> boolean isValidResponse(ResponseEntity<T> response) {
        if (response == null)
            throw new BaiduEsApiException("unknown error, not getting response");
        if (!response.getStatusCode().is2xxSuccessful())
            throw new BaiduEsApiException("connecting error, status code " + response.getStatusCodeValue());

        return true;
    }
}
