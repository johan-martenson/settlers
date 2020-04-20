package org.appland.settlers.model;

import java.util.Collection;
import java.util.List;

public class GameChangesList {

    private final long time;

    private final List<Worker> workersWithNewTargets;
    private final List<Flag> newFlags;
    private final List<Flag> removedFlags;
    private final List<Building> newBuildings;
    private final List<Building> changedBuildings;
    private final List<Building> removedBuildings;
    private final List<Road> addedRoads;
    private final List<Road> removedRoads;
    private final List<Worker> removedWorkers;
    private final List<Tree> newTrees;
    private final List<Tree> removedTrees;
    private final List<Stone> newStones;
    private final List<Sign> newSigns;
    private final List<Sign> removedSigns;
    private final List<Crop> newCrops;
    private final List<Crop> removedCrops;
    private final Collection<Point> newDiscoveredLand;
    private final List<Point> newBorder;
    private final List<Point> removedBorder;

    public GameChangesList(long time, List<Worker> workersWithNewTargets, List<Flag> newFlags, List<Flag> removedFlags, List<Building> newBuildings, List<Building> changedBuildings, List<Building> removedBuildings, List<Road> addedRoads, List<Road> removedRoads, List<Worker> removedWorkers, List<Tree> newTrees, List<Tree> removedTrees, List<Stone> newStones, List<Sign> newSigns, List<Sign> removedSigns, List<Crop> newCrops, List<Crop> removedCrops, Collection<Point> newDiscoveredLand, List<Point> newBorder, List<Point> removedBorder) {
        this.time = time;
        this.workersWithNewTargets = workersWithNewTargets;
        this.newFlags = newFlags;
        this.removedFlags = removedFlags;
        this.newBuildings = newBuildings;
        this.changedBuildings = changedBuildings;
        this.removedBuildings = removedBuildings;
        this.addedRoads = addedRoads;
        this.removedRoads = removedRoads;
        this.removedWorkers = removedWorkers;
        this.newTrees = newTrees;
        this.removedTrees = removedTrees;
        this.newStones = newStones;
        this.newSigns = newSigns;
        this.removedSigns = removedSigns;
        this.newCrops = newCrops;
        this.removedCrops = removedCrops;
        this.newDiscoveredLand = newDiscoveredLand;
        this.newBorder = newBorder;
        this.removedBorder = removedBorder;
    }

    public long getTime() {
        return time;
    }

    public List<Flag> getNewFlags() {
        return newFlags;
    }

    public List<Flag> getRemovedFlags() {
        return removedFlags;
    }

    public List<Worker> getWorkersWithNewTargets() {
        return workersWithNewTargets;
    }

    public List<Building> getNewBuildings() {
        return newBuildings;
    }

    public List<Building> getChangedBuildings() {
        return changedBuildings;
    }

    public List<Building> getRemovedBuildings() {
        return removedBuildings;
    }

    public List<Road> getNewRoads() {
        return addedRoads;
    }

    public List<Road> getRemovedRoads() {
        return removedRoads;
    }

    public List<Worker> getRemovedWorkers() {
        return removedWorkers;
    }

    public List<Tree> getNewTrees() {
        return newTrees;
    }

    public List<Tree> getRemovedTrees() {
        return removedTrees;
    }

    public List<Stone> getRemovedStones() {
        return newStones;
    }

    public List<Sign> getNewSigns() {
        return newSigns;
    }

    public List<Sign> getRemovedSigns() {
        return removedSigns;
    }

    public List<Crop> getNewCrops() {
        return newCrops;
    }

    public List<Crop> getRemovedCrops() {
        return removedCrops;
    }

    public Collection<Point> getNewDiscoveredLand() {
        return newDiscoveredLand;
    }

    @Override
    public String toString() {
        return "GameChangesList{" +
                "time=" + time +
                ", workersWithNewTargets=" + workersWithNewTargets +
                ", newFlags=" + newFlags +
                ", removedFlags=" + removedFlags +
                ", newBuildings=" + newBuildings +
                ", changedBuildings=" + changedBuildings +
                ", removedBuildings=" + removedBuildings +
                ", addedRoads=" + addedRoads +
                ", removedRoads=" + removedRoads +
                ", removedWorkers=" + removedWorkers +
                ", newTrees=" + newTrees +
                ", removedTrees=" + removedTrees +
                ", newStones=" + newStones +
                ", newSigns=" + newSigns +
                ", removedSigns=" + removedSigns +
                ", newCrops=" + newCrops +
                ", removedCrops=" + removedCrops +
                ", newDiscoveredLand=" + newDiscoveredLand +
                '}';
    }

    public List<Point> getNewBorder() {
        return newBorder;
    }

    public List<Point> getRemovedBorder() {
        return removedBorder;
    }
}
