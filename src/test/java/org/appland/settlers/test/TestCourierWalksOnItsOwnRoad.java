package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
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

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestCourierWalksOnItsOwnRoad {

    @Test
    public void testCourierWalksOnOwnRoadWhenDeliveringFromFirstFlagToSecondFlag() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
    public void testCourierWalksOnOwnRoadWhenDeliveringFromSecondFlagToFirstFlag() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Wait for the roads to get occupied */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road2);
        Courier courier3 = Utils.waitForRoadToGetAssignedCourier(map, road1);

        Utils.waitForCouriersToBeIdle(map, courier, courier3);

        /* Place cargo for the couriers to pick up */
        Utils.placeCargo(map, PLANK, woodcutter.getFlag(), headquarter);
        Utils.placeCargo(map, STONE, flag0, woodcutter);

        /* Wait for the couriers to carry cargo */
        Utils.fastForwardUntilWorkersCarryCargo(map, courier, courier3);

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
    public void testCourierWalksOnOwnRoadWhenDeliveringFromFirstFlagToSecondBuilding() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Connect the headquarters with the flag */
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

        /* Wait for a second cargo for the woodcutter to get placed on the headquarters' flag */
        Utils.waitForFlagToGetStackedCargo(map, headquarter.getFlag(), 1);

        assertEquals(headquarter.getFlag().getStackedCargo().size(), 1);
        assertEquals(headquarter.getFlag().getStackedCargo().getFirst().getTarget(), woodcutter);
        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 0);

        /* Fill up the woodcutter's flag to make it impossible to deliver cargo to it */
        Utils.placeCargos(map, STONE, 8, woodcutter.getFlag(), woodcutter);

        /* Place a second, longer road between the headquarters and the woodcutter */
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
    public void testCourierWalksOnOwnRoadWhenDeliveringFromSecondFlagToFirstBuilding() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
}
