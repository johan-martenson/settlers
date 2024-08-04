package org.appland.settlers.assets.utils;

import java.util.Set;

public class GameFiles {
    public record House (String name, int index, boolean underConstruction, boolean underConstructionShadow, boolean openDoor) {
        public House(String name, int index) {
            this(name, index, true, true, true);
        }

        public static House make(String name, int index, Missing... missingList) {
            var missing = Set.of(missingList);

            var underConstruction = !missing.contains(Missing.NO_UNDER_CONSTRUCTION);
            var underConstructionShadow = underConstruction & !missing.contains(Missing.NO_UNDER_CONSTRUCTION_SHADOW);
            var openDoor = !missing.contains(Missing.NO_OPEN_DOOR);

            return new House(name, index, underConstruction, underConstructionShadow, openDoor);
        }
    }

    public enum Missing {
        NO_UNDER_CONSTRUCTION,
        NO_UNDER_CONSTRUCTION_SHADOW,
        NO_OPEN_DOOR,
    }
}
