/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Woodcutter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.appland.settlers.model.Material.*;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class StorehouseWorker extends Worker {
    private static final int RESTING_TIME = 19;

    private final Countdown countdown = new Countdown();
    private final Map<Class<? extends Building>, Integer> assignedFood = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> assignedCoal = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> assignedWheat = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> assignedWater = new HashMap<>();
    private final Map<Class<? extends Building>, Integer> assignedIronBars = new HashMap<>();

    private State state = State.WALKING_TO_TARGET;
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

    public StorehouseWorker(Player player, GameMap map) {
        super(player, map);

        countdown.countFrom(RESTING_TIME);

        // Set the initial assignments of food to zero
        assignedFood.put(GoldMine.class, 0);
        assignedFood.put(IronMine.class, 0);
        assignedFood.put(CoalMine.class, 0);
        assignedFood.put(GraniteMine.class, 0);

        // Set the initial assignments of coal to zero
        assignedCoal.put(IronSmelter.class, 0);
        assignedCoal.put(Mint.class, 0);
        assignedCoal.put(Armory.class, 0);

        // Set the initial assignments of wheat to zero
        assignedWheat.put(Mill.class, 0);
        assignedWheat.put(DonkeyFarm.class, 0);
        assignedWheat.put(PigFarm.class, 0);
        assignedWheat.put(Brewery.class, 0);

        // Set the initial assignments of water to zero
        assignedWater.put(Bakery.class, 0);
        assignedWater.put(DonkeyFarm.class, 0);
        assignedWater.put(PigFarm.class, 0);
        assignedWater.put(Brewery.class, 0);

        // Set the initial assignments of iron bars to zero
        assignedIronBars.put(Armory.class, 0);
        assignedIronBars.put(Metalworks.class, 0);
    }

    // FIXME: HOTSPOT
    private Cargo tryToStartDelivery() {
        for (var material : player.getTransportPrioritiesForEachMaterial()) {

            // Don't try to deliver materials that are not in stock
            if (!ownStorehouse.isInStock(material)) {
                continue;
            }

            // Send out the material if it's ordered to be pushed out
            if (ownStorehouse.isPushedOut(material)) {

                // Find receiving storehouse
                var receivingStorehouse = player.getClosestStorage(home.getPosition(), home);

                var cargo = ownStorehouse.retrieve(material);

                // Deliver to the building if it exists, otherwise just put the cargo on the flag
                if (receivingStorehouse != null) {
                    receivingStorehouse.promiseDelivery(material);

                    cargo.setTarget(receivingStorehouse);
                }

                return cargo;
            }

            /* Iterate over all buildings, instead of just the ones that can be reached from the headquarters

               This will perform the fast tests first and only perform the expensive test if the quick ones pass
            */
            for (var building : player.getBuildings()) {

                // Don't deliver to itself
                if (ownStorehouse.equals(building)) {
                    continue;
                }

                // Don't deliver to burning or destroyed buildings
                if (building.isBurningDown() || building.isDestroyed()) {
                    continue;
                }

                // Make sure planks are only used for plank production if the amount is critically low
                if (material == PLANK) {
                    if (player.isTreeConservationProgramActive() &&
                        !(building instanceof Sawmill)     &&
                        !(building instanceof ForesterHut) &&
                        !(building instanceof Woodcutter)) {

                        continue;
                    }
                }

                // Check if the building needs the material
                if (!building.needsMaterial(material)) {
                    continue;
                }

                // Check that the building type is within its assigned quota
                if (!isWithinQuota(building, material) && !(resetAllocationIfNeeded(material) && isWithinQuota(building, material))) {
                    continue;
                }

                // Filter out buildings that cannot be reached from the storage
                if (!map.arePointsConnectedByRoads(home.getPosition(), building.getPosition())) {
                    continue;
                }

                // Deliver to the building
                building.promiseDelivery(material);

                var cargo = ownStorehouse.retrieve(material);
                cargo.setTarget(building);

                // Track allocation
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
                if (home.getFlag().hasPlaceForMoreCargo() && !home.getFlag().isFightingAtFlag()) {
                    var cargo = tryToStartDelivery();

                    if (cargo != null) {
                        setCargo(cargo);
                        home.getFlag().promiseCargo(getCargo());

                        setTarget(home.getFlag().getPosition());
                        state = State.DELIVERING_CARGO_TO_FLAG;
                    }
                }

                // If the storage worker didn't start a new delivery to the flag
                if (state != State.DELIVERING_CARGO_TO_FLAG) {

                    // See if there is any cargo on the flag that has been rerouted and should go back to the storage
                    for (var cargo : home.getFlag().getStackedCargo()) {

                        // Filter materials that are blocked
                        if (ownStorehouse.isDeliveryBlocked(cargo.getMaterial())) {
                            continue;
                        }

                        if (Objects.equals(cargo.getTarget(), home)) {
                            cargoToReturn = cargo;

                            state = State.WALKING_TO_FLAG_TO_PICK_UP_RETURNED_CARGO;
                            setTarget(home.getFlag().getPosition());

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
            var flag = home.getFlag();

            flag.putCargo(getCargo());
            setCargo(null);

            state = State.GOING_BACK_TO_HOUSE;
            returnHome();
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            enterBuilding(home);

            state = State.RESTING_IN_HOUSE;
            countdown.countFrom(RESTING_TIME);
        } else if (state == State.RETURNING_TO_STORAGE) {
            var storehouse = (Storehouse)map.getBuildingAtPoint(position);

            storehouse.depositWorker(this);
        } else if (state == State.WALKING_TO_FLAG_TO_PICK_UP_RETURNED_CARGO) {
            home.getFlag().retrieveCargo(cargoToReturn);

            // TODO: can the cargo be gone when the storage worker gets to the flag?

            setCargo(cargoToReturn);

            state = State.WALKING_TO_HOME_TO_DELIVER_CARGO;
            setTarget(home.getPosition());
        } else if (state == State.WALKING_TO_HOME_TO_DELIVER_CARGO) {
            home.putCargo(getCargo());
            setCargo(null);

            state = State.RESTING_IN_HOUSE;
        }
    }

    @Override
    protected void onReturnToStorage() {
        var storage = GameUtils.getClosestStorageConnectedByRoads(position, player);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroad(player, position);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            }
        }
    }

    private boolean resetAllocationIfNeeded(Material material) {

        if (material.isFood()) {

        // Reset count if all building types have reached their quota
            var reachableBuildings = GameUtils.getBuildingsWithinReach(home.getFlag());

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

            // Reset count if all building types have reached their quota
            var reachableBuildings = GameUtils.getBuildingsWithinReach(home.getFlag());

            if ((!needyConsumerExists(reachableBuildings, IronSmelter.class, COAL) || overQuota(IronSmelter.class, material)) &&
                (!needyConsumerExists(reachableBuildings, Mint.class, COAL) || overQuota(Mint.class, material)) &&
                (!needyConsumerExists(reachableBuildings, Armory.class, COAL) || overQuota(Armory.class, material))) {
                assignedCoal.put(IronSmelter.class, 0);
                assignedCoal.put(Mint.class, 0);
                assignedCoal.put(Armory.class, 0);

                return true;
            }
        } else if (material == WHEAT) {

            // Reset count if all type of buildings have reached their quota
            var reachableBuildings = GameUtils.getBuildingsWithinReach(home.getFlag());

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

            // Reset count if all type of buildings have reached their quota
            var reachableBuildings = GameUtils.getBuildingsWithinReach(home.getFlag());

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
        } else if (material == IRON_BAR) {

            // Reset count if all type of buildings have reached their quota
            var reachableBuildings = GameUtils.getBuildingsWithinReach(home.getFlag());

            if ((!needyConsumerExists(reachableBuildings, Armory.class, IRON_BAR) || overQuota(Armory.class, IRON_BAR)) &&
                (!needyConsumerExists(reachableBuildings, Metalworks.class, IRON_BAR) || overQuota(Metalworks.class, IRON_BAR))) {
                assignedIronBars.put(Armory.class, 0);
                assignedIronBars.put(Metalworks.class, 0);
            }

            return true;
        }

        return false;
    }

    private void trackAllocation(Building building, Material material) {
        if (material.isFood()) {
            assignedFood.compute(building.getClass(), (k, amount) -> amount + 1);
        } else if (material == COAL) {
            assignedCoal.compute(building.getClass(), (k, amount) -> amount + 1);
        } else if (material == WHEAT) {
            assignedWheat.compute(building.getClass(), (k, amount) -> amount + 1);
        } else if (material == WATER) {
            assignedWater.compute(building.getClass(), (k, amount) -> amount + 1);
        } else if (material == IRON_BAR) {
            assignedIronBars.compute(building.getClass(), (k, amount) -> amount + 1);
        }
    }

    private boolean isWithinQuota(Building building, Material material) {
        if (material.isFood()) {
            int quota = player.getFoodQuota(building.getClass());
            return assignedFood.get(building.getClass()) < quota;
        }

        if (material == COAL) {
            int quota = player.getCoalQuota(building.getClass());
            return assignedCoal.get(building.getClass()) < quota;
        }

        if (material == WHEAT) {
            int quota = player.getWheatQuota(building.getClass());
            return assignedWheat.get(building.getClass()) < quota;
        }

        if (material == WATER) {
            int quota = player.getWaterQuota(building.getClass());
            return assignedWater.get(building.getClass()) < quota;
        }

        if (material == IRON_BAR) {
            int quota = player.getIronBarQuota(building.getClass());
            return assignedIronBars.get(building.getClass()) < quota;
        }

        // All other materials are without quota
        return true;
    }

    private boolean overQuota(Class<? extends Building> buildingType, Material material) {

        // Handle food quota for mines
        if (material.isFood() &&
            (buildingType.equals(GoldMine.class) ||
            buildingType.equals(IronMine.class) ||
            buildingType.equals(CoalMine.class) ||
            buildingType.equals(GraniteMine.class))) {
            return assignedFood.get(buildingType) >= player.getFoodQuota(buildingType);
        }

        // Handle coal quota for coal consumers
        if (material == COAL &&
            (buildingType.equals(IronSmelter.class) ||
            buildingType.equals(Mint.class) ||
            buildingType.equals(Armory.class))) {
            return assignedCoal.get(buildingType) >= player.getCoalQuota(buildingType);
        }

        // Handle wheat quota for wheat consumers
        if (material == WHEAT &&
            (buildingType.equals(Mill.class) ||
            buildingType.equals(DonkeyFarm.class) ||
            buildingType.equals(PigFarm.class) ||
            buildingType.equals(Brewery.class))) {
            return assignedWheat.get(buildingType) >= player.getWheatQuota(buildingType);
        }

        // Handle water quota for consumers
        if (material == WATER &&
            (buildingType.equals(Bakery.class) ||
            buildingType.equals(DonkeyFarm.class) ||
            buildingType.equals(PigFarm.class) ||
            buildingType.equals(Brewery.class))) {
            return assignedWater.get(buildingType) >= player.getWaterQuota((buildingType));
        }

        // Handle iron bar quota for consumers
        if (material == IRON_BAR &&
            (buildingType.equals(Armory.class) ||
            buildingType.equals(Metalworks.class))) {
            return assignedIronBars.get(buildingType) >= player.getIronBarQuota(buildingType);
        }

        // All other buildings have no quota
        return false;
    }

    private boolean needyConsumerExists(Collection<Building> buildings, Class<? extends Building> aClass, Material material) {
        for (var building : buildings) {
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

        // Return to storage if the planned path no longer exists
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, target)) {

            // Don't try to enter the storage upon arrival
            clearTargetBuilding();

            // Go back to the storage
            returnToStorage();
        }
    }
}
