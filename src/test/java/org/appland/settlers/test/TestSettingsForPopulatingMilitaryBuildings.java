package org.appland.settlers.test;

import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Soldier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.assertEquals;

public class TestSettingsForPopulatingMilitaryBuildings {

    @Test
    public void testDefaultPopulationOfMilitaryBuildings() throws InvalidUserActionException {

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
        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 10);
        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 10);
    }

    @Test
    public void testMinimumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

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
    public void testMaximumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

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
    public void testMediumPopulationOfMilitaryBuildingsCloseToBorder() throws InvalidUserActionException {

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

    @Test
    public void testSettingPopulationCloseToBorderDoesNotAffectOtherMilitaryBuildings () throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 50);
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

        /* Set the population of military buildings close to the border to minimum */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 0);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(19, 15);
        var point2 = new Point(17, 23);
        var point3 = new Point(17, 13);
        var point4 = new Point(23, 23);
        var point5 = new Point(23, 13);

        var barracks0 = map.placeBuilding(new Barracks(player0), point1);
        var barracks1 = map.placeBuilding(new Barracks(player0), point2);
        var barracks2 = map.placeBuilding(new Barracks(player0), point3);
        var barracks3 = map.placeBuilding(new Barracks(player0), point4);
        var barracks4 = map.placeBuilding(new Barracks(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, barracks2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, barracks3.getFlag(), barracks0.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, barracks4.getFlag(), barracks0.getFlag());

        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1, barracks2, barracks3, barracks4);

        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1, barracks2, barracks3, barracks4);

        /* Verify that the setting for population of the barracks close to the border does not affect the center barracks */
        assertEquals(barracks0.getHostedSoldiers().size(), 2);
        assertEquals(barracks1.getHostedSoldiers().size(), 1);
        assertEquals(barracks2.getHostedSoldiers().size(), 1);
        assertEquals(barracks3.getHostedSoldiers().size(), 1);
        assertEquals(barracks4.getHostedSoldiers().size(), 1);

        /* Set population close to the border to max */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(10);

        Utils.fastForward(300, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 2);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);

        /* Verify that reducing the population at the border does not affect the barracks in the center */
        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(0);

        Utils.fastForward(300, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 2);
        assertEquals(barracks1.getHostedSoldiers().size(), 1);
        assertEquals(barracks2.getHostedSoldiers().size(), 1);
        assertEquals(barracks3.getHostedSoldiers().size(), 1);
        assertEquals(barracks4.getHostedSoldiers().size(), 1);
    }

    @Test
    public void testMinimumPopulationOfMilitaryBuildingsAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 50);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 0);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(19, 15);
        var point2 = new Point(17, 23);
        var point3 = new Point(17, 13);
        var point4 = new Point(23, 23);
        var point5 = new Point(23, 13);

        var barracks0 = map.placeBuilding(new Barracks(player0), point1);
        var barracks1 = map.placeBuilding(new Barracks(player0), point2);
        var barracks2 = map.placeBuilding(new Barracks(player0), point3);
        var barracks3 = map.placeBuilding(new Barracks(player0), point4);
        var barracks4 = map.placeBuilding(new Barracks(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, barracks2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, barracks3.getFlag(), barracks0.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, barracks4.getFlag(), barracks0.getFlag());

        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1, barracks2, barracks3, barracks4);

        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1, barracks2, barracks3, barracks4);

        /* Verify that the barracks in the center gets minimal population and other barracks are not affected */
        Utils.fastForward(300, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 1);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);
    }

    @Test
    public void testMaximumPopulationOfMilitaryBuildingsAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 50);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 10);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(19, 15);
        var point2 = new Point(17, 23);
        var point3 = new Point(17, 13);
        var point4 = new Point(23, 23);
        var point5 = new Point(23, 13);

        var barracks0 = map.placeBuilding(new Barracks(player0), point1);
        var barracks1 = map.placeBuilding(new Barracks(player0), point2);
        var barracks2 = map.placeBuilding(new Barracks(player0), point3);
        var barracks3 = map.placeBuilding(new Barracks(player0), point4);
        var barracks4 = map.placeBuilding(new Barracks(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, barracks2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, barracks3.getFlag(), barracks0.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, barracks4.getFlag(), barracks0.getFlag());

        Utils.waitForBuildingsToBeConstructed(barracks0, barracks1, barracks2, barracks3, barracks4);

        Utils.waitForMilitaryBuildingsToGetPopulated(barracks0, barracks1, barracks2, barracks3, barracks4);

        /* Verify that the barracks in the center gets minimal population and other barracks are not affected */
        Utils.fastForward(300, map);

        assertEquals(barracks0.getHostedSoldiers().size(), 2);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);
    }

    @Test
    public void testMediumPopulationOfMilitaryBuildingsAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 50);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(5);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 5);

        /* Place center guard house */
        var point1 = new Point(23, 15);
        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());

        /* Wait for it to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(guardHouse);

        Utils.waitForMilitaryBuildingToGetPopulated(guardHouse);

        /* Place barracks - one in center and four around it */
        var point2 = new Point(15, 23);
        var point3 = new Point(17, 11);
        var point4 = new Point(27, 23);
        var point5 = new Point(27, 11);
        var point6 = new Point(35, 15);

        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point2);
        var guardHouse2 = map.placeBuilding(new GuardHouse(player0), point3);
        var guardHouse3 = map.placeBuilding(new GuardHouse(player0), point4);
        var guardHouse4 = map.placeBuilding(new GuardHouse(player0), point5);
        var guardHouse5 = map.placeBuilding(new GuardHouse(player0), point6);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road1 = map.placeAutoSelectedRoad(player0, guardHouse1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, guardHouse2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, guardHouse3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, guardHouse4.getFlag(), guardHouse.getFlag());
        var road5 = map.placeAutoSelectedRoad(player0, guardHouse5.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4, guardHouse5);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4, guardHouse5);

        /* Verify that the barracks in the center gets minimal population and other barracks are not affected */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 2);
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
    }

    @Test
    public void testChangeMinimumToMediumPopulationOfMilitaryBuildingsAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 50);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 0);

        /* Start with the center guard house */
        var point1 = new Point(23, 15);
        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(guardHouse);

        Utils.waitForMilitaryBuildingToGetPopulated(guardHouse);

        /* Place barracks - one in center and four around it */
        var point2 = new Point(21, 19);
        var point3 = new Point(21, 13);
        var point4 = new Point(31, 19);
        var point5 = new Point(31, 13);

        var barracks1 = map.placeBuilding(new Barracks(player0), point2);
        var barracks2 = map.placeBuilding(new Barracks(player0), point3);
        var barracks3 = map.placeBuilding(new Barracks(player0), point4);
        var barracks4 = map.placeBuilding(new Barracks(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, barracks2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, barracks3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, barracks4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, barracks1, barracks2, barracks3, barracks4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, barracks1, barracks2, barracks3, barracks4);

        /* Wait for the barracks in the center to get minimal population */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 1);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);

        /* Verify that the population is increased (only) in the center guard house when the setting is changed */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(5);

        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 2);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);
    }

    @Test
    public void testChangeMaximumToMediumPopulationOfMilitaryBuildingsAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 50);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 10);

        /* Start with the center guard house */
        var point1 = new Point(23, 15);
        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(guardHouse);

        Utils.waitForMilitaryBuildingToGetPopulated(guardHouse);

        /* Place barracks - one in center and four around it */
        var point2 = new Point(21, 19);
        var point3 = new Point(21, 13);
        var point4 = new Point(31, 19);
        var point5 = new Point(31, 13);

        var barracks1 = map.placeBuilding(new Barracks(player0), point2);
        var barracks2 = map.placeBuilding(new Barracks(player0), point3);
        var barracks3 = map.placeBuilding(new Barracks(player0), point4);
        var barracks4 = map.placeBuilding(new Barracks(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road1 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, barracks2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, barracks3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, barracks4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, barracks1, barracks2, barracks3, barracks4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, barracks1, barracks2, barracks3, barracks4);

        /* Wait for the barracks in the center to get minimal population */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 3);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);

        /* Verify that the population is increased (only) in the center guard house when the setting is changed */
        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(5);

        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 2);
        assertEquals(barracks1.getHostedSoldiers().size(), 2);
        assertEquals(barracks2.getHostedSoldiers().size(), 2);
        assertEquals(barracks3.getHostedSoldiers().size(), 2);
        assertEquals(barracks4.getHostedSoldiers().size(), 2);
    }

    @Test
    public void testMinimumPopulationOfMilitaryBuildingsFarAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(39, 35);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Fill in lots of resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 500);
        Utils.adjustInventoryTo(headquarter0, STONE, 500);
        Utils.adjustInventoryTo(headquarter0, BUILDER, 100);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 0);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(43, 35);
        var point2 = new Point(41, 43);
        var point3 = new Point(41, 33);
        var point4 = new Point(47, 43);
        var point5 = new Point(47, 33);

        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point2);
        var guardHouse2 = map.placeBuilding(new GuardHouse(player0), point3);
        var guardHouse3 = map.placeBuilding(new GuardHouse(player0), point4);
        var guardHouse4 = map.placeBuilding(new GuardHouse(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, guardHouse1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, guardHouse2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, guardHouse3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, guardHouse4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        /* Place one ring of fortresses */
        var point6 = new Point(29, 43);
        var point7 = new Point(53, 43);
        var point8 = new Point(53, 29);
        var point9 = new Point(29, 29);

        var fortress = map.placeBuilding(new Fortress(player0), point6);
        var fortress1 = map.placeBuilding(new Fortress(player0), point7);
        var fortress2 = map.placeBuilding(new Fortress(player0), point8);
        var fortress3 = map.placeBuilding(new Fortress(player0), point9);

        var road5 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());
        var road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), fortress1.getFlag());
        var road7 = map.placeAutoSelectedRoad(player0, fortress1.getFlag(), fortress2.getFlag());
        var road8 = map.placeAutoSelectedRoad(player0, fortress3.getFlag(), guardHouse3.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress, fortress1, fortress2, fortress3);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress, fortress1, fortress2, fortress3);

        /* Place more fortresses */
        var point10 = new Point(19, 29);
        var point11 = new Point(21, 21);
        var point12 = new Point(45, 21);
        var point13 = new Point(59, 35);

        var fortress4 = map.placeBuilding(new Fortress(player0), point10);
        var fortress5 = map.placeBuilding(new Fortress(player0), point11);
        var fortress6 = map.placeBuilding(new Fortress(player0), point12);
        var fortress7 = map.placeBuilding(new Fortress(player0), point13);

        var road9 = map.placeAutoSelectedRoad(player0, fortress4.getFlag(), fortress3.getFlag());
        var road10 = map.placeAutoSelectedRoad(player0, fortress5.getFlag(), fortress4.getFlag());
        var road11 = map.placeAutoSelectedRoad(player0, fortress6.getFlag(), fortress5.getFlag());
        var road12 = map.placeAutoSelectedRoad(player0, fortress7.getFlag(), fortress6.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress4, fortress5, fortress6, fortress7);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress4, fortress5, fortress6, fortress7);

        /* Verify that the barracks in the center gets minimal population and others are not affected */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 1);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 1); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);
    }

    @Test
    public void testMaximumPopulationOfMilitaryBuildingsFarAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(39, 35);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Fill in lots of resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 500);
        Utils.adjustInventoryTo(headquarter0, STONE, 500);
        Utils.adjustInventoryTo(headquarter0, BUILDER, 100);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 10);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(43, 35);
        var point2 = new Point(41, 43);
        var point3 = new Point(41, 33);
        var point4 = new Point(47, 43);
        var point5 = new Point(47, 33);

        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point2);
        var guardHouse2 = map.placeBuilding(new GuardHouse(player0), point3);
        var guardHouse3 = map.placeBuilding(new GuardHouse(player0), point4);
        var guardHouse4 = map.placeBuilding(new GuardHouse(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, guardHouse1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, guardHouse2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, guardHouse3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, guardHouse4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        /* Place one ring of fortresses */
        var point6 = new Point(29, 43);
        var point7 = new Point(53, 43);
        var point8 = new Point(53, 29);
        var point9 = new Point(29, 29);

        var fortress = map.placeBuilding(new Fortress(player0), point6);
        var fortress1 = map.placeBuilding(new Fortress(player0), point7);
        var fortress2 = map.placeBuilding(new Fortress(player0), point8);
        var fortress3 = map.placeBuilding(new Fortress(player0), point9);

        var road5 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());
        var road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), fortress1.getFlag());
        var road7 = map.placeAutoSelectedRoad(player0, fortress1.getFlag(), fortress2.getFlag());
        var road8 = map.placeAutoSelectedRoad(player0, fortress3.getFlag(), guardHouse3.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress, fortress1, fortress2, fortress3);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress, fortress1, fortress2, fortress3);

        /* Place more fortresses */
        var point10 = new Point(19, 29);
        var point11 = new Point(21, 21);
        var point12 = new Point(45, 21);
        var point13 = new Point(59, 35);

        var fortress4 = map.placeBuilding(new Fortress(player0), point10);
        var fortress5 = map.placeBuilding(new Fortress(player0), point11);
        var fortress6 = map.placeBuilding(new Fortress(player0), point12);
        var fortress7 = map.placeBuilding(new Fortress(player0), point13);

        var road9 = map.placeAutoSelectedRoad(player0, fortress4.getFlag(), fortress3.getFlag());
        var road10 = map.placeAutoSelectedRoad(player0, fortress5.getFlag(), fortress4.getFlag());
        var road11 = map.placeAutoSelectedRoad(player0, fortress6.getFlag(), fortress5.getFlag());
        var road12 = map.placeAutoSelectedRoad(player0, fortress7.getFlag(), fortress6.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress4, fortress5, fortress6, fortress7);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress4, fortress5, fortress6, fortress7);

        /* Verify that the barracks in the center gets minimal population and others are not affected */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 3); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);
    }

    @Test
    public void testMediumPopulationOfMilitaryBuildingsFarAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(39, 35);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Fill in lots of resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 500);
        Utils.adjustInventoryTo(headquarter0, STONE, 500);
        Utils.adjustInventoryTo(headquarter0, BUILDER, 100);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(5);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 5);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(43, 35);
        var point2 = new Point(41, 43);
        var point3 = new Point(41, 33);
        var point4 = new Point(47, 43);
        var point5 = new Point(47, 33);

        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point2);
        var guardHouse2 = map.placeBuilding(new GuardHouse(player0), point3);
        var guardHouse3 = map.placeBuilding(new GuardHouse(player0), point4);
        var guardHouse4 = map.placeBuilding(new GuardHouse(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, guardHouse1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, guardHouse2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, guardHouse3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, guardHouse4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        /* Place one ring of fortresses */
        var point6 = new Point(29, 43);
        var point7 = new Point(53, 43);
        var point8 = new Point(53, 29);
        var point9 = new Point(29, 29);

        var fortress = map.placeBuilding(new Fortress(player0), point6);
        var fortress1 = map.placeBuilding(new Fortress(player0), point7);
        var fortress2 = map.placeBuilding(new Fortress(player0), point8);
        var fortress3 = map.placeBuilding(new Fortress(player0), point9);

        var road5 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());
        var road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), fortress1.getFlag());
        var road7 = map.placeAutoSelectedRoad(player0, fortress1.getFlag(), fortress2.getFlag());
        var road8 = map.placeAutoSelectedRoad(player0, fortress3.getFlag(), guardHouse3.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress, fortress1, fortress2, fortress3);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress, fortress1, fortress2, fortress3);

        /* Place more fortresses */
        var point10 = new Point(19, 29);
        var point11 = new Point(21, 21);
        var point12 = new Point(45, 21);
        var point13 = new Point(59, 35);

        var fortress4 = map.placeBuilding(new Fortress(player0), point10);
        var fortress5 = map.placeBuilding(new Fortress(player0), point11);
        var fortress6 = map.placeBuilding(new Fortress(player0), point12);
        var fortress7 = map.placeBuilding(new Fortress(player0), point13);

        var road9 = map.placeAutoSelectedRoad(player0, fortress4.getFlag(), fortress3.getFlag());
        var road10 = map.placeAutoSelectedRoad(player0, fortress5.getFlag(), fortress4.getFlag());
        var road11 = map.placeAutoSelectedRoad(player0, fortress6.getFlag(), fortress5.getFlag());
        var road12 = map.placeAutoSelectedRoad(player0, fortress7.getFlag(), fortress6.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress4, fortress5, fortress6, fortress7);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress4, fortress5, fortress6, fortress7);

        /* Verify that the barracks in the center gets minimal population and others are not affected */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 2);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 2); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);
    }

    @Test
    public void testChangeMinimumToMediumPopulationOfMilitaryBuildingsFarAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(39, 35);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Fill in lots of resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 500);
        Utils.adjustInventoryTo(headquarter0, STONE, 500);
        Utils.adjustInventoryTo(headquarter0, BUILDER, 100);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(0);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 0);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(43, 35);
        var point2 = new Point(41, 43);
        var point3 = new Point(41, 33);
        var point4 = new Point(47, 43);
        var point5 = new Point(47, 33);

        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point2);
        var guardHouse2 = map.placeBuilding(new GuardHouse(player0), point3);
        var guardHouse3 = map.placeBuilding(new GuardHouse(player0), point4);
        var guardHouse4 = map.placeBuilding(new GuardHouse(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, guardHouse1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, guardHouse2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, guardHouse3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, guardHouse4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        /* Place one ring of fortresses */
        var point6 = new Point(29, 43);
        var point7 = new Point(53, 43);
        var point8 = new Point(53, 29);
        var point9 = new Point(29, 29);

        var fortress = map.placeBuilding(new Fortress(player0), point6);
        var fortress1 = map.placeBuilding(new Fortress(player0), point7);
        var fortress2 = map.placeBuilding(new Fortress(player0), point8);
        var fortress3 = map.placeBuilding(new Fortress(player0), point9);

        var road5 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());
        var road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), fortress1.getFlag());
        var road7 = map.placeAutoSelectedRoad(player0, fortress1.getFlag(), fortress2.getFlag());
        var road8 = map.placeAutoSelectedRoad(player0, fortress3.getFlag(), guardHouse3.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress, fortress1, fortress2, fortress3);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress, fortress1, fortress2, fortress3);

        /* Place more fortresses */
        var point10 = new Point(19, 29);
        var point11 = new Point(21, 21);
        var point12 = new Point(45, 21);
        var point13 = new Point(59, 35);

        var fortress4 = map.placeBuilding(new Fortress(player0), point10);
        var fortress5 = map.placeBuilding(new Fortress(player0), point11);
        var fortress6 = map.placeBuilding(new Fortress(player0), point12);
        var fortress7 = map.placeBuilding(new Fortress(player0), point13);

        var road9 = map.placeAutoSelectedRoad(player0, fortress4.getFlag(), fortress3.getFlag());
        var road10 = map.placeAutoSelectedRoad(player0, fortress5.getFlag(), fortress4.getFlag());
        var road11 = map.placeAutoSelectedRoad(player0, fortress6.getFlag(), fortress5.getFlag());
        var road12 = map.placeAutoSelectedRoad(player0, fortress7.getFlag(), fortress6.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress4, fortress5, fortress6, fortress7);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress4, fortress5, fortress6, fortress7);

        /* The barracks in the center get minimal population and others are not affected */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 1);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 1); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);

        /* Verify that changing to medium setting affects the center buildings */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(5);

        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 2);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 2); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);
    }

    @Test
    public void testChangeMaximumToMediumPopulationOfMilitaryBuildingsFarAwayFromBorder() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(39, 35);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Fill in lots of resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 500);
        Utils.adjustInventoryTo(headquarter0, STONE, 500);
        Utils.adjustInventoryTo(headquarter0, BUILDER, 100);

        /* Change soldier inventory */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 200);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 2);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 2);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 2);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);

        /* Set the population of military buildings away from the border */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(10);

        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 10);

        /* Place barracks - one in center and four around it */
        var point1 = new Point(43, 35);
        var point2 = new Point(41, 43);
        var point3 = new Point(41, 33);
        var point4 = new Point(47, 43);
        var point5 = new Point(47, 33);

        var guardHouse = map.placeBuilding(new GuardHouse(player0), point1);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point2);
        var guardHouse2 = map.placeBuilding(new GuardHouse(player0), point3);
        var guardHouse3 = map.placeBuilding(new GuardHouse(player0), point4);
        var guardHouse4 = map.placeBuilding(new GuardHouse(player0), point5);

        /* Connect the barracks and wait for them to get constructed and populated */
        var road0 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter0.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, guardHouse1.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, guardHouse2.getFlag(), headquarter0.getFlag());
        var road3 = map.placeAutoSelectedRoad(player0, guardHouse3.getFlag(), guardHouse.getFlag());
        var road4 = map.placeAutoSelectedRoad(player0, guardHouse4.getFlag(), guardHouse.getFlag());

        Utils.waitForBuildingsToBeConstructed(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        Utils.waitForMilitaryBuildingsToGetPopulated(guardHouse, guardHouse1, guardHouse2, guardHouse3, guardHouse4);

        /* Place one ring of fortresses */
        var point6 = new Point(29, 43);
        var point7 = new Point(53, 43);
        var point8 = new Point(53, 29);
        var point9 = new Point(29, 29);

        var fortress = map.placeBuilding(new Fortress(player0), point6);
        var fortress1 = map.placeBuilding(new Fortress(player0), point7);
        var fortress2 = map.placeBuilding(new Fortress(player0), point8);
        var fortress3 = map.placeBuilding(new Fortress(player0), point9);

        var road5 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());
        var road6 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), fortress1.getFlag());
        var road7 = map.placeAutoSelectedRoad(player0, fortress1.getFlag(), fortress2.getFlag());
        var road8 = map.placeAutoSelectedRoad(player0, fortress3.getFlag(), guardHouse3.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress, fortress1, fortress2, fortress3);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress, fortress1, fortress2, fortress3);

        /* Place more fortresses */
        var point10 = new Point(19, 29);
        var point11 = new Point(21, 21);
        var point12 = new Point(45, 21);
        var point13 = new Point(59, 35);

        var fortress4 = map.placeBuilding(new Fortress(player0), point10);
        var fortress5 = map.placeBuilding(new Fortress(player0), point11);
        var fortress6 = map.placeBuilding(new Fortress(player0), point12);
        var fortress7 = map.placeBuilding(new Fortress(player0), point13);

        var road9 = map.placeAutoSelectedRoad(player0, fortress4.getFlag(), fortress3.getFlag());
        var road10 = map.placeAutoSelectedRoad(player0, fortress5.getFlag(), fortress4.getFlag());
        var road11 = map.placeAutoSelectedRoad(player0, fortress6.getFlag(), fortress5.getFlag());
        var road12 = map.placeAutoSelectedRoad(player0, fortress7.getFlag(), fortress6.getFlag());

        Utils.waitForBuildingsToBeConstructed(fortress4, fortress5, fortress6, fortress7);

        Utils.waitForMilitaryBuildingsToGetPopulated(fortress4, fortress5, fortress6, fortress7);

        /* The barracks in the center gets minimal population and others are not affected */
        Utils.fastForward(300, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 3); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);

        /* Verify that changing the setting to medium affects the center buildings */
        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(5);

        Utils.fastForward(100, map);

        assertEquals(guardHouse.getHostedSoldiers().size(), 2);
        assertEquals(guardHouse2.getHostedSoldiers().size(), 2); // Also far from border...
        assertEquals(guardHouse1.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse3.getHostedSoldiers().size(), 3);
        assertEquals(guardHouse4.getHostedSoldiers().size(), 3);
        assertEquals(fortress.getHostedSoldiers().size(), 9);
        assertEquals(fortress1.getHostedSoldiers().size(), 9);
        assertEquals(fortress2.getHostedSoldiers().size(), 9);
        assertEquals(fortress3.getHostedSoldiers().size(), 9);
        assertEquals(fortress4.getHostedSoldiers().size(), 9);
        assertEquals(fortress5.getHostedSoldiers().size(), 9);
        assertEquals(fortress6.getHostedSoldiers().size(), 9);
        assertEquals(fortress7.getHostedSoldiers().size(), 9);

    }
}
