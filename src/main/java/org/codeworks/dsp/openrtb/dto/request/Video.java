package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * OpenRTB 2.5 Video object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> mimes;
    private Integer minduration;
    private Integer maxduration;
    private List<Integer> protocols;
    private Integer w;
    private Integer h;
    private Integer startdelay;
    private Integer placement;
    private Integer linearity;

    public Video() {}

    public List<String> getMimes() { return mimes; }
    public void setMimes(List<String> mimes) { this.mimes = mimes; }

    public Integer getMinduration() { return minduration; }
    public void setMinduration(Integer minduration) { this.minduration = minduration; }

    public Integer getMaxduration() { return maxduration; }
    public void setMaxduration(Integer maxduration) { this.maxduration = maxduration; }

    public List<Integer> getProtocols() { return protocols; }
    public void setProtocols(List<Integer> protocols) { this.protocols = protocols; }

    public Integer getW() { return w; }
    public void setW(Integer w) { this.w = w; }

    public Integer getH() { return h; }
    public void setH(Integer h) { this.h = h; }

    public Integer getStartdelay() { return startdelay; }
    public void setStartdelay(Integer startdelay) { this.startdelay = startdelay; }

    public Integer getPlacement() { return placement; }
    public void setPlacement(Integer placement) { this.placement = placement; }

    public Integer getLinearity() { return linearity; }
    public void setLinearity(Integer linearity) { this.linearity = linearity; }
}
