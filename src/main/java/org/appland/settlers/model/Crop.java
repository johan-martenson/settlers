/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HALFWAY;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;

/**
 *
 * @author johan
 */
public class Crop implements Actor {
    private GrowthState state;
    private Countdown growthCountdown;

    public enum GrowthState {
        JUST_PLANTED, HALFWAY, FULL_GROWN
    }
    
    private Point position;

    public Crop(Point point) {
        position = point;
        state = JUST_PLANTED;
        growthCountdown = new Countdown();
        growthCountdown.countFrom(99);
    }

    @Override
    public void stepTime() {
        if (state == FULL_GROWN) {
            return;
        }
        
        if (growthCountdown.reachedZero()) {
            if (state == JUST_PLANTED) {
                state = HALFWAY;
                
                growthCountdown.countFrom(99);
            } else if (state == HALFWAY) {
                state = FULL_GROWN;
            }
        } else {
            growthCountdown.step();
        }
    }

    public Point getPosition() {
        return position;
    }

    public GrowthState getGrowthState() {
        return state;
    }
}
