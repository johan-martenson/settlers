/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.PigBreeder.States.FEEDING;
import static org.appland.settlers.model.PigBreeder.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.PigBreeder.States.GOING_BACK_TO_HOUSE_AFTER_FEEDING;
import static org.appland.settlers.model.PigBreeder.States.GOING_OUT_TO_FEED;
import static org.appland.settlers.model.PigBreeder.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.PigBreeder.States.PREPARING_PIG_FOR_DELIVERY;
import static org.appland.settlers.model.PigBreeder.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.PigBreeder.States.WALKING_TO_TARGET;
import static org.appland.settlers.model.PigBreeder.States.RETURNING_TO_STORAGE;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class PigBreeder extends Worker {

    private static final int TIME_TO_REST        = 99;
    private static final int TIME_TO_FEED        = 19;
    private static final int TIME_TO_PREPARE_PIG = 19;
    
    private States state;
    private final Countdown countdown;
    
    protected enum States {
        WALKING_TO_TARGET, 
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_FEED, 
        FEEDING,
        GOING_BACK_TO_HOUSE_AFTER_FEEDING,
        PREPARING_PIG_FOR_DELIVERY,
        GOING_BACK_TO_HOUSE, 
        GOING_OUT_TO_PUT_CARGO,
        RETURNING_TO_STORAGE
    }
    
    public PigBreeder(GameMap map) {
        super(map);

        state = WALKING_TO_TARGET;
        countdown = new Countdown();
    }

    public boolean isFeeding() {
        return state == FEEDING;
    }
    
    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Storage) {
            return;
        } else if (b instanceof PigFarm) {
            setHome(b);
        }
        
        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(TIME_TO_REST);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {
                if (getHome().getAmount(WATER) > 0 && getHome().getAmount(WHEAT) > 0) {
                    Point pointToFeedPigsAt = getHome().getPosition().downLeft();

                    state = GOING_OUT_TO_FEED;

                    setOffroadTarget(pointToFeedPigsAt);
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == FEEDING) {
            if (countdown.reachedZero()) {
                    
                getHome().consumeOne(WATER);
                getHome().consumeOne(WHEAT);
                
                state = GOING_BACK_TO_HOUSE_AFTER_FEEDING;
                    
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == PREPARING_PIG_FOR_DELIVERY) {
            if (countdown.reachedZero()) {
                Cargo cargo = new Cargo(PIG, map);
                
                setCargo(cargo);
                
                state = GOING_OUT_TO_PUT_CARGO;
                
                setTarget(getHome().getFlag().getPosition());
            } else {
                countdown.step();
            }
        }
    }

    @Override
    public void onArrival() throws Exception {
        if (state == GOING_OUT_TO_PUT_CARGO) {
            Cargo cargo = getCargo();
                
            cargo.setPosition(getPosition());
            cargo.transportToStorage();
            getHome().getFlag().putCargo(cargo);
                                
            setCargo(null);
                
            setTarget(getHome().getPosition());
                
            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            enterBuilding(getHome());
            
            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_BACK_TO_HOUSE_AFTER_FEEDING) {
            enterBuilding(getHome());
            
            state = PREPARING_PIG_FOR_DELIVERY;
            
            countdown.countFrom(TIME_TO_PREPARE_PIG);
        } else if (state == GOING_OUT_TO_FEED) {
            countdown.countFrom(TIME_TO_FEED);
            
            state = FEEDING;
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());
        
            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition(), getHome());
    
        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            
            setTarget(storage.getPosition());
        } else {
            for (Building b : map.getBuildings()) {
                if (b instanceof Storage && !b.equals(getHome())) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }
}
