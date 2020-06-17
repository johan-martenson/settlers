package org.appland.settlers.model;

public enum Size {

    SMALL, MEDIUM, LARGE;

    /**
     * Compares this Size instance against the given Size and returns true if it can contain the given size
     *
     * @param size Instance of Size
     * @return true if this instance can contain the given size
     */
    public boolean contains(Size size) {

        if (this == LARGE) {
            return true;
        }

        if (this == MEDIUM) {
            if (size == LARGE) {
                return false;
            }

            return true;
        }

        if (this == SMALL && size != SMALL) {
            return false;
        }

        return true;
    }
}
