/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.StorageWorker;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestHeadquarter {

    /*
    TODO: test discovered area
     */

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
        assertTrue(headquarter0.isReady());
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
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

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

        /* Verify that the headquarter has planks */
        assertTrue(headquarter0.getAmount(PLANK) > 0);

        /* Verify that the storage worker delivers stone or planks to the woodcutter */
        assertTrue(headquarter0.getWorker() instanceof StorageWorker);

        StorageWorker storageWorker0 = (StorageWorker) headquarter0.getWorker();

        assertTrue(storageWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(storageWorker0.isInsideBuilding());
        assertNotNull(storageWorker0.getCargo());
        assertEquals(storageWorker0.getTarget(), headquarter0.getFlag().getPosition());
        assertTrue(headquarter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, storageWorker0, headquarter0.getFlag().getPosition());

        assertNull(storageWorker0.getCargo());
        assertFalse(headquarter0.getFlag().getStackedCargo().isEmpty());
    }

    @Test(expected = InvalidUserActionException.class)
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

    @Test(expected = InvalidUserActionException.class)
    public void testHeadquarterCannotBeTornDownByRemovingFlag() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that trying to tear it down by removing the flag causes an invalid user action exception */
        map.removeFlag(headquarter0.getFlag());
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
            fail();
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

    @Test
    public void testBorderForHeadquarterIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the border around the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Border:

                -9, +9  -------  +9, +9
                  /                  \
            -18, 0        H          18, 0
                  \                  /
                -9, -9  -------  +9, +9

         */

        Set<Point> hexagonBorder = new HashSet<>();

        int upperY = point0.y;
        int lowerY = point0.y;
        for (int x = point0.x - 18; x < point0.x - 9; x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY++;
            lowerY--;
        }

        upperY = point0.y + 9;
        lowerY = point0.y - 9;
        for (int x = point0.x + 9; x <= point0.x + 18; x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY--;
            lowerY++;
        }

        for (int x = point0.x - 9; x < point0.x + 9; x += 2) {
            hexagonBorder.add(new Point(x, point0.y + 9));
            hexagonBorder.add(new Point(x, point0.y - 9));
        }

        /* Verify that all points in the hexagon are part of the actual border */
        Set<Point> border = player0.getBorderPoints();
        for (Point point : hexagonBorder) {
            assertTrue(border.contains(point));
        }

        /* Verify that all points in the actual border are part of the hexagon border */
        for (Point point : border) {
            assertTrue(hexagonBorder.contains(point));
        }
    }

    @Test
    public void testLandForHeadquarterIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the land of the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        Point position = headquarter0.getPosition();
        Set<Point> area = Utils.getAreaInsideHexagon(8, position);

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> land = headquarter0.getDefendedLand();
        for (Point point : land) {
            assertTrue(area.contains(point));
        }

        /* Verify that all points in the actual land are part of the hexagon land */
        for (Point point : area) {
            assertTrue(land.contains(point));
        }
    }

    @Test
    public void testDiscoveredLandForHeadquarterIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarter */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the discovered land of the headquarter is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
        Land

                -8, +8  -------  +8, +8
                  /                  \
            -16, 0        H          16, 0
                  \                  /
                -8, -8  -------  +8, +8

         */
        int radius = 13; // Border is at 9, then 4 more points out is discovered
        Point position = headquarter0.getPosition();
        Set<Point> area = Utils.getAreaInsideHexagon(radius, position);

        /* Verify that all points in the hexagon land are part of the actual land */
        Collection<Point> discoveredLand = player0.getDiscoveredLand();
        for (Point point : discoveredLand) {
            assertTrue(area.contains(point));
        }

        /* Verify that all points in the actual land are part of the hexagon land */
        for (Point point : area) {
            assertTrue(discoveredLand.contains(point));
        }
    }
}
