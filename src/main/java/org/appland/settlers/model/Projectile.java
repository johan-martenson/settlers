/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

/**
 *
 * @author johan
 */
public class Projectile implements Actor {
    private final int SPEED = 5;
    private final Building  target;
    private final Point     source;
    private final Countdown countdown;

    Projectile(Point src, Building tgt) {
        target = tgt;
        source = src;

        countdown = new Countdown();
        countdown.countFrom((int)(src.distance(tgt.getPosition()) * SPEED));
    }

    public Point getTarget() {
        return target.getPosition();
    }

    public Point getSource() {
        return source;
    }

    public int getProgress() {

        int traveled = countdown.getStartedAt() - countdown.getCount();

        return (int) ((double) traveled / (double) countdown.getStartedAt() * 100);
    }

    @Override
    public void stepTime() throws Exception {

        if (!countdown.reachedZero()) {
            countdown.step();
        }

        if (countdown.reachedZero()) {
            target.hitByCatapult();
        }
    }

    public boolean arrived() {
        return countdown.reachedZero();
    }
}
