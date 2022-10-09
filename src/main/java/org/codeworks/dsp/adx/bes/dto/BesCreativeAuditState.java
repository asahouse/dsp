package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.List;

/**
 * bes静态创意检查状态
 * Created by Luis on 2016/9/13.
 */
public class BesCreativeAuditState implements Serializable {

    private Integer creativeId;

    private Integer state;

    private List<String> refuseReason;

    private Integer advertiserState;

    private Integer isVulgar;

    private BesSellerAuditState sellerAuditInfo;

    public Integer getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
    }

    public State getState() {
        return State.fromValue(state);
    }

    public void setState(State state) {
        this.state = state.toValue();
    }

    public List<String> getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(List<String> refuseReason) {
        this.refuseReason = refuseReason;
    }

    public AdvertiserState getAdvertiserState() {
        return AdvertiserState.fromValue(advertiserState);
    }

    public void setAdvertiserState(AdvertiserState advertiserState) {
        this.advertiserState = advertiserState.toValue();
    }

    public Vulgar getIsVulgar() {
        return Vulgar.fromValue(isVulgar);
    }

    public void setIsVulgar(Vulgar isVulgar) {
        this.isVulgar = isVulgar.toValue();
    }

    public BesSellerAuditState getSellerAuditInfo() {
        return sellerAuditInfo;
    }

    public void setSellerAuditInfo(BesSellerAuditState sellerAuditInfo) {
        this.sellerAuditInfo = sellerAuditInfo;
    }

    public enum State {
        /**
         * approved:认可
         * pending:待定
         * denied:拒绝
         */
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

    public enum AdvertiserState {
        approved(0), pending(1), denied(2), missingInformation(3);

        private Integer value;

        AdvertiserState(Integer value) {
            this.value = value;
        }

        public static AdvertiserState fromValue(Integer value) {
            if (value != null) {
                for (AdvertiserState advertiserState : values()) {
                    if (advertiserState.value.equals(value)) {
                        return advertiserState;
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

    public enum Vulgar {
        notTagged(0), high(1), medium(2), none(3);

        private Integer value;

        Vulgar(Integer value) {
            this.value = value;
        }

        public static Vulgar fromValue(Integer value) {
            if (value != null) {
                for (Vulgar vulgar : values()) {
                    if (vulgar.value.equals(value)) {
                        return vulgar;
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
