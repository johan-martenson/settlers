package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flag implements EndPoint, Piece {

    private final static Logger log = Logger.getLogger(Flag.class.getName());

    private final List<Cargo> stackedCargo;

    private GameMap map;
    private Point   position;
    private int     geologistsCalled;
    private int     scoutsCalled;

    public Flag(Point p) {
        position          = p;
        stackedCargo      = new ArrayList<>();
        geologistsCalled   = 0;
        scoutsCalled      = 0;
    }

    @Override
    public List<Cargo> getStackedCargo() {
        return stackedCargo;
    }

    @Override
    public void putCargo(Cargo c) {
        log.log(Level.FINE, "Putting {0} at {1}", new Object[]{c, this});

        c.setPosition(getPosition());
        stackedCargo.add(c);
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        this.position = p;
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

    @Override
    public boolean hasCargoWaitingForRoad(Road r) {
        return getCargoWaitingForRoad(r) != null;
    }

    @Override
    public Cargo retrieveCargo(Cargo c) {
        if (stackedCargo.contains(c)) {

            stackedCargo.remove(c);

            return c;
        }

        return null;
    }

    @Override
    public Cargo getCargoWaitingForRoad(Road r) {
        for (Cargo c : stackedCargo) {
            if (c.isDeliveryPromised()) {
                continue;
            }
            
            if (r.getWayPoints().contains(c.getNextStep())) {
                return c;
            }
        }

        return null;
    }

    void setMap(GameMap m) {
        map = m;
    }
    
    public void callGeologist() {
        geologistsCalled++;
    }

    void geologistSent(Geologist g) {
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
}
