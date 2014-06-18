/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.List;
import static org.appland.settlers.model.Material.WOOD;

/**
 *
 * @author johan
 */
public class GameLogic {
    public void gameLoop(GameMap map) throws InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {

        // TODO: Ensure that performing the steps in order doesn't mean that things happen too fast (eg put cargo on flag, worker picks up cargo etc)
        /* Assign workers to unoccupied streets and buildings, and military to 
         * unoccupied military buildings
         */
        assignNewWorkerToUnoccupiedPlaces(map);

        /* Get idle workers and see if they can pick something up*/
        assignWorkToIdleCouriers(map);

        /* Assign traveling workers who reached their target to their task */
        assignTravelingWorkersThatHaveArrived(map);

        /* Start collection of newly produced goods */
        initiateCollectionOfNewProduce(map);

        /* Find out which buildings need deliveries and match with inventory */
        initiateNewDeliveriesForAllStorages(map);

        /* Deliver for workers who reached their targets */
        deliverForWorkersAtTarget(map);

        /* Step time */
        map.stepTime();
    }

    public void assignTravelingWorkersThatHaveArrived(GameMap map) throws Exception {
        List<Worker> travelingWorkers = map.getTravelingWorkers();

        for (Worker w : travelingWorkers) {
            if (w.isArrived()) {

                /* Handle couriers separately */
                if (w.getTargetRoad() != null) {
                    map.assignWorkerToRoad((Courier) w, w.getTargetRoad());
                    w.stopTraveling();
                } else if (w.getTargetBuilding() != null) {
                    Flag targetFlag = w.getTarget();
                    Building building = map.getBuildingByFlag(targetFlag);

                    if (building.isMilitaryBuilding()) {
                        building.hostMilitary((Military) w);
                        w.stopTraveling();
                    } else {
                        building.assignWorker(w);
                        w.stopTraveling();
                    }
                }

            }
        }
    }

    public void assignNewWorkerToUnoccupiedPlaces(GameMap map) throws Exception {
        /* Handle unoccupied roads */
        List<Road> roads = map.getRoadsWithoutWorker();

        for (Road r : roads) {
            Storage stg = map.getClosestStorage(r);

            Courier w = stg.retrieveCourier();

            w.setMap(map);

            w.setTargetRoad(r);

            map.placeWorker(w, stg.getFlag());

            r.promiseCourier();
        }

        /* Handle unoccupied regular buildings and military buildings*/
        List<Building> buildings = map.getBuildings();

        for (Building b : buildings) {
            if (b.isMilitaryBuilding()) {
                if (b.needMilitaryManning()) {
                    Storage stg = map.getClosestStorage(b);

                    Military m = stg.retrieveAnyMilitary();

                    m.setMap(map);
                    m.setTargetBuilding(b);

                    map.placeWorker(m, stg.getFlag());

                    b.promiseMilitary(m);
                }
            } else {
                if (b.needsWorker()) {
                    Material m = b.getWorkerType();

                    Storage stg = map.getClosestStorage(b);

                    Worker w = stg.retrieveWorker(m);

                    w.setMap(map);
                    w.setTargetBuilding(b);

                    map.placeWorker(w, stg.getFlag());

                    b.promiseWorker(w);
                }
            }
        }
    }

    public void initiateNewDeliveriesForAllStorages(GameMap map) throws InvalidRouteException, Exception {
        List<Storage> storages = map.getStorages();

        for (Storage s : storages) {
            initiateNewDeliveriesForStorage(s, map);
        }
    }

    /*
     * Finds all houses that needs a delivery and picks out a cargo from the storage.
     * The cargo gets the house as its target and is put at the storage's flag
     */
    public void initiateNewDeliveriesForStorage(Storage hq, GameMap map) throws InvalidRouteException, Exception {
        Building targetBuilding = null;
        Material materialToDeliver = WOOD;

        for (Material m : Material.values()) {
            
            for (Building b : map.getBuildingsWithinReach(hq.getFlag())) {

                if (b.needsMaterial(m) && hq.isInStock(m)) {
                    targetBuilding = b;
                    materialToDeliver = m;

                    break;
                }
            }

            /* Start delivery */
            if (targetBuilding != null) {
                targetBuilding.promiseDelivery(materialToDeliver);
                Cargo c = hq.retrieve(materialToDeliver);
                c.setTarget(targetBuilding, map);
                hq.getFlag().putCargo(c);

                break;
            }
        }
    }

    public void assignWorkToIdleCouriers(GameMap map) throws InvalidRouteException {
        List<Courier> idleWorkers = map.getIdleWorkers();

        for (Courier w : idleWorkers) {
            Road r = w.getRoad();

            Flag[] flags = r.getFlags();

            if (flags[0].hasCargoWaitingForRoad(r)) {
                w.pickUpCargoForRoad(flags[0], r);
            } else if (flags[1].hasCargoWaitingForRoad(r)) {
                w.pickUpCargoForRoad(flags[1], r);
            }
        }
    }

    public void deliverForWorkersAtTarget(GameMap map) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        List<Courier> workersAtTarget = map.getWorkersAtTarget();

        for (Courier w : workersAtTarget) {

            Cargo c = w.getCargo();
            
            if (c == null) {
                continue;
            }
            
            if (c.isAtTarget()) {
                Building targetBuilding = c.getTarget();

                w.deliverToTarget(targetBuilding);
            } else {
                w.putDownCargo();
            }
        }
    }

    public void initiateCollectionOfNewProduce(GameMap map) throws InvalidRouteException {
        for (Building b : map.getBuildingsWithNewProduce()) {

            Cargo c = b.retrieveCargo();
            Storage stg = map.getClosestStorage(b);

            c.setTarget(stg, map);

            b.getFlag().putCargo(c);
        }
    }
}
