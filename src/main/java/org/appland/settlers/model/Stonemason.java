/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Stonemason.States.GETTING_STONE;
import static org.appland.settlers.model.Stonemason.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Stonemason.States.GOING_BACK_TO_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.Stonemason.States.GOING_OUT_TO_GET_STONE;
import static org.appland.settlers.model.Stonemason.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.Stonemason.States.IN_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.Stonemason.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Stonemason.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Stonemason extends Worker {
    private States state;
    private final Countdown countdown;
    private Point stoneTarget;
    
    enum States {
        WALKING_TO_TARGET, 
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_GET_STONE, 
        GETTING_STONE, 
        GOING_BACK_TO_HOUSE_WITH_CARGO, 
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE
    }
    
    public Stonemason() {
        this(null);
    }
    
    public Stonemason(GameMap map) {
        super(map);
        
        state = WALKING_TO_TARGET;
        
        countdown = new Countdown();
        stoneTarget = null;
    }

    public boolean isGettingStone() {
        return state == GETTING_STONE;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Quarry) {
            setHome(b);
        }
        
        state = RESTING_IN_HOUSE;

        countdown.countFrom(99);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                Point accessPoint = null;
                double tempDistance;
                double distance = Integer.MAX_VALUE;
                Point homePoint = getHome().getPosition();
                
                for (Point p : map.getPointsWithinRadius(homePoint, 4)) {
                    if (!map.isStoneAtPoint(p)) {
                        continue;
                    }

                    if (p.equals(homePoint)) {
                        continue;
                    }
                    
                    Collection<Point> homePointList = new LinkedList<>();
                    homePointList.add(homePoint);
                    List<Point> pathToStone = map.findWayOffroad(getHome().getFlag().getPosition(), p, homePointList);

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

                state = GOING_OUT_TO_GET_STONE;
            } else {
                countdown.step();
            }
        } else if (state == GETTING_STONE) {
            if (countdown.reachedZero()) {
                map.removePartOfStone(stoneTarget);
                
                setCargo(new Cargo(STONE, map));
                
                state = GOING_BACK_TO_HOUSE_WITH_CARGO;
                
                stoneTarget = null;
                
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_CARGO) {
            try {
                setTarget(getHome().getFlag().getPosition());

                state = GOING_OUT_TO_PUT_CARGO;
            } catch (InvalidRouteException ex) {
                Logger.getLogger(WoodcutterWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void onArrival() throws Exception {
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
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(99);
        } else if (state == GOING_OUT_TO_GET_STONE) {
            state = GETTING_STONE;
            
            countdown.countFrom(49);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());
            
            state = IN_HOUSE_WITH_CARGO;
        } 
    } 
}
