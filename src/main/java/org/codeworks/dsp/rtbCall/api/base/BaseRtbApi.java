package org.codeworks.dsp.rtbCall.api.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codeworks.dsp.rtbCall.RtbConstant;
import org.codeworks.dsp.rtbCall.dto.request.BaseRtbRequest;
import org.codeworks.dsp.rtbCall.dto.response.BaseRtbResponse;
import org.codeworks.dsp.rtbCall.exception.RtbApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by benjaminkc on 16/10/23.
 */
public abstract class BaseRtbApi {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    protected RtbConstant rtbConstant;

    @Autowired
    protected ObjectMapper mapper;

    protected <S extends String, T extends BaseRtbResponse> ResponseEntity<T> post(String url, S request, Class<T> responseType) {
        return methodInvokeWithJsonRequest(url, request, responseType, HttpMethod.POST);
    }

    protected <S extends BaseRtbRequest, T extends BaseRtbResponse> ResponseEntity<T> post(String url, S request, Class<T> responseType) {
        return methodInvoke(url, request, responseType, HttpMethod.POST);
    }

    protected <S extends BaseRtbRequest, T extends BaseRtbResponse> ResponseEntity<T> put(String url, S request, Class<T> responseType) {
        return methodInvoke(url, request, responseType, HttpMethod.PUT);
    }

    protected <S extends String, T extends BaseRtbResponse> ResponseEntity<T> delete(String url, S request, Class<T> responseType) {
        return methodInvokeWithJsonRequest(url, request, responseType, HttpMethod.DELETE);
    }

    protected <S extends BaseRtbRequest, T extends BaseRtbResponse> ResponseEntity<T> delete(String url, S request, Class<T> responseType) {
        return methodInvoke(url, request, responseType, HttpMethod.DELETE);
    }

    protected <S extends BaseRtbRequest, T extends BaseRtbResponse> ResponseEntity<T> methodInvoke(String url, S request, Class<T> responseType, HttpMethod method) {
        return invoke(url, request, responseType, method);
    }

    protected <S extends String, T extends BaseRtbResponse> ResponseEntity<T> methodInvokeWithJsonRequest(String url, S request, Class<T> responseType, HttpMethod method) {
        return invoke(url, request, responseType, method);
    }

    protected ResponseEntity invoke(String url, Object request, Class responseType, HttpMethod method){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", rtbConstant.ACCEPT);
        headers.add("Content-Type", rtbConstant.CONTENT_TYPE);
        HttpEntity req = new HttpEntity(request, headers);
        return restTemplate.exchange(url, method, req, responseType);
    }

    protected <T extends BaseRtbResponse> boolean isValidResponse(ResponseEntity<T> response) {
        if (response == null)
            throw new RtbApiException("unknown error, not getting response");
        if (!response.getStatusCode().is2xxSuccessful())
            throw new RtbApiException("connecting error, status code " + response.getStatusCodeValue());

        return true;
    }
}
