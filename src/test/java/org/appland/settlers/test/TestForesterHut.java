/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Building.ConstructionState;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestForesterHut {
    @Test
    public void testConstructForrester() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        ForesterHut f = new ForesterHut();
        
        assertTrue(f.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);
        
        assertFalse(f.needsWorker());
        
        // TODO: New test to verify that a worker can't be assigned to an unfinished forrester */
        
        Utils.constructSmallHouse(f);
        
        /* Verify that the forrester is unoccupied when it's newly constructed */
        assertTrue(f.needsWorker());
        
        /* Verify that the ForesterHut requires a worker */
        assertTrue(f.needsWorker());
        
        Forester forester = new Forester();
        
        /* Assign worker */
        f.assignWorker(forester);
        
        assertFalse(f.needsWorker());
        assertTrue(f.getWorker().equals(forester));
    }

    @Test(expected=Exception.class)
    public void testAssignWorkerToUnfinishedForrester() throws Exception {
        GameMap map = new GameMap();
        ForesterHut f = new ForesterHut();
        
        assertTrue(f.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);
        
        f.assignWorker(new Forester());
    }
    
    @Test(expected=Exception.class)
    public void testAssignWorkerTwice() throws Exception {
        GameMap map = new GameMap();
        ForesterHut f = new ForesterHut();
        
        assertTrue(f.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);
        
        f.assignWorker(new Forester());
        
        f.assignWorker(new Forester());
    }
}
