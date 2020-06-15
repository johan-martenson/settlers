/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HALFWAY;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
public class Crop implements Actor {

    public enum GrowthState {
        JUST_PLANTED, HALFWAY, FULL_GROWN, HARVESTED
    }

    private final static int TIME_TO_GROW = 199;
    private final static int TIME_TO_WITHER = 199;

    private GrowthState state;
    private final Countdown growthCountdown;
    private final Point     position;
    private final GameMap   map;

    public Crop(Point point, GameMap map) {
        position = point;
        state    = JUST_PLANTED;
        this.map = map;

        growthCountdown = new Countdown();
        growthCountdown.countFrom(TIME_TO_GROW);
    }

    @Override
    public void stepTime() {
        if (state == FULL_GROWN) {
            return;
        }

        if (growthCountdown.hasReachedZero()) {
            if (state == JUST_PLANTED) {
                state = HALFWAY;

                growthCountdown.countFrom(TIME_TO_GROW);
            } else if (state == HALFWAY) {
                state = FULL_GROWN;
            } else if (state == HARVESTED) {
                map.removeCropWithinStepTime(this);
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

    public Cargo harvest() {
        state = HARVESTED;

        /* Countdown until the crop should disappear */
        growthCountdown.countFrom(TIME_TO_WITHER);

        return new Cargo(WHEAT, null);
    }
}
