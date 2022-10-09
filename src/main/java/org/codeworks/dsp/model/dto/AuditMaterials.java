package org.codeworks.dsp.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 审核物料包装
 * Created by Luis on 2016/9/28.
 */
public class AuditMaterials implements Serializable {

    private List<AuditMaterial> create = new ArrayList<>();

    private List<AuditMaterial> update = new ArrayList<>();

    public List<AuditMaterial> getCreate() {
        return create;
    }

    public void setCreate(List<AuditMaterial> create) {
        this.create = create;
    }

    public List<AuditMaterial> getUpdate() {
        return update;
    }

    public void setUpdate(List<AuditMaterial> update) {
        this.update = update;
    }
}
