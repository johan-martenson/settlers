/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.model.Material.*;

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
    private final Map<Class<? extends Building>, Integer> assignedWheat;
    private final Map<Class<? extends Building>, Integer> assignedWater;

    private State state;
    private Storehouse ownStorehouse;
    private Cargo cargoToReturn;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DELIVERING_CARGO_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        WALKING_TO_FLAG_TO_PICK_UP_RETURNED_CARGO,
        WALKING_TO_HOME_TO_DELIVER_CARGO,
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

        /* Set the initial assignments of wheat to zero */
        assignedWheat = new HashMap<>();

        assignedWheat.put(Mill.class, 0);
        assignedWheat.put(DonkeyFarm.class, 0);
        assignedWheat.put(PigFarm.class, 0);
        assignedWheat.put(Brewery.class, 0);

        /* Set the initial assignments of water to zero */
        assignedWater = new HashMap<>();

        assignedWater.put(Bakery.class, 0);
        assignedWater.put(DonkeyFarm.class, 0);
        assignedWater.put(PigFarm.class, 0);
        assignedWater.put(Brewery.class, 0);
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

            /* Iterate over all buildings, instead of just the ones that can be reached from the headquarters

               This will perform the fast tests first and only perform the expensive test if the quick ones pass
            */
            for (Building building : player.getBuildings()) {

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

                // If the storage worker didn't start a new delivery to the flag
                if (state != State.DELIVERING_CARGO_TO_FLAG) {

                    // See if there is any cargo on the flag that has been rerouted and should go back to the storage
                    for (Cargo cargo : getHome().getFlag().getStackedCargo()) {

                        // Filter materials that are blocked
                        if (ownStorehouse.isDeliveryBlocked(cargo.getMaterial())) {
                            continue;
                        }

                        if (Objects.equals(cargo.getTarget(), getHome())) {
                            cargoToReturn = cargo;

                            state = State.WALKING_TO_FLAG_TO_PICK_UP_RETURNED_CARGO;

                            setTarget(getHome().getFlag().getPosition());

                            break;
                        }
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
        } else if (state == State.WALKING_TO_FLAG_TO_PICK_UP_RETURNED_CARGO) {
            getHome().getFlag().retrieveCargo(cargoToReturn);

            // TODO: can the cargo be gone when the storage worker gets to the flag?

            setCargo(cargoToReturn);

            state = State.WALKING_TO_HOME_TO_DELIVER_CARGO;

            setTarget(getHome().getPosition());
        } else if (state == State.WALKING_TO_HOME_TO_DELIVER_CARGO) {
            getHome().putCargo(getCargo());

            setCargo(null);

            state = State.RESTING_IN_HOUSE;
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

        if (material.isFood()) {

        /* Reset count if all building types have reached their quota */
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, GoldMine.class, material) || overQuota(GoldMine.class, material)) &&
                (!needyConsumerExists(reachableBuildings, IronMine.class, material) || overQuota(IronMine.class, material)) &&
                (!needyConsumerExists(reachableBuildings, CoalMine.class, material) || overQuota(CoalMine.class, material)) &&
                (!needyConsumerExists(reachableBuildings, GraniteMine.class, material) || overQuota(GraniteMine.class, material))) {
                assignedFood.put(GoldMine.class, 0);
                assignedFood.put(IronMine.class, 0);
                assignedFood.put(CoalMine.class, 0);
                assignedFood.put(GraniteMine.class, 0);

                return true;
            }
        } else if (material == COAL) {

            /* Reset count if all building types have reached their quota */
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, IronSmelter.class, COAL) || overQuota(IronSmelter.class, material)) &&
                (!needyConsumerExists(reachableBuildings, Mint.class, COAL) || overQuota(Mint.class, material)) &&
                (!needyConsumerExists(reachableBuildings, Armory.class, COAL) || overQuota(Armory.class, material))) {
                assignedCoal.put(IronSmelter.class, 0);
                assignedCoal.put(Mint.class, 0);
                assignedCoal.put(Armory.class, 0);

                return true;
            }
        } else if (material == WHEAT) {

            /* Reset count if all type of buildings have reached their quota */
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, Mill.class, WHEAT) || overQuota(Mill.class, WHEAT)) &&
                (!needyConsumerExists(reachableBuildings, DonkeyFarm.class, WHEAT) || overQuota(DonkeyFarm.class, WHEAT)) &&
                (!needyConsumerExists(reachableBuildings, PigFarm.class, WHEAT) || overQuota(PigFarm.class, WHEAT)) &&
                (!needyConsumerExists(reachableBuildings, Brewery.class, WHEAT) || overQuota(Brewery.class, WHEAT))) {
                assignedWheat.put(Mill.class, 0);
                assignedWheat.put(DonkeyFarm.class, 0);
                assignedWheat.put(PigFarm.class, 0);
                assignedWheat.put(Brewery.class, 0);
            }

            return true;
        } else if (material == WATER) {

            /* Reset count if all type of buildings have reached their quota */
            Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, Bakery.class, WATER) || overQuota(Bakery.class, WATER)) &&
                (!needyConsumerExists(reachableBuildings, DonkeyFarm.class, WATER) || overQuota(DonkeyFarm.class, WATER)) &&
                (!needyConsumerExists(reachableBuildings, PigFarm.class, WATER) || overQuota(PigFarm.class, WATER)) &&
                (!needyConsumerExists(reachableBuildings, Brewery.class, WATER) || overQuota(Brewery.class, WATER))) {
                assignedWater.put(Bakery.class, 0);
                assignedWater.put(DonkeyFarm.class, 0);
                assignedWater.put(PigFarm.class, 0);
                assignedWater.put(Brewery.class, 0);
            }

            return true;
        }

        return false;
    }

    private void trackAllocation(Building building, Material material) {

        if (material.isFood()) {

            int amount = assignedFood.get(building.getClass());
            assignedFood.put(building.getClass(), amount + 1);

        } else if (material == COAL) {
            int amount = assignedCoal.get(building.getClass());
            assignedCoal.put(building.getClass(), amount + 1);
        } else if (material == WHEAT) {
            int amount = assignedWheat.get(building.getClass());
            assignedWheat.put(building.getClass(), amount + 1);
        } else if (material == WATER) {
            int amount = assignedWater.get(building.getClass());
            assignedWater.put(building.getClass(), amount + 1);
        }
    }

    private boolean isWithinQuota(Building building, Material material) {

        /* Handle quota for food */
        if (material.isFood()) {
            int quota = getPlayer().getFoodQuota(building.getClass());

            return assignedFood.get(building.getClass()) < quota;
        }

        /* Handle quota for coal */
        if (material == COAL) {
            int quota = getPlayer().getCoalQuota(building.getClass());

            return assignedCoal.get(building.getClass()) < quota;
        }

        /* Handle quota for wheat */
        if (material == WHEAT) {
            int quota = player.getWheatQuota(building.getClass());

            return assignedWheat.get(building.getClass()) < quota;
        }

        /* Handle quota for water */
        if (material == WATER) {
            int quota = player.getWaterQuota(building.getClass());

            return assignedWater.get(building.getClass()) < quota;
        }

        /* All other materials are without quota */
        return true;
    }

    private boolean overQuota(Class<? extends Building> buildingType, Material material) {

        /* Handle food quota for mines */
        if (material.isFood() &&
                (buildingType.equals(GoldMine.class) ||
                buildingType.equals(IronMine.class) ||
                buildingType.equals(CoalMine.class) ||
                buildingType.equals(GraniteMine.class))) {
            return assignedFood.get(buildingType) >= player.getFoodQuota(buildingType);
        }

        /* Handle coal quota for coal consumers */
        if (material == COAL &&
                (buildingType.equals(IronSmelter.class) ||
                buildingType.equals(Mint.class) ||
                buildingType.equals(Armory.class))) {
            return assignedCoal.get(buildingType) >= player.getCoalQuota(buildingType);
        }

        /* Handle wheat quota for wheat consumers */
        if (material == WHEAT &&
                (buildingType.equals(Mill.class) ||
                buildingType.equals(DonkeyFarm.class) ||
                buildingType.equals(PigFarm.class) ||
                buildingType.equals(Brewery.class))) {
            return assignedWheat.get(buildingType) >= player.getWheatQuota(buildingType);
        }

        /* Handle water quota for consumers */
        if (material == WATER &&
                (buildingType.equals(Bakery.class) ||
                buildingType.equals(DonkeyFarm.class) ||
                buildingType.equals(PigFarm.class) ||
                buildingType.equals(Brewery.class))) {
            return assignedWater.get(buildingType) >= player.getWaterQuota((buildingType));
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
