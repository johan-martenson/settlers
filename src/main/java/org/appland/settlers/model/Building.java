package org.appland.settlers.model;

import org.appland.settlers.model.Soldier.Rank;
import org.appland.settlers.utils.Duration;
import org.appland.settlers.utils.Stats;
import org.appland.settlers.utils.StatsConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Soldier.Rank.GENERAL_RANK;

public class Building implements EndPoint {

    private static final int TIME_TO_PROMOTE_SOLDIER               = 100;
    private static final int TIME_TO_BUILD_SMALL_HOUSE             = 100;
    private static final int TIME_TO_BUILD_MEDIUM_HOUSE            = 150;
    private static final int TIME_TO_BUILD_LARGE_HOUSE             = 200;
    private static final int TIME_TO_BURN_DOWN                     = 49;
    private static final int TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR = 99;
    private static final int TIME_TO_UPGRADE                       = 99;

    private final Map<Material, Integer> materialsToBuildHouse;
    private final Map<Material, Integer> totalAmountNeededForProduction;
    private final Map<Material, Integer> totalAmountNeededForUpgrade;
    private final int                    maxCoins;
    private final int                    maxHostedSoldiers;
    private final int                    defenceRadius;
    private final Map<Material, Integer> requiredGoodsForProduction;
    private final List<Soldier>         attackers;
    private final List<Soldier>         waitingAttackers;
    private final Set<Soldier>          defenders;
    private final Countdown              countdown;
    private final Countdown              upgradeCountdown;
    private final Map<Material, Integer> promisedDeliveries;
    private final List<Soldier> hostedSoldiers;
    private final List<Soldier> promisedSoldier;
    private final Map<Material, Integer> receivedMaterial;
    private final Set<Soldier>          waitingDefenders;

    private enum State {
        UNDER_CONSTRUCTION, UNOCCUPIED, OCCUPIED, BURNING, PLANNED, DESTROYED
    }

    private Collection<Point> defendedLand;
    private long     generation;
    private GameMap  map;
    private Player   player;
    private State    state;
    private Worker   worker;
    private Worker   promisedWorker;
    private Point    position;
    private Flag     flag;
    private boolean  enablePromotions;
    private boolean  evacuated;
    private boolean  productionEnabled;
    private boolean  upgrading;
    private Soldier  ownDefender;
    private Soldier  primaryAttacker;
    private boolean  outOfResources;
    private Builder  builder;

    public Building(Player player) {
        receivedMaterial      = new EnumMap<>(Material.class);
        promisedDeliveries    = new EnumMap<>(Material.class);
        countdown             = new Countdown();
        upgradeCountdown      = new Countdown();
        hostedSoldiers = new ArrayList<>();
        promisedSoldier = new ArrayList<>();
        attackers             = new LinkedList<>();
        defenders             = new HashSet<>();
        waitingDefenders      = new HashSet<>();
        waitingAttackers      = new LinkedList<>();
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
        builder               = null;

        countdown.countFrom(getConstructionCountdown());

        state = State.PLANNED;
        this.player = player;

        flag.setPlayer(player);

        /* Initialize goods required for production if the building does any production */
        requiredGoodsForProduction = new EnumMap<>(Material.class);
        Production production = getClass().getAnnotation(Production.class);

        if (production != null) {
            for (Material material : production.requiredGoods()) {
                requiredGoodsForProduction.put(material, requiredGoodsForProduction.getOrDefault(material, 0) + 1);
            }
        }

        /* Keep the materials to build house in a map to avoid calling the method over and over again */
        materialsToBuildHouse = getMaterialsToBuildHouse();
        totalAmountNeededForProduction = getMaterialNeededForProduction();
        totalAmountNeededForUpgrade = getMaterialNeededForUpgrade();
        maxCoins = getCanStoreAmountCoins();

        /* Remember how many soldiers can be hosted to avoid repeated lookups */
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);

        if (militaryBuilding != null) {
            maxHostedSoldiers = militaryBuilding.maxHostedSoldiers();
            defenceRadius = militaryBuilding.defenceRadius();
        } else  {
            maxHostedSoldiers = 0;
            defenceRadius = 0;
        }

