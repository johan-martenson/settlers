package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.HashMap;
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

public class Building implements Actor {

    private List<Military> hostedMilitary;
    private List<Military> promisedMilitary;
    private Worker worker;
    private Worker promisedWorker;
    private Point position;

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

    private boolean isProducer() {
        Production p = getClass().getAnnotation(Production.class);

        return p != null;
    }

    public void hostMilitary(Military military) {
        hostedMilitary.add(military);
        promisedMilitary.remove(military);
    }

    private boolean getWorkerRequired() {
        log.log(Level.INFO, "Checking if {0} requires a worker", this);

        RequiresWorker rw = getClass().getAnnotation(RequiresWorker.class);

        return rw != null;
    }

    public Worker getWorker() {
        return worker;
    }

    public Point getPosition() {
        return position;
    }

    private boolean isAutomaticProducer() {
        Production p = getClass().getAnnotation(Production.class);

        return !p.manualProduction();
    }

    void putProducedCargoForDelivery(Cargo carriedCargo) {
        outputCargo = carriedCargo;
    }

    public enum ConstructionState {

        UNDER_CONSTRUCTION,
        DONE,
        BURNING,
        DESTROYED
    }

    protected ConstructionState constructionState;
    protected int constructionCountdown;
    protected Map<Material, Integer> receivedMaterial;

    private Map<Material, Integer> promisedDeliveries;
    private int destructionCountdown;
    private int productionCountdown;
    private Flag flag;
    private Cargo outputCargo;
    private boolean isWorkerNeeded;

    private Logger log = Logger.getLogger(Building.class.getName());

    public Building() {
        constructionState     = ConstructionState.UNDER_CONSTRUCTION;
        receivedMaterial      = createEmptyMaterialIntMap();
        promisedDeliveries    = createEmptyMaterialIntMap();
        constructionCountdown = getConstructionCountdown(this);
        hostedMilitary        = new ArrayList<>();
        promisedMilitary      = new ArrayList<>();
        outputCargo           = null;
        flag                  = new Flag(null);
        productionCountdown   = -1;
        worker                = null;
        promisedWorker        = null;
        position              = null;

        /* Check and remember if this building requires a worker */
        isWorkerNeeded = getWorkerRequired();
    }

    void setPosition(Point p) {
        position = p;
    }
    
    public Map<Material, Integer> getInQueue() {
        return receivedMaterial;
    }

    public boolean needsWorker(Material material) throws Exception {
        if (!needsWorker()) {
            return false;
        }
        
        return getWorkerType() == material;
    }

    public boolean isCargoReady() {
        return outputCargo != null;
    }

    public void deliver(Cargo c)
            throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        log.log(Level.INFO, "Adding cargo {0} to queue ({1})", new Object[]{c, receivedMaterial});

        Material material = c.getMaterial();

        /* Wood and stone can be delivered during construction */
        if (underConstruction() && (material != PLANCK && material != STONE)) {
            throw new InvalidMaterialException(material);
            /* Can't accept delivery when building is burning or destroyed */
        } else if (burningDown() || destroyed()) {
            throw new InvalidStateForProduction(this);
        } else if (ready() && !canAcceptGoods(this)) {
            throw new DeliveryNotPossibleException(this);
        } else if (ready() && !isAccepted(material, this)) {
            throw new InvalidMaterialException(material);
        }

        int existingQuantity = receivedMaterial.get(material);
        receivedMaterial.put(material, existingQuantity + 1);

