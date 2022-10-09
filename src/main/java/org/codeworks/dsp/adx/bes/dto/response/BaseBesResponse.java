package org.codeworks.dsp.adx.bes.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codeworks.dsp.adx.bes.dto.commons.APIError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * bes基础响应
 * Created by Luis on 2016/9/12.
 */
public class BaseBesResponse implements Serializable {

    protected int status = 0;

    protected List<APIError> errors = new ArrayList<>();

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public void setStatus(Status status) {
        this.status = status.toValue();
    }

    public List<APIError> getErrors() {
        return errors;
    }

    public void setErrors(List<APIError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("errors", errors)
                .toString();
    }

    public enum Status {
        success(0), partSuccess(1), fail(2);

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public static Status fromValue(Integer value) {
            if (value != null) {
                for (Status status : values()) {
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
