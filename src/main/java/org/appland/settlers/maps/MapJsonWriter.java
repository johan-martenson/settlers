package org.appland.settlers.maps;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class MapJsonWriter {

    @Option(name = "--file", usage = "Map file to read")
    public static String mapFile;

    @Option(name = "--to-json-file", usage = "Json file to write to")
    public static String toJsonFile;
    private final MapLoader mapLoader;

    MapFile loadedMapFile;

    public static void main(String[] args) throws Exception {
        var mapJsonWriter = new MapJsonWriter();

        CmdLineParser parser = new CmdLineParser(mapJsonWriter);

        parser.parseArgument(args);

        mapJsonWriter.loadMapFromFile(mapFile);

        mapJsonWriter.writeToJsonFile(toJsonFile);
    }

    private MapJsonWriter() {
        mapLoader = new MapLoader();
    }

    private void writeToJsonFile(String toJsonFile) throws Exception {
        var gamemap = mapLoader.convertMapFileToGameMap(loadedMapFile);

        var jsonMap = new JSONObject(Map.of(
           "title", loadedMapFile.getTitle(),
           "dimension", new JSONObject(Map.of(
                   "width", gamemap.getWidth(),
                        "height", gamemap.getHeight()
                )),
            "points", pointsToJson(gamemap)
        ));

        Files.writeString(Paths.get(toJsonFile), jsonMap.toJSONString());
    }

    private JSONArray pointsToJson(GameMap map) {
        var jsonPoints = new JSONArray();

        for (var y = 0; y < map.getHeight(); y++) {
            for (var x = (y % 2) == 0 ? 0 : 1; x < map.getWidth(); x += 2) {
                var point = new Point(x, y);

                var jsonPoint = new JSONObject(Map.of(
                        "x", x,
                        "y", y,
                        "height", map.getHeightAtPoint(point),
                        "isStone", map.isStoneAtPoint(point),
                        "isTree", map.isTreeAtPoint(point),
                        "below", map.getVegetationBelow(point).toInt(),
                        "downRight", map.getVegetationDownRight(point).toInt()
                ));

                if (map.isDecoratedAtPoint(point)) {
                    jsonPoint.put("decoration", map.getDecorationAtPoint(point));
                } else if (map.isDeadTree(point)) {
                    jsonPoint.put("decoration", "DEAD_TREE");
                }

                jsonPoints.add(jsonPoint);
            }
        }

        return jsonPoints;
    }

    private void loadMapFromFile(String mapFile) throws IOException, InvalidMapException, SettlersMapLoadingException {
        loadedMapFile = mapLoader.loadMapFromFile(mapFile);
    }
}