        existingQuantity = promisedDeliveries.get(material);
        promisedDeliveries.put(material, existingQuantity - 1);
    }

    public Cargo retrieveCargo() {
        Cargo result = outputCargo;
        outputCargo = null;

        if (result == null) {
            return null;
        }

        result.setPosition(getFlag().getPosition());

        return result;
    }

    public boolean needsMaterial(Material material) {
        log.log(Level.INFO, "Does {0} require {1}", new Object[]{this, material});

        if (underConstruction()) {
            return moreMaterialNeededForConstruction(material);
        } else {
            return needsMaterialForProduction(material);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + buildingToString();
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

        str += outputCargo + " waiting to be picked up";

        return str;
    }

    public void promiseDelivery(Material m) {
        int amount = promisedDeliveries.get(m);

        promisedDeliveries.put(m, amount + 1);
    }

    @Override
    public void stepTime() {
        if (underConstruction()) {

            if (constructionCountdown > 0) {
                constructionCountdown--;
            }

            if (isConstructionReady(constructionCountdown)) {
                log.log(Level.INFO, "Construction of {0} done", this);

                consumeConstructionMaterial();

                constructionState = DONE;
            }
        } else if (burningDown()) {
            destructionCountdown--;

            if (destructionCountdown == 0) {
                constructionState = DESTROYED;
            }
        } else if (ready()) {
            if (isProducer() && !isCargoReady() && isAutomaticProducer()) {
                log.log(Level.INFO, "Calling produce");
                outputCargo = produce();
            }
        }
    }

    public Flag getFlag() {
        return flag;
    }

    public Object getConstructionState() {
        return constructionState;
    }

    public void tearDown() {
        constructionState = ConstructionState.BURNING;
        destructionCountdown = 50;
    }

    public int getProductionTime(Building building) {
        Production p = building.getClass().getAnnotation(Production.class);

        return p.productionTime();
    }

    public Material getProductionMaterial(Building building) {
        Production p = building.getClass().getAnnotation(Production.class);

        return p.output();
    }

    public Size getHouseSize(Building b) {
        HouseSize hs = b.getClass().getAnnotation(HouseSize.class);

        return hs.size();
    }

    public Map<Material, Integer> getRequiredGoodsForProduction(Building building) {
        log.log(Level.INFO, "Getting the required goods for this building");

        Production p = building.getClass().getAnnotation(Production.class);
        Map<Material, Integer> requiredGoods = new HashMap<>();

        log.log(Level.FINE, "Found annotations for {0} in class", requiredGoods);

        /* Return empty map if the annotation isn't there */
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

    public int getMaterialInQueue(Material material) throws InvalidMaterialException {
        return receivedMaterial.get(material);
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----  */
    private void consumeConstructionMaterial() {
        Map<Material, Integer> materialToConsume = this.getMaterialsToBuildHouse(this);

        for (Entry<Material, Integer> pair : materialToConsume.entrySet()) {
            int cost = pair.getValue();
            int before = receivedMaterial.get(pair.getKey());

            receivedMaterial.put(pair.getKey(), before - cost);
        }
    }

    private Map<Material, Integer> getMaterialsToBuildHouse(Building b) {
        Map<Material, Integer> materials = createEmptyMaterialIntMap();

        switch (getHouseSize(b)) {
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

    private int getConstructionCountdown(Building building) {
        HouseSize sizeAnnotation = building.getClass().getAnnotation(HouseSize.class);
        int constructionTime = 100;

        switch (sizeAnnotation.size()) {
        case SMALL:
            constructionTime = 100;
            break;
        case MEDIUM:
            constructionTime = 150;
            break;
        case LARGE:
            constructionTime = 200;
            break;
        }

        return constructionTime;
    }

    private boolean isConstructionReady(int countdown) {
        boolean timeOk = false;

        if (countdown == 0) {
            timeOk = true;
        }

        Map<Material, Integer> materialsToBuild = getMaterialsToBuildHouse(this);
        boolean materialAvailable = true;

        for (Entry<Material, Integer> entry : materialsToBuild.entrySet()) {
            Material m = entry.getKey();

            if (receivedMaterial.get(m) < entry.getValue()) {
                materialAvailable = false;
            }
        }

        return materialAvailable && timeOk;
    }

    private Cargo produce() {
        Cargo result = null;

        /* Construction hasn't started */
        if (productionCountdown == -1) {
            if (productionCanStart(this)) {
                productionCountdown = getProductionTime(this) - 2;
            }

            /* Production ongoing and not finished */
        } else if (productionCountdown > 0) {
            productionCountdown--;

            /* Production just finished */
        } else if (productionCountdown == 0) {
            result = new Cargo(getProductionMaterial(this));

            log.log(Level.INFO, "{0} produced {1}", new Object[]{this, result});

            productionCountdown = -1;
            consumeResources(this);
        }

        log.log(Level.FINE, "Result from produce is {0}", result);
        return result;
    }

    private void consumeResources(Building building) {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction(building);

        for (Entry<Material, Integer> entry : requiredGoods.entrySet()) {
            Material m = entry.getKey();
            int cost = entry.getValue();

            int before = receivedMaterial.get(m);
            receivedMaterial.put(m, before - cost);
        }
    }

    private boolean productionCanStart(Building building) {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction(building);

        if (requiredGoods.keySet().isEmpty()) {
            return true;
        }

        boolean resourcesPresent = true;

        for (Entry<Material, Integer> entry : requiredGoods.entrySet()) {
            Material m = entry.getKey();
            int amount = entry.getValue();

            if (building.receivedMaterial.get(m) < amount) {
                resourcesPresent = false;
            }
        }

        return resourcesPresent;
    }

    private boolean isAccepted(Material material, Building building) {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction(building);

        return requiredGoods.containsKey(material);
    }

    private boolean canAcceptGoods(Building building) {
        Map<Material, Integer> requiredGoods = building.getRequiredGoodsForProduction(building);

        return !requiredGoods.keySet().isEmpty();
    }

    private boolean underConstruction() {
        return constructionState == UNDER_CONSTRUCTION;
    }

    private boolean ready() {
        return constructionState == DONE;
    }

    private boolean burningDown() {
        return constructionState == BURNING;
    }

    private boolean destroyed() {
        return constructionState == DESTROYED;
    }

    protected void setConstructionReady() {
        constructionState = DONE;
    }

    private boolean moreMaterialNeededForConstruction(Material material) {
        Map<Material, Integer> allMaterialNeededForConstruction = getMaterialsToBuildHouse(this);

        int promised = promisedDeliveries.get(material);
        int delivered = receivedMaterial.get(material);
        int required = allMaterialNeededForConstruction.get(material);

        log.log(Level.INFO, "Is more {0} needed for construction: {1} > {2} + {3}",
                new Object[]{material, required, promised, delivered});

        return (required > promised + delivered);
    }

    private boolean needsMaterialForProduction(Material material) {
        Map<Material, Integer> requiredGoods = getRequiredGoodsForProduction(this);

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
}
