/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.LARGE;

@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE, STONE, STONE, STONE, STONE, STONE})
@MilitaryBuilding(maxHostedMilitary = 9, defenceRadius = 11, maxCoins = 4, attackRadius = 36, discoveryRadius = 15)
public class Fortress extends Building {

    public Fortress(Player player0) {
        super(player0);
    }

    @Override
    public void stopProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot stop production in fortress.");
    }

    @Override
    public void resumeProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot resume production in fortress.");
    }
}
