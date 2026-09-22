package org.appland.settlers.computer.util;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.LookoutTower;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.WatchTower;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import static org.appland.settlers.computer.util.GamePlay.findHeadquarter;
import static org.appland.settlers.model.GameUtils.distanceInGameSteps;

public class Placement {
    private static final Set<Class<? extends Building>> SMALL_BUILDINGS = Set.of(
            Woodcutter.class,
            ForesterHut.class,
            Quarry.class,
            Fishery.class,
            HunterHut.class,
            Well.class,
            Barracks.class,
            GuardHouse.class,
            LookoutTower.class
    );

    private static final Set<Class<? extends Building>> MEDIUM_BUILDINGS = Set.of(
            Sawmill.class,
            SlaughterHouse.class,
            Mint.class,
            Mill.class,
            Metalworks.class,
            Armory.class,
            WatchTower.class,
            Catapult.class,
            Bakery.class,
            IronSmelter.class
    );

    private static final Set<Class<? extends Building>> MINES = Set.of(
            CoalMine.class,
            IronMine.class,
            GoldMine.class,
            GraniteMine.class
    );

    public static Collection<Road> placeRemainingRoads(List<Point> path, Player player) throws InvalidUserActionException {
        // FIXME: handle the case where new roads go "over" an existing road by placing a new flag on it and continuing from there

        if (path.isEmpty()) {
            return List.of();
        }

        var points = new ArrayList<>(path);
        var map = player.getMap();
        var pointsInRoad = new ArrayList<Point>();
        var previous = (Point) null;
        var roadsToPlace = new ArrayList<List<Point>>();
        var newlyPlacedRoads = new ArrayList<Road>();

        // If an endpoint is a building it already has a road and a flag so just go to the flag
        if (map.isBuildingAtPoint(points.getFirst())) {
            points.removeFirst();
        }

        if (map.isBuildingAtPoint(points.getLast())) {
            points.removeLast();
        }

        // Collect all roads to place
        for (var point : points) {
            if (previous != null) {
                if (distanceInGameSteps(previous, point) != 1) {
                    if (pointsInRoad.size() > 2) {
                        roadsToPlace.add(pointsInRoad);
                        pointsInRoad = new ArrayList<>();
                    } else {
                        pointsInRoad = new ArrayList<>();
                    }
                } else if ((map.isRoadAtPoint(point) && map.isAvailableFlagPoint(player, point)) || map.isFlagAtPoint(point)) {
                    pointsInRoad.add(point);
                    roadsToPlace.add(pointsInRoad);
                    pointsInRoad = new ArrayList<>();
                }
            }

            pointsInRoad.add(point);
            previous = point;
        }

        if (pointsInRoad.size() > 2) {
            roadsToPlace.add(pointsInRoad);
        }

        // Place roads
        roadsToPlace.forEach(System.out::println);

        for (var roadPoints : roadsToPlace) {
            if (!map.isFlagAtPoint(roadPoints.getFirst())) {
                map.placeFlag(player, roadPoints.getFirst());
            }

            if (!map.isFlagAtPoint(roadPoints.getLast())) {
                map.placeFlag(player, roadPoints.getLast());
            }

            newlyPlacedRoads.add(
                player.getMap().placeRoad(player, roadPoints)
            );
        }

        return newlyPlacedRoads;
    }

    public record PlacementHeuristic(
            int weight,
            ToIntFunction<Point> scoreFunction) {
    }

    private static final Map<Class<? extends Building>, Class<? extends Building>> ANCHORS = Map.of(
            Bakery.class, Mill.class,
            Mill.class, Farm.class,
            Brewery.class, Farm.class,
            Mint.class, Armory.class,
            IronSmelter.class, IronMine.class,
            Metalworks.class, Sawmill.class,
            Armory.class, IronSmelter.class,
            Woodcutter.class, ForesterHut.class,
            Sawmill.class, Woodcutter.class,
            ForesterHut.class, Woodcutter.class
    );

    public static Building findAnchorFor(Class<? extends Building> buildingType, Player player) {
        var anchorType = ANCHORS.get(buildingType);

        if (anchorType == null) {
            return findHeadquarter(player);
        }

        return player.getMap().getBuildings().stream()
                .filter(b -> Objects.equals(b.getPlayer(), player))
                .filter(b -> Objects.equals(anchorType, b.getClass()))
                .filter(b -> !b.isBurningDown() && !b.isDestroyed())
                .findFirst()
                .orElse(findHeadquarter(player));
    }

