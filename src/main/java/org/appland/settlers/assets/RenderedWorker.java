package org.appland.settlers.assets;

import java.util.HashMap;
import java.util.Map;

public class RenderedWorker {
    private final JobType job;
    private final Map<AnimationKey, StackedBitmaps[]> animationMap;

    public RenderedWorker(JobType job) {
        this.job = job;
        animationMap = new HashMap<>();
    }

    public void setAnimationStep(Nation nation, CompassDirection compassDirection, StackedBitmaps bitmaps, int animationStep) {
        AnimationKey animationKey = new AnimationKey(nation, compassDirection);

        if (!animationMap.containsKey(animationKey)) {
            animationMap.put(animationKey, new StackedBitmaps[8]);
        }

        StackedBitmaps[] animation = animationMap.get(animationKey);

        animation[animationStep] = bitmaps;
    }

    public StackedBitmaps[] getAnimation(Nation nation, CompassDirection compassDirection) {
        return animationMap.get(new AnimationKey(nation, compassDirection));
    }

    @Override
    public String toString() {
        return "RenderedWorker{" +
                "job=" + job +
                ", animationMap=" + animationMap +
                '}';
    }
}
