package org.appland.settlers.model.statistics;

public record PlayerStatistics(
        CumulativeDataSeries wood,
        CumulativeDataSeries plank,
        CumulativeDataSeries stone,
        CumulativeDataSeries food,
        CumulativeDataSeries water,
        CumulativeDataSeries beer,
        CumulativeDataSeries coal,
        CumulativeDataSeries iron,
        CumulativeDataSeries gold,
        CumulativeDataSeries ironBar,
        CumulativeDataSeries coin,
        CumulativeDataSeries tools,
        CumulativeDataSeries weapons,
        CumulativeDataSeries boats,
        CumulativeDataSeries totalAmountBuildings,
        SnapshotDataSeries land,
        CumulativeDataSeries coins,
        CumulativeDataSeries soldiers,
        CumulativeDataSeries workers,
        CumulativeDataSeries killedEnemies,
        CumulativeDataSeries goods
) { }
