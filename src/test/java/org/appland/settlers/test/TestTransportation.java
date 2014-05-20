package org.appland.settlers.test;

import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.List;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

public class TestTransportation {

	
	@Test
	public void testCreateCargo() {
		for (Material m : Material.values()) {
			Cargo c = Cargo.createCargo(m);
			
			assertNotNull(c);
			
			assertTrue(c.getMaterial() == m);
		}
	}
	
	@Test
	public void testCreateRoad() throws InvalidEndPointException {
		GameMap map = GameMap.createGameMap();
		
		Storage hq = Storage.createStorage();
		Building wc = Woodcutter.createWoodcutter();
		
		map.placeBuilding(hq, new Point(1, 2));
		map.placeBuilding(wc, new Point(3, 4));
		
		map.placeRoad(new Point(1, 2), new Point(3, 4));
		
		List<Road> roads = map.getRoads();
		
		assertTrue(1 == roads.size());
		
		Road r = roads.get(0);
		
		assertTrue(r.start.x == 1);
		assertTrue(r.start.y == 2);
		
		assertTrue(r.end.x == 3);
		assertTrue(r.end.y == 4);
	}
	
	@Test(expected=InvalidEndPointException.class)
	public void testCreateRoadWithoutStartBuilding() throws InvalidEndPointException {
		GameMap map = GameMap.createGameMap();
		
		Storage s = Storage.createStorage();
		
		map.placeBuilding(s, new Point(5, 6));
		
		map.placeRoad(new Point(6, 7), new Point(5, 6));
	}
	
	@Test(expected=InvalidEndPointException.class)
	public void testCreateRoadWithoutEndBuilding() throws InvalidEndPointException {
		GameMap map = GameMap.createGameMap();
		
		Building wc = Woodcutter.createWoodcutter();
		
		map.placeBuilding(wc, new Point(8, 9));
		
		map.placeRoad(new Point(8, 9), new Point(3, 4));
	}

	@Test(expected=InvalidEndPointException.class) 
	public void testCreateRoadWithoutAnyValidEndpoints() throws InvalidEndPointException {
		GameMap map = GameMap.createGameMap();
		
		map.placeRoad(new Point(1, 2), new Point(3, 4));
	}
	
	@Test
	public void createTwoChainedRoads() throws InvalidEndPointException {
		GameMap map = GameMap.createGameMap();
		
		Building wc = Woodcutter.createWoodcutter();
		
		map.placeBuilding(wc, new Point(3, 4));
		map.placeFlag(new Point(7, 8));
		
		map.placeRoad(new Point(3, 4), new Point(7, 8));
		
		map.placeFlag(new Point(10, 11));
		
		map.placeRoad(new Point(7, 8), new Point(10, 11));
	}
	
	@Test(expected=InvalidEndPointException.class)
	public void testCreateRoadWithSameEndAndStart() throws InvalidEndPointException {
		GameMap map = GameMap.createGameMap();
		
		map.placeFlag(new Point(3, 3));
		map.placeRoad(new Point(3, 3), new Point(3, 3));	
	}

	@Test
	public void testDoesRouteExist() throws InvalidEndPointException, InvalidRouteException {

		GameMap map = GameMap.createGameMap();
		
		Point[] points = new Point[] {
				new Point(1,1),
				new Point(2,2)
		};

		int i;
		for (i = 0; i < points.length; i++) {
			map.placeFlag(points[i]);
		}

		map.placeRoad(points[0], points[1]);
		
		assertTrue(map.routeExist(points[0], points[1]));
	}
	
	@Test
	public void testDoesRouteExistNo() throws InvalidEndPointException, InvalidRouteException {
		GameMap map = GameMap.createGameMap();
		
		Point[] points = new Point[] {
				new Point(1,1),
				new Point(2,2),
				new Point(3,3)
		};

		int i;
		for (i = 0; i < points.length; i++) {
			map.placeFlag(points[i]);
		}

		map.placeRoad(points[0], points[1]);
		
		assertFalse(map.routeExist(points[0], points[2]));
	}
	
