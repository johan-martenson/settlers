/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.ALMOST_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import static org.appland.settlers.model.Crop.GrowthState.SMALL;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
public class Crop {

    public enum GrowthState {
        JUST_PLANTED,
        SMALL,
        ALMOST_GROWN,
        FULL_GROWN,
        HARVESTED
    }

    // TODO: verify the time to grow and wither and modify if needed
    private static final int TIME_TO_GROW = 199;
    private static final int TIME_TO_WITHER = 199;

    private final Countdown growthCountdown;
    private final Point     position;
    private final GameMap   map;

    private GrowthState state;

    public Crop(Point point, GameMap map) {
        position = point;
        state    = JUST_PLANTED;
        this.map = map;

        growthCountdown = new Countdown();
        growthCountdown.countFrom(TIME_TO_GROW);
    }

    public void stepTime() {
        if (state == FULL_GROWN) {
            return;
        }

        if (growthCountdown.hasReachedZero()) {
            if (state == JUST_PLANTED) {
                state = SMALL;

                growthCountdown.countFrom(TIME_TO_GROW);
            } else if (state == SMALL) {
                state = ALMOST_GROWN;

                growthCountdown.countFrom(TIME_TO_GROW);
            } else if (state == ALMOST_GROWN) {
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
