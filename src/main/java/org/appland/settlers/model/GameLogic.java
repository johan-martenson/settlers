/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.List;

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
    }

    public void assignNewWorkerToUnoccupiedPlaces(GameMap map) throws Exception {
        /* Handle unoccupied roads */
        List<Road> roads = map.getRoadsThatNeedCouriers();
        
        for (Road r : roads) {
            Storage stg = map.getClosestStorage(r.getStart());

            if (stg == null) {
                continue;
            }
            
            Courier w = stg.retrieveCourier();

            w.setMap(map);

            map.placeWorker(w, stg.getFlag());

            w.assignToRoad(r);
        }

        /* Handle unoccupied regular buildings and military buildings*/
        List<Building> buildings = map.getBuildings();

        for (Building b : buildings) {
            if (b.isMilitaryBuilding()) {
                if (b.needMilitaryManning()) {
                    Storage stg = map.getClosestStorage(b.getPosition());

                    if (!stg.hasMilitary()) {
                        continue;
                    }
                    
                    Military m = stg.retrieveAnyMilitary();

                    m.setMap(map);

                    map.placeWorker(m, stg.getFlag());
                    
                    m.setTargetBuilding(b);

                    b.promiseMilitary(m);
                }
            } else {
                if (b.needsWorker()) {
                    Material m = b.getWorkerType();

                    Storage stg = map.getClosestStorage(b.getPosition(), b);

                    Worker w = stg.retrieveWorker(m);

                    w.setMap(map);

                    map.placeWorker(w, stg.getFlag());

                    w.setTargetBuilding(b);
                    
                    b.promiseWorker(w);
                }
            }
        }
    }
}
