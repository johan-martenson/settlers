/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
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
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        assertTrue(hq.getPosition().equals(hqPoint));
    }
    
    @Test(expected=Exception.class)
    public void testPlaceSameBuildingTwice() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);

        Woodcutter wc    = new Woodcutter();
        Point wcPoint    = new Point(1, 1);
        Point otherPoint = new Point(2, 8);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(wc, otherPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceTwoBuildingsOnSameSpot() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc  = new Woodcutter();
        Quarry     qry = new Quarry();
        Point wcPoint  = new Point(1, 1);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(qry, wcPoint);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceFlagsOnSamePlace() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 1));
        
        map.placeFlag(f1);
        map.placeFlag(f2);
    }
    
    @Test(expected=Exception.class)
    public void testPlaceSameFlagTwice() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag f1 = new Flag(new Point(1, 1));
        
        map.placeFlag(f1);
        
        f1.getPosition().x = 3;
        
        map.placeFlag(f1);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadWithIdenticalStartAndEnd() throws InvalidEndPointException, Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 1));
        
        map.placeFlag(f1);
        map.placeFlag(f2);
        
        Road r = map.placeAutoSelectedRoad(f1, f2);
    }
    
    @Test(expected=Exception.class)
    public void testAddRoadBetweenFlagsNotOnMap() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(1, 2));
        
        Road r = map.placeAutoSelectedRoad(f1, f2);
    }
    
    @Test(expected=InvalidRouteException.class)
    public void testFindWayBetweenSameFlag() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
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
        
        map.placeBuilding(hq,   f6);
        map.placeBuilding(wc,   f1);
        map.placeBuilding(qry,  f2);
        map.placeBuilding(qry2, f3);
        map.placeBuilding(s1,   f4);
        map.placeBuilding(s2,   f5);
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
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag    f1        = new Flag(1, 1);
        Farm    farm      = new Farm();
        Point   farmPoint = new Point(3, 3);
        
        map.placeFlag(f1);
        map.placeBuilding(farm, farmPoint);

        List<Flag> flags = map.getFlags();

        assertTrue(flags.size() == 3);
        assertTrue(flags.contains(f1));
        assertTrue(flags.contains(farm.getFlag()));
    }

    @Test(expected=Exception.class)
    public void testPlaceBuildingOnInvalidPoint() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);        
        
        Farm    farm      = new Farm();
        Point   farmPoint = new Point(3, 4);
        
        map.placeBuilding(farm, farmPoint);
    }

    @Test(expected=Exception.class)
    public void testPlaceFlagOnInvalidPoint() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);        
        
        Flag    f   = new Flag(new Point(4, 7));

        map.placeFlag(f);
    }

    @Test
    public void testGetClosestStorage() {
        // TODO: Write test
    }

    @Test
    public void testPlaceBuildingSetsMap() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);        
        
        Farm    farm   = new Farm();
        Point   point0 = new Point(5, 5);

        assertNull(farm.getMap());
        
        map.placeBuilding(farm, point0);
        
        assertEquals(farm.getMap(), map);
    }

    @Test
    public void testFindWayBetweenHouseAndItsFlag() throws Exception {
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);        
        
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
        Point hqPoint = new Point(8, 8);
        Building hq = map.placeBuilding(new Headquarter(), hqPoint);
        
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
        
        assertTrue(map.getBorders().size() == 1);
        
        Collection<Point> border = map.getBorders().get(0);
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

    @Test(expected = Exception.class)
    public void testPlaceFlagOutsideBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(71, 71);
        map.placeFlag(point1);
    }

    @Test(expected = Exception.class)
    public void testPlaceBuildingOutsideBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(71, 71);
        map.placeBuilding(new Woodcutter(), point1);
    }
    
    @Test
    public void testPlaceBuildingOutsideBorderHasNoEffect() throws Exception {
        GameMap map = new GameMap(100, 100);
        
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(71, 71);
        
        try {
            map.placeBuilding(new Woodcutter(), point1);
        } catch (Exception e) {}
        
        assertTrue(map.getBuildings().size() == 1);
        assertTrue(map.getFlags().size() == 1);
        assertTrue(map.getRoads().size() == 1);
    }
    
    @Test
    public void testPlaceRoadThatGoesOutsideTheBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(49, 69);
        Point point2 = new Point(50, 70);
        Point point3 = new Point(51, 71);
        Point point4 = new Point(50, 72);
        Point point5 = new Point(49, 71);
        Point point6 = new Point(48, 70);
        Point point7 = new Point(47, 69);
        
        map.placeFlag(point1);
        map.placeFlag(point7);
        
        try {
            map.placeRoad(point1, point2, point3, point4, point5, point6, point7);
            assertFalse(true);
        } catch (Exception e) {}
        
        assertTrue(map.getRoads().size() == 1);
    }

    @Test
    public void testRemoveFlag() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(50, 68);
        Flag flag0 = map.placeFlag(point1);
        
        map.removeFlag(flag0);
        
        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.isFlagAtPoint(point1));
    }
    
    @Test
    public void testRemovingFlagRemovesConnectedRoad() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        Building hq = map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(52, 50);
        
        Point point2 = new Point(53, 49);
        Flag flag0 = map.placeFlag(point2);
        
        Road road0 = map.placeRoad(hq.getFlag().getPosition(), point1, point2);
        
        map.removeFlag(flag0);
        
        assertFalse(map.getFlags().contains(flag0));
        assertFalse(map.getRoads().contains(road0));
        assertNull(map.getRoad(point0, point2));
    }
    
    @Test
    public void testDestroyBuildingByRemovingFlag() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(50, 68);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        
        map.removeFlag(wc.getFlag());
        
        assertTrue(map.getFlags().size() == 1);
        assertTrue(map.getRoads().size() == 1);
        assertTrue(map.getBuildings().size() == 2);
        assertTrue(wc.burningDown());
    }

    @Test
    public void testRemovingRemoteBarracksSplitsBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        assertTrue(map.getBorders().size() == 1);
        Collection<Point> border = map.getBorders().get(0);
        
        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));
        
        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(), point1);
        
        Utils.constructSmallHouse(barracks0);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);

        assertTrue(map.getBorders().size() == 1);
        border = map.getBorders().get(0);
        
        assertFalse(border.contains(new Point(50, 70)));
        assertTrue(border.contains(new Point(50, 74)));        
        
        Point point2 = new Point(50, 72);
        Building barracks1 = map.placeBuilding(new Barracks(), point2);

        Utils.constructSmallHouse(barracks1);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks1, map);
        
        assertTrue(map.getBorders().size() == 1);
        border = map.getBorders().get(0);
        
        assertFalse(border.contains(new Point(50, 74)));
        assertTrue(border.contains(new Point(50, 78)));        
        
        Point point3 = new Point(50, 76);
        Building barracks2 = map.placeBuilding(new Barracks(), point3);
        
        Utils.constructSmallHouse(barracks2);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks2, map);
        
        assertTrue(map.getBorders().size() == 1);
        border = map.getBorders().get(0);
        
        assertFalse(border.contains(new Point(50, 78)));
        assertTrue(border.contains(new Point(50, 82)));
        
        barracks0.tearDown();
        barracks1.tearDown();
                
        assertTrue(map.getBorders().size() == 2);
    }

    @Test
    public void testShrinkingBorderDestroysHouseNowOutsideOfBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        assertTrue(map.getBorders().size() == 1);
        Collection<Point> border = map.getBorders().get(0);
        
        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));
        
        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(), point1);
        
        Utils.constructSmallHouse(barracks0);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        Point point2 = new Point(50, 72);
        Building wc = map.placeBuilding(new Woodcutter(), point2);
        
        Utils.constructSmallHouse(wc);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        assertTrue(map.getBuildings().contains(wc));
        assertTrue(wc.ready());
        
        barracks0.tearDown();
        
        assertTrue(map.getBuildings().contains(wc));
        assertTrue(wc.burningDown());
    }

    @Test
    public void testShrinkingBorderDestroysFlagNowOutsideOfBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        assertTrue(map.getBorders().size() == 1);
        Collection<Point> border = map.getBorders().get(0);
        
        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));
        
        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(), point1);
        
        Utils.constructSmallHouse(barracks0);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        Point point2 = new Point(50, 72);
        Flag flag0 = map.placeFlag(point2);
        
        assertTrue(map.getFlags().contains(flag0));
        
        barracks0.tearDown();
        
        assertFalse(map.getFlags().contains(flag0));
    }

    @Test
    public void testShrinkingBorderDestroysRoadNowOutsideOfBorder() throws Exception {
        GameMap map = new GameMap(100, 100);
        Point point0 = new Point(50, 50);
        map.placeBuilding(new Headquarter(), point0);
        
        assertTrue(map.getBorders().size() == 1);
        Collection<Point> border = map.getBorders().get(0);
        
        assertTrue(border.contains(new Point(50, 70)));
        assertFalse(border.contains(new Point(50, 74)));
        
        Point point1 = new Point(50, 68);
        Building barracks0 = map.placeBuilding(new Barracks(), point1);
        
        Utils.constructSmallHouse(barracks0);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), barracks0, map);
        
        Point point2 = new Point(50, 72);
        Flag flag0 = map.placeFlag(point2);
        
        Point point3 = new Point(48, 70);
        Flag flag1 = map.placeFlag(point3);
        
        Road road0 = map.placeRoad(point2, point2.downLeft(), point3);
        
        assertTrue(map.getRoads().contains(road0));
        
        barracks0.tearDown();
        
        assertFalse(map.getRoads().contains(road0));
    }
    
    @Test
    public void testBorderCanBeConcave() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 34 ticks from start */
        Point point3 = new Point(4, 24);
        Building building1 = map.placeBuilding(new Barracks(), point3);

        Utils.constructSmallHouse(building1);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), building1, map);
        
        /* 55 ticks from start */
        Point point4 = new Point(19, 19);
        Building building2 = map.placeBuilding(new Barracks(), point4);

        Utils.constructSmallHouse(building2);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), building2, map);
        
        /* 951 ticks from start */
        Point point39 = new Point(20, 24);
        Building building3 = map.placeBuilding(new Barracks(), point39);

        Utils.constructSmallHouse(building3);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), building3, map);
        
        /* 1957 ticks from start */
        Point point45 = new Point(24, 28);
        Building building4 = map.placeBuilding(new Barracks(), point45);
        
        Utils.constructSmallHouse(building4);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), building4, map);

        assertTrue(map.getBorders().size() == 1);
        
        Collection<Point> border = map.getBorders().get(0);
        
        assertTrue(border.contains(new Point(24, 34)));
        assertTrue(border.contains(new Point(4, 30)));
        
        Point point46 = new Point(11, 23);
        assertTrue(border.contains(point46));
    }
    
    @Test
    public void testFieldOfViewIsOutsideBorder() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        
        Point point1 = new Point(1, 23);
        
        assertTrue(map.getBorders().get(0).contains(point1));
        
        Point point2 = new Point(1, 25);
        assertTrue(map.getFieldOfView().contains(point2));
    }
    
    @Test
    public void testFieldOfViewContainsAllOwnedLand() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        
        Collection<Point> border = map.getBorders().get(0);
        List<Point> fieldOfView = map.getFieldOfView();

        Path2D.Double path = new Path2D.Double();
        path.moveTo(fieldOfView.get(fieldOfView.size() - 1).x, fieldOfView.get(fieldOfView.size() - 1).y);
        
        for (Point p : fieldOfView) {
            path.lineTo(p.x, p.y);
        }

        for (Point p : border) {
            assertTrue(path.contains(p));
        }
    }
    
    @Test
    public void testFieldOfViewCannotShrink() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        /* 34 ticks from start */
        Point point3 = new Point(4, 24);
        Building building1 = map.placeBuilding(new Barracks(), point3);

        Utils.constructSmallHouse(building1);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), building1, map);
        
        Collection<Point> fieldOfViewBefore = map.getFieldOfView();
        
        building1.tearDown();
        
        assertEquals(fieldOfViewBefore, map.getFieldOfView());
    }
    
    @Test
    public void testFieldOfViewGrowsWhenBorderGrows() throws Exception {
        GameMap map = new GameMap(40, 40);

        /* 0 ticks from start */
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);

        Collection<Point> oldFieldOfView = map.getFieldOfView();
        
        Point point1 = new Point(5, 27);
        
        assertTrue(oldFieldOfView.contains(point1));
        
        /* 34 ticks from start */
        Point point2 = new Point(4, 24);
        Building building1 = map.placeBuilding(new Barracks(), point2);

        Utils.constructSmallHouse(building1);
        Utils.occupyMilitaryBuilding(new Military(PRIVATE_RANK, map), building1, map);
        
        Collection<Point> newFieldOfView = map.getFieldOfView();

        Point point3 = new Point(4, 32);
        
        assertTrue(newFieldOfView.contains(point3));
        assertFalse(newFieldOfView.contains(point1));
    }
    
    @Test
    public void testBarracksCanOnlyBeBuiltCloseToBorder() {
        // TODO: Implement test
    }
}