/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE)
@Production(output = WHEAT, productionTime = 100, requiredGoods = {})
public class Farm extends Building {
}
