/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import java.util.Random;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.model.Vegetation.DEEP_WATER;
import static org.appland.settlers.model.Vegetation.LAVA;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WildAnimal extends Worker {

    private static final int TIME_TO_STAND = 9;
    private static final int RANGE = 10;

    private final Random random;
    private final Countdown countdown;

    private State state;

    private enum State {
        ALIVE,
        DEAD
    }

    public WildAnimal(GameMap map) {
        super(null, map);

        state = State.ALIVE;

        countdown = new Countdown();
        countdown.countFrom(TIME_TO_STAND);

        random = new Random();
        random.setSeed(1);
    }

    public static boolean cannotWalkOn(List<Vegetation> surroundingTiles) {
        for (Vegetation vegetation : surroundingTiles) {
            return (vegetation == WATER || vegetation == DEEP_WATER || vegetation == LAVA);
        }

        return false;
    }

    @Override
    protected void onIdle() throws InvalidRouteException {

        if (state == State.ALIVE) {
            if (countdown.hasReachedZero()) {

                /* Should the animal stand still or move? */
                if (random.nextBoolean()) {

                    /* Stand still for a while */
                    countdown.countFrom(TIME_TO_STAND);
                } else {

                    List<Point> pathToNextPoint = findNextPoint();

                    /* Walk if there is an available spot */
                    if (pathToNextPoint != null) {
                        setOffroadTargetWithPath(pathToNextPoint); // FIXME: HOTSPOT
                    }
                }
            } else {
                countdown.step();
            }
        }
    }

    // FIXME: HOTSPOT - allocations
    private List<Point> findNextPoint() {

        /* Get surrounding points */
        List<Point> adjacentPoints = map.getPointsWithinRadius(getPosition(), RANGE);

        int offset = random.nextInt(adjacentPoints.size());

        /* Start at a random place in the list and look for points to go to */
        for (int i = 0; i < adjacentPoints.size(); i++) {
            int index = i + offset;

            if (index >= adjacentPoints.size()) {
                index = index - adjacentPoints.size();
            }

            Point point = adjacentPoints.get(index);

            /* Filter points outside of the map */
            if (!map.isWithinMap(point)) {
                continue;
            }

            /* Filter points with buildings */
            if (map.isBuildingAtPoint(point)) {
                continue;
            }

            /* Filter points with stones */
            if (map.isStoneAtPoint(point)) {
                continue;
            }

            /* Filter points in water */
            if (map.getTerrain().isInWater(point)) {
                continue;
            }

            /* Filter un-reachable points (expensive) */
            List<Point> path = map.findWayOffroad(getPosition(), point, null);
            if (path == null) {
                continue;
            }

            /* Return the found path */
            return path;
        }

        /* Return null if there is no available point */
        return null;
    }

    public boolean isAlive() {
        return state != State.DEAD;
    }

    Cargo pickUpCargo() {
        map.removeWildAnimalWithinStepTime(this);

        return new Cargo(MEAT, map);
    }

    void shoot() {
        state = State.DEAD;

        cancelWalkingToTarget();
    }
}
