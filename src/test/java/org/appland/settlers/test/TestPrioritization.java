/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.CoalMine;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.GraniteMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.IronMine;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.MEAT;
import org.appland.settlers.model.Miner;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

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
        Utils.surroundPointWithMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(6, 14);
        Utils.surroundPointWithMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(6, 18);
        Utils.surroundPointWithMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(6, 22);
        Utils.surroundPointWithMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
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
        Utils.constructHouse(goldMine0, map);
        Utils.constructHouse(ironMine0, map);
        Utils.constructHouse(coalMine0, map);
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0, map);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0, map);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0, map);
        Utils.adjustInventoryTo(headquarter0, FISH, 0, map);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());
        
        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /*Verify that the mines get one bread each with the four first deliveries*/
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1, map);

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
            Utils.waitUntilAmountIs(map, target, BREAD, 0);

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
        Utils.surroundPointWithMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(6, 14);
        Utils.surroundPointWithMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(6, 18);
        Utils.surroundPointWithMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(6, 22);
        Utils.surroundPointWithMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
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
        Utils.constructHouse(goldMine0, map);
        Utils.constructHouse(ironMine0, map);
        Utils.constructHouse(coalMine0, map);
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0, map);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 1);
        player0.setFoodQuota(IronMine.class, 0);
        player0.setFoodQuota(CoalMine.class, 0);
        player0.setFoodQuota(GraniteMine.class, 0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0, map);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0, map);
        Utils.adjustInventoryTo(headquarter0, FISH, 0, map);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());
        
        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /*Verify that the mines get one bread each with the four first deliveries*/
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1, map);

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
            Utils.waitUntilAmountIs(map, target, BREAD, 0);

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
        Utils.surroundPointWithMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(6, 14);
        Utils.surroundPointWithMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(6, 18);
        Utils.surroundPointWithMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(6, 22);
        Utils.surroundPointWithMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
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
        Utils.constructHouse(goldMine0, map);
        Utils.constructHouse(ironMine0, map);
        Utils.constructHouse(coalMine0, map);
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0, map);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 0);
        player0.setFoodQuota(IronMine.class, 1);
        player0.setFoodQuota(CoalMine.class, 0);
        player0.setFoodQuota(GraniteMine.class, 0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0, map);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0, map);
        Utils.adjustInventoryTo(headquarter0, FISH, 0, map);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());
        
        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /*Verify that the mines get one bread each with the four first deliveries*/
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1, map);

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
            Utils.waitUntilAmountIs(map, target, BREAD, 0);

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
        Utils.surroundPointWithMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(6, 14);
        Utils.surroundPointWithMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(6, 18);
        Utils.surroundPointWithMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(6, 22);
        Utils.surroundPointWithMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
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
        Utils.constructHouse(goldMine0, map);
        Utils.constructHouse(ironMine0, map);
        Utils.constructHouse(coalMine0, map);
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0, map);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 0);
        player0.setFoodQuota(IronMine.class, 0);
        player0.setFoodQuota(CoalMine.class, 1);
        player0.setFoodQuota(GraniteMine.class, 0);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0, map);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0, map);
        Utils.adjustInventoryTo(headquarter0, FISH, 0, map);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());
        
        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /*Verify that the mines get one bread each with the four first deliveries*/
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1, map);

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
            Utils.waitUntilAmountIs(map, target, BREAD, 0);

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
        Point point0 = new Point(6, 10);
        Utils.surroundPointWithMountain(point0, map);

        Utils.putGoldAtSurroundingTiles(point0, Size.SMALL, map);

        Point point1 = new Point(6, 14);
        Utils.surroundPointWithMountain(point1, map);

        Utils.putIronAtSurroundingTiles(point1, Size.SMALL, map);

        Point point2 = new Point(6, 18);
        Utils.surroundPointWithMountain(point2, map);

        Utils.putCoalAtSurroundingTiles(point2, Size.SMALL, map);

        Point point3 = new Point(6, 22);
        Utils.surroundPointWithMountain(point3, map);

        Utils.putGraniteAtSurroundingTiles(point3, Size.SMALL, map);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
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
        Utils.constructHouse(goldMine0, map);
        Utils.constructHouse(ironMine0, map);
        Utils.constructHouse(coalMine0, map);
        Utils.constructHouse(graniteMine0, map);

        /* Occupy the mines */
        Utils.occupyBuilding(new Miner(player0, map), goldMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), ironMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), coalMine0, map);
        Utils.occupyBuilding(new Miner(player0, map), graniteMine0, map);

        /* Set the quota for mines to only give food to the coal mine */
        player0.setFoodQuota(GoldMine.class, 0);
        player0.setFoodQuota(IronMine.class, 0);
        player0.setFoodQuota(CoalMine.class, 0);
        player0.setFoodQuota(GraniteMine.class, 1);

        /* Make sure the headquarter has no food */
        Utils.adjustInventoryTo(headquarter0, BREAD, 0, map);
        Utils.adjustInventoryTo(headquarter0, MEAT, 0, map);
        Utils.adjustInventoryTo(headquarter0, FISH, 0, map);

        /* Attach the mines to the headquarter */
        map.placeAutoSelectedRoad(player0, goldMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, ironMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, coalMine0.getFlag(), headquarter0.getFlag());
        map.placeAutoSelectedRoad(player0, graniteMine0.getFlag(), headquarter0.getFlag());
        
        /* Verify that the storage worker isn't carrying something when the game starts */
        assertNull(headquarter0.getWorker().getCargo());

        /*Verify that the mines get one bread each with the four first deliveries*/
        Map<Building, Integer> breadAllocation = new HashMap<>();
        Worker carrier = headquarter0.getWorker();

        for (int i = 0; i < 5000; i++) {

            /* Add one bread to the headquarter */
            Utils.adjustInventoryTo(headquarter0, BREAD, 1, map);

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
            Utils.waitUntilAmountIs(map, target, BREAD, 0);

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
}
