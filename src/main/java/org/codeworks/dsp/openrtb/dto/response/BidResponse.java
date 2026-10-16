package org.codeworks.dsp.openrtb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.5 Top-level BidResponse object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private List<SeatBid> seatbid;
    private String bidid;
    private String cur;
    private String customdata;
    private Integer nbr;
    private Map<String, Object> ext;

    public BidResponse() {}

    public BidResponse(String id, List<SeatBid> seatbid, String cur) {
        this.id = id;
        this.seatbid = seatbid;
        this.cur = cur;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<SeatBid> getSeatbid() { return seatbid; }
    public void setSeatbid(List<SeatBid> seatbid) { this.seatbid = seatbid; }

    public String getBidid() { return bidid; }
    public void setBidid(String bidid) { this.bidid = bidid; }

    public String getCur() { return cur; }
    public void setCur(String cur) { this.cur = cur; }

    public String getCustomdata() { return customdata; }
    public void setCustomdata(String customdata) { this.customdata = customdata; }

    public Integer getNbr() { return nbr; }
    public void setNbr(Integer nbr) { this.nbr = nbr; }

    public Map<String, Object> getExt() { return ext; }
    public void setExt(Map<String, Object> ext) { this.ext = ext; }
}
