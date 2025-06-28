/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author johan
 */
public class MapPoint {
    private static final int DEFAULT_AMOUNT_FISH = 10;
    private static final int SMALL_AMOUNT_OF_MINERAL = 5;
    private static final int MEDIUM_AMOUNT_OF_MINERAL = 10;
    private static final int LARGE_AMOUNT_OF_MINERAL = 15;
    public static final int DEFAULT_HEIGHT = 10;

    private final Point      point;
    private final Set<Point> connectedNeighbors         = new HashSet<>();
    private final Set<Road>  connectedRoads             = new HashSet<>();
    private final Set<Point> connectedFlagsAndBuildings = new HashSet<>();

    private Building       building;
    private Flag           flag;
    private Tree           tree;
    private Stone          stone;
    private Crop           crop;
    private Sign           sign;
    private int            height = DEFAULT_HEIGHT;
    private int            mineralAmount;
    private Material       mineral;
    private int            fishAmount = DEFAULT_AMOUNT_FISH;
    private boolean        isDeadTree;
    private boolean        isShipyardAvailable;
    private boolean        isShip;
    private DecorationType decoration;

    public MapPoint(Point point) {
        this.point = point;
    }

    /**
     * Sets the building on this map point.
     *
     * @param building The building to be set.
     */
    void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Sets the flag on this map point.
     *
     * @param flag The flag to be set.
     */
    void setFlag(Flag flag) {
        this.flag = flag;
    }

    /**
     * Retrieves the flag on this map point.
     *
     * @return The flag on this map point.
     */
    public Flag getFlag() {
        return flag;
    }

