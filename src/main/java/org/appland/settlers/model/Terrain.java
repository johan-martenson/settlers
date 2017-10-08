/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Tile.Vegetation;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.SWAMP;
import static org.appland.settlers.model.Tile.Vegetation.WATER;

/**
 *
 * @author johan
 */
public class Terrain {

    private final Map<Integer, Tile> tileBelowMap;
    private final Map<Integer, Tile> tileDownRightMap;
    private final int width;
    private final int height;

    public Terrain(int width, int height) {
        this.width   = width;
        this.height  = height;

        tileBelowMap = new HashMap<>();
        tileDownRightMap = new HashMap<>();

        constructDefaultTiles();
    }

    public Tile getTile(Point point1, Point point2, Point point3) {

        int left = Math.min(Math.min(point1.x, point3.x), point2.x);
        int top  = Math.max(Math.max(point1.y, point3.y), point2.y);

        Tile tile = null;

        if ((point1.y + point3.y + point2.y) % 3 == 1) { // Tile with pointy end upwards
            tile = tileBelowMap.get(top * width + left + 1);
        } else {
            tile = tileDownRightMap.get(top * width + left);
        }

        return tile;
    }

    public Tile getTileAbove(Point point) {
        return tileDownRightMap.get((point.y + 1) * width + point.x - 1);
    }

    public Tile getTileBelow(Point point) {
        return tileBelowMap.get(point.y * width + point.x);
    }

    public Tile getTileDownRight(Point point) {
        return tileDownRightMap.get(point.y * width + point.x);
    }

    public Tile getTileDownLeft(Point point) {
        return tileDownRightMap.get(point.y * width + point.x - 2);
    }

    public Tile getTileUpRight(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x + 1);
    }

    public Tile getTileUpLeft(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x - 1);
    }

    private void constructDefaultTiles() {
        int x, y;

        for (y = 0; y <= height; y++) {

            int xStart = 0;
            int xEnd   = width;

            if (y % 2 == 1) {
                xStart = -1;
                xEnd   = width + 1;
            }

            for (x = xStart; x <= xEnd + 1; x++) {
                Tile tile = new Tile(GRASS);
                Tile tile2 = new Tile(GRASS);

                tileBelowMap.put(y * width + x, tile);
                tileDownRightMap.put(y * width + x, tile2);
            }
        }
    }

    public boolean isOnMountain(Point point) {
        return isSurroundedBy(point, MOUNTAIN);
    }

    public boolean isInWater(Point point) {
        return isSurroundedBy(point, WATER);
    }

    public boolean isInSwamp(Point point) {
        return isSurroundedBy(point, SWAMP);
    }

    public boolean isOnGrass(Point point) {
        return isSurroundedBy(point, GRASS);
    }

    private boolean isAnyAdjacentTile(Point point, Vegetation vegetation) {
        List<Tile> tiles = getSurroundingTiles(point);

        for (Tile tile : tiles) {
            if (tile.getVegetationType() == vegetation) {
                return true;
            }
        }

        return false;        
    }

    private boolean isSurroundedBy(Point point, Vegetation vegetation) {
        List<Tile> tiles = getSurroundingTiles(point);

        for (Tile tile : tiles) {
            if (tile.getVegetationType() != vegetation) {
                return false;
            }
        }

        return true;
    }

    public List<Tile> getSurroundingTiles(Point center) {
        List<Tile> result   = new LinkedList<>();

        /* This method is called frequently. Treat the tiles one-by-one
           to avoid creating a temporary list */

        /* Tile down right */
        Tile tile = getTileDownRight(center);

        if (tile != null) {
            result.add(tile);
        }

        /* Tile below */
        tile = getTileBelow(center);

        if (tile != null) {
            result.add(tile);
        }

        /* Tile down left */
        tile = getTileDownLeft(center);

        if (tile != null) {
            result.add(tile);
        }

        /* Tile up left */
        tile = getTileUpLeft(center);

        if (tile != null) {
            result.add(tile);
        }

        /* Tile above */
        tile = getTileAbove(center);

        if (tile != null) {
            result.add(tile);
        }

        /* Tile up right */
        tile = getTileUpRight(center);

        if (tile != null) {
            result.add(tile);
        }

        return result;
    }

    public boolean terrainMakesFlagPossible(Point point) throws Exception {
        if (isInWater(point)) {
            return false;
        }

        if (isInSwamp(point)) {
            return false;
        }

        return true;
    }

    boolean isNextToWater(Point point) {
        return isAnyAdjacentTile(point, WATER);
    }

    boolean isOnEdgeOf(Point point, Vegetation vegetation) {

        boolean matchFound = false;
        boolean nonMatchFound = false;

        /* Go through the surrounding tiles and verify that they contain at least 
           on matching and one non-matching */
        for (Tile tile : getSurroundingTiles(point)) {

            if (tile.getVegetationType().equals(vegetation)) {
                matchFound = true;
            } else if (!tile.getVegetationType().equals(vegetation)) {
                nonMatchFound = true;
            }
        }

        return matchFound && nonMatchFound;
    }

    protected void placeMountainOnTile(Point point1, Point point2, Point point3) {
        Tile tile = getTile(point1, point2, point3);

        tile.setVegetationType(MOUNTAIN);
    }

    public boolean isInDesert(Point point) {
        return isSurroundedBy(point, Vegetation.DESERT);
    }

    public boolean isNextToDesert(Point point) {
        return isAnyAdjacentTile(point, Vegetation.DESERT);
    }

    public boolean isOnSnow(Point point) {
        return isSurroundedBy(point, Vegetation.SNOW);
    }

    public boolean isNextToSnow(Point point) {
        return isAnyAdjacentTile(point, Vegetation.SNOW);
    }

    public boolean isNextToLava(Point site) {
        return isAnyAdjacentTile(site, Vegetation.LAVA);
    }

    public boolean isOnLava(Point point) {
        return isSurroundedBy(point, Vegetation.LAVA);
    }

    public boolean isInDeepWater(Point site) {
        return isSurroundedBy(site, Vegetation.DEEP_WATER);
    }

    public boolean isNextToDeepWater(Point site) {
        return isAnyAdjacentTile(site, Vegetation.DEEP_WATER);
    }

    public boolean isNextToSwamp(Point site) {
        return isAnyAdjacentTile(site, Vegetation.SWAMP);
    }

    public boolean isNextToMagenta(Point site) {
        return isAnyAdjacentTile(site, Vegetation.MAGENTA);
    }
}
