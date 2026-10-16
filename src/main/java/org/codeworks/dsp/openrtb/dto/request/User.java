package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * OpenRTB 2.5 User object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String buyeruid;
    private Integer yob;
    private String gender;
    private String keywords;
    private String customdata;
    private Geo geo;

    public User() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBuyeruid() { return buyeruid; }
    public void setBuyeruid(String buyeruid) { this.buyeruid = buyeruid; }

    public Integer getYob() { return yob; }
    public void setYob(Integer yob) { this.yob = yob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getCustomdata() { return customdata; }
    public void setCustomdata(String customdata) { this.customdata = customdata; }

    public Geo getGeo() { return geo; }
    public void setGeo(Geo geo) { this.geo = geo; }
}
