package org.appland.settlers.utils;

import java.util.ArrayList;
import java.util.List;

public class Duration {

    private final String name;
    private final List<Stamp> timestamps;
    private final long timestampAtStart;

    static class Stamp {
        private final String name;
        private final long timestamp;

        Stamp(String name) {
            this.name = name;
            this.timestamp = (new java.util.Date()).getTime();
        }
    }

    public Duration(String name) {
        this.name = name;
        timestamps = new ArrayList<>();
        timestampAtStart = (new java.util.Date()).getTime();
    }

    public void after(String stampName) {
        timestamps.add(new Stamp(stampName));
    }

    public void reportStats(Stats stats) {

        Stamp previous = null;

        for (Stamp stamp : timestamps) {
            if (previous != null) {
                String variableName = name + "." + stamp.name;

                stats.addVariableIfMissing(variableName);

                stats.reportVariableValue(variableName, stamp.timestamp - previous.timestamp);
            }

            previous = stamp;
        }

        String totalName = name + ".total";

        stats.addVariableIfMissing(totalName);

        stats.reportVariableValue(totalName, previous.timestamp - timestampAtStart);
    }
}
