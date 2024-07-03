package org.appland.settlers.rest.resource;

import org.appland.settlers.computer.CompositePlayer;
import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;

import java.util.*;

import static org.appland.settlers.rest.resource.ResourceLevel.MEDIUM;

public class GameResource {
    public GameStatus status;

    private final List<Player> players;
    private final Utils utils;
    private final Map<Player, ComputerPlayer> computerPlayers;
    private final Collection<GameResourceListener> listeners = new HashSet<>();

    private MapFile mapFile;
    private String name;
    private ResourceLevel resourceLevel;
    private GameMap map;
    private boolean othersCanJoin;
    private GameSpeed gameSpeed;

    interface GameResourceListener {
        void onGameResourceChanged(GameResource gameResource);
    }

    GameResource(Utils utils) {
        players = new ArrayList<>();

        this.utils = utils;

        computerPlayers = new HashMap<>();

        resourceLevel = MEDIUM;
        status = GameStatus.NOT_STARTED;
        othersCanJoin = true;
        gameSpeed = GameSpeed.NORMAL;
    }

    void addChangeListener(GameResourceListener listener) {
        listeners.add(listener);
    }

    void removeChangeListener(GameResourceListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        listeners.forEach(listener -> listener.onGameResourceChanged(this));
    }

    void setPlayers(List<Player> players) {
        this.players.addAll(players);

        notifyListeners();
    }

    List<Player> getPlayers() {
        return players;
    }

    void addHumanPlayer(Player player) {
        players.add(player);

        notifyListeners();
    }

    void setMap(MapFile updatedMapFile) {
        mapFile = updatedMapFile;

        notifyListeners();
    }

    MapFile getMapFile() {
        return mapFile;
    }

    void setName(String name) {
        this.name = name;

        notifyListeners();
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

        notifyListeners();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);

        notifyListeners();
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
        for (ComputerPlayer computerPlayer : computerPlayers.values()) {
            computerPlayer.setMap(map);
        }
    }

    public Collection<ComputerPlayer> getComputerPlayers() {
        return this.computerPlayers.values();
    }

    public void addComputerPlayer(Player player) {
        computerPlayers.put(player, new CompositePlayer(player, player.getMap()));
        players.add(player);

        notifyListeners();
    }

    public boolean isStarted() {
        return map != null;
    }

    public void setStatus(GameStatus gameStatus) {
        status = gameStatus;

        notifyListeners();
    }

    public boolean isComputerPlayer(Player player) {
        return computerPlayers.containsKey(player);
    }

    public void setOthersCanJoin(boolean othersCanJoin) {
        this.othersCanJoin = othersCanJoin;

        notifyListeners();
    }

    public boolean getOthersCanJoin() {
        return othersCanJoin;
    }

    public boolean isPaused() {
        return status == GameStatus.PAUSED;
    }

    public void setGameSpeed(GameSpeed gameSpeed) {
        this.gameSpeed = gameSpeed;

        notifyListeners();
    }

    public GameSpeed getGameSpeed() {
        return gameSpeed;
    }
}
