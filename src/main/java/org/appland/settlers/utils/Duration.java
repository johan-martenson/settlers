package org.appland.settlers.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Duration {

    private final String name;
    private final List<Stamp> timestamps;
    private final long timestampAtStart;

    private Stats stats;

    public void reportStatsAndContinueToAggregate(Stats stats) {
        Stamp previous = null;

        for (Stamp stamp : timestamps) {
            if (previous != null) {
                String variableName = name + "." + stamp.name;

                Variable variable = stats.addPeriodicCounterVariableIfAbsent(variableName);

                variable.reportValue(stamp.timestamp - previous.timestamp);
            }

            previous = stamp;
        }

        String totalName = name + ".total";

        Variable total = stats.addPeriodicCounterVariableIfAbsent(totalName);

        total.reportValue(previous.timestamp - timestampAtStart);
    }

    public Collection<String> getVariables() {
        List<String> variables = new ArrayList<>();

        Stamp previous = null;

        for (Stamp stamp : timestamps) {
            if (previous != null) {
                String variableName = name + "." + stamp.name;

                variables.add(variableName);
            }

            previous = stamp;
        }

        String totalName = name + ".total";

        variables.add(totalName);

        return variables;
    }

    public String getName() {
        return name;
    }

    static class Stamp {
        private final String name;
        private final long timestamp;

        Stamp(String name) {
            this.name = name;
            this.timestamp = new java.util.Date().getTime();
        }
    }

    public Duration(String name, Stats stats) {
        this(name);

        this.stats = stats;
    }

    public Duration(String name) {
        this.name = name;
        timestamps = new ArrayList<>();
        timestampAtStart = new java.util.Date().getTime();

        timestamps.add(new Stamp(name + ".start"));
    }

    public void after(String stampName) {
        timestamps.add(new Stamp(stampName));
    }

    public void report() {
        reportStats(stats);
    }

    public void reportStats(Stats stats) {

        Stamp previous = null;

        for (Stamp stamp : timestamps) {
            if (previous != null) {
                String variableName = name + "." + stamp.name;

                Variable variable = stats.addVariableIfMissing(variableName);

                variable.reportValue(stamp.timestamp - previous.timestamp);
            }

            previous = stamp;
        }

        String totalName = name + ".total";

        Variable total = stats.addVariableIfMissing(totalName);

        total.reportValue(previous.timestamp - timestampAtStart);
    }

    public long getFullDuration() {
        return timestamps.getLast().timestamp - timestampAtStart;
    }
}
