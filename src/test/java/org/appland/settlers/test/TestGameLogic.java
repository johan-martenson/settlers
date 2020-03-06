package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestGameLogic {

    @Test
    public void testInitiateNewDeliveries() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = new Headquarter(player0);
        map.placeBuilding(headquarter, point0);

        /* Place woodcutter */
        Point point1 = new Point(5, 11);
        Woodcutter woodcutter0 = new Woodcutter(player0);
        map.placeBuilding(woodcutter0, point1);

        /* Place road to connect the woodcutter and the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), woodcutter0.getFlag());

        Utils.constructHouse(woodcutter0, map);

        /* Since the woodcutter is finished it does not need any deliveries
         * Verify that no new deliveries are initiated
         */
        assertTrue(headquarter.getFlag().getStackedCargo().isEmpty());
        assertNull(headquarter.getWorker().getCargo());

        map.stepTime();

        assertTrue(headquarter.getFlag().getStackedCargo().isEmpty());
        assertNull(headquarter.getWorker().getCargo());

        /* Fast forward so the worker in the headquarter is rested */
        Utils.fastForward(20, map);

        /* Place an unfinished sawmill on the map and verify that it needs deliveries */
        Sawmill sawmill0 = new Sawmill(player0);

        Point point2 = new Point(10, 10);

        map.placeBuilding(sawmill0, point2);

        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), sawmill0.getFlag());

        /* Verify that a new delivery is initiated for the sawmill */
        assertTrue(sawmill0.needsMaterial(PLANK));
        assertTrue(sawmill0.needsMaterial(STONE));
        assertTrue(headquarter.getFlag().getStackedCargo().isEmpty());

        map.stepTime();

        assertNotNull(headquarter.getWorker().getCargo());
    }

    @Test
    public void testAssignWorkToIdleCouriers() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place sawmill */
        Point point2 = new Point(5, 5);
        Sawmill sawmill0 = new Sawmill(player0);
        map.placeBuilding(sawmill0, point2);

        /* Place road to connect the flag with the sawmill's flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Fast forward so the courier can reach its road and be assigned */
        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        Cargo cargo = new Cargo(PLANK, map);

        cargo.setPosition(flag0.getPosition());
        cargo.setTarget(sawmill0);
        flag0.putCargo(cargo);

        /* Verify that the worker is idle */
        assertTrue(courier.isIdle());
        assertNull(courier.getCargo());

        assertTrue(courier.isArrived());
        assertFalse(courier.isTraveling());
        assertTrue(flag0.hasCargoWaitingForRoad(courier.getAssignedRoad()));

        /* Verify that the worker picks up the cargo and has the sawmill as target */
        map.stepTime();

        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(cargo, courier.getCargo());
        assertEquals(courier.getTarget(), sawmill0.getPosition());
    }

    @Test
    public void testDeliverForWorkersAtTarget() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(5, 5);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place woodcutter */
        Point point2 = new Point(11, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place road to connect the flag with the woodcutter's flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road0, map);

        /* Fast forward to let the courier reach its road and get assigned */
        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        Cargo cargo0 = new Cargo(PLANK, map);

        flag0.putCargo(cargo0);
        cargo0.setTarget(woodcutter0);

        /* Let the courier detect and pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        assertEquals(courier.getCargo(), cargo0);

        /* Move worker to the sawmill */
        assertEquals(courier.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, woodcutter0.getPosition());

        assertTrue(courier.isAt(woodcutter0.getPosition()));

        /* Verify the worker delivers the cargo when it has reached the target */
        assertNull(courier.getCargo());
        assertTrue(courier.isAt(woodcutter0.getPosition()));
        assertEquals(woodcutter0.getAmount(PLANK), 1);
    }

    @Test
    public void testAssignNewWorkerToUnoccupiedPlaces() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place the barracks' flag */
        Point point1 = new Point(6, 10);
        Flag flag0 = map.placeFlag(player0, point1.downRight());

        /* Place the forester hut's flag */
        Point point2 = new Point(10, 10);
        Flag flag1 = map.placeFlag(player0, point2.downRight());

        /* Assign new workers to unoccupied places. Since there are no places
         * that require workers this should not do anything */
        assertEquals(map.getWorkers().size(), 1);

        /* Step time to make the headquarter assign new workers */
        Utils.fastForward(3, map);

        assertEquals(map.getWorkers().size(), 1);

        /* Construct a road without any courier assigned */
        Road road0  = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        assertEquals(map.getWorkers().size(), 1);

        /* Prep the headquarter's inventory */
        assertTrue(headquarter0.getAmount(PRIVATE) >= 10);
        assertTrue(headquarter0.getAmount(FORESTER) >= 3);
        assertEquals(map.getWorkers().size(), 1);

        /* Assign new workers to unoccupied places and verify that there is a
         * worker designated for the road. There should be no worker for the
         * barracks as it's not finished yet
         */
        Utils.fastForward(3, map);

        // TODO: Ensure that only one worker is dispatched even though there are two unassigned roads
        assertEquals(map.getWorkers().size(), 3);

        assertTrue(map.getWorkers().get(1) instanceof Courier);
        assertTrue(map.getWorkers().get(2) instanceof Courier);

        Courier courier0 = (Courier)map.getWorkers().get(1);
        Courier courier1 = (Courier)map.getWorkers().get(2);

        /* Fast forward to let the couriers reach their roads */
        Utils.fastForwardUntilWorkersReachTarget(map, courier0, courier1);

        assertEquals(courier0.getAssignedRoad(), road0);

        assertEquals(courier1.getAssignedRoad(), road1);

        assertEquals(map.getRoad(headquarter0.getFlag().getPosition(), flag0.getPosition()), road0);
        assertNotNull(road0.getCourier().getAssignedRoad());

        assertEquals(road0.getCourier(), courier0);
        assertEquals(road0.getCourier().getAssignedRoad(), road0);

        assertEquals(map.getWorkers().size(), 3);

        /* Construct the barracks */
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        assertTrue(barracks0.underConstruction());
        assertTrue(barracks0.isMilitaryBuilding());
        assertTrue(headquarter0.getAmount(Material.PRIVATE) >= 10);

        int currentNumberOfMilitary = headquarter0.getAmount(PRIVATE);

        Utils.constructHouse(barracks0, map);

        assertTrue(barracks0.needsMilitaryManning());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 0);

        /* Step time to make the headquarter assign new workers */
        map.stepTime();

        assertEquals(map.getWorkers().size(), 4);
        assertTrue(map.getWorkers().get(3) instanceof Military);
        assertFalse(map.getWorkers().get(3).isArrived());
        assertTrue(map.getWorkers().get(3).isTraveling());

        assertEquals(headquarter0.getAmount(Material.PRIVATE), currentNumberOfMilitary - 1);

        /* Let the military reach the barracks */
        Utils.fastForwardUntilWorkersReachTarget(map, map.getWorkers().get(3));

        assertEquals(map.getWorkers().size(), 5);
        assertTrue(map.getWorkers().get(3).isArrived());
        assertFalse(map.getWorkers().get(3).isTraveling());

        /* Make traveling workers that have arrived enter their building or road */

        Utils.fastForwardUntilWorkersReachTarget(map, map.getWorkers().get(4));

        assertFalse(barracks0.needsMilitaryManning());
        assertEquals(barracks0.getNumberOfHostedMilitary(), 2);

        /* Assign new workers to unoccupied buildings again. There is building
         * or road that requires a worker so this should have no effect
         */
        assertEquals(map.getWorkers().size(), 5);

        map.stepTime();

        assertEquals(map.getWorkers().size(), 5);

        /* Finish construction of the forester hut which requires a
         * forester worker to function
         */
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point2);
        Utils.constructHouse(foresterHut0, map);

        assertTrue(foresterHut0.needsWorker());

        /* Assign new workers to unoccupied buildings and roads. The forester
         * hut needs a forester so a forester should be dispatched from the hq
         */
        assertTrue(headquarter0.getAmount(FORESTER) >= 3);
        assertTrue(foresterHut0.needsWorker());

        /* Step time to make the headquarter assign new workers */
        map.stepTime();

        assertEquals(map.getWorkers().size(), 6);
        assertFalse(foresterHut0.needsWorker());

        Forester forester = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Forester) {
                forester = (Forester)worker;
            }
        }

        /* Let the forester reach the forester hut */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertNotNull(foresterHut0.getWorker());
        assertTrue(foresterHut0.getWorker() instanceof Forester);
    }
}
