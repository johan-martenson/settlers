/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import org.appland.settlers.model.Tile.Vegetation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Size.LARGE;
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

    Terrain(int width, int height) {
        this.width   = width;
        this.height  = height;

        tileBelowMap = new HashMap<>();
        tileDownRightMap = new HashMap<>();

        constructDefaultTiles();
    }

    /**
     * Returns the tile above the given point
     *
     * @param point
     * @return
     */
    public Tile getTileAbove(Point point) {
        return tileDownRightMap.get((point.y + 1) * width + point.x - 1);
    }

    /**
     * Returns the tile below the given point
     *
     * @param point
     * @return
     */
    public Tile getTileBelow(Point point) {
        return tileBelowMap.get(point.y * width + point.x);
    }

    /**
     * Returns the tile down to the right of the given point
     *
     * @param point
     * @return
     */
    public Tile getTileDownRight(Point point) {
        return tileDownRightMap.get(point.y * width + point.x);
    }

    /**
     * Returns the tile dow to the left of the given point
     *
     * @param point
     * @return
     */
    public Tile getTileDownLeft(Point point) {
        return tileDownRightMap.get(point.y * width + point.x - 2);
    }

    /**
     * Returns the tile up to the right of the given point
     *
     * @param point
     * @return
     */
    public Tile getTileUpRight(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x + 1);
    }

    /**
     * Returns the tile up to the left of the given point
     *
     * @param point
     * @return
     */
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

    /**
     * Returns true if the given point is surrounded by mountain tiles
     *
     * @param point
     * @return
     */
    public boolean isOnMountain(Point point) {
        return isSurroundedBy(point, MOUNTAIN);
    }

    /**
     * Returns true if the given point is surrounded by water tiles
     *
     * @param point
     * @return
     */
    public boolean isInWater(Point point) {
        return isSurroundedBy(point, WATER);
    }

    /**
     * Returns true if the given point is surrounded by swamp tiles
     *
     * @param point
     * @return
     */
    public boolean isInSwamp(Point point) {
        return isSurroundedBy(point, SWAMP);
    }

    /**
     * Returns true if the given point is surrounded by grass tiles
     *
     * @param point
     * @return
     */
    public boolean isOnGrass(Point point) {
        return isSurroundedBy(point, GRASS);
    }

    private boolean isAnyAdjacentTile(Point point, Vegetation vegetation) {

        return getTileUpLeft(point).getVegetationType()    == vegetation ||
               getTileAbove(point).getVegetationType()     == vegetation ||
               getTileUpRight(point).getVegetationType()   == vegetation ||
               getTileDownRight(point).getVegetationType() == vegetation ||
               getTileBelow(point).getVegetationType()     == vegetation ||
               getTileDownLeft(point).getVegetationType()  == vegetation;
    }

    /**
     * Surrounds the given point with the chosen type of vegetation
     *
     * @param point
     * @param vegetation
     */
    public void surroundWithVegetation(Point point, Tile.Vegetation vegetation) {
        getTileUpLeft(point).setVegetationType(vegetation);
        getTileAbove(point).setVegetationType(vegetation);
        getTileUpRight(point).setVegetationType(vegetation);
        getTileDownRight(point).setVegetationType(vegetation);
        getTileBelow(point).setVegetationType(vegetation);
        getTileDownLeft(point).setVegetationType(vegetation);
    }

    boolean isSurroundedBy(Point point, Vegetation vegetation) {

        return getTileUpLeft(point).getVegetationType()    == vegetation &&
               getTileAbove(point).getVegetationType()     == vegetation &&
               getTileUpRight(point).getVegetationType()   == vegetation &&
               getTileDownRight(point).getVegetationType() == vegetation &&
               getTileBelow(point).getVegetationType()     == vegetation &&
               getTileDownLeft(point).getVegetationType()  == vegetation;
    }

    /**
     * Returns a list of the tiles surrounding the given point
     *
     * @param center
     * @return
     */
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

    /**
     * Returns true if the given point is surrounded by desert tiles
     *
     * @param point
     * @return
     */
    public boolean isInDesert(Point point) {
        return isSurroundedBy(point, Vegetation.DESERT);
    }

    /**
     * Returns true if the given point is next to a desert tile
     *
     * @param point
     * @return
     */
    public boolean isNextToDesert(Point point) {
        return isAnyAdjacentTile(point, Vegetation.DESERT);
    }

    /**
     * Returns true if the given tile is surrounded by snow
     *
     * @param point
     * @return
     */
    public boolean isOnSnow(Point point) {
        return isSurroundedBy(point, Vegetation.SNOW);
    }

    /**
     * Returns true if the given point is next to a tile of snow
     *
     * @param point
     * @return
     */
    public boolean isNextToSnow(Point point) {
        return isAnyAdjacentTile(point, Vegetation.SNOW);
    }

    /**
     * Returns true if the given point is next to a lava tile
     *
     * @param site
     * @return
     */
    public boolean isNextToLava(Point site) {
        return isAnyAdjacentTile(site, Vegetation.LAVA);
    }

    /**
     * Returns true if the given point is surrounded by lava
     *
     * @param point
     * @return
     */
    public boolean isOnLava(Point point) {
        return isSurroundedBy(point, Vegetation.LAVA);
    }

    /**
     * Returns true if the given point is in deep water
     *
     * @param site
     * @return
     */
    public boolean isInDeepWater(Point site) {
        return isSurroundedBy(site, Vegetation.DEEP_WATER);
    }

    /**
     * Returns true if the given point is next to a tile of deep water
     *
     * @param site
     * @return
     */
    public boolean isNextToDeepWater(Point site) {
        return isAnyAdjacentTile(site, Vegetation.DEEP_WATER);
    }

    /**
     * Returns true if the given point is next to a tile of swamp
     *
     * @param site
     * @return
     */
    public boolean isNextToSwamp(Point site) {
        return isAnyAdjacentTile(site, Vegetation.SWAMP);
    }

    /**
     * Returns true if the given point is next to a tile of magenta
     *
     * @param site
     * @return
     */
    public boolean isNextToMagenta(Point site) {
        return isAnyAdjacentTile(site, Vegetation.MAGENTA);
    }

    /**
     * Returns true if the given point is on vegetation where houses can be built
     *
     * @param point
     * @return
     */
    public boolean isOnBuildable(Point point) {
        return getTileUpLeft(point).getVegetationType().isBuildable()    &&
               getTileAbove(point).getVegetationType().isBuildable()     &&
               getTileUpRight(point).getVegetationType().isBuildable()   &&
               getTileDownRight(point).getVegetationType().isBuildable() &&
               getTileBelow(point).getVegetationType().isBuildable()     &&
               getTileDownLeft(point).getVegetationType().isBuildable();
    }

    /**
     * Changes the tiles surrounding the given point to contain large amounts of
     * the given mineral.
     *
     * @param point Point to surround with large quantities of mineral
     * @param mineral The type of mineral
     */
    public void surroundPointWithMineral(Point point, Material mineral) {
        surroundPointWithMineral(point, mineral, LARGE);
    }

    public void surroundPointWithMineral(Point point, Material mineral, Size amount) {
        getTileUpLeft(point).setAmountMineral(mineral, amount);
        getTileAbove(point).setAmountMineral(mineral, amount);
        getTileUpRight(point).setAmountMineral(mineral, amount);
        getTileDownRight(point).setAmountMineral(mineral, amount);
        getTileBelow(point).setAmountMineral(mineral, amount);
        getTileDownLeft(point).setAmountMineral(mineral, amount);
    }

    public Collection<Tile> getTilesBelow() {
        return tileBelowMap.values();
    }

    public Collection<Tile> getTilesDownRight() {
        return tileDownRightMap.values();
    }
}
