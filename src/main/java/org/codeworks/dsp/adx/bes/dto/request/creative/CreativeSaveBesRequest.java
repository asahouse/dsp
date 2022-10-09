package org.codeworks.dsp.adx.bes.dto.request.creative;

import org.codeworks.dsp.adx.bes.dto.BesCreative;
import org.codeworks.dsp.adx.bes.dto.request.BaseBesRequest;

import java.io.Serializable;
import java.util.List;

/**
 * bes创意创建/更新请求
 * Created by Luis on 2016/9/13.
 */
public class CreativeSaveBesRequest extends BaseBesRequest implements Serializable {

    private List<BesCreative> request;

    public CreativeSaveBesRequest() {
    }

    public CreativeSaveBesRequest(List<BesCreative> request) {
        this.request = request;
    }

    public List<BesCreative> getRequest() {
        return request;
    }

    public void setRequest(List<BesCreative> request) {
        this.request = request;
    }
}
