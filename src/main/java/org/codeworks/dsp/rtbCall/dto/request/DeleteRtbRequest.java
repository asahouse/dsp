package org.codeworks.dsp.rtbCall.dto.request;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/27.
 */
public class DeleteRtbRequest<T extends Integer> extends BaseRtbRequest implements Serializable {

    public DeleteRtbRequest(List<T> ids){
        this.ids = ids;
    }

    private List<T> ids;

    public List<T> getIds() {
        return ids;
    }

    public void setIds(List<T> ids) {
        this.ids = ids;
    }
}
