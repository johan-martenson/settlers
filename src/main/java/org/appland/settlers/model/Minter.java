/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Minter.State.MAKING_COIN;
import static org.appland.settlers.model.Minter.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Minter.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Minter.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Minter.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Minter extends Worker {
    private final Countdown countdown;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MAKING_COIN,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }

    private State state;
    
    public Minter(GameMap m) {
        map = m;
        
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Mint) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = MAKING_COIN;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == MAKING_COIN) {
            if (getHome().getAmount(GOLD) > 0 && getHome().getAmount(COAL) > 0) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(COIN, map);

                    setCargo(cargo);

                    getHome().consumeOne(GOLD);
                    getHome().consumeOne(COAL);

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
        return "Minter " + state;
    }
}
