/**
 *
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author johan
 *
 */
public class Player {

    /* Buildings */
    List<Storage> storages;
    private String name;

    public Player(String n) {
        storages = new ArrayList<>();

        storages.add(new Storage());

        name = n;
    }

    public List<Storage> getStorages() {
        return storages;
    }

    public String getName() {
        return name;
    }
}
