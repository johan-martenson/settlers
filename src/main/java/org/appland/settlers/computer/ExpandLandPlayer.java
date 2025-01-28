package org.appland.settlers.computer;

import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.utils.CumulativeDuration;
import org.appland.settlers.utils.Duration;
import org.appland.settlers.utils.Group;
import org.appland.settlers.utils.Stats;
import org.appland.settlers.utils.Variable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author johan
 */
public class ExpandLandPlayer implements ComputerPlayer {
    private static final int CLOSE_TO_ENEMY_WEIGHT = 2;
    private static final int GOOD_ENOUGH_SCORE = 10; // A bit under measured average

    private final Collection<Building> placedBarracks;
    private final Player               player;
    private final Set<Point>           impossibleSpots;
    private final Stats stats;
    private final Group collectEachTurnGroup;

    private GameMap     map;
    private Building    unfinishedBarracks;
    private Headquarter headquarter;
    private State       state;
    private boolean     newBuildings;
    private int         counter;
    private boolean     preferEnemyDirection;
    private boolean     waitUntilOccupied;

    private enum State {
        INITIAL_STATE,
        WAITING_FOR_CONSTRUCTION,
        READY_FOR_CONSTRUCTION,
        BUILDING_NOT_CONNECTED,
        WAITING_FOR_BUILDINGS_TO_GET_OCCUPIED
    }

    private static final int MAX_PERIOD = 1000;
    private static final int MAINTENANCE_PERIOD = 50;
    private static final int MAX_DISTANCE_FROM_BORDER = 3;
    private static final int MIN_DISTANCE_TO_EDGE = 3;
    private static final int THRESHOLD_FOR_EVACUATION = 6;
    private static final int ENEMY_CLOSE = 6;

    public ExpandLandPlayer(Player player, GameMap map) {
        this(player, map, new Stats());
    }

    public ExpandLandPlayer(Player player, GameMap map, Stats stats) {
        this.player = player;
        this.map = map;
        this.stats = stats;

        collectEachTurnGroup = stats.createVariableGroupIfAbsent("COLLECT_EACH_TURN");

        placedBarracks = new HashSet<>();

        /* Set the initial state */
        state = State.INITIAL_STATE;

        newBuildings = false;
        impossibleSpots = new HashSet<>();

        /* Set default configuration */
        preferEnemyDirection = false;
        waitUntilOccupied = false;
    }

