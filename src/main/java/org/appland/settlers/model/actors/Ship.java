package org.appland.settlers.model.actors;

import org.appland.settlers.model.BorderChangeCause;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.Direction;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Harbor;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.appland.settlers.model.BorderCheck.CAN_PLACE_OUTSIDE_BORDER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;

@Walker(speed = 10)
public class Ship extends Worker {
    private static final int TIME_TO_BUILD_SHIP = 100;
    private static final int MAX_STORAGE = 33; // TODO: verify that this number is correct

    private final Countdown countdown = new Countdown();
    private final Set<Cargo> cargos = new HashSet<>();

    private State state = State.UNDER_CONSTRUCTION;
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

    public Ship(Player player, GameMap map) {
        super(player, map);

        countdown.countFrom(TIME_TO_BUILD_SHIP);
    }

    @Override
    void onIdle() {
        switch (state) {
            case UNDER_CONSTRUCTION -> {
                if (countdown.hasReachedZero()) {
                    map.reportShipReady(this);

                    var pathToWaterPoint = GameUtils.getHexagonAreaAroundPoint(getPosition(), 4, map).stream()
                            .filter(point -> GameUtils.isAll(map.getSurroundingTiles(point), Vegetation.WATER))
                            .map(point -> new GameUtils.Tuple<>(point, map.findWayForShip(getPosition(), point)))
                            .filter(entry -> entry.t2() != null)
                            .findFirst()
                            .map(GameUtils.Tuple::t2);

                    if (pathToWaterPoint.isPresent()) {
                        state = State.SAILING_TO_POINT_TO_WAIT_FOR_ORDERS;
                        setOffroadTargetWithPath(pathToWaterPoint.get());
                    } else {
                        state = State.WAITING_FOR_TASK;
                    }
                } else {
                    countdown.step();
                }
            }
            case WAITING_FOR_TASK -> {
                List<Harbor> harborsWithTasksForShipSortedByDistance = player.getBuildings().stream()
                        .filter(Building::isHarbor)
                        .filter(Building::isReady)
                        .map(Harbor.class::cast)
                        .filter(Harbor::hasTaskForShip)
                        .sorted(Comparator.comparingInt(harbor -> GameUtils.distanceInGameSteps(getPosition(), harbor.getPosition())))
                        .toList();

                harborsWithTasksForShipSortedByDistance.stream()
                        .map(harbor -> new GameUtils.Tuple<>(harbor, GameUtils.getClosestWaterPointForBuilding(harbor)))
                        .filter(entry -> entry.t2() != null)
                        .map(entry -> new GameUtils.Tuple<>(entry.t1(), map.findWayForShip(getPosition(), entry.t2())))
                        .filter(entry -> entry.t2() != null)
                        .findFirst()
                        .ifPresent(entry -> {
                            targetHarbor = entry.t1();
                            state = State.SAILING_TO_HARBOR_TO_TAKE_ON_TASK;
                            targetHarbor.promiseShip(this);
                            setOffroadTargetWithPath(entry.t2());
                        });
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
        switch (state) {
            case SAILING_TO_HARBOR_TO_TAKE_ON_TASK -> {
                Harbor harbor = GameUtils.getClosestHarborOffroadForPlayer(getPlayer(), getPosition(), 5);

                if (harbor.needsToShipToOtherHarbors()) {
                    Map<Harbor, Map<Material, Integer>> neededShipments = harbor.getNeededShipmentsFromThisHarbor();
                    targetHarbor = neededShipments.keySet().iterator().next();

                    neededShipments.get(targetHarbor).forEach((material, amountNeeded) -> {
                        int amountAvailable = harbor.getAmount(material);
                        int spaceAvailable = MAX_STORAGE - cargos.size();
                        int amountToLoad = Math.min(amountNeeded, Math.min(amountAvailable, spaceAvailable));
                        cargos.addAll(harbor.retrieve(material, amountToLoad));
                    });

                    List<Point> pathToTargetHarbor = map.findWayForShip(getPosition(), GameUtils.getClosestWaterPoint(targetHarbor.getPosition(), map));
                    state = State.SAILING_TO_HARBOR_WITH_CARGO;
                    setOffroadTargetWithPath(pathToTargetHarbor);
                } else {
                    state = State.WAITING_FOR_TASK;
                    harbor.addShipReadyForTask(this);
                }
            }
            case SAILING_TO_START_NEW_SETTLEMENT -> getPlayer().reportShipReachedDestination(this);
            case SAILING_TO_POINT_TO_WAIT_FOR_ORDERS -> state = State.WAITING_FOR_TASK;
            case SAILING_TO_HARBOR_WITH_CARGO -> {
                cargos.forEach(targetHarbor::putCargo);
                cargos.clear();
            }
        }
    }

    public Set<Cargo> getCargos() {
        return cargos;
    }

    public void putCargos(Material material, int amount) {
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

        return map.getPossiblePlacesForHarbor().stream()
                .filter(point -> !map.isBuildingAtPoint(point))
                .map(point -> GameUtils.getDirection(getPosition(), point))
                .collect(Collectors.toSet());
    }

    public void startExpedition(Direction direction) throws InvalidUserActionException {
        GameMap map = getMap();
        Point position = getPosition();

        var pathToTarget = map.getPossiblePlacesForHarbor().stream()
                .filter(point -> !map.isBuildingAtPoint(point)) // Filter occupied harbor points
                .filter(point -> Objects.equals(GameUtils.getDirection(position, point), direction)) // Filter other directions
                .map(point -> new GameUtils.Tuple<>(point, map.findWayForShip(position, GameUtils.getClosestWaterPoint(point, map)))) // Find the way to the potential target
                .filter(entry -> entry.t2() != null) // Filter potential targets that can't be reached
                .findFirst(); // Select the first valid target

        if (pathToTarget.isPresent()) {
            state = State.SAILING_TO_START_NEW_SETTLEMENT;
            setOffroadTargetWithPath(pathToTarget.get().t2());
            targetHarborPoint = pathToTarget.get().t1();
        } else {
            throw new InvalidUserActionException(String.format("No suitable target in this direction: %s", direction));
        }
    }

    @Override
    public String toString() {
        return state == State.UNDER_CONSTRUCTION
                ? String.format("Ship under construction %s", getPosition())
                : String.format("Ship %s", getPosition());
    }

    public void startSettlement() throws InvalidUserActionException {
        // TODO: make sure the harbor is not within any player's border

        Harbor newHarbor = map.placeBuilding(new Harbor(getPlayer()), targetHarborPoint, CAN_PLACE_OUTSIDE_BORDER);

        Builder builder = new Builder(getPlayer(), map);
        map.placeWorker(builder, newHarbor.getFlag());
        builder.setTargetBuilding(newHarbor);
        newHarbor.promiseBuilder(builder);

        GameUtils.putCargos(PLANK, 4, newHarbor);
        GameUtils.putCargos(STONE, 6, newHarbor);

        cargos.clear();
        newHarbor.setOwnSettlement();
        map.updateBorder(newHarbor, BorderChangeCause.NEW_SETTLEMENT);

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
