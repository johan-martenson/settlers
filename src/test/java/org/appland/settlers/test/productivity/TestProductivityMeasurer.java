package org.appland.settlers.test.productivity;

import org.appland.settlers.model.utils.ProductivityMeasurer;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestProductivityMeasurer {

    @Test
    public void startsWithZeroProductivity() {
        var measurer = new ProductivityMeasurer();

        assertEquals(0, measurer.productivity());
    }

    @Test
    public void incompleteMeasurementDoesNotChangeProductivity() {
        var measurer = new ProductivityMeasurer();

        for (int i = 0; i < 159; i++) {
            assertFalse(measurer.reportProductivity(true));
        }

        assertEquals(0, measurer.productivity());
    }

    @Test
    public void measurementCompletesAfter160Ticks() {
        var measurer = new ProductivityMeasurer();

        for (int i = 0; i < 159; i++) {
            measurer.reportProductivity(true);
        }

        assertTrue(measurer.reportProductivity(true));

        assertEquals(14, measurer.productivity());
    }

    @Test
    public void completelyWorkingWorkerEventuallyReaches100Percent() {
        var measurer = new ProductivityMeasurer();

        for (int i = 0; i < 7 * 160; i++) {
            measurer.reportProductivity(true);
        }

        assertEquals(100, measurer.productivity());
    }

    @Test
    public void completelyIdleWorkerRemainsAtZeroPercent() {
        var measurer = new ProductivityMeasurer();

        for (int i = 0; i < 7 * 160; i++) {
            measurer.reportProductivity(false);
        }

        assertEquals(0, measurer.productivity());
    }

    @Test
    public void productivityRampsUpOverSevenMeasurements() {
        var measurer = new ProductivityMeasurer();

        for (int measurement = 1; measurement <= 7; measurement++) {
            completeMeasurement(measurer, true);

            int expected = switch (measurement) {
                case 1 -> 14;
                case 2 -> 28;
                case 3 -> 42;
                case 4 -> 57;
                case 5 -> 71;
                case 6 -> 85;
                case 7 -> 100;
                default -> throw new AssertionError();
            };

            assertEquals(expected, measurer.productivity());
        }
    }

    @Test
    public void fiftyPercentWorkingProducesFiftyPercentMeasurement() {
        var measurer = new ProductivityMeasurer();

        // 80 working + 80 not-working ticks = 50% working.
        for (int i = 0; i < 80; i++) {
            measurer.reportProductivity(true);
        }

        for (int i = 0; i < 80; i++) {
            measurer.reportProductivity(false);
        }

        // First measurement is averaged with six initial zeroes.
        assertEquals(7, measurer.productivity());
    }

    @Test
    public void currentMeasurementIsIncludedInRollingAverage() {
        var measurer = new ProductivityMeasurer();

        // First six measurements: 100%.
        for (int i = 0; i < 6; i++) {
            completeMeasurement(measurer, true);
        }

        assertEquals(85, measurer.productivity());

        // Seventh measurement: 0%.
        completeMeasurement(measurer, false);

        // Average of six 100s + one 0.
        assertEquals(85, measurer.productivity());
    }

    @Test
    public void oldestMeasurementFallsOutOfHistory() {
        var measurer = new ProductivityMeasurer();

        // Seven measurements at 100%.
        for (int i = 0; i < 7; i++) {
            completeMeasurement(measurer, true);
        }

        assertEquals(100, measurer.productivity());

        // New measurement at 0%.
        completeMeasurement(measurer, false);

        // Six 100s + one 0.
        assertEquals(85, measurer.productivity());

        // Another 0% measurement.
        completeMeasurement(measurer, false);

        // Five 100s + two 0s.
        assertEquals(71, measurer.productivity());
    }

    @Test
    public void resetClearsCurrentMeasurementAndHistory() {
        var measurer = new ProductivityMeasurer();

        for (int i = 0; i < 7 * 160; i++) {
            measurer.reportProductivity(true);
        }

        assertEquals(100, measurer.productivity());

        measurer.reset();

        assertEquals(0, measurer.productivity());

        // History should really be empty after reset.
        completeMeasurement(measurer, true);

        assertEquals(14, measurer.productivity());
    }

    @Test
    public void exactly160TicksFormOneMeasurement() {
        var measurer = new ProductivityMeasurer();

        for (int i = 0; i < 159; i++) {
            assertFalse(measurer.reportProductivity(true));
        }

        assertTrue(measurer.reportProductivity(true));

        // No second measurement yet.
        assertFalse(measurer.reportProductivity(true));
        assertEquals(14, measurer.productivity());
    }

    @Test
    public void productivityCalculationUsesIntegerArithmetic() {
        var measurer = new ProductivityMeasurer();

        // 100 working ticks, 60 idle ticks = 62.5% working.
        //
        // RttR-style integer calculation:
        // 62.5% * 100 = 62.5 -> 62
        for (int i = 0; i < 100; i++) {
            measurer.reportProductivity(true);
        }

        for (int i = 0; i < 60; i++) {
            measurer.reportProductivity(false);
        }

        // First measurement is divided by seven because of
        // the six initially-zero history entries:
        //
        // 62 / 7 = 8
        assertEquals(8, measurer.productivity());
    }

    private static void completeMeasurement(ProductivityMeasurer measurer, boolean working) {
        for (int i = 0; i < 160; i++) {
            measurer.reportProductivity(working);
        }
    }
}
