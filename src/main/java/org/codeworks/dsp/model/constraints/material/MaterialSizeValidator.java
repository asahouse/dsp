package org.codeworks.dsp.model.constraints.material;

import org.codeworks.dsp.model.entities.Material;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * 物料尺寸检查
 * Created by Luis on 2016/9/7.
 */
public class MaterialSizeValidator implements ConstraintValidator<MaterialConstraint, Material> {

    @Value("#{'${upload.image.size}'.split(',')}")
    private Set<String> restrictImageSizes;

    @Override
    public void initialize(MaterialConstraint materialConstraint) {
    }

    @Override
    public boolean isValid(Material material, ConstraintValidatorContext constraintValidatorContext) {
        if (restrictImageSizes.contains(String.format("%d*%d", material.getWidth(), material.getHeight())))
            return true;
        return false;
    }
}
