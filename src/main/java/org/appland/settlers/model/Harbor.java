package org.appland.settlers.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static org.appland.settlers.model.Material.BUILDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Size.MEDIUM;

@MilitaryBuilding(maxHostedMilitary = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE, STONE, STONE, STONE, STONE})
@RequiresWorker(workerType = STORAGE_WORKER)
public class Harbor extends Storehouse {

    private final Map<Material, Integer> REQUIRED_FOR_EXPEDITION;
    private final Map<Material, Integer> promisedMaterialForNextExpedition;
    private final Map<Material, Integer> materialForNextExpedition;

    private State expeditionState;
    private boolean isOwnSettlement;
    private Ship promisedShip;
    private boolean needToShipMaterialToOtherHarbor;

    private enum State {
        NO_EXPEDITION_PLANNED,
        COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION,
        COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION

    }

    public Harbor(Player player0) {
        super(player0);

        materialForNextExpedition = new HashMap<>();

        expeditionState = State.NO_EXPEDITION_PLANNED;
        promisedMaterialForNextExpedition = new HashMap<>();

        isOwnSettlement = false;

        REQUIRED_FOR_EXPEDITION = new HashMap<>();

        REQUIRED_FOR_EXPEDITION.put(PLANK, 4);
        REQUIRED_FOR_EXPEDITION.put(STONE, 6);
        REQUIRED_FOR_EXPEDITION.put(BUILDER, 1);

        promisedShip = null;
    }

    boolean hasTaskForShip() {
        return needToShipMaterialToOtherHarbor ||
                (expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION && promisedShip == null);
    }

    public boolean isReadyForExpedition() {
        return expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION && promisedShip == null;
    }

    public void prepareForExpedition() {

        boolean allMaterialCollected = true;

        /* Start working to buffer material for the next expedition */
        expeditionState = State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION;

        /* Include existing material in the expedition */
        for (Entry<Material, Integer> entry : REQUIRED_FOR_EXPEDITION.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();

            int amountToUse = Math.min(getAmount(material), requiredAmount);

            GameUtils.retrieveCargos(this, material, amountToUse);
            materialForNextExpedition.put(material, amountToUse);

            if (requiredAmount != amountToUse) {
                allMaterialCollected = false;
            }
        }

        if (allMaterialCollected) {
            expeditionState = State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION;
        }
    }

    @Override
    public void promiseDelivery(Material material) {

        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && (material == PLANK || material == STONE || material == BUILDER)) {

            int current = materialForNextExpedition.getOrDefault(material, 0);
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);
            int needed = REQUIRED_FOR_EXPEDITION.getOrDefault(material, 0);

