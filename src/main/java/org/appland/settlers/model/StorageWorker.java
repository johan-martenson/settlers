/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.StorageWorker.State.DELIVERING_CARGO_TO_FLAG;
import static org.appland.settlers.model.StorageWorker.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.StorageWorker.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.StorageWorker.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class StorageWorker extends Worker {

    private final static int RESTING_TIME = 19;
    
    private final Countdown countdown;
    
    private State state;
    private Storage ownStorage;


    enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DELIVERING_CARGO_TO_FLAG,
        GOING_BACK_TO_HOUSE,
    }
    
    public StorageWorker(GameMap m) {
        super(m);

        state = WALKING_TO_TARGET;
        
        countdown = new Countdown();
        
        countdown.countFrom(RESTING_TIME);
    }
    
    private Cargo tryToStartDelivery() throws Exception {
        for (Material m : Material.values()) {
            for (Building b : map.getBuildingsWithinReach(ownStorage.getFlag())) {

                /* Don't deliver to itself */
                if (ownStorage.equals(b)) {
                    continue;
                }

                if (b.needsMaterial(m) && ownStorage.isInStock(m)) {
                    b.promiseDelivery(m);

                    Cargo cargo = ownStorage.retrieve(m);
                    cargo.setTarget(b);

                    return cargo;
                }
            }
        }

        return null;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Storage) {
            setHome(b);
            
            ownStorage = (Storage)b;
        }
    
        state = RESTING_IN_HOUSE;
    }
    
    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                try {
                    Cargo cargo = tryToStartDelivery();
                    
                    if (cargo != null) {
                        try {
                            setCargo(cargo);

                            setTarget(getHome().getFlag().getPosition());

                            state = DELIVERING_CARGO_TO_FLAG;
                        } catch (InvalidRouteException ex) {
                            Logger.getLogger(StorageWorker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(StorageWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == DELIVERING_CARGO_TO_FLAG) {
            try {
                Flag f = getHome().getFlag();
                
                f.putCargo(getCargo());
                
                setCargo(null);
                
                returnHome();
                
                state = GOING_BACK_TO_HOUSE;
            } catch (InvalidRouteException ex) {
                Logger.getLogger(StorageWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());
            
            state = RESTING_IN_HOUSE;
            
            countdown.countFrom(RESTING_TIME);
        }
    }
}
