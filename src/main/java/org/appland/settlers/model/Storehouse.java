package org.appland.settlers.model;

import java.util.*;

import static org.appland.settlers.model.Material.ARMORER;
import static org.appland.settlers.model.Material.AXE;
import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BOW;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.BUILDER;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.CATAPULT_WORKER;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.HAMMER;
import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.METALWORKER;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.MINTER;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PICK_AXE;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SHIPWRIGHT;
import static org.appland.settlers.model.Material.SHOVEL;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STONEMASON;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.TONGS;
import static org.appland.settlers.model.Material.WELL_WORKER;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.OFFICER_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.appland.settlers.model.Size.MEDIUM;

@HouseSize(size = MEDIUM, material = {PLANK, PLANK, PLANK, PLANK, STONE, STONE, STONE})
@RequiresWorker(workerType = STORAGE_WORKER)
public class Storehouse extends Building {

    private static final int TIME_TO_CREATE_NEW_SOLDIER = 100;
    private final Countdown draftCountdown;
    private final Map<Material, Material> workerToToolMap;
    private final Set<Material> materialToPushOut;
    private final Set<Material> materialBlockedForDelivery;

    final Map<Material, Integer> inventory;

    public Storehouse(Player player) {
        super(player);

        inventory = new EnumMap<>(Material.class);

        draftCountdown = new Countdown();
        workerToToolMap = new EnumMap<>(Material.class);

        /* Set up the mapping between workers and tools */
        workerToToolMap.put(WOODCUTTER_WORKER, AXE);
        workerToToolMap.put(FORESTER, SHOVEL);
        workerToToolMap.put(STONEMASON, PICK_AXE);
        workerToToolMap.put(FISHERMAN, FISHING_ROD);
        workerToToolMap.put(HUNTER, BOW);
        workerToToolMap.put(SCOUT, BOW);
        workerToToolMap.put(SAWMILL_WORKER, SAW);
        workerToToolMap.put(BUTCHER, CLEAVER);
        workerToToolMap.put(BAKER, ROLLING_PIN);
        workerToToolMap.put(IRON_FOUNDER, CRUCIBLE);
        workerToToolMap.put(ARMORER, TONGS);
        workerToToolMap.put(MINTER, CRUCIBLE);
        workerToToolMap.put(MINER, PICK_AXE);
        workerToToolMap.put(FARMER, SCYTHE);
        workerToToolMap.put(BUILDER, HAMMER);
        workerToToolMap.put(SHIPWRIGHT, HAMMER);

        materialToPushOut = EnumSet.noneOf(Material.class);
        materialBlockedForDelivery = EnumSet.noneOf(Material.class);
    }

    /* This method updates the inventory as a side effect, without any locking */
    private void draftMilitary() {
        int swords  = inventory.getOrDefault(SWORD, 0);
        int shields = inventory.getOrDefault(SHIELD, 0);
        int beer    = inventory.getOrDefault(BEER, 0);

        int privatesToDraft = GameUtils.min(swords, shields, beer);
        int existingPrivates = inventory.getOrDefault(PRIVATE, 0);

        inventory.put(PRIVATE, existingPrivates + privatesToDraft);
        inventory.put(BEER, beer - privatesToDraft);
        inventory.put(SHIELD, shields - privatesToDraft);
        inventory.put(SWORD, swords - privatesToDraft);
    }

