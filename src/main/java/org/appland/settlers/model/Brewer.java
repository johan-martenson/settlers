/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Brewer.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Brewer.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Brewer.State.BREWING_BEER;
import static org.appland.settlers.model.Brewer.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Brewer.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Brewer extends Worker {
    private final Countdown countdown;
    private final int PRODUCTION_TIME = 49;
    private final int RESTING_TIME = 99;

    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        BREWING_BEER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE
    }

    State state;
    
    public Brewer(GameMap m) {
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
                state = BREWING_BEER;
                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == BREWING_BEER) {
            if (getHome().getAmount(WATER) > 0 && getHome().getAmount(WHEAT) > 0) {
                if (countdown.reachedZero()) {
                    try {
                        Cargo cargo = new Cargo(BEER, map);

                        setCargo(cargo);

                        getHome().consumeOne(WATER);
                        getHome().consumeOne(WHEAT);

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
}
