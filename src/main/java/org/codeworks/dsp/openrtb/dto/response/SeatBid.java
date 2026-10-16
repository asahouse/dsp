package org.codeworks.dsp.openrtb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.5 SeatBid object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeatBid implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Bid> bid;
    private String seat;
    private Integer group;
    private Map<String, Object> ext;

    public SeatBid() {}

    public SeatBid(List<Bid> bid, String seat) {
        this.bid = bid;
        this.seat = seat;
    }

    public List<Bid> getBid() { return bid; }
    public void setBid(List<Bid> bid) { this.bid = bid; }

    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }

    public Integer getGroup() { return group; }
    public void setGroup(Integer group) { this.group = group; }

    public Map<String, Object> getExt() { return ext; }
    public void setExt(Map<String, Object> ext) { this.ext = ext; }
}
