/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author johan
 */
public class MapPoint {
    private static final int DEFAULT_AMOUNT_FISH   = 10;
    private static final int SMALL_AMOUNT_OF_MINERAL = 5;
    private static final int MEDIUM_AMOUNT_OF_MINERAL = 10;
    private static final int LARGE_AMOUNT_OF_MINERAL = 15;
    public static final int DEFAULT_HEIGHT = 10;

    private final Point      point;
    private final Set<Point> connectedNeighbors;
    private final Set<Road>  connectedRoads;
    private final Set<Point> connectedFlagsAndBuildings;

    private Building building;
    private Flag     flag;
    private Tree     tree;
    private Stone    stone;
    private Crop     crop;
    private Sign     sign;
    private int      height;
    private int      mineralAmount;
    private Material mineral;
    private int      fishAmount;
    private boolean  isDeadTree;
    private boolean  isShipyardAvailable;
    private boolean  isShip;

    public MapPoint(Point point) {
        this.point                 = point;
        building                   = null;
        flag                       = null;
        tree                       = null;
        stone                      = null;
        crop                       = null;
        sign                       = null;
        isDeadTree                 = false;
        isShipyardAvailable        = false;
        isShip                     = false;
        connectedNeighbors         = new HashSet<>();
        connectedRoads             = new HashSet<>();
        connectedFlagsAndBuildings = new HashSet<>();

        /* Set the default height */
        height = DEFAULT_HEIGHT;

        fishAmount = DEFAULT_AMOUNT_FISH;
    }

    void setBuilding(Building building) {
        this.building = building;
    }

    void setFlag(Flag flag) {
        this.flag = flag;
    }

    Flag getFlag() {
        return flag;
    }

    void addConnectingRoad(Road road) {
        Point previous = null;

        /* Find connected neighbors */
        for (Point point : road.getWayPoints()) { // FIXME: this hides a field
            if (point.equals(this.point) && previous != null) {
                connectedNeighbors.add(previous);
            }

            if (previous != null && previous.equals(this.point)) {
                connectedNeighbors.add(point);
            }

            previous = point;
        }

        connectedRoads.add(road);

        if (road.getEnd().equals(point)) {
            connectedFlagsAndBuildings.add(road.getStart());
        } else if (road.getStart().equals(point)) {
            connectedFlagsAndBuildings.add(road.getEnd());
        }
    }

    void removeConnectingRoad(Road road) {
        connectedRoads.remove(road);

        if (road.getEnd().equals(point)) {
            connectedFlagsAndBuildings.remove(road.getStart());
        } else  if (road.getStart().equals(point)) {
            connectedFlagsAndBuildings.remove(road.getEnd());
        }

        Point previous = null;

        for (Point current : road.getWayPoints()) {
            if (current.equals(point) && previous != null) {
                connectedNeighbors.remove(previous);
            }

            if (previous != null && previous.equals(point)) {
                connectedNeighbors.remove(current);
            }

            previous = current;
        }
    }

    @Override
    public String toString() {
        return "Map point " + point + " with " + building + " and " + flag;
    }

    public boolean isFlag() {
        return flag != null;
    }

    public boolean isRoad() {
        return !connectedNeighbors.isEmpty();
    }

    Set<Road> getConnectedRoads() {
        return connectedRoads;
    }

    List<Road> getConnectedRoadsAsList() {
        return new ArrayList<>(connectedRoads);
    }

    Set<Point> getConnectedNeighbors() {
        return connectedNeighbors;
    }

    Set<Point> getConnectedFlagsAndBuildings() {
        return connectedFlagsAndBuildings;
    }

    Building getBuilding() {
        return building;
    }

    Tree getTree() {
        return tree;
    }

    void setTree(Tree tree) {
        this.tree = tree;
    }

    void removeTree() {
        tree = null;
    }

    void setStone(Stone stone) {
        this.stone = stone;
    }

    Stone getStone() {
        return stone;
    }

    boolean isBuilding() {
        return building != null;
    }

    boolean isStone() {
        return stone != null;
    }

    boolean isTree() {
        return tree != null;
    }

    void setCrop(Crop crop) {
        this.crop = crop;
    }

    Crop getCrop() {
        return crop;
    }

    void removeFlag() {
        flag = null;
    }

    Sign getSign() {
        return sign;
    }

    void setSign(Sign sign) {
        this.sign = sign;
    }

    void removeBuilding() {
        building = null;
    }

    boolean isCrop() {
        return crop != null;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    boolean isSign() {
        return sign != null;
    }

    public boolean isBuildingOfSize(Size size) {
        return building != null && building.getSize() == size;
    }

    public void setMineralAmount(Material mineral, Size amount) {
        this.mineral = mineral;

        switch (amount) {
            case SMALL:
                mineralAmount = SMALL_AMOUNT_OF_MINERAL;
                break;
            case MEDIUM:
                mineralAmount = MEDIUM_AMOUNT_OF_MINERAL;
                break;
            case LARGE:
                mineralAmount = LARGE_AMOUNT_OF_MINERAL;
        }
    }

    public void mineMineral() {
        if (mineralAmount == 0) {
            throw new InvalidGameLogicException("Can't find any " + mineral + " to mine at " + point);
        }

        mineralAmount = mineralAmount - 1;
    }

    public int getAmountOfMineral(Material mineral) {
        if (mineral == this.mineral) {
            return mineralAmount;
        }

        return 0;
    }

    public int getAmountOfFish() {
        return fishAmount;
    }

    public void consumeOneFish() {
        fishAmount = fishAmount - 1;
    }

    public boolean isUnHarvestedCrop() {
        return crop != null && crop.getGrowthState() != Crop.GrowthState.HARVESTED;
    }

    public boolean isMilitaryBuilding() {
        return building != null && building.isMilitaryBuilding();
    }

    public Road getRoad() {

        /* Don't include start and end points of roads so ignore the point if there is a flag */
        if (isFlag()) {
            return null;
        }

        /* Return null if there is no connected road */
        if (connectedRoads.isEmpty()) {
            return null;
        }

        /* Return the first found connected road */
        return connectedRoads.iterator().next();
    }

    public void removeStone() {
        stone = null;
    }

    public boolean isDeadTree() {
        return isDeadTree;
    }

    public void setDeadTree() {
        isDeadTree = true;
    }

    public void removeDeadTree() {
        isDeadTree = false;
    }

    public boolean isHarborPossible() {
        return isShipyardAvailable;
    }

    public void setHarborIsPossible() {
        isShipyardAvailable = true;
    }

    public boolean isShipUnderConstruction() {
        return isShip;
    }

    public void setShipUnderConstruction() {
        isShip = true;
    }

    public void setShipDone() {
        isShip = false;
    }
}
