package org.appland.settlers.computer;

import org.appland.settlers.computer.util.GamePlay;
import org.appland.settlers.computer.util.PathFinding;
import org.appland.settlers.computer.util.Placement;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.computer.util.GamePlay.*;
import static org.appland.settlers.computer.util.Placement.*;

/**
 * Manages construction preparation tasks for the player, including setting up woodcutters, sawmills, and quarries.
 *
 * It should work like this:
 * 1. Are there trees within the player's land?
 *    1.1: Yes
 *       1.1.1: place woodcutter
 *       1.1.2: place sawmill close to the woodcutter and connect them
 *    1.2: No
 *       1.2.1: place forester
 *       1.2.2: place woodcutter close to the forester
 *       1.2.3: place sawmill close to the woddcutter and connect them
 *       1.2.4: when construction of the forester is done - remove the road to it
 * 2. Is there any stone within the player's land?
 *    2.1: Yes
 *       2.1.1: place quarry and connect it to the headquarters
 *    2.2: No
 *       2.1.2: signal that no stone is available so land expansion can start
 * 3. Has the woodcutter run out of trees AND is there no forester close to it?
 *    2.1: Yes
 *       2.1.1: place forester close to the woodcutter
 *       2.1.2: when construction of the forester is done - remove the road to it
 * 4. Has new land been acquired where stone is available AND there is no existing quarry?
 *    4.1: Yes
 *       4.1.2: Build a quarry
 *    4.2: No
 *       4.2.2: Signal to continue land expansions
 *
 * Perform continuous maintenance
 *  - When the quarry runs out of stone - remove the existing quarry and place a new quarry or start land expansion
 *  - Has any road been broken? Repair the connection to the headquarters
 *  - Has any building been destroyed (e.g. by war)? Place a new building
 */
public class ConstructionPreparationPlayer extends BasePlayer {
    private static final int PERIODIC_STONES_CHECK = 100;
    private static final int STONE_RECHECK_COUNTER_MAX = 10000;

    private ForesterHut foresterHut;
    private Woodcutter woodcutter0;
    private Headquarter headquarter;
    private Sawmill sawmill;
    private Quarry quarry;
    private int stoneRecheckCounter = 0;
    private boolean hasStonesOnLand = true;

    /**
     * Constructs a ConstructionPreparationPlayer to manage the building process for the specified player and game map.
     *
     * @param player The player for whom the buildings are managed.
     * @param map    The game map.
     */
    public ConstructionPreparationPlayer(Player player, GameMap map) {
        super(map, player);
    }

