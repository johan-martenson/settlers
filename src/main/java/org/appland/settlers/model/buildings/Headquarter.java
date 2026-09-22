package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.ResourceLevel;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.actors.Rank;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.utils.InventoryUtils;
import org.appland.settlers.policy.InitialState;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static org.appland.settlers.model.GameUtils.createSoldiersForStorehouse;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Rank.*;
import static org.appland.settlers.model.utils.MilitaryUtils.strengthToRank;

/**
 * TODO:
 *  - should Headquarters signal that it needs more soldiers if reserved is higher than actual?
 *  - can't evacuate headquarters - test
 *  - retrieveHostedSoldier should use military-settings.defense-strength to get the right rank. Test!
 *  - only soldiers that aren't reserved are part of the inventory - test
 *  - headquarters gets hit by catapult - hostedSoldiers and hostedSoldiersByRank are both affected
 *
 * Consider getMaxHostedSoldiers, isSpaceAvailableToHostSoldier
 */
@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedSoldiers = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
public class Headquarter extends Storehouse {
    private static final Map<Material, Integer> LOW_RESOURCES = Map.<Material, Integer>ofEntries(
            entry(SHIELD, 0),
            entry(SWORD, 0),

            entry(PRIVATE, 14), // Should be 13 and 1 in reserve
            entry(PRIVATE_FIRST_CLASS, 0),
            entry(SERGEANT, 0),
            entry(OFFICER, 0),
            entry(GENERAL, 0),

            entry(WOOD, 12),
            entry(PLANK, 22),
            entry(STONE, 34),
            entry(WHEAT, 0),
            entry(FISH, 2),
            entry(MEAT, 3),
            entry(BREAD, 4),
            entry(WATER, 0),
            entry(BEER, 0),
            entry(GOLD, 0),
            entry(COAL, 8),
            entry(IRON, 8),
            entry(IRON_BAR, 0),
            entry(COIN, 0),
            entry(PIG, 0),
            entry(DONKEY, 4),

            entry(FORESTER, 2),
            entry(WOODCUTTER_WORKER, 4),
            entry(STONEMASON, 2),
            entry(FARMER, 0),
            entry(CARPENTER, 2),
            entry(WELL_WORKER, 0),
            entry(MILLER, 0),
            entry(BAKER, 0),
            entry(STOREHOUSE_WORKER, 0),
            entry(FISHERMAN, 0),
            entry(IRON_FOUNDER, 0),
            entry(BREWER, 0),
            entry(MINTER, 0),
            entry(PIG_BREEDER, 0),
            entry(BUTCHER, 0),
            entry(DONKEY_BREEDER, 0),
            entry(AXE, 3),
            entry(SAW, 1),
            entry(PICK_AXE, 1),
            entry(HAMMER, 8),
            entry(SHOVEL, 2),
            entry(CRUCIBLE, 2),
            entry(FISHING_ROD, 3),
            entry(SCYTHE, 4),
            entry(CLEAVER, 1),
            entry(ROLLING_PIN, 1),
            entry(BOW, 1),
            entry(BOAT, 6),
            entry(BUILDER, 5),
            entry(PLANER, 3),
            entry(HUNTER, 1),
            entry(MINER, 5),
            entry(ARMORER, 2),
            entry(METALWORKER, 1),
            entry(SHIPWRIGHT, 0),
            entry(GEOLOGIST, 3),
            entry(SCOUT, 1)
    );

    private static final Map<Material, Integer> MEDIUM_RESOURCES = Map.ofEntries(
            entry(PRIVATE, 51), // Should be 13 and 1 in reserve
            entry(PRIVATE_FIRST_CLASS, 0),
            entry(SERGEANT, 0),
            entry(OFFICER, 0),
            entry(GENERAL, 0),

            entry(WOOD, 24),
            entry(PLANK, 44),
            entry(STONE, 68),
            entry(WHEAT, 0),
            entry(FISH, 4),
            entry(MEAT, 6),
            entry(BREAD, 8),
            entry(WATER, 0),
            entry(BEER, 0),
            entry(GOLD, 0),
            entry(COAL, 16),
            entry(IRON, 16),
            entry(IRON_BAR, 0),
            entry(COIN, 0),
            entry(TONGS, 0),
            entry(AXE, 6),
            entry(SAW, 2),
            entry(PICK_AXE, 2),
            entry(HAMMER, 16),
            entry(SHOVEL, 4),
            entry(CRUCIBLE, 4),
            entry(FISHING_ROD, 6),
            entry(SCYTHE, 8),
            entry(CLEAVER, 2),
            entry(ROLLING_PIN, 2),
            entry(BOW, 2),
            entry(SHIELD, 0),
            entry(SWORD, 0),
            entry(BOAT, 12),

            entry(PIG, 0),

            entry(BUILDER, 10),
            entry(PLANER, 6),
            entry(WOODCUTTER_WORKER, 8),
            entry(FORESTER, 4),
            entry(STONEMASON, 4),
            entry(FISHERMAN, 0),
            entry(HUNTER, 2),
            entry(CARPENTER, 4),
            entry(FARMER, 0),
            entry(PIG_BREEDER, 0),
            entry(DONKEY_BREEDER, 0),
            entry(MILLER, 0),
            entry(BAKER, 0),
            entry(BUTCHER, 0),
            entry(BREWER, 0),
            entry(MINER, 10),
            entry(IRON_FOUNDER, 0),
            entry(WELL_WORKER, 0),
            entry(STOREHOUSE_WORKER, 0),
            entry(ARMORER, 4),
            entry(MINTER, 0),
            entry(METALWORKER, 2),
            entry(SHIPWRIGHT, 0),
            entry(GEOLOGIST, 6),
            entry(SCOUT, 2),
            entry(DONKEY, 8)
    );

