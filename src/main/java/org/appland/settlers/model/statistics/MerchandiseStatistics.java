package org.appland.settlers.model.statistics;

public record MerchandiseStatistics(
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
        CumulativeDataSeries boats
) { }
