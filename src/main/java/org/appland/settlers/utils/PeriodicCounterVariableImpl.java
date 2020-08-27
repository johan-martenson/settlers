package org.appland.settlers.utils;

class PeriodicCounterVariableImpl extends VariableImpl {

    private long sum;

    PeriodicCounterVariableImpl(String name) {
        super(name);
    }

    public void reportValue(long value) {
        sum = sum + value;
    }

    void collectionPeriodDone() {
        super.reportValue(sum);

        sum = 0;
    }
}
