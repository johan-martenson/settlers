package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.appland.settlers.model.Material.COIN;
import static org.junit.Assert.*;

public class TestDoorOpenClose {

    /*
    Worker delivering cargo:
     - Door should open when worker to enter is about to start to walk on the building's driveway
        - Door should stay open a little bit after the worker turns around and walks out
    Worker inside going out: (DONE)
     - Door should open when worker inside appears outside
    Worker going inside: (DONE)
     - Door should close when worker is "inside"
     */

    @Test
    public void testDoorIsClosedOnBuildingUnderConstruction() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut and connect it to the headquarters */
        Point point1 = new Point(6, 12);
        var woodcutterHut = map.placeBuilding(new Woodcutter(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutterHut.getFlag(), headquarter0.getFlag());

        /* Wait for the builder to start building */
        for (int i = 0; i < 200; i++) {
            if (woodcutterHut.isUnderConstruction()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        /* Verify that the door is closed */
        assertTrue(woodcutterHut.isUnderConstruction());
        assertTrue(woodcutterHut.isDoorClosed());
    }

    @Test
    public void testDoorIsClosedOnUnoccupiedBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut and connect it with the headquarters */
        Point point1 = new Point(6, 12);
        var woodcutterHut = map.placeBuilding(new Woodcutter(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutterHut.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter hut to be finished and unoccupied */
        Utils.waitForBuildingToBeConstructed(woodcutterHut);

        assertTrue(woodcutterHut.isReady());
        assertTrue(woodcutterHut.isUnoccupied());
        assertTrue(woodcutterHut.isDoorClosed());
    }

    @Test
    public void testDoorIsClosedOnOccupiedBuildingWithWorkerInside() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut and connect it with the headquarters */
        Point point1 = new Point(6, 12);
        var woodcutterHut = map.placeBuilding(new Woodcutter(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutterHut.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter hut to be finished and unoccupied */
        Utils.waitForBuildingToBeConstructed(woodcutterHut);

        /* Wait for the woodcutter hut to get occupied and have its worker inside */
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutterHut);

        assertTrue(woodcutterHut.getWorker().isInsideBuilding());
        assertTrue(woodcutterHut.isReady());
        assertTrue(woodcutterHut.isOccupied());
        assertTrue(woodcutterHut.isDoorClosed());
    }

    @Test
    public void testWoodcutterDoorOpensAndCloses() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place tree */
        Point point1 = new Point(8, 10);
        var tree = map.placeTree(point1, Tree.TreeType.OAK, Tree.TreeSize.FULL_GROWN);

        /* Place woodcutter hut and connect it with the headquarters */
        Point point2 = new Point(6, 12);
        var woodcutterHut = map.placeBuilding(new Woodcutter(player0), point2);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutterHut.getFlag(), headquarter0.getFlag());

        /* Wait for the woodcutter hut to be finished and unoccupied */
        for (int i = 0; i < 500; i++) {
            if (woodcutterHut.isReady() && woodcutterHut.isUnoccupied()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        /* Wait for the worker to come out */
        for (int i = 0; i < 500; i++) {
            var workers = Utils.findWorkersOfTypeOutsideForPlayer(WoodcutterWorker.class, player0);

            if (!workers.isEmpty()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        var worker = Utils.findWorkersOfTypeOutsideForPlayer(WoodcutterWorker.class, player0).getFirst();

        /* Verify that the door opens when the worker gets to the building's flag */
        for (int i = 0; i < 500; i++) {
            if (worker.isExactlyAtPoint() && worker.getPosition().equals(woodcutterHut.getFlag().getPosition())) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertFalse(woodcutterHut.isDoorClosed());
        assertEquals(worker.getTarget(), woodcutterHut.getPosition());

        for (int i = 0; i < 500; i++) {
            if (worker.isInsideBuilding()) {
                break;
            }

            assertFalse(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertTrue(worker.isInsideBuilding());
        assertTrue(woodcutterHut.isDoorClosed());

        /* Verify that the door of the woodcutter hut opens when the woodcutter worker goes outside */
        for (int i = 0; i < 200; i++) {
            if (!woodcutterHut.getWorker().isInsideBuilding()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertFalse(woodcutterHut.getWorker().isInsideBuilding());
        assertFalse(woodcutterHut.isDoorClosed());

        /* Verify that the door remains open for a little while and then closes */
        for (int i = 0; i < 10; i++) {
            assertFalse(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertTrue(woodcutterHut.isDoorClosed());

        /* Wait for the worker to be returning to the house */
        for (int i = 0; i < 2000; i++) {
            if (Objects.equals(worker.getTarget(), woodcutterHut.getPosition())) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        /* Verify that the door opens again when the woodcutter worker returns with cargo and is at the flag */
        for (int i = 0; i < 5000; i++) {
            if (woodcutterHut.getWorker().getPosition().equals(woodcutterHut.getFlag().getPosition())) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertEquals(woodcutterHut.getWorker().getPosition(), woodcutterHut.getFlag().getPosition());
        assertFalse(woodcutterHut.isDoorClosed());

        /* Verify that the door closes when the worker is inside the building again */
        for (int i = 0; i < 20; i++) {
            if (woodcutterHut.getWorker().isInsideBuilding()) {
                break;
            }

            assertFalse(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertTrue(woodcutterHut.getWorker().isInsideBuilding());
        assertTrue(woodcutterHut.isDoorClosed());

        /* Verify that the door opens when the worker goes out to leave the cargo */
        for (int i = 0; i < 200; i++) {
            if (!woodcutterHut.getWorker().isInsideBuilding()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertFalse(woodcutterHut.getWorker().isInsideBuilding());
        assertFalse(woodcutterHut.isDoorClosed());

        for (int i = 0; i < 200; i++) {
            if (woodcutterHut.getWorker().isInsideBuilding()) {
                break;
            }

            assertFalse(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertTrue(woodcutterHut.isDoorClosed());
    }

    @Test
    public void testHeadquarterDoorOpensAndClosesWhenItGetsDelivery() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a flag and a road */
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Wait for the road to get an assigned courier */
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Place a cargo to be delivered to the headquarters */
        var cargo = Utils.placeCargo(map, COIN, flag0, headquarter0);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Verify that the door of the headquarters is closed, and opens when the delivery happens */
        assertTrue(headquarter0.isDoorClosed());

        for (int i = 0; i < 2000; i++) {
            if (courier.getPosition().equals(headquarter0.getFlag().getPosition())) {
                break;
            }

            assertTrue(headquarter0.isDoorClosed());

            map.stepTime();
        }

        assertFalse(headquarter0.isDoorClosed());
        assertEquals(courier.getPosition(), headquarter0.getFlag().getPosition());

        for (int i = 0; i < 2000; i++) {
            if (courier.getPosition().equals(headquarter0.getPosition())) {
                break;
            }

            assertFalse(headquarter0.isDoorClosed());

            map.stepTime();
        }

        map.stepTime();

        assertEquals(courier.getPosition(), headquarter0.getPosition());
        assertNull(courier.getCargo());

        for (int i = 0; i < 2000; i++) {
            if (courier.getPosition().equals(headquarter0.getFlag().getPosition())) {
                break;
            }

            assertFalse(headquarter0.isDoorClosed());

            map.stepTime();
        }

        assertTrue(courier.isExactlyAtPoint());
        assertEquals(courier.getPosition(), headquarter0.getFlag().getPosition());
        assertTrue(headquarter0.isDoorClosed());
    }
}
