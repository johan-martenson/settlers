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
import static org.appland.settlers.model.Armorer.State.RETURNING_TO_STORAGE;
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

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        PRODUCING_WEAPON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
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
            if (getHome().getAmount(IRON) > 0 && getHome().getAmount(COAL) > 0 && getHome().isProductionEnabled()) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(nextWeapon, map);

                    setCargo(cargo);

                    getHome().consumeOne(IRON);
                    getHome().consumeOne(COAL);

                    nextWeapon = getNextWeapon(nextWeapon);

                    state = GOING_TO_FLAG_WITH_CARGO;

                    setTarget(getHome().getFlag().getPosition());
                } else if (getHome().isProductionEnabled()) {
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
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());
        
            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());
    
        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            
            setTarget(storage.getPosition());
        } else {
            for (Building b : map.getBuildings()) {
                if (b instanceof Storage) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + state.name().toLowerCase();
    }
}
