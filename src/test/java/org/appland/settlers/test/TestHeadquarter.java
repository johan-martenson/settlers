/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.StorageWorker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;
import static org.junit.Assert.*;

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarters is ready immediately */
        assertTrue(headquarter0.isReady());
    }

    @Test
    public void testHeadquarterNeedsNoWorker() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarters doesn't need a worker */
        assertFalse(headquarter0.needsWorker());
    }

    @Test
    public void testHeadquarterGetsWorkerAutomatically() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarters has a worker */
        assertFalse(headquarter0.needsWorker());
        assertNotNull(headquarter0.getWorker());
    }

    @Test
    public void testHeadquartersStorageWorkerDeliversCargo() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(11, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        /* Connect the woodcutter to the headquarters */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* The storage worker rests */
        Utils.fastForward(19, map);

        /* Verify that the headquarters has planks */
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

    @Test
    public void testHeadquarterCannotBeTornDown() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that trying to tear it down causes an exception */
        try {
            headquarter0.tearDown();

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testHeadquarterCannotBeTornDownByRemovingFlag() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that trying to tear it down by removing the flag causes an invalid user action exception */
        try {
            map.removeFlag(headquarter0.getFlag());

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testHeadquarterCannotProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarters can't produce */
        assertFalse(headquarter0.canProduce());
    }

    @Test
    public void testCannotPlaceTwoHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to place a second headquarters */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the reported output is correct */
        assertEquals(headquarter0.getProducedMaterial().length, 0);
    }

    @Test
    public void testHeadquarterReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the reported needed production material is correct */
        assertEquals(headquarter0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(headquarter0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testBorderForHeadquarterIsCorrect() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarters */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the border around the headquarters is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarters */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the land of the headquarters is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarters */
        Point point0 = new Point(30, 30);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the discovered land of the headquarters is hexagon shaped and the middle of each line is 9 steps away from the center of the headquarter
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

    @Test
    public void testDiscoveredLandForPlayerCannotBeOutsideTheMap() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarters */
        Point point0 = new Point(4, 4);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getDiscoveredLand()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testOwnedLandForPlayerCannotBeOutsideTheMap() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 80, 80);

        /* Place headquarters */
        Point point0 = new Point(4, 4);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the discovered land is only inside the map */
        for (Point point : player0.getLandInPoints()) {
            assertTrue(point.x >= 0);
            assertTrue(point.y >= 0);
        }
    }

    @Test
    public void testCreatedSoldierPlacedInInventoryWhenReservedAmountIsZero() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road to get an assigned courier */
        Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Place a cask of beer to be delivered to the headquarters */
        Cargo beerCargo = Utils.placeCargo(map, BEER, flag0, headquarter0);

        /* Wait for the courier to pick up the beer and carry it to the headquarters */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), beerCargo);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), headquarter0.getPosition());

        /* Verify that no private soldier is kept as reserve */
        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(headquarter0.getAmount(PRIVATE), 0);
    }

    @Test
    public void testCreatedSoldierHostedWhenReservedAmountIsHigher() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);

        /* Set reserved privates to zero */
        Utils.setReservedSoldiers(headquarter0, 3, 0, 0, 0, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiers().size(), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road to get an assigned courier */
        Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Place a cask of beer to be delivered to the headquarters */
        Cargo beerCargo = Utils.placeCargo(map, BEER, flag0, headquarter0);

        /* Wait for the courier to pick up the beer and carry it to the headquarters */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), beerCargo);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), headquarter0.getPosition());

        /* Verify that the new private soldier is kept as reserve */
        Utils.fastForward(110, map);

        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 1);
        assertEquals(headquarter0.getAmount(PRIVATE), 0);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 1);
        //assertEquals(headquarter0.getHostedSoldiers().size(), 1);
    }

    @Test
    public void testArrivingPrivateIsHostedWhenReservedAmountIsHigher() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);

        /* Set reserved privates */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiers().size(), 0);
        assertEquals(headquarter0.getAmount(PRIVATE), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as reserve */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 1);
        //assertEquals(headquarter0.getHostedSoldiers().size(), 1);
        assertEquals(headquarter0.getAmount(PRIVATE), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 1);

    }

    @Test
    public void testArrivingPrivateFirstRankIsHostedWhenReservedAmountIsHigher() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiers().size(), 0);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 0);
        //assertEquals(headquarter0.getHostedSoldiers().stream().filter(soldier -> soldier.getRank() == Soldier.Rank.PRIVATE_FIRST_CLASS_RANK).count(), 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as reserve */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 1);
        //assertEquals(headquarter0.getHostedSoldiers().stream().filter(s -> s.getRank() == Soldier.Rank.PRIVATE_FIRST_CLASS_RANK).count(), 1);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 1);
    }

    @Test
    public void testArrivingSergeantIsHostedWhenReservedAmountIsHigher() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.SERGEANT_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(SERGEANT), 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.SERGEANT_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as reserve */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 1);
        assertEquals(headquarter0.getAmount(SERGEANT), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 1);
    }

    @Test
    public void testArrivingOfficerIsHostedWhenReservedAmountIsHigher() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.OFFICER_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(OFFICER), 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.OFFICER_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as reserve */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 1);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 1);
        assertEquals(headquarter0.getAmount(OFFICER), 0);
    }

    @Test
    public void testArrivingGeneralIsHostedWhenReservedAmountIsHigher() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.GENERAL_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 0);
        assertEquals(headquarter0.getAmount(GENERAL), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.GENERAL_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as reserve */
        assertEquals(headquarter0.getAmount(GENERAL), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 1);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 1);
        assertEquals(headquarter0.getAmount(GENERAL), 0);
    }

    @Test
    public void testArrivingPrivateIsPutInInventoryWhenReservedAmountIsFull() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as inventory */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(PRIVATE), 1);
    }

    @Test
    public void testArrivingPrivateFirstClassIsPutInInventoryWhenReservedAmountIsFull() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as inventory */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 1);
    }

    @Test
    public void testArrivingSergeantIsPutInInventoryWhenReservedAmountIsFull() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.SERGEANT_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.SERGEANT_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as inventory */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(SERGEANT), 1);
    }

    @Test
    public void testArrivingOfficerIsPutInInventoryWhenReservedAmountIsFull() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.OFFICER_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.OFFICER_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as inventory */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(OFFICER), 1);
    }

    @Test
    public void testArrivingGeneralIsPutInInventoryWhenReservedAmountIsFull() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Set reserved privates to zero */
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.GENERAL_RANK), 0);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place and construct barracks */
        Point point2 = new Point(9, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        Utils.constructHouse(barracks0);

        /* Place private in the barracks, burn it down, so it walks to the headquarters */
        Soldier soldier = Utils.occupyMilitaryBuilding(Soldier.Rank.GENERAL_RANK, barracks0);

        barracks0.tearDown();

        /* Verify that the new private soldier is kept as inventory */
        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter0.getPosition());

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 0);
        assertEquals(headquarter0.getAmount(GENERAL), 1);
    }

    @Test
    public void testIncreasingReservedAmountMovesPrivateFromInventoryToHosted() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);

        /* Verify that increasing the reserved amount moves the soldier from inventory to hosted */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 3);
        assertEquals(headquarter0.getAmount(PRIVATE), 2);
    }

    @Test
    public void testIncreasingReservedAmountMovesPrivateFirstClassFromInventoryToHosted() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);

        /* Verify that increasing the reserved amount moves the soldier from inventory to hosted */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 3);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 2);
    }

    @Test
    public void testIncreasingReservedAmountMovesSergeantFromInventoryToHosted() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);

        /* Verify that increasing the reserved amount moves the soldier from inventory to hosted */
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.SERGEANT_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 3);
        assertEquals(headquarter0.getAmount(SERGEANT), 2);
    }

    @Test
    public void testIncreasingReservedAmountMovesOfficerFromInventoryToHosted() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);

        /* Verify that increasing the reserved amount moves the soldier from inventory to hosted */
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.OFFICER_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 3);
        assertEquals(headquarter0.getAmount(OFFICER), 2);
    }

    @Test
    public void testIncreasingReservedAmountMovesGeneralFromInventoryToHosted() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Verify that increasing the reserved amount moves the soldier from inventory to hosted */
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.GENERAL_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 3);
        assertEquals(headquarter0.getAmount(GENERAL), 2);
    }

    @Test
    public void testDecreasingReservedAmountMovesPrivateFromHostedToInventory() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, BEER, 0);
        Utils.adjustInventoryTo(headquarter0, SWORD, 0);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);

        /* Reserve four soldiers */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 4);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 4);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 4);
        assertEquals(headquarter0.getAmount(PRIVATE), 1);

        /* Verify that decreasing the reserved amount moves the soldier from hosted to inventory */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 2);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 2);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 2);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 2);
        assertEquals(headquarter0.getAmount(PRIVATE), 3);
    }

    /*
      Test reserved soldiers:
       - Default value for reserved soldiers
       - Reserved soldiers are correct at start
       - Can protect the headquarters
       - Don't populate military buildings - OK
       - Can't attack - OK
       - Can't defend other buildings - OK
       - Aren't promoted - OK
       - Aren't visible in the inventory - OK
       - Soldier enter become reserved - OK
       - Created soldier enters reserve - OK (private only)

       Also:
        - Decide how getHostedSoldiers() and getHostedSoldiersByRank() should behave with respect to the reserve
        - getAmount() only shows what's in inventory. Reserved soldiers are not included
     */

    @Test
    public void testReservedSoldiersArentPromoted() throws Exception {

        /* Start single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        Utils.setReservedSoldiers(headquarter0, 0, 0, 0, 0, 0);

        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 0);

        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);
        Utils.adjustInventoryTo(headquarter0, GOLD, 20);

        /* Reserve four soldiers */
        Utils.setReservedSoldiers(headquarter0, 4, 4, 4, 4, 4);

        /* Verify that the reserved soldiers aren't promoted */
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 4);
        assertEquals(headquarter0.getAmount(PRIVATE), 1);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 1);
        assertEquals(headquarter0.getAmount(SERGEANT), 1);
        assertEquals(headquarter0.getAmount(OFFICER), 1);
        assertEquals(headquarter0.getAmount(GENERAL), 1);

        Utils.fastForward(200, map);

        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.SERGEANT_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.OFFICER_RANK), (Integer) 4);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.GENERAL_RANK), (Integer) 4);
        assertEquals(headquarter0.getAmount(GOLD), 20);
    }

    @Test
    public void testReservedSoldiersDontDefendOtherBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Set remote defenders to max for player 1 */
        player1.setDefenseFromSurroundingBuildings(10);

        /* Set soldiers in inventory & reserve for player 1 */
        Utils.setReservedSoldiers(headquarter1, 0, 0, 0, 0, 0);

        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 10);

        headquarter1.setReservedSoldiers(GENERAL_RANK, 10);

        assertEquals(headquarter1.getAmount(GENERAL), 0);
        assertEquals(headquarter1.getActualReservedSoldiers().get(GENERAL_RANK), (Integer) 10);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate the barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that player 1's reserved soldiers don't defend when its barracks is attacked */
        assertTrue(barracks1.isOccupied());
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        for (int i = 0; i < 2000; i++) {
            if (barracks1.getPlayer().equals(player0)) {
                break;
            }

            assertEquals(headquarter1.getAmount(GENERAL), 0);
            assertEquals(headquarter1.getActualReservedSoldiers().get(GENERAL_RANK), (Integer) 10);

            map.stepTime();
        }

        assertEquals(barracks1.getPlayer(), player0);
    }

    @Test
    public void testReservedSoldiersCantAttackOtherBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Set remote defenders to max for player 1 */
        player1.setDefenseFromSurroundingBuildings(10);

        /* Set soldiers in inventory & reserve for player 1 */
        Utils.setReservedSoldiers(headquarter1, 0, 0, 0, 0, 0);

        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 10);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that player 1 can't use its reserved soldiers to attack */
        assertTrue(player1.canAttack(barracks0));

        headquarter1.setReservedSoldiers(GENERAL_RANK, 10);

        assertEquals(headquarter1.getAmount(GENERAL), 0);
        assertEquals(headquarter1.getActualReservedSoldiers().get(GENERAL_RANK), (Integer) 10);
        assertFalse(player1.canAttack(barracks0));

        try {
            player1.attack(barracks0, 2, AttackStrength.STRONG);

            fail();
        } catch (InvalidUserActionException ignored) { }
    }

    @Test
    public void testReservedSoldiersDontPopulateMilitaryBuildings() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set soldiers in inventory & reserve for player 1 */
        Utils.setReservedSoldiers(headquarter0, 0, 0, 0, 0, 0);

        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER);
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 10);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 10);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 10);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 10);

        headquarter0.setReservedSoldiers(PRIVATE_RANK, 10);
        headquarter0.setReservedSoldiers(PRIVATE_FIRST_CLASS_RANK, 10);
        headquarter0.setReservedSoldiers(SERGEANT_RANK, 10);
        headquarter0.setReservedSoldiers(OFFICER_RANK, 10);
        headquarter0.setReservedSoldiers(GENERAL_RANK, 10);

        assertEquals(headquarter0.getAmount(PRIVATE), 0);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 0);
        assertEquals(headquarter0.getAmount(SERGEANT), 0);
        assertEquals(headquarter0.getAmount(OFFICER), 0);
        assertEquals(headquarter0.getAmount(GENERAL), 0);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place road to the barracks */
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to get constructed */
        Utils.constructHouse(barracks0);

        /* Verify that the barracks is not populated because all soldiers are reserved */
        assertFalse(barracks0.isOccupied());

        Utils.fastForward(500, map);

        assertFalse(barracks0.isOccupied());
    }
}
