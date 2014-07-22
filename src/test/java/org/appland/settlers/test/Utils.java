package org.appland.settlers.test;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.appland.settlers.model.Actor;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState;
import static org.appland.settlers.model.Building.ConstructionState.*;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;

import static org.appland.settlers.model.Material.*;

import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

public class Utils {

    private static Logger log = Logger.getLogger(Courier.class.getName());

    public static void fastForward(int time, Actor b) {
        int i = 0;
        for (i = 0; i < time; i++) {
            b.stepTime();
        }
    }

    public static void fastForward(int time, Actor... b) {
        fastForward(time, Arrays.asList(b));
    }

    public static void fastForward(int time, List<Actor> actors) {
        for (Actor a : actors) {
            fastForward(time, a);
        }
    }

    public static void fastForward(int time, GameMap map) {
        int i;
        for (i = 0; i < time; i++) {
            map.stepTime();
        }
    }

    public static void assertConstructionStateDuringFastForward(int time, Building b, ConstructionState state) {
        int i;
        for (i = 0; i < time; i++) {
            assertTrue(b.getConstructionState() == state);
            b.stepTime();
        }
    }

    public static List<Building> getNeedForMaterial(Material wood, List<Building> buildings) {
        log.log(Level.INFO, "Finding buildings requiring {0} in list {1}", new Object[]{wood, buildings});

        List<Building> result = new ArrayList<>();

        for (Building b : buildings) {
            if (b.needsMaterial(wood)) {
                log.log(Level.FINE, "Building {0} requires {1}", new Object[]{b, wood});
                result.add(b);
            }
        }

        return result;
    }

    static void constructMediumHouse(Building sm) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);

        Cargo woodCargo = new Cargo(PLANCK);
        Cargo stoneCargo = new Cargo(STONE);

        /* Deliver 4 wood and 3 stone */
        sm.promiseDelivery(PLANCK);
        sm.promiseDelivery(PLANCK);
        sm.promiseDelivery(PLANCK);
        sm.promiseDelivery(PLANCK);
        sm.deliver(woodCargo);
        sm.deliver(woodCargo);
        sm.deliver(woodCargo);
        sm.deliver(woodCargo);

        sm.promiseDelivery(STONE);
        sm.promiseDelivery(STONE);
        sm.promiseDelivery(STONE);
        sm.deliver(stoneCargo);
        sm.deliver(stoneCargo);
        sm.deliver(stoneCargo);

        Utils.fastForward(150, sm);
    }

    static void constructSmallHouse(Building house) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        assertTrue(house.getConstructionState() == UNDER_CONSTRUCTION);

        Cargo woodCargo = new Cargo(PLANCK);
        Cargo stoneCargo = new Cargo(STONE);

        /* Deliver 2 wood and 2 stone */
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.deliver(woodCargo);
        house.deliver(woodCargo);

        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.deliver(stoneCargo);
        house.deliver(stoneCargo);

        Utils.fastForward(100, house);
    }

    static boolean roadStartStopIsCorrect(Road r, Point p1, Point p2) {
        if (r.getStart().equals(p1) && r.getEnd().equals(p2)) {
            return true;
        } else if (r.getStart().equals(p2) && r.getEnd().equals(p1)) {
            return true;
        }

        return false;
    }

    static void stepTime(List<Actor> actors) {

        for (Actor a : actors) {
            a.stepTime();
        }
    }

    static Map<Building, Material> getNeedForDelivery(List<Building> buildings) {

        Map<Building, Material> result = new HashMap<>();

        for (Building b : buildings) {
            Map<Material, Integer> neededMaterial = b.getRequiredGoodsForProduction();

            for (Material m : neededMaterial.keySet()) {
                result.put(b, m);
            }
        }

        return result;
    }

    static void fillUpInventory(Storage hq, Material material, int amount) {
        Cargo c = new Cargo(material);

        int i;
        for (i = 0; i < amount; i++) {
            hq.deliver(c);
        }
    }

    static boolean materialIntMapIsEmpty(Map<Material, Integer> inQueue) {
        boolean isEmpty = true;

        for (Material m : Material.values()) {
            if (inQueue.get(m) != 0) {
                isEmpty = false;
            }
        }

        return isEmpty;
    }

    static void constructLargeHouse(Storage house) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        assertTrue(house.getConstructionState() == UNDER_CONSTRUCTION);

        Cargo planckCargo = new Cargo(PLANCK);
        Cargo stoneCargo = new Cargo(STONE);

        /* Deliver 4 wood and 3 stone */
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.deliver(planckCargo);
        house.deliver(planckCargo);
        house.deliver(planckCargo);
        house.deliver(planckCargo);

        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.deliver(stoneCargo);
        house.deliver(stoneCargo);
        house.deliver(stoneCargo);
        house.deliver(stoneCargo);
    }

    static void assertNoStepDirectlyUpwards(List<Point> route) {
        Point previous = null;
        
        for (Point iter : route) {
            if (previous == null) {
                previous = iter;
                continue;
            }

            assertFalse(iter.x == previous.x && abs(iter.y - previous.y) == 2);
            
            previous = iter;
        }
    }

    static void fastForwardUntilWorkersReachTarget(GameMap map, Worker... couriers) {
        for (Worker c : couriers) {
            assertTrue(c.isTraveling());
        }
            
        for (int i = 0; i < 1000; i++) {
            boolean allDone = true;

            for (Worker c : couriers) {
                if (!c.isArrived()) {
                    allDone = false;
                }
            }

            if (allDone) {
                break;
            }
            
            map.stepTime();
        }
    }
}
