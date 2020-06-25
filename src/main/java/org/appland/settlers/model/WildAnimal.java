/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import java.util.Random;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import static org.appland.settlers.model.Tile.Vegetation.DEEP_WATER;
import static org.appland.settlers.model.Tile.Vegetation.LAVA;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WildAnimal extends Worker {

    private static final int TIME_TO_STAND = 9;
    private static final int MAX_TRIES = 5;
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

    public static boolean cannotWalkOn(List<Tile> surroundingTiles) {
        for (Tile tile : surroundingTiles) {
            Tile.Vegetation vegetation = tile.getVegetationType();

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

                    Point nextPoint = findNextPoint();

                    /* Walk if there is an available spot */
                    if (nextPoint != null) {
                        setOffroadTarget(nextPoint);
                    }
                }
            } else {
                countdown.step();
            }
        }
    }

    // FIXME: HOTSPOT
    private boolean canGoTo(Point point) {

        if (!map.isWithinMap(point)) {
            return false;
        }

        if (map.isBuildingAtPoint(point)) {
            return false;
        }

        if (map.isStoneAtPoint(point)) {
            return false;
        }

        if (map.getTerrain().isInWater(point)) {
            return false;
        }

        if (map.findWayOffroad(getPosition(), point, null) == null) {
            return false;
        }

        return true;
    }

    // FIXME: HOTSPOT - allocations
    private Point findNextPoint() {

        /* Get surrounding points */
        List<Point> adjacentPoints = map.getPointsWithinRadius(getPosition(), RANGE);

        /* Try choosing the next point randomly */
        for (int tries = 0; tries < MAX_TRIES; tries++) {

            int index = random.nextInt(adjacentPoints.size());

            Point point = adjacentPoints.get(index);

            if (canGoTo(point)) {
                return point;
            }
        }

        /* Give up and search through all available points sequentially */
        for (Point point : adjacentPoints) {

            /* Filter points where the animal cannot stand */
            if (!canGoTo(point)) {
                continue;
            }

            /* Return the found point */
            return point;
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
