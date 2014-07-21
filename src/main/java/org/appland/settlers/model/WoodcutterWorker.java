/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.WoodcutterWorker.States.CUTTING_TREE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_OUT_TO_CUT_TREE;
import static org.appland.settlers.model.WoodcutterWorker.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.WoodcutterWorker.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WoodcutterWorker extends Worker {
    private States    state;
    private Countdown countdown;
    private Building  hut;
    private Cargo     woodCargo;

    WoodcutterWorker() {
        this(null);
    }

    private Point getTreeToCutDown() {
        Point[] adjacentPoints = getPosition().getAdjacentPoints();

        for (Point p : adjacentPoints) {
            if (!map.isTreeAtPoint(p)) {
                continue;
            }

            Tree tree = map.getTreeAtPoint(p);
            if (tree.getSize() != LARGE) {
                continue;
            }
            
            if (map.findWayOffroad(p, getPosition(), null) != null) {
                return p;
            }
        }

        return null;
    }
    
    enum States {
        WALKING_TO_TARGET, RESTING_IN_HOUSE, GOING_OUT_TO_CUT_TREE, CUTTING_TREE, GOING_BACK_TO_HOUSE
    }
    
    public WoodcutterWorker(GameMap map) {
        super(map);
        
        state = WALKING_TO_TARGET;
        countdown = new Countdown();
        hut = null;
        woodCargo = null;
    }
    
    public boolean isCuttingTree() {
        return state == CUTTING_TREE;
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
                Point p = getTreeToCutDown();
                
                if (p == null) {
                    return;
                }
                
                setOffroadTarget(p);
                
                state = GOING_OUT_TO_CUT_TREE;
            } else {
                countdown.step();
            }
        } else if (state == CUTTING_TREE) {
            if (countdown.reachedZero()) {
                map.removeTree(getPosition());
                
                woodCargo = new Cargo(WOOD);
                
                state = States.GOING_BACK_TO_HOUSE;
                
                setOffroadTarget(hut.getFlag().getPosition());
            } else {
                countdown.step();
            }
        } else if (state == GOING_OUT_TO_CUT_TREE) {
            state = CUTTING_TREE;
            
            countdown.countFrom(49);
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            if (woodCargo != null) {
                hut.putProducedCargoForDelivery(woodCargo);
                woodCargo = null;
            }
    
            enterBuilding(hut);
            
            countdown.countFrom(99);
        }
    }
}
