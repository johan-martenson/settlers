/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.List;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Stonemason.States.GETTING_STONE;
import static org.appland.settlers.model.Stonemason.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Stonemason.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Stonemason extends Worker {
    private States state;
    private final Countdown countdown;
    private Building hut;
    private Point stoneTarget;
    
    enum States {
        WALKING_TO_TARGET, RESTING_IN_HOUSE, GOING_OUT_TO_GET_STONE, GETTING_STONE, GOING_BACK_TO_HOUSE
    }
    
    public Stonemason() {
        this(null);
    }
    
    public Stonemason(GameMap map) {
        super(map);
        
        state = WALKING_TO_TARGET;
        
        countdown = new Countdown();
        hut = null;
        stoneTarget = null;
    }

    public boolean isGettingStone() {
        return state == GETTING_STONE;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        hut = b;
        
        state = States.RESTING_IN_HOUSE;

        countdown.countFrom(99);
    }

    @Override
    protected void onIdle() {
        if (state == States.RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                Point accessPoint = null;
                double tempDistance;
                double distance = Integer.MAX_VALUE;
                
                for (Point p : map.getPointsWithinRadius(hut.getPosition(), 4)) {
                    if (!map.isStoneAtPoint(p)) {
                        continue;
                    }

                    List<Point> pathToStone = map.findWayOffroad(hut.getFlag().getPosition(), p, null);

                    if (pathToStone == null) {
                        continue;
                    }

                    tempDistance = map.getDistanceForPath(pathToStone);

                    if (tempDistance < distance) {
                        distance = tempDistance;
                        accessPoint = pathToStone.get(pathToStone.size() - 2);
                        stoneTarget = p;
                    }
                }

                if (accessPoint == null) {
                    return;
                }
                
                setOffroadTarget(accessPoint);
                
                state = States.GOING_OUT_TO_GET_STONE;
            } else {
                countdown.step();
            }
        } else if (state == GETTING_STONE) {
            if (countdown.reachedZero()) {
                map.removePartOfStone(stoneTarget);
                
                setCargo(new Cargo(STONE, map));
                
                state = GOING_BACK_TO_HOUSE;
                
                stoneTarget = null;
                
                setOffroadTarget(hut.getFlag().getPosition());
            } else {
                countdown.step();
            }
        } else if (state == States.GOING_OUT_TO_GET_STONE) {
            state = GETTING_STONE;
            
            countdown.countFrom(49);
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = States.RESTING_IN_HOUSE;
            
            if (getCargo() != null) {
                hut.putProducedCargoForDelivery(getCargo());
                setCargo(null);
            }
    
            enterBuilding(hut);
            
            countdown.countFrom(99);
        }
    }
}
