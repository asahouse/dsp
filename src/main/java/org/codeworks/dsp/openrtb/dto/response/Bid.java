package org.codeworks.dsp.openrtb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.5 Bid object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bid implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String impid;
    private Double price;
    private String adid;
    private String nurl;
    private String burl;
    private String lurl;
    private String adm;
    private List<String> adomain;
    private String iurl;
    private String cid;
    private String crid;
    private List<String> cat;
    private Integer w;
    private Integer h;
    private String dealid;
    private Map<String, Object> ext;

    public Bid() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImpid() { return impid; }
    public void setImpid(String impid) { this.impid = impid; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getAdid() { return adid; }
    public void setAdid(String adid) { this.adid = adid; }

    public String getNurl() { return nurl; }
    public void setNurl(String nurl) { this.nurl = nurl; }

    public String getBurl() { return burl; }
    public void setBurl(String burl) { this.burl = burl; }

    public String getLurl() { return lurl; }
    public void setLurl(String lurl) { this.lurl = lurl; }

    public String getAdm() { return adm; }
    public void setAdm(String adm) { this.adm = adm; }

    public List<String> getAdomain() { return adomain; }
    public void setAdomain(List<String> adomain) { this.adomain = adomain; }

    public String getIurl() { return iurl; }
    public void setIurl(String iurl) { this.iurl = iurl; }

    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public String getCrid() { return crid; }
    public void setCrid(String crid) { this.crid = crid; }

    public List<String> getCat() { return cat; }
    public void setCat(List<String> cat) { this.cat = cat; }

    public Integer getW() { return w; }
    public void setW(Integer w) { this.w = w; }

    public Integer getH() { return h; }
    public void setH(Integer h) { this.h = h; }

    public String getDealid() { return dealid; }
    public void setDealid(String dealid) { this.dealid = dealid; }

    public Map<String, Object> getExt() { return ext; }
    public void setExt(Map<String, Object> ext) { this.ext = ext; }
}
