package org.appland.settlers.test.pigfarm;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestProductivity {

    @Test
    public void testPigFarmReportsZeroProductivityWhenNoPigBreederIsWorking() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var point = new Point(10, 6);
        var pigFarm = map.placeBuilding(new PigFarm(player0), point);
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // No water or wheat has been supplied, so the pig farm cannot work.
        Utils.fastForward(160, map, () -> assertEquals(0, pigFarm.getProductivity()));

        assertEquals(0, pigFarm.getProductivity());
    }

    @Test
    public void testPigFarmConsidersPigBreederProductionCycleAsWorking() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder =
                (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        // Fill up the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        /*
         * The breeder initially rests before starting its first production
         * cycle. This initial rest is intentionally not considered productive.
         */
        Utils.fastForward(100, map);

        assertEquals(0, pigFarm.getProductivity());

        /*
         * Complete the first productivity measurement. This measurement
         * may still contain some of the initial non-productive rest, so its
         * exact value is not asserted.
         */
        Utils.fastForward(160, map);

        int initialProductivity = pigFarm.getProductivity();

        assertTrue(initialProductivity > 0);
        assertTrue(initialProductivity < 14);

        /*
         * Complete another full productivity measurement while the breeder
         * is performing its normal production cycle.
         *
         * The breeder's walking, feeding, walking back, and pig preparation
         * states all count as productive time.
         */
        Utils.fastForward(160, map);

        /*
         * The rolling average should now have increased because the new
         * measurement represents a fully productive production cycle.
         */
        assertTrue(pigFarm.getProductivity() > initialProductivity);
    }

    @Test
    public void testPigFarmProductivityRampsUpOverSevenMeasurements() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Load wheat and water into the headquarters
        Utils.adjustInventoryTo(headquarter, WHEAT, 30);
        Utils.adjustInventoryTo(headquarter, WATER, 30);

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        // Give the farm enough resources that it can operate continuously.
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        /*
         * The breeder initially rests before starting its first production
         * cycle. This initial rest is not considered productive.
         */
        Utils.fastForward(100, map, () -> {
            assertTrue(pigBreeder.isInsideBuilding());
            assertEquals(0, pigFarm.getProductivity());
        });

        /*
         * Productivity is measured every 16 seconds and averages the
         * current measurement with the previous six measurements.
         *
         * The first measurement can contain part of the breeder's initial
         * non-productive rest, so the exact productivity values during the
         * initial ramp-up depend on where the production cycle falls within
         * the measurement window.
         *
         * Once the breeder is continuously working, productivity must
         * steadily increase as the initial partial measurements are replaced
         * by fully productive measurements.
         */
        int previousProductivity = pigFarm.getProductivity();

        for (int i = 0; i < 7; i++) {
            // Allow one complete productivity measurement to finish.
            Utils.fastForward(160, map);

            int productivity = pigFarm.getProductivity();

            // Productivity must never decrease while the farm has supplies.
            assertTrue(productivity >= previousProductivity);

            // Productivity must remain within the valid range.
            assertTrue(productivity <= 100);

            previousProductivity = productivity;
        }

        /*
         * Allow enough additional measurements for the initial partial
         * measurements to be completely flushed out of the rolling history.
         */
        Utils.fastForward(160 * 10, map);

        // A continuously supplied and continuously working farm eventually reaches 100%.
        assertEquals(100, pigFarm.getProductivity());
    }

    @Test
    public void testPigFarmProductivityOnlyUpdatesEvery16Seconds() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Clear out any water and wheat from the headquarters
        Utils.clearInventory(headquarter, WATER, WHEAT);

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Fill the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        /*
         * Wait until the first non-zero productivity value is reported.
         *
         * The exact point at which this happens depends on where the
         * breeder is in its initial production cycle, so don't assume
         * that the first 160 ticks start at a measurement boundary.
         */
        Utils.fastForwardUntil(map, () -> pigFarm.getProductivity() > 0);

        int firstProductivity = pigFarm.getProductivity();

        assertTrue(firstProductivity > 0);

        /*
         * Once productivity has become non-zero, the displayed value
         * must remain unchanged until the next 16-second measurement
         * completes.
         */
        Utils.fastForward(159, map);

        assertEquals(firstProductivity, pigFarm.getProductivity());

        /*
         * The next tick completes the next 16-second measurement.
         *
         * The breeder has water and wheat available, so it should have
         * a higher productivity measurement than the previous one.
         */
        map.stepTime();

        int secondProductivity = pigFarm.getProductivity();

        assertTrue(secondProductivity > firstProductivity);

        /*
         * Another 159 ticks must leave the displayed productivity
         * unchanged.
         */
        Utils.fastForward(159, map);

        assertEquals(secondProductivity, pigFarm.getProductivity());
    }

    @Test
    public void testPigFarmProductivityFallsToZeroWhenItRunsOutOfResources() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Clear out any water and wheat from the headquarters
        Utils.clearInventory(headquarter, WATER, WHEAT);

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));

        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Give the pig farm enough resources to work continuously.
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        /*
         * Wait until the farm has reported non-zero productivity.
         *
         * The exact timing of the first measurement depends on where
         * the breeder is in its production cycle.
         */
        Utils.fastForwardUntil(
                map,
                () -> pigFarm.getProductivity() > 0
        );

        assertTrue(pigFarm.getProductivity() > 0);

        /*
         * Remove the remaining resources from the pig farm.
         *
         * No more water or wheat can now be delivered, so the breeder
         * will eventually be unable to perform another production cycle.
         */
        Utils.clearInventory(headquarter, WATER, WHEAT);

        /*
         * The productivity is a rolling average over seven measurements,
         * so it should not immediately drop to zero. Give the breeder
         * enough time for the existing productive measurements to leave
         * the rolling window.
         */
        Utils.fastForwardUntil(
                map,
                () -> pigFarm.getProductivity() == 0
        );

        assertEquals(0, pigFarm.getProductivity());
    }
}
