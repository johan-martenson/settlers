package org.appland.settlers.test;

import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Well;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WELL_WORKER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMultipleStorehouses {

    @Test
    public void testHouseGetsDeliveryFromRemoteHeadquarterWhenLocalStorehouseLacksMaterial() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(22, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place well to the left of the headquarter */
        Point point2 = new Point(7, 5);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Place second well to the right of the storehouse */
        Point point3 = new Point(30, 6);
        Well well1 = map.placeBuilding(new Well(player0), point3);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Wait for the storehouse to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Make sure there is no construction material in the storehouse */
        Utils.adjustInventoryTo(storehouse, PLANK, 0);
        Utils.adjustInventoryTo(storehouse, STONE, 0);

        /* Connect the storehouse with the right-most well */
        Road road1 = map.placeAutoSelectedRoad(player0, well1.getFlag(), storehouse.getFlag());

        /* Connect the left-most well with the headquarter */
        Road road2 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter.getFlag());

        /* Assign builders */
        Utils.assignBuilder(well0);
        Utils.assignBuilder(well1);

        /* Verify that both of the wells get deliveries and get constructed */
        assertTrue(well0.isUnderConstruction());
        assertTrue(well1.isUnderConstruction());

        Utils.waitForBuildingsToBeConstructed(well0, well1);

        assertFalse(well0.isUnderConstruction());
        assertFalse(well1.isUnderConstruction());
    }

    @Test
    public void testHouseGetsAssignedWorkerFromRemoteHeadquarterWhenLocalStorehouseLacksWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(22, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place well to the left of the headquarter */
        Point point2 = new Point(7, 5);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Place second well to the right of the storehouse */
        Point point3 = new Point(30, 6);
        Well well1 = map.placeBuilding(new Well(player0), point3);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Wait for the storehouse to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Make sure there is no construction material in the storehouse */
        Utils.adjustInventoryTo(storehouse, PLANK, 0);
        Utils.adjustInventoryTo(storehouse, STONE, 0);

        /* Connect the storehouse with the right-most well */
        Road road1 = map.placeAutoSelectedRoad(player0, well1.getFlag(), storehouse.getFlag());

        /* Connect the left-most well with the headquarter */
        Road road2 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter.getFlag());

        /* Wait for both wells to get constructed */
        Utils.adjustInventoryTo(headquarter, WELL_WORKER, 5);
        Utils.adjustInventoryTo(storehouse, WELL_WORKER, 0);

        Utils.waitForBuildingsToBeConstructed(well0, well1);

        /* Verify that both of the wells get assigned workers */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(well0, well1);

        assertTrue(well0.isOccupied());
        assertTrue(well1.isOccupied());
    }

    @Test
    public void testMilitaryHouseGetsAssignedSoldierFromRemoteHeadquarterWhenLocalStorehouseLacksSoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(22, 6);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place barracks to the left of the headquarter */
        Point point2 = new Point(7, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place second barracks to the right of the storehouse */
        Point point3 = new Point(30, 6);
        Barracks barracks1 = map.placeBuilding(new Barracks(player0), point3);

        /* Make sure there is enough construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        /* Connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Wait for the storehouse to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        /* Make sure there is no construction material in the storehouse */
        Utils.adjustInventoryTo(storehouse, PLANK, 0);
        Utils.adjustInventoryTo(storehouse, STONE, 0);

        /* Connect the storehouse with the right-most barracks */
        Road road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), storehouse.getFlag());

        /* Connect the left-most barracks with the headquarter */
        Road road2 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        /* Wait for both barracks to get constructed */
        Utils.adjustInventoryTo(headquarter, PRIVATE, 5);
        Utils.adjustInventoryTo(storehouse, PRIVATE, 0);
        Utils.adjustInventoryTo(storehouse, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(storehouse, SERGEANT, 0);
        Utils.adjustInventoryTo(storehouse, OFFICER, 0);
        Utils.adjustInventoryTo(storehouse, GENERAL, 0);

        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1);

        /* Verify that both of the barracks get assigned soldiers */
        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1);

        assertTrue(barracks0.isOccupied());
        assertTrue(barracks1.isOccupied());
    }
}
