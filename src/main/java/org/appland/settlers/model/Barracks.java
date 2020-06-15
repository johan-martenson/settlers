/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANK, PLANK})
@MilitaryBuilding(maxHostedMilitary = 2, defenceRadius = 8, maxCoins = 1, attackRadius = 12, discoveryRadius = 12)
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
    protected void doUpgradeBuilding() throws Exception {
        Building upgraded = new GuardHouse(getPlayer());

        /* Set the map in the upgraded building */
        upgraded.setMap(getMap());

        /* Pre-construct the upgraded building */
        upgraded.setConstructionReady();

        /* Set the position of the upgraded building so the soldiers can enter */
        upgraded.setPosition(getPosition());

        /* Replace the buildings on the map */
        getMap().replaceBuilding(upgraded, getPosition());

        /* Ensure that the new building is occupied */
        if (isOccupied()) {
            upgraded.setOccupied();
        }

        /* Move the soldiers to the new building */
        int currentMilitary = getNumberOfHostedMilitary();

        for (int i = 0; i < currentMilitary; i++) {

            /* Move one military from the old to the new building */
            Military military = retrieveMilitary();

            upgraded.promiseMilitary(military);
            military.enterBuilding(upgraded);
        }

        /* Make sure the border is updated only once */
        if (upgraded.getNumberOfHostedMilitary() == 0) {
            getMap().updateBorder(this, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
        }

        /* Move the coins to the new building */
        for (int i = 0; i < getAmount(COIN); i++) {

            /* Put one coin in the new building */
            Cargo coinCargo = new Cargo(COIN, getMap());

            upgraded.promiseDelivery(COIN);

            upgraded.putCargo(coinCargo);
        }
    }
}
