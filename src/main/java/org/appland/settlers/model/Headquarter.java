package org.appland.settlers.model;

import org.appland.settlers.policy.InitialState;

import java.util.Map;

import static org.appland.settlers.model.Material.ARMORER;
import static org.appland.settlers.model.Material.BAKER;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.BREWER;
import static org.appland.settlers.model.Material.BUTCHER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.DONKEY_BREEDER;
import static org.appland.settlers.model.Material.FARMER;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHERMAN;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.HUNTER;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.IRON_FOUNDER;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.METALWORKER;
import static org.appland.settlers.model.Material.MILLER;
import static org.appland.settlers.model.Material.MINER;
import static org.appland.settlers.model.Material.MINTER;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PIG_BREEDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SAWMILL_WORKER;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STONEMASON;
import static org.appland.settlers.model.Material.STORAGE_WORKER;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WELL_WORKER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Material.WOODCUTTER_WORKER;

@HouseSize(size = Size.LARGE)
@MilitaryBuilding(maxHostedMilitary = 0, defenceRadius = 9, attackRadius = 20, discoveryRadius = 13)
public class Headquarter extends Storehouse {

    public Headquarter(Player player) {
        super(player);

        setHeadquarterDefaultInventory(inventory);
        setConstructionReady();
    }

    @Override
    protected void setMap(GameMap map) throws Exception {
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
    void capture(Player player) throws Exception {

        /* Destroy the headquarter if it's captured */
        super.tearDown();
    }
}
