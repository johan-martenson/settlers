package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flag implements EndPoint {

    private static final int MAX_NUMBER_OF_STACKED_CARGO = 8;

    private final List<Cargo> stackedCargo;

    private Point  position;
    private int    geologistsCalled;
    private int    scoutsCalled;
    private Player player;
    private Set<Cargo> promisedCargo;

    public Flag(Point point) {
        position         = point;
        stackedCargo     = new ArrayList<>();
        geologistsCalled = 0;
        scoutsCalled     = 0;
        promisedCargo    = new HashSet<>();
    }

    Flag(Player player, Point point) {
        this(point);

        this.player = player;
    }

    public List<Cargo> getStackedCargo() {
        return stackedCargo;
    }

    @Override
    public void putCargo(Cargo cargo) throws InvalidRouteException {

        cargo.setPosition(getPosition());
        stackedCargo.add(cargo);

        /* Give the cargo a chance to re-plan */
        cargo.rerouteIfNeeded();

        /* Remove the promise for this cargo */
        promisedCargo.remove(cargo);

        /* Report that the flag has changed */
        if (player != null) {
            GameMap map = player.getMap();

            if (map != null) {
                player.getMap().reportChangedFlag(this);
            }
        }

    }

    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point point) {
        position = point;
    }

    @Override
    public String toString() {
        if (stackedCargo.isEmpty()) {
            return "Flag " + position;
        } else {
            StringBuilder stringBuilder = new StringBuilder("Flag " + position + " (stacked cargo:");

            for (Cargo cargo : stackedCargo) {
                stringBuilder.append(" ").append(cargo.getMaterial().name());
            }

            stringBuilder.append(")");

            return stringBuilder.toString();
        }
    }

    public boolean hasCargoWaitingForRoad(Road road) {
        return getCargoWaitingForRoad(road) != null;
    }

    public Cargo retrieveCargo(Cargo cargo) {

        if (stackedCargo.contains(cargo)) {

            stackedCargo.remove(cargo);

            if (player != null) {
                GameMap map = player.getMap();

                if (map != null) {
                    player.getMap().reportChangedFlag(this);
                }
            }

            return cargo;
        }

        return null;
    }

    public Cargo getCargoWaitingForRoad(Road road) {

        Cargo waitingCargo = null;
        int priority = Integer.MAX_VALUE;

        for (Cargo cargo : stackedCargo) {

            /* Filter cargos where pickup is already planned */
            if (cargo.isPickupPromised()) {
                continue;
            }

            /* Filter cargos without a target */
            if (cargo.getTarget() == null) {
                continue;
            }

            /* Filter cargos that will not benefit from going through the courier's road */
            GameMap map = cargo.getMap();

            Building target = cargo.getTarget();
            EndPoint otherEndOfRoad = road.getOtherEndPoint(this);

            List<Point> bestPath = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(this, target);

            List<Point> pathThroughRoad = map.findDetailedWayWithExistingRoadsInFlagsAndBuildings(otherEndOfRoad, target, this.getPosition());

            /* Filter cargos where there is no road available */
            if (bestPath == null) {
                continue;
            }

            /* Filter roads that don't lead to the target building */
            if (pathThroughRoad == null) {
                continue;
            }

            /* Let the best courier do the delivery if it's available */
            Road optimalRoad = map.getRoadAtPoint(bestPath.get(1));

            Courier courierForOptimalRoad = optimalRoad.getCourier();
            Donkey donkeyForOptimalRoad = optimalRoad.getDonkey();

            /* If the "asking road" is not the optimal road - see if the optimal courier or donkey is idle */
            if (!road.equals(optimalRoad)) {
                if ((courierForOptimalRoad != null && courierForOptimalRoad.isIdle()) ||
                    (donkeyForOptimalRoad != null && donkeyForOptimalRoad.isIdle())) {
                    continue;
                }
            }

            /* Avoid roads that are more than double as long as the most optimal road */
            if (pathThroughRoad.size() > bestPath.size() * 2) {
                continue;
            }

            int tmpPriority = player.getTransportPriority(cargo);

            if (tmpPriority < priority) {
                priority = tmpPriority;
                waitingCargo = cargo;
            }

            if (priority == 0) {
                break;
            }
        }

        return waitingCargo;
    }

    public void callGeologist() {
        geologistsCalled++;
    }

    void geologistSent() {
        geologistsCalled--;
    }

    boolean needsGeologist() {
        return geologistsCalled > 0;
    }

    public void callScout() {
        scoutsCalled++;
    }

    void scoutSent() {
        scoutsCalled--;
    }

    boolean needsScout() {
        return scoutsCalled > 0;
    }

    public Player getPlayer() {
        return player;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    public void promiseCargo(Cargo cargo) {
        promisedCargo.add(cargo);
    }

    public boolean hasPlaceForMoreCargo() {
        return stackedCargo.size() + promisedCargo.size() < MAX_NUMBER_OF_STACKED_CARGO;
    }

    public void onRemove() {

        /* Break delivery promises for any stacked cargo */
        for (Cargo cargo : stackedCargo) {

            if (!cargo.isPickupPromised()) {
                continue;
            }

            Building building = cargo.getTarget();

            building.cancelPromisedDelivery(cargo);
        }
    }
}
