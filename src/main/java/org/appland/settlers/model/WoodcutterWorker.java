/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.WoodcutterWorker.States.CUTTING_TREE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_BACK_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_BACK_TO_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_OUT_TO_CUT_TREE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.IN_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.RESTING_IN_HOUSE;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WoodcutterWorker extends Worker {
    private States    state;
    private Countdown countdown;

    WoodcutterWorker() {
        this(null);
    }

    private Point getTreeToCutDown() {
        Iterable<Point> adjacentPoints = map.getPointsWithinRadius(getHome().getPosition(), 4);

        for (Point p : adjacentPoints) {
            if (!map.isTreeAtPoint(p)) {
                continue;
            }

            Tree tree = map.getTreeAtPoint(p);
            if (tree.getSize() != LARGE) {
                continue;
            }

            if (map.findWayOffroad(p, getHome().getFlag().getPosition(), null) != null) {
                return p;
            }

        }

        return null;
    }

    enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_CUT_TREE,
        CUTTING_TREE, 
        GOING_BACK_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE_WITH_CARGO, 
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE 
    }
    
    public WoodcutterWorker(GameMap map) {
        super(map);
        
        state = States.WALKING_TO_TARGET;
        countdown = new Countdown();
    }
    
    public boolean isCuttingTree() {
        return state == CUTTING_TREE;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Woodcutter) {
            setHome(b);
        }
        
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
                
                setCargo(new Cargo(WOOD, map));
                
                state = GOING_BACK_TO_HOUSE_WITH_CARGO;
                
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == GOING_OUT_TO_CUT_TREE) {
            state = CUTTING_TREE;
            
            countdown.countFrom(49);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());
                
            state = IN_HOUSE_WITH_CARGO;
        } else if (state == IN_HOUSE_WITH_CARGO) {
            try {
                setTarget(getHome().getFlag().getPosition());

                state = GOING_OUT_TO_PUT_CARGO;
            } catch (InvalidRouteException ex) {
                Logger.getLogger(WoodcutterWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            enterBuilding(getHome());
            
            countdown.countFrom(99);
        }
    }

    @Override
    public void onArrival() {
        if (state == GOING_OUT_TO_PUT_CARGO) {
            try {
                Storage stg = map.getClosestStorage(getPosition());

                Cargo cargo = getCargo();
                
                cargo.setPosition(getPosition());
                cargo.setTarget(stg);
                getHome().getFlag().putCargo(cargo);
                                
                setCargo(null);
                
                setTarget(getHome().getPosition());
                
                state = GOING_BACK_TO_HOUSE;
            } catch (Exception ex) {
                Logger.getLogger(WoodcutterWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (state == GOING_BACK_TO_FLAG_WITH_CARGO) {
            try {
                setTarget(getHome().getPosition());
                
                state = GOING_BACK_TO_HOUSE_WITH_CARGO;
            } catch (InvalidRouteException ex) {
                Logger.getLogger(WoodcutterWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
