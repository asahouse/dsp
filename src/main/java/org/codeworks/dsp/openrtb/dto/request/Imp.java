package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * OpenRTB 2.5 Impression object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Imp implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Banner banner;
    private Video video;

    @JsonProperty("native")
    private Native nativeObj;

    private String displaymanager;
    private String displaymanagerver;
    private Integer instl;
    private String tagid;
    private Double bidfloor;
    private String bidfloorcur;
    private Integer secure;
    private Map<String, Object> ext;

    public Imp() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Banner getBanner() { return banner; }
    public void setBanner(Banner banner) { this.banner = banner; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public Native getNativeObj() { return nativeObj; }
    public void setNativeObj(Native nativeObj) { this.nativeObj = nativeObj; }

    public String getDisplaymanager() { return displaymanager; }
    public void setDisplaymanager(String displaymanager) { this.displaymanager = displaymanager; }

    public String getDisplaymanagerver() { return displaymanagerver; }
    public void setDisplaymanagerver(String displaymanagerver) { this.displaymanagerver = displaymanagerver; }

    public Integer getInstl() { return instl; }
    public void setInstl(Integer instl) { this.instl = instl; }

    public String getTagid() { return tagid; }
    public void setTagid(String tagid) { this.tagid = tagid; }

    public Double getBidfloor() { return bidfloor; }
    public void setBidfloor(Double bidfloor) { this.bidfloor = bidfloor; }

    public String getBidfloorcur() { return bidfloorcur; }
    public void setBidfloorcur(String bidfloorcur) { this.bidfloorcur = bidfloorcur; }

    public Integer getSecure() { return secure; }
    public void setSecure(Integer secure) { this.secure = secure; }

    public Map<String, Object> getExt() { return ext; }
    public void setExt(Map<String, Object> ext) { this.ext = ext; }
}
