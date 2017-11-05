package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flag implements EndPoint, Piece {

    private final static Logger log = Logger.getLogger(Flag.class.getName());

    private final List<Cargo> stackedCargo;

    private Point  position;
    private int    geologistsCalled;
    private int    scoutsCalled;
    private Player player;

    public Flag(Point p) {
        position          = p;
        stackedCargo      = new ArrayList<>();
        geologistsCalled  = 0;
        scoutsCalled      = 0;
    }

    Flag(Player player, Point p) {
        this(p);

        this.player = player;
    }

    public List<Cargo> getStackedCargo() {
        return stackedCargo;
    }

    @Override
    public void putCargo(Cargo c) throws Exception {
        log.log(Level.FINE, "Putting {0} at {1}", new Object[]{c, this});

        c.setPosition(getPosition());
        stackedCargo.add(c);

        /* Give the cargo a chance to re-plan */
        c.rerouteIfNeeded();
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point point) {
        this.position = point;
    }

    @Override
    public String toString() {
        if (stackedCargo.isEmpty()) {
            return "Flag at " + position;
        } else {
            StringBuilder s = new StringBuilder("Flag at " + position + " (stacked cargo:");

            for (Cargo cargo : stackedCargo) {
                s.append(" ").append(cargo.getMaterial().name());
            }

            s.append(")");

            return s.toString();
        }
    }

    @Override
    public boolean hasCargoWaitingForRoad(Road road) {
        return getCargoWaitingForRoad(road) != null;
    }

    @Override
    public Cargo retrieveCargo(Cargo cargo) {

        if (stackedCargo.contains(cargo)) {

            stackedCargo.remove(cargo);

            return cargo;
        }

        return null;
    }

    @Override
    public Cargo getCargoWaitingForRoad(Road road) {

        Cargo waitingCargo = null;
        int priority = Integer.MAX_VALUE;

        for (Cargo cargo : stackedCargo) {
            if (cargo.isDeliveryPromised()) {
                continue;
            }

            if (!road.getEnd().equals(cargo.getNextFlagOrBuilding()) &&
                !road.getStart().equals(cargo.getNextFlagOrBuilding())) {
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

    void geologistSent(Geologist geologist) {
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
}
