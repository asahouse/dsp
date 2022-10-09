package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/17.
 */
public class BesAdvertiserAuditState implements Serializable {

    private Integer advertiserId;

    private Integer state;

    private List<String> refuseReason;

    private BesSellerAuditState sellerAuditInfo;

    public Integer getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Integer advertiserId) {
        this.advertiserId = advertiserId;
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

        public static BesAdvertiserAuditState.State fromValue(Integer value) {
            if (value != null) {
                for (BesAdvertiserAuditState.State state : values()) {
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
}
