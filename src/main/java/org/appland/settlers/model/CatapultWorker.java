/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.STONE;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class CatapultWorker extends Worker {
    private final Countdown countdown;
    private static final int RESTING_TIME = 99;
    private static final int MAX_RANGE = 15;

    private State state;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        RETURNING_TO_STORAGE
    }

    public CatapultWorker(Player player, GameMap map) {
        super(player, map);

        countdown = new Countdown();
        state = State.WALKING_TO_TARGET;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (building instanceof Catapult) {
            setHome(building);
        }

        state = State.RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() {
        if (state == State.RESTING_IN_HOUSE) {

            /* Countdown if there are stones available */
            if (getHome().getAmount(STONE) > 0) {

                if (countdown.hasReachedZero()) {
                    Building target = findReachableTarget();

                    /* Fire a projectile if there was a suitable target */
                    if (target != null) {
                        Projectile projectile = new Projectile((Catapult)getHome(), target, map);

                        map.placeProjectile(projectile);

                        /* Consume the stone */
                        getHome().consumeOne(STONE);

                        /* Rest again */
                        countdown.countFrom(RESTING_TIME);
                    }
                } else {
                    countdown.step();
                }
            }
        }
    }

    @Override
    protected void onArrival() {
        if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        }
    }

    @Override
    public String toString() {
        return "Catapult worker " + state;
    }

    @Override
    protected void onReturnToStorage() throws InvalidRouteException {
        Building storage = GameUtils.getClosestStorageConnectedByRoads(getPosition(), getPlayer());

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            }
        }
    }

    private Building findReachableTarget() {

        for (Point point : map.getPointsWithinRadius(getPosition(), MAX_RANGE)) {

            /* Filter points without a building */
            if (!map.isBuildingAtPoint(point)) {
                continue;
            }

            Building building = map.getBuildingAtPoint(point);

            /* Filter buildings belonging to the same player */
            if (building.getPlayer().equals(getPlayer())) {
                continue;
            }

            /* Filter non-military buildings */
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            /* Filer buildings that are not ready */
            if (!building.isReady()) {
                continue;
            }

            return building;
        }

        return null;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws InvalidRouteException {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the catapult upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
