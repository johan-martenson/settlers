/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

/**
 *
 * @author johan
 */
public enum MassType {
    UNUSED(0),
    LAND(1),
    WATER(2);

    private final int id;

    MassType(int id) {
        this.id = id;
    }

    public static MassType massTypeFromInt(int type) {

        switch (type) {
            case 0:
                return MassType.UNUSED;
            case 1:
                return MassType.LAND;
            case 2:
                return MassType.WATER;
            default:
                return null;
        }
    }
}
