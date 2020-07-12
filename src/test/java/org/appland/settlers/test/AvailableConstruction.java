package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AvailableConstruction {

    public PossibleBuildings getAvailableBuilding() {
        return building;
    }

    public PossibleFlag getAvailableFlag() {
        return flag;
    }

    public void setFlagNotPossible() {
        flag = PossibleFlag.NO_FLAG_POSSIBLE;
    }

    public enum PossibleBuildings {
        LARGE_POSSIBLE,
        MEDIUM_POSSIBLE,
        SMALL_POSSIBLE,
        MINE_POSSIBLE,
        NO_BUILDING_POSSIBLE
    }

    public enum PossibleFlag {
        FLAG_POSSIBLE,
        NO_FLAG_POSSIBLE
    }

    PossibleBuildings building;
    PossibleFlag flag;
    private final Point position;

    public AvailableConstruction(PossibleBuildings building, PossibleFlag flag, Point position) {
        this.building = building;
        this.flag = flag;
        this.position = position;
    }

    public void setFlagPossible() {
        flag = PossibleFlag.FLAG_POSSIBLE;
    }

    public void setAvailableBuilding(PossibleBuildings possibleBuilding) {
        building = possibleBuilding;
    }

}
