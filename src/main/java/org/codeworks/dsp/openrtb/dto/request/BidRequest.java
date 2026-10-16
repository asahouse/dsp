package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.5 Top-level BidRequest object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private List<Imp> imp;
    private Site site;
    private App app;
    private Device device;
    private User user;
    private Integer test;
    private Integer at;
    private Integer tmax;
    private List<String> wseat;
    private List<String> bcat;
    private List<String> badv;
    private List<String> bapp;
    private Map<String, Object> ext;

    public BidRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Imp> getImp() { return imp; }
    public void setImp(List<Imp> imp) { this.imp = imp; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public App getApp() { return app; }
    public void setApp(App app) { this.app = app; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getTest() { return test; }
    public void setTest(Integer test) { this.test = test; }

    public Integer getAt() { return at; }
    public void setAt(Integer at) { this.at = at; }

    public Integer getTmax() { return tmax; }
    public void setTmax(Integer tmax) { this.tmax = tmax; }

    public List<String> getWseat() { return wseat; }
    public void setWseat(List<String> wseat) { this.wseat = wseat; }

    public List<String> getBcat() { return bcat; }
    public void setBcat(List<String> bcat) { this.bcat = bcat; }

    public List<String> getBadv() { return badv; }
    public void setBadv(List<String> badv) { this.badv = badv; }

    public List<String> getBapp() { return bapp; }
    public void setBapp(List<String> bapp) { this.bapp = bapp; }

    public Map<String, Object> getExt() { return ext; }
    public void setExt(Map<String, Object> ext) { this.ext = ext; }
}