	@Test(expected=InvalidRouteException.class)
	public void testFindRouteWithSameStartAndEnd() throws InvalidRouteException {
		GameMap map = GameMap.createGameMap();
		
		map.findWay(new Point(1, 1), new Point(1,1));
	}
	
	@Test
	public void testWorkerWalk() throws InvalidEndPointException, InvalidRouteException {
		/*
		 * F--F1--F2--F3--F4
		 *    |    |
		 *    |    |---F9--Target building
		 *    |
		 *    | 
		 *    |---F5---F6
		 *    |
		 *    |---F7---F8
		 */
		
		GameMap map = GameMap.createGameMap();
		
		Point[] points = new Point[] {
		new Point(1,1),
		new Point(2, 1),
		new Point (3, 1),
		new Point(4, 1),
		new Point(5, 1),
		new Point(2, 4),
		new Point(3, 4),
		new Point(2, 6),
		new Point(3, 6),
		new Point(4, 2)};
		
		Point target = new Point(6, 2);
		
		int i;
		for (i = 0; i < points.length; i++) {
			map.placeFlag(points[i]);
		}

		map.placeFlag(target);
		
		map.placeBuilding(Woodcutter.createWoodcutter(), target);
		
		map.placeRoad(points[0], points[1]);
		map.placeRoad(points[1], points[2]);
		map.placeRoad(points[2], points[3]);
		map.placeRoad(points[3], points[4]);
		map.placeRoad(points[2], points[9]);
		map.placeRoad(points[9], target);
		map.placeRoad(points[1], points[5]);
		map.placeRoad(points[5], points[6]);
		map.placeRoad(points[1], points[7]);
		map.placeRoad(points[7], points[8]);

		Worker worker = Worker.createWorker(map);
		
		assertNotNull(worker);
		
		worker.setPosition(points[0]);
		worker.setTarget(target);
		
		assertTrue(worker.getLocation().equals(points[0]));
		
		Utils.fastForward(10, worker);
		
		assertTrue(worker.getLocation().equals(points[1]));
		
		Utils.fastForward(10, worker);
		assertTrue(worker.getLocation().equals(points[2]));
		
		Utils.fastForward(10, worker);
		assertTrue(worker.getLocation().equals(points[9]));
		
		Utils.fastForward(10, worker);
		
		assertTrue(worker.getLocation().equals(target));
	}
	
	@Test(expected=InvalidRouteException.class)
	public void testWorkerUnreachableTarget() throws InvalidRouteException {
		GameMap map = GameMap.createGameMap();
		
		Point target = new Point(6, 2);
		Point start = new Point(2, 2);

		map.placeFlag(start);
		map.placeFlag(target);

		Worker worker = Worker.createWorker(map);
		
		worker.setPosition(start);
		worker.setTarget(target);
	}
	
	@Test
	public void testProduceThenDeliverToStorage() throws InvalidStateForProduction, InvalidRouteException, InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException {
		GameMap map = GameMap.createGameMap();
		
		Quarry qry = Quarry.createQuarry();
		Storage stge = Storage.createStorage();
		
		Point target = new Point(6, 2);
		Point start = new Point(2, 2);

		map.placeBuilding(qry, start);
		map.placeBuilding(stge, target);
		
		map.placeRoad(start, target);
		
		Worker worker = Worker.createWorker(map);
		
		worker.setPosition(start);

		Utils.constructSmallHouse(qry);
                Utils.constructMediumHouse(stge);
		
		assertTrue(qry.getConstructionState() == DONE);
		
		/* Production starts, wait for it to finish */
		Utils.fastForward(100, qry, stge, worker);
		
		assertTrue(qry.outputAvailable());
		
		Cargo c = qry.retrieveCargo();
		
		c.setTarget(stge, map);
		
		qry.getFlag().putCargo(c);
		
		List<Cargo> cargos = qry.getFlag().getStackedCargo();
		
		assertTrue(cargos.size() == 1);
		
		c = cargos.get(0);
		
		assertTrue(c.getMaterial() == STONE);
		
		// TODO: Make sure the cargo has a target which is to go to the closest storage building
		
		c.setTarget(stge, map);
		
		
	}
}
