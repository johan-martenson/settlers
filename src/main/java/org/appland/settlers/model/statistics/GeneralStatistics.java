package org.appland.settlers.model.statistics;

public record GeneralStatistics(
        CumulativeDataSeries totalAmountBuildings,
        SnapshotDataSeries land,
        CumulativeDataSeries coins,
        CumulativeDataSeries soldiers,
        CumulativeDataSeries workers,
        CumulativeDataSeries killedEnemies
) { }
