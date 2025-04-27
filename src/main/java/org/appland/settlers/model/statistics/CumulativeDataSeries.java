package org.appland.settlers.model.statistics;

import java.util.ArrayList;
import java.util.List;

public class CumulativeDataSeries {
    private final String name;
    private final List<Measurement> measurements = new ArrayList<>();

    public CumulativeDataSeries(String name) {
        this.name = name;
    }

    public CumulativeDataSeries(String name, int initialAmount) {
        this(name);

        measurements.add(new Measurement(1, initialAmount));
    }

    public String getName() {
        return name;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void increase(long time) {
        increase(1, time);
    }

    public void increase(int amount, long time) {
        if (measurements.isEmpty()) {
            measurements.add(new Measurement(time, amount));
        } else {
            var lastValue = measurements.getLast().value();
            var lastTime = measurements.getLast().time();

            if (time == lastTime) {
                measurements.removeLast();
                measurements.add(new Measurement(time, lastValue + amount));
            } else {
                measurements.add(new Measurement(time, lastValue + amount));
            }
        }
    }

    public void decrease(long time) {
        decrease(1, time);
    }

    public void decrease(int amount, long time) {
        if (measurements.isEmpty()) {
            measurements.add(new Measurement(time, -amount));
        } else {
            var lastValue = measurements.getLast().value();
            var lastTime = measurements.getLast().time();

            if (time == lastTime) {
                measurements.removeLast();
                measurements.add(new Measurement(time, lastValue - amount));
            } else {
                measurements.add(new Measurement(time, lastValue - amount));
            }
        }
    }

    public void report(long time, int value) {
        if (!measurements.isEmpty() && measurements.getLast().time() == time) {
            measurements.removeLast();
        }

        measurements.add(new Measurement(time, value));
    }
}
