package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Building.ConstructionState.BURNING;
import static org.appland.settlers.model.Building.ConstructionState.DESTROYED;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import static org.appland.settlers.model.GameUtils.createEmptyMaterialIntMap;

import static org.appland.settlers.model.Material.*;

public class Building implements Actor, EndPoint {
    void setFlag(Flag flagAtPoint) {
        flag = flagAtPoint;
    }

    int getDefenceRadius() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);

        if (mb == null) {
            return 0;
        } else {
            return mb.defenceRadius();
        }    
    }

    Collection<Point> getDefendedLand() {
        return map.getPointsWithinRadius(getPosition(), getDefenceRadius());
    }

    public int getAmount(Material material) {
        return getInQueue().get(material);
    }

    void consumeOne(Material material) {
        int amount = getInQueue().get(material);
        
        getInQueue().put(material, amount - 1);
    }

    Collection<Point> getDiscoveredLand() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);
        
        if (mb == null) {
            return new LinkedList<>();
        }

        return map.getPointsWithinRadius(getPosition(), mb.defenceRadius() + 2);
    }

    boolean isMine() {
        return (this instanceof GoldMine || 
                this instanceof IronMine ||
                this instanceof CoalMine ||
                this instanceof GraniteMine);
    }

    private boolean needsCoins() {
        return getAmount(COIN) < getMaxCoins();
    }

    private int getMaxCoins() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);
        
        return mb.maxCoins();
    }

    public enum ConstructionState {
        UNDER_CONSTRUCTION, DONE, BURNING, DESTROYED
    }

    protected ConstructionState constructionState;
    protected GameMap           map;
    
    private Worker  worker;
    private Worker  promisedWorker;
    private Point   position;
    private Flag    flag;

    private final boolean                isWorkerNeeded;
    private final Countdown              constructionCountdown;
    private final Map<Material, Integer> promisedDeliveries;
    private final Countdown              destructionCountdown;
    private final List<Military>         hostedMilitary;
    private final List<Military>         promisedMilitary;
    private final Map<Material, Integer> receivedMaterial;

    private static final Logger log = Logger.getLogger(Building.class.getName());

    public Building() {
        constructionState     = UNDER_CONSTRUCTION;
        receivedMaterial      = createEmptyMaterialIntMap();
        promisedDeliveries    = createEmptyMaterialIntMap();
        constructionCountdown = new Countdown();
        destructionCountdown  = new Countdown();
        hostedMilitary        = new ArrayList<>();
        promisedMilitary      = new ArrayList<>();
        flag                  = new Flag(null);
        worker                = null;
        promisedWorker        = null;
        position              = null;
        map                   = null;

        /* Check and remember if this building requires a worker */
        isWorkerNeeded = getWorkerRequired();
        
        constructionCountdown.countFrom(getConstructionCountdown());
    }
    
    protected void setMap(GameMap m) {
        map = m;
    }
    
    public GameMap getMap() {
        return map;
    }
    
    public boolean isMilitaryBuilding() {
        MilitaryBuilding a = getClass().getAnnotation(MilitaryBuilding.class);

        return a != null;
    }

    public int getMaxHostedMilitary() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);

        if (mb == null) {
            return 0;
        } else {
            return mb.maxHostedMilitary();
        }
    }

    public int getHostedMilitary() {
        return hostedMilitary.size();
    }

    public boolean needsWorker() {
        if (!ready()) {
            return false;
        }

        if (worker != null || promisedWorker != null) {
            return false;
        }

        return isWorkerNeeded;
    }

    public Material getWorkerType() throws Exception {
        RequiresWorker rw = getClass().getAnnotation(RequiresWorker.class);

        if (rw == null) {
            throw new Exception("No worker needed in " + this);
        }

        return rw.workerType();
    }

    public void promiseMilitary(Military m) {
        promisedMilitary.add(m);
    }

    public void promiseWorker(Worker w) throws Exception {
        if (!ready()) {
            throw new Exception("Can't promise worker to building in state " + constructionState);
        }

        if (promisedWorker != null) {
            throw new Exception("Building " + this + " is already promised worker " + promisedWorker);
        }

        promisedWorker = w;
    }

    public boolean needMilitaryManning() {
        if (ready()) {
            int promised = promisedMilitary.size();
            int actual = hostedMilitary.size();
            int maxHost = getMaxHostedMilitary();

            return maxHost > promised + actual;
        } else {
            return false;
        }
    }

    public int getPromisedMilitary() {
        return promisedMilitary.size();
    }

    public void assignWorker(Worker w) throws Exception {
        if (!ready()) {
            throw new Exception("Can't assign " + w + " to unfinished " + this);
        }

        if (worker != null) {
            throw new Exception("Building " + this + " is already occupied.");
        }

        log.log(Level.INFO, "Assigning worker {0} to building {1}", new Object[]{w, this});

        worker = w;
        promisedWorker = null;
    }

    public void hostMilitary(Military military) throws Exception {
        if (!ready()) {
            throw new Exception("Cannot assign military when the building is not ready");
        }
        
        if (hostedMilitary.isEmpty()) {
            if (isMilitaryBuilding()) {
                try {
                    map.updateBorder();
                } catch (Exception ex) {
                    Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        if (hostedMilitary.size() >= getMaxHostedMilitary()) {
            throw new Exception("Can not host military, " + this + " already hosting " + hostedMilitary.size() + " militaries");
        }
        
        hostedMilitary.add(military);
        promisedMilitary.remove(military);
    }

    private boolean getWorkerRequired() {
        log.log(Level.FINE, "Checking if {0} requires a worker", this);

        RequiresWorker rw = getClass().getAnnotation(RequiresWorker.class);

        return rw != null;
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    void setPosition(Point p) {
        position = p;
    }

    private Map<Material, Integer> getInQueue() {
        return receivedMaterial;
    }

    @Override
    public void putCargo(Cargo c) throws Exception {
        log.log(Level.FINE, "Adding cargo {0} to queue ({1})", new Object[]{c, receivedMaterial});

        Material material = c.getMaterial();

        /* Wood and stone can be delivered during construction */
        if (underConstruction() && (material != PLANCK && material != STONE)) {
            throw new InvalidMaterialException(material);
        }

        /* Can't accept delivery when building is burning or destroyed */
        if (burningDown() || destroyed()) {
            throw new InvalidStateForProduction(this);
        }

        if (ready() && material == COIN && isMilitaryBuilding() && !needsCoins()) {
            throw new Exception("This building doesn't need any more coins");
        }
        
        if (ready() && !canAcceptGoods()) {
            throw new DeliveryNotPossibleException();
        }
        
        if (ready() && !isAccepted(material)) {
            throw new InvalidMaterialException(material);
        }

        int existingQuantity = receivedMaterial.get(material);
        receivedMaterial.put(material, existingQuantity + 1);

        existingQuantity = promisedDeliveries.get(material);
        promisedDeliveries.put(material, existingQuantity - 1);
    }

    public boolean needsMaterial(Material material) {
        log.log(Level.FINE, "Does {0} require {1}", new Object[]{this, material});

        if (underConstruction()) {
            return moreMaterialNeededForConstruction(material);
        } else {
            return needsMaterialForProduction(material);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + buildingToString();
    }

    public String buildingToString() {
        String str = " at " + flag + " with ";

        if (GameUtils.isQueueEmpty(receivedMaterial)) {
            str += "nothing in queue and ";
        } else {
            for (Entry<Material, Integer> pair : receivedMaterial.entrySet()) {
                if (pair.getValue() != 0) {
                    str = str + pair.getKey() + ": " + pair.getValue();
                }
            }

            str += "in queue and ";
        }

        return str;
    }

    public void promiseDelivery(Material m) {
        int amount = promisedDeliveries.get(m);

        promisedDeliveries.put(m, amount + 1);
    }

    @Override
    public void stepTime() {
        log.log(Level.FINE, "Stepping time in building");

        if (underConstruction()) {
            if (constructionCountdown.reachedZero()) {
                if (isMaterialForConstructionAvailable()) {
                    log.log(Level.INFO, "Construction of {0} done", this);

                    consumeConstructionMaterial();
                    
                    constructionState = DONE;
                }
            } else {
                constructionCountdown.step();
            }
        } else if (burningDown()) {
            if (destructionCountdown.reachedZero()) {
                constructionState = DESTROYED;
            } else {
                destructionCountdown.step();
            }
        }
    }

    public Flag getFlag() {
        return flag;
    }

    public void tearDown() throws Exception {
        constructionState = BURNING;
        destructionCountdown.countFrom(49);
        
        if (isMilitaryBuilding()) {
            map.updateBorder();
        }
    }

    public Material[] getProductionMaterial() {
        Production p = getClass().getAnnotation(Production.class);

        return p.output();
    }

    public Size getHouseSize() {
        HouseSize hs = getClass().getAnnotation(HouseSize.class);

        return hs.size();
    }

    public Map<Material, Integer> getRequiredGoodsForProduction() {
        log.log(Level.FINE, "Getting the required goods for this building");

        Production p = getClass().getAnnotation(Production.class);
        Map<Material, Integer> requiredGoods = new HashMap<>();

        if (isMilitaryBuilding() && getMaxCoins() > 0) {
            requiredGoods.put(COIN, getMaxCoins());
        }
        
        log.log(Level.FINER, "Found annotations for {0} in class", requiredGoods);

        if (p == null) {
            return requiredGoods;
        }

        Material[] goods = p.requiredGoods();

        for (Material m : goods) {
            if (!requiredGoods.containsKey(m)) {
                requiredGoods.put(m, 0);
            }

            requiredGoods.put(m, 1);
        }

        return requiredGoods;
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----  */
    private void consumeConstructionMaterial() {
        Map<Material, Integer> materialToConsume = getMaterialsToBuildHouse();

        for (Entry<Material, Integer> pair : materialToConsume.entrySet()) {
            int cost = pair.getValue();
            int before = receivedMaterial.get(pair.getKey());

            receivedMaterial.put(pair.getKey(), before - cost);
        }
    }

    private Map<Material, Integer> getMaterialsToBuildHouse() {
        Map<Material, Integer> materials = createEmptyMaterialIntMap();

        switch (getHouseSize()) {
        case SMALL:
            materials.put(PLANCK, 2);
            materials.put(STONE, 2);
            break;
        case MEDIUM:
            materials.put(PLANCK, 4);
            materials.put(STONE, 3);
            break;
        case LARGE:
            materials.put(PLANCK, 4);
            materials.put(STONE, 4);
            break;
        }

        return materials;
    }

    private int getConstructionCountdown() {
        HouseSize sizeAnnotation = getClass().getAnnotation(HouseSize.class);
        int constructionTime = 100;

        switch (sizeAnnotation.size()) {
        case SMALL:
            constructionTime = 100 - 1;
            break;
        case MEDIUM:
            constructionTime = 150 - 1;
            break;
        case LARGE:
            constructionTime = 200 - 1;
            break;
        }

        return constructionTime;
    }

    private boolean isMaterialForConstructionAvailable() {
        Map<Material, Integer> materialsToBuild = getMaterialsToBuildHouse();

        for (Entry<Material, Integer> entry : materialsToBuild.entrySet()) {
            Material m = entry.getKey();

            if (receivedMaterial.get(m) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    private boolean isAccepted(Material material) {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction();

        return requiredGoods.containsKey(material);
    }

    private boolean canAcceptGoods() {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction();

        return !requiredGoods.keySet().isEmpty();
    }

    public boolean underConstruction() {
        return constructionState == UNDER_CONSTRUCTION;
    }

    public boolean ready() {
        return constructionState == DONE;
    }

    public boolean burningDown() {
        return constructionState == BURNING;
    }

    public boolean destroyed() {
        return constructionState == DESTROYED;
    }

    protected void setConstructionReady() {
        constructionState = DONE;
    }

    private boolean moreMaterialNeededForConstruction(Material material) {
        Map<Material, Integer> allMaterialNeededForConstruction = getMaterialsToBuildHouse();

        int promised = promisedDeliveries.get(material);
        int delivered = receivedMaterial.get(material);
        int required = allMaterialNeededForConstruction.get(material);

        log.log(Level.FINE, "Is more {0} needed for construction: {1} > {2} + {3}",
                new Object[]{material, required, promised, delivered});

        return (required > promised + delivered);
    }

    private boolean needsMaterialForProduction(Material material) {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction();

        if (isMilitaryBuilding() && needsCoins() && material == COIN) {
            return true;
        }
        
        if (!requiredGoods.containsKey(material)) {
            /* Building does not accept the material */
            log.log(Level.FINE, "This building does not accept {0}", material);
            return false;
        }

        int neededAmount = requiredGoods.get(material);

        if (receivedMaterial.get(material) >= neededAmount) {
            /* Building has all the cargos it needs of the material */
            log.log(Level.FINE, "This building has all the {0} it needs", material);
            return false;
        }

        log.log(Level.FINE, "This building requires {0}", material);
        return true;
    }

    @Override
    public List<Cargo> getStackedCargo() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasCargoWaitingForRoad(Road r) {
        return false;
    }

    @Override
    public Cargo retrieveCargo(Cargo c) {
        return null;
    }

    @Override
    public Cargo getCargoWaitingForRoad(Road r) {
        return null;
    }
}
