package org.appland.settlers.test;

import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestCourierWalksOnItsOwnRoad {

    /*
    * TODO: Verify that the courier walks on its own road when shorter alternatives exist in these cases...
    *   - Flag 1 to flag 2
    *   - Flag 2 to flag 1
    *   - Flag 1 to building 2 DONE
    *   - Flag 2 to building 1
    *   - Building 1 to building 2
    *   - Building 2 to building 1
    *   - All of the above also for donkeys
    * */

    @Test
    public void testCourierWalksOnOwnRoadWhenDeliveringFromFirstFlagToSecondFlag() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(24, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(28, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place woodcutter */
        Point point3 = new Point(31, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point3);

        /* Connect the headquarter with the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Connect the second flag with the woodcutter */
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, woodcutter.getFlag());

        /* Wait for the first road to get occupied */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier to carry cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertEquals(courier.getCargo().getTarget(), woodcutter);

        /* Fill up the flag to make it impossible to deliver cargo the fast way */
        Utils.placeCargos(map, STONE, 8, flag0, headquarter);

        /* Wait for the courier to get blocked */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition().left());

        /* Make sure the courier is stuck */
        Utils.verifyWorkerDoesNotMove(map, courier, 20);

        /* Place a second, longer road between the headquarter and the second flag */
        Road road3 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag1);

        /* Wait for the new road to get occupied */
        Courier courier1 = Utils.waitForRoadToGetAssignedCourier(map, road3);

        /* Wait for the new courier to carry a cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1);

        /* Verify that the courier walks to the woodcutter following its own road */
        assertEquals(courier1.getPosition(), headquarter.getFlag().getPosition());
        assertNotNull(courier1.getCargo());
        assertEquals(courier1.getCargo().getTarget(), woodcutter);
        assertEquals(courier1.getTarget(), flag1.getPosition());
        assertEquals(courier.getPosition(), flag0.getPosition().left());

        Utils.verifyWorkerWalksOnPath(map, courier1,
                headquarter.getFlag().getPosition(),
                headquarter.getFlag().getPosition().downRight(),
                headquarter.getFlag().getPosition().downRight().right(),
                flag1.getPosition().downLeft().left(),
                flag1.getPosition().downLeft(),
                flag1.getPosition());
    }

    @Test
    public void testCourierWalksOnOwnRoadWhenDeliveringFromSecondFlagToFirstFlag() throws InvalidEndPointException, InvalidUserActionException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(24, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(28, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place woodcutter */
        Point point3 = new Point(31, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point3);

        /* Connect the headquarter with the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Connect the second flag with the woodcutter */
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, woodcutter.getFlag());

        /* Remove all planks from the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 0);

        /* Wait for the second road to get occupied */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road2);

        /* Place a cargo for the courier to pick up */
        Utils.placeCargo(map, PLANK, woodcutter.getFlag(), headquarter);

        /* Wait for the courier to carry cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertEquals(courier.getCargo().getTarget(), headquarter);

        /* Fill up the first flag to make it impossible to deliver cargo the fast way */
        Utils.placeCargos(map, STONE, 8, flag1, woodcutter);

        /* Wait for the courier to get blocked */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag1.getPosition().right());

        /* Make sure the courier is stuck */
        Utils.verifyWorkerDoesNotMove(map, courier, 20);

        /* Place a second, longer road between the second flag and the headquarter */
        Road road3 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        /* Wait for the new road to get occupied */
        Courier courier1 = Utils.waitForRoadToGetAssignedCourier(map, road3);

        /* Place a cargo for the courier to pick up */
        Utils.placeCargo(map, GOLD, woodcutter.getFlag(), headquarter);

        /* Wait for the new courier to carry a cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1);

        /* Verify that the courier walks to the woodcutter following its own road */
        assertEquals(courier1.getPosition(), woodcutter.getFlag().getPosition());
        assertNotNull(courier1.getCargo());
        assertEquals(courier1.getCargo().getTarget(), headquarter);
        assertEquals(courier1.getTarget(), flag0.getPosition());

        Utils.verifyWorkerWalksOnPath(map, courier1,
                woodcutter.getFlag().getPosition(),
                woodcutter.getFlag().getPosition().downLeft(),
                woodcutter.getFlag().getPosition().downLeft().left(),
                flag0.getPosition().downRight().right(),
                flag0.getPosition().downRight(),
                flag0.getPosition());
    }

    @Test
    public void testCourierWalksOnOwnRoadWhenDeliveringFromFirstFlagToSecondBuilding() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(24, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place woodcutter */
        Point point2 = new Point(27, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        /* Connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag0);

        /* Connect the flag with the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        /* Wait for the first road to get occupied */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier to carry cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertEquals(courier.getCargo().getTarget(), woodcutter);

        /* Fill up the flag to make it impossible to deliver cargo the fast way */
        Utils.placeCargos(map, STONE, 8, flag0, headquarter);

        /* Wait for the courier to get blocked */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition().left());

        /* Make sure the courier is stuck */
        Utils.verifyWorkerDoesNotMove(map, courier, 20);

        /* Place a second, longer road between the headquarter and the woodcutter */
        Road road2 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), woodcutter.getFlag());

        /* Wait for the new road to get occupied */
        Courier courier1 = Utils.waitForRoadToGetAssignedCourier(map, road2);

        /* Wait for the new courier to carry a cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1);

        /* Verify that the courier walks to the woodcutter following its own road */
        assertEquals(courier1.getPosition(), headquarter.getFlag().getPosition());
        assertNotNull(courier1.getCargo());
        assertEquals(courier1.getCargo().getTarget(), woodcutter);
        assertEquals(courier1.getTarget(), woodcutter.getPosition());
        assertEquals(courier.getPosition(), flag0.getPosition().left());

        Utils.verifyWorkerWalksOnPath(map, courier1,
                headquarter.getFlag().getPosition(),
                headquarter.getFlag().getPosition().downRight(),
                headquarter.getFlag().getPosition().downRight().right(),
                woodcutter.getFlag().getPosition().downLeft().left(),
                woodcutter.getFlag().getPosition().downLeft(),
                woodcutter.getFlag().getPosition(),
                woodcutter.getPosition());
    }

    @Test
    public void testCourierWalksOnOwnRoadWhenDeliveringFromSecondFlagToFirstBuilding() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(19, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(24, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(28, 4);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place woodcutter */
        Point point3 = new Point(31, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point3);

        /* Connect the headquarter with the first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Connect the second flag with the woodcutter */
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, woodcutter.getFlag());

        /* Remove all planks from the headquarter */
        Utils.adjustInventoryTo(headquarter, PLANK, 0);

        /* Wait for the second road to get occupied */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road2);

        /* Place a cargo for the courier to pick up */
        Utils.placeCargo(map, PLANK, woodcutter.getFlag(), headquarter);

        /* Wait for the courier to carry cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertEquals(courier.getCargo().getTarget(), headquarter);

        /* Fill up the first flag to make it impossible to deliver cargo the fast way */
        Utils.placeCargos(map, STONE, 8, flag1, woodcutter);

        /* Wait for the courier to get blocked */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag1.getPosition().right());

        /* Make sure the courier is stuck */
        Utils.verifyWorkerDoesNotMove(map, courier, 20);

        /* Place a second, longer road between the second flag and the headquarter */
        Road road3 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), woodcutter.getFlag());

        /* Wait for the new road to get occupied */
        Courier courier1 = Utils.waitForRoadToGetAssignedCourier(map, road3);

        /* Place a cargo for the courier to pick up */
        Utils.placeCargo(map, GOLD, woodcutter.getFlag(), headquarter);

        /* Wait for the new courier to carry a cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier1);

        /* Verify that the courier walks to the woodcutter following its own road */
        assertEquals(courier1.getPosition(), woodcutter.getFlag().getPosition());
        assertNotNull(courier1.getCargo());
        assertEquals(courier1.getCargo().getTarget(), headquarter);
        assertEquals(courier1.getTarget(), headquarter.getPosition());

        Utils.verifyWorkerWalksOnPath(map, courier1,
                woodcutter.getFlag().getPosition(),
                woodcutter.getFlag().getPosition().downLeft(),
                woodcutter.getFlag().getPosition().downLeft().left(),
                woodcutter.getFlag().getPosition().downLeft().left().left(),
                headquarter.getFlag().getPosition().downRight().right().right(),
                headquarter.getFlag().getPosition().downRight().right(),
                headquarter.getFlag().getPosition().downRight(),
                headquarter.getFlag().getPosition());
    }

    public void testCourierWalksOnOwnRoadWhenDeliveringFromFirstBuildingToSecondBuilding() {}

    public void testCourierWalksOnOwnRoadWhenDeliveringFromSecondBuildingToFirstBuilding() {}
}
