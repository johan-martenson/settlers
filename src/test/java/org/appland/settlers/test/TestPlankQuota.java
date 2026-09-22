package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.junit.Assert.*;

public class TestPlankQuota {

    @Test
    public void testDefaultPlankQuotasAreEqualDistribution() {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(1, player.getConstructionPlankQuota());
        assertEquals(1, player.getShipyardPlankQuota());
        assertEquals(1, player.getMetalworksPlankQuota());
    }

    @Test
    public void testCanSetConstructionPlankQuotaToMinimum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player), 20, 20);

        player.setConstructionPlankQuota(0);

        assertEquals(0, player.getConstructionPlankQuota());
    }

    @Test
    public void testCanSetConstructionPlankQuotaToMaximum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player), 20, 20);

        player.setConstructionPlankQuota(10);

        assertEquals(10, player.getConstructionPlankQuota());
    }

    @Test
    public void testCanSetShipyardPlankQuotaToMinimum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        player.setShipyardPlankQuota(0);

        assertEquals(0, player.getShipyardPlankQuota());
    }

    @Test
    public void testCanSetShipyardPlankQuotaToMaximum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        player.setShipyardPlankQuota(10);

        assertEquals(10, player.getShipyardPlankQuota());
    }

    @Test
    public void testCanSetMetalworksPlankQuotaToMinimum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        player.setMetalworksPlankQuota(0);

        assertEquals(0, player.getMetalworksPlankQuota());
    }

    @Test
    public void testCanSetMetalworksPlankQuotaToMaximum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        player.setMetalworksPlankQuota(10);

        assertEquals(10, player.getMetalworksPlankQuota());
    }

    @Test
    public void testCannotSetConstructionPlankQuotaBelowMinimum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player), 20, 20);

        player.setConstructionPlankQuota(4);

        try {
            player.setConstructionPlankQuota(-1);

            fail();
        } catch (InvalidUserActionException e) {
            assertTrue(e.getMessage().contains("below min quota"));
        }

        assertEquals(4, player.getConstructionPlankQuota());
    }

    @Test
    public void testCannotSetConstructionPlankQuotaAboveMaximum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player), 20, 20);

        player.setConstructionPlankQuota(4);

        try {
            player.setConstructionPlankQuota(11);

            fail();
        } catch (InvalidUserActionException e) {
            assertTrue(e.getMessage().contains("above max quota"));
        }

        assertEquals(4, player.getConstructionPlankQuota());
    }

    @Test
    public void testCannotSetShipyardPlankQuotaBelowMinimum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        player.setShipyardPlankQuota(4);

        try {
            player.setShipyardPlankQuota(-1);

            fail();
        } catch (InvalidUserActionException e) {
            assertTrue(e.getMessage().contains("below min quota"));
        }

        assertEquals(4, player.getShipyardPlankQuota());
    }

    @Test
    public void testCannotSetShipyardPlankQuotaAboveMaximum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        player.setShipyardPlankQuota(4);

        try {
            player.setShipyardPlankQuota(11);

            fail();
        } catch (InvalidUserActionException e) {
            assertTrue(e.getMessage().contains("above max quota"));
        }

        assertEquals(4, player.getShipyardPlankQuota());
    }

    @Test
    public void testCannotSetMetalworksPlankQuotaBelowMinimum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        player.setMetalworksPlankQuota(4);

        try {
            player.setMetalworksPlankQuota(-1);

            fail();
        } catch (InvalidUserActionException e) {
            assertTrue(e.getMessage().contains("below min quota"));
        }

        assertEquals(4, player.getMetalworksPlankQuota());
    }

    @Test
    public void testCannotSetMetalworksPlankQuotaAboveMaximum() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        player.setMetalworksPlankQuota(4);

        try {
            player.setMetalworksPlankQuota(11);

            fail();
        } catch (InvalidUserActionException e) {
            assertTrue(e.getMessage().contains("above max quota"));
        }

        assertEquals(4, player.getMetalworksPlankQuota());
    }

    @Test
    public void testChangingConstructionPlankQuotaNotifiesPlayerChangeListeners() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        player.setConstructionPlankQuota(2);

        assertEquals(1, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testChangingShipyardPlankQuotaNotifiesPlayerChangeListeners() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        player.setShipyardPlankQuota(2);

        assertEquals(1, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testChangingMetalworksPlankQuotaNotifiesPlayerChangeListeners() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        player.setMetalworksPlankQuota(2);

        assertEquals(1, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testSettingConstructionPlankQuotaToExistingValueDoesNotNotifyListeners() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player), 20, 20);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        player.setConstructionPlankQuota(player.getConstructionPlankQuota());

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testSettingShipyardPlankQuotaToExistingValueDoesNotNotifyListeners() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 20, 20);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        player.setShipyardPlankQuota(player.getShipyardPlankQuota());

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testSettingMetalworksPlankQuotaToExistingValueDoesNotNotifyListeners() throws InvalidUserActionException {
        var player = new Player("Player", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        player.setMetalworksPlankQuota(player.getMetalworksPlankQuota());

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testEqualQuotaDeliversPlanksToConstructionAndShipyard() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Ensure that there is enough planks in the headquarters to avoid triggering the tree conservation program
        Utils.adjustInventoryTo(headquarter0, PLANK, 40);

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Set equal quotas
        player0.setConstructionPlankQuota(1);
        player0.setShipyardPlankQuota(1);

        // Verify that both targets get one plank
        var allocation = new HashMap<Object, Integer>();
        var storehouseWorker = headquarter0.getWorker();

        for (int i = 0; i < 2; i++) {
            assertTrue(shipyard0.needsMaterial(PLANK));
            assertFalse(shipyard0.isUnderConstruction());
            assertTrue(shipyard0.isReady());
            assertTrue(storehouse0.needsMaterial(PLANK));
            assertTrue(storehouse0.isUnderConstruction() || storehouse0.isPlanned());
            assertFalse(player0.isTreeConservationProgramActive());

            var cargo = Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);
            allocation.merge(cargo.getTarget(), 1, Integer::sum);

            Utils.fastForwardUntilWorkerReachesPoint(storehouseWorker, storehouseWorker.getTarget());

            assertNull(storehouseWorker.getCargo());
        }

        assertEquals(1, allocation.getOrDefault(storehouse0, 0).intValue());
        assertEquals(1, allocation.getOrDefault(shipyard0, 0).intValue());
    }

    @Test
    public void testConstructionReceivesDoubleShipyardWithTwoToOneQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Set construction to get twice as many planks as shipyard production
        player0.setConstructionPlankQuota(2);
        player0.setShipyardPlankQuota(1);
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);

        // Verify the first cycle follows the quota
        var allocation = new HashMap<Object, Integer>();
        var carrier = headquarter0.getWorker();

        for (int i = 0; i < 3; i++) {
            Utils.adjustInventoryTo(headquarter0, PLANK, 11);

            var cargo = Utils.fastForwardUntilWorkerCarriesCargo(carrier, PLANK);
            allocation.merge(cargo.getTarget(), 1, Integer::sum);

            Utils.waitForCargoToReachTarget(map, cargo);
        }

        assertEquals(2, allocation.getOrDefault(storehouse0, 0).intValue());
        assertEquals(1, allocation.getOrDefault(shipyard0, 0).intValue());
    }

    @Test
    public void testShipyardReceivesDoubleConstructionWithOneToTwoQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        shipyard0.produceShips();

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Set shipyard production to get twice as many planks as construction
        player0.setConstructionPlankQuota(1);
        player0.setShipyardPlankQuota(2);
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);

        // Verify the first cycle follows the quota
        var allocation = new HashMap<Object, Integer>();
        var carrier = headquarter0.getWorker();

        for (int i = 0; i < 3; i++) {
            Utils.adjustInventoryTo(headquarter0, PLANK, 11);

            var cargo = Utils.fastForwardUntilWorkerCarriesCargo(carrier, PLANK);
            allocation.merge(cargo.getTarget(), 1, Integer::sum);

            Utils.waitForCargoToReachTarget(map, cargo);
        }

        assertEquals(1, allocation.getOrDefault(storehouse0, 0).intValue());
        assertEquals(2, allocation.getOrDefault(shipyard0, 0).intValue());
    }

    @Test
    public void testShipyardProducingBoatsUsesShipyardQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingsToBeConstructed(shipyard0);

        // Block construction and allow shipyard production
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify that the plank goes to the shipyard producing boats
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyard0, headquarter0.getWorker().getCargo().getTarget());
    }

    @Test
    public void testShipyardProducingShipsUsesShipyardQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Configure the shipyard to produce ships
        shipyard0.produceShips();

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Block construction and allow shipyard production
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify that the plank goes to the shipyard producing ships
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyard0, headquarter0.getWorker().getCargo().getTarget());
    }

    @Test
    public void testShipyardUnderConstructionUsesConstructionQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct one ready shipyard
        var readyShipyard = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, readyShipyard.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(readyShipyard);

        // Place another shipyard under construction
        var shipyardUnderConstruction = map.placeBuilding(new Shipyard(player0), new Point(6, 12));

        // Connect both shipyards to the headquarters
        map.placeAutoSelectedRoad(player0, shipyardUnderConstruction.getFlag(), headquarter0.getFlag());

        // Allow construction and block shipyard production
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify that the plank goes to the shipyard under construction
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyardUnderConstruction, headquarter0.getWorker().getCargo().getTarget());
    }

    @Test
    public void testConstructionQuotaZeroBlocksConstructionWhenShipyardNeedsPlanks() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Block construction and allow shipyard production
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify the construction site does not receive the plank
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyard0, headquarter0.getWorker().getCargo().getTarget());
        assertEquals(0, storehouse0.getAmount(PLANK));
    }

    @Test
    public void testShipyardQuotaZeroBlocksShipyardWhenConstructionNeedsPlanks() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Allow construction and block shipyard production
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify the shipyard does not receive the plank
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(storehouse0, headquarter0.getWorker().getCargo().getTarget());
        assertEquals(0, shipyard0.getAmount(PLANK));
    }

    @Test
    public void testBothPlankQuotasZeroKeepPlanksInStorage() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Set both quotas to zero
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);

        // Verify that no plank delivery starts
        var storehouseWorker = headquarter0.getWorker();

        for (int i = 0; i < 10_000; i++) {
            assertTrue(shipyard0.needsMaterial(PLANK));
            assertTrue(storehouse0.needsMaterial(PLANK));
            assertTrue(storehouseWorker.getCargo() == null || storehouseWorker.getCargo().getMaterial() != PLANK);

            map.stepTime();
        }

        assertEquals(30, headquarter0.getAmount(PLANK));
        assertEquals(0, storehouse0.getAmount(PLANK));
        assertEquals(0, shipyard0.getAmount(PLANK));
    }

    @Test
    public void testConstructionReceivesPlanksWhenNoShipyardExists() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect construction to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Set high shipyard quota but no shipyard demand
        player0.setConstructionPlankQuota(1);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 11);

        // Verify that construction receives the plank
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(storehouse0, headquarter0.getWorker().getCargo().getTarget());
    }

    @Test
    public void testShipyardReceivesPlanksWhenNoConstructionNeedsPlanks() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        // Connect shipyard to the headquarters
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Set high construction quota but no construction demand
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(1);
        Utils.adjustInventoryTo(headquarter0, PLANK, 11);

        // Verify that shipyard receives the plank
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyard0, headquarter0.getWorker().getCargo().getTarget());
    }

    @Test
    public void testConstructionReceivesPlanksWhenShipyardIsFullyStocked() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Fill the shipyard with production planks
        Utils.deliverCargos(shipyard0, PLANK, 4);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Prefer shipyard, but shipyard has no unallocated plank demand
        player0.setConstructionPlankQuota(1);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 11);

        // Verify construction receives the plank
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(storehouse0, headquarter0.getWorker().getCargo().getTarget());
    }

    @Test
    public void testNoPlankDeliveryWhenNoTargetNeedsPlanks() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        // Connect shipyard to the headquarters
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Fill the shipyard with production planks
        Utils.deliverCargos(shipyard0, PLANK, 4);

        // Pause production in the shipyard so it doesn't consume its planks and thus don't need any more delivered.
        shipyard0.stopProduction();

        // Set both quotas high but provide no plank demand
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 13);

        // Verify that no plank delivery starts
        for (int i = 0; i < 1000; i++) {
            assertFalse(shipyard0.needsMaterial(PLANK));
            assertNull(headquarter0.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(13, headquarter0.getAmount(PLANK));
    }

    @Test
    public void testDoesNotOverdeliverPlanksToConstructionWithPromisedDelivery() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place construction site that only needs one plank
        var armory0 = map.placeBuilding(new Armory(player0), new Point(6, 12));

        // Connect construction to the headquarters
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Allow construction deliveries
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 12);

        // Make the construction have one plank missing and promise delivery of that final plank
        assertEquals(0, armory0.getAmount(PLANK));

        Utils.deliverCargos(armory0, PLANK, 1);

        assertTrue(armory0.needsMaterial(PLANK));

        armory0.promiseDelivery(PLANK);

        assertFalse(armory0.needsMaterial(PLANK));

        // Verify that no plank is sent out because the construction has already been promised to get the final plank
        for (int i = 0; i < 200; i++) {
            assertTrue(headquarter0.getWorker().getCargo() == null || headquarter0.getWorker().getCargo().getMaterial() != PLANK);

            map.stepTime();
        }
    }

    @Test
    public void testDoesNotOverdeliverPlanksToShipyardWithPromisedDelivery() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        // Connect shipyard to the headquarters
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Fill the shipyard so it only needs one more plank
        Utils.deliverCargos(shipyard0, PLANK, 3);

        // Allow shipyard production deliveries
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 2);

        // Wait for the first plank to be sent
        var cargo = Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyard0, cargo.getTarget());

        // Verify that a second plank is not dispatched for the same demand
        for (int i = 0; i < 200; i++) {
            assertTrue(headquarter0.getWorker().getCargo() == null || headquarter0.getWorker().getCargo() == cargo);

            map.stepTime();
        }
    }

    @Test
    public void testUnreachableConstructionDoesNotParticipateInPlankQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place unreachable construction
        var unreachableWell = map.placeBuilding(new Well(player0), new Point(26, 10));

        // Place and construct reachable shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        // Connect only the shipyard to the headquarters
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Set high construction quota
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(1);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify that unreachable construction is ignored
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(shipyard0, headquarter0.getWorker().getCargo().getTarget());
        assertEquals(0, unreachableWell.getAmount(PLANK));
    }

    @Test
    public void testUnreachableShipyardDoesNotParticipateInPlankQuota() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place reachable construction
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Place and construct unreachable shipyard
        var unreachableShipyard = map.placeBuilding(new Shipyard(player0), new Point(20, 10));

        var road0 = map.placeAutoSelectedRoad(player0, unreachableShipyard.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(unreachableShipyard);

        // Remove the road to the shipyard to make it unreachable
        map.removeRoad(road0);

        // Connect only the construction site to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Set high shipyard quota
        player0.setConstructionPlankQuota(1);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 1);

        // Verify that unreachable shipyard is ignored
        Utils.fastForwardUntilWorkerCarriesCargo(headquarter0.getWorker(), PLANK);

        assertEquals(storehouse0, headquarter0.getWorker().getCargo().getTarget());
        assertEquals(0, unreachableShipyard.getAmount(PLANK));
    }

    @Test
    public void testQuotaCountersAreLocalToEachStorehouse() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct second storehouse
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(18, 12));

        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(storehouse0);

        // Place a shipyard site reachable from the second storehouse
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(22, 12));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), storehouse0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction reachable from headquarters
        var construction0 = map.placeBuilding(new Storehouse(player0), new Point(4, 14));
        map.placeAutoSelectedRoad(player0, construction0.getFlag(), headquarter0.getFlag());

        // Disconnect the second storehouse from the headquarters
        map.removeRoad(road0);

        // Stock both storages and set equal quotas
        player0.setConstructionPlankQuota(1);
        player0.setShipyardPlankQuota(1);
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(storehouse0, PLANK, 20);

        // Pause production in the shipyard to prevent it from consuming the planks too quickly
        shipyard0.stopProduction();

        // Verify the two storehouses do not share one global counter
        //  - the headquarters repeatedly delivers to the construction
        //  - the second storehouse repeatedly delivers to the shipyard
        assertEquals(0, shipyard0.getAmount(PLANK));
        assertEquals(0, construction0.getAmount(PLANK));

        for (int i = 0; i < 2_000; i++) {
            if (shipyard0.getAmount(PLANK) >= 2 && construction0.getAmount(PLANK) >= 2) {
                break;
            }

            map.stepTime();
        }

        assertTrue(construction0.isUnderConstruction());
        assertTrue(shipyard0.isReady());
        assertTrue(shipyard0.getAmount(PLANK) >= 2);
        assertTrue(construction0.getAmount(PLANK) >= 2);
    }

    @Test
    public void testTreeConservationAllowsPlanksToWoodcutterConstruction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place forestry recovery construction
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Activate tree conservation
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 9);
        Utils.waitForTreeConservationProgramToActivate(player0);

        // Wait for the storehouse worker to finish ongoing deliveries (if any)
        var storehouseWorker = headquarter0.getWorker();

        Utils.fastForwardUntilWorkerCarriesNoCargo(storehouseWorker);

        // Verify that tree conservation sends planks to the woodcutter
        assertTrue(woodcutter0.needsMaterial(PLANK));

        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(woodcutter0, storehouseWorker.getCargo().getTarget());
        assertEquals(0, shipyard0.getAmount(PLANK));
    }

    @Test
    public void testTreeConservationAllowsPlanksToForesterHutConstruction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Activate tree conservation
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 9);
        Utils.waitForTreeConservationProgramToActivate(player0);

        // Wait for the storehouse worker to not carry anything
        var storehouseWorker = headquarter0.getWorker();

        Utils.fastForwardUntilWorkerCarriesNoCargo(storehouseWorker);

        // Place forester hut
        var foresterHut0 = map.placeBuilding(new ForesterHut(player0), new Point(6, 12));

        map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        // Verify that tree conservation sends planks to the forester hut
        assertTrue(foresterHut0.needsMaterial(PLANK));
        assertTrue(shipyard0.needsMaterial(PLANK));
        assertTrue(shipyard0.isReady());
        assertNull(storehouseWorker.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(foresterHut0, storehouseWorker.getCargo().getTarget());
        assertEquals(0, shipyard0.getAmount(PLANK));
    }

    @Test
    public void testTreeConservationAllowsPlanksToSawmillConstruction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place forestry recovery construction
        var sawmill0 = map.placeBuilding(new Sawmill(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        // Activate tree conservation
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 9);
        Utils.waitForTreeConservationProgramToActivate(player0);

        // Wait for the storehouse worker to finish its ongoing delivery (if any)
        var storehouseWorker = headquarter0.getWorker();

        Utils.fastForwardUntilWorkerCarriesNoCargo(storehouseWorker);

        // Verify that tree conservation sends planks to the sawmill
        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(sawmill0, storehouseWorker.getCargo().getTarget());
        assertEquals(0, shipyard0.getAmount(PLANK));
    }

    @Test
    public void testTreeConservationBlocksNonForestryConstruction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place non-forestry construction
        var armory0 = map.placeBuilding(new Armory(player0), new Point(6, 12));

        // Connect construction to the headquarters
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Activate tree conservation
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);
        Utils.waitForTreeConservationProgramToActivate(player0);

        // Verify that no plank is sent to non-forestry construction
        var storehouseWorker = headquarter0.getWorker();

        for (int i = 0; i < 1000; i++) {
            assertTrue(armory0.needsMaterial(PLANK));
            assertTrue(storehouseWorker.getCargo() == null || storehouseWorker.getCargo().getMaterial() != PLANK);

            map.stepTime();
        }

        assertEquals(10, headquarter0.getAmount(PLANK));
        assertEquals(0, armory0.getAmount(PLANK));
    }

    @Test
    public void testTreeConservationBlocksReadyShipyardProduction() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Activate tree conservation
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.adjustInventoryTo(headquarter0, PLANK, 9);
        Utils.waitForTreeConservationProgramToActivate(player0);

        // Wait for the storehouse worker to finish an ongoing delivery (if any)
        var storehouseWorker = headquarter0.getWorker();

        Utils.fastForwardUntilWorkerCarriesNoCargo(storehouseWorker);

        // Verify that no plank is sent to ready shipyard production
        var amountPlanks = headquarter0.getAmount(PLANK);

        // Wait for a potential plank already in-flight to get delivered
        Utils.fastForward(200, map);

        var amountPlanksInShipyard = shipyard0.getAmount(PLANK);

        assertTrue(shipyard0.isReady());

        for (int i = 0; i < 1000; i++) {
            assertNull(storehouseWorker.getCargo());

            map.stepTime();
        }

        assertEquals(amountPlanks, headquarter0.getAmount(PLANK));
        assertEquals(amountPlanksInShipyard, shipyard0.getAmount(PLANK));
    }

    @Test
    public void testChangingPlankQuotaResetsAllocationCycle() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Start with construction-heavy quota
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 21);

        var storehouseWorker = headquarter0.getWorker();

        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(storehouse0, storehouseWorker.getCargo().getTarget());

        // Change to shipyard-heavy quota
        player0.setConstructionPlankQuota(0);
        player0.setShipyardPlankQuota(10);
        Utils.waitForCargoToReachTarget(map, storehouseWorker.getCargo());
        Utils.adjustInventoryTo(headquarter0, PLANK, 21);

        assertTrue(shipyard0.isReady());
        assertTrue(shipyard0.needsMaterial(PLANK));
        assertTrue(map.findWayWithExistingRoads(shipyard0.getPosition(), headquarter0.getPosition()).size() <
                map.findWayWithExistingRoads(shipyard0.getPosition(), storehouse0.getPosition()).size());

        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(shipyard0, storehouseWorker.getCargo().getTarget());
    }

    @Test
    public void testChangingShipyardQuotaResetsAllocationCycle() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var headquarter0 = map.placeBuilding(new Headquarter(player0), new Point(15, 9));

        // Place and construct shipyard
        var shipyard0 = map.placeBuilding(new Shipyard(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard0);

        // Place construction site
        var storehouse0 = map.placeBuilding(new Storehouse(player0), new Point(6, 12));

        // Connect both consumers to the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Start with construction-heavy quota
        player0.setConstructionPlankQuota(10);
        player0.setShipyardPlankQuota(0);
        Utils.adjustInventoryTo(headquarter0, PLANK, 21);

        var storehouseWorker = headquarter0.getWorker();

        // Verify first allocation goes to construction
        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(storehouse0, storehouseWorker.getCargo().getTarget());

        // Change only the shipyard quota
        player0.setShipyardPlankQuota(10);
        player0.setConstructionPlankQuota(0);

        Utils.waitForCargoToReachTarget(map, storehouseWorker.getCargo());
        Utils.adjustInventoryTo(headquarter0, PLANK, 21);

        assertTrue(shipyard0.isReady());
        assertTrue(shipyard0.needsMaterial(PLANK));
        assertTrue(map.findWayWithExistingRoads(shipyard0.getPosition(), headquarter0.getPosition()).size() <
                map.findWayWithExistingRoads(shipyard0.getPosition(), storehouse0.getPosition()).size());

        // Verify that the allocation cycle was reset and immediately reflects the new quota configuration
        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(shipyard0, storehouseWorker.getCargo().getTarget());

        Utils.fastForwardUntilWorkerCarriesNoCargo(storehouseWorker);

        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker, PLANK);

        assertEquals(shipyard0, storehouseWorker.getCargo().getTarget());
    }
}
