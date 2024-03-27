package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HouseSize {

    Size size();

    Material[] material() default {};
}
