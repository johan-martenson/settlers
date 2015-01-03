/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.WoodcutterWorker.States.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.WoodcutterWorker.States.CUTTING_TREE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_BACK_TO_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_OUT_TO_CUT_TREE;
import static org.appland.settlers.model.WoodcutterWorker.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.IN_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.WoodcutterWorker.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.WoodcutterWorker.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WoodcutterWorker extends Worker {
    private final static int TIME_TO_REST = 99;
    private final static int TIME_TO_CUT_TREE = 49;
    
    private States  state;
    private final Countdown countdown;

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

    protected enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_CUT_TREE,
        CUTTING_TREE, 
        GOING_BACK_TO_HOUSE_WITH_CARGO, 
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }
    
    public WoodcutterWorker(Player player, GameMap map) {
        super(player, map);
        
        state             = WALKING_TO_TARGET;
        countdown         = new Countdown();
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
        
        countdown.countFrom(TIME_TO_REST);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point p = getTreeToCutDown();
                
                if (p == null) {
                    return;
                }

                setOffroadTarget(p);
                
                state = GOING_OUT_TO_CUT_TREE;
            } else if (getHome().isProductionEnabled()) {
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
        } else if (state == IN_HOUSE_WITH_CARGO) {
            setTarget(getHome().getFlag().getPosition());

            state = GOING_OUT_TO_PUT_CARGO;
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
        } else if (state == GOING_OUT_TO_CUT_TREE) {
            state = CUTTING_TREE;
            
            countdown.countFrom(TIME_TO_CUT_TREE);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());
                
            state = IN_HOUSE_WITH_CARGO;
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
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }
}
