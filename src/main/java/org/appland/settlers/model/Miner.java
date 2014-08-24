/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Miner.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Miner.States.GOING_OUT_TO_FLAG;
import static org.appland.settlers.model.Miner.States.MINING_GOLD;
import static org.appland.settlers.model.Miner.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Miner.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class Miner extends Worker {

    enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MINING_GOLD,
        GOING_OUT_TO_FLAG,
        GOING_BACK_TO_HOUSE
    }
    
    private final static int RESTING_TIME = 99;
    private final static int TIME_TO_MINE = 49;
    
    private final Countdown countdown;
    
    private States state;
    
    public Miner(GameMap map) {
        super(map);
        
        countdown = new Countdown();
        
        state = WALKING_TO_TARGET;
    }

    public boolean isMining() {
        return state == MINING_GOLD;
    }
    
    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof GoldMine) {
            setHome(b);
        }
        
        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(RESTING_TIME);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                if (hasFood()) {
                    state = MINING_GOLD;
                    countdown.countFrom(TIME_TO_MINE);
                }
            } else {
                countdown.step();
            }
        } else if (state == MINING_GOLD) {
            if (countdown.reachedZero()) {
                Cargo cargo = map.mineGoldAtPoint(getPosition());
                
                setCargo(cargo);
                
                setTarget(getHome().getFlag().getPosition());
                
                state = GOING_OUT_TO_FLAG;
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_OUT_TO_FLAG) {
            Storage stg = map.getClosestStorage(getPosition());
            
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.setTarget(stg);
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());
            
            state = RESTING_IN_HOUSE;
            
            countdown.countFrom(RESTING_TIME);
        }
    }
    
    private boolean hasFood() {
        Building home = getHome();
        
        return home.getAmount(BREAD) > 0 || home.getAmount(FISH) > 0;
    }
}
