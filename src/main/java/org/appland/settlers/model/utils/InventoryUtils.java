package org.appland.settlers.model.utils;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Storehouse;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;

public class InventoryUtils {

    static List<Material> WORKERS_TO_COUNT_IN_INVENTORY = List.of(
            BUILDER,
            PLANER,
            WOODCUTTER_WORKER,
            FORESTER,
            STONEMASON,
            FISHERMAN,
            HUNTER,
            CARPENTER,
            FARMER,
            PIG_BREEDER,
            DONKEY_BREEDER,
            MILLER,
            BAKER,
            BUTCHER,
            BREWER,
            MINER,
            IRON_FOUNDER,
            ARMORER,
            MINTER,
            METALWORKER,
            SHIPWRIGHT,
            GEOLOGIST,
            SCOUT,
            DONKEY
    );

    public static int countGoodsInInventory(Storehouse storage) {
        return Material.GOODS.stream().mapToInt(storage::getAmount).sum();
    }

    /**
     * Puts a specified amount of cargos of a material into a building.
     *
     * @param material The material to be put into the building.
     * @param amount   The amount of cargos to put into the building.
     * @param building The building where the cargos are to be placed.
     */
    public static void putCargos(Material material, int amount, Building building) {
        var map = building.getMap();

        for (int i = 0; i < amount; i++) {
            var cargo = new Cargo(material, map);
            building.promiseDelivery(material);
            building.putCargo(cargo);
        }
    }

