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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;

@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedSoldiers = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
public class Headquarter extends Storehouse {
    private final Map<Soldier.Rank, Integer> wantedReservedSoldiers = new HashMap<>();
    private final Map<Soldier.Rank, Integer> actualReservedSoldiers;

    public Headquarter(Player player) {
        super(player);

        setHeadquarterDefaultInventory(inventory);
        setConstructionReady();
        actualReservedSoldiers = new HashMap<>();

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
        return String.format("Headquarter with inventory %s", inventory);
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
        if (isInStock(PRIVATE)) {
            Soldier defender = (Soldier) retrieveWorker(PRIVATE);
            getMap().placeWorker(defender, this);
            defender.setHome(this);
            defender.setPosition(getPosition());

            return defender;
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
        int wantedPrivatesInReserve = wantedReservedSoldiers.get(PRIVATE_RANK);
        int privatesToEnterReserve = Math.min(wantedPrivatesInReserve - privatesInReserve, privatesToDraft);
        int privatesToEnterInventory = privatesToDraft - privatesToEnterReserve;

        actualReservedSoldiers.put(PRIVATE_RANK, privatesToEnterReserve);
        inventory.merge(PRIVATE, privatesToEnterInventory, Integer::sum);

        inventory.merge(BEER, -privatesToDraft, Integer::sum);
        inventory.merge(SHIELD, -privatesToDraft, Integer::sum);
        inventory.merge(SWORD, -privatesToDraft, Integer::sum);
    }

    public boolean hasAny(Material... materials) {
        return Arrays.stream(materials).anyMatch(material -> getAmount(material) > 0);
    }
}
