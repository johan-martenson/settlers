package org.appland.settlers.test;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.appland.settlers.model.Land;
import org.appland.settlers.model.Material;

import static org.appland.settlers.model.Material.*;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;

import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
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

    public static void fastForward(int time, Actor b) throws Exception {
        int i;
        for (i = 0; i < time; i++) {
            b.stepTime();
        }
    }

    public static void fastForward(int time, Actor... b) throws Exception {
        fastForward(time, Arrays.asList(b));
    }

    public static void fastForward(int time, List<Actor> actors) throws Exception {
        for (Actor a : actors) {
            fastForward(time, a);
        }
    }

    public static void fastForward(int time, GameMap map) throws Exception {
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

    public static boolean roadStartStopIsCorrect(Road r, Point p1, Point p2) {
        if (r.getStart().equals(p1) && r.getEnd().equals(p2)) {
            return true;
        } else if (r.getStart().equals(p2) && r.getEnd().equals(p1)) {
            return true;
        }

        return false;
    }

    public static void stepTime(List<Actor> actors) throws Exception {

        for (Actor a : actors) {
            a.stepTime();
        }
    }

    public static Map<Building, Material> getNeedForDelivery(List<Building> buildings) {

        Map<Building, Material> result = new HashMap<>();

        for (Building b : buildings) {
            Map<Material, Integer> neededMaterial = b.getRequiredGoodsForProduction();

            for (Material m : neededMaterial.keySet()) {
                result.put(b, m);
            }
        }

        return result;
    }

    public static void fillUpInventory(Storage hq, Material material, int amount) throws Exception {
        Cargo c = new Cargo(material, null);

        int i;
        for (i = 0; i < amount; i++) {
            hq.putCargo(c);
        }
    }

    public static boolean materialIntMapIsEmpty(Map<Material, Integer> inQueue) {
        boolean isEmpty = true;

        for (Material m : Material.values()) {
            if (inQueue.get(m) != 0) {
                isEmpty = false;
            }
        }

        return isEmpty;
    }

    public static void assertNoStepDirectlyUpwards(List<Point> route) {
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

    public static void fastForwardUntilWorkersReachTarget(GameMap map, Worker... workers) throws Exception {
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

    public static void fastForwardUntilWorkerReachesPoint(GameMap map, Worker worker, Point target) throws Exception {
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

    public static <T extends Worker> T occupyBuilding(T worker, Building building, GameMap map) throws Exception {
        map.placeWorker(worker, building);
        building.assignWorker(worker);
        worker.enterBuilding(building);

        assertEquals(building.getWorker(), worker);
        
        return worker;
    }

    public static void fastForwardUntilTreeIsGrown(Tree tree, GameMap map) throws Exception {
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertEquals(tree.getSize(), LARGE);
    }

    public static void fastForwardUntilCropIsGrown(Crop crop, GameMap map) throws Exception {
        int i;
        for (i = 0; i < 500; i++) {
            if (crop.getGrowthState() == FULL_GROWN) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), FULL_GROWN);
    }

    public static void verifyListContainsWorkerOfType(List<Worker> allWorkers, Class aClass) {
        boolean found = false;
        
        for (Worker w : allWorkers) {
            if (w.getClass().equals(aClass)) {
                found = true;
            }
        }
    
        assertTrue(found);
    }

    public static void surroundPointWithWater(Point point0, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setVegetationType(WATER);
        }
    }
    
    public static void setTileToWater(Point p1, Point p2, Point p3, GameMap map) throws Exception {        
        Tile waterTile = map.getTerrain().getTile(p1, p2, p3);
        
        waterTile.setVegetationType(WATER);
    }

    public static void surroundPointWithMountain(Point point0, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setVegetationType(MOUNTAIN);
        }
    }

    public static void fastForwardUntilBuildingIsConstructed(Building building, GameMap map) throws Exception {
        for (int i = 0; i < 10000; i++) {
            if (building.ready()) {
                break;
            }
            
            map.stepTime();
        }
    
        assertTrue(building.ready());
    }

    public static void fastForwardUntilBuildingIsOccupied(Building building, GameMap map) throws Exception {
        for (int i = 0; i < 1000; i++) {
            if (building.getWorker() != null) {
                break;
            }
        
            map.stepTime();
        }
    
        assertNotNull(building.getWorker());
    }

    public static void putGoldAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(GOLD, size);
        }
    }

    public static void putIronAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(IRON, size);
        }
    }

    public static void putCoalAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(COAL, size);
        }
    }

    public static void putGraniteAtSurroundingTiles(Point point0, Size size, GameMap map) throws Exception {
        for (Tile t : map.getTerrain().getSurroundingTiles(point0)) {
            t.setAmountMineral(STONE, size);
        }
    }

    public static void createMountainWithinRadius(Point point1, int i, GameMap map) throws Exception {
        Set<Tile> tiles = new HashSet<>();
        Terrain terrain = map.getTerrain();
        
        for (Point p : map.getPointsWithinRadius(point1, i - 1)) {
            tiles.addAll(terrain.getSurroundingTiles(p));
        }

        for (Tile t : tiles) {
            t.setVegetationType(MOUNTAIN);
        }
    }

    public static void putMineralWithinRadius(Material mineral, Point point1, int i, GameMap map) throws Exception {
        Set<Tile> tiles = new HashSet<>();
        Terrain terrain = map.getTerrain();
        
        for (Point p : map.getPointsWithinRadius(point1, i - 1)) {
            tiles.addAll(terrain.getSurroundingTiles(p));
        }

        for (Tile t : tiles) {
            t.setAmountMineral(mineral, LARGE);
        }
    }

    public static Courier occupyRoad(Road road1, GameMap map) throws Exception {
        Courier courier = new Courier(road1.getPlayer(), map);

        map.placeWorker(courier, road1.getFlags()[0]);
        courier.assignToRoad(road1);

        assertEquals(road1.getCourier(), courier);

        return courier;
    }

    public static void adjustInventoryTo(Storage storage, Material material, int amount, GameMap map) throws Exception {
        for (int i = 0; i < 1000; i++) {
        
            if (storage.getAmount(material) == amount) {
                break;
            }

            if (storage.getAmount(material) > amount) {
                
                if (material == PRIVATE || material == SERGEANT || material == GENERAL) {
                    storage.retrieveMilitary(material);
                } else {
                    storage.retrieve(material);
                }
            } else if (storage.getAmount(material) < amount) {
                storage.putCargo(new Cargo(material, map));
            }
        }

        assertEquals(storage.getAmount(material), amount);
    }

    public static void constructHouse(Building b, GameMap map) throws Exception {
        assertTrue(b.underConstruction());

        for (int i = 0; i < 20; i++) {
            if (b.needsMaterial(PLANCK)) {
                try {
                    Cargo cargo = new Cargo(PLANCK, map);
                    b.putCargo(cargo);
                } catch (Exception ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (b.needsMaterial(STONE)) {                
                try {
                    Cargo cargo = new Cargo(STONE, map);
                    b.putCargo(cargo);
                } catch (Exception ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        for (int i = 0; i < 500; i++) {
            if (b.ready()) {
                break;
            }
        
            b.stepTime();
        }
        
        assertTrue(b.ready());
    }

    public static void fastForwardUntilWorkerCarriesCargo(GameMap map, Courier courier1, Cargo cargo) throws Exception {
        for (int j = 0; j < 1000; j++) {
            if (cargo.equals(courier1.getCargo())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(courier1.getCargo(), cargo);
    }

    public static void fastForwardUntilWorkerProducesCargo(GameMap map, Worker worker) throws Exception {
        for (int i = 0; i < 300; i++) {
            if (worker.getCargo() != null) {
                break;
            }
        
            map.stepTime();
        }

        assertNotNull(worker.getCargo());
    }

    public static void waitForMilitaryBuildingToGetPopulated(GameMap map, Building barracks0, int nr) throws Exception {
        boolean populated = false;
        for (int i = 0; i < 1000; i++) {
            if (barracks0.getHostedMilitary() == nr) {
                populated = true;

                break;
            }

            map.stepTime();
        }

        assertTrue(populated);
        assertEquals(barracks0.getHostedMilitary(), nr);
    }

    public static void verifyPointIsWithinBorder(GameMap map, Player player0, Point position) {
        boolean insideLand = false;

        for (Land land : player0.getLands()) {
            if (land.isWithinBorder(position)) {
                insideLand = true;

                break;
            }
        }

        assertTrue(insideLand);
    }

    public static void verifyPointIsNotWithinBorder(GameMap map, Player player0, Point position) {
        boolean insideLand = false;

        for (Land land : player0.getLands()) {
            if (land.isWithinBorder(position)) {
                insideLand = true;

                break;
            }
        }

        assertFalse(insideLand);
    }

    public static void verifyDeliveryOfMaterial(GameMap map, Road road, Material material) throws Exception {
        Courier courier = road.getCourier();

        boolean delivery = false;

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == COIN) {
                delivery = true;
            }

            map.stepTime();
        }

        assertTrue(delivery);
    }

    public static void verifyNoDeliveryOfMaterial(GameMap map, Road road0, Material material) throws Exception {
        Courier courier = road0.getCourier();

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == COIN) {
                assertFalse(true);
            }

            map.stepTime();
        }
    }

    public static void occupyMilitaryBuilding(Military.Rank rank, int amount, Building building, GameMap map) throws Exception {
        assertTrue(building.ready());
        for (int i = 0; i < amount; i++) {
            occupyMilitaryBuilding(rank, building, map);
        }
    }

    public static Military occupyMilitaryBuilding(Military.Rank rank, Building building, GameMap map) throws Exception {
        Player player = building.getPlayer();

        Military military = new Military(player, rank, map);

        map.placeWorker(military, building);
        building.deployMilitary(military);
        military.enterBuilding(building);

        return military;
    }

    public static Military findMilitaryOutsideBuilding(Player player, GameMap map) {
        Military attacker = null;
        for (Worker w : map.getWorkers()) {
            if (w instanceof Military && !w.isInsideBuilding() && w.getPlayer().equals(player)) {
                attacker = (Military)w;
            }
        }

        return attacker;
    }

    public static void waitForWorkerToDisappear(Worker worker, GameMap map) throws Exception {
        for (int i = 0; i < 500; i++) {
            if (!map.getWorkers().contains(worker)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    public static Military waitForMilitaryOutsideBuilding(Player player, GameMap map) throws Exception {
        for (int i = 0; i < 1000; i++) {
            Military military = findMilitaryOutsideBuilding(player, map);

            if (military != null) {
                assertEquals(military.getPlayer(), player);

                return military;
            }

            map.stepTime();
        }

        assertFalse(true);

        return null;
    }

    public static <T> List<T> findWorkersOfTypeOutsideForPlayer(Class<T> aClass, Player player0, GameMap map) {
        List<T> workersFound = new LinkedList<>();

        for (Worker w : map.getWorkers()) {
            if (w.getClass().equals(aClass) && !w.isInsideBuilding() && w.getPlayer().equals(player0)) {
                workersFound.add((T)w);
            }
        }

        return workersFound;
    }

    public static <T> List<T> waitForWorkersOutsideBuilding(Class<T> type, int nr, Player player0, GameMap map) throws Exception {
        List<T> workers = new LinkedList<>();

        for (int i = 0; i < 1000; i++) {
            workers.clear();

            for (Worker w : map.getWorkers()) {
                if (w.getClass().equals(type) && !w.isInsideBuilding() && player0.equals(w.getPlayer())) {
                    workers.add((T)w);
                }
            }

            if (workers.size() == nr) {
                break;
            }

            map.stepTime();
        }

        assertEquals(workers.size(), nr);

        return workers;
    }

    public static <T extends Building> void waitForBuildingToDisappear(GameMap map, T building) throws Exception {
        assertTrue(building.burningDown() || building.destroyed());

        for (int i = 0; i < 1000; i++) {
            if (!map.getBuildings().contains(building)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getBuildings().contains(building));
    }
}
