package org.appland.settlers.assets.wsapi;

import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.rest.resource.IdManager;
import org.appland.settlers.utils.JsonUtils;
import org.json.simple.JSONArray;
import org.junit.Test;

import java.util.Arrays;

import static org.appland.settlers.model.Vegetation.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTerrainToJson {

    @Test
    public void testTerrainToJsonForEvenHeight() {
        int fileWidth = 5;
        int fileHeight = 5;

        var tilesBelow = new Vegetation[] {WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN,
                WATER_2, MOUNTAIN_1, DESERT_1, DESERT_2, BUILDABLE_MOUNTAIN};
        var tilesDownRight = new Vegetation[] {LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                LAVA_1, LAVA_2, LAVA_3, LAVA_4, DESERT_2,
                WATER_2, LAVA_2, LAVA_3, LAVA_4, DESERT_2};

        var heights = new Integer[] {
                1, 2, 3, 4, 5,
                6, 7, 8, 9, 10,
                11, 12, 13, 14, 15,
                16, 17, 18, 19, 20,
                21, 22, 23, 24, 25
        };

        // Create a MapFile instance
        var mapFile = new MapFile();

        mapFile.setMaxNumberOfPlayers(4);
        mapFile.setWidth(fileWidth);
        mapFile.setHeight(fileHeight);
        mapFile.setTilesBelow(Arrays.asList(tilesBelow));
        mapFile.setTilesDownRight(Arrays.asList(tilesDownRight));
        mapFile.setHeights(Arrays.asList(heights));

        // Verify that the terrain is serialized properly to JSON
        var jsonUtils = new JsonUtils(new IdManager());

        var jsonTerrain = jsonUtils.mapFileTerrainToJson(mapFile);

        assertTrue(jsonTerrain.containsKey("heights"));
        assertTrue(jsonTerrain.containsKey("tilesBelow"));
        assertTrue(jsonTerrain.containsKey("tilesDownRight"));
        assertEquals(jsonTerrain.get("width"), fileWidth * 2);
        assertEquals(jsonTerrain.get("height"), fileHeight + 1);

        var jsonTilesBelow = (JSONArray) jsonTerrain.get("tilesBelow");
        var jsonTilesDownRight = (JSONArray) jsonTerrain.get("tilesDownRight");
        var jsonHeights = (JSONArray) jsonTerrain.get("heights");

        assertEquals(jsonTilesBelow.size(), fileWidth * fileHeight);
        assertEquals(jsonTilesDownRight.size(), fileWidth * fileHeight);
        assertEquals(jsonHeights.size(), fileWidth * (fileHeight + 1));

        for (int y = 0; y < mapFile.getHeight(); y++) {
            for (int x = 0; x < mapFile.getWidth(); x++) {
                var fileIndex = y * mapFile.getWidth() + x;

                assertEquals(tilesBelow[fileIndex].toInt(), jsonTilesBelow.get(fileIndex));
                assertEquals(tilesDownRight[fileIndex].toInt(), jsonTilesDownRight.get(fileIndex));
                assertEquals(heights[fileIndex], jsonHeights.get(fileIndex));
            }
        }

        for (int x = 0; x < mapFile.getWidth(); x++) {
            assertEquals(heights[20 + x], jsonHeights.get(x + 20));
        }
    }
}
