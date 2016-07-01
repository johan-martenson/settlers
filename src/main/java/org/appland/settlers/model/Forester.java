/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/* WALKING_TO_TARGET -> RESTING_IN_HOUSE -> GOING_OUT_TO_PLANT -> PLANTING -> GOING_BACK_TO_HOUSE -> RESTING_IN_HOUSE  */

@Walker(speed = 10)
public class Forester extends Worker {
    private static final int TIME_TO_PLANT = 19;
    private static final int TIME_TO_REST = 99;
    private static final int RANGE = 8;
    
    private final Countdown countdown;
    private State state;

    private Point getTreeSpot() throws Exception {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), RANGE);
        
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

            if (map.getTerrain().isOnMountain(p)) {
                continue;
            }

            if (map.getTerrain().isInWater(p)) {
                continue;
            }

            if (map.findWayOffroad(
                    getHome().getFlag().getPosition(), 
                    p,
                    null) == null) {
                continue;
            }

            return p;
        }

        return null;
    }

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_PLANT,
        PLANTING,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }
    
    public Forester(Player player, GameMap map) {
        super(player, map);
        
        state = State.WALKING_TO_TARGET;
        
        countdown = new Countdown();
    }

    public boolean isPlanting() {
        return state == State.PLANTING;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof ForesterHut) {
            setHome(b);
        }
        
        state = State.RESTING_IN_HOUSE;
        
        countdown.countFrom(TIME_TO_REST);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point p = getTreeSpot();

                if (p == null) {
                    return;
                }

                setOffroadTarget(p);

                state = State.GOING_OUT_TO_PLANT;
            } else {
                countdown.step();
            }
        } else if (state == State.PLANTING) {
            if (countdown.reachedZero()) {
                map.placeTree(getPosition());

                state = State.GOING_BACK_TO_HOUSE;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == State.GOING_OUT_TO_PLANT) {
            state = State.PLANTING;
            
            countdown.countFrom(TIME_TO_PLANT);
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            state = State.RESTING_IN_HOUSE;
            
            enterBuilding(getHome());
            
            countdown.countFrom(TIME_TO_REST);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());
        
            storage.depositWorker(this);
        }

    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());
    
        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            
            setTarget(storage.getPosition());
        } else {
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage) {
                    state = State.RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }
}
