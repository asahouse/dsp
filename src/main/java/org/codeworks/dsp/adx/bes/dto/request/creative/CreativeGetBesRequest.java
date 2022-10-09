package org.codeworks.dsp.adx.bes.dto.request.creative;

import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;

import java.io.Serializable;
import java.util.Set;

/**
 * bes根据id获取创意请求
 * Created by Luis on 2016/9/12.
 */
public class CreativeGetBesRequest extends BaseBesRequest implements Serializable {

    private Set<Integer> creativeIds;

    public CreativeGetBesRequest() {
    }

    public CreativeGetBesRequest(Set<Integer> creativeIds) {
        this.creativeIds = creativeIds;
    }

    public Set<Integer> getCreativeIds() {
        return creativeIds;
    }

    public void setCreativeIds(Set<Integer> creativeIds) {
        this.creativeIds = creativeIds;
    }
}