    /**
     * Executes one turn of actions for constructing necessary buildings and maintaining resources.
     *
     * @throws Exception If an error occurs during the turn.
     */
    @Override
    public void turn() throws Exception {

        // Find the headquarters if needed
        headquarter = headquarter != null ? headquarter : GamePlay.findHeadquarter(player);

        // Handle relevant game messages
        for (var message : player.getMessages()) {
            if (message instanceof NoMoreResourcesMessage noMoreResourcesMessage && noMoreResourcesMessage.building().equals(quarry)) {
                System.out.printf("[%s]  - No more resources for quarry. Tear down.%n", player);
                quarry.tearDown();
                removeRoadWithoutAffectingOthers(map, quarry.getFlag());
                quarry = null;
                player.markMessageAsRead(noMoreResourcesMessage);
            }
        }

        // Detect if a building has been destroyed
        woodcutter0 = validate(woodcutter0);
        foresterHut = validate(foresterHut);
        sawmill = validate(sawmill);

        // Re-connect buildings if a road has been destroyed
        // TODO: ...

        // If there's no woodcutter -- try to place one next to a forest
        if (woodcutter0 == null) {
            var pointForWoodcutter = findBestPointForBuilding(
                    Woodcutter.class,
                    player,
                    Set.of(new Placement.PlacementHeuristic(100, point -> countTreesNearby(point, map, 6))),
                    Set.of(p -> canConnectPredicate(p.downRight(), List.of(p), headquarter))
            );

            if (pointForWoodcutter != null) {
                woodcutter0 = map.placeBuilding(new Woodcutter(player), pointForWoodcutter);
                connectToBuildingByRoad(woodcutter0.getFlag().getPosition(), headquarter, player, 0.5);
                System.out.printf("[%s] - Built woodcutter at %s%n", player, woodcutter0.getPosition());
            } else {
                System.out.printf("[%s] - Failed to find point for woodcutter%n", player);
            }

            return;
        }

        // Construct a forester
        if (foresterHut == null && (woodcutter0 == null || !treesCloseToBuilding(woodcutter0))) {
            var anchor = findAnchorFor(ForesterHut.class, player);
            var point = findBestPointForBuildingCloseToPoint(
                    ForesterHut.class,
                    anchor.getPosition(),
                    6,
                    player,
                    Set.of(),
                    Set.of(p -> canConnectPredicate(p.downRight(), List.of(p), headquarter))
            );

            if (point != null) {
                foresterHut = map.placeBuilding(new ForesterHut(player), point);
                connectToBuildingByRoad(foresterHut.getFlag().getPosition(), headquarter, player, 0.5);

                System.out.printf("[%s]    + Done with forester hut%n", player);
            } else {
                System.out.printf("[%s] - Failed to find point for forester hut%n", player);
            }
        } else if (foresterHut != null && foresterHut.isOccupied() && map.getRoadsFromFlag(foresterHut.getFlag()).size() == 2) {
            System.out.printf("[%s] - Forester hut is ready and still connected. Removing road.%n", player);
            GamePlay.removeRoadWithoutAffectingOthers(map, foresterHut.getFlag());
        } else if (sawmill == null) {
            var anchor = findAnchorFor(Sawmill.class, player);
            var point = findBestPointForBuildingCloseToPoint(
                    Sawmill.class,
                    anchor.getPosition(),
                    12,
                    player,
                    Set.of(
                            new PlacementHeuristic(100, p -> Integer.MAX_VALUE - GameUtils.distanceInGameSteps(p, anchor.getPosition()))
                    ),
                    Set.of(p -> canConnectPredicate(p.downRight(), List.of(p), anchor, headquarter)));

            if (point != null) {
                sawmill = map.placeBuilding(new Sawmill(player), point);
                connectToBuildingByRoad(sawmill.getFlag().getPosition(), anchor, player, 0.5);

                System.out.printf("[%s]   + Done with sawmill%n", player);
            } else {
                System.out.printf("[%s] - Failed to find point for sawmill%n", player);
            }
        } else if (quarry == null) {
            var pointForQuarry = findBestPointForBuilding(
                    Quarry.class,
                    player,
                    Set.of(
                            new Placement.PlacementHeuristic(100, point -> countStonesNearby(point, map, 6))
                    ),
                    Set.of(p -> canConnectPredicate(p.downRight(), List.of(p), headquarter))
            );

            if (pointForQuarry != null) {
                quarry = map.placeBuilding(new Quarry(player), pointForQuarry);

                connectToBuildingByRoad(quarry.getFlag().getPosition(), headquarter, player, 0.5);
                System.out.printf("[%s]    + Done with quarry%n", player);
            } else {
                System.out.printf("[%s] - Failed to find point for quarry%n", player);
            }
        }
    }

    public static boolean canConnectPredicate(Point point, Collection<Point> avoid, Building... buildings) {
        return Arrays.stream(buildings).anyMatch(building -> canConnectTo(point, avoid, building, false));
    }

    public boolean plankProductionWorking() {
        return woodcutter0 != null && woodcutter0.isReady() && treesCloseToBuilding(woodcutter0) && sawmill != null && sawmill.isReady();
    }

    public boolean treesCloseToBuilding(Building building) {
        var map = building.getMap();

        return GameUtils.getHexagonAreaAroundPoint(building.getPosition(), 8, map).stream()
                .anyMatch(p -> map.isTreeAtPoint(p) && !map.getTreeAtPoint(p).isFalling());
    }

    public boolean stoneProductionWorking() {

        // Periodically check if there are remaining stones
        if (stoneRecheckCounter < STONE_RECHECK_COUNTER_MAX) {
            stoneRecheckCounter++;
        } else {
            stoneRecheckCounter = 0;
        }

        if (stoneRecheckCounter % PERIODIC_STONES_CHECK == 0) {
            hasStonesOnLand = GamePlay.hasStoneWithinArea(map, player);
        }

        return quarry != null && quarry.isReady() && !quarry.isOutOfNaturalResources();
    }

    public boolean hasAccessToStone() {
        return GamePlay.hasStoneWithinArea(map, player);
    }

    public static boolean canConnectTo(Point start, Collection<Point> avoid, Building placement, boolean debug) {
        try {
            return PathFinding.canConnectTo(
                    start,
                    placement.getPlayer(),
                    placement.getPlayer().getMap(),
                    placement.getPosition(),
                    avoid
            );
        } catch (InvalidUserActionException e) {
            System.out.printf("Can't connect to %s%n", placement.getPlayer().getName());
            System.out.println(e.getMessage());
            return false;
        }
    }
}
