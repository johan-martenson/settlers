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

    public Terrain(int w, int h) {
        width   = w;
        height  = h;

        tileBelowMap = new HashMap<>();
        tileDownRightMap = new HashMap<>();

        constructDefaultTiles();
    }

    public Tile getTile(Point p1, Point p3, Point p2) {

        int left = Math.min(Math.min(p1.x, p2.x), p3.x);
        int top  = Math.max(Math.max(p1.y, p2.y), p3.y);

        Tile tile = null;

        if (p1.y + p2.y + p3.y % 3 == 1) { // Tile with pointy end upwards
            tile = tileBelowMap.get(top * width + left + 1);
        } else {
            tile = tileDownRightMap.get(top * width + left);
        }

        return tile;
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

    public boolean isOnMountain(Point p) {
        return isSurroundedBy(p, MOUNTAIN);
    }

    public boolean isInWater(Point p) {
        return isSurroundedBy(p, WATER);
    }

    boolean isInSwamp(Point p) {
        return isSurroundedBy(p, SWAMP);
    }

    protected boolean isOnGrass(Point p) {
        return isSurroundedBy(p, GRASS);
    }

    private boolean isAnyAdjacentTile(Point point, Vegetation vegetation) {
        List<Tile> tiles = getSurroundingTiles(point);

        for (Tile t : tiles) {
            if (t.getVegetationType() == vegetation) {
                return true;
            }
        }

        return false;        
    }

    private boolean isSurroundedBy(Point p, Vegetation vegetation) {
        List<Tile> tiles = getSurroundingTiles(p);

        for (Tile t : tiles) {
            if (t.getVegetationType() != vegetation) {
                return false;
            }
        }

        return true;
    }

    public List<Tile> getSurroundingTiles(Point center) {
        List<Tile> result   = new LinkedList<>();

        Point rightPoint = new Point(center.x + 2, center.y);
        Point leftPoint  = new Point(center.x - 2, center.y);
        Point p4 = new Point(center.x + 1, center.y - 1);
        Point p5 = new Point(center.x - 1, center.y - 1);
        Point p1 = new Point(center.x - 1, center.y + 1);
        Point p2 = new Point(center.x + 1, center.y + 1);

        Tile t = getTile(p4, center, rightPoint);

        /* This method is called frequently. Treat the tiles one-by-one
           to avoid creating a temporary list */
        if (t != null) {
            result.add(t);
        }

        t = getTile(p5, center, p4);

        if (t != null) {
            result.add(t);
        }

        t = getTile(leftPoint, center, p5);

        if (t != null) {
            result.add(t);
        }

        t = getTile(p1, center, leftPoint);

        if (t != null) {
            result.add(t);
        }

        t = getTile(p2, center, p1);

        if (t != null) {
            result.add(t);
        }

        t = getTile(rightPoint, center, p2);

        if (t != null) {
            result.add(t);
        }

        return result;
    }

    public boolean terrainMakesFlagPossible(Point p) throws Exception {
        if (isInWater(p)) {
            return false;
        }

        if (isInSwamp(p)) {
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
           on matching and one non-matching*/
        for (Tile t : getSurroundingTiles(point)) {

            if (t.getVegetationType().equals(vegetation)) {
                matchFound = true;
            } else if (!t.getVegetationType().equals(vegetation)) {
                nonMatchFound = true;
            }
        }

        return matchFound && nonMatchFound;
    }

    protected void placeMountainOnTile(Point p1, Point p2, Point p3) {
        Tile tile = getTile(p1, p2, p3);

        tile.setVegetationType(MOUNTAIN);
    }
}
