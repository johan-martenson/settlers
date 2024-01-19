package org.appland.settlers.model;

import org.appland.settlers.policy.InitialState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.*;

@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedMilitary = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
public class Headquarter extends Storehouse {

    private final Map<Military.Rank, Integer> reservedSoldiers;

    public Headquarter(Player player) {
        super(player);

        reservedSoldiers = new HashMap<>();

        setHeadquarterDefaultInventory(inventory);
        setConstructionReady();
    }

    @Override
    void stepTime() {
        super.stepTime();

        List<Military> hostedSoldiers = getHostedMilitary();

        long amountHostedPrivates = hostedSoldiers.stream().filter(soldier -> soldier.getRank() == Military.Rank.PRIVATE_RANK).count();

        boolean lackReservedSoldiers = this.reservedSoldiers.getOrDefault(Military.Rank.PRIVATE_RANK, 0) > (int) amountHostedPrivates;

        if (lackReservedSoldiers && getAmount(PRIVATE) > 0) {
            deployMilitary(retrieveSoldierFromInventory(PRIVATE));
        }
    }

    @Override
    public void putCargo(Cargo cargo) {
        if (cargo.getMaterial().isMilitary()) {
            Military.Rank rank = cargo.getMaterial().toRank();

            if (reservedSoldiers.getOrDefault(rank, 0) > getHostedSoldiersWithRank(rank)) {
                deployMilitary(new Military(getPlayer(), rank, getMap()));

                return;
            }
        }

        super.putCargo(cargo);
    }

    @Override
    boolean spaceAvailableToHostSoldier(Military soldier) {
        Military.Rank rank = soldier.getRank();

        return reservedSoldiers.getOrDefault(rank, 0) > getHostedSoldiersWithRank(rank);
    }

    @Override
    protected void setMap(GameMap map) {
        super.setMap(map);

        Worker storageWorker = new StorageWorker(getPlayer(), map);
        getMap().placeWorker(storageWorker, this);

        storageWorker.enterBuilding(this);

        assignWorker(storageWorker);
    }

    private void setHeadquarterDefaultInventory(Map<Material, Integer> inventory) {
        inventory.put(SHIELD, InitialState.STORAGE_INITIAL_SHIELDS);
        inventory.put(SWORD, InitialState.STORAGE_INITIAL_SWORDS);
        inventory.put(BEER, InitialState.STORAGE_INITIAL_BEER);
        inventory.put(GOLD, InitialState.STORAGE_INITIAL_GOLD);

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
        inventory.put(STORAGE_WORKER, InitialState.STORAGE_INITIAL_STORAGE_WORKER);
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
        return "Headquarter with inventory " + inventory;
    }

    @Override
    public void tearDown() throws InvalidUserActionException {
        throw new InvalidUserActionException("Can not tear down headquarter");
    }

    @Override
    void capture(Player player) throws InvalidUserActionException {

        /* Destroy the headquarters if it's captured */
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

    public void setReservedSoldiers(Military.Rank rank, int reservedAmount) {
        reservedSoldiers.put(rank, reservedAmount);

        int hostedAmount = getHostedSoldiersWithRank(rank);
        int amountInInventory = getAmount(rank.toMaterial());
        int reserveGap = reservedAmount - hostedAmount;

        // Increase the amount of hosted soldiers to close the gap as much as possible
        if (reservedAmount > hostedAmount && amountInInventory > 0) {
            for (int i = 0; i < Math.min(reserveGap, amountInInventory); i++) {
                deployMilitary(retrieveSoldierFromInventory(rank));
            }

            getPlayer().reportChangedInventory(this);

        // Decrease the amount of hosted soldiers if needed
        } else if (hostedAmount > reservedAmount) {
            for (int i = 0; i < hostedAmount - reservedAmount; i++) {
                retrieveHostedSoldierWithRank(rank);

                putCargo(new Cargo(rank.toMaterial(), getMap()));
            }

            getPlayer().reportChangedInventory(this);
        } else {
            getPlayer().reportChangedReserveAmount(this);
        }
    }

    public int getReservedSoldiers(Military.Rank rank) {
        return reservedSoldiers.getOrDefault(rank, 0);
    }

    @Override
    public int getNumberOfHostedMilitary() {
        return inventory.getOrDefault(PRIVATE, 0) +
                inventory.getOrDefault(PRIVATE_FIRST_CLASS, 0) +
                inventory.getOrDefault(SERGEANT, 0) +
                inventory.getOrDefault(OFFICER, 0) +
                inventory.getOrDefault(GENERAL, 0);
    }

    @Override
    public List<Military> getHostedMilitary() {
        List<Military> hostedMilitary = new ArrayList<>();

        for (Military.Rank rank : Military.Rank.values()) {
            for (int i = 0; i < inventory.getOrDefault(rank.toMaterial(), 0); i++) {
                var soldier = new Military(getPlayer(), rank, getMap());

                soldier.setPosition(getPosition());
                soldier.setHome(this);

                hostedMilitary.add(soldier);
            }
        }

        return hostedMilitary;
    }

    @Override
    Military retrieveHostedSoldier(Military soldier) {
        var amount = inventory.getOrDefault(soldier.getRank().toMaterial(), 0);

        inventory.put(soldier.getRank().toMaterial(), amount - 1);

        soldier.setHome(this);

        getMap().placeWorkerFromStepTime(soldier, this);

        soldier.setPosition(getPosition());

        return soldier;
    }

    @Override
    Military retrieveHostedSoldier() {
        if (isInStock(PRIVATE)) {
            Military defender = (Military) retrieveWorker(PRIVATE);

            getMap().placeWorker(defender, this);

            defender.setHome(this);

            defender.setPosition(getPosition());

            return defender;
        }

        throw new InvalidGameLogicException("Can't retrieve soldier!");
    }
}
