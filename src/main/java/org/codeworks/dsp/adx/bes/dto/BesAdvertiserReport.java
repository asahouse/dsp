package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by benjaminkc on 16/11/24.
 */
public class BesAdvertiserReport implements Serializable{

    private Long advertiserId;

    private String advertiserName;

    private LocalDate showDate;

    private Integer srchs;

    private Integer clks;

    private Double cost;

    private Double ctr;

    private Double acp;

    private Double cpm;

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public Integer getSrchs() {
        return srchs;
    }

    public void setSrchs(Integer srchs) {
        this.srchs = srchs;
    }

    public Integer getClks() {
        return clks;
    }

    public void setClks(Integer clks) {
        this.clks = clks;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public Double getAcp() {
        return acp;
    }

    public void setAcp(Double acp) {
        this.acp = acp;
    }

    public Double getCpm() {
        return cpm;
    }

    public void setCpm(Double cpm) {
        this.cpm = cpm;
    }

    public enum AdViewType {
        web(1), mobile(2), video(3);

        private Integer value;

        AdViewType(Integer value) {
            this.value = value;
        }

        public static BesAdvertiserReport.AdViewType fromValue(Integer value) {
            if (value != null) {
                for (BesAdvertiserReport.AdViewType adViewType : values()) {
                    if (adViewType.value.equals(value)) {
                        return adViewType;
                    }
                }
            }
            return null;
        }

        @JsonValue
        public Integer toValue() {
            return value;
        }
    }
}
