package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.GameUtils.createEmptyMaterialIntMap;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import org.appland.settlers.model.Military.Rank;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.policy.ProductionDelays.PROMOTION_DELAY;

public class Building implements Actor, EndPoint, Piece {
    private Military ownDefender;
    private Military primaryAttacker;
    private boolean  outOfResources;

    private enum State {
        UNDER_CONSTRUCTION, UNOCCUPIED, OCCUPIED, BURNING, DESTROYED
    }

    private static final int TIME_TO_BUILD_SMALL_HOUSE             = 99;
    private static final int TIME_TO_BUILD_MEDIUM_HOUSE            = 149;
    private static final int TIME_TO_BUILD_LARGE_HOUSE             = 199;
    private static final int TIME_TO_BURN_DOWN                     = 49;
    private static final int TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR = 99;
    private static final int TIME_TO_UPGRADE                       = 99;

    private GameMap        map;
    private Player         player;
    private State          state;
    private Worker         worker;
    private Worker         promisedWorker;
    private Point          position;
    private Flag           flag;
    private boolean        enablePromotions;
    private boolean        evacuated;
    private boolean        productionEnabled;
    private boolean        upgrading;

    private final Material[]             requiredGoodsForProduction;
    private final List<Military>         attackers;
    private final List<Military>         waitingAttackers;
    private final List<Military>         defenders;
    private final Countdown              countdown;
    private final Countdown              upgradeCountdown;
    private final Map<Material, Integer> promisedDeliveries;
    private final List<Military>         hostedMilitary;
    private final List<Military>         promisedMilitary;
    private final Map<Material, Integer> receivedMaterial;

    private static final Logger log = Logger.getLogger(Building.class.getName());

    public Building(Player p) {
        receivedMaterial      = createEmptyMaterialIntMap();
        promisedDeliveries    = createEmptyMaterialIntMap();
        countdown             = new Countdown();
        upgradeCountdown      = new Countdown();
        hostedMilitary        = new ArrayList<>();
        promisedMilitary      = new ArrayList<>();
        waitingAttackers      = new LinkedList<>();
        attackers             = new LinkedList<>();
        defenders             = new LinkedList<>();
        flag                  = new Flag(null);
        worker                = null;
        promisedWorker        = null;
        position              = null;
        map                   = null;
        enablePromotions      = true;
        evacuated             = false;
        productionEnabled     = true;
        outOfResources        = false;
        upgrading             = false;

        countdown.countFrom(getConstructionCountdown());

        state = State.UNDER_CONSTRUCTION;
        player = p;

        flag.setPlayer(p);

        /* Initialize goods required for production if the building does any
           any production
        */
        Production production = getClass().getAnnotation(Production.class);

        if (production != null) {
            requiredGoodsForProduction = production.requiredGoods();
        } else {
            requiredGoodsForProduction = new Material[0];
        }
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

    private int getMaxCoins() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);

