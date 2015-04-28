/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
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
    }
    
    private Cargo tryToStartDelivery() throws Exception {
        for (Material m : Material.values()) {
            for (Building b : map.getBuildingsWithinReach(ownStorage.getFlag())) {

                /* Don't deliver to itself */
                if (ownStorage.equals(b)) {
                    continue;
                }

                if (ownStorage.getAmount(PLANCK) <= TREE_CONSERVATION_LIMIT && 
                    !(b instanceof Sawmill)     &&
                    !(b instanceof ForesterHut) &&
                    !(b instanceof Woodcutter)) {
                    continue;
                }
                
                if (b.needsMaterial(m) && ownStorage.isInStock(m)) {
                    b.promiseDelivery(m);

                    Cargo cargo = ownStorage.retrieve(m);
                    cargo.setTarget(b);

                    return cargo;
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
}
