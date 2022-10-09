package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by benjaminkc on 16/11/24.
 */
public class BesCreativeRTBReport implements Serializable {

    private LocalDate showDate;

    private Integer creativeId;

    private Integer creativeType;

    private Double winRate;

    private Double loseRate;

    private Double sizeIncorrectRate;

    private Double adTypeIncorrectRate;

    private Double tradeRejectRate;

    private Double advertiserIncorrectRate;

    private Double minPriceBadRate;

    private Double advertiserRejectRate;

    private Double adRejectRate;

    private Double snippetIncorrectRate;

    private Double notWhitelistRate;

    private Double advertiserNotExistRate;

    private Double flashVerIncorrectRate;

    private Double idMisMatchRate;

    private Double incorrectOrderIdRate;

    private Double bidderAbandonBiddingRate;

    private Double bidderResponseDataErrorRate;

    private Double bidderInvalidHtmlSnippetRate;

    private Double bidderMissingCategoryRate;


    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public Integer getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
    }

    public Integer getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(Integer creativeType) {
        this.creativeType = creativeType;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public Double getLoseRate() {
        return loseRate;
    }

    public void setLoseRate(Double loseRate) {
        this.loseRate = loseRate;
    }

    public Double getSizeIncorrectRate() {
        return sizeIncorrectRate;
    }

    public void setSizeIncorrectRate(Double sizeIncorrectRate) {
        this.sizeIncorrectRate = sizeIncorrectRate;
    }

    public Double getAdTypeIncorrectRate() {
        return adTypeIncorrectRate;
    }

    public void setAdTypeIncorrectRate(Double adTypeIncorrectRate) {
        this.adTypeIncorrectRate = adTypeIncorrectRate;
    }

    public Double getTradeRejectRate() {
        return tradeRejectRate;
    }

    public void setTradeRejectRate(Double tradeRejectRate) {
        this.tradeRejectRate = tradeRejectRate;
    }

    public Double getAdvertiserIncorrectRate() {
        return advertiserIncorrectRate;
    }

    public void setAdvertiserIncorrectRate(Double advertiserIncorrectRate) {
        this.advertiserIncorrectRate = advertiserIncorrectRate;
    }

    public Double getMinPriceBadRate() {
        return minPriceBadRate;
    }

    public void setMinPriceBadRate(Double minPriceBadRate) {
        this.minPriceBadRate = minPriceBadRate;
    }

    public Double getAdvertiserRejectRate() {
        return advertiserRejectRate;
    }

    public void setAdvertiserRejectRate(Double advertiserRejectRate) {
        this.advertiserRejectRate = advertiserRejectRate;
    }

    public Double getAdRejectRate() {
        return adRejectRate;
    }

    public void setAdRejectRate(Double adRejectRate) {
        this.adRejectRate = adRejectRate;
    }

    public Double getSnippetIncorrectRate() {
        return snippetIncorrectRate;
    }

    public void setSnippetIncorrectRate(Double snippetIncorrectRate) {
        this.snippetIncorrectRate = snippetIncorrectRate;
    }

    public Double getNotWhitelistRate() {
        return notWhitelistRate;
    }

    public void setNotWhitelistRate(Double notWhitelistRate) {
        this.notWhitelistRate = notWhitelistRate;
    }

    public Double getAdvertiserNotExistRate() {
        return advertiserNotExistRate;
    }

    public void setAdvertiserNotExistRate(Double advertiserNotExistRate) {
        this.advertiserNotExistRate = advertiserNotExistRate;
    }

    public Double getFlashVerIncorrectRate() {
        return flashVerIncorrectRate;
    }

    public void setFlashVerIncorrectRate(Double flashVerIncorrectRate) {
        this.flashVerIncorrectRate = flashVerIncorrectRate;
    }

    public Double getIdMisMatchRate() {
        return idMisMatchRate;
    }

    public void setIdMisMatchRate(Double idMisMatchRate) {
        this.idMisMatchRate = idMisMatchRate;
    }

    public Double getIncorrectOrderIdRate() {
        return incorrectOrderIdRate;
    }

    public void setIncorrectOrderIdRate(Double incorrectOrderIdRate) {
        this.incorrectOrderIdRate = incorrectOrderIdRate;
    }

    public Double getBidderAbandonBiddingRate() {
        return bidderAbandonBiddingRate;
    }

    public void setBidderAbandonBiddingRate(Double bidderAbandonBiddingRate) {
        this.bidderAbandonBiddingRate = bidderAbandonBiddingRate;
    }

    public Double getBidderResponseDataErrorRate() {
        return bidderResponseDataErrorRate;
    }

    public void setBidderResponseDataErrorRate(Double bidderResponseDataErrorRate) {
        this.bidderResponseDataErrorRate = bidderResponseDataErrorRate;
    }

    public Double getBidderInvalidHtmlSnippetRate() {
        return bidderInvalidHtmlSnippetRate;
    }

    public void setBidderInvalidHtmlSnippetRate(Double bidderInvalidHtmlSnippetRate) {
        this.bidderInvalidHtmlSnippetRate = bidderInvalidHtmlSnippetRate;
    }

    public Double getBidderMissingCategoryRate() {
        return bidderMissingCategoryRate;
    }

    public void setBidderMissingCategoryRate(Double bidderMissingCategoryRate) {
        this.bidderMissingCategoryRate = bidderMissingCategoryRate;
    }

    public enum AdViewType {
        web(1), mobile(2), video(3);

        private Integer value;

        AdViewType(Integer value) {
            this.value = value;
        }

        public static BesCreativeRTBReport.AdViewType fromValue(Integer value) {
            if (value != null) {
                for (BesCreativeRTBReport.AdViewType adViewType : values()) {
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
