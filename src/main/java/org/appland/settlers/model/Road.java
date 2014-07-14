package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

public class Road {

    public Flag start;
    public Flag end;
    private boolean promisedCourier;
    private Courier courier;
    private List<Point> steps;

    public Road(Flag start, Flag end) {
        this.start = start;
        this.end = end;

        promisedCourier = false;
        courier = null;
        
        setSteps(new ArrayList<Point>());
    }

    Road(Flag start, List<Point> wayPoints, Flag end) {
        this.start = start;
        this.end = end;

        promisedCourier = false;
        courier = null;
        
        setSteps(wayPoints);
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

    public Flag[] getFlags() {
        return new Flag[]{start, end};
    }

    public boolean needsCourier() {
        if (promisedCourier || courier != null) {
            return false;
        } else {
            return true;
        }
    }

    public void promiseCourier() throws Exception {
        if (promisedCourier) {
            throw new Exception("Road " + this + " already has a courier promised");
        }

        promisedCourier = true;
    }

    public Courier getCourier() {
        return courier;
    }

    void setCourier(Courier wr) {
        courier = wr;

        promisedCourier = false;
    }

    private void setSteps(List<Point> intermediatePoints) {
        steps = intermediatePoints;
    }

    public List<Point> getWayPoints() {
        return steps;
    }

    protected boolean roadStepsTooLong() {
        Point previous = null;

        for (Point current : steps) {
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

    Flag getEndFlag() {
        return end;
    }

    Flag getStartFlag() {
        return start;
    }

    Point getStart() {
        return start.getPosition();
    }

    Point getEnd() {
        return end.getPosition();
    }
}
