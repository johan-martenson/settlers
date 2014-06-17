/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Courier;
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
import static org.junit.Assert.assertFalse;
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
    public void setup() {
        map = new GameMap();
    }
    
    @Test
    public void testPlaceBuildingOnEmptyMap() throws Exception {
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(1, 1);
        
        map.placeBuilding(wc, wcPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceSameBuildingTwice() throws Exception {
        Woodcutter wc    = new Woodcutter();
        Point wcPoint    = new Point(1, 1);
        Point otherPoint = new Point(2, 3);
        
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
    public void testAssignWorkerNotOnMapToRoad() throws Exception {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(2, 2));
        Road r  = new Road(f1, f2);
        
        map.placeRoad(r);
        
        map.assignWorkerToRoad(new Courier(map), r);
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
    public void testAddRoadWithExactSameStartAndEnd() throws InvalidEndPointException, Exception {
        Flag f1 = new Flag(new Point(1, 1));
        Road r  = new Road(f1, f1);

        map.placeFlag(f1);
        
        map.placeRoad(r);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadWithIdenticalStartAndEnd() throws InvalidEndPointException, Exception {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 1));
        Road r  = new Road(f1, f2);
        
        map.placeFlag(f1);
        map.placeFlag(f2);
        
        map.placeRoad(r);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadBetweenFlagsNotOnMap() throws InvalidEndPointException {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 2));
        Road r  = new Road(f1, f2);
        
        map.placeRoad(r);
    }
    
    @Test(expected=InvalidRouteException.class)
    public void testFindWayBetweenSameFlag() throws Exception {
        Flag f = new Flag(new Point(1, 1));
        
        map.placeFlag(f);
        
        map.findWay(f, f);
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
        
        Point f1 = new Point(1, 1);
        Point f2 = new Point(3, 2);
        Point f3 = new Point(6, 3);
        Point f4 = new Point(9, 4);
        Point f5 = new Point(12, 5);
        Point f6 = new Point(15, 6);
        Point f7 = new Point(18, 7);
        
        map.placeBuilding(wc, f1);
        map.placeBuilding(qry, f2);
        map.placeBuilding(qry2, f3);
        map.placeBuilding(s1, f4);
        map.placeBuilding(s2, f5);
        map.placeBuilding(hq, f6);
        map.placeBuilding(farm, f7);
        
        List<Storage> storages = map.getStorages();
    
        assertTrue(storages.size() == 3);
        assertTrue(storages.contains(s1));
        assertTrue(storages.contains(s2));
        assertTrue(storages.contains(hq));
    }
}
