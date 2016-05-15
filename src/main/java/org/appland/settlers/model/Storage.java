package org.appland.settlers.model;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.GameUtils.createEmptyMaterialIntMap;
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
import static org.appland.settlers.model.Material.GOLD;
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
    
    private final Countdown promotionalCountdown;
    private final Countdown draftCountdown;

    private static final Logger log = Logger.getLogger(Storage.class.getName());

    public Storage(Player p) {
        super(p);
        
        inventory = createEmptyMaterialIntMap();
        
        promotionalCountdown = new Countdown();
        draftCountdown = new Countdown();
    }

    public Storage() {
        this(null);
    }

    /* This method updates the inventory as a side effect, without any locking */
    private void draftMilitary() {
        int swords = inventory.get(SWORD);
        int shields = inventory.get(SHIELD);
        int beer = inventory.get(BEER);

        int privatesToAdd = Math.min(swords, shields);

        privatesToAdd = Math.min(privatesToAdd, beer);

        int existingPirates = inventory.get(PRIVATE);

        inventory.put(PRIVATE, existingPirates + privatesToAdd);
        inventory.put(BEER, beer - privatesToAdd);
        inventory.put(SHIELD, shields - privatesToAdd);
        inventory.put(SWORD, swords - privatesToAdd);
    }

    @Override
    public void stepTime() throws Exception {
        super.stepTime();
        
        /* Handle promotion with delay */
        if (isPromotionPossible(inventory)) {
            if (promotionalCountdown.reachedZero()) {
                doPromoteMilitary();
                promotionalCountdown.countFrom(ProductionDelays.PROMOTION_DELAY);
            } else if (promotionalCountdown.isCounting()) {
                promotionalCountdown.step();
            } else {
                promotionalCountdown.countFrom(ProductionDelays.PROMOTION_DELAY);
            }
        }

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
        for (Flag f : getMap().getFlags()) {
            if (f.needsGeologist()) {

                /* Don't send out scout if there is no way to the flag */
                List<Point> path = getMap().findWayWithExistingRoads(getPosition(), f.getPosition());

                if (path == null) {
                    continue;
                }

                /* Don't send a geologist if there is a closer storage */
                if (!isClosestStorage(this)) {
                    continue;
                }

                /* Send a geologist to the flag */
                Geologist geologist = (Geologist)retrieveWorker(GEOLOGIST);

                getMap().placeWorker(geologist, this);
                geologist.setTarget(f.getPosition());
                f.geologistSent(geologist);

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
        for (Flag f : getMap().getFlags()) {
            if (f.needsScout()) {

                List<Point> path = getMap().findWayWithExistingRoads(getPosition(), f.getPosition());

                /* Don't send out a scout if there is no way to the flag */
                if (path == null) {
                    continue;
                }
 
                /* Don't send out a scout if there is a closer storage */
                if (!isClosestStorage(this)) {
                    continue;
                }

                /* Send a scout to the flag */
                Scout scout = (Scout)retrieveWorker(SCOUT);

                getMap().placeWorker(scout, this);
                scout.setTarget(f.getPosition());
                f.scoutSent();

                return true;
            }
        }

        return false;
    }
    
    private boolean assignWorkerToUnoccupiedBuildings() throws Exception {
        for (Building b : getMap().getBuildings()) {
            if (b.isMilitaryBuilding()) {
                if (!hasMilitary()) {
                    continue;
                }

                if (b.needsMilitaryManning()) {
                    if (!isClosestStorage(b)) {
                        continue;
                    }

                    Military m = retrieveAnyMilitary();
                    getMap().placeWorker(m, this);
                    m.setTargetBuilding(b);
                    b.promiseMilitary(m);
                    
                    return true;
                }
            } else {
                if (b.needsWorker()) {
                    Material m = b.getWorkerType();

                    if (!hasAtLeastOne(m)) {
                        continue;
                    }

                    Storage stg = getMap().getClosestStorage(b.getPosition(), b);
                    
                    if (!equals(stg)) {
                        continue;
                    }

                    Worker w = stg.retrieveWorker(m);
                    getMap().placeWorker(w, stg.getFlag());
                    w.setTargetBuilding(b);
                    b.promiseWorker(w);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean assignCouriers() throws Exception {

        if (hasAtLeastOne(COURIER)) {
            for (Road r : getMap().getRoads()) {
                if (!r.needsCourier()) {
                    continue;
                }

                Storage stg = getMap().getClosestStorage(r.getStart());

                if (!equals(stg)) {
                    continue;
                }

                Courier w = stg.retrieveCourier();
                getMap().placeWorker(w, stg.getFlag());
                w.assignToRoad(r);

                return true;
            }
        }

        return false;
    }

    public boolean isDraftPossible(Map<Material, Integer> inventory) {
        return inventory.get(BEER) > 0
                && inventory.get(SWORD) > 0
                && inventory.get(SHIELD) > 0;
    }

    public boolean isPromotionPossible(Map<Material, Integer> inventory) {
        return inventory.get(GOLD) > 0
                && (inventory.get(PRIVATE) > 0
                || inventory.get(SERGEANT) > 0);
    }

    private void doPromoteMilitary() {
        int gold = inventory.get(GOLD);
        int privates = inventory.get(PRIVATE);
        int sergeants = inventory.get(SERGEANT);
        int generals = inventory.get(GENERAL);

        if (gold > 0 && privates > 0) {
            sergeants++;
            privates--;
            gold--;
        }

        if (gold > 0 && sergeants > 1) {
            generals++;
            sergeants--;
            gold--;
        }

        inventory.put(PRIVATE, privates);
        inventory.put(SERGEANT, sergeants);
        inventory.put(GENERAL, generals);
        inventory.put(GOLD, gold);
    }

    @Override
    public void putCargo(Cargo c) throws Exception {
        if (!isWorking()) {
            super.putCargo(c);
        } else {
            log.log(Level.FINE, "Depositing cargo {0}", c);

            storeOneInInventory(c.getMaterial());

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

        Cargo c = new Cargo(material, getMap());

        c.setPosition(getFlag().getPosition());

        log.log(Level.FINER, "Inventory is {0} after retrieval", inventory);
        
        return c;
    }

    public boolean isInStock(Material m) {
        return hasAtLeastOne(m);
    }

    public void depositWorker(Worker w) throws Exception {
        if (w instanceof Military) {
            Military m = (Military) w;
            Material material;

            switch (m.getRank()) {
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
                throw new Exception("Can't handle military with rank " + m.getRank());
            }

            storeOneInInventory(material);
        } else if (w instanceof Forester) {
            storeOneInInventory(FORESTER);
        } else if (w instanceof WellWorker) {
            storeOneInInventory(WELL_WORKER);
        } else if (w instanceof WoodcutterWorker) {
            storeOneInInventory(WOODCUTTER_WORKER);
        } else if (w instanceof StorageWorker) {
            storeOneInInventory(STORAGE_WORKER);
        } else if (w instanceof Butcher) {
            storeOneInInventory(BUTCHER);
        } else if (w instanceof SawmillWorker) {
            storeOneInInventory(SAWMILL_WORKER);
        } else if (w instanceof Stonemason) {
            storeOneInInventory(STONEMASON);
        } else if (w instanceof PigBreeder) {
            storeOneInInventory(PIG_BREEDER);
        } else if (w instanceof Minter) {
            storeOneInInventory(MINTER);
        } else if (w instanceof Miller) {
            storeOneInInventory(MILLER);
        } else if (w instanceof IronFounder) {
            storeOneInInventory(IRON_FOUNDER);
        } else if (w instanceof Miner) {
            storeOneInInventory(MINER);
        } else if (w instanceof Forester) {
            storeOneInInventory(FORESTER);
        } else if (w instanceof Fisherman) {
            storeOneInInventory(FISHERMAN);
        } else if (w instanceof Farmer) {
            storeOneInInventory(FARMER);
        } else if (w instanceof Brewer) {
            storeOneInInventory(BREWER);
        } else if (w instanceof Baker) {
            storeOneInInventory(BAKER);
        } else if (w instanceof Armorer) {
            storeOneInInventory(ARMORER);
        } else if (w instanceof Geologist) {
            storeOneInInventory(GEOLOGIST);
        } else if (w instanceof DonkeyBreeder) {
            storeOneInInventory(DONKEY_BREEDER);
        } else if (w instanceof Scout) {
            storeOneInInventory(SCOUT);
        } else if (w instanceof Hunter) {
            storeOneInInventory(HUNTER);
        }
    
        getMap().removeWorker(w);
    }

    public Worker retrieveWorker(Material material) throws Exception {
        Worker w = null;

        if (!hasAtLeastOne(material)) {
            throw new Exception("There are no " + material + " to retrieve");
        }
        
        switch (material) {
        case FORESTER:
            w = new Forester(getPlayer(), getMap());
            break;
        case WOODCUTTER_WORKER:
            w = new WoodcutterWorker(getPlayer(), getMap());
            break;
        case STONEMASON:
            w = new Stonemason(getPlayer(), getMap());
            break;
        case FARMER:
            w = new Farmer(getPlayer(), getMap());
            break;
        case SAWMILL_WORKER:
            w = new SawmillWorker(getPlayer(), getMap());
            break;
        case WELL_WORKER:
            w = new WellWorker(getPlayer(), getMap());
            break;
        case MILLER:
            w = new Miller(getPlayer(), getMap());
            break;
        case BAKER:
            w = new Baker(getPlayer(), getMap());
            break;
        case STORAGE_WORKER:
            w = new StorageWorker(getPlayer(), getMap());
            break;
        case FISHERMAN:
            w = new Fisherman(getPlayer(), getMap());
            break;
        case MINER:
            w = new Miner(getPlayer(), getMap());
            break;
        case IRON_FOUNDER:
            w = new IronFounder(getPlayer(), getMap());
            break;
        case BREWER:
            w = new Brewer(getPlayer(), getMap());
            break;
        case MINTER:
            w = new Minter(getPlayer(), getMap());
            break;
        case ARMORER:
            w = new Armorer(getPlayer(), getMap());
            break;
        case PIG_BREEDER:
            w = new PigBreeder(getPlayer(), getMap());
            break;
        case BUTCHER:
            w = new Butcher(getPlayer(), getMap());
            break;
        case GEOLOGIST:
            w = new Geologist(getPlayer(), getMap());
            break;
        case DONKEY_BREEDER:
            w = new DonkeyBreeder(getPlayer(), getMap());
            break;
        case SCOUT:
            w = new Scout(getPlayer(), getMap());
            break;
        case CATAPULT_WORKER:
            w = new CatapultWorker(getPlayer(), getMap());
            break;
        case HUNTER:
            w = new Hunter(getPlayer(), getMap());
            break;
        default:
            throw new Exception("Can't retrieve worker of type " + material);
        }

        w.setPosition(getFlag().getPosition());

        retrieveOneFromInventory(material);
        
        return w;
    }

    public Military retrieveMilitary(Material material) throws Exception {
        Military.Rank r = Military.Rank.PRIVATE_RANK;

        if (!hasAtLeastOne(material)) {
            throw new Exception("Can't retrieve military " + material);
        }
        
        retrieveOneFromInventory(material);
        
        switch (material) {
        case GENERAL:
            r = Military.Rank.GENERAL_RANK;
            break;
        case SERGEANT:
            r = Military.Rank.SERGEANT_RANK;
            break;
        case PRIVATE:
            r = Military.Rank.PRIVATE_RANK;
            break;
        default:
            throw new Exception("Can't retrieve worker of type " + material);
        }

        Military m = new Military(getPlayer(), r, getMap());

        m.setPosition(getFlag().getPosition());
        
        return m;
    }

    public Courier retrieveCourier() {
        /* The storage never runs out of couriers */

        Courier c = new Courier(getPlayer(), getMap());

        c.setPosition(getFlag().getPosition());

        return c;
    }

    public Military retrieveAnyMilitary() throws Exception {
        Military m = null;

        if (hasAtLeastOne(PRIVATE)) {
            retrieveOneFromInventory(PRIVATE);
            m = new Military(getPlayer(), PRIVATE_RANK, getMap());
        } else if (hasAtLeastOne(SERGEANT)) {
            retrieveOneFromInventory(SERGEANT);
            m = new Military(getPlayer(), SERGEANT_RANK, getMap());
        } else if (hasAtLeastOne(GENERAL)) {
            retrieveOneFromInventory(GENERAL);
            m = new Military(getPlayer(), GENERAL_RANK, getMap());
        } else {
            throw new Exception("No militaries available");
        }

        m.setPosition(getFlag().getPosition());

        return m;
    }

    private boolean hasAtLeastOne(Material m) {
        if (m == COURIER) {
            return true;
        } else if (m == CATAPULT_WORKER) {
            return true;
        }

        return inventory.get(m) > 0;
    }

    private void retrieveOneFromInventory(Material m) {
        if (m == COURIER) {
            return;
        } else if (m == CATAPULT_WORKER) {
            return;
        }
        
        int amount = inventory.get(m);

        inventory.put(m, amount - 1);
    }

    private void storeOneInInventory(Material m) {
        if (m == COURIER) {
            return;
        } else if (m == CATAPULT_WORKER) {
            return;
        }

        int amount = inventory.get(m);

        inventory.put(m, amount + 1);
    }

    @Override
    public int getAmount(Material m) {
        if (m == COURIER) {
            return 1;
        }
        
        return inventory.get(m);
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

    private boolean isClosestStorage(Building b) {
        Storage stg = getMap().getClosestStorage(b.getPosition());
                    
        return equals(stg);
    }

    private boolean assignDonkeys() throws Exception {
        if (hasAtLeastOne(DONKEY)) {
            for (Road r : getMap().getRoads()) {
                if (!r.isMainRoad()) {
                    continue;
                }

                if (!r.needsDonkey()) {
                    continue;
                }

                Storage stg = getMap().getClosestStorage(r.getStart());

                if (stg != null && !this.equals(stg)) {
                    continue;
                }

                Donkey d = retrieveDonkey();
                getMap().placeWorker(d, getFlag());
                d.assignToRoad(r);

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
