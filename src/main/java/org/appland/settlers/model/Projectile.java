/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.Random;

/**
 *
 * @author johan
 */
public class Projectile implements Actor {
    private final static double FAIL_RATE = 0.25;
    private final static int    SPEED     = 5;
    private final static Random RANDOM    = new Random(1);

    private final Building  target;
    private final Catapult  source;
    private final Countdown countdown;
    private final GameMap   map;

    Projectile(Catapult source, Building targetBuilding, GameMap map) {
        target = targetBuilding;
        this.source = source;
        this.map = map;

        countdown = new Countdown();
        countdown.countFrom((int)(source.getPosition().distance(targetBuilding.getPosition()) * SPEED));
    }

    public Point getTarget() {
        return target.getPosition();
    }

    public Catapult getSource() {
        return source;
    }

    public int getProgress() {

        int traveled = countdown.getStartedAt() - countdown.getCount();

        return (int) ((double) traveled / countdown.getStartedAt() * 100);
    }

    @Override
    public void stepTime() throws Exception {

        if (!countdown.hasReachedZero()) {
            countdown.step();
        }

        if (countdown.hasReachedZero()) {

            /* Determine if the projectile hit the target - the hit rate is 75% */
            if (RANDOM.nextDouble() > FAIL_RATE) {
                target.hitByCatapult(source);
            }

            map.removeProjectileFromWithinStepTime(this);
        }
    }

    public boolean isArrived() {
        return countdown.hasReachedZero();
    }
}
