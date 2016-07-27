/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PLANCK;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class StorageWorker extends Worker {

    private final static int RESTING_TIME = 19;
    private final static int TREE_CONSERVATION_LIMIT = 10;
    
    private final Countdown countdown;
    private final Map<Class<? extends Building>, Integer> assignedFood;
    private final Map<Class<? extends Building>, Integer> assignedCoal;

    private State state;
    private Storage ownStorage;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        DELIVERING_CARGO_TO_FLAG,
        GOING_BACK_TO_HOUSE,
        RETURNING_TO_STORAGE
    }

    public StorageWorker(Player player, GameMap m) {
        super(player, m);

        state = State.WALKING_TO_TARGET;
        
        countdown = new Countdown();
        
        countdown.countFrom(RESTING_TIME);

        /* Set the initial assignments of food to zero */
        assignedFood = new HashMap<>();

        assignedFood.put(GoldMine.class, 0);
        assignedFood.put(IronMine.class, 0);
        assignedFood.put(CoalMine.class, 0);
        assignedFood.put(GraniteMine.class, 0);

        /* Set the initial assignments of coal to zero */
        assignedCoal = new HashMap<>();

        assignedCoal.put(IronSmelter.class, 0);
        assignedCoal.put(Mint.class, 0);
        assignedCoal.put(Armory.class, 0);
    }

    private Cargo tryToStartDelivery() throws Exception {

        for (Material material : getPlayer().getTransportPriorityList()) {

            /* Don't try to deliver materials that are not in stock */
            if (!ownStorage.isInStock(material)) {
                continue;
            }

            /* Iterate over all buildings, instead of just the ones that can be
               reached from the headquarter
            
               This will perform the quick tests first and only perform the
               expensive test if the quick ones pass
            */
            for (Building b : getPlayer().getBuildings()) {

                /* Don't deliver to itself */
                if (ownStorage.equals(b)) {
                    continue;
                }

                /* Make sure plancks are only used for planck production if
                   the limit is critically low */
                if (material == PLANCK && 
                    ownStorage.getAmount(PLANCK) <= TREE_CONSERVATION_LIMIT && 
                    !(b instanceof Sawmill)     &&
                    !(b instanceof ForesterHut) &&
                    !(b instanceof Woodcutter)) {
                    continue;
                }

                /* Check if the building needs the material */
                if (b.needsMaterial(material)) {

                    /* Check that the building type is within its assigned quota */
                    if (isWithinQuota(b, material) || (resetAllocationIfNeeded(material) && isWithinQuota(b, material))) {

                        /* Filter out buildings that cannot be reached from the storage */
                        if (map.findWayWithExistingRoads(getHome().getPosition(), b.getPosition()) != null) {
                            b.promiseDelivery(material);

                            Cargo cargo = ownStorage.retrieve(material);
                            cargo.setTarget(b);

                            /* Track allocation */
                            trackAllocation(b, material);

                            return cargo;
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Storage) {
            setHome(b);
            
            ownStorage = (Storage)b;
        }
    
        state = State.RESTING_IN_HOUSE;
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == State.RESTING_IN_HOUSE) {
            if (countdown.reachedZero()) {
                Cargo cargo = tryToStartDelivery();
                    
                if (cargo != null) {
                    setCargo(cargo);

                    setTarget(getHome().getFlag().getPosition());

                    state = State.DELIVERING_CARGO_TO_FLAG;
                }
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == State.DELIVERING_CARGO_TO_FLAG) {
            Flag f = getHome().getFlag();
                
            f.putCargo(getCargo());
                
            setCargo(null);
                
            returnHome();
                
            state = State.GOING_BACK_TO_HOUSE;
        } else if (state == State.GOING_BACK_TO_HOUSE) {
            enterBuilding(getHome());
            
            state = State.RESTING_IN_HOUSE;
            
            countdown.countFrom(RESTING_TIME);
        } else if (state == State.RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());
        
            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition(), getHome());
    
        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            
            setTarget(storage.getPosition());
        } else {
            for (Building b : getPlayer().getBuildings()) {
                if (b instanceof Storage && !b.equals(getHome())) {
                    state = State.RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }

    private boolean resetAllocationIfNeeded(Material m) {
        
        if (isFood(m)) {

        /* Reset count if all building types have reached their quota */
            Set<Building> reachableBuildings = map.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, GoldMine.class, m)    || 
                  overQuota(GoldMine.class))                                  &&
                (!needyConsumerExists(reachableBuildings, IronMine.class, m)    || 
                  overQuota(IronMine.class))                                  &&
                (!needyConsumerExists(reachableBuildings, CoalMine.class, m)    || 
                  overQuota(CoalMine.class))                                  &&
                (!needyConsumerExists(reachableBuildings, GraniteMine.class, m) || 
                  overQuota(GraniteMine.class))) {
                assignedFood.put(GoldMine.class, 0);
                assignedFood.put(IronMine.class, 0);
                assignedFood.put(CoalMine.class, 0);
                assignedFood.put(GraniteMine.class, 0);

                return true;
            }
        } else if (m == COAL) {

            /* Reset count if all building types have reached their quota */
            Set<Building> reachableBuildings = map.getBuildingsWithinReach(getHome().getFlag());

            if ((!needyConsumerExists(reachableBuildings, IronSmelter.class, m)  ||
                  overQuota(IronSmelter.class))                                    &&
                (!needyConsumerExists(reachableBuildings, Mint.class, m)         ||
                  overQuota(Mint.class))                                           &&
                (!needyConsumerExists(reachableBuildings, Armory.class, m)       ||
                  overQuota(Armory.class))) {
                assignedCoal.put(IronSmelter.class, 0);
                assignedCoal.put(Mint.class, 0);
                assignedCoal.put(Armory.class, 0);

                return true;
            }
        }

        return false;
    }

    private void trackAllocation(Building b, Material m) {

        if (isFood(m)) {

            int amount = assignedFood.get(b.getClass());
            assignedFood.put(b.getClass(), amount + 1);

        } else if (m == COAL) {
            int amount = assignedCoal.get(b.getClass());
            assignedCoal.put(b.getClass(), amount + 1);
        }
    }

    private boolean isFood(Material material) {
        return material == FISH || material == BREAD || material == MEAT;
    }

    private boolean isWithinQuota(Building b, Material m) {

        /* Handle quota for food */
        if (isFood(m)) {
            int quota = getPlayer().getFoodQuota(b.getClass());

            return assignedFood.get(b.getClass()) < quota;
        }

        /* Handle quota for coal */
        if (m == COAL) {
            int quota = getPlayer().getCoalQuota(b.getClass());

            return assignedCoal.get(b.getClass()) < quota;
        }

        /* All other materials are without quota */
        return true;
    }

    private boolean overQuota(Class<? extends Building> aClass) {

        /* Handle food quota for mines */
        if (aClass.equals(GoldMine.class) ||
            aClass.equals(IronMine.class) ||
            aClass.equals(CoalMine.class) ||
            aClass.equals(GraniteMine.class)) {
            return assignedFood.get(aClass) >= getPlayer().getFoodQuota(aClass);
        }

        /* Handle coal quota for coal consumers */
        if (aClass.equals(IronSmelter.class) ||
            aClass.equals(Mint.class)        ||
            aClass.equals(Armory.class)) {
            return assignedCoal.get(aClass) >= getPlayer().getCoalQuota(aClass);
        }

        /* All other buildlings have no quota */
        return false;
    }

    private boolean needyConsumerExists(Collection<Building> buildings, 
            Class<? extends Building> aClass,
            Material material) {

        for (Building b : buildings) {
            if (b.getClass().equals(aClass) && 
                b.ready()                   && 
                b.needsMaterial(material)) {
                return true;
            }
        }

        return false;
    }
}
