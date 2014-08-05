/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.Collection;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestGameMap {
    private GameMap map;
    
    @Before
    public void setup() throws Exception {
        map = new GameMap(50, 50);
    }
    
    @Test
    public void testPlaceBuildingOnEmptyMap() throws Exception {
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(3, 3);
        
        map.placeBuilding(wc, wcPoint);
        
        assertTrue(wc.getPosition().equals(wcPoint));
    }
    
    @Test(expected=Exception.class)
    public void testPlaceSameBuildingTwice() throws Exception {
        Woodcutter wc    = new Woodcutter();
        Point wcPoint    = new Point(1, 1);
        Point otherPoint = new Point(2, 8);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(wc, otherPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceTwoBuildingsOnSameSpot() throws Exception {
        Woodcutter wc  = new Woodcutter();
        Quarry     qry = new Quarry();
        Point wcPoint  = new Point(1, 1);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(qry, wcPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceFlagsOnSamePlace() throws Exception {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 1));
        
        map.placeFlag(f1);
        map.placeFlag(f2);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceSameFlagTwice() throws Exception {
        Flag f1 = new Flag(new Point(1, 1));
        
        map.placeFlag(f1);
        
        f1.getPosition().x = 3;
        
        map.placeFlag(f1);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadWithIdenticalStartAndEnd() throws InvalidEndPointException, Exception {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 1));
        
        map.placeFlag(f1);
        map.placeFlag(f2);
        
        Road r = map.placeAutoSelectedRoad(f1, f2);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadBetweenFlagsNotOnMap() throws Exception {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 2));
        
        Road r = map.placeAutoSelectedRoad(f1, f2);
    }
    
    @Test(expected=InvalidRouteException.class)
    public void testFindWayBetweenSameFlag() throws Exception {
        Flag f = new Flag(new Point(1, 1));
        
        map.placeFlag(f);
        
        map.findWayWithExistingRoads(f.getPosition(), f.getPosition());
    }
      
    @Test
    public void testGetStorages() throws Exception {
        Woodcutter wc  = new Woodcutter();
        Quarry qry     = new Quarry();
        Quarry qry2    = new Quarry();
        Storage s1     = new Storage();
        Storage s2     = new Storage();
        Headquarter hq = new Headquarter();
        Farm farm      = new Farm();
        
        Point f1 = new Point(3,  3);
        Point f2 = new Point(9,  3);
        Point f3 = new Point(15, 3);
        Point f4 = new Point(4,  8);
        Point f5 = new Point(8,  8);
        Point f6 = new Point(14, 8);
        Point f7 = new Point(3,  15);
        
        map.placeBuilding(wc,   f1);
        map.placeBuilding(qry,  f2);
        map.placeBuilding(qry2, f3);
        map.placeBuilding(s1,   f4);
        map.placeBuilding(s2,   f5);
        map.placeBuilding(hq,   f6);
        map.placeBuilding(farm, f7);
        
        List<Storage> storages = map.getStorages();
    
        assertTrue(storages.size() == 3);
        assertTrue(storages.contains(s1));
        assertTrue(storages.contains(s2));
        assertTrue(storages.contains(hq));
    }

    @Test
    public void testCreateMinimalMap() throws Exception {
        new GameMap(5, 5);
    }
    
    @Test(expected=Exception.class)
    public void testCreateTooSmallMap() throws Exception {
        new GameMap(4, 4);
    }
    
    @Test(expected=Exception.class)
    public void testCreateMapWithNegativeHeight() throws Exception {
        new GameMap(10, -1);
    }
    
    @Test(expected=Exception.class)
    public void testCreateMapWithNegativeWidth() throws Exception {
        new GameMap(-3, 10);
    }

    @Test
    public void testGetFlags() throws Exception {
        GameMap map       = new GameMap(10, 10);
        Flag    f1        = new Flag(1, 1);
        Farm    farm      = new Farm();
        Point   farmPoint = new Point(3, 3);
        
        map.placeFlag(f1);
        map.placeBuilding(farm, farmPoint);

        List<Flag> flags = map.getFlags();

        assertTrue(flags.size() == 2);
        assertTrue(flags.contains(f1));
        assertTrue(flags.contains(farm.getFlag()));
    }

    @Test(expected=Exception.class)
    public void testPlaceBuildingOnInvalidPoint() throws Exception {
        GameMap map       = new GameMap(10, 10);
        Farm    farm      = new Farm();
        Point   farmPoint = new Point(3, 4);
        
        map.placeBuilding(farm, farmPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagOnInvalidPoint() throws Exception {
	GameMap map = new GameMap(10, 10);
        Flag    f   = new Flag(new Point(4, 7));

        map.placeFlag(f);
    }

    @Test
    public void testGetClosestStorage() {
        // TODO: Write test
    }

    @Test
    public void testPlaceBuildingSetsMap() throws Exception {
        GameMap map    = new GameMap(10, 10);
        Farm    farm   = new Farm();
        Point   point0 = new Point(5, 5);

        assertNull(farm.getMap());
        
        map.placeBuilding(farm, point0);
        
        assertEquals(farm.getMap(), map);
    }

    @Test
    public void testFindWayBetweenHouseAndItsFlag() throws Exception {
        GameMap    map    = new GameMap(10, 10);
        Woodcutter wc     = new Woodcutter();
        Point      point0 = new Point(5, 5);
        Point      point1 = new Point(6, 4);

        map.placeBuilding(wc, point0);
        
        assertNotNull(map.getRoad(point0, point1));

        assertNotNull(map.findWayOffroad(point0, point1, null));
        assertNotNull(map.findWayOffroad(point1, point0, null));
        
        assertNotNull(map.findWayWithExistingRoads(point0, point1));
        assertNotNull(map.findWayWithExistingRoads(point1, point0));
    }

    @Test
    public void testCreateHouseNextToExistingFlag() throws Exception {
        GameMap    map    = new GameMap(10, 10);
        Point      point0 = new Point(5, 5);
        Flag       flag0  = map.placeFlag(point0);
        
        Building wc = map.placeBuilding(new Woodcutter(), point0.upLeft());
        
        assertEquals(wc.getFlag(), flag0);
        assertNotNull(map.getRoad(wc.getPosition(), point0));
    }

    @Test
    public void testGetPerimeterWithOnlyHeadquarter() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        Collection<Point> border = map.getLandBorder();
        assertTrue(border.contains(new Point(50, 30)));
        assertTrue(border.contains(new Point(50, 70)));
        
        assertTrue(border.contains(new Point(30, 50)));
        assertTrue(border.contains(new Point(70, 50)));
        
        assertFalse(border.contains(new Point(31, 50)));
        assertFalse(border.contains(new Point(29, 50)));
    
        assertFalse(border.contains(new Point(71, 50)));
        assertFalse(border.contains(new Point(69, 50)));
    
        assertFalse(border.contains(new Point(50, 31)));
        assertFalse(border.contains(new Point(50, 29)));
        
        assertFalse(border.contains(new Point(50, 31)));
        assertFalse(border.contains(new Point(50, 29)));
    }
}