    private static final Map<Material, Integer> HIGH_RESOURCES = Map.<Material, Integer>ofEntries(
            entry(PRIVATE, 103), // Should be 102 and 1 in reserve
            entry(PRIVATE_FIRST_CLASS, 0),
            entry(SERGEANT, 0),
            entry(OFFICER, 0),
            entry(GENERAL, 0),

            entry(WOOD, 48),
            entry(PLANK, 88),
            entry(STONE, 136),
            entry(WHEAT, 0),
            entry(FISH, 8),
            entry(MEAT, 12),
            entry(BREAD, 16),
            entry(WATER, 0),
            entry(BEER, 0),
            entry(GOLD, 0),
            entry(COAL, 32),
            entry(IRON, 32),
            entry(IRON_BAR, 0),
            entry(COIN, 0),
            entry(TONGS, 0),
            entry(AXE, 12),
            entry(SAW, 4),
            entry(PICK_AXE, 4),
            entry(HAMMER, 32),
            entry(SHOVEL, 8),
            entry(CRUCIBLE, 8),
            entry(FISHING_ROD, 12),
            entry(SCYTHE, 16),
            entry(CLEAVER, 4),
            entry(ROLLING_PIN, 4),
            entry(BOW, 4),
            entry(SHIELD, 0),
            entry(SWORD, 0),
            entry(BOAT, 24),

            entry(PIG, 0),

            entry(BUILDER, 20),
            entry(PLANER, 12),
            entry(WOODCUTTER_WORKER, 16),
            entry(FORESTER, 8),
            entry(STONEMASON, 8),
            entry(FISHERMAN, 0),
            entry(HUNTER, 4),
            entry(CARPENTER, 8),
            entry(FARMER, 0),
            entry(PIG_BREEDER, 0),
            entry(DONKEY_BREEDER, 0),
            entry(MILLER, 0),
            entry(BAKER, 0),
            entry(BUTCHER, 0),
            entry(BREWER, 0),
            entry(MINER, 20),
            entry(IRON_FOUNDER, 0),
            entry(WELL_WORKER, 0),
            entry(STOREHOUSE_WORKER, 0),
            entry(ARMORER, 8),
            entry(MINTER, 0),
            entry(METALWORKER, 4),
            entry(SHIPWRIGHT, 0),
            entry(GEOLOGIST, 12),
            entry(SCOUT, 4),
            entry(DONKEY, 16)
    );

    private final Map<Rank, Integer> wantedReservedSoldiers = new EnumMap<>(
            Map.of(
                    PRIVATE_RANK, 0,
                    PRIVATE_FIRST_CLASS_RANK, 0,
                    SERGEANT_RANK, 0,
                    OFFICER_RANK, 0,
                    GENERAL_RANK, 0
            )
    );
    private final Map<Rank, Set<Soldier>> hostedSoldiersByRank = Map.of(
            PRIVATE_RANK, new HashSet<>(),
            PRIVATE_FIRST_CLASS_RANK, new HashSet<>(),
            SERGEANT_RANK, new HashSet<>(),
            OFFICER_RANK, new HashSet<>(),
            GENERAL_RANK, new HashSet<>()
    );

    public Headquarter(Player player) {
        super(player);

        setHeadquarterDefaultInventory(inventory);

        setConstructionReady();
    }

