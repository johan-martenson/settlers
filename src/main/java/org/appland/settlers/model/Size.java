package org.appland.settlers.model;

public enum Size {

    SMALL, MEDIUM, LARGE;

    /**
     * Compares two Size instances and returns true if the second instance
     * fits within the first instance.
     * 
     * @param available Instance of Size
     * @param needed Instance of Size
     * @return true if s2 fits in s1
     */
    static public boolean contains(Size available, Size needed) {

        if (available == null) {
            return false;
        }

        if (available == LARGE) {
            return true;
        }

        if (available == MEDIUM) {
            if (needed == LARGE) {
                return false;
            }

            return true;
        }

        if (available == SMALL && needed != SMALL) {
            return false;
        }

        return true;
    }
}
