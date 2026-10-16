package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * OpenRTB 2.5 Native object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Native implements Serializable {
    private static final long serialVersionUID = 1L;

    private String request;
    private String ver;
    private List<Integer> api;
    private List<Integer> battr;

    public Native() {}

    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }

    public String getVer() { return ver; }
    public void setVer(String ver) { this.ver = ver; }

    public List<Integer> getApi() { return api; }
    public void setApi(List<Integer> api) { this.api = api; }

    public List<Integer> getBattr() { return battr; }
    public void setBattr(List<Integer> battr) { this.battr = battr; }
}
