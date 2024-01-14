package org.appland.settlers.test;

import org.appland.settlers.model.Brewer;
import org.appland.settlers.model.Brewery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DonkeyBreeder;
import org.appland.settlers.model.DonkeyFarm;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Baker;
import org.appland.settlers.model.PigBreeder;
import org.appland.settlers.model.PigFarm;
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

public class TestWaterPrioritization {

    @Test
    public void testOnlyBakeryGetsWater() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place bakery */
        Point point2 = new Point(10, 14);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Baker(player0, map), bakery0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the bakery */
        player0.setWaterQuota(Bakery.class, 1);
        player0.setWaterQuota(DonkeyFarm.class, 0);
        player0.setWaterQuota(PigFarm.class, 0);
        player0.setWaterQuota(Brewery.class, 0);

        /* Make sure the headquarters has no water */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the bakery gets any water */
        Map<Building, Integer> waterAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (bakery0.getAmount(FLOUR) == 0) {
                Utils.deliverCargo(bakery0, FLOUR);
            }

            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(donkeyFarm0, WHEAT);
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(pigFarm0, WHEAT);
            }

            if (brewery.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(brewery, WHEAT);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!waterAllocation.containsKey(targetBuilding)) {
                waterAllocation.put(targetBuilding, 0);
            }

            int amount = waterAllocation.get(targetBuilding);
            waterAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(WATER), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WATER, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : waterAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(waterAllocation.keySet().size(), 1);
        assertEquals((int)waterAllocation.get(bakery0), 8);
    }

    @Test
    public void testOnlyDonkeyFarmGetsWheat() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place bakery */
        Point point2 = new Point(10, 14);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Baker(player0, map), bakery0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the bakery */
        player0.setWaterQuota(Bakery.class, 0);
        player0.setWaterQuota(DonkeyFarm.class, 1);
        player0.setWaterQuota(PigFarm.class, 0);
        player0.setWaterQuota(brewery.getClass(), 0);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the bakery gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(donkeyFarm0, WHEAT);
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(pigFarm0, WHEAT);
            }

            if (brewery.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(brewery, WHEAT);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(WATER), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WATER, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : wheatAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(wheatAllocation.keySet().size(), 1);
        assertEquals((int)wheatAllocation.get(donkeyFarm0), 8);
    }

    @Test
    public void testOnlyPigFarmGetsWheat() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place bakery */
        Point point2 = new Point(10, 14);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Baker(player0, map), bakery0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the bakery */
        player0.setWaterQuota(Bakery.class, 0);
        player0.setWaterQuota(DonkeyFarm.class, 0);
        player0.setWaterQuota(PigFarm.class, 1);
        player0.setWaterQuota(Brewery.class, 0);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the bakery gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(donkeyFarm0, WHEAT);
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(pigFarm0, WHEAT);
            }

            if (brewery.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(brewery, WHEAT);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(WATER), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WATER, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : wheatAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(wheatAllocation.keySet().size(), 1);
        assertEquals((int)wheatAllocation.get(pigFarm0), 8);
    }

    @Test
    public void testOnlyBreweryGetsWheat() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place bakery */
        Point point2 = new Point(10, 14);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Baker(player0, map), bakery0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the bakery */
        player0.setWaterQuota(Bakery.class, 0);
        player0.setWaterQuota(DonkeyFarm.class, 0);
        player0.setWaterQuota(PigFarm.class, 0);
        player0.setWaterQuota(Brewery.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the bakery gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(donkeyFarm0, WHEAT);
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(pigFarm0, WHEAT);
            }

            if (brewery.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(brewery, WHEAT);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(WATER), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WATER, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : wheatAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(wheatAllocation.keySet().size(), 1);
        assertEquals((int)wheatAllocation.get(brewery), 8);
    }

    @Test
    public void testOtherConsumersGetWheatWithBakeryMissing() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place pig farm */
        Point point2 = new Point(10, 14);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point2);

        /* Place brewery */
        Point point3 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point3);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the bakery */
        player0.setWaterQuota(Bakery.class, 1);
        player0.setWaterQuota(DonkeyFarm.class, 1);
        player0.setWaterQuota(PigFarm.class, 1);
        player0.setWaterQuota(Brewery.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the pig farm and the donkey farm get half of the delivered wheat each */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(donkeyFarm0, WHEAT);
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(pigFarm0, WHEAT);
            }

            if (brewery.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(brewery, WHEAT);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(WATER), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WATER, 0);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : wheatAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 12) {
                break;
            }
        }

        assertEquals(wheatAllocation.keySet().size(), 3);
        assertEquals((int)wheatAllocation.get(pigFarm0), 4);
        assertEquals((int)wheatAllocation.get(donkeyFarm0), 4);
        assertEquals((int)wheatAllocation.get(brewery), 4);
    }

    @Test
    public void testOtherConsumersGetWheatWithBakeryNotReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place bakery */
        Point point2 = new Point(10, 10);
        var bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(20, 10);
        var pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(pigFarm0);

        /* Occupy the buildings except for the bakery */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Make sure there is no construction material in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Set the quota for wheat consumers to only give wheat to the bakery */
        player0.setWaterQuota(Bakery.class, 1);
        player0.setWaterQuota(DonkeyFarm.class, 1);
        player0.setWaterQuota(PigFarm.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get wheat when the bakery is not yet constructed */
        Map<Building, Integer> waterAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(donkeyFarm0, WHEAT);
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                Utils.deliverCargo(pigFarm0, WHEAT);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!waterAllocation.containsKey(targetBuilding)) {
                waterAllocation.put(targetBuilding, 0);
            }

            int amount = waterAllocation.get(targetBuilding);
            waterAllocation.put(targetBuilding, amount + 1);

            /* Wait for the water to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            /* Exit after six delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : waterAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 6) {
                break;
            }
        }

        assertEquals(waterAllocation.keySet().size(), 3);
        assertEquals((int)waterAllocation.get(pigFarm0), 2);
        assertEquals((int)waterAllocation.get(donkeyFarm0), 2);
        assertEquals((int)waterAllocation.get(bakery0), 2);
    }

    @Test
    public void testOtherConsumersGetWaterWithFullyStockedBakery() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(6, 10);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place bakery */
        Point point2 = new Point(10, 14);
        Building bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Finish construction of the water consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(pigFarm0);

        /* Occupy the buildings except for the bakery */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Set the quota to even distribution */
        player0.setFoodQuota(Bakery.class, 1);
        player0.setFoodQuota(DonkeyFarm.class, 1);
        player0.setFoodQuota(PigFarm.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WATER, 0);

        /* Make sure the headquarters has no baker so the bakery will not get occupied */
        Utils.adjustInventoryTo(headquarter0, MILLER, 0);

        /* Fill the stock in the bakery so it doesn't need anything */
        Utils.deliverCargos(bakery0, WATER, 6);
        Utils.deliverCargos(bakery0, FLOUR, 6);

        assertEquals(bakery0.getAmount(WATER), 6);
        assertFalse(bakery0.needsMaterial(WATER));

        /* Stop production in the bakery */
        bakery0.stopProduction();

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, bakery0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get wheat when the bakery is already fully stocked and does not consume its resources */
        Map<Building, Integer> waterAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WHEAT) == 0) {
                donkeyFarm0.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm0.getAmount(WHEAT) == 0) {
                pigFarm0.putCargo(new Cargo(WHEAT, map));
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WATER, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WATER);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!waterAllocation.containsKey(targetBuilding)) {
                waterAllocation.put(targetBuilding, 0);
            }

            int amount = waterAllocation.get(targetBuilding);
            waterAllocation.put(targetBuilding, amount + 1);

            /* Wait for the water to reach the building */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : waterAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 4) {
                break;
            }
        }

        assertEquals(waterAllocation.keySet().size(), 2);
        assertEquals((int)waterAllocation.get(donkeyFarm0), 2);
        assertEquals((int)waterAllocation.get(pigFarm0), 2);
    }
}
