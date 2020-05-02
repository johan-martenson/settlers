package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AvailableConstruction {

    public static AvailableConstruction createFromPointOnMap(GameMap map, Point point, Player player) {
        Map<Point, Size> availableBuildings = map.getAvailableHousePoints(player);
        List<Point> availableMinePoints = map.getAvailableMinePoints(player);
        Collection<Point> availableFlagPoints = map.getAvailableFlagPoints(player);

        AvailableConstruction availableConstruction = new AvailableConstruction(PossibleBuildings.NO_BUILDING_POSSIBLE, PossibleFlag.NO_FLAG_POSSIBLE, point);

        if (availableBuildings.containsKey(point)) {
            Size availableBuilding = availableBuildings.get(point);

            if (availableBuilding == Size.LARGE) {
                availableConstruction.setAvailableBuilding(PossibleBuildings.LARGE_POSSIBLE);
            } else if (availableBuilding == Size.MEDIUM) {
                availableConstruction.setAvailableBuilding(PossibleBuildings.MEDIUM_POSSIBLE);
            } else if (availableBuilding == Size.SMALL) {
                availableConstruction.setAvailableBuilding(PossibleBuildings.SMALL_POSSIBLE);
            }
        }

        if (availableMinePoints.contains(point)) {
            availableConstruction.setAvailableBuilding(PossibleBuildings.MINE_POSSIBLE);
        }

        if (availableFlagPoints.contains(point)) {
            availableConstruction.setFlagPossible();
        }

        return availableConstruction;
    }

    public PossibleBuildings getAvailableBuilding() {
        return building;
    }

    public PossibleFlag getAvailableFlag() {
        return flag;
    }

    public void setMineNotPossible() {
        if (building == PossibleBuildings.MINE_POSSIBLE) {
            building = PossibleBuildings.NO_BUILDING_POSSIBLE;
        }
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

    public Point getPosition() {
        return position;
    }
}
