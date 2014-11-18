/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.LARGE;

@HouseSize(size = LARGE, material = {PLANCK, PLANCK, PLANCK, PLANCK, STONE, STONE, STONE, STONE, STONE, STONE, STONE})
@MilitaryBuilding(maxHostedMilitary = 9, defenceRadius = 18, maxCoins = 4)
public class Fortress extends Building {

    @Override
    public void stopProduction() throws Exception {
        throw new Exception("Cannot stop production in barracks.");
    }

    @Override
    public void resumeProduction() throws Exception {
        throw new Exception("Cannot resume production in barracks.");
    }
}