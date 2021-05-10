package org.appland.settlers.test;

import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Harbor;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Ship;
import org.appland.settlers.model.Shipwright;
import org.appland.settlers.model.Shipyard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Direction.DOWN;
import static org.appland.settlers.model.Direction.DOWN_LEFT;
import static org.appland.settlers.model.Direction.DOWN_RIGHT;
import static org.appland.settlers.model.Direction.LEFT;
import static org.appland.settlers.model.Direction.RIGHT;
import static org.appland.settlers.model.Direction.UP;
import static org.appland.settlers.model.Direction.UP_LEFT;
import static org.appland.settlers.model.Direction.UP_RIGHT;
import static org.appland.settlers.model.Material.BUILDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    public void testAlreadyStoredMaterialIsUsedToPrepareExpeditionIfAvailable() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        /* Verify that an expedition can be prepared */
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
    public void testCannotStartExpeditionInWrongDirection() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(11, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Pause the production in the shipyard */
        shipyard.stopProduction();

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Put resources into the harbor */
        Utils.adjustInventoryTo(harbor, PLANK, 30);
        Utils.adjustInventoryTo(harbor, STONE, 30);
        Utils.adjustInventoryTo(harbor, BUILDER, 30);

        /* Prepare for an expedition */
        harbor.prepareForExpedition();

        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 4);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 6);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 1);
        assertFalse(ship.isReadyToStartExpedition());

        /* Wait for the ship to get to ready for the expedition */
        Utils.waitForShipToBeReadyForExpedition(ship, map);

        /* Verify that it's not possible to start an expedition in the wrong direction */
        assertTrue(ship.isReadyToStartExpedition());
        assertEquals(ship.getPossibleDirectionsForExpedition().size(), 1);
        assertTrue(ship.getPossibleDirectionsForExpedition().contains(RIGHT));

        try {
            ship.startExpedition(LEFT);

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testNoPossibleExpeditionsFromShipWithoutPreparingExpedition() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Verify that there are no possible expeditions from the ship */
        assertEquals(ship.getPossibleDirectionsForExpedition().size(), 0);
    }

    @Test
    public void testGetNoPossibleExpeditionsFromShip() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        Point point0 = new Point(13, 9);
        Utils.surroundPointWithDetailedVegetation(point0, DetailedVegetation.WATER, map);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Prepare for an expedition */
        Utils.adjustInventoryTo(harbor, PLANK, 20);
        Utils.adjustInventoryTo(harbor, STONE, 20);
        Utils.adjustInventoryTo(harbor, BUILDER, 20);

        harbor.prepareForExpedition();

        /* Verify that there are no possible expeditions from the ship */
        assertEquals(ship.getPossibleDirectionsForExpedition().size(), 0);
    }

    @Test
    public void testGetPossibleExpeditionAboveFromHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 40);

        /* Place a lake */
        for (int i = 9; i < 31; i += 2) {
            Point point = new Point(13, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(7, 29);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP));
    }

    @Test
    public void testGetPossibleExpeditionBelowFromHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 40);

        /* Place a lake */
        for (int i = 9; i < 31; i += 2) {
            Point point = new Point(13, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(7, 5);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 29);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 35);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(10, 36);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(DOWN));
    }

    @Test
    public void testGetPossibleExpeditionRightOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 40);

        /* Place a lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(11, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(7, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));
    }

    @Test
    public void testGetPossibleExpeditionLeftOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 70, 70);

        /* Place a lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(11, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(55, 5);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(60, 6);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(55, 9);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(LEFT));
    }

    @Test
    public void testGetPossibleExpeditionUpLeftOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 70, 70);

        /* Place a bent lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            Point point = new Point(13, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(15, 57);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(55, 5);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(60, 6);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(55, 9);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP_LEFT));
    }

    @Test
    public void testGetPossibleExpeditionDownLeftOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 70, 70);

        /* Place a bent lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(i, 55);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            Point point = new Point(13, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(14, 8);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(55, 55);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(60, 56);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(55, 59);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(DOWN_LEFT));
    }

    @Test
    public void testGetPossibleExpeditionUpRightOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 70, 70);

        /* Place a bent lake */
        for (int i = 3; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            Point point = new Point(53, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(50, 58);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 10);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(4, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(9, 7);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Stop production of ships */
        shipyard.stopProduction();

        /* Make the ship sail to the waiting point */
        assertNotNull(ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Prepare for an expedition */
        harbor.prepareForExpedition();

        Utils.deliverCargos(harbor, PLANK, 4);
        Utils.deliverCargos(harbor, STONE, 6);
        Utils.deliverCargos(harbor, BUILDER, 1);

        /* Wait for the ship to sail to the harbor */
        map.stepTime();

        assertEquals(map.getShips().size(), 1);
        assertEquals(ship.getTarget(), harbor.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, harbor.getPosition());

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(UP_RIGHT));
    }

    @Test
    public void testGetPossibleExpeditionDownRightOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 70, 70);

        /* Place a bent lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(i, 55);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            Point point = new Point(53, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(50, 8);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(5, 59);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(4, 54);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(9, 57);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(DOWN_RIGHT));
    }

    @Test
    public void testGetPossibleExpeditionLeftAndDownLeftOfHarbor() throws InvalidUserActionException, InvalidRouteException, InvalidEndPointException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 70, 70);

        /* Place a bent lake */
        for (int i = 13; i < 53; i += 2) {
            Point point = new Point(i, 55);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        for (int i = 11; i < 53; i+= 2) {
            Point point = new Point(13, i);

            Utils.surroundPointWithDetailedVegetation(point, DetailedVegetation.WATER, map);
        }

        /* Mark place for harbor */
        Point pointX = new Point(8, 58);
        map.setPossiblePlaceForHarbor(pointX);

        /* Mark a possible place for a harbor */
        Point point0 = new Point(14, 8);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(55, 55);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(60, 56);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place shipyard */
        Point point3 = new Point(55, 59);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the intended spot and start to build the ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        /* Wait for the shipwright to hammer */
        Utils.fastForward(19, map);

        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().get(0);

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Verify that there are no possible expeditions from the harbor */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 2);
        assertTrue(directions.contains(LEFT));
        assertTrue(directions.contains(DOWN_LEFT));
    }
}
