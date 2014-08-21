/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Fisherman.States.FISHING;
import static org.appland.settlers.model.Fisherman.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Fisherman.States.GOING_BACK_TO_HOUSE_WITH_FISH;
import static org.appland.settlers.model.Fisherman.States.GOING_OUT_TO_FISH;
import static org.appland.settlers.model.Fisherman.States.GOING_TO_FLAG;
import static org.appland.settlers.model.Fisherman.States.IN_HOUSE_WITH_FISH;
import static org.appland.settlers.model.Fisherman.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Fisherman.States.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.FISH;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Fisherman extends Worker {
    private static final int TIME_TO_FISH = 19;
    private static final int TIME_TO_REST = 99;
    
    private final Countdown countdown;

    private States state;

    private Point getFishingSpot() {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), 4);
        
        for (Point p : adjacentPoints) {
            if (map.isBuildingAtPoint(p)) {
                continue;
            }
                        
            if (map.isStoneAtPoint(p)) {
                continue;
            }

            if (!map.isNextToWater(p)) {
                continue;
            }
            
            return p;
        }

        return null;
    }
    
    enum States {
        WALKING_TO_TARGET, 
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_FISH, 
        FISHING, 
        GOING_BACK_TO_HOUSE_WITH_FISH,
        IN_HOUSE_WITH_FISH,
        GOING_TO_FLAG,
        GOING_BACK_TO_HOUSE
    }
    
    public Fisherman(GameMap map) {
        super(map);
        
        state = WALKING_TO_TARGET;
        
        countdown = new Countdown();
    }

    public boolean isFishing() {
        return state == FISHING;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Fishery) {
            setHome(b);
        }
        
        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(TIME_TO_REST);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                Point p = getFishingSpot();

                if (p == null) {
                    return;
                }
                
                setOffroadTarget(p);

                state = GOING_OUT_TO_FISH;
            } else {
                countdown.step();
            }
        } else if (state == FISHING) {
            if (countdown.reachedZero()) {
                
                Cargo cargo = new Cargo(FISH, map);
                
                setCargo(cargo);
                
                state = GOING_BACK_TO_HOUSE_WITH_FISH;
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_FISH) {
            state = GOING_TO_FLAG;
            
            setTarget(getHome().getFlag().getPosition());
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_OUT_TO_FISH) {
            state = FISHING;
            
            countdown.countFrom(TIME_TO_FISH);
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            enterBuilding(getHome());
            
            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_FISH) {
            enterBuilding(getHome());

            state = IN_HOUSE_WITH_FISH;            
        } else if (state == GOING_TO_FLAG) {
            Storage stg = map.getClosestStorage(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.setTarget(stg);
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            setTarget(getHome().getPosition());

            state = GOING_BACK_TO_HOUSE;
        }
    }
}
