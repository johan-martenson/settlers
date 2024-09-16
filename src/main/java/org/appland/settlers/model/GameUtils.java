package org.appland.settlers.model;

import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.SawmillWorker;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.Headquarter;
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
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static org.appland.settlers.model.Direction.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;

/**
 *
 * @author johan
 */
public class GameUtils {

    /**
     * Sorts a list of soldiers based on the preferred strength, expressed as a value from 0 to 10.
     *
     * @param soldiers List of soldiers to be sorted.
     * @param strength Preferred strength of soldiers
     * @return A new list of soldiers sorted by preferred rank.
     */
    public static List<Soldier> sortSoldiersByPreferredStrength(List<Soldier> soldiers, int strength) {
        return soldiers.stream()
                .sorted((soldier0, soldier1) -> {
                    List<Soldier.Rank> prefRankList = GameUtils.strengthToRank(strength);

                    int rankDist0 = prefRankList.indexOf(soldier0.getRank());
                    int rankDist1 = prefRankList.indexOf(soldier1.getRank());

                    return Integer.compare(rankDist0, rankDist1);
                })
                .collect(Collectors.toList());
    }

    /**
     * Sorts a list of soldiers by the preferred strength and distance from a given position.
     *
     * @param soldiers List of soldiers to be sorted.
     * @param strength Strength value determining the preference order of ranks.
     * @param position Position from which distance is calculated.
     */
    public static void sortSoldiersByPreferredStrengthAndDistance(List<Soldier> soldiers, int strength, Point position) {
        soldiers.sort((soldier0, soldier1) -> {
            if (soldier0.getRank() == soldier1.getRank()) {
                var dist0 = GameUtils.distanceInGameSteps(soldier0.getHome().getPosition(), position);
                var dist1 = GameUtils.distanceInGameSteps(soldier1.getHome().getPosition(), position);

                return Integer.compare(dist0, dist1);
            } else {
                var prefRankList = GameUtils.strengthToRank(strength);

                var rankDist0 = prefRankList.indexOf(soldier0.getRank());
                var rankDist1 = prefRankList.indexOf(soldier1.getRank());

                return Integer.compare(rankDist0, rankDist1);
            }
        });
    }

