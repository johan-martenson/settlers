package org.appland.settlers.model;

import java.util.*;
import java.util.stream.Collectors;

import static org.appland.settlers.model.BorderCheck.CAN_PLACE_OUTSIDE_BORDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;

@Walker(speed = 10)
public class Ship extends Worker {
    private static final int TIME_TO_BUILD_SHIP = 100;
    private static final int MAX_STORAGE = 33; // TODO: verify that this number is correct

    private final Countdown countdown;
    private final Set<Cargo> cargos;

    private State state;
    private Point targetHarborPoint;
    private Harbor targetHarbor;

    private enum State {
        WAITING_FOR_TASK,
        SAILING_TO_HARBOR_TO_TAKE_ON_TASK,
        SAILING_TO_START_NEW_SETTLEMENT,
        SAILING_TO_POINT_TO_WAIT_FOR_ORDERS,
        READY_TO_START_EXPEDITION,
        SAILING_TO_HARBOR_WITH_CARGO,
        UNDER_CONSTRUCTION
    }

    Ship(Player player, GameMap map) {
        super(player, map);

        state = State.UNDER_CONSTRUCTION;

        countdown = new Countdown();

        countdown.countFrom(TIME_TO_BUILD_SHIP);
        cargos = new HashSet<>();
    }

    @Override
    void onIdle() {

        if (state == State.UNDER_CONSTRUCTION) {
            if (countdown.hasReachedZero()) {

                Point position = getPosition();

                /* Tell the map that the ship is ready, so it's possible to place construction on it again */
                map.reportShipReady(this);

                /* Sail out a small step into the water if there is no expedition to start */
                Point surroundedByWaterPoint = null;
                List<Point> pathToWaterPoint = null;

                for (Point point : GameUtils.getHexagonAreaAroundPoint(position, 4, map)) {

                    /* Filter points not surrounded by water */
                    if (!GameUtils.isAll(map.getSurroundingTiles(point), DetailedVegetation.WATER)) {
                        continue;
                    }

                    /* Filter points that can't be reached */
                    pathToWaterPoint = map.findWayForShip(position, point);

                    if (pathToWaterPoint == null) {
                        continue;
                    }

                    surroundedByWaterPoint = point;

                    break;
                }

                /* Sail to the point surrounded by water close to the shipyard and wait */
                if (surroundedByWaterPoint != null) {
                    state = State.SAILING_TO_POINT_TO_WAIT_FOR_ORDERS;

                    setOffroadTargetWithPath(pathToWaterPoint);

                /* Just stay in the current place if there is nowhere to go */
                } else {
                    state = State.WAITING_FOR_TASK;
                }
            } else {
                countdown.step();
            }
        } else if (state == State.WAITING_FOR_TASK) {

            Point position = getPosition();

            List<Harbor> harborsWithTasksForShipSortedByDistance = player.getBuildings().stream()
                    .filter(Building::isHarbor)
                    .filter(Building::isReady)
                    .map(building -> (Harbor) building)
                    .filter(Harbor::hasTaskForShip)

                    /* Sort by distance to the ship - closest first */
                    .map(harbor -> new GameUtils.BuildingAndData<>(
                            harbor,
                            GameUtils.getDistanceInGameSteps(position, harbor.getPosition())))
                    .sorted(Comparator.comparingInt(GameUtils.BuildingAndData::getData))
                    .map(GameUtils.BuildingAndData::getBuilding)
                    .collect(Collectors.toUnmodifiableList());

            /* Start sailing to the nearest point that can be reached (if any) */
            for (Harbor harbor : harborsWithTasksForShipSortedByDistance) {

                Point waterPoint = GameUtils.getClosestWaterPointForBuilding(harbor);

                /* Filter harbors without any close points in water */
                if (waterPoint == null) {
                    continue;
                }

                /* Filter harbors that cannot be reached */
                List<Point> path = map.findWayForShip(getPosition(), waterPoint);

                if (path == null) {
                    continue;
                }

                /* Sail to the harbor to pick up a new task */
                targetHarbor = harbor;

                state = State.SAILING_TO_HARBOR_TO_TAKE_ON_TASK;

                targetHarbor.promiseShip(this);

                setOffroadTargetWithPath(path);

                break;
            }
        }
    }

    public boolean isUnderConstruction() {
        return state == State.UNDER_CONSTRUCTION;
    }

    public boolean isReady() {
        return state != State.UNDER_CONSTRUCTION;
    }

