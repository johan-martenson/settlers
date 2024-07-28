package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.buildings.Harbor;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.messages.HarborIsFinishedMessage;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.messages.ShipHasReachedDestinationMessage;
import org.appland.settlers.model.messages.ShipReadyForExpeditionMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.appland.settlers.model.Direction.RIGHT;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.messages.Message.MessageType.SHIP_READY_FOR_EXPEDITION;
import static org.junit.Assert.*;

public class TestExpeditionNormalFlow {

    /*
    * Normal expedition flow:
    *  0.  Build shipyard
    *  1.  Build ship
    *        -- Next to water
    *  2.  Build harbor
    *        -- Game message: New harbor building finished
    *  3.  Ship sails to harbor
    *  4.  Prepare expedition
    *        -- Material collects outside
    *        -- Builder works on it
    *        -- 4 planks & 6 stones & 1 builder
    *  5.  All material collected
    *  6.  Ship sails to harbor
    *  7.  Ready for expedition
    *        -- Game message: A ship is ready for an expedition
    *  8.  Click ship and select direction
    *        -- Lists all possible directions
    *        -- Ships have names (e.g. Astrid)
    *  9.  Ship sails to harbor point
    *        -- Game message: A ship has reached the destination of its expedition
    *  10. Select ship and click "anchor" to place a harbor
    *        -- Also lists all possible directions
    *        -- Can choose to instead go to another harbor
    *  11. Builders starts building harbor
    *        -- Range: 8
    *  12. Harbor is done
    *        -- Game message: New harbor building finished
    *  13. Build something and connect to the harbor
    *  14. Ships sails back to get material for construction
    *
    *
    * TODO: variations to test ...
    *     - find way to right point close to harbor (if harbor is next to different seas)
    *     - prepare for expedition when no free ship exists, when ship later appears an expedition can be started as normal
    *     - make sure material gets transported from flag to the new harbor
    *     - can continue to next possible harbor spot if the first one is not wanted
    *     - ship deliver either directly to harbor or to its flag
    * */

