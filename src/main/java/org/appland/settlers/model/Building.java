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
import static org.appland.settlers.model.Building.State.BURNING;
import static org.appland.settlers.model.Building.State.DESTROYED;
import static org.appland.settlers.model.Building.State.OCCUPIED;
import static org.appland.settlers.model.Building.State.UNDER_CONSTRUCTION;
import static org.appland.settlers.model.Building.State.UNOCCUPIED;
import static org.appland.settlers.model.GameUtils.createEmptyMaterialIntMap;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import org.appland.settlers.model.Military.Rank;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.policy.ProductionDelays.PROMOTION_DELAY;

public class Building implements Actor, EndPoint, Piece {
    private Player player;

    enum State {
        UNDER_CONSTRUCTION, UNOCCUPIED, OCCUPIED, BURNING, DESTROYED
    }

    private static final int TIME_TO_BUILD_SMALL_HOUSE             = 99;
    private static final int TIME_TO_BUILD_MEDIUM_HOUSE            = 149;
    private static final int TIME_TO_BUILD_LARGE_HOUSE             = 199;
    private static final int TIME_TO_BURN_DOWN                     = 49;
    private static final int TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR = 99;
    
    protected GameMap map;
    
    private State   state;
    private Worker  worker;
    private Worker  promisedWorker;
    private Point   position;
    private Flag    flag;
    private boolean enablePromotions;
    private boolean evacuated;
    private boolean productionEnabled;

    private final Countdown              countdown;
    private final Map<Material, Integer> promisedDeliveries;
    private final List<Military>         hostedMilitary;
    private final List<Military>         promisedMilitary;
    private final Map<Material, Integer> receivedMaterial;

    private static final Logger log = Logger.getLogger(Building.class.getName());

    public Building(Player p) {
        receivedMaterial      = createEmptyMaterialIntMap();
        promisedDeliveries    = createEmptyMaterialIntMap();
        countdown             = new Countdown();
        hostedMilitary        = new ArrayList<>();
        promisedMilitary      = new ArrayList<>();
        flag                  = new Flag(null);
        worker                = null;
        promisedWorker        = null;
        position              = null;
        map                   = null;
        enablePromotions      = true;
        evacuated             = false;
        productionEnabled     = true;

        countdown.countFrom(getConstructionCountdown());

        state = UNDER_CONSTRUCTION;
        player = p;
        
        flag.setPlayer(p);
    }
    
    void setFlag(Flag flagAtPoint) {
        flag = flagAtPoint;
        
        flag.setPlayer(player);
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
        return receivedMaterial.get(material);
    }

    void consumeOne(Material material) {
        int amount = getAmount(material);
        
        receivedMaterial.put(material, amount - 1);
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
        if (!enablePromotions) {
            return false;
        }
        
        return getAmount(COIN) < getMaxCoins();
    }

