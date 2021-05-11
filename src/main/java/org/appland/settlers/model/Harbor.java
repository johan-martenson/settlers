package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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
    private final List<Ship> shipsWaitingForExpedition;

    private State expeditionState;
    private boolean isOwnSettlement;

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
        shipsWaitingForExpedition = new ArrayList<>();
    }

    public boolean isReadyForExpedition() {
        return expeditionState == State.COLLECTED_MATERIAL_FOR_NEXT_EXPEDITION;
    }

    private Ship getAvailableShipForExpedition() {

        /* Look for a ship that can go on the expedition */
        for (Ship ship : getMap().getShips()) {

            /* Filter ships that are not ready */
            if (!ship.isReady()) {
                continue;
            }

            /* Filter ships that belong to other players */
            if (!Objects.equals(ship.getPlayer(), getPlayer())) {
                continue;
            }

            return ship;
        }

        return null;
    }

    public void startExpedition(Direction direction) throws InvalidUserActionException {

        /* Get the ship */
        Ship ship = getAvailableShipForExpedition();

        if (ship == null) {
            throw new InvalidUserActionException("Cannot start expedition because there is no available ship");
        }

        /* Select target */
        for (Point point : getMap().getPossiblePlacesForHarbor()) {

            if (Objects.equals(getPosition(), point)) {
                continue;
            }

            if (!Objects.equals(GameUtils.getDirection(getPosition(), point), direction)) {
                continue;
            }

            /* Pick the point as target for the expedition */
            List<Point> path = getMap().findWayForShip(ship.getPosition(), point.downRight());

            ship.setOffroadTargetWithPath(path);

            break;
        }

        if (ship.getTarget() == null) {
            throw new InvalidUserActionException("No suitable target in this direction: " + direction);
        }
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

    public void addShipReadyForTask(Ship ship) {
        for (Material material : materialForNextExpedition.keySet()) {
            int amount = materialForNextExpedition.get(material);

            ship.putCargos(material, amount, null);

            materialForNextExpedition.put(material, 0);
        }

        ship.setReadyForExpedition();

        expeditionState = State.NO_EXPEDITION_PLANNED;

        /* Report that the ship is ready for an expedition */
        getPlayer().reportShipReadyForExpedition(ship);
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
    void onBuildingOccupied() {
        getMap().updateBorder(this, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
    }
}
