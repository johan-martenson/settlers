package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.ProductionDataPoint;
import org.appland.settlers.model.ProductionDataSeries;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.StatisticsManager;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestProductionStatistics {

    @Test
    public void testGetStatisticsInstance() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that we can get a statistics instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        assertNotNull(statisticsManager);
    }

    @Test
    public void testGetProductionStatisticsForAMaterial() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Verify that it's possible to get the production statistics for a material */
        ProductionDataSeries woodProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        assertNotNull(woodProductionDataSeries);

        /* Verify that there is only one measurement point and that it is 0 for all players */
        List<ProductionDataPoint> dataPoints =  woodProductionDataSeries.getProductionDataPoints();

        assertEquals(dataPoints.size(), 1);
        assertEquals(0, dataPoints.get(0).getValues()[0]);
    }

    @Test
    public void testGetTimeForAProductionStatisticsMeasurement() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Get the production statistics for a material */
        ProductionDataSeries woodProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        /* Verify that the time is 0 for the first and only measurement */
        List<ProductionDataPoint> dataPoints =  woodProductionDataSeries.getProductionDataPoints();

        assertEquals(dataPoints.size(), 1);
        assertEquals(0, (int)dataPoints.get(0).getTime());
    }

    @Test
    public void testGetProductionStatisticsHasRightAmountOfValues() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        Player player2 = new Player("Player 2", java.awt.Color.YELLOW);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        players.add(player2);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point01 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point01);

        Point point02 = new Point(5, 35);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point02);

        Point point03 = new Point(35, 5);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point03);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Verify that it's possible to get the production statistics for a material */
        ProductionDataSeries woodProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        assertNotNull(woodProductionDataSeries);

        /* Verify that there are three values in the measurement point, one for each player */
        List<ProductionDataPoint> dataPoints =  woodProductionDataSeries.getProductionDataPoints();

        assertEquals(dataPoints.get(0).getValues().length, 3);
    }

    @Test
    public void testProductionStatisticsDataSeriesGrowsOverTime() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        Player player2 = new Player("Player 2", java.awt.Color.YELLOW);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        players.add(player2);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point01 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point01);

        Point point02 = new Point(5, 35);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point02);

        Point point03 = new Point(35, 5);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point03);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Get the wood production data series at time start */
        ProductionDataSeries woodProductionDataSeriesAtStart = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        assertNotNull(woodProductionDataSeriesAtStart);

        List<ProductionDataPoint> dataPointsAtStart =  woodProductionDataSeriesAtStart.getProductionDataPoints();

        assertEquals(dataPointsAtStart.size(), 1);
        assertEquals(dataPointsAtStart.get(0).getValues().length, 3);

        /* Verify that there are are more values reported over time */
        Utils.fastForward(2000, map);

        ProductionDataSeries woodProductionDataSeriesLater = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        assertNotNull(woodProductionDataSeriesLater);

        List<ProductionDataPoint> dataPointsLater =  woodProductionDataSeriesAtStart.getProductionDataPoints();

        assertTrue(dataPointsLater.size() > 1);
    }

    @Test
    public void testCollectedWoodIsReportedInProductionStatistics() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        Player player2 = new Player("Player 2", java.awt.Color.YELLOW);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        players.add(player2);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point01 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point01);

        Point point02 = new Point(5, 35);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point02);

        Point point03 = new Point(35, 5);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point03);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Get the wood production data series at time start */
        ProductionDataSeries woodProductionDataSeriesAtStart = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        List<ProductionDataPoint> dataPointsAtStart =  woodProductionDataSeriesAtStart.getProductionDataPoints();

        /* Place woodcutter hut */
        Point point04 = new Point(9, 5);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point04);

        /* Connect the woodcutter hut to the headquarter */
        Road road01 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Place a tree close to the woodcutter */
        Point point05 = new Point(12, 4);
        Tree tree01 = map.placeTree(point05);

        /* Verify a collected tree is part of the collected production statistics */
        int nrDataPoints = statisticsManager.getProductionStatisticsForMaterial(WOOD).getProductionDataPoints().size();

        assertEquals(statisticsManager.getProductionStatisticsForMaterial(WOOD).getProductionDataPoints().get(nrDataPoints - 1).getValues()[0], 0);

        /* Wait for the woodcutter hut to get populated */
        WoodcutterWorker woodcutterWorker01 = (WoodcutterWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        /* Wait for the woodcutter to cut down the tree */
        Utils.fastForwardUntilWorkerCarriesCargo(map, woodcutterWorker01);

        /* Wait for the flag to have a cargo placed */
        Cargo woodCargo = Utils.waitForFlagToHaveCargoWaiting(map, woodcutter0.getFlag());

        assertNotNull(woodCargo);

        /* Wait for the wood to get stored in the headquarter */
        Utils.waitForCargoToReachTarget(map, woodCargo);

        /* Verify that the collected wood production statistics contains the wood within 1000 game steps */
        boolean woodStatisticsReported = false;
        for (int i = 0; i < 5000; i++) {

            nrDataPoints = statisticsManager.getProductionStatisticsForMaterial(WOOD).getProductionDataPoints().size();

            if (statisticsManager.getProductionStatisticsForMaterial(WOOD).getProductionDataPoints().get(nrDataPoints - 1).getValues()[0] == 1) {
                woodStatisticsReported = true;

                break;
            }

            map.stepTime();
        }

        assertTrue(woodStatisticsReported);

        nrDataPoints = statisticsManager.getProductionStatisticsForMaterial(WOOD).getProductionDataPoints().size();

        assertEquals(statisticsManager.getProductionStatisticsForMaterial(WOOD).getProductionDataPoints().get(nrDataPoints - 1).getTime(), 1000);
    }

    @Test
    public void testProductionStatisticsIsCollectedPeriodically() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        Player player1 = new Player("Player 1", java.awt.Color.RED);
        Player player2 = new Player("Player 2", java.awt.Color.YELLOW);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        players.add(player2);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point01 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point01);

        Point point02 = new Point(5, 35);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point02);

        Point point03 = new Point(35, 5);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point03);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Get the wood production data series at time start */
        ProductionDataSeries woodProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        assertNotNull(woodProductionDataSeries);

        List<ProductionDataPoint> dataPoints =  woodProductionDataSeries.getProductionDataPoints();

        assertEquals(dataPoints.size(), 1);
        assertEquals(dataPoints.get(0).getValues().length, 3);

        /* Verify that no new values are collected for 499 game steps */
        for (int i = 0; i < 499; i++) {
            woodProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(WOOD);

            assertNotNull(woodProductionDataSeries);

            dataPoints =  woodProductionDataSeries.getProductionDataPoints();

            assertEquals(dataPoints.size(), 1);

            map.stepTime();
        }

        /* Verify that new values are collected at the 1000th step */
        woodProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(WOOD);

        assertNotNull(woodProductionDataSeries);

        dataPoints =  woodProductionDataSeries.getProductionDataPoints();

        assertEquals(dataPoints.size(), 1);
    }

    @Test
    public void testGetInitialProductionStatisticsForRequiredMaterials() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Get the statistics manager instance */
        StatisticsManager statisticsManager = map.getStatisticsManager();

        /* Verify that it's possible to get the production statistics for stone */
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(STONE));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(PLANK));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(WOOD));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(GOLD));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(SWORD));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(SHIELD));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(COIN));

        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(COAL));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(GOLD));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(IRON));

        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(PRIVATE));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(PRIVATE_FIRST_CLASS));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(SERGEANT));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(OFFICER));
        assertNotNull(statisticsManager.getProductionStatisticsForMaterial(GENERAL));
    }
}
