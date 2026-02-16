package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.*;


public class TestRobustTransportation {

    @Test
    public void testCourierDoesNotCarryItsCargoBackAndForth() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var players = new ArrayList<Player>();        players.add(player0);
        var map = new GameMap(players, 30, 30);

        // Place headquarter
        var point0 = new Point(8, 8);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(7, 5);
        var flag0 = map.placeFlag(player0, point1);

        // Place second flag
        var point2 = new Point(21, 5);
        var flag1 = map.placeFlag(player0, point2);

        // Place woodcutter
        var point3 = new Point(18, 8);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point3);

        // Create the short route
        var road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), woodcutter.getFlag());

        // Create the longer route
        var road1 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag0);
        var road2 = map.placeAutoSelectedRoad(player0, flag0, flag1);
        var road3 = map.placeAutoSelectedRoad(player0, flag1, woodcutter.getFlag());

        // Check that the longer route is a possible alternative for transportation
        var shortRoute = map.findWayWithExistingRoads(headquarter.getFlag().getPosition(), woodcutter.getPosition());
        List<Point> longerRoute = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(
                flag0,
                woodcutter,
                headquarter.getFlag().getPosition()
        );

        assertTrue(shortRoute.size() < longerRoute.size());
        assertTrue(shortRoute.size() * 2 > longerRoute.size());

        // Check that when the courier is standing on flag 0, going back via the headquarter's flag is the shortest route
        var secondShortestRoute = map.findWayWithExistingRoads(flag0.getPosition(), woodcutter.getPosition());

        assertEquals(secondShortestRoute.get(1), flag0.getPosition().upRight());

        // Wait for the roads to get assigned couriers and for the couriers to be idle
        var couriers = Utils.waitForRoadsToGetAssignedCouriers(map, road0, road1, road2, road3);

        Utils.waitForCouriersToBeIdle(map, couriers);

        // Place a cargo for the courier on the fastest route to pick up
        var cargo0 = Utils.placeCargo(map, GOLD, headquarter.getFlag(), woodcutter);

        // Wait for the courier of the short route to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), cargo0);

        // Place a second cargo that the courier for the first road of the longer route will pick up
        var cargo1 = Utils.placeCargo(map, STONE, headquarter.getFlag(), woodcutter);

        // Wait for the first courier of the longer route to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road1.getCourier(), cargo1);

        // Verify that the courier transports the cargo and does not immediately transport it back to the start again
        assertEquals(road1.getCourier().getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, road1.getCourier(), flag0.getPosition());

        assertNull(road1.getCourier().getCargo());
        assertEquals(road1.getCourier().getTarget(), flag0.getPosition().upRight());

        Utils.fastForwardUntilWorkerReachesPoint(map, road1.getCourier(), flag0.getPosition().upRight());

        assertNull(road1.getCourier().getCargo());
    }
}
