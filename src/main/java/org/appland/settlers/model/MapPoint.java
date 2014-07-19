/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author johan
 */
public class MapPoint {
    Building   building;
    Flag       flag;
    Point      point;
    Set<Point> connectedNeighbors;
    boolean    isRoad;
    Set<Road>  connectedRoads;

    public MapPoint(Point p) {
        point              = p;
        building           = null;
        flag               = null;
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

    private boolean hasRoadTo(Point next) {
        return connectedNeighbors.contains(next); 
    }

    public Iterable<Road> getConnectedRoads() {
        return connectedRoads;
    }

    Set<Point> getConnectedNeighbors() {
        return connectedNeighbors;
    }

    Building getBuilding() {
        return building;
    }
}