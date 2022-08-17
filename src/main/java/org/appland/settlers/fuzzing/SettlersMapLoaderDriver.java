package org.appland.settlers.fuzzing;

import org.appland.settlers.maps.InvalidMapException;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.maps.SettlersMapLoadingException;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SettlersMapLoaderDriver {

    public static void main(String[] args) throws Exception {

        try {
            MapLoader mapLoader = new MapLoader();

            MapFile mapFile = mapLoader.loadMapFromFile(args[0]);

            GameMap map = mapLoader.convertMapFileToGameMap(mapFile);

            List<Player> players = new ArrayList<>();

            players.add(new Player("Player 0", Color.BLUE));

            map.stepTime();
        } catch (SettlersMapLoadingException e) {
            e.printStackTrace();
        } catch (InvalidMapException e) {
            e.printStackTrace();
        }
    }
}
