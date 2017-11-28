/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
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
    public void testInitialInventory() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the initial inventory is correct */
        assertEquals(headquarter0.getAmount(WOOD), 24);
        assertEquals(headquarter0.getAmount(PLANK), 44);
        assertEquals(headquarter0.getAmount(STONE), 68);
        assertEquals(headquarter0.getAmount(PIG), 0);
        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertEquals(headquarter0.getAmount(FLOUR), 0);
        assertEquals(headquarter0.getAmount(FISH), 4);
        assertEquals(headquarter0.getAmount(MEAT), 6);
        assertEquals(headquarter0.getAmount(BREAD), 8);
        assertEquals(headquarter0.getAmount(WATER), 0);
        assertEquals(headquarter0.getAmount(BEER), 0);
        assertEquals(headquarter0.getAmount(COAL), 16);
        assertEquals(headquarter0.getAmount(IRON), 0);
        assertEquals(headquarter0.getAmount(GOLD), 0);
        assertEquals(headquarter0.getAmount(IRON_BAR), 16);
        assertEquals(headquarter0.getAmount(COIN), 0);
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), 6);
        assertEquals(headquarter0.getAmount(SAWMILL_WORKER), 2);
        assertEquals(headquarter0.getAmount(MINER), 2);
        assertEquals(headquarter0.getAmount(FORESTER), 4);
        assertEquals(headquarter0.getAmount(IRON_FOUNDER), 4);
        assertEquals(headquarter0.getAmount(FISHERMAN), 6);
        assertEquals(headquarter0.getAmount(FARMER), 8);
        assertEquals(headquarter0.getAmount(BUTCHER), 2);
        assertEquals(headquarter0.getAmount(BAKER), 2);
        assertEquals(headquarter0.getAmount(HUNTER), 2);
        assertEquals(headquarter0.getAmount(SWORD), 0);
        assertEquals(headquarter0.getAmount(SHIELD), 0);

        assertEquals(headquarter0.getAmount(DONKEY), 8);
        assertEquals(headquarter0.getAmount(PRIVATE), 51);
        assertEquals(headquarter0.getAmount(SERGEANT), 0);
        assertEquals(headquarter0.getAmount(GENERAL), 0);

        // TODO: add all other material
    }

    @Test
    public void testHeadquarterIsReadyDirectly() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarter is ready immediately */
        assertTrue(headquarter0.ready());
    }

    @Test
    public void testHeadquarterNeedsNoWorker() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);;

        /* Verify that the headquarter doesn't need a worker */
        assertFalse(headquarter0.needsWorker());
    }

    @Test
    public void testHeadquarterGetsWorkerAutomatically() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarter has a worker */
        assertFalse(headquarter0.needsWorker());
        assertNotNull(headquarter0.getWorker());
    }

    @Test
    public void testHeadquartersStorageWorkerDeliversCargo() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        /* Connect the woodcutter to the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* The storage worker rests */
        Utils.fastForward(19, map);

        /* Verify that the hq has planks */
        assertTrue(headquarter0.getAmount(PLANK) > 0);

        /* Verify that the storage worker delivers stone or planks to the woodcutter */
        assertTrue(headquarter0.getWorker() instanceof StorageWorker);

        StorageWorker sw = (StorageWorker) headquarter0.getWorker();

        assertTrue(sw.isInsideBuilding());

        map.stepTime();

        assertFalse(sw.isInsideBuilding());
        assertNotNull(sw.getCargo());
        assertEquals(sw.getTarget(), headquarter0.getFlag().getPosition());
        assertTrue(headquarter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, sw, headquarter0.getFlag().getPosition());

        assertNull(sw.getCargo());
        assertFalse(headquarter0.getFlag().getStackedCargo().isEmpty());
    }

    @Test(expected = Exception.class)
    public void testHeadquarterCannotBeTornDown() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that trying to tear it down causes an exception */
        headquarter0.tearDown();
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarter can't produce */
        assertFalse(headquarter0.canProduce());
    }

    @Test
    public void testCannotPlaceTwoHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a second headquarter */
        Point point1 = new Point(10, 10);
        try {
            Headquarter headquarter1 = map.placeBuilding(new Headquarter(player0), point1);
            assertTrue(false);
        } catch (Exception e) {
        }
    }

    @Test
    public void testHeadquarterReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the reported output is correct */
        assertEquals(headquarter0.getProducedMaterial().length, 0);
    }

    @Test
    public void testHeadquarterReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the reported needed production material is correct */
        assertEquals(headquarter0.getMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(headquarter0.getTotalAmountNeeded(material), 0);
        }
    }
}
