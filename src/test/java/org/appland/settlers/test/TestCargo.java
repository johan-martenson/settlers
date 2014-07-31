/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestCargo {
    
    @Test
    public void testNextStepIsNullForCargoWithoutTarget() throws Exception {
        Cargo cargo = new Cargo(WOOD);
        assertNull(cargo.getNextStep());
    }

    @Test
    public void testGetNextIsValidDirectlyAfterSetTarget() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        Cargo cargo = new Cargo(PLANCK);
        
        flag0.putCargo(cargo);
        
        cargo.setTarget(wc, map);
        
        assertEquals(cargo.getNextStep(), point3);
    }

    @Test
    public void testPuttingCargoAtFlagSetsPosition() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(8, 6);
        Flag flag0 = map.placeFlag(point1);

        Cargo cargo = new Cargo(PLANCK);
        
        flag0.putCargo(cargo);
        
        assertEquals(cargo.getPosition(), point1);
    }
}
