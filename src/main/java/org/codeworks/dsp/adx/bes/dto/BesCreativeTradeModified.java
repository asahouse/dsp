package org.codeworks.dsp.adx.bes.dto;

import java.io.Serializable;

/**
 * bes修正过的创意及行业
 * Created by Luis on 2016/9/13.
 */
public class BesCreativeTradeModified implements Serializable {

    private Integer creativeId;

    private Integer oriCreativeTradeId;

    private Integer modifiedCreativeTradeId;

    public Integer getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
    }

    public Integer getOriCreativeTradeId() {
        return oriCreativeTradeId;
    }

    public void setOriCreativeTradeId(Integer oriCreativeTradeId) {
        this.oriCreativeTradeId = oriCreativeTradeId;
    }

    public Integer getModifiedCreativeTradeId() {
        return modifiedCreativeTradeId;
    }

    public void setModifiedCreativeTradeId(Integer modifiedCreativeTradeId) {
        this.modifiedCreativeTradeId = modifiedCreativeTradeId;
    }
}
