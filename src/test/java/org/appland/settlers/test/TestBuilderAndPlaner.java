package org.appland.settlers.test;

import org.appland.settlers.model.Builder;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestBuilderAndPlaner {

    /*
    * TODO:
    *   - for builder, test all standard worker things
    *      - walking and road is removed
    *      - building under construction is removed
    *      - sent out
    *      - storage blocked
    *      - walks to die
    *   - test broken promise
    *   - test builder is created with a hammer if needed
    *   - test cannot attack planned building
    *   - test soldiers are not assigned to planned building
    *   - test workers are not assigned to planned building
    *   - building construction time starts when builder reaches the building
    * */

    @Test
    public void testJustPlacedWoodcutterIsPlanned() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Verify that the status of the woodcutter is planned */
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isUnderConstruction());
        assertFalse(woodcutter0.isReady());
        assertFalse(woodcutter0.isOccupied());
        assertFalse(woodcutter0.isBurningDown());
        assertFalse(woodcutter0.isDestroyed());
    }

    @Test
    public void testBuilderIsAssignedToPlannedBuilding() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that a builder is assigned to the planned building */
        assertTrue(headquarter0.getAmount(Material.BUILDER) > 0);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertNotNull(builder0);
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());
    }

    @Test
    public void testPlannedBuildingGetsNeededMaterialDelivered() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that the planned building gets resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());
    }

    @Test
    public void testPlannedBuildingIsNotConstructedWithoutBuilder() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Verify that the building does not get built because there is no builder assigned */
        Utils.fastForward(500, map);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());
    }

    @Test
    public void testPlannedBuildingIsConstructedByBuilder() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Wait for a builder to get assigned and to reach the building */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        Worker builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getPosition());

        /* Verify that the building gets built */
        Utils.fastForward(100, map);

        assertTrue(woodcutter0.isReady());
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertFalse(woodcutter0.isPlanned());
    }

    @Test
    public void testBuilderWalksAroundDuringConstruction() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Wait for a builder to leave the headquarter */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Verify that the builder is not building while walking to the woodcutter */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        for (int i = 0; i < 5000; i++) {

            if (builder0.isExactlyAtPoint() && builder0.getPosition().equals(woodcutter0.getPosition())) {
                break;
            }

            assertFalse(builder0.isHammering());

            map.stepTime();
        }

        assertEquals(builder0.getPosition(), woodcutter0.getPosition());

        /* Verify that the builder walks around the building and sometimes stops and hammers */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition().downLeft());
        assertEquals(builder0.getPosition(), woodcutter0.getPosition());
        assertFalse(builder0.isHammering());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getPosition().downLeft()); // 20 (not taking direct path)

        Utils.verifyBuilderHammersInPlaceForDuration(map, builder0, 20); // 20

        assertEquals(builder0.getTarget(), woodcutter0.getPosition().downLeft().left());
        assertFalse(builder0.isHammering());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getPosition().downLeft().left()); // 10

        Utils.verifyBuilderHammersInPlaceForDuration(map, builder0, 20); // 20

        assertEquals(builder0.getTarget(), woodcutter0.getPosition().upRight());
        assertFalse(builder0.isHammering());
        assertFalse(woodcutter0.isReady());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getPosition().upRight()); // 30

        map.stepTime();

        assertTrue(woodcutter0.isReady());
        assertFalse(builder0.isHammering());
    }

    @Test
    public void testBuilderWalksBackWhenConstructionIsDone() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Wait for a builder to leave the headquarter */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Verify that the builder is not building while walking to the woodcutter */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        for (int i = 0; i < 5000; i++) {

            if (builder0.isExactlyAtPoint() && builder0.getPosition().equals(woodcutter0.getPosition())) {
                break;
            }

            assertFalse(builder0.isHammering());

            map.stepTime();
        }

        assertEquals(builder0.getPosition(), woodcutter0.getPosition());

        /* Wait for the builder to construct the building */
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        assertTrue(woodcutter0.isReady());

        map.stepTime();

        /* Give the builder a chance to complete an ongoing walk first */
        if (!builder0.getTarget().equals(headquarter0.getPosition())) {
            Utils.fastForwardUntilWorkerReachesPoint(map, builder0, builder0.getTarget());
        }

        /* Verify that the builder goes back to the headquarter */
        if (!builder0.getPosition().equals(woodcutter0.getFlag().getPosition())) {
            assertEquals(builder0.getTarget(), woodcutter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getFlag().getPosition());
        }

        assertEquals(builder0.getPosition(), woodcutter0.getFlag().getPosition());
        assertEquals(builder0.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, headquarter0.getPosition());
    }

    @Test
    public void testBuilderIsDepositedInTheHeadquarterWhenHesBack() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Wait for a builder to leave the headquarter */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Verify that the builder is not building while walking to the woodcutter */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        for (int i = 0; i < 5000; i++) {

            if (builder0.isExactlyAtPoint() && builder0.getPosition().equals(woodcutter0.getPosition())) {
                break;
            }

            assertFalse(builder0.isHammering());

            map.stepTime();
        }

        assertEquals(builder0.getPosition(), woodcutter0.getPosition());

        /* Wait for the builder to construct the building */
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        assertTrue(woodcutter0.isReady());

        map.stepTime();

        /* Give the builder a chance to complete an ongoing walk first */
        if (!builder0.getTarget().equals(headquarter0.getPosition())) {
            Utils.fastForwardUntilWorkerReachesPoint(map, builder0, builder0.getTarget());
        }

        /* Verify that the builder goes back to the headquarter */
        if (!builder0.getPosition().equals(woodcutter0.getFlag().getPosition())) {
            assertEquals(builder0.getTarget(), woodcutter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getFlag().getPosition());
        }

        assertEquals(builder0.getPosition(), woodcutter0.getFlag().getPosition());
        assertEquals(builder0.getTarget(), headquarter0.getPosition());

        int amountBuilders = headquarter0.getAmount(Material.BUILDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, headquarter0.getPosition());

        assertEquals(headquarter0.getAmount(Material.BUILDER), amountBuilders + 1);
        assertFalse(map.getWorkers().contains(builder0));
    }
}
