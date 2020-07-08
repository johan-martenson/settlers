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

import static org.appland.settlers.model.Vegetation.GRASS;
import static org.appland.settlers.model.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Vegetation.SWAMP;
import static org.appland.settlers.model.Vegetation.WATER;

/**
 *
 * @author johan
 */
public class Terrain {

    // FIXME: change to use Vegetation arrays instead to make access faster
    private final Map<Integer, Vegetation> tileBelowMap;
    private final Map<Integer, Vegetation> tileDownRightMap;
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
    public Vegetation getTileAbove(Point point) {
        return tileDownRightMap.get((point.y + 1) * width + point.x - 1);
    }

    public void setTileAbove(Point point, Vegetation vegetation) {
        tileDownRightMap.put((point.y + 1) * width + point.x - 1, vegetation);
    }

    /**
     * Returns the tile below the given point
     *
     * @param point
     * @return
     */
    public Vegetation getTileBelow(Point point) {
        return tileBelowMap.get(point.y * width + point.x);
    }

    public void setTileBelow(Point point, Vegetation vegetation) {
        tileBelowMap.put(point.y * width + point.x, vegetation);
    }

    /**
     * Returns the tile down to the right of the given point
     *
     * @param point
     * @return
     */
    public Vegetation getTileDownRight(Point point) {
        return tileDownRightMap.get(point.y * width + point.x);
    }

    public void setTileDownRight(Point point, Vegetation vegetation) {
        tileDownRightMap.put(point.y * width + point.x, vegetation);
    }

    /**
     * Returns the tile dow to the left of the given point
     *
     * @param point
     * @return
     */
    public Vegetation getTileDownLeft(Point point) {
        return tileDownRightMap.get(point.y * width + point.x - 2);
    }

    public void setTileDownLeft(Point point, Vegetation vegetation) {
        tileDownRightMap.put(point.y * width + point.x - 2, vegetation);
    }

    /**
     * Returns the tile up to the right of the given point
     *
     * @param point
     * @return
     */
    public Vegetation getTileUpRight(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x + 1);
    }

    public void setTileUpRight(Point point, Vegetation vegetation) {
        tileBelowMap.put((point.y + 1) * width + point.x + 1, vegetation);
    }

    /**
     * Returns the tile up to the left of the given point
     *
     * @param point
     * @return
     */
    public Vegetation getTileUpLeft(Point point) {
        return tileBelowMap.get((point.y + 1) * width + point.x - 1);
    }

    public void setTileUpLeft(Point point, Vegetation vegetation) {
        tileBelowMap.put((point.y + 1) * width + point.x - 1, vegetation);
    }

    // FIXME: HOTSPOT FOR ALLOCATION
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
                tileBelowMap.put(y * width + x, GRASS);
                tileDownRightMap.put(y * width + x, GRASS);
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

        return getTileUpLeft(point)    == vegetation ||
               getTileAbove(point)     == vegetation ||
               getTileUpRight(point)   == vegetation ||
               getTileDownRight(point) == vegetation ||
               getTileBelow(point)     == vegetation ||
               getTileDownLeft(point)  == vegetation;
    }

    /**
     * Surrounds the given point with the chosen type of vegetation
     *
     * @param point
     * @param vegetation
     */
    public void surroundWithVegetation(Point point, Vegetation vegetation) {
        setTileUpLeft(point, vegetation);
        setTileAbove(point, vegetation);
        setTileUpRight(point, vegetation);
        setTileDownRight(point, vegetation);
        setTileBelow(point, vegetation);
        setTileDownLeft(point, vegetation);
    }

    boolean isSurroundedBy(Point point, Vegetation vegetation) {

        return getTileUpLeft(point)    == vegetation &&
               getTileAbove(point)     == vegetation &&
               getTileUpRight(point)   == vegetation &&
               getTileDownRight(point) == vegetation &&
               getTileBelow(point)     == vegetation &&
               getTileDownLeft(point)  == vegetation;
    }

    /**
     * Returns a list of the tiles surrounding the given point
     *
     * @param point
     * @return
     */
    public List<Vegetation> getSurroundingTiles(Point point) {
        List<Vegetation> result = new LinkedList<>();

        Vegetation vegetationUpLeft    = getTileUpLeft(point);
        Vegetation vegetationAbove     = getTileAbove(point);
        Vegetation vegetationUpRight   = getTileUpRight(point);
        Vegetation vegetationDownRight = getTileDownRight(point);
        Vegetation vegetationBelow     = getTileBelow(point);
        Vegetation vegetationDownLeft  = getTileDownLeft(point);

        if (vegetationUpLeft != null) {
            result.add(vegetationUpLeft);
        }

        if (vegetationAbove != null) {
            result.add(vegetationAbove);
        }

        if (vegetationUpRight != null) {
            result.add(vegetationUpRight);
        }

        if (vegetationDownRight != null) {
            result.add(vegetationDownRight);
        }

        if (vegetationBelow != null) {
            result.add(vegetationBelow);
        }

        if (vegetationDownLeft != null) {
            result.add(vegetationDownLeft);
        }

        return result;
    }

    boolean isNextToWater(Point point) {
        return isAnyAdjacentTile(point, WATER);
    }

    boolean isOnEdgeOf(Point point, Vegetation vegetation) {

        boolean matchFound = false;
        boolean nonMatchFound = false;

        /* Go through the surrounding tiles and verify that they contain at least on matching and one non-matching */
        for (Vegetation vegetation1 : getSurroundingTiles(point)) {

            if (vegetation1 == vegetation) {
                matchFound = true;

                continue;
            }

            nonMatchFound = true;
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
        return isAnyAdjacentTile(site, SWAMP);
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
        return getTileUpLeft(point).isBuildable()    &&
               getTileAbove(point).isBuildable()     &&
               getTileUpRight(point).isBuildable()   &&
               getTileDownRight(point).isBuildable() &&
               getTileBelow(point).isBuildable()     &&
               getTileDownLeft(point).isBuildable();
    }

    public void fillMapWithVegetation(Vegetation vegetation) {
        for (Map.Entry<Integer, Vegetation> entry : tileBelowMap.entrySet()) {
            tileBelowMap.put(entry.getKey(), vegetation);
        }

        for (Map.Entry<Integer, Vegetation> entry : tileDownRightMap.entrySet()) {
            tileDownRightMap.put(entry.getKey(), vegetation);
        }
    }
}
