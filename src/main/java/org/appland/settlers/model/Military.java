/**
 *
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.appland.settlers.model.Military.State.IN_STORAGE;
import static org.appland.settlers.model.Military.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Military.State.WALKING_TO_TARGET;

/**
 * @author johan
 *
 */
@Walker(speed = 10)
public class Military extends Worker {

    public enum Rank {

        PRIVATE_RANK,
        SERGEANT_RANK,
        GENERAL_RANK;
    }
    
    enum State {
        WALKING_TO_TARGET,
        DEPLOYED,
        RETURNING_TO_STORAGE,
        IN_STORAGE
    }

    private Rank rank;
    private State state;
    
    public Military(Rank r, GameMap map) {
        super(map);

        rank = r;
        
        state = WALKING_TO_TARGET;
    }

    public Rank getRank() {
        return rank;
    }

    void promote() {
        switch (rank) {
        case PRIVATE_RANK:
            rank = SERGEANT_RANK;
            break;
        case SERGEANT_RANK:
            rank = GENERAL_RANK;
            break;
        default:
        }
    }

    @Override
    public String toString() {
        return rank.name();
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());
            
            enterBuilding(storage);
            
            storage.putCargo(new Cargo(rankToMaterial(rank), map));
            
            state = IN_STORAGE;
        }
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b.isMilitaryBuilding()) {
            setHome(b);
        }
    }
    
    protected void onReturnToStorage() throws Exception {
        Building stg = map.getClosestStorage(getPosition(), getHome());
        
        state = RETURNING_TO_STORAGE;
        
        if (stg != null) {
            setTarget(stg.getPosition());
        } else {
            stg = getClosestStorageOffroad();
            
            setOffroadTarget(stg.getPosition());
        }
    }

    private Material rankToMaterial(Rank rank) {

        switch (rank) {
        case PRIVATE_RANK:
            return PRIVATE;
        case SERGEANT_RANK:
            return SERGEANT;
        case GENERAL_RANK:
            return GENERAL;
        default:
            return null;
        }
    }

    private Building getClosestStorageOffroad() {
        int distance = Integer.MAX_VALUE;
        Building storage = null;
        
        for (Building b : map.getBuildings()) {
            if (b instanceof Storage) {
                int currentDistance = map.findWayOffroad(getPosition(), b.getPosition(), null).size();
                
                if (currentDistance < distance) {
                    storage = b;
                    distance = currentDistance;
                }
            }
        }
        
        return storage;
    }
}
