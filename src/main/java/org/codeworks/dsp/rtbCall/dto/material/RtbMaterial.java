package org.codeworks.dsp.rtbCall.dto.material;

import org.codeworks.dsp.rtbCall.dto.RtbDTO;

import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/24.
 */
public class RtbMaterial extends RtbDTO implements Serializable{

    private Integer id;
    private Integer campaignId;
    private Integer advId;
    private Integer width;
    private Integer height;
    private Integer tradeId;
    private String landingPage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getAdvId() {
        return advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }
}
