package org.appland.settlers.fuzzing;

import org.appland.settlers.maps.InvalidMapException;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.maps.SettlersMapLoadingException;

public class SettlersMapLoaderDriver {

    public static void main(String[] args) throws Exception {

        try {
            var mapLoader = new MapLoader();
            var mapFile = mapLoader.loadMapFromFile(args[0]);
            var map = mapLoader.convertMapFileToGameMap(mapFile);

            map.stepTime();
        } catch (SettlersMapLoadingException | InvalidMapException e) {
            e.printStackTrace();
        }
    }
}
