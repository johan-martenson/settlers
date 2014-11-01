package org.appland.settlers.model;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.GameUtils.createEmptyMaterialIntMap;
import static org.appland.settlers.model.Material.ARMORER;
import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.MINTER;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
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

@HouseSize(size = MEDIUM)
@RequiresWorker(workerType = STORAGE_WORKER)
public class Storage extends Building implements Actor {

    protected Map<Material, Integer> inventory;
    
    private final Countdown promotionalCountdown;
    private final Countdown draftCountdown;

    private static final Logger log = Logger.getLogger(Storage.class.getName());

    public Storage() {
        inventory = createEmptyMaterialIntMap();
        
        promotionalCountdown = new Countdown();
        draftCountdown = new Countdown();
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
    public void stepTime() {
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
        try {
            assignNewWorkerToUnoccupiedPlaces(map);
        } catch (Exception ex) {
            Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        for (Flag f : map.getFlags()) {
            if (f.needsGeologist()) {
                if (!isClosestStorage(this)) {
                    continue;
                }

                if (!hasAtLeastOne(GEOLOGIST)) {
                    continue;
                }
                
                Geologist geologist = (Geologist)retrieveWorker(GEOLOGIST);

                map.placeWorker(geologist, this);
                geologist.setTarget(f.getPosition());
                f.geologistSent(geologist);
                
                return true;
            }
        }
    
        return false;
    }

    private boolean assignScouts() throws Exception {
        for (Flag f : map.getFlags()) {
            if (f.needsScout()) {
                if (!isClosestStorage(this)) {
                    continue;
                }
            
                if (!hasAtLeastOne(SCOUT)) {
                    continue;
                }
            
                Scout scout = (Scout)retrieveWorker(SCOUT);
            
                map.placeWorker(scout, this);
                scout.setTarget(f.getPosition());
                f.scoutSent();
            
                return true;
            }
        }
    
        return false;
    }
    
    private boolean assignWorkerToUnoccupiedBuildings() throws Exception {
        for (Building b : map.getBuildings()) {
            if (b.isMilitaryBuilding()) {
                if (b.needsMilitaryManning()) {
                    if (!isClosestStorage(b)) {
                        continue;
                    }
                    
                    if (!hasMilitary()) {
                        continue;
                    }
                    
                    Military m = retrieveAnyMilitary();
                    map.placeWorker(m, this);
                    m.setTargetBuilding(b);
                    b.promiseMilitary(m);
                    
                    return true;
                }
            } else {
                if (b.needsWorker()) {
                    Material m = b.getWorkerType();
                    Storage stg = map.getClosestStorage(b.getPosition(), b);
                    
                    if (!equals(stg)) {
                        continue;
                    }
                    
                    if (!hasAtLeastOne(m)) {
                        continue;
                    }
                    
                    Worker w = stg.retrieveWorker(m);
                    map.placeWorker(w, stg.getFlag());
                    w.setTargetBuilding(b);
                    b.promiseWorker(w);
                    
                    return true;
                }
            }
        }

        return false;
    }

    private boolean assignCouriers() throws Exception {
        for (Road r : map.getRoads()) {
            if (!r.needsCourier()) {
                continue;
            }

            Storage stg = map.getClosestStorage(r.getStart());

            if (!equals(stg)) {
                continue;
            }

            if (!hasAtLeastOne(COURIER)) {
                return true;
            }

            Courier w = stg.retrieveCourier();
            w.setMap(map);
            map.placeWorker(w, stg.getFlag());
            w.assignToRoad(r);
            
            return true;
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

        Cargo c = new Cargo(material, map);

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
        }
    
        map.removeWorker(w);
    }

    public Worker retrieveWorker(Material material) throws Exception {
        Worker w = null;

        if (!hasAtLeastOne(material)) {
            throw new Exception("There are no " + material + " to retrieve");
        }
        
        switch (material) {
        case FORESTER:
            w = new Forester(map);
            break;
        case WOODCUTTER_WORKER:
            w = new WoodcutterWorker(map);
            break;
        case STONEMASON:
            w = new Stonemason(map);
            break;
        case FARMER:
            w = new Farmer(map);
            break;
        case SAWMILL_WORKER:
            w = new SawmillWorker(map);
            break;
        case WELL_WORKER:
            w = new WellWorker(map);
            break;
        case MILLER:
            w = new Miller(map);
            break;
        case BAKER:
            w = new Baker(map);
            break;
        case STORAGE_WORKER:
            w = new StorageWorker(map);
            break;
        case FISHERMAN:
            w = new Fisherman(map);
            break;
        case MINER:
            w = new Miner(map);
            break;
        case IRON_FOUNDER:
            w = new IronFounder(map);
            break;
        case BREWER:
            w = new Brewer(map);
            break;
        case MINTER:
            w = new Minter(map);
            break;
        case ARMORER:
            w = new Armorer(map);
            break;
        case PIG_BREEDER:
            w = new PigBreeder(map);
            break;
        case BUTCHER:
            w = new Butcher(map);
            break;
        case GEOLOGIST:
            w = new Geologist(map);
            break;
        case DONKEY_BREEDER:
            w = new DonkeyBreeder(map);
            break;
        case SCOUT:
            w = new Scout(map);
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

        Military m = new Military(r, map);

        m.setPosition(getFlag().getPosition());

        return m;
    }

    public Courier retrieveCourier() {
        /* The storage never runs out of couriers */

        Courier c = new Courier(map);

        c.setPosition(getFlag().getPosition());

        return c;
    }

    public Military retrieveAnyMilitary() throws Exception {
        Military m = null;

        if (hasAtLeastOne(PRIVATE)) {
            retrieveOneFromInventory(PRIVATE);
            m = new Military(PRIVATE_RANK, map);
        } else if (hasAtLeastOne(SERGEANT)) {
            retrieveOneFromInventory(SERGEANT);
            m = new Military(SERGEANT_RANK, map);
        } else if (hasAtLeastOne(GENERAL)) {
            retrieveOneFromInventory(GENERAL);
            m = new Military(GENERAL_RANK, map);
        } else {
            throw new Exception("No militaries available");
        }

        m.setPosition(getFlag().getPosition());

        return m;
    }

    private boolean hasAtLeastOne(Material m) {
        if (m == COURIER) {
            return true;
        }

        return inventory.get(m) > 0;
    }

    private void retrieveOneFromInventory(Material m) {
        if (m == COURIER) {
            return;
        }
        
        int amount = inventory.get(m);

        inventory.put(m, amount - 1);
    }

    private void storeOneInInventory(Material m) {
        if (m == COURIER) {
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
        Storage stg = map.getClosestStorage(b.getPosition());
                    
        return equals(stg);
    }

    private boolean assignDonkeys() throws Exception {
        for (Road r : map.getRoads()) {
            if (!r.isMainRoad()) {
                continue;
            }

            if (!r.needsDonkey()) {
                continue;
            }
        
            if (!hasAtLeastOne(DONKEY)) {
                continue;
            }
        
            Storage stg = map.getClosestStorage(r.getStart());
            
            if (stg != null && !this.equals(stg)) {
                continue;
            }
            
            Donkey d = retrieveDonkey();
            map.placeWorker(d, getFlag());
            d.assignToRoad(r);

            return true;
        }
    
        return false;
    }

    private Donkey retrieveDonkey() {
        if (hasAtLeastOne(DONKEY)) {
            retrieveOneFromInventory(DONKEY);
            
            return new Donkey(map);
        }
    
        return null;
    }

    @Override
    public void stopProduction() throws Exception {
        throw new Exception("Can't stop production in storage");
    }
}
