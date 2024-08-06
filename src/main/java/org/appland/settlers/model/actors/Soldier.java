package org.appland.settlers.model.actors;

import org.appland.settlers.model.BorderChangeCause;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.OffroadOption;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WorkerAction;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.*;
import static org.appland.settlers.model.actors.Soldier.State.*;

/**
 * @author johan
 *
 */
@Walker(speed = 10)
public class Soldier extends Worker {
    private static final Random random = new Random(1);
    private static final int TIME_FOR_HIT = 10;
    private static final int TIME_TO_DIE = 10;
    public int id = random.nextInt();

    private int countdown;

    public Soldier getOpponent() {
        return opponent;
    }

    enum FightState {
        HITTING,
        GETTING_HIT,
        JUMPING_BACK,
        STANDING_ASIDE,
        DYING,
        WAITING
    }

    public enum Rank {
        PRIVATE_RANK,
        SERGEANT_RANK,
        OFFICER_RANK,
        PRIVATE_FIRST_CLASS_RANK,
        GENERAL_RANK;

        public static Rank intToRank(int soldierInt) {
            return switch (soldierInt) {
                case 0, 1 -> PRIVATE_RANK;
                case 2, 3 -> PRIVATE_FIRST_CLASS_RANK;
                case 4, 5, 6 -> SERGEANT_RANK;
                case 7, 8 -> OFFICER_RANK;
                case 9, 10 -> GENERAL_RANK;
                default -> throw new InvalidGameLogicException(String.format("Can't translate %d to rank", soldierInt));
            };
        }

        public String getSimpleName() {
            return switch (this) {
                case PRIVATE_RANK -> "Private";
                case PRIVATE_FIRST_CLASS_RANK -> "Private first class";
                case SERGEANT_RANK -> "Sergeant";
                case OFFICER_RANK -> "Officer";
                case GENERAL_RANK -> "General";
            };
        }

        public Material toMaterial() {
            return switch (this) {
                case PRIVATE_RANK -> PRIVATE;
                case PRIVATE_FIRST_CLASS_RANK -> PRIVATE_FIRST_CLASS;
                case SERGEANT_RANK -> SERGEANT;
                case OFFICER_RANK -> OFFICER;
                case GENERAL_RANK -> GENERAL;
            };
        }

        public int toInt() {
            return switch (this) {
                case PRIVATE_RANK -> 0;
                case PRIVATE_FIRST_CLASS_RANK -> 1;
                case SERGEANT_RANK -> 2;
                case OFFICER_RANK -> 3;
                case GENERAL_RANK -> 4;
            };
        }
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
        WAITING_TO_DEFEND,
        WALKING_APART_TO_DEFEND,
        WALKING_APART_TO_ATTACK,
        WALKING_TO_FIXED_POINT_AFTER_ATTACK,
        WALKING_TO_FIXED_POINT_AFTER_DEFENSE,
        WALKING_TO_DEFEND,
        DEAD
    }

    private static final int PRIVATE_HEALTH  = 3;
    private static final int PRIVATE_FIRST_CLASS_HEALTH = 4;
    private static final int SERGEANT_HEALTH = 5;
    private static final int OFFICER_HEALTH  = 6;
    private static final int GENERAL_HEALTH  = 7;

    private Soldier    opponent;
    private Rank       rank;
    private State      state;
    private int        health;
    private Building   buildingToAttack;
    private Building   buildingToDefend;
    private FightState fightState;

    public Soldier(Player player, Rank rank, GameMap map) {
        super(player, map);

        this.rank = rank;

        health = getHealthForRank(rank);

        state = WALKING_TO_TARGET;
    }

    public Rank getRank() {
        return rank;
    }

    public void promote() {
        rank = switch (rank) {
            case PRIVATE_RANK -> PRIVATE_FIRST_CLASS_RANK;
            case PRIVATE_FIRST_CLASS_RANK -> SERGEANT_RANK;
            case SERGEANT_RANK -> OFFICER_RANK;
            case OFFICER_RANK -> GENERAL_RANK;
            default -> rank;
        };
    }

