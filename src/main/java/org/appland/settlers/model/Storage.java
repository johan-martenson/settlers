package org.appland.settlers.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Material.ARMORER;
import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.CATAPULT_WORKER;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.MINTER;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STONEMASON;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WELL_WORKER;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.policy.ProductionDelays;

@HouseSize(size = MEDIUM, material = {PLANCK, PLANCK, PLANCK, PLANCK, STONE, STONE, STONE})
@RequiresWorker(workerType = STORAGE_WORKER)
public class Storage extends Building implements Actor {

    protected final Map<Material, Integer> inventory;

    private final Countdown draftCountdown;

    private static final Logger log = Logger.getLogger(Storage.class.getName());

    public Storage(Player player) {
        super(player);

        inventory = new HashMap<>();

        draftCountdown = new Countdown();
    }

    public Storage() {
        this(null);
    }

    /* This method updates the inventory as a side effect, without any locking */
    private void draftMilitary() {
        int swords  = inventory.getOrDefault(SWORD, 0);
        int shields = inventory.getOrDefault(SHIELD, 0);
        int beer    = inventory.getOrDefault(BEER, 0);

        int privatesToAdd = Math.min(swords, shields);

        privatesToAdd = Math.min(privatesToAdd, beer);

        int existingPirates = inventory.getOrDefault(PRIVATE, 0);

        inventory.put(PRIVATE, existingPirates + privatesToAdd);
        inventory.put(BEER, beer - privatesToAdd);
        inventory.put(SHIELD, shields - privatesToAdd);
        inventory.put(SWORD, swords - privatesToAdd);
    }

    @Override
    public void stepTime() throws Exception {
        super.stepTime();

        /* Handle draft with delay */
        if (isDraftPossible(inventory)) {
            if (draftCountdown.reachedZero()) {
                draftMilitary();
                draftCountdown.countFrom(ProductionDelays.DRAFT_DELAY);
            } else if (draftCountdown.isCounting()) {
                draftCountdown.step();
            } else {
                draftCountdown.countFrom(ProductionDelays.DRAFT_DELAY);
            }
        }

        /* Send out new workers */
        assignNewWorkerToUnoccupiedPlaces(getMap());
    }

    public void assignNewWorkerToUnoccupiedPlaces(GameMap map) throws Exception {
        if (assignCouriers()) {
            return;
        }

        if (assignDonkeys()) {
            return;
        }

        if (assignWorkerToUnoccupiedBuildings()) {
            return;
        }

        if (assignGeologists()) {
            return;
        }

        if (assignScouts()) {
            return;
        }
    }

