/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.HashMap;
import java.util.Map;
import org.appland.settlers.model.Tile.Vegetation;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.SWAMP;
import static org.appland.settlers.model.Tile.Vegetation.WATER;

/**
 *
 * @author johan
 */
public class Terrain {
    
    private final Map<TileKey, Tile> tileMap;
    private final int width;
    private final int height;

    public Terrain(int w, int h) {
        width   = w;
        height  = h;
        tileMap = new HashMap<>();

        constructDefaultTiles();
    }
    
    public Tile getTile(Point p1, Point p3, Point p2) {
        int leftMost  = Math.min(p1.x, p2.x);
        int rightMost = Math.max(p1.x, p2.x);
        int bottom    = Math.min(p1.y, p2.y);
        int top       = Math.max(p1.y, p2.y);
        
        leftMost  = Math.min(leftMost,  p3.x);
        rightMost = Math.max(rightMost, p3.x);
        bottom    = Math.min(bottom,    p3.y);
        top       = Math.max(top,       p3.y);
        
        return tileMap.get(new TileKey(leftMost, rightMost, top, bottom));
    }

    private void constructDefaultTiles() {
        int x, y;
        
        for (y = 0; y <= height; y++) {

            int xStart = 0;
            int xEnd   = width;
            boolean oddEvenFlip = true;
            
            if (y % 2 == 1) {
                xStart = -1;
                xEnd   = width + 1;
            }

            for (x = xStart; x <= xEnd + 1; x++) {
                if (oddEvenFlip) {
                    TileKey tk = new TileKey(x, x + 2, y + 1, y);
                    
                    Tile tile = new Tile(GRASS);
                    
                    tileMap.put(tk, tile);
                    
                    if (y > 0) {
                        TileKey tk2 = new TileKey(x, x + 2, y, y - 1);
                    
                        Tile tile2 = new Tile(GRASS);
                    
                        tileMap.put(tk2, tile2);
                    }
                }
                
                oddEvenFlip = !oddEvenFlip;
            }
        }
    }

    boolean isInWater(Point p) {
        return isSurroundedBy(p, WATER);
    }

    boolean isInSwamp(Point p) {
        return isSurroundedBy(p, SWAMP);
    }

    private boolean isSurroundedBy(Point p, Vegetation vegetation) {
        boolean isSurrounded = true;
        Tile[]  tiles     = getSurroundingTiles(p);
        
        for (Tile t : tiles) {
            if (t.getVegetationType() != vegetation) {
                isSurrounded = false;
                break;
            }
        }
    
        return isSurrounded;
    }

    Tile[] getSurroundingTiles(Point center) {
        int nrSurroundingTiles;
        
        if (center.y > 0 && center.y < height) {
            nrSurroundingTiles = 6;
        } else {
            nrSurroundingTiles = 3;
        }
        
        Tile[] tiles = new Tile[nrSurroundingTiles];

        Point rightPoint = new Point(center.x + 2, center.y);
        Point leftPoint  = new Point(center.x - 2, center.y);

        if (center.y > 0) {
            Point p4 = new Point(center.x + 1, center.y - 1);
            Point p5 = new Point(center.x - 1, center.y - 1);

            tiles[3] = getTile(p4, center, rightPoint);
            tiles[4] = getTile(p5, center, p4);
            tiles[5] = getTile(leftPoint, center, p5);
        }
    
        if (center.y < height) {
            Point p1 = new Point(center.x - 1, center.y + 1);
            Point p2 = new Point(center.x + 1, center.y + 1);

            tiles[0] = getTile(p1, center, leftPoint);
            tiles[1] = getTile(p2, center, p1);
            tiles[2] = getTile(rightPoint, center, p2);
        }        
        return tiles;
    }

    public boolean terrainMakesFlagPossible(Point p) {
        if (isInWater(p)) {
            return false;
        }
        
        if (isInSwamp(p)) {
            return false;
        }
    
        return true;
    }

    public static class TileKey {
        private final int rightMost;
        private final int leftMost;
        private final int top;
        private final int bottom;

        public TileKey(int l, int r, int t, int b) {
            leftMost = l;
            rightMost = r;
            top = t;
            bottom = b;
        }

        @Override
        public String toString() {
            return " (" + leftMost + ", " + rightMost + ", " + top + ", " + bottom + ") \n";
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this.rightMost;
            hash = 53 * hash + this.leftMost;
            hash = 53 * hash + this.top;
            hash = 53 * hash + this.bottom;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TileKey other = (TileKey) obj;
            if (this.rightMost != other.rightMost) {
                return false;
            }
            if (this.leftMost != other.leftMost) {
                return false;
            }
            if (this.top != other.top) {
                return false;
            }
            if (this.bottom != other.bottom) {
                return false;
            }
            return true;
        }
    }
}
