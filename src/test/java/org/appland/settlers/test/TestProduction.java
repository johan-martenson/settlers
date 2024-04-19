package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestProduction {

    @Test
    public void testWrongMaterialToSawmill() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 10);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point1);

        assertTrue(sawmill.isPlanned());
        assertFalse(sawmill.needsWorker());

        /* Connect the forester with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(sawmill);

        assertTrue(sawmill.isReady());

        try {
            sawmill.putCargo(new Cargo(GOLD, null));

            fail();
        } catch (InvalidMaterialException e) {}
    }

    @Test
    public void testDeliverMaterialToWoodcutter() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter.isPlanned());
        assertFalse(woodcutter.needsWorker());

        /* Connect the forester with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        try {
            woodcutter.putCargo(new Cargo(WOOD, null));

            fail();
        } catch (DeliveryNotPossibleException e) {}
    }

    @Test
    public void testDeliveryMaterialToQuarry() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 10);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        assertTrue(quarry.isPlanned());
        assertFalse(quarry.needsWorker());

        /* Connect the forester with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter0.getFlag());

        /* Wait for the forester hut to get constructed */
        Utils.waitForBuildingToBeConstructed(quarry);

        try {
            quarry.putCargo(new Cargo(BEER, null));

            fail();
        } catch (DeliveryNotPossibleException e) {}
    }

    @Test
    public void testGetWorkerTypeForBuildingNotNeedingWorker() {
        Headquarter headquarter = new Headquarter(null);

        assertFalse(headquarter.needsWorker());
        assertNull(headquarter.getWorkerType());
    }
}
