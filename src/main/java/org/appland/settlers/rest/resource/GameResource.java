package org.appland.settlers.rest.resource;

import org.appland.settlers.computer.CompositePlayer;
import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerChangeListener;
import org.appland.settlers.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.rest.resource.GameUtils.gamePlaceholderToGame;
import static org.appland.settlers.rest.resource.ResourceLevel.MEDIUM;

public class GameResource implements PlayerChangeListener {
    public GameStatus status;

    private final List<Player> players;
    private final Map<Player, ComputerPlayer> computerPlayers;
    private final Collection<GameResourceListener> listeners = new HashSet<>();

    private MapFile mapFile;
    private String name;
    private ResourceLevel resourceLevel;
    private GameMap map;
    private boolean othersCanJoin;
    private GameSpeed gameSpeed;

    @Override
    public void onPlayerChanged() {
        listeners.forEach(listener -> listener.onGameResourceChanged(this));
    }

    interface GameResourceListener {
        void onGameResourceChanged(GameResource gameResource);
    }

    GameResource(JsonUtils utils) {
        players = new ArrayList<>();

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

        players.forEach(player -> player.addPlayerChangeListener(this));

        notifyListeners();
    }

    public List<Player> getPlayers() {
        return players;
    }

    void addHumanPlayer(Player player) {
        players.add(player);

        player.addPlayerChangeListener(this);

        notifyListeners();
    }

    void setMap(MapFile updatedMapFile) {
        mapFile = updatedMapFile;

        notifyListeners();
    }

    public MapFile getMapFile() {
        return mapFile;
    }

    void setName(String name) {
        this.name = name;

        notifyListeners();
    }

    public String getName() {
        return name;
    }

    boolean isNameSet() {
        return name != null;
    }

    public ResourceLevel getResources() {
        return this.resourceLevel;
    }

    void setResource(ResourceLevel resourceLevel) {
        this.resourceLevel = resourceLevel;

        notifyListeners();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);

        player.removePlayerChangeListener(this);

        notifyListeners();
    }

    public GameMap getGameMap() {
        return this.map;
    }

    public void createGameMap() throws Exception {
        this.map = gamePlaceholderToGame(this);

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

        player.addPlayerChangeListener(this);

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
