/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANK, PLANK})
@MilitaryBuilding(maxHostedSoldiers = 2, defenceRadius = 8, maxCoins = 1, attackRadius = 12, discoveryRadius = 12)
@UpgradeCost(stones = 3)
public class Barracks extends Building {

    public Barracks(Player player) {
        super(player);
    }

    @Override
    public void stopProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot stop production in barracks.");
    }

    @Override
    public void resumeProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot resume production in barracks.");
    }

    @Override
    protected void doUpgradeBuilding() {
        Building upgraded = new GuardHouse(getPlayer());

        GameUtils.upgradeMilitaryBuilding(this, upgraded);
    }

    @Override
    public boolean isMilitaryBuilding() {
        return true;
    }
}
