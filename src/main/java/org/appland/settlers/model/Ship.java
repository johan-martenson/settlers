package org.appland.settlers.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;

@Walker(speed = 10)
public class Ship extends Worker {
    private static final int TIME_TO_BUILD_SHIP = 100;

    private final Countdown countdown;
    private final Set<Cargo> cargos;

    private State state;
    private Point targetHarborPoint;
    private Harbor targetHarbor;

    private enum State {
        WAITING_FOR_TASK,
        SAILING_TO_HARBOR_TO_TAKE_ON_TASK,
        SAILING_TO_START_NEW_SETTLEMENT,
        SAILING_TO_POINT_TO_WAIT_FOR_ORDERS, READY_TO_START_EXPEDITION, UNDER_CONSTRUCTION
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

                /* Tell the map that the ship is ready so it's possible to place construction on it again */
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

            /* Is there a harbor that has collected material for an expedition? */
            List<Point> pathToClosestHarbor = null;
            Harbor targetHarbor = null;
            int distanceToClosestHarbor = Integer.MAX_VALUE;

            for (Building building : player.getBuildings()) {

                /* Filter all buildings that are not harbors */
                if (!building.isHarbor()) {
                    continue;
                }

                Harbor harbor = (Harbor) building;

                /* Filter harbors that are not ready for an expedition */
                if (!harbor.isReadyForExpedition()) {
                    continue;
                }

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

                /* Look for the closest harbor */
                if (path.size() >= distanceToClosestHarbor) {
                    continue;
                }

                distanceToClosestHarbor = path.size();
                pathToClosestHarbor = path;
                targetHarbor = harbor;
            }

            if (pathToClosestHarbor != null) {
                state = State.SAILING_TO_HARBOR_TO_TAKE_ON_TASK;

                targetHarbor.promiseShip(this);

                setOffroadTargetWithPath(pathToClosestHarbor);
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

            state = State.WAITING_FOR_TASK;

            harbor.addShipReadyForTask(this);
        } else if (state == State.SAILING_TO_START_NEW_SETTLEMENT) {
            player.reportShipReachedDestination(this);
        } else if (state == State.SAILING_TO_POINT_TO_WAIT_FOR_ORDERS) {
            state = State.WAITING_FOR_TASK;
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

        /* Place a harbor */
        Harbor newHarbor = map.placeBuilding(new Harbor(player), targetHarborPoint);

        /* Place the builder */
        Builder builder = new Builder(player, map);

        map.placeWorker(builder, newHarbor.getFlag());

        builder.setTargetBuilding(newHarbor);

        newHarbor.promiseBuilder(builder);

        /* Deliver the required material to the harbor */
        GameUtils.putCargos(PLANK, 4, newHarbor);
        GameUtils.putCargos(STONE, 6, newHarbor);

        /* Update the border */
        newHarbor.setOwnSettlement();

        map.updateBorder(newHarbor, BorderChangeCause.NEW_SETTLEMENT);
    }

    public boolean isReadyToStartExpedition() {
        return state == State.READY_TO_START_EXPEDITION;
    }

    public void setReadyForExpedition() {
        state = State.READY_TO_START_EXPEDITION;
    }
}
