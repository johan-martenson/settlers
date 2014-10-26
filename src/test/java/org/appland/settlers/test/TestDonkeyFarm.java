/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Donkey;
import org.appland.settlers.model.DonkeyBreeder;
import org.appland.settlers.model.DonkeyFarm;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
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
public class TestDonkeyFarm {

    @Test
    public void testUnfinishedDonkeyFarmNeedsNoDonkeyBreeder() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place donkey farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new DonkeyFarm(), point0);

        assertTrue(farm.underConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedDonkeyFarmNeedsDonkeyBreeder() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place donkey farm */
        Point point0 = new Point(10, 6);
        Building farm = map.placeBuilding(new DonkeyFarm(), point0);

        Utils.constructHouse(farm, map);
        
        assertTrue(farm.ready());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testDonkeyBreederIsAssignedToFinishedDonkeyFarm() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        /* Finish the donkey farm */
        Utils.constructHouse(farm, map);
        
        /* Fast forward so the headquarter dispatches a courier and a donkey breeder */
        Utils.fastForward(20, map);

        /* Verify that there was a donkey breeder added */
        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), DonkeyBreeder.class);
    }

    @Test
    public void testDonkeyBreederRestsInDonkeyFarmThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);
        
        /* Run the game logic 99 times and make sure the donkey breeder stays in the donkey farm */
        int i;
        for (i = 0; i < 99; i++) {
            assertTrue(donkeyBreeder.isInsideBuilding());
            map.stepTime();
        }
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Step once and make sure the donkey breedre goes out of the donkey farm */
        map.stepTime();        
        
        assertFalse(donkeyBreeder.isInsideBuilding());
    }

    @Test
    public void testDonkeyBreederFeedsTheDonkeysWhenItHasResources() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Deliver wheat and donkey to the farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);
        
        donkeyFarm.putCargo(wheatCargo);
        donkeyFarm.putCargo(waterCargo);
        
        /* Occupy the donkey farm with a donkey breeder */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Step once and make sure the donkey breeder goes out of the farm */
        map.stepTime();        
        
        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());
        
        /* Let the donkey breeder reach the spot and start to feed the donkeys */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);
        
        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());
        
        int i;
        for (i = 0; i < 19; i++) {
            assertTrue(donkeyBreeder.isFeeding());
            map.stepTime();
        }

        assertTrue(donkeyBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();
        
        /* Verify that the donkey breeder stopped feeding */
        assertFalse(donkeyBreeder.isFeeding());
        assertNull(donkeyBreeder.getCargo());
    }

    @Test
    public void testDonkeyBreederReturnsAfterFeeding() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Wait for the donkey breeder to rest */
        Utils.fastForward(99, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Step once to let the donkey breeder go out to plant */
        map.stepTime();        
        
        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());
        
        /* Let the donkey breeder reach the intended spot and start to feed */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);
        
        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());
        
        /* Wait for the donkey breeder to feed */
        Utils.fastForward(19, map);
        
        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();
        
        /* Verify that the donkey breeder stopped feeding and is walking back to the farm */
        assertFalse(donkeyBreeder.isFeeding());
        assertTrue(donkeyBreeder.isTraveling());
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm.getPosition());
        assertTrue(donkeyBreeder.getPlannedPath().contains(donkeyFarm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);
        
        assertTrue(donkeyBreeder.isArrived());        
        assertTrue(donkeyBreeder.isInsideBuilding());
    }

    @Test
    public void testDonkeyWalksToStorageByItself() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);
        
        Utils.constructHouse(donkeyFarm, map);
        
        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Step once and to let the donkey breeder go out to feed */
        map.stepTime();        
        
        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();
        
        assertTrue(donkeyBreeder.isTraveling());
        
        /* Let the donkey breeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);
        
        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());
        
        /* Wait for the donkey breeder to feed the donkeys */
        Utils.fastForward(19, map);
        
        assertTrue(donkeyBreeder.isFeeding());
        
        map.stepTime();
        
        /* DonkeyBreeder is walking back to farm without carrying a cargo */
        assertFalse(donkeyBreeder.isFeeding());
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm.getPosition());
        assertNull(donkeyBreeder.getCargo());
        
        /* Let the donkey breeder reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Wait for the donkey breeder to prepare the donkey */
        Utils.fastForward(20, map);
        
        /* Verify that the donkey walks to the storage by itself and the donkey 
           breeder stays in the farm */
        int amount = map.getAllWorkers().size();
        
        map.stepTime();
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(map.getAllWorkers().size(), amount + 1);
        assertNull(donkeyBreeder.getCargo());

        Donkey donkey = null;
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Donkey && w.getTarget().equals(hq.getPosition())) {
                donkey = (Donkey)w;
                
                break;
            }
        }

        assertNotNull(donkey);
        assertEquals(donkey.getTarget(), hq.getPosition());
    
        /* Verify that the donkey walks to the headquarter */
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, hq.getPosition());
        
        assertEquals(donkey.getPosition(), hq.getPosition());
    }

    @Test
    public void testDonkeyFarmWithoutDonkeyBreederProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm0, map);
        
        /* Verify that the farm does not produce any donkeys */
        boolean newDonkeyFound = false;
        
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;
                    
                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }
        
            map.stepTime();
        }
    
        assertFalse(newDonkeyFound);
    }

    @Test
    public void testDonkeyFarmWithoutConnectedStorageDoesNotProduce() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(map), donkeyFarm0, map);

        /* Deliver material to the donkey farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        donkeyFarm0.putCargo(wheatCargo);
        donkeyFarm0.putCargo(wheatCargo);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(waterCargo);
        
        /* Let the donkey breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the donkey breeder to produce a new donkey */
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }

            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertFalse(newDonkeyFound);
    }

    @Test
    public void testDonkeyBreederGoesBackToStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point26);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(map), donkeyFarm0, map);
        
        /* Destroy the donkey farm */
        Worker ww = donkeyFarm0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(DONKEY_BREEDER);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the donkey breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederGoesBackOnToStorageOnRoadsIfPossibleWhenDonkeyFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point26);

        /* Connect the donkey farm with the headquarter */
        map.placeAutoSelectedRoad(donkeyFarm0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Occupy the donkey farm */
        Utils.occupyBuilding(new DonkeyBreeder(map), donkeyFarm0, map);
        
        /* Destroy the donkey farm */
        Worker ww = donkeyFarm0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

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
    public void testDonkeyBreederWithoutResourcesProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Utils.constructHouse(donkeyFarm, map);

        /* Occupy the donkey farm with a donkey breeder */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Verify that the donkey breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertEquals(donkeyBreeder.getCargo(), null);
            
            map.stepTime();
        }
    }

    @Test
    public void testDonkeyBreederWithoutResourcesStaysInHouse() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Utils.constructHouse(donkeyFarm, map);

        /* Occupy the donkey farm with a donkey breeder */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Verify that the donkey breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertTrue(donkeyBreeder.isInsideBuilding());
            
            map.stepTime();
        }
    }

    @Test
    public void testDonkeyBreederFeedsDonkeysWithWaterAndWheat() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);
        
        Utils.constructHouse(donkeyFarm, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Let the donkey breeder rest */
        Utils.fastForward(99, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Step once and to let the donkey breeder go out to feed */
        map.stepTime();        
        
        assertFalse(donkeyBreeder.isInsideBuilding());

        Point point = donkeyBreeder.getTarget();
        
        assertTrue(donkeyBreeder.isTraveling());
        
        /* Let the donkey breeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);
        
        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());
        
        /* Wait for the donkey breeder to feed the donkeys */
        Utils.fastForward(19, map);
        
        assertTrue(donkeyBreeder.isFeeding());
        
        map.stepTime();
        
        /* Verify that the donkey breeder is done feeding and has consumed the water and wheat */
        assertFalse(donkeyBreeder.isFeeding());
        assertEquals(donkeyFarm.getAmount(WATER), 0);
        assertEquals(donkeyFarm.getAmount(WHEAT), 0);
    }

    @Test
    public void testDestroyedDonkeyFarmIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point26);

        /* Connect the donkey farm with the headquarter */
        map.placeAutoSelectedRoad(donkeyFarm0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Destroy the donkey farm */
        donkeyFarm0.tearDown();

        assertTrue(donkeyFarm0.burningDown());

        /* Wait for the donkey farm to stop burning */
        Utils.fastForward(50, map);
        
        assertTrue(donkeyFarm0.destroyed());
        
        /* Wait for the donkey farm to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), donkeyFarm0);
            
            map.stepTime();
        }
        
        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(donkeyFarm0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point26);
        
        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
        
        map.removeFlag(donkeyFarm0.getFlag());

        assertNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point25);

        /* Placing donkey farm */
        Point point26 = new Point(8, 8);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point26);
        
        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
        
        donkeyFarm0.tearDown();

        assertNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInDonkeyFarmCanBeStopped() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);
        
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        /* Place donkey farm */
        Point point1 = new Point(8, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point1);
        
        /* Connect the donkey farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);
        
        /* Assign a worker to the donkey farm */
        DonkeyBreeder ww = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(ww, donkeyFarm0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    System.out.println("TARGET " + w.getTarget());
                    System.out.println(w);
                    
                    break;
                }

            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertTrue(newDonkeyFound);

        /* Wait for the new donkey to walk away from the donkey farm */
        Utils.fastForward(20, map);
        
        /* Stop production and verify that no donkey is produced */
        donkeyFarm0.stopProduction();
        
        assertFalse(donkeyFarm0.isProductionEnabled());
        
        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;

                    break;
                }

            }

            if (newDonkeyFound) {
                break;
            }

            map.stepTime();
        }

        assertFalse(newDonkeyFound);
    }

    @Test
    public void testProductionInDonkeyFarmCanBeResumed() throws Exception {

        /* Create game map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point1 = new Point(8, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point1);

        /* Connect the donkey farm and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);
        
        /* Finish the donkey farm */
        Utils.constructHouse(donkeyFarm0, map);
        
        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(waterCargo);

        donkeyFarm0.putCargo(wheatCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Assign a worker to the donkey farm */
        DonkeyBreeder ww = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(ww, donkeyFarm0, map);
        
        assertTrue(ww.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);
        
        /* Wait for the donkey breeder to produce donkey */
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;
                    
                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }
            
            map.stepTime();
        }

        assertTrue(newDonkeyFound);

        /* Wait for the new donkey to walk away from the donkey farm */
        Utils.fastForward(20, map);
        
        /* Stop production */
        donkeyFarm0.stopProduction();

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;
                    
                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }
            
            map.stepTime();
        }

        assertFalse(newDonkeyFound);

        /* Resume production and verify that the donkey farm produces donkey again */
        donkeyFarm0.resumeProduction();

        assertTrue(donkeyFarm0.isProductionEnabled());

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    newDonkeyFound = true;
                    
                    break;
                }
            }

            if (newDonkeyFound) {
                break;
            }
            
            map.stepTime();
        }

        assertTrue(newDonkeyFound);
    }

    @Test
    public void testDonkeyBreederCarriesNoCargo() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);

        Utils.constructHouse(donkeyFarm, map);

        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());

        /* Verify that the donkey breeder does not pick up any cargo */
        for (int i = 0; i < 500; i++) {
            assertNull(donkeyBreeder.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyWalksToStorageOnExistingRoads() throws Exception {
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(), point0);

        /* Place donkey farm */
        Point point3 = new Point(10, 6);
        Building donkeyFarm0 = map.placeBuilding(new DonkeyFarm(), point3);

        Point point4 = new Point(11, 5);
        Point point5 = new Point(10, 4);
        Point point6 = new Point(9, 3);
        Point point7 = new Point(7, 3);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(point4, point5, point6, point7, point8);
        
        Utils.constructHouse(donkeyFarm0, map);
        
        /* Deliver resources to the donkey farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        /* Assign a donkey breeder to the farm */
        DonkeyBreeder donkeyBreeder = new DonkeyBreeder(map);
        
        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0, map);
        
        assertTrue(donkeyBreeder.isInsideBuilding());
        
        /* Wait for the donkey farm to create a donkey */
        Donkey donkey = null;

        for (int i = 0; i < 500; i++) {
            for (Worker w : map.getAllWorkers()) {
                if (w instanceof Donkey && w.getPosition().equals(donkeyFarm0.getPosition())) {
                    donkey = (Donkey)w;

                    break;
                }

            }

            if (donkey != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(donkey);

        /* Verify that the donkey walks to the storage on existing roads */
        Point previous = null;
        
        for (Point p : donkey.getPlannedPath()) {
            if (previous == null) {
                previous = p;

                continue;
            }

            if (!map.isFlagAtPoint(p)) {
                continue;
            }

            assertNotNull(map.getRoad(previous, p));

            previous = p;
        }
    }
}
