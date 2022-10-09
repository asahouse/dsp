package org.codeworks.dsp.rtbCall.dto.request.material;

import org.codeworks.dsp.rtbCall.dto.material.RtbMaterial;
import org.codeworks.dsp.rtbCall.dto.request.BaseRtbRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/24.
 */
public class MaterialSyncRtbRequest extends BaseRtbRequest implements Serializable {

    public MaterialSyncRtbRequest(List<RtbMaterial> rtbMaterials){
        this.rtbMaterials = rtbMaterials;
    }

    private List<RtbMaterial> rtbMaterials;

    public List<RtbMaterial> getRtbMaterials() {
        return rtbMaterials;
    }

    public void setRtbMaterials(List<RtbMaterial> rtbMaterials) {
        this.rtbMaterials = rtbMaterials;
    }
}
