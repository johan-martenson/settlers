package org.appland.settlers.test;

import org.appland.settlers.model.Armorer;
import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Metalworker;
import org.appland.settlers.model.Metalworks;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestIronBarPrioritization {

    @Test
    public void testOnlyArmoryGetsIronBar() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 10);
        var metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 14);
        var armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(metalworks0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for wheat consumers to only give wheat to the armory */
        player0.setIronBarQuota(Armory.class, 1);
        player0.setIronBarQuota(Metalworks.class, 0);

        /* Make sure the headquarters has no iron bar */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the armory gets any iron bar */
        Map<Building, Integer> ironBAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (armory0.getAmount(COAL) == 0) {
                Utils.deliverCargo(armory0, COAL);
            }

            if (metalworks0.getAmount(PLANK) == 0) {
                Utils.deliverCargo(metalworks0, PLANK);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, IRON_BAR, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, IRON_BAR);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!ironBAllocation.containsKey(targetBuilding)) {
                ironBAllocation.put(targetBuilding, 0);
            }

            int amount = ironBAllocation.get(targetBuilding);
            ironBAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(IRON_BAR), 1);

            /* Wait for the consumer to consume the iron bar */
            Utils.waitUntilAmountIs(target, IRON_BAR, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : ironBAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(ironBAllocation.keySet().size(), 1);
        assertEquals((int)ironBAllocation.get(armory0), 8);
    }

    @Test
    public void testOnlyMetalworksGetsIronBars() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 10);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(metalworks0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for wheat consumers to only give wheat to the armory */
        player0.setIronBarQuota(Armory.class, 0);
        player0.setIronBarQuota(Metalworks.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the armory gets any wheat */
        Map<Building, Integer> ironBarAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (metalworks0.getAmount(PLANK) == 0) {
                Utils.deliverCargo(metalworks0, PLANK);
            }

            if (armory0.getAmount(COAL) == 0) {
                Utils.deliverCargo(armory0, COAL);
            }

            /* Add one iron bar to the headquarters */
            Utils.adjustInventoryTo(headquarter0, IRON_BAR, 1);

            /* Wait for the storage worker to pick up an iron bar cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, IRON_BAR);

            /* Keep track of where the iron bar cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!ironBarAllocation.containsKey(targetBuilding)) {
                ironBarAllocation.put(targetBuilding, 0);
            }

            int amount = ironBarAllocation.get(targetBuilding);
            ironBarAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(IRON_BAR), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, IRON_BAR, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : ironBarAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(ironBarAllocation.keySet().size(), 1);
        assertEquals((int)ironBarAllocation.get(metalworks0), 8);
    }

    @Test
    public void testMetalworksGetsDoubleAmountIronBars() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 10);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(metalworks0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for wheat consumers to only give wheat to the armory */
        player0.setIronBarQuota(Armory.class, 1);
        player0.setIronBarQuota(Metalworks.class, 2);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the armory gets any wheat */
        Map<Building, Integer> ironBarAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (metalworks0.getAmount(PLANK) == 0) {
                Utils.deliverCargo(metalworks0, PLANK);
            }

            if (armory0.getAmount(COAL) == 0) {
                Utils.deliverCargo(armory0, COAL);
            }

            /* Add one iron bar to the headquarters */
            Utils.adjustInventoryTo(headquarter0, IRON_BAR, 1);

            /* Wait for the storage worker to pick up an iron bar cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, IRON_BAR);

            /* Keep track of where the iron bar cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!ironBarAllocation.containsKey(targetBuilding)) {
                ironBarAllocation.put(targetBuilding, 0);
            }

            int amount = ironBarAllocation.get(targetBuilding);
            ironBarAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(IRON_BAR), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, IRON_BAR, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : ironBarAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 12) {
                break;
            }
        }

        assertEquals(ironBarAllocation.keySet().size(), 2);
        assertEquals((int)ironBarAllocation.get(armory0), 4);
        assertEquals((int)ironBarAllocation.get(metalworks0), 8);
    }

    @Test
    public void testArmoryGetsDoubleAmountIronBars() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 10);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(metalworks0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for wheat consumers to only give wheat to the armory */
        player0.setIronBarQuota(Armory.class, 2);
        player0.setIronBarQuota(Metalworks.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the armory gets any wheat */
        Map<Building, Integer> ironBarAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (metalworks0.getAmount(PLANK) == 0) {
                Utils.deliverCargo(metalworks0, PLANK);
            }

            if (armory0.getAmount(COAL) == 0) {
                Utils.deliverCargo(armory0, COAL);
            }

            /* Add one iron bar to the headquarters */
            Utils.adjustInventoryTo(headquarter0, IRON_BAR, 1);

            /* Wait for the storage worker to pick up an iron bar cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, IRON_BAR);

            /* Keep track of where the iron bar cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!ironBarAllocation.containsKey(targetBuilding)) {
                ironBarAllocation.put(targetBuilding, 0);
            }

            int amount = ironBarAllocation.get(targetBuilding);
            ironBarAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(IRON_BAR), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, IRON_BAR, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : ironBarAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 12) {
                break;
            }
        }

        assertEquals(ironBarAllocation.keySet().size(), 2);
        assertEquals((int)ironBarAllocation.get(armory0), 8);
        assertEquals((int)ironBarAllocation.get(metalworks0), 4);
    }

    @Test
    public void testMetalworksGetsIronBarWithFullyStockedArmory() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 10);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the iron bar consumers */
        Utils.constructHouse(metalworks0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings except for the armory */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Set the quota to even distribution */
        player0.setIronBarQuota(Armory.class, 1);
        player0.setIronBarQuota(Metalworks.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Make sure the headquarters has no armorer so the armory will not get occupied */
        Utils.adjustInventoryTo(headquarter0, MILLER, 0);

        /* Fill the stock in the armory so it doesn't need anything */
        Utils.deliverCargos(armory0, IRON_BAR, 6);
        Utils.deliverCargos(armory0, COAL, 6);

        assertEquals(armory0.getAmount(IRON_BAR), 6);
        assertFalse(armory0.needsMaterial(IRON_BAR));

        /* Stop production in the armory */
        armory0.stopProduction();

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get wheat when the armory is already fully stocked and does not consume its resources */
        Map<Building, Integer> ironBAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (metalworks0.getAmount(PLANK) == 0) {
                metalworks0.putCargo(new Cargo(PLANK, map));
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, IRON_BAR, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, IRON_BAR);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!ironBAllocation.containsKey(targetBuilding)) {
                ironBAllocation.put(targetBuilding, 0);
            }

            int amount = ironBAllocation.get(targetBuilding);
            ironBAllocation.put(targetBuilding, amount + 1);

            /* Wait for the iron bar to reach the building */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : ironBAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 4) {
                break;
            }
        }

        assertEquals(ironBAllocation.keySet().size(), 1);
        assertEquals((int)ironBAllocation.get(metalworks0), 4);
    }
}
