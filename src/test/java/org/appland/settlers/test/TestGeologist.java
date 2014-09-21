/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Geologist;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Sign;
import static org.appland.settlers.model.Size.LARGE;
import org.appland.settlers.model.Storage;
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
public class TestGeologist {

    @Test
    public void testGeologistCanBeCalledFromAFlag() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Call geologist from the flag */
        flag.callGeologist();
    }
    
    @Test
    public void testStorageDispatchesGeologistWhenItHasBeenCalled() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        int amountWorkers = map.getAllWorkers().size();
        
        flag.callGeologist();
        
        /* Verify that a geologist is dispatched from the headquarter */
        map.stepTime();
        
        assertEquals(map.getAllWorkers().size(), amountWorkers + 1);
        
        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Geologist.class);
    }

    @Test
    public void testGeologistGetsToFlagThenLeavesToNearbySpot() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Worker geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Verify that the geologist keeps going to a point within a radius of 3 */
        assertTrue(geologist.getPosition().distance(geologist.getTarget()) < 3);

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
    }

    @Test
    public void testGeologistDoesResearchAndPutsUpSign() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist investigates the point for the right amount of time */
        Point site = geologist.getPosition();
        
        for (int i = 0; i < 20; i++) {
            assertTrue(geologist.isInvestigating());
    
            map.stepTime();
        }
        
        assertFalse(geologist.isInvestigating());
        assertNotNull(map.getSignAtPoint(site));
    }

    @Test
    public void testGeologistGoesBackIfItCannotFindSiteToInvestigate() {
        // TODO: Implement test
    }

    @Test
    public void testGeologistWillNotInvestigateTrees() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();

        /* Fill surrounding area with trees */
        for (Point p : map.getPointsWithinRadius(flag.getPosition(), 6)) {
            try {
                map.placeTree(p);
            } catch (Exception e) {}
        }
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist investigates the road since it's the only point without a tree */
        assertTrue(map.isRoadAtPoint(geologist.getTarget()));
    }

    @Test
    public void testGeologistWillNotInvestigateStones() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();

        /* Fill surrounding area with stones */
        for (Point p : map.getPointsWithinRadius(flag.getPosition(), 6)) {
            try {
                map.placeStone(p);
            } catch (Exception e) {}
        }
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist investigates the road since it's the only point without a stone */
        assertTrue(map.isRoadAtPoint(geologist.getTarget()));
    }
    
    @Test
    public void testGeologistInvestigatesFiveSites() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertFalse(geologist.isInvestigating());
        assertFalse(geologist.getTarget().equals(flag.getPosition()));

        /* Let the geologist go to the second site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to investigate the second site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertFalse(geologist.isInvestigating());
        assertFalse(geologist.getTarget().equals(flag.getPosition()));

        /* Let the geologist go to the third site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to investigate the third site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertFalse(geologist.isInvestigating());
        assertFalse(geologist.getTarget().equals(flag.getPosition()));

        /* Let the geologist go to the fourth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to investigate the fourth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertFalse(geologist.isInvestigating());
        assertFalse(geologist.getTarget().equals(flag.getPosition()));

        /* Let the geologist go to the fifth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to investigate the fifth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertFalse(geologist.isInvestigating());
        assertEquals(geologist.getTarget(), flag.getPosition());
        
    }        
    
    @Test
    public void testGeologistFindsWaterOnGrass() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));
        
        Sign sign = map.getSignAtPoint(geologist.getPosition());
        
        assertEquals(sign.getType(), WATER);
        assertEquals(sign.getSize(), LARGE);
    }

    @Test
    public void testGeologistFindsGoldOnMountain() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 4, map);
        Utils.putMineralWithinRadius(GOLD, point1, 4, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));
        
        Sign sign = map.getSignAtPoint(geologist.getPosition());
        
        assertEquals(sign.getType(), GOLD);
        assertEquals(sign.getSize(), LARGE);
    }

    @Test
    public void testGeologistFindsCoalOnMountain() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 4, map);
        Utils.putMineralWithinRadius(COAL, point1, 4, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));
        
        Sign sign = map.getSignAtPoint(geologist.getPosition());
        
        assertEquals(sign.getType(), COAL);
        assertEquals(sign.getSize(), LARGE);
    }

    @Test
    public void testGeologistFindsIronOnMountain() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 4, map);
        Utils.putMineralWithinRadius(IRON, point1, 4, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));
        
        Sign sign = map.getSignAtPoint(geologist.getPosition());
        
        assertEquals(sign.getType(), IRON);
        assertEquals(sign.getSize(), LARGE);
    }

    @Test
    public void testGeologistFindsStoneOnMountain() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 4, map);
        Utils.putMineralWithinRadius(STONE, point1, 4, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);
        
        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));
        
        Sign sign = map.getSignAtPoint(geologist.getPosition());
        
        assertEquals(sign.getType(), STONE);
        assertEquals(sign.getSize(), LARGE);
    }

    @Test
    public void testGeologistReturnsToStorageAfterInvestigationsAreDone() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to investigate five sites */
        for (int i = 0; i < 5; i++) {

            /* Break when the geologist goes back to its flag */
            if (geologist.getTarget().equals(flag.getPosition())) {
                break;
            }
            
            /* Verify that the chosen site does not have a sign */
            assertFalse(map.isSignAtPoint(geologist.getTarget()));
            
            /* Wait for the geologist to reach the next site to investigate */
            Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

            /* Wait for the geologist to investigate the site */
            assertTrue(geologist.isInvestigating());

            Utils.fastForward(20, map);
        
            assertFalse(geologist.isInvestigating());
        }
        
        /* Wait for the geologist to reach the flag */
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());
        
        assertEquals(geologist.getTarget(), headquarter0.getPosition());
        
        /* Wait for the geologist to reach the headquarter and verify that it's correctly stored */
        int amount = headquarter0.getAmount(GEOLOGIST);
        
        Utils.fastForwardUntilWorkersReachTarget(map, geologist);
        
        assertEquals(headquarter0.getAmount(GEOLOGIST), amount + 1);
        
    }        

    @Test
    public void testGeologistAvoisSitesWithSigns() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Surround the flag with signs */
        for (Point p : map.getPointsWithinRadius(point1, 5)) {
            try {
                map.placeSign(WATER, LARGE, p);
            } catch (Exception e) {}
        }
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Verify that cannot find a place to investigate and goes back */
        assertEquals(geologist.getTarget(), headquarter0.getPosition());
    }        
    
    @Test
    public void testGeologistPlacesEmptySignWhenItFindsNothing() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Place mountain without ore */
        Utils.createMountainWithinRadius(point1, 6, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist)w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Wait for the geologist to investigate five sites */
        for (int i = 0; i < 5; i++) {

            /* Break when the geologist goes back to its flag */
            if (geologist.getTarget().equals(flag.getPosition())) {
                break;
            }
            
            Point target = geologist.getTarget();
            
            /* Verify that the chosen site does not have a sign */
            assertFalse(map.isSignAtPoint(target));
            
            /* Wait for the geologist to reach the next site to investigate */
            Utils.fastForwardUntilWorkerReachesPoint(map, geologist, target);

            /* Wait for the geologist to investigate the site */
            assertTrue(geologist.isInvestigating());

            Utils.fastForward(20, map);
        
            assertFalse(geologist.isInvestigating());
            assertTrue(map.isSignAtPoint(target));
            
            Sign sign = map.getSignAtPoint(target);
            
            assertNull(sign.getType());
            assertTrue(sign.isEmpty());
        }
    }

    @Test
    public void testDepositingGeologistIncreasesAmountOfGeologists() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = (Storage)map.placeBuilding(new Headquarter(), point0);

        /* Add a geologist to the headquarter and verify that the amount goes up*/
        int amount = headquarter0.getAmount(GEOLOGIST);
        
        headquarter0.depositWorker(new Geologist(map));
        
        assertEquals(headquarter0.getAmount(GEOLOGIST), amount + 1);
    }

    @Test
    public void testSeveralGeologistsCanBeCalled() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = (Storage)map.placeBuilding(new Headquarter(), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(point1);
        
        /* Add more geologists to the headquarter */
        headquarter0.depositWorker(new Geologist(map));
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        int workers = map.getAllWorkers().size();
        
        flag.callGeologist();
        
        /* Wait for the geologist to leave the headquarter */
        map.stepTime();

        assertEquals(map.getAllWorkers().size(), workers + 1);
        
        /* Call for another geologist and verify that there is a new geologist on the way*/
        flag.callGeologist();

        map.stepTime();
        
        assertEquals(map.getAllWorkers().size(), workers + 2);
    }

// TODO: test that geologist doesn't investigate trees, stones, houses, flags, signs etc
    // TODO: test that geologist goes via the flag when it returns to the storage
}
