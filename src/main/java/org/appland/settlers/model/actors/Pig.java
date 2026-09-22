package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.PigFarm;

public class Pig {
    private PigAge pigAge;
    private PigFarm.StyeSlot slot;

    public PigFarm.StyeSlot slot() {
        return slot;
    }

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

    public Pig(PigAge pigAge, PigFarm.StyeSlot slot) {
        this.pigAge = pigAge;
        this.slot = slot;
    }

    public PigAge getAge() {
        return pigAge;
    }
}
