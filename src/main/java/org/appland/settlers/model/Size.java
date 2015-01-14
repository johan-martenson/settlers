package org.appland.settlers.model;

public enum Size {

    SMALL, MEDIUM, LARGE;

    /**
     * Compares two Size instances and returns true if the second instance
     * fits within the first instance.
     * 
     * @param s1 Instance of Size
     * @param s2 Instance of Size
     * @return true if s2 fits in s1
     */
    static public boolean contains(Size s1, Size s2) {
        if (s1 == s2) {
            return true;
        }

        if (s1 == MEDIUM) {
            if (s2 == LARGE) {
                return false;
            }

            return true;
        }

        return true;
    }
}