        return mb.maxCoins();
    }

    protected void setMap(GameMap m) throws Exception {
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

    public int getNumberOfHostedMilitary() {
        return hostedMilitary.size();
    }

    public List<Military> getHostedMilitary() {
        return Collections.unmodifiableList(hostedMilitary);
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

        /* A building can't get an assigned worker while it's still under construction */
        if (underConstruction()) {
            throw new Exception("Can't assign " + w + " to unfinished " + this);
        }

        /* A building can only have one worker */
        if (occupied()) {
            throw new Exception("Building " + this + " is already occupied.");
        }

        /* Can't assign workers to military buildings */
        if (isMilitaryBuilding() && ! (this instanceof Headquarter)) {
            throw new Exception("Can't assign worker to military building");
        }

        log.log(Level.INFO, "Assigning worker {0} to building {1}", new Object[]{w, this});

        worker = w;
        promisedWorker = null;

        state = State.OCCUPIED;
    }

    protected void deployMilitary(Military military) throws Exception {

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

        state = State.OCCUPIED;

        if (previousState == State.UNOCCUPIED) {
            map.updateBorder();
        }

        if (!isEvacuated()) {
            hostedMilitary.add(military);
            promisedMilitary.remove(military);
        } else {
            promisedMilitary.remove(military);
            military.returnToStorage();
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

        /* Plancks and stone can be delivered during construction */
        if (underConstruction()) {

            Map<Material, Integer> materialsNeeded = getMaterialsToBuildHouse();

            /* Throw an exception if another material is being delivered */
            if (!materialsNeeded.containsKey(c.getMaterial())) {
                throw new InvalidMaterialException(material);
            }

            /* Throw an exception if too much is being delivered */
            if (getAmount(material) >= getTotalAmountNeeded(material)) {
                throw new Exception("Can't accept delivery of " + material);
            }
        }

        /* Can't accept delivery when building is burning or destroyed */
        if (burningDown() || destroyed()) {
            throw new InvalidStateForProduction(this);
        }

        if (ready()) {

            if (material == COIN && isMilitaryBuilding() && getAmount(COIN) >= getMaxCoins()) {
                throw new Exception("This building doesn't need any more coins");
            }

            if (!canAcceptGoods()) {
                throw new DeliveryNotPossibleException(this, c);
            }

            if (!isAccepted(material)) {
                throw new InvalidMaterialException(material);
            }
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

        return getLackingAmountWithProjected(material) > 0;
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
    public void stepTime() throws Exception {
        log.log(Level.FINE, "Stepping time in building");

        if (isUnderAttack()) {

            /* There is nothing to do if the building has no hosted militaries */
            if (getNumberOfHostedMilitary() > 0) {

                /* Send out a defender to the flag if needed */
                if (isAttackerAtFlag() && ownDefender == null) {

                    /* Retrieve a defender locally */
                    ownDefender = retrieveMilitary();

                    /* Tell the defender to handle the attacker at the flag */
                    ownDefender.defendBuilding(this);
                }
            }
        }

        if (underConstruction()) {
            if (countdown.reachedZero()) {

                if (isMaterialForConstructionAvailable()) {
                    log.log(Level.INFO, "Construction of {0} done", this);

                    consumeConstructionMaterial();

                    state = State.UNOCCUPIED;
                }
            } else {
                countdown.step();
            }
        } else if (burningDown()) {
            if (countdown.reachedZero()) {
                state = State.DESTROYED;

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
                map.removeBuilding(this);
            } else {
                countdown.step();
            }
        }

        if (isUpgrading()) {

            if (upgradeCountdown.reachedZero()) {

                if (isMaterialForUpgradeAvailable()) {

                    /* Replace the current building from the map */
                    doUpgradeBuilding();

                    /* Re-calculate the border after the upgrade */
                    map.updateBorder();
                }
            } else {
                upgradeCountdown.step();
            }
        }
    }

    public Flag getFlag() {
        return flag;
    }

    public void tearDown() throws Exception {

        /* Clear up after the attack */
        attackers.clear();
        defenders.clear();
        ownDefender = null;

        /* Change building state to burning */
        state = State.BURNING;

        /* Start countdown for burning */
        countdown.countFrom(TIME_TO_BURN_DOWN);

        /* Update the border if this was a military building */
        if (isMilitaryBuilding()) {
            map.updateBorder();
        }

        /* Send home the worker */
        if (worker != null) {
            worker.returnToStorage();
        }

        /* Send home deployed militaries */
        for (Military m : hostedMilitary) {
            m.returnToStorage();
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

    public int getTotalAmountOfMaterialNeededForProduction(Material material) {

        if (isMilitaryBuilding()) {

            if (material == COIN && getMaxCoins() > 0 && enablePromotions) {
                return getMaxCoins();
            }

            if (isUpgrading()) {

                if (material == PLANCK) {
                    int plancks = getTotalAmountNeededForUpgrade(PLANCK);

                    if (plancks > 0) {
                        return plancks;
                    }
                }

                if (material == STONE) {
                    int stones = getTotalAmountNeededForUpgrade(STONE);

                    if (stones > 0) {
                        return stones;
                    }
                }
            }
        }

        int amount = 0;

        for (Material m : requiredGoodsForProduction) {
            if (m == material) {
                amount++;
            }
        }

        return amount;
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
        Map<Material, Integer> materials = new HashMap<>();

        if (materialsArray.length != 0) {
            for (Material m : materialsArray) {

                if (!materials.containsKey(m)) {
                    materials.put(m, 0);
                }

                int amount = materials.get(m);
                materials.put(m, amount + 1);
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

        /* Return true if the production in the building requires the material */
        if (getTotalAmountOfMaterialNeededForProduction(material) > 0) {
            return true;
        }

        /* Return true if the building is being upgraded and requires the
           material for the upgrade
        */
        if (getTotalAmountNeededForUpgrade(material) > 0) {
            return true;
        }

        return false;
    }

    private boolean canAcceptGoods() {

        if (requiredGoodsForProduction.length > 0) {
            return true;
        }

        if (isMilitaryBuilding()) {

            if (isPromotionEnabled() && getMaxCoins() > getAmount(COIN)) {
                return true;
            }

            if (isUpgrading()) {

                if (getTotalAmountNeededForUpgrade(PLANCK) > getAmount(PLANCK)) {
                    return true;
                }

                if (getTotalAmountNeededForUpgrade(STONE) > getAmount(STONE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean underConstruction() {
        return state == State.UNDER_CONSTRUCTION;
    }

    public boolean ready() {
        return state == State.UNOCCUPIED || state == State.OCCUPIED;
    }

    public boolean burningDown() {
        return state == State.BURNING;
    }

    public boolean destroyed() {
        return state == State.DESTROYED;
    }

    protected void setConstructionReady() {
        state = State.UNOCCUPIED;
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
        return state == State.UNOCCUPIED;
    }

    public boolean occupied() {
        return state == State.OCCUPIED;
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

    public void enablePromotions() {
        enablePromotions = true;
    }

    public void evacuate() throws Exception {
        for (Military m : hostedMilitary) {
            m.returnToStorage();
        }

        hostedMilitary.clear();

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

    public boolean isEvacuated() {
        return evacuated;
    }

    public boolean isPromotionEnabled() {
        return enablePromotions;
    }

    List<Military> getRemoteDefenders() {
        return Collections.unmodifiableList(defenders);
    }

    void registerDefender(Military defender) {
        defenders.add(defender);
    }

    void removeDefender(Military defender) {

        if (defender.equals(ownDefender)) {
            ownDefender = null;
        }

        defenders.remove(defender);
    }

    void registerAttacker(Military attacker) {

        /* Register the attacker */
        if (!attackers.contains(attacker)) {
            attackers.add(attacker);
        }

        waitingAttackers.add(attacker);
    }

    void removeWaitingAttacker(Military attacker) {
        waitingAttackers.remove(attacker);
    }

    void removeAttacker(Military attacker) {
        waitingAttackers.remove(attacker);
        attackers.remove(attacker);

        if (attacker.equals(primaryAttacker)) {
            primaryAttacker = null;
        }
    }

    List<Military> getWaitingAttackers() {
        return Collections.unmodifiableList(waitingAttackers);
    }

    Military pickWaitingAttacker() {
        return attackers.remove(0);
    }

    List<Military> getAttackers() {
        return Collections.unmodifiableList(attackers);
    }

    public boolean isUnderAttack() {
        return !attackers.isEmpty();
    }

    private boolean isAttackerAtFlag() {

        /* Return false if there is no primary attacker */
        if (primaryAttacker == null) {
            return false;
        }

        /* Return false if the primary attacker is not at the flag yet */
        if (!primaryAttacker.getPosition().equals(getFlag().getPosition())) {
            return false;
        }

        return true;
    }

    Military getPrimaryAttacker() {
        return primaryAttacker;
    }

    void setPrimaryAttacker(Military attacker) {
        primaryAttacker = attacker;
    }

    boolean isDefenseLess() {
        if (getNumberOfHostedMilitary() == 0       && 
            getRemoteDefenders().isEmpty() &&
            ownDefender == null) {
            return true;
        }

        return false;
    }

    void capture(Player p) throws Exception {

        /* Change the ownership of the building */
        setPlayer(p);

        /* Reset the number of promised militaries */
        promisedMilitary.clear();

        /* Remove traces of the attack */
        attackers.clear();
        defenders.clear();
        ownDefender = null;
    }

    void cancelPromisedDelivery(Cargo aThis) {
        int amount = promisedDeliveries.get(aThis.getMaterial());

        promisedDeliveries.put(aThis.getMaterial(), amount - 1);
    }

    private Integer getProjectedAmount(Material material) {
        return promisedDeliveries.get(material) + getAmount(material);
    }

    private int getTotalAmountNeeded(Material material) {

        if (state == State.UNDER_CONSTRUCTION) {

            if (!getMaterialsToBuildHouse().containsKey(material)) {
                return 0;
            }

            return getMaterialsToBuildHouse().get(material);
        } else if (state == State.OCCUPIED || state == State.UNOCCUPIED) {
            return getTotalAmountOfMaterialNeededForProduction(material);
        }

        return 0;
    }

    private int getLackingAmountWithProjected(Material material) {
        if (state == State.UNDER_CONSTRUCTION) {

            if (!getMaterialsToBuildHouse().containsKey(material)) {
                return 0;
            }

            return getMaterialsToBuildHouse().get(material) - getProjectedAmount(material);
        } else if (state == State.OCCUPIED || state == State.UNOCCUPIED) {

            return getTotalAmountNeeded(material) - getProjectedAmount(material);
        }

        return 0;
    }

    private int getLackingAmountWithoutProjected(Material material) {

        if (state == State.UNDER_CONSTRUCTION) {

            if (!getMaterialsToBuildHouse().containsKey(material)) {
                return 0;
            }

            return getMaterialsToBuildHouse().get(material) - getAmount(material);
        } else if (state == State.OCCUPIED || state == State.UNOCCUPIED) {

            if (!isAccepted(material)) {
                return 0;
            } else {
                return getTotalAmountNeeded(material) - getAmount(material);
            }
        }

        return 0;
    }

    void hitByCatapult() throws Exception {

        if (getNumberOfHostedMilitary() > 0) {
            hostedMilitary.remove(0);
        } else {
            tearDown();
        }
    }

    void reportNoMoreNaturalResources() {
        outOfResources = true;
    }

    public boolean outOfNaturalResources() {
        return outOfResources;
    }

    public void upgrade() throws InvalidUserActionException {

        /* Refuse to upgrade non-upgradable buildings */
        if (!isUpgradable()) {
            throw new InvalidUserActionException("Cannot upgrade " + getClass().getSimpleName());
        }

        /* Refuse to upgrade while under construction */
        if (underConstruction()) {
            throw new InvalidUserActionException("Cannot upgrade while under construction.");
        }

        /* Refuse to upgrade while being torn down */
        if (burningDown()) {
            throw new InvalidUserActionException("Cannot upgrade while burning down.");
        }

        /* Refuse to upgrade while already being upgraded */
        if (isUpgrading()) {
            throw new InvalidUserActionException("Cannot upgrade while being upgraded.");
        }

        /* Start the upgrade */
        upgrading = true;

        upgradeCountdown.countFrom(TIME_TO_UPGRADE);
    }

    protected void doUpgradeBuilding() throws Exception {
    }

    private boolean isMaterialForUpgradeAvailable() {

        /* Get the cost for upgrade */
        UpgradeCost upgradeCost = getClass().getAnnotation(UpgradeCost.class);

        int plancksNeeded = upgradeCost.plancks();
        int stoneNeeded   = upgradeCost.stones();

        /* Get available resources */

        int planckAvailable = receivedMaterial.get(PLANCK);
        int stoneAvailable = receivedMaterial.get(STONE);

        /* Determine if an upgrade is possible */
        if (plancksNeeded <= planckAvailable && stoneNeeded <= stoneAvailable) {
            return true;
        } else {
            return false;
        }
    }

    private int getTotalAmountNeededForUpgrade(Material material) {
        UpgradeCost upgrade = getClass().getAnnotation(UpgradeCost.class);

        /* Only need material for upgrades if the building is actually being
           upgraded
        */
        if (!isUpgrading()) {
            return 0;
        }

        /* Only require material for upgrades if the building is capable of
           upgrades
        */
        if (upgrade == null) {
            return 0;
        }

        /* Only plancks and stones are used for upgrades */
        switch (material) {
            case PLANCK:
                return upgrade.plancks();
            case STONE:
                return upgrade.stones();
            default:
                return 0;
        }
    }

    public boolean isUpgradable() {
        return getClass().getAnnotation(UpgradeCost.class) != null;
    }

    public boolean isUpgrading() {
        return upgrading;
    }

    void setOccupied() {
        state = State.OCCUPIED;
    }
}
