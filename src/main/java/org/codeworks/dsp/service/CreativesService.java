package org.codeworks.dsp.service;

import org.codeworks.dsp.model.dto.Materials;
import org.codeworks.dsp.model.entities.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 创意业务接口
 * Created by Luis on 2016/8/29.
 */
public interface CreativesService {

    void createMaterials(Integer advId, Integer cId, Materials materials);

    List<Material> updateMaterials(Integer advId, Integer cId, Materials materials);

    Page<Material> getMaterials(Integer advId, Integer cId, Map<String, Object> params, Pageable page);

    Material getMaterial(Integer id, Integer advId, Integer cId);

    List<Material> getMaterials(List<Integer> ids);
}
