/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.ArrayList;
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
    private static Random random;

    private final Type type;
    private final Countdown countdown;

    private State state;

    public WildAnimal(GameMap map, Type type) {
        super(null, map);

        this.type = type;

        state = State.ALIVE;

        countdown = new Countdown();
        countdown.countFrom(TIME_TO_STAND);

        if (random == null) {
            random = new Random();
            random.setSeed(1);
        }
    }

    public Type getType() {
        return type;
    }

    private enum State {
        ALIVE,
        DEAD
    }

    public WildAnimal(GameMap map) {
        this(map, WildAnimal.Type.FOX);
    }

    public static boolean cannotWalkOnAny(Collection<DetailedVegetation> surroundingTiles) {
        for (DetailedVegetation vegetation : surroundingTiles) {
            if (!DetailedVegetation.WILD_ANIMAL_CAN_NOT_WALK_ON.contains(vegetation)) {
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

    private List<Point> findNextPoint() {
        for (Point point : map.getPossibleAdjacentOffRoadConnections(getPosition())) {

            MapPoint mapPoint = map.getMapPoint(point);

            /* Filter buildings as animals can't walk into buildings */
            if (mapPoint.isBuilding()) {
                continue;
            }

            /* Filter points surrounded by shallow water as animals can't walk on water */
            Collection<DetailedVegetation> surroundingVegetation = map.getSurroundingTiles(point);

            if (cannotWalkOnAny(surroundingVegetation)) {
                continue;
            }

            List<Point> step = new ArrayList<>();

            step.add(getPosition());
            step.add(point);

            return step;
        }

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

    public enum Type {
        RABBIT,
        FOX,
        STAG,
        DEER,
        DUCK,
        SHEEP,
        DEER_2,
        DUCK_2
    }
}
