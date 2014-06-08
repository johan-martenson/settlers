/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WOOD;

@HouseSize(size=Size.SMALL)
@Production(output=WOOD, productionTime=100, requiredGoods={})
@RequiresWorker(workerType=Material.FORESTER)
public class ForesterHut extends Building {
}