    @Override
    public Soldier retrieveSoldierToPopulateBuilding() {

        // Go through the list in order of preference and try to retrieve a soldier
        for (var rank : strengthToRank(player.getStrengthOfSoldiersPopulatingBuildings())) {
            if (hostedSoldiersByRank.get(rank).size() > wantedReservedSoldiers.get(rank)) {
                var soldier = hostedSoldiersByRank.get(rank).iterator().next();

                hostedSoldiersByRank.get(rank).remove(soldier);
                hostedSoldiers.remove(soldier);

                player.reportChangedInventory(this);

                return soldier;
            }
        }

        throw new InvalidGameLogicException("Can't retrieve soldier!");
    }

    @Override
    public void deploySoldier(Soldier soldier) {
        super.deploySoldier(soldier);

        hostedSoldiersByRank.get(soldier.getRank()).add(soldier);
    }

    @Override
    public void putCargo(Cargo cargo) {
        var material = cargo.getMaterial();

        if (material.isSoldier()) {
            var rank = material.toRank();
            var soldier = new Soldier(player, rank, player.getMap());
            soldier.setHome(this);
            soldier.setPosition(position);

            hostedSoldiersByRank.get(rank).add(soldier);
            hostedSoldiers.add(soldier);
        } else {
            super.putCargo(cargo);
        }
    }

    @Override
    public boolean isSpaceAvailableToHostSoldier(Soldier soldier) {
        var rank = soldier.getRank();
        return wantedReservedSoldiers.getOrDefault(rank, 0) > getHostedSoldiersWithRank(rank);
    }

    @Override
    public void setMap(GameMap map) {
        super.setMap(map);

        var storageWorker = new StorehouseWorker(player, map);
        map.placeWorker(storageWorker, this);
        storageWorker.enterBuilding(this);
        assignWorker(storageWorker);
    }

    public void setInitialResources(ResourceLevel resourceLevel) {
        hostedSoldiers.clear();
        Arrays.stream(Rank.values()).forEach(rank -> hostedSoldiersByRank.get(rank).clear());
        inventory.clear();

        var resources = switch (resourceLevel) {
            case LOW -> LOW_RESOURCES;
            case MEDIUM -> MEDIUM_RESOURCES;
            case HIGH -> HIGH_RESOURCES;
        };

        Arrays.stream(Rank.values())
                .forEach(rank -> {
                    var soldiers = createSoldiersForStorehouse(rank, resources.get(rank.toMaterial()), player, this);
                    hostedSoldiersByRank.get(rank).addAll(soldiers);
                    hostedSoldiers.addAll(soldiers);
                });
        inventory.putAll(resources);

        var statisticsManager = map.getStatisticsManager();

        statisticsManager.getPlayerStatistics(player)
                .workers()
                .report(map.getTime(), InventoryUtils.countWorkersInInventory(this));

        statisticsManager.getPlayerStatistics(player)
                .goods()
                .report(map.getTime(), InventoryUtils.countGoodsInInventory(this));
    }

