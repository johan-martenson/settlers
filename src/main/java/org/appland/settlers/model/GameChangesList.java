package org.appland.settlers.model;

import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.messages.Message;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record GameChangesList(
        long time,
        List<Worker> workersWithNewTargets,
        List<Flag> newFlags,
        List<Flag> removedFlags,
        List<Building> newBuildings,
        Collection<Building> changedBuildings,
        List<Building> removedBuildings,
        List<Road> newRoads,
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
        List<BorderChange> changedBorders,
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
        Collection<Tree> newFallingTrees,
        boolean transportPriorityChanged
) {
    @Override
    public String toString() {
        return "GameChangesList{" +
                "time=" + time +
                (isNonEmpty(workersWithNewTargets) ? ", workersWithNewTargets=" + workersWithNewTargets : "") +
                (isNonEmpty(newFlags) ? ", newFlags=" + newFlags : "") +
                (isNonEmpty(removedFlags) ? ", removedFlags=" + removedFlags : "") +
                (isNonEmpty(newBuildings) ? ", newBuildings=" + newBuildings : "") +
                (isNonEmpty(changedBuildings) ? ", changedBuildings=" + changedBuildings : "") +
                (isNonEmpty(removedBuildings) ? ", removedBuildings=" + removedBuildings : "") +
                (isNonEmpty(newRoads) ? ", newRoads=" + newRoads : "") +
                (isNonEmpty(removedRoads) ? ", removedRoads=" + removedRoads : "") +
                (isNonEmpty(removedWorkers) ? ", removedWorkers=" + removedWorkers : "") +
                (isNonEmpty(newTrees) ? ", newTrees=" + newTrees : "") +
                (isNonEmpty(removedTrees) ? ", removedTrees=" + removedTrees : "") +
                (isNonEmpty(removedStones) ? ", removedStones=" + removedStones : "") +
                (isNonEmpty(newSigns) ? ", newSigns=" + newSigns : "") +
                (isNonEmpty(removedSigns) ? ", removedSigns=" + removedSigns : "") +
                (isNonEmpty(newCrops) ? ", newCrops=" + newCrops : "") +
                (isNonEmpty(removedCrops) ? ", removedCrops=" + removedCrops : "") +
                (isNonEmpty(newDiscoveredLand) ? ", newDiscoveredLand=" + newDiscoveredLand : "") +
                (isNonEmpty(changedBorders) ? ", changedBorders=" + changedBorders : "") +
                (isNonEmpty(newStones) ? ", newStones=" + newStones : "") +
                (isNonEmpty(newWorkers) ? ", newWorkers=" + newWorkers : "") +
                (isNonEmpty(changedAvailableConstruction) ? ", changedAvailableConstruction=" + changedAvailableConstruction : "") +
                (isNonEmpty(newMessages) ? ", newMessages=" + newMessages : "") +
                (isNonEmpty(promotedRoads) ? ", promotedRoads=" + promotedRoads : "") +
                (isNonEmpty(changedFlags) ? ", changedFlags=" + changedFlags : "") +
                (isNonEmpty(removedDeadTrees) ? ", removedDeadTrees=" + removedDeadTrees : "") +
                (isNonEmpty(discoveredDeadTrees) ? ", discoveredDeadTrees=" + discoveredDeadTrees : "") +
                (isNonEmpty(harvestedCrops) ? ", harvestedCrops=" + harvestedCrops : "") +
                (isNonEmpty(newShips) ? ", newShips=" + newShips : "") +
                (isNonEmpty(finishedShips) ? ", finishedShips=" + finishedShips : "") +
                (isNonEmpty(shipsWithNewTargets) ? ", shipsWithNewTargets=" + shipsWithNewTargets : "") +
                (isNonEmpty(workersWithStartedActions) ? ", workersWithStartedActions=" + workersWithStartedActions : "") +
                (isNonEmpty(removedDecorations) ? ", removedDecorations=" + removedDecorations : "") +
                (isNonEmpty(newDecorations) ? ", newDecorations=" + newDecorations : "") +
                (isNonEmpty(upgradedBuildings) ? ", upgradedBuildings=" + upgradedBuildings : "") +
                (isNonEmpty(removedMessages) ? ", removedMessages=" + removedMessages : "") +
                (isNonEmpty(changedStones) ? ", changedStones=" + changedStones : "") +
                (isNonEmpty(newFallingTrees) ? ", newFallingTrees=" + newFallingTrees : "") +
                (transportPriorityChanged ? ", transportPriorityChanged" : "") +
                '}';
    }

    private <T> boolean isNonEmpty(Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

    private boolean isNonEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
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
