package org.appland.settlers.test;

import org.appland.settlers.model.Actor;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Catapult;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Hunter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Projectile;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Tile;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.LARGE_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.MEDIUM_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.MINE_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.NO_BUILDING_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.SMALL_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleFlag.FLAG_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE;
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.Tile.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Tile.Vegetation.SWAMP;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Utils {

    public static void fastForward(int time, Actor actor) throws Exception {

        for (int i = 0; i < time; i++) {
            actor.stepTime();
        }
    }

    public static void fastForward(int time, List<Actor> actors) throws Exception {
        for (Actor actor : actors) {
            fastForward(time, actor);
        }
    }

    public static void fastForward(int time, GameMap map) throws Exception {

        for (int i = 0; i < time; i++) {
            map.stepTime();
        }
    }

    public static List<Building> getNeedForMaterial(Material wood, List<Building> buildings) {

        List<Building> result = new ArrayList<>();

        for (Building building : buildings) {
            if (building.needsMaterial(wood)) {
                result.add(building);
            }
        }

        return result;
    }

    public static void fillUpInventory(Storage storage, Material material, int amount) throws Exception {
        Cargo cargo = new Cargo(material, null);

        for (int i = 0; i < amount; i++) {
            storage.putCargo(cargo);
        }
    }

    public static void assertNoStepDirectlyUpwards(List<Point> route) {
        Point previous = null;

        for (Point iterator : route) {
            if (previous == null) {
                previous = iterator;
                continue;
            }

            assertFalse(iterator.x == previous.x && abs(iterator.y - previous.y) == 2);

            previous = iterator;
        }
    }

    public static void fastForwardUntilWorkersReachTarget(GameMap map, Worker... workers) throws Exception {
        fastForwardUntilWorkersReachTarget(map, Arrays.asList(workers));
    }

    public static void fastForwardUntilWorkersReachTarget(GameMap map, List<Worker> workers) throws Exception {

        assertNotNull(map);
        assertFalse(workers.isEmpty());

        for (Worker worker : workers) {
            assertTrue(worker.isTraveling());
        }

        for (int i = 0; i < 1000; i++) {
            boolean allDone = true;

            for (Worker worker : workers) {
                if (!worker.isArrived()) {
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

        for (int i = 0; i < 1000; i++) {

            if (worker.isAt(target)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getPosition(), target);
        assertTrue(worker.isAt(target));
    }

    public static <T extends Worker> T occupyBuilding(T worker, Building building) throws Exception {
        GameMap map = building.getMap();

        map.placeWorker(worker, building);
        building.assignWorker(worker);
        worker.enterBuilding(building);

        assertEquals(building.getWorker(), worker);

        return worker;
    }

    public static void fastForwardUntilTreeIsGrown(Tree tree, GameMap map) throws Exception {

        for (int i = 0; i < 500; i++) {
            map.stepTime();

            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertEquals(tree.getSize(), LARGE);
    }

    public static void fastForwardUntilCropIsGrown(Crop crop, GameMap map) throws Exception {

        for (int i = 0; i < 500; i++) {
            if (crop.getGrowthState() == FULL_GROWN) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), FULL_GROWN);
    }

    public static void verifyListContainsWorkerOfType(List<Worker> workers, Class<? extends Worker> workerClass) {
        boolean found = false;

        for (Worker worker : workers) {
            if (worker.getClass().equals(workerClass)) {
                found = true;

                break;
            }
        }

        assertTrue(found);
    }

    public static void surroundPointWithWater(Point point, GameMap map) {
        surroundPointWithVegetation(point, WATER, map);
    }

    public static void surroundPointWithMountain(Point point, GameMap map) {
        surroundPointWithVegetation(point, MOUNTAIN, map);
    }

    public static void surroundPointWithSwamp(Point point, GameMap map) {
        surroundPointWithVegetation(point, SWAMP, map);
    }

    public static void fastForwardUntilBuildingIsConstructed(Building building) throws Exception {
        GameMap map = building.getMap();

        for (int i = 0; i < 10000; i++) {
            if (building.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static void fastForwardUntilBuildingIsOccupied(Building building) throws Exception {
        GameMap map = building.getMap();

        for (int i = 0; i < 1000; i++) {
            if (building.getWorker() != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(building.getWorker());
    }

    public static void putGoldAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.getTerrain().surroundPointWithMineral(point, GOLD, size);
    }

    public static void putIronAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.getTerrain().surroundPointWithMineral(point, IRON, size);
    }

    public static void putCoalAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.getTerrain().surroundPointWithMineral(point, COAL, size);
    }

    public static void putGraniteAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.getTerrain().surroundPointWithMineral(point, STONE, size);
    }

    public static void createMountainWithinRadius(Point point, int radius, GameMap map) {
        for (Point p : map.getPointsWithinRadius(point, radius - 1)) {
            surroundPointWithVegetation(p, MOUNTAIN, map);
        }
    }

    public static void putMineralWithinRadius(Material mineral, Point point1, int radius, GameMap map) {
        for (Point p : map.getPointsWithinRadius(point1, radius - 1)) {
            map.getTerrain().surroundPointWithMineral(p, mineral, LARGE);
        }
    }

    public static Courier occupyRoad(Road road, GameMap map) throws Exception {
        Courier courier = new Courier(road.getPlayer(), map);

        map.placeWorker(courier, road.getFlags()[0]);
        courier.assignToRoad(road);

        assertEquals(road.getCourier(), courier);

        return courier;
    }

    public static void adjustInventoryTo(Storage storage, Material material, int amount) throws Exception {
        GameMap map = storage.getMap();

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

    public static void constructHouse(Building building) throws Exception {
        GameMap map = building.getMap();

        assertTrue(building.underConstruction());

        for (int i = 0; i < 20; i++) {
            if (building.needsMaterial(PLANK)) {
                try {
                    Cargo cargo = new Cargo(PLANK, map);

                    building.promiseDelivery(PLANK);
                    building.putCargo(cargo);
                } catch (Exception ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (building.needsMaterial(STONE)) {
                try {
                    Cargo cargo = new Cargo(STONE, map);

                    building.promiseDelivery(STONE);
                    building.putCargo(cargo);
                } catch (Exception ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        for (int i = 0; i < 500; i++) {
            if (building.isReady()) {
                break;
            }

            building.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static void fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker, Material material) throws Exception {

        for (int j = 0; j < 20000; j++) {
            if (worker.getCargo() != null && worker.getCargo().getMaterial().equals(material)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getCargo().getMaterial(), material);
    }

    public static void fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker, Cargo cargo) throws Exception {
        for (int j = 0; j < 2000; j++) {
            if (cargo.equals(worker.getCargo())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getCargo(), cargo);
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

    public static void waitForMilitaryBuildingToGetPopulated(Building building, int nr) throws Exception {
        GameMap map = building.getMap();

        boolean populated = false;

        for (int i = 0; i < 1000; i++) {
            if (building.getNumberOfHostedMilitary() == nr) {
                populated = true;

                break;
            }

            map.stepTime();
        }

        assertTrue(populated);
        assertEquals(building.getNumberOfHostedMilitary(), nr);
    }

    public static void waitForMilitaryBuildingToGetPopulated(Building building) throws Exception {
        GameMap map = building.getMap();

        assertFalse(building.isOccupied());

        for (int i = 0; i < 1000; i++) {
            if (building.isOccupied()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isOccupied());
    }

    public static void verifyPointIsWithinBorder(Player player, Point point) {
        assertTrue(player.getLandInPoints().contains(point));
    }

    public static void verifyPointIsNotWithinBorder(Player player, Point point) {
        assertFalse(player.getLandInPoints().contains(point));
    }

    public static void verifyDeliveryOfMaterial(GameMap map, Road road) throws Exception {
        Courier courier = road.getCourier();

        boolean delivery = false;

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == COIN) {
                delivery = true;

                break;
            }

            map.stepTime();
        }

        assertTrue(delivery);
    }

    public static void verifyNoDeliveryOfMaterial(GameMap map, Road road) throws Exception {
        Courier courier = road.getCourier();

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == COIN) {
                fail();
            }

            map.stepTime();
        }
    }

    public static void occupyMilitaryBuilding(Military.Rank rank, int amount, Building building) throws Exception {
        assertTrue(building.isReady());
        for (int i = 0; i < amount; i++) {
            occupyMilitaryBuilding(rank, building);
        }
    }

    public static Military occupyMilitaryBuilding(Military.Rank rank, Building building) throws Exception {
        GameMap map = building.getMap();
        Player player = building.getPlayer();

        Military military = new Military(player, rank, map);

        map.placeWorker(military, building);

        building.promiseMilitary(military);

        military.enterBuilding(building);

        return military;
    }

    public static Military findMilitaryOutsideBuilding(Player player) {
        GameMap map = player.getMap();

        Military attacker = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Military && !worker.isInsideBuilding() && worker.getPlayer().equals(player)) {
                attacker = (Military)worker;
            }
        }

        return attacker;
    }

    public static List<Military> findMilitariesOutsideBuilding(Player player) {
        GameMap map = player.getMap();
        List<Military> result = new LinkedList<>();

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Military && !worker.isInsideBuilding() && worker.getPlayer().equals(player)) {
                result.add((Military)worker);
            }
        }

        return result;
    }

    public static void waitForWorkerToDisappear(Worker worker, GameMap map) throws Exception {
        for (int i = 0; i < 10000; i++) {
            if (!map.getWorkers().contains(worker)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    public static Military waitForMilitaryOutsideBuilding(Player player) throws Exception {
        GameMap map = player.getMap();
        for (int i = 0; i < 1000; i++) {
            Military military = findMilitaryOutsideBuilding(player);

            if (military != null) {
                assertEquals(military.getPlayer(), player);

                return military;
            }

            map.stepTime();
        }

        fail();

        return null;
    }

    public static <T extends Worker> List<T> findWorkersOfTypeOutsideForPlayer(Class<T> aClass, Player player) {
        GameMap map = player.getMap();
        List<T> workersFound = new LinkedList<>();

        for (Worker worker : map.getWorkers()) {
            if (worker.getClass().equals(aClass) && !worker.isInsideBuilding() && worker.getPlayer().equals(player)) {
                workersFound.add((T)worker);
            }
        }

        return workersFound;
    }

    public static <T extends Worker> List<T> waitForWorkersOutsideBuilding(Class<T> type, int nr, Player player) throws Exception {
        GameMap map = player.getMap();
        List<T> workers = new LinkedList<>();

        for (int i = 0; i < 1000; i++) {

            workers.clear();

            for (Worker worker : map.getWorkers()) {
                if (worker.getClass().equals(type) && !worker.isInsideBuilding() && player.equals(worker.getPlayer())) {
                    workers.add((T)worker);
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

    public static <T extends Building> void waitForBuildingToDisappear(T building) throws Exception {
        GameMap map = building.getMap();

        assertTrue(building.isBurningDown() || building.isDestroyed());

        for (int i = 0; i < 1000; i++) {
            if (!map.getBuildings().contains(building)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getBuildings().contains(building));
    }

    static void waitForFightToStart(GameMap map, Military attacker, Military defender) throws Exception {

        for (int i = 0; i < 1000; i++) {
            if (attacker.isFighting() && defender.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(defender.isFighting());
        assertTrue(attacker.isFighting());
    }

    static Military getMainAttacker(Building building, Collection<Military> attackers) throws Exception {

        GameMap map = building.getMap();
        Military firstAttacker = null;

        for (Military military : attackers) {
            if (military.getTarget().equals(building.getFlag().getPosition())) {
                firstAttacker = military;

                break;
            }

            map.stepTime();
        }

        assertNotNull(firstAttacker);

        return firstAttacker;
    }

    static Projectile waitForCatapultToThrowProjectile(Catapult catapult) throws Exception {
        GameMap map = catapult.getMap();

        Projectile projectile = null;

        assertTrue(map.getProjectiles().isEmpty());

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (!map.getProjectiles().isEmpty()) {

                projectile = map.getProjectiles().get(0);

                break;
            }
        }

        assertNotNull(projectile);

        return projectile;
    }

    static void waitForProjectileToReachTarget(Projectile projectile, GameMap map) throws Exception {

        for (int i = 0; i < 1000; i++) {

            if (projectile.arrived()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(projectile.arrived());
    }

    static WildAnimal waitForAnimalToAppear(GameMap map) throws Exception {

        for (int i = 0; i < 2000; i++) {

            if (!map.getWildAnimals().isEmpty()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWildAnimals().isEmpty());

        return map.getWildAnimals().get(0);
    }

    static WildAnimal waitForWildAnimalCloseToPoint(Point point, GameMap map) throws Exception {
        WildAnimal animal = null;

        for (int i = 0; i < 5000; i++) {

            /* Check if there is a wild animal close to the hut */
            for (WildAnimal wa : map.getWildAnimals()) {
                if (wa.getPosition().distance(point) < 20) {
                    animal = wa;

                    break;
                }
            }

            /* Exit the loop if an animal was found */
            if (animal != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(animal);
        assertTrue(animal.getPosition().distance(point) < 20);

        return animal;
    }

    static void waitForActorsToGetClose(Hunter hunter, WildAnimal animal, int distance, GameMap map) throws Exception {
        for (int i = 0; i < 5000; i++) {
            if (hunter.getPosition().distance(animal.getPosition()) <= distance) {
                break;
            }

            map.stepTime();
        }

        assertTrue(hunter.getPosition().distance(animal.getPosition()) <= distance);
    }

    static void fastForwardUntilWorkerCarriesNoCargo(GameMap map, Worker worker) throws Exception {

        for (int j = 0; j < 2000; j++) {
            if (worker.getCargo() == null) {
                break;
            }

            map.stepTime();
        }

        assertNull(worker.getCargo());
    }

    static void waitForCargoToReachTarget(GameMap map, Cargo cargo) throws Exception {

        assertNotNull(cargo);
        assertNotNull(cargo.getPosition());
        assertNotNull(cargo.getTarget());

        for (int i = 0; i < 2000; i++) {

            if (cargo.getPosition().equals(cargo.getTarget().getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(cargo.getPosition(), cargo.getTarget().getPosition());
    }

    static void waitUntilAmountIs(Building building, Material material, int amount) throws Exception {
        GameMap map = building.getMap();

        for (int i = 0; i < 2000; i++) {

            if (building.getAmount(material) == amount) {
                break;
            }

            map.stepTime();
        }

        assertEquals(building.getAmount(material), amount);
    }

    static void deliverCargo(Building building, Material material) throws Exception {
        GameMap map = building.getMap();
        Cargo cargo = new Cargo(material, map);

        building.promiseDelivery(material);

        building.putCargo(cargo);
    }

    static Cargo fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker) throws Exception {

        for (int i = 0; i < 2000; i++) {

            if (worker.getCargo() != null) {
                return worker.getCargo();
            }

            map.stepTime();
        }

        fail();

        return null;
    }

    static void waitForCropToGetReady(GameMap map, Crop crop) throws Exception {

        for (int i = 0; i < 1000; i++) {

            if (crop.getGrowthState() == Crop.GrowthState.FULL_GROWN) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), Crop.GrowthState.FULL_GROWN);
    }

    static Crop waitForFarmerToPlantCrop(GameMap map, Farmer farmer0) throws Exception {

        waitForFarmerToStartPlanting(map, farmer0);

        Point position = farmer0.getPosition();

        assertFalse(map.isCropAtPoint(position));

        waitForFarmerToStopPlanting(map, farmer0);

        assertTrue(map.isCropAtPoint(position));

        return map.getCropAtPoint(position);
    }

    private static void waitForFarmerToStopPlanting(GameMap map, Farmer farmer) throws Exception {

        for (int i = 0; i < 10000; i++) {

            if (!farmer.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(farmer.isPlanting());
    }

    private static void waitForFarmerToStartPlanting(GameMap map, Farmer farmer) throws Exception {

        for (int i = 0; i < 10000; i++) {

            if (farmer.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
    }

    static void waitForCropToGetHarvested(GameMap map, Crop crop) throws Exception {

        for (int i = 0; i < 1000; i++) {

            if (crop.getGrowthState() == HARVESTED) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), HARVESTED);
    }

    static int getAmountMilitary(Headquarter headquarter0) {
        return headquarter0.getAmount(PRIVATE)  +
               headquarter0.getAmount(SERGEANT) +
               headquarter0.getAmount(GENERAL);
    }

    static <T> int countNumberElementAppearsInList(List<T> transportPriorityList, T element) {
        int sum = 0;

        for (T type : transportPriorityList) {
            if (element == null) {
                if (type == null) {
                    sum++;
                }
            } else {
                if (element.equals(type)) {
                    sum++;
                }
            }
        }

        return sum;
    }

    static Building waitForBuildingToGetUpgraded(Building building) throws Exception {
        GameMap map = building.getMap();

        for (int i = 0; i < 10000; i++) {
            if (!map.getBuildingAtPoint(building.getPosition()).equals(building)) {
                break;
            }

            map.stepTime();
        }

        assertNotEquals(map.getBuildingAtPoint(building.getPosition()), building);

        return map.getBuildingAtPoint(building.getPosition());
    }

    static void waitForBuildingToBurnDown(Building building) throws Exception {

        GameMap map = building.getMap();

        for (int i = 0; i < 10000; i++) {

            if (building.isDestroyed()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isDestroyed());
    }

    static void removePiecesFromStoneUntil(Stone stone, int amountLeft) {
        for (int i = 0; i < 1000; i++) {
            if (stone.getAmount() > amountLeft) {
                stone.removeOnePart();
            }
        }

        assertEquals(stone.getAmount(), amountLeft);
    }

    static void waitForTreeToGetPlanted(GameMap map, Point point) throws Exception {
        for (int i = 0; i < 1000; i++) {
            if (map.isTreeAtPoint(point)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.isTreeAtPoint(point));

    }

    static void surroundPointWithVegetation(Point point, Tile.Vegetation vegetation, GameMap map) {
        map.getTerrain().surroundWithVegetation(point, vegetation);

        assertEquals(map.getTerrain().getTileUpLeft(point).getVegetationType(), vegetation);
        assertEquals(map.getTerrain().getTileAbove(point).getVegetationType(), vegetation);
        assertEquals(map.getTerrain().getTileUpRight(point).getVegetationType(), vegetation);
        assertEquals(map.getTerrain().getTileDownRight(point).getVegetationType(), vegetation);
        assertEquals(map.getTerrain().getTileBelow(point).getVegetationType(), vegetation);
        assertEquals(map.getTerrain().getTileDownLeft(point).getVegetationType(), vegetation);
    }

    public static void verifyWorkerStaysAtHome(Worker worker, GameMap map) throws Exception {

        for (int i = 0; i < 1000; i++) {
            assertEquals(worker.getHome().getPosition(), worker.getPosition());

            map.stepTime();
        }
    }

    public static void plantTreesOnPlayersLand(Player player) {

        for (Point point: player.getLandInPoints()) {
            try {
                player.getMap().placeTree(point);
            } catch (Exception e) {}
        }
    }

    public static void plantTreesOnPoints(List<Point> pointsWithinRadius, GameMap map) {
        for (Point point : pointsWithinRadius) {
            if (map.isTreeAtPoint(point)) {
                continue;
            }

            try {
                map.placeTree(point);
            } catch (Exception e) {}
        }
    }

    public static void putStonesOnPoints(List<Point> points, GameMap map) {
        for (Point point : points) {
            if (map.isStoneAtPoint(point)) {
                continue;
            }

            try {
                map.placeStone(point);
            } catch (Exception e) {}
        }
    }

    public static void putStoneOnOnePoint(List<Point> points, GameMap map) {
        for (Point point : points) {
            if (map.isStoneAtPoint(point)) {
                continue;
            }

            try {
                map.placeStone(point);
                break;
            } catch (Exception e) {}
        }
    }

    public static void putWildAnimalOnOnePoint(List<Point> points, GameMap map) {
        for (Point point : points) {
            try {
                map.placeWildAnimal(point);
                break;
            } catch (Exception e) {}
        }
    }

    public static void fillMapWithVegetation(GameMap map, Tile.Vegetation vegetation) {
        for (Tile tile : map.getTerrain().getTilesBelow()) {
            tile.setVegetationType(vegetation);
        }

        for (Tile tile : map.getTerrain().getTilesDownRight()) {
            tile.setVegetationType(vegetation);
        }
    }

    public static Courier waitForRoadToGetAssignedCourier(GameMap map, Road road0) throws Exception {
        Courier courier = null;

        for (int i = 0; i < 10000; i++) {

            courier = road0.getCourier();

            if (courier != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(courier);

        return courier;
    }

    public static Worker waitForNonMilitaryBuildingToGetPopulated(Building building) throws Exception {

        GameMap map = building.getMap();
        Worker worker = null;

        for (int i = 0; i < 1000; i++) {
            worker = building.getWorker();

            if (worker != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker);

        return worker;
    }

    public static Cargo waitForFlagToHaveCargoWaiting(GameMap map, Flag flag) throws Exception {
        Cargo cargo = null;

        for (int i = 0; i < 1000; i++) {

            if (flag.getStackedCargo().size() > 0) {
                cargo = flag.getStackedCargo().get(0);

                break;
            }

            map.stepTime();
        }

        assertNotNull(cargo);

        return cargo;
    }

    public static Set<Courier> waitForRoadsToGetAssignedCouriers(GameMap map, Road... roads) throws Exception {
        Set<Courier> couriers = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            for (Road road : roads) {
                couriers.add(road.getCourier());

                if (couriers.size() == roads.length) {
                    break;
                }
            }

            map.stepTime();
        }

        assertEquals(couriers.size(), roads.length);

        return couriers;
    }

    public static void waitForNewMessage(Player player0) throws Exception {
        GameMap map = player0.getMap();

        int amountMessages = player0.getMessages().size();

        for (int i = 0; i < 1000; i++) {

            if (player0.getMessages().size() > amountMessages) {
                break;
            }

            map.stepTime();
        }

        assertTrue(amountMessages < player0.getMessages().size());
    }

    public static void waitForBuildingToBeConstructed(Building building) throws Exception {
        GameMap map = building.getMap();

        for (int i = 0; i < 2000; i++) {

            if (building.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static void waitForStonemasonToStartGettingStone(GameMap map, Stonemason stonemason) throws Exception {
        for (int i = 0; i < 1000; i++) {
            if (stonemason.isGettingStone()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());
    }

    public static void waitForStonemasonToFinishGettingStone(GameMap map, Stonemason stonemason) throws Exception {
        assertTrue(stonemason.isGettingStone());

        for (int i = 0; i < 1000; i++) {
            if (!stonemason.isGettingStone()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(stonemason.isGettingStone());
    }

    public static void waitForSignToDisappear(GameMap map, Sign sign0) throws Exception {
        assertTrue(map.isSignAtPoint(sign0.getPosition()));

        for (int i = 0; i < 5000; i++) {
            if (!map.isSignAtPoint(sign0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isSignAtPoint(sign0.getPosition()));
    }

    public static void waitForFarmerToHarvestCrop(GameMap map, Farmer farmer, Crop crop0) throws Exception {
        assertTrue(farmer.isHarvesting());
        assertEquals(farmer.getPosition(), crop0.getPosition());
        assertTrue(map.isCropAtPoint(crop0.getPosition()));

        for (int i = 0; i < 1000; i++) {
            if (!farmer.isHarvesting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(farmer.isHarvesting());
        assertEquals(map.getCropAtPoint(crop0.getPosition()).getGrowthState(), HARVESTED);
        assertEquals(farmer.getCargo().getMaterial(), WHEAT);
    }

    public static void waitForWorkerToGoToPoint(GameMap map, Worker worker, Point point) throws Exception {
        for (int i = 0; i < 5000; i++) {
            if (worker.isExactlyAtPoint() && worker.getPosition().equals(point)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isExactlyAtPoint());
        assertEquals(worker.getPosition(), point);
    }

    public static void waitForWorkerToSetTarget(GameMap map, Worker worker, Point point) throws Exception {
        for (int i = 0; i < 5000; i++) {
            if (point.equals(worker.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getTarget(), point);
    }

    public static void waitForHarvestedCropToDisappear(GameMap map, Crop crop0) throws Exception {
        assertTrue(map.isCropAtPoint(crop0.getPosition()));
        assertEquals(crop0.getGrowthState(), HARVESTED);

        for (int i = 0; i < 5000; i++) {
            if (!map.isCropAtPoint(crop0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isCropAtPoint(crop0.getPosition()));
    }

    public static void waitForTreeToGetCutDown(Tree tree0, GameMap map) throws Exception {
        assertTrue(map.isTreeAtPoint(tree0.getPosition()));
        assertEquals(map.getTreeAtPoint(tree0.getPosition()), tree0);

        for (int i = 0; i < 1000; i++) {
            if (!map.isTreeAtPoint(tree0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isTreeAtPoint(tree0.getPosition()));
    }

    public static void waitForWorkerToBeOutside(Worker worker, GameMap map) throws Exception {
        assertTrue(worker.isInsideBuilding());

        for (int i = 0; i < 1000; i++) {

            if (!worker.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(worker.isInsideBuilding());
    }

    public static void waitForUpgradeToFinish(Building building) throws Exception {
        GameMap map = building.getMap();

        assertTrue(building.isUpgrading());

        for (int i = 0; i < 1000; i++) {
            if (!map.getBuildingAtPoint(building.getPosition()).isUpgrading()) {
                break;
            }

            assertEquals(map.getBuildingAtPoint(building.getPosition()), building);
            assertTrue(building.isUpgrading());
            assertTrue(map.getBuildingAtPoint(building.getPosition()).isUpgrading());

            map.stepTime();
        }

        assertFalse(map.getBuildingAtPoint(building.getPosition()).isUpgrading());
    }

    public static class GameViewMonitor implements PlayerGameViewMonitor {

        private final List<GameChangesList> gameChanges;
        private final HashMap<Point, AvailableConstruction> availableConstruction;

        public GameViewMonitor() {
            gameChanges = new ArrayList<>();
            availableConstruction = new HashMap<>();
        }

        @Override
        public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {
            gameChanges.add(gameChangesList);

            /* Update the monitoring of available construction */
            GameMap map = player.getMap();
            Map<Point, Size> availableBuildingsOnMap = map.getAvailableHousePoints(player);
            Collection<Point> availableFlagsOnMap = map.getAvailableFlagPoints(player);
            List<Point> availableMinesOnMap = map.getAvailableMinePoints(player);

            for (Point point : gameChangesList.getChangedAvailableConstruction()) {

                AvailableConstruction.PossibleBuildings possibleBuilding = NO_BUILDING_POSSIBLE;
                AvailableConstruction.PossibleFlag possibleFlag = NO_FLAG_POSSIBLE;

                if (!availableConstruction.containsKey(point)) {
                    availableConstruction.put(point, new AvailableConstruction(NO_BUILDING_POSSIBLE, NO_FLAG_POSSIBLE, point));
                }

                if (availableBuildingsOnMap.containsKey(point)) {
                    Size size = availableBuildingsOnMap.get(point);

                    if (size == LARGE) {
                        possibleBuilding = LARGE_POSSIBLE;
                    } else if (size == MEDIUM) {
                        possibleBuilding = MEDIUM_POSSIBLE;
                    } else if (size == SMALL) {
                        possibleBuilding = SMALL_POSSIBLE;
                    }
                }

                if (availableFlagsOnMap.contains(point)) {
                    possibleFlag = FLAG_POSSIBLE;
                }

                if (availableMinesOnMap.contains(point)) {
                    possibleBuilding = MINE_POSSIBLE;
                }

                availableConstruction.get(point).setAvailableBuilding(possibleBuilding);

                if (possibleFlag == FLAG_POSSIBLE) {
                    availableConstruction.get(point).setFlagPossible();
                } else {
                    availableConstruction.get(point).setFlagNotPossible();
                }
            }
        }

        public List<GameChangesList> getEvents() {
            return gameChanges;
        }

        public GameChangesList getLastEvent() {
            if (gameChanges.isEmpty()) {
                return null;
            }

            return gameChanges.get(gameChanges.size() - 1);
        }

        public List<GameChangesList> getEventsAfterEvent(GameChangesList gameChangesEvent) {
            if (gameChangesEvent.equals(getLastEvent())) {
                return Collections.EMPTY_LIST;
            }

            int index = this.gameChanges.indexOf(gameChangesEvent);

            return gameChanges.subList(index + 1, gameChanges.size() - 1);

        }

        public void setAvailableConstruction(Map<Point, Size> availableHousePoints, Collection<Point> availableFlagPoints, List<Point> availableMinePoints) {
            availableConstruction.clear();

            for (Map.Entry<Point, Size> entry : availableHousePoints.entrySet()) {
                if (entry.getValue() == LARGE) {
                    availableConstruction.put(entry.getKey(), new AvailableConstruction(AvailableConstruction.PossibleBuildings.LARGE_POSSIBLE, AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE, entry.getKey()));
                } else if (entry.getValue() == MEDIUM) {
                    availableConstruction.put(entry.getKey(), new AvailableConstruction(AvailableConstruction.PossibleBuildings.MEDIUM_POSSIBLE, AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE, entry.getKey()));
                } else if (entry.getValue() == Size.SMALL) {
                    availableConstruction.put(entry.getKey(), new AvailableConstruction(AvailableConstruction.PossibleBuildings.SMALL_POSSIBLE, AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE, entry.getKey()));
                }
            }

            for (Point flagPoint : availableFlagPoints) {
                AvailableConstruction availableConstructionAtPoint = availableConstruction.get(flagPoint);

                if (availableConstructionAtPoint == null) {
                    availableConstruction.put(flagPoint, new AvailableConstruction(NO_BUILDING_POSSIBLE, FLAG_POSSIBLE, flagPoint));
                } else {
                    availableConstruction.get(flagPoint).setFlagPossible();
                }
            }

            for (Point minePoint : availableMinePoints) {
                AvailableConstruction availableConstructionAtPoint = availableConstruction.get(minePoint);

                if (availableConstructionAtPoint == null) {
                    availableConstruction.put(minePoint, new AvailableConstruction(AvailableConstruction.PossibleBuildings.MINE_POSSIBLE, AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE, minePoint));
                } else {
                    availableConstruction.get(minePoint).setAvailableBuilding(AvailableConstruction.PossibleBuildings.MINE_POSSIBLE);
                }
            }
        }

        public void assertMonitoredAvailableConstructionMatchesWithMap(GameMap map, Player player0) {

            Map<Point, Size> availableBuildingsOnMap = map.getAvailableHousePoints(player0);
            Collection<Point> availableFlagsOnMap = map.getAvailableFlagPoints(player0);
            List<Point> availableMinesOnMap = map.getAvailableMinePoints(player0);

            /* Run monitored against real */
            for (Map.Entry<Point, AvailableConstruction> entry : availableConstruction.entrySet()) {

                if (entry.getValue().getAvailableBuilding() == NO_BUILDING_POSSIBLE) {
                    assertFalse(availableBuildingsOnMap.containsKey(entry.getKey()));
                } else if (entry.getValue().getAvailableBuilding() == AvailableConstruction.PossibleBuildings.LARGE_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(entry.getKey()), Size.LARGE);
                } else if (entry.getValue().getAvailableBuilding() == AvailableConstruction.PossibleBuildings.MEDIUM_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(entry.getKey()), MEDIUM);
                } else if (entry.getValue().getAvailableBuilding() == AvailableConstruction.PossibleBuildings.SMALL_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(entry.getKey()), Size.SMALL);
                }

                if (entry.getValue().getAvailableFlag() == FLAG_POSSIBLE) {
                    assertTrue(availableFlagsOnMap.contains(entry.getKey()));
                } else {
                    assertFalse(availableFlagsOnMap.contains(entry.getKey()));
                }

                if (entry.getValue().getAvailableBuilding() == MINE_POSSIBLE) {
                    assertTrue(availableMinesOnMap.contains(entry.getKey()));
                } else {
                    assertFalse(availableMinesOnMap.contains(entry.getKey()));
                }
            }

            /* Run real against monitored */
            for (Map.Entry<Point, Size> entry : map.getAvailableHousePoints(player0).entrySet()) {
                if (entry.getValue() == LARGE) {
                    assertNotNull(availableConstruction.get(entry.getKey()));
                    assertEquals(
                            availableConstruction.get(entry.getKey()).getAvailableBuilding(),
                            AvailableConstruction.PossibleBuildings.LARGE_POSSIBLE
                    );
                } else if (entry.getValue() == MEDIUM) {
                    assertEquals(
                            availableConstruction.get(entry.getKey()).getAvailableBuilding(),
                            AvailableConstruction.PossibleBuildings.MEDIUM_POSSIBLE
                    );
                } else if (entry.getValue() == SMALL) {
                    assertEquals(
                            availableConstruction.get(entry.getKey()).getAvailableBuilding(),
                            AvailableConstruction.PossibleBuildings.SMALL_POSSIBLE
                    );
                } else {
                    assertEquals(
                            availableConstruction.get(entry.getKey()).getAvailableBuilding(),
                            NO_BUILDING_POSSIBLE
                    );
                }
            }

            for (Point flagPoint : availableFlagsOnMap) {
                assertEquals(availableConstruction.get(flagPoint).getAvailableFlag(), FLAG_POSSIBLE);
            }

            for (Point minePoint : availableMinesOnMap) {
                assertEquals(availableConstruction.get(minePoint).getAvailableBuilding(), MINE_POSSIBLE);
            }
        }
    }
}
