package org.codeworks.dsp.rtbCall.dto.request.material;

import org.codeworks.dsp.rtbCall.dto.request.BaseRtbRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/24.
 */
public class MaterialDeleteRtbRequest extends BaseRtbRequest implements Serializable {

    public MaterialDeleteRtbRequest(List<Integer> ids){
        this.ids = ids;
    }

    private List<Integer> ids;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
