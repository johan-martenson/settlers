package org.appland.settlers.model.buildings;

import org.appland.settlers.model.BorderChangeCause;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.EndPoint;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Soldier.Rank;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.utils.Duration;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toCollection;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;

public class Building implements EndPoint {

    public record PlanksAndStones(int planks, int stones) {
        public boolean contains(Material material) {
            return (material == PLANK && planks > 0) || (material == STONE && stones > 0);
        }

        public int getAmount(Material material) {
            return switch (material) {
                case PLANK -> planks;
                case STONE -> stones;
                default -> 0;
            };
        }
    }

    private static final int TIME_TO_PROMOTE_SOLDIER = 100;
    private static final int TIME_TO_BUILD_SMALL_HOUSE = 100;
    private static final int TIME_TO_BUILD_MEDIUM_HOUSE = 150;
    private static final int TIME_TO_BUILD_LARGE_HOUSE = 200;
    private static final int TIME_TO_BURN_DOWN = 49;
    private static final int TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR = 99;
    private static final int TIME_TO_UPGRADE = 99;

    private final PlanksAndStones materialsToBuildHouse = getMaterialsToBuildHouse();
    private final Map<Material, Integer> totalAmountNeededForProduction = getMaterialNeededForProduction();
    private final int maxHostedSoldiers = initMaxHostedSoldiers();
    private final int defenceRadius = initDefenceRadius();
    private final int discoveryRadius = initDiscoveryRadius();
    private final int maxCoins = initCanStoreAmountCoins();
    private final Material workerType = initWorkerType();
    private final Map<Material, Integer> upgradeCost = initUpgradeCost();
    private final Map<Material, Integer> requiredGoodsForProduction = initRequiredGoodsForProduction();
    private final List<Soldier> attackers = new LinkedList<>();
    private final Set<Soldier> waitingAttackers = new HashSet<>();
    private final Set<Soldier> remoteDefenders = new HashSet<>();
    private final Countdown countdown = new Countdown();
    private final Countdown upgradeCountdown = new Countdown();
    private final Map<Material, Integer> promisedDeliveries = new EnumMap<>(Material.class);
    private final List<Soldier> hostedSoldiers = new ArrayList<>();
    private final List<Soldier> promisedSoldier = new ArrayList<>();
    private final Set<Soldier> waitingDefenders = new HashSet<>();

    private enum State {
        UNDER_CONSTRUCTION,
        UNOCCUPIED,
        OCCUPIED,
        BURNING,
        PLANNED,
        DESTROYED
    }

    protected final Map<Material, Integer> inventory = new EnumMap<>(Material.class);
    protected GameMap map = null;
    protected Player player;

    private Flag flag = new Flag(null);
    private Set<Point> defendedLand = null;
    private long generation;
    public State state = State.PLANNED;
    private Worker worker = null;
    private Worker promisedWorker = null;
    private Point position = null;
    private boolean enablePromotions = true;
    private boolean evacuated = false;
    private boolean productionEnabled = true;
    private boolean upgrading = false;
    private Soldier ownDefender = null;
    private Soldier primaryAttacker = null;
    private boolean outOfResources = false;
    private Builder builder = null;
    private DoorState door = DoorState.CLOSED;
    private int doorClosing = 0;

    public Building(Player player) {
        this.player = player;

        countdown.countFrom(getConstructionCountdown());
        flag.setPlayer(player);
    }

