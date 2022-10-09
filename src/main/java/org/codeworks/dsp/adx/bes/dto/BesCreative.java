package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.List;

/**
 * bes创意
 * Created by Luis on 2016/9/12.
 */
public class BesCreative implements Serializable {

    private Integer creativeId;

    private Integer adviewType;

    private Integer type;

    private Integer state;

    private String creativeUrl;

    private byte[] binaryData;

    private String targetUrl;

    private String landingPage;

    private List<String> monitorUrls;

    private Integer width;

    private Integer height;

    private Integer creativeTradeId;

    private Integer advertiserId;

    private String frameAgreementNo;

    private Integer interactiveStyle;

    private String telNo;

    private String downloadUrl;

    private String appName;

    private String appDesc;

    private Float appPackageSize;

    private Integer dataRate;

    private Integer duration;

    private String playTimeMonitorUrl;

    public Integer getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
    }

    public AdViewType getAdviewType() {
        return AdViewType.fromValue(adviewType);
    }

    public void setAdviewType(AdViewType adviewType) {
        this.adviewType = adviewType.toValue();
    }

    public Type getType() {
        return Type.fromValue(type);
    }

    public void setType(Type type) {
        this.type = type.toValue();
    }

    public State getState() {
        return State.fromValue(state);
    }

    public void setState(State state) {
        this.state = state.toValue();
    }

    public String getCreativeUrl() {
        return creativeUrl;
    }

    public void setCreativeUrl(String creativeUrl) {
        this.creativeUrl = creativeUrl;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public List<String> getMonitorUrls() {
        return monitorUrls;
    }

    public void setMonitorUrls(List<String> monitorUrls) {
        this.monitorUrls = monitorUrls;
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

    public Integer getCreativeTradeId() {
        return creativeTradeId;
    }

    public void setCreativeTradeId(Integer creativeTradeId) {
        this.creativeTradeId = creativeTradeId;
    }

    public Integer getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Integer advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getFrameAgreementNo() {
        return frameAgreementNo;
    }

    public void setFrameAgreementNo(String frameAgreementNo) {
        this.frameAgreementNo = frameAgreementNo;
    }

    public InteractiveStyle getInteractiveStyle() {
        return InteractiveStyle.fromValue(interactiveStyle);
    }

    public void setInteractiveStyle(InteractiveStyle interactiveStyle) {
        this.interactiveStyle = interactiveStyle.toValue();
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public Float getAppPackageSize() {
        return appPackageSize;
    }

    public void setAppPackageSize(Float appPackageSize) {
        this.appPackageSize = appPackageSize;
    }

    public Integer getDataRate() {
        return dataRate;
    }

    public void setDataRate(Integer dataRate) {
        this.dataRate = dataRate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPlayTimeMonitorUrl() {
        return playTimeMonitorUrl;
    }

    public void setPlayTimeMonitorUrl(String playTimeMonitorUrl) {
        this.playTimeMonitorUrl = playTimeMonitorUrl;
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

    public enum Type {
        image(1), flash(2), video(3);

        private Integer value;

        Type(Integer value) {
            this.value = value;
        }

        public static Type fromValue(Integer value) {
            if (value != null) {
                for (Type type : values()) {
                    if (type.value.equals(value)) {
                        return type;
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

    public enum State {
        approved(0), pending(1), denied(2);

        private Integer value;

        State(Integer value) {
            this.value = value;
        }

        public static State fromValue(Integer value) {
            if (value != null) {
                for (State state : values()) {
                    if (state.value.equals(value)) {
                        return state;
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

    public enum InteractiveStyle {
        none(0), phoneDial(1), clickDownload(2);

        private Integer value;

        InteractiveStyle(Integer value) {
            this.value = value;
        }

        public static InteractiveStyle fromValue(Integer value) {
            if (value != null) {
                for (InteractiveStyle interactiveStyle : values()) {
                    if (interactiveStyle.value.equals(value)) {
                        return interactiveStyle;
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
