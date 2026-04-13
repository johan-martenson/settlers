/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Random;

import static org.appland.settlers.model.Crop.GrowthState.*;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
public class Crop {

    private final CropType type;

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

    public Crop(Point point, GameMap map, CropType cropType) {
        position  = point;
        state     = JUST_PLANTED;
        this.map  = map;
        this.type = cropType;

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

        // Countdown until the crop should disappear
        growthCountdown.countFrom(TIME_TO_WITHER);

        return new Cargo(WHEAT, null);
    }

    public CropType getType() {
        return type;
    }

    public enum CropType {
        TYPE_2, TYPE_1;

        private final static Random RANDOM = new Random(1);

        public static CropType getRandom() {
            return RANDOM.nextInt(10) % 2 == 0
                    ? Crop.CropType.TYPE_2
                    : Crop.CropType.TYPE_1;
        }
    }

    @Override
    public String toString() {
        return "Crop at %s (%s)".formatted(position, state);
    }
}
