package org.appland.settlers.computer;

import org.appland.settlers.computer.util.Placement;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.IronMine;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.computer.util.GamePlay.connectToBuildingByRoad;
import static org.appland.settlers.computer.util.Placement.findBestPointForFlag;
import static org.appland.settlers.model.Material.*;

/**
 *
 * @author johan
 */
public class SearchForMineralsPlayer extends BasePlayer implements PlayerGameViewMonitor {
    private static final int RANGE_BETWEEN_FLAG_AND_POINT = 5;
    private static final int GEOLOGIST_WAIT_TIMEOUT = 200;

    private final Set<Point>             concludedPoints     = new HashSet<>();
    private final Set<Point>             pointsToInvestigate = new HashSet<>();
    private final Map<Point, Material>   foundMinerals       = new HashMap<>();
    private final Map<Material, Integer> activeMines         = new EnumMap<>(Material.class);
    private final Countdown              countdown           = new Countdown();
    private final Set<Point>             unreachablePoints   = new HashSet<>();
    private final EventTrigger           eventTrigger;

    private State      state = State.INITIALIZING;
    private Building   headquarter;
    private Flag       geologistFlag = null;
    private Geologist  calledGeologist;
    private Set<Point> ownedLand = new HashSet<>();

    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {

        // Track newly found points to investigate
        if (!gameChangesList.changedBorders().isEmpty()) {
            player.getOwnedLand().stream()
                    .filter(point -> !ownedLand.contains(point))
                    .filter(point -> map.isOnMineableMountain(point))
                    .forEach(pointsToInvestigate::add);

            // Track the player's owned land
            ownedLand.clear();
            ownedLand.addAll(player.getOwnedLand());
        }
    }

    private enum State {
        INITIALIZING,
        LOOKING_FOR_MINERALS,
        LOOKING_FOR_GEOLOGIST,
        WAITING_FOR_GEOLOGY_RESULTS,
        ALL_CURRENTLY_CONCLUDED
    }

    public SearchForMineralsPlayer(Player player, GameMap map, EventTrigger eventTrigger) {
        super(map, player);

        this.eventTrigger = eventTrigger;

        activeMines.put(GOLD, 0);
        activeMines.put(IRON, 0);
        activeMines.put(COAL, 0);
        activeMines.put(STONE, 0);
    }

    @Override
    public void turn() throws Exception {
        switch (state) {
            case INITIALIZING -> {

                // Ensure we have access to the map
                map = map != null ? map : player.getMap();

                if (map == null) {
                    return;
                }

                // Find the headquarters
                headquarter = map.getBuildings().stream()
                        .filter(building -> building instanceof Headquarter)
                        .filter(building -> Objects.equals(building.getPlayer(), this.player))
                        .findFirst()
                        .orElse(null);

                if (headquarter != null) {
                    state = State.LOOKING_FOR_MINERALS;
                }

                // Track the player's owned land
                ownedLand.addAll(player.getOwnedLand());

                pointsToInvestigate.addAll(ownedLand.stream()
                        .filter(point -> map.isOnMineableMountain(point))
                        .filter(this::isAvailableForSign)
                        .toList());

                // Listen to changes to the player
                player.monitorGameView(this);
            }

            case LOOKING_FOR_MINERALS -> {

                // Update points to investigate
                var noLongerValid = pointsToInvestigate.stream()
                        .filter(point -> !isAvailableForSign(point))
                        .toList();

                noLongerValid.forEach(pointsToInvestigate::remove);

                if (pointsToInvestigate.isEmpty()) {
                    System.out.println(" - Has investigated all available spots");

                    state = State.ALL_CURRENTLY_CONCLUDED;
                } else {

                    // Send out geologists if needed and possible
                    for (var p : pointsToInvestigate) {

                        // Skip un-reachable points
                        if (unreachablePoints.contains(p)) {
                            continue;
                        }

                        // Skip points where no sign can be placed
                        if (!isAvailableForSign(p)) {
                            continue;
                        }

                        // Look for a suitable flag close to the point
                        var flag = findFlagCloseBy(p);

                        if (flag == null) {
                            var flagPoint = findBestPointForFlag(
                                    player,

                                    // Preferred
                                    Set.of(
                                            new Placement.PlacementHeuristic(100, point ->
                                            Integer.MAX_VALUE - GameUtils.distanceInGameSteps(point, p))),

                                    // Required
                                    Set.of()
                            );

                            if (flagPoint != null) {
                                flag = map.placeFlag(player, flagPoint);

                                connectToBuildingByRoad(flagPoint, headquarter, player, 0.5);
                            } else {
                                unreachablePoints.add(p);
                            }
                        }

                        if (flag != null) {
                            state = State.LOOKING_FOR_GEOLOGIST;

                            geologistFlag = flag;

                            // Call two geologist to speed up search
                            flag.callGeologist();
                            flag.callGeologist();

                            // Set a countdown for how long to wait for the geologist
                            countdown.countFrom(GEOLOGIST_WAIT_TIMEOUT);

                            break;
                        }
                    }
                }
            }

            case LOOKING_FOR_GEOLOGIST -> {
                for (var w : map.getWorkers()) {
                    if (! (w instanceof Geologist)) {
                        continue;
                    }

                    if (w.getTarget().equals(geologistFlag.getPosition())) {
                        calledGeologist = (Geologist)w;

                        state = State.WAITING_FOR_GEOLOGY_RESULTS;

                        break;
                    }
                }

                if (countdown.hasReachedZero()) {

                    // Give up on waiting for the geologist if the timeout expired
                    state = State.LOOKING_FOR_MINERALS;
                } else {
                    countdown.step();
                }
            }

            case WAITING_FOR_GEOLOGY_RESULTS -> {
                var newlyInvestigatedPoints = new LinkedList<Point>();

                // Find any new results
                for (var point : pointsToInvestigate) {
                    if (!map.isSignAtPoint(point)) {
                        continue;
                    }

                    var sign = map.getSignAtPoint(point);

                    if (sign.getType() == null) {
                        continue;
                    }

                    if (sign.getType() == GOLD) {
                        eventTrigger.report(GamePlayEvent.FOUND_GOLD, player);
                    }

                    foundMinerals.put(point, sign.getType());

                    newlyInvestigatedPoints.add(point);

                    if (buildMineIfPossible(point, sign.getType())) {

                        // Remove the flag as well from the list of points to investigate
                        newlyInvestigatedPoints.add(point.downRight());
                    }
                }

                concludedPoints.addAll(newlyInvestigatedPoints);

                newlyInvestigatedPoints.forEach(pointsToInvestigate::remove);

                if (calledGeologist.getTarget().equals(headquarter.getPosition())) {
                    state = State.LOOKING_FOR_MINERALS;
                }
            }
        }
    }

