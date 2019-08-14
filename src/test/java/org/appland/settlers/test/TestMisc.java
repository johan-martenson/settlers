package org.appland.settlers.test;

import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestMisc {

    @Test
    public void testRemoveRoadWhenCourierGoesToBuildingToDeliverCargo() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Placing headquarter */
        Point point0 = new Point(429, 201);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Place flag */
        Point point1 = new Point(434, 200);
        Flag flag0 = map.placeFlag(player, point1);

        /* Place automatic road between flag and headquarter's flag */
        Road road0 = map.placeAutoSelectedRoad(player, headquarter0.getFlag().getPosition(), point1);

        /* Place woodcutter by the flag */
        Woodcutter woodcutter0 = map.placeBuilding(new org.appland.settlers.model.Woodcutter(player), flag0.getPosition().upLeft());

        /* Wait for the road to get an assigned courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Fast forward a bit until the courier is carrying a cargo to deliver to the woodcutter */
        for (int i = 0; i < 2000; i++) {

            if (courier.getCargo() != null &&
                woodcutter0.getPosition().equals(courier.getTarget()) &&
                courier.getNextPoint().equals(woodcutter0.getPosition().downRight())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(map.getWorkers().size(), 2);
        assertNotNull(courier.getCargo());
        assertEquals(courier.getTarget(), woodcutter0.getPosition());
        assertEquals(courier.getLastPoint(), headquarter0.getFlag().getPosition().right());
        assertEquals(courier.getNextPoint(), woodcutter0.getPosition().downRight());

        /* Remove the flag and cause the woodcutter to get torn down */
        map.removeFlag(map.getFlagAtPoint(point1));

        assertEquals(map.getWorkers().size(), 2);

        /* Verify that the courier goes back to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertEquals(courier.getPosition(), headquarter0.getPosition());
        assertFalse(map.getWorkers().contains(courier));
    }

    @Test
    public void testScoutReturnsWhenFlagRemainsButRoadHasBeenRemoved() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Placing headquarter */
        Point point0 = new Point(429, 201);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Place flag */
        Point point1 = new Point(434, 200);
        Flag flag0 = map.placeFlag(player, point1);

        /* Call scout */
        flag0.callScout();

        /* Create a road that connects the flag with the headquarter's flag */
        Road road0 = map.placeAutoSelectedRoad(player, new Point(430, 200), new Point(434, 200));

        /* Wait for a scout to appear */
        Scout scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player, map).get(0);

        /* Wait the scout to get to the flag */
        assertEquals(scout.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        assertEquals(scout.getPosition(), flag0.getPosition());

        /* Wait for the scout to continue away from the flag */
        Utils.fastForward(10, map);

        assertNotEquals(scout.getPosition(), flag0.getPosition());

        /* Remove the road so the scout has no way back using roads */
        map.removeRoad(road0);

        /* Wait for the scout to get back to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        assertEquals(scout.getPosition(), flag0.getPosition());

        /* Verify that the scout goes back to the headquarter */
        assertEquals(scout.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getPosition());

        assertEquals(scout.getPosition(), headquarter0.getPosition());
    }
}
