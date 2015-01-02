/**
 *
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import static org.appland.settlers.model.Military.State.STANDBY_WAITING_DEFEND;
import static org.appland.settlers.model.Military.State.WAITING_FOR_DEFENDING_OPPONENT;
import static org.appland.settlers.model.Military.State.WALKING_HOME_AFTER_FIGHT;
import static org.appland.settlers.model.Military.State.WALKING_TO_ATTACK;
import static org.appland.settlers.model.Military.State.WALKING_TO_FIGHT_TO_DEFEND;
import static org.appland.settlers.model.Military.State.WALKING_TO_TAKE_OVER_BUILDING;
import static org.appland.settlers.model.Military.State.WALKING_TO_TARGET;

/**
 * @author johan
 *
 */
@Walker(speed = 10)
public class Military extends Worker {
    private Building defendedBuilding;

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
        WALKING_TO_FIGHT_TO_DEFEND,
        DEFENDING,
        ATTACKING,
        WALKING_HOME_AFTER_FIGHT,
        WALKING_TO_BE_ON_STANDBY_TO_DEFEND,
        STANDBY_WAITING_DEFEND
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
    protected void onIdle() throws Exception {
        if (state == DEFENDING || state == ATTACKING) {

            /* Hit the opponent if the military is involved in a fight */
            opponent.hit(this);
        } else if (state == WAITING_FOR_DEFENDING_OPPONENT) {

            if (buildingToAttack.getPlayer().equals(getPlayer())) {
                if (buildingToAttack.needsMilitaryManning()) {

                    /* Enter the building if it has already been taken over and 
                       needs additional manning */
                    buildingToAttack.promiseMilitary(this);

                    state = WALKING_TO_TAKE_OVER_BUILDING;

                    setOffroadTarget(buildingToAttack.getPosition(), buildingToAttack.getFlag().getPosition());
                } else if (getHome().needsMilitaryManning()) {

                    /* Return home if the building is captured and doesn't need
                       an additional military */
                    getHome().promiseMilitary(this);

                    state = WALKING_HOME_AFTER_FIGHT;

                    returnHomeOffroad();
                } else {

                    /* Return to storage if this military is not needed in the 
                       captured building nor in the building it came from */
                    returnToStorage();
                }
            }
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

            if (buildingToAttack.getHostedMilitary() == 0 && buildingToAttack.getDefenders().isEmpty()) {

                /* Take over the building directly if can not protect itself */
                state = WALKING_TO_TAKE_OVER_BUILDING;

                setTarget(buildingToAttack.getPosition());
            } else {

                /* Register as an attacker and start waiting for an opponent */
                buildingToAttack.registerAttacker(this);

                state = WAITING_FOR_DEFENDING_OPPONENT;
            }
        } else if (state == WALKING_TO_TAKE_OVER_BUILDING) {
            if (buildingToAttack.ready()) {

                /* Change ownership of the building */
                buildingToAttack.setPlayer(getPlayer());

                enterBuilding(buildingToAttack);

                buildingToAttack.deployMilitary(this);

                map.updateBorder();

                state = DEPLOYED;
            } else {
                state = WALKING_HOME_AFTER_FIGHT;

                returnHomeOffroad();
            }
        } else if (state == WALKING_TO_FIGHT_TO_DEFEND) {
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
        Building stg = getPlayer().getClosestStorage(getPosition(), getHome());

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

        for (Building b : getPlayer().getBuildings()) {
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

    void attack(Building building, Point meetingPoint) {
        buildingToAttack = building;

        state = WALKING_TO_ATTACK;

        setOffroadTarget(meetingPoint);
    }

    void fight(Military opponent) {
        this.opponent = opponent;

        state = WALKING_TO_FIGHT_TO_DEFEND;

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

                /* Take over the building if it's unprotected */
                if (buildingToAttack.getHostedMilitary() == 0 && buildingToAttack.getDefenders().isEmpty()) {                    
                    state = WALKING_TO_TAKE_OVER_BUILDING;

                    setOffroadTarget(buildingToAttack.getPosition());
                } else {
                    
                    /* Notify the building about this attacker if it can still be defended */
                    buildingToAttack.registerAttacker(this);

                    state = WAITING_FOR_DEFENDING_OPPONENT;
                }
            } else {

                /* Return home if the other player destroyed the building */
                state = WALKING_HOME_AFTER_FIGHT;

                returnHomeOffroad();
            }
        } else if (state == DEFENDING) {

            if (defendedBuilding.getAttackers().isEmpty()) {
                
                /* Go home if there are no more attackers */
                state = WALKING_HOME_AFTER_FIGHT;

                returnHomeOffroad();
            } else if (defendedBuilding.getWaitingAttackers().isEmpty()) {

                /* Stand by and wait to see if there is a need to defend again */
                state = STANDBY_WAITING_DEFEND;
            } else {

                /* Fight the next waiting attacker */
                opponent = defendedBuilding.pickWaitingAttacker();

                /* Walk to fight the opponent */
                state = WALKING_TO_FIGHT_TO_DEFEND;

                setOffroadTarget(opponent.getPosition());
            }
        } else {
            state = WALKING_HOME_AFTER_FIGHT;

            returnHomeOffroad();
        }
    }

    private void lost() {

        /* Remove the military from the map (i.e. "die" */
        map.removeWorker(this);

        /* Remove this military from the list of the building's defenders */
        if (state == DEFENDING) {
            defendedBuilding.removeDefender(this);
        } else if (state == ATTACKING) {
            buildingToAttack.removeAttacker(this);
        }
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

        /* Decrease health */
        health--;

        /* Handle "death" and notify the winner */
        if (health == 0) {
            lost();
            opponent.won();
        }
    }

    static List<Point> getListOfPossibleMeetingPoints(Building buildingToAttack) {
        List<Point> meetupPoints = new ArrayList<>();

        meetupPoints.add(buildingToAttack.getFlag().getPosition());

        meetupPoints.addAll(Arrays.asList(buildingToAttack.getFlag().getPosition().getAdjacentPoints()));

        meetupPoints.remove(buildingToAttack.getPosition());

        return meetupPoints;
    }

    void defendBuilding(Building building) {
        defendedBuilding = building;

        /* Register in the building's defense */
        building.registerDefender(this);

        /* Fight an attacker if there are attackers waiting for opponents */
        if (!building.getWaitingAttackers().isEmpty()) {
            
            /* Get a waiting attacker */
            opponent = building.pickWaitingAttacker();

            /* Fight the attacker */
            state = WALKING_TO_FIGHT_TO_DEFEND;

            setOffroadTarget(opponent.getPosition());
        }
    }
}
