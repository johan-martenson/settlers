package org.appland.settlers.test;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.appland.settlers.model.Actor;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Material;

import static org.appland.settlers.model.Material.*;
import org.appland.settlers.model.Military;

import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.LARGE;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Terrain;
import org.appland.settlers.model.Tile;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertTrue;

public class Utils {

    private static final Logger log = Logger.getLogger(Courier.class.getName());

    public static void fastForward(int time, Actor b) {
        int i;
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

    static void constructMediumHouse(Building sm) throws Exception {
        assertTrue(sm.underConstruction());

        Cargo woodCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);

        /* Deliver 4 wood and 3 stone */
        sm.promiseDelivery(PLANCK);
        sm.promiseDelivery(PLANCK);
        sm.promiseDelivery(PLANCK);
        sm.promiseDelivery(PLANCK);
        sm.putCargo(woodCargo);
        sm.putCargo(woodCargo);
        sm.putCargo(woodCargo);
        sm.putCargo(woodCargo);

        sm.promiseDelivery(STONE);
        sm.promiseDelivery(STONE);
        sm.promiseDelivery(STONE);
        sm.putCargo(stoneCargo);
        sm.putCargo(stoneCargo);
        sm.putCargo(stoneCargo);

        Utils.fastForward(150, sm);
        
        assertTrue(sm.ready());
    }

    static void constructSmallHouse(Building house) throws Exception {
        assertTrue(house.underConstruction());

        Cargo woodCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);

        /* Deliver 2 wood and 2 stone */
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.putCargo(woodCargo);
        house.putCargo(woodCargo);

        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.putCargo(stoneCargo);
        house.putCargo(stoneCargo);

        Utils.fastForward(100, house);
        
        assertTrue(house.ready());
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

