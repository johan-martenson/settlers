package org.appland.settlers.model;

import java.util.List;

public class Cargo {

    private final Material material;
    private final GameMap  map;

    private Building    target;
    private Point       position;
    private List<Point> path;
    private boolean     deliveryPromised;

    public Cargo(Material material, GameMap map) {

        this.material = material;
        deliveryPromised = false;
        this.map = map;
    }

    public Material getMaterial() {
        return material;
    }

    public void setTarget(Building target) {
        this.target = target;

        Flag flag = map.getFlagAtPoint(getPosition());
        path = map.findWayWithExistingRoadsInFlagsAndBuildings(flag, target);

        path.remove(0);
    }

    public Building getTarget() {
        return target;
    }

    public Point getNextFlagOrBuilding() {
        if (path == null || path.isEmpty()) {
            return null;
        }

        return path.get(0);
    }

    // FIXME: HOTSPOT
    public void setPosition(Point point) {

        MapPoint mapPoint = map.getMapPoint(point);

        if (mapPoint.isFlag() || mapPoint.isBuilding()) {
            if (position != null && position != point) {

                MapPoint mapPointCurrent = map.getMapPoint(position);

                if (mapPointCurrent.isFlag() || mapPointCurrent.isBuilding()) {
                    Road road = map.getRoad(point, position);

                    if (road != null) {
                        road.registerUsage();
                    }
                } else {

                    Road road = map.getRoadAtPoint(position);

                    if (road != null) {
                        road.registerUsage();
                    }
                }
            }
        }

        position = point;

        if (path != null && !path.isEmpty() && path.get(0).equals(point)) {
            path.remove(0);
        }
    }

    @Override
    public String toString() {
        String materialName = material.getSimpleName();
        String houseName = target.getSimpleName();
        Point housePosition = target.getPosition();

        return "Cargo of " + materialName + " to " + houseName + " " + housePosition + ", at " + position;
    }

    public Point getPosition() {
        return position;
    }

    void promiseDelivery() {
        deliveryPromised = true;
    }

    public boolean isDeliveryPromised() {
        return deliveryPromised;
    }

    void clearPromisedDelivery() {
        deliveryPromised = false;
    }

    void transportToStorage() throws InvalidRouteException {
        Storehouse storehouse0 = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, material);

        if (storehouse0 != null) {
            setTarget(storehouse0);
        } else {
            target = null;
        }
    }

    private void returnToClosestStorage() throws InvalidRouteException {
        Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, getMaterial());

        if (storehouse != null) {
            setTarget(storehouse);
        }
    }

    private void returnToStorage() throws InvalidRouteException {
        Storehouse storehouse0 = GameUtils.getClosestStorageConnectedByRoads(getPosition(), map);

        if (storehouse0 != null) {
            setTarget(storehouse0);
        }
    }

    // FIXME: ALLOCATION HOTSPOT
    void rerouteIfNeeded() throws InvalidRouteException {

        /* Handle the case where the targeted building cannot receive the cargo */
        if (target == null) {
            returnToClosestStorage();
        } else if (!target.equals(map.getBuildingAtPoint(target.getPosition()))) {
            returnToStorage();
        } else if (target.isBurningDown()) {
            returnToStorage();
        } else if (target.isDestroyed()) {
            returnToStorage();
        } else {

            /* Re-evaluate the route if the current one is not optimal or if it's
             * no longer available
             *
             * Note: the path only contains flags and buildings. It does not contain each individual step
            */
            if (!looksLikeOptimalRoute(getPosition(), path)                                   ||  // Is there theoretically a better way?
                !map.isValidRouteThroughFlagsAndBuildingsViaRoads(getPosition(), path.get(0)) ||  // Is it still possible to go the next step?
                !map.isValidRouteThroughFlagsAndBuildingsViaRoads(path)) {                        // Is the planned path still possible?

                /* Find the best way from this flag */
                Flag flag = map.getFlagAtPoint(getPosition());
                List<Point> closestPath = map.findWayWithExistingRoadsInFlagsAndBuildings(flag, getTarget());

                /* Return the cargo to storage if there is no available route to the target */
                if (closestPath == null) {

                    /* Break the promise to deliver to the target */
                    getTarget().cancelPromisedDelivery(this);

                    /* Return the cargo to the storage */
                    returnToStorage();
                } else {

                    /* Update the planned route to use the closest way */
                    closestPath.remove(0);

                    path = closestPath;
                }
            }
        }
    }

    /**
     * Note: this is only definitely optimal if all points are included. If the path only consists of flags the result
     * might not be too optimistic
     * @param start Start of the route
     * @param path Path to take from the starting point
     * @return Returns true if the route looks optimal
     */
    private boolean looksLikeOptimalRoute(Point start, List<Point> path) {

        Point target = path.get(path.size() - 1); //FIXME: this hides a field

        Point previousPoint = start; // TODO: this variable is never used so this method shouldn't work

        for (Point point : path) {

            if (previousPoint != null) {
                int currentDistanceX = Math.abs(target.x - point.x);
                int currentDistanceY = Math.abs(target.y - point.y);

                int previousDistanceX = Math.abs(target.x - previousPoint.x);
                int previousDistanceY = Math.abs(target.y - previousPoint.y);

                if (currentDistanceX > previousDistanceX || currentDistanceY > previousDistanceY) {
                    return false;
                }
            }

        }

        return true;
    }
}
