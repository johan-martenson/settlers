/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestCargo {

    @Test
    public void testNextStepIsNullForCargoWithoutTarget() {
        Cargo cargo = new Cargo(WOOD, null);
        assertNull(cargo.getNextFlagOrBuilding());
    }

    @Test
    public void testGetNextIsValidDirectlyAfterSetTarget() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create the game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place the headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a woodcutter */
        Point point1 = new Point(8, 6);
        Building woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place a flag */
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place a road from the flag to the woodcutter's flag */
        Point point3 = new Point(8, 4);
        Road road0 = map.placeRoad(player0, point2, point3, woodcutter.getFlag().getPosition());

        /* Create a cargo and put on the flag */
        Cargo cargo = new Cargo(PLANK, map);

        flag0.putCargo(cargo);

        /* Set the woodcutter as target */
        cargo.setTarget(woodcutter);

        /* Verify that the cargo will go via the woodcutter's flag */
        assertEquals(cargo.getNextFlagOrBuilding(), woodcutter.getFlag().getPosition());
    }

    @Test
    public void testPuttingCargoAtFlagSetsPosition() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Point point1 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point1);

        Cargo cargo = new Cargo(PLANK, map);

        flag0.putCargo(cargo);

        assertEquals(cargo.getPosition(), point1);
    }

    @Test
    public void testCargoIsReturnedToStorageWhenTargetBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Placing forester */
        Point point39 = new Point(10, 8);
        Building foresterHut0 = map.placeBuilding(new ForesterHut(player0), point39);

        /* Placing flag */
        Point point2 = new Point(9, 5);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Placing road between (11, 7) and (9, 5) */
        Point point40 = new Point(11, 7);
        Point point41 = new Point(10, 6);
        Point point42 = new Point(7, 5);
        Point point43 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point40, point41, point2);

        /* Placing road between (9, 5) and (6, 4) */
        Road road1 = map.placeRoad(player0, point2, point42, point43);

        /* Place couriers on the roads */
        Utils.occupyRoad(road0, map);
        Utils.occupyRoad(road1, map);

        /* Wait for a cargo with the forester hut as target to get picked up by the first courier */
        for (int i = 0; i < 2000; i++) {

            Cargo cargo = road1.getCourier().getCargo();

            if (cargo != null && cargo.getTarget().equals(foresterHut0)) {
                break;
            }

            map.stepTime();
        }

        /* Remove the forester hut */
        foresterHut0.tearDown();

        /* Verify that the courier delivers the cargo to the next flag */
        Courier courier = road1.getCourier();
        Cargo cargo = courier.getCargo();

        assertEquals(courier.getTarget(), point2);
        assertTrue(flag0.getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point2);

        /* Verify that the courier picks up the cargo again and returns it to the storage */
        for (int i = 0; i < 200; i++) {

            if (cargo.equals(courier.getCargo())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertNull(courier.getCargo());
    }
}
