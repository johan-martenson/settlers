/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANCK, PLANCK})
@MilitaryBuilding(maxHostedMilitary = 2, defenceRadius = 8, maxCoins = 1, attackRadius = 12)
@UpgradeCost(stones = 3)
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

    @Override
    protected Building getUpgradedBuilding() throws Exception {
        Building upgraded = new GuardHouse(getPlayer());

        /* Set the map in the upgraded building */
        upgraded.setMap(getMap());

        /* Pre-construct the upgraded building */
        upgraded.setConstructionReady();

        /* Move the soldiers to the new building */
        for (int i = 0; i < getHostedMilitary(); i++) {

            /* Move one military from the old to the new building */
            Military military = this.retrieveMilitary();
            upgraded.deployMilitary(military);
        }

        /* Move the coins to the new building */
        for (int i = 0; i < getAmount(COIN); i++) {

            /* Put one coin in the new building */
            Cargo coinCargo = new Cargo(COIN, getMap());

            upgraded.promiseDelivery(COIN);

            upgraded.putCargo(coinCargo);
        }

        return upgraded;
    }
}
