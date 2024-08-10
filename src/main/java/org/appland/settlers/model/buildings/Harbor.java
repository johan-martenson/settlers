package org.appland.settlers.model.buildings;

import org.appland.settlers.model.BorderChangeCause;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.Worker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.MEDIUM;

@MilitaryBuilding(maxHostedSoldiers = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE, STONE, STONE, STONE, STONE})
@RequiresWorker(workerType = STOREHOUSE_WORKER)
public class Harbor extends Storehouse {
    private static final Map<Material, Integer> REQUIRED_FOR_EXPEDITION = Map.of(
            PLANK, 4,
            STONE, 6,
            BUILDER, 1
    );

    private final Map<Material, Integer> promisedMaterialForNextExpedition = new HashMap<>();
    private final Map<Material, Integer> materialForNextExpedition = new HashMap<>();

    private State expeditionState = State.NO_EXPEDITION_PLANNED;
    private boolean isOwnSettlement = false;
    private Ship promisedShip;
    private boolean needToShipMaterialToOtherHarbor;

    private enum State {
        NO_EXPEDITION_PLANNED,
        COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION,
        COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION
    }

    public Harbor(Player player0) {
        super(player0);
    }

    public boolean hasTaskForShip() {
        return needToShipMaterialToOtherHarbor ||
                (expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION && promisedShip == null);
    }

    public boolean isReadyForExpedition() {
        return expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION && promisedShip == null;
    }

    public void prepareForExpedition() {
        expeditionState = State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION;

        for (Entry<Material, Integer> entry : REQUIRED_FOR_EXPEDITION.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();

            int amountToUse = Math.min(getAmount(material), requiredAmount);

            GameUtils.retrieveCargos(this, material, amountToUse);
            materialForNextExpedition.put(material, amountToUse);
        }

        if (isAllExpeditionMaterialCollected()) {
            expeditionState = State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION;
        }
    }

    @Override
    public void promiseDelivery(Material material) {
        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && REQUIRED_FOR_EXPEDITION.containsKey(material)) {
            int current = materialForNextExpedition.getOrDefault(material, 0);
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);
            int needed = REQUIRED_FOR_EXPEDITION.getOrDefault(material, 0);

