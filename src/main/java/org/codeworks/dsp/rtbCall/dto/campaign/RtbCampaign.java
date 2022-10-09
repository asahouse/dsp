package org.codeworks.dsp.rtbCall.dto.campaign;

import org.codeworks.dsp.rtbCall.dto.RtbDTO;
import org.codeworks.dsp.rtbCall.dto.material.RtbMobileDevice;
import org.codeworks.dsp.rtbCall.dto.material.RtbUserCategory;

import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/27.
 */
public class RtbCampaign extends RtbDTO implements Serializable {

    private Integer id;
    private Integer advdId;
    private Float bid;
    private RtbUserCategory userCategory;
    private Float budget;
    private RtbMobileDevice mobileDevice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdvdId() {
        return advdId;
    }

    public void setAdvdId(Integer advdId) {
        this.advdId = advdId;
    }

    public Float getBid() {
        return bid;
    }

    public void setBid(Float bid) {
        this.bid = bid;
    }

    public RtbUserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(RtbUserCategory userCategory) {
        this.userCategory = userCategory;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public RtbMobileDevice getMobileDevice() {
        return mobileDevice;
    }

    public void setMobileDevice(RtbMobileDevice mobileDevice) {
        this.mobileDevice = mobileDevice;
    }
}
