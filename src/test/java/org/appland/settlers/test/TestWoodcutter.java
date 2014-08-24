/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Building.ConstructionState;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructSmallHouse;
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
public class TestWoodcutter {
    
    @Test
    public void testUnfinishedWoodcutterNeedsNoWoodcutter() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        assertTrue(wc.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);

        assertFalse(wc.needsWorker());
    }

    @Test
    public void testFinishedWoodcutterNeedsWoodcutterWorker() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        Utils.constructSmallHouse(wc);
        
        assertTrue(wc.needsWorker());
    }
    
    @Test
    public void testWoodcutterIsAssignedToFinishedHouse() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the woodcutter */
        Utils.constructSmallHouse(wc);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        boolean foundWoodcutter = false;
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof WoodcutterWorker) {
                foundWoodcutter = true;
            }
        }
        
        assertTrue(foundWoodcutter);    
    }
    
    @Test
    public void testOnlyOneWoodcutterIsAssignedToHouse() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Run game logic twice, once to place courier and once to place forester */
        Utils.fastForward(2, map);

        int nrWorkers = map.getAllWorkers().size();

        /* Keep running the gameloop and make sure no more workers are allocated */
        int i;
        for (i = 0; i < 20; i++) {
            Utils.fastForward(10, map);
        }

        assertTrue(map.getAllWorkers().size() == nrWorkers);
    }

    @Test
    public void testArrivedWoodcutterRestsInHutAndThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */        
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(map), wc, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Run the game logic 99 times and make sure the forester stays in the hut */
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(wcWorker.isInsideBuilding());
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
    }
    
    @Test
    public void testWoodcutterFindsSpotToCutDownTree() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        /* Place and grow tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(map), wc, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Wait for woodcutter worker to leave the hut */
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();
        assertNotNull(point);
        
        assertEquals(point, point2);
        assertTrue(map.isTreeAtPoint(point));
        assertTrue(wcWorker.isTraveling());
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
    }

    @Test
    public void testWoodcutterReachesPointToCutDownTree() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        /* Place and grow tree */
        Point point2 = new Point(12, 4);        
        Tree tree = map.placeTree(point2);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(map), wc, map);

        
        /* Wait for woodcutter worker to rest */
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(wcWorker.getTarget(), point2);
        assertTrue(wcWorker.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertEquals(wcWorker.getPosition(), point);
        assertFalse(wcWorker.isTraveling());
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));
    }

    @Test
    public void testWoodcutterCutsDownTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        Utils.occupyBuilding(wcWorker, wc, map);


        /* Wait for the woodcutter to rest */        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());
        
        /* Wait for the woodcutter to finish cutting the tree */
        int i;
        for (i = 0; i < 49; i++) {
            assertTrue(wcWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));
            map.stepTime();
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
    }

    @Test
    public void testWoodcutterReturnsAndStoresWoodAsCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point3 = new Point(6, 4);
        Building hq = map.placeBuilding(new Headquarter(), point3);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);        

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        
        Point point4 = new Point(9, 3);
        
        Road road0 = map.placeRoad(hq.getFlag().getPosition(), point4, wc.getFlag().getPosition());
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);

        Utils.occupyBuilding(wcWorker, wc, map);

        
        /* Wait for the woodcutter to rest */        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);
        
        /* The woodcutter has cut down the tree and goes back via the flag*/
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getPosition());
                
        assertTrue(wcWorker.isInsideBuilding());
        assertNotNull(wcWorker.getCargo());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Let the woodcuttter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getFlag().getPosition());
        
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Cargo cargo = wc.getFlag().getStackedCargo().get(0);
        
        assertEquals(cargo.getTarget(), hq);
        
        /* Let the woodcutter go back to the hut */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        map.stepTime();
        
        /* Verify that the woodcutter remains in the hut */
        assertTrue(wcWorker.isInsideBuilding());
        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
    }
    
        @Test
    public void testWoodCargoIsCorrect() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point3 = new Point(6, 4);
        Building hq = map.placeBuilding(new Headquarter(), point3);
        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place forester hut (is this needed for the test?) */
        Point point0 = new Point(14, 4);
        Building hut = map.placeBuilding(new ForesterHut(), point0);

        /* Place woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        
        Point point4 = new Point(2, 4);
        Building hut2 = map.placeBuilding(new ForesterHut(), point4);
        
        Point point5 = new Point(9, 3);
        
        Road road0 = map.placeRoad(hq.getFlag().getPosition(), point5, wc.getFlag().getPosition());
        Road road1 = map.placeAutoSelectedRoad(hut.getFlag(), wc.getFlag());
        Road road2 = map.placeAutoSelectedRoad(hut2.getFlag(), hq.getFlag());
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        constructSmallHouse(hut);
        constructSmallHouse(hut2);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        Utils.occupyBuilding(wcWorker, wc, map);

        
        /* Wait for the woodcutter to rest */        
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);
        
        /* The woodcutter has cut down the tree and goes back via the flag*/
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getPosition());
        
        assertTrue(wcWorker.isInsideBuilding());
        assertNotNull(wcWorker.getCargo());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Let the woodcuttter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getFlag().getPosition());
        
        /* Verify that the cargo is setup correctly */
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Cargo cargo = wc.getFlag().getStackedCargo().get(0);
        
        assertEquals(cargo.getTarget(), hq);

        Road wcToHqRoad = map.getRoad(wc.getFlag().getPosition(), hq.getFlag().getPosition());

        assertEquals(cargo.getNextStep(), wcToHqRoad.getWayPoints().get(1));
    }
    
    @Test
    public void testWoodcutterHutWithoutTreesProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        /* Construct the forester hut */
        
        constructSmallHouse(wc);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);

        Utils.occupyBuilding(wcWorker, wc, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        assertNull(wcWorker.getCargo());

        int i;
        for (i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(wcWorker.getCargo());
        }
    }

    @Test
    public void testWoodcutterStaysInHouseWhenNoTreeIsAvailable() {
        // TODO
    }

    @Test
    public void testWoodcutterDoesNotCutSmallOrMediumTrees() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        /* Construct the woodcutter hut */
        constructSmallHouse(wc);
        
        /* Place the woodcutter */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);

        Utils.occupyBuilding(wcWorker, wc, map);
        
        /* Run the game logic 99 times and make sure the forester stays in the hut */

        int i;
        for (i = 0; i < 9; i++) {
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        assertTrue(tree.getSize() == Size.SMALL);
        
        /* Step once and make sure the forester stays in the hut */
        map.stepTime();
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Grow tree to medium*/
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == MEDIUM) {
                break;
            }
        }

        assertTrue(tree.getSize() == MEDIUM);

        /* Step once and make sure the forester stays in the hut */
        
        map.stepTime();
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Grow the tree to large */
        Utils.fastForwardUntilTreeIsGrown(tree, map);
        
        /* Step time and make sure the forester leaves the hut */
        
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
    }
    
    @Test
    public void testWoodcutterGoesOutToCutTreesSeveralTimes() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point3 = new Point(6, 4);
        Building hq = map.placeBuilding(new Headquarter(), point3);

        /* Plant and grow trees */
        Point point2 = new Point(12, 4);
        Tree tree0 = map.placeTree(point2);

        Point point5 = new Point(11, 5);
        Tree tree1 = map.placeTree(point5);
        
        Utils.fastForwardUntilTreeIsGrown(tree0, map);
        
        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        
        Point point4 = new Point(9, 3);
        
        Road road0 = map.placeRoad(hq.getFlag().getPosition(), point4, wc.getFlag().getPosition());
        

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);

        Utils.occupyBuilding(wcWorker, wc, map);
        
        /* Wait for the woodcutter worker to leave the hut */
        Utils.fastForward(100, map);
        
        assertFalse(wcWorker.isInsideBuilding());    

        assertTrue(wcWorker.isTraveling());
        assertTrue(wcWorker.getPlannedPath().contains(wc.getFlag().getPosition()));

        /* Let the woodcutter reach the tree */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForward(50, map);
        
        /* The woodcutter has cut down the tree and goes back to the hut */
        assertFalse(wcWorker.isCuttingTree());
        
        assertEquals(wcWorker.getTarget(), wc.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getPosition());

        /* Woodcutter enters building but does not store the cargo yet */
        assertTrue(wcWorker.isInsideBuilding());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        assertEquals(wc.getPosition(), wc.getPosition());
        assertNotNull(wcWorker.getCargo());
        
        /* Woodcutter leaves the building and puts the cargo on the building's flag */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Let the woodcuttter reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, wc.getFlag().getPosition());
        
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Cargo cargo = wc.getFlag().getStackedCargo().get(0);
        
        assertEquals(cargo.getTarget(), hq);
        
        /* Let the woodcutter go back to the hut */
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isInsideBuilding());
        
        /* Let the woodcutter rest */
        Utils.fastForward(99, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Verify that the woodcutter goes out again */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
        
    }

    @Test
    public void testPositionIsCorrectWhenWoodcutterEntersHut() throws Exception {
        GameMap map = new GameMap(20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the woodcutter */
        Utils.constructSmallHouse(wc);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);
        
        WoodcutterWorker wcWorker = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof WoodcutterWorker) {
                wcWorker = (WoodcutterWorker)w;
            }
        }
    
        assertNotNull(wcWorker);
        assertEquals(wcWorker.getTarget(), wc.getPosition());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertEquals(wcWorker.getPosition(), wc.getPosition());
    }
}
