/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.WATER;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class WellWorker extends Worker {
    private final static int PRODUCTION_TIME = 49;
    private final static int RESTING_TIME    = 99;

    private final Countdown countdown;

    private States  state;

    public WellWorker(Player player, GameMap m) {
        super(player, m);

        countdown = new Countdown();
        state     = States.WALKING_TO_TARGET;
    }

    private enum States {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DRAWING_WATER,
        GOING_TO_FLAG_WITH_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Well) {
            setHome(b);
        }

        state = States.RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == States.RESTING_IN_HOUSE) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {
                state = States.DRAWING_WATER;

                countdown.countFrom(PRODUCTION_TIME);
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == States.DRAWING_WATER) {
            if (countdown.reachedZero()) {
                Cargo cargo = new Cargo(WATER, map);

                setCargo(cargo);

                setTarget(getHome().getFlag().getPosition());

                state = States.GOING_TO_FLAG_WITH_CARGO;
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == States.GOING_TO_FLAG_WITH_CARGO) {
            Flag f = getHome().getFlag();
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();

            f.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = States.GOING_BACK_TO_HOUSE;
        } else if (state == States.GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = States.RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == States.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());

        if (storage != null) {
            state = States.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage) {
                    state = States.RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }
}
