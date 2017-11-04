/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestHeadquarter {

    @Test
    public void testInitialInventory() {
        Headquarter hq = new Headquarter(null);

        assertEquals(hq.getAmount(WOOD),    4);
        assertEquals(hq.getAmount(PLANCK), 15);
        assertEquals(hq.getAmount(STONE),  10);
        assertEquals(hq.getAmount(DONKEY), 1);

        assertEquals(hq.getAmount(MINER),   3);

        // TODO: add all other material
    }

    @Test
    public void testHeadquarterIsReadyDirectly() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);
        Headquarter hq = new Headquarter(player0);
        Point hqPoint = new Point(5, 5);

        map.placeBuilding(hq, hqPoint);

        assertTrue(hq.ready());
    }

    @Test
    public void testHeadquarterNeedsNoWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);
        Headquarter hq = new Headquarter(player0);
        Point hqPoint = new Point(5, 5);

        map.placeBuilding(hq, hqPoint);

        assertFalse(hq.needsWorker());
    }

    @Test
    public void testHeadquarterGetsWorkerAutomatically() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);
        Headquarter hq = new Headquarter(player0);
        Point hqPoint = new Point(5, 5);

        map.placeBuilding(hq, hqPoint);

        assertFalse(hq.needsWorker());
        assertNotNull(hq.getWorker());
    }

    @Test
    public void testHeadquartersStorageWorkerDeliversCargo() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(11, 9);
        Building wc = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        map.placeAutoSelectedRoad(player0, hq.getFlag(), wc.getFlag());

        /* The storage worker rests */
        Utils.fastForward(19, map);

        /* Verify that the hq has plancks */
        assertTrue(hq.getAmount(PLANCK) > 0);

        /* The storage worker delivers stone or plancks to the woodcutter */
        assertTrue(hq.getWorker() instanceof StorageWorker);

        StorageWorker sw = (StorageWorker)hq.getWorker();

        assertTrue(sw.isInsideBuilding());

        map.stepTime();

        assertFalse(sw.isInsideBuilding());
        assertNotNull(sw.getCargo());
        assertEquals(sw.getTarget(), hq.getFlag().getPosition());
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, sw, hq.getFlag().getPosition());

        assertNull(sw.getCargo());
        assertFalse(hq.getFlag().getStackedCargo().isEmpty());
    }

    @Test(expected = Exception.class)
    public void testHeadquarterCannotBeTornDown() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that trying to tear it down causes an exception */
        hq.tearDown();
    }

    @Test
    public void testHeadquarterCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarter can't produce */
        assertFalse(headquarter0.canProduce());
    }
}
