package org.appland.settlers.model.statistics;

import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;

public interface StatisticsListener {
    void buildingStatisticsChanged(Building building);

    void generalStatisticsChanged(Player player);
}
