package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

public class Road {

    private static final int MAIN_ROAD_THRESHOLD = 99;
    
    private int         usage;
    private EndPoint    start;
    private EndPoint    end;
    private Courier     courier;
    private Donkey      donkey;
    private List<Point> steps;
    private boolean     needsCourier;

    Road(EndPoint start, List<Point> wayPoints, EndPoint end) throws Exception {
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

    @Override
    public String toString() {
        if (courier == null) {
            return "Road " + start + " to " + end + " with no courier";
        } else {
            return "Road " + start + " to " + end + " with courier";
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Road other = (Road) obj;
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
            return false;
        }
        return true;
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
        }

        if (courier != null) {
            throw new Exception("Can't assign courier, there is already a courier assigned.");
        }

        courier = wr;
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

    EndPoint getOtherFlag(EndPoint flag) throws Exception {
        if (flag.equals(start)) {
            return end;
        } else if (flag.equals(end)) {
            return start;
        }

        throw new Exception(flag + " is not an endpoint to this road (" + this + ")");
    }

    void setNeedsCourier(boolean b) {
        needsCourier = b;
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
        if (isMainRoad() && donkey == null) {
            return true;
        }

        return false;
    }

    Point getOtherPoint(Point position) {
        if (start.getPosition().equals(position)) {
            return end.getPosition();
        } else {
            return start.getPosition();
        }
    }
}
