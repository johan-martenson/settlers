package org.appland.settlers.test;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Military.Rank.*;
import static org.junit.Assert.*;

public class TestMilitarySettings {

    // Test strength of soldiers when populating new military buildings. Also existing military buildings

    @Test
    public void tesDefaultStrengthWhenPopulatingMilitaryBuildings() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify the default strength of population of new military buildings */
        assertEquals(player0.getStrengthOfSoldiersPopulatingBuildings(), 5);
    }

    @Test
    public void testPrivateOccupiesNewBarracksWithPopulatingStrengthSetToMinimum() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(0);

        assertEquals(player0.getStrengthOfSoldiersPopulatingBuildings(), 0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.PRIVATE_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.PRIVATE_RANK);
    }

    @Test
    public void testPrivateFirstClassOccupiesNewBarracksWithPopulatingStrengthSetToLow() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(2);

        assertEquals(player0.getStrengthOfSoldiersPopulatingBuildings(), 2);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.PRIVATE_FIRST_CLASS_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testSergeantOccupiesNewBarracksWithPopulatingStrengthSetToMedium() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(5);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.SERGEANT_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.SERGEANT_RANK);
    }

    @Test
    public void testOfficerOccupiesNewBarracksWithPopulatingStrengthSetToHigher() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(7);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.OFFICER_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.OFFICER_RANK);
    }

    @Test
    public void testGeneralOccupiesNewBarracksWithPopulatingStrengthSetToMaximum() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(10);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.GENERAL_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.GENERAL_RANK);
    }

    @Test
    public void testSergeantOccupiesNewBarracksWithPopulatingStrengthSetToMaximumIfNoHigherRankIsAvailable() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 5);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 5);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 5);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(10);

        assertEquals(player0.getStrengthOfSoldiersPopulatingBuildings(), 10);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.SERGEANT_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.SERGEANT_RANK);
    }

    @Test
    public void testOfficerOccupiesNewBarracksWithPopulatingStrengthSetToMinimumIfNoLowerRankIsAvailable() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Add soldiers of each rank to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 5);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 5);

        /* Set strength of population of new military buildings to minimum */
        player0.setStrengthOfSoldiersPopulatingBuildings(0);

        assertEquals(player0.getStrengthOfSoldiersPopulatingBuildings(), 0);

        /* Place barracks */
        Point point1 = new Point(6, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road between (7, 21) and (6, 4) */
        Road road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Verify that two privates are sent to populate the barracks */
        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 2, player0);

        assertEquals(soldiers.size(), 2);
        assertEquals(soldiers.getFirst().getRank(), Military.Rank.OFFICER_RANK);
        assertEquals(soldiers.get(1).getRank(), Military.Rank.OFFICER_RANK);
    }

    // Test strength of defenders
    // - Attack when preferred soldier is not available to defend

    @Test
    public void testDefaultDefenseStrength() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify the default defense strength */
        assertEquals(player0.getDefenseStrength(), 5);
    }

    @Test
    public void testPrivateFromFortressDefendsWhenDefenseStrengthIsMinimum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(0);

        assertEquals(player1.getDefenseStrength(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), fortress.getFlag().getPosition());
        assertFalse(fortress.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress.getFlag().getPosition());
        assertEquals(fortress.getNumberOfHostedMilitary(), 4);

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getRank(), PRIVATE_RANK);
    }

    @Test
    public void testPrivateFirstClassFromFortressDefendsWhenDefenseStrengthIsLow() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(3);

        assertEquals(player1.getDefenseStrength(), 3);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), fortress.getFlag().getPosition());
        assertFalse(fortress.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress.getFlag().getPosition());
        assertEquals(fortress.getNumberOfHostedMilitary(), 4);

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testSergeantFromFortressDefendsWhenDefenseStrengthIsMedium() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(6);

        assertEquals(player1.getDefenseStrength(), 6);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), fortress.getFlag().getPosition());
        assertFalse(fortress.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress.getFlag().getPosition());
        assertEquals(fortress.getNumberOfHostedMilitary(), 4);

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testOfficerFromFortressDefendsWhenDefenseStrengthIsHigh() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(8);

        assertEquals(player1.getDefenseStrength(), 8);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), fortress.getFlag().getPosition());
        assertFalse(fortress.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress.getFlag().getPosition());
        assertEquals(fortress.getNumberOfHostedMilitary(), 4);

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getRank(), OFFICER_RANK);
    }

    @Test
    public void testGeneralFromFortressDefendsWhenDefenseStrengthIsMaximum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(9);

        assertEquals(player1.getDefenseStrength(), 9);

        /* Order an attack */
        assertTrue(player0.canAttack(fortress));

        player0.attack(fortress, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), fortress.getFlag().getPosition());
        assertFalse(fortress.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, fortress.getFlag().getPosition());

        assertEquals(attacker.getPosition(), fortress.getFlag().getPosition());
        assertEquals(fortress.getNumberOfHostedMilitary(), 4);

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getRank(), GENERAL_RANK);
    }

    @Test
    public void testPrivateFromSurroundingFortressDefendsWhenDefenseStrengthIsMinimum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place fortress for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Place barracks for player 1 */
        Point point4 = new Point(17, 11);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        /* Connect the barracks with the headquarters and wait for it to get built and occupied */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Evacuate the barracks and wait for the soldier to go back to the headquarters */
        barracks1.evacuate();

        Military soldier = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(soldier.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter1.getPosition());

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(0);

        assertEquals(player1.getDefenseStrength(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves from a surrounding building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertEquals(fortress.getNumberOfHostedMilitary(), 4);
        assertEquals(defender.getHome(), fortress);
        assertNotNull(defender);
        assertEquals(defender.getRank(), PRIVATE_RANK);
    }

    @Test
    public void testPrivateFirstClassFromSurroundingFortressDefendsWhenDefenseStrengthIsMinimum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place fortress for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Place barracks for player 1 */
        Point point4 = new Point(17, 11);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        /* Connect the barracks with the headquarters and wait for it to get built and occupied */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Evacuate the barracks and wait for the soldier to go back to the headquarters */
        barracks1.evacuate();

        Military soldier = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(soldier.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter1.getPosition());

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(3);

        assertEquals(player1.getDefenseStrength(), 3);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves from a surrounding building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertEquals(fortress.getNumberOfHostedMilitary(), 4);
        assertEquals(defender.getHome(), fortress);
        assertNotNull(defender);
        assertEquals(defender.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testSergeantFromSurroundingFortressDefendsWhenDefenseStrengthIsMinimum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place fortress for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Place barracks for player 1 */
        Point point4 = new Point(17, 11);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        /* Connect the barracks with the headquarters and wait for it to get built and occupied */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Evacuate the barracks and wait for the soldier to go back to the headquarters */
        barracks1.evacuate();

        Military soldier = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(soldier.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter1.getPosition());

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(5);

        assertEquals(player1.getDefenseStrength(), 5);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves from a surrounding building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertEquals(fortress.getNumberOfHostedMilitary(), 4);
        assertEquals(defender.getHome(), fortress);
        assertNotNull(defender);
        assertEquals(defender.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testOfficerFromSurroundingFortressDefendsWhenDefenseStrengthIsMinimum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place fortress for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Place barracks for player 1 */
        Point point4 = new Point(17, 11);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        /* Connect the barracks with the headquarters and wait for it to get built and occupied */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Evacuate the barracks and wait for the soldier to go back to the headquarters */
        barracks1.evacuate();

        Military soldier = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(soldier.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter1.getPosition());

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(8);

        assertEquals(player1.getDefenseStrength(), 8);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves from a surrounding building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertEquals(fortress.getNumberOfHostedMilitary(), 4);
        assertEquals(defender.getHome(), fortress);
        assertNotNull(defender);
        assertEquals(defender.getRank(), OFFICER_RANK);
    }

    @Test
    public void testGeneralFromSurroundingFortressDefendsWhenDefenseStrengthIsMinimum() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place fortress for player 1 */
        Point point3 = new Point(23, 15);
        var fortress = map.placeBuilding(new Fortress(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(fortress);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's fortress with each type of soldier */
        assertTrue(fortress.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress);
        Utils.occupyMilitaryBuilding(PRIVATE_FIRST_CLASS_RANK, fortress);
        Utils.occupyMilitaryBuilding(SERGEANT_RANK, fortress);
        Utils.occupyMilitaryBuilding(OFFICER_RANK, fortress);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, fortress);

        /* Place barracks for player 1 */
        Point point4 = new Point(17, 11);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        /* Connect the barracks with the headquarters and wait for it to get built and occupied */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Evacuate the barracks and wait for the soldier to go back to the headquarters */
        barracks1.evacuate();

        Military soldier = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(soldier.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, soldier, headquarter1.getPosition());

        /* Set defense strength for player 1 */
        player1.setDefenseStrength(9);

        assertEquals(player1.getDefenseStrength(), 9);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves from a surrounding building to defend when the attacker reaches the flag */
        assertEquals(fortress.getNumberOfHostedMilitary(), 5);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());
        assertFalse(barracks1.isUnderAttack());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that a soldier of the right type goes out to defend */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertEquals(fortress.getNumberOfHostedMilitary(), 4);
        assertEquals(defender.getHome(), fortress);
        assertNotNull(defender);
        assertEquals(defender.getRank(), GENERAL_RANK);
    }

    // Test amount of defenders from surrounding buildings

    // Test amount of soldiers not being available for attacking

    // High/low population of military buildings far from the border

    // High/low population of military buildings closer to the border

    // High/low population of military buildings close to the border
}
