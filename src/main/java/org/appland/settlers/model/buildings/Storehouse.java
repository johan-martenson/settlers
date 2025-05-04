package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Map.entry;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE, STONE})
@RequiresWorker(workerType = STOREHOUSE_WORKER)
public class Storehouse extends Building {
    private static final int TIME_TO_CREATE_NEW_SOLDIER = 100;
    private static final Set<Material> CAN_CREATE_WORKER_WITHOUT_TOOL = Set.of(WELL_WORKER, HELPER, BREWER, PIG_BREEDER);
    private static final Map<Material, Material> WORKER_TO_TOOL_MAP = Map.ofEntries(
            entry(WOODCUTTER_WORKER, AXE),
            entry(FORESTER, SHOVEL),
            entry(STONEMASON, PICK_AXE),
            entry(FISHERMAN, FISHING_ROD),
            entry(HUNTER, BOW),
            entry(SCOUT, BOW),
            entry(CARPENTER, SAW),
            entry(BUTCHER, CLEAVER),
            entry(BAKER, ROLLING_PIN),
            entry(IRON_FOUNDER, CRUCIBLE),
            entry(ARMORER, TONGS),
            entry(MINTER, CRUCIBLE),
            entry(MINER, PICK_AXE),
            entry(FARMER, SCYTHE),
            entry(BUILDER, HAMMER),
            entry(SHIPWRIGHT, HAMMER),
            entry(GEOLOGIST, PICK_AXE),
            entry(METALWORKER, HAMMER)
    );

    private final Countdown draftCountdown = new Countdown();
    private final Set<Material> materialToPushOut = EnumSet.noneOf(Material.class);
    private final Set<Material> materialBlockedForDelivery = EnumSet.noneOf(Material.class);

    final Map<Material, Integer> inventory = new EnumMap<>(Material.class);

    public Storehouse(Player player) {
        super(player);
    }

    void draftMilitary() {
        var privatesToDraft = GameUtils.min(
                inventory.getOrDefault(SWORD, 0),
                inventory.getOrDefault(SHIELD, 0),
                inventory.getOrDefault(BEER, 0)
        );

        inventory.merge(PRIVATE, privatesToDraft, Integer::sum);
        inventory.merge(BEER, -privatesToDraft, Integer::sum);
        inventory.merge(SHIELD, -privatesToDraft, Integer::sum);
        inventory.merge(SWORD, -privatesToDraft, Integer::sum);

        map.getStatisticsManager().soldiersDrafted(getPlayer(), map.getTime(), privatesToDraft);
    }

    @Override
    public void stepTime() {
        super.stepTime();

        // Handle draft with delay.
        if (isDraftPossible(inventory)) {
            if (draftCountdown.hasReachedZero()) {
                draftMilitary();
                draftCountdown.countFrom(TIME_TO_CREATE_NEW_SOLDIER);
            } else if (draftCountdown.isCounting()) {
                draftCountdown.step();
            } else {
                draftCountdown.countFrom(TIME_TO_CREATE_NEW_SOLDIER);
            }
        }

        var sentOutWorker = assignNewWorkerToUnoccupiedPlaces();

        if (!sentOutWorker) {
            sentOutWorker = materialToPushOut.stream()
                    .filter(material -> getAmount(material) > 0)
                    .filter(Material::isWorker)
                    .findFirst()
                    .map(material -> {
                        Worker worker = retrieveWorker(material, null);
                        map.placeWorker(worker, this);
                        worker.goToOtherStorage(this);

                        return true;
                    })
                    .orElse(false);
        }

        // Send workers needed in other storehouses.
        if (!sentOutWorker) {
            Material.WORKERS.stream()
                    .filter(worker -> getAmount(worker) > 0)
                    .forEach(workerType -> getPlayer().getBuildings().stream()
                            .filter(building -> !Objects.equals(building, this)) // Skip this storehouse
                            .filter(building -> building instanceof Storehouse) // Filter buildings that are not storehouses
                            .filter(Building::isReady) // Filter storehouses that are not ready
                            .filter(storehouse -> storehouse.needsMaterial(workerType)) // Filter storehouses that don't need the worker
                            .findFirst() // Find the first suitable storehouse
                            .ifPresent(storehouse -> {
                                Worker worker = retrieveWorker(workerType, null);
                                map.placeWorker(worker, this);
                                worker.goToStorehouse((Storehouse) storehouse);
                                storehouse.promiseDelivery(workerType);
                            })
                    );
        }
    }

