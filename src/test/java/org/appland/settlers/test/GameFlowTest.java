package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Actor;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Military.Rank;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForward;

import static org.appland.settlers.test.Utils.fastForward;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GameFlowTest {

    @Test
    public void gameFlowTest() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {

        List<Actor> actors = new ArrayList<>();

        /* Create starting position */
        GameMap map = GameMap.createGameMap();
        Headquarter hq = Headquarter.createHeadquarter();

        Point startPosition = new Point(6, 6);

        map.placeBuilding(hq, startPosition);
        actors.add(hq);

        hq.setReady();
        
        /* Game loop */
        
        /* Start collection of newly produced goods */
        initiateCollectionOfNewProduce(map);
        
        /* Get idle workers and see if they can pick something up*/
        assignWorkToIdleWorkers(map);

        /* Find out which buildings need deliveries and match with inventory */
        initiateNewDeliveries(hq, map);
        
        /* Deliver for workers who reached their targets */
        deliverForWorkersAtTarget(map);
        
        /* Step time */
        Utils.stepTime(actors);

        /* Player creates woodcutter, sawmill and quarry */
        Building wc = Woodcutter.createWoodcutter();
        Sawmill sm = Sawmill.createSawmill();
        Quarry qry = Quarry.createQuarry();

        actors.add(wc);
        actors.add(sm);
        actors.add(qry);

        Point wcSpot = new Point(6, 8);
        Point smSpot = new Point(8, 6);
        Point qrySpot = new Point(4, 6);
        
        map.placeBuilding(wc, wcSpot);
        map.placeBuilding(sm, smSpot);
        map.placeBuilding(qry, qrySpot);
        map.placeBuilding(hq, startPosition);

        /* Create roads */
        Road r1 = Road.createRoad(hq.getFlag(), wc.getFlag());
        Road r2 = Road.createRoad(hq.getFlag(), sm.getFlag());
        Road r3 = Road.createRoad(hq.getFlag(), qry.getFlag());
        
        map.placeRoad(r1);
        map.placeRoad(r2);
        map.placeRoad(r3);

        /* Assign workers to the roads */
        Courier wr1 = Courier.createWorker(map);
        Courier wr2 = Courier.createWorker(map);
        Courier wr3 = Courier.createWorker(map);

        actors.add(wr1);
        actors.add(wr2);
        actors.add(wr3);

        map.assignWorkerToRoad(wr1, r1);
        map.assignWorkerToRoad(wr2, r2);
        map.assignWorkerToRoad(wr3, r3);

	// TODO: add that workers need to populate the buildings before they start producing
        // TODO: add that workers need to move to roads to populate them
        /* Move forward in time until the small buildings are done */
        // TODO: Change to deliver required material for construction
        Utils.constructSmallHouse(wc);
        Utils.constructSmallHouse(qry);
        Utils.constructMediumHouse(sm);

        assertTrue(wc.getConstructionState() == DONE);
        assertTrue(qry.getConstructionState() == DONE);
        assertTrue(sm.getConstructionState() == DONE);

        /* Fast forward until the woodcutter has cut some wood */
        fastForward(100, actors);

        /* Retrieve cargo from woodcutter and put it on the flag */
        assertTrue(wc.isCargoReady());

        // TODO: test that a building can't produce if there is a cargo ready for pickup
        Cargo c = wc.retrieveCargo();

        c.setTarget(hq, map);

        wc.getFlag().putCargo(c);

        Courier nextWorker = map.getNextWorkerForCargo(c);

        /* Transport cargo one hop */
        nextWorker.pickUpCargo(wc.getFlag());
        assertTrue(nextWorker.getPosition().equals(wc.getFlag()));
        assertTrue(nextWorker.getTarget().equals(hq.getFlag()));

        fastForward(100, actors);

        assertTrue(nextWorker.isArrived());

        nextWorker.putDownCargo();

        /* Cargo has arrived at its target so store it */
        assertTrue(c.isAtTarget());

        hq.deposit(c);

        /* Find out who needs the wood */
        List<Building> buildings = new ArrayList<>();

        buildings.add(wc);
        buildings.add(sm);
        buildings.add(qry);

        List<Building> woodRecipients = Utils.getNeedForMaterial(WOOD, buildings);
        assertTrue(woodRecipients.size() == 1);
        assertTrue(woodRecipients.get(0).equals(sm));

        /* Deliver the wood to the sawmill that needs it */
        Building targetSawmill = woodRecipients.get(0);

        c = hq.retrieve(WOOD);

        c.setTarget(targetSawmill, map);

        hq.getFlag().putCargo(c);

        nextWorker = map.getNextWorkerForCargo(c);

        nextWorker.pickUpCargo(hq.getFlag());

        fastForward(100, actors);

        assertTrue(nextWorker.isArrived());

        /* Cargo has arrived at its target so store it */
        assertTrue(c.isAtTarget());
        assertEquals(c.getTarget(), targetSawmill);
        assertTrue(c.getMaterial() == WOOD);

        c.getTarget().deliver(c);
        assertTrue(targetSawmill.getMaterialInQueue(WOOD) == 1);

        /* Produce plancks in sawmill */
        assertFalse(targetSawmill.isCargoReady());

        fastForward(100, actors);

        assertTrue(targetSawmill.isCargoReady());
        assertTrue(targetSawmill.getMaterialInQueue(WOOD) == 0);

        c = targetSawmill.retrieveCargo();
        assertNotNull(c);
        assertFalse(targetSawmill.isCargoReady());

        targetSawmill.getFlag().putCargo(c);

        /* Transport plancks to nearest storage */
        Storage closestStorage = map.getClosestStorage(targetSawmill);
        assertNotNull(closestStorage);

        c.setTarget(closestStorage, map);

        nextWorker = map.getNextWorkerForCargo(c);

        nextWorker.pickUpCargo(targetSawmill.getFlag());

        fastForward(10, actors);

        assertTrue(c.isAtTarget());
    }

    @Test
    public void gameFlowWithProperGameLoopTest() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        List<Actor> actors = new ArrayList<>();

        /* Create Initial Game Setup */
        GameMap map = GameMap.createGameMap();
        Headquarter hq = Headquarter.createHeadquarter();

        Point startPosition = new Point(6, 6);

        map.placeBuilding(hq, startPosition);
        actors.add(hq);

        hq.setReady();
        
        /* Game loop */
        gameLoop(hq, actors, map);
 
        /* Player creates woodcutter, sawmill and quarry */
        Building wc = Woodcutter.createWoodcutter();
        Sawmill sm = Sawmill.createSawmill();
        Quarry qry = Quarry.createQuarry();

        actors.add(wc);
        actors.add(sm);
        actors.add(qry);

        Point wcSpot = new Point(6, 8);
        Point smSpot = new Point(8, 6);
        Point qrySpot = new Point(4, 6);
        
        map.placeBuilding(wc, wcSpot);
        map.placeBuilding(sm, smSpot);
        map.placeBuilding(qry, qrySpot);
        map.placeBuilding(hq, startPosition);

        /* Create roads */
        map.placeRoad(hq.getFlag(), wc.getFlag());
        map.placeRoad(hq.getFlag(), sm.getFlag());
        map.placeRoad(hq.getFlag(), qry.getFlag());

        /* Assign workers to the roads */
        Courier wr1 = Courier.createWorker(map);
        Courier wr2 = Courier.createWorker(map);
        Courier wr3 = Courier.createWorker(map);

        actors.add(wr1);
        actors.add(wr2);
        actors.add(wr3);

        Road r = map.getRoad(hq.getFlag(), wc.getFlag());
        assertNotNull(r);
        map.assignWorkerToRoad(wr1, r);
        
        r = map.getRoad(hq.getFlag(), sm.getFlag());
        assertNotNull(r);
        map.assignWorkerToRoad(wr2, r);
        
        r = map.getRoad(hq.getFlag(), qry.getFlag());
        assertNotNull(r);
        map.assignWorkerToRoad(wr3, r);

	// TODO: add that workers need to populate the buildings before they start producing
        // TODO: add that workers need to move to roads to populate them
        
        /* Buildings, roads, workers done - test that construction happens */
        assertTrue(wc.getConstructionState() == UNDER_CONSTRUCTION);
        assertTrue(qry.getConstructionState() == UNDER_CONSTRUCTION);
        assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);

        /* Add more plancks and stone to the HQ inventory */
        Utils.fillUpInventory(hq, PLANCK, 10);
        Utils.fillUpInventory(hq, STONE, 10);
        
        /* -- Assert that all workers are idle */
        Collection<Courier> workers = map.getCourierAssignedToRoads();
        
        for (Courier w : workers) {
            assertNull(w.getCargo());
            assertNull(w.getTarget());
        }
        
        /* Gameloop */
        gameLoop(hq, actors, map);
        
        List<Cargo> hqOutCargos = hq.getFlag().getStackedCargo();
        assertTrue(hqOutCargos.size() == 1);
        Cargo c = hqOutCargos.get(0);
        assertTrue(c.getMaterial() == PLANCK || c.getMaterial() == STONE);
        assertFalse(c.getTarget().equals(hq));

        
        /* Gameloop */
        fastForward(10, actors);
        gameLoop(hq, actors, map);

        
        /* -- Assert that delivery is started for one cargo */
        workers = map.getCourierAssignedToRoads();
        
        int busyWorkers = 0;
        for (Courier w : workers) {
            if (w.getCargo() != null) {
                busyWorkers++;
            }
        }
        

        assertTrue(busyWorkers == 1);
        
        /* Gameloop */
        fastForward(10, actors);
        gameLoop(hq, actors, map);

        /* Ensure first cargo is delivered to house */
        /* Ensure next cargo is started */
        
        
        
        
        
        
        
        
        
    }

    @Test
    public void testInitiateCollectionOfNewProduce() throws InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidRouteException {
        GameMap map    = GameMap.createGameMap();
        Woodcutter wc  = Woodcutter.createWoodcutter();
        Storage stg    = Storage.createStorage();
        Point stgPoint = new Point(1, 1);
        Point wcPoint  = new Point(2, 2);
        Road r;
        Courier w       = Courier.createWorker(map);
        
        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(stg, stgPoint);
        
        r = Road.createRoad(stg.getFlag(), wc.getFlag());
        
        map.placeRoad(r);
        map.assignWorkerToRoad(w, r);
        
        Utils.constructSmallHouse(wc);
        
        /* Fast forward until the woodcutter has produced a cargo with WOOD */
        fastForward(100, wc, w);
        assertTrue(wc.isCargoReady());
        assertNull(w.getCargo());
        
        /* Verify that the worker doesn't pick up the cargo by himself as time passes */
        fastForward(100, wc, w);
        assertTrue(wc.isCargoReady());
        assertNull(w.getCargo());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Verify that initiateCollectionOfNewProduce gets the worker to pick up the cargo */
        initiateCollectionOfNewProduce(map);
        assertFalse(wc.isCargoReady());
        assertNull(w.getCargo());
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        
        Cargo c = wc.getFlag().getStackedCargo().get(0);
        assertTrue(c.getTarget().equals(stg));
        assertTrue(c.getMaterial() == WOOD);
    }
    
    @Test
    public void testInitiateNewDeliveries() throws InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidEndPointException {
        GameMap map = GameMap.createGameMap();
        
        Headquarter hq = Headquarter.createHeadquarter();
        Woodcutter wc = Woodcutter.createWoodcutter();
        
        hq.setReady();
        
        Point hqPoint = new Point(1, 1);
        Point wcPoint = new Point(2, 2);
        
        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(wc, wcPoint);
        
        map.placeRoad(hq.getFlag(), wc.getFlag());
        
        Utils.constructSmallHouse(wc);
        
        /* Since the woodcutter is finished it does not need any deliveries
         * Verify that no new deliveries are initiated
        */
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        initiateNewDeliveries(hq, map);
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        
        /* Place an unfinished sawmill on the map and verify that it needs deliveries */
        Sawmill sm = Sawmill.createSawmill();
        
        Point smPoint = new Point(1, 2);
        
        map.placeBuilding(sm, smPoint);
        
        map.placeRoad(hq.getFlag(), sm.getFlag());
        
        /* Verify that a new delivery is initiated for the sawmill */
        assertTrue(sm.needsMaterial(PLANCK));
        assertTrue(sm.needsMaterial(STONE));
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        
        initiateNewDeliveries(hq, map);
        assertFalse(hq.getFlag().getStackedCargo().isEmpty());
    }
    
    @Test
    public void testAssignWorkToIdleWorkers() throws InvalidEndPointException, InvalidRouteException {
        GameMap map   = GameMap.createGameMap();
        Sawmill sm    = Sawmill.createSawmill();
        Point smPoint = new Point(1, 1);
        Flag f        = new Flag(2, 2);
        Road r        = Road.createRoad(f, sm.getFlag());
        Courier w      = Courier.createWorker(map);
        
        map.placeFlag(f);
        map.placeBuilding(sm, smPoint);
        map.placeRoad(r);
        map.assignWorkerToRoad(w, r);
        
        Cargo c = Cargo.createCargo(PLANCK);
        
        c.setPosition(f);
        c.setTarget(sm, map);
        f.putCargo(c);
        
        /* Verify that the worker is idle */
        assertNull(w.getCargo());
        assertNull(w.getTarget());
        
        Utils.fastForward(100, w);
        assertNull(w.getCargo());
        assertNull(w.getTarget());
        
        /* Verify that the worker picks up the cargo and has the sawmill as target */
        assignWorkToIdleWorkers(map);
        assertNotNull(w.getCargo());
        
        Cargo tmp = w.getCargo();
        assertEquals(c, tmp);
        assertEquals(w.getTarget(), sm.getFlag());
    }

    @Test
    public void testDeliverForWorkersAtTarget() throws InvalidEndPointException, InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        GameMap map   = GameMap.createGameMap();
        Woodcutter wc = Woodcutter.createWoodcutter();
        Flag src      = new Flag(1, 1);
        Point wcPoint = new Point(2, 2);
        Courier w      = Courier.createWorker(map);
        Cargo c;
        
        map.placeFlag(src);
        map.placeBuilding(wc, wcPoint);        
        
        Road r = Road.createRoad(src, wc.getFlag());
        
        map.placeRoad(r);
        
        map.assignWorkerToRoad(w, r);
        
        c = Cargo.createCargo(PLANCK);
        
        src.putCargo(c);
        c.setTarget(wc, map);
        
        w.pickUpCargoForRoad(src, r);
        
        /* Move worker to the sawmill */
        fastForward(10, w, wc);
        assertTrue(w.getPosition().equals(wc.getFlag()));
        
        /* Verify that fast forwarding does not get the worker to deliver the cargo */
        fastForward(100, w, wc);
        assertEquals(w.getCargo(), c);
        assertTrue(w.getPosition().equals(wc.getFlag()));
        assertTrue(wc.getMaterialInQueue(PLANCK) == 0);
        
        /* Verify that deliverForWorkersAtTarget gets the worker to deliver the cargo */
        deliverForWorkersAtTarget(map);
        
        assertNull(w.getCargo());
        assertTrue(wc.getMaterialInQueue(PLANCK) == 1);
    }
    
    @Test
    public void testAssignNewWorkerToUnoccupiedPlaces() throws Exception {
        GameMap map = new GameMap();
        
        Headquarter hq = new Headquarter();
        Barracks bk = new Barracks();
        
        Point hqPoint = new Point(1, 1);
        Point wcPoint = new Point(2, 2);
        
        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(bk, wcPoint);
        
        Road r = new Road(hq.getFlag(), bk.getFlag());
        
        map.placeRoad(r);
        
        assertTrue(map.getAllWorkers().isEmpty());

        /* Prep the headquarter with militaryies */
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        
        /* Assign new workers to unoccupied places and verify that there is a 
         * worker designated for the road. There should be no worker for the 
         * barracks as it's not finished yet
        */
        assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 1);
        Worker w1 = map.getAllWorkers().get(0);

        assertTrue(w1.getPosition().equals(hq.getFlag()));
        assertTrue(w1.getTargetRoad().equals(r));

        /* Fast forward to let the courier reach its road */
        fastForward(100, w1);
        
        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getTravelingWorkers().get(0).equals(w1));
        assertTrue(w1.isArrived());
        assertTrue(w1.isTraveling());
        
        /* Assign arrived workers to tasks */
        assertTrue(map.getTravelingWorkers().contains(w1));
        assertTrue(w1.getTargetRoad().equals(r));

        assignTravelingWorkersThatHaveArrived(map);
        
        assertFalse(w1.isTraveling());
        assertTrue(map.getRoadsWithoutWorker().isEmpty());
        assertEquals(map.getRoad(hq.getFlag(), bk.getFlag()), r);
        assertNotNull(r.getCourier().getRoad());
        
        assertTrue(r.getCourier().equals(w1));
        assertTrue(r.getCourier().getRoad().equals(r));
        
        /* Finish the construction of the barracks and assign new workers to 
         * unoccupied places and verify that there is a military assigned
         * to occupy the barracks
        */
        Utils.constructSmallHouse(bk);
        
        assertTrue(map.getAllWorkers().size() == 1);
        assertTrue(bk.isMilitaryBuilding());
        assertTrue(bk.needMilitaryManning());
        assertTrue(bk.getHostedMilitary() == 0);
        assertTrue(hq.getInventory().get(Material.PRIVATE) == 3);
        assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(map.getAllWorkers().get(1) instanceof Military);
        assertFalse(map.getAllWorkers().get(1).isArrived());
        assertTrue(map.getAllWorkers().get(1).isTraveling());
        System.out.println(map.getTravelingWorkers());
        assertTrue(map.getTravelingWorkers().size() == 1);
        
        assertTrue(hq.getInventory().get(Material.PRIVATE) == 2);
        
        /* Let the military reach the barracks */
        Utils.fastForward(100, map);
        
        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(map.getAllWorkers().get(1).isArrived());
        assertTrue(map.getAllWorkers().get(1).isTraveling());
        
        /* Make traveling workers that have arrived enter their building or road */
        assignTravelingWorkersThatHaveArrived(map);

        assertTrue(map.getTravelingWorkers().isEmpty());
        assertTrue(bk.needMilitaryManning());
        assertTrue(bk.getHostedMilitary() == 1);
        
        /* Assign new workers again to see that a second military is dispatched
         * for the barracks
        */
        assignNewWorkerToUnoccupiedPlaces(map);
        
        assertTrue(map.getAllWorkers().size() == 3);
        assertTrue(map.getAllWorkers().get(2) instanceof Military);
        assertFalse(map.getAllWorkers().get(2).isArrived());
        assertTrue(map.getAllWorkers().get(2).isTraveling());
        assertTrue(map.getTravelingWorkers().size() == 1);
        
        assertFalse(bk.needMilitaryManning());
        
        assertTrue(hq.getInventory().get(Material.PRIVATE) == 1);

        /* Let the military reach the barracks */
        Utils.fastForward(100, map);

        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getAllWorkers().size() == 3);
        assertTrue(map.getAllWorkers().get(2).isArrived());
        assertTrue(map.getAllWorkers().get(2).isTraveling());
        assertTrue(bk.getHostedMilitary() == 1);
        
        /* Make traveling workers that have arrived at their assigned
         * buildings or roads enter
        */
        assignTravelingWorkersThatHaveArrived(map);
        
        assertTrue(bk.getHostedMilitary() == 2);
        assertTrue(map.getTravelingWorkers().isEmpty());
    }
    
    private void gameLoop(Storage hq, List<Actor> actors, GameMap map) throws InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
       /* Assign workers to unoccupied streets and buildings, and military to 
        * unoccupied military buildings
        */
        assignNewWorkerToUnoccupiedPlaces(map);
        
        /* Assign traveling workers who reached their target to their task */
        assignTravelingWorkersThatHaveArrived(map);
        
       /* Start collection of newly produced goods */
        initiateCollectionOfNewProduce(map);
        
        /* Get idle workers and see if they can pick something up*/
        assignWorkToIdleWorkers(map);

        /* Find out which buildings need deliveries and match with inventory */
        initiateNewDeliveries(hq, map);
        
        /* Deliver for workers who reached their targets */
        deliverForWorkersAtTarget(map);
        
        /* Step time */
        Utils.stepTime(actors);        
    }
    
    private void assignTravelingWorkersThatHaveArrived(GameMap map) {
        List<Worker> travelingWorkers = map.getTravelingWorkers();
        
        for (Worker w : travelingWorkers) {
            if (w.isArrived()) {
                
                /* Handle couriers separately */
                if (w.getTargetRoad() != null) {
                    map.assignWorkerToRoad((Courier)w, w.getTargetRoad());
                    w.stopTraveling();
                } else {
                    Flag targetFlag   = w.getTarget();
                    Building building = map.getBuildingByFlag(targetFlag);
                    
                    if (building.isMilitaryBuilding()) {
                        building.hostMilitary((Military)w);
                        w.stopTraveling();
                    } else {
                        building.assignWorker(w);
                        w.stopTraveling();
                    }
                }
                
            }
        }
    }
    
    private void assignNewWorkerToUnoccupiedPlaces(GameMap map) throws Exception {
        /* Handle unoccupied roads */
        List<Road> roads = map.getRoadsWithoutWorker();
        
        for (Road r : roads) {
            Storage stg = map.getClosestStorage(r);
            
            Courier w = stg.retrieveCourier();
            
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
                    
                    Military m = stg.retrieveMilitary();

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
                    
                    w.setTargetBuilding(b);

                    map.placeWorker(w, stg.getFlag());
                    
                    b.promiseWorker();
                }
            }
        }
    }
    
    /*
     * Finds all houses that needs a delivery and picks out a cargo from the HQ.
     * The cargo gets the house as its target and is put at the HQ's flag
    */
    private void initiateNewDeliveries(Storage hq, GameMap map) throws InvalidRouteException {
        Map<Material, Integer> inventory = hq.getInventory();
        Building targetBuilding = null;
        Material materialToDeliver = WOOD;
        
        for (Material m : inventory.keySet()) {
            assertNotNull(m);
            
            for (Building b : map.getBuildings()) {
                assertNotNull(b);
                
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

    private void assignWorkToIdleWorkers(GameMap map) throws InvalidRouteException {
        List<Courier> idleWorkers = map.getIdleWorkers();
        
        for (Courier w : idleWorkers) {
            assertNull(w.getCargo());
            
            Road r = w.getRoad();
            
            Flag[] flags = r.getFlags();
            assertTrue(flags.length == 2);
            assertNotNull(flags[0]);
            assertNotNull(flags[1]);
            
            if (flags[0].hasCargoWaitingForRoad(r)) {
                w.pickUpCargoForRoad(flags[0], r);
                assertTrue(w.getPosition().equals(flags[0]));
                assertTrue(w.getTarget().equals(w.getCargo().getTarget().getFlag()));
            } else if (flags[1].hasCargoWaitingForRoad(r)) {
                w.pickUpCargoForRoad(flags[1], r);
                assertTrue(w.getPosition().equals(flags[1]));
                assertTrue(w.getTarget().equals(w.getCargo().getTarget().getFlag()));
            }
        }
    }

    private void deliverForWorkersAtTarget(GameMap map) throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
        List<Courier> workersAtTarget = map.getWorkersAtTarget();
        
        for (Courier w : workersAtTarget) {
            assertTrue(w.isArrived());
            
            Cargo c = w.getCargo();
            Building targetBuilding = c.getTarget();
            
            w.deliverToTarget(targetBuilding);
        }
    }
    
    private void initiateCollectionOfNewProduce(GameMap map) throws InvalidRouteException {
        for (Building b : map.getBuildingsWithNewProduce()) {
            assertTrue(b.isCargoReady());
            
            Cargo c = b.retrieveCargo();
            Storage stg = map.getClosestStorage(b);
            
            c.setTarget(stg, map);
            assertTrue(c.getTarget().equals(stg));
            
            b.getFlag().putCargo(c);
            assertTrue(b.getFlag().getStackedCargo().contains(c));
        }
    }
}
