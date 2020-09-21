package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Catapult;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Hunter;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
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
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Material.AXE;
import static org.appland.settlers.model.Material.BOW;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PICK_AXE;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.SHOVEL;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.TONGS;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Vegetation.SWAMP;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.LARGE_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.MEDIUM_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.MINE_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.NO_BUILDING_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.SMALL_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleFlag.FLAG_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Utils {

    public static void fastForward(int time, GameMap map) throws InvalidRouteException, InvalidUserActionException {

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

    public static void fillUpInventory(Storehouse storehouse, Material material, int amount) {
        Cargo cargo = new Cargo(material, null);

        for (int i = 0; i < amount; i++) {
            storehouse.putCargo(cargo);
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

    public static void fastForwardUntilWorkerReachesPoint(GameMap map, Worker worker, Point target) throws InvalidRouteException, InvalidUserActionException {
        assertNotNull(target);
        assertNotNull(worker);
        assertNotNull(map);

        for (int i = 0; i < 50000; i++) {

            if (worker.isAt(target)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getPosition(), target);
        assertTrue(worker.isAt(target));
    }

    public static <T extends Worker> T occupyBuilding(T worker, Building building) throws InvalidRouteException {
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
        map.surroundPointWithMineral(point, GOLD, size);
    }

    public static void putIronAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.surroundPointWithMineral(point, IRON, size);
    }

    public static void putCoalAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.surroundPointWithMineral(point, COAL, size);
    }

    public static void putGraniteAtSurroundingTiles(Point point, Size size, GameMap map) {
        map.surroundPointWithMineral(point, STONE, size);
    }

    public static void createMountainWithinRadius(Point point, int radius, GameMap map) {
        for (Point p : map.getPointsWithinRadius(point, radius - 1)) {
            surroundPointWithVegetation(p, MOUNTAIN, map);
        }
    }

    public static void putMineralWithinRadius(Material mineral, Point point1, int radius, GameMap map) {
        for (Point p : map.getPointsWithinRadius(point1, radius - 1)) {
            map.surroundPointWithMineral(p, mineral, LARGE);
        }
    }

    public static Courier occupyRoad(Road road, GameMap map) throws Exception {
        Courier courier = new Courier(road.getPlayer(), map);

        map.placeWorker(courier, road.getFlags()[0]);
        courier.assignToRoad(road);

        assertEquals(road.getCourier(), courier);

        return courier;
    }

    public static void adjustInventoryTo(Storehouse storehouse, Material material, int amount) {
        GameMap map = storehouse.getMap();

        for (int i = 0; i < 1000; i++) {

            if (storehouse.getAmount(material) == amount) {
                break;
            }

            if (storehouse.getAmount(material) > amount) {

                if (isSoldier(material)) {
                    storehouse.retrieveMilitary(material);
                } else {
                    storehouse.retrieve(material);
                }
            } else if (storehouse.getAmount(material) < amount) {
                storehouse.putCargo(new Cargo(material, map));
            }
        }

        assertEquals(storehouse.getAmount(material), amount);
    }

    private static boolean isSoldier(Material material) {
        if (material == PRIVATE) {
            return true;
        }

        if (material == PRIVATE_FIRST_CLASS) {
            return true;
        }

        if (material == SERGEANT) {
            return true;
        }

        if (material == OFFICER) {
            return true;
        }

        if (material == GENERAL) {
            return true;
        }

        return false;
    }

    public static void constructHouse(Building building) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = building.getMap();

        assertTrue(building.isUnderConstruction());

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

            map.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static Cargo fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker, Material... materials) throws Exception {

        Set<Material> setOfMaterials = new HashSet<>(Arrays.asList(materials));

        for (int j = 0; j < 20000; j++) {
            if (worker.getCargo() != null && setOfMaterials.contains(worker.getCargo().getMaterial())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());
        assertTrue(setOfMaterials.contains(worker.getCargo().getMaterial()));

        return worker.getCargo();
    }

    public static void fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker, Cargo cargo) throws InvalidRouteException, InvalidUserActionException {
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

    public static void waitForMilitaryBuildingToGetPopulated(Building building, int nr) throws InvalidRouteException, InvalidUserActionException {
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

    public static Military occupyMilitaryBuilding(Military.Rank rank, Building building) throws InvalidRouteException {
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

        Military soldier = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Military && !worker.isInsideBuilding() && worker.getPlayer().equals(player)) {
                soldier = (Military)worker;
            }
        }

        return soldier;
    }

    public static List<Military> findSoldiersOutsideBuilding(Player player) {
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
                workersFound.add(aClass.cast(worker));
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
                    workers.add(type.cast(worker));
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

    public static <T extends Worker> T waitForWorkerOutsideBuilding(Class<T> type, Player player) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = player.getMap();

        for (int i = 0; i < 1000; i++) {

            for (Worker worker : map.getWorkers()) {
                if (worker.getClass().equals(type) && !worker.isInsideBuilding() && player.equals(worker.getPlayer())) {
                    return type.cast(worker);
                }
            }

            map.stepTime();
        }

        fail();

        return null;
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

            if (projectile.isArrived()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(projectile.isArrived());
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

    static void deliverCargo(Building building, Material material) {
        GameMap map = building.getMap();
        Cargo cargo = new Cargo(material, map);

        building.promiseDelivery(material);

        building.putCargo(cargo);
    }

    static Cargo fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker) throws InvalidRouteException, InvalidUserActionException {

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

            if (crop.getGrowthState() == FULL_GROWN) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), FULL_GROWN);
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

    static void surroundPointWithVegetation(Point point, Vegetation vegetation, GameMap map) {
        map.surroundWithVegetation(point, vegetation);

        assertEquals(map.getTileUpLeft(point), vegetation);
        assertEquals(map.getTileAbove(point), vegetation);
        assertEquals(map.getTileUpRight(point), vegetation);
        assertEquals(map.getTileDownRight(point), vegetation);
        assertEquals(map.getTileBelow(point), vegetation);
        assertEquals(map.getTileDownLeft(point), vegetation);
    }

    public static void verifyWorkerStaysAtHome(Worker worker, GameMap map) throws InvalidRouteException, InvalidUserActionException {

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

    public static void fillMapWithVegetation(GameMap map, Vegetation vegetation) {
        map.fillMapWithVegetation(vegetation);
    }

    public static Courier waitForRoadToGetAssignedCourier(GameMap map, Road road0) throws InvalidRouteException, InvalidUserActionException {
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

    public static Set<Courier> waitForRoadsToGetAssignedCouriers(GameMap map, Road... roads) throws InvalidRouteException, InvalidUserActionException {
        Set<Courier> couriers = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            for (Road road : roads) {

                if (road.getCourier() == null) {
                    continue;
                }

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

    public static void waitForBuildingToBeConstructed(Building building) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = building.getMap();

        for (int i = 0; i < 4000; i++) {

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

    public static void waitForTreeConservationProgramToActivate(Player player0) throws Exception {
        GameMap map = player0.getMap();

        for (int i = 0; i < 1000; i++) {

            if (player0.isTreeConservationProgramActive()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(player0.isTreeConservationProgramActive());
    }

    public static void waitForStonesToDisappear(GameMap map, Stone... stones) throws Exception {

        for (int i = 0; i < 10000; i++) {
            boolean allStonesGone = true;

            for (Stone stone : stones) {
                if (map.isStoneAtPoint(stone.getPosition())) {
                    allStonesGone = false;

                    break;
                }
            }

            if (allStonesGone) {
                break;
            }

            map.stepTime();
        }

        for (Stone stone : stones) {
            assertFalse(map.isStoneAtPoint(stone.getPosition()));
        }
    }

    public static Point getPointAtMaxX(Collection<Point> points) {
        Point pointAtMaxX = new Point(0, 0);

        for (Point point : points) {
            if (point.x > pointAtMaxX.x) {
                pointAtMaxX = point;
            }
        }

        return pointAtMaxX;
    }

    public static Point getPointAtMaxY(Collection<Point> points) {
        Point pointAtMaxY = new Point(0, 0);

        for (Point point : points) {
            if (point.y > pointAtMaxY.y) {
                pointAtMaxY = point;
            }
        }

        return pointAtMaxY;
    }

    public static Collection<Point> getPointsForX(Collection<Point> fieldOfView, int x) {
        List<Point> points = new ArrayList<>();

        for (Point point : fieldOfView) {
            if (point.x == x) {
                points.add(point);
            }
        }

        return points;
    }

    public static Point getPointAtMinX(Collection<Point> points) {
        Point pointAtMinX = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (Point point : points) {
            if (point.x < pointAtMinX.x) {
                pointAtMinX = point;
            }
        }

        return pointAtMinX;
    }

    public static Point getPointAtMinY(Collection<Point> points) {
        Point pointAtMinY = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (Point point : points) {
            if (point.y < pointAtMinY.y) {
                pointAtMinY = point;
            }
        }

        return pointAtMinY;
    }

    static Set<Point> getHexagonBorder(Point point0, int radius) {
        Set<Point> hexagonBorder = new HashSet<>();

        int upperY = point0.y;
        int lowerY = point0.y;
        for (int x = point0.x - (radius * 2); x < point0.x - radius; x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY++;
            lowerY--;
        }

        upperY = point0.y + radius;
        lowerY = point0.y - radius;
        for (int x = point0.x + radius; x <= point0.x + (radius * 2); x++) {
            hexagonBorder.add(new Point(x, upperY));
            hexagonBorder.add(new Point(x, lowerY));

            upperY--;
            lowerY++;
        }

        for (int x = point0.x - radius; x < point0.x + radius; x += 2) {
            hexagonBorder.add(new Point(x, point0.y + radius));
            hexagonBorder.add(new Point(x, point0.y - radius));
        }
        return hexagonBorder;
    }

    public static void printMaxMinPoints(Collection<Point> points) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        Point pointAtMinX = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointAtMaxX = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Point pointAtMinY = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointAtMaxY = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (Point point : points) {
            if (point.x > maxX) {
                maxX = point.x;
                pointAtMaxX = point;
            }

            if (point.x < minX) {
                minX = point.x;
                pointAtMinX = point;
            }

            if (point.y > maxY) {
                maxY = point.y;
                pointAtMaxY = point;
            }

            if (point.y < minY) {
                minY = point.y;
                pointAtMinY = point;
            }

        }

        System.out.println(" -- Right: " + maxX + " " + pointAtMaxX);
        System.out.println(" -- Left:  " + minX + " " + pointAtMinX);
        System.out.println(" -- Upper: " + maxY + " " + pointAtMaxY);
        System.out.println(" -- Lower: " + minY + " " + pointAtMinY);
    }

    public static void printAdjacentPointsForX(Collection<Point> borderPoints, int x) {
        for (Point point : borderPoints) {
            if (point.x == x || point.x == x - 1 || point.x == x + 1) {
                System.out.print(point + " ");
            }
        }

        System.out.println();
    }

    static void placeAndOccupyBarracks(Player player0, Point point88) throws Exception {
        GameMap map = player0.getMap();
        Building barracks9 = map.placeBuilding(new Barracks(player0), point88);

        /* Finish construction of barracks */
        constructHouse(barracks9);

        /* Occupy barracks */
        occupyMilitaryBuilding(PRIVATE_RANK, barracks9);
    }

    public static void printMinYAdjacentToX(Collection<Point> landInPoints, int x) {
        Point pointMinYLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointMinY = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointMinYRight = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (Point point : landInPoints) {
            if (point.x == x - 1 && pointMinYLeft.y > point.y) {
                pointMinYLeft = point;
            }

            if (point.x == x && pointMinY.y > point.y) {
                pointMinY = point;
            }

            if (point.x == x + 1 && pointMinYRight.y > point.y) {
                pointMinYRight = point;
            }
        }

        System.out.println(pointMinYLeft + " " + pointMinY + " " + pointMinYRight);
    }
    public static void waitForCouriersToBeIdle(GameMap map, Courier... couriers) throws InvalidRouteException, InvalidUserActionException {
        List<Courier> listOfCouriers = new ArrayList<>();

        listOfCouriers.addAll(Arrays.asList(couriers));

        waitForCouriersToBeIdle(map, listOfCouriers);
    }

    public static void waitForCouriersToBeIdle(GameMap map, Collection<Courier> couriers) throws InvalidRouteException, InvalidUserActionException {

        for (int i = 0; i < 5000; i++) {
            boolean allIdle = true;

            for (Courier courier : couriers) {
                if (!courier.isIdle()) {
                    allIdle = false;

                    break;
                }
            }

            if (allIdle) {
                break;
            }

            map.stepTime();
        }

        for (Courier courier : couriers) {
            assertTrue(courier.isIdle());
        }
    }

    public static Cargo placeCargo(GameMap map, Material material, Flag flag, Building building) throws InvalidRouteException {
        Cargo cargo = new Cargo(material, map);

        cargo.setPosition(flag.getPosition());
        cargo.setTarget(building);

        flag.promiseCargo(cargo);
        flag.putCargo(cargo);

        return cargo;
    }

    public static void placeCargos(GameMap map, Material material, int amount, Flag flag, Building building) throws InvalidRouteException {
        for (int i = 0; i < amount; i++) {
            placeCargo(map, material, flag, building);
        }
    }

    public static void waitForBuildingToGetAmountOfMaterial(Building building, Material material, int targetAmount) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = building.getMap();

        for (int i = 0; i < 10000; i++) {
            if (building.getAmount(material) == targetAmount) {
                break;
            }

            map.stepTime();
        }

        assertEquals(building.getAmount(material), targetAmount);
    }

    public static void putCargoToBuilding(Building mill, Material material) {
        mill.promiseDelivery(material);
        mill.putCargo(new Cargo(material, mill.getMap()));
    }

    public static void waitForFlagToGetStackedCargo(GameMap map, Flag flag, int amount) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 20000; i++) {
            if (flag.getStackedCargo().size() == amount) {
                break;
            }

            map.stepTime();
        }

        assertEquals(flag.getStackedCargo().size(), amount);
    }

    public static void waitForBuildingsToBeConstructed(Building... buildings) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = buildings[0].getMap();

        for (int i = 0; i < 10000; i++) {
            boolean allDone = true;

            for (Building building : buildings) {
                if (!building.isReady()) {
                    allDone = false;

                    break;
                }
            }

            if (allDone) {
                break;
            }

            map.stepTime();
        }

        for (Building building : buildings) {
            assertTrue(building.isReady());
        }
    }

    public static void waitForNonMilitaryBuildingsToGetPopulated(Building... buildings) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = buildings[0].getMap();

        for (int i = 0; i < 4000; i++) {
            boolean allPopulated = true;

            for (Building building : buildings) {
                if (!building.isOccupied()) {
                    allPopulated = false;

                    break;
                }
            }

            if (allPopulated) {
                break;
            }

            map.stepTime();
        }

        for (Building building : buildings) {
            assertTrue(building.isOccupied());
            assertNotNull(building.getWorker());
        }
    }

    public static void verifyWorkerWalksToTargetOnRoads(GameMap map, Worker worker, Point point) throws Exception {
        for (int i = 0; i < 10000; i++) {

            if (worker.isExactlyAtPoint() && point.equals(worker.getPosition())) {
                break;
            }

            if (worker.isExactlyAtPoint()) {
                assertTrue(map.isRoadAtPoint(worker.getPosition()));
            }

            map.stepTime();
        }

        assertTrue(worker.isExactlyAtPoint());
        assertEquals(worker.getPosition(), point);
    }

    public static void waitForWorkerToBeInside(Worker worker, GameMap map) throws Exception {
        for (int i = 0; i < 10000; i++) {
            if (worker.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isInsideBuilding());
    }

    public static void verifyWorkerCarriesTool(Worker worker) {
        assertNotNull(worker.getCargo());

        switch (worker.getCargo().getMaterial()) {
            case AXE:
            case SHOVEL:
            case PICK_AXE:
            case FISHING_ROD:
            case BOW:
            case SAW:
            case CLEAVER:
            case ROLLING_PIN:
            case CRUCIBLE:
            case TONGS:
            case SCYTHE:
                break;
            default:
                fail();
        }
    }

    public static void blockDeliveryOfTools(Storehouse storehouse) {
        storehouse.blockDeliveryOfMaterial(AXE);
        storehouse.blockDeliveryOfMaterial(SHOVEL);
        storehouse.blockDeliveryOfMaterial(PICK_AXE);
        storehouse.blockDeliveryOfMaterial(FISHING_ROD);
        storehouse.blockDeliveryOfMaterial(BOW);
        storehouse.blockDeliveryOfMaterial(SAW);
        storehouse.blockDeliveryOfMaterial(CLEAVER);
        storehouse.blockDeliveryOfMaterial(ROLLING_PIN);
        storehouse.blockDeliveryOfMaterial(CRUCIBLE);
        storehouse.blockDeliveryOfMaterial(TONGS);
        storehouse.blockDeliveryOfMaterial(SCYTHE);
    }

    public static void verifyWorkerDoesNotCarryTool(Worker worker) {
        switch (worker.getCargo().getMaterial()) {
            case BOW:
            case SAW:
                fail();
                break;
            default:
                break;
        }
    }

    public static void waitForWorkersToDisappearFromMap(List<Worker> workers, GameMap map) throws InvalidRouteException, InvalidUserActionException {

        boolean stillOnMap;
        for (int i = 0; i < 5000; i++) {

            stillOnMap = false;

            for (Worker worker : workers) {
                if (map.getWorkers().contains(worker)) {
                    stillOnMap = true;

                    break;
                }
            }

            if (!stillOnMap) {
                break;
            }

            map.stepTime();
        }

        for (Worker worker : workers) {
            assertFalse(map.getWorkers().contains(worker));
        }
    }

    public static void removeAllFish(GameMap map, Point point1) {
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point1) == 0) {
                break;
            }

            map.catchFishAtPoint(point1);
        }

        assertEquals(map.getAmountFishAtPoint(point1), 0);
    }

    public static void removeAllFishExceptOne(GameMap map, Point point0) {
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) <= 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        assertEquals(map.getAmountFishAtPoint(point0), 1);
    }

    public static void waitForFishermanToStopFishing(Fisherman fisherman, GameMap map) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (!fisherman.isFishing()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(fisherman.isFishing());
    }

    public static void waitForPointToBeNext(GameMap map, Courier courier, Point position) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {

            if (!courier.isExactlyAtPoint() && courier.getNextPoint().equals(position)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(courier.isExactlyAtPoint());
        assertEquals(courier.getNextPoint(), position);
    }

    public static void waitForMilitaryBuildingsToGetPopulated(Building... buildings) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = buildings[0].getMap();

        for (int i = 0; i < 10000; i++) {
            boolean allPopulated = true;

            for (Building building : buildings) {
                if (!building.isOccupied()) {
                    allPopulated = false;

                    break;
                }
            }

            if (allPopulated) {
                break;
            }

            map.stepTime();
        }

        for (Building building : buildings) {
            assertTrue(building.isOccupied());
        }
    }

    public static void constructHouses(Building... buildings) throws InvalidRouteException, InvalidUserActionException {
        GameMap map = buildings[0].getMap();

        for (Building building : buildings) {
            assertTrue(building.isUnderConstruction());

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
        }

        for (int i = 0; i < 500; i++) {

            boolean allReady = true;

            for (Building building : buildings) {
                if (!building.isReady()) {
                    allReady = false;

                    break;
                }
            }

            if (allReady) {
                break;
            }

            map.stepTime();
        }

        for (Building building : buildings) {
            assertTrue(building.isReady());
        }
    }

    public static void waitForWorkerToBeExactlyOnPoint(Worker worker, GameMap map) throws InvalidRouteException, InvalidUserActionException {

        for (int i = 0; i < 500; i++) {
            if (worker.isExactlyAtPoint()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isExactlyAtPoint());
    }

    public static void verifyWorkerWalksOnPath(GameMap map, Worker worker, Point... points) throws InvalidRouteException, InvalidUserActionException {

        System.out.println(Arrays.asList(points));

        for (Point point : points) {

            System.out.println(point);

            assertTrue(worker.isExactlyAtPoint());
            assertEquals(point, worker.getPosition());

            if (worker.getPosition().equals(points[points.length - 1])) {
                break;
            }

            map.stepTime();

            for (int i = 0; i < 20; i++) {

                if (worker.isExactlyAtPoint()) {
                    break;
                }

                map.stepTime();
            }

            waitForWorkerToBeExactlyOnPoint(worker, map);
        }
    }

    public static void verifyWorkerDoesNotMove(GameMap map, Courier courier, int time) throws InvalidRouteException, InvalidUserActionException {
        Point point = courier.getPosition();

        for (int i = 0; i < time; i++) {
            assertTrue(courier.isExactlyAtPoint());

            map.stepTime();
        }

        assertEquals(point, courier.getPosition());
    }

    public static void fastForwardUntilWorkersCarryCargo(GameMap map, Material material, Worker... workers) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {

            boolean allWorkersCarryCargo = true;

            for (Worker worker : workers) {
                if (worker.getCargo() == null || worker.getCargo().getMaterial() != material) {
                    allWorkersCarryCargo = false;

                    break;
                }
            }

            if (allWorkersCarryCargo) {
                break;
            }

            map.stepTime();
        }

        for (Worker worker : workers) {
            assertNotNull(worker.getCargo());
            assertEquals(worker.getCargo().getMaterial(), material);
        }
    }

    public static void verifyWorkersDoNotMove(GameMap map, Worker... workers) throws InvalidRouteException, InvalidUserActionException {
        Map<Worker, Point> positions = new HashMap<>();

        for (Worker worker : workers) {
            positions.put(worker, worker.getPosition());
        }

        for (int i = 0; i < 500; i++) {

            map.stepTime();

            for (Worker worker : workers) {
                assertEquals(worker.getPosition(), positions.get(worker));
            }
        }
    }

    public static void waitForCouriersToGetBlocked(GameMap map, Courier... couriers) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 20000; i++) {

            boolean allCouriersBlocked = true;

            for (Courier courier : couriers) {

                if (courier.getCargo() != null && courier.getTarget() == null && !map.isFlagAtPoint(courier.getPosition())) {
                    continue;
                }

                allCouriersBlocked = false;

                break;
            }

            if (allCouriersBlocked) {
                break;
            }

            map.stepTime();
        }

        for (Courier courier : couriers) {
            assertNotNull(courier.getCargo());
            assertNull(courier.getTarget());
            assertFalse(map.isFlagAtPoint(courier.getPosition()));
        }
    }

    public static void deliverCargos(Building building, Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            deliverCargo(building, material);
        }
    }

    public static Cargo retrieveOneCargo(Flag flag) {
        Cargo cargo = flag.getStackedCargo().get(0);

        flag.retrieveCargo(cargo);

        return cargo;
    }

    public static void fastForwardUntilWorkersCarryCargo(GameMap map, Worker... workers) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {

            boolean allWorkersCarryCargo = true;

            for (Worker worker : workers) {
                if (worker.getCargo() != null) {
                    continue;
                }

                allWorkersCarryCargo = false;

                break;
            }

            if (allWorkersCarryCargo) {
                break;
            }

            map.stepTime();
        }

        for (Worker worker : workers) {
            assertNotNull(worker.getCargo());
        }
    }

    public static void waitForMilitaryToStopFighting(GameMap map, Military military) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 2000; i++) {
            if (!military.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(military.isFighting());
    }

    public static void waitForMilitaryToStartFighting(GameMap map, Military military) throws InvalidRouteException, InvalidUserActionException {
        for (int i = 0; i < 2000; i++) {
            if (military.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(military.isFighting());
    }

    public static class GameViewMonitor implements PlayerGameViewMonitor {

        private final List<GameChangesList> gameChanges;
        private final HashMap<Point, AvailableConstruction> availableConstruction;

        public GameViewMonitor() {
            gameChanges = new ArrayList<>();
            availableConstruction = new HashMap<>();
        }

        // FIXME: HOTSPOT
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
                return new ArrayList<>();
            }

            int index = gameChanges.indexOf(gameChangesEvent);

            return gameChanges.subList(index + 1, gameChanges.size() - 1);

        }

        public void setAvailableConstruction(Map<Point, Size> availableHousePoints, Collection<Point> availableFlagPoints, List<Point> availableMinePoints) {
            availableConstruction.clear();

            for (Map.Entry<Point, Size> entry : availableHousePoints.entrySet()) {
                if (entry.getValue() == LARGE) {
                    availableConstruction.put(entry.getKey(), new AvailableConstruction(LARGE_POSSIBLE, NO_FLAG_POSSIBLE, entry.getKey()));
                } else if (entry.getValue() == MEDIUM) {
                    availableConstruction.put(entry.getKey(), new AvailableConstruction(MEDIUM_POSSIBLE, NO_FLAG_POSSIBLE, entry.getKey()));
                } else if (entry.getValue() == SMALL) {
                    availableConstruction.put(entry.getKey(), new AvailableConstruction(SMALL_POSSIBLE, NO_FLAG_POSSIBLE, entry.getKey()));
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
                    availableConstruction.put(minePoint, new AvailableConstruction(MINE_POSSIBLE, NO_FLAG_POSSIBLE, minePoint));
                } else {
                    availableConstruction.get(minePoint).setAvailableBuilding(MINE_POSSIBLE);
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
                } else if (entry.getValue().getAvailableBuilding() == LARGE_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(entry.getKey()), LARGE);
                } else if (entry.getValue().getAvailableBuilding() == MEDIUM_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(entry.getKey()), MEDIUM);
                } else if (entry.getValue().getAvailableBuilding() == SMALL_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(entry.getKey()), SMALL);
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
                            LARGE_POSSIBLE
                    );
                } else if (entry.getValue() == MEDIUM) {
                    assertEquals(
                            availableConstruction.get(entry.getKey()).getAvailableBuilding(),
                            MEDIUM_POSSIBLE
                    );
                } else if (entry.getValue() == SMALL) {
                    assertEquals(
                            availableConstruction.get(entry.getKey()).getAvailableBuilding(),
                            SMALL_POSSIBLE
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

    static Set<Point> getAreaInsideHexagon(int radius, Point position) {
        Set<Point> area = new HashSet<>();

        int xStart = position.x - radius;
        int xEnd = position.x + radius;

        for (int y = position.y - radius; y < position.y; y++) {
            for (int x = xStart; x <= xEnd; x += 2) {
                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }

        xStart = position.x - radius;
        xEnd = position.x + radius;

        for (int y = position.y + radius; y >= position.y; y--) {
            for (int x = xStart; x <= xEnd; x += 2) {
                area.add(new Point(x, y));
            }

            xStart--;
            xEnd++;
        }
        return area;
    }
}
