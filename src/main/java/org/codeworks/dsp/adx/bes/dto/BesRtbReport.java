package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * bes rtb报告
 * Created by Luis on 2016/9/13.
 */
public class BesRtbReport implements Serializable {

    private LocalDate showDate;

    private Integer availablePv;

    private Integer actualPv;

    private Double actualRate;

    private Integer competePv;

    private Double competeRate;

    private Integer overtimePv;

    private Double overtimeRate;

    private Integer parseErrorPv;

    private Double parseErrRate;

    private Integer giveUpPv;

    private Integer successPv;

    private Double successRate;

    private Integer invalidPv;

    private Integer failPv;

    private Integer adviewType;

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public Integer getAvailablePv() {
        return availablePv;
    }

    public void setAvailablePv(Integer availablePv) {
        this.availablePv = availablePv;
    }

    public Integer getActualPv() {
        return actualPv;
    }

    public void setActualPv(Integer actualPv) {
        this.actualPv = actualPv;
    }

    public Double getActualRate() {
        return actualRate;
    }

    public void setActualRate(Double actualRate) {
        this.actualRate = actualRate;
    }

    public Integer getCompetePv() {
        return competePv;
    }

    public void setCompetePv(Integer competePv) {
        this.competePv = competePv;
    }

    public Double getCompeteRate() {
        return competeRate;
    }

    public void setCompeteRate(Double competeRate) {
        this.competeRate = competeRate;
    }

    public Integer getOvertimePv() {
        return overtimePv;
    }

    public void setOvertimePv(Integer overtimePv) {
        this.overtimePv = overtimePv;
    }

    public Double getOvertimeRate() {
        return overtimeRate;
    }

    public void setOvertimeRate(Double overtimeRate) {
        this.overtimeRate = overtimeRate;
    }

    public Integer getParseErrorPv() {
        return parseErrorPv;
    }

    public void setParseErrorPv(Integer parseErrorPv) {
        this.parseErrorPv = parseErrorPv;
    }

    public Double getParseErrRate() {
        return parseErrRate;
    }

    public void setParseErrRate(Double parseErrRate) {
        this.parseErrRate = parseErrRate;
    }

    public Integer getGiveUpPv() {
        return giveUpPv;
    }

    public void setGiveUpPv(Integer giveUpPv) {
        this.giveUpPv = giveUpPv;
    }

    public Integer getSuccessPv() {
        return successPv;
    }

    public void setSuccessPv(Integer successPv) {
        this.successPv = successPv;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Integer getInvalidPv() {
        return invalidPv;
    }

    public void setInvalidPv(Integer invalidPv) {
        this.invalidPv = invalidPv;
    }

    public Integer getFailPv() {
        return failPv;
    }

    public void setFailPv(Integer failPv) {
        this.failPv = failPv;
    }

    public AdViewType getAdviewType() {
        return AdViewType.fromValue(adviewType);
    }

    public void setAdviewType(AdViewType adviewType) {
        this.adviewType = adviewType.toValue();
    }

    public enum AdViewType {
        web(1), mobile(2), video(3);

        private Integer value;

        AdViewType(Integer value) {
            this.value = value;
        }

        public static AdViewType fromValue(Integer value) {
            if (value != null) {
                for (AdViewType adViewType : values()) {
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
