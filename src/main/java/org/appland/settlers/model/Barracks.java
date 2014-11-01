/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANCK, PLANCK})
@MilitaryBuilding(maxHostedMilitary = 2, defenceRadius = 6, maxCoins = 1)
public class Barracks extends Building {

    @Override
    public void stopProduction() throws Exception {
        throw new Exception("Cannot stop production in barracks.");
    }

    @Override
    public void resumeProduction() throws Exception {
        throw new Exception("Cannot resume production in barracks.");
    }
}
