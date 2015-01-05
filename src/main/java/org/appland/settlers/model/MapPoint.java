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
    private final Point      point;
    private final Set<Point> connectedNeighbors;
    private final Set<Road>  connectedRoads;

    private Building building;
    private Flag     flag;
    private Tree     tree;
    private Stone    stone;
    private Crop     crop;
    private Sign     sign;

    public MapPoint(Point p) {
        point              = p;
        building           = null;
        flag               = null;
        tree               = null;
        stone              = null;
        crop               = null;
        sign               = null;
        connectedNeighbors = new HashSet<>();
        connectedRoads     = new HashSet<>();
    }

    void setBuilding(Building b) throws Exception {
        if (isOccupied()) {
            throw new Exception(this + " is already occupied");
        }

        building = b;
    }

    void setFlag(Flag f) throws Exception {
        if (isOccupied()) {
            throw new Exception(this + " is already occupied");
        }

        flag = f;
    }

    Flag getFlag() {
        return flag;
    }

    void addConnectingRoad(Road r) throws Exception {
        Point previous = null;

        for (Point current : r.getWayPoints()) {
            if (current.equals(point) && previous != null) {
                connectedNeighbors.add(previous);
            }

            if (previous != null && previous.equals(point)) {
                connectedNeighbors.add(current);
            }

            previous = current;
        }

        connectedRoads.add(r);
    }

    void removeConnectingRoad(Road r) throws Exception {
        if (!connectedRoads.contains(r)) {
            throw new Exception(r + " is not connected to " + this);
        }

        connectedRoads.remove(r);

        Point previous = null;

        for (Point current : r.getWayPoints()) {
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
        return new ArrayList<Road>(connectedRoads);
    }

    Set<Point> getConnectedNeighbors() {
        return connectedNeighbors;
    }

    Building getBuilding() {
        return building;
    }

    Tree getTree() {
        return tree;
    }

    void setTree(Tree t) {
        tree = t;
    }

    void removeTree() {
        tree = null;
    }

    void setStone(Stone s) {
        stone = s;
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

    void setCrop(Crop c) {
        crop = c;
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

    void setSign(Sign s) {
        sign = s;
    }

    void removeBuilding() {
        building = null;
    }

    boolean isCrop(Point p) {
        return crop != null;
    }
}