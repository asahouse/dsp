package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * bes广告主
 * Created by Luis on 2016/9/12.
 */
public class BesAdvertiser implements Serializable {

    private Integer advertiserId;

    private String advertiserLiteName;

    private String advertiserName;

    private String siteName;

    private String siteUrl;

    private String telephone;

    private String address;

    private Integer isWhiteUser;

    public Integer getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Integer advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getAdvertiserLiteName() {
        return advertiserLiteName;
    }

    public void setAdvertiserLiteName(String advertiserLiteName) {
        this.advertiserLiteName = advertiserLiteName;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WhiteUser getIsWhiteUser() {
        return WhiteUser.fromValue(isWhiteUser);
    }

    public void setIsWhiteUser(WhiteUser whiteUser) {
        this.isWhiteUser = whiteUser.toValue();
    }

    public enum WhiteUser {
        no(0), yes(1);

        private Integer value;

        WhiteUser(Integer value) {
            this.value = value;
        }

        public static WhiteUser fromValue(Integer value) {
            if (value != null) {
                for (WhiteUser whiteUser : values()) {
                    if (whiteUser.value.equals(value)) {
                        return whiteUser;
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
