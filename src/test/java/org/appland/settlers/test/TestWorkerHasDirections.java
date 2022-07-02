package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestWorkerHasDirections {

    /**
     * TODO:
     *    - test fisherman around single triangles, upwards- and downwards-facing
     */

    @Test
    public void testCourierLeavingHeadquartersHasDirectionDownRight() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.DOWN_RIGHT);
    }

    @Test
    public void testCourierWalkingRightHasDirectionRight() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /*  Wait for the courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        /* Let the courier take a first step on the next road */
        map.stepTime();

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.RIGHT);
    }

    @Test
    public void testCourierWalkingUpRightHasDirectionUpRight() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 6);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /*  Wait for the courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        /* Let the courier take a first step on the next road */
        map.stepTime();

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.UP_RIGHT);
    }

    @Test
    public void testCourierWalkingDownRightHasDirectionDownRight() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(8, 2);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /*  Wait for the courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        /* Let the courier take a first step on the next road */
        map.stepTime();

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.DOWN_RIGHT);
    }

    @Test
    public void testCourierWalkingLeftHasDirectionLeft() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(4, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /*  Wait for the courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        /* Let the courier take a first step on the next road */
        map.stepTime();

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.LEFT);
    }

    @Test
    public void testCourierWalkingDownLeftHasDirectionDownLeft() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(6, 2);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /*  Wait for the courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        /* Let the courier take a first step on the next road */
        map.stepTime();

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.DOWN_LEFT);
    }

    @Test
    public void testCourierWalkingUpLeftHasDirectionUpLeft() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(7, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(12, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road to get its courier assigned */
        Courier courier0 = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Place flag */
        Point point2 = new Point(10, 6);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place road */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Wait for the second road's courier to come out from the headquarters */
        Courier courier1 = null;

        for (int i = 0; i < 200; i++) {
            int nrCouriers = 0;

            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Courier) {
                    nrCouriers = nrCouriers + 1;

                    if (worker != courier0) {
                        courier1 = (Courier) worker;
                    }
                }
            }

            if (nrCouriers == 2) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(courier1);

        /*  Wait for the second courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, headquarter0.getFlag().getPosition());

        /* Wait for the courier to reach the second flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier1, flag0.getPosition());

        /* Let the courier take a first step on the next road */
        map.stepTime();

        /* Verify that the courier has the right direction set */
        assertEquals(courier1.getDirection(), Direction.UP_LEFT);
    }

    // TODO: courier walking for new assignment gets proper direction

    @Test
    public void testCourierWalkingForNextPickupHasCorrectDirection() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road's courier to come out from the headquarters */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /*  Wait for the courier to reach the headquarters' flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        /* Let the courier get to the the middle of its road */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point1.left());

        /* Place a house at the second flag so the courier needs to go and pick up cargos */
        Point point2 = new Point(9, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        Utils.waitForWorkerToSetTarget(map, courier, headquarter0.getFlag().getPosition());

        /* Verify that the courier has the right direction set */
        assertEquals(courier.getDirection(), Direction.LEFT);
    }

    @Test
    public void testForesterReturnsHomeAfterPlantingTreeWithDirectionDownLeft() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Put stones on the map and leave only one point where the forester can plant a tree */
        Point point2 = new Point(14, 6);

        for (Point point : Utils.getAllPointsOnMap(map)) {
            if (point.equals(point2)) {
                continue;
            }

            if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point)) {
                continue;
            }

            map.placeStone(point);
        }

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to plant the tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        /* Verify that the forester goes back home */
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertEquals(forester.getTarget(), foresterHut.getPosition());
        assertEquals(forester.getPosition(), point2);
        assertTrue(forester.isTraveling());
        assertEquals(forester.getDirection(), Direction.DOWN_LEFT);
    }

    @Test
    public void testCourierWithNewlyPickedUpCargoHasCorrectDirection() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point3 = new Point(20, 8);
        Flag flag0 = map.placeFlag(player0, point3);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), flag0);

        /* Wait for the road to get an assigned courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier to stand in the middle of the road */
        Utils.waitForWorkerToGoToPoint(map, courier, point3.left());

        /* Place forester hut */
        Point point2 = new Point(19, 9);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point2);

        /* Wait for the courier to pick up a cargo for the forester hut */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        /* Verify that the worker has the right direction set */
        assertEquals(courier.getPosition(), headquarter.getFlag().getPosition());
        assertEquals(courier.getDirection(), Direction.RIGHT);
    }

    @Test
    public void testFishermanFishingHasCorrectDirection() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a lake */
        Point point0 = new Point(4, 4);
        Utils.surroundPointWithVegetation(point0, WATER, map);

        /* Place headquarter */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Wait for the fisherman to fish at all points around the lake */
        Map<Point, Direction> fishingDirection = new HashMap<>();

        for (int i = 0; i < 20000; i++) {

            if (fishingDirection.size() == 6) {
                break;
            }

            /* Wait for the fisherman to leave the house */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            /* Wait for the fisherman to get to the fishing spot */
            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fisherman.getTarget());

            /* Store the direction the fisherman has while he's fishing */
            map.stepTime();

            assertTrue(fisherman.isFishing());

            if (!fishingDirection.containsKey(fisherman.getPosition())) {
                fishingDirection.put(fisherman.getPosition(), fisherman.getDirection());
            } else {
                assertEquals(fisherman.getDirection(), fishingDirection.get(fisherman.getPosition()));
            }

            /* Wait for the fisherman to finish fishing */
            Utils.waitForFishermanToStopFishing(fisherman, map);

            /* Wait for the fisherman to go back to the fishery */
            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());

            /* Wait for the fisherman to leave the fish by the flag and go back to the house */
            Utils.waitForWorkerToBeOutside(fisherman, map);

            assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fisherman.getTarget());

            assertEquals(fisherman.getTarget(), fishery.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getPosition());
        }

        assertEquals(fishingDirection.size(), 6);
        assertEquals(fishingDirection.get(point0.right()), Direction.LEFT);
        assertEquals(fishingDirection.get(point0.downRight()), Direction.UP_LEFT);
        assertEquals(fishingDirection.get(point0.downLeft()), Direction.UP_RIGHT);
        assertEquals(fishingDirection.get(point0.left()), Direction.RIGHT);
        assertEquals(fishingDirection.get(point0.upLeft()), Direction.DOWN_RIGHT);
        assertEquals(fishingDirection.get(point0.upRight()), Direction.DOWN_LEFT);
    }

    @Test
    public void testFishermanFishingOnHorizontalShoreWithWaterDownHasCorrectDirection() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place a long horizontal shore */
        for (int i = 0; i < 40; i++) {

            if ((i + 3) % 2 != 0) {
                continue;
            }

            try {
                Point point0 = new Point(i, 3);
                Utils.surroundPointWithVegetation(point0, WATER, map);
            } catch (Exception e) { }
        }

        /* Place headquarter */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Wait for the fisherman to fish by the lake */

        /* Wait for the fisherman to leave the house */
        Utils.waitForWorkerToBeOutside(fisherman, map);

        /* Wait for the fisherman to get to the fishing spot */
        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fisherman.getTarget());

        /* Verify the direction the fisherman has while he's fishing */
        map.stepTime();

        assertTrue(fisherman.isFishing());
        assertEquals(fisherman.getDirection(), Direction.DOWN_RIGHT);
    }
}
