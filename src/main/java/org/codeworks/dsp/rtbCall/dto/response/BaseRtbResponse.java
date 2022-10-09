package org.codeworks.dsp.rtbCall.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Rtb基础响应
 * Created by benjaminkc on 16/10/24.
 */
public abstract class BaseRtbResponse implements Serializable {

    protected int status = 0;

    protected String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("message", message)
                .toString();
    }

    public enum Status {
        success(0), partSuccess(1), fail(2);

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public static BaseRtbResponse.Status fromValue(Integer value) {
            if (value != null) {
                for (BaseRtbResponse.Status status : values()) {
                    if (status.value.equals(value)) {
                        return status;
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
