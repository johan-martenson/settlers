/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Forester.States.GOING_OUT_TO_PLANT;
import static org.appland.settlers.model.Forester.States.PLANTING;
import static org.appland.settlers.model.Forester.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Forester.States.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Forester.States.WALKING_TO_TARGET;

/* WALKING_TO_TARGET -> RESTING_IN_HOUSE -> GOING_OUT_TO_PLANT -> PLANTING -> GOING_BACK_TO_HOUSE -> RESTING_IN_HOUSE  */

@Walker(speed = 10)
public class Forester extends Worker {
    private static final int TIME_TO_PLANT = 19;
    private static final int TIME_TO_REST = 99;
    
    private final Countdown countdown;
    private States state;

    private Point getTreeSpot() {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), 4);
        
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
    
    protected enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_PLANT,
        PLANTING,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }
    
    public Forester(Player player, GameMap map) {
        super(player, map);
        
        state = WALKING_TO_TARGET;
        
        countdown = new Countdown();
    }

    public boolean isPlanting() {
        return state == PLANTING;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof ForesterHut) {
            setHome(b);
        }
        
        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(TIME_TO_REST);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point p = getTreeSpot();

                if (p == null) {
                    return;
                }
                
                setOffroadTarget(p);

                state = GOING_OUT_TO_PLANT;
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
                
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_OUT_TO_PLANT) {
            state = PLANTING;
            
            countdown.countFrom(TIME_TO_PLANT);
        } else if (state == States.GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            enterBuilding(getHome());
            
            countdown.countFrom(TIME_TO_REST);
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());
        
            storage.depositWorker(this);
        }

    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());
    
        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            
            setTarget(storage.getPosition());
        } else {
            for (Building b : map.getBuildings()) {
                if (b instanceof Storage) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }
}
