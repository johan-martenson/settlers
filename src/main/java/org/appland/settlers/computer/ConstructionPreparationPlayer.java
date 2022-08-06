/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;

import java.util.List;

import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
public class ConstructionPreparationPlayer implements ComputerPlayer {
    private static final int PERIODIC_STONES_CHECK = 100;
    private static final int STONE_RECHECK_COUNTER_MAX = 10000;

    private ForesterHut foresterHut;
    private Woodcutter  woodcutter0;
    private Woodcutter  woodcutter1;
    private Headquarter headquarter;
    private Sawmill     sawmill;
    private Quarry      quarry;
    private int         stoneRecheckCounter;
    private GameMap     map;
    private boolean     hasStonesOnLand;

    private final Player    player;


    public ConstructionPreparationPlayer(Player p, GameMap m) {
        player = p;
        map    = m;

        stoneRecheckCounter = 0;
        hasStonesOnLand = true;
    }

    @Override
    public void turn() throws Exception {

        /* Find the headquarter if needed */
        if (headquarter == null) {
            headquarter = Utils.findHeadquarter(player);
        }

        /* Construct a forester */
        if (noForester()) {

            /* Place a forester hut */
            foresterHut = Utils.placeBuilding(player, headquarter, new ForesterHut(player));
            System.out.println(" - Built forester at " + foresterHut.getPosition());
        } else if (woodCuttersNotPlaced()) {

            /* Place the woodcutter */
            if (!Utils.buildingInPlace(woodcutter0)) {
                woodcutter0 = Utils.placeBuilding(player, foresterHut, new Woodcutter(player));

                System.out.println(" - Built woodcutter at " + woodcutter0.getPosition());
            }

            /* Place the woodcutter */
            if (!Utils.buildingInPlace(woodcutter1)) {
                woodcutter1 = Utils.placeBuilding(player, foresterHut, new Woodcutter(player));

                System.out.println(" - Built woodcutter at " + woodcutter1.getPosition());
            }

        } else if (noSawmill()) {

            /* Place the sawmill */
            sawmill = Utils.placeBuilding(player, headquarter, new Sawmill(player));
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
            List<Point> points = Utils.findAvailableHousePointsWithinRadius(map, player, stonePoint, SMALL, 5);

            /* Return null if there are no available places */
            if (points.isEmpty()) {
                System.out.println(" -- No site available to build quarry!");
                return;
            }

            /* Place the quarry */
            quarry = map.placeBuilding(new Quarry(player), points.get(0));
            System.out.println(" - Built quarry at " + quarry.getPosition());

            /* Connect the quarry to the headquarter */
            Road road = Utils.connectPointToBuilding(player, map, quarry.getFlag().getPosition(), headquarter);

            /* Place flags on the road where possible */
            if (road != null) {
                System.out.println(" - Connected the quarry: " + road.getWayPoints());
                Utils.fillRoadWithFlags(map, road);
            }
        } else if (quarryDone() && quarry.isOutOfNaturalResources()) {
            System.out.println(" - No more stone in quarry");
            /* Destroy the quarry if it can't reach any stone */
            quarry.tearDown();

            /* Remove the part of the road that is used only by the quarry */
            Utils.removeRoadWithoutAffectingOthers(map, quarry.getFlag());

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
        for (Point point : player.getLandInPoints()) {
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
            hasStonesOnLand = Utils.hasStoneWithinArea(map, player);
        }

        return foresterDone()    &&
               woodcuttersDone() &&
               sawmillDone()     &&
                ((quarryDone() && !quarry.isOutOfNaturalResources()) ||
                 (noQuarry() && !hasStonesOnLand));
    }

    private boolean foresterDone() {
        return Utils.buildingDone(foresterHut);
    }

    private boolean noForester() {
        return !Utils.buildingInPlace(foresterHut);
    }

    private boolean woodCuttersNotPlaced() {
        return !Utils.buildingInPlace(woodcutter0) ||
               !Utils.buildingInPlace(woodcutter1);
    }

    private boolean woodcuttersDone() {
        return Utils.buildingDone(woodcutter0) &&
               Utils.buildingDone(woodcutter1);
    }

    private boolean noSawmill() {
        return !Utils.buildingInPlace(sawmill);
    }

    private boolean sawmillDone() {
        return Utils.buildingDone(sawmill);
    }

    private boolean noQuarry() {
        return !Utils.buildingInPlace(quarry);
    }

    private boolean quarryDone() {
        return Utils.buildingDone(quarry);
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
            hasStonesOnLand = Utils.hasStoneWithinArea(map, player);
        }

        return quarryDone() && !quarry.isOutOfNaturalResources();
    }

    public boolean hasAccessToStone() {
        return Utils.hasStoneWithinArea(map, player);
    }
}
