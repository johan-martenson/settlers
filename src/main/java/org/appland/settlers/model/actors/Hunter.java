/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.List;

import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.MEAT;

@Walker(speed = 10)
public class Hunter extends Worker {
    private static final int TIME_TO_SHOOT = 4;
    private static final int TIME_TO_REST = 99;
    private static final int SHOOTING_DISTANCE = 2;
    private static final int DETECTION_RANGE = 20;
    private static final int TIME_FOR_SKELETON_TO_DISAPPEAR = 99;

    private final Countdown countdown;
    private final ProductivityMeasurer productivityMeasurer;

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
        WAITING_FOR_SPACE_ON_FLAG,
        GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE,
        GOING_TO_DIE,
        DEAD,
        GOING_BACK_TO_HOUSE_WITHOUT_CARGO
    }

    public Hunter(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;

        countdown = new Countdown();

        prey = null;

        productivityMeasurer = new ProductivityMeasurer(TIME_TO_REST + TIME_TO_SHOOT, null);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;

        countdown.countFrom(TIME_TO_REST);

        productivityMeasurer.setBuilding(building);
    }

    // FIXME: HOTSPOT
    @Override
    protected void onIdle() {

        if (state == State.RESTING_IN_HOUSE && getHome().isProductionEnabled()) {
            if (countdown.hasReachedZero()) {

                /* Find an animal to hunt */
                for (WildAnimal animal : getMap().getWildAnimals()) {

                    /* Filter animals too far away */
                    if (animal.getPosition().distance(getHome().getPosition()) > DETECTION_RANGE) {
                        continue;
                    }

                    /* Filter animals that can't be reached */
                    List<Point> path;

                    if (getPosition().equals(getHome().getPosition())) {
                        path = getMap().findWayOffroad(getPosition(), animal.getPosition(), getHome().getFlag().getPosition(), null);
                    } else {
                        path = getMap().findWayOffroad(getPosition(), animal.getPosition(), null);
                    }

                    if (path == null) {
                        continue;
                    }

                    /* Start hunting the prey */
                    prey = animal;

                    setOffroadTargetWithPath(path.subList(0, 2));

                    state = State.TRACKING;

                    break;
                }

                /* Report if the hunter couldn't find an animal to hunt */
                if (state == State.RESTING_IN_HOUSE) {
                    productivityMeasurer.reportUnproductivity();
                }
            } else {
                countdown.step();
            }
        } else if (state == State.SHOOTING) {
            if (countdown.hasReachedZero()) {

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
        } else if (state == State.WAITING_FOR_SPACE_ON_FLAG) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = State.GOING_TO_FLAG_TO_LEAVE_CARGO;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo(getCargo());
            }
        } else if (state == State.DEAD) {
            if (countdown.hasReachedZero()) {
                map.removeWorker(this);
            } else {
                countdown.step();
            }
        }
    }

    private boolean isMeatReceiver(Building building) {
        if (building.isReady() && building instanceof Storehouse storehouse) {
            return !storehouse.isDeliveryBlocked(MEAT);
        }

        if (building.isReady() && building.needsMaterial(MEAT)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onArrival() {
        if (state == State.TRACKING) {

            /* Keep tracking if the prey is too far away to shoot */
            if (prey.getPosition().distance(getPosition()) > SHOOTING_DISTANCE) {

                /* Get way to target */
                List<Point> steps = getMap().findWayOffroad(getPosition(), prey.getPosition(), null);

                /* Take the first step toward the prey */
                setOffroadTarget(steps.get(1));
            } else if (prey.isExactlyAtPoint()) {
                state = State.SHOOTING;

                countdown.countFrom(TIME_TO_SHOOT);
            }
        } else if (state == State.GOING_BACK_TO_HOUSE_WITH_CARGO) {
            if (getHome().getFlag().hasPlaceForMoreCargo()) {
                state = State.GOING_TO_FLAG_TO_LEAVE_CARGO;

                setTarget(getHome().getFlag().getPosition());

                getHome().getFlag().promiseCargo(getCargo());
            } else {
                state = State.WAITING_FOR_SPACE_ON_FLAG;
            }
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        } else if (state == State.GOING_TO_PICK_UP_MEAT) {
            Cargo cargo = prey.pickUpCargo();

            setCargo(cargo);

            /* Start going back to the hunter hut with the wild animal */
            state = State.GOING_BACK_TO_HOUSE_WITH_CARGO;
            setOffroadTarget(getHome().getPosition(), getHome().getFlag().getPosition());

            /* Report that the hunter felled a wild animal */
            productivityMeasurer.reportProductivity();
            productivityMeasurer.nextProductivityCycle();

            map.getStatisticsManager().meatProduced(player, map.getTime());
        } else if (state == State.GOING_TO_FLAG_TO_LEAVE_CARGO) {
            Flag flag = map.getFlagAtPoint(getPosition());

            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToReceivingBuilding(this::isMeatReceiver);

            flag.putCargo(getCargo());

            setCargo(null);

            state = State.GOING_BACK_TO_HOUSE_WITHOUT_CARGO;

            setTarget(getHome().getPosition());
        } else if (state == State.GOING_BACK_TO_HOUSE_WITHOUT_CARGO) {
            state = State.RESTING_IN_HOUSE;

            enterBuilding(getHome());
        } else if (state == State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE) {

            /* Go to the closest storage */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, HUNTER);

            if (storehouse != null) {

                state = State.RETURNING_TO_STORAGE;

                setTarget(storehouse.getPosition());
            } else {
                state = State.GOING_TO_DIE;

                Point point = findPlaceToDie();

                setOffroadTarget(point);
            }
        } else if (state == State.GOING_TO_DIE) {
            setDead();

            state = State.DEAD;

            countdown.countFrom(TIME_FOR_SKELETON_TO_DISAPPEAR);
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, HUNTER);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;

            setTarget(storage.getPosition());
        } else {

            storage = GameUtils.getClosestStorageOffroadWhereDeliveryIsPossible(getPosition(), null, getPlayer(), HUNTER);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;

                setOffroadTarget(storage.getPosition());
            } else {
                Point point = findPlaceToDie();

                setOffroadTarget(point, getPosition().downRight());

                state = State.GOING_TO_DIE;
            }
        }
    }

    public boolean isShooting() {
        return state == State.SHOOTING;
    }

    public WildAnimal getPrey() {
        return prey;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

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

    @Override
    public int getProductivity() {

        /* Measure productivity across the length of four rest-work periods */
        return (int)
                (((double)productivityMeasurer.getSumMeasured() /
                        (productivityMeasurer.getNumberOfCycles())) * 100);
    }

    @Override
    public void goToOtherStorage(Building building) {
        state = State.GOING_TO_FLAG_THEN_GOING_TO_OTHER_STORAGE;

        setTarget(building.getFlag().getPosition());
    }
}
