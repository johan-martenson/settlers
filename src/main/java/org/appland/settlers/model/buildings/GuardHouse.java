/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.buildings;

import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANK, PLANK, PLANK, STONE, STONE})
@MilitaryBuilding(maxHostedSoldiers = 3, defenceRadius = 9, maxCoins = 2, attackRadius = 20, discoveryRadius = 13)
@UpgradeCost(stones = 3)
public class GuardHouse extends Building {

    public GuardHouse(Player player0) {
        super(player0);
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
    public void doUpgradeBuilding() {
        Building upgraded = new WatchTower(getPlayer());

        GameUtils.upgradeMilitaryBuilding(this, upgraded);
    }

    @Override
    public boolean isMilitaryBuilding() {
        return true;
    }
}
