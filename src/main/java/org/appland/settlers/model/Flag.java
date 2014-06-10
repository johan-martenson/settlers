package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flag {

    private Point position;
    private final List<Cargo> stackedCargo;

    private static Logger log = Logger.getLogger(Flag.class.getName());

    public Flag(int x, int y) {
        this(new Point(x, y));
    }

    public Flag(Point p) {
        this.position = p;
        stackedCargo = new ArrayList<>();
    }

    public List<Cargo> getStackedCargo() {
        return stackedCargo;
    }

    public void putCargo(Cargo c) {
        log.log(Level.INFO, "Putting {0} at {1}", new Object[]{c, this});

        c.setPosition(this);
        stackedCargo.add(c);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        this.position = p;
    }

    public Cargo retrieveNextCargo() {
        int size = stackedCargo.size();
        Cargo c = stackedCargo.get(size - 1);
        stackedCargo.remove(size - 1);

        return c;
    }

    @Override
    public String toString() {
        if (stackedCargo.isEmpty()) {
            return "Flag at " + position;
        } else {
            String s = "Flag at " + position + " (stacked cargo:";

            for (Cargo c : stackedCargo) {
                s += " " + c.getMaterial().name();
            }

            s += ")";

            return s;
        }
    }

    public boolean hasCargoWaitingForRoad(Road r) {
        for (Cargo c : stackedCargo) {
            Road nextRoad = c.getPlannedRoads().get(0);

            if (nextRoad.equals(r)) {
                return true;
            }
        }

        return false;
    }

    Cargo retrieveCargo(Cargo c) {
        if (stackedCargo.contains(c)) {

            stackedCargo.remove(c);

            return c;
        }

        return null;
    }
}
