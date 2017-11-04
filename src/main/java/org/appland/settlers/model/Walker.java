package org.appland.settlers.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface Walker {

    int speed(); /* Speed is defined as time to travel one road segment */
}