    @Override
    public String toString() {
        return isExactlyAtPoint() ?
                String.format("%s soldier %s", rank.getSimpleName(), getPosition()) :
                String.format("%s soldier %s - %s", rank.getSimpleName(), getPosition(), getNextPoint());
    }

    @Override
    public void stepTime() throws InvalidUserActionException {
        super.stepTime();

        if (state == ATTACKING || state == DEFENDING) {
            switch (fightState) {
                case GETTING_HIT -> {
                    if (countdown == 0) {
                        health -= 1;

                        if (health == 0) {
                            fightState = FightState.DYING;

                            if (map.isFlagAtPoint(getPosition())) {
                                map.getFlagAtPoint(getPosition()).setNoFightAtFlag();
                            }

                            map.reportWorkerStartedAction(this, WorkerAction.DIE);

                            countdown = TIME_TO_DIE;
                        } else {
                            fightState = FightState.WAITING;
                        }
                    } else {
                        countdown -= 1;
                    }
                }

                case WAITING -> {
                    if (opponent.isDead()) {
                        opponent = null;
                        fightState = null;

                        /* Return to the fixed point */
                        state = switch (state) {
                            case ATTACKING -> State.WALKING_TO_FIXED_POINT_AFTER_ATTACK;
                            case DEFENDING -> State.WALKING_TO_FIXED_POINT_AFTER_DEFENSE;
                            default -> state;
                        };

                        returnToFixedPoint();
                    } else if (opponent != null && opponent.isReadyToFight()) {

                        // Somehow needed to prevent always getting defenders to make first hit
                        random.nextBoolean();

                        if (random.nextBoolean()) {
                            fightState = FightState.HITTING;

                            opponent.setBeingHit();

                            countdown = TIME_FOR_HIT;

                            map.reportWorkerStartedAction(this, WorkerAction.HIT);
                        }
                    }
                }

                case HITTING, JUMPING_BACK, STANDING_ASIDE -> {
                    if (countdown == 0) {
                        fightState = FightState.WAITING;
                    } else {
                        countdown -= 1;
                    }
                }

                case DYING -> {
                    if (countdown == 0) {

                        /* Remove the military from the map (i.e. "die") */
                        map.removeWorker(this);

                        /* Remove this military from the list of the building's defenders */
                        switch (state) {
                            case DEFENDING -> buildingToDefend.removeDefender(this);
                            case ATTACKING -> buildingToAttack.removeAttacker(this);
                        }

                        /* Remember that this military is dead */
                        state = State.DEAD;

                        fightState = null;

                        setDead();
                    } else {
                        countdown -= 1;
                    }
                }
            }
        }
    }

