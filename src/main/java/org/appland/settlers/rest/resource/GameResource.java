package org.appland.settlers.rest.resource;

import org.appland.settlers.computer.CompositePlayer;
import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.rest.resource.ResourceLevel.MEDIUM;

public class GameResource {
    public GameStatus status;
    private int height;
    private int width;
    private final List<Player> players;
    private MapFile mapFile;
    private String name;
    private ResourceLevel resourceLevel;
    private GameMap map;
    private final Utils utils;
    private final List<ComputerPlayer> computerPlayers;

    GameResource(Utils utils) {
        players = new ArrayList<>();

        resourceLevel = MEDIUM;

        this.utils = utils;
        computerPlayers = new ArrayList<>();

        status = GameStatus.NOT_STARTED;
    }

    void setPlayers(List<Player> players) {
        this.players.addAll(players);
    }

    List<Player> getPlayers() {
        return players;
    }

    void addHumanPlayer(Player player) {
        players.add(player);
    }

    void setMap(MapFile updatedMapFile) {
        width = updatedMapFile.getWidth();
        height = updatedMapFile.getHeight();

        mapFile = updatedMapFile;
    }

    MapFile getMapFile() {
        return mapFile;
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    boolean isNameSet() {
        return name != null;
    }

    ResourceLevel getResources() {
        return this.resourceLevel;
    }

    void setResource(ResourceLevel resourceLevel) {
        this.resourceLevel = resourceLevel;
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public GameMap getGameMap() {
        return this.map;
    }

    public void createGameMap() throws Exception {
        this.map = utils.gamePlaceholderToGame(this);

        /* Assign the map to each player */
        for (Player player : players) {
            player.setMap(map);
        }

        /* Assign the map to each computer player */
        for (ComputerPlayer computerPlayer : computerPlayers) {
            computerPlayer.setMap(map);
        }
    }

    public List<ComputerPlayer> getComputerPlayers() {
        return this.computerPlayers;
    }

    public void addComputerPlayer(Player player) {
        computerPlayers.add(new CompositePlayer(player, player.getMap()));
        players.add(player);
    }

    public boolean isStarted() {
        return map != null;
    }

    public void setStatus(GameStatus gameStatus) {
        status = gameStatus;
    }
}
