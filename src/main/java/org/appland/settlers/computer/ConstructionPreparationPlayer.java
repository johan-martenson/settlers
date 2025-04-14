/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;

import java.util.List;

import static org.appland.settlers.model.Size.SMALL;

/**
 * Manages construction preparation tasks for the player, including setting up woodcutters, sawmills, and quarries.
 */
public class ConstructionPreparationPlayer implements ComputerPlayer {
    private static final int PERIODIC_STONES_CHECK = 100;
    private static final int STONE_RECHECK_COUNTER_MAX = 10000;

    private final Player player;

    private ForesterHut foresterHut;
    private Woodcutter woodcutter0;
    private Woodcutter woodcutter1;
    private Headquarter headquarter;
    private Sawmill sawmill;
    private Quarry quarry;
    private int stoneRecheckCounter = 0;
    private GameMap map;
    private boolean hasStonesOnLand = true;

    /**
     * Constructs a ConstructionPreparationPlayer to manage the building process for the specified player and game map.
     *
     * @param player The player for whom the buildings are managed.
     * @param map    The game map.
     */
    public ConstructionPreparationPlayer(Player player, GameMap map) {
        this.player = player;
        this.map = map;
    }

    /**
     * Executes one turn of actions for constructing necessary buildings and maintaining resources.
     *
     * @throws Exception If an error occurs during the turn.
     */
    @Override
    public void turn() throws Exception {

        /* Find the headquarters if needed */
        if (headquarter == null) {
            headquarter = GamePlayUtils.findHeadquarter(player);
        }

        /* Construct a forester */
        if (noForester()) {

            /* Place a forester hut */
            foresterHut = GamePlayUtils.placeBuilding(player, headquarter, new ForesterHut(player));
            System.out.printf(" - Built forester at %s%n", foresterHut.getPosition());
        } else if (woodCuttersNotPlaced()) {

            /* Place the woodcutter */
            if (!GamePlayUtils.buildingInPlace(woodcutter0)) {
                woodcutter0 = GamePlayUtils.placeBuilding(player, foresterHut, new Woodcutter(player));

                System.out.printf(" - Built woodcutter at %s%n", woodcutter0.getPosition());
            }

            /* Place the woodcutter */
            if (!GamePlayUtils.buildingInPlace(woodcutter1)) {
                woodcutter1 = GamePlayUtils.placeBuilding(player, foresterHut, new Woodcutter(player));

                System.out.printf(" - Built woodcutter at %s%n", woodcutter1.getPosition());
            }

        } else if (noSawmill()) {

            /* Place the sawmill */
            sawmill = GamePlayUtils.placeBuilding(player, headquarter, new Sawmill(player));
            System.out.println(" - Built sawmill at " + sawmill.getPosition());
        } else if (noQuarry()) {

            /* Look for stone within the border */
            Point stonePoint = findStoneWithinBorder();

            /* Write a warning and exit if no stone is found */
            if (stonePoint == null) {
                System.out.println("WARNING: No stone found within border");

                return;
            }

            /* Find spot close to stone to place quarry */
            List<Point> points = GamePlayUtils.findAvailableHousePointsWithinRadius(map, player, stonePoint, SMALL, 5);

            /* Return null if there are no available places */
            if (points.isEmpty()) {
                System.out.println(" -- No site available to build quarry!");
                return;
            }

            /* Place the quarry */
            quarry = map.placeBuilding(new Quarry(player), points.getFirst());
            System.out.println(" - Built quarry at " + quarry.getPosition());

            /* Connect the quarry to the headquarters */
            Road road = GamePlayUtils.connectPointToBuilding(player, map, quarry.getFlag().getPosition(), headquarter);

            /* Place flags on the road where possible */
            if (road != null) {
                System.out.println(" - Connected the quarry: " + road.getWayPoints());
                GamePlayUtils.fillRoadWithFlags(map, road);
            }
        } else if (quarryDone() && quarry.isOutOfNaturalResources()) {
            System.out.println(" - No more stone in quarry");
            /* Destroy the quarry if it can't reach any stone */
            quarry.tearDown();

            /* Remove the part of the road that is used only by the quarry */
            GamePlayUtils.removeRoadWithoutAffectingOthers(map, quarry.getFlag());

            quarry = null;
        }
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public Player getControlledPlayer() {
        return player;
    }

    private Point findStoneWithinBorder() {
        for (Point point : player.getOwnedLand()) {
            if (map.isStoneAtPoint(point)) {
                return point;
            }
        }

        return null;
    }
    boolean basicConstructionDone() {

        /* Periodically check if there are remaining stones */
        if (stoneRecheckCounter < STONE_RECHECK_COUNTER_MAX) {
            stoneRecheckCounter++;
        } else {
            stoneRecheckCounter = 0;
        }

        if (stoneRecheckCounter % PERIODIC_STONES_CHECK == 0) {
            hasStonesOnLand = GamePlayUtils.hasStoneWithinArea(map, player);
        }

        return foresterDone()    &&
               woodcuttersDone() &&
               sawmillDone()     &&
                ((quarryDone() && !quarry.isOutOfNaturalResources()) ||
                 (noQuarry() && !hasStonesOnLand));
    }

    private boolean foresterDone() {
        return GamePlayUtils.buildingDone(foresterHut);
    }

    private boolean noForester() {
        return !GamePlayUtils.buildingInPlace(foresterHut);
    }

    private boolean woodCuttersNotPlaced() {
        return !GamePlayUtils.buildingInPlace(woodcutter0) ||
               !GamePlayUtils.buildingInPlace(woodcutter1);
    }

    private boolean woodcuttersDone() {
        return GamePlayUtils.buildingDone(woodcutter0) &&
               GamePlayUtils.buildingDone(woodcutter1);
    }

    private boolean noSawmill() {
        return !GamePlayUtils.buildingInPlace(sawmill);
    }

    private boolean sawmillDone() {
        return GamePlayUtils.buildingDone(sawmill);
    }

    private boolean noQuarry() {
        return !GamePlayUtils.buildingInPlace(quarry);
    }

    private boolean quarryDone() {
        return GamePlayUtils.buildingDone(quarry);
    }

    public boolean plankProductionWorking() {
        return foresterDone() && woodcuttersDone() && sawmillDone();
    }

    public boolean stoneProductionWorking() {

        /* Periodically check if there are remaining stones */
        if (stoneRecheckCounter < STONE_RECHECK_COUNTER_MAX) {
            stoneRecheckCounter++;
        } else {
            stoneRecheckCounter = 0;
        }

        if (stoneRecheckCounter % PERIODIC_STONES_CHECK == 0) {
            hasStonesOnLand = GamePlayUtils.hasStoneWithinArea(map, player);
        }

        return quarryDone() && !quarry.isOutOfNaturalResources();
    }

    public boolean hasAccessToStone() {
        return GamePlayUtils.hasStoneWithinArea(map, player);
    }
}
