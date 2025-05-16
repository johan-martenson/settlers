package org.appland.settlers.model.statistics;

public record PlayerStatistics(

        // Measures produced goods. Start at 0
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

        // Measures current stock of goods. Start with initial inventory
        CumulativeDataSeries totalAmountBuildings,
        SnapshotDataSeries land,
        CumulativeDataSeries coins,
        CumulativeDataSeries soldiers,
        CumulativeDataSeries workers,
        CumulativeDataSeries killedEnemies,
        CumulativeDataSeries goods
) { }