    @Test
    public void testReadyShipWaitsCloseToShipyard() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 5; i < 53; i += 2) {
            Point point = new Point(i, 11);  // 5, 11  --  51, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point1 = new Point(3, 9);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(13, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        /* Place shipyard */
        Point point3 = new Point(22, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Wait for the ship to sail to its waiting point */
        assertNotNull(ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Verify that the ship waits close to the shipyard */
        assertTrue(GameUtils.distanceInGameSteps(shipyard.getPosition(), harbor.getPosition()) > 8);

        for (int i = 0; i < 200; i++) {

            assertTrue(GameUtils.distanceInGameSteps(ship.getPosition(), shipyard.getPosition()) < 8);

            map.stepTime();
        }
    }

    @Test
    public void testPrepareExpedition() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 7; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 13, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(56, 8);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(8, 8);
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
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Verify that the harbor collects the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

        assertEquals((int)expeditionMaterial.getOrDefault(PLANK, 0), 4);
        assertEquals((int)expeditionMaterial.getOrDefault(STONE, 0), 6);
        assertEquals((int)expeditionMaterial.getOrDefault(BUILDER, 0), 1);
    }

    @Test
    public void testShipReadyForExpeditionGameMessage() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        for (int i = 3; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(4, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        assertFalse(harbor.needsMaterial(BUILDER));

        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to build ships */
        shipyard.produceShips();

        /* Wait for the shipyard to get occupied */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has enough material for one ship */
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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Stop production of ships */
        shipyard.stopProduction();

        /* Wait for the ship to sail to a waiting point */
        assertNotNull(ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 20);
        Utils.adjustInventoryTo(headquarter, STONE, 20);
        Utils.adjustInventoryTo(headquarter, BUILDER, 1);

        harbor.prepareForExpedition();

        assertTrue(harbor.isCollectingMaterialForExpedition());

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            if (!harbor.isCollectingMaterialForExpedition()) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        map.stepTime();

        assertEquals(map.getShips().size(), 1);

        /* The ship sails to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);
        assertTrue(ship.getPosition().distance(point1) > 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        assertTrue(ship.getPosition().distance(point1) < 4);
        assertTrue(ship.isWaitingForExpedition());

        /* Verify that a ship is ready for expedition game message is sent */
        assertTrue(player0.getMessages().size() > 1);

        ShipReadyForExpeditionMessage message = (ShipReadyForExpeditionMessage) player0.getMessages().getLast();

        assertEquals(message.getMessageType(), SHIP_READY_FOR_EXPEDITION);
        assertEquals(message.ship(), ship);
    }

    @Test
    public void testExpeditionMaterialIsTransferredToShip() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        for (int i = 3; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Verify that the collected material for the expedition was transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);
    }

    @Test
    public void testExpeditionsAreAvailableInTheShipAfterMaterialIsTransferred() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place a lake */
        for (int i = 3; i < 53; i += 2) {
            Point point = new Point(i, 11);

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(57, 11);
        map.setPossiblePlaceForHarbor(point0);

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Verify that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));
    }

    @Test
    public void testStartExpedition() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Verify that the expedition can be started */
        ship.startExpedition(RIGHT);
    }

    @Test
    public void testShipSailsToExpeditionTargetAndDoesNotAutomaticallyBuildHarbor() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Verify that a harbor is built automatically, only if the player decides to */
        assertFalse(map.isBuildingAtPoint(point0));
    }

    @Test
    public void testGameMessageWhenShipReachesExpeditionTarget() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(56, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());


        /* Prepare for the expedition */
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

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(56, 10); // Closest water point for the potential harbor site

        assertEquals(ship.getTarget(), point5);
        assertNotEquals(ship.getPosition(), point0.downRight());
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, point5);

        /* Verify that a game message is sent */
        assertFalse(player0.getMessages().isEmpty());

        ShipHasReachedDestinationMessage message = (ShipHasReachedDestinationMessage) player0.getMessages().getLast();

        assertEquals(message.getMessageType(), Message.MessageType.SHIP_HAS_REACHED_DESTINATION);
        assertEquals(message.ship(), ship);
        assertEquals(message.position(), point5);
    }

    @Test
    public void testShipSailsToExpeditionTargetPlayerCanBuildHarbor() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Verify that a harbor can be built */
        assertFalse(map.isBuildingAtPoint(point0));

        ship.startSettlement();

        assertTrue(map.isBuildingAtPoint(point0));

        Harbor newHarbor = (Harbor) map.getBuildingAtPoint(point0);

        assertNotNull(newHarbor.getBuilder());

        Utils.waitForBuildingToBeUnderConstruction(newHarbor);

        /* Wait for the harbor to finish construction */
        Utils.waitForBuildingToBeConstructed(newHarbor);
    }

    @Test
    public void testGameMessageWhenNewHarborInSettlementIsFinished() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        /* Place shipyard */
        Point point3 = new Point(18, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        Utils.adjustInventoryTo(harbor, PLANK, 0);
        Utils.adjustInventoryTo(harbor, STONE, 0);
        Utils.adjustInventoryTo(harbor, BUILDER, 0);

        harbor.prepareForExpedition();

        assertTrue(harbor.isReady());
        assertTrue(harbor.needsMaterial(PLANK));
        assertTrue(harbor.needsMaterial(STONE));
        assertTrue(harbor.needsMaterial(BUILDER));

        /* Wait for the harbor to collect the required material for the expedition */
        assertTrue(ship.getPosition().distance(point1) > 4);

        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);
        assertTrue(ship.getPosition().distance(point1) > 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        assertTrue(ship.getPosition().distance(point1) < 4);

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Verify that a harbor can be built */
        assertFalse(map.isBuildingAtPoint(point0));

        ship.startSettlement();

        assertTrue(map.isBuildingAtPoint(point0));

        Harbor newHarbor = (Harbor) map.getBuildingAtPoint(point0);

        assertNotNull(newHarbor.getBuilder());

        Utils.waitForBuildingToBeUnderConstruction(newHarbor);

        /* Wait for the harbor to finish construction */
        Utils.waitForBuildingToBeConstructed(newHarbor);

        /* Verify that a game message is sent when the harbor is done */
        assertFalse(player0.getMessages().isEmpty());

        HarborIsFinishedMessage message = (HarborIsFinishedMessage) player0.getMessages().getLast();

        assertEquals(message.getMessageType(), Message.MessageType.HARBOR_IS_FINISHED);
        assertEquals(message.harbor(), newHarbor);
    }

    @Test
    public void testNewHarborGetsOwnBorderDirectly() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());


        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, point5);

        /* Verify that a harbor is built and that it gets its border when construction starts */
        Point point6 = new Point(point0.x + 9, point0.y + 9);

        assertFalse(player0.getBorderPoints().contains(point6));
        assertFalse(map.isBuildingAtPoint(point0));

        ship.startSettlement();

        assertTrue(map.isBuildingAtPoint(point0));
        assertTrue(player0.getBorderPoints().contains(point6));
    }

    @Test
    public void testNewHarborGetsStoredBuilderWhenConstructionIsDone() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
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

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());


        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        harbor.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Start a settlement */
        ship.startSettlement();

        Harbor harbor1 = (Harbor) map.getBuildingAtPoint(point0);

        assertNotNull(harbor1);
        assertTrue(harbor1.isPlanned());

        Builder builder = harbor1.getBuilder();

        assertNotNull(builder);

        /* Wait for the construction of the new harbor to finish */
        Utils.waitForBuildingToBeConstructed(harbor1);

        /* Verify that the builder gets stored in the newly constructed harbor */
        assertEquals(harbor1.getAmount(BUILDER), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, builder, harbor1.getPosition());

        assertEquals(harbor1.getAmount(BUILDER), 1);
    }

    @Test
    public void testNewHarborGetsShipmentWithNeededGoods() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor0 = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor0.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor0);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor0);

        assertTrue(harbor0.isReady());

        /* Place shipyard */
        Point point3 = new Point(14, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor0.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Make sure there is only one ship */
        shipyard.stopProduction();

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor0.getMaterialForExpedition().size(), 0);

        harbor0.prepareForExpedition();

        /* Wait for the harbor to collect the required material for the expedition */
        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor0.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor0.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor0.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor0.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor0.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        assertTrue(ship.getTarget().distance(point0) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Start a settlement */
        ship.startSettlement();

        Harbor harbor1 = (Harbor) map.getBuildingAtPoint(point0);

        assertNotNull(harbor1);
        assertTrue(harbor1.isPlanned());

        Builder builder = harbor1.getBuilder();

        assertNotNull(builder);

        /* Wait for the construction of the new harbor to finish */
        Utils.waitForBuildingToBeConstructed(harbor1);

        /* Empty all planks from the new harbor and fill up with planks in the first harbor */
        Utils.adjustInventoryTo(harbor1, PLANK, 0);
        Utils.adjustInventoryTo(harbor0, PLANK, 30);

        /* Place a woodcutter at the settlement */
        Point point4 = new Point(58, 6);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point4);

        /* Connect the woodcutter with the second harbor */
        Road road2 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), harbor1.getFlag());

        map.stepTime();
        map.stepTime();

        /* Verify that the harbor gets a delivery of two planks */
        assertTrue(ship.getTarget().distance(harbor0.getPosition()) < 4);
        assertEquals(ship.getCargos().size(), 0);
        assertEquals(harbor0.getAmount(PLANK), 30);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        assertEquals(map.getShips().size(), 1);
        assertEquals(ship.getCargos().size(), 2);
        assertEquals(harbor0.getAmount(PLANK), 28);
        assertTrue(ship.getTarget().distance(harbor1.getPosition()) < 4);
        assertEquals(harbor1.getAmount(PLANK), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        assertEquals(ship.getCargos().size(), 0);
        assertEquals(harbor1.getAmount(PLANK), 2);
    }

    @Test
    public void testShipCanStartNewExpeditionAfterFirstSettlementIsFinished() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place a lake */
        for (int i = 3; i < 57; i += 2) {
            Point point = new Point(i, 11);  // 3, 11  --  55, 11

            Utils.surroundPointWithDetailedVegetation(point, Vegetation.WATER, map);
        }

        /* Mark a possible place for a harbor */
        Point point0 = new Point(54, 8);
        map.setPossiblePlaceForHarbor(point0);

        assertTrue(map.isAvailableHarborPoint(point0));

        /* Mark a possible place for a harbor */
        Point point1 = new Point(6, 8);
        map.setPossiblePlaceForHarbor(point1);

        /* Place headquarter */
        Point point2 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point2);

        /* Place harbor */
        Harbor harbor = map.placeBuilding(new Harbor(player0), point1);

        /* Connect the harbor to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, harbor.getFlag(), headquarter.getFlag());

        /* Wait for the harbor to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(harbor);

        Utils.fastForward(2000, map);

        Utils.waitForNonMilitaryBuildingToGetPopulated(harbor);

        assertTrue(harbor.isReady());

        /* Place shipyard */
        Point point3 = new Point(18, 8);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point3);

        /* Connect the shipyard to the headquarters */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        assertFalse(harbor.needsMaterial(BUILDER));

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

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Make sure there is only one ship */
        shipyard.stopProduction();

        /* The ship sails to a waiting point close to the shipyard */
        assertNotNull(ship.getTarget());
        assertNotEquals(ship.getPosition(), ship.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Prepare for the expedition */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);
        Utils.adjustInventoryTo(headquarter, BUILDER, 30);

        assertEquals(headquarter.getAmount(PLANK), 30);
        assertEquals(headquarter.getAmount(STONE), 30);
        assertEquals(headquarter.getAmount(BUILDER), 30);
        assertEquals(harbor.getMaterialForExpedition().size(), 0);

        Utils.adjustInventoryTo(harbor, PLANK, 0);
        Utils.adjustInventoryTo(harbor, STONE, 0);
        Utils.adjustInventoryTo(harbor, BUILDER, 0);

        harbor.prepareForExpedition();

        assertTrue(harbor.isReady());
        assertTrue(harbor.needsMaterial(PLANK));
        assertTrue(harbor.needsMaterial(STONE));
        assertTrue(harbor.needsMaterial(BUILDER));

        /* Wait for the harbor to collect the required material for the expedition */
        assertTrue(ship.getPosition().distance(point1) > 4);

        for (int i = 0; i < 10000; i++) {

            Map<Material, Integer> expeditionMaterial = harbor.getMaterialForExpedition();

            if (expeditionMaterial.getOrDefault(PLANK, 0) == 4 &&
                    expeditionMaterial.getOrDefault(STONE, 0) == 6 &&
                    expeditionMaterial.getOrDefault(BUILDER, 0) == 1) {
                break;
            }

            assertTrue(harbor.isReady());

            map.stepTime();
        }

        /* Wait for the ship to sail to the harbor */
        assertTrue(ship.getTarget().distance(point1) < 4);
        assertTrue(ship.getPosition().distance(point1) > 4);

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* The collected material for the expedition is transferred to the ship */
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(PLANK, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(STONE, 0), 0);
        assertEquals((int)harbor.getMaterialForExpedition().getOrDefault(BUILDER, 0), 0);

        Set<Cargo> cargos = ship.getCargos();

        Map<Material, Integer> materialInShip = new HashMap<>();

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();
            int amount = materialInShip.getOrDefault(material, 0);

            amount = amount + 1;

            materialInShip.put(material, amount);
        }

        assertEquals((int)materialInShip.getOrDefault(PLANK, 0), 4);
        assertEquals((int)materialInShip.getOrDefault(STONE, 0), 6);
        assertEquals((int)materialInShip.getOrDefault(BUILDER, 0), 1);

        /* Check that there is an expedition available to the possible harbor point */
        Set<Direction> directions = ship.getPossibleDirectionsForExpedition();

        assertEquals(directions.size(), 1);
        assertTrue(directions.contains(RIGHT));

        /* Start the expedition */
        ship.startExpedition(RIGHT);

        Point point5 = new Point(54, 10); // Closest water point for the potential harbor site

        assertTrue(ship.getTarget().distance(point5) < 4);
        assertTrue(ship.getPosition().distance(point0.downRight()) > 4);
        assertFalse(map.isBuildingAtPoint(point0));

        /* Wait for the ship to sail to the possible harbor point */
        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        /* Verify that a harbor can be built */
        assertFalse(map.isBuildingAtPoint(point0));

        ship.startSettlement();

        assertEquals(ship.getCargos().size(), 0);
        assertTrue(map.isBuildingAtPoint(point0));

        Harbor newHarbor = (Harbor) map.getBuildingAtPoint(point0);

        assertNotNull(newHarbor.getBuilder());

        Utils.waitForBuildingToBeUnderConstruction(newHarbor);

        /* Wait for the harbor to finish construction */
        Utils.waitForBuildingToBeConstructed(newHarbor);

        /* Verify that the ship can start a new expedition when the first expedition is done */
        Utils.adjustInventoryTo(harbor, PLANK, 40);
        Utils.adjustInventoryTo(harbor, STONE, 40);
        Utils.adjustInventoryTo(harbor, BUILDER, 40);

        harbor.prepareForExpedition();

        map.stepTime();

        assertEquals(map.getShips().size(), 1);
        assertEquals(ship.getCargos().size(), 0);
        assertTrue(ship.getTarget().distance(harbor.getPosition()) < 4);
        assertFalse(ship.isReadyToStartExpedition());

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, ship.getTarget());

        assertEquals(ship.getCargos().size(), 11);
        assertTrue(ship.isReadyToStartExpedition());
    }
}
