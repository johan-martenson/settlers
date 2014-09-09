/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.IronFounder.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.IronFounder.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.IronFounder.State.MELTING_IRON;
import static org.appland.settlers.model.IronFounder.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.IronFounder.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class IronFounder extends Worker {
    private final Countdown countdown;
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MELTING_IRON,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }

    State state;
    
    public IronFounder(GameMap m) {
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
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE) {            
            if (countdown.reachedZero()) {
                state = MELTING_IRON;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == MELTING_IRON) {
            if (getHome().getAmount(COAL) > 0 && getHome().getAmount(IRON) > 0) {
                if (countdown.reachedZero()) {
                    try {
                        Cargo cargo = new Cargo(IRON_BAR, map);

                        setCargo(cargo);

                        getHome().consumeOne(COAL);
                        getHome().consumeOne(IRON);

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
                
                Cargo cargo = getCargo();
                
                cargo.setPosition(getPosition());
                cargo.transportToStorage();
                
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
        return "Iron founder " + state;
    }

}