            if (current + promised < needed) {
                promisedMaterialForNextExpedition.put(material, promised + 1);
            } else {
                super.promiseDelivery(material);
            }

        } else {
            super.promiseDelivery(material);
        }
    }

    @Override
    public boolean needsMaterial(Material material) {

        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && (material == PLANK || material == STONE || material == BUILDER)) {

            int current = materialForNextExpedition.getOrDefault(material, 0);
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);
            int needed = REQUIRED_FOR_EXPEDITION.getOrDefault(material, 0);

            return current + promised < needed || super.needsMaterial(material);

        } else {
            return super.needsMaterial(material);
        }
    }

    @Override
    public void putCargo(Cargo cargo) {
        Material material = cargo.getMaterial();

        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && (material == PLANK || material == STONE || material == BUILDER)) {

            int current = materialForNextExpedition.getOrDefault(material, 0);
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);

            if (promised > 0) {

                materialForNextExpedition.put(material, current + 1);
                promisedMaterialForNextExpedition.put(material, promised - 1);

                /* Put the cargo in a waiting ship if all material is available and there is one */

                /* Check if all needed material is available */
                boolean expeditionMaterialDone = true;

                for (Entry<Material, Integer> pair : REQUIRED_FOR_EXPEDITION.entrySet()) {
                    Material requiredMaterial = pair.getKey();
                    int requiredAmount = pair.getValue();

                    if (materialForNextExpedition.getOrDefault(requiredMaterial, 0) != requiredAmount) {
                        expeditionMaterialDone = false;
                    }
                }

                if (expeditionMaterialDone) {
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

        if (expeditionState == State.COLLECTING_MATERIAL_FOR_NEXT_EXPEDITION && (material == PLANK || material == STONE || material == BUILDER)) {

            int current = materialForNextExpedition.getOrDefault(material, 0);
            int promised = promisedMaterialForNextExpedition.getOrDefault(material, 0);

            if (promised > 0) {
                materialForNextExpedition.put(material, current + 1);
                promisedMaterialForNextExpedition.put(material, promised - 1);
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

    void addShipReadyForTask(Ship ship) {

        /* Start an expedition if needed */
        if (expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION) {
            for (Material material : materialForNextExpedition.keySet()) {
                int amount = materialForNextExpedition.get(material);

                ship.putCargos(material, amount, null);

                materialForNextExpedition.put(material, 0);
            }

            ship.setReadyForExpedition();

            expeditionState = State.NO_EXPEDITION_PLANNED;

            /* Report that the ship is ready for an expedition */
            getPlayer().reportShipReadyForExpedition(ship);

        /* Ship material to other harbors if needed */
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
    void onConstructionFinished() {

        /* Report that the harbor is finished */
        getPlayer().reportHarborReady(this);

        /* Add a storage worker manually if this is a separate settlement */
        if (isOwnSettlement) {
            Player player = getPlayer();
            GameMap map = getMap();

            StorageWorker storageWorker = new StorageWorker(player, map);

            map.placeWorker(storageWorker, getFlag());
            storageWorker.setTargetBuilding(this);
            promiseWorker(storageWorker);
        }
    }

    void setOwnSettlement() {
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
    void onBuildingOccupied() {
        getMap().updateBorder(this, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
    }

    public void promiseShip(Ship ship) {
        promisedShip = ship;
    }

    @Override
    void onStepTime() {

        /* Find out if there is a need to ship something to another harbor */
        if (isOccupied()) {

            needToShipMaterialToOtherHarbor = getPlayer().getBuildings().stream()

                    /* Find all harbors that are not this one */
                    .filter(building -> !Objects.equals(building, this))
                    .filter(Building::isHarbor)
                    .filter(Building::isReady)
                    .map(building -> (Harbor) building)

                    /* Look for any material that needs shipping and that this harbor has in store */
                    .anyMatch(harbor -> harbor.getMaterialNeedingShippingAsStream()
                                .anyMatch(material -> getAmount(material) > 0)
                    );
        }
    }

    private Stream<Material> getMaterialNeedingShippingAsStream() {

        List<Building> buildings = getPlayer().getBuildings();
        GameMap map = getMap();

        return Arrays.stream(Material.values())

                /* Find materials needed */
                .filter(material -> buildings.stream().anyMatch(building -> building.needsMaterial(material)))

                /* Find materials that need shipping - i.e. that are not available locally */
                .filter(material -> buildings.stream()
                                .filter(building -> !Objects.equals(this, building))
                                .filter(Building::isStorehouse)
                                .filter(Building::isReady)
                                .filter(storeHouse -> GameUtils.areBuildingsOrFlagsConnected(this, storeHouse, map))
                                .noneMatch(localStoreHouse -> localStoreHouse.getAmount(material) > 0)
                );
    }

    public boolean needsToShipToOtherHarbors() {
        return needToShipMaterialToOtherHarbor;
    }

    /**
     * Answers the question - what do other harbors need from this harbor?
     *
     * @return Returns a nested map with information about how much resources each harbor needs
     */
    public Map<Harbor, Map<Material, Integer>> getNeededShipmentsFromThisHarbor() {
        Map<Harbor, Map<Material, Integer>> neededShipmentsFromThisHarbor = new HashMap<>();

        for (Building building : getPlayer().getBuildings()) {

            if (!building.isReady()) {
                continue;
            }

            if (!(building instanceof Harbor )) {
                continue;
            }

            Harbor otherHarbor = (Harbor) building;

            if (this.equals(otherHarbor)) {
                continue;
            }

            neededShipmentsFromThisHarbor.put(otherHarbor, new HashMap<>());

            for (Entry<Material, Integer> entry : otherHarbor.getShipmentNeededForSettlement().entrySet()) {
                Material material = entry.getKey();
                int amount = entry.getValue();

                int currentAmount = neededShipmentsFromThisHarbor.get(otherHarbor).getOrDefault(material, 0);

                neededShipmentsFromThisHarbor.get(otherHarbor).put(material, currentAmount + amount);
            }
        }

        return neededShipmentsFromThisHarbor;
    }

    Map<Material, Integer> getShipmentNeededForSettlement() {

        Map<Material, Integer> shipmentsNeeded = new HashMap<>();

        /* Get all buildings within reach */
        Set<Building> reachableBuildings = GameUtils.getBuildingsWithinReach(getFlag());

        /* Remove this harbor */
        reachableBuildings.remove(this);

        /* Collect needed material for each building */
        for (Building building : reachableBuildings) {

            for (Material material : building.getMaterialNeeded()) {
                int amountNeededByHouse = building.getTotalAmountNeeded(material);
                int amountInStorage = getAmount(material);

                int currentAmountNeeded = shipmentsNeeded.getOrDefault(material, 0);

                shipmentsNeeded.put(material, currentAmountNeeded + amountNeededByHouse - amountInStorage);
            }
        }

        return shipmentsNeeded;
    }
}
