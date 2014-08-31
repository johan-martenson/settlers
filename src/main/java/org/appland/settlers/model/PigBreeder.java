/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.PigBreeder.States.FEEDING;
import static org.appland.settlers.model.PigBreeder.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.PigBreeder.States.GOING_BACK_TO_HOUSE_AFTER_FEEDING;
import static org.appland.settlers.model.PigBreeder.States.GOING_OUT_TO_FEED;
import static org.appland.settlers.model.PigBreeder.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.PigBreeder.States.PREPARING_PIG_FOR_DELIVERY;
import static org.appland.settlers.model.PigBreeder.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.PigBreeder.States.WALKING_TO_TARGET;

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
    
    enum States {
        WALKING_TO_TARGET, 
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_FEED, 
        FEEDING,
        GOING_BACK_TO_HOUSE_AFTER_FEEDING,
        PREPARING_PIG_FOR_DELIVERY,
        GOING_BACK_TO_HOUSE, 
        GOING_OUT_TO_PUT_CARGO,
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
            if (countdown.reachedZero()) {
                Point pointToFeedPigsAt = getHome().getPosition().downLeft();

                state = GOING_OUT_TO_FEED;
                
                setOffroadTarget(pointToFeedPigsAt);
            } else {
                countdown.step();
            }
        } else if (state == FEEDING) {
            if (countdown.reachedZero()) {
                    
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
            Storage stg = map.getClosestStorage(getPosition());

            Cargo cargo = getCargo();
                
            cargo.setPosition(getPosition());
            cargo.setTarget(stg);
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
        }
    }
}
