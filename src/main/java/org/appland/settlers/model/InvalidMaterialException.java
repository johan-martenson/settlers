package org.appland.settlers.model;

public class InvalidMaterialException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -313923316497082077L;

    public InvalidMaterialException(Material material) {
        super("This material can not be used: " + material);
    }
}
