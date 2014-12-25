/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HALFWAY;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
public class Crop implements Actor, Piece {

    public enum GrowthState {
        JUST_PLANTED, HALFWAY, FULL_GROWN, HARVESTED
    }
    
    private final static int TIME_TO_GROW = 199;
    
    private GrowthState state;
    private final Countdown growthCountdown;
    private final Point position;

    
    public Crop(Point point) {
        position = point;
        state = JUST_PLANTED;
        growthCountdown = new Countdown();
        growthCountdown.countFrom(TIME_TO_GROW);
    }

    @Override
    public void stepTime() {
        if (state == FULL_GROWN) {
            return;
        }
        
        if (growthCountdown.reachedZero()) {
            if (state == JUST_PLANTED) {
                state = HALFWAY;
                
                growthCountdown.countFrom(TIME_TO_GROW);
            } else if (state == HALFWAY) {
                state = FULL_GROWN;
            }
        } else {
            growthCountdown.step();
        }
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public GrowthState getGrowthState() {
        return state;
    }

    public Cargo harvest() {
        state = GrowthState.HARVESTED;
        
        return new Cargo(WHEAT, null);
    }
}
