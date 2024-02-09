package org.appland.settlers.model;

import java.util.*;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Soldier.Rank.*;
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
                    if (! (building instanceof Storehouse storehouse)) {
                        continue;
                    }

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

        if (!hasAtLeastOne(BUILDER) && !hasAtLeastOne(HAMMER)) {
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

                Soldier military = retrieveSoldierToPopulateBuilding();

                getMap().placeWorker(military, this);
                military.setTargetBuilding(building);
                building.promiseSoldier(military);

                return true;
            } else {
                if (building.needsWorker()) {

                    Material material = building.getWorkerType();
                    Material toolForWorker = getToolForWorker(material);

                    boolean hasWorker = hasAtLeastOne(material);
                    boolean canMakeWorker = (toolForWorker != null && hasAtLeastOne(toolForWorker)) || material == WELL_WORKER;

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

            getPlayer().reportProduction(cargo.getMaterial(), this);
        }
    }

    public Cargo retrieve(Material material) {
        if (!hasAtLeastOne(material)) {
            throw new InvalidGameLogicException("Can't retrieve " + material);
        }

        retrieveOneFromInventory(material);

        Cargo cargo = new Cargo(material, getMap());

        cargo.setPosition(getFlag().getPosition());

        getPlayer().reportChangedInventory(this);

        return cargo;
    }

    public boolean isInStock(Material material) {
        return hasAtLeastOne(material);
    }

    public void depositWorker(Worker worker) {
        if (worker.isSoldier()) { // FIXME: deposit for soldier does not seem to work for some ranks
            Soldier military = (Soldier) worker;
            Material material = switch (military.getRank()) {
                case PRIVATE_RANK -> PRIVATE;
                case SERGEANT_RANK -> SERGEANT;
                case GENERAL_RANK -> GENERAL;
                default -> throw new InvalidGameLogicException("Can't handle military with rank " + military.getRank());
            };

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
            } else if (workerType != WELL_WORKER) {
                throw new InvalidGameLogicException("There are no " + workerType + " to retrieve");
            }
        }

        worker = switch (workerType) {
            case FORESTER -> new Forester(getPlayer(), getMap());
            case WOODCUTTER_WORKER -> new WoodcutterWorker(getPlayer(), getMap());
            case STONEMASON -> new Stonemason(getPlayer(), getMap());
            case FARMER -> new Farmer(getPlayer(), getMap());
            case SAWMILL_WORKER -> new SawmillWorker(getPlayer(), getMap());
            case WELL_WORKER -> new WellWorker(getPlayer(), getMap());
            case MILLER -> new Miller(getPlayer(), getMap());
            case BAKER -> new Baker(getPlayer(), getMap());
            case STORAGE_WORKER -> new StorageWorker(getPlayer(), getMap());
            case FISHERMAN -> new Fisherman(getPlayer(), getMap());
            case MINER -> new Miner(getPlayer(), getMap());
            case IRON_FOUNDER -> new IronFounder(getPlayer(), getMap());
            case BREWER -> new Brewer(getPlayer(), getMap());
            case MINTER -> new Minter(getPlayer(), getMap());
            case ARMORER -> new Armorer(getPlayer(), getMap());
            case PIG_BREEDER -> new PigBreeder(getPlayer(), getMap());
            case BUTCHER -> new Butcher(getPlayer(), getMap());
            case GEOLOGIST -> new Geologist(getPlayer(), getMap());
            case DONKEY_BREEDER -> new DonkeyBreeder(getPlayer(), getMap());
            case SCOUT -> new Scout(getPlayer(), getMap());
            case CATAPULT_WORKER -> new CatapultWorker(getPlayer(), getMap());
            case HUNTER -> new Hunter(getPlayer(), getMap());
            case METALWORKER -> new Metalworker(getPlayer(), getMap());
            case DONKEY -> new Donkey(getPlayer(), getMap());
            case COURIER -> new Courier(getPlayer(), getMap());
            case PRIVATE -> new Soldier(getPlayer(), PRIVATE_RANK, getMap());
            case PRIVATE_FIRST_CLASS -> new Soldier(getPlayer(), PRIVATE_FIRST_CLASS_RANK, getMap());
            case SERGEANT -> new Soldier(getPlayer(), SERGEANT_RANK, getMap());
            case OFFICER -> new Soldier(getPlayer(), OFFICER_RANK, getMap());
            case GENERAL -> new Soldier(getPlayer(), GENERAL_RANK, getMap());
            case BUILDER -> new Builder(getPlayer(), getMap());
            case SHIPWRIGHT -> new Shipwright(getPlayer(), getMap());
            default -> throw new InvalidGameLogicException("Can't retrieve worker of type " + workerType);
        };

        worker.setPosition(getFlag().getPosition());

        retrieveOneFromInventory(workerType);

        return worker;
    }

    public Soldier retrieveSoldierFromInventory(Soldier.Rank rank) {
        return retrieveSoldierFromInventory(rank.toMaterial());
    }

    public Soldier retrieveSoldierFromInventory(Material material) {
        if (!hasAtLeastOne(material)) {
            throw new InvalidGameLogicException("Can't retrieve military " + material);
        }

        retrieveOneFromInventory(material);

        Soldier.Rank rank = material.toRank();

        Soldier military = new Soldier(getPlayer(), rank, getMap());

        military.setPosition(getFlag().getPosition());

        return military;
    }

    public Courier retrieveCourier() {
        /* The storage never runs out of couriers */

        Courier courier = new Courier(getPlayer(), getMap());

        courier.setPosition(getFlag().getPosition());

        return courier;
    }

    public Soldier retrieveSoldierToPopulateBuilding() {

        /* Go through the list in order of preference and try to retrieve a soldier */
        for (Soldier.Rank rank : GameUtils.strengthToRank(getPlayer().getStrengthOfSoldiersPopulatingBuildings())) {
            Material preferredSoldierType = rank.toMaterial();

            if (hasAtLeastOne(preferredSoldierType)) {
                retrieveOneFromInventory(preferredSoldierType);

                Soldier military = new Soldier(getPlayer(), rank, getMap());
                military.setPosition(getFlag().getPosition());

                return military;
            }
        }

        throw new InvalidGameLogicException("Can't retrieve soldier!");
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
        } else if (material == WELL_WORKER && inventory.getOrDefault(WELL_WORKER, 0) == 0) {
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