    /**
     * Retrieves a specified amount of cargos of a material from a storehouse.
     *
     * @param storehouse The storehouse from which to retrieve cargos.
     * @param material   The material to retrieve from the storehouse.
     * @param amount     The amount of cargos to retrieve.
     */
    public static void retrieveCargos(Storehouse storehouse, Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            storehouse.retrieve(material);
        }
    }

    /**
     * Create a worker of the given type. In the case of helpers, consider the given building.
     * @param material
     * @param building
     * @param player
     * @param map
     * @return
     */
    public static Worker createWorker(Material material, Building building, Player player, GameMap map) {
        if (material == HELPER) {
            return switch (building) {
                case Mill mill -> new Miller(player, map);
                case DonkeyFarm donkeyFarm -> new DonkeyBreeder(player, map);
                default -> throw new InvalidGameLogicException(format("Can't create helper for %s", building));
            };
        }

        return switch (material) {
            case FORESTER -> new Forester(player, map);
            case WOODCUTTER_WORKER -> new WoodcutterWorker(player, map);
            case STONEMASON -> new Stonemason(player, map);
            case FARMER -> new Farmer(player, map);
            case CARPENTER -> new Carpenter(player, map);
            case WELL_WORKER -> new WellWorker(player, map);
            case MILLER -> new Miller(player, map);
            case BAKER -> new Baker(player, map);
            case STOREHOUSE_WORKER -> new StorehouseWorker(player, map);
            case FISHERMAN -> new Fisherman(player, map);
            case MINER -> new Miner(player, map);
            case IRON_FOUNDER -> new IronFounder(player, map);
            case BREWER -> new Brewer(player, map);
            case MINTER -> new Minter(player, map);
            case ARMORER -> new Armorer(player, map);
            case PIG_BREEDER -> new PigBreeder(player, map);
            case BUTCHER -> new Butcher(player, map);
            case GEOLOGIST -> new Geologist(player, map);
            case DONKEY_BREEDER -> new DonkeyBreeder(player, map);
            case SCOUT -> new Scout(player, map);
            case CATAPULT_WORKER -> new CatapultWorker(player, map);
            case HUNTER -> new Hunter(player, map);
            case METALWORKER -> new Metalworker(player, map);
            case DONKEY -> new Donkey(player, map);
            case COURIER -> new Courier(player, map);
            case PRIVATE -> new Soldier(player, PRIVATE_RANK, map);
            case PRIVATE_FIRST_CLASS -> new Soldier(player, PRIVATE_FIRST_CLASS_RANK, map);
            case SERGEANT -> new Soldier(player, SERGEANT_RANK, map);
            case OFFICER -> new Soldier(player, OFFICER_RANK, map);
            case GENERAL -> new Soldier(player, GENERAL_RANK, map);
            case BUILDER -> new Builder(player, map);
            case SHIPWRIGHT -> new Shipwright(player, map);
            default -> throw new InvalidGameLogicException(
                    format("Can't retrieve worker of type %s", material));
        };
    }

    /**
     * Counts the amount of workers in the inventory of the given storehouse. Soldiers and couriers ("helpers") are
     * not counted.
     * @param storehouse
     * @return
     */
    public static int countWorkersInInventory(Storehouse storehouse) {
        return WORKERS_TO_COUNT_IN_INVENTORY.stream().mapToInt(storehouse::getAmount).sum();
    }

    /**
     * Enum for types of allocation that can be controlled.
     */
    public enum AllocationType {
        WHEAT_ALLOCATION,
        COAL_ALLOCATION,
        WATER_ALLOCATION,
        FOOD_ALLOCATION,
        PLANK_ALLOCATION,
        IRON_BAR_ALLOCATION
    }

    /**
     * Tracks allocations for resources and materials in the game.
     * Manages the quota and allocation of these resources.
     */
    public static class AllocationTracker {
        private static final Map<AllocationType, Set<Class<? extends Building>>> AFFECTED_BUILDING_TYPES = new HashMap<>();
        private static final Map<AllocationType, Set<Material>> TRACKED_MATERIALS = new HashMap<>();

        static {
            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.WHEAT_ALLOCATION,
                    Set.of(Mill.class, Brewery.class, DonkeyFarm.class, PigFarm.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.COAL_ALLOCATION,
                    Set.of(Mint.class, Metalworks.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.WATER_ALLOCATION,
                    Set.of(Bakery.class, Brewery.class, DonkeyFarm.class, PigFarm.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.FOOD_ALLOCATION,
                    Set.of(CoalMine.class, IronMine.class, GoldMine.class, GraniteMine.class)
            );

            AFFECTED_BUILDING_TYPES.put(
                    AllocationType.IRON_BAR_ALLOCATION,
                    Set.of(Armory.class, Metalworks.class)
            );

            TRACKED_MATERIALS.put(AllocationType.WHEAT_ALLOCATION, Set.of(WHEAT));
            TRACKED_MATERIALS.put(AllocationType.COAL_ALLOCATION, Set.of(COAL));
            TRACKED_MATERIALS.put(AllocationType.WATER_ALLOCATION, Set.of(WATER));
            TRACKED_MATERIALS.put(AllocationType.IRON_BAR_ALLOCATION, Set.of(IRON_BAR));
            TRACKED_MATERIALS.put(AllocationType.PLANK_ALLOCATION, Set.of(PLANK));
            TRACKED_MATERIALS.put(AllocationType.FOOD_ALLOCATION, Set.of(BREAD, FISH, MEAT));
        }

        private final Map<Class<? extends Building>, Integer> consumed = new HashMap<>();
        private final AllocationType allocationType;
        private final Player player;
        private final Point position;

        public AllocationTracker(AllocationType allocationType, Player player, Point position) {
            this.player = player;
            this.allocationType = allocationType;
            this.position = position;
        }

        /**
         * Tracks the allocation of resources for a building by incrementing the consumed count.
         *
         * @param building The building for which the allocation is tracked.
         */
        public void trackAllocation(Building building) {
            consumed.merge(building.getClass(), 1, Integer::sum);
        }

        /**
         * Determines if delivery is allowed for a building based on the allocation type.
         *
         * @param building The building to check for delivery allowance.
         * @return True if delivery is allowed, false otherwise.
         */
        public boolean isDeliveryAllowed(Building building) {
            var material = TRACKED_MATERIALS.get(allocationType).stream().findFirst().get();
            return isDeliveryAllowed(building, material);
        }

        /**
         * Determines if delivery is allowed for a building given a specific material.
         *
         * @param building The building to check for delivery allowance.
         * @param material The material to be delivered.
         * @return True if delivery is allowed, false otherwise.
         */
        public boolean isDeliveryAllowed(Building building, Material material) {
            int quota = quotaForBuilding(building);

            var withinQuota = consumed.getOrDefault(building.getClass(), 0) < quota;

            if (withinQuota) {
                return true;
            }

            var didReset = resetAllocationIfNeeded(material);

            return didReset && consumed.getOrDefault(building.getClass(), 0) < quota;
        }

        private int quotaForBuilding(Building building) {
            return quotaForBuildingType(building.getClass());
        }

        private int quotaForBuildingType(Class<? extends Building> buildingType) {
            return switch (allocationType) {
                case WHEAT_ALLOCATION -> player.getWheatQuota(buildingType);
                case COAL_ALLOCATION -> player.getCoalQuota(buildingType);
                case FOOD_ALLOCATION -> player.getFoodQuota(buildingType);
                case WATER_ALLOCATION -> player.getWaterQuota(buildingType);
                case IRON_BAR_ALLOCATION -> player.getIronBarQuota(buildingType);
                case PLANK_ALLOCATION -> throw new RuntimeException("Plank allocation is not implemented yet.");
            };
        }

        /**
         * Checks if a building type is over its allocation quota.
         *
         * @param buildingType The type of building to check.
         * @return True if the building type is over quota, false otherwise.
         */
        public boolean isOverQuota(Class<? extends Building> buildingType) {
            return consumed.getOrDefault(buildingType, 0) >= quotaForBuildingType(buildingType);
        }

        /**
         * Resets allocation if needed based on the material and reachable buildings.
         *
         * @param material The material to check for allocation reset.
         * @return True if the allocation was reset, false otherwise.
         */
        public boolean resetAllocationIfNeeded(Material material) {
            var reachableBuildings = GameUtils.getBuildingsWithinReach(position, player);

            if (AFFECTED_BUILDING_TYPES.get(allocationType).stream().allMatch(buildingType ->
                    !needyConsumerExists(reachableBuildings, buildingType, material) || isOverQuota(buildingType)
            )) {
                AFFECTED_BUILDING_TYPES.get(allocationType).forEach(buildingType -> consumed.put(buildingType, 0));

                return true;
            }

            return false;
        }

        private boolean needyConsumerExists(Collection<Building> buildings, Class<? extends Building> buildingType, Material material) {
            return buildings.stream().anyMatch(building ->
                    building.getClass().equals(buildingType) &&
                    building.isReady() &&
                    building.needsMaterial(material));
        }
    }
}
