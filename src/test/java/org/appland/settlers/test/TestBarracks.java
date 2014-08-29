/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PRIVATE;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
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
        
        Utils.constructSmallHouse(barracks0);

        /* Occupy the barracks with two militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Verify that the barracks does not need another military */
        assertFalse(barracks0.needsMilitaryManning());
    }

    @Test
    public void testBarracksCannotHoldMilitariesBeforeFinished() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);

        /* Verify that the barracks can't hold militaries before it's finished */
        assertFalse(barracks0.needsMilitaryManning());
        
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, barracks0);
        
        try {
            barracks0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
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
        
        Utils.constructSmallHouse(barracks0);

        /* Occupy the barracks with two militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Verify that the barracks does not need another military */
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, barracks0);
        
        try {
            barracks0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
        assertEquals(barracks0.getHostedMilitary(), 2);
    }

    @Test
    public void testBarracksRadiusIsCorrect() throws Exception{

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);

        /* Verify that the border is grown with the correct radius */
        assertTrue(map.getBorders().get(0).contains(new Point(6, 24)));
        
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        assertTrue(map.getBorders().get(0).contains(new Point(6, 28)));
    }
    
    @Test
    public void testBarracksNeedsCoin() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);

        assertTrue(barracks0.needsMaterial(COIN));
    }
    
    @Test
    public void testUnfinishedBarracksNotNeedsCoin() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        assertFalse(barracks0.needsMaterial(COIN));
    }
    
    @Test
    public void testBarracksCanHoldOnlyOneCoin() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);

        assertTrue(barracks0.needsMaterial(COIN));
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);
        
        /* Verify that the barracks can't hold another coin */
        assertFalse(barracks0.needsMaterial(COIN));
        assertEquals(barracks0.getAmount(COIN), 1);
        
        try {
            barracks0.putCargo(cargo);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertEquals(barracks0.getAmount(COIN), 1);
    }

    @Test
    public void testPrivateIsPromotedWhenCoinIsAvailable() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Verify that the private is promoted at the right time */
        for (int i = 0; i < 100; i++) {
            assertEquals(military.getRank(), PRIVATE_RANK);
            map.stepTime();
        }

        assertEquals(military.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testOnlyOnePrivateIsPromoted() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == SERGEANT_RANK && military2.getRank() == PRIVATE_RANK) ||
                   (military1.getRank() == PRIVATE_RANK  && military2.getRank() == SERGEANT_RANK));
    }

    @Test
    public void testTimeSpentWithCoinButNoMilitaryDoesNotSpeedUpPromotion() throws Exception {
        
        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Wait before the barracks is populated */
        Utils.fastForward(200, map);
        
        /* Occupy the barracks with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);

        /* Verify that it still takes the same time for the private to get promoted */
        Utils.fastForward(99, map);
        
        assertEquals(military.getRank(), PRIVATE_RANK);
        
        map.stepTime();

        assertEquals(military.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testPromotionConsumesCoin() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(barracks0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(barracks0.getAmount(COIN), 0);
    }

    @Test
    public void testBarracksWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), barracks0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), barracks0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(barracks0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(barracks0.getAmount(COIN), 1);
    }
    
    @Test
    public void testCanDisableCoinsToBarracks() throws Exception {

        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(), point22);
        
        Utils.constructSmallHouse(barracks0);
        
        /* Deliver one coin to the barracks */
        assertTrue(barracks0.needsMaterial(COIN));
        
        /* Disable coins to the barracks and verify that it doesn't need coins*/
        barracks0.disablePromotions();
        
        assertFalse(barracks0.needsMaterial(COIN));
    }
}
