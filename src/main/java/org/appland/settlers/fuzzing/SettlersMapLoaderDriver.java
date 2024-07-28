package org.appland.settlers.fuzzing;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.maps.InvalidMapException;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.maps.SettlersMapLoadingException;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;

import java.util.ArrayList;
import java.util.List;

public class SettlersMapLoaderDriver {

    public static void main(String[] args) throws Exception {

        try {
            MapLoader mapLoader = new MapLoader();

            MapFile mapFile = mapLoader.loadMapFromFile(args[0]);

            GameMap map = mapLoader.convertMapFileToGameMap(mapFile);

            List<Player> players = new ArrayList<>();

            players.add(new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN));

            map.stepTime();
        } catch (SettlersMapLoadingException | InvalidMapException e) {
            e.printStackTrace();
        }
    }
}
