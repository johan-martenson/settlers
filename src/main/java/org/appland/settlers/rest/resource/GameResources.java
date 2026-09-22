package org.appland.settlers.rest.resource;

import org.appland.settlers.model.GameMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class GameResources {
    private final Collection<GameResource> games = new ArrayList<>();
    private final Collection<GameListListener> listeners = new ArrayList<>();

    static final GameResources GAME_RESOURCES = new GameResources();

    GameResources() { }

    void addGame(GameResource gameResource) {
        games.add(gameResource);

        listeners.forEach(listener -> listener.onGameListChanged(games));
    }

    void removeGame(GameResource gameResource) {
        games.remove(gameResource);

        listeners.forEach(listener -> listener.onGameListChanged(games));
        listeners.forEach(listener -> listener.onGameRemoved(gameResource));
    }

    Collection<GameResource> getGames() {
        return games;
    }

    void addAddedAndRemovedGamesListener(GameListListener listener) {
        listeners.add(listener);
    }

    void removeAddedAndRemovedGamesListener(GameListListener listener) {
        listeners.remove(listener);
    }

    interface GameListListener {
        void onGameListChanged(Collection<GameResource> games);
        void onGameRemoved(GameResource game);
    }

    GameResource getGameResource(GameMap map) {
        return games.stream()
                .filter(game -> Objects.equals(game.getGameMap(), (map)))
                .findFirst()
                .orElse(null);
    }
}
