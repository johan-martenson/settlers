package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

public class Road {

    private static final int MAIN_ROAD_THRESHOLD = 99;

    private int         usage;
    private final EndPoint    start;
    private final EndPoint    end;
    private Courier     courier;
    private Donkey      donkey;
    private final List<Point> steps;
    private boolean     needsCourier;
    private Player      player;

    protected Road(EndPoint start, List<Point> wayPoints, EndPoint end) throws Exception {
        if (roadStepsTooLong(wayPoints)) {
            throw new Exception("The steps are too long in " + wayPoints);
        }

        this.start = start;
        this.end = end;

        courier = null;
        donkey  = null;

        steps = new ArrayList<>();
        steps.addAll(wayPoints);

        needsCourier = true;

        usage = 0;
    }

    protected Road(Player player, EndPoint startFlag, List<Point> wayPoints, EndPoint endFlag) throws Exception {
        this(startFlag, wayPoints, endFlag);

        this.player = player;
    }

    @Override
    public String toString() {
        if (courier == null) {
            return "Road " + start + " to " + end + " with no courier";
        } else {
            return "Road " + start + " to " + end + " with courier";
        }
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

    void setCourier(Courier wr) throws Exception {
        if (wr instanceof Donkey) {

            if (donkey != null) {
                throw new Exception("Can't assign donkey, there is already a donkey assigned.");
            }

            donkey = (Donkey) wr;
        } else {
            if (courier != null) {
                throw new Exception("Can't assign courier, there is already a courier assigned.");
            }

            courier = wr;
        }
    }

    public List<Point> getWayPoints() {
        return steps;
    }

    private boolean roadStepsTooLong(List<Point> wayPoints) {
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

    public EndPoint getOtherFlag(EndPoint flag) throws Exception {
        if (flag.equals(start)) {
            return end;
        } else if (flag.equals(end)) {
            return start;
        }

        throw new Exception(flag + " is not an endpoint to this road (" + this + ")");
    }

    void setNeedsCourier(boolean needsCourier) {
        this.needsCourier = needsCourier;
    }

    public boolean isMainRoad() {
        return usage > MAIN_ROAD_THRESHOLD;
    }

    void registerUsage() {
        if (usage <= MAIN_ROAD_THRESHOLD) {
            usage++;
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

    void setPlayer(Player player) {
        this.player = player;
    }

}
