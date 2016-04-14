/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import org.appland.settlers.model.Player;
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
public class TestBarracks {

    @Test
    public void testBarracksOnlyNeedsTwoPlancksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        barracks0.putCargo(cargo);
        barracks0.putCargo(cargo);
    
        /* Verify that this is enough to construct the barracks */
        for (int i = 0; i < 100; i++) {
            assertTrue(barracks0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(barracks0.ready());
    }

    @Test
    public void testBarracksCannotBeConstructedWithOnePlanck() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        barracks0.putCargo(cargo);
    
        /* Verify that this is enough to construct the barracks */
        for (int i = 0; i < 500; i++) {
            assertTrue(barracks0.underConstruction());

            map.stepTime();
        }

        assertFalse(barracks0.ready());
    }

    @Test
    public void testBarracksGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(7, 21);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        /* Verify that a military is sent from the headquarter */
        map.stepTime();
        
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Military.class);
        
        Military m = null;
        for (Worker w : map.getWorkers()) {
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));

        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
    }
    
    @Test
    public void testBorderIsExtendedWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        /* Verify that a military is sent from the headquarter */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);
        
        map.stepTime();
        
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Military.class);
        
        Military m = null;
        for (Worker w : map.getWorkers()) {
            if (w instanceof Military) {
                m = (Military)w;
            }
        }
        
        assertNotNull(m);
        
        /* Verify that the border is extended when the military reaches the barracks */
        assertEquals(m.getTarget(), barracks0.getPosition());        
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));

        Utils.fastForwardUntilWorkerReachesPoint(map, m, barracks0.getPosition());

        assertFalse(player0.getBorders().get(0).contains(new Point(5, 25)));
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 31)));
    }

    @Test
    public void testBarracksOnlyNeedsTwoMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks with two militaries */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Verify that the barracks does not need another military */
        assertFalse(barracks0.needsMilitaryManning());
    }

    @Test
    public void testBarracksCannotHoldMilitariesBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Verify that the barracks can't hold militaries before it's finished */
        assertFalse(barracks0.needsMilitaryManning());
        
        Military military = new Military(player0, PRIVATE_RANK, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks with two militaries */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Verify that the barracks does not need another military */
        Military military = new Military(player0, PRIVATE_RANK, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        /* Verify that the border is grown with the correct radius */
        assertTrue(player0.getBorders().get(0).contains(new Point(6, 24)));
        
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        assertTrue(player0.getBorders().get(0).contains(new Point(6, 30)));
    }
    
    @Test
    public void testBarracksNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        assertTrue(barracks0.needsMaterial(COIN));
    }
    
    @Test
    public void testUnfinishedBarracksNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        assertFalse(barracks0.needsMaterial(COIN));
    }
    
    @Test
    public void testBarracksCanHoldOnlyOneCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        assertTrue(barracks0.needsMaterial(COIN));
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);

        barracks0.promiseDelivery(COIN);
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
    public void testStorageStopsDeliveringCoinsWhenBarracksIsFull() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Fill up headquarter with coins */
        Utils.adjustInventoryTo(headquarter0, COIN, 10, map);

        /* Wait for the barracks to get a coin */
        for (int i = 0; i < 1000; i++) {
            if (barracks0.getAmount(COIN) == 1) {
                break;
            }

            map.stepTime();
        }

        assertEquals(barracks0.getAmount(COIN), 1);

        /* Disable promotion */
        barracks0.disablePromotions();

        /* Verify that no more coin is deliverd */
        Courier courier = road0.getCourier();
        for (int i = 0; i < 1000; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial().equals(COIN)) {
                assertFalse(true);
            }

            map.stepTime();
        }
    }
    
    @Test
    public void testPrivateIsPromotedWhenCoinIsAvailable() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        Military military2 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == SERGEANT_RANK && military2.getRank() == PRIVATE_RANK) ||
                   (military1.getRank() == PRIVATE_RANK  && military2.getRank() == SERGEANT_RANK));
    }

    @Test
    public void testTimeSpentWithCoinButNoMilitaryDoesNotSpeedUpPromotion() throws Exception {
        
        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Wait before the barracks is populated */
        Utils.fastForward(200, map);
        
        /* Occupy the barracks with one private */
        Military military = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Verify that it still takes the same time for the private to get promoted */
        Utils.fastForward(99, map);
        
        assertEquals(military.getRank(), PRIVATE_RANK);
        
        map.stepTime();

        assertEquals(military.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testPromotionConsumesCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(barracks0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(barracks0.getAmount(COIN), 0);
    }

    @Test
    public void testBarracksWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);
        
        /* Deliver one coin to the barracks */
        Cargo cargo = new Cargo(COIN, map);
        
        barracks0.putCargo(cargo);

        /* Occupy the barracks with one private */
        Military military1 = Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Military military2 = Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(barracks0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(barracks0.getAmount(COIN), 1);
    }

    @Test
    public void testCanDisableCoinsToBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Add coins to the headquarter */
        headquarter0.putCargo(new Cargo(COIN, map));

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        Utils.constructHouse(barracks0, map);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Verify that promotions are enabled initially */
        assertTrue(barracks0.isPromotionEnabled());

        /* Disable coins to the barracks and verify that it doesn't need coins*/
        barracks0.disablePromotions();

        assertFalse(barracks0.needsMaterial(COIN));

        /* Verify that promotions are disabled */
        assertFalse(barracks0.isPromotionEnabled());

        /* Verify that no coins are delivered */
        Utils.verifyNoDeliveryOfMaterial(map, road0, COIN);
    }

    @Test
    public void testCanResumeDeliveryOfCoinsToBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Add coins to the headquarter */
        headquarter0.putCargo(new Cargo(COIN, map));

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        Utils.constructHouse(barracks0, map);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Disable coins to the barracks and verify that it doesn't need coins*/
        barracks0.disablePromotions();

        assertFalse(barracks0.needsMaterial(COIN));

        /* Verify that no coins are delivered */
        Utils.verifyNoDeliveryOfMaterial(map, road0, COIN);

        /* Resume delivery of coins to the barracks */
        barracks0.enablePromotions();

        /* Verify that the barracks needs coins again */
        assertTrue(barracks0.needsMaterial(COIN));

        /* Verify that promotions are enabled again */
        assertTrue(barracks0.isPromotionEnabled());

        /* Verify that a coin is delivered to the barracks */
        Utils.verifyDeliveryOfMaterial(map, road0, COIN);
    }

    @Test
    public void testOccupiedBarracksCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Connect headquarter and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        
        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Military m = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Evacuate the barracks and verify that the military leaves the barracks */
        assertTrue(m.isInsideBuilding());
        
        barracks0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());        
    }

    @Test
    public void testEvacuatedMilitaryReturnsToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Connect headquarter and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        
        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Military m = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Evacuate the barracks */
        assertTrue(m.isInsideBuilding());
        
        barracks0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        assertEquals(barracks0.getHostedMilitary(), 0);
        
        /* Verify that the evacuated military returns to the storage */
        assertEquals(m.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, m.getTarget());
        
        assertTrue(m.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testEvacuatedSoldierReturnsOffroadWhenNotConnected() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Military m = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Evacuate the barracks */
        assertTrue(m.isInsideBuilding());
        
        barracks0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        
        /* Verify that the evacuated military returns to the storage */
        assertEquals(m.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, m.getTarget());
        
        assertTrue(m.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testNoMilitaryIsDispatchedToEvacuatedBarracks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Connect headquarters and barracks */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());
        
        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Verify that the barracks are evacuated */
        assertFalse(barracks0.isEvacuated());

        /* Evacuate the barracks */
        barracks0.evacuate();

        /* Verify that the barracks are evacuated */
        assertTrue(barracks0.isEvacuated());

        /* Verify that no militaries are assigned to the barracks */
        for (int i = 0; i < 200; i++) {
            assertEquals(barracks0.getHostedMilitary(), 0);
            map.stepTime();
        }
    }
    
    @Test
    public void testEvacuationCanBeCanceled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(6, 22);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);
        
        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Military m = Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Evacuate the barracks */
        assertTrue(m.isInsideBuilding());
        
        barracks0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        
        /* Wait for the evacuated military to return to the storage */
        assertEquals(m.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, m.getTarget());
        
        assertTrue(m.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    
        /* Cancel evacuation */
        assertFalse(barracks0.needsMilitaryManning());

        barracks0.cancelEvacuation();

        assertTrue(barracks0.needsMilitaryManning());

        /* Verify that the barracks are not evacuated */
        assertFalse(barracks0.isEvacuated());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenBarracksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(8, 8);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);

        /* Destroy the barracks */
        assertEquals(barracks0.getHostedMilitary(), 1);

        barracks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0, map);

        int amount = headquarter0.getAmount(PRIVATE);

        assertEquals(military.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenBarracksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(8, 8);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0, map);
        
        /* Destroy the barracks */
        assertEquals(barracks0.getHostedMilitary(), 1);

        barracks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0, map);

        assertEquals(military.getTarget(), headquarter0.getPosition());

        /* Verify that the military plans to use the roads */
        boolean firstStep = true;
        for (Point p : military.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test (expected = Exception.class)
    public void testProductionCannotBeResumedInBarracks() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(8, 8);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Verify that production cannot be resumed in barracks */
        barracks0.resumeProduction();
    }

    @Test
    public void testFieldOfViewIsExtendedWhenBarracksIsOccupied() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Verify that the field of view remains the same until the barracks 
           gets occupied */
        Point pointInOldFOV = new Point(27, 5);
        Point pointInNewFOV = new Point(31, 5);

        for (int i = 0; i < 1000; i++) {
            if (barracks0.getHostedMilitary() == 0) {
                assertTrue(player0.getFieldOfView().contains(pointInOldFOV));
                assertTrue(player0.getDiscoveredLand().contains(pointInOldFOV));
                assertFalse(player0.getFieldOfView().contains(pointInNewFOV));
                assertFalse(player0.getDiscoveredLand().contains(pointInNewFOV));
            } else {
                break;
            }

            map.stepTime();
        }

        /* Verify that the field of view is updated when a military has entered
           the barracks */
        assertTrue(barracks0.getHostedMilitary() > 0);
        assertTrue(player0.getFieldOfView().contains(pointInNewFOV));
        assertTrue(player0.getDiscoveredLand().contains(pointInNewFOV));
        assertFalse(player0.getFieldOfView().contains(pointInOldFOV));
        assertTrue(player0.getDiscoveredLand().contains(pointInOldFOV));
    }

    @Test
    public void testBarracksCanBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);
        
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        assertNotNull(map.getBuildingAtPoint(barracks0.getPosition()));
        assertFalse(barracks0.equals(map.getBuildingAtPoint(barracks0.getPosition())));
        assertEquals(map.getBuildingAtPoint(barracks0.getPosition()).getClass(), GuardHouse.class);
    }

    @Test (expected = Exception.class)
    public void testUnfinishedBarracksCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Upgrade the barracks */
        barracks0.upgrade();
    }

    @Test (expected = InvalidUserActionException.class)
    public void testBurningBarracksCannotBeUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Tear down the barracks so it's on fire */
        barracks0.tearDown();

        /* Upgrade the barracks */
        barracks0.upgrade();
    }

    @Test
    public void testCannotUpgradeBarracksWithoutMaterial() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials but not enough for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);
        
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the upgrade cannot happen without the required material */
        for (int i = 0; i < 1000; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Add the last required material for the upgrade */
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);

        /* Step time once and verify that the barracks is upgraded */
        map.stepTime();

        assertFalse(barracks0.equals(map.getBuildingAtPoint(barracks0.getPosition())));
        assertEquals(map.getBuildingAtPoint(barracks0.getPosition()).getClass(), GuardHouse.class);
    }

    @Test (expected = InvalidUserActionException.class)
    public void testCannotUpgradeBarracksBeingUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Verify that the barracks can't get upgraded again */
        barracks0.upgrade();
    }

    @Test
    public void testUpgradingCausesMaterialToGetDelivered() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Make sure there is material for upgrading */
        Utils.adjustInventoryTo(headquarter0, PLANCK, 10, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 10, map);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Connect the barracks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        /* Place the courier on the road */
        Courier courier0 = Utils.occupyRoad(road0, map);

        /* Verify that the barracks doesn't need stone before the upgrade */
        assertFalse(barracks0.needsMaterial(STONE));

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Verify that the barracks needs stone */
        assertTrue(barracks0.needsMaterial(STONE));

        /* Verify that the courier picks up a stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), barracks0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, barracks0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier picks up the second stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), barracks0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, barracks0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier picks up the third stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier0);

        assertEquals(courier0.getCargo().getMaterial(), STONE);

        /* Verify that the courier delivers the stone */
        assertEquals(courier0.getCargo().getTarget(), barracks0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier0, barracks0.getPosition());

        assertNull(courier0.getCargo());

        /* Verify that the courier doesn't deliver anything else to the barracks */
        for (int i = 0; i < 1000; i++) {

            assertTrue(courier0.getCargo() == null || !courier0.getCargo().getTarget().equals(barracks0));

            map.stepTime();
        }
    }

    @Test
    public void testOccupiedBarracksIsOccupiedAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertTrue(barracks0.occupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);
        
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify that the upgrade isn't too quick */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the upgraded building is also occupied */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        assertTrue(guardHouse0.occupied());
        assertEquals(guardHouse0.getHostedMilitary(), 1);
    }

    @Test
    public void testCoinRemainsAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertTrue(barracks0.occupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        barracks0.promiseDelivery(COIN);

        barracks0.putCargo(coinCargo);

        assertEquals(barracks0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the coin is still in the building */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        assertEquals(guardHouse0.getAmount(COIN), 1);
    }

    @Test
    public void testBuildingDuringUpgradeCanBeDestroyed() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertTrue(barracks0.occupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Upgrade for a while */
        for (int i = 0; i < 10; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the building can be destroyed */
        barracks0.tearDown();

        assertTrue(barracks0.burningDown());
    }

    @Test
    public void testPlayerIsCorrectAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        Player player2 = new Player("Player 2", java.awt.Color.BLACK);
        List<Player> players = new ArrayList<>();
        players.add(player2);
        players.add(player0);
        players.add(player1);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertTrue(barracks0.occupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Put a coin in the building */
        Cargo coinCargo = new Cargo(COIN, map);

        barracks0.promiseDelivery(COIN);

        barracks0.putCargo(coinCargo);

        assertEquals(barracks0.getAmount(COIN), 1);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the player is set correctly in the upgraded building */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        assertEquals(guardHouse0.getPlayer(), player0);
    }

    @Test
    public void testCanHostRightNumberOfSoldiersAfterUpgraded() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertTrue(barracks0.occupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that two more militaries can be hosted in the building */
        Building guardHouse0 = map.getBuildingAtPoint(barracks0.getPosition());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, guardHouse0, map);
    }

    @Test
    public void testBorderIsExpandedAfterUpgrade() throws Exception {

        /* Creating new player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing barracks */
        Point point26 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point26);

        /* Finish construction of the barracks */
        Utils.constructHouse(barracks0, map);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks0, map);

        assertTrue(barracks0.occupied());

        /* Upgrade the barracks */
        barracks0.upgrade();

        /* Add materials for the upgrade */
        Cargo stoneCargo = new Cargo(STONE, map);

        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);
        barracks0.promiseDelivery(STONE);

        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);
        barracks0.putCargo(stoneCargo);

        /* Verify the border before the upgrade */
        assertTrue(player0.getBorders().get(0).contains(new Point(29, 5)));

        /* Wait for the upgrade */
        for (int i = 0; i < 100; i++) {

            assertEquals(barracks0, map.getBuildingAtPoint(barracks0.getPosition()));

            map.stepTime();
        }

        /* Verify that the border is expanded after the upgrade */
        assertFalse(player0.getBorders().get(0).contains(new Point(29, 5)));
        assertTrue(player0.getBorders().get(0).contains(new Point(31, 5)));
    }

    /*

    percentage of upgrade progress is getting updated
    is possible to see if upgrades are possible
    promotion timers are running through upgrades    
    it's not possible to deliver too much material to the barracks during upgrade
    barracks being upgraded can be attacked and won
    upgrades finish (and state goes back to normal)
    
    lack of space can hinder upgrades
    upgrade of regular building
    */
}
