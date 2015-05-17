/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WildAnimal extends Worker {

    private final static int TIME_TO_STAND = 19;
    private final static int[] PSEUDO_RANDOM = {-1, 0, 3, 5, 4, 2, 5, 2, -1, 0, 4, 3, 1, 1};

    private final Countdown countdown;
    private State state;
    private int nextPick;

    private enum State {

        ALIVE,
        DEAD
    }

    public WildAnimal(GameMap map) {
        super(null, map);

        state = State.ALIVE;

        countdown = new Countdown();

        countdown.countFrom(TIME_TO_STAND);

        nextPick = 0;
    }

    @Override
    protected void onIdle() throws Exception {

        if (state == State.ALIVE) {
            if (countdown.reachedZero()) {

                /* Should the animal stand still or move? */
                Point nextPoint = findNextPoint();

                /* Walk if there is an available spot */
                if (nextPoint == null) {
                    countdown.countFrom(TIME_TO_STAND);
                } else {
                    setOffroadTarget(nextPoint);
                }
            } else {
                countdown.step();
            }
        }
    }

    private boolean canGoTo(Point p) throws Exception {

        if (!map.isWithinMap(p)) {
            return false;
        }

        if (map.isBuildingAtPoint(p)) {
            return false;
        }

        if (map.isStoneAtPoint(p)) {
            return false;
        }

        if (map.getTerrain().isInWater(p)) {
            return false;
        }

        return true;
    }

    private Point findNextPoint() throws Exception {
        /* Get surrounding points */
        List<Point> adjacentPoints = getPosition().getDiagonalPointsAndSides();

        /* Choose the next point to go to */
        for (int i = nextPick; i < PSEUDO_RANDOM.length; i++) {

            if (PSEUDO_RANDOM[i] == -1) {

                nextPick++;

                if (nextPick >= PSEUDO_RANDOM.length) {
                    nextPick = 0;
                }

                return null;
            }

            Point p = adjacentPoints.get(PSEUDO_RANDOM[i]);

            if (canGoTo(p)) {

                nextPick++;

                if (nextPick >= PSEUDO_RANDOM.length) {
                    nextPick = 0;
                }

                return p;
            }
        }

        return null;
    }
}