    private Flag findFlagCloseBy(Point point) {
        for (var p : map.getPointsWithinRadius(point, RANGE_BETWEEN_FLAG_AND_POINT)) {
            if (!map.isFlagAtPoint(p)) {
                continue;
            }

            if (!map.arePointsConnectedByRoads(p, headquarter.getFlag().getPosition())) {
                continue;
            }

            return map.getFlagAtPoint(p);
        }

        return null;
    }

    private boolean buildMineIfPossible(Point point, Material type) throws Exception {
        if (map.isAvailableMinePoint(player, point)) {
            switch (type) {
                case GOLD -> map.placeBuilding(new GoldMine(player), point);
                case IRON -> map.placeBuilding(new IronMine(player), point);
                case COAL -> map.placeBuilding(new CoalMine(player), point);
                case STONE -> map.placeBuilding(new GraniteMine(player), point);
                default -> throw new Exception("Cannot create mine to get %s".formatted(type));
            };

            if (activeMines.get(type) == 0) {
                connectToBuildingByRoad(point.downRight(), headquarter, player, 0.5);

                activeMines.put(type, 1);
            }

            return true;
        }

        return false;
    }

    boolean allCurrentMineralsKnown() {
        return state == State.ALL_CURRENTLY_CONCLUDED;
    }

    boolean hasCoalMine() {
        return activeMines.getOrDefault(COAL, 0) > 0;
    }

    boolean hasIronMine() {
        return activeMines.getOrDefault(IRON, 0) > 0;
    }

    boolean hasGoldMine() {
        return activeMines.getOrDefault(GOLD, 0) > 0;
    }

    private boolean hasGraniteMine() {
        return activeMines.getOrDefault(STONE, 0) > 0;
    }

    boolean hasMines() {
        return hasCoalMine() || hasIronMine() || hasGoldMine() || hasGraniteMine();
    }

    private boolean isAvailableForSign(Point point) {
        return map.isOnMineableMountain(point)
                && !map.isBuildingAtPoint(point)
                && !map.isCropAtPoint(point)
                && !map.isFlagAtPoint(point)
                && !map.isSignAtPoint(point)
                && !map.isStoneAtPoint(point)
                && !map.isTreeAtPoint(point)
                && !map.isRoadAtPoint(point);
    }

    void scanForNewMinerals() {
        if (state != State.INITIALIZING) {
            state = State.LOOKING_FOR_MINERALS;
        }
    }
}