    /**
     * Checks if all provided collections are empty.
     *
     * @param collections Varargs of collections to be checked.
     * @return True if all collections are empty, false otherwise.
     */
    public static boolean allCollectionsEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).allMatch(Collection::isEmpty);
    }

    /**
     * Checks if all provided maps are empty.
     *
     * @param maps Varargs of maps to be checked.
     * @return True if all maps are empty, false otherwise.
     */
    public static boolean allMapsEmpty(Map<?, ?>... maps) {
        return Arrays.stream(maps).allMatch(Map::isEmpty);
    }

    /**
     * Returns the headquarters for the given player
     *
     * @param player
     * @return
     */
    public static Headquarter getHeadquarterForPlayer(Player player) {
        for (Building building : player.getBuildings()) {
            if (building instanceof Headquarter) {
                return (Headquarter) building;
            }
        }

        return null;
    }

    /**
     * Returns the distance to the closest border point for the given point
     *
     * @param infoPoint
     * @param player
     * @return
     */
    public static int getDistanceToBorder(Point infoPoint, Player player) {
        int distance = Integer.MAX_VALUE;
        for (Point point : player.getBorderPoints()) {

            int tmpDistance = distanceInGameSteps(point, infoPoint);

            if (tmpDistance < distance) {
                distance = tmpDistance;
            }
        }

        return distance;
    }

    /**
     * Enum for types of allocation that can be controlled.
     */
    public enum AllocationType {
        WHEAT_ALLOCATION,
        COAL_ALLOCATION,
        WATER_ALLOCATION,
        FOOD_ALLOCATION,
        PLANK_ALLOCATION,
        IRON_BAR_ALLOCATION
    }

    /**
     * Tracks allocations for resources and materials in the game.
     * Manages the quota and allocation of these resources.
     */
    public static class AllocationTracker {
        private static final Map<AllocationType, Set<Class<? extends Building>>> AFFECTED_BUILDING_TYPES = new HashMap<>();
        private static final Map<AllocationType, Set<Material>> TRACKED_MATERIALS = new HashMap<>();

        static {
            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.WHEAT_ALLOCATION,
                    Set.of(Mill.class, Brewery.class, DonkeyFarm.class, PigFarm.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.COAL_ALLOCATION,
                    Set.of(Mint.class, Metalworks.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.WATER_ALLOCATION,
                    Set.of(Bakery.class, Brewery.class, DonkeyFarm.class, PigFarm.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.FOOD_ALLOCATION,
                    Set.of(CoalMine.class, IronMine.class, GoldMine.class, GraniteMine.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.IRON_BAR_ALLOCATION,
                    Set.of(Armory.class, Metalworks.class)
            );

            TRACKED_MATERIALS.put(AllocationType.WHEAT_ALLOCATION, Set.of(WHEAT));
            TRACKED_MATERIALS.put(AllocationType.COAL_ALLOCATION, Set.of(COAL));
            TRACKED_MATERIALS.put(AllocationType.WATER_ALLOCATION, Set.of(WATER));
            TRACKED_MATERIALS.put(AllocationType.IRON_BAR_ALLOCATION, Set.of(IRON_BAR));
            TRACKED_MATERIALS.put(AllocationType.PLANK_ALLOCATION, Set.of(PLANK));
            TRACKED_MATERIALS.put(AllocationType.FOOD_ALLOCATION, Set.of(BREAD, FISH, MEAT));
        }

        private final Map<Class<? extends Building>, Integer> consumed = new HashMap<>();
        private final AllocationType allocationType;
        private final Player player;
        private final Point position;

        public AllocationTracker(AllocationType allocationType, Player player, Point position) {
            this.player = player;
            this.allocationType = allocationType;
            this.position = position;
        }

        /**
         * Tracks the allocation of resources for a building by incrementing the consumed count.
         *
         * @param building The building for which the allocation is tracked.
         */
        public void trackAllocation(Building building) {
            int amount = consumed.getOrDefault(building.getClass(), 0);
            consumed.put(building.getClass(), amount + 1);
        }

        /**
         * Determines if delivery is allowed for a building based on the allocation type.
         *
         * @param building The building to check for delivery allowance.
         * @return True if delivery is allowed, false otherwise.
         */
        public boolean isDeliveryAllowed(Building building) {
            Material material = TRACKED_MATERIALS.get(allocationType).stream().findFirst().get();

            return isDeliveryAllowed(building, material);
        }

        /**
         * Determines if delivery is allowed for a building given a specific material.
         *
         * @param building The building to check for delivery allowance.
         * @param material The material to be delivered.
         * @return True if delivery is allowed, false otherwise.
         */
        public boolean isDeliveryAllowed(Building building, Material material) {
            int quota = quotaForBuilding(building);

            var withinQuota = consumed.getOrDefault(building.getClass(), 0) < quota;

            if (withinQuota) {
                return true;
            }

            var didReset = resetAllocationIfNeeded(material);

            return didReset && consumed.getOrDefault(building.getClass(), 0) < quota;
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

        /**
         * Checks if a building type is over its allocation quota.
         *
         * @param buildingType The type of building to check.
         * @return True if the building type is over quota, false otherwise.
         */
        public boolean isOverQuota(Class<? extends Building> buildingType) {
            return consumed.getOrDefault(buildingType, 0) >= quotaForBuildingType(buildingType);
        }

        /**
         * Resets allocation if needed based on the material and reachable buildings.
         *
         * @param material The material to check for allocation reset.
         * @return True if the allocation was reset, false otherwise.
         */
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

    /**
     * Checks if any of the points in the specified list exists in the given set.
     *
     * @param set   The set of discovered land points.
     * @param list  The list of waypoints to check for presence in the set.
     * @return True if any of the points are in the set, false otherwise.
     */public static boolean setContainsAny(Set<Point> set, List<Point> list) {
        return list.stream().anyMatch(set::contains);
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

    /**
     * Represents a point and its associated cost.
     * Used for priority-based pathfinding algorithms.
     */
    record ToSearchItem(Point point, int cost) implements Comparable<ToSearchItem> {

        // TODO: align with implementation of equals to make them consistent!
        @Override
        public int compareTo(ToSearchItem otherItem) {
                return Integer.compare(this.cost, otherItem.cost);
        }
    }

    /**
     * Finds the closest house or road from a starting point that matches the given criteria.
     *
     * @param start   The starting point for the search.
     * @param isMatch A function to determine if a HouseOrRoad matches the criteria.
     * @param map     The game map to search within.
     * @return An Optional containing the closest matching HouseOrRoad, or an empty Optional if none found.
     */
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

    /**
     * Represents a combination of building and associated data.
     *
     * @param <B> Type of the building.
     * @param <D> Type of the associated data.
     */
    public record BuildingAndData<B extends Building, D>(B building, D data) { }

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

    /**
     * Determines if all items in a collection are equal to a given item.
     *
     * @param collection The collection to check.
     * @param item       The item to compare against.
     * @param <T>        The type of the item.
     * @return True if all items are equal to the specified item, false otherwise.
     */
    public static <T> boolean isAll(Collection<T> collection, T item) {
        return collection.stream().allMatch(item::equals);
    }

    /**
     * Determines if some, but not all, items in a collection are equal to a given item.
     *
     * @param collection The collection to check.
     * @param item       The item to compare against.
     * @param <T>        The type of the item.
     * @return True if some but not all items are equal to the specified item, false otherwise.
     */
    public static <T> boolean isSomeButNotAll(Collection<T> collection, T item) {
        return collection.stream().anyMatch(item::equals) &&
               collection.stream().anyMatch(e -> !item.equals(e));
    }

    /**
     * Upgrades a military building, transferring soldiers and resources.
     *
     * @param fromBuilding The original building to be upgraded.
     * @param upgraded     The new upgraded building.
     */
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

    /**
     * Checks if an integer is even.
     *
     * @param i The integer to check.
     * @return True if the integer is even, false otherwise.
     */
    public static boolean isEven(int i) {
        return i % 2 == 0;
    }

    /**
     * Determines if any of the items in a list are part of a given set.
     *
     * @param <T>   The type of item.
     * @param set   The collection to check against.
     * @param items The list of items to check.
     * @return True if any of the items are in the collection, false otherwise.
     */
    public static <T> boolean containsAny(Set<T> set, Collection<T> items) {
        return items.stream().anyMatch(set::contains);
    }

    /**
     * Determines if all points in a collection are unique.
     *
     * @param points The collection of points to check.
     * @return True if all points are unique, false otherwise.
     */
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

    /**
     * Finds the closest water point from a given position on the map.
     *
     * @param position The starting position to search from.
     * @param map      The game map containing water tiles.
     * @return The closest water point, or null if none is found.
     */
    public static Point getClosestWaterPoint(Point position, GameMap map) {
        return GameUtils.getHexagonAreaAroundPoint(position, 4, map).stream()
                .filter(point -> map.getSurroundingTiles(point).contains(Vegetation.WATER))
                .min(Comparator.comparingInt(point -> distanceInGameSteps(position, point)))
                .orElse(null);
    }

    /**
     * Finds the closest water point for a given building on the map.
     *
     * @param building The building for which the closest water point is to be found.
     * @return The closest water point, or null if none is found.
     */
    public static Point getClosestWaterPointForBuilding(Building building) {
        return getClosestWaterPoint(building.getPosition(), building.getMap());
    }

    /**
     * Finds the minimum value among the provided integers.
     *
     * @param numbers The integers to compare.
     * @return The minimum value among the numbers.
     */
    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(numbers[0]);
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

    /**
     * Puts a specified amount of cargos of a material into a building.
     *
     * @param material The material to be put into the building.
     * @param amount   The amount of cargos to put into the building.
     * @param building The building where the cargos are to be placed.
     */
    public static void putCargos(Material material, int amount, Building building) {
        GameMap map = building.getMap();

        for (int i = 0; i < amount; i++) {
            Cargo cargo = new Cargo(material, map);
            building.promiseDelivery(material);
            building.putCargo(cargo);
        }
    }

    /**
     * Retrieves a specified amount of cargos of a material from a storehouse.
     *
     * @param storehouse The storehouse from which to retrieve cargos.
     * @param material   The material to retrieve from the storehouse.
     * @param amount     The amount of cargos to retrieve.
     */
    public static void retrieveCargos(Storehouse storehouse, Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            storehouse.retrieve(material);
        }
    }

    interface ConnectionsProvider {
        Iterable<Point> getPossibleConnections(Point start, Point goal);
    }

    /**
     * Represents a point and its estimated cost for pathfinding.
     * Used in priority queues for pathfinding algorithms.
     */
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
            return Integer.compare(this.estimatedFullCostThroughPoint, pointAndCost.estimatedFullCostThroughPoint);
        }

        @Override
        public String toString() {
            return String.format("Point: %s, cost: %d", point, estimatedFullCostThroughPoint);
        }
    }

    /**
     * Finds the shortest path between two points, avoiding specified points.
     *
     * @param start             The starting point.
     * @param goal              The goal point.
     * @param avoid             Set of points to avoid during pathfinding.
     * @param connectionProvider Provides connections between points.
     * @return The shortest path as a list of points, or null if no path is found.
     */
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

    /**
     * Finds the closest point to the given coordinates.
     *
     * @param px The x-coordinate.
     * @param py The y-coordinate.
     * @return The closest point as a Point object.
     */
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

    /**
     * Provides possible connections between points using existing roads on the map.
     */
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
     * must be flags or buildings. The path returned will only contain points with flags or buildings.
     *
     * @param start The point to start from.
     * @param goal  The point to reach.
     * @param map   The instance of the map.
     * @param avoid List of points to avoid when finding the path.
     * @return The list of flag points to pass (including the starting point) required to travel from start to goal.
     */
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
     * @param start The point to start from.
     * @param goal  The point to reach.
     * @param map   The game map instance.
     * @return True if the start and end are connected, false otherwise.
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
     * @param startEndPoint Flag or building to start from.
     * @param goalEndPoint  Flag or building to reach.
     * @param map           The instance of the map.
     * @return A detailed list with the steps required to travel from the start to the goal.
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
     * @param startEndPoint Flag or building to start from.
     * @param goalEndPoint  Flag or building to reach.
     * @param map           The instance of the map.
     * @return True if the start and end are connected, false otherwise.
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
     * Finds the closest storehouse off-road for a given player from a specified point.
     *
     * @param player The player for whom the closest storehouse is to be found.
     * @param point  The starting point to search from.
     * @return The closest Storehouse building that is accessible off-road, or null if none are available.
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

    /**
     * Finds the closest storehouse connected by roads for a given player from a specified point.
     *
     * @param point  The starting point to search from.
     * @param player The player for whom the closest storehouse is to be found.
     * @return The closest Storehouse building that is connected by roads, or null if none are available.
     */
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

    /**
     * Finds the closest storehouse connected by roads to the given point for a specified map.
     *
     * @param point The starting point from which to find the closest storehouse.
     * @param map   The game map containing the buildings.
     * @return The closest storehouse connected by roads, or null if none is found.
     */
    public static Storehouse getClosestStorageConnectedByRoads(Point point, GameMap map) {
        return getClosestStorageConnectedByRoads(point, null, map);
    }

    /**
     * A utility class representing a tuple of two elements.
     *
     * @param <T1> The type of the first element.
     * @param <T2> The type of the second element.
     */
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

    /**
     * Finds the closest building connected by roads for a specified map, point, and condition.
     *
     * @param point    The starting point from which to find the closest building.
     * @param avoid    The building to avoid in the search.
     * @param map      The game map containing the buildings.
     * @param func     The function to determine if a building meets the criteria.
     * @return The closest building connected by roads that meets the criteria, or null if none is found.
     */
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

    /**
     * Finds the closest storehouse connected by roads where delivery is possible.
     *
     * @param point    The starting point from which to find the closest storehouse.
     * @param avoid    The building to avoid in the search.
     * @param map      The game map containing the buildings.
     * @param material The material for which delivery is possible.
     * @return The closest storehouse connected by roads where delivery is possible, or null if none is found.
     */
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

    /**
     * Finds flags that are reachable from a starting point for a specific player.
     *
     * @param player The player whose flags are being searched.
     * @param start  The starting point for the search.
     * @return A set of flags reachable from the starting point.
     */
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

    /**
     * Gets buildings within reach of a specified flag.
     *
     * @param flag The flag from which reachability is determined.
     * @return A set of buildings reachable from the flag.
     */
    public static Set<Building> getBuildingsWithinReach(Flag flag) {
        return getBuildingsWithinReach(flag.getPosition(), flag.getPlayer());
    }

    /**
     * Gets buildings within reach of a specified starting position and player.
     *
     * @param startPosition The starting position for reachability.
     * @param player        The player owning the map.
     * @return A set of buildings reachable from the starting position.
     */
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

    /**
     * Calculates the distance in game steps between two points. Game steps means that only
     * movement horizontally and diagonally is allowed, not vertically.
     *
     * @param start The starting point.
     * @param end   The end point.
     * @return The distance in game steps between the two points.
     */
    public static int distanceInGameSteps(Point start, Point end) {
        int distanceX = abs(start.x - end.x);
        int distanceY = abs(start.y - end.y);

        if (distanceX > distanceY) {
            return distanceY + (distanceX - distanceY) / 2;
        } else {
            return distanceY;
        }
    }

    /**
     * Calculates the angle between two directions given x and y components.
     *
     * @param directionX The x-component of the direction.
     * @param directionY The y-component of the direction.
     * @return The angle in radians between the directions.
     */
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

    // Comparator for sorting soldiers by strength
    public static Comparator<? super Soldier> strengthSorter = Comparator.comparingInt(s -> s.getRank().toInt());

    // Comparator for sorting soldiers by strength and shorter distance
    public static Comparator<SoldierAndDistance> strongerAndShorterDistanceSorter = Comparator
            .comparing((SoldierAndDistance sd) -> sd.soldier.getRank().toInt()).reversed()
            .thenComparingInt(sd -> sd.distance);

    // Comparator for sorting soldiers by weaker and shorter distance
    public static Comparator<SoldierAndDistance> weakerAndShorterDistanceSorter = Comparator
            .comparingInt((SoldierAndDistance sd) -> sd.soldier.getRank().toInt())
            .thenComparingInt(sd -> sd.distance);

    /**
     * Represents a soldier and its associated distance.
     * Used for sorting soldiers based on distance and rank.
     */
    public record SoldierAndDistance(Soldier soldier, int distance) { }

    /**
     * Converts a strength value to a list of preferred Soldier ranks.
     *
     * @param strength The strength value to convert.
     * @return A list of Soldier ranks ordered by preference.
     */
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

    public static Worker materialToWorker(Material material, Player player, GameMap map) {
        return switch (material) {
            case FORESTER -> new Forester(player, map);
            case WOODCUTTER_WORKER -> new WoodcutterWorker(player, map);
            case STONEMASON -> new Stonemason(player, map);
            case FARMER -> new Farmer(player, map);
            case SAWMILL_WORKER -> new SawmillWorker(player, map);
            case WELL_WORKER -> new WellWorker(player, map);
            case MILLER -> new Miller(player, map);
            case BAKER -> new Baker(player, map);
            case STOREHOUSE_WORKER -> new StorehouseWorker(player, map);
            case FISHERMAN -> new Fisherman(player, map);
            case MINER -> new Miner(player, map);
            case IRON_FOUNDER -> new IronFounder(player, map);
            case BREWER -> new Brewer(player, map);
            case MINTER -> new Minter(player, map);
            case ARMORER -> new Armorer(player, map);
            case PIG_BREEDER -> new PigBreeder(player, map);
            case BUTCHER -> new Butcher(player, map);
            case GEOLOGIST -> new Geologist(player, map);
            case DONKEY_BREEDER -> new DonkeyBreeder(player, map);
            case SCOUT -> new Scout(player, map);
            case CATAPULT_WORKER -> new CatapultWorker(player, map);
            case HUNTER -> new Hunter(player, map);
            case METALWORKER -> new Metalworker(player, map);
            case DONKEY -> new Donkey(player, map);
            case COURIER -> new Courier(player, map);
            case PRIVATE -> new Soldier(player, PRIVATE_RANK, map);
            case PRIVATE_FIRST_CLASS -> new Soldier(player, PRIVATE_FIRST_CLASS_RANK, map);
            case SERGEANT -> new Soldier(player, SERGEANT_RANK, map);
            case OFFICER -> new Soldier(player, OFFICER_RANK, map);
            case GENERAL -> new Soldier(player, GENERAL_RANK, map);
            case BUILDER -> new Builder(player, map);
            case SHIPWRIGHT -> new Shipwright(player, map);
            default -> throw new InvalidGameLogicException(
                    String.format("Can't retrieve worker of type %s", material));
        };
    }
}
