package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * OpenRTB 2.5 Banner object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Banner implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer w;
    private Integer h;
    private List<Format> format;
    private String id;
    private Integer pos;
    private List<Integer> battr;
    private List<Integer> btype;
    private List<String> mimes;
    private Integer topframe;
    private List<Integer> expdir;
    private List<Integer> api;
    private Integer vcm;

    public Banner() {}

    public Integer getW() { return w; }
    public void setW(Integer w) { this.w = w; }

    public Integer getH() { return h; }
    public void setH(Integer h) { this.h = h; }

    public List<Format> getFormat() { return format; }
    public void setFormat(List<Format> format) { this.format = format; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getPos() { return pos; }
    public void setPos(Integer pos) { this.pos = pos; }

    public List<Integer> getBattr() { return battr; }
    public void setBattr(List<Integer> battr) { this.battr = battr; }

    public List<Integer> getBtype() { return btype; }
    public void setBtype(List<Integer> btype) { this.btype = btype; }

    public List<String> getMimes() { return mimes; }
    public void setMimes(List<String> mimes) { this.mimes = mimes; }

    public Integer getTopframe() { return topframe; }
    public void setTopframe(Integer topframe) { this.topframe = topframe; }

    public List<Integer> getExpdir() { return expdir; }
    public void setExpdir(List<Integer> expdir) { this.expdir = expdir; }

    public List<Integer> getApi() { return api; }
    public void setApi(List<Integer> api) { this.api = api; }

    public Integer getVcm() { return vcm; }
    public void setVcm(Integer vcm) { this.vcm = vcm; }
}
