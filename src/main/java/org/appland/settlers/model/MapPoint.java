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
class MapPoint {
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

    public MapPoint(Point point) {
        this.point                 = point;
        building                   = null;
        flag                       = null;
        tree                       = null;
        stone                      = null;
        crop                       = null;
        sign                       = null;
        connectedNeighbors         = new HashSet<>();
        connectedRoads             = new HashSet<>();
        connectedFlagsAndBuildings = new HashSet<>();
    }

    void setBuilding(Building building) throws Exception {
        if (flag != null) {
            throw new Exception(this + " is already occupied by flag " + flag);
        }

        if (this.building != null) {
            throw new Exception(this + " is already occupied by building " + this.building);
        }

        this.building = building;
    }

    void setFlag(Flag flag) throws Exception {
        if (isOccupied()) {
            throw new Exception(this + " is already occupied");
        }

        this.flag = flag;
    }

    Flag getFlag() {
        return flag;
    }

    void addConnectingRoad(Road road) {
        Point previous = null;

        for (Point current : road.getWayPoints()) {
            if (current.equals(point) && previous != null) {
                connectedNeighbors.add(previous);
            }

            if (previous != null && previous.equals(point)) {
                connectedNeighbors.add(current);
            }

            previous = current;
        }

        connectedRoads.add(road);

        if (road.getEnd().equals(point)) {
            connectedFlagsAndBuildings.add(road.getStart());
        } else if (road.getStart().equals(point)) {
            connectedFlagsAndBuildings.add(road.getEnd());
        }
    }

    void removeConnectingRoad(Road road) throws Exception {
        if (!connectedRoads.contains(road)) {
            throw new Exception(road + " is not connected to " + this);
        }

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

    private boolean isOccupied() {
        return flag != null || building != null;
    }

    @Override
    public String toString() {
        return "Map point " + point + " with " + building + " and " + flag;
    }

    boolean isFlag() {
        return flag != null;
    }

    boolean isRoad() {
        return !connectedNeighbors.isEmpty();
    }

    List<Road> getConnectedRoads() {
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
}
