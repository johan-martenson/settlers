package org.appland.settlers.model.buildings;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.policy.InitialState;
import org.appland.settlers.rest.resource.ResourceLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Map.entry;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;

@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedSoldiers = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
public class Headquarter extends Storehouse {
    private static final Map<Material, Integer> LOW_RESOURCES = Map.ofEntries(
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
            entry(SAWMILL_WORKER, 2),
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
            entry(SAWMILL_WORKER, 4),
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

    private static final Map<Material, Integer> HIGH_RESOURCES = Map.ofEntries(
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
            entry(SAWMILL_WORKER, 8),
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

    private final Map<Soldier.Rank, Integer> wantedReservedSoldiers = new HashMap<>();
    private final Map<Soldier.Rank, Integer> actualReservedSoldiers = new HashMap<>();

    public Headquarter(Player player) {
        super(player);

        setHeadquarterDefaultInventory(inventory);
        setConstructionReady();

        Arrays.stream(Soldier.Rank.values()).forEach(rank -> actualReservedSoldiers.put(rank, 0));
    }

    @Override
    public void stepTime() {
        super.stepTime();

        long amountHostedPrivates = getHostedSoldiers().stream()
                .filter(soldier -> soldier.getRank() == PRIVATE_RANK)
                .count();

        boolean lackReservedSoldiers = this.wantedReservedSoldiers.getOrDefault(PRIVATE_RANK, 0) > (int) amountHostedPrivates;

        if (lackReservedSoldiers && getAmount(PRIVATE) > 0) {
            deploySoldier(retrieveSoldierFromInventory(PRIVATE));
        }
    }

    private void putCargos(Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            putCargo(new Cargo(material, getMap()));
        }
    }

    @Override
    public void putCargo(Cargo cargo) {
        Material material = cargo.getMaterial();

        if (material.isMilitary()) {
            Soldier.Rank rank = material.toRank();
            int reservedSoldiers = actualReservedSoldiers.get(rank);

            if (wantedReservedSoldiers.getOrDefault(rank, 0) > reservedSoldiers) {
                actualReservedSoldiers.merge(rank, 1, Integer::sum);
                return;
            }
        }

        super.putCargo(cargo);
    }

    @Override
    public boolean isSpaceAvailableToHostSoldier(Soldier soldier) {
        Soldier.Rank rank = soldier.getRank();
        return wantedReservedSoldiers.getOrDefault(rank, 0) > getHostedSoldiersWithRank(rank);
    }

    @Override
    public void setMap(GameMap map) {
        super.setMap(map);

        Worker storageWorker = new StorehouseWorker(getPlayer(), map);
        getMap().placeWorker(storageWorker, this);
        storageWorker.enterBuilding(this);
        assignWorker(storageWorker);
    }

    public void setInitialResources(ResourceLevel resourceLevel) {
        switch (resourceLevel) {
            case LOW -> inventory.putAll(LOW_RESOURCES);
            case MEDIUM -> inventory.putAll(MEDIUM_RESOURCES);
            case HIGH -> inventory.putAll(HIGH_RESOURCES);
        }

        getMap().getStatisticsManager()
                .getGeneralStatistics(getPlayer())
                .workers()
                .report(getMap().getTime(), GameUtils.countWorkersInInventory(this));
    }

    private void setHeadquarterDefaultInventory(Map<Material, Integer> inventory) {
        inventory.put(SHIELD, InitialState.STORAGE_INITIAL_SHIELDS);
        inventory.put(SWORD, InitialState.STORAGE_INITIAL_SWORDS);
        inventory.put(BEER, InitialState.STORAGE_INITIAL_BEER);
        inventory.put(GOLD, InitialState.STORAGE_INITIAL_GOLD);

        // TODO: add default inventory for officer and private first class
        inventory.put(PRIVATE, InitialState.STORAGE_INITIAL_PRIVATE);
        inventory.put(SERGEANT, InitialState.STORAGE_INITIAL_SERGEANT);
        inventory.put(GENERAL, InitialState.STORAGE_INITIAL_GENERAL);

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
        inventory.put(SAWMILL_WORKER, InitialState.STORAGE_INITIAL_SAWMILL_WORKER);
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
    public String toString() {
        return format("Headquarter with inventory %s", mapToString(inventory));
    }

    private <K> String mapToString(Map<K, Integer> map) {
        StringBuilder s = new StringBuilder("{");

        var first = true;

        for (var entry : map.entrySet()) {
            if (entry.getValue() != 0) {
                if (first) {
                    first = false;
                    s.append(format("%s=%s", entry.getKey(), entry.getValue()));
                } else {
                    s.append(format(", %s=%s", entry.getKey(), entry.getValue()));
                }
            }
        }

        return s.toString();
    }

    @Override
    public void tearDown() throws InvalidUserActionException {
        throw new InvalidUserActionException("Can not tear down headquarter");
    }

    @Override
    public void capture(Player player) throws InvalidUserActionException {

        // Destroy the headquarters if it's captured
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

    public void setReservedSoldiers(Soldier.Rank rank, int reservedAmount) {
        wantedReservedSoldiers.put(rank, reservedAmount);

        int amountInReserve = actualReservedSoldiers.get(rank);
        int amountInInventory = getAmount(rank.toMaterial());
        int reserveGap = reservedAmount - amountInReserve;

        if (reserveGap > 0) {
            var addToReserve = Math.min(reserveGap, amountInInventory);

            actualReservedSoldiers.merge(rank, addToReserve, Integer::sum);
            retrieve(rank.toMaterial(), addToReserve);

            getPlayer().reportChangedInventory(this);
        } else if (amountInReserve > reservedAmount) {
            var excessSoldiers = amountInReserve - reservedAmount;
            actualReservedSoldiers.put(rank, reservedAmount);

            putCargos(rank.toMaterial(), excessSoldiers);

            getPlayer().reportChangedInventory(this);
        } else {
            getPlayer().reportChangedReserveAmount(this);
        }
    }

    public int getReservedSoldiers(Soldier.Rank rank) {
        return wantedReservedSoldiers.getOrDefault(rank, 0);
    }

    @Override
    public int getNumberOfHostedSoldiers() {
        return Arrays.stream(Soldier.Rank.values())
                .mapToInt(rank -> inventory.getOrDefault(rank.toMaterial(), 0) +
                        wantedReservedSoldiers.getOrDefault(rank, 0))
                .sum();
    }

    @Override
    public List<Soldier> getHostedSoldiers() {
        List<Soldier> hostedSoldiers = new ArrayList<>();

        for (Soldier.Rank rank : Soldier.Rank.values()) {
            for (int i = 0; i < inventory.getOrDefault(rank.toMaterial(), 0); i++) {
                var soldier = new Soldier(getPlayer(), rank, getMap());

                soldier.setPosition(getPosition());
                soldier.setHome(this);

                hostedSoldiers.add(soldier);
            }
        }

        return hostedSoldiers;
    }

    @Override
    public Soldier retrieveHostedSoldier(Soldier soldier) {
        inventory.merge(soldier.getRank().toMaterial(), -1, Integer::sum);
        soldier.setHome(this);
        getMap().placeWorkerFromStepTime(soldier, this);

        return soldier;
    }

    @Override
    public Soldier retrieveHostedSoldier() {
        // TODO: when defending, should pick soldier based on chosen defense strength.
        // This method is also used when upgrading the building and moving soldiers

        for (var rank : Soldier.Rank.values()) {
            var material = rank.toMaterial();

            if (isInStock(material)) {
                Soldier defender = (Soldier) retrieveWorker(material);
                getMap().placeWorker(defender, this);
                defender.setHome(this);
                defender.setPosition(getPosition());

                return defender;
            }
        }

        throw new InvalidGameLogicException("Can't retrieve soldier!");
    }

    public Map<Soldier.Rank, Integer> getActualReservedSoldiers() {
        return actualReservedSoldiers;
    }

    @Override
    void draftMilitary() {
        int swords = inventory.getOrDefault(SWORD, 0);
        int shields = inventory.getOrDefault(SHIELD, 0);
        int beer = inventory.getOrDefault(BEER, 0);

        int privatesToDraft = GameUtils.min(swords, shields, beer);
        int privatesInReserve = actualReservedSoldiers.get(PRIVATE_RANK);
        int wantedPrivatesInReserve = wantedReservedSoldiers.getOrDefault(PRIVATE_RANK, 0);
        int privatesToEnterReserve = Math.min(wantedPrivatesInReserve - privatesInReserve, privatesToDraft);
        int privatesToEnterInventory = privatesToDraft - privatesToEnterReserve;

        actualReservedSoldiers.put(PRIVATE_RANK, privatesToEnterReserve);
        inventory.merge(PRIVATE, privatesToEnterInventory, Integer::sum);
        inventory.merge(BEER, -privatesToDraft, Integer::sum);
        inventory.merge(SHIELD, -privatesToDraft, Integer::sum);
        inventory.merge(SWORD, -privatesToDraft, Integer::sum);

        getMap().getStatisticsManager().getGeneralStatistics(getPlayer()).soldiers().increase(
                getMap().getTime(),
                privatesToDraft
        );
    }

    public boolean hasAny(Material... materials) {
        return Arrays.stream(materials).anyMatch(material -> getAmount(material) > 0);
    }
}
