/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WELL_WORKER;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Well;
import org.appland.settlers.model.WellWorker;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestWell {

    @Test
    public void testFinishedWellNeedsWorker() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place well */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well, map);

        assertTrue(well.ready());
        assertTrue(well.needsWorker());
    }
    
    @Test
    public void testWellWorkerIsAssignedToFinishedHouse() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the well */
        Utils.constructHouse(well, map);
        
        /* Run game logic twice, once to place courier and once to place well worker */
        Utils.fastForward(2, map);
        
        assertEquals(map.getWorkers().size(), 3);
        
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), WellWorker.class);
    }

    @Test
    public void testUnoccupiedWellProducesNothing() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Connect the well with the headquarter */
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(player0), point1);
        
        /* Finish the well */
        Utils.constructHouse(well, map);
        
        /* Verify that the unoccupied well produces nothing */
        int i;
        for (i = 0; i < 200; i++) {
            assertTrue(well.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testAssignedWellWorkerEntersTheWell() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Connect well with headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, hq.getFlag());
        courier.assignToRoad(road0);
        
        /* Finish the well */
        Utils.constructHouse(well, map);
        
        /* Run game logic twice, once to place courier and once to place well worker */
        Utils.fastForward(2, map);
        
        /* Get the well worker */
        Worker ww = null;
        
        for (Worker w : map.getWorkers()) {
            if (w instanceof WellWorker) {
                ww = w;
            }
        }

        /* Let the well worker reach the well */
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well.getPosition());
        
        assertNotNull(ww);
        assertTrue(ww.isInsideBuilding());
        assertEquals(well.getWorker(), ww);
    }
    
    @Test
    public void testWellWorkerRests() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the well */
        Utils.constructHouse(well, map);
        
        /* Assign a worker to the well */
        WellWorker ww = new WellWorker(player0, map);
        
        Utils.occupyBuilding(ww, well, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Verify that the worker rests first without producing anything */
        int i;
        for (i = 0; i < 100; i++) {
            assertNull(ww.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testWellWorkerProducesWater() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(player0), point1);
        
        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the well */
        Utils.constructHouse(well, map);
        
        /* Assign a worker to the well */
        WellWorker ww = new WellWorker(player0, map);
        
        Utils.occupyBuilding(ww, well, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Verify that it the worker produces water at the right time */
        int i;
        for (i = 0; i < 50; i++) {
            assertNull(ww.getCargo());
            map.stepTime();
        }
    
        assertNotNull(ww.getCargo());
        assertEquals(ww.getCargo().getMaterial(), WATER);
    }

    @Test
    public void testWellWorkerPlacesWaterCargoAtTheFlag() throws Exception {
        
        /* Create gamemap */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(8, 6);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the well */
        Utils.constructHouse(well, map);
        
        /* Assign a worker to the well */
        WellWorker ww = new WellWorker(player0, map);
        
        Utils.occupyBuilding(ww, well, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the well worker to produce water */
        Utils.fastForward(50, map);
        
        assertNotNull(ww.getCargo());
        assertEquals(ww.getTarget(), well.getFlag().getPosition());
        
        /* Let the worker reach the flag and place the cargo*/
        assertTrue(well.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well.getFlag().getPosition());

        assertFalse(well.getFlag().getStackedCargo().isEmpty());

        /* Verify that the water cargo has the right target */
        assertEquals(well.getFlag().getStackedCargo().get(0).getTarget(), hq);
        
        /* Let the worker walk back to the well */
        assertEquals(ww.getTarget(), well.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, ww);
        
        assertTrue(ww.isInsideBuilding());
    }

    @Test
    public void testWellWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0, map);

        /* Let the well worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce a new water cargo */
        Utils.fastForward(50, map);

        Worker ww = well0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the well worker puts the water cargo at the flag */
        assertEquals(ww.getTarget(), well0.getFlag().getPosition());
        assertTrue(well0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(well0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait for the worker to go back to the well */
        assertEquals(ww.getTarget(), well0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(ww.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(ww.getTarget(), well0.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well0.getFlag().getPosition());
        
        assertNull(ww.getCargo());
        assertEquals(well0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0, map);

        /* Let the well worker rest */
        Utils.fastForward(100, map);

        /* Wait for the well worker to produce a new water cargo */
        Utils.fastForward(50, map);

        Worker ww = well0.getWorker();

        assertNotNull(ww.getCargo());

        /* Verify that the well worker puts the water cargo at the flag */
        assertEquals(ww.getTarget(), well0.getFlag().getPosition());
        assertTrue(well0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well0.getFlag().getPosition());

        assertNull(ww.getCargo());
        assertFalse(well0.getFlag().getStackedCargo().isEmpty());
        
        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = well0.getFlag().getStackedCargo().get(0);
        
        Utils.fastForward(50, map);
        
        assertEquals(cargo.getPosition(), well0.getFlag().getPosition());
    
        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well0.getFlag());
    
        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);
    
        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(well0.getFlag().getPosition()));
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));
    
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
    
        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();
        
        assertEquals(courier.getTarget(), well0.getFlag().getPosition());
    
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);
    
        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());
        
        int amount = headquarter0.getAmount(WATER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());
        
        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(WATER), amount + 1);
    }

    @Test
    public void testWellWorkerGoesBackToStorageWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);

        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0, map);
        
        /* Destroy the well */
        Worker ww = well0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(WELL_WORKER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the well worker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(WELL_WORKER), amount + 1);
    }

    @Test
    public void testWellWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenWellIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Occupy the well */
        Utils.occupyBuilding(new WellWorker(player0, map), well0, map);
        
        /* Destroy the well */
        Worker ww = well0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), well0.getPosition());

        well0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point p : ww.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedWellIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Destroy the well */
        well0.tearDown();

        assertTrue(well0.burningDown());

        /* Wait for the well to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(well0.destroyed());
        
        /* Wait for the well to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), well0);
            
            map.stepTime();
        }
        
        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(well0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);
        
        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));
        
        map.removeFlag(well0.getFlag());

        assertNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing well */
        Point point26 = new Point(8, 8);
        Building well0 = map.placeBuilding(new Well(player0), point26);
        
        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));
        
        well0.tearDown();

        assertNull(map.getRoad(well0.getPosition(), well0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInWellCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place well */
        Point point1 = new Point(8, 6);
        Building well0 = map.placeBuilding(new Well(player0), point1);
        
        /* Connect the well and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the well */
        Utils.constructHouse(well0, map);
        
        /* Assign a worker to the well */
        WellWorker ww = new WellWorker(player0, map);
        
        Utils.occupyBuilding(ww, well0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the well worker to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);
        
        assertEquals(ww.getCargo().getMaterial(), WATER);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ww.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well0.getFlag().getPosition());

        /* Stop production and verify that no water is produced */
        well0.stopProduction();
        
        assertFalse(well0.isProductionEnabled());
        
        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }
    }

    @Test
    public void testProductionInWellCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);
        
        /* Place well */
        Point point1 = new Point(8, 6);
        Building well0 = map.placeBuilding(new Well(player0), point1);
        
        /* Connect the well and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);
        
        /* Finish the well */
        Utils.constructHouse(well0, map);
        
        /* Assign a worker to the well */
        WellWorker ww = new WellWorker(player0, map);
        
        Utils.occupyBuilding(ww, well0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the well worker to produce water */
        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertEquals(ww.getCargo().getMaterial(), WATER);

        /* Wait for the worker to deliver the cargo */
        assertEquals(ww.getTarget(), well0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ww, well0.getFlag().getPosition());

        /* Stop production */
        well0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(ww.getCargo());
            
            map.stepTime();
        }

        /* Resume production and verify that the well produces water again */
        well0.resumeProduction();

        assertTrue(well0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, ww);

        assertNotNull(ww.getCargo());
    }

    @Test
    public void testAssignedWellWorkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place well*/
        Point point1 = new Point(20, 14);
        Building well0 = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well0, map);
        
        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well0.getFlag());

        /* Wait for well worker to get assigned and leave the headquarter */
        List<WellWorker> workers = Utils.waitForWorkersOutsideBuilding(WellWorker.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        WellWorker worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0, map);

        /* Place well close to the new border */
        Point point4 = new Point(28, 18);
        Well well0 = map.placeBuilding(new Well(player0), point4);

        /* Finish construction of the well */
        Utils.constructHouse(well0, map);

        /* Occupy the well */
        WellWorker worker = Utils.occupyBuilding(new WellWorker(player0, map), well0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down*/
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }
}
