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
        increase(time, 1);
    }

    public void increase(long time, int amount) {
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
        if (measurements.isEmpty()) {
            measurements.add(new Measurement(time, -1));
        } else {
            var lastValue = measurements.getLast().value();
            var lastTime = measurements.getLast().time();

            if (time == lastTime) {
                measurements.removeLast();
                measurements.add(new Measurement(time, lastValue - 1));
            } else {
                measurements.add(new Measurement(time, lastValue - 1));
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
