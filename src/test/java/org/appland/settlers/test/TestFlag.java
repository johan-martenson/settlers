package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Flag.Type.MAIN;
import static org.appland.settlers.model.Flag.Type.MARINE;
import static org.appland.settlers.model.Flag.Type.NORMAL;
import static org.appland.settlers.model.Material.COIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestFlag {

    @Test
    public void testFlagTypes() {
        assertEquals(Flag.Type.values().length, 3);

        assertEquals(NORMAL.name(), "NORMAL");
        assertEquals(Flag.Type.MAIN.name(), "MAIN");
        assertEquals(MARINE.name(), "MARINE");
    }

    @Test
    public void testFlagOnLandAtStartIsNormal() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that the headquarter's flag is normal */
        assertEquals(headquarter0.getFlag().getType(), NORMAL);
    }

    @Test
    public void testFlagNextToWaterIsMarine() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place lake */
        Point point1 = new Point(10, 10);
        Utils.surroundPointWithWater(point1, map);

        /* Verify that a flag placed next to water is a marine flag */
        Flag flag0 = map.placeFlag(player0, point1.upLeft());
        assertEquals(flag0.getType(), MARINE);
    }

    @Test
    public void testFlagAtMainRoadIsMainFlag() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point38 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point38);

        /* Place flag */
        Point point2 = new Point(5, 9);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place flag */
        Point point3 = new Point(5, 13);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place road between the headquarter and the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road between the headquarter and the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Place workers on the roads */
        Courier courier0 = Utils.occupyRoad(road0, map);
        Courier courier1 = Utils.occupyRoad(road1, map);

        /* Deliver 99 cargo and verify that the road does not become a main road */
        for (int i = 0; i < 99; i++) {
            Cargo cargo = new Cargo(COIN, map);

            flag1.putCargo(cargo);

            cargo.setTarget(headquarter0);

            /* Wait for the courier to pick up the cargo */
            assertNull(courier1.getCargo());

            Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

            /* Wait for the courier to deliver the cargo */
            assertEquals(courier1.getTarget(), flag0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

            assertNull(courier1.getCargo());

            assertFalse(road1.isMainRoad());
        }

        /* Deliver one more cargo and verify that the road becomes a main road */
        Cargo cargo = new Cargo(COIN, map);

        flag1.putCargo(cargo);

        cargo.setTarget(headquarter0);

        /* Wait for the courier to pick up the cargo */
        assertNull(courier1.getCargo());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1, cargo);

        /* Wait for the road to become a main road and verify that a donkey gets dispatched from the headquarter */
        assertEquals(courier1.getTarget(), flag0.getPosition());
        assertNull(road1.getDonkey());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

        assertTrue(road1.isMainRoad());
        assertEquals(flag0.getType(), MAIN);
        assertEquals(flag1.getType(), MAIN);
    }
}
