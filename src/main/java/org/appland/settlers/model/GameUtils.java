/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

import static java.lang.Math.*;
import static org.appland.settlers.model.Direction.*;
import static org.appland.settlers.model.Material.*;

/**
 *
 * @author johan
 */
public class GameUtils {

    public static List<Soldier> sortSoldiersByPreferredRank(List<Soldier> soldiers, int strength) {
        var sortedSoldiers = new ArrayList<>(soldiers);

        sortedSoldiers.sort((soldier0, soldier1) -> {
            var prefRankList = GameUtils.strengthToRank(strength);

            var rankDist0 = prefRankList.indexOf(soldier0.getRank());
            var rankDist1 = prefRankList.indexOf(soldier1.getRank());

            if (rankDist0 == rankDist1) {
                return 0;
            } else  {
                var diff = rankDist0 - rankDist1;

                return diff / Math.abs(diff);
            }
        });

        return sortedSoldiers;
    }

    public static void sortSoldiersByPreferredRankAndDistance(List<Soldier> soldiers, int strength, Point position) {
        soldiers.sort((soldier0, soldier1) -> {
            if (soldier0.getRank() == soldier1.getRank()) {
                var dist0 = GameUtils.distanceInGameSteps(soldier0.getHome().getPosition(), position);
                var dist1 = GameUtils.distanceInGameSteps(soldier1.getHome().getPosition(), position);

                if (dist0 == dist1) {
                    return 0;
                }

                var diff = dist0 - dist1;

                return diff / Math.abs(diff);
            } else {
                var prefRankList = GameUtils.strengthToRank(strength);

                var rankDist0 = prefRankList.indexOf(soldier0.getRank());
                var rankDist1 = prefRankList.indexOf(soldier1.getRank());

                var diff = rankDist0 - rankDist1;

                return diff / Math.abs(diff);
            }
        });
    }

    public static boolean allCollectionsEmpty(Collection... collections) {
        return Arrays.stream(collections).allMatch(Collection::isEmpty);
    }

    public static boolean allMapsEmpty(Map... maps) {
        return Arrays.stream(maps).allMatch(Map::isEmpty);
    }

    public enum AllocationType {
        WHEAT_ALLOCATION,
        COAL_ALLOCATION,
        WATER_ALLOCATION,
        FOOD_ALLOCATION,
        PLANK_ALLOCATION,
        IRON_BAR_ALLOCATION
    }

    public static class AllocationTracker {
        private static final Map<AllocationType, Set<Class<? extends Building>>> AFFECTED_BUILDING_TYPES = new HashMap<>();
        private static final Map<AllocationType, Set<Material>> TRACKED_MATERIALS = new HashMap<>();

