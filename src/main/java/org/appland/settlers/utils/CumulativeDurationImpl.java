package org.appland.settlers.utils;

public class CumulativeDurationImpl implements CumulativeDuration {

    private final Duration duration;
    private final Stats stats;
    private final Group group;

    CumulativeDurationImpl(String name, Group group, Stats stats) {
        duration = new Duration(name, stats);
        this.stats = stats;
        this.group = group;
    }

    @Override
    public void after(String name) {
        duration.after(name);
    }

    @Override
    public void report() {
        duration.reportStatsAndContinueToAggregate(stats);

        group.addVariables(duration.getVariables());
    }
}
