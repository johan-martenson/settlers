/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.GoldMine;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.model.Tile;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlacement {

    @Test
    public void testDefaultMapIsEmpty() throws Exception {
        GameMap map = new GameMap(10, 10);
    
        List<Point> flagPoints = map.getAvailableFlagPoints();
        
        int x, y;
        
        boolean yFlip = true;
        boolean xFlip = true;
        for (x = 1; x < 10; x++) {
            yFlip = xFlip;
            
            for (y = 1; y < 10; y++) {
                if (yFlip) {
                    assertTrue(flagPoints.contains(new Point(x, y)));
                } else {
                    assertFalse(flagPoints.contains(new Point(x, y)));
                }
            
                yFlip = !yFlip;
            }
            
            xFlip = !xFlip;
        }
    }
    
    @Test
    public void testGetPossibleFlagPlacements() throws Exception {
        GameMap map = new GameMap(10, 10);
        
        List<Point> flagPoints = map.getAvailableFlagPoints();
        
        /* Test that flags can be placed every second point within the map */
        int x, y;
        boolean oddRow = true;
        for (y = 1; y < 10; y++) {
            for (x = 1; x < 10; x++) {
                if (oddRow) {
                    if (x % 2 == 0) {
                        assertFalse(flagPoints.contains(new Point(x, y)));
                    } else {
                        assertTrue(flagPoints.contains(new Point(x, y)));
                    }
                } else {
                    if (x % 2 == 0) {
                        assertTrue(flagPoints.contains(new Point(x, y)));
                    } else {
                        assertFalse(flagPoints.contains(new Point(x, y)));
                    }
                }
            }

            oddRow = !oddRow;
        }

        /* Test that flags can't be placed on the borders */
        for (y = 0; y < 11; y++) {
            assertFalse(flagPoints.contains(new Point(0, y)));
            assertFalse(flagPoints.contains(new Point(10, y)));
        }
        
        for (x = 0; x < 11; x++) {
            assertFalse(flagPoints.contains(new Point(x, 0)));
            assertFalse(flagPoints.contains(new Point(x, 10)));
        }
    }

    @Test
    public void testAvailableHousesNextToSmallHouse() throws Exception {
        GameMap map   = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);
        
        Map<Point, Size> possibleHouses = map.getAvailableHousePoints();
        
        /* The house's own point */
        assertFalse(possibleHouses.containsKey(new Point(6, 4)));
        
        /* The house's flag */
        assertFalse(possibleHouses.containsKey(new Point(7, 3)));
        
        /* Points in front, sampled */
        assertTrue(possibleHouses.get(new Point(8, 2)) == MEDIUM);
        assertTrue(possibleHouses.get(new Point(9, 3)) == MEDIUM);
        
        /* Points on left, sampled */
        assertFalse(possibleHouses.containsKey(new Point(6, 2)));
        assertFalse(possibleHouses.containsKey(new Point(5, 3)));
        
        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(new Point(4, 4)));
        assertFalse(possibleHouses.containsKey(new Point(5, 5)));
        assertTrue(possibleHouses.get(new Point(6, 6)) == MEDIUM);
        
        /* Points on right, sampled*/
        assertFalse(possibleHouses.containsKey(new Point(7, 5)));
        assertFalse(possibleHouses.containsKey(new Point(8, 4)));
    }
    
    @Test
    public void testAvailableFlagsNextToSmallHouse() throws Exception {
        GameMap map   = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);
        
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints();
        
        /* The house's own point */
        assertFalse(possibleFlagPoints.contains(new Point(6, 4)));
        
        /* The house's flag */
        assertFalse(possibleFlagPoints.contains(new Point(7, 3)));
        
        /* Points in front, sampled */
        assertFalse(possibleFlagPoints.contains(new Point(8, 2)));
        assertFalse(possibleFlagPoints.contains(new Point(9, 3)));
        
        /* Points on left, sampled */
        assertFalse(possibleFlagPoints.contains(new Point(6, 2)));
        assertFalse(possibleFlagPoints.contains(new Point(5, 3)));
        
        /* Points on top, sampled */
        assertTrue (possibleFlagPoints.contains(new Point(4, 4)));
        assertTrue (possibleFlagPoints.contains(new Point(5, 5)));
        assertTrue (possibleFlagPoints.contains(new Point(6, 6)));
        
        /* Points on right, sampled*/
        assertTrue (possibleFlagPoints.contains(new Point(7, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(8, 4)));
    }

    @Test
    public void testAvailableHousesNextToMediumHouse() throws Exception {
        GameMap map     = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Sawmill sawmill = new Sawmill();
        Point farmPoint = new Point(7,3);
        
        map.placeBuilding(sawmill, farmPoint);
        
        Map<Point, Size> possibleHouseSizes = map.getAvailableHousePoints();
        
        /* The house's own point */
        assertFalse(possibleHouseSizes.containsKey(new Point(7, 3)));
        
        /* The house's flag */
        assertFalse(possibleHouseSizes.containsKey(new Point(8, 2)));
        
        /* Points in front, sampled */
        assertTrue(possibleHouseSizes.get(new Point(9, 1)) == MEDIUM);
        assertTrue(possibleHouseSizes.get(new Point(10, 2)) == MEDIUM);

        /* Points on left, sampled */
        assertTrue(possibleHouseSizes.get(new Point(7, 1)) == MEDIUM); // WEIRD!!
        assertFalse(possibleHouseSizes.containsKey(new Point(6, 2)));
        assertFalse(possibleHouseSizes.containsKey(new Point(5, 3)));
        
        assertTrue (possibleHouseSizes.get(new Point(4, 2)) == MEDIUM);
        assertTrue (possibleHouseSizes.get(new Point(5, 1)) == MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouseSizes.containsKey(new Point(6, 4)));
        assertTrue (possibleHouseSizes.get(new Point(7, 5)) == MEDIUM);
        
        /* Points on right, sampled */
        assertFalse(possibleHouseSizes.containsKey(new Point(8, 4)));
        assertFalse(possibleHouseSizes.containsKey(new Point(9, 3)));
    }
    
    @Test
    public void testAvailableFlagsNextToMediumHouse() throws Exception {
        GameMap map     = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Sawmill sawmill = new Sawmill();
        Point farmPoint = new Point(7,3);
        
        map.placeBuilding(sawmill, farmPoint);
        
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints();
        
        /* The house's own point */
        assertFalse(possibleFlagPoints.contains(new Point(7, 3)));
        
        /* The house's flag */
        assertFalse(possibleFlagPoints.contains(new Point(8, 2)));
        
        /* Points in front, sampled */
        assertFalse(possibleFlagPoints.contains(new Point(9, 1)));
        assertFalse(possibleFlagPoints.contains(new Point(10, 2)));

        /* Points on left, sampled */
        assertFalse(possibleFlagPoints.contains(new Point(7, 1)));
        assertFalse(possibleFlagPoints.contains(new Point(6, 2)));
        assertTrue (possibleFlagPoints.contains(new Point(5, 3)));
        
        assertTrue (possibleFlagPoints.contains(new Point(4, 2)));
        assertTrue (possibleFlagPoints.contains(new Point(5, 1)));

        /* Points on top, sampled */
        assertTrue (possibleFlagPoints.contains(new Point(6, 4)));
        assertTrue (possibleFlagPoints.contains(new Point(7, 5)));
        assertTrue (possibleFlagPoints.contains(new Point(8, 6)));
        
        /* Points on right, sampled */
        assertTrue (possibleFlagPoints.contains(new Point(8, 4)));
        assertFalse(possibleFlagPoints.contains(new Point(9, 3)));

        assertTrue (possibleFlagPoints.contains(new Point(9, 5)));
        assertTrue (possibleFlagPoints.contains(new Point(10, 4)));
        assertTrue (possibleFlagPoints.contains(new Point(11, 3)));
        assertTrue (possibleFlagPoints.contains(new Point(12, 2)));
    }
    
    @Test
    public void testAvailableHousesNextToLargeHouse() throws Exception {
        GameMap map     = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Farm farm       = new Farm();
        Point farmPoint = new Point(7,3);
        
        map.placeBuilding(farm, farmPoint);
        
        Map<Point, Size> possibleHouses = map.getAvailableHousePoints();
        
        /* The house's own point */
        assertFalse(possibleHouses.containsKey(new Point(7, 3)));
        
        /* More space under the house */
        assertFalse(possibleHouses.containsKey(new Point(6, 4)));
        assertFalse(possibleHouses.containsKey(new Point(6, 2)));
        assertFalse(possibleHouses.containsKey(new Point(5, 3)));
        
        /* The house's flag */
        assertFalse(possibleHouses.containsKey(new Point(8, 2)));
        
        /* Points in front, TBD sampled */ 
        // assertFalse(possibleFlagPoints.contains(new Point(9, 1)));
        // assertFalse(possibleFlagPoints.contains(new Point(10, 2)));
        assertFalse(possibleHouses.containsKey(new Point(7, 1)));
        assertFalse(possibleHouses.containsKey(new Point(9, 3)));

        /* Points on left, sampled */
        assertFalse(possibleHouses.containsKey(new Point(5, 1)));
        assertTrue(possibleHouses.get(new Point(4, 2)) == MEDIUM);
        assertTrue(possibleHouses.get(new Point(3, 3)) == MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(new Point(4, 4)));
        assertFalse(possibleHouses.containsKey(new Point(5, 5)));
        assertTrue(possibleHouses.get(new Point(6, 6)) == MEDIUM);
        
        /* Points on right, sampled */
        assertFalse(possibleHouses.containsKey(new Point(7, 5)));
        assertFalse(possibleHouses.containsKey(new Point(8, 4)));
    }
    
    @Test
    public void testAvailableFlagsNextToLargeHouse() throws Exception {
        GameMap map     = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Farm farm       = new Farm();
        Point farmPoint = new Point(7,3);
        
        map.placeBuilding(farm, farmPoint);
        
        List<Point> possibleFlags = map.getAvailableFlagPoints();
        
        /* The house's own point */
        assertFalse(possibleFlags.contains(new Point(7, 3)));
        
        /* More space under the house */
        assertFalse(possibleFlags.contains(new Point(6, 4)));
        assertFalse(possibleFlags.contains(new Point(6, 2)));
        assertFalse(possibleFlags.contains(new Point(5, 3)));
        
        /* The house's flag */
        assertFalse(possibleFlags.contains(new Point(8, 2)));
        
        /* Points in front, TBD sampled */ 
        // assertFalse(possibleFlagPoints.contains(new Point(9, 1)));
        // assertFalse(possibleFlagPoints.contains(new Point(10, 2)));
        assertFalse(possibleFlags.contains(new Point(7, 1)));
        assertFalse(possibleFlags.contains(new Point(9, 3)));

        /* Points on left, sampled */
        assertTrue(possibleFlags.contains(new Point(5, 1)));
        assertTrue(possibleFlags.contains(new Point(4, 2)));
        assertTrue(possibleFlags.contains(new Point(3, 3)));

        /* Points on top, sampled */
        assertTrue(possibleFlags.contains(new Point(4, 4)));
        assertTrue(possibleFlags.contains(new Point(5, 5)));
        assertTrue(possibleFlags.contains(new Point(6, 6)));
        
        /* Points on right, sampled */
        assertTrue(possibleFlags.contains(new Point(7, 5)));
        assertFalse(possibleFlags.contains(new Point(8, 4)));        
    }
    
    @Test
    public void testPlaceFlagTakesSpace() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Point   p   = new Point(3, 3);
        Flag    f   = new Flag(p);
        
        map.placeFlag(f);
        
        List<Point> availableFlagPoints = map.getAvailableFlagPoints();
        
        assertTrue(availableFlagPoints.contains(new Point(1, 1)));
        assertTrue(availableFlagPoints.contains(new Point(3, 1)));
        assertTrue(availableFlagPoints.contains(new Point(5, 1)));
        
        assertFalse(availableFlagPoints.contains(new Point(2, 2)));
        assertFalse(availableFlagPoints.contains(new Point(4, 2)));

        assertTrue (availableFlagPoints.contains(new Point(1, 3)));
        assertFalse(availableFlagPoints.contains(new Point(3, 3)));
        assertTrue (availableFlagPoints.contains(new Point(5, 3)));
    
        assertFalse(availableFlagPoints.contains(new Point(2, 4)));
        assertFalse(availableFlagPoints.contains(new Point(4, 4)));
    
        assertTrue(availableFlagPoints.contains(new Point(1, 5)));
        assertTrue(availableFlagPoints.contains(new Point(3, 5)));
        assertTrue(availableFlagPoints.contains(new Point(5, 5)));

    }
    
    @Test(expected=Exception.class)
    public void testPlaceFlagTooCloseToSmallHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);

        Flag f = new Flag(new Point(6, 2));
        
        map.placeFlag(f);
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagOnHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        
        map.placeBuilding(wc, wcPoint);

        Flag f = new Flag(new Point(6, 4));
        
        map.placeFlag(f);
    }

    @Test(expected=Exception.class) 
    public void testPlaceHouseOnFlag() throws Exception {
        GameMap map   = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(6, 4);
        Flag f        = new Flag(new Point(6, 4));
        
        map.placeFlag(f);

        map.placeBuilding(wc, wcPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceHouseOnHouse() throws Exception {
        GameMap map    = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc  = new Woodcutter();
        Quarry     qry = new Quarry();
        Point wcPoint  = new Point(6, 4);
        Point qryPoint = new Point(5, 5);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(qry, qryPoint);
    }

    @Test
    public void testWaypointsNextToFlag() {
        
    }

    @Test
    public void testAutomaticWaypointSelection() throws Exception {
        GameMap map = new GameMap(10, 10);
        //List<Point> = map.proposeNewRoad(new Point(1, 1), new Point(3, 7));
        
    }

    @Test
    public void testPossibleFlagsNextToWater() throws Exception {
        GameMap map        = new GameMap(6, 6);
        Point sharedPoint1 = new Point(3, 1);
        Point sharedPoint2 = new Point(4, 2);
        Point grassPoint   = new Point(5, 1);
        Point waterPoint   = new Point(2, 2);
        
        Tile waterTile = map.getTerrain().getTile(sharedPoint1, sharedPoint2, waterPoint);
        Tile grassTile = map.getTerrain().getTile(sharedPoint1, sharedPoint2, grassPoint);
        
        waterTile.setVegetationType(WATER);
        grassTile.setVegetationType(GRASS);

        map.terrainIsUpdated();
        
        List<Point> possibleFlags = map.getAvailableFlagPoints();
        
        assertTrue(possibleFlags.contains(sharedPoint1));
        assertTrue(possibleFlags.contains(sharedPoint2));
        assertTrue(possibleFlags.contains(grassPoint));
    }

    @Test
    public void testCanNotPlaceFlagInWater() throws Exception {
        GameMap map        = new GameMap(6, 6);
        Point borderPoint1 = new Point(1, 1);
        Point borderPoint2 = new Point(2, 0);
        Point borderPoint3 = new Point(4, 0);
        Point borderPoint4 = new Point(5, 1);
        Point borderPoint5 = new Point(4, 2);
        Point borderPoint6 = new Point(2, 2);
        Point centerPoint  = new Point(3, 1);
        
        Tile waterTile1 = map.getTerrain().getTile(borderPoint1, borderPoint2, centerPoint);
        Tile waterTile2 = map.getTerrain().getTile(borderPoint2, borderPoint3, centerPoint);
        Tile waterTile3 = map.getTerrain().getTile(borderPoint3, borderPoint4, centerPoint);
        Tile waterTile4 = map.getTerrain().getTile(borderPoint4, borderPoint5, centerPoint);
        Tile waterTile5 = map.getTerrain().getTile(borderPoint5, borderPoint6, centerPoint);
        Tile waterTile6 = map.getTerrain().getTile(borderPoint6, borderPoint1, centerPoint);
        
        waterTile1.setVegetationType(WATER);
        waterTile2.setVegetationType(WATER);
        waterTile3.setVegetationType(WATER);
        waterTile4.setVegetationType(WATER);
        waterTile5.setVegetationType(WATER);
        waterTile6.setVegetationType(WATER);
        
        map.terrainIsUpdated();
        
        List<Point> possibleFlags = map.getAvailableFlagPoints();
        
        assertFalse(possibleFlags.contains(centerPoint));
    }    

    @Test
    public void testDifferentPointOrderGivesSameTile() throws Exception {
        GameMap map  = new GameMap(10, 10);
        Point point1 = new Point(3, 1);
        Point point2 = new Point(4, 2);
        Point point3 = new Point(5, 1);
        
        Tile tile1 = map.getTerrain().getTile(point1, point2, point3);
        Tile tile2 = map.getTerrain().getTile(point1, point3, point2);
        Tile tile3 = map.getTerrain().getTile(point2, point1, point3);
        Tile tile4 = map.getTerrain().getTile(point2, point3, point1);
        Tile tile5 = map.getTerrain().getTile(point3, point1, point2);
        Tile tile6 = map.getTerrain().getTile(point3, point2, point1);
    
        assertEquals(tile1, tile2);
        assertEquals(tile2, tile3);
        assertEquals(tile3, tile4);
        assertEquals(tile4, tile5);
        assertEquals(tile5, tile6);
    }

    @Test
    public void testDifferentiateBetweenCloseTiles() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point bottom1 = new Point(1, 1);
        Point bottom2 = new Point(3, 1);
        Point middle  = new Point(2, 2);        
        Point top1    = new Point(1, 3);
        Point top2    = new Point(3, 3);
    
        Tile t1 = map.getTerrain().getTile(bottom1, bottom2, middle);
        Tile t2 = map.getTerrain().getTile(top1, top2, middle);
        
        assertFalse(t1.equals(t2));
    }

    @Test
    public void testSetTileToMountainTerrain() throws Exception {
        GameMap map = new GameMap(10, 10);
        Point left  = new Point(1, 1);
        Point top   = new Point(2, 2);        
        Point right = new Point(3, 1);
    
        Tile t1 = map.getTerrain().getTile(left, right, top);

        t1.setVegetationType(MOUNTAIN);

        map.terrainIsUpdated();
        
        t1 = map.getTerrain().getTile(left, right, top);
        
        assertEquals(t1.getVegetationType(), MOUNTAIN);
    }
    
    @Test
    public void testTreeCannotBePlacedOnStone() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        map.placeStone(point0);
        
        try {
            map.placeTree(point0);
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getTrees().isEmpty());
    }

    @Test
    public void testCanNotPlaceFlagOnStone() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeStone(point0);
        
        try {
            map.placeFlag(point0);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertTrue(map.getBuildings().size() == 1);
    }

    @Test
    public void testCanNotPlaceFlagOnTree() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeTree(point0);
        
        try {
            map.placeFlag(point0);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertTrue(map.getBuildings().size() == 1);
    }

    @Test
    public void testCanNotPlaceFlagOnHouse() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(), hqPoint);
            
        map.placeBuilding(new Woodcutter(), point0);
        
        try {
            map.placeFlag(point0);
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getFlags().size() == 2);
    }

    @Test
    public void testCanNotPlaceBuildingIfFlagCanNotBePlaced() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeTree(point0.downRight());
            
        try {
            map.placeBuilding(new Woodcutter(), point0);        
            assertFalse(true);
        } catch (Exception e) {}

        assertTrue(map.getBuildings().size() == 1);
        assertTrue(map.getFlags().size() == 1);
        assertTrue(map.getTrees().size() == 1);
    }
}
