/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import static org.appland.settlers.model.Material.WOOD;
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
}
