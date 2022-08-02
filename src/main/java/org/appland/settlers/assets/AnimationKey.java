package org.appland.settlers.assets;

import java.util.Objects;

public class AnimationKey {
    private final Nation nation;
    private final CompassDirection compassDirection;

    public AnimationKey(Nation nation, CompassDirection compassDirection) {
        this.nation = nation;
        this.compassDirection = compassDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimationKey that = (AnimationKey) o;
        return nation == that.nation && compassDirection == that.compassDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nation, compassDirection);
    }

    @Override
    public String toString() {
        return "AnimationKey{" +
                "nation=" + nation +
                ", direction=" + compassDirection +
                '}';
    }
}
