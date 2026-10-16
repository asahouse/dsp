package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * OpenRTB 2.5 Format object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Format implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer w;
    private Integer h;
    private Integer wext;
    private Integer hext;

    public Format() {}

    public Format(Integer w, Integer h) {
        this.w = w;
        this.h = h;
    }

    public Integer getW() { return w; }
    public void setW(Integer w) { this.w = w; }

    public Integer getH() { return h; }
    public void setH(Integer h) { this.h = h; }

    public Integer getWext() { return wext; }
    public void setWext(Integer wext) { this.wext = wext; }

    public Integer getHext() { return hext; }
    public void setHext(Integer hext) { this.hext = hext; }
}
