package org.codeworks.dsp.model.dto;

import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * 素材集合包装
 * Created by Luis on 2016/9/7.
 */
public class Materials implements Serializable {

    @Valid
    @Size(min = 1, max = 20, groups = {Create.class, Update.class})
    private LinkedList<Material> materials;

    public LinkedList<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(LinkedList<Material> materials) {
        this.materials = materials;
    }
}