    @Override
    protected void onIdle() {
        switch (state) {
            case WAITING_FOR_DEFENDING_OPPONENT -> {
                if (buildingToAttack.getPlayer().equals(getPlayer())) {
                    if (buildingToAttack.needsMilitaryManning()) {

                        /* Enter the building if it has already been taken over and needs additional manning */
                        buildingToAttack.promiseSoldier(this);

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
            }
            case WAITING_TO_DEFEND -> {

                /* Go home if there are no more attackers */
                if (buildingToDefend.getAttackers().isEmpty()) {

                    /* Return home or to storage */
                    returnAfterAttackIsOver();

                    /* Look for an attacker at the flag if this is the soldiers own building */
                } else if (getHome().equals(buildingToDefend)) {

                    /* Get the attacker at the flag */
                    Soldier attackerAtFlag = buildingToDefend.getPrimaryAttacker();

                    /* Keep waiting if there is no primary attacker */
                    if (attackerAtFlag == null) {
                        return;
                    }

                    /* Keep waiting if the primary attacker is not at the flag */
                    if (!attackerAtFlag.getPosition().equals(getHome().getFlag().getPosition()) || attackerAtFlag.isTraveling()) {
                        return;
                    }

                    /* Keep waiting if the primary attacker is not waiting for a new fight */
                    if (attackerAtFlag.isReservedForFight() || attackerAtFlag.isFighting()) {
                        return;
                    }

                    /* Fight the attacker */

                    /* Remember the opponent */
                    opponent = attackerAtFlag;

                    /* Tell the attacker to take position for the fight */
                    opponent.prepareForFight(this);

                    /* Tell the attacked building that this defender is not waiting anymore */
                    buildingToDefend.removeWaitingDefender(this);

                    /* Walk apart from the attacker before starting the fight */
                    state = State.WALKING_APART_TO_DEFEND;

                    /* Walk half a point away */
                    walkHalfWayOffroadTo(getPosition().right(), OffroadOption.CAN_END_ON_STONE);

                    /* Fight the next attacker if this is a remote defender and there are attackers waiting */
                } else if (!buildingToDefend.getWaitingAttackers().isEmpty()) {

                    /* Pick the next waiting attacker */
                    opponent = buildingToDefend.pickWaitingAttacker();

                    state = WALKING_TO_FIGHT_TO_DEFEND;

                    /* Notify the attacker so it doesn't move */
                    opponent.reserveForFight(this);

                    /* Walk to the attacker */
                    setOffroadTarget(opponent.getPosition());
                }
            }
            case WALKING_TO_FIGHT_TO_DEFEND -> {
                if (!opponent.isTraveling()) {

                    /* Tell the attacker that the fight is about to start */
                    opponent.prepareForFight(this);

                    /* Walk apart from the attacker before starting the fight */
                    state = State.WALKING_APART_TO_DEFEND;

                    /* Walk half a point away */
                    walkHalfWayOffroadTo(getPosition().right(), OffroadOption.CAN_END_ON_STONE);
                }
            }
        }
    }

    private boolean isReservedForFight() {
        return state == RESERVED_BY_DEFENDING_OPPONENT;
    }

    @Override
    protected void onArrival() throws InvalidUserActionException {
        switch (state) {
            case WALKING_TO_TARGET -> {
                /* Get the building at the position */
                Building building = map.getBuildingAtPoint(getPosition());

                /* Deploy military in building */
                enterBuilding(building);

                /* The building may have sent us back immediately, otherwise become deployed */
                if (state == WALKING_TO_TARGET) {
                    state = DEPLOYED;
                }
            }
            case RETURNING_TO_STORAGE -> {
                Building storage = map.getBuildingAtPoint(getPosition());

                enterBuilding(storage);

                storage.putCargo(new Cargo(rankToMaterial(rank), map));

                map.removeWorker(this);

                state = IN_STORAGE;
            }
            case WALKING_TO_ATTACK -> {
                buildingToAttack.registerWaitingAttacker(this);

                /* Main attacker */
                if (getPosition().equals(buildingToAttack.getFlag().getPosition())) {

                    /* Take over the building directly if it can not protect itself */
                    if (buildingToAttack.isDefenseLess()) {

                        /* Walk to capture the building */
                        state = WALKING_TO_TAKE_OVER_BUILDING;

                        setOffroadTarget(buildingToAttack.getPosition(), buildingToAttack.getFlag().getPosition());

                        /* Notify the building about the attacker and start waiting for an opponent */
                    } else {
                        state = WAITING_FOR_DEFENDING_OPPONENT;
                    }

                    /* Not main attacker */
                } else {
                    state = WAITING_FOR_DEFENDING_OPPONENT;
                }
            }

            /* Capture the building */
            case WALKING_TO_TAKE_OVER_BUILDING -> {
                if (buildingToAttack.isReady()) {

                    Player previousOwner = buildingToAttack.getPlayer();

                    /* Capture the building */
                    buildingToAttack.capture(getPlayer());

                    /* Report the takeover */
                    previousOwner.reportBuildingLost(buildingToAttack);
                    getPlayer().reportBuildingCaptured(buildingToAttack);

                    /* Return home if it's a headquarters */
                    if (buildingToAttack.isHeadquarter()) {

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
            }
            case WALKING_HOME_AFTER_FIGHT -> {
                enterBuilding(getHome());

                state = DEPLOYED;
            }
            case WALKING_TO_FIXED_POINT_AFTER_ATTACK -> {
                if (buildingToAttack.isReady()) {

                    /* Take over the building if it's unprotected */
                    if (buildingToAttack.isDefenseLess()) {
                        state = WALKING_TO_TAKE_OVER_BUILDING;

                        setOffroadTarget(buildingToAttack.getPosition());
                    } else {
                        state = WAITING_FOR_DEFENDING_OPPONENT;

                        buildingToAttack.registerWaitingAttacker(this);
                    }
                } else {

                    /* Return home if the other player destroyed the building */
                    returnAfterAttackIsOver();
                }
            }
            case WALKING_TO_FIXED_POINT_AFTER_DEFENSE -> {
                if (buildingToDefend.getAttackers().isEmpty()) {

                    /* Go home or to storage if there are no more attackers */
                    returnAfterAttackIsOver();

                    buildingToDefend.removeDefender(this);
                } else if (getHome().equals(buildingToDefend)) {

                    /* Stay by the flag if the military is defending its own building and the attack isn't over */
                    state = WAITING_TO_DEFEND;

                } else if (buildingToDefend.getWaitingAttackers().isEmpty()) {

                    /* All attackers are busy so stand by and wait to see if there is a need to defend again */
                    state = WAITING_TO_DEFEND;

                } else {

                    /* Fight the next waiting attacker */
                    opponent = buildingToDefend.pickWaitingAttacker();

                    opponent.reserveForFight(this);

                    /* Walk to fight the opponent */
                    state = WALKING_TO_FIGHT_TO_DEFEND;

                    setOffroadTarget(opponent.getPosition());
                }
            }
            case WALKING_TO_DEFEND -> state = WAITING_TO_DEFEND;
        }
    }

    @Override
    protected void onEnterBuilding(Building building) {
        if (state == WALKING_TO_TARGET             ||
            state == WALKING_TO_TAKE_OVER_BUILDING ||
            state == WALKING_HOME_AFTER_FIGHT      ||
            state == DEPLOYED) {
            building.deploySoldier(this);
        }
    }

    @Override
    protected void onReturnToStorage() {
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
        return switch (rank) {
            case PRIVATE_RANK -> PRIVATE;
            case PRIVATE_FIRST_CLASS_RANK -> PRIVATE_FIRST_CLASS;
            case SERGEANT_RANK -> SERGEANT;
            case OFFICER_RANK -> OFFICER;
            case GENERAL_RANK -> GENERAL;
        };
    }

    public void attack(Building building) {

        /* Save the building to attack */
        buildingToAttack = building;

        /* Set state to walking to attack */
        state = WALKING_TO_ATTACK;

        /* Tell the building that we are an attacker */
        building.registerAttacker(this);

        /* Walk close to the fighting and wait for a defender to be free */
        state = WALKING_TO_ATTACK;

        Set<Point> taken = Stream.concat(
                        buildingToAttack.getAttackers().stream(),
                        buildingToAttack.getDefenders().stream()
                )
                .filter(worker -> !Objects.equals(worker, this))
                .filter(Worker::isSoldier)
                .filter(soldier -> !soldier.isInsideBuilding())
                .map(soldier -> soldier.isTraveling() ? soldier.getTarget() : soldier.getPosition())
                .collect(Collectors.toSet());

        var candidates = GameUtils.getHexagonAreaAroundPoint(
                        buildingToAttack.getFlag().getPosition(),
                        6,
                        map).stream()
                .filter(point -> !taken.contains(point))
                .filter(point -> !map.isBuildingAtPoint(point))
                .filter(point -> !map.isStoneAtPoint(point))
                .filter(point -> map.getVegetationUpLeft(point).canWalkOn() || map.getVegetationDownLeft(point).canWalkOn())
                .filter(point -> map.getVegetationUpRight(point).canWalkOn() || map.getVegetationDownRight(point).canWalkOn())
                .filter(point -> map.findWayOffroad(
                        getPosition(),
                        point,
                        null
                ) != null)
                .sorted(
                        (point0, point1) -> {
                            var dist0 = GameUtils.distanceInGameSteps(point0, buildingToAttack.getFlag().getPosition());
                            var dist1 = GameUtils.distanceInGameSteps(point1, buildingToAttack.getFlag().getPosition());

                            if (dist0 == dist1) {
                                return 0;
                            }

                            var diff = dist0 - dist1;

                            return diff / Math.abs(diff);
                        }
                )
                .toList();

        Point point = candidates.getFirst();

        /* Walk to the attacked building */
        setOffroadTarget(point, getHome().getFlag().getPosition());

        /* Become the primary attacker if the meeting point is the building's flag */
        if (buildingToAttack.getFlag().getPosition().equals(point)) {
            buildingToAttack.setPrimaryAttacker(this);
        }
    }

    public boolean isFighting() {
        return state == DEFENDING || state == ATTACKING;
    }

    private int getHealthForRank(Rank rank) {
        return switch (rank) {
            case PRIVATE_RANK -> PRIVATE_HEALTH;
            case PRIVATE_FIRST_CLASS_RANK -> PRIVATE_FIRST_CLASS_HEALTH;
            case SERGEANT_RANK -> SERGEANT_HEALTH;
            case OFFICER_RANK -> OFFICER_HEALTH;
            case GENERAL_RANK -> GENERAL_HEALTH;
        };
    }

    public void defendOwnBuilding(Building building) {
        buildingToDefend = building;

        /* Register in the building's defense */
        building.registerDefender(this);

        /* Get the opponent */
        opponent = buildingToDefend.getPrimaryAttacker();

        buildingToDefend.removeWaitingAttacker(opponent);

        opponent.reserveForFight(this);

        /* Fight the attacker */
        state = WALKING_TO_FIGHT_TO_DEFEND;

        /* Walk to the flag */
        setOffroadTarget(buildingToDefend.getFlag().getPosition());
    }

    public void defendOtherBuilding(Building building) {
        buildingToDefend = building;

        /* Register in the building's defense */
        building.registerDefender(this);

        getHome().retrieveHostedSoldier(this);

        /* Fight an attacker if there are attackers waiting for opponents */
        if (!building.getWaitingAttackers().isEmpty()) {

            /* Get a waiting attacker */
            opponent = building.pickWaitingAttacker();
            building.removeWaitingAttacker(opponent);

            opponent.reserveForFight(this);

            /* Fight the attacker */
            state = WALKING_TO_FIGHT_TO_DEFEND;

            setOffroadTarget(opponent.getPosition());

            /* Walk to a point close to the building and wait for an attacker to fight */
        } else {

            /* Walk close to the fighting and wait for an attacker to be free */
            state = WALKING_TO_DEFEND;

            Set<Point> taken = Stream.concat(
                            buildingToDefend.getDefenders().stream(),
                            buildingToDefend.getAttackers().stream())
                    .filter(Worker::isSoldier)
                    .filter(soldier -> !soldier.isInsideBuilding())
                    .map(soldier -> soldier.isTraveling() ? soldier.getTarget() : soldier.getPosition())
                    .collect(Collectors.toSet());

                var candidates = GameUtils.getHexagonAreaAroundPoint(
                                buildingToDefend.getFlag().getPosition(),
                                6,
                                map).stream()
                        .filter(point -> !taken.contains(point))
                        .filter(point -> !map.isBuildingAtPoint(point))
                        .filter(point -> !map.isStoneAtPoint(point))
                        .filter(point -> map.getVegetationUpLeft(point).canWalkOn() || map.getVegetationDownLeft(point).canWalkOn())
                        .filter(point -> map.getVegetationUpRight(point).canWalkOn() || map.getVegetationDownRight(point).canWalkOn())
                        .filter(point -> map.findWayOffroad(
                                getPosition(),
                                point,
                                null
                        ) != null)
                        .sorted(
                                (point0, point1) -> {
                                    var dist0 = GameUtils.distanceInGameSteps(point0, buildingToDefend.getFlag().getPosition());
                                    var dist1 = GameUtils.distanceInGameSteps(point1, buildingToDefend.getFlag().getPosition());

                                    if (dist0 == dist1) {
                                        return 0;
                                    }

                                    var diff = dist0 - dist1;

                                    return diff / Math.abs(diff);
                                }
                        )
                        .toList();

            Point point = candidates.getFirst();

            setOffroadTarget(point, getHome().getFlag().getPosition());
        }
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {
        switch (state) {
            case WALKING_TO_TARGET -> {

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
            default -> { }
        }
    }

    @Override
    protected void onWalkedHalfWay() {

        /* Start the fight when the military is in the right position */
        if (state == State.WALKING_APART_TO_DEFEND) {
            state = DEFENDING;

            fightState = FightState.WAITING;
        } else if (state == WALKING_APART_TO_ATTACK) {
            state = ATTACKING;

            fightState = FightState.WAITING;
        }
    }

    private void setBeingHit() {
        int likelihoodToHit = switch (opponent.getRank()) {
            case GENERAL_RANK -> 5;
            case OFFICER_RANK -> 4;
            case SERGEANT_RANK -> 3;
            case PRIVATE_FIRST_CLASS_RANK -> 2;
            case PRIVATE_RANK -> 1;
        };

        int maybeHit = random.nextInt(10); // 0-9 almost uniformly distributed

        if (maybeHit < likelihoodToHit) {
            fightState = FightState.GETTING_HIT;
        } else {
            int next = random.nextInt(2);

            fightState = switch (next) {
                case 0 -> FightState.JUMPING_BACK;
                case 1 -> FightState.STANDING_ASIDE;
                default -> throw new InvalidGameLogicException("Failed to set a reasonable fight state!");
            };
        }

        map.reportWorkerStartedAction(this, switch (fightState) {
            case GETTING_HIT -> WorkerAction.GET_HIT;
            case JUMPING_BACK -> WorkerAction.JUMP_BACK;
            case STANDING_ASIDE -> WorkerAction.STAND_ASIDE;
            default -> throw new InvalidGameLogicException("Found unexpected fight state");
        });

        countdown = TIME_FOR_HIT;
    }

    private boolean isReadyToFight() {
        return (state == ATTACKING || state == DEFENDING) && fightState == FightState.WAITING;
    }

    private void prepareForFight(Soldier soldier) {

        /* Tell the building that this soldier is not waiting anymore as the fight is starting */
        if (state == ATTACKING || state == WAITING_FOR_DEFENDING_OPPONENT) {
            buildingToAttack.removeWaitingAttacker(this);
        } else if (state == DEFENDING || state == WAITING_TO_DEFEND) {
            buildingToDefend.removeWaitingDefender(this);
        }

        /* Remember the opponent */
        opponent = soldier;

        /* Walk halfway to the next point to not stand on top of the defender */
        walkHalfWayOffroadTo(getPosition().left(), OffroadOption.CAN_END_ON_STONE);

        state = WALKING_APART_TO_ATTACK;

        /* Tell the flag (if any) that a fight is taking place */
        if (map.isFlagAtPoint(getPosition())) {
            var flag = map.getFlagAtPoint(getPosition());

            flag.setFightIsTakingPlace();
        }
    }

    private void reserveForFight(Soldier soldier) {

        /* A defender has decided to fight this attacker so wait for it instead of looking for a new fight */
        state = State.RESERVED_BY_DEFENDING_OPPONENT;

        opponent = soldier;
    }

    private void returnAfterAttackIsOver() {

        /* Return home if there is a need for a military at home */
        if (getHome().needsMilitaryManning() && getHome().getPlayer().equals(getPlayer())) {

            /* Promise to return home */
            getHome().promiseSoldier(this);

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

    @Override
    public boolean isSoldier() {
        return true;
    }

    public boolean isAttacking() {
        return state == ATTACKING;
    }

    public boolean isDefending() {
        return state == DEFENDING;
    }

    public boolean isJumpingBack() {
        return fightState == FightState.JUMPING_BACK;
    }

    public boolean isStandingAside() {
        return fightState == FightState.STANDING_ASIDE;
    }

    public boolean isGettingHit() {
        return fightState == FightState.GETTING_HIT;
    }

    public boolean isHitting() {
        return fightState == FightState.HITTING;
    }

    public boolean isDying() {
        return fightState == FightState.DYING;
    }

    public int getHealth() {
        return health;
    }

    public boolean isWalkingApartToFight() {
        return state == WALKING_APART_TO_DEFEND || state == WALKING_APART_TO_ATTACK;
    }

    public boolean isWalkingBackToFixedPointAfterFight() {
        return state == WALKING_TO_FIXED_POINT_AFTER_ATTACK || state == WALKING_TO_FIXED_POINT_AFTER_DEFENSE;
    }
}
