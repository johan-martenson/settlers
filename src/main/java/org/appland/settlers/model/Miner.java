/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Miner.State.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Miner.State.GOING_OUT_TO_FLAG;
import static org.appland.settlers.model.Miner.State.MINING;
import static org.appland.settlers.model.Miner.State.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Miner.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Miner.State.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker (speed = 10)
public class Miner extends Worker {
    private final static int RESTING_TIME = 99;
    private final static int TIME_TO_MINE = 49;

    private final Countdown countdown;

    private Material mineral;
    private State state;

    protected enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        MINING,
        GOING_OUT_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }


    public Miner(Player player, GameMap map) {
        super(player, map);

        mineral = null;

        countdown = new Countdown();

        state = WALKING_TO_TARGET;
    }

    public boolean isMining() {
        return state == MINING;
    }

    private void consumeFood() {
        Building home = getHome();

        if (home.getAmount(BREAD) > 0) {
            home.consumeOne(BREAD);
        } else if (home.getAmount(FISH) > 0) {
            home.consumeOne(FISH);
        } else if (home.getAmount(MEAT) > 0) {
            home.consumeOne(MEAT);
        }
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b.isMine()) {
            setHome(b);
        }

        if (b instanceof GoldMine) {
            mineral = GOLD;    
        } else if (b instanceof IronMine) {
            mineral = IRON;
        } else if (b instanceof CoalMine) {
            mineral = COAL;
        } else if (b instanceof GraniteMine) {
            mineral = STONE;
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                if (hasFood()) {
                    state = MINING;
                    countdown.countFrom(TIME_TO_MINE);
                }
            } else {
                countdown.step();
            }
        } else if (state == MINING) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {
                if (map.getAmountOfMineralAtPoint(mineral, getPosition()) > 0) {
                    consumeFood();

                    Cargo cargo = map.mineMineralAtPoint(mineral, getPosition());

                    setCargo(cargo);

                    setTarget(getHome().getFlag().getPosition());

                    state = GOING_OUT_TO_FLAG;
                } else {

                    /* Report that there is no more ore available in the mine */
                    getHome().reportNoMoreNaturalResources();
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == GOING_OUT_TO_FLAG) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();
            getHome().getFlag().putCargo(cargo);

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

    private boolean hasFood() {
        Building home = getHome();

        return home.getAmount(BREAD) > 0 || 
               home.getAmount(FISH)  > 0 ||
               home.getAmount(MEAT)  > 0;
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

            /* Don't try to enter the mine upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
