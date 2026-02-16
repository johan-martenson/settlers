package org.appland.settlers.model.actors;

public class Pig {
    private PigAge pigAge;

    public enum PigAction {
        PIG_ACTION_1,
        PIG_ACTION_2,
        PIG_ACTION_3,
        PIG_ACTION_4,
        PIG_ACTION_5,
        PIG_ACTION_6,
        PIG_ACTION_7,
        PIG_ACTION_8
    }

    public enum PigAge {
        ADULT,
        PIGLET
    }

    public Pig(PigAge pigAge) {
        this.pigAge = pigAge;
    }

    public PigAge getAge() {
        return pigAge;
    }
}
