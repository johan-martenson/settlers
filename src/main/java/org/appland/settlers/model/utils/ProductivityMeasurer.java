package org.appland.settlers.model.utils;

import java.util.Arrays;

public final class ProductivityMeasurer {
    private static final int TICKS_PER_MEASUREMENT = 160;
    private static final int HISTORY_SIZE = 6;

    private int ticksInWindow;
    private int notWorkingTicks;

    private final int[] previousProductivity = new int[HISTORY_SIZE];

    private int productivity;

    /**
     * Advances the productivity measurement by one game tick.
     *
     * @param working true if the worker/building is considered working
     * @return true when a new 16-second measurement was completed
     */
    public boolean reportProductivity(boolean working) {
        ticksInWindow++;

        if (!working) {
            notWorkingTicks++;
        }

        if (ticksInWindow < TICKS_PER_MEASUREMENT) {
            return false;
        }

        updateProductivity();

        ticksInWindow = 0;
        notWorkingTicks = 0;

        return true;
    }

    /**
     * Returns the current productivity, from 0 to 100.
     */
    public int productivity() {
        return productivity;
    }

    /**
     * Resets the current measurement and all productivity history.
     */
    public void reset() {
        ticksInWindow = 0;
        notWorkingTicks = 0;
        productivity = 0;
        Arrays.fill(previousProductivity, 0);
    }

    private void updateProductivity() {
        int workingTicks = TICKS_PER_MEASUREMENT - notWorkingTicks;
        int currentProductivity = workingTicks * 5 / 8;

        int sum = currentProductivity;

        for (int previous : previousProductivity) {
            sum += previous;
        }

        productivity = sum / 7;

        System.arraycopy(
                previousProductivity,
                1,
                previousProductivity,
                0,
                HISTORY_SIZE - 1
        );

        previousProductivity[HISTORY_SIZE - 1] = currentProductivity;
    }
}