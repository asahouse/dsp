package org.codeworks.dsp.model.constraints.material;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 物料检查
 * Created by Luis on 2016/9/7.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaterialSizeValidator.class)
@Documented
public @interface MaterialConstraint {

    String message() default "{material.size.not.satisfy}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