    private void setHeadquarterDefaultInventory(Map<Material, Integer> inventory) {
        inventory.put(SHIELD, InitialState.STORAGE_INITIAL_SHIELDS);
        inventory.put(SWORD, InitialState.STORAGE_INITIAL_SWORDS);
        inventory.put(BEER, InitialState.STORAGE_INITIAL_BEER);
        inventory.put(GOLD, InitialState.STORAGE_INITIAL_GOLD);

        // TODO: add default inventory for officer and private first class
        hostedSoldiersByRank.get(PRIVATE_RANK).addAll(createSoldiersForStorehouse(PRIVATE_RANK, InitialState.STORAGE_INITIAL_PRIVATE, player, this));
        hostedSoldiersByRank.get(PRIVATE_FIRST_CLASS_RANK).addAll(createSoldiersForStorehouse(PRIVATE_FIRST_CLASS_RANK, InitialState.STORAGE_INITIAL_PRIVATE_FIRST_CLASS, player, this));
        hostedSoldiersByRank.get(SERGEANT_RANK).addAll(createSoldiersForStorehouse(SERGEANT_RANK, InitialState.STORAGE_INITIAL_SERGEANT, player, this));
        hostedSoldiersByRank.get(OFFICER_RANK).addAll(createSoldiersForStorehouse(OFFICER_RANK, InitialState.STORAGE_INITIAL_OFFICER, player, this));
        hostedSoldiersByRank.get(GENERAL_RANK).addAll(createSoldiersForStorehouse(GENERAL_RANK, InitialState.STORAGE_INITIAL_GENERAL, player, this));

        Arrays.stream(Rank.values()).forEach(rank -> hostedSoldiers.addAll(hostedSoldiersByRank.get(rank)));

        inventory.put(WOOD, InitialState.STORAGE_INITIAL_WOOD);
        inventory.put(PLANK, InitialState.STORAGE_INITIAL_PLANKS);
        inventory.put(STONE, InitialState.STORAGE_INITIAL_STONES);
        inventory.put(WHEAT, InitialState.STORAGE_INITIAL_WHEAT);
        inventory.put(FISH, InitialState.STORAGE_INITIAL_FISH);
        inventory.put(PIG, InitialState.STORAGE_INITIAL_PIG);
        inventory.put(DONKEY, InitialState.STORAGE_INITIAL_DONKEY);
        inventory.put(MEAT, InitialState.STORAGE_INITIAL_MEAT);
        inventory.put(BREAD, InitialState.STORAGE_INITIAL_BREAD);
        inventory.put(WATER, InitialState.STORAGE_INITIAL_WATER);
        inventory.put(COAL, InitialState.STORAGE_INITIAL_COAL);
        inventory.put(IRON, InitialState.STORAGE_INITIAL_IRON);
        inventory.put(IRON_BAR, InitialState.STORAGE_INITIAL_IRON_BAR);
        inventory.put(COIN, InitialState.STORAGE_INITIAL_COIN);

        inventory.put(FORESTER, InitialState.STORAGE_INITIAL_FORESTER);
        inventory.put(WOODCUTTER_WORKER, InitialState.STORAGE_INITIAL_WOODCUTTER_WORKER);
        inventory.put(STONEMASON, InitialState.STORAGE_INITIAL_STONEMASON);
        inventory.put(FARMER, InitialState.STORAGE_INITIAL_FARMER);
        inventory.put(CARPENTER, InitialState.STORAGE_INITIAL_SAWMILL_WORKER);
        inventory.put(WELL_WORKER, InitialState.STORAGE_INITIAL_WELL_WORKER);
        inventory.put(MILLER, InitialState.STORAGE_INITIAL_MILLER);
        inventory.put(BAKER, InitialState.STORAGE_INITIAL_BAKER);
        inventory.put(STOREHOUSE_WORKER, InitialState.STORAGE_INITIAL_STORAGE_WORKER);
        inventory.put(FISHERMAN, InitialState.STORAGE_INITIAL_FISHERMAN);
        inventory.put(MINER, InitialState.STORAGE_INITIAL_MINER);
        inventory.put(IRON_FOUNDER, InitialState.STORAGE_INITIAL_IRON_FOUNDER);
        inventory.put(BREWER, InitialState.STORAGE_INITIAL_BREWER);
        inventory.put(MINTER, InitialState.STORAGE_INITIAL_MINTER);
        inventory.put(ARMORER, InitialState.STORAGE_INITIAL_ARMORER);
        inventory.put(PIG_BREEDER, InitialState.STORAGE_INITIAL_PIG_BREEDER);
        inventory.put(BUTCHER, InitialState.STORAGE_INITIAL_BUTCHER);
        inventory.put(GEOLOGIST, InitialState.STORAGE_INITIAL_GEOLOGIST);
        inventory.put(DONKEY_BREEDER, InitialState.STORAGE_INITIAL_DONKEY_BREEDER);
        inventory.put(SCOUT, InitialState.STORAGE_INITIAL_SCOUT);
        inventory.put(HUNTER, InitialState.STORAGE_INITIAL_HUNTER);
        inventory.put(METALWORKER, InitialState.STORAGE_INITIAL_METALWORKER);
        inventory.put(BUILDER, InitialState.STORAGE_INITIAL_BUILDER);
        inventory.put(SHIPWRIGHT, InitialState.STORAGE_INITIAL_SHIPWRIGHT);
    }

    @Override
    public void tearDown() throws InvalidUserActionException {
        throw new InvalidUserActionException("Can't tear down headquarter");
    }

    @Override
    public void capture(Player player) throws InvalidUserActionException {
        super.tearDown();
    }

    @Override
    public boolean isMilitaryBuilding() {
        return true;
    }

    @Override
    public boolean isHeadquarter() {
        return true;
    }

    public void setReservedSoldiers(Rank rank, int amount) {
        var reservedAmountBefore = wantedReservedSoldiers.get(rank);

        if (amount == reservedAmountBefore) {
            return;
        }

        var actualReservedBefore = Math.min(wantedReservedSoldiers.get(rank), hostedSoldiersByRank.get(rank).size());
        var actualReservedAfter = Math.min(amount, hostedSoldiersByRank.get(rank).size());

        wantedReservedSoldiers.put(rank, amount);

        if (actualReservedAfter != actualReservedBefore) {
            player.reportChangedInventory(this);
        }

        player.reportChangedReserveAmount(this);
    }

