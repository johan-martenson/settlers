package org.appland.settlers.model;

import java.util.List;
import java.util.function.Function;

public class Cargo {

    private final Material material;
    private final GameMap  map;

    private Building target;
    private Point    position;
    private boolean  pickupPromised;

    public Cargo(Material material, GameMap map) {
        this.material = material;
        this.pickupPromised = false;
        this.map = map;
    }

    public Material getMaterial() {
        return material;
    }

    public void setTarget(Building target) {
        this.target = target;
    }

    public Building getTarget() {
        return target;
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
    }

    @Override
    public String toString() {

        String materialName = material.getSimpleName();

        if (target == null) {
            return "Cargo of " + materialName + " to unknown, at " + position;
        }

        Point housePosition = target.getPosition();
        String houseName = target.getSimpleName();

        return "Cargo of " + materialName + " to " + houseName + " " + housePosition + ", at " + position;
    }

    public Point getPosition() {
        return position;
    }

    public void promisePickUp() {
        pickupPromised = true;
    }

    public boolean isPickupPromised() {
        return pickupPromised;
    }

    public void cancelPromisedPickUp() {
        pickupPromised = false;
    }

    public void transportToReceivingBuilding(Function<Building, Boolean> func) {
        Building receivingBuilding = GameUtils.getClosestBuildingConnectedByRoads(getPosition(), null, map, func);

        setTarget(receivingBuilding);

        if (receivingBuilding != null) {
            receivingBuilding.promiseDelivery(material);
        }
    }

    public void transportToStorage() {
        Storehouse storehouse0 = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, material);

        if (storehouse0 != null) {
            setTarget(storehouse0);
        } else {
            target = null;
        }
    }

    private void returnToClosestStorage() {
        Storehouse storehouse = GameUtils.getClosestStorageConnectedByRoadsWhereDeliveryIsPossible(getPosition(), null, map, getMaterial());

        if (storehouse != null) {
            setTarget(storehouse);
        }
    }

    private void returnToStorage() {
        Storehouse storehouse0 = GameUtils.getClosestStorageConnectedByRoads(getPosition(), map);

        if (storehouse0 != null) {
            setTarget(storehouse0);
        }
    }

    // FIXME: ALLOCATION HOTSPOT
    void rerouteIfNeeded() {

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

            /* Re-evaluate the route if the current one is not optimal or if it's no longer available
             *
             * Note: the path only contains flags and buildings. It does not contain each individual step
            */
            if (!map.isValidRouteThroughFlagsAndBuildingsViaRoads(getPosition(), target.getPosition())) {

                /* Find the best way from this flag */
                Flag flag = map.getFlagAtPoint(getPosition());
                List<Point> closestPath = map.findWayWithExistingRoadsInFlagsAndBuildings(flag, getTarget());

                /* Return the cargo to storage if there is no available route to the target */
                if (closestPath == null) {

                    /* Break the promise to deliver to the target */
                    getTarget().cancelPromisedDelivery(this);

                    /* Return the cargo to the storage */
                    returnToStorage();
                }
            }
        }
    }

    GameMap getMap() {
        return map;
    }
}