    private boolean assignNewWorkerToUnoccupiedPlaces() {
        return assignCouriers() || assignDonkeys() || assignBuildersToPlannedBuildings()
                || assignWorkerToUnoccupiedBuildings() || assignGeologists() || assignScouts();
    }

    private boolean assignBuildersToPlannedBuildings() {
        if (!hasAtLeastOne(BUILDER) && !hasAtLeastOne(HAMMER)) {
            return false;
        }

        return getPlayer().getBuildings().stream()
                .filter(building -> !building.equals(this))
                .filter(building -> building.needsBuilder() && map.findWayWithExistingRoads(getPosition(), building.getPosition()) != null)
                .filter(building -> {
                    Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), building, getPlayer());
                    return storehouse == null || equals(storehouse) || !storehouse.hasAtLeastOne(BUILDER);
                })
                .findFirst()
                .map(building -> {
                    Worker builder = retrieveWorker(BUILDER, null);
                    map.placeWorker(builder, this);
                    builder.setTargetBuilding(building);
                    building.promiseBuilder((Builder) builder);
                    return true;
                })
                .orElse(false);
    }

    private boolean assignGeologists() {

        // Leave if there are no scouts in this storage.
        if (!hasAtLeastOne(GEOLOGIST) && !hasAtLeastOne(WORKER_TO_TOOL_MAP.get(GEOLOGIST))) {
            return false;
        }

        // Go through the flags and look for flags waiting for geologists.
        return map.getFlags().stream()
                .filter(Flag::needsGeologist)
                .filter(flag -> map.arePointsConnectedByRoads(getPosition(), flag.getPosition()))
                .filter(flag -> isClosestStorage(this))
                .findFirst()
                .map(flag -> {
                    var geologist = (Geologist) retrieveWorker(GEOLOGIST, null);
                    map.placeWorker(geologist, this);
                    geologist.setTarget(flag.getPosition());
                    flag.geologistSent();
                    return true;
                })
                .orElse(false);
    }

    private boolean assignScouts() {
        if (!hasAtLeastOne(SCOUT)) {
            if (hasAtLeastOne(BOW)) {
                System.out.println("Created scout");
                inventory.merge(BOW, -1, Integer::sum);
                inventory.merge(SCOUT, 1, Integer::sum);

                map.getStatisticsManager().workerCreated(player, map.getTime());
            } else {
                return false;
            }
        }

        // Go through flags and look for flags that are waiting for scouts.
        return map.getFlags().stream()
                .filter(Flag::needsScout)
                .filter(flag -> map.arePointsConnectedByRoads(getPosition(), flag.getPosition()))
                .filter(flag -> isClosestStorage(this))
                .findFirst()
                .map(flag -> {
                    Scout scout = (Scout) retrieveWorker(SCOUT, null);
                    map.placeWorker(scout, this);
                    scout.setTarget(flag.getPosition());
                    flag.scoutSent();

                    return true;
                })
                .orElse(false);
    }

    private boolean assignWorkerToUnoccupiedBuildings() {
        return getPlayer().getBuildings().stream()
                .filter(building -> !building.equals(this) && !building.isBurningDown() && !building.isDestroyed())
                .filter(building -> {
                    if (building.isMilitaryBuilding() && !building.isHarbor()) {
                        if (!hasMilitary()) {
                            return false;
                        }

                        if (!building.needsMilitaryManning()) {
                            return false;
                        }

                        if (map.findWayWithExistingRoads(getPosition(), building.getPosition()) == null) {
                            return false;
                        }

                        Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), building, getPlayer());
                        return storehouse == null || equals(storehouse) || !storehouse.hasMilitary();
                    } else if (building.needsWorker()) {
                        var material = building.getWorkerType();
                        var toolForWorker = WORKER_TO_TOOL_MAP.get(material);
                        var hasWorker = hasAtLeastOne(material);
                        var canMakeWorker = (toolForWorker != null && hasAtLeastOne(toolForWorker))
                                || CAN_CREATE_WORKER_WITHOUT_TOOL.contains(material);

                        if (!hasWorker && !canMakeWorker) {
                            return false;
                        }

                        if (map.findWayWithExistingRoads(getPosition(), building.getPosition()) == null) {
                            return false;
                        }

                        var storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), building, getPlayer());
                        return storehouse == null || equals(storehouse) || !storehouse.hasAtLeastOne(material);
                    }

                    return false;
                })
                .findFirst()
                .map(building -> {
                    if (building.isMilitaryBuilding() && !building.isHarbor()) {
                        Soldier military = retrieveSoldierToPopulateBuilding();
                        map.placeWorker(military, this);
                        military.setTargetBuilding(building);
                        building.promiseSoldier(military);
                    } else if (building.needsWorker()) {
                        Worker worker = retrieveWorker(building.getWorkerType(), building);
                        map.placeWorker(worker, this);
                        worker.setTargetBuilding(building);
                        building.promiseWorker(worker);
                    }
                    return true;
                })
                .orElse(false);
    }

    private boolean assignCouriers() {
        return hasAtLeastOne(COURIER) && map.getRoads().stream()
                    .filter(road -> road.getPlayer().equals(getPlayer()) && road.needsCourier())
                    .filter(road -> {
                        Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(road.getStart(), getPlayer());
                        return equals(storehouse); // Ensure the current storehouse is the closest.
                    })
                    .findFirst()
                    .map(road -> {
                        Courier courier = retrieveCourier();
                        map.placeWorker(courier, this);
                        courier.assignToRoad(road);
                        return true;
                    }).orElse(false);
    }

    private boolean isDraftPossible(Map<Material, Integer> inventory) {
        return inventory.getOrDefault(BEER, 0) > 0
                && inventory.getOrDefault(SWORD, 0) > 0
                && inventory.getOrDefault(SHIELD, 0) > 0;
    }

    @Override
    public void putCargo(Cargo cargo) {
        if (!isReady()) {
            super.putCargo(cargo);
        } else {
            storeOneInInventory(cargo.getMaterial());
            getPlayer().reportProduction(cargo.getMaterial(), this);
        }
    }

    public Cargo retrieve(Material material) {
        if (!hasAtLeastOne(material)) {
            throw new InvalidGameLogicException(String.format("Can't retrieve %s", material));
        }

        retrieveOneFromInventory(material);

        var cargo = new Cargo(material, map);
        cargo.setPosition(getFlag().getPosition());
        player.reportChangedInventory(this);

        return cargo;
    }

    public boolean isInStock(Material material) {
        return hasAtLeastOne(material);
    }

    public void depositWorker(Worker worker) {
        var material = Material.workerToMaterial(worker);

        storeOneInInventory(material);
        map.removeWorker(worker);
        getPlayer().reportChangedInventory(this);
    }

    public Worker retrieveWorker(Material workerType, Building building) {
        var hasWorker = hasAtLeastOne(workerType);
        var canMakeWorkerWithoutTool = CAN_CREATE_WORKER_WITHOUT_TOOL.contains(workerType);
        var tool = WORKER_TO_TOOL_MAP.get(workerType);
        var canMakeWorkerWithTool = tool != null && hasAtLeastOne(WORKER_TO_TOOL_MAP.get(workerType));

        if (!hasWorker && !canMakeWorkerWithoutTool && !canMakeWorkerWithTool) {
            throw new InvalidGameLogicException(String.format("Can't retrieve %s", workerType));
        }

        if (!hasWorker && canMakeWorkerWithoutTool) {
            map.getStatisticsManager().workerCreatedWithoutTool(player, map.getTime());
        } else if (!hasWorker && canMakeWorkerWithTool) {
            inventory.merge(tool, -1, Integer::sum);
            inventory.merge(workerType, 1, Integer::sum);

            map.getStatisticsManager().workerCreated(player, map.getTime());
        }

        var worker = GameUtils.createWorker(workerType, building, player, map);
        worker.setPosition(getFlag().getPosition());
        retrieveOneFromInventory(workerType);

        return worker;
    }

    public Soldier retrieveSoldierFromInventory(Soldier.Rank rank) {
        return retrieveSoldierFromInventory(rank.toMaterial());
    }

    public Soldier retrieveSoldierFromInventory(Material material) {
        if (!hasAtLeastOne(material)) {
            throw new InvalidGameLogicException(String.format("Can't retrieve soldier %s", material));
        }

        retrieveOneFromInventory(material);

        var rank = material.toRank();

        var soldier = new Soldier(getPlayer(), rank, map);

        soldier.setPosition(getFlag().getPosition());

        return soldier;
    }

    public Courier retrieveCourier() {
        // The storage never runs out of couriers
        var courier = new Courier(getPlayer(), map);
        courier.setPosition(getFlag().getPosition());
        return courier;
    }

    public Soldier retrieveSoldierToPopulateBuilding() {

        /* Go through the list in order of preference and try to retrieve a soldier */
        for (var rank : GameUtils.strengthToRank(getPlayer().getStrengthOfSoldiersPopulatingBuildings())) {
            var preferredSoldierType = rank.toMaterial();

            if (hasAtLeastOne(preferredSoldierType)) {
                retrieveOneFromInventory(preferredSoldierType);

                var soldier = new Soldier(getPlayer(), rank, map);
                soldier.setPosition(getFlag().getPosition());

                return soldier;
            }
        }

        throw new InvalidGameLogicException("Can't retrieve soldier!");
    }

    private boolean hasAtLeastOne(Material material) {
        return material == COURIER || material == CATAPULT_WORKER ||
                inventory.getOrDefault(material, 0) > 0;
    }

    private void retrieveOneFromInventory(Material material) {
        if (material == COURIER) {
            return;
        } else if (material == CATAPULT_WORKER) {
            return;
        } else if (material == WELL_WORKER && inventory.getOrDefault(WELL_WORKER, 0) == 0) {
            return;
        }

        player.reportChangedInventory(this);

        inventory.merge(material, -1, Integer::sum);
    }

    private void storeOneInInventory(Material material) {
        if (material != COURIER && material != CATAPULT_WORKER) {
            inventory.merge(material, 1, Integer::sum);
        }
    }

    @Override
    public int getAmount(Material material) {
        if (!isReady()) {
            return super.getAmount(material);
        }

        return material == COURIER ? 1 : inventory.getOrDefault(material, 0);
    }

    private boolean hasMilitary() {
        return inventory.getOrDefault(PRIVATE, 0) > 0 ||
                inventory.getOrDefault(PRIVATE_FIRST_CLASS, 0) > 0 ||
                inventory.getOrDefault(SERGEANT, 0) > 0 ||
                inventory.getOrDefault(OFFICER, 0) > 0 ||
                inventory.getOrDefault(GENERAL, 0) > 0;
    }

    private boolean isClosestStorage(Building building) {
        var storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), getPlayer());
        return equals(storehouse);
    }

    private boolean assignDonkeys() {
        if (!hasAtLeastOne(DONKEY)) {
            return false;
        }

        return map.getRoads().stream()
                .filter(road -> road.getPlayer().equals(getPlayer())) // Filter roads that belong to the player
                .filter(Road::isMainRoad) // Filter only main roads
                .filter(Road::needsDonkey) // Filter roads that need a donkey
                .filter(road -> {
                    Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(road.getStart(), getPlayer());
                    return storehouse == null || equals(storehouse); // Ensure the current storehouse is the closest
                })
                .findFirst() // Find the first suitable road
                .map(road -> {
                    Donkey donkey = retrieveDonkey();
                    map.placeWorker(donkey, getFlag());
                    donkey.assignToRoad(road);
                    return true;
                })
                .orElse(false);
    }

    private Donkey retrieveDonkey() {
        if (hasAtLeastOne(DONKEY)) {
            retrieveOneFromInventory(DONKEY);

            return new Donkey(getPlayer(), map);
        }

        return null;
    }

    @Override
    public void stopProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Can't stop production in storage");
    }

    @Override
    public void resumeProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Can't resume production in storage");
    }

    @Override
    public void onConstructionFinished() {
        getPlayer().reportStorageReady(this);
    }

    public void pushOutAll(Material material) {
        materialToPushOut.add(material);
    }

    public boolean isPushedOut(Material material) {
        return materialToPushOut.contains(material);
    }

    public void blockDeliveryOfMaterial(Material water) {
        materialBlockedForDelivery.add(water);
    }

    public boolean isDeliveryBlocked(Material material) {
        return materialBlockedForDelivery.contains(material);
    }

    @Override
    public boolean isStorehouse() {
        return true;
    }

    public Collection<Cargo> retrieve(Material material, int amount) {
        var cargos = new ArrayList<Cargo>();

        int cargosToReturn = Math.min(amount, getAmount(material));

        for (int i = 0; i < cargosToReturn; i++) {
            cargos.add(retrieve(material));
        }

        return cargos;
    }

    @Override
    public Map<Material, Integer> getInventory() {
        if (isReady()) {
            return inventory;
        }

        return super.getInventory();
    }
}
