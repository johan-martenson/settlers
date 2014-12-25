/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.WatchTower;
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
public class TestWatchTower {
    
    @Test
    public void testWatchTowerNeedsThreePlancksAndFourStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Deliver two plancks and three stones*/
        Cargo cargo = new Cargo(PLANCK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the watch tower */
        for (int i = 0; i < 150; i++) {
            assertTrue(watchTower0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(watchTower0.ready());
    }
    
    @Test
    public void testWatchTowerCannotBeConstructedWithOnePlanckTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Deliver one planck and three stones */
        Cargo cargo = new Cargo(PLANCK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the watch tower needs a planck */
        assertTrue(watchTower0.needsMaterial(PLANCK));
        
        /* Verify that this is not enough to construct the watch tower */
        for (int i = 0; i < 500; i++) {
            assertTrue(watchTower0.underConstruction());

            map.stepTime();
        }

        assertFalse(watchTower0.ready());
    }

    @Test
    public void testWatchTowerCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Deliver one planck and three stones */
        Cargo cargo = new Cargo(PLANCK, map);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);
        watchTower0.putCargo(stoneCargo);

        /* Verify that the watch tower needs a stone */
        assertTrue(watchTower0.needsMaterial(STONE));

        /* Verify that this is not enough to construct the watch tower */
        for (int i = 0; i < 500; i++) {
            assertTrue(watchTower0.underConstruction());

            map.stepTime();
        }

        assertFalse(watchTower0.ready());
    }

