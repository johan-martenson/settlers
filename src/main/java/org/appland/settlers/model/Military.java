package org.appland.settlers.model;

import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Military.Rank.CORPORAL_RANK;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.OFFICER_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.appland.settlers.model.Military.State.ATTACKING;
import static org.appland.settlers.model.Military.State.DEFENDING;
import static org.appland.settlers.model.Military.State.DEPLOYED;
import static org.appland.settlers.model.Military.State.IN_STORAGE;
import static org.appland.settlers.model.Military.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Military.State.STANDBY_WAITING_DEFEND;
import static org.appland.settlers.model.Military.State.WAITING_FOR_DEFENDING_OPPONENT;
import static org.appland.settlers.model.Military.State.WALKING_APART_TO_ATTACK;
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
        CORPORAL_RANK,
        OFFICER_RANK,
        GENERAL_RANK
    }

    protected enum State {
        WALKING_TO_TARGET,
        DEPLOYED,
        RETURNING_TO_STORAGE,
        IN_STORAGE,
        WALKING_TO_ATTACK,
        WALKING_TO_TAKE_OVER_BUILDING,
        WAITING_FOR_DEFENDING_OPPONENT,
        RESERVED_BY_DEFENDING_OPPONENT,
        WALKING_TO_FIGHT_TO_DEFEND,
        DEFENDING,
        ATTACKING,
        WALKING_HOME_AFTER_FIGHT,
        STANDBY_WAITING_DEFEND,
        WALKING_APART_TO_DEFEND,
        WALKING_APART_TO_ATTACK,
        WALKING_TO_FIXED_POINT_AFTER_ATTACK,
        WALKING_TO_FIXED_POINT_AFTER_DEFENSE,
        DEAD
    }

    private static final int PRIVATE_FIGHT_DURATION  = 100;
    private static final int SERGEANT_FIGHT_DURATION = 200;
    private static final int GENERAL_FIGHT_DURATION  = 300;

    private static final int PRIVATE_HEALTH  = 20;
    private static final int CORPORAL_HEALTH = 70;
    private static final int SERGEANT_HEALTH = 220;
    private static final int OFFICER_HEALTH  = 670;
    private static final int GENERAL_HEALTH  = 2020;

    private Building  buildingToAttack;
    private Military  opponent;
    private Rank      rank;
    private State     state;
    private int       health;

    public Military(Player player, Rank rank, GameMap map) {
        super(player, map);

        this.rank = rank;

        health = getHealthForRank(rank);

        state = WALKING_TO_TARGET;
    }

    public Rank getRank() {
        return rank;
    }

    void promote() {
        switch (rank) {
        case PRIVATE_RANK:
            rank = CORPORAL_RANK;
            break;
        case CORPORAL_RANK:
            rank = SERGEANT_RANK;
            break;
        case SERGEANT_RANK:
            rank = OFFICER_RANK;
            break;
        case OFFICER_RANK:
            rank = GENERAL_RANK;
            break;
        default:
        }
    }

    @Override
    public String toString() {
        return rank.name() + " " + state + " at " + getPosition();
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
                } else {

                    /* Return home or to storage */
                    returnAfterAttackIsOver();
                }

            /* Leave if the building is destroyed */
            } else if (!buildingToAttack.isReady()) {

                /* Return home or to storage */
                returnAfterAttackIsOver();

            /* Try to take the place as primary attacker */
            } else if (buildingToAttack.getPrimaryAttacker() == null) {

                /* Become the primary attacker */
                buildingToAttack.setPrimaryAttacker(this);

                /* Walk to the flag */
                state = WALKING_TO_ATTACK;

                setOffroadTarget(buildingToAttack.getFlag().getPosition());
            }

        } else if (state == STANDBY_WAITING_DEFEND) {

            /* Go home if there are no more attackers */
            if (defendedBuilding.getAttackers().isEmpty()) {

                /* Return home or to storage */
                returnAfterAttackIsOver();

            /* Look for an attacker at the flag if this is the militaries own building */
            } else if (getHome().equals(defendedBuilding)) {

                /* Get the attacker at the flag */
                Military attackerAtFlag = defendedBuilding.getPrimaryAttacker();

                /* Keep waiting if there is no primary attacker */
                if (attackerAtFlag == null) {
                    return;
                }

                /* Keep waiting if the primary attacker is not at the flag */
                if (!attackerAtFlag.getPosition().equals(getHome().getFlag().getPosition())) {
                    return;
                }

                /* Fight the attacker */

                /* Remember the opponent */
                opponent = attackerAtFlag;

                /* Tell the attacker to take position for the fight */
                opponent.prepareForFight(this);

                /* Walk apart from the attacker before starting the fight */
                state = State.WALKING_APART_TO_DEFEND;

                /* Walk half a point away */
                walkHalfWayOffroadTo(getPosition().right());

            /* Fight the next attacker if this is a remote defender and there are
               attackers waiting */
            } else if (!defendedBuilding.getWaitingAttackers().isEmpty()) {

                /* Pick the next waiting attacker */
                opponent = defendedBuilding.pickWaitingAttacker();

                /* Notify the attacker so it doesn't move */
                opponent.reserveForFight();

                /* Walk to the attacker */
                setOffroadTarget(opponent.getPosition());
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {

        if (state == WALKING_TO_TARGET) {

            /* Get the building at the position */
            Building building = map.getBuildingAtPoint(getPosition());

            /* Deploy military in building */
            enterBuilding(building);

            /* The building may have sent us back immediately, otherwise become
               deployed
            */
            if (state == WALKING_TO_TARGET) {
                state = State.DEPLOYED;
            }
        } else if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());

            enterBuilding(storage);

            storage.putCargo(new Cargo(rankToMaterial(rank), map));

            state = IN_STORAGE;
        } else if (state == WALKING_TO_ATTACK) {

            /* Main attacker */
            if (getPosition().equals(buildingToAttack.getFlag().getPosition())) {

                /* Take over the building directly if it can not protect itself */
                if (buildingToAttack.isDefenseLess()) {

                    /* Walk to capture the building */
                    state = WALKING_TO_TAKE_OVER_BUILDING;

                    setOffroadTarget(buildingToAttack.getPosition(), buildingToAttack.getFlag().getPosition());

                /* Notify the building about the attacker and start waiting for an opponent */
                } else {

                    /* Register as an attacker and start waiting for an opponent */
                    buildingToAttack.registerAttacker(this);

                    state = WAITING_FOR_DEFENDING_OPPONENT;
                }

            /* Not main attacker */
            } else {

                /* Register as an attacker and start waiting for an opponent */
                buildingToAttack.registerAttacker(this);

                state = WAITING_FOR_DEFENDING_OPPONENT;
            }

        /* Capture the building */
        } else if (state == WALKING_TO_TAKE_OVER_BUILDING) {

            if (buildingToAttack.isReady()) {

                Player previousOwner = buildingToAttack.getPlayer();

                /* Capture the building */
                buildingToAttack.capture(getPlayer());

                /* Report the takeover */
                previousOwner.reportBuildingLost(buildingToAttack);
                getPlayer().reportBuildingCaptured(buildingToAttack);

                /* Return home if it's a headquarter */
                if (buildingToAttack instanceof Headquarter) {

                    /* Can't occupy headquarter so return home or to storage */
                    returnAfterAttackIsOver();

                } else {

                    /* Enter the building */
                    enterBuilding(buildingToAttack);

                    /* Extend the border */
                    map.updateBorder(buildingToAttack, BorderChangeCause.MILITARY_BUILDING_CAPTURED);

                    state = DEPLOYED;
                }

            /* Return home if the building has been destroyed */
            } else {

                /* Return home or to storage */
                returnAfterAttackIsOver();
            }
        } else if (state == WALKING_TO_FIGHT_TO_DEFEND) {

            /* Tell the attacker that the fight is about to start */
            opponent.prepareForFight(this);

            /* Walk apart from the attacker before starting the fight */
            state = State.WALKING_APART_TO_DEFEND;

            /* Walk half a point away */
            walkHalfWayOffroadTo(getPosition().right());

        } else if (state == WALKING_HOME_AFTER_FIGHT) {
            enterBuilding(getHome());

            state = DEPLOYED;
        } else if (state == State.WALKING_TO_FIXED_POINT_AFTER_ATTACK) {

            if (buildingToAttack.isReady()) {

                /* Take over the building if it's unprotected */
                if (buildingToAttack.isDefenseLess()) {
                    state = WALKING_TO_TAKE_OVER_BUILDING;

                    setOffroadTarget(buildingToAttack.getPosition());
                } else {

                    /* Notify the building about this attacker if it can still be defended */
                    buildingToAttack.registerAttacker(this);

                    state = WAITING_FOR_DEFENDING_OPPONENT;
                }
            } else {

                /* Return home if the other player destroyed the building */
                returnAfterAttackIsOver();
            }
        } else if (state == State.WALKING_TO_FIXED_POINT_AFTER_DEFENSE) {

            if (defendedBuilding.getAttackers().isEmpty()) {

                /* Go home or to storage if there are no more attackers */
                returnAfterAttackIsOver();
            } else if (getHome().equals(defendedBuilding)) {

                /* Stay by the flag if the military is defending its own
                   building and the attack isn't over */
                state = STANDBY_WAITING_DEFEND;

            } else if (defendedBuilding.getWaitingAttackers().isEmpty()) {

                /* All attackers are busy so stand by and wait to see if
                   there is a need to defend again */
                state = STANDBY_WAITING_DEFEND;

            } else {

                /* Fight the next waiting attacker */
                opponent = defendedBuilding.pickWaitingAttacker();

                /* Walk to fight the opponent */
                state = WALKING_TO_FIGHT_TO_DEFEND;

                setOffroadTarget(opponent.getPosition());
            }
        }
    }

    @Override
    protected void onEnterBuilding(Building building) throws Exception {
        if (building.isMilitaryBuilding()) {
            setHome(building);
        }

	if (state == State.WALKING_TO_TARGET             ||
	    state == State.WALKING_TO_TAKE_OVER_BUILDING ||
	    state == State.WALKING_HOME_AFTER_FIGHT      ||
            state == State.DEPLOYED) {
            building.deployMilitary(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = getPlayer().getClosestStorage(getPosition(), getHome());

        state = RETURNING_TO_STORAGE;

        if (storage != null) {
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            setOffroadTarget(storage.getPosition());
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

    void attack(Building building, Point meetingPoint) throws Exception {

        /* Save the building to attack */
        buildingToAttack = building;

        /* Set state to walking to attack */
        state = WALKING_TO_ATTACK;

        /* Walk to the attacked building */
        setOffroadTarget(meetingPoint);

        /* Become the primary attacker if the meeting point is the building's flag */
        if (buildingToAttack.getFlag().getPosition().equals(meetingPoint)) {
            buildingToAttack.setPrimaryAttacker(this);
        }
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

        /* Return to the fixed point */
        if (state == ATTACKING) {

            /* Change the state to walking back to the point after the fight */
            state = State.WALKING_TO_FIXED_POINT_AFTER_ATTACK;
        } else if (state == DEFENDING) {

            /* Change the state to walking back to the point after the fight */
            state = State.WALKING_TO_FIXED_POINT_AFTER_DEFENSE;
        }

        returnToFixedPoint();
    }

    private void lost() {

        /* Remove the military from the map (i.e. "die") */
        map.removeWorker(this);

        /* Remove this military from the list of the building's defenders */
        if (state == DEFENDING) {
            defendedBuilding.removeDefender(this);
        } else if (state == ATTACKING) {
            buildingToAttack.removeAttacker(this);
        }

        /* Remember that this military is dead */
        state = State.DEAD;
    }

    public boolean isFighting() {
        return state == State.DEFENDING || state == State.ATTACKING;
    }

    private int getHealthForRank(Rank rank) {
        switch (rank) {
        case PRIVATE_RANK:
            return PRIVATE_HEALTH;
        case CORPORAL_RANK:
            return CORPORAL_HEALTH;
        case SERGEANT_RANK:
            return SERGEANT_HEALTH;
        case OFFICER_RANK:
            return OFFICER_HEALTH;
        case GENERAL_RANK:
            return GENERAL_HEALTH;
        default:
            return 0;
        }
    }

    private void hit(Military military) {

        /* Decrease health */
        health--;

        /* Handle "death" and notify the winner */
        if (health == 0) {

            lost();
            opponent.won();
        }
    }

    void defendBuilding(Building building) throws Exception {

        defendedBuilding = building;

        /* Handle the attacker at the flag if this is the defender's own building */
        if (defendedBuilding.equals(getHome())) {

            /* Get the opponent */
            opponent = defendedBuilding.getPrimaryAttacker();

            /* Fight the attacker */
            state = WALKING_TO_FIGHT_TO_DEFEND;

            /* Walk to the flag */
            setOffroadTarget(getHome().getFlag().getPosition());

        } else {

            /* Register in the building's defense */
            building.registerDefender(this);

            /* Fight an attacker if there are attackers waiting for opponents */
            if (!building.getWaitingAttackers().isEmpty()) {

                /* Get a waiting attacker */
                opponent = building.pickWaitingAttacker();

                /* Fight the attacker */
                state = WALKING_TO_FIGHT_TO_DEFEND;

                setOffroadTarget(opponent.getPosition());
            } else {

                /* Just wait for an attacker to become free */
                state = STANDBY_WAITING_DEFEND;
            }
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        if (state == WALKING_TO_TARGET) {

            /* Return to the storage if the target building changed owner */
            if (!getTargetBuilding().getPlayer().equals(getPlayer())) {

                /* Set state to returning to storage */
                state = RETURNING_TO_STORAGE;

                returnToStorage();

            /* Return to the storage if the target building is destroyed */
            } else if (!getTargetBuilding().isReady()) {

                /* Set state to returning to storage */
                state = RETURNING_TO_STORAGE;

                returnToStorage();
            }
        }
    }

    @Override
    protected void onWalkedHalfWay() {

        /* Start the fight when the military is in the right position */
        if (state == State.WALKING_APART_TO_DEFEND) {
            state = DEFENDING;
        } else if (state == State.WALKING_APART_TO_ATTACK) {
            state = ATTACKING;
        }
    }

    private void prepareForFight(Military military) throws Exception {

        /* Remember the opponent */
        opponent = military;

        /* Walk half way to the next point to not stand on top of the defender */
        walkHalfWayOffroadTo(getPosition().left());

        state = WALKING_APART_TO_ATTACK;
    }

    private void reserveForFight() {

        /* A defender has decided to fight this attacker so wait for it instead
           of looking for a new fight */
        state = State.RESERVED_BY_DEFENDING_OPPONENT;
    }

    private void returnAfterAttackIsOver() throws Exception {

        /* Return home if there is a need for a military at home */
        if (getHome().needsMilitaryManning()  && getHome().getPlayer().equals(getPlayer())) {

            /* Promise to return home */
            getHome().promiseMilitary(this);

            /* Change state to walking home */
            state = WALKING_HOME_AFTER_FIGHT;

            /* Walk home */
            returnHomeOffroad();
        } else {

            /* Go to the storage if there is no space in the home */
            state = RETURNING_TO_STORAGE;

            returnToStorage();
        }
    }
}
