/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANCK, PLANCK, STONE, STONE, STONE})
@MilitaryBuilding(maxHostedMilitary = 3, defenceRadius = 14, maxCoins = 2, attackRadius = 28)
public class GuardHouse extends Building {

    public GuardHouse(Player player0) {
        super(player0);
    }

    @Override
    public void stopProduction() throws Exception {
        throw new Exception("Cannot stop production in barracks.");
    }

    @Override
    public void resumeProduction() throws Exception {
        throw new Exception("Cannot resume production in barracks.");
    }
}
