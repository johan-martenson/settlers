package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.PlayerColor.BLUE;
import static org.appland.settlers.model.PlayerColor.RED;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestPlayer {

    @Test
    public void testPlayerIsSetWhenBuildingIsCreated() {

        // Create player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Create building with the player set
        var woodcutter0 = new Woodcutter(player0);

        // Verify that the player is set in the building
        assertNotNull(woodcutter0.getPlayer());
        assertEquals(woodcutter0.getPlayer(), player0);
    }

    @Test
    public void testNameIsSetInPlayer() {

        // Create player
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Verify that the name is set
        assertEquals(player0.getName(), "Player 0");
    }

    @Test
    public void testCreatePlayer() {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(player.getName(), "Player one");
        assertEquals(player.getColor(), BLUE);
    }

    @Test
    public void testCreateHouseWithPlayer() {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Create house belonging to player one
        var woodcutter0 = new Woodcutter(player);

        assertEquals(woodcutter0.getPlayer(), player);
    }

    @Test
    public void testPlayerIsAlsoSetInBuildingsFlag() {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Create house belonging to player one
        var woodcutter0 = new Woodcutter(player);

        // Verify that the building's flag has the player set correctly
        assertEquals(woodcutter0.getFlag().getPlayer(), player);
    }

    @Test
    public void testPlayerCanOnlyCreateOneHeadquarter() throws Exception {

        // Create game
        var player = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player), 50, 51);

        // Place first headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player), point0);

        // Create second headquarter belonging to player one
        var headquarter1 = new Headquarter(player);

        // Verify that it's not possible to place a second headquarter
        var point1 = new Point(15, 15);

        try {
            map.placeBuilding(headquarter1, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerIsAlsoSetInRoad() throws Exception {

        // Create game
        var player0 = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place first headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(9, 5);
        var flag0 = map.placeFlag(player0, point1);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        assertEquals(road0.getPlayer(), player0);
    }

    @Test
    public void testPlayerIsSetInDriveWay() throws Exception {

        // Create game
        var player0 = new Player("Player one", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place first headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(11, 5);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Verify that the woodcutter's driveway has the player set correctly
        var road0 = map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition());

        assertEquals(road0.getPlayer(), player0);
    }

    @Test
    public void testCannotPlaceBuildingWithInvalidPlayer() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that it's not possible to place a building with an invalid player
        var invalidPlayer = new Player("", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var point1 = new Point(8, 6);

        try {
            var woodcutter = map.placeBuilding(new Woodcutter(invalidPlayer), point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceRoadWithInvalidPlayer() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(8, 6);
        var point3 = new Point(12, 6);
        var flag0 = map.placeFlag(player0, point1);
        var flag1 = map.placeFlag(player0, point3);

        // Verify that it's not possible to place a road with an invalid player
        var invalidPlayer = new Player("", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var point2 = new Point(10, 6);

        try {
            var road0 = map.placeRoad(invalidPlayer, point1, point2, point3);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testCannotPlaceFlagWithInvalidPlayer() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that it's not possible to place a flag with an invalid player
        var invalidPlayer = new Player("", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var point1 = new Point(8, 6);

        try {
            var flag0 = map.placeFlag(invalidPlayer, point1);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testColorIsCorrectInPlayer() {

        // Create player
        var player0 = new Player("Player 0", RED, Nation.ROMANS, PlayerType.HUMAN);

        // Verify that the color is set correctly
        assertEquals(player0.getColor(), RED);
    }

    @Test
    public void testChangeColorInPlayer() {

        // Create player
        var player0 = new Player("Player 0", RED, Nation.ROMANS, PlayerType.HUMAN);

        // Change the color
        player0.setColor(BLUE);

        // Verify that the color is set correctly
        assertEquals(player0.getColor(), BLUE);
    }

    @Test
    public void testChangeNameInPlayer() {

        // Create player
        var player0 = new Player("Player 0", RED, Nation.ROMANS, PlayerType.HUMAN);

        // Change the name
        player0.setName("Another player");

        // Verify that the color is set correctly
        assertEquals(player0.getName(), "Another player");
    }

    @Test
    public void testCannotHaveTwoPlayersWithSameColor() {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Verify that it's not possible to have two players with the same color
        try {
            var map = new GameMap(List.of(player0, player1), 20, 21);

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testPlayerDefaultNationIsRoman() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(player0.getNation(), Nation.ROMANS);

        // Verify that it's not possible to have two players with the same color
        var map = new GameMap(List.of(player0), 20, 21);

        assertEquals(player0.getNation(), Nation.ROMANS);
    }

    @Test
    public void testSetNationForPlayer() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        assertEquals(player0.getNation(), Nation.ROMANS);

        player0.setNation(Nation.VIKINGS);

        assertEquals(player0.getNation(), Nation.VIKINGS);

        var map = new GameMap(List.of(player0), 20, 21);

        assertEquals(player0.getNation(), Nation.VIKINGS);
    }

    @Test
    public void testUpdatePlayer() {
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var listener = new Utils.PlayerMonitor();

        // Listen to changes in the player
        player0.addPlayerChangeListener(listener);

        // Verify that updating the player sets the name, color, and nation
        player0.update("Other name", Nation.JAPANESE, PlayerColor.RED);

        assertEquals(player0.getName(), "Other name");
        assertEquals(player0.getColor(), PlayerColor.RED);
        assertEquals(player0.getNation(), Nation.JAPANESE);

        // Verify that the listener was called
        assertTrue(listener.getEventsForPlayer(player0).size() > 0);
    }

    @Test
    public void testNotificationWhenSettingMilitaryPolicies() throws InvalidUserActionException {

        // Create a player object to update
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        // Subscribe to player changes
        var playerListener = new Utils.PlayerMonitor();
        player0.addPlayerChangeListener(playerListener);

        // Verify that no notification is sent if a military setting is unchanged
        assertEquals(player0.getDefenseFromSurroundingBuildings(), 5);
        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);

        player0.setDefenseFromSurroundingBuildings(5);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);
        assertEquals(player0.getAmountOfSoldiersWhenPopulatingCloseToBorder(), 10);

        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(10);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);
        assertEquals(player0.getAmountOfSoldiersWhenPopulatingAwayFromBorder(), 10);

        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(10);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);
        assertEquals(player0.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 10);

        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(10);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);
        assertEquals(player0.getStrengthOfSoldiersPopulatingBuildings(), 5);

        player0.setStrengthOfSoldiersPopulatingBuildings(5);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);
        assertEquals(player0.getDefenseStrength(), 5);

        player0.setDefenseStrength(5);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);
        assertEquals(player0.getAmountOfSoldiersAvailableForAttack(), 10);

        player0.setAmountOfSoldiersAvailableForAttack(10);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 0);

        // Verify that a notification is sent each time a military setting is changed
        player0.setDefenseFromSurroundingBuildings(2);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 1);

        player0.setDefenseStrength(3);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 2);

        player0.setStrengthOfSoldiersPopulatingBuildings(4);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 3);

        player0.setAmountOfSoldiersWhenPopulatingCloseToBorder(2);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 4);

        player0.setAmountOfSoldiersWhenPopulatingAwayFromBorder(2);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 5);

        player0.setAmountOfSoldiersWhenPopulatingFarFromBorder(2);

        assertEquals(playerListener.getEventsForPlayer(player0).size(), 6);
    }

    @Test
    public void testMilitaryPoliciesUpdatedBeforeNotification() throws InvalidUserActionException {

        var player = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        // Defense from surrounding buildings
        var eventsBefore = monitor.getEventsForPlayer(player).size();
        player.setDefenseFromSurroundingBuildings(2);

        var events = monitor.getEventsForPlayer(player);
        assertEquals(eventsBefore + 1, events.size());
        assertEquals(2, events.getLast().defenseFromSurroundingBuildings());

        // Defense strength
        eventsBefore = events.size();
        player.setDefenseStrength(3);

        events = monitor.getEventsForPlayer(player);
        assertEquals(eventsBefore + 1, events.size());
        assertEquals(3, events.getLast().defenseStrength());

        // Soldiers available for attack
        eventsBefore  = events.size();
        player.setAmountOfSoldiersAvailableForAttack(2);

        events = monitor.getEventsForPlayer(player);
        assertEquals(eventsBefore + 1, events.size());
        assertEquals(2, events.getLast().soldiersAvailableForAttack());

        // Strength of soldiers populating buildings
        eventsBefore = events.size();
        player.setStrengthOfSoldiersPopulatingBuildings(4);

        events = monitor.getEventsForPlayer(player);
        assertEquals(eventsBefore + 1, events.size());
        assertEquals(4, events.getLast().strengthOfSoldiersPopulatingBuildings());

        // Close to border
        eventsBefore = events.size();
        player.setAmountOfSoldiersWhenPopulatingCloseToBorder(2);

        events = monitor.getEventsForPlayer(player);
        assertEquals(eventsBefore + 1, events.size());
        assertEquals(2, events.getLast().closeToBorder());

        // Away from border
        eventsBefore = events.size();
        player.setAmountOfSoldiersWhenPopulatingAwayFromBorder(2);

        events = monitor.getEventsForPlayer(player);
        assertEquals(eventsBefore + 1, events.size());
        assertEquals(2, events.getLast().awayFromBorder());

        // Far from border
        eventsBefore = events.size();

        assertNotEquals(player.getAmountOfSoldiersWhenPopulatingFarFromBorder(), 2);

        player.setAmountOfSoldiersWhenPopulatingFarFromBorder(2);

        events = monitor.getEventsForPlayer(player);

        assertEquals(eventsBefore + 1, events.size());
        assertEquals(2, events.getLast().farFromBorder());
    }

    @Test
    public void testSetCoalQuotaTriggersNotification() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        var eventsBefore = monitor.getEventsForPlayer(player).size();

        player.setCoalQuota(org.appland.settlers.model.buildings.IronSmelter.class, 3);

        var events = monitor.getEventsForPlayer(player);

        assertEquals(eventsBefore + 1, events.size());
        assertEquals(3, events.getLast().coalQuota().ironSmelter());
    }

    @Test
    public void testSetIronQuotaTriggersNotification() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        var eventsBefore = monitor.getEventsForPlayer(player).size();

        player.setIronBarQuota(org.appland.settlers.model.buildings.Armory.class, 2);

        var events = monitor.getEventsForPlayer(player);

        assertEquals(eventsBefore + 1, events.size());
        assertEquals(2, events.getLast().ironQuota().armory());
    }

    @Test
    public void testSetFoodQuotaTriggersNotification() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        var eventsBefore = monitor.getEventsForPlayer(player).size();

        player.setFoodQuota(org.appland.settlers.model.buildings.IronMine.class, 4);

        var events = monitor.getEventsForPlayer(player);

        assertEquals(eventsBefore + 1, events.size());
        assertEquals(4, events.getLast().foodQuota().ironMine());
    }

    @Test
    public void testSetWaterQuotaTriggersNotification() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        var eventsBefore = monitor.getEventsForPlayer(player).size();

        player.setWaterQuota(org.appland.settlers.model.buildings.Bakery.class, 5);

        var events = monitor.getEventsForPlayer(player);

        assertEquals(eventsBefore + 1, events.size());
        assertEquals(5, events.getLast().waterQuota().bakery());
    }

    @Test
    public void testSetWheatQuotaTriggersNotification() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        var eventsBefore = monitor.getEventsForPlayer(player).size();

        player.setWheatQuota(org.appland.settlers.model.buildings.Mill.class, 6);

        var events = monitor.getEventsForPlayer(player);

        assertEquals(eventsBefore + 1, events.size());
        assertEquals(6, events.getLast().wheatQuota().mill());
    }

    @Test
    public void testSetCoalQuotaNoNotificationWhenUnchanged() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        assertEquals(player.getCoalQuota(Mint.class), 1);

        player.setCoalQuota(org.appland.settlers.model.buildings.Mint.class, 1);

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testSetFoodQuotaNoNotificationWhenUnchanged() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        assertEquals(1, player.getFoodQuota(org.appland.settlers.model.buildings.IronMine.class));

        player.setFoodQuota(org.appland.settlers.model.buildings.IronMine.class, 1);

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testSetWaterQuotaNoNotificationWhenUnchanged() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        assertEquals(1, player.getWaterQuota(org.appland.settlers.model.buildings.Bakery.class));

        player.setWaterQuota(org.appland.settlers.model.buildings.Bakery.class, 1);

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }

    @Test
    public void testSetWheatQuotaNoNotificationWhenUnchanged() {

        var player = new Player("Player", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var monitor = new Utils.PlayerMonitor();
        player.addPlayerChangeListener(monitor);

        assertEquals(1, player.getWheatQuota(org.appland.settlers.model.buildings.Mill.class));

        player.setWheatQuota(org.appland.settlers.model.buildings.Mill.class, 1);

        assertEquals(0, monitor.getEventsForPlayer(player).size());
    }
}

