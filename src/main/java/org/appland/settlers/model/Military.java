/**
 *
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.appland.settlers.model.Military.State.ATTACKING;
import static org.appland.settlers.model.Military.State.DEPLOYED;
import static org.appland.settlers.model.Military.State.DEFENDING;
import static org.appland.settlers.model.Military.State.IN_STORAGE;
import static org.appland.settlers.model.Military.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Military.State.WAITING_FOR_DEFENDING_OPPONENT;
import static org.appland.settlers.model.Military.State.WALKING_HOME_AFTER_FIGHT;
import static org.appland.settlers.model.Military.State.WALKING_TO_ATTACK;
import static org.appland.settlers.model.Military.State.WALKING_TO_FIGHT;
import static org.appland.settlers.model.Military.State.WALKING_TO_TAKE_OVER_BUILDING;
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
        IN_STORAGE,
        WALKING_TO_ATTACK,
        WALKING_TO_TAKE_OVER_BUILDING,
        WAITING_FOR_DEFENDING_OPPONENT,
        WALKING_TO_FIGHT,
        DEFENDING,
        ATTACKING,
        WALKING_HOME_AFTER_FIGHT
    }

    private static final int PRIVATE_FIGHT_DURATION  = 100;
    private static final int SERGEANT_FIGHT_DURATION = 200;
    private static final int GENERAL_FIGHT_DURATION  = 300;

    private static final int PRIVATE_HEALTH  = 20;
    private static final int SERGEANT_HEALTH = 70;
    private static final int GENERAL_HEALTH  = 130;

    private Building  buildingToAttack;
    private Military  opponent;
    private Rank      rank;
    private State     state;
    private int       health;
    
    public Military(Player player, Rank r, GameMap map) {
        super(player, map);

        rank = r;

        health = getHealthForRank(rank);

        state = WALKING_TO_TARGET;
    }

    public Military(Rank r, GameMap map) {
        this(null, r, map);
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
    protected void onIdle() throws InvalidRouteException {

        if (state == DEFENDING || state == ATTACKING) {
            opponent.hit(this);
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());
            
            enterBuilding(storage);
            
            storage.putCargo(new Cargo(rankToMaterial(rank), map));

            state = IN_STORAGE;
        } else if (state == WALKING_TO_ATTACK) {
            if (buildingToAttack.getHostedMilitary() == 0) {
                state = WALKING_TO_TAKE_OVER_BUILDING;

                setTarget(buildingToAttack.getPosition());
            } else {
                buildingToAttack.getPlayer().sendDefense(buildingToAttack, this);

                state = WAITING_FOR_DEFENDING_OPPONENT;
            }
        } else if (state == WALKING_TO_TAKE_OVER_BUILDING) {
            if (buildingToAttack.ready()) {
                buildingToAttack.setPlayer(getPlayer());

                enterBuilding(buildingToAttack);

                map.updateBorder();

                state = DEPLOYED;
            } else {
                state = WALKING_HOME_AFTER_FIGHT;

                returnHomeOffroad();
            }
        } else if (state == WALKING_TO_FIGHT) {
            state = DEFENDING;
        } else if (state == WALKING_HOME_AFTER_FIGHT) {
            state = DEPLOYED;

            enterBuilding(getHome());

            getHome().deployMilitary(this);
        }
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b.isMilitaryBuilding()) {
            setHome(b);
        }
    }
    
    @Override
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

    void attack(Building buildingToAttack) {
        this.buildingToAttack = buildingToAttack;

        state = WALKING_TO_ATTACK;

        setOffroadTarget(buildingToAttack.getFlag().getPosition());
    }

    void fight(Military opponent) {
        this.opponent = opponent;

        state = WALKING_TO_FIGHT;

        setOffroadTarget(opponent.getPosition());
    }

    int getFightTime() {
        switch (getRank()) {
        case PRIVATE_RANK:
            return PRIVATE_FIGHT_DURATION;
        case SERGEANT_RANK:
            return SERGEANT_FIGHT_DURATION;
        case GENERAL_RANK:
            return GENERAL_FIGHT_DURATION;
        default:
            return -1;
        }
    }

    private void won() {
        if (state == ATTACKING) {
            if (buildingToAttack.ready()) {
                if (buildingToAttack.getHostedMilitary() == 0) {
                    state = WALKING_TO_TAKE_OVER_BUILDING;

                    setOffroadTarget(buildingToAttack.getPosition());
                } else {
                    buildingToAttack.getPlayer().sendDefense(buildingToAttack, this);

                    state = WAITING_FOR_DEFENDING_OPPONENT;
                }
            } else {
                state = WALKING_HOME_AFTER_FIGHT;

                returnHomeOffroad();
            }
        } else {
            state = WALKING_HOME_AFTER_FIGHT;

            returnHomeOffroad();
        }
    }

    private void lost() {
        map.removeWorker(this);

    /*            
            if (getRank() == PRIVATE_RANK) {
                opponent.won();

                map.removeWorker(this);
            } else if (getRank() == SERGEANT_RANK && opponent.getRank() == GENERAL_RANK) {
                opponent.won();
                    
                // should have removeWorker here, test and fix!
            } else {
                opponent.lost();

            }
*/
    }

    public boolean isFighting() {
        return state == DEFENDING || state == ATTACKING;
    }

    private int getHealthForRank(Rank rank) {
        switch (rank) {
        case PRIVATE_RANK:
            return PRIVATE_HEALTH;
        case SERGEANT_RANK:
            return SERGEANT_HEALTH;
        case GENERAL_RANK:
            return GENERAL_HEALTH;
        default:
            return 0;
        }
    }

    private void hit(Military m) {
        if (state == WAITING_FOR_DEFENDING_OPPONENT) {
            state = ATTACKING;
        }

        opponent = m;

        health--;

        if (health == 0) {
            opponent.won();
            lost();
        }
    }
}
