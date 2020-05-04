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
    private final List<Stone> removedStones;
    private final List<Sign> newSigns;
    private final List<Sign> removedSigns;
    private final List<Crop> newCrops;
    private final List<Crop> removedCrops;
    private final Collection<Point> newDiscoveredLand;
    private final List<BorderChange> borderChanges;
    private final List<Stone> newStones;
    private final List<Worker> newWorkers;
    private final Collection<Point> changedAvailableConstruction;
    private final List<Message> newMessages;

    public GameChangesList(long time,
                           List<Worker> workersWithNewTargets,
                           List<Flag> newFlags,
                           List<Flag> removedFlags,
                           List<Building> newBuildings,
                           List<Building> changedBuildings,
                           List<Building> removedBuildings,
                           List<Road> addedRoads,
                           List<Road> removedRoads,
                           List<Worker> removedWorkers,
                           List<Tree> newTrees,
                           List<Tree> removedTrees,
                           List<Stone> removedStones,
                           List<Sign> newSigns,
                           List<Sign> removedSigns,
                           List<Crop> newCrops,
                           List<Crop> removedCrops,
                           Collection<Point> newDiscoveredLand,
                           List<BorderChange> borderChanges,
                           List<Stone> newStones,
                           List<Worker> newWorkers,
                           Collection<Point> changedAvailableConstruction,
                           List<Message> newMessages) {
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
        this.removedStones = removedStones;
        this.newSigns = newSigns;
        this.removedSigns = removedSigns;
        this.newCrops = newCrops;
        this.removedCrops = removedCrops;
        this.newDiscoveredLand = newDiscoveredLand;
        this.borderChanges = borderChanges;
        this.newStones = newStones;
        this.newWorkers = newWorkers;
        this.changedAvailableConstruction = changedAvailableConstruction;
        this.newMessages = newMessages;
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
        return removedStones;
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
                ", removedStones=" + removedStones +
                ", newSigns=" + newSigns +
                ", removedSigns=" + removedSigns +
                ", newCrops=" + newCrops +
                ", removedCrops=" + removedCrops +
                ", newDiscoveredLand=" + newDiscoveredLand +
                ", borderChanges=" + borderChanges +
                ", newStones=" + newStones +
                ", newWorkers=" + newWorkers +
                ", hangedAvailableConstruction=" + changedAvailableConstruction +
                '}';
    }

    public List<BorderChange> getChangedBorders() {
        return borderChanges;
    }

    public List<Stone> getNewStones() {
        return newStones;
    }

    public List<Worker> getNewWorkers() {
        return newWorkers;
    }

    public Collection<Point> getChangedAvailableConstruction() {
        return changedAvailableConstruction;
    }

    public List<Message> getNewGameMessages() {
        return newMessages;
    }
}
