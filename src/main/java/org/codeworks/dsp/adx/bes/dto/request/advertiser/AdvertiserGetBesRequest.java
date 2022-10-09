package org.codeworks.dsp.adx.bes.dto.request.advertiser;

import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;

import java.io.Serializable;
import java.util.Set;

/**
 * bes获取根据id获取广告主
 * Created by Luis on 2016/9/12.
 */
public class AdvertiserGetBesRequest extends BaseBesRequest implements Serializable {

    private Set<Integer> advertiserIds;

    public AdvertiserGetBesRequest() {
    }

    public AdvertiserGetBesRequest(Set<Integer> advIds) {
        this.advertiserIds = advIds;
    }

    public Set<Integer> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(Set<Integer> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }
}
