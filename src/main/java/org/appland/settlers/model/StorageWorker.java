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
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Material.BREAD;
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
    }
    
    private Cargo tryToStartDelivery() throws Exception {
        for (Material m : Material.values()) {
            for (Building b : map.getBuildingsWithinReach(ownStorage.getFlag())) {

                /* Don't deliver to itself */
                if (ownStorage.equals(b)) {
                    continue;
                }

                /* Make sure plancks are only used for planck production if
                   the limit is critically low */
                if (m == PLANCK && 
                    ownStorage.getAmount(PLANCK) <= TREE_CONSERVATION_LIMIT && 
                    !(b instanceof Sawmill)     &&
                    !(b instanceof ForesterHut) &&
                    !(b instanceof Woodcutter)) {
                    continue;
                }

                if (b.needsMaterial(m) && ownStorage.isInStock(m)) {

                    /* Check that the building type is within its assigned quota */
                    if (isWithinQuota(b, m)) {
                        b.promiseDelivery(m);

                        Cargo cargo = ownStorage.retrieve(m);
                        cargo.setTarget(b);

                        /* Track allocation */
                        trackAllocation(b, m);

                        return cargo;
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
                    try {
                        setCargo(cargo);

                        setTarget(getHome().getFlag().getPosition());

                        state = State.DELIVERING_CARGO_TO_FLAG;
                        } catch (InvalidRouteException ex) {
                            Logger.getLogger(StorageWorker.class.getName()).log(Level.SEVERE, null, ex);
                        }
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

    private void trackAllocation(Building b, Material m) {

        if (isFood(m)) {

            int amount = assignedFood.get(b.getClass());
            assignedFood.put(b.getClass(), amount + 1);

            /* Reset count if all building types have reached their quota */
            if (!isWithinQuota(b, m)) {

                Set<Building> reachableBuildings = map.getBuildingsWithinReach(getHome().getFlag());

                if ((!readyConsumerExists(reachableBuildings, GoldMine.class)    || 
                      overQuota(GoldMine.class))                                  &&
                    (!readyConsumerExists(reachableBuildings, IronMine.class)    || 
                      overQuota(IronMine.class))                                  &&
                    (!readyConsumerExists(reachableBuildings, CoalMine.class)    || 
                      overQuota(CoalMine.class))                                  &&
                    (!readyConsumerExists(reachableBuildings, GraniteMine.class) || 
                      overQuota(GraniteMine.class))) {
                    assignedFood.put(GoldMine.class, 0);
                    assignedFood.put(IronMine.class, 0);
                    assignedFood.put(CoalMine.class, 0);
                    assignedFood.put(GraniteMine.class, 0);
                }
            }
        }
    }

    private boolean isFood(Material m) {
        return m == FISH || m == BREAD || m == MEAT;
    }

    private boolean isWithinQuota(Building b, Material m) {

        /* Handle quota for food */
        if (isFood(m)) {
            int quota = getPlayer().getFoodQuota(b.getClass());

            return assignedFood.get(b.getClass()) < quota;
        }

        /* All other materials are without quota */
        return true;
    }

    private boolean overQuota(Class<? extends Building> aClass) {

        /* Only handle food quota for mines */
        if (aClass.equals(GoldMine.class) ||
            aClass.equals(IronMine.class) ||
            aClass.equals(CoalMine.class) ||
            aClass.equals(GraniteMine.class)) {
            return assignedFood.get(aClass) >= getPlayer().getFoodQuota(aClass);
        }

        /* All other buildlings have no quota */
        return false;
    }

    private boolean readyConsumerExists(Collection<Building> buildings, Class<? extends Building> aClass) {

        for (Building b : buildings) {
            if (b.getClass().equals(aClass) && b.ready()) {
                return true;
            }
        }

        return false;
    }
}
