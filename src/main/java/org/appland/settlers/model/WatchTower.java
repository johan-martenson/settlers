/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE, STONE, STONE})
@MilitaryBuilding(maxHostedMilitary = 6, defenceRadius = 16, maxCoins = 3, attackRadius = 32)
public class WatchTower extends Building {

    public WatchTower(Player player0) {
        super(player0);
    }

    @Override
    public void stopProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot stop production in watch tower.");
    }

    @Override
    public void resumeProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot resume production in watch tower.");
    }
}
