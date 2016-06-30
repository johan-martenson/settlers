/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Geologist;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import org.appland.settlers.model.Player;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Call geologist from the flag */
        flag.callGeologist();
    }
    
    @Test
    public void testStorageDispatchesGeologistWhenItHasBeenCalled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        int amountWorkers = map.getWorkers().size();
        
        flag.callGeologist();
        
        /* Verify that a geologist is dispatched from the headquarter */
        map.stepTime();
        
        assertEquals(map.getWorkers().size(), amountWorkers + 1);
        
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Geologist.class);
    }

    @Test
    public void testGeologistGetsToFlagThenLeavesToNearbySpot() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Worker geologist = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Geologist) {
                geologist = w;
            }
        }
    
        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
        
        /* Verify that the geologist keeps going to a point within a radius of 7 */
        assertTrue(geologist.getPosition().distance(geologist.getTarget()) < 7);

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());
    }

    @Test
    public void testGeologistDoesResearchAndPutsUpSign() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
    public void testGeologistWillNotInvestigateTrees() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();

        /* Fill surrounding area with trees */
        for (Point p : map.getPointsWithinRadius(flag.getPosition(), 10)) {
            try {
                map.placeTree(p);
            } catch (Exception e) {}
        }
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();

        /* Fill surrounding area with stones */
        for (Point p : map.getPointsWithinRadius(flag.getPosition(), 10)) {
            try {
                map.placeStone(p);
            } catch (Exception e) {}
        }
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 7, map);
        Utils.putMineralWithinRadius(GOLD, point1, 7, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 7, map);
        Utils.putMineralWithinRadius(COAL, point1, 7, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 7, map);
        Utils.putMineralWithinRadius(IRON, point1, 7, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 7, map);
        Utils.putMineralWithinRadius(STONE, point1, 7, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
    public void testGeologistAvoidsSitesWithSigns() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Surround the flag with signs */
        for (Point p : map.getPointsWithinRadius(point1, 10)) {
            try {
                map.placeSign(WATER, LARGE, p);
            } catch (Exception e) {}
        }
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
    public void testGeologistReturnsAfterNoMoreSignsCanBePlaced() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Surround the flag with trees except for one point*/
        Point point2 = new Point(11, 11);

        for (Point p : map.getPointsWithinRadius(point1, 10)) {

            /* Leave one point free */
            if (p.equals(point2)) {
                continue;
            }

            try {
                map.placeTree(p);
            } catch (Exception e) {}
        }

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0, map).get(0);

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist only investigates free points */
        while (!geologist.getTarget().equals(flag.getPosition())) {

            assertFalse(map.isTreeAtPoint(geologist.getTarget()));

            map.stepTime();
        }

        /* Verify that the geologist goes back */
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        assertEquals(geologist.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testGeologistDoesNotPlaceSignOnBuilding() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Build a forester hut */
        Point point2 = new Point(9, 11);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Surround the flag with trees except for the forester hut */
        for (Point p : map.getPointsWithinRadius(point1, 10)) {

            /* Leave one point free */
            if (p.equals(point2)) {
                continue;
            }

            try {
                map.placeTree(p);
            } catch (Exception e) {}
        }

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0, map).get(0);

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist only investigates free points */
        while (!geologist.getTarget().equals(flag.getPosition())) {

            assertFalse(map.isBuildingAtPoint(geologist.getTarget()));
            assertFalse(map.isTreeAtPoint(geologist.getTarget()));

            map.stepTime();
        }

        /* Verify that the geologist goes back */
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        assertEquals(geologist.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testGeologistDoesNotPlaceSignOnFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Place flag */
        Point point2 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Surround the flag with trees except for the forester hut */
        for (Point p : map.getPointsWithinRadius(point1, 10)) {

            /* Leave one point free */
            if (p.equals(point2)) {
                continue;
            }

            try {
                map.placeTree(p);
            } catch (Exception e) {}
        }

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0, map).get(0);

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist only investigates free points */
        while (!geologist.getTarget().equals(flag.getPosition())) {

            assertFalse(map.isBuildingAtPoint(geologist.getTarget()));
            assertFalse(map.isTreeAtPoint(geologist.getTarget()));

            map.stepTime();
        }

        /* Verify that the geologist goes back */
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        assertEquals(geologist.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testGeologistPlacesEmptySignWhenItFindsNothing() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Place mountain without ore */
        Utils.createMountainWithinRadius(point1, 6, map);
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        flag.callGeologist();
        
        /* Wait for the geologist to go to the flag */
        map.stepTime();
        
        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add a geologist to the headquarter and verify that the amount goes up*/
        int amount = headquarter0.getAmount(GEOLOGIST);
        
        headquarter0.depositWorker(new Geologist(player0, map));
        
        assertEquals(headquarter0.getAmount(GEOLOGIST), amount + 1);
    }

    @Test
    public void testSeveralGeologistsCanBeCalled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Storage headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Add more geologists to the headquarter */
        headquarter0.depositWorker(new Geologist(player0, map));
        
        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);
        
        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);
        
        /* Call geologist from the flag */
        int workers = map.getWorkers().size();
        
        flag.callGeologist();
        
        /* Wait for the geologist to leave the headquarter */
        map.stepTime();

        assertEquals(map.getWorkers().size(), workers + 1);
        
        /* Call for another geologist and verify that there is a new geologist on the way*/
        flag.callGeologist();

        map.stepTime();
        
        assertEquals(map.getWorkers().size(), workers + 2);
    }

    @Test
    public void testGeologistGoesOutAgainIfNeeded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one geologist in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GEOLOGIST, 1, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist) w;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Call for a second geologist */
        flag.callGeologist();
        
        /* Wait for the geologist to explore and then return to the flag */
        for (int i = 0; i < 1000; i++) {
            if (flag.getPosition().equals(geologist.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        /* Let the geologist go back to the headquarter */
        assertEquals(geologist.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, headquarter0.getPosition());

        /* Verify that the geologist leaves again */
        geologist = null;

        for (int i = 0; i < 100; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Geologist && flag.getPosition().equals(w.getTarget())) {
                    geologist = (Geologist)w;

                    break;
                }
            }

            if (geologist != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());
    }

    @Test
    public void testReturningGeologistIncreasesAmountInStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one geologist in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GEOLOGIST, 1, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Geologist) {
                geologist = (Geologist) w;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the amount of geologists in the headquarter is 0 */
        assertEquals(headquarter0.getAmount(GEOLOGIST), 0);

        /* Wait for the geologist to explore and then return to the flag */
        for (int i = 0; i < 1000; i++) {
            if (flag.getPosition().equals(geologist.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        /* Let the geologist go back to the headquarter */
        assertEquals(geologist.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, headquarter0.getPosition());

        /* Verify that the amount of geologists is 1 */
        assertEquals(headquarter0.getAmount(GEOLOGIST), 1);
    }

    @Test
    public void testAssignedGeologistHasPlayerSetCorrectly() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one geologist in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GEOLOGIST, 1, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        List<Geologist> geologists = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0, map);

        assertEquals(geologists.size(), 1);

        Geologist geologist = geologists.get(0);

        assertEquals(geologist.getPlayer(), player0);
    }

    @Test
    public void testCannotCallGeologistFlagNotConnectedToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);
        
        /* Call geologist from the flag */
        flag.callGeologist();

        /* Verify that no geologist is sent from the headquarter */
        for (int i = 0; i < 200; i++) {

            assertTrue(Utils.findWorkersOfTypeOutsideForPlayer(Geologist.class, player0, map).isEmpty());

            map.stepTime();
        }
    }

    @Test
    public void testGeologistReturnsToStorageEvenIfFlagIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;
        for (int i = 0; i < 200; i++) {
            for (Worker w : map.getWorkers()) {
                if (w instanceof Geologist) {
                    geologist = (Geologist)w;
                }
            }

            if (geologist != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the site */
        assertTrue(geologist.isInvestigating());

        /* Tear down the flag */
        map.removeFlag(flag);

        /* Verify that the geologist goes back to the headquarter even though
           the flag is removed
        */
        for (int i = 0; i < 10000; i++) {

            if (geologist.getPosition().equals(headquarter0.getFlag().getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(geologist.getPosition(), headquarter0.getFlag().getPosition());

        /* Wait for the geologist to reach the headquarter and verify that it's correctly stored */
        assertEquals(geologist.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(GEOLOGIST);

        Utils.fastForwardUntilWorkersReachTarget(map, geologist);

        assertEquals(headquarter0.getAmount(GEOLOGIST), amount + 1);
    }
    // TODO: test that geologist doesn't investigate trees, stones, houses, flags, signs etc
    // TODO: test that geologist goes via the flag when it returns to the storage
}