            if (current + promised < needed) {
                promisedMaterialForNextExpedition.merge(material, 1, Integer::sum);
            } else {
                super.promiseDelivery(material);
            }

        } else {
            super.promiseDelivery(material);
        }
    }

    @Override
    public boolean needsMaterial(Material material) {
        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && REQUIRED_FOR_EXPEDITION.containsKey(material)) {
            int current = materialForNextExpedition.getOrDefault(material, 0);
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);
            int needed = REQUIRED_FOR_EXPEDITION.getOrDefault(material, 0);

            return current + promised < needed || super.needsMaterial(material);
        } else {
            return super.needsMaterial(material);
        }
    }

    private boolean isAllExpeditionMaterialCollected() {
        return REQUIRED_FOR_EXPEDITION.entrySet().stream()
                .allMatch(entry -> {
                    var material = entry.getKey();
                    var requiredAmount = entry.getValue();

                    return (int)materialForNextExpedition.getOrDefault(material, 0) == requiredAmount;
                });
    }

    @Override
    public void putCargo(Cargo cargo) {
        Material material = cargo.getMaterial();

        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && REQUIRED_FOR_EXPEDITION.containsKey(material)) {
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);

            if (promised > 0) {
                materialForNextExpedition.merge(material, 1, Integer::sum);
                promisedMaterialForNextExpedition.merge(material, -1, Integer::sum);

                if (isAllExpeditionMaterialCollected()) {
                    expeditionState = State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION;
                }
            } else {
                super.putCargo(cargo);
            }

        } else {
            super.putCargo(cargo);
        }
    }

    @Override
    public void depositWorker(Worker worker) {
        Material material = Material.workerToMaterial(worker);

        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && REQUIRED_FOR_EXPEDITION.containsKey(material)) {
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);

            if (promised > 0) {
                materialForNextExpedition.merge(material, 1, Integer::sum);
                promisedMaterialForNextExpedition.merge(material, -1, Integer::sum);
            } else {
                super.depositWorker(worker);
            }
        } else {
            super.depositWorker(worker);
        }
    }

    public Map<Material, Integer> getMaterialForExpedition() {
        return materialForNextExpedition;
    }

    public void addShipReadyForTask(Ship ship) {
        if (expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION) {
            materialForNextExpedition.forEach(ship::putCargos);
            materialForNextExpedition.clear();

            ship.setReadyForExpedition();
            expeditionState = State.NO_EXPEDITION_PLANNED;

            getPlayer().reportShipReadyForExpedition(ship);
        } else if (needToShipMaterialToOtherHarbor) {

            // What does each settlement need?
            // Go through materials in priority order
            // Create cargos
            // Put on ship
        }

        promisedShip = null;
    }

    public boolean isCollectingMaterialForExpedition() {
        return expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION;
    }

    @Override
    public boolean isHarbor() {
        return true;
    }

    @Override
    public void onConstructionFinished() {
        getPlayer().reportHarborReady(this);

        /* Add a storage worker manually if this is a separate settlement */
        if (isOwnSettlement) {
            Player player = getPlayer();
            GameMap map = getMap();

            StorehouseWorker storehouseWorker = new StorehouseWorker(player, map);

            map.placeWorker(storehouseWorker, getFlag());
            storehouseWorker.setTargetBuilding(this);
            promiseWorker(storehouseWorker);
        }
    }

    public void setOwnSettlement() {
        isOwnSettlement = true;
    }

    @Override
    public boolean isMilitaryBuilding() {
        return true;
    }

    public boolean isOwnSettlement() {
        return isOwnSettlement;
    }

    @Override
    public void onBuildingOccupied() {
        getMap().updateBorder(this, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
    }

    public void promiseShip(Ship ship) {
        promisedShip = ship;
    }

    @Override
    public void onStepTime() {
        if (isOccupied()) {
            needToShipMaterialToOtherHarbor = getPlayer().getBuildings().stream()

                    // Find all harbors that aren't this one
                    .filter(building -> !Objects.equals(building, this))
                    .filter(Building::isHarbor)
                    .filter(Building::isReady)
                    .map(building -> (Harbor) building)

                    // Look for any material that needs shipping and that this harbor has in store
                    .anyMatch(harbor -> harbor.getMaterialNeedingShippingAsStream()
                                .anyMatch(material -> getAmount(material) > 0));
        }
    }

    private Stream<Material> getMaterialNeedingShippingAsStream() {
        List<Building> buildings = getPlayer().getBuildings();
        GameMap map = getMap();

        return Arrays.stream(Material.values())

                // Find materials needed
                .filter(material -> buildings.stream().anyMatch(building -> building.needsMaterial(material)))

                // Find materials that need shipping - i.e. that are not available locally
                .filter(material -> buildings.stream()
                                .filter(building -> !Objects.equals(this, building))
                                .filter(Building::isStorehouse)
                                .filter(Building::isReady)
                                .filter(storeHouse -> GameUtils.areBuildingsOrFlagsConnected(this, storeHouse, map))
                                .noneMatch(localStoreHouse -> localStoreHouse.getAmount(material) > 0));
    }

    public boolean needsToShipToOtherHarbors() {
        return needToShipMaterialToOtherHarbor;
    }

    /**
     * Answers the question - what do other harbors need from this harbor?
     *
     * @return Returns a nested map with information about how many resources each harbor needs
     */
    public Map<Harbor, Map<Material, Integer>> getNeededShipmentsFromThisHarbor() {
        return getPlayer().getBuildings().stream()
                .filter(Building::isReady)
                .filter(building -> building instanceof Harbor)
                .filter(building -> !this.equals(building))
                .map(Harbor.class::cast)
                .collect(Collectors.toMap(
                        harbor -> harbor,
                        Harbor::getShipmentNeededForSettlement
                ));
    }

    Map<Material, Integer> getShipmentNeededForSettlement() {
        Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getFlag());
        reachableBuildings.remove(this);

        return reachableBuildings.stream()
                .flatMap(building -> building.getTypesOfMaterialNeeded().stream()
                        .map(material -> Map.entry(material, building.getCanHoldAmount(material) - getAmount(material))))
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        Integer::sum
                ));
    }
}
