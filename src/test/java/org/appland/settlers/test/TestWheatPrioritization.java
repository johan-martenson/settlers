package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.PigFarm;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Utils.deliverCargos(mill0, WHEAT, 6);

        assertEquals(mill0.getAmount(WHEAT), 6);
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

    @Test
    public void testOnlyMillGetsWheatFromFarm() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 19);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point7 = new Point(16, 18);
        var fortress = map.placeBuilding(new Fortress(player0), point7);

        /* Connected the fortress with the headquarters and wait for it to get constructed and populated */
        Road road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        /* Place farm */
        Point point5 = new Point(24, 16);
        var farm = map.placeBuilding(new Farm(player0), point5);

        /* Place donkey farm up-right */
        Point point1 = new Point(28, 20);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place mill down-left */
        Point point2 = new Point(16, 12);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(22, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(28, 14);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Adjust inventory of the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);
        Utils.adjustInventoryTo(headquarter0, STONE, 50);
        Utils.clearInventory(headquarter0, WHEAT);

        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertTrue(headquarter0.getAmount(DONKEY_BREEDER) > 0);

        /* Connect the farm with the headquarters */
        map.placeAutoSelectedRoad(player0, farm.getFlag(), fortress.getFlag());

        /* Connect the wheat consumers with the farm */
        Road road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), farm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), farm.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), farm.getFlag());
        Road road4 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), farm.getFlag());

        /* Wait for the buildings to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 1);
        player0.setWheatQuota(DonkeyFarm.class, 0);
        player0.setWheatQuota(PigFarm.class, 0);
        player0.setWheatQuota(Brewery.class, 0);

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) < 0) {
                Utils.deliverMaxCargos(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(brewery, WATER);
            }

            /* Start production */
            farm.resumeProduction();

            /* Wait for the farmer to place one wheat cargo on its flag */
            Utils.waitForFlagToHaveCargoWaiting(map, farm.getFlag(), WHEAT);

            assertEquals(farm.getFlag().getStackedCargo().size(), 1);
            assertEquals(farm.getFlag().getStackedCargo().getFirst().getMaterial(), WHEAT);

            Cargo cargo = farm.getFlag().getStackedCargo().getFirst();

            /* Stop production */
            farm.stopProduction();

            /* Wait for the storage worker to pick up a wheat cargo */
            Worker carrier = Utils.fastForwardUntilOneOfWorkersCarriesCargo(
                    map,
                    farm.getFlag().getStackedCargo().getFirst(),
                    road1.getCourier(),
                    road2.getCourier(),
                    road3.getCourier(),
                    road4.getCourier());

            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
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
    public void testOnlyDonkeyFarmGetsWheatFromFarm() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 19);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point7 = new Point(16, 18);
        var fortress = map.placeBuilding(new Fortress(player0), point7);

        /* Connected the fortress with the headquarters and wait for it to get constructed and populated */
        Road road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        /* Place farm */
        Point point5 = new Point(24, 16);
        var farm = map.placeBuilding(new Farm(player0), point5);

        /* Place donkey farm up-right */
        Point point1 = new Point(28, 20);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place mill down-left */
        Point point2 = new Point(16, 12);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(22, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(28, 14);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Adjust inventory of the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);
        Utils.adjustInventoryTo(headquarter0, STONE, 50);
        Utils.clearInventory(headquarter0, WHEAT);

        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertTrue(headquarter0.getAmount(DONKEY_BREEDER) > 0);

        /* Connect the farm with the headquarters */
        map.placeAutoSelectedRoad(player0, farm.getFlag(), fortress.getFlag());

        /* Connect the wheat consumers with the farm */
        Road road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), farm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), farm.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), farm.getFlag());
        Road road4 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), farm.getFlag());

        /* Wait for the buildings to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 0);
        player0.setWheatQuota(DonkeyFarm.class, 1);
        player0.setWheatQuota(PigFarm.class, 0);
        player0.setWheatQuota(brewery.getClass(), 0);

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(brewery, WATER);
            }

            /* Start production again */
            farm.resumeProduction();

            /* Wait for the farm to produce another wheat and place it at its flag */
            Cargo cargo = Utils.waitForFlagToHaveCargoWaiting(map, farm.getFlag(), WHEAT);

            /* Stop production */
            farm.stopProduction();

            /* Wait for a courier worker to pick up a wheat cargo */
            Worker carrier = Utils.fastForwardUntilOneOfWorkersCarriesCargo(
                    map,
                    farm.getFlag().getStackedCargo().getFirst(),
                    road1.getCourier(),
                    road2.getCourier(),
                    road3.getCourier(),
                    road4.getCourier());

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = cargo.getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
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
    public void testOnlyPigFarmGetsWheatFromFarm() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 19);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point7 = new Point(16, 18);
        var fortress = map.placeBuilding(new Fortress(player0), point7);

        /* Connected the fortress with the headquarters and wait for it to get constructed and populated */
        Road road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        /* Place farm */
        Point point5 = new Point(24, 16);
        var farm = map.placeBuilding(new Farm(player0), point5);

        /* Place donkey farm up-right */
        Point point1 = new Point(28, 20);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place mill down-left */
        Point point2 = new Point(16, 12);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(22, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(28, 14);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Adjust inventory of the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);
        Utils.adjustInventoryTo(headquarter0, STONE, 50);
        Utils.clearInventory(headquarter0, WHEAT);

        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertTrue(headquarter0.getAmount(DONKEY_BREEDER) > 0);

        /* Connect the farm with the headquarters */
        map.placeAutoSelectedRoad(player0, farm.getFlag(), fortress.getFlag());

        /* Connect the wheat consumers with the farm */
        Road road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), farm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), farm.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), farm.getFlag());
        Road road4 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), farm.getFlag());

        /* Wait for the buildings to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 0);
        player0.setWheatQuota(DonkeyFarm.class, 0);
        player0.setWheatQuota(PigFarm.class, 1);
        player0.setWheatQuota(Brewery.class, 0);

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(brewery, WATER);
            }

            /* Start production */
            farm.resumeProduction();

            /* Wait for the farmer to place one wheat cargo on its flag */
            Utils.waitForFlagToHaveCargoWaiting(map, farm.getFlag(), WHEAT);

            assertEquals(farm.getFlag().getStackedCargo().size(), 1);
            assertEquals(farm.getFlag().getStackedCargo().getFirst().getMaterial(), WHEAT);

            Cargo cargo = farm.getFlag().getStackedCargo().getFirst();

            /* Stop production */
            farm.stopProduction();

            /* Wait for the storage worker to pick up a wheat cargo */
            Worker carrier = Utils.fastForwardUntilOneOfWorkersCarriesCargo(
                    map,
                    farm.getFlag().getStackedCargo().getFirst(),
                    road1.getCourier(),
                    road2.getCourier(),
                    road3.getCourier(),
                    road4.getCourier());

            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
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
    public void testOnlyBreweryGetsWheatFromFarm() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 19);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point7 = new Point(16, 18);
        var fortress = map.placeBuilding(new Fortress(player0), point7);

        /* Connected the fortress with the headquarters and wait for it to get constructed and populated */
        Road road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        /* Place farm */
        Point point5 = new Point(24, 16);
        var farm = map.placeBuilding(new Farm(player0), point5);

        /* Place donkey farm up-right */
        Point point1 = new Point(28, 20);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place mill down-left */
        Point point2 = new Point(16, 12);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(22, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(28, 14);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Adjust inventory of the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);
        Utils.adjustInventoryTo(headquarter0, STONE, 50);
        Utils.clearInventory(headquarter0, WHEAT);

        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertTrue(headquarter0.getAmount(DONKEY_BREEDER) > 0);

        /* Connect the farm with the headquarters */
        map.placeAutoSelectedRoad(player0, farm.getFlag(), fortress.getFlag());

        /* Connect the wheat consumers with the farm */
        Road road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), farm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), farm.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), farm.getFlag());
        Road road4 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), farm.getFlag());

        /* Wait for the buildings to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm, donkeyFarm0, mill0, pigFarm0, brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 0);
        player0.setWheatQuota(DonkeyFarm.class, 0);
        player0.setWheatQuota(PigFarm.class, 0);
        player0.setWheatQuota(Brewery.class, 1);

        /* Verify that only the mill gets any wheat */
        Map<Building, Integer> wheatAllocation = new HashMap<>();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) < 0) {
                Utils.deliverMaxCargos(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(brewery, WATER);
            }

            /* Start production */
            farm.resumeProduction();

            /* Wait for the farmer to place one wheat cargo on its flag */
            Utils.waitForFlagToHaveCargoWaiting(map, farm.getFlag(), WHEAT);

            assertEquals(farm.getFlag().getStackedCargo().size(), 1);
            assertEquals(farm.getFlag().getStackedCargo().getFirst().getMaterial(), WHEAT);

            Cargo cargo = farm.getFlag().getStackedCargo().getFirst();

            /* Stop production */
            farm.stopProduction();

            /* Wait for the storage worker to pick up a wheat cargo */
            Worker carrier = Utils.fastForwardUntilOneOfWorkersCarriesCargo(
                    map,
                    farm.getFlag().getStackedCargo().getFirst(),
                    road1.getCourier(),
                    road2.getCourier(),
                    road3.getCourier(),
                    road4.getCourier());

            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!wheatAllocation.containsKey(targetBuilding)) {
                wheatAllocation.put(targetBuilding, 0);
            }

            int amount = wheatAllocation.get(targetBuilding);
            wheatAllocation.put(targetBuilding, amount + 1);

            /* Wait for the wheat to reach the consumer */
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
    public void testOtherConsumersGetWheatWithMillMissingFromFarm() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 19);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point7 = new Point(16, 18);
        var fortress = map.placeBuilding(new Fortress(player0), point7);

        /* Connected the fortress with the headquarters and wait for it to get constructed and populated */
        Road road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        /* Place farm */
        Point point5 = new Point(24, 16);
        var farm = map.placeBuilding(new Farm(player0), point5);

        /* Place donkey farm up-right */
        Point point1 = new Point(28, 20);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place pig farm */
        Point point3 = new Point(22, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(28, 14);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Adjust inventory of the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);
        Utils.adjustInventoryTo(headquarter0, STONE, 50);
        Utils.clearInventory(headquarter0, WHEAT);

        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertTrue(headquarter0.getAmount(DONKEY_BREEDER) > 0);

        /* Connect the farm with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), fortress.getFlag());

        /* Connect the wheat consumers with the farm */
        Road road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), farm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), farm.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), farm.getFlag());

        /* Wait for the buildings to get constructed and occupied */
        Utils.waitForBuildingsToBeConstructed(farm, donkeyFarm0, pigFarm0, brewery);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm, donkeyFarm0, pigFarm0, brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 1);
        player0.setWheatQuota(DonkeyFarm.class, 1);
        player0.setWheatQuota(PigFarm.class, 1);
        player0.setWheatQuota(Brewery.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the pig farm and the donkey farm get half of the delivered wheat each */
        Map<Building, Integer> wheatAllocation = new HashMap<>();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(brewery, WATER);
            }

            /* Start production */
            farm.resumeProduction();

            /* Wait for the farmer to place one wheat cargo on its flag */
            Utils.waitForFlagToHaveCargoWaiting(map, farm.getFlag(), WHEAT);

            assertEquals(farm.getFlag().getStackedCargo().size(), 1);
            assertEquals(farm.getFlag().getStackedCargo().getFirst().getMaterial(), WHEAT);

            Cargo cargo = farm.getFlag().getStackedCargo().getFirst();

            assertNotNull(cargo);

            /* Stop production */
            farm.stopProduction();

            /* Wait for the storage worker to pick up a wheat cargo */
            assertTrue(pigFarm0.needsMaterial(WHEAT));
            assertTrue(brewery.needsMaterial(WHEAT));
            assertTrue(donkeyFarm0.needsMaterial(WHEAT));

            assertTrue(road1.getCourier().isIdle());
            assertTrue(road2.getCourier().isIdle());
            assertTrue(road3.getCourier().isIdle());
            assertTrue(farm.getFlag().getStackedCargo().contains(cargo));

            Worker carrier = Utils.fastForwardUntilOneOfWorkersCarriesCargo(
                    map,
                    cargo,
                    road1.getCourier(),
                    road2.getCourier(),
                    road3.getCourier(),
                    road1.getDonkey(),
                    road2.getDonkey(),
                    road3.getDonkey());

            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, WHEAT);

            /* Wait for the wheat to reach the consumer */
            Building target = cargo.getTarget();

            assertNotEquals(target, headquarter0);
            assertNotEquals(target, fortress);

            wheatAllocation.put(target, wheatAllocation.getOrDefault(target, 0) + 1);

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
    public void testOtherConsumersGetWheatFromFarmWithMillNotReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(9, 19);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place fortress */
        Point point7 = new Point(16, 18);
        var fortress = map.placeBuilding(new Fortress(player0), point7);

        /* Connected the fortress with the headquarters and wait for it to get constructed and populated */
        Road road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        /* Place farm */
        Point point5 = new Point(24, 16);
        var farm = map.placeBuilding(new Farm(player0), point5);

        /* Place donkey farm up-right */
        Point point1 = new Point(28, 20);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Place mill down-left */
        Point point2 = new Point(16, 12);
        Building mill0 = map.placeBuilding(new Mill(player0), point2);

        /* Place pig farm */
        Point point3 = new Point(22, 10);
        Building pigFarm0 = map.placeBuilding(new PigFarm(player0), point3);

        /* Place brewery */
        Point point4 = new Point(28, 14);
        var brewery = map.placeBuilding(new Brewery(player0), point4);

        /* Adjust inventory of the headquarters */
        Utils.clearInventory(headquarter0, WHEAT, PLANK, STONE);

        assertEquals(headquarter0.getAmount(WHEAT), 0);
        assertTrue(headquarter0.getAmount(DONKEY_BREEDER) > 0);

        /* Connect the farm with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), fortress.getFlag());

        /* Connect the wheat consumers with the farm */
        Road road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), farm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), farm.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), farm.getFlag());
        Road road4 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), farm.getFlag());

        /* Construct the buildings (but not the mill) */
        Utils.constructHouses(farm, donkeyFarm0, pigFarm0, brewery);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(farm, donkeyFarm0, pigFarm0, brewery);

        /* Set the quota for wheat consumers to only give wheat to the mill */
        player0.setWheatQuota(Mill.class, 1);
        player0.setWheatQuota(DonkeyFarm.class, 1);
        player0.setWheatQuota(PigFarm.class, 1);
        player0.setWheatQuota(Brewery.class, 1);

        /* Make sure the headquarters has no wheat */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 0);

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get wheat when the mill is not yet constructed */
        Map<Building, Integer> wheatAllocation = new HashMap<>();

        for (int i = 0; i < 6; i++) {

            /* Give all consumers the other materials they need for production */
            if (donkeyFarm0.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(donkeyFarm0, WATER);
            }

            if (pigFarm0.getAmount(WATER) < 0) {
                Utils.deliverMaxCargos(pigFarm0, WATER);
            }

            if (brewery.getAmount(WATER) < 6) {
                Utils.deliverMaxCargos(brewery, WATER);
            }

            assertTrue(donkeyFarm0.needsMaterial(WHEAT));
            assertTrue(pigFarm0.needsMaterial(WHEAT));
            assertTrue(brewery.needsMaterial(WHEAT));

            /* Start production */
            farm.resumeProduction();

            /* Wait for the farmer to place one wheat cargo on its flag */
            Utils.waitForFlagToHaveCargoWaiting(map, farm.getFlag(), WHEAT);

            assertEquals(farm.getFlag().getStackedCargo().size(), 1);
            assertEquals(farm.getFlag().getStackedCargo().getFirst().getMaterial(), WHEAT);

            Cargo cargo = farm.getFlag().getStackedCargo().getFirst();

            /* Stop production */
            farm.stopProduction();

            /* Wait for the storage worker to pick up a wheat cargo */
            assertNotEquals(farm.getFlag().getStackedCargo().size(), 0);

            Worker carrier = Utils.fastForwardUntilOneOfWorkersCarriesCargo(map, cargo, road1.getCourier(), road2.getCourier(), road3.getCourier(), road4.getCourier(), road6.getCourier());

            assertTrue(farm.getFlag().getStackedCargo().isEmpty());
            assertNotNull(carrier);
            assertNotEquals(carrier, road6.getCourier());

            /* Keep track of where the wheat cargos end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            wheatAllocation.put(targetBuilding, wheatAllocation.getOrDefault(targetBuilding, 0) + 1);

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
        assertEquals((int)wheatAllocation.get(brewery), 2);
    }

    @Test
    public void testOtherConsumersGetWheatWithFullyStockedMillFromFarm() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Utils.deliverCargos(mill0, WHEAT, 6);

        assertEquals(mill0.getAmount(WHEAT), 6);
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
