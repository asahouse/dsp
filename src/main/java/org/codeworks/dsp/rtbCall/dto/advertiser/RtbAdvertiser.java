package org.codeworks.dsp.rtbCall.dto.advertiser;

import org.codeworks.dsp.rtbCall.dto.RtbDTO;

import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/27.
 */
public class RtbAdvertiser extends RtbDTO implements Serializable {

    private Integer id;
    private Double balance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
