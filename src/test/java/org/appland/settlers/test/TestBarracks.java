/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PRIVATE;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestBarracks {
    
    @Test
    public void testBarracksGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(7, 21);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(point23, point36);

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        /* Verify that a military is sent from the headquarter */
        map.stepTime();
        
        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Military.class);
        
        Military m = null;
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Military) {
                m = (Military)w;
            }
        }
        
        assertNotNull(m);
        
        /* Wait for the military to reach the barracks */
        assertEquals(m.getTarget(), barracks0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, barracks0.getPosition());
        
        assertTrue(m.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenBarracksIsFinished() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 25);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(6, 24);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(point23, point36);

        /* Wait for the barracks to finish construction */
        assertTrue(map.getBorders().get(0).contains(new Point(5, 25)));

        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        assertTrue(map.getBorders().get(0).contains(new Point(5, 25)));
    }
    
    @Test
    public void testBorderIsExtendedWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 24);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);

        /* Placing road between (7, 23) and (6, 4) */
        Point point23 = new Point(7, 23);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(point23, point36);

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        /* Verify that a military is sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);
        
        map.stepTime();
        
        Utils.verifyListContainsWorkerOfType(map.getAllWorkers(), Military.class);
        
        Military m = null;
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Military) {
                m = (Military)w;
            }
        }
        
        assertNotNull(m);
        
        /* Verify that the border is extended when the military reaches the barracks */
        assertEquals(m.getTarget(), barracks0.getPosition());        
        assertTrue(map.getBorders().get(0).contains(new Point(5, 25)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, barracks0.getPosition());
        
        assertFalse(map.getBorders().get(0).contains(new Point(5, 25)));
        assertTrue(map.getBorders().get(0).contains(new Point(5, 29)));
    }

    @Test
    public void testBarracksOnlyNeedsTwoMilitaries() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);

        /* Occupy the barracks with two militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Verify that the barracks does not need another military */
        assertFalse(barracks0.needMilitaryManning());
    }

    @Test
    public void testBarracksCannotHoldMoreThanTwoMilitaries() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);

        /* Occupy the barracks with two militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Verify that the barracks does not need another military */
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, barracks0);
        
        try {
            barracks0.hostMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
        assertEquals(barracks0.getHostedMilitary(), 2);
    }
}
