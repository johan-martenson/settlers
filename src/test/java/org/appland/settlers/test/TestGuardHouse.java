/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestGuardHouse {
    
    @Test
    public void testGuardHouseNeedsTwoPlancksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Deliver two plancks and three stones*/
        Cargo cargo = new Cargo(PLANCK, map);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the guard house */
        for (int i = 0; i < 100; i++) {
            assertTrue(guardHouse0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(guardHouse0.ready());
    }
    
    @Test
    public void testGuardHouseCannotBeConstructedWithOnePlanckTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Deliver one planck and three stones */
        Cargo cargo = new Cargo(PLANCK, map);

        guardHouse0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the guard house */
        for (int i = 0; i < 500; i++) {
            assertTrue(guardHouse0.underConstruction());

            map.stepTime();
        }

        assertFalse(guardHouse0.ready());
    }

    @Test
    public void testGuardHouseCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Deliver one planck and three stones */
        Cargo cargo = new Cargo(PLANCK, map);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        guardHouse0.putCargo(stoneCargo);
        guardHouse0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the guard house */
        for (int i = 0; i < 500; i++) {
            assertTrue(guardHouse0.underConstruction());

            map.stepTime();
        }

        assertFalse(guardHouse0.ready());
    }

    @Test
    public void testGuardHouseGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(7, 21);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the guard house to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0, map);

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
        
        /* Wait for the military to reach the guard house */
        assertEquals(m.getTarget(), guardHouse0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, guardHouse0.getPosition());
        
        assertTrue(m.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenGuardHouseIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(5, 25);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(6, 24);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the guard house to finish construction */
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));

        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
    }
    
    @Test
    public void testBorderIsExtendedWhenGuardHouseIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 24);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);

        /* Placing road between (7, 23) and (6, 4) */
        Point point23 = new Point(7, 23);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the guard house to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(guardHouse0, map);

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
        
        /* Verify that the border is extended when the military reaches the guard house */
        assertEquals(m.getTarget(), guardHouse0.getPosition());
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, guardHouse0.getPosition());
        
        assertFalse(player0.getBorders().get(0).contains(new Point(5, 25)));
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 33)));
    }

    @Test
    public void testGuardHouseOnlyNeedsThreeMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house with two militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Verify that the guard house does not need another military */
        assertFalse(guardHouse0.needsMilitaryManning());
    }

    @Test
    public void testGuardHouseCannotHoldMilitariesBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);

        /* Verify that the guard house can't hold militaries before it's finished */
        assertFalse(guardHouse0.needsMilitaryManning());
        
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, guardHouse0);
        
        try {
            guardHouse0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
    }

    @Test
    public void testGuardHouseCannotHoldMoreThanThreeMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house with two militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Verify that the guard house does not need another military */
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, guardHouse0);
        
        try {
            guardHouse0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
        assertEquals(guardHouse0.getHostedMilitary(), 3);
    }

    @Test
    public void testGuardHouseRadiusIsCorrect() throws Exception{

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);

        /* Verify that the border is grown with the correct radius */
        assertTrue(player0.getBorders().get(0).contains(new Point(6, 24)));
        
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(6, 32)));
    }
    
    @Test
    public void testGuardHouseNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);

        assertTrue(guardHouse0.needsMaterial(COIN));
    }
    
    @Test
    public void testUnfinishedGuardHouseNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        assertFalse(guardHouse0.needsMaterial(COIN));
    }
    
    @Test
    public void testGuardHouseCanHoldTwoCoins() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);

        assertTrue(guardHouse0.needsMaterial(COIN));
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.promiseDelivery(COIN);
        guardHouse0.promiseDelivery(COIN);

        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        /* Verify that the guard house can't hold another coin */
        assertFalse(guardHouse0.needsMaterial(COIN));
        assertEquals(guardHouse0.getAmount(COIN), 2);

        try {
            guardHouse0.putCargo(cargo);
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(guardHouse0.getAmount(COIN), 2);
    }

    @Test
    public void testPrivateIsPromotedWhenCoinIsAvailable() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
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
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Wait for the promotion to happen */
        Utils.fastForward(100, map);

        assertTrue((military1.getRank() == SERGEANT_RANK && military2.getRank() == PRIVATE_RANK) ||
                   (military1.getRank() == PRIVATE_RANK  && military2.getRank() == SERGEANT_RANK));
    }

    @Test
    public void testTimeSpentWithCoinButNoMilitaryDoesNotSpeedUpPromotion() throws Exception {
        
        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.putCargo(cargo);

        /* Wait before the guard house is populated */
        Utils.fastForward(200, map);
        
        /* Occupy the guard house with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);

        /* Verify that it still takes the same time for the private to get promoted */
        Utils.fastForward(99, map);
        
        assertEquals(military.getRank(), PRIVATE_RANK);
        
        map.stepTime();

        assertEquals(military.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testPromotionConsumesCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(guardHouse0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(guardHouse0.getAmount(COIN), 0);
    }

    @Test
    public void testPromotionOnlyConsumesOneCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.putCargo(cargo);
        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(SERGEANT_RANK, map), guardHouse0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(guardHouse0.getAmount(COIN), 2);
        
        Utils.fastForward(100, map);

        assertEquals(guardHouse0.getAmount(COIN), 1);
    }

    @Test
    public void testGuardHouseWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        Cargo cargo = new Cargo(COIN, map);
        
        guardHouse0.putCargo(cargo);

        /* Occupy the guard house with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), guardHouse0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), guardHouse0, map);
        
        /* Verify that coin is not consumed */
        assertEquals(guardHouse0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(guardHouse0.getAmount(COIN), 1);
    }
    
    @Test
    public void testCanDisableCoinsToGuardHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        Utils.constructHouse(guardHouse0, map);
        
        /* Deliver one coin to the guard house */
        assertTrue(guardHouse0.needsMaterial(COIN));
        
        /* Disable coins to the guard house and verify that it doesn't need coins*/
        guardHouse0.disablePromotions();
        
        assertFalse(guardHouse0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedGuardHouseCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Connect headquarter and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());
        
        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Evacuate the guard house and verify that the military leaves the guard house */
        assertTrue(m.isInsideBuilding());
        
        guardHouse0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        assertEquals(guardHouse0.getHostedMilitary(), 0);
    }

    @Test
    public void testEvacuatedMilitaryReturnsToStorage() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Connect headquarter and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());
        
        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Evacuate the guard house */
        assertTrue(m.isInsideBuilding());
        
        guardHouse0.evacuate();
        
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
    public void testEvacuatedSoldierReturnsOffroadWhenNotConnected() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Evacuate the guard house */
        assertTrue(m.isInsideBuilding());
        
        guardHouse0.evacuate();
        
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
    public void testNoMilitaryIsDispatchedToEvacuatedGuardHouse() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Connect headquarters and guard house */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());
        
        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Evacuate the guard house */
        guardHouse0.evacuate();

        /* Verify that no militaries are assigned to the guard house */
        for (int i = 0; i < 200; i++) {
            assertEquals(guardHouse0.getHostedMilitary(), 0);
            map.stepTime();
        }
    }
    
    @Test
    public void testEvacuationCanBeCanceled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing guard house */
        Point point22 = new Point(6, 22);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point22);
        
        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Evacuate the guard house */
        assertTrue(m.isInsideBuilding());
        
        guardHouse0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        
        /* Wait for the evacuated military to return to the storage */
        assertEquals(m.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, m.getTarget());
        
        assertTrue(m.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    
        /* Cancel evacuation */
        assertFalse(guardHouse0.needsMilitaryManning());
        
        guardHouse0.cancelEvacuation();
        
        assertTrue(guardHouse0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenGuardHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing guard house */
        Point point26 = new Point(8, 8);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point26);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house */
        Utils.occupyBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Destroy the guard house */
        Worker ww = guardHouse0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), guardHouse0.getPosition());

        guardHouse0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenGuardHouseIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing guard house */
        Point point26 = new Point(8, 8);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point26);

        /* Connect the guard house with the headquarter */
        map.placeAutoSelectedRoad(player0, guardHouse0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Occupy the guard house */
        Utils.occupyBuilding(new Military(PRIVATE_RANK, map), guardHouse0, map);
        
        /* Destroy the guard house */
        Worker ww = guardHouse0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), guardHouse0.getPosition());

        guardHouse0.tearDown();

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

    @Test (expected = Exception.class)
    public void testProductionCannotBeResumedInGuardHouse() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing guard house */
        Point point26 = new Point(8, 8);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point26);

        /* Finish construction of the guard house */
        Utils.constructHouse(guardHouse0, map);

        /* Verify that production cannot be resumed in guard house */
        guardHouse0.resumeProduction();
    }
}
