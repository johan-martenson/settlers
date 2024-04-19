package org.appland.settlers.model;

import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.messages.Message;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private final List<Road> promotedRoads;
    private final Collection<Flag> changedFlags;
    private final Collection<Point> removedDeadTrees;
    private final Collection<Point> discoveredDeadTrees;
    private final List<Crop> harvestedCrops;
    private final List<Ship> newShips;
    private final List<Ship> finishedShips;
    private final List<Ship> shipsWithNewTargets;
    private final Map<Worker, WorkerAction> workersWithStartedActions;
    private final List<Point> removedDecorations;
    private final Map<Point, DecorationType> newDecorations;
    private final Collection<NewAndOldBuilding> upgradedBuildings;
    private final Collection<Message> removedMessages;
    private final Collection<Stone> changedStones;
    private final Collection<Tree> newFallingTrees;

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
                           List<Message> newMessages,
                           List<Road> promotedRoads,
                           Collection<Flag> changedFlags,
                           Collection<Point> removedDeadTrees,
                           Collection<Point> discoveredDeadTrees,
                           List<Crop> harvestedCrops,
                           List<Ship> newShips,
                           List<Ship> finishedShips,
                           List<Ship> shipsWithNewTargets,
                           Map<Worker, WorkerAction> workersWithStartedActions,
                           List<Point> removedDecorations,
                           Map<Point, DecorationType> newDecorations,
                           Collection<NewAndOldBuilding> upgradedBuildings,
                           Collection<Message> removedMessages,
                           Collection<Stone> changedStones,
                           Collection<Tree> newFallingTrees) {
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
        this.promotedRoads = promotedRoads;
        this.changedFlags = changedFlags;
        this.removedDeadTrees = removedDeadTrees;
        this.discoveredDeadTrees = discoveredDeadTrees;
        this.harvestedCrops = harvestedCrops;
        this.newShips = newShips;
        this.finishedShips = finishedShips;
        this.shipsWithNewTargets = shipsWithNewTargets;
        this.workersWithStartedActions = workersWithStartedActions;
        this.removedDecorations = removedDecorations;
        this.newDecorations = newDecorations;
        this.upgradedBuildings = upgradedBuildings;
        this.removedMessages = removedMessages;
        this.changedStones = changedStones;
        this.newFallingTrees = newFallingTrees;
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
                ((workersWithNewTargets.isEmpty()) ? "" : ", workersWithNewTargets=" + workersWithNewTargets) +
                ((newFlags.isEmpty()) ? "" : ", newFlags=" + newFlags) +
                ((removedFlags.isEmpty()) ? "" : ", removedFlags=" + removedFlags) +
                ((newBuildings.isEmpty()) ? "" : ", newBuildings=" + newBuildings) +
                ((changedBuildings.isEmpty()) ? "" : ", changedBuildings=" + changedBuildings) +
                ((removedBuildings.isEmpty()) ? "" : ", removedBuildings=" + removedBuildings) +
                ((addedRoads.isEmpty()) ? "" : ", addedRoads=" + addedRoads) +
                ((removedRoads.isEmpty()) ? "" : ", removedRoads=" + removedRoads) +
                ((removedWorkers.isEmpty()) ? "" : ", removedWorkers=" + removedWorkers) +
                ((newTrees.isEmpty()) ? "" : ", newTrees=" + newTrees) +
                ((removedTrees.isEmpty()) ? "" : ", removedTrees=" + removedTrees) +
                ((removedStones.isEmpty()) ? "" : ", removedStones=" + removedStones) +
                ((newSigns.isEmpty()) ? "" : ", newSigns=" + newSigns) +
                ((removedSigns.isEmpty()) ? "" : ", removedSigns=" + removedSigns) +
                ((newCrops.isEmpty()) ? "" : ", newCrops=" + newCrops) +
                ((removedCrops.isEmpty()) ? "" : ", removedCrops=" + removedCrops) +
                ((newDiscoveredLand.isEmpty()) ? "" : ", newDiscoveredLand=" + newDiscoveredLand) +
                ((borderChanges.isEmpty()) ? "" : ", borderChanges=" + borderChanges) +
                ((newStones.isEmpty()) ? "" : ", newStones=" + newStones) +
                ((newWorkers.isEmpty()) ? "" : ", newWorkers=" + newWorkers) +
                ((changedAvailableConstruction.isEmpty()) ? "" : ", changedAvailableConstruction=" + changedAvailableConstruction) +
                ((newMessages.isEmpty()) ? "" : ", newMessages=" + newMessages) +
                ((promotedRoads.isEmpty()) ? "" : ", promotedRoads=" + promotedRoads) +
                ((changedFlags.isEmpty()) ? "" : ", changedFlags=" + changedFlags) +
                ((removedDeadTrees.isEmpty()) ? "" : ", removedDeadTrees=" + removedDeadTrees) +
                ((discoveredDeadTrees.isEmpty()) ? "" : ", discoveredDeadTrees=" + discoveredDeadTrees) +
                ((harvestedCrops.isEmpty()) ? "" : ", harvestedCrops=" + harvestedCrops) +
                ((newShips.isEmpty()) ? "" : ", newShips=" + newShips) +
                ((finishedShips.isEmpty()) ? "" : ", finishedShips=" + finishedShips) +
                ((shipsWithNewTargets.isEmpty()) ? "" : ", shipsWithNewTargets=" + shipsWithNewTargets) +
                ((workersWithStartedActions.isEmpty()) ? "" : ", workersWithStartedActions=" + workersWithStartedActions) +
                ((removedDecorations.isEmpty()) ? "" : ", removedDecorations=" + removedDecorations) +
                ((newDecorations.isEmpty()) ? "" : ", newDecorations=" + newDecorations) +
                ((upgradedBuildings.isEmpty()) ? "" : ", upgradedBuildings=" + upgradedBuildings) +
                ((removedMessages.isEmpty()) ? "" : ", removedMessages=" + removedMessages) +
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

    public List<Road> getPromotedRoads() {
        return promotedRoads;
    }

    public Collection<Flag> getChangedFlags() {
        return changedFlags;
    }

    public Collection<Point> getRemovedDeadTrees() {
        return removedDeadTrees;
    }

    public Collection<Point> getDiscoveredDeadTrees() {
        return discoveredDeadTrees;
    }

    public List<Crop> getHarvestedCrops() {
        return harvestedCrops;
    }

    public List<Ship> getNewShips() {
        return newShips;
    }

    public List<Ship> getFinishedShips() {
        return finishedShips;
    }

    public List<Ship> getShipsWithNewTargets() {
        return shipsWithNewTargets;
    }

    public Map<Worker, WorkerAction> getWorkersWithStartedActions() {
        return workersWithStartedActions;
    }

    public List<Point> getRemovedDecorations() {
        return removedDecorations;
    }

    public Map<Point, DecorationType> getNewDecorations() {
        return newDecorations;
    }

    public Collection<NewAndOldBuilding> getUpgradedBuildings() {
        return upgradedBuildings;
    }

    public Collection<Message> getRemovedMessages() {
        return removedMessages;
    }

    public Collection<Stone> getChangedStones() {
        return changedStones;
    }

    public Collection<Tree> getNewFallingTrees() {
        return newFallingTrees;
    }

    public static class NewAndOldBuilding {
        public Building newBuilding;
        public Building oldBuilding;

        public NewAndOldBuilding(Building fromBuilding, Building upgraded) {
            newBuilding = upgraded;
            oldBuilding = fromBuilding;
        }
    }
}
