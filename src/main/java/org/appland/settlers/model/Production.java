package org.appland.settlers.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Production {

    Material output();

    int productionTime() default 100;

    Material[] requiredGoods();
    
    boolean manualProduction() default false;
}
