package org.codeworks.dsp.adx.bes.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * BesCreativeAuditState子对象
 * Created by Luis on 2016/9/13.
 */
public class BesSellerAuditState implements Serializable {

    private Integer sellerId;

    private Integer state;

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public State getState() {
        return State.fromValue(state);
    }

    public void setState(State state) {
        this.state = state.toValue();
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
}