    public static Point findBestPointForBuildingCloseToPoint(
            Class<? extends Building> buildingType,
            Point anchor,
            int searchRadius,
            Player player,
            Collection<PlacementHeuristic> heuristics,
            Collection<Predicate<Point>> required) {
        var map = player.getMap();
        Point bestPoint = null;
        int bestScore = Integer.MIN_VALUE;

        for (var point : map.getPointsWithinRadius(anchor, searchRadius)) {
            if (!canPlaceBuilding(buildingType, point, player)) {
                continue;
            }

            int score = 0;

            if (required.stream().anyMatch(p -> !p.test(point))) {
                continue;
            }

            for (var heuristic : heuristics) {
                score += heuristic.weight() * heuristic.scoreFunction().applyAsInt(point);
            }

            if (bestPoint == null || score > bestScore) {
                bestPoint = point;
                bestScore = score;
            }
        }

        return bestPoint;
    }

    public static int countStonesNearby(Point point, GameMap map, int radius) {
        return (int) GameUtils.getHexagonAreaAroundPoint(point, radius, map).stream()
                .filter(map::isStoneAtPoint)
                .count();
    }

    static Size getBuildingSize(Class<? extends Building> buildingType) {
        if (SMALL_BUILDINGS.contains(buildingType)) {
            return Size.SMALL;
        } else if (MEDIUM_BUILDINGS.contains(buildingType)) {
            return Size.MEDIUM;
        } else {
            return Size.LARGE;
        }
    }

    static boolean isMine(Class<? extends Building> buildingType) {
        return MINES.contains(buildingType);
    }

    static boolean canPlaceBuilding(Class<? extends Building> buildingClass, Point point, Player player) {
        var map = player.getMap();
        var buildingSize = getBuildingSize(buildingClass);
        var availableSize = map.isAvailableHousePoint(player, point);

        if (isMine(buildingClass)) {
            return map.isAvailableMinePoint(player, point);
        }

        if (Objects.equals(buildingClass, Harbor.class)) {
            return map.isAvailableHarborPoint(point) && player.getOwnedLand().contains(point);
        }

        if (availableSize == null) {
            return false;
        } else if (availableSize == Size.LARGE) {
            return true;
        } else if (availableSize == Size.MEDIUM && buildingSize != Size.LARGE) {
            return true;
        } else if (availableSize == Size.SMALL && buildingSize == Size.SMALL) {
            return true;
        }

        return false;
    }


    public static Point findBestPointForFlag(
            Player player,
            Collection<PlacementHeuristic> preferred,
            Collection<Predicate<Point>> required) {
        var map = player.getMap();
        Point bestPoint = null;
        int bestScore = Integer.MIN_VALUE;

        for (var point : player.getOwnedLand()) {
            if (!map.isAvailableFlagPoint(player, point)) {
                continue;
            }

            if (required.stream().anyMatch(r -> !r.test(point))) {
                continue;
            }

            int score = 0;

            for (var heuristic : preferred) {
                score += heuristic.weight() * heuristic.scoreFunction().applyAsInt(point);
            }

            if (bestPoint == null || score > bestScore) {
                bestPoint = point;
                bestScore = score;
            }
        }

        return bestPoint;
    }

    public static Point findBestPointForBuilding(
            Class<? extends Building> buildingType,
            Player player,
            Collection<PlacementHeuristic> preferred,
            Collection<Predicate<Point>> required) {
        var map = player.getMap();
        Point bestPoint = null;
        int bestScore = Integer.MIN_VALUE;

        for (var point : player.getOwnedLand()) {
            if (!canPlaceBuilding(buildingType, point, player)) {
                continue;
            }

            if (required.stream().anyMatch(r -> !r.test(point))) {
                continue;
            }

            int score = 0;

            for (var heuristic : preferred) {
                score += heuristic.weight() * heuristic.scoreFunction().applyAsInt(point);
            }

            if (bestPoint == null || score > bestScore) {
                bestPoint = point;
                bestScore = score;
            }
        }

        return bestPoint;
    }
}
