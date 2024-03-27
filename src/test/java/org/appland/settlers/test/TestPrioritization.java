/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.actors.SawmillWorker;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.actors.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author johan
 */
public class TestPrioritization {

    @Test
    public void testMinesGetEqualAmountsOfFood() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(20, 10);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 4);

        assertEquals((int)breadAllocation.get(goldMine0), 2);
        assertEquals((int)breadAllocation.get(ironMine0), 2);
        assertEquals((int)breadAllocation.get(coalMine0), 2);
        assertEquals((int)breadAllocation.get(graniteMine0), 2);
    }

    @Test
    public void testOnlyGoldMineGetsFood() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.LARGE, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(20, 10);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 1);
        player0.setFoodQuota(IronMine.class, 0);
        player0.setFoodQuota(CoalMine.class, 0);
        player0.setFoodQuota(GraniteMine.class, 0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 1);

        assertEquals((int)breadAllocation.get(goldMine0), 8);
    }

    @Test
    public void testOnlyIronMineGetsFood() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.LARGE, map);

        Point point2 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(20, 10);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 0);
        player0.setFoodQuota(IronMine.class, 1);
        player0.setFoodQuota(CoalMine.class, 0);
        player0.setFoodQuota(GraniteMine.class, 0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 1);

        assertEquals((int)breadAllocation.get(ironMine0), 8);
    }

    @Test
    public void testOnlyCoalMineGetsFood() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.LARGE, map);

        Point point3 = new Point(20, 10);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 0);
        player0.setFoodQuota(IronMine.class, 0);
        player0.setFoodQuota(CoalMine.class, 1);
        player0.setFoodQuota(GraniteMine.class, 0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 1);

        assertEquals((int)breadAllocation.get(coalMine0), 8);
    }

    @Test
    public void testOnlyGraniteMineGetsFood() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(5, 9);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(9, 15);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(9, 5);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(25, 11);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.LARGE, map);

        /* Place headquarter */
        Point point21 = new Point(20, 10);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 0);
        player0.setFoodQuota(IronMine.class, 0);
        player0.setFoodQuota(CoalMine.class, 0);
        player0.setFoodQuota(GraniteMine.class, 1);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 1);

        assertEquals((int)breadAllocation.get(graniteMine0), 8);
    }

    @Test
    public void testOtherMinesGetFoodWithCoalMissing() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point3 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 6) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 3);

        assertEquals((int)breadAllocation.get(goldMine0), 2);
        assertEquals((int)breadAllocation.get(ironMine0), 2);
        assertEquals((int)breadAllocation.get(graniteMine0), 2);
    }

    @Test
    public void testOtherMinesGetFoodWithCoalMineNotReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(20, 10);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines except for the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Set the quota to even distribution */
        player0.setFoodQuota(GoldMine.class, 1);
        player0.setFoodQuota(IronMine.class, 1);
        player0.setFoodQuota(CoalMine.class, 1);
        player0.setFoodQuota(GraniteMine.class, 1);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Make sure the headquarter has no planks or stone so the coal mine will not be constructed */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 4);

        assertEquals((int)breadAllocation.get(goldMine0), 2);
        assertEquals((int)breadAllocation.get(ironMine0), 2);
        assertEquals((int)breadAllocation.get(coalMine0), 2);
        assertEquals((int)breadAllocation.get(graniteMine0), 2);
    }

    @Test
    public void testOtherMinesGetFoodWithFullyStockedCoalMine() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(10, 14);
        Utils.surroundPointWithMinableMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(10, 6);
        Utils.surroundPointWithMinableMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(16, 14);
        Utils.surroundPointWithMinableMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mine */
        assertTrue(map.isAvailableMinePoint(player0, point0));
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Place iron mine */
        assertTrue(map.isAvailableMinePoint(player0, point1));
        Building ironMine0 = map.placeBuilding(new IronMine(player0), point1);

        /* Place coal mine */
        assertTrue(map.isAvailableMinePoint(player0, point2));
        Building coalMine0 = map.placeBuilding(new CoalMine(player0), point2);

        /* Place granite mine */
        assertTrue(map.isAvailableMinePoint(player0, point3));
        Building graniteMine0 = map.placeBuilding(new GraniteMine(player0), point3);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);
        Utils.constructHouse(ironMine0);
        Utils.constructHouse(coalMine0);
        Utils.constructHouse(graniteMine0);

        /* Occupy the mines except for the coal mine */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0);

        /* Set the quota to even distribution */
        player0.setFoodQuota(GoldMine.class, 1);
        player0.setFoodQuota(IronMine.class, 1);
        player0.setFoodQuota(CoalMine.class, 1);
        player0.setFoodQuota(GraniteMine.class, 1);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Make sure the headquarter has no miners so the coal mine will not be occupied */
        Utils.adjustInventoryTo(headquarter0, MINER, 0);

        /* Fill the stock in the coal mine so it doesn't need anything */
        Utils.deliverCargos(coalMine0, FISH, 2);
        Utils.deliverCargos(coalMine0, BREAD, 2);
        Utils.deliverCargos(coalMine0, MEAT, 2);

        assertEquals(coalMine0.getAmount(FISH), 2);
        assertEquals(coalMine0.getAmount(BREAD), 2);
        assertEquals(coalMine0.getAmount(MEAT), 2);

        assertFalse(coalMine0.needsMaterial(FISH));
        assertFalse(coalMine0.needsMaterial(BREAD));
        assertFalse(coalMine0.needsMaterial(MEAT));

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the mines get one bread each with the four first deliveries */
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            /* Keep track of where the breads end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!breadAllocation.containsKey(targetBuilding)) {
                breadAllocation.put(targetBuilding, 0);
            }

            int amount = breadAllocation.get(targetBuilding);
            breadAllocation.put(targetBuilding, amount + 1);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Exit after four delivered breads */
            int sum = 0;

            for (Integer amountInBuilding : breadAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 6) {
                break;
            }
        }

        assertEquals(breadAllocation.keySet().size(), 3);

        assertEquals((int)breadAllocation.get(goldMine0), 2);
        assertEquals((int)breadAllocation.get(ironMine0), 2);
        assertEquals((int)breadAllocation.get(graniteMine0), 2);
    }

    @Test
    public void testFoodContinuesToBeAllocatedWhenQuotaIsReduced() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Put small mountains with ore on the map */
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMinableMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place gold mines */
        Building goldMine0 = map.placeBuilding(new GoldMine(player0), point0);

        /* Finish construction of the mines */
        Utils.constructHouse(goldMine0);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0);

        /* Set the quota for mine to give maximum food to the gold mine */
        player0.setFoodQuota(GoldMine.class, 10);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0);
        Utils.adjustInventoryTo(headquarter0, FISH, 0);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Wait for the headquarter to deliver five breads to the mine */
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5; i++) {

            /* Place one bread in the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1);

            /* Wait for the storage worker to pick up a bread cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

            assertEquals(carrier.getCargo().getTarget(), goldMine0);

            /* Wait for the bread to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(BREAD), 1);

            /* Wait for the mine to consume the bread */
            Utils.waitUntilAmountIs(target, BREAD, 0);
        }

        player0.setFoodQuota(GoldMine.class, 1);

        /* Place one bread in the headquarter */
        Utils.adjustInventoryTo(headquarter0, BREAD, 1);

        /* Wait for the storage worker to pick up a bread cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, BREAD);

        assertEquals(carrier.getCargo().getTarget(), goldMine0);

        /* Wait for the bread to reach the mine */
        Cargo cargo = carrier.getCargo();
        Building target = cargo.getTarget();

        Utils.waitForCargoToReachTarget(map, cargo);

        assertEquals(target.getAmount(BREAD), 1);
    }

    @Test
    public void testCoalConsumersGetEqualAmountsOfCoal() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(ironSmelter0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the coal consumers get one coal each with the three first deliveries */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (ironSmelter0.getAmount(IRON) == 0) {
                ironSmelter0.putCargo(new Cargo(IRON, map));
            }

            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Wait for the consumer to consume the coal */
            Utils.waitUntilAmountIs(target, COAL, 0);

            /* Exit after four delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 6) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 3);

        assertEquals((int)coalAllocation.get(mint0), 2);
        assertEquals((int)coalAllocation.get(ironSmelter0), 2);
        assertEquals((int)coalAllocation.get(armory0), 2);
    }

    @Test
    public void testOnlyIronSmelterGetsCoal() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(ironSmelter0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for coal consumers to only give coal to the iron smelter */
        player0.setCoalQuota(IronSmelter.class, 1);
        player0.setCoalQuota(Mint.class, 0);
        player0.setCoalQuota(Armory.class, 0);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Make sure the iron smelter has iron to smelt */
        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the iron smelter gets any coal */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (ironSmelter0.getAmount(IRON) == 0) {
                ironSmelter0.putCargo(new Cargo(IRON, map));
            }

            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Wait for the consumer to consume the coal */
            Utils.waitUntilAmountIs(target, COAL, 0);

            /* Exit after four delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 1);

        assertEquals((int)coalAllocation.get(ironSmelter0), 8);
    }

    @Test
    public void testOnlyMintGetsCoal() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(ironSmelter0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for coal consumers to only give coal to the iron smelter */
        player0.setCoalQuota(IronSmelter.class, 0);
        player0.setCoalQuota(Mint.class, 1);
        player0.setCoalQuota(Armory.class, 0);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Make sure the iron smelter has iron to smelt */
        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the iron smelter gets any coal */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (ironSmelter0.getAmount(IRON) == 0) {
                ironSmelter0.putCargo(new Cargo(IRON, map));
            }

            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Wait for the consumer to consume the coal */
            Utils.waitUntilAmountIs(target, COAL, 0);

            /* Exit after four delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 1);

        assertEquals((int)coalAllocation.get(mint0), 8);
    }

    @Test
    public void testOnlyArmoryGetsCoal() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(ironSmelter0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for coal consumers to only give coal to the iron smelter */
        player0.setCoalQuota(IronSmelter.class, 0);
        player0.setCoalQuota(Mint.class, 0);
        player0.setCoalQuota(Armory.class, 1);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Make sure the iron smelter has iron to smelt */
        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));
        ironSmelter0.putCargo(new Cargo(IRON, map));

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the iron smelter gets any coal */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (ironSmelter0.getAmount(IRON) == 0) {
                ironSmelter0.putCargo(new Cargo(IRON, map));
            }

            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Wait for the consumer to consume the coal */
            Utils.waitUntilAmountIs(target, COAL, 0);

            /* Exit after four delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 1);

        assertEquals((int)coalAllocation.get(armory0), 8);
    }

    @Test
    public void testOtherConsumersGetCoalWithIronSmelterMissing() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place armory */
        Point point2 = new Point(10, 14);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota for coal consumers to only give coal to the iron smelter */
        player0.setCoalQuota(IronSmelter.class, 1);
        player0.setCoalQuota(Mint.class, 1);
        player0.setCoalQuota(Armory.class, 1);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that only the iron smelter gets any coal */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Wait for the consumer to consume the coal */
            Utils.waitUntilAmountIs(target, COAL, 0);

            /* Exit after four delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 8) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 2);

        assertEquals((int)coalAllocation.get(armory0), 4);
        assertEquals((int)coalAllocation.get(mint0), 4);
    }

    @Test
    public void testOtherConsumersGetCoalWithIronSmelterNotReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 10);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place armory */
        Point point2 = new Point(20, 10);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(ironSmelter0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings except for the iron smelter */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Make sure there is no construction material in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Set the quota for coal consumers to only give coal to the iron smelter */
        player0.setCoalQuota(IronSmelter.class, 1);
        player0.setCoalQuota(Mint.class, 1);
        player0.setCoalQuota(Armory.class, 1);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get coal when the iron smelter is not yet constructed */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (ironSmelter0.getAmount(IRON) == 0) {
                ironSmelter0.putCargo(new Cargo(IRON, map));
            }

            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the consumer */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Exit after six delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 6) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 3);

        assertEquals((int)coalAllocation.get(armory0), 2);
        assertEquals((int)coalAllocation.get(mint0), 2);
        assertEquals((int)coalAllocation.get(ironSmelter0), 2);
    }

    @Test
    public void testOtherConsumersGetCoalWithFullyStockedIronSmelter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(15, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place mint */
        Point point0 = new Point(6, 10);
        Building mint0 = map.placeBuilding(new Mint(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(10, 14);
        Building ironSmelter0 = map.placeBuilding(new IronSmelter(player0), point1);

        /* Place armory */
        Point point2 = new Point(10, 6);
        Building armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Finish construction of the coal consumers */
        Utils.constructHouse(mint0);
        Utils.constructHouse(ironSmelter0);
        Utils.constructHouse(armory0);

        /* Occupy the buildings except for the iron smelter */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new Armorer(player0, map), armory0);

        /* Set the quota to even distribution */
        player0.setFoodQuota(IronSmelter.class, 1);
        player0.setFoodQuota(Mint.class, 1);
        player0.setFoodQuota(Armory.class, 1);

        /* Make sure the headquarter has no coal */
        Utils.adjustInventoryTo(headquarter0, COAL, 0);

        /* Make sure the headquarter has no iron founder so the coal mine will not be constructed */
        Utils.adjustInventoryTo(headquarter0, IRON_FOUNDER, 0);

        /* Fill the stock in the iron smelter so it doesn't need anything */
        Utils.deliverCargo(ironSmelter0, IRON);
        Utils.deliverCargo(ironSmelter0, COAL);

        assertEquals(ironSmelter0.getAmount(IRON), 1);
        assertEquals(ironSmelter0.getAmount(COAL), 1);

        assertFalse(ironSmelter0.needsMaterial(IRON));
        assertFalse(ironSmelter0.needsMaterial(COAL));

        /* Attach the coal consumers to the headquarter */
        map.placeAutoSelectedRoad(player0, mint0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironSmelter0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /* Verify that the other consumers get coal when the iron smelter is already fully stocked and does not consume its resources */
        Map<Building, Integer> coalAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Give all consumers the other materials they need for production */
            if (ironSmelter0.getAmount(IRON) == 0) {
                ironSmelter0.putCargo(new Cargo(IRON, map));
            }

            if (mint0.getAmount(GOLD) == 0) {
                mint0.putCargo(new Cargo(GOLD, map));
            }

            if (armory0.getAmount(IRON_BAR) == 0) {
                armory0.putCargo(new Cargo(IRON_BAR, map));
            }

            /* Add one coal to the headquarter */
            Utils.adjustInventoryTo(headquarter0, COAL, 1);

            /* Wait for the storage worker to pick up a coal cargo */
            Utils.fastForwardUntilWorkerCarriesCargo(map, carrier, COAL);

            /* Keep track of where the coals end up */
            Building targetBuilding = carrier.getCargo().getTarget();

            if (!coalAllocation.containsKey(targetBuilding)) {
                coalAllocation.put(targetBuilding, 0);
            }

            int amount = coalAllocation.get(targetBuilding);
            coalAllocation.put(targetBuilding, amount + 1);

            /* Wait for the coal to reach the mine */
            Cargo cargo = carrier.getCargo();
            Building target = cargo.getTarget();

            Utils.waitForCargoToReachTarget(map, cargo);

            assertEquals(target.getAmount(COAL), 1);

            /* Exit after four delivered coals */
            int sum = 0;

            for (Integer amountInBuilding : coalAllocation.values()) {
                sum += amountInBuilding;
            }

            if (sum == 4) {
                break;
            }
        }

        assertEquals(coalAllocation.keySet().size(), 2);

        assertEquals((int)coalAllocation.get(mint0), 2);
        assertEquals((int)coalAllocation.get(armory0), 2);
    }

    @Test
    public void testTransportationCategories() {
        assertEquals(TransportCategory.values().length, 17);

        assertEquals(TransportCategory.valueOf("WEAPONS"), TransportCategory.WEAPONS);
        assertEquals(TransportCategory.valueOf("BEER"), TransportCategory.BEER);
        assertEquals(TransportCategory.valueOf("COIN"), TransportCategory.COIN);
        assertEquals(TransportCategory.valueOf("FOOD"), TransportCategory.FOOD);
        assertEquals(TransportCategory.valueOf("WATER"), TransportCategory.WATER);
        assertEquals(TransportCategory.valueOf("WHEAT"), TransportCategory.WHEAT);
        assertEquals(TransportCategory.valueOf("FLOUR"), TransportCategory.FLOUR);
        assertEquals(TransportCategory.valueOf("IRON"), TransportCategory.IRON);
        assertEquals(TransportCategory.valueOf("IRON_BAR"), TransportCategory.IRON_BAR);
        assertEquals(TransportCategory.valueOf("GOLD"), TransportCategory.GOLD);
        assertEquals(TransportCategory.valueOf("COAL"), TransportCategory.COAL);
        assertEquals(TransportCategory.valueOf("PLANK"), TransportCategory.PLANK);
        assertEquals(TransportCategory.valueOf("WOOD"), TransportCategory.WOOD);
        assertEquals(TransportCategory.valueOf("STONE"), TransportCategory.STONE);
        assertEquals(TransportCategory.valueOf("TOOLS"), TransportCategory.TOOLS);
        assertEquals(TransportCategory.valueOf("PIG"), TransportCategory.PIG);
        assertEquals(TransportCategory.valueOf("BOAT"), TransportCategory.BOAT);
    }

    @Test
    public void testCourierPicksUpCargoOfHighestPriority() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(20, 14);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);

        map.placeWorker(courier, flag0);
        courier.assignToRoad(road0);

        /* Wait for the courier to rest at the middle of the road */
        assertEquals(courier.getTarget(), flag0.getPosition().left());

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        /* Place wood cargo to be delivered to the headquarter */
        Cargo woodCargo = new Cargo(WOOD, map);
        woodCargo.setPosition(point1);
        woodCargo.setTarget(headquarter0);

        flag0.putCargo(woodCargo);

        /* Place stone cargo to be delivered to the headquarter */
        Cargo stoneCargo = new Cargo(STONE, map);
        stoneCargo.setPosition(point1);
        stoneCargo.setTarget(headquarter0);

        flag0.putCargo(stoneCargo);

        /* Place plank cargo to be delivered to the headquarter */
        Cargo plankCargo = new Cargo(PLANK, map);
        plankCargo.setPosition(point1);
        plankCargo.setTarget(headquarter0);

        flag0.putCargo(plankCargo);

        /* Set stone deliveries to highest priority */
        player0.setTransportPriority(0, TransportCategory.STONE);
        player0.setTransportPriority(1, TransportCategory.WOOD);
        player0.setTransportPriority(2, TransportCategory.PLANK);

        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(flag0.getPosition().left()));
        assertNull(courier.getCargo());
        assertFalse(plankCargo.isPickupPromised());

        /* Verify that the courier picks up the stone cargo first */
        assertNull(courier.getCargo());

        map.stepTime();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point1);

        assertTrue(courier.isAt(point1));
        assertEquals(courier.getCargo(), stoneCargo);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        map.stepTime();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier picks up the wood cargo next */
        assertNull(courier.getCargo());

        map.stepTime();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point1);

        assertTrue(courier.isAt(point1));
        assertEquals(courier.getCargo(), woodCargo);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier picks up the plank cargo next */
        assertNull(courier.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point1);

        assertTrue(courier.isAt(point1));
        assertEquals(courier.getCargo(), plankCargo);

        /* Wait for the courier to deliver the cargo */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
    }

    @Test
    public void testStorageWorkerHandsOutCargoOfHighestPriority() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(20, 14);
        Mint mint0 = map.placeBuilding(new Mint(player0), point1);

        /* Place bakery */
        Point point2 = new Point(24, 14);
        Bakery bakery0 = map.placeBuilding(new Bakery(player0), point2);

        /* Place sawmill */
        Point point3 = new Point(28, 14);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point3);

        /* Place mill */
        Point point4 = new Point(24, 10);
        Mill mill0 = map.placeBuilding(new Mill(player0), point4);

        /* Place well */
        Point point5 = new Point(20, 10);
        Well well0 = map.placeBuilding(new Well(player0), point5);

        /* Construct all the buildings except the well */
        Utils.constructHouse(mint0);
        Utils.constructHouse(bakery0);
        Utils.constructHouse(sawmill0);
        Utils.constructHouse(mill0);

        /* Utils occupy the constructed buildings */
        Utils.occupyBuilding(new Minter(player0, map), mint0);
        Utils.occupyBuilding(new Baker(player0, map), bakery0);
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);
        Utils.occupyBuilding(new Miller(player0, map), mill0);

        /* Connect the buildings with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint0.getFlag());
        Road road1 = map.placeAutoSelectedRoad(player0, mint0.getFlag(), bakery0.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, bakery0.getFlag(), sawmill0.getFlag());
        Road road3 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), mill0.getFlag());
        Road road4 = map.placeAutoSelectedRoad(player0, mill0.getFlag(), well0.getFlag());

        /* Fill up the buildings so there is only space for one more resource of each type */
        Utils.deliverCargos(mint0, COAL, 5);
        Utils.deliverCargos(mint0, GOLD, 5);
        Utils.deliverCargos(bakery0, WATER, 5);
        Utils.deliverCargos(bakery0, FLOUR, 5);
        Utils.deliverCargos(sawmill0, WOOD, 5);
        Utils.deliverCargos(mill0, WHEAT, 5);

        /* Stop production in the buildings */
        mint0.stopProduction();
        bakery0.stopProduction();
        sawmill0.stopProduction();
        mill0.stopProduction();

        /* Assign couriers to the roads */
        Utils.occupyRoad(road0, map);
        Utils.occupyRoad(road1, map);
        Utils.occupyRoad(road2, map);
        Utils.occupyRoad(road3, map);
        Utils.occupyRoad(road4, map);

        /* Verify that the storage worker in the headquarter delivers cargo in the right order */
        assertTrue(mint0.needsMaterial(COAL));
        assertTrue(mint0.needsMaterial(GOLD));
        assertTrue(bakery0.needsMaterial(WATER));
        assertTrue(bakery0.needsMaterial(FLOUR));
        assertTrue(sawmill0.needsMaterial(WOOD));
        assertTrue(mill0.needsMaterial(WHEAT));
        assertTrue(well0.needsMaterial(PLANK));

        Worker storageWorker = headquarter0.getWorker();

        assertNull(storageWorker.getCargo());

        /* Set the transport priority for the materials */
        player0.setTransportPriority(0, TransportCategory.WHEAT);
        player0.setTransportPriority(1, TransportCategory.PLANK);
        player0.setTransportPriority(2, TransportCategory.COAL);
        player0.setTransportPriority(3, TransportCategory.GOLD);
        player0.setTransportPriority(4, TransportCategory.WATER);
        player0.setTransportPriority(5, TransportCategory.FLOUR);

        /* Ensure the headquarter has all the materials and enough to avoid the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, WHEAT, 20);
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, COAL, 20);
        Utils.adjustInventoryTo(headquarter0, GOLD, 20);
        Utils.adjustInventoryTo(headquarter0, WATER, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);

        /* Verify that the storage worker first delivers wheat */
        Cargo currentCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, storageWorker);

        assertNotNull(currentCargo);
        assertEquals(currentCargo.getMaterial(), WHEAT);

        /* Wait for the worker to deliver the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, storageWorker);

        /* Verify that the storage worker then delivers planks */
        currentCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, storageWorker);

        assertNotNull(currentCargo);
        assertEquals(currentCargo.getMaterial(), PLANK);

        /* Wait for the worker to deliver the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, storageWorker);

        assertFalse(mill0.needsMaterial(WHEAT));
        assertTrue(well0.needsMaterial(PLANK));

        /* Verify that the storage worker then planks until the well doesn't need them anymore */
        currentCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, storageWorker);

        assertNotNull(currentCargo);
        assertEquals(currentCargo.getMaterial(), PLANK);

        /* Wait for the worker to deliver the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, storageWorker);

        assertFalse(mill0.needsMaterial(WHEAT));
        assertFalse(well0.needsMaterial(PLANK));

        /* Verify that the storage worker then delivers coal */
        assertTrue(mint0.needsMaterial(COAL));

        currentCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, storageWorker);

        assertNotNull(currentCargo);
        assertEquals(currentCargo.getMaterial(), COAL);

        /* Wait for the worker to deliver the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, storageWorker);

        assertFalse(mill0.needsMaterial(WHEAT));
        assertFalse(well0.needsMaterial(PLANK));
        assertFalse(mint0.needsMaterial(COAL));

        /* Verify that the storage worker then delivers gold */
        assertTrue(mint0.needsMaterial(GOLD));

        currentCargo = Utils.fastForwardUntilWorkerCarriesCargo(map, storageWorker);

        assertNotNull(currentCargo);
        assertEquals(currentCargo.getMaterial(), GOLD);

        /* Wait for the worker to deliver the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, storageWorker);
    }

    @Test
    public void testReprioritizedMaterialsDoNotGetDuplicated() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Count initial number of times planks appear */
        assertEquals(Utils.countNumberElementAppearsInList(player0.getTransportPriorities(), TransportCategory.PLANK), 1);

        /* Put planks on top priority and verify that it appears only once */
        player0.setTransportPriority(0, TransportCategory.PLANK);

        assertEquals(player0.getTransportPriorities().getFirst(), TransportCategory.PLANK);
        assertNotEquals(player0.getTransportPriorities().get(10), TransportCategory.PLANK);
        assertEquals(Utils.countNumberElementAppearsInList(player0.getTransportPriorities(), TransportCategory.PLANK), 1);

        /* Put planks on medium priority and verify that it appears only once */
        player0.setTransportPriority(10, TransportCategory.PLANK);

        assertNotEquals(player0.getTransportPriorities().getFirst(), TransportCategory.PLANK);
        assertEquals(player0.getTransportPriorities().get(10), TransportCategory.PLANK);
        assertEquals(Utils.countNumberElementAppearsInList(player0.getTransportPriorities(), TransportCategory.PLANK), 1);
    }

    @Test
    public void testCannotSetTransportPriorityToNegativeNumber() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to set transport priority for an item to a negative index */
        try {
            player0.setTransportPriority(-1, TransportCategory.WOOD);

            fail();
        } catch (InvalidUserActionException e) {

        }
    }

    @Test
    public void testCannotSetTransportPriorityToTooLargeNumber() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's not possible to set for an index higher than the number of items - 1 */
        try {
            player0.setTransportPriority(17, TransportCategory.WOOD); // There are 16 items, and indexing starts at 0

            fail();
        } catch (InvalidUserActionException e) {

        }
    }

    @Test
    public void testCanSetTransportPriorityToLargeNumber() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that it's possible to set the largest number allowed (number of items - 1) */
        player0.setTransportPriority(15, TransportCategory.WOOD); // There are 16 items, and indexing starts at 0

        assertEquals(player0.getTransportPriorities().get(15), TransportCategory.WOOD);
    }
}
