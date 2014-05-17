package org.appland.settlers.test;

import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.test.Utils.fastForward;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.appland.settlers.model.Actor;

import org.junit.Test;

public class GameFlowTest {

	@Test
	public void gameFlowTest() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException {
		
	    List<Actor> actors = new ArrayList<Actor>();

		/* Create starting position */
		GameMap map = GameMap.createGameMap();
		Storage hq = Headquarter.createHeadquarter();

		Point startPosition = new Point(6, 6);
		
		map.placeBuilding(hq, startPosition);
		
		/* Create Woodcutter, sawmill and quarry */
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
		
		/* Create roads */
		map.placeRoad(startPosition, wcSpot);
		map.placeRoad(startPosition, smSpot);
		map.placeRoad(startPosition, qrySpot);
		
		/* Assign workers to the roads */
		Worker wr1 = Worker.createWorker(map);
		Worker wr2 = Worker.createWorker(map);
		Worker wr3 = Worker.createWorker(map);
		
		actors.add(wr1);
		actors.add(wr2);
		actors.add(wr3);

		map.assignWorkerToRoad(wr1, map.getRoad(startPosition, wcSpot));
		map.assignWorkerToRoad(wr2, map.getRoad(startPosition, smSpot));
		map.assignWorkerToRoad(wr3, map.getRoad(startPosition, qrySpot));
		
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
		assertTrue(wc.outputAvailable());
		
		// TODO: test that a building can't produce if there is a cargo ready for pickup
		
		Cargo c = wc.retrieveCargo();
		
		c.setTarget(hq, map);
		
		wc.getFlag().putCargo(c);
		
		Worker nextWorker = map.getNextWorkerForCargo(c);
		
		/* Transport cargo one hop */
		nextWorker.pickUpCargo(wc.getFlag());
		assertTrue(nextWorker.getLocation().equals(wc.getFlag().getPosition()));
		assertTrue(nextWorker.getTarget().equals(hq.getFlag().getPosition()));
		
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
                System.out.println(woodRecipients);
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
		assertTrue(targetSawmill.getQueue(WOOD) == 1);

		/* Produce plancks in sawmill */
		assertFalse(targetSawmill.outputAvailable());

		fastForward(100, actors);

		assertTrue(targetSawmill.outputAvailable());
		assertTrue(targetSawmill.getQueue(WOOD) == 0);
		
		c = targetSawmill.retrieveCargo();
		assertNotNull(c);
		assertFalse(targetSawmill.outputAvailable());

		targetSawmill.getFlag().putCargo(c);

		/* Transport plancks to nearest storage */
		Storage closestStorage = map.getClosestStorage();
		assertNotNull(closestStorage);

		c.setTarget(closestStorage, map);
	       
		nextWorker = map.getNextWorkerForCargo(c);

		nextWorker.pickUpCargo(targetSawmill.getFlag());

		fastForward(10, actors);

		assertTrue(c.isAtTarget());
	}
}
