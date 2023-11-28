package org.appland.settlers.test;

import org.appland.settlers.model.Brewer;
import org.appland.settlers.model.Brewery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DonkeyBreeder;
import org.appland.settlers.model.DonkeyFarm;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Mill;
import org.appland.settlers.model.Miller;
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

public class TestWheatPrioritization {

    @Test
    public void testOnlyMillGetsWheat() throws Exception {

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

        /* Place mill */
        Point point2 = new Point(10, 14);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(mill0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Miller(player0, map), mill0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 1);
        player0.setWheatQuota(DonkeyFarm.class, 0);
        player0.setWheatQuota(PigFarm.class, 0);
        player0.setWheatQuota(Brewery.class, 0);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) == 0) {
                Utils.deliverCargo(brewery, WATER);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

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

            assertEquals(target.getAmount(WHEAT), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WHEAT, 0);

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
        assertEquals((int)wheatAllocation.get(mill0), 8);
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

        /* Place mill */
        Point point2 = new Point(10, 14);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(mill0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Miller(player0, map), mill0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 0);
        player0.setWheatQuota(DonkeyFarm.class, 1);
        player0.setWheatQuota(PigFarm.class, 0);
        player0.setWheatQuota(brewery.getClass(), 0);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) == 0) {
                Utils.deliverCargo(brewery, WATER);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

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

            assertEquals(target.getAmount(WHEAT), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WHEAT, 0);

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

        /* Place mill */
        Point point2 = new Point(10, 14);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(mill0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Miller(player0, map), mill0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 0);
        player0.setWheatQuota(DonkeyFarm.class, 0);
        player0.setWheatQuota(PigFarm.class, 1);
        player0.setWheatQuota(Brewery.class, 0);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) == 0) {
                Utils.deliverCargo(brewery, WATER);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

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

            assertEquals(target.getAmount(WHEAT), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WHEAT, 0);

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

        /* Place mill */
        Point point2 = new Point(10, 14);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(6, 16);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(mill0);
        Utils.constructHouse(pigFarm0);
        Utils.constructHouse(brewery);

        /* Occupy the buildings */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new Miller(player0, map), mill0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);
        Utils.occupyBuilding(new Brewer(player0, map), brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 0);
        player0.setWheatQuota(DonkeyFarm.class, 0);
        player0.setWheatQuota(PigFarm.class, 0);
        player0.setWheatQuota(Brewery.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) == 0) {
                Utils.deliverCargo(brewery, WATER);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

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

            assertEquals(target.getAmount(WHEAT), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WHEAT, 0);

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
    public void testOtherConsumersGetWheatWithMillMissing() throws Exception {

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

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 1);
        player0.setWheatQuota(DonkeyFarm.class, 1);
        player0.setWheatQuota(PigFarm.class, 1);
        player0.setWheatQuota(Brewery.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

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
            if (donkeyFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                Utils.deliverCargo(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) == 0) {
                Utils.deliverCargo(brewery, WATER);
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

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

            assertEquals(target.getAmount(WHEAT), 1);

            /* Wait for the consumer to consume the wheat */
            Utils.waitUntilAmountIs(target, WHEAT, 0);

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
    public void testOtherConsumersGetWheatWithMillNotReady() throws Exception {

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

        /* Place mill */
        Point point2 = new Point(10, 10);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(20, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(mill0);
        Utils.constructHouse(pigFarm0);

        /* Occupy the buildings except for the mill */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Make sure there is no construction material in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 1);
        player0.setWheatQuota(DonkeyFarm.class, 1);
        player0.setWheatQuota(PigFarm.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get wheat when the mill is not yet constructed */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) == 0) {
                donkeyFarm0.putCargo(new Cargo(WATER, map));
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                pigFarm0.putCargo(new Cargo(WATER, map));
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

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

            assertEquals(target.getAmount(WHEAT), 1);

            /* Exit after six delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : wheatAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 6) {
                break;
            }
        }

        assertEquals(wheatAllocation.keySet().size(), 3);
        assertEquals((int)wheatAllocation.get(pigFarm0), 2);
        assertEquals((int)wheatAllocation.get(donkeyFarm0), 2);
        assertEquals((int)wheatAllocation.get(mill0), 2);
    }

    @Test
    public void testOtherConsumersGetWheatWithFullyStockedMill() throws Exception {

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

        /* Place mill */
        Point point2 = new Point(10, 14);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Finish construction of the wheat consumers */
        Utils.constructHouse(donkeyFarm0);
        Utils.constructHouse(mill0);
        Utils.constructHouse(pigFarm0);

        /* Occupy the buildings except for the mill */
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Set the quota to even distribution */
        player0.setFoodQuota(Mill.class, 1);
        player0.setFoodQuota(DonkeyFarm.class, 1);
        player0.setFoodQuota(PigFarm.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Make sure the headquarters has no miller so the mill will not get occupied */
        Utils.adjustInventoryTo(headquarter0, MILLER, 0);

        /* Fill the stock in the mill so it doesn't need anything */
        Utils.deliverCargo(mill0, WHEAT);

        assertEquals(mill0.getAmount(WHEAT), 1);
        assertFalse(mill0.needsMaterial(WHEAT));

        /* Attach the wheat consumers to the headquarters */
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, mill0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get wheat when the mill is already fully stocked and does not consume its resources */
        Map<Building, Integer> wheatAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) == 0) {
                donkeyFarm0.putCargo(new Cargo(WATER, map));
            }

            if (pigFarm0.getAmount(WATER) == 0) {
                pigFarm0.putCargo(new Cargo(WATER, map));
            }

            /* Add one wheat to the headquarters */
            Utils.adjustInventoryTo(headquarter0, WHEAT, 1);

            /* Wait for the storage worker to pick up a wheat cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(WHEAT), 1);

            /* Exit after four delivered wheat cargos */
            int sum = 0;

            for (Integer amountInBuilding : wheatAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 4) {
                break;
            }
        }

        assertEquals(wheatAllocation.keySet().size(), 2);
        assertEquals((int)wheatAllocation.get(donkeyFarm0), 2);
        assertEquals((int)wheatAllocation.get(pigFarm0), 2);
    }
}