        static {
            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.WHEAT_ALLOCATION,
                    new HashSet<>(Arrays.asList(Mill.class, Brewery.class, DonkeyFarm.class, PigFarm.class)
                    ));

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.COAL_ALLOCATION,
                    new HashSet<>(Arrays.asList(Mint.class, Metalworks.class)
                    ));

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.WATER_ALLOCATION,
                    new HashSet<>(Arrays.asList(Bakery.class, Brewery.class, DonkeyFarm.class, PigFarm.class)
                    ));

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.FOOD_ALLOCATION,
                    new HashSet<>(Arrays.asList(CoalMine.class, IronMine.class, GoldMine.class, GraniteMine.class)
                    ));

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.IRON_BAR_ALLOCATION,
                    new HashSet<>(Arrays.asList(Armory.class, Metalworks.class)
                    ));

            TRACKED_MATERIALS.put(AllocationType.WHEAT_ALLOCATION, new HashSet<>(List.of(WHEAT)));
            TRACKED_MATERIALS.put(AllocationType.COAL_ALLOCATION, new HashSet<>(List.of(COAL)));
            TRACKED_MATERIALS.put(AllocationType.WATER_ALLOCATION, new HashSet<>(List.of(WATER)));
            TRACKED_MATERIALS.put(AllocationType.IRON_BAR_ALLOCATION, new HashSet<>(List.of(IRON_BAR)));
            TRACKED_MATERIALS.put(AllocationType.PLANK_ALLOCATION, new HashSet<>(List.of(PLANK)));
            TRACKED_MATERIALS.put(AllocationType.FOOD_ALLOCATION, new HashSet<>(List.of(BREAD, FISH, MEAT)));
        }

        private final Map<Class<? extends Building>, Integer> consumed;
        private final AllocationType allocationType;
        private final Player player;
        private final Point position;

        public AllocationTracker(AllocationType allocationType, Player player, Point position) {
            this.player = player;
            this.allocationType = allocationType;
            this.position = position;

            consumed = new HashMap<>();
        }

        public void trackAllocation(Building building) {
            int amount = consumed.getOrDefault(building.getClass(), 0);
            consumed.put(building.getClass(), amount + 1);
        }

        public boolean isDeliveryAllowed(Building building) {
            Material material = TRACKED_MATERIALS.get(allocationType).stream().findFirst().get();

            return isDeliveryAllowed(building, material);
        }

        public boolean isDeliveryAllowed(Building building, Material material) {
            int quota = quotaForBuilding(building);

            var withinQuota = consumed.getOrDefault(building.getClass(), 0) < quota;

            if (withinQuota) {
                return true;
            }

            var didReset = resetAllocationIfNeeded(material);

            if (!didReset) {
                return false;
            }

            return consumed.getOrDefault(building.getClass(), 0) < quota;
        }

        private int quotaForBuilding(Building building) {
            return quotaForBuildingType(building.getClass());
        }

        private int quotaForBuildingType(Class<? extends Building> buildingType) {
            return switch (allocationType) {
                case WHEAT_ALLOCATION -> player.getWheatQuota(buildingType);
                case COAL_ALLOCATION -> player.getCoalQuota(buildingType);
                case FOOD_ALLOCATION -> player.getFoodQuota(buildingType);
                case WATER_ALLOCATION -> player.getWaterQuota(buildingType);
                case IRON_BAR_ALLOCATION -> player.getIronBarQuota(buildingType);
                case PLANK_ALLOCATION -> throw new RuntimeException("Plank allocation is not implemented yet.");
            };
        }

        public boolean isOverQuota(Class<? extends Building> buildingType) {
            return consumed.getOrDefault(buildingType, 0) >= quotaForBuildingType(buildingType);
        }

        public boolean resetAllocationIfNeeded(Material material) {
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(position, player);

            if (AFFECTED_BUILDING_TYPES.get(allocationType).stream().allMatch(buildingType ->
                    !needyConsumerExists(reachableBuildings, buildingType, material) || isOverQuota(buildingType)
            )) {
                AFFECTED_BUILDING_TYPES.get(allocationType).forEach(buildingType -> consumed.put(buildingType, 0));

                return true;
            }

            return false;
        }

        private boolean needyConsumerExists(Collection<Building> buildings, Class<? extends Building> buildingType, Material material) {
            return buildings.stream().anyMatch(building ->
                    building.getClass().equals(buildingType) &&
                    building.isReady() &&
                    building.needsMaterial(material));
        }
    }

    public static boolean setContainsNone(Set<Point> set, List<Point> needles) {
        for (Point point : needles) {
            if (set.contains(point)) {
                return false;
            }
        }

        return true;
    }

    public static boolean setContainsAny(Set<Point> discoveredLand, List<Point> wayPoints) {
        for (Point point : wayPoints) {
            if (discoveredLand.contains(point)) {
                return true;
            }
        }

        return false;
    }

    public static Direction getDirectionBetweenPoints(Point from, Point to) {
        Direction direction = null;

        if (to.x == from.x + 2 && to.y == from.y) {
            direction = RIGHT;
        } else if (to.x == from.x + 1 && to.y == from.y + 1) {
            direction = UP_RIGHT;
        } else if (to.x == from.x + 1 && to.y == from.y - 1) {
            direction = DOWN_RIGHT;
        } else if (to.x == from.x - 2 && to.y == from.y) {
            direction = LEFT;
        } else if (to.x == from.x - 1 && to.y == from.y + 1) {
            direction = UP_LEFT;
        } else if (to.x == from.x - 1 && to.y == from.y - 1) {
            direction = DOWN_LEFT;
        }

        if (direction == null) {
            throw new RuntimeException("Direction is null which should be impossible. From " + from + " to " + to);
        }

        return direction;
    }

    record ToSearchItem(Point point, int cost) implements Comparable {

        // TODO: align with implementation of equals to make them consistent!
            @Override
            public int compareTo(Object o) {
                if (o == null) {
                    return -1;
                }

                ToSearchItem otherItem = (ToSearchItem) o;

                if (cost < otherItem.cost) {
                    return -1;
                } else if (cost > otherItem.cost) {
                    return 1;
                }

                return 0;
            }
        }

    public static Optional<HouseOrRoad> getClosestHouseOrRoad(Point start, Function<HouseOrRoad, Boolean> isMatch, GameMap map) {
        PriorityQueue<ToSearchItem> toSearch = new PriorityQueue<>();
        Set<Point> searched = new HashSet<>();

        toSearch.add(new ToSearchItem(start, 0));

        // Keep searching the closest point
        while (!toSearch.isEmpty()) {
            ToSearchItem toSearchItem = toSearch.remove();

            Point point = toSearchItem.point;
            int cost = toSearchItem.cost;

            // Avoid coming back to the same point again
            searched.add(point);

            // Return the building if it fulfills the criteria
            if (map.isBuildingAtPoint(point)) {
                Building building = map.getBuildingAtPoint(point);

                HouseOrRoad houseOrRoad = new HouseOrRoad(building);

                if (isMatch.apply(houseOrRoad)) {
                    return Optional.of(houseOrRoad);
                }
            }

            // Otherwise check each connected road except for the one we came with. Add each matching endpoint to be searched
            for (Road road : map.getMapPoint(point).getConnectedRoads()) {

                Point otherEnd = road.getOtherPoint(point);

                // Skip the road we used to get to this point
                if (searched.contains(otherEnd)) {
                    continue;
                }

                // Return the road if it matches the criteria
                HouseOrRoad houseOrRoad = new HouseOrRoad(road);

                if (isMatch.apply(houseOrRoad)) {
                    return Optional.of(houseOrRoad);
                }

                // Add the other end of the road to be searched
                toSearch.add(new ToSearchItem(otherEnd, cost + road.getLength()));
            }
        }

        return Optional.empty();
    }

    public record BuildingAndData<B extends Building, D>(B building, D data) { }

    public static Collection<Point> getHexagonAroundPoint(Point point, int radius) {
        Set<Point> hexagonBorder = new HashSet<>();

        // Draw the diagonal left-hand lines
        int upperY = point.y;
        int lowerY = point.y;
        for (int x = point.x - (radius * 2); x < point.x - radius; x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY++;
            lowerY--;
        }

        // Draw the diagonal right-hand lines
        upperY = point.y + radius;
        lowerY = point.y - radius;
        for (int x = point.x + radius; x <= point.x + (radius * 2); x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY--;
            lowerY++;
        }

        // Draw the top and bottom lines
        for (int x = point.x - radius; x < point.x + radius; x += 2) {
            hexagonBorder.add(new Point(x, point.y + radius));
            hexagonBorder.add(new Point(x, point.y - radius));
        }

        return hexagonBorder;
    }

    public static Set<Point> getHexagonAreaAroundPoint(Point position, int radius, GameMap map) {
        Set<Point> area = new HashSet<>();

        int xStart = position.x - radius;
        int xEnd = position.x + radius;

        for (int y = position.y - radius; y < position.y; y++) {
            for (int x = xStart; x <= xEnd; x += 2) {
                if (x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight()) {
                    continue;
                }

                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }

        xStart = position.x - radius;
        xEnd = position.x + radius;

        for (int y = position.y + radius; y >= position.y; y--) {
            for (int x = xStart; x <= xEnd; x += 2) {
                if (x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight()) {
                    continue;
                }

                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }

        return area;
    }

    /**
     * Finds the closest storehouse off-road where delivery is possible.
     *
     * @param point   The starting point to search from.
     * @param avoid   The building to avoid during the search.
     * @param player  The player owning the buildings.
     * @param material The material to be delivered.
     * @return The closest storehouse where delivery is possible, or null if no storehouse is found.
     */
    public static Building getClosestStorageOffroadWhereDeliveryIsPossible(Point point, Building avoid, Player player, Material material) {
        GameMap map = player.getMap();

        return map.getBuildings().stream()
                .filter(building -> player.equals(building.getPlayer())) // Filter buildings that belong to another player
                .filter(building -> !building.equals(avoid)) // Filter buildings to avoid
                .filter(Building::isReady) // Filter buildings that are not operational
                .filter(Building::isStorehouse) // Filter buildings that are not storehouses
                .map(Storehouse.class::cast) // Cast Building to Storehouse
                .filter(storehouse -> !storehouse.isDeliveryBlocked(material)) // Filter storehouses where delivery is blocked
                .map(storehouse -> {
                    if (storehouse.getFlag().getPosition().equals(point)) {
                        return new Tuple<>(storehouse, 0);
                    }

                    var path = map.findWayOffroad(point, storehouse.getFlag().getPosition(), null);

                    return new Tuple<>(storehouse, path != null ? path.size() : Integer.MAX_VALUE);
                })
                .filter(tuple -> tuple.t2() != Integer.MAX_VALUE)
                .min(Comparator.comparingInt(Tuple::t2))
                .map(Tuple::t1)
                .orElse(null); // Return null if no storehouse is found
    }

    public static <T> boolean isAll(Collection<T> collection, T item) {
        for (T itemInList : collection) {
            if (!Objects.equals(itemInList, item)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean isAny(Collection<T> collection, T item) {
        for (T itemInList : collection) {
            if (Objects.equals(itemInList, item)) {
                return true;
            }
        }

        return false;
    }

    public static <T> boolean isSomeButNotAll(Collection<T> collection, T item) {
        boolean foundMatch = false;
        boolean foundNotMatch = false;

        for (T itemInList : collection) {
            if (Objects.equals(itemInList, item)) {
                foundMatch = true;

                continue;
            }

            foundNotMatch = true;
        }

        return foundMatch && foundNotMatch;
    }

    public static void upgradeMilitaryBuilding(Building fromBuilding, Building upgraded) {

        /* Set the map in the upgraded building */
        upgraded.setMap(fromBuilding.getMap());

        /* Pre-construct the upgraded building */
        upgraded.setConstructionReady();

        /* Set the position of the upgraded building so the soldiers can enter */
        upgraded.setPosition(fromBuilding.getPosition());

        /* Replace the buildings on the map */
        fromBuilding.getMap().replaceBuilding(upgraded, fromBuilding.getPosition());

        /* Ensure that the new building is occupied */
        if (fromBuilding.isOccupied()) {
            upgraded.setOccupied();
        }

        /* Move the soldiers to the new building */
        int currentMilitary = fromBuilding.getNumberOfHostedSoldiers();

        for (int i = 0; i < currentMilitary; i++) {

            /* Move one military from the old to the new building */
            Soldier military = fromBuilding.retrieveHostedSoldier();

            upgraded.promiseSoldier(military);
            military.enterBuilding(upgraded);
        }

        /* Make sure the border is updated only once */
        if (upgraded.getNumberOfHostedSoldiers() == 0) {
            fromBuilding.getMap().updateBorder(fromBuilding, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
        }

        /* Move the coins to the new building */
        int amountCoins = fromBuilding.getAmount(COIN);
        for (int i = 0; i < amountCoins; i++) {

            /* Put one coin in the new building */
            Cargo coinCargo = new Cargo(COIN, fromBuilding.getMap());

            upgraded.promiseDelivery(COIN);

            upgraded.putCargo(coinCargo);
        }
    }

    public static boolean isEven(int i) {
        return i % 2 == 0;
    }

    /**
     * Returns true if each item in the collection is part of the given set
     *
     * @param items The items to determine if the given collection contains
     * @param collection The collection that may contain all the given items
     * @param <T> The type of item
     * @return True if the set of items is a subset of the collection
     */
    public static <T> boolean areAllOneOf(Collection<T> items, Set<T> collection) {
        for (T itemInList : items) {
            if (!collection.contains(itemInList)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean areNonePartOf(Collection<T> items, Set<T> collection) {
        for (T item : items) {
            if (collection.contains(item)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean areAnyOneOf(List<T> items, Set<T> collection) {
        for (T item : items) {
            if (collection.contains(item)) {
                return true;
            }
        }

        return false;
    }

    static boolean areAllUnique(Collection<Point> points) {
        Set<Point> pointsSet = new HashSet<>(points);

        return points.size() == pointsSet.size();
    }

    public static Direction getDirection(Point from, Point to) {
        int deltaX = to.x - from.x;
        int deltaY = to.y - from.y;

        /* To the right */
        if (deltaX > 0) {

            /* Above */
            if (deltaY > 0) {
                if (deltaY > deltaX * 2) {
                    return UP;
                }

                if (deltaX > deltaY * 2) {
                    return RIGHT;
                }

                return UP_RIGHT;

            /* Below */
            } else {
                if (abs(deltaY) > deltaX * 2) {
                    return DOWN;
                }

                if (deltaX > abs(deltaY) * 2) {
                    return RIGHT;
                }

                return DOWN_RIGHT;

            }

        /* To the left */
        } else {

            /* Above */
            if (deltaY > 0) {

                if (deltaY > abs(deltaX) * 2) {
                    return UP;
                }

                if (abs(deltaX) > deltaY * 2) {
                    return LEFT;
                }

                return UP_LEFT;

            /* Below */
            } else {
                if (deltaY < deltaX * 2) {
                    return DOWN;
                }

                if (deltaX < deltaY * 2) {
                    return LEFT;
                }

                return DOWN_LEFT;
            }
        }
    }

    public static Point getClosestWaterPoint(Point position, GameMap map) {
        int distanceToClosestWater = Integer.MAX_VALUE;
        Point pointClosestWater = null;

        for (Point point : GameUtils.getHexagonAreaAroundPoint(position, 4, map)) {

            /* Filter points that are not connected to water */
            if (!isAny(map.getSurroundingTiles(point), Vegetation.WATER)) {
                continue;
            }

            int candidateDistance = distanceInGameSteps(position, point);

            /* Filter points that are not closer than the current best pick */
            if (candidateDistance >= distanceToClosestWater){
                continue;
            }

            distanceToClosestWater = candidateDistance;
            pointClosestWater = point;
        }

        return pointClosestWater;
    }

    public static Point getClosestWaterPointForBuilding(Building building) {
        GameMap map = building.getMap();

        Point position = building.getPosition();

        return getClosestWaterPoint(position, map);
    }

    public static int min(int... numbers) {
        int minimum = numbers[0];

        for (int number : numbers) {
            minimum = Math.min(minimum, number);
        }

        return minimum;
    }

    /**
     * Finds the closest harbor off-road for a given player within a specified radius.
     *
     * @param player   The player for whom to find the closest harbor.
     * @param position The starting position to search from.
     * @param radius   The radius within which to search for harbors.
     * @return The closest harbor off-road or null if no harbor is found within the radius.
     */
    public static Harbor getClosestHarborOffroadForPlayer(Player player, Point position, int radius) {
        var map = player.getMap();

        return player.getBuildings().stream()
                .filter(Building::isReady)
                .filter(Building::isHarbor)
                .map(Harbor.class::cast)
                .map(harbor -> {
                    var path = map.findWayOffroad(position, harbor.getPosition(), null);

                    if (path == null) {
                        return new Tuple<>(harbor, Integer.MAX_VALUE);
                    }

                    return new Tuple<>(harbor, path.size());
                })
                .filter(tuple -> tuple.t2() <= radius)
                .min(Comparator.comparingInt(Tuple::t2))
                .map(Tuple::t1)
                .orElse(null);
    }

    public static void putCargos(Material material, int amount, Building building) {
        GameMap map = building.getMap();

        for (int i = 0; i < amount; i++) {
            Cargo cargo = new Cargo(material, map);

            building.promiseDelivery(material);

            building.putCargo(cargo);
        }
    }

    public static void retrieveCargos(Storehouse storehouse, Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            storehouse.retrieve(material);
        }
    }

    interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);
    }

    static class PointAndCost implements Comparable<PointAndCost> {
        private final Point point;
        private final int estimatedFullCostThroughPoint;

        PointAndCost(Point point, int estimatedFullCostThroughPoint) {
            this.point = point;
            this.estimatedFullCostThroughPoint = estimatedFullCostThroughPoint;
        }

        // TODO: align with implementation of equals to make them consistent!
        @Override
        public int compareTo(PointAndCost pointAndCost) {
            if (estimatedFullCostThroughPoint < pointAndCost.estimatedFullCostThroughPoint) {
                return -1;
            } else if (estimatedFullCostThroughPoint > pointAndCost.estimatedFullCostThroughPoint) {
                return 1;
            }

            return 0;
        }

        @Override
        public String toString() {
            return " Point: " + point + ", cost: " + estimatedFullCostThroughPoint;
        }
    }

    // FIXME: HOTSPOT
    static List<Point> findShortestPath(Point start, Point goal, Set<Point> avoid, ConnectionsProvider connectionProvider) {
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        Map<Point, Point>   cameFrom         = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        bestCaseCost = distanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        /* Keep going through points yet to be evaluated until either a perfect match is found or all points have been done */
        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point to evaluate with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                Point previousPoint = currentPoint.point;
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (previousPoint != start) {
                    path.addFirst(previousPoint);

                    previousPoint = cameFrom.get(previousPoint);
                }

                path.addFirst(start);

                return path;
            }

            /* Evaluate each direct neighbor */
            for (Point neighbor : connectionProvider.getPossibleConnections(currentPoint.point, goal)) {

                /* Skip points we should avoid */
                if (avoid != null && avoid.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + GameUtils.distanceInGameSteps(currentPoint.point, neighbor);

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint.point);

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + distanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndEstimatedCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndEstimatedCost);
                }
            }
        }

        return null;
    }

    public static Point getClosestPoint(double px, double py) {

        /* Round to integers */
        int roundedX = (int) round(px);
        int roundedY = (int) round(py);

        /* Calculate the error */
        double errorX = abs(px - roundedX);
        double errorY = abs(py - roundedY);

        /* Adjust the values if needed to avoid invalid points */
        if ((roundedX + roundedY) % 2 != 0) {
            if (errorX < errorY) {
                if (roundedY > py) {
                    roundedY = (int) floor(py);
                } else {
                    roundedY = (int) ceil(py);
                }
            } else if (errorX > errorY) {
                if (roundedX > px) {
                    roundedX = (int) floor(px);
                } else {
                    roundedX = (int) ceil(px);
                }
            } else {
                roundedX++;
            }
        }

        return new Point(roundedX, roundedY);
    }

    public static class PathOnExistingRoadsProvider implements ConnectionsProvider {
        private final GameMap map;

        public PathOnExistingRoadsProvider(GameMap map) {
            this.map = map;
        }

        @Override
        public Iterable<Point> getPossibleConnections(Point start, Point goal) {
            return map.getMapPoint(start).getConnectedNeighbors();
        }
    }

    /**
     * Finds the shortest path following roads between any two points. The points
     * must be flags or buildings.
     *
     * The path returned will only contain points with flags or buildings.
     *
     * @param start The point to start from
     * @param goal The point to reach
     * @param map The instance of the map
     * @param avoid List of points to avoid when finding the path
     * @return the list of flag points to pass (included the starting point) required to travel from start to goal
     */
    // FIXME: ALLOCATION HOTSPOT
    static List<Point> findShortestPathViaRoads(Point start, Point goal, GameMap map, Point... avoid) {
        Set<Point>          evaluated        = new HashSet<>();
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        Map<Point, Point>   cameFrom         = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        Set<Point> avoidSet = null;

        if (avoid.length != 0) {
            avoidSet = new HashSet<>(Arrays.asList(avoid));
        }

        /* Define starting parameters */
        bestCaseCost = distanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point to evaluate with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                Point previousPoint = currentPoint.point;
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken */
                while (previousPoint != start) {
                    path.addFirst(previousPoint);

                    previousPoint = cameFrom.get(previousPoint);
                }

                path.addFirst(start);

                return path;
            }

            /* Do not re-evaluate the same point */
            evaluated.add(currentPoint.point);

            /* Evaluate each neighbor directly connected by a road */
            MapPoint mapPoint = map.getMapPoint(currentPoint.point);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint.point);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Skip points to avoid */
                if (avoidSet != null && avoidSet.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Keep track of how the neighbor was reached */
                    cameFrom.put(neighbor, currentPoint.point);

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + distanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
                }
            }
        }

        return null;
    }

    /**
     * Determines whether any two points are connected by roads. The points
     * don't need to be flags or buildings but can be any point on a road.
     *
     * @param start The point to start from
     * @param goal The point to reach
     * @param map The game map instance
     * @return true if the start and end are connected
     */
    static boolean arePointsConnectedByRoads(Point start, Point goal, GameMap map) {
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        bestCaseCost = distanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                return true;
            }

            /* Evaluate each direct neighbor */
            MapPoint mapPoint = map.getMapPoint(currentPoint.point);

            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint.point);

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + distanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
                }
            }
        }

        return false;
    }

    /**
     * Returns a detailed path including points between flags or buildings. Can
     * only be called with a building or flag as start and end point.
     *
     * @param startEndPoint Flag or building to start from
     * @param goalEndPoint Flag or building to reach
     * @param map The instance of the map
     * @return a detailed list with the steps required to travel from the start to the goal.
     */
    static List<Point> findShortestDetailedPathViaRoads(EndPoint startEndPoint, EndPoint goalEndPoint, GameMap map, Point... avoid) {
        Set<Point>         evaluated         = new HashSet<>();
        Set<Point>         toEvaluate        = new HashSet<>();
        Map<Point, Double> realCostToPoint   = new HashMap<>();
        Map<Point, Double> estimatedFullCost = new HashMap<>();
        Map<Point, Road>   cameVia           = new HashMap<>();
        double             bestCaseCost;

        Set<Point> avoidSet = null;

        if (avoid.length != 0) {
            avoidSet = new HashSet<>(Arrays.asList(avoid));
        }

        Point start = startEndPoint.getPosition();
        Point goal = goalEndPoint.getPosition();

        /* Define starting parameters */
        bestCaseCost = start.distance(goal);
        toEvaluate.add(start);
        realCostToPoint.put(start, (double)0);
        estimatedFullCost.put(start, realCostToPoint.get(start) + start.distance(goal));

        /* Declare variables outside the loop to keep memory churn down */
        Point currentPoint;
        double currentEstimatedCost;

        double tmpEstimatedCost;

        double tentativeCost;

        while (!toEvaluate.isEmpty()) {
            currentPoint = null;
            currentEstimatedCost = Double.MAX_VALUE;

            /* Find the point with the lowest estimated full cost */
            for (Point iteratedPoint : toEvaluate) {

                tmpEstimatedCost = estimatedFullCost.get(iteratedPoint);

                if (currentEstimatedCost > tmpEstimatedCost) {
                    currentEstimatedCost = tmpEstimatedCost;
                    currentPoint = iteratedPoint;

                    if (currentEstimatedCost == bestCaseCost) {
                        break;
                    }
                }
            }

            /* Handle if the goal is reached */
            if (Objects.equals(currentPoint, goal)) {
                List<Point> path = new ArrayList<>();

                /* Re-construct the path taken, backwards from the goal */
                while (!currentPoint.equals(start)) {
                    Road road = cameVia.get(currentPoint);

                    /* Follow the road and add up the points */
                    int numberWayPoints = road.getWayPoints().size();

                    List<Point> roadPoints = road.getWayPoints();

                    if (roadPoints.getFirst().equals(currentPoint)) {

                        for (int i = 0; i < numberWayPoints - 1; i++) {
                            path.addFirst(roadPoints.get(i));
                        }

                        currentPoint = roadPoints.getLast();
                    } else {
                        for (int i = numberWayPoints - 1; i > 0; i--) {
                            path.addFirst(roadPoints.get(i));
                        }

                        currentPoint = roadPoints.getFirst();
                    }
                }

                path.addFirst(currentPoint);

                return path;
            }

            /* Do not re-evaluate the same point */
            toEvaluate.remove(currentPoint);
            evaluated.add(currentPoint);

            /* Evaluate each direct neighbor */
            MapPoint mapPoint = map.getMapPoint(currentPoint);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Skip points to avoid */
                if (avoidSet != null && avoidSet.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                tentativeCost = realCostToPoint.get(currentPoint) +
                        road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we
                 * have found a cheaper way to reach it
                */
                if (!toEvaluate.contains(neighbor) || tentativeCost < realCostToPoint.get(neighbor)) {

                    /* Keep track of how the neighbor was reached */
                    cameVia.put(neighbor, road);

                    /* Remember the cost to reach the neighbor */
                    realCostToPoint.put(neighbor, tentativeCost);

                    /* Remember the estimated full cost to go via the neighbor */
                    estimatedFullCost.put(neighbor, realCostToPoint.get(neighbor) + neighbor.distance(goal));

                    /* Add the neighbor to the evaluation list */
                    toEvaluate.add(neighbor);
                }
            }
        }

        return null;
    }

    /**
     * Determines if two points with flags or buildings are connected by roads.
     *
     * @param startEndPoint Flag or building to start from
     * @param goalEndPoint Flag or building to reach
     * @param map The instance of the map
     * @return true if the start and end are connected
     */
    public static boolean areBuildingsOrFlagsConnected(EndPoint startEndPoint, EndPoint goalEndPoint, GameMap map) {
        Set<Point>          evaluated        = new HashSet<>();
        Map<Point, Integer> costToGetToPoint = new HashMap<>();
        int                 bestCaseCost;

        PriorityQueue<PointAndCost> toEvaluatePriorityQueue = new PriorityQueue<>();

        /* Define starting parameters */
        Point start = startEndPoint.getPosition();
        Point goal = goalEndPoint.getPosition();

        bestCaseCost = distanceInGameSteps(start, goal);
        costToGetToPoint.put(start, 0);

        PointAndCost startingPointAndCost = new PointAndCost(start, bestCaseCost);

        toEvaluatePriorityQueue.add(startingPointAndCost);

        /* Declare variables outside the loop to keep memory churn down */
        PointAndCost currentPoint;
        int newCostToGetToPoint;

        while (!toEvaluatePriorityQueue.isEmpty()) {

            /* Find the point to evaluate with the lowest estimated full cost */
            currentPoint = toEvaluatePriorityQueue.poll();

            /* Handle if the goal is reached */
            if (goal.equals(currentPoint.point)) {
                return true;
            }

            /* Do not re-evaluate the same point */
            evaluated.add(currentPoint.point);

            /* Evaluate each direct neighbor */
            MapPoint mapPoint = map.getMapPoint(currentPoint.point);
            for (Road road : mapPoint.getConnectedRoads()) {

                Point neighbor = road.getOtherPoint(currentPoint.point);

                /* Skip already evaluated points */
                if (evaluated.contains(neighbor)) {
                    continue;
                }

                /* Calculate the real cost to reach the neighbor from the start */
                newCostToGetToPoint = costToGetToPoint.get(currentPoint.point) + road.getWayPoints().size() - 1;

                /* Check if the neighbor hasn't been evaluated yet or if we have found a cheaper way to reach it */
                int currentCostToGetToPoint = costToGetToPoint.getOrDefault(neighbor, Integer.MAX_VALUE);

                if (newCostToGetToPoint < currentCostToGetToPoint) {

                    /* Remember the cost to reach the neighbor */
                    costToGetToPoint.put(neighbor, newCostToGetToPoint);

                    /* Remember the estimated full cost to go via the neighbor */
                    int estimatedFullCostThroughPoint = newCostToGetToPoint + distanceInGameSteps(neighbor, goal);

                    /* Add the neighbor to the evaluation list */
                    PointAndCost neighborPointAndCost = new PointAndCost(neighbor, estimatedFullCostThroughPoint);

                    toEvaluatePriorityQueue.add(neighborPointAndCost);
                }
            }
        }

        return false;
    }

    /**
     * Finds the closest storage offroad for a given player from a specified point.
     *
     * @param player The player for whom the closest storage is to be found.
     * @param point  The starting point to search from.
     * @return The closest Storehouse building that is accessible offroad, or null if none are available.
     */
    public static Storehouse getClosestStorageOffroad(Player player, Point point) {
        GameMap map = player.getMap();

        return player.getBuildings().stream()
                .filter(Building::isReady)
                .filter(Building::isStorehouse)
                .map(building -> new Tuple<>(
                        (Storehouse) building,
                        map.findWayOffroad(point, building.getPosition(), null)))
                .filter(entry -> entry.t2() != null)
                .min(Comparator.comparingInt(entry -> entry.t2().size()))
                .map(Tuple::t1)
                .orElse(null);
    }

    public static Storehouse getClosestStorageConnectedByRoads(Point point, Player player) {
        return getClosestStorageConnectedByRoads(point, null, player);
    }

    /**
     * Finds the closest storehouse connected by roads to the given point for a specified player.
     *
     * @param point  The starting point from which to find the closest storehouse.
     * @param avoid  The building to avoid in the search.
     * @param player The player whose buildings will be considered in the search.
     * @return The closest storehouse connected by roads, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, Player player) {
        GameMap map = player.getMap();

        return player.getBuildings().stream()
                .filter(building -> !building.equals(avoid))
                .filter(Building::isReady)
                .filter(Building::isStorehouse)
                .map(building -> {

                    // If the building's flag is directly at the point, return it immediately
                    if (building.getFlag().getPosition().equals(point)) {
                        return new Tuple<>((Storehouse) building, 0);
                    }

                    List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());
                    return new Tuple<>((Storehouse) building, path == null ? Integer.MAX_VALUE : path.size());
                })
                .filter(entry -> entry.t2() < Integer.MAX_VALUE)
                .min(Comparator.comparingInt(Tuple::t2))
                .map(Tuple::t1)
                .orElse(null);
    }

    public static Storehouse getClosestStorageConnectedByRoads(Point point, GameMap map) {
        return getClosestStorageConnectedByRoads(point, null, map);
    }

    public record Tuple<T1, T2>(T1 t1, T2 t2) { }

    /**
     * Finds the closest storehouse connected by roads to the given point.
     *
     * @param point The starting point from which to find the closest storehouse.
     * @param avoid The building to avoid in the search.
     * @param map   The game map containing buildings and roads.
     * @return The closest storehouse connected by roads, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, Building avoid, GameMap map) {
        return map.getBuildings().stream()
                .filter(building -> !building.equals(avoid))
                .filter(Building::isReady)
                .filter(Building::isStorehouse)
                .map(building -> {

                    // Check if the building's flag is directly at the point
                    if (building.getFlag().getPosition().equals(point)) {
                        return new Tuple<>((Storehouse) building, 0);
                    }

                    // Calculate the path to the building's flag
                    List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());
                    return new Tuple<>((Storehouse) building, path == null ? Integer.MAX_VALUE : path.size());
                })
                .filter(tuple -> tuple.t2() < Integer.MAX_VALUE)
                .min(Comparator.comparingInt(Tuple::t2))
                .map(Tuple::t1)
                .orElse(null);
    }

    public static Building getClosestBuildingConnectedByRoads(Point point, Building avoid, GameMap map, Function <Building, Boolean> func) {
        return map.getBuildings().stream()
                .filter(candidate -> !candidate.equals(avoid))
                .filter(func::apply)
                .map(candidate -> {
                    if (candidate.getFlag().getPosition().equals(point)) {
                        return new AbstractMap.SimpleEntry<>(candidate, List.of(point));
                    }
                    List<Point> path = map.findWayWithExistingRoads(point, candidate.getFlag().getPosition());
                    return new AbstractMap.SimpleEntry<>(candidate, path);
                })
                .filter(entry -> entry.getValue() != null)
                .min(Comparator.comparingInt(entry -> entry.getValue().size()))
                .map(AbstractMap.SimpleEntry::getKey)
                .orElse(null);
    }

    public static Storehouse getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(Point point, Building avoid, GameMap map, Material material) {
        return (Storehouse) map.getBuildings().stream()
                .filter(building -> !building.equals(avoid))                       // Avoid specific building
                .filter(Building::isReady)                                         // Check if building is ready
                .filter(Building::isStorehouse)                                    // Check if it's a storehouse
                .filter(building -> !((Storehouse) building).isDeliveryBlocked(material)) // Check delivery is possible
                .map(building -> {
                    if (building.getFlag().getPosition().equals(point)) {
                        return new AbstractMap.SimpleEntry<>(building, 0);
                    }

                    List<Point> path = map.findWayWithExistingRoads(point, building.getFlag().getPosition());
                    return path == null ? null : new AbstractMap.SimpleEntry<>(building, path.size());
                })
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))   // Find min by path size
                .map(AbstractMap.SimpleEntry::getKey)
                .orElse(null);
    }
    public static Set<Flag> findFlagsReachableFromPoint(Player player, Point start) {
        List<Point> toEvaluate = new LinkedList<>();
        Set<Point>  visited    = new HashSet<>();
        Set<Flag>   reachable  = new HashSet<>();
        GameMap     map        = player.getMap();

        toEvaluate.add(start);

        /* Declare variables outside the loop to keep memory churn down */
        Point point;
        Point oppositePoint;

        while (!toEvaluate.isEmpty()) {

            point = toEvaluate.getFirst();
            toEvaluate.remove(point);

            MapPoint mapPoint = map.getMapPoint(point);

            /* Test if this point is connected to a building */
            if (mapPoint.isFlag()) {
                reachable.add(mapPoint.getFlag());
            }

            /* Remember that this point has been tested */
            visited.add(point);

            /* Go through the neighbors and add the new points to the list to be evaluated */
            for (Road road : mapPoint.getConnectedRoads()) {

                oppositePoint = road.getOtherPoint(point);

                /* Filter already visited */
                if (visited.contains(oppositePoint)) {
                    continue;
                }

                /* Add the point to the list */
                toEvaluate.add(oppositePoint);
            }
        }

        return reachable;
    }

    public static Set<Building> getBuildingsWithinReach(Flag flag) {
        return getBuildingsWithinReach(flag.getPosition(), flag.getPlayer());
    }

    public static Set<Building> getBuildingsWithinReach(Point startPosition, Player player) {
        List<Point> toEvaluate = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        Set<Building> reachable = new HashSet<>();
        GameMap map = player.getMap();

        toEvaluate.add(startPosition);

        while (!toEvaluate.isEmpty()) {
            Point point = toEvaluate.removeFirst();
            MapPoint mapPoint = map.getMapPoint(point);

            if (mapPoint.isBuilding()) reachable.add(mapPoint.getBuilding());
            visited.add(point);

            mapPoint.getConnectedRoads().stream()
                    .map(road -> road.getOtherPoint(point))
                    .filter(oppositePoint -> !visited.contains(oppositePoint))
                    .forEach(toEvaluate::add);
        }

        return reachable;
    }

    public static int distanceInGameSteps(Point start, Point end) {
        int distanceX = abs(start.x - end.x);
        int distanceY = abs(start.y - end.y);

        if (distanceX > distanceY) {
            return distanceY + (distanceX - distanceY) / 2;
        } else {
            return distanceY;
        }
    }

    public static double calculateAngle(int directionX, int directionY) {
        double angle;

        if (directionX == 0 && directionY > 0) {
            angle = Math.PI / 2;
        } else if (directionX == 0 && directionY < 0) {
            angle = 1.5 * Math.PI;
        } else if (directionY == 0 && directionX > 0) {
            angle = 0;
        } else if (directionY == 0 && directionX < 0) {
            angle = Math.PI;
        } else {

            angle = Math.atan((double)directionY / (double)directionX);

            if (directionX < 0 && directionY > 0) {
                angle = angle + Math.PI;
            }

            if (directionX < 0 && directionY < 0) {
                angle = angle + Math.PI;
            }

            if (directionX > 0 && directionY < 0) {
                angle = angle - 2 * Math.PI;
            }
        }

        return angle;
    }

    public static class HouseOrRoad {
        public final Building building;
        public final Road road;

        HouseOrRoad(Building building) {
            this.building = building;
            this.road = null;
        }

        HouseOrRoad(Road road) {
            this.road = road;
            this.building = null;
        }

        public boolean isBuilding() {
            return building != null;
        }

        public boolean isRoad() {
            return road != null;
        }
    }

    private static int rankToInt(Soldier.Rank rank) {
        return switch (rank) {
            case PRIVATE_RANK -> 0;
            case PRIVATE_FIRST_CLASS_RANK -> 1;
            case SERGEANT_RANK -> 2;
            case OFFICER_RANK -> 3;
            case GENERAL_RANK -> 4;
        };
    }

    public static Comparator<? super Soldier> strengthSorter = Comparator.comparingInt(s -> rankToInt(s.getRank()));

    public static Comparator<SoldierAndDistance> strongerAndShorterDistanceSorter = Comparator
            .comparing((SoldierAndDistance sd) -> rankToInt(sd.soldier.getRank())).reversed()
            .thenComparingInt(sd -> sd.distance);


    public static Comparator<SoldierAndDistance> weakerAndShorterDistanceSorter = Comparator
            .comparingInt((SoldierAndDistance sd) -> rankToInt(sd.soldier.getRank()))
            .thenComparingInt(sd -> sd.distance);

    public record SoldierAndDistance(Soldier soldier, int distance) { }

    public static List<Soldier.Rank> strengthToRank(int strength) {
        List<Integer> populationPreferenceOrder = new ArrayList<>();

        populationPreferenceOrder.add(strength);

        for (int i = 1; i < Math.max(10 - strength, strength); i++) {
            if (strength + i < 11) {
                populationPreferenceOrder.add(strength + i);
            }

            if (strength - i > -1) {
                populationPreferenceOrder.add(strength - i);
            }
        }

        /* Go through the list in order of preference and add the rank */
        List<Soldier.Rank> ranks = new ArrayList<>();

        for (int preferred : populationPreferenceOrder) {
            Soldier.Rank rank = Soldier.Rank.intToRank(preferred);

            if (ranks.isEmpty() || ranks.getLast() != rank) {
                ranks.add(rank);
            }
        }

        return ranks;
    }
}
