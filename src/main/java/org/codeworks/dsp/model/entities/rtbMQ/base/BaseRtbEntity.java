package org.codeworks.dsp.model.entities.rtbMQ.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

/**
 * Created by benjaminkc on 16/12/19.
 */
@Data
@MappedSuperclass
public abstract class BaseRtbEntity extends AbstractRtbEntity {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "compressDate", nullable = false)
    private LocalDate compressDate;

    @Column(name="compressHour", nullable = false)
    private Integer compressHour;

    @Column(name="compressIds", nullable = false)
    private String compressIds;

    @Column(name="status")
    private Integer status;

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public void setStatus(Status status) {
        this.status = status.toValue();
    }

    public enum Status {
        /**
         * 暂未使用
         * normal:正常
         * unnormal:非正常
         */
        normal(0), unnormal(1);

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
