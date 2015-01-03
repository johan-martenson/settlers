/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Collections;
import java.util.List;
import static org.appland.settlers.model.Geologist.State.GOING_TO_NEXT_SITE;
import static org.appland.settlers.model.Geologist.State.INVESTIGATING;
import static org.appland.settlers.model.Geologist.State.RETURNING_TO_FLAG;
import static org.appland.settlers.model.Geologist.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Geologist.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Geologist extends Worker {

    protected enum State {
        WALKING_TO_TARGET,
        GOING_TO_NEXT_SITE,
        INVESTIGATING,
        GOING_BACK_TO_FLAG,
        RETURNING_TO_FLAG,
        RETURNING_TO_STORAGE
    }
    
    private final static int TIME_TO_INVESTIGATE   = 19;
    private final static int RADIUS_TO_INVESTIGATE = 3;
    
    private final Countdown countdown;
    
    private State state;
    private int   nrSitesInvestigated;
    private Point flagPoint;
    private boolean searchFlip;
    
    public Geologist(GameMap m) {
        super(m);
    
        countdown           = new Countdown();
        nrSitesInvestigated = 0;

        state = WALKING_TO_TARGET;
        
        searchFlip = true;
    }

    public boolean isInvestigating() {
        return state == INVESTIGATING;
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == INVESTIGATING) {
            if (countdown.reachedZero()) {
                placeSignWithResult(getPosition());
                
                nrSitesInvestigated++;
                
                /* Return after investigating five sites */
                if (nrSitesInvestigated == 5) {
                    state = RETURNING_TO_FLAG;
                    
                    setOffroadTarget(flagPoint);
                    
                    return;
                }
                
                Point nextSite = findSiteToExamine();
                
                if (nextSite == null) {
                    state = RETURNING_TO_STORAGE;
                
                    setTarget(map.getClosestStorage(getPosition()).getPosition(), flagPoint);
                } else {
                    state = GOING_TO_NEXT_SITE;
                    
                    setOffroadTarget(nextSite);
                }
            } else {
                countdown.step();
            }
        }
    }
    
    @Override
    protected void onArrival() throws Exception {
        if (state == WALKING_TO_TARGET) {
            flagPoint = getPosition();
            
            Point point = findSiteToExamine();
            
            if (point == null) {
                state = RETURNING_TO_STORAGE;
                
                setTarget(map.getClosestStorage(flagPoint).getPosition(), flagPoint);
            } else {            
                state = GOING_TO_NEXT_SITE;
            
                setOffroadTarget(point);
            }
        } else if (state == GOING_TO_NEXT_SITE) {
            state = INVESTIGATING;
            
            countdown.countFrom(TIME_TO_INVESTIGATE);
        } else if (state == RETURNING_TO_FLAG) {
            state = RETURNING_TO_STORAGE;
            
            setTarget(map.getClosestStorage(flagPoint).getPosition());
        } else if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());
            
            storage.putCargo(new Cargo(GEOLOGIST, map));
            
            enterBuilding(storage);
        }
    }

    private void placeSignWithResult(Point point) throws Exception {
        Terrain terrain = map.getTerrain();
        boolean placedSign = false;

        if (terrain.isOnGrass(point)) {
            map.placeSign(WATER, LARGE, point);
            placedSign = true;
        } else if (terrain.isOnMountain(point)) {
            for (Material mineral: Material.getMinerals()) {
                int amount = map.getAmountOfMineralAtPoint(mineral, point);

                if (amount > 10) {
                    map.placeSign(mineral, LARGE, point);
                    placedSign = true;
                    break;
                } else if (amount > 5) {
                    map.placeSign(mineral, MEDIUM, point);
                    placedSign = true;
                    break;
                } else if (amount > 0) {
                    map.placeSign(mineral, SMALL, point);
                    placedSign = true;
                    break;
                }
            }
        } 
        
        if (!placedSign) {
            map.placeEmptySign(point);
        }
    }

    private Point findSiteToExamine() {
        List<Point> points = map.getPointsWithinRadius(getPosition(), RADIUS_TO_INVESTIGATE);
        
        if (searchFlip) {
            Collections.reverse(points);
        }
        
        searchFlip = !searchFlip;
        
        for (Point p : points) {
            if (p.equals(getPosition())) {
                continue;
            }
            
            if (map.isSignAtPoint(p)) {
                continue;
            }
        
            if (map.isTreeAtPoint(p)) {
                continue;
            }
            
            if (map.isStoneAtPoint(p)) {
                continue;
            }
            
            if (map.isFlagAtPoint(p)) {
                continue;
            }
            
            if (map.findWayOffroad(getPosition(), p, null) == null) {
                continue;
            }
            
            return p;
        }

        return null;
    }
}