    @Test
    public void testWatchTowerGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(7, 21);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the watch tower to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0, map);

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
        
        /* Wait for the military to reach the watch tower */
        assertEquals(m.getTarget(), watchTower0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, watchTower0.getPosition());
        
        assertTrue(m.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenWatchTowerIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(5, 25);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(6, 24);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the watch tower to finish construction */
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));

        Utils.fastForwardUntilBuildingIsConstructed(watchTower0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
    }
    
    @Test
    public void testBorderIsExtendedWhenWatchTowerIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 24);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Placing road between (7, 23) and (6, 4) */
        Point point23 = new Point(7, 23);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the watch tower to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(watchTower0, map);

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
        
        /* Verify that the border is extended when the military reaches the watch tower */
        assertEquals(m.getTarget(), watchTower0.getPosition());
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, watchTower0.getPosition());
        
        assertFalse(player0.getBorders().get(0).contains(new Point(5, 25)));
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 39)));
    }

    @Test
    public void testWatchTowerOnlyNeedsSixMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower with six militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);

        /* Verify that the watch tower does not need another military */
        assertFalse(watchTower0.needsMilitaryManning());
    }

    @Test
    public void testWatchTowerCannotHoldMilitariesBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);

        /* Verify that the watch tower can't hold militaries before it's finished */
        assertFalse(watchTower0.needsMilitaryManning());
        
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, watchTower0);
        
        try {
            watchTower0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
    }

    @Test
    public void testWatchTowerCannotHoldMoreThanSixMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower with six militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);

        /* Verify that the watch tower does not need another military */
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, watchTower0);
        
        try {
            watchTower0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
        assertEquals(watchTower0.getHostedMilitary(), 6);
    }

    @Test
    public void testWatchTowerRadiusIsCorrect() throws Exception{

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);

        /* Verify that the border is grown with the correct radius */
        assertTrue(player0.getBorders().get(0).contains(new Point(6, 24)));
        
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(6, 38)));
    }
    
    @Test
    public void testWatchTowerNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);

        assertTrue(watchTower0.needsMaterial(COIN));
    }
    
    @Test
    public void testUnfinishedWatchTowerNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        assertFalse(watchTower0.needsMaterial(COIN));
    }
    
    @Test
    public void testWatchTowerCanHoldThreeCoins() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);

        assertTrue(watchTower0.needsMaterial(COIN));
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.promiseDelivery(COIN);
        watchTower0.promiseDelivery(COIN);
        watchTower0.promiseDelivery(COIN);

        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        /* Verify that the watch tower can't hold another coin */
        assertFalse(watchTower0.needsMaterial(COIN));
        assertEquals(watchTower0.getAmount(COIN), 3);

        try {
            watchTower0.putCargo(cargo);
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(watchTower0.getAmount(COIN), 3);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.putCargo(cargo);

        /* Wait before the watch tower is populated */
        Utils.fastForward(200, map);
        
        /* Occupy the watch tower with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);

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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(watchTower0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 0);
    }

    @Test
    public void testOnePromotionOnlyConsumesOneCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.putCargo(cargo);
        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(SERGEANT_RANK, map), watchTower0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(watchTower0.getAmount(COIN), 2);
        
        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 1);
    }

    @Test
    public void testWatchTowerWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        Cargo cargo = new Cargo(COIN, map);
        
        watchTower0.putCargo(cargo);

        /* Occupy the watch tower with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), watchTower0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), watchTower0, map);
        
        /* Verify that coin is not consumed */
        assertEquals(watchTower0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(watchTower0.getAmount(COIN), 1);
    }
    
    @Test
    public void testCanDisableCoinsToWatchTower() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        Utils.constructHouse(watchTower0, map);
        
        /* Deliver one coin to the watch tower */
        assertTrue(watchTower0.needsMaterial(COIN));
        
        /* Disable coins to the watch tower and verify that it doesn't need coins*/
        watchTower0.disablePromotions();
        
        assertFalse(watchTower0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedWatchTowerCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Connect headquarter and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());
        
        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Evacuate the watch tower and verify that the military leaves the watch tower */
        assertTrue(m.isInsideBuilding());
        
        watchTower0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        assertEquals(watchTower0.getHostedMilitary(), 0);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Connect headquarter and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());
        
        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Evacuate the watch tower */
        assertTrue(m.isInsideBuilding());
        
        watchTower0.evacuate();
        
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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Evacuate the watch tower */
        assertTrue(m.isInsideBuilding());
        
        watchTower0.evacuate();
        
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
    public void testNoMilitaryIsDispatchedToEvacuatedWatchTower() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Connect headquarters and watch tower */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), watchTower0.getFlag());
        
        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Evacuate the watch tower */
        watchTower0.evacuate();

        /* Verify that no militaries are assigned to the watch tower */
        for (int i = 0; i < 200; i++) {
            assertEquals(watchTower0.getHostedMilitary(), 0);
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

        /* Placing watch tower */
        Point point22 = new Point(6, 22);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point22);
        
        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Evacuate the watch tower */
        assertTrue(m.isInsideBuilding());
        
        watchTower0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        
        /* Wait for the evacuated military to return to the storage */
        assertEquals(m.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, m.getTarget());
        
        assertTrue(m.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    
        /* Cancel evacuation */
        assertFalse(watchTower0.needsMilitaryManning());
        
        watchTower0.cancelEvacuation();
        
        assertTrue(watchTower0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenWatchTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing watch tower */
        Point point26 = new Point(8, 8);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower */
        Utils.occupyBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Destroy the watch tower */
        Worker ww = watchTower0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), watchTower0.getPosition());

        watchTower0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenWatchTowerIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing watch tower */
        Point point26 = new Point(8, 8);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        /* Connect the watch tower with the headquarter */
        map.placeAutoSelectedRoad(player0, watchTower0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Occupy the watch tower */
        Utils.occupyBuilding(new Military(PRIVATE_RANK, map), watchTower0, map);
        
        /* Destroy the watch tower */
        Worker ww = watchTower0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), watchTower0.getPosition());

        watchTower0.tearDown();

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
    public void testProductionCannotBeResumedInWatchTower() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing watch tower */
        Point point26 = new Point(8, 8);
        Building watchTower0 = map.placeBuilding(new WatchTower(player0), point26);

        /* Finish construction of the watch tower */
        Utils.constructHouse(watchTower0, map);

        /* Verify that production cannot be resumed in watch tower */
        watchTower0.resumeProduction();
    }
}