    static void fillUpInventory(Storage hq, Material material, int amount) throws Exception {
        Cargo c = new Cargo(material, null);

        int i;
        for (i = 0; i < amount; i++) {
            hq.putCargo(c);
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

    static void constructLargeHouse(Building house) throws Exception {
        assertTrue(house.underConstruction());

        Cargo planckCargo = new Cargo(PLANCK, null);
        Cargo stoneCargo = new Cargo(STONE, null);

        /* Deliver 4 wood and 3 stone */
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.promiseDelivery(PLANCK);
        house.putCargo(planckCargo);
        house.putCargo(planckCargo);
        house.putCargo(planckCargo);
        house.putCargo(planckCargo);

        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.promiseDelivery(STONE);
        house.putCargo(stoneCargo);
        house.putCargo(stoneCargo);
        house.putCargo(stoneCargo);
        house.putCargo(stoneCargo);
        
        fastForward(200, house);
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

    static void fastForwardUntilWorkersReachTarget(GameMap map, Worker... workers) {
        assertNotNull(map);
        assertFalse(workers.length == 0);
        
        for (Worker c : workers) {
            assertTrue(c.isTraveling());
        }
            
        for (int i = 0; i < 1000; i++) {
            boolean allDone = true;

            for (Worker c : workers) {
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

    static void fastForwardUntilWorkerReachesPoint(GameMap map, Worker worker, Point target) {
        assertNotNull(target);
        assertNotNull(worker);
        assertNotNull(map);
        
        assertTrue(worker.getPlannedPath().contains(target));
        
        for (int i = 0; i < 1000; i++) {
            if (worker.isAt(target)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isAt(target));
    }

    static SawmillWorker occupySawmill(Sawmill sm, GameMap map) throws InvalidRouteException {
        SawmillWorker sw = new SawmillWorker(map);
        
        map.placeWorker(sw, sm.getFlag());
        
        sw.setTargetBuilding(sm);
        
        fastForwardUntilWorkersReachTarget(map, sw);
        
        return sw;
    }

    static Worker occupyBuilding(Worker worker, Building building, GameMap map) throws Exception {
        map.placeWorker(worker, building);
        building.assignWorker(worker);
        worker.enterBuilding(building);

        assertEquals(building.getWorker(), worker);
        
        return worker;
    }

    static void fastForwardUntilTreeIsGrown(Tree tree, GameMap map) {
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertEquals(tree.getSize(), LARGE);
    }

    static void fastForwardUntilCropIsGrown(Crop crop, GameMap map) {
        int i;
        for (i = 0; i < 500; i++) {
            if (crop.getGrowthState() == FULL_GROWN) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), FULL_GROWN);
    }

    static void verifyListContainsWorkerOfType(List<Worker> allWorkers, Class aClass) {
        boolean found = false;
        
        for (Worker w : allWorkers) {
            if (w.getClass().equals(aClass)) {
                found = true;
            }
        }
    
        assertTrue(found);
    }

    static void surroundPointWithWater(Point p1, Point p2, Point p3, GameMap map) throws Exception {        
        Tile waterTile = map.getTerrain().getTile(p1, p2, p3);
        
        waterTile.setVegetationType(WATER);

        map.terrainIsUpdated();
    }

    static void surroundPointWithMountain(Point point0, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setVegetationType(MOUNTAIN);
        }

        map.terrainIsUpdated();
    }

    static void fastForwardUntilBuildingIsConstructed(Building building, GameMap map) {
        for (int i = 0; i < 2000; i++) {
            if (building.ready()) {
                break;
            }
            
            map.stepTime();
        }
    
        assertTrue(building.ready());
    }

    static void fastForwardUntilBuildingIsOccupied(Building building, GameMap map) {
        for (int i = 0; i < 1000; i++) {
            if (building.getWorker() != null) {
                break;
            }
        
            map.stepTime();
        }
    
        assertNotNull(building.getWorker());
    }

    static Military occupyMilitaryBuilding(Military m, Building building, GameMap map) throws Exception {
        map.placeWorker(m, building);
        building.deployMilitary(m);
        m.enterBuilding(building);

        return m;
    }

    static void putGoldAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(GOLD, size);
        }
    }

    static void putIronAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(IRON, size);
        }
    }

    static void putCoalAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(COAL, size);
        }
    }

    static void putGraniteAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(STONE, size);
        }
    }

    static void createMountainWithinRadius(Point point1, int i, GameMap map) throws Exception {
        Set<Tile> tiles = new HashSet<>();
        Terrain terrain = map.getTerrain();
        
        for (Point p : map.getPointsWithinRadius(point1, i - 1)) {
            tiles.addAll(terrain.getSurroundingTiles(p));
        }

        for (Tile t : tiles) {
            t.setVegetationType(MOUNTAIN);
        }

        map.terrainIsUpdated();
    }

    static void putMineralWithinRadius(Material mineral, Point point1, int i, GameMap map) throws Exception {
        Set<Tile> tiles = new HashSet<>();
        Terrain terrain = map.getTerrain();
        
        for (Point p : map.getPointsWithinRadius(point1, i - 1)) {
            tiles.addAll(terrain.getSurroundingTiles(p));
        }

        for (Tile t : tiles) {
            t.setAmountMineral(mineral, LARGE);
        }

        map.terrainIsUpdated();
    }

    static Courier occupyRoad(Courier courier, Road road1, GameMap map) throws Exception {
        map.placeWorker(courier, road1.getFlags()[0]);
        courier.assignToRoad(road1);

        assertEquals(road1.getCourier(), courier);
        
        return courier;
    }

    static void adjustInventoryTo(Storage storage, Material material, int amount, GameMap map) throws Exception {
        for (int i = 0; i < 1000; i++) {
        
            if (storage.getAmount(material) == amount) {
                break;
            }

            if (storage.getAmount(material) > amount) {
                storage.retrieve(material);
            } else if (storage.getAmount(material) < amount) {
                storage.putCargo(new Cargo(material, map));
            }
        }

        assertEquals(storage.getAmount(material), amount);
    }
}