    private boolean assignGeologists() throws Exception {

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
                flag.geologistSent(geologist);

                return true;
            }
        }

        return false;
    }

    private boolean assignScouts() throws Exception {

        /* Leave if there are no scouts in this storage */
        if (!hasAtLeastOne(SCOUT)) {
            return false;
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

    private boolean assignWorkerToUnoccupiedBuildings() throws Exception {
        for (Building building : getPlayer().getBuildings()) {
            if (building.isMilitaryBuilding()) {
                if (!hasMilitary()) {
                    continue;
                }

                if (building.needsMilitaryManning()) {
                    if (!isClosestStorage(building)) {
                        continue;
                    }

                    Military military = retrieveAnyMilitary();
                    getMap().placeWorker(military, this);
                    military.setTargetBuilding(building);
                    building.promiseMilitary(military);

                    return true;
                }
            } else {
                if (building.needsWorker()) {
                    Material material = building.getWorkerType();

                    if (!hasAtLeastOne(material)) {
                        continue;
                    }

                    Storage storage = GameUtils.getClosestStorage(building.getPosition(), building, getPlayer());

                    if (!equals(storage)) {
                        continue;
                    }

                    Worker worker = storage.retrieveWorker(material);
                    getMap().placeWorker(worker, storage.getFlag());
                    worker.setTargetBuilding(building);
                    building.promiseWorker(worker);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean assignCouriers() throws Exception {

        if (hasAtLeastOne(COURIER)) {
            for (Road road : getMap().getRoads()) {
                if (!road.getPlayer().equals(getPlayer())) {
                    continue;
                }

                if (!road.needsCourier()) {
                    continue;
                }

                Storage storage = GameUtils.getClosestStorage(road.getStart(), getPlayer());

                if (!equals(storage)) {
                    continue;
                }

                Courier courier = storage.retrieveCourier();
                getMap().placeWorker(courier, storage.getFlag());
                courier.assignToRoad(road);

                return true;
            }
        }

        return false;
    }

    public boolean isDraftPossible(Map<Material, Integer> inventory) {
        return inventory.getOrDefault(BEER, 0) > 0
                && inventory.getOrDefault(SWORD, 0) > 0
                && inventory.getOrDefault(SHIELD, 0) > 0;
    }

    @Override
    public void putCargo(Cargo cargo) throws Exception {
        if (!isWorking()) {
            super.putCargo(cargo);
        } else {
            log.log(Level.FINE, "Depositing cargo {0}", cargo);

            storeOneInInventory(cargo.getMaterial());

            log.log(Level.FINER, "Inventory is {0} after deposit", inventory);
        }
    }

    public Cargo retrieve(Material material) throws Exception {
        log.log(Level.FINE, "Retrieving one piece of {0}", material);

        if (!hasAtLeastOne(material)) {
            throw new Exception("Can't retrieve " + material);
        }

        if (isWorker(material)) {
            throw new Exception("Can't retrieve " + material + " as stuff");
        }

        retrieveOneFromInventory(material);

        Cargo cargo = new Cargo(material, getMap());

        cargo.setPosition(getFlag().getPosition());

        log.log(Level.FINER, "Inventory is {0} after retrieval", inventory);

        return cargo;
    }

    public boolean isInStock(Material material) {
        return hasAtLeastOne(material);
    }

    public void depositWorker(Worker worker) throws Exception {
        if (worker instanceof Military) {
            Military military = (Military) worker;
            Material material;

            switch (military.getRank()) {
            case PRIVATE_RANK:
                material = Material.PRIVATE;
                break;
            case SERGEANT_RANK:
                material = Material.SERGEANT;
                break;
            case GENERAL_RANK:
                material = Material.GENERAL;
                break;
            default:
                throw new Exception("Can't handle military with rank " + military.getRank());
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
        } else if (worker instanceof Forester) {
            storeOneInInventory(FORESTER);
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
        }

        getMap().removeWorker(worker);
    }

    public Worker retrieveWorker(Material material) throws Exception {
        Worker worker = null;

        if (!hasAtLeastOne(material)) {
            throw new Exception("There are no " + material + " to retrieve");
        }

        switch (material) {
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
        default:
            throw new Exception("Can't retrieve worker of type " + material);
        }

        worker.setPosition(getFlag().getPosition());

        retrieveOneFromInventory(material);

        return worker;
    }

    public Military retrieveMilitary(Material material) throws Exception {
        Military.Rank rank = Military.Rank.PRIVATE_RANK;

        if (!hasAtLeastOne(material)) {
            throw new Exception("Can't retrieve military " + material);
        }

        retrieveOneFromInventory(material);

        switch (material) {
        case GENERAL:
            rank = Military.Rank.GENERAL_RANK;
            break;
        case SERGEANT:
            rank = Military.Rank.SERGEANT_RANK;
            break;
        case PRIVATE:
            rank = Military.Rank.PRIVATE_RANK;
            break;
        default:
            throw new Exception("Can't retrieve worker of type " + material);
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

    public Military retrieveAnyMilitary() throws Exception {
        Military military = null;

        if (hasAtLeastOne(PRIVATE)) {
            retrieveOneFromInventory(PRIVATE);
            military = new Military(getPlayer(), PRIVATE_RANK, getMap());
        } else if (hasAtLeastOne(SERGEANT)) {
            retrieveOneFromInventory(SERGEANT);
            military = new Military(getPlayer(), SERGEANT_RANK, getMap());
        } else if (hasAtLeastOne(GENERAL)) {
            retrieveOneFromInventory(GENERAL);
            military = new Military(getPlayer(), GENERAL_RANK, getMap());
        } else {
            throw new Exception("No militaries available");
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
        if (material == COURIER) {
            return 1;
        }

        return inventory.getOrDefault(material, 0);
    }

    private boolean isWorker(Material material) {
        switch (material) {
        case PRIVATE:
        case SERGEANT:
        case GENERAL:
        case FORESTER:
        case COURIER:
            return true;
        default:
            return false;
        }
    }

    boolean hasMilitary() {
        if (!hasAtLeastOne(PRIVATE) && !hasAtLeastOne(SERGEANT) && !hasAtLeastOne(GENERAL)) {
            return false;
        }

        return true;
    }

    private boolean isWorking() {
        return ready();
    }

    private boolean isClosestStorage(Building building) throws InvalidRouteException {
        Storage storage = GameUtils.getClosestStorage(building.getPosition(), getPlayer());

        return equals(storage);
    }

    private boolean assignDonkeys() throws Exception {
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

                Storage storage = GameUtils.getClosestStorage(road.getStart(), getPlayer());

                if (storage != null && !this.equals(storage)) {
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
    public void stopProduction() throws Exception {
        throw new Exception("Can't stop production in storage");
    }
}
