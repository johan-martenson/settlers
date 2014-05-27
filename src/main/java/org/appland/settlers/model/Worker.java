package org.appland.settlers.model;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.appland.settlers.model.Worker.State.BUSY;
import static org.appland.settlers.model.Worker.State.IDLE;

@Walker(speed = 10)
public class Worker implements Actor {
    private State state;

    enum State {
        IDLE,
        BUSY
    }
    
    private GameMap    map;
    private Flag       position;
    private Flag       target;
    private List<Flag> path;
    private int        walkCountdown;
    private Cargo      cargo;
    private Road       road;

    private static Logger log = Logger.getLogger(Worker.class.getName());

    private Worker() {
        log.info("Default Worker constructor");

        path          = null;
        walkCountdown = -1;
        road          = null;
        state         = IDLE;
    }

    public static Worker createWorker(GameMap map) {
        log.info("Creating new worker");
        Worker w = new Worker();
        w.setMap(map);
        return w;
    }

    public void setRoad(Road road) {
        log.log(Level.FINE, "Setting road to {0}", road);
        this.road = road;
    }

    public Road getRoad() {
        log.log(Level.FINE, "Getting road {0}", road);
        return road;
    }

    private void setMap(GameMap map) {
        log.log(Level.FINE, "Setting map to {0}", map);
        this.map = map;
    }

    public void setPosition(Flag point) {
        log.log(Level.FINE, "Setting position to {0}", position);
        this.position = point;
    }

    public void setTarget(Flag target) throws InvalidRouteException {
        log.log(Level.INFO, "Setting target to {0}, previous target was {1}", new Object[]{map, this.target});

        this.target = target;

        this.path = map.findWay(position, target);
        path.remove(0);

        log.log(Level.FINE, "Way to target is {0}", path);
    }

    public Flag getLocation() {
        return this.position;
    }

    @Override
    public void stepTime() {
        log.log(Level.INFO, "Stepping time");

        if (path != null) {
            log.log(Level.FINE, "There is a path set: {0}", path);

            if (walkCountdown == 0) {
                position = path.get(0);

                log.log(Level.FINE, "Reached next stop: {0}", position);

                path.remove(0);
                walkCountdown = getSpeed(this) - 2;

                if (cargo != null) {
                    cargo.setPosition(position);
                }

                if (position == target) {
                    log.log(Level.FINE, "Arrived at target: {0}", target);
                    path = null;
                }

            } else if (walkCountdown == -1) {
                log.log(Level.FINE, "Starting to walk, currently at {0}", position);
                walkCountdown = getSpeed(this) - 2;
            } else {
                log.log(Level.FINE, "Continuing to walk, currently at {0}", position);
                walkCountdown--;
            }
        }
    }

    private int getSpeed(Worker worker) {
        Walker w = worker.getClass().getAnnotation(Walker.class);

        return w.speed();
    }

    public boolean isArrived() {
        log.log(Level.INFO, "Checking if worker has arrived");
        log.log(Level.FINE, "Worker is at {0} and target is {1}", new Object[]{position, target});

        /* A worker can be idle and not at either of the road's flags */
        if (target == null || position == null) {
            return false;
        }

        if (target.equals(position)) {
            log.log(Level.FINE, "Worker has arrived at target {0}", target);
            return true;
        }

        log.log(Level.FINE, "Worker has not arrived at target");
        return false;
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
            setTarget(road.end);
        } else {
            setTarget(road.start);
        }

        log.log(Level.FINE, "Target is {0}", target);
        
        state = BUSY;
    }

    public Flag getTarget() {
        return target;
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
            setTarget(road.end);
        } else {
            setTarget(road.start);
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
        if (state == IDLE) {
            return "Idle worker";
        } else {
            return "Worker carrying " + cargo;
        }
    }
}