    @Override
    void onArrival() {

        Player player = getPlayer();
        Point position = getPosition();

        if (state == State.SAILING_TO_HARBOR_TO_TAKE_ON_TASK) {
            Harbor harbor = GameUtils.getClosestHarborOffroadForPlayer(player, position, 5);

            if (harbor.needsToShipToOtherHarbors()) {

                // Get needed deliveries from this harbor to other harbors. Harbor-#material
                // Pick one of the harbors
                // FIXME: follow priority, pick up material for several harbors if there is capacity, limit pickup to ship's capacity
                Map<Harbor, Map<Material, Integer>> neededShipments = harbor.getNeededShipmentsFromThisHarbor();

                targetHarbor = neededShipments.keySet().iterator().next();
                Map<Material, Integer> neededShipmentsForTargetHarbor = neededShipments.get(targetHarbor);

                // Load up on cargo
                for (Map.Entry<Material, Integer> entry : neededShipmentsForTargetHarbor.entrySet()) {
                    Material material = entry.getKey();
                    int amountNeeded = entry.getValue();
                    int amountAvailable = harbor.getAmount(material);
                    int spaceAvailable = MAX_STORAGE - cargos.size();

                    int amountToLoadOfMaterial = Math.min(
                            Math.min(amountNeeded, amountAvailable),
                            spaceAvailable
                    );

                    cargos.addAll(harbor.retrieve(material, amountToLoadOfMaterial));
                }

                /* Find the way to the potential target */
                Point closestWaterPoint = GameUtils.getClosestWaterPoint(targetHarbor.getPosition(), map);

                List<Point> pathToTargetHarbor = map.findWayForShip(position, closestWaterPoint);

                state = State.SAILING_TO_HARBOR_WITH_CARGO;

                setOffroadTargetWithPath(pathToTargetHarbor);
            } else {
                state = State.WAITING_FOR_TASK;

                harbor.addShipReadyForTask(this);
            }
        } else if (state == State.SAILING_TO_START_NEW_SETTLEMENT) {
            player.reportShipReachedDestination(this);
        } else if (state == State.SAILING_TO_POINT_TO_WAIT_FOR_ORDERS) {
            state = State.WAITING_FOR_TASK;
        } else if (state == State.SAILING_TO_HARBOR_WITH_CARGO) {
            for (Cargo cargo : cargos) {
                targetHarbor.putCargo(cargo);
            }

            cargos.clear();
        }
    }

    public Set<Cargo> getCargos() {
        return cargos;
    }

    public void putCargos(Material material, int amount, Building building) {
        for (int i = 0; i < amount; i++) {
            cargos.add(new Cargo(material, getMap()));

            // FIXME: test & add setting target building
        }
    }

    public boolean isWaitingForExpedition() {
        return state == State.READY_TO_START_EXPEDITION;
    }

    public Set<Direction> getPossibleDirectionsForExpedition() {

        GameMap map = getMap();
        Set<Direction> directions = new HashSet<>();

        for (Point point : map.getPossiblePlacesForHarbor()) {

            /* Filter occupied harbor points */
            if (map.isBuildingAtPoint(point)) {
                continue;
            }

            Direction direction = GameUtils.getDirection(getPosition(), point);

            directions.add(direction);
        }

        return directions;
    }

    public void startExpedition(Direction direction) throws InvalidUserActionException {
        GameMap map = getMap();
        Point position = getPosition();

        /* Select target */
        for (Point point : map.getPossiblePlacesForHarbor()) {

            /* Filter occupied harbor points */
            if (map.isBuildingAtPoint(point)) {
                continue;
            }

            /* Filter other directions than the chosen one */
            if (!Objects.equals(GameUtils.getDirection(position, point), direction)) {
                continue;
            }

            /* Find the way to the potential target */
            Point closestWaterPoint = GameUtils.getClosestWaterPoint(point, map);

            List<Point> path = map.findWayForShip(position, closestWaterPoint);

            /* Filter potential targets that can't be reached */
            if (path == null) {
                continue;
            }

            /* Select the target */
            state = State.SAILING_TO_START_NEW_SETTLEMENT;

            setOffroadTargetWithPath(path);

            targetHarborPoint = point;

            break;
        }

        if (state != State.SAILING_TO_START_NEW_SETTLEMENT) {
            throw new InvalidUserActionException("No suitable target in this direction: " + direction);
        }
    }

    @Override
    public String toString() {
        if (state == State.UNDER_CONSTRUCTION) {
            return "Ship under construction " + getPosition();
        }

        return "Ship " + getPosition();
    }

    public void startSettlement() throws InvalidUserActionException {

        // TODO: make sure the harbor is not within any player's border

        /* Place a harbor */
        Harbor newHarbor = map.placeBuilding(new Harbor(player), targetHarborPoint, CAN_PLACE_OUTSIDE_BORDER);

        /* Place the builder */
        Builder builder = new Builder(player, map);

        map.placeWorker(builder, newHarbor.getFlag());

        builder.setTargetBuilding(newHarbor);

        newHarbor.promiseBuilder(builder);

        /* Deliver the required material to the harbor */
        GameUtils.putCargos(PLANK, 4, newHarbor);
        GameUtils.putCargos(STONE, 6, newHarbor);

        cargos.clear();

        /* Update the border */
        newHarbor.setOwnSettlement();

        map.updateBorder(newHarbor, BorderChangeCause.NEW_SETTLEMENT);

        /* Get ready for next task */
        state = State.WAITING_FOR_TASK;
    }

    public boolean isReadyToStartExpedition() {
        return state == State.READY_TO_START_EXPEDITION;
    }

    public void setReadyForExpedition() {
        state = State.READY_TO_START_EXPEDITION;
    }

    @Override
    protected void setOffroadTargetWithPath(List<Point> path) {
        super.setOffroadTargetWithPath(path);

        map.reportShipWithNewTarget(this);
    }
}
