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
    private final static int RESTING_TIME = 99;
    private final static int MAX_RANGE = 15;

    private State state;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        RETURNING_TO_STORAGE
    }

    public CatapultWorker(Player player, GameMap m) {
        super(player, m);

        countdown = new Countdown();
        state = State.WALKING_TO_TARGET;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Catapult) {
            setHome(b);
        }

        state = State.RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE) {            
            if (countdown.reachedZero()) {

                /* Fire the catapult if there are stones available */
                if (getHome().getAmount(STONE) > 0) {

                    Building target = findReachableTarget();

                    /* Fire a projectile if there was a suitable target */
                    if (target != null) {
                        Projectile projectile = new Projectile(getPosition(), target, map);

                        map.placeProjectile(projectile, getPosition());

                        /* Consume the stone */
                        getHome().consumeOne(STONE);

                        /* Rest again */
                        countdown.countFrom(RESTING_TIME);
                    }
                }

            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    public String toString() {
        return "Catapult worker " + state;
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());
    
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

    private Building findReachableTarget() {

        for (Point p : map.getPointsWithinRadius(getPosition(), MAX_RANGE)) {

            /* Filter points without a building */
            if (!map.isBuildingAtPoint(p)) {
                continue;
            }

            Building building = map.getBuildingAtPoint(p);

            /* Filter buildings belonging to the same player */
            if (building.getPlayer().equals(getPlayer())) {
                continue;
            }

            /* Filter non-military buildings */
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            /* Filer buildings that are not ready */
            if (!building.ready()) {
                continue;
            }

            return building;
        }

        return null;
    }
}
