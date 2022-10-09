package org.codeworks.dsp.rtbCall.dto.request;


import org.codeworks.dsp.rtbCall.dto.RtbDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/27.
 */
public class SyncRtbRequest<T extends RtbDTO> extends BaseRtbRequest implements Serializable{

    public SyncRtbRequest(List<T> rtbs){
        this.rtbs = rtbs;
    }

    private List<T> rtbs;

    public List<T> getRtbs() {
        return rtbs;
    }

    public void setRtbs(List<T> rtbs) {
        this.rtbs = rtbs;
    }
}
