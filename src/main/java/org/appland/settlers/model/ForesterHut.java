/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

@HouseSize(size = Size.SMALL)
@RequiresWorker(workerType = Material.FORESTER)
public class ForesterHut extends Building {

    public ForesterHut(Player player0) {
        super(player0);
    }
}
