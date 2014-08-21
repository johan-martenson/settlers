/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final int PRODUCTION_TIME = 49;
    private final int RESTING_TIME = 99;

    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BAKING_BREAD,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }

    State state;
    
    public Baker(GameMap m) {
        map = m;
        
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Sawmill) {
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
                    try {
                        Cargo cargo = new Cargo(BREAD, map);

                        setCargo(cargo);

                        getHome().consumeOne(WATER);
                        getHome().consumeOne(FLOUR);

                        state = GOING_TO_FLAG_WITH_CARGO;

                        setTarget(getHome().getFlag().getPosition());
                    } catch (InvalidRouteException ex) {
                        Logger.getLogger(SawmillWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    countdown.step();
                }
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            try {
                Flag f = map.getFlagAtPoint(getPosition());
                
                Storage stg = map.getClosestStorage(getPosition());
                
                Cargo cargo = getCargo();
                
                cargo.setPosition(getPosition());
                cargo.setTarget(stg);
                
                f.putCargo(getCargo());
                
                setCargo(null);
                
                state = GOING_BACK_TO_HOUSE;
                
                returnHome();
            } catch (Exception ex) {
                Logger.getLogger(SawmillWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
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