    private int initMaxHostedSoldiers() {
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);
        return militaryBuilding != null ? militaryBuilding.maxHostedSoldiers() : 0;
    }

    private int initDefenceRadius() {
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);
        return militaryBuilding != null ? militaryBuilding.defenceRadius() : 0;
    }

    private Map<Material, Integer> initRequiredGoodsForProduction() {
        var requiredGoods = new EnumMap<Material, Integer>(Material.class);
        var production = getClass().getAnnotation(Production.class);

        if (production != null) {
            for (var material : production.requiredGoods()) {
                requiredGoods.merge(material, 1, Integer::sum);
            }
        }

        return requiredGoods;
    }

    private Map<Material, Integer> initUpgradeCost() {
        var materialNeeded = new EnumMap<Material, Integer>(Material.class);
        var upgradeCost = getClass().getAnnotation(UpgradeCost.class);

        if (upgradeCost != null) {
            materialNeeded.put(STONE, upgradeCost.stones());
            materialNeeded.put(PLANK, upgradeCost.planks());
        }

        return materialNeeded;
    }

    public void setFlag(Flag flagAtPoint) {
        flag = flagAtPoint;
        flag.setPlayer(player);
    }

    public int getDefenceRadius() {
        return defenceRadius;
    }

    public Collection<Point> getDefendedLand() {
        if (defendedLand == null) {
            defendedLand = GameUtils.getHexagonAreaAroundPoint(position, defenceRadius - 1, player.getMap());
        }

        return defendedLand;
    }

    public int getAmount(Material material) {
        return inventory.getOrDefault(material, 0);
    }

    public void consumeOne(Material material) {
        inventory.merge(material, -1, Integer::sum);
    }

    private int initDiscoveryRadius() {
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);
        return militaryBuilding != null ? militaryBuilding.discoveryRadius() : 0;
    }

    public Collection<Point> getDiscoveredLand() {
        return GameUtils.getHexagonAreaAroundPoint(getPosition(), discoveryRadius, getMap());
    }

    public boolean isMine() {
        return false;
    }

    private int initCanStoreAmountCoins() {
        MilitaryBuilding militaryBuilding = getClass().getAnnotation(MilitaryBuilding.class);
        return militaryBuilding != null ? militaryBuilding.maxCoins() : 0;
    }

    public void setMap(GameMap map) {
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
        return workerType != null && worker == null && state == State.UNOCCUPIED && promisedWorker == null;
    }

    public Material getWorkerType() {
        return workerType;
    }

    private Material initWorkerType() {
        RequiresWorker requiresWorker = getClass().getAnnotation(RequiresWorker.class);
        return requiresWorker != null ? requiresWorker.workerType() : null;
    }

    public void promiseSoldier(Soldier military) {
        promisedSoldier.add(military);
    }

    public void promiseWorker(Worker worker) {
        if (state != State.UNOCCUPIED) {
            throw new InvalidGameLogicException(format("Can't promise worker to building in state %s", state));
        }

        if (promisedWorker != null) {
            throw new InvalidGameLogicException(format("Building %s is already promised worker %s", this, promisedWorker));
        }

        promisedWorker = worker;
    }

    public boolean needsMilitaryManning() {
        return switch (state) {
            case UNOCCUPIED, OCCUPIED -> {
                int promised = promisedSoldier.size();
                int actual = hostedSoldiers.size();
                int wanted = getWantedAmountHostedSoldiers();

                yield !evacuated && isReady() && wanted > promised + actual;
            }
            default -> false;
        };
    }

    public int getPromisedSoldier() {
        return promisedSoldier.size();
    }

    public void assignWorker(Worker worker) {
        switch (state) {
            case UNOCCUPIED -> {
                this.worker = worker;
                promisedWorker = null;
                state = State.OCCUPIED;

                // Give each type of building a chance to add extra logic when the building has become occupied
                onBuildingOccupied();
            }
            default ->
                    throw new InvalidGameLogicException(format("Can't assign %s to building in state %s", worker, state));
        }
    }

    public boolean isSpaceAvailableToHostSoldier(Soldier soldier) {
        return hostedSoldiers.size() < getMaxHostedSoldiers();
    }

    public void deploySoldier(Soldier soldier) {
        if (!isReady()) {
            throw new InvalidGameLogicException("Cannot assign military when the building is not ready");
        }

        if (!isSpaceAvailableToHostSoldier(soldier)) {
            throw new InvalidGameLogicException(format("Cannot host military, %s already hosting %d soldiers", this, hostedSoldiers.size()));
        }

        State previousState = state;
        state = State.OCCUPIED;

        if (previousState == State.UNOCCUPIED) {
            map.updateBorder(this, BorderChangeCause.MILITARY_BUILDING_OCCUPIED);
            player.reportMilitaryBuildingOccupied(this);
        }

        if (!isEvacuated()) {
            hostedSoldiers.add(soldier);
        } else {
            soldier.returnToStorage();
        }
        promisedSoldier.remove(soldier);

        player.reportSoldierEnteredBuilding(this);
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point point) {
        position = point;
    }

    @Override
    public void putCargo(Cargo cargo) {
        var material = cargo.getMaterial();

        if (promisedDeliveries.getOrDefault(material, 0) == 0) {
            switch (state) {
                case PLANNED, UNDER_CONSTRUCTION -> {
                    if (!materialsToBuildHouse.contains(material)) {
                        throw new InvalidMaterialException(material);
                    }

                    if (getAmount(material) >= getCanHoldAmount(material)) {
                        throw new InvalidGameLogicException(format("Can't accept delivery of %s", material));
                    }
                }
                case BURNING, DESTROYED -> throw new InvalidStateForProduction(this);
                case UNOCCUPIED, OCCUPIED -> {
                    if (material == COIN && isMilitaryBuilding() && getAmount(COIN) >= maxCoins) {
                        throw new InvalidGameLogicException("This building doesn't need any more coins");
                    }

                    if (!canAcceptGoods()) {
                        throw new DeliveryNotPossibleException(this, cargo);
                    }

                    if (!isAccepted(material)) {
                        throw new InvalidMaterialException(material);
                    }
                }
                default -> throw new InvalidGameLogicException("Invalid building state for delivery");
            }
        }

        // Update the list of received materials and the list of promised deliveries
        inventory.merge(material, 1, Integer::sum);
        promisedDeliveries.merge(material, -1, Integer::sum);

        // Start the promotion countdown if it's a coin
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
        return inventory.entrySet().stream().anyMatch(pair -> pair.getValue() != 0)
                ? format("%s at %s with %s in queue and state: %s", getClass().getSimpleName(), flag, flag.getStackedCargo(), state)
                : format("%s at %s with nothing in queue and state: %s", getClass().getSimpleName(), flag, state);
    }

    public void promiseDelivery(Material material) {
        promisedDeliveries.merge(material, 1, Integer::sum);
    }

    public void stepTime() {
        var stats = map.getStats();
        var counterName = format("Building.%s.stepTime", getClass().getSimpleName());

        stats.addPeriodicCounterVariableIfAbsent(counterName);
        stats.addVariableToGroup(counterName, StatsConstants.AGGREGATED_EACH_STEP_TIME_GROUP);

        var duration = new Duration(counterName);

        if (door == DoorState.OPEN_CLOSE_SOON) {
            if (doorClosing == 0) {
                door = DoorState.CLOSED;
                map.reportChangedBuilding(this);
            } else {
                doorClosing--;
            }
        }

        if (isUnderAttack()) {
            if (ownDefender == null &&
                    getNumberOfHostedSoldiers() > 0 &&
                    primaryAttacker != null &&
                    primaryAttacker.isWaitingForFight()) {
                ownDefender = retrieveHostedSoldier();
                ownDefender.defendOwnBuilding(this);
            }

            if (remoteDefenders.isEmpty()) {
                List<Soldier> potentialDefenders = player.getBuildings().stream()
                        .filter(b -> !b.equals(this) && b.isReady() && b.isMilitaryBuilding())
                        .filter(b -> b instanceof Headquarter hq && hq.hasAny(PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL)
                                || b.getHostedSoldiers().size() > 1)
                        .filter(b -> b.getAttackRadius() >= GameUtils.distanceInGameSteps(position, b.getPosition()))
                        .flatMap(b -> {
                            var sorted = GameUtils.sortSoldiersByPreferredStrength(b.getHostedSoldiers(), player.getDefenseStrength());
                            return sorted.subList(0, sorted.size() - 1).stream();
                        })
                        .collect(toCollection(ArrayList::new));

                GameUtils.sortSoldiersByPreferredStrengthAndDistance(potentialDefenders, player.getDefenseStrength(), getPosition());

                if (!potentialDefenders.isEmpty()) {
                    int count = (int) Math.round(potentialDefenders.size() * (player.getDefenseFromSurroundingBuildings() / 10.0));
                    potentialDefenders.subList(0, count).forEach(soldier -> soldier.defendOtherBuilding(this));
                }
            }
        }

        switch (state) {
            case UNDER_CONSTRUCTION -> {
                if (countdown.hasReachedZero()) {
                    if (isMaterialForConstructionAvailable()) {
                        consumeConstructionMaterial();
                        state = State.UNOCCUPIED;
                        door = DoorState.OPEN;

                        if (isMilitaryBuilding()) {
                            player.reportMilitaryBuildingReady(this);
                        }

                        onConstructionFinished();
                        map.reportBuildingConstructed(this);
                    }
                } else {
                    countdown.step();
                    if (countdown.getCount() % 10 == 0) {
                        player.reportChangedBuilding(this);
                    }
                }
            }

            case BURNING -> {
                if (countdown.hasReachedZero()) {
                    state = State.DESTROYED;
                    countdown.countFrom(TIME_FOR_DESTROYED_HOUSE_TO_DISAPPEAR);
                    map.reportBuildingBurnedDown(this);
                } else {
                    countdown.step();
                }
            }

            case OCCUPIED -> {
                if (isMilitaryBuilding() && !isHeadquarter() && getHostedSoldiers().size() > getWantedAmountHostedSoldiers()) {
                    var sorted = GameUtils.sortSoldiersByPreferredStrength(getHostedSoldiers(), player.getStrengthOfSoldiersPopulatingBuildings());
                    var soldier = sorted.getLast();
                    hostedSoldiers.remove(soldier);
                    soldier.returnToStorage();
                }

                if (isMilitaryBuilding() && getAmount(COIN) > 0 && hostsPromotableSoldiers()) {
                    if (countdown.hasReachedZero()) {
                        doPromotion();
                    } else {
                        countdown.step();
                    }
                }
            }

            case DESTROYED -> {
                if (countdown.hasReachedZero()) {
                    map.removeBuilding(this);
                    map.reportBuildingRemoved(this);
                } else {
                    countdown.step();
                }
            }
        }

        if (upgrading) {
            if (upgradeCountdown.hasReachedZero()) {
                if (isMaterialForUpgradeAvailable()) {
                    doUpgradeBuilding();
                    map.updateBorder(this, BorderChangeCause.MILITARY_BUILDING_UPGRADED);
                }
            } else {
                upgradeCountdown.step();
            }
        }

        onStepTime();

        duration.after("stepTime");
        stats.reportVariableValue(counterName, duration.getFullDuration());
    }

    public Flag getFlag() {
        return flag;
    }

    public void tearDown() throws InvalidUserActionException {

        // A building cannot be torn down if it's already burning or destroyed
        if (state == State.BURNING || state == State.DESTROYED) {
            throw new InvalidUserActionException("The building has already been torn down.");
        }

        // Update statistics
        map.getStatisticsManager().houseTornDown(this, map.getTime());

        // Clear up after the attack
        attackers.clear();
        waitingAttackers.clear();
        remoteDefenders.clear();
        ownDefender = null;

        // Change building state
        if (state != State.PLANNED) {
            state = State.BURNING;
        }

        // Start countdown for burning
        countdown.countFrom(TIME_TO_BURN_DOWN);

        // Update the border if this was a military building
        if (isMilitaryBuilding()) {
            map.updateBorder(this, BorderChangeCause.MILITARY_BUILDING_TORN_DOWN);
        }

        // Send home the worker and builder (if any)
        if (worker != null) {
            worker.returnToStorage();
        }

        if (builder != null) {
            builder.returnToStorage();
        }

        // Send home deployed soldiers
        for (var military : hostedSoldiers) {
            military.returnToStorage();
        }

        // Remove driveway
        var driveway = map.getRoad(getPosition(), getFlag().getPosition());

        map.doRemoveRoad(driveway);

        // Report that the building has been torn down
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
        inventory.merge(PLANK, materialsToBuildHouse.planks(), (currentValue, newValues) -> currentValue - newValues);
        inventory.merge(STONE, materialsToBuildHouse.stones(), (currentValue, newValues) -> currentValue - newValues);

        map.getStatisticsManager().buildingConstructed(this, map.getTime());
    }

    // FIXME: HOTSPOT - allocations
    private PlanksAndStones getMaterialsToBuildHouse() {
        HouseSize houseSize = this.getClass().getAnnotation(HouseSize.class);

        int planks = 0;
        int stones = 0;

        for (Material material : houseSize.material()) {
            if (material == PLANK) {
                planks++;
            } else if (material == STONE) {
                stones++;
            }
        }

        return new PlanksAndStones(planks, stones);
    }

    private int getConstructionCountdown() {
        HouseSize sizeAnnotation = getClass().getAnnotation(HouseSize.class);

        return switch (sizeAnnotation.size()) {
            case SMALL -> TIME_TO_BUILD_SMALL_HOUSE;
            case MEDIUM -> TIME_TO_BUILD_MEDIUM_HOUSE;
            case LARGE -> TIME_TO_BUILD_LARGE_HOUSE;
        };
    }

    private boolean isMaterialForConstructionAvailable() {
        return inventory.getOrDefault(PLANK, 0) >= materialsToBuildHouse.planks() &&
                inventory.getOrDefault(STONE, 0) >= materialsToBuildHouse.stones();
    }

    private boolean isAccepted(Material material) {
        return getCanHoldAmount(material) > 0;
    }

    private boolean canAcceptGoods() {
        if (!requiredGoodsForProduction.isEmpty()) {
            return true;
        }

        if (isMilitaryBuilding()) {
            if (isPromotionEnabled() && maxCoins > getAmount(COIN)) {
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

    public void setConstructionReady() {
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

        for (var rank : Rank.values()) {
            if (rank == GENERAL_RANK) {
                continue;
            }

            for (var military : hostedSoldiers) {
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

        player.reportDisabledPromotions(this);
    }

    public void enablePromotions() {
        enablePromotions = true;

        player.reportEnabledPromotions(this);
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

        player.reportBuildingEvacuationCanceled(this);
    }

    public void stopProduction() throws InvalidUserActionException {
        productionEnabled = false;

        player.reportProductionStopped(this);
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

    public boolean canAttack(Building buildingToAttack) {
        if (isMilitaryBuilding()) {
            double distance = getPosition().distance(buildingToAttack.getPosition());

            return distance < getAttackRadius();
        }

        return false;
    }

    private int getAttackRadius() {
        return getClass().getAnnotation(MilitaryBuilding.class).attackRadius();
    }

    public Soldier retrieveHostedSoldier(Soldier soldier) {
        hostedSoldiers.remove(soldier);

        return soldier;
    }

    public Soldier retrieveHostedSoldier() {
        for (var rank : GameUtils.strengthToRank(player.getDefenseStrength())) {
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

    public void registerRemoteDefender(Soldier defender) {
        remoteDefenders.add(defender);
    }

    public void removeDefender(Soldier defender) {
        if (defender.equals(ownDefender)) {
            ownDefender = null;
        }

        remoteDefenders.remove(defender);
    }

    public void registerAttacker(Soldier attacker) {
        if (!attackers.contains(attacker)) {
            attackers.add(attacker);
        }
    }

    public void removeAttacker(Soldier attacker) {
        waitingAttackers.remove(attacker);
        attackers.remove(attacker);

        if (attacker.equals(primaryAttacker)) {
            primaryAttacker = null;
        }
    }

    public Set<Soldier> getWaitingSecondaryAttackers() {
        return waitingAttackers.stream()
                .filter(soldier -> !Objects.equals(soldier, primaryAttacker))
                .collect(Collectors.toSet());
    }

    public Set<Soldier> getWaitingAttackers() {
        return waitingAttackers;
    }

    public Soldier pickWaitingSecondaryAttacker() {
        var attacker = getWaitingSecondaryAttackers().iterator().next();
        waitingAttackers.remove(attacker);

        return attacker;
    }

    public Soldier pickWaitingAttacker() {
        var attacker = waitingAttackers.iterator().next();
        waitingAttackers.remove(attacker);

        return attacker;
    }

    public List<Soldier> getAttackers() {
        return attackers;
    }

    public boolean isUnderAttack() {
        return !attackers.isEmpty();
    }

    public Soldier getPrimaryAttacker() {
        return primaryAttacker;
    }

    public void setPrimaryAttacker(Soldier attacker) {
        primaryAttacker = attacker;
    }

    public boolean isDefenseLess() {
        if (getNumberOfHostedSoldiers() == 0 && remoteDefenders.isEmpty() && ownDefender == null) {
            return true;
        }

        return false;
    }

    public void capture(Player player) throws InvalidUserActionException {
        setPlayer(player);

        promisedSoldier.clear();
        attackers.clear();
        remoteDefenders.clear();
        ownDefender = null;
        evacuated = false;
    }

    public void cancelPromisedDelivery(Cargo cargo) {
        promisedDeliveries.merge(cargo.getMaterial(), -1, Integer::sum);
    }

    /**
     * Returns the total amount needed for the given material
     * <p>
     * Considers:
     * - Building planned, under construction, unoccupied/occupied
     * <p>
     * Does not consider:
     * - Current inventory
     * - Whether promotions are enabled/disabled
     * - Whether production is paused/resumed
     * <p>
     * NOTE: Will not return valid response when called for ready Storehouse or for Headquarters
     *
     * @param material to find the total need for
     * @return the total need for the material
     */
    public int getCanHoldAmount(Material material) {
        return switch (state) {
            case PLANNED, UNDER_CONSTRUCTION -> materialsToBuildHouse.getAmount(material);

            case UNOCCUPIED, OCCUPIED -> {
                if (isMilitaryBuilding()) {
                    if (material == COIN) {
                        yield  maxCoins;
                    }

                    if (isUpgrading()) {
                        yield  upgradeCost.getOrDefault(material, 0);
                    }

                    yield 0;
                } else {
                    yield  requiredGoodsForProduction.getOrDefault(material, 0);
                }
            }

            default -> 0;
        };
    }

    private int getLackingAmountWithProjected(Material material) {
        return switch (state) {
            case State.UNDER_CONSTRUCTION, State.PLANNED -> {
                if (!materialsToBuildHouse.contains(material)) {
                    yield 0;
                }

                int total = materialsToBuildHouse.getAmount(material);
                int promised = promisedDeliveries.getOrDefault(material, 0);
                int received = inventory.getOrDefault(material, 0);

                yield total - promised - received;
            }
            case State.UNOCCUPIED, State.OCCUPIED -> {
                if (isMilitaryBuilding()) {
                    if (material != PLANK && material != STONE && material != COIN) {
                        yield 0;
                    }

                    int total = 0;

                    // Handle coins for promotions
                    if (material == COIN && enablePromotions) {
                        total = maxCoins;

                        // Handle planks and stones for upgrades
                    } else if (isUpgrading()) {
                        total = upgradeCost.getOrDefault(material, 0);
                    }

                    if (total > 0) {
                        int promised = promisedDeliveries.getOrDefault(material, 0);
                        int received = inventory.getOrDefault(material, 0);

                        yield total - promised - received;
                    }

                    yield 0;
                } else {
                    int total = totalAmountNeededForProduction.getOrDefault(material, 0);
                    int promised = promisedDeliveries.getOrDefault(material, 0);
                    int received = inventory.getOrDefault(material, 0);

                    yield total - promised - received;
                }
            }
            default -> 0;
        };
    }

    public void hitByCatapult(Catapult catapult) throws InvalidUserActionException {
        player.reportHitByCatapult(catapult, this);

        if (getNumberOfHostedSoldiers() > 0) {
            hostedSoldiers.removeFirst();

            map.getStatisticsManager().soldierDied(player, catapult.getPlayer(), map.getTime());
        } else {
            tearDown();
        }
    }

    public void reportNoMoreNaturalResources() {
        outOfResources = true;
    }

    public boolean isOutOfNaturalResources() {
        return outOfResources;
    }

    public void upgrade() throws InvalidUserActionException {

        // Refuse to upgrade non-upgradable buildings
        if (!isUpgradable()) {
            throw new InvalidUserActionException(format("Cannot upgrade %s", getClass().getSimpleName()));
        }

        // Refuse to upgrade while under construction
        if (state == State.UNDER_CONSTRUCTION || state == State.PLANNED) {
            throw new InvalidUserActionException("Cannot upgrade while under construction.");
        }

        // Refuse to upgrade while being torn down
        if (isBurningDown()) {
            throw new InvalidUserActionException("Cannot upgrade while burning down.");
        }

        // Refuse to upgrade while already being upgraded
        if (isUpgrading()) {
            throw new InvalidUserActionException("Cannot upgrade while being upgraded.");
        }

        // Start the upgrade
        upgrading = true;

        upgradeCountdown.countFrom(TIME_TO_UPGRADE);

        player.reportUpgradeStarted(this);
    }

    public void doUpgradeBuilding() {
        // Empty - available for subclasses to implement
    }

    private boolean isMaterialForUpgradeAvailable() {

        // Get the cost for upgrade
        UpgradeCost upgradeCost = getClass().getAnnotation(UpgradeCost.class);

        int planksNeeded = upgradeCost.planks();
        int stoneNeeded = upgradeCost.stones();

        // Get available resources
        int plankAvailable = inventory.getOrDefault(PLANK, 0);
        int stoneAvailable = inventory.getOrDefault(STONE, 0);

        // Determine if an upgrade is possible
        if (planksNeeded <= plankAvailable && stoneNeeded <= stoneAvailable) {
            return true;
        } else {
            return false;
        }
    }

    private int getTotalAmountNeededForUpgrade(Material material) {
        UpgradeCost upgrade = getClass().getAnnotation(UpgradeCost.class);

        // Only need material for upgrades if the building is actually being upgraded
        if (!isUpgrading()) {
            return 0;
        }

        // Only require material for upgrades if the building is capable of upgrades
        if (upgrade == null) {
            return 0;
        }

        // Only planks and stones are used for upgrades
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

    public void setOccupied() {
        state = State.OCCUPIED;
    }

    public int getProductivity() {
        if (isOutOfNaturalResources()) {
            return 0;
        }

        // An unoccupied building has no productivity
        if (worker == null) {
            return 0;
        }

        return worker.getProductivity();
    }

    public boolean canProduce() {
        return getClass().getAnnotation(Production.class) != null;
    }

    public Material[] getProducedMaterial() {
        Production production = getClass().getAnnotation(Production.class);

        return production == null
                ? new Material[]{}
                : getClass().getAnnotation(Production.class).output();
    }

    private Map<Material, Integer> getMaterialNeededForProduction() {
        var production = getClass().getAnnotation(Production.class);

        if (production == null) {
            return Map.of(); // immutable empty map
        }

        var materialNeeded = new EnumMap<Material, Integer>(Material.class);

        for (var material : production.requiredGoods()) {
            materialNeeded.merge(material, 1, Integer::sum);
        }

        return materialNeeded;
    }

    /**
     * Returns the set of materials the building needs. Does not consider:
     * - Current inventory
     * - Whether production is enabled/disabled
     * - Whether in case of military building evacuations are ordered
     * <p>
     * NOTE: It does not return material needed because of upgrade if the building is military and is being upgraded
     * <p>
     * NOTE: It does consider whether max amount of coins are already stored. This is most likely a bug
     *
     * @return the set of materials the building type needs
     */
    public Collection<Material> getTypesOfMaterialNeeded() {
        var result = EnumSet.noneOf(Material.class);

        if (state == State.UNDER_CONSTRUCTION || state == State.PLANNED) {
            HouseSize houseSize = getClass().getAnnotation(HouseSize.class);
            result.addAll(Arrays.asList(houseSize.material()));
        } else if (isReady()) {
            Production production = getClass().getAnnotation(Production.class);

            if (production != null) {
                result.addAll(Arrays.asList(production.requiredGoods()));
            }

            if (isMilitaryBuilding() && getAmount(COIN) < maxCoins && isPromotionEnabled()) {
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

    // Intended to be overridden by subclasses if needed
    public void onConstructionFinished() {
        // Empty - available for subclasses to implement
    }

    public void setGeneration(long generation) {
        this.generation = generation;
    }

    public long getGeneration() {
        return generation;
    }

    public String getSimpleName() {
        return getClass().getSimpleName();
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

    public boolean needsBuilder() {
        return this.state == State.PLANNED && builder == null;
    }

    public void promiseBuilder(Builder builder) {
        this.builder = builder;
    }

    public void startConstruction() {
        state = State.UNDER_CONSTRUCTION;
        countdown.countFrom(getConstructionCountdown());

        map.reportBuildingUnderConstruction(this);
    }

    public Builder getBuilder() {
        return builder;
    }

    public void cancelPromisedBuilder(Builder builder) {
        this.builder = null;
    }

    public boolean isHarbor() {
        return false;
    }

    public void onBuildingOccupied() {
        // Empty - available for subclasses to implement
    }

    public void onStepTime() {
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
    }

    public Set<Soldier> getRemoteDefenders() {
        return remoteDefenders;
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

    public boolean isDoorClosed() {
        return door == DoorState.CLOSED;
    }

    public void openDoor(int time) {
        door = DoorState.OPEN_CLOSE_SOON;
        doorClosing = time;

        map.reportChangedBuilding(this);
    }

    public void openDoor() {
        door = DoorState.OPEN;
        map.reportChangedBuilding(this);
    }

    public void closeDoor() {
        door = DoorState.CLOSED;
        map.reportChangedBuilding(this);
    }

    public boolean hasOwnDefender() {
        return ownDefender != null;
    }

    public Soldier pickPrimaryAttacker() {
        waitingAttackers.remove(primaryAttacker);

        return primaryAttacker;
    }

    public boolean isWorking() {
        return worker != null && worker.isWorking();
    }

    public PlanksAndStones getMaterialNeededForConstruction() {
        return materialsToBuildHouse;
    }

    public Map<Material, Integer> getInventory() {
        return inventory;
    }
}
