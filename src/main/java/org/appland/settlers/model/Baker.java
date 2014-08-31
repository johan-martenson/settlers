/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Baker.State.BAKING_BREAD;
import static org.appland.settlers.model.Baker.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Baker.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Baker.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Baker.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WATER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Baker extends Worker {
    private final Countdown countdown;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;
    
    private State state;

    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BAKING_BREAD,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }

    
    public Baker(GameMap m) {
        map = m;
        
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Bakery) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = BAKING_BREAD;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == BAKING_BREAD) {
            if (getHome().getAmount(WATER) > 0 && getHome().getAmount(FLOUR) > 0) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(BREAD, map);

                    setCargo(cargo);

                    getHome().consumeOne(WATER);
                    getHome().consumeOne(FLOUR);

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
                
            Storage stg = map.getClosestStorage(getPosition());
                
            Cargo cargo = getCargo();
                
            cargo.setPosition(getPosition());
            cargo.setTarget(stg);
                
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
        return "Baker " + state;
    }
}
