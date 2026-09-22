package org.appland.settlers.computer;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;

public abstract class BasePlayer implements ComputerPlayer {
    protected GameMap map;
    protected Player player;

    public BasePlayer(GameMap map, Player player) {
        this.map = map;
        this.player = player;
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public Player getControlledPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "%s (%s)".formatted(getClass().getSimpleName(), player.getName());
    }
}
