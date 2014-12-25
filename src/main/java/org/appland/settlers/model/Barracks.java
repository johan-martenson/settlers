/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANCK, PLANCK})
@MilitaryBuilding(maxHostedMilitary = 2, defenceRadius = 8, maxCoins = 1, attackRadius = 12)
public class Barracks extends Building {

    public Barracks(Player p) {
        super(p);
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
