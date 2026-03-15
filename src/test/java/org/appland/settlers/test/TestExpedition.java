package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Shipyard;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.appland.settlers.model.Direction.*;
import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestExpedition {

    /*
    * TODO:
    *    - does ship sail to harbor when it's been built?
    *    - how much resources are required to start an expedition?
    *    - test cannot start expedition:
    *        - all ready ships are already sailing
    *        - no ready ship exists
    *        - ready ships belong to other player
    *        - there are not enough resources
    *        - all possible target places are taken (?)
    *    - ship can only sail on WATER (not WATER_2 and BUILDABLE_WATER)
    *    - can list and choose direction for expedition towards possible harbors
    *        - can't pick direction to harbor that is within another player's land
    *        - can't pick direction to harbor that is in own player's land
    *        - can't pick direction to harbor that's not connected to other harbor by water
    *        - list
    *            - DONE: up, down
    *            - TBD: right, left, up-right, up-left, down-right, down-left, combination
    *    - can't prepare for a new expedition while already preparing for an expedition
    *    - only prepares for one expedition, then stops until selected again
    *    - expeditions respect the emergency program limit
    * */

    @Test
    public void testAlreadyStoredMaterialIsUsedToPrepareExpeditionIfAvailable() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 101);

        // Place a lake
        for (int i = 13; i < 53; i += 2) {
            var point = new Point(i, 11);  // 13, 11  --  51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(52, 8);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(12, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        // Verify that an expedition can be prepared
        Utils.adjustInventoryTo(harbor, PLANK, 30);
        Utils.adjustInventoryTo(harbor, STONE, 30);
        Utils.adjustInventoryTo(harbor, BUILDER, 30);

        assertEquals(harbor.getAmount(PLANK), 30);
        assertEquals(harbor.getAmount(STONE), 30);
        assertEquals(harbor.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        harbor.prepareForExpedition();

        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 4);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 6);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 1);
        assertFalse(harbor.needsMaterial(PLANK));
        assertFalse(harbor.needsMaterial(STONE));
        assertFalse(harbor.needsMaterial(BUILDER));
    }

    @Test
    public void testCannotStartExpeditionInWrongDirection() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 101);

        // Place a lake
        for (int i = 7; i < 59; i += 2) {
            var point = new Point(i, 11);  // 7, 11  --  57, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(56, 8);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(10, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Pause the production in the shipyard
        shipyard.stopProduction();

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Put resources into the harbor
        Utils.adjustInventoryTo(harbor, PLANK, 30);
        Utils.adjustInventoryTo(harbor, STONE, 30);
        Utils.adjustInventoryTo(harbor, BUILDER, 30);

        // Prepare for an expedition
        harbor.prepareForExpedition();

        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 4);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 6);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 1);
        assertFalse(ship.isReadyToStartExpedition());

        // Wait for the ship to get to ready for the expedition
        Utils.waitForShipToBeReadyForExpedition(ship, map);

        // Verify that it's not possible to start an expedition in the wrong direction
        assertTrue(ship.isReadyToStartExpedition());
        assertEquals(ship.getPossibleDirectionsForExpedition().size(), 1);
        assertTrue(ship.getPossibleDirectionsForExpedition().contains(RIGHT));

        try {
            ship.startExpedition(LEFT);

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testNoPossibleExpeditionsFromShipWithoutPreparingExpedition() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place a lake
        var point0 = new Point(13, 9);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(10, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Verify that there are no possible expeditions from the ship
        assertEquals(ship.getPossibleDirectionsForExpedition().size(), 0);
    }

    @Test
    public void testGetNoPossibleExpeditionsFromShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place a lake
        var point0 = new Point(13, 9);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        // Mark a possible place for a harbor
        var point1 = new Point(12, 6);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(16, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Prepare for an expedition
        Utils.adjustInventoryTo(harbor, PLANK, 20);
        Utils.adjustInventoryTo(harbor, STONE, 20);
        Utils.adjustInventoryTo(harbor, BUILDER, 20);

        harbor.prepareForExpedition();

        // Verify that there are no possible expeditions from the ship
        assertEquals(ship.getPossibleDirectionsForExpedition().size(), 0);
    }

    @Test
    public void testCannotLaunchExpeditionInInvalidDirection() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 30, 41);

        // Place a lake
        for (int i = 9; i < 31; i += 2) {
            var point = new Point(13, i);  // 13, 9  --  13, 29

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(9, 27);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(9, 11);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(10, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that it's not possible to start an expedition in the wrong direction
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP));

        try {
            ship.startExpedition(DOWN);

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testGetPossibleExpeditionAboveFromShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 30, 41);

        // Place a lake
        for (int i = 9; i < 31; i += 2) {
            var point = new Point(13, i);  // 13, 9  --  13, 29

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(9, 29);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(9, 11);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(10, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP));
    }

    @Test
    public void testGetPossibleExpeditionBelowFromShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 30, 41);

        // Place a lake
        for (int i = 9; i < 31; i += 2) {
            var point = new Point(13, i);  // 13, 9  --  13, 29

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(9, 11);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(9, 29);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 35);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(10, 36);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(DOWN));
    }

    @Test
    public void testGetPossibleExpeditionRightOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 60, 41);

        // Place a lake
        for (int i = 7; i < 53; i += 2) {
            var point = new Point(i, 11);  // 13, 11  --  51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(52, 8);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(10, 6);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));
    }

    @Test
    public void testGetPossibleExpeditionLeftOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 70, 71);

        // Place a lake
        for (int i = 13; i < 53; i += 2) {
            var point = new Point(i, 11);

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(11, 9);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(52, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(60, 6);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(55, 9);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(LEFT));
    }

    @Test
    public void testGetPossibleExpeditionUpLeftOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 70, 71);

        // Place a bent lake
        for (int i = 13; i < 53; i += 2) {
            var point = new Point(i, 11);  // 13, 11  --  51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            var point = new Point(13, i);  // 13, 11  --  13, 51

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(16, 50);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(50, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(60, 6);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(55, 9);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP_LEFT));
    }

    @Test
    public void testGetPossibleExpeditionDownLeftOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 70, 71);

        // Place a bent lake
        for (int i = 13; i < 53; i += 2) {
            var point = new Point(i, 55);

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            var point = new Point(13, i);

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(14, 8);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(50, 52);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(60, 56);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(55, 59);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(DOWN_LEFT));
    }

    @Test
    public void testGetPossibleExpeditionUpRightOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 70, 71);

        // Place a bent lake
        for (int i = 3; i < 53; i += 2) {
            var point = new Point(i, 11); // 3, 11  --  51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            var point = new Point(53, i); // 53, 11  --  53, 51

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(49, 49);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(5, 9);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(4, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(9, 7);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Stop production of ships
        shipyard.stopProduction();

        // Make the ship sail to the waiting point
        assertNotNull(ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Prepare for an expedition
        harbor.prepareForExpedition();

        Utils.deliverCargos(harbor, PLANK, 4);
        Utils.deliverCargos(harbor, STONE, 6);
        Utils.deliverCargos(harbor, BUILDER, 1);

        // Wait for the ship to sail to the harbor
        map.stepTime();

        assertEquals(map.getShips().size(), 1);

        var target = ship.getTarget();

        assertTrue(harbor.getPosition().distance(target.x, target.y) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, target);

        // Verify that there is a possible expedition up-right
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP_RIGHT));
    }

    @Test
    public void testGetPossibleExpeditionDownRightOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 70, 71);

        // Place a bent lake
        for (int i = 3; i < 53; i += 2) {
            var point = new Point(i, 55);  // 3, 55  --  51, 55

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        for (int i = 7; i < 53; i+= 2) {
            var point = new Point(53, i);  // 53, 7  --  53, 51

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(49, 9);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(5, 57);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(6, 60);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(11, 59);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(DOWN_RIGHT));
    }

    @Test
    public void testGetPossibleExpeditionLeftAndDownLeftOfShip() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 70, 71);

        // Place a bent lake
        for (int i = 13; i < 53; i += 2) {
            var point = new Point(i, 55);  // 13, 55  --  51, 55

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            var point = new Point(13, i);  // 13, 11  --  13, 51

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark place for harbor
        var pointX = new Point(9, 55);
        map.setPossiblePlaceForHarbor(pointX);

        // Mark a possible place for a harbor
        var point0 = new Point(9, 15);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(52, 52);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(60, 56);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place shipyard
        var point3 = new Point(55, 59);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        // Verify that there are no possible expeditions from the harbor
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 2);
        assertTrue(directions.contains(LEFT));
        assertTrue(directions.contains(DOWN_LEFT));
    }

    @Test
    public void testNotMoreThanRequiredMaterialIsCollected() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 101);

        // Place a lake
        for (int i = 3; i < 59; i += 2) {
            var point = new Point(i, 11);  // 3, 11  -- 51, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(58, 8);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(8, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        // Verify that an expedition can be prepared
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        // Wait for the harbor to collect the required material for the expedition
        for (int i = 0; i < 10000; i++) {
            var expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        var expeditionMaterial = harbor.getMaterialForExpedition();

        assertEquals((int)expeditionMaterial.getOrDefault(PLANK, 0), 4);
        assertEquals((int)expeditionMaterial.getOrDefault(STONE, 0), 6);
        assertEquals((int)expeditionMaterial.getOrDefault(BUILDER, 0), 1);

        // Verify that there is no additional material collected
        Utils.fastForward(500, map);

        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 4);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 6);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 1);
    }

    @Test
    public void testShipIsNotReusedWhenStartingNextExpedition() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 101);

        // Place a lake
        for (int i = 3; i < 57; i += 2) {
            var point = new Point(i, 11);  // 3, 11  -- 55, 11

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        // Mark a possible place for a harbor
        var point1 = new Point(6, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        // Place shipyard
        var point3 = new Point(14, 8);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed and occupied
        assertFalse(harbor.needsMaterial(BUILDER));

        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has plenty of materials
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Tear down the shipyard to make sure there is only one ship
        shipyard.tearDown();

        // The ship sails to a waiting point close to the shipyard
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        // Prepare for the expedition
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        assertTrue(harbor.isReady());
        assertTrue(harbor.needsMaterial(PLANK));
        assertTrue(harbor.needsMaterial(STONE));

        // Wait for the harbor to collect the required material for the expedition
        for (int i = 0; i < 10000; i++) {
            var expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        // Wait for the ship to sail to the harbor
        var target = ship.getTarget();

        assertTrue(point1.distance(target.x, target.y) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, target);

        // The collected material for the expedition is transferred to the ship
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        var cargos = ship.getCargos();

        var materialInShip = new HashMap<Material, Integer>();
        for (var cargo : cargos) {
            var material = cargo.getMaterial();
            var amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        // Check that there is an expedition available to the possible harbor point
        var directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        // Start the expedition
        ship.startExpedition(RIGHT);

        var point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertFalse(ship.getPosition().distance(point0.downRight()) < 4);
        assertFalse(map.isBuildingAtPoint(point0));

        // Wait for the ship to sail to the possible harbor point
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, point5);

        assertEquals(map.getShips().size(), 1);

        // Fill up with material in the harbor for a second expedition
        Utils.adjustInventoryTo(harbor, PLANK, 20);
        Utils.adjustInventoryTo(harbor, STONE, 20);
        Utils.adjustInventoryTo(harbor, BUILDER, 20);

        // Prepare for the next expedition
        harbor.prepareForExpedition();

        // Verify that the ship is not re-used when a second expedition is launched
        for (int i = 0; i < 500; i++) {
            assertTrue(ship.getPosition().distance(point5) < 4);
            assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 4);
            assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 6);
            assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 1);

            map.stepTime();
        }
    }

    @Test
    public void testOnlyOneShipSailsToHarborToGetReadyForExpedition() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 60, 21);

        // Place a lake
        for (int i = 3; i < 53; i += 2) {
            var point = new Point(i, 11);

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(4, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        // Place shipyard
        var point3 = new Point(14, 8);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed
        assertFalse(harbor.needsMaterial(BUILDER));

        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has enough material for one ship
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Let the shipyard build a second ship
        Utils.waitForNumberItems(map.getShips(), 2, map);

        // Wait for the second ship to get constructed
        assertTrue(map.getShips().get(0).isReady());
        assertTrue(map.getShips().get(1).isUnderConstruction());

        var ship1 = map.getShips().get(1);

        Utils.waitForShipToGetBuilt(map, ship1);

        // Stop production of ships
        shipyard.stopProduction();

        // Wait for the ship to sail to a waiting point
        assertNotNull(ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        // Prepare for the expedition
        Utils.adjustInventoryTo(headquarter, PLANK, 20);
        Utils.adjustInventoryTo(headquarter, STONE, 20);
        Utils.adjustInventoryTo(headquarter, BUILDER, 1);

        harbor.prepareForExpedition();

        assertTrue(harbor.isCollectingMaterialForExpedition());

        // Wait for the harbor to collect the required material for the expedition
        for (int i = 0; i < 10000; i++) {
            if (!harbor.isCollectingMaterialForExpedition()) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        map.stepTime();

        assertEquals(map.getShips().size(), 2);

        // Verify that one ship sails to the harbor and the other stays
        for (int i = 0; i < 1000; i++) {
            if (ship.isExactlyAtPoint() && ship1.isExactlyAtPoint() &&
                (ship.getPosition().equals(harbor.getPosition()) || ship1.getPosition().equals(harbor.getPosition()))) {
                break;
            }

            map.stepTime();
        }

        var shipPosition = ship.getPosition();
        var ship1Position = ship1.getPosition();
        var harborPosition = harbor.getPosition();

        assertTrue(
                (shipPosition.distance(harborPosition.x, harborPosition.y) < 4 && ship1Position.distance(harborPosition.x, harborPosition.y) > 4) ||
                        (shipPosition.distance(harborPosition.x, harborPosition.y) > 4 && ship1Position.distance(harborPosition.x, harborPosition.y) < 4));
    }

    @Test
    public void testCanDoTwoExpeditions() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 60, 21);

        // Place a lake
        for (int i = 3; i < 53; i += 2) {
            var point = new Point(i, 11);

            Utils.surroundPointWithVegetation(point, Vegetation.WATER, map);
        }

        // Mark a possible place for a harbor
        var point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        // Mark a possible place for a harbor
        var point1 = new Point(4, 8);
        map.setPossiblePlaceForHarbor(point1);

        // Place headquarter
        var point2 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point2);

        // Place harbor
        var harbor = map.placeBuilding(new Harbor(player0), point1);

        // Connect the harbor to the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        // Wait for the harbor to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        // Place shipyard
        var point3 = new Point(14, 8);
        var shipyard = map.placeBuilding(new Shipyard(player0), point3);

        // Connect the shipyard to the headquarter
        var road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        // Wait for the shipyard to get constructed
        assertFalse(harbor.needsMaterial(BUILDER));

        Utils.waitForBuildingToBeConstructed(shipyard);

        // Set the shipyard to build ships
        shipyard.produceShips();

        // Wait for the shipyard to get occupied
        var shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        // Ensure the shipyard has enough material for one ship
        Utils.deliverCargos(shipyard, PLANK, 4);

        // Wait for the shipwright to rest
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        // Step once to let the shipwright go out to start building a ship
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        var point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        // Let the shipwright reach the intended spot and start to build the ship
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        // Wait for the shipwright to hammer
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        var ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        // Wait for the ship to get fully constructed
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        // Let the shipyard build a second ship
        Utils.waitForNumberItems(map.getShips(), 2, map);

        // Wait for the second ship to get constructed
        assertTrue(map.getShips().get(0).isReady());
        assertTrue(map.getShips().get(1).isUnderConstruction());

        var ship1 = map.getShips().get(1);

        Utils.waitForShipToGetBuilt(map, ship1);

        // Stop production of ships
        shipyard.stopProduction();

        // Wait for the ship to sail to a waiting point
        assertNotNull(ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        // Prepare for the expedition
        Utils.adjustInventoryTo(headquarter, PLANK, 20);
        Utils.adjustInventoryTo(headquarter, STONE, 20);
        Utils.adjustInventoryTo(headquarter, BUILDER, 1);

        harbor.prepareForExpedition();

        assertTrue(harbor.isCollectingMaterialForExpedition());

        // Wait for the harbor to collect the required material for the expedition
        for (int i = 0; i < 10000; i++) {

            if (!harbor.isCollectingMaterialForExpedition()) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        map.stepTime();

        assertEquals(map.getShips().size(), 2);

        // Wait for a ship to reach the harbor and load up the material
        for (int i = 0; i < 5000; i++) {
            if (ship.getPosition().equals(harbor.getPosition()) || ship1.getPosition().equals(harbor.getPosition())) {
                break;
            }

            map.stepTime();
        }

        var harborPosition = harbor.getPosition();

        assertTrue(
                ship.getPosition().distance(harborPosition) < 4 ||
                        ship1.getPosition().distance(harborPosition) < 4
        );

        Ship shipForSecondExpedition;

        if (ship.getPosition().distance(harbor.getPosition()) < 4) {
            assertEquals(ship.getCargos().size(), 11);
            assertEquals(ship1.getCargos().size(), 0);

            shipForSecondExpedition = ship1;
        } else {
            assertEquals(ship1.getCargos().size(), 11);
            assertEquals(ship.getCargos().size(), 0);

            shipForSecondExpedition = ship;
        }

        // Prepare for a second expedition
        harbor.prepareForExpedition();

        assertTrue(harbor.isCollectingMaterialForExpedition());

        // Wait for the harbor to collect the required material for the expedition
        for (int i = 0; i < 10000; i++) {
            if (!harbor.isCollectingMaterialForExpedition()) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        map.stepTime();

        // Verify that the other ship sails to the harbor and takes on the expedition
        assertTrue(shipForSecondExpedition.getTarget().distance(harbor.getPosition()) < 4);
        assertEquals(shipForSecondExpedition.getCargos().size(), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipForSecondExpedition, shipForSecondExpedition.getTarget());

        assertEquals(shipForSecondExpedition.getCargos().size(), 11);
    }
}
