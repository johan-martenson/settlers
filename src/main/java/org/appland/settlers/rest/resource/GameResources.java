package org.appland.settlers.rest.resource;

import java.util.ArrayList;
import java.util.Collection;

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
    }
}
