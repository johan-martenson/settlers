/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestCargo {

    @Test
    public void testPuttingCargoAtFlagSetsPosition() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarter
        var point0 = new Point(12, 12);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(8, 6);
        var flag0 = map.placeFlag(player0, point1);

        // Verify that placing a cargo sets its position
        var cargo = new Cargo(PLANK, map);

        flag0.putCargo(cargo);

        assertEquals(cargo.getPosition(), point1);
    }

    @Test
    public void testCargoIsReturnedToStorageWhenTargetBuildingIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place forester
        var point1 = new Point(10, 8);
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), point1);

        // Place flag
        var point2 = new Point(9, 5);
        var flag0 = map.placeFlag(player0, point2);

        // Place road between (11, 7) and (9, 5)
        var point3 = new Point(11, 7);
        var point4 = new Point(10, 6);
        var point5 = new Point(7, 5);
        var point6 = new Point(6, 4);
        var road0 = map.placeRoad(player0, point3, point4, point2);

        // Place road between (9, 5) and (6, 4)
        var road1 = map.placeRoad(player0, point2, point5, point6);

        // Place couriers on the roads
        Utils.occupyRoad(road0, map);
        Utils.occupyRoad(road1, map);

        // Wait for a cargo with the forester hut as target to get picked up by the first courier
        for (int i = 0; i < 2000; i++) {

            var cargo = road1.getCourier().getCargo();

            if (cargo != null && cargo.getTarget().equals(foresterHut0)) {
                break;
            }

            map.stepTime();
        }

        // Remove the forester hut
        foresterHut0.tearDown();

        // Verify that the courier delivers the cargo to the next flag
        var courier = road1.getCourier();
        var cargo = courier.getCargo();

        assertEquals(courier.getTarget(), point2);
        assertTrue(flag0.getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point2);

        // Verify that the courier picks up the cargo again and returns it to the storage
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