    @Override
    void stepTime() {
        super.stepTime();

        /* Handle draft with delay */
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

        /* Send out new workers */
        boolean sentOutWorker;

        sentOutWorker = assignNewWorkerToUnoccupiedPlaces();

        /* Send pushed out workers */
        if (!sentOutWorker) {
            for (Material material : materialToPushOut) {

                if (getAmount(material) <= 0) {
                    continue;
                }

                if (!material.isWorker()) {
                    continue;
                }

                Worker worker = retrieveWorker(material);

                getMap().placeWorker(worker, this);

                worker.goToOtherStorage(this);

                // FIXME: fix so that only one worker is pushed out

                sentOutWorker = true;
            }
        }

        /* Send workers needed in other storehouses */
        if (!sentOutWorker) {
            for (Material workerType : Material.WORKERS) {

                // FIXME: fix to filter workers that are not in store

                /* Go through each storehouse */
                for (Building building : getPlayer().getBuildings()) {

                    /* Skip this storehouse, so we don't try to deliver to ourselves */
                    if (Objects.equals(building, this)) {
                        continue;
                    }

                    /* Filter buildings that are not storehouses */
                    if (! (building instanceof Storehouse)) {
                        continue;
                    }

                    Storehouse storehouse = (Storehouse) building;

                    /* Filter storehouses that are not ready */
                    if (!storehouse.isReady()) {
                        continue;
                    }

                    /* Filter storehouses that don't need the worker */
                    if (!storehouse.needsMaterial(workerType)) {
                        continue;
                    }

                    Worker worker = retrieveWorker(workerType);

                    getMap().placeWorker(worker, this);

                    worker.goToStorehouse(storehouse);
                    storehouse.promiseDelivery(workerType);

                    // FIXME: fix so that only one worker is pushed out
                }
            }
        }
    }

    private boolean assignNewWorkerToUnoccupiedPlaces() {
        if (assignCouriers()) {
            return true;
        }

        if (assignDonkeys()) {
            return true;
        }

        if (assignBuildersToPlannedBuildings()) {
            return true;
        }

        if (assignWorkerToUnoccupiedBuildings()) {
            return true;
        }

        if (assignGeologists()) {
            return true;
        }

        if (assignScouts()) {
            return true;
        }

        return false;
    }

    private boolean assignBuildersToPlannedBuildings() {

        if (!hasAtLeastOne(BUILDER) && !hasAtLeastOne(Material.HAMMER)) {
            return false;
        }

        for (Building building : getPlayer().getBuildings()) {

            if (building.equals(this)) {
                continue;
            }

            if (!building.needsBuilder()) {
                continue;
            }

            /* Filter buildings that cannot be reached from this storehouse */
            if (getMap().findWayWithExistingRoads(getPosition(), building.getPosition()) == null) {
                continue;
            }

            /* Filter buildings that can get the worker assigned from a more local storehouse */
            Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), building, getPlayer());

            if (storehouse != null && !this.equals(storehouse) && storehouse.hasAtLeastOne(BUILDER)) {
                continue;
            }

            /* Assign the builder */
            Worker builder = retrieveWorker(BUILDER);

            getMap().placeWorker(builder, this);
            builder.setTargetBuilding(building);
            building.promiseBuilder((Builder) builder);