    @Override
    public void turn() throws Exception {
        Duration duration = stats.measureOneShotDuration("ExpandLandPlayer.turn");

        if (counter % MAINTENANCE_PERIOD == 0) {
            evacuateWherePossible(player);

            duration.after("Evacuate where possible");
        }

        if (counter == MAX_PERIOD) {
            counter = 0;
        } else {
            counter++;
        }

        if (unfinishedBarracks != null && map.getBuildingAtPoint(unfinishedBarracks.getPosition()) != null) {
            unfinishedBarracks = map.getBuildingAtPoint(unfinishedBarracks.getPosition());
        }

        /* Start with finding the headquarters */
        if (state == State.INITIAL_STATE) {

            /* Find headquarter */
            headquarter = GamePlayUtils.findHeadquarter(player);

            /* Change the state to ready to build */
            state = State.READY_FOR_CONSTRUCTION;

            duration.after("Find headquarter");
        } else if (state == State.READY_FOR_CONSTRUCTION) {
            if (waitUntilOccupied && !militaryBuildingsFullyOccupied(player)) {
                state = State.WAITING_FOR_BUILDINGS_TO_GET_OCCUPIED;

                duration.after("Check military buildings fully occupied");

                return;
            }

            /* Find the spot for the next barracks */
            Point site = findSpotForNextBarracks(player, impossibleSpots);

            duration.after("Find spot for next barracks");

            /* Stay in the ready to build state if there is no suitable site to build at */
            if (site == null) {
                return;
            }

            /* Place barracks */
            unfinishedBarracks = map.placeBuilding(new Barracks(player), site);

            /* Connect the barracks with the headquarters */
            Road road = GamePlayUtils.connectPointToBuilding(player, map, unfinishedBarracks.getFlag().getPosition(), headquarter);

            if (!map.getRoads().contains(road)) {
                System.out.println("\nBarracks at " + site + " is not connected!");
            }

            /* Place flags where possible */
            GamePlayUtils.fillRoadWithFlags(map, road);

            /* Change state to wait for the barracks to be ready and occupied */
            state = State.WAITING_FOR_CONSTRUCTION;

            duration.after("Build new barracks");
        } else if (state == State.WAITING_FOR_CONSTRUCTION) {

            /* Build a new barracks if this barracks was destroyed */
            if (unfinishedBarracks.isBurningDown() || unfinishedBarracks.isDestroyed() || !map.isBuildingAtPoint(unfinishedBarracks.getPosition())) {

                /* Set state to build new barracks */
                state = State.READY_FOR_CONSTRUCTION;

            /* Disable promotions directly when the barracks is ready */
            } else if (unfinishedBarracks.isReady() && unfinishedBarracks.getNumberOfHostedSoldiers() == 0) {

                /* Disable promotions if the barracks is not close to the enemy */
                if (unfinishedBarracks.isPromotionEnabled() &&
                    GamePlayUtils.distanceToKnownEnemiesWithinRange(unfinishedBarracks, 20) > 9) {

                    if (unfinishedBarracks.isPromotionEnabled()) {
                        unfinishedBarracks.disablePromotions();
                    }
                } else {

                    /* Upgrade barracks close to the enemy */
                    if (unfinishedBarracks instanceof Barracks && !unfinishedBarracks.isUpgrading()) {
                        unfinishedBarracks.upgrade();
                    }
                }

                duration.after("Disable promotions and do upgrades");

            /* Check if construction is done and the building is occupied */
            } else if (unfinishedBarracks.isReady() && unfinishedBarracks.getNumberOfHostedSoldiers() > 0) {

                /* Save the barracks */
                placedBarracks.add(unfinishedBarracks);

                /* Evacuate any buildings far enough from the border */
                evacuateWherePossible(player);

                /* Change the state to construction done */
                state = State.READY_FOR_CONSTRUCTION;

                /* Signal that there is at least one new building in place */
                newBuildings = true;

                duration.after("Handle construction done");

            /* Verify that the barracks under construction is still reachable from the headquarters */
            } else if (!map.areFlagsOrBuildingsConnectedViaRoads(headquarter, unfinishedBarracks)) {

                /* Try to repair the connection */
                state = State.BUILDING_NOT_CONNECTED;
            }
        } else if (state == State.BUILDING_NOT_CONNECTED) {

            /* Try to repair the connection */
            GamePlayUtils.repairConnection(map, player, unfinishedBarracks.getFlag(), headquarter.getFlag());

            /* Wait for the building to get constructed if the repair worked */
            if (map.areFlagsOrBuildingsConnectedViaRoads(headquarter, unfinishedBarracks)) {

                /* Wait for construction */
                state = State.WAITING_FOR_CONSTRUCTION;

            /* Destroy the building if the repair failed */
            } else {

                /* Destroy the building */
                unfinishedBarracks.tearDown();

                /* Remember that this spot didn't work out */
                impossibleSpots.add(unfinishedBarracks.getPosition());

                /* Construct a new building */
                state = State.READY_FOR_CONSTRUCTION;
            }

            duration.after("Check building is still connected");

        } else if (state == State.WAITING_FOR_BUILDINGS_TO_GET_OCCUPIED) {
            if (militaryBuildingsFullyOccupied(player)) {
                state = State.READY_FOR_CONSTRUCTION;
            }

            duration.after("Waited for building to get occupied");
        }

        duration.reportStats(stats);
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public Player getControlledPlayer() {
        return player;
    }

    private Point findSpotForNextBarracks(Player player, Set<Point> ignore) {

        CumulativeDuration duration = stats.measureCumulativeDuration("ExpandLandPlayer.findSpotForNextBarracks", collectEachTurnGroup);

        Set<Point> candidates = new HashSet<>();
        Set<Point> investigated = new HashSet<>();

        Set<Building> ownMilitaryBuildings = GamePlayUtils.getMilitaryBuildingsForPlayer(player);
        Set<Building> enemyMilitaryBuildings = GamePlayUtils.getDiscoveredEnemyMilitaryBuildingsForPlayer(player);
        Set<Flag> flagsReachableFromHeadquarter = GameUtils.findFlagsReachableFromPoint(player, headquarter.getPosition());

        /* Score the candidates and pick the one with the best score */
        int bestScore = 0;
        Point bestPoint = null;

        /* First collect all possible points to build on */
        for (Point borderPoint : player.getBorderPoints()) {

            /* Filter border points that are too close to the edge of the map */
            if (borderPoint.x < 3 || borderPoint.x > map.getWidth() - 3 &&
                borderPoint.y < 3 || borderPoint.y > map.getHeight() - 3) {
                continue;
            }

            /* Go through points for construction close to the border point */
            for (Point point : map.getPointsWithinRadius(borderPoint, MAX_DISTANCE_FROM_BORDER)) {

                CumulativeDuration innerDuration = stats.measureCumulativeDuration("ExpandLandPlayer.findSpotForNextBarracks.innerFor", collectEachTurnGroup);

                /* Filter out border too close to the edge of the map */
                if (point.x < MIN_DISTANCE_TO_EDGE || point.x > map.getWidth() - MIN_DISTANCE_TO_EDGE ||
                    point.y < MIN_DISTANCE_TO_EDGE || point.y > map.getHeight() - MIN_DISTANCE_TO_EDGE) {

                    innerDuration.after("Exit on border filter");

                    innerDuration.report();

                    continue;
                }

                /* Don't re-examine already added candidates */
                if (investigated.contains(point)) {

                    innerDuration.after("Exit on investigated filter");

                    innerDuration.report();

                    continue;
                }

                /* Make sure this point will not be investigated again */
                investigated.add(point);

                /* Filter points the player doesn't own */
                if (!player.getLandInPoints().contains(point)) {

                    innerDuration.after("Exit on land owned filter");

                    innerDuration.report();

                    continue;
                }

                /* Filter out spots we have tried before and failed at */
                if (ignore.contains(point)) {

                    innerDuration.after("Exit on ignore filter");

                    innerDuration.report();

                    continue;
                }

                /* Filter out impossible points */
                if (impossibleSpots.contains(point)) {

                    innerDuration.after("Exit on impossible filter");

                    innerDuration.report();

                    continue;
                }


                innerDuration.after("Quick filters");

                /* Filter out points that cannot be built on */
                if (map.isAvailableHousePoint(player, point) == null) {

                    innerDuration.after("Is available house point");

                    innerDuration.report();

                    continue;
                }

                innerDuration.after("Is available house point");

                int candidateScore = 0;

                /* Determine if this point is close to an enemy */
                if (preferEnemyDirection) {

                    for (Building enemyMilitaryBuilding : enemyMilitaryBuildings) {

                        double distanceToEnemyBuilding = point.distance(enemyMilitaryBuilding.getPosition());

                        if (distanceToEnemyBuilding < ENEMY_CLOSE) {
                            candidateScore = candidateScore + CLOSE_TO_ENEMY_WEIGHT;

                            break;
                        }
                    }
                }

                innerDuration.after("Close to enemy");

                /* Reward points that are far from own military buildings */
                double distanceToClosestMilitaryBuilding = Double.MAX_VALUE;

                for (Building militaryBuilding : ownMilitaryBuildings) {

                    double tempDistance = point.distance(militaryBuilding.getPosition());

                    if (tempDistance < distanceToClosestMilitaryBuilding) {
                        distanceToClosestMilitaryBuilding = tempDistance;
                    }
                }

                innerDuration.after("Far from own military building");

                /* Filter points that cannot be connected to the headquarters */
                Point pointDownRight = point.downRight();

                /* Can a road be placed directly to the headquarters? */
                Set<Point> avoid = new HashSet<>();
                avoid.add(point);

                List<Point> wayPoints = map.findAutoSelectedRoad(player, pointDownRight, headquarter.getFlag().getPosition().downRightDownRight(), avoid);

                boolean canPlaceRoadToHeadquarter = wayPoints != null;

                /* Can a road be placed to the closest flag? */
                boolean canReachClosestFlag = false;

                if (!canPlaceRoadToHeadquarter) {
                    Flag closestFlag = null;
                    int distanceToClosestFlag = Integer.MAX_VALUE;

                    for (Flag flag : flagsReachableFromHeadquarter) {
                        int candidateDistanceToClosestFlag = GameUtils.distanceInGameSteps(pointDownRight, flag.getPosition());

                        if (candidateDistanceToClosestFlag < distanceToClosestFlag) {
                            distanceToClosestFlag = candidateDistanceToClosestFlag;

                            closestFlag = flag;
                        }

                        if (distanceToClosestFlag < 3) {
                            break;
                        }
                    }

                    wayPoints = map.findAutoSelectedRoad(player, pointDownRight, closestFlag.getPosition(), avoid);

                    canReachClosestFlag = wayPoints != null;
                }

                if (!canPlaceRoadToHeadquarter && !canReachClosestFlag) {
                    innerDuration.after("Can connect building");

                    innerDuration.report();

                    continue;
                }

                innerDuration.after("Can connect building");

                /* Add the point as a candidate if it passed the filters */
                candidates.add(point);

                /* Look for the best score */
                candidateScore = candidateScore + (int)distanceToClosestMilitaryBuilding;

                if (candidateScore > bestScore) {
                    bestScore = candidateScore;

                    bestPoint = point;
                }

                /* Stop early if we find a score that's good enough */
                if (candidateScore > GOOD_ENOUGH_SCORE) {
                    bestPoint = point;

                    break;
                }

                innerDuration.report();
            }
        }

        duration.after("Find possible points to build on");

        duration.report();

        /* Track the score */
        Variable scoreVariable = stats.addVariableIfMissing("ExpandLandPlayer.NewBarracks.score");

        scoreVariable.reportValue(bestScore);

        /* Return the least bad alternative if no good point was found */
        return bestPoint;
    }

    private void evacuateWherePossible(Player player) {

        /* Go through the buildings and evacuate where possible */
        for (Building storedBuilding : placedBarracks) {

            /* Cater for upgrades */
            Building building = map.getBuildingAtPoint(storedBuilding.getPosition());

            /* Only investigate military buildings */
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            /* Skip buildings that are already evacuated */
            if (building.isEvacuated()) {
                continue;
            }

            /* Check if the building is far enough from the border */
            boolean borderClose = false;

            for (Point borderPoint : player.getBorderPoints()) {

                /* Filter points beyond the evacuation threshold */
                if (borderPoint.distance(building.getPosition()) >= THRESHOLD_FOR_EVACUATION) {
                    continue;
                }

                /* Filter points at the edge of the map since no attack can come that way */
                if (borderPoint.x < 3 || borderPoint.x > map.getWidth() - 3 ||
                    borderPoint.y < 3 || borderPoint.y > map.getHeight() - 3) {
                    continue;
                }

                /* The border is close if we made it here */
                borderClose = true;

                break;
            }


            /* Evacuate the building if it's not close to the border */
            if (!borderClose) {
                building.evacuate();
            }
        }
    }

    void clearNewBuildings() {
        newBuildings = false;
    }

    boolean hasNewBuildings() {
        return newBuildings;
    }

    void registerBuildings(List<Building> wonBuildings) throws InvalidUserActionException {

        for (Building building : wonBuildings) {

            /* Connect the building to the headquarters if it's not already done */
            try {
                if (!map.areFlagsOrBuildingsConnectedViaRoads(headquarter, building)) {
                    Road road = GamePlayUtils.connectPointToBuilding(player, map, building.getFlag().getPosition(), headquarter);

                    if (road != null) {
                        GamePlayUtils.fillRoadWithFlags(map, road);
                    } else {
                        System.out.println("Could not place road for newly registered barracks at " + building.getPosition());
                    }
                }
            } catch (Exception e) { }

            /* Disable promotions if the barracks is not close to the enemy */
            if (GamePlayUtils.distanceToKnownEnemiesWithinRange(building, 20) > 9) {
                if (building.isPromotionEnabled()) {
                    building.disablePromotions();
                }

            } else {

                /* Upgrade barracks close to the enemy */
                if (!building.isUpgrading() && building instanceof Barracks) {
                    building.upgrade();
                }
            }

            /* Treat these as regular buildings placed by the expand land player */
            if (!placedBarracks.contains(building)) {
                placedBarracks.add(building);
            }
        }
    }

    void setExpandTowardEnemies(boolean b) {
        preferEnemyDirection = b;
    }

    void waitForBuildingsToGetCompletelyOccupied(boolean b) {
        waitUntilOccupied = b;
    }

    private boolean militaryBuildingsFullyOccupied(Player player) {
        for (Building building : player.getBuildings()) {

            /* Filter non-military buildings */
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            /* Filter evacuated buildings */
            if (building.isEvacuated()) {
                continue;
            }

            /* Filter not constructed buildings */
            if (!building.isReady()) {
                continue;
            }

            /* Check if the building is fully occupied */
            if (building.getNumberOfHostedSoldiers() < building.getMaxHostedSoldiers()) {
                return false;
            }
        }

        return true;
    }
}
