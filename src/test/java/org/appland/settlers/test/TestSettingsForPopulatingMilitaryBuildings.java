package org.appland.settlers.test;

import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Soldier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.assertEquals;

public class TestSettingsForPopulatingMilitaryBuildings {

    @Test
    public void tesDefaultPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify the default strength of population of new military buildings */
        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 10);
    }

    @Test
    public void tesMinimumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the population of military buildings close to the border */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 0);

        /* Place fortress */
        var point1 = new Point(19, 5);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the fortress with the headquarters and wait for it to get constructed */
        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        /* Verify that only one soldier is sent out to occupy the fortress */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        assertEquals(fortress.getHostedSoldiers().size(), 1);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 1);
    }

    @Test
    public void tesMaximumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the population of military buildings close to the border */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 10);

        /* Place fortress */
        var point1 = new Point(19, 5);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the fortress with the headquarters and wait for it to get constructed */
        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        /* Verify that only one soldier is sent out to occupy the fortress */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 9);
    }

    @Test
    public void tesMediumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the population of military buildings close to the border */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(5);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 5);

        /* Place fortress */
        var point1 = new Point(19, 5);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the fortress with the headquarters and wait for it to get constructed */
        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        /* Verify that only one soldier is sent out to occupy the fortress */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);
    }

    @Test
    public void testChangeMinimumToMediumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the population of military buildings close to the border */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 0);

        /* Place fortress */
        var point1 = new Point(19, 5);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the fortress with the headquarters and wait for it to get constructed */
        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        /* Only one soldier is sent out to occupy the fortress */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 1);

        /* Verify that more soldiers are sent out to the fortress when the setting is changed */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(5);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);
    }

    @Test
    public void testChangeMaximumToMediumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the population of military buildings close to the border */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 10);

        /* Place fortress */
        var point1 = new Point(19, 5);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the fortress with the headquarters and wait for it to get constructed */
        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        /* Only one soldier is sent out to occupy the fortress */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 9);

        /* Verify that more soldiers are sent out to the fortress when the setting is changed */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(5);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);
    }

    @Test
    public void testPreferredSoldierStaysWhenChangeMaximumToMediumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Military settings - no reserve and prefer strong population */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        player0.setStrengthOfSoldiersPopulatingBuildings(10);

        /* Set the population of military buildings close to the border */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 10);

        /* Place fortress */
        var point1 = new Point(19, 5);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        /* Connect the fortress with the headquarters and wait for it to get constructed */
        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(fortress);

        /* Only one soldier is sent out to occupy the fortress */
        Utils.waitForMilitaryBuildingToGetPopulated(fortress);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 1);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 2);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 2);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 2);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 2);

        /* Verify that more soldiers are sent out to the fortress when the setting is changed */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(5);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);

        Utils.fastForward(200, map);

        assertEquals(fortress.getHostedSoldiers().size(), 5);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 0);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK), 0);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.SERGEANT_RANK), 1);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.OFFICER_RANK), 2);
        assertEquals(fortress.getHostedSoldiersWithRank(Soldier.Rank.GENERAL_RANK), 2);
    }

    // TODO: test least wanted defender leaves when there are too many defenders in the building
    // TODO: all same tests for closer to border and far from border
    // TODO: test that they are not affecting each other
}
