package org.appland.settlers.rest.resource;

import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.WildAnimal;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.rest.GameTicker;

import java.util.ArrayList;
import java.util.List;

public class GameUtils {
    public static void startGame(GameResource gameResource, GameTicker gameTicker) throws Exception {

        /* Create the game map */
        gameResource.createGameMap();
        GameMap map = gameResource.getGameMap();

        /* Limit the amount of wild animals to make performance bearable -- temporary! */
        List<WildAnimal> wildAnimals = map.getWildAnimals();
        List<WildAnimal> reducedWildAnimals = new ArrayList<>(wildAnimals);

        if (reducedWildAnimals.size() > 10) {
            reducedWildAnimals = reducedWildAnimals.subList(0, 10);
        }

        wildAnimals.clear();

        wildAnimals.addAll(reducedWildAnimals);

        /* Place a headquarters for each player */
        List<Player> players = map.getPlayers();
        List<Point> startingPoints = map.getStartingPoints();

        for (int i = 0; i < startingPoints.size(); i++) {
            if (i == players.size()) {
                break;
            }

            map.placeBuilding(new Headquarter(players.get(i)), startingPoints.get(i));
        }

        /* Adjust the initial set of resources */
        adjustResources(map, gameResource.getResources());

        /* Start the time for the game by adding it to the game ticker */
        gameTicker.startGame(gameResource);

        gameResource.setStatus(GameStatus.STARTED);
    }

    public static GameMap gamePlaceholderToGame(GameResource gamePlaceholder) throws Exception {
        MapLoader mapLoader = new MapLoader();
        GameMap map = mapLoader.convertMapFileToGameMap(gamePlaceholder.getMapFile());

        map.setPlayers(gamePlaceholder.getPlayers());

        return map;
    }

    public static void adjustResources(GameMap map, ResourceLevel resources) {
        for (Player player : map.getPlayers()) {
            Headquarter headquarter = (Headquarter)player.getBuildings().getFirst();

            if (resources == ResourceLevel.LOW) {
                headquarter.retrieve(Material.STONE);
                headquarter.retrieve(Material.STONE);
                headquarter.retrieve(Material.STONE);

                headquarter.retrieve(Material.PLANK);
                headquarter.retrieve(Material.PLANK);
                headquarter.retrieve(Material.PLANK);

                headquarter.retrieve(Material.WOOD);
                headquarter.retrieve(Material.WOOD);
                headquarter.retrieve(Material.WOOD);
            } else if (resources == ResourceLevel.HIGH) {
                deliver(Material.STONE, 3, headquarter);
                deliver(Material.PLANK, 3, headquarter);
                deliver(Material.WOOD, 3, headquarter);
            }
        }
    }

    public static void deliver(Material material, int amount, Headquarter headquarter) {
        for (int i = 0; i < amount; i++) {
            headquarter.promiseDelivery(material);
            headquarter.putCargo(new Cargo(material, headquarter.getMap()));
        }
    }
}