    public int getReservedSoldiers(Rank rank) {
        return wantedReservedSoldiers.getOrDefault(rank, 0);
    }

    @Override
    public Soldier retrieveHostedSoldier(Soldier soldier) {
        super.retrieveHostedSoldier(soldier);
        hostedSoldiersByRank.get(soldier.getRank()).remove(soldier);

        // TODO: is it safe to remove?
        map.placeWorkerFromStepTime(soldier, this);

        return soldier;
    }

    @Override
    public Soldier retrieveHostedSoldierForDefense() {
        var soldier = super.retrieveHostedSoldierForDefense();

        if (!hostedSoldiersByRank.containsKey(soldier.getRank())) {
            throw new InvalidGameLogicException("Can't retrieve soldier that's not here: " + soldier);
        }

        hostedSoldiersByRank.get(soldier.getRank()).remove(soldier);
        map.placeWorkerFromStepTime(soldier, this);

        return soldier;
    }

    /**
     * Returns whether a material exists in the inventory. Only soldiers that are not reserved are part of the inventory.
     * @param material The material
     * @return Whether the material exists in the inventory.
     */
    @Override
    public boolean isInStock(Material material) {
        if (material.isSoldier()) {
            return getAmount(material) > 0;
        }

        return super.isInStock(material);
    }

    @Override
    public Worker retrieveWorker(Material workerType, Building building) {
        if (workerType.isSoldier()) {
            var rank = workerType.toRank();

            var soldier = hostedSoldiersByRank.get(rank).iterator().next();
            hostedSoldiersByRank.get(rank).remove(soldier);
            hostedSoldiers.remove(soldier);

            return soldier;
        }

        return super.retrieveWorker(workerType, building);
    }

    public Map<Rank, Integer> getActualReservedSoldiers() {
        var actualReserved = new HashMap<Rank, Integer>();

        for (var rank : Rank.values()) {
            actualReserved.put(rank, Math.min(hostedSoldiersByRank.get(rank).size(), wantedReservedSoldiers.get(rank)));
        }

        return actualReserved;
    }

    @Override
    void draftMilitary() {
        int swords = inventory.getOrDefault(SWORD, 0);
        int shields = inventory.getOrDefault(SHIELD, 0);
        int beer = inventory.getOrDefault(BEER, 0);
        int privatesToDraft = GameUtils.min(swords, shields, beer);

        var draftedSoldiers = createSoldiersForStorehouse(PRIVATE_RANK, privatesToDraft, player, this);
        hostedSoldiersByRank.get(PRIVATE_RANK).addAll(draftedSoldiers);
        hostedSoldiers.addAll(draftedSoldiers);
        inventory.merge(BEER, -privatesToDraft, Integer::sum);
        inventory.merge(SHIELD, -privatesToDraft, Integer::sum);
        inventory.merge(SWORD, -privatesToDraft, Integer::sum);

        map.getStatisticsManager().soldiersDrafted(player, map.getTime(), privatesToDraft);
    }

    public boolean hasAny(Material... materials) {
        return Arrays.stream(materials).anyMatch(material -> getAmount(material) > 0);
    }

    /**
     * Returns the amount of the given material in the headquarters' inventory. For soldiers, the amount returned does
     * not include soldiers reserved for the headquarters' defense.
     * @param material The material
     * @return The amount of the material stored in the headquarters' inventory
     */
    @Override
    public int getAmount(Material material) {
        if (material.isSoldier()) {
            var rank = material.toRank();
            return Math.max(
                    hostedSoldiersByRank.get(rank).size() - wantedReservedSoldiers.get(rank),
                    0);
        }

        return super.getAmount(material);
    }

    @Override
    public Soldier retrieveSoldierFromInventory(Rank rank) {
        var soldier = hostedSoldiersByRank.get(rank).iterator().next();

        hostedSoldiersByRank.get(rank).remove(soldier);
        hostedSoldiers.remove(soldier);

        return soldier;
    }

    @Override
    public Soldier retrieveSoldierFromInventory(Material material) {
        return retrieveSoldierFromInventory(material.toRank());
    }

    @Override
    int getNumberOfSoldiersAvailableForRemoteDefense() {
        return Arrays.stream(Rank.values())
                .mapToInt(rank -> getAmount(rank.toMaterial()))
                .sum();
    }

    @Override
    public int getNumberOfSoldiersAvailableForNewAttack() {
        var amount = 0;

        for (var rank : Rank.values()) {
            amount += getAmount(rank.toMaterial());
        }

        return amount;
    }

    @Override
    public String toString() {
        return "Headquarter %s (%s)".formatted(position, state);
    }
}
