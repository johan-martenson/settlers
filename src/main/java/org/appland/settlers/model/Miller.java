/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Miller.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Miller.State.GOING_TO_FLAG_WITH_CARGO;
import static org.appland.settlers.model.Miller.State.GRINDING_WHEAT;
import static org.appland.settlers.model.Miller.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Miller.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Miller.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Miller extends Worker {
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME = 99;

    private final Countdown countdown;

    private State state;

    public Miller(Player player, GameMap m) {
        super(player, m);
        countdown = new Countdown();
        state = WALKING_TO_TARGET;
    }

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GRINDING_WHEAT,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Mill) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                state = GRINDING_WHEAT;

                countdown.countFrom(PRODUCTION_TIME);
            } else {
                countdown.step();
            }
        } else if (state == GRINDING_WHEAT) {
            if (getHome().getAmount(WHEAT) > 0 && getHome().isProductionEnabled()) {
                if (countdown.reachedZero()) {
                    Cargo cargo = new Cargo(FLOUR, map);

                    getHome().consumeOne(WHEAT);

                    setCargo(cargo);

                    setTarget(getHome().getFlag().getPosition());

                    state = GOING_TO_FLAG_WITH_CARGO;
                } else if (getHome().isProductionEnabled()) {
                    countdown.step();
                }
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_TO_FLAG_WITH_CARGO) {
            Flag f = getHome().getFlag();

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            f.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }    

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = GameUtils.getClosestStorage(getPosition(), map);

        if (storage != null) {
            state = RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the mill upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
