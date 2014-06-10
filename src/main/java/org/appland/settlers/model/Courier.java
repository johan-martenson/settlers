package org.appland.settlers.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Courier.State.BUSY;
import static org.appland.settlers.model.Courier.State.IDLE;

@Walker(speed = 10)
public class Courier extends Worker {

    private State state;

    enum State {

        IDLE,
        BUSY,
        WALKING_TO_DESTINATION
    }

    private Cargo cargo;
    private Road road;

    private static Logger log = Logger.getLogger(Courier.class.getName());

    public Courier(GameMap gm) {
        log.info("Creating new worker");
        path = null;
        road = null;
        state = IDLE;
        position = null;
        cargo = null;
        map = gm;
    }

    public void setRoad(Road road) {
        log.log(Level.INFO, "Setting road to {0}", road);
        this.road = road;
    }

    public Road getRoad() {
        log.log(Level.FINE, "Getting road {0}", road);
        return road;
    }

    @Override
    public void stepTime() {
        super.stepTime();

        if (cargo != null) {
            cargo.setPosition(getPosition());
        }
    }

    public void putDownCargo() {
        log.log(Level.INFO, "Putting cargo down at position {0}", position);

        log.log(Level.FINER, "Putting {0} at flag {0}", position);

        position.putCargo(cargo);

        log.log(Level.FINER, "Setting position in {0} to {1}", new Object[]{cargo, position});
        cargo.setPosition(position);

        cargo = null;

        state = IDLE;
    }

    public void pickUpCargo(Flag flag) throws InvalidRouteException {
        log.log(Level.INFO, "Picking up cargo from flag {0}", flag);

        this.cargo = flag.retrieveNextCargo();

        log.log(Level.FINE, "Got cargo: {0}", cargo);
        this.position = flag;

        if (flag.equals(road.start)) {
            setTargetFlag(road.end);
        } else {
            setTargetFlag(road.start);
        }

        log.log(Level.FINE, "Target is {0}", target);

        state = BUSY;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void pickUpCargoForRoad(Flag flag, Road r) throws InvalidRouteException {
        for (Cargo c : flag.getStackedCargo()) {
            if (c.getPlannedRoads().get(0).equals(r)) {
                cargo = c;
            }
        }

        position = flag;

        cargo = position.retrieveCargo(cargo);

        if (flag.equals(road.start)) {
            setTargetFlag(road.end);
        } else {
            setTargetFlag(road.start);
        }

        state = BUSY;
    }

    public void deliverToTarget(Building targetBuilding) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        targetBuilding.deliver(this.getCargo());

        this.cargo = null;

        state = IDLE;
    }

    @Override
    public String toString() {
        if (isTraveling()) {
            String str = "Courier at " + getPosition() + "traveling to flag " + getTarget();

            if (targetRoad != null) {
                str += " for " + targetRoad;
            }

            return str;
        }

        if (state == IDLE) {
            return "Idle courier at road (" + getRoad() + ")";
        } else if (state == BUSY) {
            return "Courier at road (" + getRoad() + ") carrying " + cargo;
        } else {
            return "Courier walking to road (" + getTarget() + ")";
        }
    }
}
