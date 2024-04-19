/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestGeologist {

    /*
    * TODO: test geologist is created if there is a need and there is a hammer available
    * */

    @Test
    public void testGeologistCanBeCalledFromAFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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
    public void testStorageDispatchesGeologistIsNotASoldier() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        int amountWorkers = map.getWorkers().size();

        flag.callGeologist();

        /* Wait for a geologist to walk out */
        Geologist geologist0 = Utils.waitForWorkerOutsideBuilding(Geologist.class, player0);

        assertNotNull(geologist0);
        assertFalse(geologist0.isSoldier());
    }

    @Test
    public void testGeologistGetsToFlagThenLeavesToNearbySpot() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Fill surrounding area with trees */
        for (Point point : map.getPointsWithinRadius(flag.getPosition(), 10)) {
            try {
                map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Fill surrounding area with stones */
        for (Point point : map.getPointsWithinRadius(flag.getPosition(), 10)) {
            try {
                map.placeStone(point, Stone.StoneType.STONE_1, 7);
            } catch (Exception e) {}
        }

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that the geologist investigates the road since it's the only point without a stone */
        assertTrue(map.isRoadAtPoint(geologist.getTarget()));
    }

    @Test
    public void testGeologistInvestigatesTenSites() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the first site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the second site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the second site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the third site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the third site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the fourth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the fourth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the fifth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the fifth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        /* Wait for the geologist to reach the sixth site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the sixth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the seventh site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the seventh site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the eighth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the eighth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the ninth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the ninth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertNotEquals(geologist.getTarget(), flag.getPosition());

        /* Let the geologist go to the tenth site */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate the tenth site */
        assertTrue(geologist.isInvestigating());

        Utils.fastForward(20, map);

        assertFalse(geologist.isInvestigating());
        assertEquals(geologist.getTarget(), flag.getPosition());

    }

    @Test
    public void testGeologistFindsWaterOnGrass() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 9, map);
        Utils.putMineralWithinRadius(GOLD, point1, 9, map);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 7, map);
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(17, 17);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a large mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 9, map);
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 9, map);
        Utils.putMineralWithinRadius(STONE, point1, 9, map);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        assertFalse(geologist.isInvestigating());

        Sign sign = map.getSignAtPoint(geologist.getPosition());

        assertEquals(sign.getType(), STONE);
        assertEquals(sign.getSize(), LARGE);
    }

    @Test
    public void testGeologistReturnsToStorageAfterInvestigationsAreDone() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to investigate ten sites */
        for (int i = 0; i < 10; i++) {

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Surround the flag with signs */
        for (Point point : map.getPointsWithinRadius(point1, 10)) {
            try {
                map.placeSign(WATER, LARGE, point);
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

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Surround the flag with trees except for one point */
        Point point2 = new Point(11, 11);

        for (Point point : map.getPointsWithinRadius(point1, 10)) {

            /* Leave one point free */
            if (point.equals(point2)) {
                continue;
            }

            try {
                map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0).getFirst();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Build a forester hut */
        Point point2 = new Point(9, 11);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);

        /* Surround the flag with trees except for the forester hut */
        for (Point point : map.getPointsWithinRadius(point1, 10)) {

            /* Leave one point free */
            if (point.equals(point2)) {
                continue;
            }

            try {
                map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0).getFirst();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Place flag */
        Point point2 = new Point(14, 10);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Surround the flag with trees except for the forester hut */
        for (Point point : map.getPointsWithinRadius(point1, 10)) {

            /* Leave one point free */
            if (point.equals(point2)) {
                continue;
            }

            try {
                map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0).getFirst();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(9, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(17, 17);
        Flag flag = map.placeFlag(player0, point1);

        /* Place mountain without ore */
        Utils.createMinableMountainWithinRadius(point1, 9, map);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Storehouse headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add a geologist to the headquarter and verify that the amount goes up */
        int amount = headquarter0.getAmount(GEOLOGIST);

        headquarter0.depositWorker(new Geologist(player0, map));

        assertEquals(headquarter0.getAmount(GEOLOGIST), amount + 1);
    }

    @Test
    public void testSeveralGeologistsCanBeCalled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Storehouse headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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

        /* Call for another geologist and verify that there is a new geologist on the way */
        flag.callGeologist();

        map.stepTime();

        assertEquals(map.getWorkers().size(), workers + 2);
    }

    @Test
    public void testGeologistGoesOutAgainIfNeeded() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(6, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one geologist in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GEOLOGIST, 1);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
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
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Geologist && flag.getPosition().equals(worker.getTarget())) {
                    geologist = (Geologist)worker;

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one geologist in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GEOLOGIST, 1);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 8);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(22, 8);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Ensure there is exactly one geologist in the headquarter */
        Utils.adjustInventoryTo(headquarter0, GEOLOGIST, 1);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        List<Geologist> geologists = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0);

        assertEquals(geologists.size(), 1);

        Geologist geologist = geologists.getFirst();

        assertEquals(geologist.getPlayer(), player0);
    }

    @Test
    public void testCannotCallGeologistFlagNotConnectedToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Verify that no geologist is sent from the headquarter */
        for (int i = 0; i < 200; i++) {

            assertTrue(Utils.findWorkersOfTypeOutsideForPlayer(Geologist.class, player0).isEmpty());

            map.stepTime();
        }
    }

    @Test
    public void testGeologistReturnsToStorageEvenIfFlagIsTornDown() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
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
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Geologist) {
                    geologist = (Geologist)worker;
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

        /* Verify that the geologist goes back to the headquarter even though the flag is removed */
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

    @Test
    public void testGeologistReturnsToStorageIfFlagIsLeftButRoadIsGone() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;
        for (int i = 0; i < 200; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Geologist) {
                    geologist = (Geologist)worker;
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

        /* Remove the road but leave the flag */
        map.removeRoad(road0);

        /* Verify that the geologist goes back to the headquarter even though the flag is removed */
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
    @Test
    public void testGeologistReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place second flag */
        Point point2 = new Point(14, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Call geologist from the second flag */
        flag1.callGeologist();

        /* Wait for the geologist to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0);

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the geologist has started walking */
        assertFalse(geologist.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the geologist continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag0.getPosition());

        assertEquals(geologist.getPosition(), flag0.getPosition());

        /* Verify that the geologist returns to the headquarter when it reaches the flag */
        assertEquals(geologist.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, headquarter0.getPosition());

        assertTrue(geologist.isInsideBuilding());
    }

    @Test
    public void testGeologistContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place second flag */
        Point point2 = new Point(14, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Call geologist from the second flag */
        flag1.callGeologist();

        /* Wait for the geologist to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0);

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the geologist has started walking */
        assertFalse(geologist.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the geologist continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag0.getPosition());

        assertEquals(geologist.getPosition(), flag0.getPosition());

        /* Verify that the geologist continues to the final flag */
        assertEquals(geologist.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag1.getPosition());

        /* Verify that the geologist goes out to geologist instead of going directly back */
        assertNotEquals(geologist.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testGeologistContinuesEvenIfFlagIsRemovedWhenItIsClose() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place second flag */
        Point point2 = new Point(14, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Call geologist from the second flag */
        flag1.callGeologist();

        /* Wait for the geologist to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0);

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist) worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag1.getPosition());

        /* Wait for the geologist to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag0.getPosition());

        map.stepTime();

        /* See that the geologist has started walking */
        assertFalse(geologist.isExactlyAtPoint());

        /* Remove the second flag */
        map.removeFlag(flag1);

        /* Verify that the geologist continues walking to the second flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag1.getPosition());

        assertEquals(geologist.getPosition(), flag1.getPosition());

        /* Verify that the geologist goes out to geologist instead of going directly back */
        map.stepTime();

        assertNotEquals(geologist.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testGeologistReturnsOffroadIfRoadIsMissingWhScoutReachesFlag() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 23);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(17, 23);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarter and flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to come out */
        Geologist geologist = Utils.waitForWorkersOutsideBuilding(Geologist.class, 1, player0).getFirst();

        /* Wait for the geologist to reach the flag */
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, flag.getPosition());

        /* Wait for the geologist to leave the flag */
        assertEquals(geologist.getPosition(), flag.getPosition());
        assertTrue(geologist.isExactlyAtPoint());

        map.stepTime();

        assertFalse(geologist.isExactlyAtPoint());

        /* Wait for the geologist to be on the way back to the flag */
        Utils.waitForWorkerToSetTarget(map, geologist, flag.getPosition());

        /* Wait for the geologist to be almost at the flag */
        for (int i = 0; i < 2000; i++) {
            if (!geologist.isExactlyAtPoint() && geologist.getNextPoint().equals(flag.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(geologist.isExactlyAtPoint());
        assertEquals(geologist.getNextPoint(), flag.getPosition());

        /* Remove the road between the flag and the headquarter */
        map.removeRoad(road0);

        /* Verify that the geologist walks offroad back to the headquarter */
        Utils.verifyWorkerWalksToTarget(map, geologist, headquarter0.getPosition());
    }
}
