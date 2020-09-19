package org.appland.settlers.model;

import java.util.Collections;
import java.util.List;

public class Road {

    private static final int MAIN_ROAD_THRESHOLD = 100;

    private final EndPoint    start;
    private final EndPoint    end;
    private final List<Point> steps;

    private int     usage;
    private Courier courier;
    private Donkey  donkey;
    private boolean needsCourier;
    private Player  player;
    private boolean isMainRoad;
    private GameMap map;

    protected Road(Player player, List<Point> wayPoints) {

        if (areRoadStepsTooLong(wayPoints)) {
            throw new InvalidGameLogicException("The steps are too long in " + wayPoints);
        }

        this.player = player;
        this.map = player.getMap();

        steps = Collections.unmodifiableList(wayPoints);

        Point pointStart = steps.get(0);
        Point pointEnd = steps.get(steps.size() - 1);

        MapPoint mapPointStart = map.getMapPoint(pointStart);
        MapPoint mapPointEnd = map.getMapPoint(pointEnd);

        if (mapPointStart.isFlag()) {
            start = mapPointStart.getFlag();
        } else {
            start = mapPointStart.getBuilding();
        }

        if (mapPointEnd.isFlag()) {
            end = mapPointEnd.getFlag();
        } else {
            end = mapPointEnd.getBuilding();
        }

        courier = null;
        donkey  = null;

        needsCourier = true;

        usage = 0;

        isMainRoad = false;
    }

    @Override
    public String toString() {
        return "Road " + start.getPosition() + " - " + end.getPosition();
    }

    public EndPoint[] getFlags() {
        return new EndPoint[]{start, end};
    }

    public boolean needsCourier() {
        return courier == null && needsCourier;
    }

    public Courier getCourier() {
        return courier;
    }

    void setCourier(Courier wr) {
        if (wr instanceof Donkey) {

            if (donkey != null) {
                throw new InvalidGameLogicException("Can't assign donkey, there is already a donkey assigned.");
            }

            donkey = (Donkey) wr;
        } else {
            if (courier != null) {
                throw new InvalidGameLogicException("Can't assign courier, there is already a courier assigned.");
            }

            courier = wr;
        }
    }

    public List<Point> getWayPoints() {
        return steps;
    }

    private boolean areRoadStepsTooLong(List<Point> wayPoints) {
        Point previous = null;

        for (Point current : wayPoints) {
            if (previous == null) {
                previous = current;
                continue;
            }

            if (!previous.isAdjacent(current)) {
                return true;
            }

            if (previous.equals(current.up()) || previous.equals(current.down())) {
                return true;
            }

            previous = current;
        }

        return false;
    }

    EndPoint getEndFlag() {
        return end;
    }

    EndPoint getStartFlag() {
        return start;
    }

    public Point getStart() {
        return start.getPosition();
    }

    public Point getEnd() {
        return end.getPosition();
    }

    public EndPoint getOtherEndPoint(EndPoint flag) {
        if (flag.equals(start)) {
            return end;
        } else if (flag.equals(end)) {
            return start;
        }

        return null;
    }

    void setDriveway() {
        needsCourier = false;
    }

    public boolean isMainRoad() {
        return isMainRoad;
    }

    void registerUsage() {
        if (isMainRoad) {
            return;
        }

        usage = usage + 1;

        if (usage == MAIN_ROAD_THRESHOLD) {
            isMainRoad = true;

            map.reportPromotedRoad(this);
        }
    }

    public Donkey getDonkey() {
        return donkey;
    }

    public boolean needsDonkey() {
        return isMainRoad() && donkey == null && needsCourier;

    }

    Point getOtherPoint(Point position) {
        if (start.getPosition().equals(position)) {
            return end.getPosition();
        } else {
            return start.getPosition();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setMap(GameMap gameMap) {
        map = gameMap;
    }
}
