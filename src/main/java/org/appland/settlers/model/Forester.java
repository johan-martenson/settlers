/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Forester.States.PLANTING;
import static org.appland.settlers.model.Forester.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Forester.States.WALKING_TO_TARGET;

/* WALKING_TO_TARGET -> RESTING_IN_HOUSE -> GOING_OUT_TO_PLANT -> PLANTING -> GOING_BACK_TO_HOUSE -> RESTING_IN_HOUSE  */

@Walker(speed = 10)
public class Forester extends Worker {
    private States state;
    private Countdown countdown;
    private Building hut;

    private Point getTreeSpot() {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(hut.getPosition(), 4);
        
        for (Point p : adjacentPoints) {
            if (map.isBuildingAtPoint(p)) {
                continue;
            }
            
            if (map.isFlagAtPoint(p)) {
                continue;
            }
            
            if (map.isRoadAtPoint(p)) {
                continue;
            }

            if (map.isTreeAtPoint(p)) {
                continue;
            }
            
            if (map.isStoneAtPoint(p)) {
                continue;
            }

            return p;
        }

        return null;
    }
    
    enum States {
        WALKING_TO_TARGET, RESTING_IN_HOUSE, GOING_OUT_TO_PLANT, PLANTING, GOING_BACK_TO_HOUSE
    }
    
    public Forester() {
        this(null);
    }
    
    public Forester(GameMap map) {
        super(map);
        
        state = WALKING_TO_TARGET;
        
        countdown = new Countdown();
        
        hut = null;
    }

    public boolean isPlanting() {
        return state == PLANTING;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        hut = b;
        
        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(99);
    }
    
    @Override
    protected void onIdle() {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                Point p = getTreeSpot();

                if (p == null) {
                    return;
                }
                
                setOffroadTarget(p);

                state = States.GOING_OUT_TO_PLANT;
            } else {
                countdown.step();
            }
        } else if (state == PLANTING) {
            if (countdown.reachedZero()) {
                try {
                    map.placeTree(getPosition());
                } catch (Exception ex) {
                    Logger.getLogger(Forester.class.getName()).log(Level.SEVERE, null, ex);
                }
                state = States.GOING_BACK_TO_HOUSE;
                
                setOffroadTarget(hut.getFlag().getPosition());
            } else {
                countdown.step();
            }
        } else if (state == States.GOING_OUT_TO_PLANT) {
            state = PLANTING;
            
            countdown.countFrom(19);
        } else if (state == States.GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            countdown.countFrom(99);
        }

    }

    @Override
    protected void onArrival() {
    }
}
