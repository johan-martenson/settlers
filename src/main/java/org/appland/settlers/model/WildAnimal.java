/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.appland.settlers.model.Material.MEAT;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WildAnimal extends Worker {

    private static final int TIME_TO_STAND = 39;
    private static final int RANGE = 10;
    private final Countdown countdown;

    private static Random random;

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

        if (random == null) {
            random = new Random();
            random.setSeed(1);
        }
    }

    public static boolean cannotWalkOnAny(Collection<Vegetation> surroundingTiles) {
        for (Vegetation vegetation : surroundingTiles) {
            if (vegetation.canAnimalWalkOn()) {
                return false;
            }
        }

        return true;
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
            MapPoint mapPoint = map.getMapPoint(point);

            /* Filter points outside of the map */
            if (mapPoint == null) {
                continue;
            }

            /* Filter points with buildings */
            if (mapPoint.isBuilding()) {
                continue;
            }

            /* Filter points with stones */
            if (mapPoint.isStone()) {
                continue;
            }

            Collection<Vegetation> surroundingVegetation = map.getSurroundingTiles(point);

            /* Filter points where there is tile to walk on */
            if (cannotWalkOnAny(surroundingVegetation)) {
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
