/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Miller.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Miller.States.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Miller.States.GRINDING_WHEAT;
import static org.appland.settlers.model.Miller.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Miller.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Miller extends Worker {
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME = 99;
    
    private final Countdown countdown;

    private States state;

    public Miller(GameMap m) {
        super(m);
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }
    
    enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GRINDING_WHEAT,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }
    
    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Mill) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = GRINDING_WHEAT;
                
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == GRINDING_WHEAT) {
            if (getHome().getAmount(WHEAT) > 0) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(FLOUR, map);

                    getHome().consumeOne(WHEAT);
                        
                    setCargo(cargo);

                    setTarget(getHome().getFlag().getPosition());

                    state = GOING_TO_FLAG_WITH_CARGO;
                } else {
                    countdown.step();
                }
            }
        }
    }
    
    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag f = getHome().getFlag();
                
            Cargo cargo = getCargo();
                
            cargo.setPosition(getPosition());
            cargo.transportToStorage();

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
