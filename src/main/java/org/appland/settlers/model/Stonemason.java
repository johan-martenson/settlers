/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import java.util.List;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Stonemason.States.GETTING_STONE;
import static org.appland.settlers.model.Stonemason.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Stonemason.States.GOING_BACK_TO_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.Stonemason.States.GOING_OUT_TO_GET_STONE;
import static org.appland.settlers.model.Stonemason.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.Stonemason.States.IN_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.Stonemason.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Stonemason.States.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Stonemason.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Stonemason extends Worker {

    private final static int TIME_TO_REST = 99;
    private final static int TIME_TO_GET_STONE = 49;
    private final Countdown countdown;
    private States state;
    private Point stoneTarget;

    protected enum States {

        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        GOING_OUT_TO_GET_STONE,
        GETTING_STONE,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        IN_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public Stonemason(Player player, GameMap map) {
        super(player, map);

        state = WALKING_TO_TARGET;

        countdown = new Countdown();
        stoneTarget = null;
    }

    public boolean isGettingStone() {
        return state == GETTING_STONE;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Quarry) {
            setHome(b);
        }

        state = RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.reachedZero()) {
                Point accessPoint = null;
                double distance = Integer.MAX_VALUE;
                Point homePoint = getHome().getPosition();

                /* Look for stones within range */
                for (Point p : map.getPointsWithinRadius(homePoint, 4)) {

                    /* Filter points without stones */
                    if (!map.isStoneAtPoint(p)) {
                        continue;
                    }

                    /* Is the stone reachable? */
                    int distanceToAccessPoint = Integer.MAX_VALUE;
                    Point potentialAccessPoint = null;
                    for (Point p2 : p.getAdjacentPoints()) {

                        /* Filter the quarry since the stone mason needs to go outside  */
                        if (p2.equals(getHome().getPosition())) {
                            continue;
                        }

                        /* Filter points that can't be reached */
                        List<Point> path = map.findWayOffroad(getHome().getPosition(), p2, null);
                        if (path == null) {
                            continue;
                        }

                        /* Look for the closest access point */
                        if (path.size() < distanceToAccessPoint) {
                            distanceToAccessPoint = path.size();

                            potentialAccessPoint = p2;
                        
                        }
                    }

                    /* Skip the stone if there is no way to reach it */
                    if (potentialAccessPoint == null) {
                        continue;
                    }

                    /* Check if this is the closest access point this far */
                    if (distanceToAccessPoint < distance) {
                        distance = distanceToAccessPoint;
                        
                        accessPoint = potentialAccessPoint;

                        stoneTarget = p;
                    }
                }

                if (accessPoint == null) {
                    return;
                }

                setOffroadTarget(accessPoint);

                state = GOING_OUT_TO_GET_STONE;
            } else {
                countdown.step();
            }
        } else if (state == GETTING_STONE) {
            if (countdown.reachedZero()) {
                map.removePartOfStone(stoneTarget);

                setCargo(new Cargo(STONE, map));

                state = GOING_BACK_TO_HOUSE_WITH_CARGO;

                stoneTarget = null;

                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_CARGO) {
            setTarget(getHome().getFlag().getPosition());

            state = GOING_OUT_TO_PUT_CARGO;
        }
    }

    @Override
    public void onArrival() throws Exception {
        if (state == GOING_OUT_TO_PUT_CARGO) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            setTarget(getHome().getPosition());

            state = GOING_BACK_TO_HOUSE;
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;

            enterBuilding(getHome());

            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_OUT_TO_GET_STONE) {
            state = GETTING_STONE;

            countdown.countFrom(TIME_TO_GET_STONE);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());

            state = IN_HOUSE_WITH_CARGO;
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage) map.getBuildingAtPoint(getPosition());

            storage.depositWorker(this);
        }

    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());

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
}
