package org.appland.settlers.model;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.GameUtils.createEmptyMaterialIntMap;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Material.SWORD;
import org.appland.settlers.model.Military.Rank;
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
        /* Handle unoccupied roads */
        List<Road> roads = map.getRoadsThatNeedCouriers();
        
        for (Road r : roads) {
            Storage stg = map.getClosestStorage(r.getStart());

            if (!equals(stg)) {
                continue;
            }

            if (!hasAtLeastOne(COURIER)) {
                return;
            }
            
            Courier w = stg.retrieveCourier();

            w.setMap(map);

            map.placeWorker(w, stg.getFlag());

            w.assignToRoad(r);
        }

        /* Handle unoccupied regular buildings and military buildings*/
        List<Building> buildings = map.getBuildings();

        for (Building b : buildings) {
            if (b.isMilitaryBuilding()) {
                if (b.needMilitaryManning()) {
                    Storage stg = map.getClosestStorage(b.getPosition());

                    if (!equals(stg)) {
                        continue;
                    }
                    
                    if (!hasMilitary()) {
                        continue;
                    }
                    
                    Military m = stg.retrieveAnyMilitary();

                    m.setMap(map);

                    map.placeWorker(m, stg.getFlag());
                    
                    m.setTargetBuilding(b);

                    b.promiseMilitary(m);
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

                    w.setMap(map);

                    map.placeWorker(w, stg.getFlag());

                    w.setTargetBuilding(b);
                    
                    b.promiseWorker(w);
                }
            }
        }
    }

    /* TODO: Write unit tests */
    public boolean isDraftPossible(Map<Material, Integer> inventory) {
        return inventory.get(BEER) > 0
                && inventory.get(SWORD) > 0
                && inventory.get(SHIELD) > 0;
    }

    /* TODO: Write unit tests */
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
        }
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
            m = new Military(Rank.PRIVATE_RANK);
        } else if (hasAtLeastOne(SERGEANT)) {
            retrieveOneFromInventory(SERGEANT);
            m = new Military(Rank.SERGEANT_RANK);
        } else if (hasAtLeastOne(GENERAL)) {
            retrieveOneFromInventory(GENERAL);
            m = new Military(Rank.GENERAL_RANK);
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
        return getConstructionState() == DONE;
    }
}
