package org.codeworks.dsp.adx.bes.dto.request.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * bes报告查询请求
 * Created by Luis on 2016/9/13.
 */
public class ReportDateRangeBesRequest extends BaseBesRequest implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer adviewType;

    public ReportDateRangeBesRequest() {
    }

    public ReportDateRangeBesRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ReportDateRangeBesRequest(LocalDate startDate, LocalDate endDate, AdViewType adViewType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.setAdviewType(adViewType);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
