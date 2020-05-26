package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Mill;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Well;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestLimitedSpaceOnFlag {

    @Test
    public void testFlagCanContainRightAmountOfItems() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(6, 12);
        Building well0 = map.placeBuilding(new Well(player0), point1);

        /* Connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Wait for the well to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(well0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(well0);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the well can place eight cargos on the flag */
        for (int i = 0; i < 5000; i++) {
            if (well0.getFlag().getStackedCargo().size() == 8) {
                break;
            }

            map.stepTime();
        }

        assertEquals(well0.getFlag().getStackedCargo().size(), 8);
    }

    @Test
    public void testCourierWaitsToDeliverWhenFlagIsFull() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag far away from the headquarter */
        Point point1 = new Point(21, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag closer to the headquarter */
        Point point2 = new Point(17, 5);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place a short road to make a courier wait on */
        Road road0 = map.placeRoad(player0, flag0.getPosition(), flag0.getPosition().left(), flag1.getPosition());

        /* Place a long road to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        /* Wait for both roads to get their couriers assigned and standing idle */
        Collection<Courier> couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1);

        Utils.waitForCouriersToBeIdle(map, couriers);

        /* Place eight cargos on the flag between the roads targeting the headquarter */
        Utils.placeCargos(map, WATER, 8, flag1, headquarter0);

        /* Place two cargos on the other flag */
        Utils.placeCargo(map, IRON, flag0, headquarter0);

        map.stepTime();

        /* Wait for the courier of the short road to pick up a cargo */
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());
        assertNull(road1.getCourier().getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Wait for the courier for the short road to be one step away from the middle flag */
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());
        assertNull(road1.getCourier().getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition().right());

        /* Verify that the courier waits because there is no space on the flag for more cargo */
        assertEquals(flag1.getStackedCargo().size(), 8);

        for (int i = 0; i < 100; i++) {

            if (flag1.getStackedCargo().size() < 8) {
                break;
            }

            assertEquals(road0.getCourier().getPosition(), flag1.getPosition().right());
            assertFalse(road0.getCourier().isTraveling());
            assertNotNull(road0.getCourier().getCargo());
            assertTrue(flag1.getStackedCargo().size() < 9);

            map.stepTime();
        }
    }

    @Test
    public void testCourierResumesDeliveryAfterWaiting() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag far away from the headquarter */
        Point point1 = new Point(21, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag closer to the headquarter */
        Point point2 = new Point(17, 5);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place a short road to make a courier wait on */
        Road road0 = map.placeRoad(player0, flag0.getPosition(), flag0.getPosition().left(), flag1.getPosition());

        /* Place a long road to the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        /* Wait for both roads to get their couriers assigned and standing idle */
        Collection<Courier> couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1);

        Utils.waitForCouriersToBeIdle(map, couriers);

        /* Place eight cargos on the flag between the roads targeting the headquarter */
        Utils.placeCargos(map, WATER, 8, flag1, headquarter0);

        /* Place two cargos on the other flag */
        Utils.placeCargo(map, IRON, flag0, headquarter0);

        map.stepTime();

        /* Wait for the courier of the short road to pick up a cargo */
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());
        assertNull(road1.getCourier().getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Wait for the courier for the short road to be one step away from the middle flag */
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());
        assertNull(road1.getCourier().getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition().right());

        /* Wait for the courier on the long road to deliver its cargo and come back to pick up the second cargo */
        assertEquals(flag1.getStackedCargo().size(), 8);

        for (int i = 0; i < 1000; i++) {

            if (road1.getCourier().isExactlyAtPoint() && road1.getCourier().getPosition().equals(flag1.getPosition())) {
                break;
            }

            assertEquals(road0.getCourier().getPosition(), flag1.getPosition().right());
            assertFalse(road0.getCourier().isTraveling());
            assertNotNull(road0.getCourier().getCargo());
            assertTrue(flag1.getStackedCargo().size() < 9);

            map.stepTime();
        }

        /* Verify that the courier for the short road delivers its cargo when the other courier picks up a cargo so there is space available */
        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road1.getCourier().getPosition(), flag1.getPosition());
        assertNotNull(road1.getCourier().getCargo());
        assertEquals(flag1.getStackedCargo().size(), 7);

        map.stepTime();

        assertEquals(road0.getCourier().getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition());

        assertEquals(flag1.getStackedCargo().size(), 8);
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testOnlyOneCourierDeliversWhenThereIsOnlySpaceForOneButTwoWantsToDeliver() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag far away from the headquarter */
        Point point1 = new Point(21, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag closer to the headquarter */
        Point point2 = new Point(17, 5);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(15, 7);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place a short road to make */
        Road road0 = map.placeRoad(player0, flag0.getPosition(), flag0.getPosition().left(), flag1.getPosition());

        /* Place a second short road */
        Road road1 = map.placeRoad(player0, flag2.getPosition(), flag2.getPosition().downRight(), flag1.getPosition());

        /* Place a long road to the headquarter */
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        /* Wait for both roads to get their couriers assigned and standing idle */
        Collection<Courier> couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1, road2);

        Utils.waitForCouriersToBeIdle(map, couriers);

        /* Place eight cargos on the flag between the roads targeting the headquarter */
        Utils.placeCargos(map, WATER, 7, flag1, headquarter0);

        /* Place cargos on the other flags for the short roads */
        Utils.placeCargo(map, IRON, flag0, headquarter0);
        Utils.placeCargo(map, COAL, flag2, headquarter0);

        map.stepTime();

        /* Wait for the couriers of the short roads to pick up their cargo */
        Courier courierAtLongRoad = road2.getCourier();

        assertEquals(courierAtLongRoad.getTarget(), flag1.getPosition());
        assertNull(courierAtLongRoad.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road1.getCourier().getPosition(), flag2.getPosition());

        /* Wait for the couriers for the short roads to be one step away from the middle flag */
        assertEquals(courierAtLongRoad.getTarget(), flag1.getPosition());
        assertNull(courierAtLongRoad.getCargo());
        assertEquals(road0.getCourier().getTarget(), flag1.getPosition());
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition().right());

        assertTrue(road0.getCourier().isExactlyAtPoint());
        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road0.getCourier().getPosition(), flag1.getPosition().right());
        assertEquals(road1.getCourier().getPosition(), flag1.getPosition().upLeft());

        /* Verify that one of the couriers delivers its cargo and the other waits */
        assertEquals(flag1.getStackedCargo().size(), 7);

        Courier courierWalking;
        Courier courierWaiting;
        Point waitingPoint;

        if (road0.getCourier().getTarget().equals(flag1.getPosition())) {
            assertNull(road1.getCourier().getTarget());

            courierWalking = road0.getCourier();
            courierWaiting = road1.getCourier();
        } else {
            assertNull(road0.getCourier().getTarget());
            assertEquals(road1.getCourier().getTarget(), flag1.getPosition());

            courierWalking = road1.getCourier();
            courierWaiting = road0.getCourier();
        }

        waitingPoint = courierWaiting.getPosition();

        for (int i = 0; i < 1000; i++) {

            if (courierWalking.isExactlyAtPoint() && courierWalking.getPosition().equals(flag1.getPosition())) {
                break;
            }

            assertEquals(courierWaiting.getPosition(), waitingPoint);
            assertEquals(courierWalking.getTarget(), flag1.getPosition());

            map.stepTime();
        }

        assertNull(courierWalking.getCargo());
        assertEquals(courierWaiting.getPosition(), waitingPoint);
    }

    @Test
    public void testOnlyOneCourierResumesDeliveryWhenThereIsSpaceAfterTwoHaveWaitedToDeliver() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag far away from the headquarter */
        Point point1 = new Point(21, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag closer to the headquarter */
        Point point2 = new Point(17, 5);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(15, 7);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place a short road to make */
        Road road0 = map.placeRoad(player0, flag0.getPosition(), flag0.getPosition().left(), flag1.getPosition());

        /* Place a second short road */
        Road road1 = map.placeRoad(player0, flag2.getPosition(), flag2.getPosition().downRight(), flag1.getPosition());

        /* Place a long road to the headquarter */
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        /* Wait for both roads to get their couriers assigned and standing idle */
        Collection<Courier> couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1, road2);

        Utils.waitForCouriersToBeIdle(map, couriers);

        /* Place eight cargos on the flag between the roads targeting the headquarter */
        Utils.placeCargos(map, WATER, 8, flag1, headquarter0);

        /* Place cargos on the other flags for the short roads */
        Utils.placeCargo(map, IRON, flag0, headquarter0);
        Utils.placeCargo(map, COAL, flag2, headquarter0);

        map.stepTime();

        /* Wait for the couriers of the short roads to pick up their cargo */
        Courier courierAtLongRoad = road2.getCourier();

        assertEquals(courierAtLongRoad.getTarget(), flag1.getPosition());
        assertNull(courierAtLongRoad.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road1.getCourier().getPosition(), flag2.getPosition());

        /* Wait for the couriers for the short roads to be one step away from the middle flag */
        assertEquals(courierAtLongRoad.getTarget(), flag1.getPosition());
        assertNull(courierAtLongRoad.getCargo());
        assertEquals(road0.getCourier().getTarget(), flag1.getPosition());
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition().right());

        assertTrue(road0.getCourier().isExactlyAtPoint());
        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road0.getCourier().getPosition(), flag1.getPosition().right());
        assertEquals(road1.getCourier().getPosition(), flag1.getPosition().upLeft());

        /* Both of the couriers wait */
        assertEquals(flag1.getStackedCargo().size(), 8);
        assertNull(road0.getCourier().getTarget());
        assertNull(road1.getCourier().getTarget());

        /* Wait for the courier for the long road to pick up one cargo */
        for (int i = 0; i < 300; i++) {
            if (courierAtLongRoad.isExactlyAtPoint() && courierAtLongRoad.getPosition().equals(flag1.getPosition())) {
                break;
            }

            assertNull(road0.getCourier().getTarget());
            assertNull(road1.getCourier().getTarget());
            assertEquals(road0.getCourier().getPosition(), flag0.getPosition().left());
            assertEquals(road1.getCourier().getPosition(), flag1.getPosition().upLeft());

            map.stepTime();
        }

        assertEquals(flag1.getStackedCargo().size(), 7);

        /* Verify that one of the waiting couriers does its delivery and the other one keeps waiting */
        map.stepTime();

        Courier courierWalking;
        Courier courierWaiting;
        Point waitingPoint;

        if (flag1.getPosition().equals(road0.getCourier().getTarget())) {
            assertNull(road1.getCourier().getTarget());

            courierWalking = road0.getCourier();
            courierWaiting = road1.getCourier();
        } else {
            assertNull(road0.getCourier().getTarget());
            assertEquals(road1.getCourier().getTarget(), flag1.getPosition());

            courierWalking = road1.getCourier();
            courierWaiting = road0.getCourier();
        }

        waitingPoint = courierWaiting.getPosition();

        for (int i = 0; i < 1000; i++) {

            if (courierWalking.isExactlyAtPoint() && courierWalking.getPosition().equals(flag1.getPosition())) {
                break;
            }

            assertEquals(courierWaiting.getPosition(), waitingPoint);
            assertEquals(courierWalking.getTarget(), flag1.getPosition());

            map.stepTime();
        }

        assertNull(courierWalking.getCargo());
        assertEquals(courierWaiting.getPosition(), waitingPoint);
    }

    @Test
    public void testSeveralDeliveriesHappenInSequenceAfterTwoHaveWaitedToDeliver() throws Exception {

        /* Start new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag far away from the headquarter */
        Point point1 = new Point(21, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag closer to the headquarter */
        Point point2 = new Point(17, 5);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(15, 7);
        Flag flag2 = map.placeFlag(player0, point3);

        /* Place a short road to make */
        Road road0 = map.placeRoad(player0, flag0.getPosition(), flag0.getPosition().left(), flag1.getPosition());

        /* Place a second short road */
        Road road1 = map.placeRoad(player0, flag2.getPosition(), flag2.getPosition().downRight(), flag1.getPosition());

        /* Place a long road to the headquarter */
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, headquarter0.getFlag());

        /* Wait for both roads to get their couriers assigned and standing idle */
        Collection<Courier> couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1, road2);

        Utils.waitForCouriersToBeIdle(map, couriers);

        /* Place eight cargos on the flag between the roads targeting the headquarter */
        Utils.placeCargos(map, WATER, 8, flag1, headquarter0);

        /* Place cargos on the other flags for the short roads */
        Utils.placeCargo(map, IRON, flag0, headquarter0);
        Utils.placeCargo(map, COAL, flag2, headquarter0);

        map.stepTime();

        /* Wait for the couriers of the short roads to pick up their cargo */
        Courier courierAtLongRoad = road2.getCourier();

        assertEquals(courierAtLongRoad.getTarget(), flag1.getPosition());
        assertNull(courierAtLongRoad.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road1.getCourier().getPosition(), flag2.getPosition());

        /* Wait for the couriers for the short roads to be one step away from the middle flag */
        assertEquals(courierAtLongRoad.getTarget(), flag1.getPosition());
        assertNull(courierAtLongRoad.getCargo());
        assertEquals(road0.getCourier().getTarget(), flag1.getPosition());
        assertEquals(road1.getCourier().getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), flag1.getPosition().right());

        assertTrue(road0.getCourier().isExactlyAtPoint());
        assertTrue(road1.getCourier().isExactlyAtPoint());
        assertEquals(road0.getCourier().getPosition(), flag1.getPosition().right());
        assertEquals(road1.getCourier().getPosition(), flag1.getPosition().upLeft());

        /* Both of the couriers wait */
        assertEquals(flag1.getStackedCargo().size(), 8);
        assertNull(road0.getCourier().getTarget());
        assertNull(road1.getCourier().getTarget());

        /* Wait for the courier for the long road to pick up one cargo */
        for (int i = 0; i < 300; i++) {
            if (courierAtLongRoad.isExactlyAtPoint() && courierAtLongRoad.getPosition().equals(flag1.getPosition())) {
                break;
            }

            assertNull(road0.getCourier().getTarget());
            assertNull(road1.getCourier().getTarget());
            assertEquals(road0.getCourier().getPosition(), flag0.getPosition().left());
            assertEquals(road1.getCourier().getPosition(), flag1.getPosition().upLeft());

            map.stepTime();
        }

        assertEquals(flag1.getStackedCargo().size(), 7);

        /* Verify that one of the waiting couriers does its delivery and the other one keeps waiting */
        map.stepTime();

        Courier courierWalking;
        Courier courierWaiting;
        Point waitingPoint;

        if (flag1.getPosition().equals(road0.getCourier().getTarget())) {
            assertNull(road1.getCourier().getTarget());

            courierWalking = road0.getCourier();
            courierWaiting = road1.getCourier();
        } else {
            assertNull(road0.getCourier().getTarget());
            assertEquals(road1.getCourier().getTarget(), flag1.getPosition());

            courierWalking = road1.getCourier();
            courierWaiting = road0.getCourier();
        }

        waitingPoint = courierWaiting.getPosition();

        for (int i = 0; i < 1000; i++) {

            if (courierWalking.isExactlyAtPoint() && courierWalking.getPosition().equals(flag1.getPosition())) {
                break;
            }

            assertEquals(courierWaiting.getPosition(), waitingPoint);
            assertEquals(courierWalking.getTarget(), flag1.getPosition());

            map.stepTime();
        }

        assertNull(courierWalking.getCargo());
        assertEquals(courierWaiting.getPosition(), waitingPoint);

        /* Wait for the courier for the long road to pick up a cargo */
        assertEquals(flag1.getStackedCargo().size(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, courierAtLongRoad, flag1.getPosition());

        assertEquals(flag1.getStackedCargo().size(), 7);

        /* Verify that the waiting courier can now do its delivery */
        map.stepTime();

        assertEquals(courierWaiting.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courierWaiting, flag1.getPosition());

        assertNull(courierWaiting.getCargo());
        assertEquals(flag1.getStackedCargo().size(), 8);
    }

    @Test
    public void testContinuousDeliveryToHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(12, 8);
        Building mill = map.placeBuilding(new Mill(player0), point1);

        /* Connect the mill with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        /* Wait for the mill to get constructed and assigned a miller */
        Utils.waitForBuildingToBeConstructed(mill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        /* Put a lot of wheat into the headquarter */
        Utils.adjustInventoryTo(headquarter, WHEAT, 30);

        /* Verify that the mill keeps operating and produces more flour than the limit of the flag */
        for (int i = 0; i < 20; i++) {

            /* Wait for the miller to come out carrying flour */
            Utils.fastForwardUntilWorkerCarriesCargo(map, mill.getWorker(), FLOUR);

            assertNotNull(mill.getWorker().getCargo());
            assertEquals(mill.getWorker().getCargo().getMaterial(), FLOUR);

            /* Wait for the miller to leave the flour at the flag */
            Utils.fastForwardUntilWorkerReachesPoint(map, mill.getWorker(), mill.getFlag().getPosition());

            assertNull(mill.getWorker().getCargo());
        }
    }
}
