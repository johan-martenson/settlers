/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;

@Walker(speed = 10)
public class Hunter extends Worker {
    private static final int TIME_TO_SHOOT = 4;
    private static final int TIME_TO_REST = 99;
    private static final int SHOOTING_DISTANCE = 2;
    private static final int DETECTION_RANGE = 20;

    private final Countdown countdown;

    private State      state;
    private WildAnimal prey;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        TRACKING,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        RETURNING_TO_STORAGE, 
        SHOOTING, 
        GOING_TO_PICK_UP_MEAT, 
        GOING_TO_FLAG_TO_LEAVE_CARGO, 
        GOING_BACK_TO_HOUSE_WITHOUT_CARGO
    }

    public Hunter(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;

        countdown = new Countdown();

        prey = null;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof HunterHut) {
            setHome(b);
        }

        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {

        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                prey = findPreyCloseEnough();

                if (prey == null) {
                    return;
                }

                /* Get way to target */
                List<Point> steps = getMap().findWayOffroad(getHome().getPosition(), 
                                                            prey.getPosition(), 
                                                            getHome().getFlag().getPosition(), 
                                                            null);

                /* Take the first step toward the prey */
                setOffroadTarget(steps.get(1));

                state = State.TRACKING;
            } else {
                countdown.step();
            }
        } else if (state == State.SHOOTING) {
            if (countdown.reachedZero()) {

                /* Tell the animal it's been shot */
                prey.shoot();

                /* Go to pick up the meat from the dead animal */
                state = State.GOING_TO_PICK_UP_MEAT;

                setOffroadTarget(prey.getPosition());
            } else {
                countdown.step();
            }
        } else if (state == State.TRACKING) {
            if (prey.isExactlyAtPoint()) {
                state = State.SHOOTING;

                countdown.countFrom(TIME_TO_SHOOT);
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == State.TRACKING) {

            /* Keep tracking if the prey is too far away to shoot */
            if (prey.getPosition().distance(getPosition()) > SHOOTING_DISTANCE) {

                /* Get way to target */
                List<Point> steps = getMap().findWayOffroad(getPosition(), 
                                                            prey.getPosition(), 
                                                            null);

                /* Take the first step toward the prey */
                setOffroadTarget(steps.get(1));
            } else if (prey.isExactlyAtPoint()) {
                state = State.SHOOTING;

                countdown.countFrom(TIME_TO_SHOOT);
            }
        } else if (state == State.GOING_BACK_TO_HOUSE_WITH_CARGO) {
            state = State.GOING_TO_FLAG_TO_LEAVE_CARGO;

            setTarget(getHome().getFlag().getPosition());
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        } else if (state == State.GOING_TO_PICK_UP_MEAT) {
            Cargo cargo = prey.pickUpCargo();

            setCargo(cargo);

            state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;
            setOffroadTarget(getHome().getPosition(), getHome().getFlag().getPosition());
        } else if (state == State.GOING_TO_FLAG_TO_LEAVE_CARGO) {
            Cargo cargo = getCargo();

            setCargo(null);

            getHome().getFlag().putCargo(cargo);

            state = State.GOING_BACK_TO_HOUSE_WITHOUT_CARGO;

            returnHome();
        } else if (state == State.GOING_BACK_TO_HOUSE_WITHOUT_CARGO) {
            state = State.RESTING_IN_HOUSE;

            enterBuilding(getHome());
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), map);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage) {
                    state = State.RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }

    private WildAnimal findPreyCloseEnough() {

        for (WildAnimal animal : getMap().getWildAnimals()) {

            /* Filter animals too far away */
            if (animal.getPosition().distance(getHome().getPosition()) > DETECTION_RANGE) {
                continue;
            }

            return animal;
        }

        return null;
    }

    public boolean isShooting() {
        return state == State.SHOOTING;
    }

    public WildAnimal getPrey() {
        return prey;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the hunter hut upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