        defendedLand = null;
    }

    private Map<Material, Integer> getMaterialNeededForUpgrade() {
        Map<Material, Integer> materialNeeded = new EnumMap<>(Material.class);

        UpgradeCost upgradeCost = getClass().getAnnotation(UpgradeCost.class);

        if (upgradeCost != null) {
            materialNeeded.put(STONE, upgradeCost.stones());
            materialNeeded.put(PLANK, upgradeCost.planks());
        }

        return materialNeeded;
    }

    void setFlag(Flag flagAtPoint) {
        flag = flagAtPoint;

        flag.setPlayer(player);
    }

    int getDefenceRadius() {
        return defenceRadius;
    }

    public Collection<Point> getDefendedLand() {
        if (defendedLand == null) {
            defendedLand = GameUtils.getHexagonAreaAroundPoint(position, defenceRadius - 1, player.getMap());
        }

        return defendedLand;
    }

    public int getAmount(Material material) {
        return receivedMaterial.getOrDefault(material, 0);
    }

    void consumeOne(Material material) {
        int amount = getAmount(material);

        receivedMaterial.put(material, amount - 1);
    }

    Collection<Point> getDiscoveredLand() {
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);

        return GameUtils.getHexagonAreaAroundPoint(getPosition(), militaryBuilding.discoveryRadius(), getMap());
    }

    boolean isMine() {
        return false;
    }

    private int getCanStoreAmountCoins() {
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);

        if (militaryBuilding != null) {
            return militaryBuilding.maxCoins();
        }

        return 0;
    }

    void setMap(GameMap map) {
        this.map = map;
    }

    public GameMap getMap() {
        return map;
    }

    public boolean isMilitaryBuilding() {
        return false;
    }

    public int getMaxHostedSoldiers() {
        return maxHostedSoldiers;
    }

    public int getNumberOfHostedSoldiers() {
        return hostedSoldiers.size();
    }

    public List<Soldier> getHostedSoldiers() {
        return hostedSoldiers;
    }

    public boolean needsWorker() {
        if (getWorkerType() == null) {
            return false;
        }

        if (!isUnoccupied()) {
            return false;
        }

        return worker == null && promisedWorker == null;
    }

    public Material getWorkerType() {
        RequiresWorker requiresWorker = getClass().getAnnotation(RequiresWorker.class);

        if (requiresWorker == null) {
            return null;
        }

        return requiresWorker.workerType();
    }

    public void promiseSoldier(Soldier military) {
        promisedSoldier.add(military);
    }

    public void promiseWorker(Worker worker) {
        if (!isReady()) {
            throw new InvalidGameLogicException("Can't promise worker to building in state " + state);
        }

        if (promisedWorker != null) {
            throw new InvalidGameLogicException("Building " + this + " is already promised worker " + promisedWorker);
        }

        promisedWorker = worker;
    }

    public boolean needsMilitaryManning() {

        /* The building needs no manning if evacuation has been ordered */
        if (evacuated) {
            return false;
        }

        /* The building may need military manning if construction is finished */
        if (isReady()) {
            int promised = promisedSoldier.size();
            int actual = hostedSoldiers.size();
            int wanted = getWantedAmountHostedSoldiers();

            return wanted > promised + actual;
        }

        return false;
    }

    public int getPromisedSoldier() {
        return promisedSoldier.size();
    }

    public void assignWorker(Worker worker) {

        /* A building can't get an assigned worker while it's still under construction */
        if (state == State.PLANNED || state == State.UNDER_CONSTRUCTION) {
            throw new InvalidGameLogicException("Can't assign " + worker + " to unfinished " + this);
        }

        /* A building can only have one worker */
        if (isOccupied()) {
            throw new InvalidGameLogicException("Building " + this + " is already occupied.");
        }

        /* Change this building to be occupied by the worker */
        this.worker = worker;
        promisedWorker = null;

        state = State.OCCUPIED;

        /* Give each type of building a chance to add extra logic when the building has become occupied */
        onBuildingOccupied();
    }

    boolean spaceAvailableToHostSoldier(Soldier soldier) {
        return hostedSoldiers.size() < getMaxHostedSoldiers();
    }

    void deploySoldier(Soldier soldier) {

        if (!isReady()) {
            throw new InvalidGameLogicException("Cannot assign military when the building is not ready");
        }

        if (!spaceAvailableToHostSoldier(soldier)) {
            throw new InvalidGameLogicException("Can not host military, " + this + " already hosting " + hostedSoldiers.size() + " soldiers");
        }

        State previousState = state;

        state = State.OCCUPIED;

        if (previousState == State.UNOCCUPIED) {
            map.updateBorder(this, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);

            getPlayer().reportMilitaryBuildingOccupied(this);
        }

        if (!isEvacuated()) {
            hostedSoldiers.add(soldier);
            promisedSoldier.remove(soldier);
        } else {
            promisedSoldier.remove(soldier);
            soldier.returnToStorage();
        }

        getPlayer().reportSoldierEnteredBuilding(this);
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    void setPosition(Point point) {
        position = point;
    }

    @Override
    public void putCargo(Cargo cargo) {

        Material material = cargo.getMaterial();

        /* Planks and stone can be delivered during construction */
        if (state == State.PLANNED || state == State.UNDER_CONSTRUCTION) {

            Map<Material, Integer> materialsNeeded = getMaterialsToBuildHouse();

            /* Throw an exception if another material is being delivered */
            if (!materialsNeeded.containsKey(cargo.getMaterial())) {
                throw new InvalidMaterialException(material);
            }

            /* Throw an exception if too much is being delivered */
            if (getAmount(material) >= getCanHoldAmount(material)) {
                throw new InvalidGameLogicException("Can't accept delivery of " + material);
            }
        }

        /* Can't accept delivery when building is burning or destroyed */
        if (isBurningDown() || isDestroyed()) {
            throw new InvalidStateForProduction(this);
        }

        if (state == State.UNOCCUPIED || state == State.OCCUPIED) {

            if (material == COIN && isMilitaryBuilding() && getAmount(COIN) >= getCanStoreAmountCoins()) {
                throw new InvalidGameLogicException("This building doesn't need any more coins");
            }

            if (!canAcceptGoods()) {
                throw new DeliveryNotPossibleException(this, cargo);
            }

            if (!isAccepted(material)) {
                throw new InvalidMaterialException(material);
            }
        }

        /* Update the list of received materials and the list of promised deliveries */
        int received = receivedMaterial.getOrDefault(material, 0);
        receivedMaterial.put(material, received + 1);

        int promised = promisedDeliveries.getOrDefault(material, 0);
        promisedDeliveries.put(material, promised - 1);

        /* Start the promotion countdown if it's a coin */
        if (material == COIN && isMilitaryBuilding()) {
            countdown.countFrom(TIME_TO_PROMOTE_SOLDIER - 1);
        }

        player.reportChangedInventory(this);
    }

    /**
     * Returns whether the given material is needed by the building based on the total need and the current inventory.
     *
     * @param material that might be needed
     * @return whether the given material is needed
     */
    public boolean needsMaterial(Material material) {
        return getLackingAmountWithProjected(material) > 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + buildingToString() + " state: " + state;
    }

    private String buildingToString() {
        StringBuilder stringBuilder = new StringBuilder(" at " + flag + " with ");

        boolean hasReceivedMaterial = false;
        for (Entry<Material, Integer> pair : receivedMaterial.entrySet()) {
            if (pair.getValue() != 0) {
                stringBuilder.append(pair.getKey()).append(": ").append(pair.getValue());

                hasReceivedMaterial = true;
            }
        }

        if (hasReceivedMaterial) {
            stringBuilder.append("in queue and ");
        } else {
            stringBuilder.append("nothing in queue and ");
        }

        return stringBuilder.toString();
    }

    public void promiseDelivery(Material material) {
        int amount = promisedDeliveries.getOrDefault(material, 0);

        promisedDeliveries.put(material, amount + 1);
    }

    void stepTime() {

        Stats stats = map.getStats();

        String counterName = "Building." + getClass().getSimpleName() + ".stepTime";

        stats.addPeriodicCounterVariableIfAbsent(counterName);
        stats.addVariableToGroup(counterName, StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);

        Duration duration = new Duration(counterName);

        if (isUnderAttack()) {

            /* There is nothing to do if the building has no hosted soldiers */
            if (getNumberOfHostedSoldiers() > 0) {

                /* Send out a defender to the flag if needed */
                if (isAttackerAtFlag() && ownDefender == null) {

                    /* Retrieve a defender locally */
                    ownDefender = retrieveHostedSoldier();

                    /* Tell the defender to handle the attacker at the flag */
                    ownDefender.defendOwnBuilding(this);
                }
            }
        }

        if (state == State.UNDER_CONSTRUCTION) {
            if (countdown.hasReachedZero()) {
                if (isMaterialForConstructionAvailable()) {
                    consumeConstructionMaterial();

                    state = State.UNOCCUPIED;

                    /* For military buildings, report the construction */
                    if (isMilitaryBuilding()) {
                        getPlayer().reportMilitaryBuildingReady(this);
                    }

                    /* Give subclasses a chance to add behavior */
                    onConstructionFinished();

                    /* Report that the construction is done */
                    map.reportBuildingConstructed(this);
                }
            } else {
                countdown.step();
            }
        } else if (state == State.BURNING) {
            if (countdown.hasReachedZero()) {
                state = State.DESTROYED;

                countdown.countFrom(TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR);

                /* Report that the building has burned down */
                map.reportBuildingBurnedDown(this);
            } else {
                countdown.step();
            }
        } else if (state == State.OCCUPIED) {

            // Send out soldiers if there are too many compared to the settings
            if (isMilitaryBuilding() &&
                    !isHeadquarter() &&
                    getHostedSoldiers().size() > getWantedAmountHostedSoldiers()) {
                var sortedHostedSoldiers = GameUtils.sortSoldiersByPreferredRank(getHostedSoldiers(), player.getStrengthOfSoldiersPopulatingBuildings());
                var soldier = sortedHostedSoldiers.getLast();

                hostedSoldiers.remove(soldier);

                soldier.returnToStorage();
            }

            // Handle promotions
            if (isMilitaryBuilding() && getAmount(COIN) > 0 && hostsPromotableSoldiers()) {
                if (countdown.hasReachedZero()) {
                    doPromotion();
                } else {
                    countdown.step();
                }
            }
        } else if (state == State.DESTROYED) {
            if (countdown.hasReachedZero()) {
                map.removeBuilding(this);

                /* Report that the building is removed */
                map.reportBuildingRemoved(this);
            } else {
                countdown.step();
            }
        }

        if (isUpgrading()) {

            if (upgradeCountdown.hasReachedZero()) {
                if (isMaterialForUpgradeAvailable()) {

                    /* Replace the current building from the map */
                    doUpgradeBuilding();

                    /* Re-calculate the border after the upgrade */
                    map.updateBorder(this, BorderChangeCause.MILITARY_BUILDING_UPGRADED);
                }
            } else {
                upgradeCountdown.step();
            }
        }

        /* Give buildings a chance to add additional logic on step time */
        onStepTime();

        duration.after("stepTime");

        map.getStats().reportVariableValue(counterName, duration.getFullDuration());
    }

    public Flag getFlag() {
        return flag;
    }

    public void tearDown() throws InvalidUserActionException {

        /* A building cannot be torn down if it's already burning or destroyed */
        if (state == State.BURNING || state == State.DESTROYED) {
            throw new InvalidUserActionException("The building has already been torn down.");
        }

        /* Clear up after the attack */
        attackers.clear();
        waitingAttackers.clear();
        defenders.clear();
        ownDefender = null;

        /* Change building state */
        if (state != State.PLANNED) {
            state = State.BURNING;
        }

        /* Start countdown for burning */
        countdown.countFrom(TIME_TO_BURN_DOWN);

        /* Update the border if this was a military building */
        if (isMilitaryBuilding()) {
            map.updateBorder(this, BorderChangeCause.MILITARY_BUILDING_TORN_DOWN);
        }

        /* Send home the worker and builder (if any) */
        if (worker != null) {
            worker.returnToStorage();
        }

        if (builder != null) {
            builder.returnToStorage();
        }

        /* Send home deployed soldiers */
        for (Soldier military : hostedSoldiers) {
            military.returnToStorage();
        }

        /* Remove driveway */
        Road driveway = map.getRoad(getPosition(), getFlag().getPosition());

        map.doRemoveRoad(driveway);

        /* Report that the building has been torn down */
        if (state == State.PLANNED) {
            map.removeBuilding(this);

            map.reportBuildingRemoved(this);
        } else {
            map.reportTornDownBuilding(this);
        }

        player.reportBuildingTornDown(this);
    }

    public Size getSize() {
        HouseSize hs = getClass().getAnnotation(HouseSize.class);

        return hs.size();
    }

    private void consumeConstructionMaterial() {
        Map<Material, Integer> materialToConsume = getMaterialsToBuildHouse();

        for (Entry<Material, Integer> pair : materialToConsume.entrySet()) {
            int cost = pair.getValue();
            int before = receivedMaterial.getOrDefault(pair.getKey(), 0);

            receivedMaterial.put(pair.getKey(), before - cost);
        }
    }

    // FIXME: HOTSPOT - allocations
    private Map<Material, Integer> getMaterialsToBuildHouse() {
        HouseSize houseSize              = getClass().getAnnotation(HouseSize.class);
        Material[] materialsArray        = houseSize.material();
        Map<Material, Integer> materials = new EnumMap<>(Material.class);

        for (Material material : materialsArray) {

            int amount = materials.getOrDefault(material, 0);
            materials.put(material, amount + 1);
        }

        return materials;
    }

    private int getConstructionCountdown() {
        HouseSize sizeAnnotation = getClass().getAnnotation(HouseSize.class);

        return switch (sizeAnnotation.size()) {
            case SMALL -> TIME_TO_BUILD_SMALL_HOUSE;
            case MEDIUM -> TIME_TO_BUILD_MEDIUM_HOUSE;
            case LARGE -> TIME_TO_BUILD_LARGE_HOUSE;
            default -> 0;
        };
    }

    private boolean isMaterialForConstructionAvailable() {
        Map<Material, Integer> materialsToBuild = getMaterialsToBuildHouse();

        /* Check if the required amount is available for each required material */
        for (Entry<Material, Integer> entry : materialsToBuild.entrySet()) {
            Material material = entry.getKey();

            /* Return false if there is missing material */
            if (receivedMaterial.getOrDefault(material, 0) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    private boolean isAccepted(Material material) {
        return getCanHoldAmount(material) > 0;
    }

    private boolean canAcceptGoods() {
        if (!requiredGoodsForProduction.isEmpty()) {
            return true;
        }

        if (isMilitaryBuilding()) {

            if (isPromotionEnabled() && getCanStoreAmountCoins() > getAmount(COIN)) {
                return true;
            }

            if (isUpgrading()) {

                if (getTotalAmountNeededForUpgrade(PLANK) > getAmount(PLANK)) {
                    return true;
                }

                if (getTotalAmountNeededForUpgrade(STONE) > getAmount(STONE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isUnderConstruction() {
        return state == State.UNDER_CONSTRUCTION;
    }

    public boolean isReady() {
        return state == State.UNOCCUPIED || state == State.OCCUPIED;
    }

    public boolean isBurningDown() {
        return state == State.BURNING;
    }

    public boolean isDestroyed() {
        return state == State.DESTROYED;
    }

    void setConstructionReady() {
        state = State.UNOCCUPIED;
    }

    public boolean isUnoccupied() {
        return state == State.UNOCCUPIED;
    }

    public boolean isOccupied() {
        return state == State.OCCUPIED;
    }

    private void doPromotion() {
        Collection<Soldier> promoted = new LinkedList<>();

        for (Rank rank : Rank.values()) {
            if (rank == GENERAL_RANK) {
                continue;
            }

            for (Soldier military : hostedSoldiers) {
                if (promoted.contains(military)) {
                    continue;
                }

                if (military.getRank() == rank) {
                    military.promote();

                    promoted.add(military);

                    break;
                }
            }
        }

        if (!promoted.isEmpty()) {
            consumeOne(COIN);
        }
    }

    private boolean hostsPromotableSoldiers() {
        for (Soldier military : hostedSoldiers) {
            if (military.getRank() != GENERAL_RANK) {
                return true;
            }
        }

        return false;
    }

    public void disablePromotions() {
        enablePromotions = false;

        getPlayer().reportDisabledPromotions(this);
    }

    public void enablePromotions() {
        enablePromotions = true;

        getPlayer().reportEnabledPromotions(this);
    }

    public void evacuate() {
        for (Soldier military : hostedSoldiers) {
            military.returnToStorage();

            player.reportSoldierLeftBuilding(this);
        }

        hostedSoldiers.clear();

        evacuated = true;

        player.reportBuildingEvacuated(this);
    }

    public void cancelEvacuation() {
        evacuated = false;

        getPlayer().reportBuildingEvacuationCanceled(this);
    }

    public void stopProduction() throws InvalidUserActionException {
        productionEnabled = false;

        getPlayer().reportProductionStopped(this);
    }

    public void resumeProduction() throws InvalidUserActionException {
        productionEnabled = true;

        player.reportProductionResumed(this);
    }

    public boolean isProductionEnabled() {
        return productionEnabled;
    }

    public Player getPlayer() {
        return player;
    }

    void setPlayer(Player player) {
        if (this.player != null) {
            player.removeBuilding(this);
        }

        this.player = player;

        flag.setPlayer(player);

        player.addBuilding(this);
    }

    boolean canAttack(Building buildingToAttack) {
        if (isMilitaryBuilding()) {
            double distance = getPosition().distance(buildingToAttack.getPosition());

            return distance < getAttackRadius();
        }

        return false;
    }

    private int getAttackRadius() {
        MilitaryBuilding mb = getClass().getAnnotation(MilitaryBuilding.class);

        return mb.attackRadius();
    }

    Soldier retrieveHostedSoldier(Soldier soldier) {
        hostedSoldiers.remove(soldier);

        return soldier;
    }

    Soldier retrieveHostedSoldier() {
        for (Rank rank : GameUtils.strengthToRank(player.getDefenseStrength())) {
            Optional<Soldier> maybeSoldier = hostedSoldiers.stream().filter(soldier -> soldier.getRank() == rank).findFirst();

            if (maybeSoldier.isPresent()) {
                hostedSoldiers.remove(maybeSoldier.get());

                return maybeSoldier.get();
            }
        }

        throw new InvalidGameLogicException("Can't retrieve soldier");
    }

    Soldier retrieveHostedSoldierWithRank(Rank rank) {
        Optional<Soldier> optionalMilitary = hostedSoldiers.stream().filter(soldier -> soldier.getRank() == rank).findFirst();

        hostedSoldiers.remove(optionalMilitary.get());

        return optionalMilitary.get();
    }

    public boolean isEvacuated() {
        return evacuated;
    }

    public boolean isPromotionEnabled() {
        return enablePromotions;
    }

    void registerDefender(Soldier defender) {
        defenders.add(defender);
    }

    void removeDefender(Soldier defender) {
        if (defender.equals(ownDefender)) {
            ownDefender = null;
        }

        defenders.remove(defender);
    }

    void registerAttacker(Soldier attacker) {

        /* Register the attacker */
        if (!attackers.contains(attacker)) {
            attackers.add(attacker);
        }
    }

    void removeAttacker(Soldier attacker) {
        waitingAttackers.remove(attacker);
        attackers.remove(attacker);

        if (attacker.equals(primaryAttacker)) {
            primaryAttacker = null;
        }
    }

    List<Soldier> getWaitingAttackers() {
        return waitingAttackers;
    }

    Soldier pickWaitingAttacker() {
        return waitingAttackers.removeFirst();
    }

    List<Soldier> getAttackers() {
        return attackers;
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

    Soldier getPrimaryAttacker() {
        return primaryAttacker;
    }

    void setPrimaryAttacker(Soldier attacker) {
        primaryAttacker = attacker;
    }

    boolean isDefenseLess() {
        if (getNumberOfHostedSoldiers() == 0 && defenders.isEmpty() && ownDefender == null) {
            return true;
        }

        return false;
    }

    void capture(Player player) throws InvalidUserActionException {

        /* Change the ownership of the building */
        setPlayer(player);

        /* Reset the number of promised soldiers */
        promisedSoldier.clear();

        /* Remove traces of the attack */
        attackers.clear();
        defenders.clear();
        ownDefender = null;

        /* Stop the evacuation if it is enabled */
        evacuated = false;
    }

    void cancelPromisedDelivery(Cargo cargo) {
        int amount = promisedDeliveries.getOrDefault(cargo.getMaterial(), 0);

        promisedDeliveries.put(cargo.getMaterial(), amount - 1);
    }

    /**
     * Returns the total amount needed for the given material
     *
     * Considers:
     *  - Building planned, under construction, unoccupied/occupied
     *
     * Does not consider:
     *  - Current inventory
     *  - Whether promotions are enabled/disabled
     *  - Whether production is paused/resumed
     *
     * NOTE: Will not return valid response when called for ready Storehouse or for Headquarters
     *
     * @param material to find the total need for
     * @return the total need for the material
     */
    public int getCanHoldAmount(Material material) {
        switch (state) {
            case PLANNED:
            case UNDER_CONSTRUCTION:
                return materialsToBuildHouse.getOrDefault(material, 0);

            case UNOCCUPIED:
            case OCCUPIED:
                if (isMilitaryBuilding()) {
                    if (material == COIN) {
                        return getCanStoreAmountCoins();
                    }

                    if (isUpgrading()) {
                        return getMaterialNeededForUpgrade().getOrDefault(material, 0);
                    }
                } else {
                    return requiredGoodsForProduction.getOrDefault(material, 0);
                }

            default:
                return 0;
        }
    }

    private int getLackingAmountWithProjected(Material material) {

        /* Handle buildings that are under construction */
        if (state == State.UNDER_CONSTRUCTION || state == State.PLANNED) {

            if (!materialsToBuildHouse.containsKey(material)) {
                return 0;
            }

            int total = materialsToBuildHouse.get(material);
            int promised = promisedDeliveries.getOrDefault(material, 0);
            int received = receivedMaterial.getOrDefault(material, 0);

            return total - promised - received;

        /* Handle military buildings that are being upgraded */
        } else if (isMilitaryBuilding() && isReady()) {

            /* Fully built military buildings can only need planks, stones, and coins */
            if (material != PLANK && material != STONE && material != COIN) {
                return 0;
            }

            int total = 0;

            /* Handle coins for promotions */
            if (material == COIN && enablePromotions) {
                total = maxCoins;

            /* Handle planks and stones for upgrades */
            } else if (isUpgrading()){
                total = totalAmountNeededForUpgrade.getOrDefault(material, 0);
            }

            if (total > 0) {

                int promised = promisedDeliveries.getOrDefault(material, 0);
                int received = receivedMaterial.getOrDefault(material, 0);

                return total - promised - received;
            }

            return 0;

        /* Handle buildings that are ready and are not military buildings */
        } else if (state == State.OCCUPIED || state == State.UNOCCUPIED) {
            int total = totalAmountNeededForProduction.getOrDefault(material, 0);
            int promised = promisedDeliveries.getOrDefault(material, 0);
            int received = receivedMaterial.getOrDefault(material, 0);

            return total - promised - received;
        }

        return 0;
    }

    void hitByCatapult(Catapult catapult) throws InvalidUserActionException {

        getPlayer().reportHitByCatapult(catapult, this);

        if (getNumberOfHostedSoldiers() > 0) {
            hostedSoldiers.removeFirst();
        } else {
            tearDown();
        }
    }

    void reportNoMoreNaturalResources() {
        outOfResources = true;
    }

    public boolean isOutOfNaturalResources() {
        return outOfResources;
    }

    public void upgrade() throws InvalidUserActionException {

        /* Refuse to upgrade non-upgradable buildings */
        if (!isUpgradable()) {
            throw new InvalidUserActionException("Cannot upgrade " + getClass().getSimpleName());
        }

        /* Refuse to upgrade while under construction */
        if (state == State.UNDER_CONSTRUCTION || state == State.PLANNED) {
            throw new InvalidUserActionException("Cannot upgrade while under construction.");
        }

        /* Refuse to upgrade while being torn down */
        if (isBurningDown()) {
            throw new InvalidUserActionException("Cannot upgrade while burning down.");
        }

        /* Refuse to upgrade while already being upgraded */
        if (isUpgrading()) {
            throw new InvalidUserActionException("Cannot upgrade while being upgraded.");
        }

        /* Start the upgrade */
        upgrading = true;

        upgradeCountdown.countFrom(TIME_TO_UPGRADE);

        player.reportUpgradeStarted(this);
    }

    void doUpgradeBuilding() {
        // Empty - available for subclasses to implement
    }

    private boolean isMaterialForUpgradeAvailable() {

        /* Get the cost for upgrade */
        UpgradeCost upgradeCost = getClass().getAnnotation(UpgradeCost.class);

        int planksNeeded = upgradeCost.planks();
        int stoneNeeded   = upgradeCost.stones();

        /* Get available resources */
        int plankAvailable = receivedMaterial.getOrDefault(PLANK, 0);
        int stoneAvailable = receivedMaterial.getOrDefault(STONE, 0);

        /* Determine if an upgrade is possible */
        if (planksNeeded <= plankAvailable && stoneNeeded <= stoneAvailable) {
            return true;
        } else {
            return false;
        }
    }

    private int getTotalAmountNeededForUpgrade(Material material) {
        UpgradeCost upgrade = getClass().getAnnotation(UpgradeCost.class);

        /* Only need material for upgrades if the building is actually being upgraded */
        if (!isUpgrading()) {
            return 0;
        }

        /* Only require material for upgrades if the building is capable of upgrades */
        if (upgrade == null) {
            return 0;
        }

        /* Only planks and stones are used for upgrades */
        return switch (material) {
            case PLANK -> upgrade.planks();
            case STONE -> upgrade.stones();
            default -> 0;
        };
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

    public int getProductivity() {

        if (isOutOfNaturalResources()) {
            return 0;
        }

        /* An unoccupied building has no productivity */
        if (worker == null) {
            return 0;
        }

        return worker.getProductivity();
    }

    public boolean canProduce() {
        Production production = getClass().getAnnotation(Production.class);

        return production != null;
    }

    public Material[] getProducedMaterial() {
        Production production = getClass().getAnnotation(Production.class);

        if (production == null) {
            return new Material[] {};
        }

        return getClass().getAnnotation(Production.class).output();
    }

    private Map<Material, Integer> getMaterialNeededForProduction() {
        Map<Material, Integer> materialNeeded = new EnumMap<>(Material.class);

        Production production = getClass().getAnnotation(Production.class);

        if (production != null) {
            for (Material material : production.requiredGoods()) {
                int amount = materialNeeded.getOrDefault(material, 0);

                amount = amount + 1;

                materialNeeded.put(material, amount);
            }
        }

        return materialNeeded;
    }

    /**
     * Returns the set of materials the building needs. Does not consider:
     *  - Current inventory
     *  - Whether production is enabled/disabled
     *  - Whether in case of military building evacuations are ordered
     *
     * NOTE: It does not return material needed because of upgrade if the building is military and is being upgraded
     *
     * NOTE: It does consider whether max amount of coins are already stored. This is most likely a bug
     * @return the set of materials the building type needs
     */
    public Collection<Material> getTypesOfMaterialNeeded() {

        Set<Material> result = EnumSet.noneOf(Material.class);

        if (state == State.UNDER_CONSTRUCTION || state == State.PLANNED) {
            HouseSize houseSize = getClass().getAnnotation(HouseSize.class);

            result.addAll(Arrays.asList(houseSize.material()));
        } else if (isReady()) {
            Production production = getClass().getAnnotation(Production.class);

            if (production != null) {
                result.addAll(Arrays.asList(production.requiredGoods()));
            }

            if (isMilitaryBuilding() && getAmount(COIN) < getCanStoreAmountCoins() && isPromotionEnabled()) {
                result.add(COIN);
            }
        }

        return result;
    }

    public int getConstructionProgress() {
        int fullConstructionTime = getConstructionCountdown();
        int currentConstructionTime = countdown.getCount();

        if (currentConstructionTime == fullConstructionTime) {
            return 0;
        }

        return (int) (((fullConstructionTime - countdown.getCount()) / (double) fullConstructionTime) * 100);
    }

    /* Intended to be overridden by subclasses if needed */
    void onConstructionFinished() {
        // Empty - available for subclasses to implement
    }

    void setGeneration(long generation) {
        this.generation = generation;
    }

    public long getGeneration() {
        return generation;
    }

    public String getSimpleName() {
        String className = getClass().getSimpleName();
        String nameWithSpaces = className.replace("_", " ");
        String nameLowerCase = nameWithSpaces.toLowerCase();

        return nameLowerCase.substring(0, 1).toUpperCase() + nameLowerCase.substring(1);
    }

    public boolean isStorehouse() {
        return false;
    }

    public boolean isHeadquarter() {
        return false;
    }

    public boolean isPlanned() {
        return state == State.PLANNED;
    }

    protected boolean needsBuilder() {
        return this.state == State.PLANNED && builder == null;
    }

    public void promiseBuilder(Builder builder) {
        this.builder = builder;
    }

    void startConstruction() {
        state = State.UNDER_CONSTRUCTION;

        countdown.countFrom(getConstructionCountdown());

        map.reportBuildingUnderConstruction(this);
    }

    public Builder getBuilder() {
        return builder;
    }

    void cancelPromisedBuilder(Builder builder) {
        this.builder = null;
    }

    public boolean isHarbor() {
        return false;
    }

    void onBuildingOccupied() {
        // Empty - available for subclasses to implement
    }

    void onStepTime() {
        // Empty - available for subclasses to implement
    }

    public int getHostedSoldiersWithRank(Rank rank) {
        return ((Long) hostedSoldiers.stream().filter(soldier -> soldier.getRank() == rank).count()).intValue();
    }

    public void removeWaitingAttacker(Soldier soldier) {
        waitingAttackers.remove(soldier);
    }

    public void removeWaitingDefender(Soldier defender) {
        waitingDefenders.remove(defender);
    }

    public void registerWaitingAttacker(Soldier attacker) {
        waitingAttackers.add(attacker);

        /* Send out a local defender if there is none (and if possible) */
        if (!getHostedSoldiers().isEmpty() && ownDefender == null) {
            var sortedHostedSoldiers = GameUtils.sortSoldiersByPreferredRank(getHostedSoldiers(), player.getDefenseStrength());

            this.ownDefender = sortedHostedSoldiers.getFirst();

            retrieveHostedSoldier(ownDefender);

            ownDefender.defendOwnBuilding(this);
        }

        /* Try to get remote defenders */
        if (defenders.size() <= 1) {

            /* Find potential defenders */
            List<Soldier> potentialDefenders = new ArrayList<>();

            player.getBuildings()
                    .stream()
                    .filter(building -> !building.equals(this))
                    .filter(Building::isReady)
                    .filter(Building::isMilitaryBuilding)
                    .filter(building ->
                            (building instanceof Headquarter headquarter &&
                                    headquarter.hasAny(PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL)) ||
                            building.getHostedSoldiers().size() > 1)
                    .filter(building -> building.getAttackRadius() >= GameUtils.distanceInGameSteps(position, building.getPosition()))
                    .forEach(building -> {
                        var hostedSoldiersSorted = GameUtils.sortSoldiersByPreferredRank(building.getHostedSoldiers(), player.getDefenseStrength());

                        potentialDefenders.addAll(hostedSoldiersSorted.subList(0, hostedSoldiersSorted.size() - 1));
                    });

            /* Sort by rank, then distance to this building */
            GameUtils.sortSoldiersByPreferredRankAndDistance(potentialDefenders, player.getDefenseStrength(), getPosition());

            /* Pick defender(s) to come and help defending */
            if (!potentialDefenders.isEmpty()) {
                var nrDefendersToPick = (int) Math.round(potentialDefenders.size() * (player.getDefenseFromSurroundingBuildings() / 10.0));

                potentialDefenders.subList(0, nrDefendersToPick).forEach(soldier -> soldier.defendOtherBuilding(this));
            }
        }
    }

    public Set<Soldier> getDefenders() {
        return defenders;
    }

    private int getWantedAmountHostedSoldiers() {
        int distanceToBorder = player.getBorderPoints().stream().mapToInt(point -> GameUtils.distanceInGameSteps(position, point)).min().getAsInt();

        int populationSetting = player.getAmountOfSoldiersWhenPopulatingFarFromBorder();

        if (distanceToBorder <= getDefenceRadius()) {
            populationSetting = player.getAmountOfSoldiersWhenPopulatingCloseToBorder();
        } else if (distanceToBorder < getDefenceRadius() * 2) {
            populationSetting = player.getAmountOfSoldiersWhenPopulatingAwayFromBorder();
        }

        return 1 + ((Long)
                (Math.round((getMaxHostedSoldiers() - 1) *
                        populationSetting / 10.0))).intValue();
    }
}
