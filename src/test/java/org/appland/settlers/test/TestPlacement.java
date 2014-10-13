/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.model.Tile;
import org.appland.settlers.model.Tile.Vegetation;
import static org.appland.settlers.model.Tile.Vegetation.GRASS;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import org.appland.settlers.model.Woodcutter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    
        assertTrue(map.getBuildings().isEmpty());
        assertTrue(map.getRoads().isEmpty());
        assertTrue(map.getStones().isEmpty());
        assertTrue(map.getTrees().isEmpty());
        assertTrue(map.getFlags().isEmpty());
    
        // TODO: verify all placable objects in map
    }
    
    @Test
    public void testEmptyMapHasNoBorders() throws Exception {
        GameMap map = new GameMap(10, 10);
    
        assertEquals(map.getBorders().size(), 0);
    }
    
    @Test
    public void testAvailableFlagPointsContainsValidFlagPoint() throws Exception {
        GameMap map = new GameMap(10, 10);
    
        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(new Headquarter(), point0);
        
        /* Verify that there is a valid flag point in the available flag points */
        List<Point> flagPoints = map.getAvailableFlagPoints();

        assertTrue(flagPoints.contains(new Point(8, 6)));
    }
    
    @Test
    public void testFlagsCannotBePlacedEdgeOfGameMap() throws Exception {
        GameMap map = new GameMap(10, 10);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        List<Point> flagPoints = map.getAvailableFlagPoints();

        /* Test that flags can't be placed on the borders */
        for (int y = 0; y < 11; y++) {
            assertFalse(flagPoints.contains(new Point(0, y)));
            assertFalse(flagPoints.contains(new Point(10, y)));
        }
        
        for (int x = 0; x < 11; x++) {
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
        assertEquals(possibleHouses.get(new Point(8, 2)), MEDIUM);
        assertEquals(possibleHouses.get(new Point(9, 3)), MEDIUM);
        
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
    public void testAvailableFlagsNextToFlag() throws Exception {
        GameMap map   = new GameMap(20, 20);
        
        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);

        /* Place flag */
        Point point0 = new Point(5, 5);
        Flag flag0 = map.placeFlag(point0);
        
        List<Point> possibleFlagPoints = map.getAvailableFlagPoints();
        
        /* The flag's own point */
        assertFalse(possibleFlagPoints.contains(point0));
        
        /* The right and left of the flag */
        assertFalse(possibleFlagPoints.contains(point0.right()));
        assertFalse(possibleFlagPoints.contains(point0.left()));
        
        /* Diagonally of the flag */
        assertFalse(possibleFlagPoints.contains(point0.upRight()));
        assertFalse(possibleFlagPoints.contains(point0.downRight()));
        assertFalse(possibleFlagPoints.contains(point0.upLeft()));
        assertFalse(possibleFlagPoints.contains(point0.downLeft()));
        
        /* Surrounding points */
        assertTrue (possibleFlagPoints.contains(point0.up()));
        assertTrue (possibleFlagPoints.contains(point0.down()));
        assertTrue (possibleFlagPoints.contains(point0.upRight().upRight()));
        assertTrue (possibleFlagPoints.contains(point0.upRight().right()));
        assertTrue (possibleFlagPoints.contains(point0.right().right()));
        assertTrue (possibleFlagPoints.contains(point0.downRight().right()));
        assertTrue (possibleFlagPoints.contains(point0.downRight().downRight()));
        assertTrue (possibleFlagPoints.contains(point0.down()));
        assertTrue (possibleFlagPoints.contains(point0.downLeft().downLeft()));
        assertTrue (possibleFlagPoints.contains(point0.downLeft().left()));
        assertTrue (possibleFlagPoints.contains(point0.left().left()));
        assertTrue (possibleFlagPoints.contains(point0.upLeft().left()));
        assertTrue (possibleFlagPoints.contains(point0.upLeft().upLeft()));
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
        
        /* Points on right, sampled */
        assertTrue (possibleFlagPoints.contains(new Point(7, 5)));
        assertFalse(possibleFlagPoints.contains(new Point(8, 4)));
    }

    @Test
    public void testAvailableHousesNextToMediumHouse() throws Exception {
        GameMap map     = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Sawmill sawmill = new Sawmill();
        Point point0 = new Point(7,3);
        
        map.placeBuilding(sawmill, point0);
        
        Map<Point, Size> possibleHouseSizes = map.getAvailableHousePoints();
        
        /* The house's own point */
        assertFalse(possibleHouseSizes.containsKey(new Point(7, 3)));
        
        /* The house's flag */
        assertFalse(possibleHouseSizes.containsKey(new Point(8, 2)));
        
        /* Points in front, sampled */
        assertTrue(possibleHouseSizes.get(new Point(9, 1)) == MEDIUM);
        assertTrue(possibleHouseSizes.get(new Point(10, 2)) == MEDIUM);

        /* Points on left, sampled */
//        assertTrue(possibleHouseSizes.get(new Point(7, 1)) == MEDIUM); // WEIRD!!
        assertFalse(possibleHouseSizes.containsKey(new Point(6, 2)));
        assertFalse(possibleHouseSizes.containsKey(new Point(5, 3)));
        
        assertEquals(possibleHouseSizes.get(new Point(4, 2)), MEDIUM);
        assertEquals(possibleHouseSizes.get(new Point(5, 1)), MEDIUM);

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
        Point farmPoint = new Point(7, 3);
        
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
        assertEquals(possibleHouses.get(new Point(4, 2)), MEDIUM);
        assertEquals(possibleHouses.get(new Point(3, 3)), MEDIUM);

        /* Points on top, sampled */
        assertFalse(possibleHouses.containsKey(new Point(4, 4)));
        assertFalse(possibleHouses.containsKey(new Point(5, 5)));
        assertEquals(possibleHouses.get(new Point(6, 6)), MEDIUM);
        
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
        Point farmPoint = new Point(7, 3);
        
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
        GameMap map        = new GameMap(20, 20);
        Point sharedPoint1 = new Point(3, 1);
        Point sharedPoint2 = new Point(4, 2);
        Point grassPoint   = new Point(5, 1);
        Point waterPoint   = new Point(2, 2);
        
        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(), point0);
        
        /* Create water and grass tiles */
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

    @Test(expected = Exception.class)
    public void testCannotPlaceRoadAcrossLake() throws Exception {
        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Point point1 = new Point(8, 4);
        Point point2 = new Point(9, 5);
        map.getTerrain().getTile(point0, point1, point2).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* 1 ticks from start */
        Utils.fastForward(1, map);

        /* Place a water tile */
        Point point3 = new Point(11, 5);
        map.getTerrain().getTile(point0, point2, point3).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point4 = new Point(12, 4);
        map.getTerrain().getTile(point0, point3, point4).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point5 = new Point(11, 3);
        map.getTerrain().getTile(point0, point4, point5).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point6 = new Point(9, 3);
        map.getTerrain().getTile(point0, point5, point6).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        map.getTerrain().getTile(point0, point6, point1).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing flag */
        Flag flag0 = map.placeFlag(point1);

        /* Placing flag */
        Flag flag1 = map.placeFlag(point4);

        /* Placing road between (8, 4) and (12, 4) */
        Road road0 = map.placeRoad(point1, point0, point4);
    }

    @Test
    public void testRoadConnectionSuggestionsDoNotIncludePointsInWater() throws Exception {
        /* Starting new game */
        GameMap map = new GameMap(40, 40);

        /* Place a water tile */
        Point point0 = new Point(10, 4);
        Point point1 = new Point(8, 4);
        Point point2 = new Point(9, 5);
        map.getTerrain().getTile(point0, point1, point2).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point3 = new Point(11, 5);
        map.getTerrain().getTile(point0, point2, point3).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point4 = new Point(12, 4);
        map.getTerrain().getTile(point0, point3, point4).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point5 = new Point(11, 3);
        map.getTerrain().getTile(point0, point4, point5).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        Point point6 = new Point(9, 3);
        map.getTerrain().getTile(point0, point5, point6).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Place a water tile */
        map.getTerrain().getTile(point0, point6, point1).setVegetationType(Vegetation.WATER);
        map.terrainIsUpdated();

        /* Placing headquarter */
        Point point21 = new Point(10, 10);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point21);

        /* Placing flag */
        Flag flag0 = map.placeFlag(point1);

        /* Placing flag */
        Flag flag1 = map.placeFlag(point4);

        /* Verify that suggested connections from flag0 don't include a point in the water */
        assertFalse(map.getPossibleAdjacentRoadConnections(point1, point4).contains(point1.right()));

        assertFalse(map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(point1).contains(point1.right()));
        
        assertFalse(map.getPossibleRoadConnectionsExcludingEndpoints(point1).contains(point1.right()));
    }

    @Test
    public void testFlagCanBePlacedOnSign() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        /* Place headquarter */
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Place sign */
        map.placeEmptySign(point0);
        
        assertFalse(map.getSigns().isEmpty());
        assertTrue(map.isSignAtPoint(point0));
        
        /* Place flag on the sign */
        map.placeFlag(point0);
        
        /* Verify that the sign is gone and the flag exists */
        assertTrue(map.isFlagAtPoint(point0));
        assertFalse(map.isSignAtPoint(point0));
        assertTrue(map.getSigns().isEmpty());
    }

    @Test
    public void testMineCanBePlacedOnSign() throws Exception {
        GameMap map   = new GameMap(10, 10);
        Point point0  = new Point(3, 3);
    
        /* Place headquarter */
        Point hqPoint = new Point(6, 6);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        /* Put a small mountain on point0 */
        Utils.surroundPointWithMountain(point0, map);
        
        /* Place sign */
        map.placeEmptySign(point0);
        
        assertFalse(map.getSigns().isEmpty());
        assertTrue(map.isSignAtPoint(point0));
        
        /* Build a mine on the sign */
        map.placeBuilding(new GoldMine(), point0);
        
        /* Verify that the sign is gone and the mine exists */
        assertTrue(map.isBuildingAtPoint(point0));
        assertFalse(map.isSignAtPoint(point0));
        assertTrue(map.getSigns().isEmpty());
    }
}
