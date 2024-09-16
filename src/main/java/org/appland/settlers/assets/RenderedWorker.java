package org.appland.settlers.assets;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the management and retrieval of rendered animations for different nations and compass directions.
 */
public class RenderedWorker {
    private final JobType jobType;
    private final Map<AnimationKey, StackedBitmaps[]> animations = new HashMap<>();

    /**
     * Constructs a RenderedWorker with the specified job type.
     *
     * @param jobType The job type associated with this worker.
     */
    public RenderedWorker(JobType jobType) {
        this.jobType = jobType;
    }

    /**
     * Sets the animation step for a given nation and compass direction.
     *
     * @param nation The nation for which the animation is being set.
     * @param compassDirection The compass direction for which the animation is being set.
     * @param bitmaps The stacked bitmaps for the animation step.
     * @param animationStep The step in the animation sequence.
     */
    public void addAnimationStep(Nation nation, CompassDirection compassDirection, StackedBitmaps bitmaps, int animationStep) {
        AnimationKey animationKey = new AnimationKey(nation, compassDirection);

        animations.computeIfAbsent(animationKey, k -> new StackedBitmaps[8])[animationStep] = bitmaps;
    }

    /**
     * Retrieves the animation for a given nation and compass direction.
     *
     * @param nation The nation for which the animation is requested.
     * @param compassDirection The compass direction for which the animation is requested.
     * @return The array of stacked bitmaps representing the animation.
     */
    public StackedBitmaps[] getAnimation(Nation nation, CompassDirection compassDirection) {
        return animations.get(new AnimationKey(nation, compassDirection));
    }

    @Override
    public String toString() {
        return "RenderedWorker{" +
                "job=" + jobType +
                ", animationMap=" + animations +
                '}';
    }
}