    private int getMaxCoins() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);
        
        return mb.maxCoins();
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
        if (!unoccupied()) {
            return false;
        }

        return worker == null && promisedWorker == null;
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
            throw new Exception("Can't promise worker to building in state " + state);
        }

        if (promisedWorker != null) {
            throw new Exception("Building " + this + " is already promised worker " + promisedWorker);
        }

        promisedWorker = w;
    }

    public boolean needsMilitaryManning() {
        if (evacuated) {
            return false;
        }
        
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
        if (underConstruction()) {
            throw new Exception("Can't assign " + w + " to unfinished " + this);
        } else if (occupied()) {
            throw new Exception("Building " + this + " is already occupied.");
        }

        log.log(Level.INFO, "Assigning worker {0} to building {1}", new Object[]{w, this});

        worker = w;
        promisedWorker = null;
        
        state = OCCUPIED;
    }

    public void deployMilitary(Military military) throws Exception {
        if (!ready()) {
            throw new Exception("Cannot assign military when the building is not ready");
        }
        
        if (!isMilitaryBuilding()) {
            throw new Exception("Cannot assign military to non-military building");
        }

        if (hostedMilitary.size() >= getMaxHostedMilitary()) {
            throw new Exception("Can not host military, " + this + " already hosting " + hostedMilitary.size() + " militaries");
        }

        State previousState = state;

        hostedMilitary.add(military);
        promisedMilitary.remove(military);
        
        state = OCCUPIED;

        
        if (previousState == UNOCCUPIED) {
            map.updateBorder();
        }
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
        
        /* Start the promotion countdown if it's a coin */
        if (material == COIN && isMilitaryBuilding()) {
            countdown.countFrom(PROMOTION_DELAY - 1);
        }
    }

    public boolean needsMaterial(Material material) {
        log.log(Level.FINE, "Does {0} require {1}", new Object[]{this, material});

        if (underConstruction()) {
            return moreMaterialNeededForConstruction(material);
        } else if (ready()) {
            return needsMaterialForProduction(material);
        }
    
        return false;
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
            if (countdown.reachedZero()) {
                if (isMaterialForConstructionAvailable()) {
                    log.log(Level.INFO, "Construction of {0} done", this);

                    consumeConstructionMaterial();
                    
                    state = UNOCCUPIED;
                }
            } else {
                countdown.step();
            }
        } else if (burningDown()) {
            if (countdown.reachedZero()) {
                state = DESTROYED;
                
                countdown.countFrom(TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR);
            } else {
                countdown.step();
            }
        } else if (occupied()) {            
            if (isMilitaryBuilding() && getAmount(COIN) > 0 && hostsPromotableMilitaries()) {
                if (countdown.reachedZero()) {
                    doPromotion();
                } else {
                    countdown.step();
                }
            }
        } else if (destroyed()) {
            if (countdown.reachedZero()) {
                try {
                    map.removeBuilding(this);
                } catch (Exception ex) {
                    Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                countdown.step();
            }
        }
    }

    public Flag getFlag() {
        return flag;
    }

    public void tearDown() throws Exception {
        state = BURNING;
        countdown.countFrom(TIME_TO_BURN_DOWN);
        
        if (isMilitaryBuilding()) {
            map.updateBorder();
        }
    
        /* Send home the worker */
        if (worker != null) {
            worker.returnToStorage();
        }

        /* Remove driveway */
        Road driveway = map.getRoad(getPosition(), getFlag().getPosition());
        
        if (driveway != null) {
            map.removeRoad(driveway);
        }
    }

    public Size getSize() {
        HouseSize hs = getClass().getAnnotation(HouseSize.class);

        return hs.size();
    }

    public Map<Material, Integer> getRequiredGoodsForProduction() {
        log.log(Level.FINE, "Getting the required goods for this building");

        Production p = getClass().getAnnotation(Production.class);
        Map<Material, Integer> requiredGoods = new HashMap<>();

        if (isMilitaryBuilding() && getMaxCoins() > 0 && enablePromotions) {
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

    private void consumeConstructionMaterial() {
        Map<Material, Integer> materialToConsume = getMaterialsToBuildHouse();

        for (Entry<Material, Integer> pair : materialToConsume.entrySet()) {
            int cost = pair.getValue();
            int before = receivedMaterial.get(pair.getKey());

            receivedMaterial.put(pair.getKey(), before - cost);
        }
    }

    private Map<Material, Integer> getMaterialsToBuildHouse() {
        HouseSize hs                     = getClass().getAnnotation(HouseSize.class);
        Material[] materialsArray        = hs.material();
        Map<Material, Integer> materials = createEmptyMaterialIntMap();

        if (materialsArray.length != 0) {
            for (Material m : materialsArray) {
                int amount = materials.get(m);
                materials.put(m, amount + 1);
            }
        } else {
            switch (getSize()) {
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
        }

        return materials;
    }

    private int getConstructionCountdown() {
        HouseSize sizeAnnotation = getClass().getAnnotation(HouseSize.class);

        switch (sizeAnnotation.size()) {
        case SMALL:
            return TIME_TO_BUILD_SMALL_HOUSE;
        case MEDIUM:
            return TIME_TO_BUILD_MEDIUM_HOUSE;
        case LARGE:
            return TIME_TO_BUILD_LARGE_HOUSE;
        default:
            return 0;
        }
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
        return state == UNDER_CONSTRUCTION;
    }

    public boolean ready() {
        return state == UNOCCUPIED || state == OCCUPIED;
    }

    public boolean burningDown() {
        return state == BURNING;
    }

    public boolean destroyed() {
        return state == DESTROYED;
    }

    protected void setConstructionReady() {
        state = UNOCCUPIED;
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

        /* Building does not accept the material */
        if (!requiredGoods.containsKey(material)) {            
            log.log(Level.FINE, "This building does not accept {0}", material);
            return false;
        }

        int neededAmount = requiredGoods.get(material);

        /* Building has all the cargos it needs of the material */
        if (receivedMaterial.get(material) >= neededAmount) {
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

    private boolean unoccupied() {
        return state == UNOCCUPIED;
    }

    public boolean occupied() {
        return state == OCCUPIED;
    }

    private void doPromotion() {
        Collection<Military> promoted = new LinkedList<>();
        
        for (Rank rank : Rank.values()) {
            if (rank == GENERAL_RANK) {
                continue;
            }
            
            for (Military m : hostedMilitary) {
                if (promoted.contains(m)) {
                    continue;
                }
                
                if (m.getRank() == rank) {
                    m.promote();
                    
                    promoted.add(m);
                    
                    break;
                }
            }
        }

        if (!promoted.isEmpty()) {
            consumeOne(COIN);
        }
    }

    private boolean hostsPromotableMilitaries() {
        for (Military m : hostedMilitary) {
            if (m.getRank() != GENERAL_RANK) {
                return true;
            }
        }
    
        return false;
    }

    public void disablePromotions() {
        enablePromotions = false;
    }

    public void evacuate() throws Exception {
        for (Military m : hostedMilitary) {
            m.returnToStorage();
        }
        
        evacuated = true;
    }

    public void cancelEvacuation() {
        evacuated = false;
    }

    public void stopProduction() throws Exception {
        productionEnabled = false;
    }

    public void resumeProduction() throws Exception {
        productionEnabled = true;
    }

    public boolean isProductionEnabled() {
        return productionEnabled;
    }

    public Player getPlayer() {
        return player;
    }

    void setPlayer(Player p) {
        if (player != null) {
            player.removeBuilding(this);
        }

        player = p;

        flag.setPlayer(p);

        player.addBuilding(this);
    }

    boolean canAttack(Building buildingToAttack) {
        if (isMilitaryBuilding()) {
            double distance = getPosition().distance(buildingToAttack.getPosition());
            
            if (distance < getAttackRadius()) {
                return true;
            }
        }
    
        return false;
    }

    private int getAttackRadius() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);
    
        return mb.attackRadius();
    }

    Military retrieveMilitary() {
        return hostedMilitary.remove(0);
    }
}
