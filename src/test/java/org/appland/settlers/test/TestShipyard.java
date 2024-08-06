/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestShipyard {

    /*
    * TODO:
    *   - available shipyard places
    *        - shipyard can only be placed in some places (e.g. close to water)
    *        - can only place shipyard close to water where ship can sail
    *        - test that shipwright doesn't pick point it cannot go to
    *   - the way the shipwright builds is to 1) pick a plank, 2) go to the ship, 3) hammer, 4) go back -- and iterate
    *   - what does a ship cost?
    *   - production of small boats - pause and resume, produce without connection to storage, place on flag, etc.
    *   - available construction around ship being built
    *   - switch between boats and ships during production
    *   - how far away from water can a ship be built?
    *   - rule for where shipyards can be placed???
    *   - test and handle case where there is no point to place the ship on (incl. wrong type of water)
    *   - can't build ship on stones, trees, houses, roads, flags
    *
    * */

    @Test
    public void testShipyardOnlyNeedsThreePlanksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(6, 12);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Deliver two planks and three stones */
        Utils.deliverCargos(shipyard0, PLANK, 3);
        Utils.deliverCargos(shipyard0, STONE, 3);

        /* Assign builder */
        Utils.assignBuilder(shipyard0);

        /* Verify that this is enough to construct the shipyard */
        for (int i = 0; i < 200; i++) {
            assertTrue(shipyard0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(shipyard0.isReady());
    }

    @Test
    public void testShipyardCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(6, 12);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Deliver one plank and three stone */
        Utils.deliverCargos(shipyard0, PLANK, 2);
        Utils.deliverCargos(shipyard0, STONE, 3);

        /* Assign builder */
        Utils.assignBuilder(shipyard0);

        /* Verify that this is not enough to construct the shipyard */
        for (int i = 0; i < 500; i++) {
            assertTrue(shipyard0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(shipyard0.isReady());
    }

    @Test
    public void testShipyardCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(6, 12);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Deliver three planks and two stones */
        Utils.deliverCargos(shipyard0, PLANK, 3);
        Utils.deliverCargos(shipyard0, STONE, 2);

        /* Assign builder */
        Utils.assignBuilder(shipyard0);

        /* Verify that this is not enough to construct the shipyard */
        for (int i = 0; i < 500; i++) {
            assertTrue(shipyard0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(shipyard0.isReady());
    }

    @Test
    public void testUnfinishedShipyardNeedsNoShipwright() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        assertTrue(shipyard.isPlanned());
        assertFalse(shipyard.needsWorker());
    }

    @Test
    public void testFinishedShipyardNeedsShipwright() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Construct the shipyard */
        Utils.constructHouse(shipyard);

        /* Verify that the shipyard needs a shipwright */
        assertTrue(shipyard.isReady());
        assertTrue(shipyard.needsWorker());
    }

    @Test
    public void testShipwrightIsAssignedToFinishedShipyard() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Finish the shipyard */
        Utils.constructHouse(shipyard);

        /* Verify that a shipwright starts walking to the shipyard */
        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertNotNull(shipwright);
        assertEquals(shipwright.getTarget(), shipyard.getPosition());
    }

    @Test
    public void testShipwrightIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Finish the shipyard */
        Utils.constructHouse(shipyard);

        /* Wait for a shipwright to walk out */
        Shipwright shipwright0 = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertNotNull(shipwright0);
        assertFalse(shipwright0.isSoldier());
    }

    @Test
    public void testShipwrightIsCreatedFromHammer() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Remove all shipwrights from the headquarter and add one scythe */
        Utils.adjustInventoryTo(headquarter, Material.SHIPWRIGHT, 0);
        Utils.adjustInventoryTo(headquarter, Material.HAMMER, 1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Finish the shipyard */
        Utils.constructHouse(shipyard);

        /* Verify that a shipwright starts walking to the shipyard */
        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertNotNull(shipwright);
        assertEquals(headquarter.getAmount(HAMMER), 0);
    }

    @Test
    public void testShipyardMakesBoatsByDefault() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Verify that the shipyard makes boats by default */
        assertTrue(shipyard.isProducingBoats());
        assertFalse(shipyard.isProducingShips());

        Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        Utils.deliverCargos(shipyard, PLANK, 2);

        Shipwright shipwright = (Shipwright) shipyard.getWorker();

        Utils.fastForwardUntilWorkerCarriesCargo(map, shipwright);

        assertNotNull(shipwright.getCargo());
        assertEquals(shipwright.getCargo().getMaterial(), BOAT);
    }

    @Test
    public void testShipwrightRestsInShipyardThenLeavesIfShipsAreConstructed() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Worker shipwright = Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Run the game logic 99 times and make sure the shipwright stays in the shipyard */
        for (int i = 0; i < 99; i++) {
            assertTrue(shipwright.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(shipwright.isInsideBuilding());

        /* Step once and make sure the shipwright goes out of the shipyard */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());
    }

    @Test
    public void testShipwrightGoesOutViaFlagWhenShipsAreProduced() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        assertTrue(headquarter.getAmount(SHIPWRIGHT) > 0);

        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Worker shipwright = Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Run the game logic 99 times and make sure the shipwright stays in the shipyard */
        for (int i = 0; i < 99; i++) {
            assertTrue(shipwright.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(shipwright.isInsideBuilding());

        /* Step once and make sure the shipwright goes out */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        /* Verify that the shipwright goes out via the flag */
        assertTrue(shipwright.getPlannedPath().contains(shipyard.getFlag().getPosition()));
    }

    @Test
    public void testShipwrightStartsBuildingAShipWhenThereIsAFreeSpot() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Let the shipwright rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once and make sure the shipwright goes out of the shipyard */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());

        /* Let the shipwright reach the spot and start to build a ship */
        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isAt(point));
        assertTrue(shipwright.isHammering());

        for (int i = 0; i < 20; i++) {
            assertTrue(shipwright.isHammering());

            map.stepTime();
        }

        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        assertEquals(ship.getPosition(), shipwright.getPosition());
        assertTrue(ship.isUnderConstruction());
        assertFalse(ship.isReady());

        /* Verify that the shipwright stopped hammering and there is a ship under construction */
        assertFalse(shipwright.isHammering());
        assertNull(shipwright.getCargo());
    }

    @Test
    public void testShipwrightBuildsAShipPointOnWaterEdge() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Ensure the shipyard has plenty of materials */
        Utils.deliverCargos(shipyard, PLANK, 4);

        /* Let the shipwright rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once and make sure the shipwright goes out of the shipyard */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        Point point = shipwright.getTarget();

        assertTrue(shipwright.isTraveling());
        assertTrue(GameUtils.isSomeButNotAll(map.getSurroundingTiles(point), Vegetation.WATER));
    }

    @Test
    public void testShipwrightReturnsViaFlagAfterStartingToBuildShip() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        map.stepTime();

        /* Verify that the shipwright stopped hammering and is walking back to the shipyard */
        assertFalse(shipwright.isHammering());
        assertTrue(shipwright.isTraveling());
        assertEquals(shipwright.getTarget(), shipyard.getPosition());
        assertTrue(shipwright.getPlannedPath().contains(shipyard.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, shipwright);

        assertTrue(shipwright.isArrived());
        assertTrue(shipwright.isInsideBuilding());
    }

    @Test
    public void testShipwrightWithEnoughResourcesBuildsShip() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Verify that the shipwright builds a ship given enough resources */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        for (int i = 0; i < 100; i++) {
            if (ship.isReady()) {
                break;
            }

            if (shipyard.getAmount(PLANK) < 2) {
                Utils.deliverCargo(shipyard, PLANK);
            }

            assertTrue(ship.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());
    }

    @Test
    public void testShipwrightOnlyBuildsShipWhenThereAreEnoughResources() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Pause production in the shipyard to control the amount of planks when we start */
        shipyard.stopProduction();

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipyard to have all required planks */
        Utils.waitForBuildingToGetAmountOfMaterial(shipyard, PLANK, 4);

        assertFalse(shipyard.needsMaterial(PLANK));

        /* Remove the road to make sure there are no more deliveries of planks */
        map.removeRoad(road0);

        /* Resume production */
        shipyard.resumeProduction();

        /* Wait for the shipwright to rest */
        Utils.fastForward(99, map);

        assertTrue(shipwright.isInsideBuilding());

        /* Step once to let the shipwright go out to start building a ship */
        map.stepTime();

        assertFalse(shipwright.isInsideBuilding());

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Wait for the shipwright to build a ship */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        for (int i = 0; i < 100; i++) {
            if (ship.isReady()) {
                break;
            }

            assertTrue(ship.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());
        assertEquals(shipyard.getAmount(PLANK), 0);

        /* Verify that the shipyard doesn't build any more ships because it's out of resources */
        for (int i = 0; i < 1000; i++) {
            assertEquals(map.getShips().size(), 1);
            assertEquals(shipyard.getAmount(PLANK), 0);

            map.stepTime();
        }
    }

    @Test
    public void testFinishedShipStaysInWaterCloseToShipyard() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Wait for the shipwright to build a ship */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        assertTrue(ship.isUnderConstruction());
        assertFalse(ship.isReady());

        Point point3 = shipwright.getPosition();

        for (int i = 0; i < 100; i++) {
            if (ship.isReady()) {
                break;
            }

            if (shipyard.getAmount(PLANK) < 2) {
                Utils.deliverCargo(shipyard, PLANK);
            }

            assertTrue(ship.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(ship.isReady());

        /* Verify that the ship sails a short distance and then lies waiting */
        Point point4 = ship.getTarget();

        assertNotNull(point4);
        assertNotEquals(point4, point3);
        assertTrue(GameUtils.distanceInGameSteps(point4, shipyard.getPosition()) < 6);
        assertTrue(GameUtils.isAll(map.getSurroundingTiles(point4), Vegetation.WATER));

        Utils.fastForwardUntilWorkerReachesPoint(map, ship, point4);

        Utils.verifyWorkersDoNotMove(map, ship);
    }

    @Test
    public void testShipIsBuiltCloseToWaterRightOfShipyard() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(17, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Wait for the shipwright to start building a ship */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        /* Verify that the ship is built close to the water */
        assertTrue(Math.abs(ship.getPosition().x - point2.x) < 5);
        assertTrue(Math.abs(ship.getPosition().y - point2.y) < 5);
    }

    @Test
    public void testCannotBuildShipCloseToWater2() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(17, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER_2, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Verify that the shipwright stays in the building because there is no correct type of water close by */
        for (int i = 0; i < 500; i++) {

            assertTrue(shipwright.isInsideBuilding());
            assertEquals(map.getShips().size(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testCannotBuildShipCloseToBuildableWater() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(17, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.BUILDABLE_WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipwright.isInsideBuilding());

        /* Verify that the shipwright stays in the building because there is no correct type of water close by */
        for (int i = 0; i < 500; i++) {
            assertTrue(shipwright.isInsideBuilding());
            assertEquals(map.getShips().size(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testShipIsBuiltCloseToWaterLeftOfShipyard() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(4, 10);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Wait for the shipwright to start building a ship */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        /* Verify that the ship is built close to the water */
        assertTrue(Math.abs(ship.getPosition().x - point2.x) < 5);
        assertTrue(Math.abs(ship.getPosition().y - point2.y) < 5);
    }

    @Test
    public void testShipIsBuiltCloseToWaterAboveShipyard() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(10, 14);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Wait for the shipwright to start building a ship */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        /* Verify that the ship is built close to the water */
        assertTrue(Math.abs(ship.getPosition().x - point2.x) < 5);
        assertTrue(Math.abs(ship.getPosition().y - point2.y) < 5);
    }

    @Test
    public void testShipIsBuiltCloseToWaterBelowShipyard() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place lake */
        Point point2 = new Point(9, 3);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place shipyard */
        Point point0 = new Point(9, 11);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Instruct the shipyard to construct ships */
        shipyard.produceShips();

        assertTrue(shipyard.isProducingShips());

        /* Wait for a shipwright to occupy the shipyard */
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

        /* Wait for the shipwright to reach the position where the new ship will be built */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        /* Wait for the shipwright to start building a ship */
        assertTrue(shipwright.isHammering());
        assertEquals(map.getShips().size(), 1);

        Ship ship = map.getShips().getFirst();

        /* Verify that the ship is built close to the water */
        assertTrue(Math.abs(ship.getPosition().x - point2.x) < 5);
        assertTrue(Math.abs(ship.getPosition().y - point2.y) < 5);
    }

    @Test
    public void testShipyardWithoutPlanksProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Adjust resources to there is only resources for constructing the shipyard */
        Utils.adjustInventoryTo(headquarter, PLANK, 30);
        Utils.adjustInventoryTo(headquarter, STONE, 30);

        /* Change so the headquarter can't provide a shipwright */
        Utils.adjustInventoryTo(headquarter, SHIPWRIGHT, 0);
        Utils.adjustInventoryTo(headquarter, HAMMER, 0);

        /* Construct and occupy the shipyard */
        Utils.constructHouse(shipyard);

        Shipwright shipwright = Utils.occupyBuilding(new Shipwright(player0, map), shipyard);

        /* Verify that the shipyard does not produce anything because it has no resources */
        for (int i = 0; i < 200; i++) {
            assertEquals(shipyard.getAmount(PLANK), 0);
            assertEquals(map.getShips().size(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testShipwrightGoesBackToStorageWhenShipyardIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(8, 8);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Destroy the shipyard */
        assertTrue(shipwright.isInsideBuilding());
        assertEquals(shipwright.getPosition(), shipyard0.getPosition());

        shipyard0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(shipwright.isInsideBuilding());
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SHIPWRIGHT);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getPosition());

        /* Verify that the shipwright is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SHIPWRIGHT), amount + 1);
    }

    @Test
    public void testShipwrightGoesBackOnToStorageOnRoadsIfPossibleWhenShipyardIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(8, 8);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Destroy the shipyard */
        assertTrue(shipwright.isInsideBuilding());
        assertEquals(shipwright.getPosition(), shipyard0.getPosition());

        shipyard0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(shipwright.isInsideBuilding());
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : shipwright.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInShipyardCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(12, 8);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the shipwright to produce leave the shipyard */
        for (int i = 0; i < 100; i++) {
            if (!shipwright.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(shipwright.isInsideBuilding());

        /* Wait for the shipwright to return to the shipyard */
        for (int i = 0; i < 200; i++) {
            if (shipwright.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(shipwright.isInsideBuilding());

        /* Stop production and verify that the shipwright stays in the shipyard */
        shipyard0.stopProduction();

        assertFalse(shipyard0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertTrue(shipwright.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInShipyardCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(12, 8);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the shipwright to produce leave the shipyard */
        for (int i = 0; i < 100; i++) {
            if (!shipwright.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(shipwright.isInsideBuilding());

        /* Wait for the shipwright to return to the shipyard */
        for (int i = 0; i < 200; i++) {
            if (shipwright.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(shipwright.isInsideBuilding());

        /* Stop production */
        shipyard0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertTrue(shipwright.isInsideBuilding());

            map.stepTime();
        }

        /* Resume production and verify that the shipwright leaves the shipyard */
        shipyard0.resumeProduction();

        assertTrue(shipyard0.isProductionEnabled());

        for (int i = 0; i < 200; i++) {
            if (!shipwright.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(shipwright.isInsideBuilding());
    }

    @Test
    public void testAssignedShipwrightHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point1 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(20, 14);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), shipyard0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        /* Wait for shipwright to get assigned and leave the headquarter */
        List<Shipwright> workers = Utils.waitForWorkersOutsideBuilding(Shipwright.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Shipwright worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        Player player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Point point1 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point1);

        /* Place player 0's headquarter */
        Point point2 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point2);

        /* Place player 1's headquarter */
        Point point3 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point3);

        /* Place fortress for player 0 */
        Point point4 = new Point(21, 9);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point4);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place shipyard close to the new border */
        Point point0 = new Point(28, 18);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(shipwright.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testCannotPlaceBuildingOnShipBeingBuilt() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set shipyard to build ships */
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

        /* Verify that it's not possible to place a building on the ship being built */
        try {
            map.placeBuilding(new Woodcutter(player0), ship.getPosition());

            fail();
        } catch (Exception e) {}

        assertFalse(map.isBuildingAtPoint(ship.getPosition()));
        assertNull(map.getBuildingAtPoint(ship.getPosition()));
    }

    @Test
    public void testNoAvailableBuildingSpaceOnShipBeingBuilt() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point2 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Change to producing ships */
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

        /* Verify that there is no available building space on the ship being built */
        assertNull(map.isAvailableHousePoint(player0, ship.getPosition()));
    }

    @Test
    public void testCannotPlaceFlagOnShipBeingBuilt() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point0 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point2 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point2);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Change to ship production */
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

        /* Verify that it's not possible to place a flag on a ship being built */
        try {
            Flag flag0 = map.placeFlag(player0, ship.getPosition());

            fail();
        } catch (Exception e) {}

        assertFalse(map.isFlagAtPoint(ship.getPosition()));
        assertNull(map.getFlagAtPoint(ship.getPosition()));
    }

    @Test
    public void testNoAvailableFlagOnShipBeingBuilt() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place lake */
        Point point0 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point0, Vegetation.WATER, map);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point2 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point2);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        /* Set the shipyard to produce ships */
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

        /* Verify that it's not possible to place a flag on the ship being built */
        assertTrue(ship.isUnderConstruction());
        assertFalse(map.isAvailableFlagPoint(player0, ship.getPosition()));
    }

    @Test
    public void testAvailableFlagSpaceOnSpaceWhereShipWasBeingBuiltWhenConstructionIsDone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lake */
        Point point1 = new Point(15, 9);
        Utils.surroundPointWithVegetation(point1, Vegetation.WATER, map);

        /* Place shipyard */
        Point point2 = new Point(10, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point2);

        /* Place road */
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
        assertTrue(map.isAvailableFlagPoint(player0, point));

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
        assertEquals(ship.getPosition(), point);
        assertTrue(ship.isUnderConstruction());
        assertFalse(map.isAvailableFlagPoint(player0, point));

        /* Wait for the ship to get fully constructed */
        Utils.waitForShipToGetBuilt(map, ship);

        assertTrue(ship.isReady());
        assertFalse(ship.isUnderConstruction());

        /* Verify that there is available building space where the ship was being built before */
        assertTrue(map.isAvailableFlagPoint(player0, point));
    }

    @Test
    public void testShipwrightReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place first flag */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place shipyard */
        Point point0 = new Point(13, 5);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, shipyard0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertNotNull(shipwright);
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the shipwright has started walking */
        assertFalse(shipwright.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the shipwright continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, flag0.getPosition());

        assertEquals(shipwright.getPosition(), flag0.getPosition());

        /* Verify that the shipwright returns to the headquarter when it reaches the flag */
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getPosition());
    }

    @Test
    public void testShipwrightContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place first flag */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place shipyard */
        Point point0 = new Point(13, 5);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, shipyard0.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        /* Wait for a shipwright to start walking towards the shipyard */
        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the shipwright has started walking */
        assertFalse(shipwright.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the shipwright continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, flag0.getPosition());

        assertEquals(shipwright.getPosition(), flag0.getPosition());

        /* Verify that the shipwright continues to the final flag */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getFlag().getPosition());

        /* Verify that the shipwright goes back to the headquarter */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());
    }

    @Test
    public void testShipwrightReturnsToStorageIfShipyardIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place first flag */
        Point point2 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place shipyard */
        Point point0 = new Point(13, 5);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, shipyard0.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        /* Wait for a shipwright start to walk to the shipyard */
        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertNotNull(shipwright);
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        /* Wait for the shipwright to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, flag0.getPosition());

        map.stepTime();

        /* See that the shipwright has started walking */
        assertFalse(shipwright.isExactlyAtPoint());

        /* Tear down the shipyard */
        shipyard0.tearDown();

        /* Verify that the shipwright continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getFlag().getPosition());

        assertEquals(shipwright.getPosition(), shipyard0.getFlag().getPosition());

        /* Verify that the shipwright goes back to storage */
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testShipwrightGoesOffroadBackToClosestStorageWhenShipyardIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(17, 17);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipwright to get to the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Place a second storage closer to the shipyard */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the shipyard */
        Utils.waitForWorkerToBeInside(shipwright, map);

        assertTrue(shipwright.isInsideBuilding());
        assertEquals(shipwright.getPosition(), shipyard0.getPosition());

        shipyard0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(shipwright.isInsideBuilding());
        assertEquals(shipwright.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(SHIPWRIGHT);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, storehouse0.getPosition());

        /* Verify that the shipwright is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(SHIPWRIGHT), amount + 1);
    }

    @Test
    public void testShipwrightReturnsOffroadAndAvoidsBurningStorageWhenShipyardIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(17, 17);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipwright to reach the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Place a second storage closer to the shipyard */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the shipyard */
        Utils.waitForWorkerToBeInside(shipwright, map);

        assertTrue(shipwright.isInsideBuilding());
        assertEquals(shipwright.getPosition(), shipyard0.getPosition());

        shipyard0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(shipwright.isInsideBuilding());
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SHIPWRIGHT);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getPosition());

        /* Verify that the shipwright is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SHIPWRIGHT), amount + 1);
    }

    @Test
    public void testShipwrightReturnsOffroadAndAvoidsDestroyedStorageWhenShipyardIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(17, 17);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipwright to reach the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Place a second storage closer to the shipyard */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Wait for the shipwright to be inside the shipyard */
        Utils.waitForWorkerToBeInside(shipwright, map);

        /* Destroy the shipyard */
        assertTrue(shipwright.isInsideBuilding());
        assertEquals(shipwright.getPosition(), shipyard0.getPosition());

        shipyard0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(shipwright.isInsideBuilding());
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SHIPWRIGHT);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getPosition());

        /* Verify that the shipwright is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SHIPWRIGHT), amount + 1);
    }

    @Test
    public void testShipwrightReturnsOffroadAndAvoidsUnfinishedStorageWhenShipyardIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(17, 17);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipwright to reach the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Place a second storage closer to the shipyard */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the shipyard */
        assertTrue(shipwright.isInsideBuilding());
        assertEquals(shipwright.getPosition(), shipyard0.getPosition());

        shipyard0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(shipwright.isInsideBuilding());
        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(SHIPWRIGHT);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getPosition());

        /* Verify that the shipwright is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(SHIPWRIGHT), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(17, 17);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road to connect the headquarter and the shipyard */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), shipyard0.getFlag());

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        /* Wait for a shipwright to start walking to the shipyard */
        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertFalse(shipwright.isInsideBuilding());

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getFlag().getPosition());

        /* Tear down the building */
        shipyard0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        assertEquals(shipwright.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getPosition());
    }

    @Test
    public void testShipyardWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Construct and occupy the shipyard */
        Utils.constructHouse(shipyard0);

        Shipwright shipwright = Utils.occupyBuilding(new Shipwright(player0, map), shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Verify that the productivity is 0% when the shipyard doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(shipyard0.getFlag().getStackedCargo().isEmpty());
            assertNull(shipwright.getCargo());
            assertEquals(shipyard0.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testShipyardWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), shipyard0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipwright to reach the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Make the shipyard create some wheat with full resources available */
        for (int i = 0; i < 3000; i++) {
            map.stepTime();
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(shipyard0.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            assertEquals(shipyard0.getProductivity(), 100);
        }
    }

    @Test
    public void testShipyardLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), shipyard0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Remove all planks from the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);

        /* Wait for the shipwright to reach the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Wait for the shipyard to reach 100% productivity */
        for (int i = 0; i < 3000; i++) {
            if (shipyard0.getAmount(PLANK) < 2) {
                Utils.deliverCargo(shipyard0, PLANK);
            }

            map.stepTime();

            if (shipyard0.getProductivity() == 100) {
                break;
            }
        }

        /* Verify that the productivity goes down when there are no resources */
        assertEquals(shipyard0.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();

            if (shipyard0.getProductivity() == 0) {
                break;
            }
        }

        assertEquals(shipyard0.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedShipyardHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(10, 10);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Finish construction of the shipyard */
        Utils.constructHouse(shipyard);

        /* Give the shipyard planks */
        Utils.deliverCargos(shipyard, PLANK, 2);

        /* Verify that the unoccupied shipyard is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(shipyard.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testShipyardCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(7, 9);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        Shipwright shipwright = (Shipwright) Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright.isInsideBuilding());

        /* Wait for the shipwright to reach the shipyard */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getPosition());

        /* Verify that the shipyard can produce */
        assertTrue(shipyard0.canProduce());
    }

    @Test
    public void testShipyardReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(6, 12);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Construct the shipyard */
        Utils.constructHouse(shipyard0);

        /* Verify that the reported output is correct */
        assertEquals(shipyard0.getProducedMaterial().length, 1);
        assertEquals(shipyard0.getProducedMaterial()[0], BOAT);
    }

    @Test
    public void testShipyardWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(14, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(shipyard);
        Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        /* Fill the flag with eight cargos */
        Utils.placeCargos(map, FLOUR, 8, shipyard.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Give planks to the shipyard */
        Utils.deliverCargos(shipyard, PLANK, 2);

        /* Verify that the shipyard waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 800; i++) {
            assertEquals(shipyard.getFlag().getStackedCargo().size(), 8);
            assertNotEquals(shipyard.getWorker().getTarget(), shipyard.getFlag().getPosition());

            map.stepTime();
        }

        /* Reconnect the shipyard with the headquarter */
        assertTrue(map.isFlagAtPoint(shipyard.getFlag().getPosition()));
        assertTrue(map.isFlagAtPoint(headquarter.getFlag().getPosition()));

        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(shipyard.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(shipyard.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a boat cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, shipyard.getWorker(), BOAT);
    }

    // TODO: test that boat is delivered to water way directly if it's closer than the nearest storehouse
    // TODO: test that promiseCargo is used so the water way doesn't get two boats
    // TODO: double-check required material to construct shipyard. 2 or 3 planks?

    @Test
    public void testBoatIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all planks and stones from the headquarters to prevent the storehouse from being finished */
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 0);

        /* Place store house */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the store house to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the shipyard */
        Point point1 = new Point(14, 4);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point1);

        /* Connect the shipyard with the store house */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the shipyard */
        Utils.deliverCargos(shipyard, PLANK, 3);
        Utils.deliverCargos(shipyard, STONE, 3);

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard);

        Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        /* Wait for the courier on the road between the store house and the shipyard hut to have a boat cargo */
        Utils.deliverCargos(shipyard, PLANK, 3);

        Utils.waitForFlagToGetStackedCargo(map, shipyard.getFlag(), 1);

        assertEquals(shipyard.getFlag().getStackedCargo().getFirst().getMaterial(), BOAT);
        assertEquals(shipyard.getFlag().getStackedCargo().getFirst().getTarget(), headquarter);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the store house's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(BOAT), 0);
        assertEquals(shipyard.getAmount(BOAT), 0);
        assertFalse(storehouse.needsMaterial(BOAT));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(BOAT));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testShipyardDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(14, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(shipyard);
        Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        assertTrue(shipyard.isProducingBoats());
        assertFalse(shipyard.isProducingShips());

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, shipyard.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Give the shipyard planks */
        Utils.deliverCargos(shipyard, PLANK, 2);

        /* The shipyard waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(shipyard.getFlag().getStackedCargo().size(), 8);
            assertNull(shipyard.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the shipyard with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(shipyard.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(shipyard.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(shipyard.getFlag().getStackedCargo().size(), 7);
        assertTrue(shipyard.getFlag().hasPlaceForMoreCargo());

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.waitForWorkerToSetTarget(map, shipyard.getWorker(), shipyard.getFlag().getPosition());

        assertNotNull(shipyard.getWorker().getCargo());
        assertEquals(shipyard.getWorker().getCargo().getMaterial(), BOAT);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(shipyard.getWorker().getTarget(), shipyard.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipyard.getWorker(), shipyard.getFlag().getPosition());

        assertEquals(shipyard.getFlag().getStackedCargo().size(), 8);

        /* Verify that the shipyard doesn't produce anything because the flag is full */
        for (int i = 0; i < 800; i++) {
            assertEquals(shipyard.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }
    }

    @Test
    public void testWhenBoatDeliveryAreBlockedShipyardFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place Shipyard */
        Point point0 = new Point(7, 9);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road to connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Wait for the shipyard to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(shipyard0);

        Worker shipwright0 = Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        assertTrue(shipwright0.isInsideBuilding());
        assertEquals(shipwright0.getHome(), shipyard0);
        assertEquals(shipyard0.getWorker(), shipwright0);

        /* Block storage of boats */
        headquarter0.blockDeliveryOfMaterial(BOAT);

        /* Verify that the shipyard puts eight boats on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, shipyard0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright0, shipyard0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(shipyard0.getFlag().getStackedCargo().size(), 8);

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), BOAT);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndShipyardIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place storehouse */
        Point point2 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point2);

        /* Place shipyard */
        Point point0 = new Point(18, 4);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the shipyard */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the shipyard and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, shipyard0);

        /* Wait for the shipyard and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, shipyard0);

        Worker shipwright0 = shipyard0.getWorker();

        assertTrue(shipwright0.isInsideBuilding());
        assertEquals(shipwright0.getHome(), shipyard0);
        assertEquals(shipyard0.getWorker(), shipwright0);

        /* Verify that the worker goes to the storage when the shipyard is torn down */
        headquarter0.blockDeliveryOfMaterial(SHIPWRIGHT);

        shipyard0.tearDown();

        map.stepTime();

        assertFalse(shipwright0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright0, shipyard0.getFlag().getPosition());

        assertEquals(shipwright0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, shipwright0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(shipwright0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndShipyardIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place storehouse */
        Point point2 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point2);

        /* Place shipyard */
        Point point0 = new Point(18, 6);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the shipyard */
        Road road1 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the shipyard and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, shipyard0);

        /* Wait for the shipyard and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, shipyard0);

        Worker shipwright0 = shipyard0.getWorker();

        assertTrue(shipwright0.isInsideBuilding());
        assertEquals(shipwright0.getHome(), shipyard0);
        assertEquals(shipyard0.getWorker(), shipwright0);

        /* Verify that the worker goes to the storage off-road when the shipyard is torn down */
        headquarter0.blockDeliveryOfMaterial(SHIPWRIGHT);

        Utils.waitForWorkerToBeInside(shipyard0.getWorker(), map);

        assertTrue(map.findWayOffroad(shipyard0.getPosition(), storehouse.getPosition(), null).size() >
                map.findWayOffroad(shipyard0.getPosition(), headquarter0.getPosition(), null).size());

        map.removeRoad(road0);

        shipyard0.tearDown();

        map.stepTime();

        assertFalse(shipwright0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright0, shipyard0.getFlag().getPosition());

        assertEquals(shipwright0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(shipwright0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, SHIPWRIGHT, 1);

        assertEquals(headquarter0.getAmount(SHIPWRIGHT), 1);

        headquarter0.pushOutAll(SHIPWRIGHT);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

            assertEquals(headquarter0.getAmount(SHIPWRIGHT), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, SHIPWRIGHT, 1);

        headquarter0.blockDeliveryOfMaterial(SHIPWRIGHT);
        headquarter0.pushOutAll(SHIPWRIGHT);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(7, 9);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road to connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the shipyard to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(shipyard0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(SHIPWRIGHT);

        Worker worker = shipyard0.getWorker();

        shipyard0.tearDown();

        assertEquals(worker.getPosition(), shipyard0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, shipyard0.getFlag().getPosition());

        assertEquals(worker.getPosition(), shipyard0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), shipyard0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point1 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(7, 9);
        Shipyard shipyard0 = map.placeBuilding(new Shipyard(player0), point0);

        /* Place road to connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the shipyard to get constructed */
        Utils.waitForBuildingToBeConstructed(shipyard0);

        /* Wait for a shipwright to start walking to the shipyard */
        Shipwright shipwright = Utils.waitForWorkerOutsideBuilding(Shipwright.class, player0);

        /* Wait for the shipwright to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the shipwright goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(shipwright.getTarget(), shipyard0.getPosition());

        headquarter0.blockDeliveryOfMaterial(SHIPWRIGHT);

        shipyard0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipyard0.getFlag().getPosition());

        assertEquals(shipwright.getPosition(), shipyard0.getFlag().getPosition());
        assertNotEquals(shipwright.getTarget(), headquarter0.getPosition());
        assertFalse(shipwright.isInsideBuilding());
        assertNull(shipyard0.getWorker());
        assertNotNull(shipwright.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, shipwright, shipwright.getTarget());

        Point point = shipwright.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(shipwright.isDead());
            assertEquals(shipwright.getPosition(), point);
            assertTrue(map.getWorkers().contains(shipwright));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(shipwright));
    }
}
