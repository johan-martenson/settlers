/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Fortress;
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
public class TestFortress {
    
    @Test
    public void testFortressNeedsFourPlancksAndSevenStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Deliver two plancks and three stones*/
        Cargo cargo = new Cargo(PLANCK, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);

        /* Verify that this is enough to construct the fortress */
        for (int i = 0; i < 200; i++) {
            assertTrue(fortress0.underConstruction());
            
            map.stepTime();
        }

        assertTrue(fortress0.ready());
    }
    
    @Test
    public void testFortressCannotBeConstructedWithOnePlanckTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Deliver one planck and three stones */
        Cargo cargo = new Cargo(PLANCK, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);

        /* Verify that the fortress needs a planck */
        assertTrue(fortress0.needsMaterial(PLANCK));
        
        /* Verify that this is not enough to construct the fortress */
        for (int i = 0; i < 500; i++) {
            assertTrue(fortress0.underConstruction());

            map.stepTime();
        }

        assertFalse(fortress0.ready());
    }

    @Test
    public void testFortressCannotBeConstructedWithOneStoneTooLittle() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Deliver four plancks and six stones */
        Cargo cargo = new Cargo(PLANCK, map);

        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        Cargo stoneCargo = new Cargo(STONE, map);

        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);
        fortress0.putCargo(stoneCargo);

        /* Verify that the fortress needs a stone */
        assertTrue(fortress0.needsMaterial(STONE));

        /* Verify that this is not enough to construct the fortress */
        for (int i = 0; i < 500; i++) {
            assertTrue(fortress0.underConstruction());

            map.stepTime();
        }

        assertFalse(fortress0.ready());
    }

    @Test
    public void testFortressGetPopulatedWhenFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(7, 21);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the fortress to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0, map);

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
        
        /* Wait for the military to reach the fortress */
        assertEquals(m.getTarget(), fortress0.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, fortress0.getPosition());
        
        assertTrue(m.isInsideBuilding());
    }

    @Test
    public void testBorderIsNotExtendedWhenFortressIsFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(5, 25);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Placing road between (7, 21) and (6, 4) */
        Point point23 = new Point(6, 24);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the fortress to finish construction */
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));

        Utils.fastForwardUntilBuildingIsConstructed(fortress0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
    }
    
    @Test
    public void testBorderIsExtendedWhenFortressIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 24);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Placing road between (7, 23) and (6, 4) */
        Point point23 = new Point(7, 23);
        Point point36 = new Point(6, 4);
        Road road0 = map.placeAutoSelectedRoad(player0, point23, point36);

        /* Wait for the fortress to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0, map);

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
        
        /* Verify that the border is extended when the military reaches the fortress */
        assertEquals(m.getTarget(), fortress0.getPosition());
        assertTrue(player0.getBorders().get(0).contains(new Point(5, 25)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, fortress0.getPosition());
        
        assertFalse(player0.getBorders().get(0).contains(new Point(5, 25)));

        assertTrue(player0.getBorders().get(0).contains(new Point(5, 41)));
    }

    @Test
    public void testFortressOnlyNeedsNineMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress with nine militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);

        /* Verify that the fortress does not need another military */
        assertFalse(fortress0.needsMilitaryManning());
    }

    @Test
    public void testFortressCannotHoldMilitariesBeforeFinished() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Verify that the fortress can't hold militaries before it's finished */
        assertFalse(fortress0.needsMilitaryManning());
        
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, fortress0);
        
        try {
            fortress0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
    }

    @Test
    public void testFortressCannotHoldMoreThanNineMilitaries() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress with nine militaries */
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);

        /* Verify that the fortress does not need another military */
        Military military = new Military(PRIVATE_RANK, map);
        
        map.placeWorker(military, fortress0);
        
        try {
            fortress0.deployMilitary(military);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertFalse(military.isInsideBuilding());
        assertEquals(fortress0.getHostedMilitary(), 9);
    }

    @Test
    public void testFortressRadiusIsCorrect() throws Exception{

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 50, 50);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);

        /* Verify that the border is grown with the correct radius */
        assertTrue(player0.getBorders().get(0).contains(new Point(6, 24)));
        
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);

        assertTrue(player0.getBorders().get(0).contains(new Point(6, 40)));
    }
    
    @Test
    public void testFortressNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);

        assertTrue(fortress0.needsMaterial(COIN));
    }
    
    @Test
    public void testUnfinishedFortressNotNeedsCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        assertFalse(fortress0.needsMaterial(COIN));
    }
    
    @Test
    public void testFortressCanHoldFourCoins() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);

        assertTrue(fortress0.needsMaterial(COIN));
        
        /* Deliver four coins to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        /* Verify that the fortress can't hold another coin */
        assertFalse(fortress0.needsMaterial(COIN));
        assertEquals(fortress0.getAmount(COIN), 4);

        try {
            fortress0.putCargo(cargo);
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(fortress0.getAmount(COIN), 4);
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);

        /* Wait until the fortress is populated */
        Utils.fastForward(200, map);
        
        /* Occupy the fortress with one private */
        Military military = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);

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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(fortress0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(fortress0.getAmount(COIN), 0);
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);
        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(SERGEANT_RANK, map), fortress0, map);
        
        /* Verify that the promotion consumes the coin */
        assertEquals(fortress0.getAmount(COIN), 2);
        
        Utils.fastForward(100, map);

        assertEquals(fortress0.getAmount(COIN), 1);
    }

    @Test
    public void testFortressWithNoPromotionPossibleDoesNotConsumeCoin() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        Cargo cargo = new Cargo(COIN, map);
        
        fortress0.putCargo(cargo);

        /* Occupy the fortress with one private */
        Military military1 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), fortress0, map);
        Military military2 = Utils.occupyMilitaryBuilding(new Military(GENERAL_RANK, map), fortress0, map);
        
        /* Verify that coin is not consumed */
        assertEquals(fortress0.getAmount(COIN), 1);
        
        Utils.fastForward(100, map);

        assertEquals(fortress0.getAmount(COIN), 1);
    }
    
    @Test
    public void testCanDisableCoinsToFortress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        Utils.constructHouse(fortress0, map);
        
        /* Deliver one coin to the fortress */
        assertTrue(fortress0.needsMaterial(COIN));
        
        /* Disable coins to the fortress and verify that it doesn't need coins*/
        fortress0.disablePromotions();
        
        assertFalse(fortress0.needsMaterial(COIN));
    }

    @Test
    public void testOccupiedFortressCanBeEvacuated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Connect headquarter and fortress */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());
        
        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Evacuate the fortress and verify that the military leaves the fortress */
        assertTrue(m.isInsideBuilding());
        
        fortress0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());        
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Connect headquarter and fortress */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());
        
        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Evacuate the fortress */
        assertTrue(m.isInsideBuilding());
        
        fortress0.evacuate();
        
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Evacuate the fortress */
        assertTrue(m.isInsideBuilding());
        
        fortress0.evacuate();
        
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
    public void testNoMilitaryIsDispatchedToEvacuatedFortress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Connect headquarters and fortress */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());
        
        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Evacuate the fortress */
        fortress0.evacuate();

        /* Verify that no militaries are assigned to the fortress */
        for (int i = 0; i < 200; i++) {
            assertEquals(fortress0.getHostedMilitary(), 0);
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

        /* Placing fortress */
        Point point22 = new Point(6, 22);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point22);
        
        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Military m = Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Evacuate the fortress */
        assertTrue(m.isInsideBuilding());
        
        fortress0.evacuate();
        
        map.stepTime();
        
        assertFalse(m.isInsideBuilding());
        
        /* Wait for the evacuated military to return to the storage */
        assertEquals(m.getTarget(), headquarter0.getPosition());
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, m, m.getTarget());
        
        assertTrue(m.isInsideBuilding());
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    
        /* Cancel evacuation */
        assertFalse(fortress0.needsMilitaryManning());
        
        fortress0.cancelEvacuation();
        
        assertTrue(fortress0.needsMilitaryManning());
    }

    @Test
    public void testMilitaryGoesBackToStorageWhenFortressIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fortress */
        Point point26 = new Point(8, 8);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point26);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Destroy the fortress */
        Worker ww = fortress0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), fortress0.getPosition());

        fortress0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(ww.isInsideBuilding());
        assertEquals(ww.getTarget(), headquarter0.getPosition());
    
        int amount = headquarter0.getAmount(PRIVATE);
        
        Utils.fastForwardUntilWorkerReachesPoint(map, ww, headquarter0.getPosition());

        /* Verify that the military is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PRIVATE), amount + 1);
    }

    @Test
    public void testMilitaryGoesBackOnToStorageOnRoadsIfPossibleWhenFortressIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fortress */
        Point point26 = new Point(8, 8);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point26);

        /* Connect the fortress with the headquarter */
        map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());
        
        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyBuilding(new Military(PRIVATE_RANK, map), fortress0, map);
        
        /* Destroy the fortress */
        Worker ww = fortress0.getWorker();
        
        assertTrue(ww.isInsideBuilding());
        assertEquals(ww.getPosition(), fortress0.getPosition());

        fortress0.tearDown();

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
    public void testProductionCannotBeResumedInFortress() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0");
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing fortress */
        Point point26 = new Point(8, 8);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point26);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Verify that production cannot be resumed in fortress */
        fortress0.resumeProduction();
    }
}
