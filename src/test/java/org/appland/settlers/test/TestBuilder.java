package org.appland.settlers.test;

import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Well;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.actors.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.appland.settlers.model.Material.BUILDER;
import static org.appland.settlers.model.Material.HAMMER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestBuilder {

    /*
    * TODO:
    *   - for builder, test all standard worker things
    *      - walking and road is removed
    *      - tear down building that builder is working on, while builder is walking from one point to the other to continue hammering
    *   - test cannot attack planned building
    *   - test soldiers are not assigned to planned building
    *   - test workers are not assigned to planned building
    *   - building construction time starts when builder reaches the building
    *   - test builder that constructs house 1 and then house 2 does not cancel construction if house 1 is torn down
    *   - HAMMERING_HOUSE_HIGH_AND_LOW, INSPECTING_HOUSE_CONSTRUCTION
    * */

    @Test
    public void testJustPlacedWoodcutterIsPlanned() throws InvalidUserActionException {

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
    public void testBuilderIsAssignedToPlannedBuilding() throws InvalidUserActionException {

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
    public void testBreakingPromisedBuilder() throws InvalidUserActionException {

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

        /* Wait for a builder to be assigned to the planned building */
        Utils.adjustInventoryTo(headquarter0, BUILDER, 2);

        assertEquals(headquarter0.getAmount(Material.BUILDER), 2);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertNotNull(builder0);
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        /* Remove the road so the builder can't go to the building, and gives up and goes back to the headquarter */
        map.removeRoad(road0);

        /* Wait for the builder to go the headquarter's flag and start going back again */
        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, headquarter0.getFlag().getPosition());

        assertEquals(builder0.getTarget(), headquarter0.getPosition());

        /* Verify that the second build starts going out when the road is put back */
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        List<Builder> builders = Utils.waitForWorkersOutsideBuilding(Builder.class, 2, player0);

        assertEquals(builders.size(), 2);

        builders.remove(builder0);

        Builder builder1 = builders.get(0);

        assertEquals(builder1.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder1, woodcutter0.getPosition());

        assertEquals(builder1, woodcutter0.getBuilder());
    }

    @Test
    public void testBuilderIsCreated() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust contents of the headquarter */
        Utils.adjustInventoryTo(headquarter0, BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, HAMMER, 1);

        assertEquals(headquarter0.getAmount(BUILDER), 0);
        assertEquals(headquarter0.getAmount(HAMMER), 1);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that a builder is assigned to the planned building */
        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertNotNull(builder0);
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());
        assertEquals(headquarter0.getAmount(HAMMER), 0);
    }

    @Test
    public void testPlannedBuildingGetsNeededMaterialDelivered() throws InvalidUserActionException {

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
    public void testPlannedBuildingIsNotConstructedWithoutBuilder() throws InvalidUserActionException {

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
    public void testPlannedBuildingIsConstructedByBuilder() throws InvalidUserActionException {

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
    public void testBuilderWalksAroundDuringConstruction() throws InvalidUserActionException {

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
    public void testBuilderWalksBackWhenConstructionIsDone() throws InvalidUserActionException {

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
    public void testBuilderIsDepositedInTheHeadquarterWhenHesBack() throws InvalidUserActionException {

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

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndWellIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Wait for the builder of the storehouse */
        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Wait for the storehouse to get constructed */
        Utils.waitForBuildingToBeConstructed(storehouse);

        /* Wait for the builder of the storehouse to go back to the headquarter */
        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(builder0));

        /* Place well */
        Point point2 = new Point(18, 6);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Place road to connect the headquarter with the well */
        Road road1 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for a builder to reach the well */
        Builder builder1 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertNotNull(builder1);
        assertEquals(builder1.getTargetBuilding(), well0);
        assertEquals(builder1.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder1, well0.getPosition());

        assertTrue(well0.isUnderConstruction());

        /* Verify that the worker goes to the storage when the well is torn down */
        headquarter0.blockDeliveryOfMaterial(BUILDER);

        well0.tearDown();

        map.stepTime();

        Utils.fastForwardUntilWorkerReachesPoint(map, builder1, well0.getFlag().getPosition());

        assertEquals(builder1.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, builder1, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(builder1));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndWellIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Wait for the builder of the storehouse */
        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Wait for the storehouse to get constructed */
        Utils.waitForBuildingToBeConstructed(storehouse);

        /* Wait for the builder of the storehouse to go back to the headquarter */
        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(builder0));

        /* Place well */
        Point point2 = new Point(18, 6);
        Well well0 = map.placeBuilding(new Well(player0), point2);

        /* Place road to connect the headquarter with the well */
        Road road1 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for a builder to reach the well */
        Builder builder1 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertNotNull(builder1);
        assertEquals(builder1.getTargetBuilding(), well0);
        assertEquals(builder1.getTarget(), well0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder1, well0.getPosition());

        assertTrue(well0.isUnderConstruction());

        /* Verify that the worker goes to the storage when the well is torn down */
        headquarter0.blockDeliveryOfMaterial(BUILDER);

        well0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        Utils.fastForwardUntilWorkerReachesPoint(map, builder1, well0.getFlag().getPosition());

        assertEquals(builder1.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder1, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(builder1));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, BUILDER, 1);

        assertEquals(headquarter0.getAmount(BUILDER), 1);

        headquarter0.pushOutAll(BUILDER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

            assertEquals(headquarter0.getAmount(BUILDER), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, BUILDER, 1);

        headquarter0.blockDeliveryOfMaterial(BUILDER);
        headquarter0.pushOutAll(BUILDER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(7, 9);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the builder to reach the well and start construction */
        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertFalse(well0.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, well0.getPosition());

        assertTrue(well0.isUnderConstruction());

        /* Verify that worker walks away and dies when the building is torn down because delivery is blocked in the headquarter */
        headquarter0.blockDeliveryOfMaterial(BUILDER);

        well0.tearDown();

        assertNotNull(builder0.getTarget());
        assertNotEquals(builder0.getTarget(), well0.getPosition());
        assertNotEquals(builder0.getTarget(), headquarter0.getPosition());
        assertFalse(builder0.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, builder0.getTarget());

        assertTrue(builder0.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(builder0.isDead());
            assertTrue(map.getWorkers().contains(builder0));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(builder0));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(7, 9);
        Well well0 = map.placeBuilding(new Well(player0), point1);

        /* Place road to connect the well with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, well0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the builder to start walking towards the well */
        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertTrue(well0.isPlanned());
        assertFalse(well0.isUnderConstruction());

        map.stepTime();

        assertFalse(builder0.isExactlyAtPoint());
        assertEquals(builder0.getTarget(), well0.getPosition());

        /* Wait for the well worker to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the builder goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(builder0.getTarget(), well0.getPosition());

        headquarter0.blockDeliveryOfMaterial(BUILDER);

        well0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, well0.getFlag().getPosition());

        assertEquals(builder0.getPosition(), well0.getFlag().getPosition());
        assertNotEquals(builder0.getTarget(), headquarter0.getPosition());
        assertFalse(builder0.isInsideBuilding());
        assertNull(well0.getWorker());
        assertNotNull(builder0.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, builder0.getTarget());

        Point point = builder0.getPosition();

        for (int i = 0; i < 100; i++) {
            assertTrue(builder0.isDead());
            assertEquals(builder0.getPosition(), point);
            assertTrue(map.getWorkers().contains(builder0));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(builder0));
    }
}
