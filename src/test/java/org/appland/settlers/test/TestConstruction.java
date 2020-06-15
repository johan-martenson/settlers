package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestConstruction {

    @Test
    public void testCreateNewWoodcutter() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(9, 9);
        Building sawmill0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that the sawmill is not a military building */
        assertFalse(sawmill0.isMilitaryBuilding());

        /* Verify that the sawmill needs the right amount of material for construction */
        assertEquals(sawmill0.getAmount(WOOD), 0);
        assertEquals(sawmill0.getAmount(PLANK), 0);
        assertTrue(sawmill0.isUnderConstruction());

        assertTrue(sawmill0.needsMaterial(PLANK));

        assertFalse(sawmill0.needsMaterial(SERGEANT));

        assertTrue(sawmill0.needsMaterial(PLANK));

        sawmill0.promiseDelivery(PLANK);
        assertTrue(sawmill0.needsMaterial(PLANK));

        sawmill0.promiseDelivery(PLANK);
        assertFalse(sawmill0.needsMaterial(PLANK));

        /* Verify that construction doesn't finish before material is delivered */
        for (int i = 0; i < 1000; i++) {
            assertTrue(sawmill0.isUnderConstruction());
            sawmill0.stepTime();
        }

        Cargo plankCargo = new Cargo(PLANK, null);

        sawmill0.putCargo(plankCargo);

        for (int i = 0; i < 1000; i++) {
            assertTrue(sawmill0.isUnderConstruction());
            sawmill0.stepTime();
        }

        /* Verify that construction can finish when all material is delivered */
        sawmill0.putCargo(plankCargo);
        sawmill0.stepTime();

        assertTrue(sawmill0.isReady());

        assertFalse(sawmill0.isMilitaryBuilding());

        /* Verify that all material was consumed by the construction */
        assertEquals(sawmill0.getAmount(PLANK), 0);
        assertEquals(sawmill0.getAmount(STONE), 0);

        /* Verify that the sawmill doesn't need any material when it's finished */
        for (Material material : Material.values()) {
            assertFalse(sawmill0.needsMaterial(material));
        }

        sawmill0.tearDown();

        assertTrue(sawmill0.isBurningDown());

        for (int i = 0; i < 50; i++) {
            assertTrue(sawmill0.isBurningDown());
            sawmill0.stepTime();
        }

        assertTrue(sawmill0.isDestroyed());
    }

    @Test
    public void testCreateNewBarracks() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players,30, 30);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(13, 13);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        assertTrue(barracks0.isUnderConstruction());
        assertTrue(barracks0.isMilitaryBuilding());
        assertEquals(barracks0.getMaxHostedMilitary(), 2);
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
        assertEquals(barracks0.getPromisedMilitary(), 0);
        assertFalse(barracks0.needsMilitaryManning());

        /* Construct the barracks */
        Utils.constructHouse(barracks0);

        assertTrue(barracks0.isMilitaryBuilding());
        assertTrue(barracks0.isReady());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);
        assertEquals(barracks0.getMaxHostedMilitary(), 2);
        assertTrue(barracks0.needsMilitaryManning());
        assertTrue(barracks0.isMilitaryBuilding());
    }

    @Test
    public void testCreateNewSawmill() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(9, 9);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        assertTrue(sawmill0.isUnderConstruction());

        /* Verify that construction doesn't finish before material is delivered */
        for (int i = 0; i < 1000; i++) {
            assertTrue(sawmill0.isUnderConstruction());
            sawmill0.stepTime();
        }

        Cargo plankCargo = new Cargo(PLANK, null);
        Cargo stoneCargo = new Cargo(STONE, null);

        sawmill0.promiseDelivery(PLANK);
        sawmill0.putCargo(plankCargo);

        sawmill0.promiseDelivery(PLANK);
        sawmill0.putCargo(plankCargo);

        sawmill0.promiseDelivery(STONE);
        sawmill0.putCargo(stoneCargo);

        for (int i = 0; i < 1000; i++) {
            assertTrue(sawmill0.isUnderConstruction());
            sawmill0.stepTime();
        }

        /* Verify that construction can finish when all material is delivered */
        sawmill0.promiseDelivery(STONE);
        sawmill0.putCargo(stoneCargo);

        sawmill0.stepTime();

        assertTrue(sawmill0.isReady());

        /* Verify that all material was consumed by the construction */
        assertEquals(sawmill0.getAmount(PLANK), 0);
        assertEquals(sawmill0.getAmount(STONE), 0);

        /* Verify that the sawmill needs only WOOD when it's finished */
        assertTrue(sawmill0.needsMaterial(WOOD));
        assertFalse(sawmill0.needsMaterial(PLANK));
        assertFalse(sawmill0.needsMaterial(STONE));

        sawmill0.tearDown();

        for (int i = 0; i < 50; i++) {
            assertTrue(sawmill0.isBurningDown());
            sawmill0.stepTime();
        }

        assertTrue(sawmill0.isDestroyed());
    }

    @Test
    public void testCreateFarm() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(9, 9);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        assertTrue(farm.isUnderConstruction());

        /* Verify that construction doesn't finish before material is delivered */
        for (int i = 0; i < 1000; i++) {
            assertTrue(farm.isUnderConstruction());
            farm.stepTime();
        }

        Cargo plankCargo = new Cargo(PLANK, null);
        Cargo stoneCargo = new Cargo(STONE, null);
        farm.putCargo(plankCargo);
        farm.putCargo(plankCargo);
        farm.putCargo(plankCargo);
        farm.putCargo(stoneCargo);
        farm.putCargo(stoneCargo);

        for (int i = 0; i < 1000; i++) {
            assertTrue(farm.isUnderConstruction());
            farm.stepTime();
        }

        /* Verify that construction can finish when all material is delivered */
        farm.putCargo(stoneCargo);
        farm.stepTime();

        assertTrue(farm.isReady());

        /* Verify that all material was consumed by the construction */
        assertEquals(farm.getAmount(PLANK), 0);
        assertEquals(farm.getAmount(STONE), 0);

        farm.tearDown();

        for (int i = 0; i < 50; i++) {
            assertTrue(farm.isBurningDown());
            farm.stepTime();
        }

        assertTrue(farm.isDestroyed());
    }

    @Test
    public void testInvalidDeliveryToUnfinishedSawmill() throws Exception {
        Sawmill sawmill0 = new Sawmill(null);

        try {
            sawmill0.putCargo(new Cargo(SWORD, null));

            fail();
        } catch (InvalidMaterialException e) {}
    }

    @Test
    public void testDeliveryToBurningSawmill() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(9, 9);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        Utils.constructHouse(sawmill0);

        assertTrue(sawmill0.isReady());

        sawmill0.tearDown();

        try {
            sawmill0.putCargo(new Cargo(WOOD, null));

            fail();
        } catch (InvalidStateForProduction e) {}
    }

    @Test
    public void testDeliveryToDestroyedSawmill() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(4, 4);
        Building sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill0);

        assertTrue(sawmill0.isReady());

        /* Tear down the sawmill */
        sawmill0.tearDown();

        Utils.fastForward(1000, map);

        /* Verify that it's not possible to deliver wood to a sawmill that doesn't exist on the map */
        try {
            sawmill0.putCargo(new Cargo(WOOD, null));

            fail();
        } catch (InvalidStateForProduction e) {}
    }
}
