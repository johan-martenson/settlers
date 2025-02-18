package org.appland.settlers.assets;

import java.util.Objects;

/**
 * Represents a key for an animation, combining a nation and compass direction.
 */
public record AnimationKey(Nation nation, CompassDirection compassDirection) {

    /**
     * Checks if this AnimationKey is equal to another object.
     *
     * @param o The object to compare with.
     * @return true if the other object is an AnimationKey and has the same nation and compass direction.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; // Check if the object is the same instance
        }

        if (!(o instanceof AnimationKey(Nation nation1, CompassDirection direction))) {
            return false; // Check if the object is of the same type
        }

        // Check if both the nation and compass direction match
        return nation == nation1 && compassDirection == direction;
    }

    /**
     * Computes the hash code based on the nation and compass direction.
     *
     * @return The computed hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nation, compassDirection);
    }
}
