/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.WellWorker.States.DRAWING_WATER;
import static org.appland.settlers.model.WellWorker.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.WellWorker.States.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.WellWorker.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.WellWorker.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WellWorker extends Worker {
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;
    
    private final Countdown countdown;

    private States state;

    public WellWorker(GameMap m) {
        super(m);
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }
    
    enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DRAWING_WATER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }
    
    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Well) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = DRAWING_WATER;
                
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == DRAWING_WATER) {
            if (countdown.reachedZero()) {
                Cargo cargo = new Cargo(WATER, map);
                    
                setCargo(cargo);
                    
                setTarget(getHome().getFlag().getPosition());
                    
                state = GOING_TO_FLAG_WITH_CARGO;
            } else {
                countdown.step();
            }
        }
    }
    
    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag f = getHome().getFlag();
            Storage stg = map.getClosestStorage(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.setTarget(stg);

            f.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());
                
            state = RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        }
    }
}
