package org.codeworks.dsp.rtbCall.dto.material;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/24.
 */
public class RtbMobileDevice implements Serializable {
    private List<Integer> carrier;
    private List<Integer> networkType;
    private List<Integer> os;

    public List<Integer> getCarrier() {
        return carrier;
    }

    public void setCarrier(List<Integer> carrier) {
        this.carrier = carrier;
    }

    public List<Integer> getNetworkType() {
        return networkType;
    }

    public void setNetworkType(List<Integer> networkType) {
        this.networkType = networkType;
    }

    public List<Integer> getOs() {
        return os;
    }

    public void setOs(List<Integer> os) {
        this.os = os;
    }
}
