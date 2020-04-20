package org.appland.settlers.model;

public interface PlayerGameViewMonitor {
    void onViewChangesForPlayer(Player player, GameChangesList gameChangesList);
}
