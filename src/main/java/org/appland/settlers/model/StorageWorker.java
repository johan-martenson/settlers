/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PLANK;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class StorageWorker extends Worker {

    private static final int RESTING_TIME = 19;

    private final Countdown countdown;
    private final Map<Class<? extends Building>, Integer> assignedFood;
    private final Map<Class<? extends Building>, Integer> assignedCoal;

    private State state;
    private Storehouse ownStorehouse;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DELIVERING_CARGO_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public StorageWorker(Player player, GameMap map) {
        super(player, map);

        state = State.WALKING_TO_TARGET;

        countdown = new Countdown();

        countdown.countFrom(RESTING_TIME);

        /* Set the initial assignments of food to zero */
        assignedFood = new HashMap<>();

        assignedFood.put(GoldMine.class, 0);
        assignedFood.put(IronMine.class, 0);
        assignedFood.put(CoalMine.class, 0);
        assignedFood.put(GraniteMine.class, 0);

        /* Set the initial assignments of coal to zero */
        assignedCoal = new HashMap<>();

        assignedCoal.put(IronSmelter.class, 0);
        assignedCoal.put(Mint.class, 0);
        assignedCoal.put(Armory.class, 0);
    }

    // FIXME: HOTSPOT
    private Cargo tryToStartDelivery() {

        for (Material material : getPlayer().getTransportPrioritiesForEachMaterial()) {

            /* Don't try to deliver materials that are not in stock */
            if (!ownStorehouse.isInStock(material)) {
                continue;
            }

            /* Send out the material if it's ordered to be pushed out */
            if (ownStorehouse.isPushedOut(material)) {

                /* Find receiving storehouse */
                Storehouse receivingStorehouse = getPlayer().getClosestStorage(getHome().getPosition(), getHome());

                Cargo cargo = ownStorehouse.retrieve(material);

                /* Deliver to the building if it exists, otherwise just put the cargo on the flag */
                if (receivingStorehouse != null) {

                    receivingStorehouse.promiseDelivery(material);

                    cargo.setTarget(receivingStorehouse);
                }

                return cargo;
            }

            /* Iterate over all buildings, instead of just the ones that can be reached from the headquarter

               This will perform the fast tests first and only perform the expensive test if the quick ones pass
            */
            for (Building building : getPlayer().getBuildings()) {

                /* Don't deliver to itself */
                if (ownStorehouse.equals(building)) {
                    continue;
                }

                /* Don't deliver to burning or destroyed buildings */
                if (building.isBurningDown() || building.isDestroyed()) {
                    continue;
                }

                /* Make sure planks are only used for plank production if the amount is critically low */
                if (material == PLANK) {

                    if (getPlayer().isTreeConservationProgramActive() &&
                        !(building instanceof Sawmill)     &&
                        !(building instanceof ForesterHut) &&
                        !(building instanceof Woodcutter)) {

                        continue;
                    }
                }

                /* Check if the building needs the material */
                if (!building.needsMaterial(material)) {
                    continue;
                }

                /* Check that the building type is within its assigned quota */
                if (!isWithinQuota(building, material) && !(resetAllocationIfNeeded(material) && isWithinQuota(building, material))) {
                    continue;
                }

                /* Filter out buildings that cannot be reached from the storage */
                if (!map.arePointsConnectedByRoads(getHome().getPosition(), building.getPosition())) {
                    continue;
                }

                /* Deliver to the building */
                building.promiseDelivery(material);

                Cargo cargo = ownStorehouse.retrieve(material);
                cargo.setTarget(building);

                /* Track allocation */
                trackAllocation(building, material);

                return cargo;
            }
        }

        return null;
    }

    @Override
    protected void onEnterBuilding(Building building) {
        ownStorehouse = (Storehouse)building;

        state = State.RESTING_IN_HOUSE;
    }

    @Override
    protected void onIdle() {
        if (state == State.RESTING_IN_HOUSE) {
            if (countdown.hasReachedZero()) {

                if (getHome().getFlag().hasPlaceForMoreCargo()) {
                    Cargo cargo = tryToStartDelivery();

                    if (cargo != null) {
                        setCargo(cargo);

                        setTarget(getHome().getFlag().getPosition());

                        state = State.DELIVERING_CARGO_TO_FLAG;

                        getHome().getFlag().promiseCargo(getCargo());
                    }
                }
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() {
        if (state == State.DELIVERING_CARGO_TO_FLAG) {
            Flag flag = getHome().getFlag();

            flag.putCargo(getCargo());

            setCargo(null);

            returnHome();

            state = State.GOING_BACK_TO_HOUSE;
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());

            state = State.RESTING_IN_HOUSE;

            countdown.countFrom(RESTING_TIME);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storehouse storehouse = (Storehouse)map.getBuildingAtPoint(getPosition());

            storehouse.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() {
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

    private boolean resetAllocationIfNeeded(Material material) {

        if (isFood(material)) {

        /* Reset count if all building types have reached their quota */
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, GoldMine.class, material)    ||
                  overQuota(GoldMine.class))                                  &&
                (!needyConsumerExists(reachableBuildings, IronMine.class, material)    ||
                  overQuota(IronMine.class))                                  &&
                (!needyConsumerExists(reachableBuildings, CoalMine.class, material)    ||
                  overQuota(CoalMine.class))                                  &&
                (!needyConsumerExists(reachableBuildings, GraniteMine.class, material) ||
                  overQuota(GraniteMine.class))) {
                assignedFood.put(GoldMine.class, 0);
                assignedFood.put(IronMine.class, 0);
                assignedFood.put(CoalMine.class, 0);
                assignedFood.put(GraniteMine.class, 0);

                return true;
            }
        } else if (material == COAL) {

            /* Reset count if all building types have reached their quota */
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, IronSmelter.class, COAL)  ||
                  overQuota(IronSmelter.class))                                    &&
                (!needyConsumerExists(reachableBuildings, Mint.class, COAL)         ||
                  overQuota(Mint.class))                                           &&
                (!needyConsumerExists(reachableBuildings, Armory.class, COAL)       ||
                  overQuota(Armory.class))) {
                assignedCoal.put(IronSmelter.class, 0);
                assignedCoal.put(Mint.class, 0);
                assignedCoal.put(Armory.class, 0);

                return true;
            }
        }

        return false;
    }

    private void trackAllocation(Building building, Material material) {

        if (isFood(material)) {

            int amount = assignedFood.get(building.getClass());
            assignedFood.put(building.getClass(), amount + 1);

        } else if (material == COAL) {
            int amount = assignedCoal.get(building.getClass());
            assignedCoal.put(building.getClass(), amount + 1);
        }
    }

    private boolean isFood(Material material) {
        return material == FISH || material == BREAD || material == MEAT;
    }

    private boolean isWithinQuota(Building building, Material material) {

        /* Handle quota for food */
        if (isFood(material)) {
            int quota = getPlayer().getFoodQuota(building.getClass());

            return assignedFood.get(building.getClass()) < quota;
        }

        /* Handle quota for coal */
        if (material == COAL) {
            int quota = getPlayer().getCoalQuota(building.getClass());

            return assignedCoal.get(building.getClass()) < quota;
        }

        /* All other materials are without quota */
        return true;
    }

    private boolean overQuota(Class<? extends Building> buildingType) {

        /* Handle food quota for mines */
        if (buildingType.equals(GoldMine.class) ||
            buildingType.equals(IronMine.class) ||
            buildingType.equals(CoalMine.class) ||
            buildingType.equals(GraniteMine.class)) {
            return assignedFood.get(buildingType) >= getPlayer().getFoodQuota(buildingType);
        }

        /* Handle coal quota for coal consumers */
        if (buildingType.equals(IronSmelter.class) ||
            buildingType.equals(Mint.class)        ||
            buildingType.equals(Armory.class)) {
            return assignedCoal.get(buildingType) >= getPlayer().getCoalQuota(buildingType);
        }

        /* All other buildings have no quota */
        return false;
    }

    private boolean needyConsumerExists(Collection<Building> buildings, Class<? extends Building> aClass, Material material) {

        for (Building building : buildings) {
            if (building.getClass().equals(aClass) &&
                building.isReady()                   &&
                building.needsMaterial(material)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {

            /* Don't try to enter the storage upon arrival */
            clearTargetBuilding();

            /* Go back to the storage */
            returnToStorage();
        }
    }
}
