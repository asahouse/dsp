package org.codeworks.dsp.adx.bes.dto.request.advertiser;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * bes获取所有广告主请求
 * Created by Luis on 2016/9/12.
 */
public class AdvertiserGetAllBesRequest extends BaseBesRequest implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public AdvertiserGetAllBesRequest() {
    }

    public AdvertiserGetAllBesRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
}