    void addConnectingRoad(Road road) {

        // Store connected neighbors
        for (int i = 0; i < road.getWayPoints().size(); i++) {
            var roadPoint = road.getWayPoints().get(i);

            if (roadPoint.equals(this.point)) {
                if (i > 0) {
                    connectedNeighbors.add(road.getWayPoints().get(i - 1));
                }

                if (i < road.getWayPoints().size() - 1) {
                    connectedNeighbors.add(road.getWayPoints().get(i + 1));
                }

                // A road can't have any duplicate points
                break;
            }
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

    /**
     * Provides a string representation of the map point.
     *
     * @return A string describing the map point.
     */
    @Override
    public String toString() {
        return "MapPoint{" +
                "point=" + point +
                ", connectedNeighbors=" + connectedNeighbors +
                ", connectedRoads=" + connectedRoads +
                ", connectedFlagsAndBuildings=" + connectedFlagsAndBuildings +
                ", building=" + building +
                ", flag=" + flag +
                ", tree=" + tree +
                ", stone=" + stone +
                ", crop=" + crop +
                ", sign=" + sign +
                ", height=" + height +
                ", mineralAmount=" + mineralAmount +
                ", mineral=" + mineral +
                ", fishAmount=" + fishAmount +
                ", isDeadTree=" + isDeadTree +
                ", isShipyardAvailable=" + isShipyardAvailable +
                ", isShip=" + isShip +
                ", decoration=" + decoration +
                '}';
    }

    /**
     * Checks if this map point has a flag.
     *
     * @return True if a flag is present, false otherwise.
     */
    public boolean isFlag() {
        return flag != null;
    }

    /**
     * Checks if this map point has a road.
     *
     * @return True if a road is connected, false otherwise.
     */
    public boolean isRoad() {
        return !connectedNeighbors.isEmpty();
    }

    /**
     * Retrieves the set of connected roads.
     *
     * @return A set of connected roads.
     */
    public Set<Road> getConnectedRoads() {
        return connectedRoads;
    }

    /**
     * Retrieves the set of connected neighbors.
     *
     * @return A set of connected neighbor points.
     */
    Set<Point> getConnectedNeighbors() {
        return connectedNeighbors;
    }

    /**
     * Retrieves the set of connected flags and buildings.
     *
     * @return A set of connected flags and buildings.
     */
    Set<Point> getConnectedFlagsAndBuildings() {
        return connectedFlagsAndBuildings;
    }

    /**
     * Retrieves the building on this map point.
     *
     * @return The building on this map point.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Retrieves the tree on this map point.
     *
     * @return The tree on this map point.
     */
    Tree getTree() {
        return tree;
    }

    /**
     * Sets the tree on this map point.
     *
     * @param tree The tree to be set.
     */
    void setTree(Tree tree) {
        this.tree = tree;
    }

    /**
     * Removes the tree from this map point.
     */
    void removeTree() {
        tree = null;
    }

    /**
     * Sets the stone on this map point.
     *
     * @param stone The stone to be set.
     */
    void setStone(Stone stone) {
        this.stone = stone;
    }

    /**
     * Retrieves the stone on this map point.
     *
     * @return The stone on this map point.
     */
    Stone getStone() {
        return stone;
    }

    /**
     * Checks if this map point has a building.
     *
     * @return True if a building is present, false otherwise.
     */
    public boolean isBuilding() {
        return building != null;
    }

    /**
     * Checks if this map point has a stone.
     *
     * @return True if a stone is present, false otherwise.
     */
    public boolean isStone() {
        return stone != null;
    }

    /**
     * Checks if this map point has a tree.
     *
     * @return True if a tree is present, false otherwise.
     */
    public boolean isTree() {
        return tree != null;
    }

    /**
     * Sets the crop on this map point.
     *
     * @param crop The crop to be set.
     */
    void setCrop(Crop crop) {
        this.crop = crop;
    }

    /**
     * Retrieves the crop on this map point.
     *
     * @return The crop on this map point.
     */
    Crop getCrop() {
        return crop;
    }

    /**
     * Removes the flag from this map point.
     */
    void removeFlag() {
        flag = null;
    }

    /**
     * Retrieves the sign on this map point.
     *
     * @return The sign on this map point.
     */
    Sign getSign() {
        return sign;
    }

    /**
     * Sets the sign on this map point.
     *
     * @param sign The sign to be set.
     */
    void setSign(Sign sign) {
        this.sign = sign;
    }

    /**
     * Removes the building from this map point.
     */
    void removeBuilding() {
        building = null;
    }

    /**
     * Checks if this map point has a crop.
     *
     * @return True if a crop is present, false otherwise.
     */
    public boolean isCrop() {
        return crop != null;
    }

    /**
     * Sets the height of this map point.
     *
     * @param height The height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Retrieves the height of this map point.
     *
     * @return The height of this map point.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Checks if this map point has a sign.
     *
     * @return True if a sign is present, false otherwise.
     */
    public boolean isSign() {
        return sign != null;
    }

    /**
     * Checks if this map point has a building of the specified size.
     *
     * @param size The size to check.
     * @return True if a building of the specified size is present, false otherwise.
     */
    public boolean isBuildingOfSize(Size size) {
        return building != null && building.getSize() == size;
    }

    /**
     * Sets the mineral amount on this map point based on the size specified.
     *
     * @param mineral The mineral to set.
     * @param amount  The size of the mineral amount.
     */
    public void setMineralAmount(Material mineral, Size amount) {
        this.mineral = mineral;

        mineralAmount = switch (amount) {
            case SMALL -> SMALL_AMOUNT_OF_MINERAL;
            case MEDIUM -> MEDIUM_AMOUNT_OF_MINERAL;
            case LARGE -> LARGE_AMOUNT_OF_MINERAL;
        };
    }

    /**
     * Mines a mineral from this map point, reducing the mineral amount by one.
     */
    public void mineMineral() {
        if (mineralAmount == 0) {
            throw new InvalidGameLogicException(String.format("Can't find any %s to mine at %s", mineralAmount, point));
        }

        mineralAmount -= 1;
    }

    /**
     * Retrieves the amount of the specified mineral on this map point.
     *
     * @param mineral The mineral to check.
     * @return The amount of the specified mineral.
     */
    public int getAmountOfMineral(Material mineral) {
        return mineral == this.mineral ? mineralAmount : 0;
    }

    /**
     * Retrieves the amount of fish on this map point.
     *
     * @return The amount of fish.
     */
    public int getAmountOfFish() {
        return fishAmount;
    }

    /**
     * Consumes one fish from this map point, reducing the fish amount by one.
     */
    public void consumeOneFish() {
        fishAmount -= 1;
    }

    /**
     * Checks if this map point has an unharvested crop.
     *
     * @return True if an unharvested crop is present, false otherwise.
     */
    public boolean isUnHarvestedCrop() {
        return crop != null && crop.getGrowthState() != Crop.GrowthState.HARVESTED;
    }

    /**
     * Checks if this map point has a military building.
     *
     * @return True if a military building is present, false otherwise.
     */
    public boolean isMilitaryBuilding() {
        return building != null && building.isMilitaryBuilding();
    }

    /**
     * Retrieves a road on this map point, if available.
     *
     * @return A road if available, null otherwise.
     */
    public Road getRoad() {

        // Don't include start and end points of roads so ignore the point if there is a flag
        if (isFlag()) {
            return null;
        }

        // Return null if there is no connected road
        if (connectedRoads.isEmpty()) {
            return null;
        }

        // Return the first found connected road
        return connectedRoads.iterator().next();
    }

    /**
     * Removes the stone from this map point.
     */
    public void removeStone() {
        stone = null;
    }

    /**
     * Checks if this map point has a dead tree.
     *
     * @return True if a dead tree is present, false otherwise.
     */
    public boolean isDeadTree() {
        return isDeadTree;
    }

    /**
     * Marks this map point as having a dead tree.
     */
    public void setDeadTree() {
        isDeadTree = true;
    }

    /**
     * Removes the dead tree from this map point.
     */
    public void removeDeadTree() {
        isDeadTree = false;
    }

    /**
     * Checks if a harbor is possible at this map point.
     *
     * @return True if a harbor is possible, false otherwise.
     */
    public boolean isHarborPossible() {
        return isShipyardAvailable;
    }

    /**
     * Marks this map point as a location where a harbor is possible.
     */
    public void setHarborIsPossible() {
        isShipyardAvailable = true;
    }

    /**
     * Checks if a ship is under construction at this map point.
     *
     * @return True if a ship is under construction, false otherwise.
     */
    public boolean isShipUnderConstruction() {
        return isShip;
    }

    /**
     * Marks this map point as having a ship under construction.
     */
    public void setShipUnderConstruction() {
        isShip = true;
    }

    /**
     * Marks this map point as having a ship construction completed.
     */
    public void setShipDone() {
        isShip = false;
    }

    /**
     * Checks if this map point has a decoration.
     *
     * @return True if a decoration is present, false otherwise.
     */
    public boolean isDecoration() {
        return decoration != null;
    }

    /**
     * Removes the decoration from this map point.
     */
    public void removeDecoration() {
        decoration = null;
    }

    /**
     * Sets the decoration on this map point.
     *
     * @param decoration The decoration to be set.
     */
    public void setDecoration(DecorationType decoration) {
        this.decoration = decoration;
    }

    /**
     * Retrieves the decoration on this map point.
     *
     * @return The decoration on this map point.
     */
    public DecorationType getDecoration() {
        return decoration;
    }

    /**
     * Retrieves the point represented by this map point.
     *
     * @return The point represented by this map point.
     */
    public Point getPoint() {
        return point;
    }
}