            return true;
        }

        return false;
    }

    private boolean assignGeologists() {

        /* Leave if there are no scouts in this storage */
        if (!hasAtLeastOne(GEOLOGIST)) {
            return false;
        }

        /* Go through the flags and look for flags waiting for geologists */
        for (Flag flag : getMap().getFlags()) {
            if (flag.needsGeologist()) {

                /* Don't send out scout if there is no way to the flag */
                if (!getMap().arePointsConnectedByRoads(getPosition(), flag.getPosition())) {
                    continue;
                }

                /* Don't send a geologist if there is a closer storage */
                if (!isClosestStorage(this)) {
                    continue;
                }

                /* Send a geologist to the flag */
                Geologist geologist = (Geologist)retrieveWorker(GEOLOGIST);

                getMap().placeWorker(geologist, this);
                geologist.setTarget(flag.getPosition());
                flag.geologistSent();

                return true;
            }
        }

        return false;
    }

    private boolean assignScouts() {

        /* Leave if there are no scouts in this storage */
        if (!hasAtLeastOne(SCOUT)) {

            if (hasAtLeastOne(BOW)) {
                int toolAmount = inventory.get(BOW);
                int scoutAmount = inventory.get(SCOUT);

                inventory.put(BOW, toolAmount - 1);
                inventory.put(SCOUT, scoutAmount + 1);
            } else {
                return false;
            }
        }

        /* Go through flags and look for flags that are waiting for scouts */
        for (Flag flag : getMap().getFlags()) {
            if (flag.needsScout()) {

                /* Don't send out a scout if there is no way to the flag */
                if (!getMap().arePointsConnectedByRoads(getPosition(), flag.getPosition())) {
                    continue;
                }

                /* Don't send out a scout if there is a closer storage */
                if (!isClosestStorage(this)) {
                    continue;
                }

                /* Send a scout to the flag */
                Scout scout = (Scout)retrieveWorker(SCOUT);

                getMap().placeWorker(scout, this);
                scout.setTarget(flag.getPosition());
                flag.scoutSent();

                return true;
            }
        }

        return false;
    }

    private boolean assignWorkerToUnoccupiedBuildings() {
        for (Building building : getPlayer().getBuildings()) {

            if (building.equals(this)) {
                continue;
            }

            if (building.isBurningDown()) {
                continue;
            }

            if (building.isDestroyed()) {
                continue;
            }

            if (building.isMilitaryBuilding() && !building.isHarbor()) {
                if (!hasMilitary()) {
                    continue;
                }

                if (!building.needsMilitaryManning()) {
                    continue;
                }

                /* Filter buildings that cannot be reached from this storehouse */
                if (getMap().findWayWithExistingRoads(getPosition(), building.getPosition()) == null) {
                    continue;
                }

                /* Filter buildings that can get the worker assigned from a more local storehouse */
                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), building, getPlayer());

                if (storehouse != null && !this.equals(storehouse) && storehouse.hasMilitary()) {
                    continue;
                }

                Military military = retrieveAnyMilitary();

                getMap().placeWorker(military, this);
                military.setTargetBuilding(building);
                building.promiseMilitary(military);

                return true;
            } else {
                if (building.needsWorker()) {

                    Material material = building.getWorkerType();
                    Material toolForWorker = getToolForWorker(material);

                    boolean hasWorker = hasAtLeastOne(material);
                    boolean canMakeWorker = toolForWorker != null && hasAtLeastOne(toolForWorker);

                    /* Filter buildings that need a worker that this storehouse cannot assign */
                    if (!hasWorker && !canMakeWorker) {
                        continue;
                    }

                    /* Filter buildings that cannot be reached from this storehouse */
                    if (getMap().findWayWithExistingRoads(getPosition(), building.getPosition()) == null) {
                        continue;
                    }

                    /* Filter buildings that can get the worker assigned from a more local storehouse */
                    Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), building, getPlayer());

                    if (storehouse != null && !this.equals(storehouse) && storehouse.hasAtLeastOne(material)) {
                        continue;
                    }

                    /* Assign the worker */
                    Worker worker = retrieveWorker(material);

                    getMap().placeWorker(worker, this);
                    worker.setTargetBuilding(building);
                    building.promiseWorker(worker);

                    return true;
                }
            }
        }

        return false;
    }

    private Material getToolForWorker(Material worker) {
        return workerToToolMap.get(worker);
    }

    private boolean assignCouriers() {

        if (hasAtLeastOne(COURIER)) {
            for (Road road : getMap().getRoads()) {
                if (!road.getPlayer().equals(getPlayer())) {
                    continue;
                }

                if (!road.needsCourier()) {
                    continue;
                }

                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(road.getStart(), getPlayer());

                if (!equals(storehouse)) {
                    continue;
                }

                Courier courier = storehouse.retrieveCourier();
                getMap().placeWorker(courier, storehouse);
                courier.assignToRoad(road);

                return true;
            }
        }

        return false;
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

            getPlayer().reportProduction(cargo.getMaterial());
        }
    }

    public Cargo retrieve(Material material) {

        if (!hasAtLeastOne(material)) {
            throw new InvalidGameLogicException("Can't retrieve " + material);
        }

        retrieveOneFromInventory(material);

        Cargo cargo = new Cargo(material, getMap());

        cargo.setPosition(getFlag().getPosition());


        return cargo;
    }

    public boolean isInStock(Material material) {
        return hasAtLeastOne(material);
    }

    public void depositWorker(Worker worker) {
        if (worker.isSoldier()) { // FIXME: deposit for soldier does not seem to work for some ranks
            Military military = (Military) worker;
            Material material;

            switch (military.getRank()) {
            case PRIVATE_RANK:
                material = PRIVATE;
                break;
            case SERGEANT_RANK:
                material = SERGEANT;
                break;
            case GENERAL_RANK:
                material = GENERAL;
                break;
            default:
                throw new InvalidGameLogicException("Can't handle military with rank " + military.getRank());
            }

            storeOneInInventory(material);
        } else if (worker instanceof Forester) {
            storeOneInInventory(FORESTER);
        } else if (worker instanceof WellWorker) {
            storeOneInInventory(WELL_WORKER);
        } else if (worker instanceof WoodcutterWorker) {
            storeOneInInventory(WOODCUTTER_WORKER);
        } else if (worker instanceof StorageWorker) {
            storeOneInInventory(STORAGE_WORKER);
        } else if (worker instanceof Butcher) {
            storeOneInInventory(BUTCHER);
        } else if (worker instanceof SawmillWorker) {
            storeOneInInventory(SAWMILL_WORKER);
        } else if (worker instanceof Stonemason) {
            storeOneInInventory(STONEMASON);
        } else if (worker instanceof PigBreeder) {
            storeOneInInventory(PIG_BREEDER);
        } else if (worker instanceof Minter) {
            storeOneInInventory(MINTER);
        } else if (worker instanceof Miller) {
            storeOneInInventory(MILLER);
        } else if (worker instanceof IronFounder) {
            storeOneInInventory(IRON_FOUNDER);
        } else if (worker instanceof Miner) {
            storeOneInInventory(MINER);
        } else if (worker instanceof Fisherman) {
            storeOneInInventory(FISHERMAN);
        } else if (worker instanceof Farmer) {
            storeOneInInventory(FARMER);
        } else if (worker instanceof Brewer) {
            storeOneInInventory(BREWER);
        } else if (worker instanceof Baker) {
            storeOneInInventory(BAKER);
        } else if (worker instanceof Armorer) {
            storeOneInInventory(ARMORER);
        } else if (worker instanceof Geologist) {
            storeOneInInventory(GEOLOGIST);
        } else if (worker instanceof DonkeyBreeder) {
            storeOneInInventory(DONKEY_BREEDER);
        } else if (worker instanceof Scout) {
            storeOneInInventory(SCOUT);
        } else if (worker instanceof Hunter) {
            storeOneInInventory(HUNTER);
        } else if (worker instanceof Metalworker) {
            storeOneInInventory(METALWORKER);
        } else if (worker instanceof Builder) {
            storeOneInInventory(BUILDER);
        } else if (worker instanceof Shipwright) {
            storeOneInInventory(SHIPWRIGHT);
        }

        getMap().removeWorker(worker);
    }

    public Worker retrieveWorker(Material workerType) {
        Worker worker;

        if (!hasAtLeastOne(workerType)) {
            Material tool = getToolForWorker(workerType);

            if (hasAtLeastOne(tool)) {
                int toolAmount = inventory.get(tool);
                int workerAmount = inventory.get(workerType);

                inventory.put(tool, toolAmount - 1);
                inventory.put(workerType, workerAmount + 1);
            } else {
                throw new InvalidGameLogicException("There are no " + workerType + " to retrieve");
            }
        }

        switch (workerType) {
        case FORESTER:
            worker = new Forester(getPlayer(), getMap());
            break;
        case WOODCUTTER_WORKER:
            worker = new WoodcutterWorker(getPlayer(), getMap());
            break;
        case STONEMASON:
            worker = new Stonemason(getPlayer(), getMap());
            break;
        case FARMER:
            worker = new Farmer(getPlayer(), getMap());
            break;
        case SAWMILL_WORKER:
            worker = new SawmillWorker(getPlayer(), getMap());
            break;
        case WELL_WORKER:
            worker = new WellWorker(getPlayer(), getMap());
            break;
        case MILLER:
            worker = new Miller(getPlayer(), getMap());
            break;
        case BAKER:
            worker = new Baker(getPlayer(), getMap());
            break;
        case STORAGE_WORKER:
            worker = new StorageWorker(getPlayer(), getMap());
            break;
        case FISHERMAN:
            worker = new Fisherman(getPlayer(), getMap());
            break;
        case MINER:
            worker = new Miner(getPlayer(), getMap());
            break;
        case IRON_FOUNDER:
            worker = new IronFounder(getPlayer(), getMap());
            break;
        case BREWER:
            worker = new Brewer(getPlayer(), getMap());
            break;
        case MINTER:
            worker = new Minter(getPlayer(), getMap());
            break;
        case ARMORER:
            worker = new Armorer(getPlayer(), getMap());
            break;
        case PIG_BREEDER:
            worker = new PigBreeder(getPlayer(), getMap());
            break;
        case BUTCHER:
            worker = new Butcher(getPlayer(), getMap());
            break;
        case GEOLOGIST:
            worker = new Geologist(getPlayer(), getMap());
            break;
        case DONKEY_BREEDER:
            worker = new DonkeyBreeder(getPlayer(), getMap());
            break;
        case SCOUT:
            worker = new Scout(getPlayer(), getMap());
            break;
        case CATAPULT_WORKER:
            worker = new CatapultWorker(getPlayer(), getMap());
            break;
        case HUNTER:
            worker = new Hunter(getPlayer(), getMap());
            break;
        case METALWORKER:
            worker = new Metalworker(getPlayer(), getMap());
            break;
        case DONKEY:
            worker = new Donkey(getPlayer(), getMap());
            break;
        case COURIER:
            worker = new Courier(getPlayer(), getMap());
            break;
        case PRIVATE:
            worker = new Military(getPlayer(), PRIVATE_RANK, getMap());
            break;
        case PRIVATE_FIRST_CLASS:
            worker = new Military(getPlayer(), PRIVATE_FIRST_CLASS_RANK, getMap());
            break;
        case SERGEANT:
            worker = new Military(getPlayer(), SERGEANT_RANK, getMap());
            break;
        case OFFICER:
            worker = new Military(getPlayer(), OFFICER_RANK, getMap());
            break;
        case GENERAL:
            worker = new Military(getPlayer(), GENERAL_RANK, getMap());
            break;
        case BUILDER:
            worker = new Builder(getPlayer(), getMap());
            break;
        case SHIPWRIGHT:
            worker = new Shipwright(getPlayer(), getMap());
            break;
        default:
            throw new InvalidGameLogicException("Can't retrieve worker of type " + workerType);
        }

        worker.setPosition(getFlag().getPosition());

        retrieveOneFromInventory(workerType);

        return worker;
    }

    public Military retrieveMilitary(Material material) {
        Military.Rank rank;

        if (!hasAtLeastOne(material)) {
            throw new InvalidGameLogicException("Can't retrieve military " + material);
        }

        retrieveOneFromInventory(material);

        switch (material) {
        case GENERAL:
            rank = GENERAL_RANK;
            break;
        case SERGEANT:
            rank = SERGEANT_RANK;
            break;
        case PRIVATE:
            rank = PRIVATE_RANK;
            break;
        default:
            throw new InvalidGameLogicException("Can't retrieve worker of type " + material);
        }

        Military military = new Military(getPlayer(), rank, getMap());

        military.setPosition(getFlag().getPosition());

        return military;
    }

    public Courier retrieveCourier() {
        /* The storage never runs out of couriers */

        Courier courier = new Courier(getPlayer(), getMap());

        courier.setPosition(getFlag().getPosition());

        return courier;
    }

    public Military retrieveAnyMilitary() {
        Military military;

        if (hasAtLeastOne(PRIVATE)) {
            retrieveOneFromInventory(PRIVATE);
            military = new Military(getPlayer(), PRIVATE_RANK, getMap());
        } else if (hasAtLeastOne(PRIVATE_FIRST_CLASS)) {
            retrieveOneFromInventory(PRIVATE_FIRST_CLASS);
            military = new Military(getPlayer(), PRIVATE_FIRST_CLASS_RANK, getMap());
        } else if (hasAtLeastOne(SERGEANT)) {
            retrieveOneFromInventory(SERGEANT);
            military = new Military(getPlayer(), SERGEANT_RANK, getMap());
        } else if (hasAtLeastOne(OFFICER)) {
            retrieveOneFromInventory(OFFICER);
            military = new Military(getPlayer(), OFFICER_RANK, getMap());
        } else if (hasAtLeastOne(GENERAL)) {
            retrieveOneFromInventory(GENERAL);
            military = new Military(getPlayer(), GENERAL_RANK, getMap());
        } else {
            throw new InvalidGameLogicException("No soldiers available");
        }

        military.setPosition(getFlag().getPosition());

        return military;
    }

    private boolean hasAtLeastOne(Material material) {
        if (material == COURIER) {
            return true;
        } else if (material == CATAPULT_WORKER) {
            return true;
        }

        return inventory.getOrDefault(material, 0) > 0;
    }

    private void retrieveOneFromInventory(Material material) {
        if (material == COURIER) {
            return;
        } else if (material == CATAPULT_WORKER) {
            return;
        }

        int amount = inventory.getOrDefault(material, 0);

        inventory.put(material, amount - 1);
    }

    private void storeOneInInventory(Material material) {
        if (material == COURIER) {
            return;
        } else if (material == CATAPULT_WORKER) {
            return;
        }

        int amount = inventory.getOrDefault(material, 0);

        inventory.put(material, amount + 1);
    }

    @Override
    public int getAmount(Material material) {
        if (!isReady()) {
            return super.getAmount(material);
        }

        if (material == COURIER) {
            return 1;
        }

        return inventory.getOrDefault(material, 0);
    }

    private boolean hasMilitary() {

        if (hasAtLeastOne(PRIVATE)) {
            return true;
        }

        if (hasAtLeastOne(PRIVATE_FIRST_CLASS)) {
            return true;
        }

        if (hasAtLeastOne(SERGEANT)) {
            return true;
        }

        if (hasAtLeastOne(OFFICER)) {
            return true;
        }

        if (hasAtLeastOne(GENERAL)) {
            return true;
        }

        return false;
    }

    private boolean isClosestStorage(Building building) {
        Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(building.getPosition(), getPlayer());

        return equals(storehouse);
    }

    private boolean assignDonkeys() {
        if (hasAtLeastOne(DONKEY)) {
            for (Road road : getMap().getRoads()) {
                if (!road.getPlayer().equals(getPlayer())) {
                    continue;
                }

                if (!road.isMainRoad()) {
                    continue;
                }

                if (!road.needsDonkey()) {
                    continue;
                }

                Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoads(road.getStart(), getPlayer());

                if (storehouse != null && !equals(storehouse)) {
                    continue;
                }

                Donkey donkey = retrieveDonkey();
                getMap().placeWorker(donkey, getFlag());
                donkey.assignToRoad(road);

                return true;
            }
        }

        return false;
    }

    private Donkey retrieveDonkey() {
        if (hasAtLeastOne(DONKEY)) {
            retrieveOneFromInventory(DONKEY);

            return new Donkey(getPlayer(), getMap());
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
    void onConstructionFinished() {
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
        List<Cargo> cargos = new ArrayList<>();

        int cargosToReturn = Math.min(amount, getAmount(material));

        for (int i = 0; i < cargosToReturn; i++) {
            cargos.add(retrieve(material));
        }

        return cargos;
    }
}
