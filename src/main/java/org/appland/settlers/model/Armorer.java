/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Armorer.State.PRODUCING_WEAPON;
import static org.appland.settlers.model.Armorer.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Armorer.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Armorer.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Armorer.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SWORD;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Armorer extends Worker {
    private final Countdown countdown;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private Material nextWeapon = SWORD;
    private State state;

    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        PRODUCING_WEAPON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }

    
    public Armorer(GameMap m) {
        map = m;
        
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }

    private Material getNextWeapon(Material current) {
        if (current == SWORD) {
            return SHIELD;
        } else {
            return SWORD;
        }
    }
    
    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Armory) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = PRODUCING_WEAPON;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == PRODUCING_WEAPON) {
            if (getHome().getAmount(IRON) > 0 && getHome().getAmount(COAL) > 0) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(nextWeapon, map);

                    setCargo(cargo);

                    getHome().consumeOne(IRON);
                    getHome().consumeOne(COAL);

                    nextWeapon = getNextWeapon(nextWeapon);

                    state = GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());
                } else {
                    countdown.step();
                }
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag f = map.getFlagAtPoint(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            f.putCargo(getCargo());

            setCargo(null);

            state = GOING_BACK_TO_HOUSE;

            returnHome();
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());
            
            state = RESTING_IN_HOUSE;
            
            countdown.countFrom(RESTING_TIME);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + state.name().toLowerCase();
    }
}
