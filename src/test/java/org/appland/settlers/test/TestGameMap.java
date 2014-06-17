/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
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
    public void testPlaceFlagOnSamePlace() throws Exception {
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
    public void testAddRoadWithExactSameStartAndEnd() throws InvalidEndPointException {
        Flag f1 = new Flag(new Point(1, 1));
        Road r  = new Road(f1, f1);
        
        map.placeRoad(r);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadWithIdenticalStartAndEnd() throws InvalidEndPointException {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 1));
        Road r  = new Road(f1, f2);
        
        map.placeRoad(r);
    }
}
