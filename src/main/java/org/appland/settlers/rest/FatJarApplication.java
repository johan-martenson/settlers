package org.appland.settlers.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.appland.settlers.rest.resource.SettlersAPI;

public class FatJarApplication extends Application {

    /*
     *
     * public Set<Class<?>> getClasses() { HashSet<Class<?>> set = new
     * HashSet<Class<?>>(); set.add(MessageResource.class); return set; }
     *
     */

    @Override
    public Set<Object> getSingletons() {
        HashSet<Object> set = new HashSet<>();
        //set.add(new MessageResource());
        try {
            set.add(new SettlersAPI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set;
    }
}
