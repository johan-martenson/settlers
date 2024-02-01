package org.appland.settlers.test;

import org.appland.settlers.assets.CropType;
import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Builder;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Catapult;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.Farmer;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Hunter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Soldier;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Projectile;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Ship;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.StoneType;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Worker;
import org.appland.settlers.model.WorkerAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.DetailedVegetation.MOUNTAIN_1;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.*;
import static org.appland.settlers.test.AvailableConstruction.PossibleBuildings.*;
import static org.appland.settlers.test.AvailableConstruction.PossibleFlag.FLAG_POSSIBLE;
import static org.appland.settlers.test.AvailableConstruction.PossibleFlag.NO_FLAG_POSSIBLE;
import static org.junit.Assert.*;

public class Utils {

    public static void fastForward(int time, GameMap map) throws InvalidUserActionException {

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

    public static void fastForwardUntilWorkersReachTarget(GameMap map, Worker... workers) throws InvalidUserActionException {
        fastForwardUntilWorkersReachTarget(map, Arrays.asList(workers));
    }

    public static void fastForwardUntilWorkersReachTarget(GameMap map, List<Worker> workers) throws InvalidUserActionException {

        assertNotNull(map);
        assertFalse(workers.isEmpty());
        assertTrue(workers.stream().allMatch(Worker::isTraveling));

        for (int i = 0; i < 1000; i++) {
            if (workers.stream().allMatch(Worker::isArrived)) {
                break;
            }

            map.stepTime();
        }
    }

    public static void waitForSoldierToWinFight(Soldier soldier, GameMap map) throws InvalidUserActionException {
        assertTrue(soldier.isFighting());

        for (int i = 0; i < 2000; i++) {
            assertFalse(soldier.isDying());
            assertFalse(soldier.isDead());

            if (!soldier.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(soldier.isFighting());
        assertFalse(soldier.isDying());
        assertFalse(soldier.isDead());
    }

    public static void fastForwardUntilWorkerReachesPoint(GameMap map, Worker worker, Point target) throws InvalidUserActionException {
        assertNotNull(target);
        assertNotNull(worker);
        assertNotNull(map);

        for (int i = 0; i < 100000; i++) {

            if (Objects.equals(worker.getPosition(), target)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getPosition(), target);
    }

    public static <T extends Worker> T occupyBuilding(T worker, Building building) {
        GameMap map = building.getMap();

        map.placeWorker(worker, building);
        building.assignWorker(worker);
        worker.enterBuilding(building);

        assertEquals(building.getWorker(), worker);

        return worker;
    }

    public static void fastForwardUntilTreeIsGrown(Tree tree, GameMap map) throws InvalidUserActionException {

        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (tree.getSize() == TreeSize.FULL_GROWN) {
                break;
            }
        }

        assertEquals(tree.getSize(), TreeSize.FULL_GROWN);
    }

    public static void fastForwardUntilCropIsGrown(Crop crop, GameMap map) throws InvalidUserActionException {

        for (int i = 0; i < 1000; i++) {
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
        surroundPointWithVegetation(point, DetailedVegetation.WATER, map);
    }

    public static void surroundPointWithMinableMountain(Point point, GameMap map) {
        surroundPointWithVegetation(point, MOUNTAIN_1, map);
    }

    public static void surroundPointWithSwamp(Point point, GameMap map) {
        surroundPointWithVegetation(point, DetailedVegetation.SWAMP, map);
    }

    public static void fastForwardUntilBuildingIsConstructed(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();

        for (int i = 0; i < 10000; i++) {
            if (building.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static void fastForwardUntilBuildingIsOccupied(Building building) throws InvalidUserActionException {
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

    public static void createMinableMountainWithinRadius(Point point, int radius, GameMap map) {
        for (Point p : map.getPointsWithinRadius(point, radius - 1)) {
            surroundPointWithVegetation(p, MOUNTAIN_1, map);
        }
    }

    public static void putMineralWithinRadius(Material mineral, Point point1, int radius, GameMap map) {
        for (Point p : map.getPointsWithinRadius(point1, radius - 1)) {
            map.surroundPointWithMineral(p, mineral, LARGE);
        }
    }

    public static Courier occupyRoad(Road road, GameMap map) {
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
                    storehouse.retrieveSoldierFromInventory(material);
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

    public static void constructHouse(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();

        /* Assign builder */
        Builder builder = new Builder(building.getPlayer(), building.getMap());
        map.placeWorker(builder, building.getFlag());

        building.promiseBuilder(builder);
        builder.setTargetBuilding(building);

        assertEquals(builder.getTarget(), building.getPosition());

        fastForwardUntilWorkerReachesPoint(map, builder, building.getPosition());

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

    public static Cargo fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker, Material... materials) throws InvalidUserActionException {

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

    public static void fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker, Cargo cargo) throws InvalidUserActionException {
        for (int j = 0; j < 2000; j++) {
            if (cargo.equals(worker.getCargo())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getCargo(), cargo);
    }

    public static void fastForwardUntilWorkerProducesCargo(GameMap map, Worker worker) throws InvalidUserActionException {

        for (int i = 0; i < 300; i++) {
            if (worker.getCargo() != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());
    }

    public static void waitForMilitaryBuildingToGetPopulated(Building building, int nr) throws InvalidUserActionException {
        GameMap map = building.getMap();

        boolean populated = false;

        for (int i = 0; i < 1000; i++) {
            if (building.getNumberOfHostedSoldiers() == nr) {
                populated = true;

                break;
            }

            map.stepTime();
        }

        assertTrue(populated);
        assertEquals(building.getNumberOfHostedSoldiers(), nr);
    }

    public static void waitForMilitaryBuildingToGetPopulated(Building building) throws InvalidUserActionException {
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

    public static void verifyDeliveryOfMaterial(GameMap map, Road road) throws InvalidUserActionException {
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

    public static void verifyNoDeliveryOfMaterial(GameMap map, Road road) throws InvalidUserActionException {
        Courier courier = road.getCourier();

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == COIN) {
                fail();
            }

            map.stepTime();
        }
    }

    public static void occupyMilitaryBuilding(Soldier.Rank rank, int amount, Building building) {
        assertTrue(building.isReady());
        for (int i = 0; i < amount; i++) {
            occupyMilitaryBuilding(rank, building);
        }
    }

    public static Soldier occupyMilitaryBuilding(Soldier.Rank rank, Building building) {
        GameMap map = building.getMap();
        Player player = building.getPlayer();

        Soldier military = new Soldier(player, rank, map);

        map.placeWorker(military, building);

        building.promiseSoldier(military);

        military.enterBuilding(building);

        return military;
    }

    public static Soldier findMilitaryOutsideBuilding(Player player) {
        GameMap map = player.getMap();

        Soldier soldier = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier && !worker.isInsideBuilding() && worker.getPlayer().equals(player)) {
                soldier = (Soldier)worker;
            }
        }

        return soldier;
    }

    public static List<Soldier> findSoldiersOutsideBuilding(Player player) {
        GameMap map = player.getMap();
        List<Soldier> result = new LinkedList<>();

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier && !worker.isInsideBuilding() && worker.getPlayer().equals(player)) {
                result.add((Soldier)worker);
            }
        }

        return result;
    }

    public static void waitForWorkerToDisappear(Worker worker, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (!map.getWorkers().contains(worker)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    public static Soldier waitForMilitaryOutsideBuilding(Player player) throws InvalidUserActionException {
        GameMap map = player.getMap();

        for (int i = 0; i < 1000; i++) {
            Soldier military = findMilitaryOutsideBuilding(player);

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

    public static <T extends Worker> List<T> waitForWorkersOutsideBuilding(Class<T> type, int nr, Player player) throws InvalidUserActionException {
        GameMap map = player.getMap();
        List<T> workers = new LinkedList<>();

        for (int i = 0; i < 5000; i++) {

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

    public static <T extends Worker> T waitForWorkerOutsideBuilding(Class<T> type, Player player) throws InvalidUserActionException {
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

    public static <T extends Building> void waitForBuildingToDisappear(T building) throws InvalidUserActionException {
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

    static void waitForFightToStart(GameMap map, Soldier attacker, Soldier defender) throws InvalidUserActionException {

        for (int i = 0; i < 1000; i++) {
            if (attacker.isFighting() && defender.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(defender.isFighting());
        assertTrue(attacker.isFighting());
    }

    static Soldier getMainAttacker(Building building, Collection<Soldier> attackers) throws InvalidUserActionException {
        GameMap map = building.getMap();
        Soldier firstAttacker = null;

        for (Soldier military : attackers) {
            if (military.getTarget().equals(building.getFlag().getPosition())) {
                firstAttacker = military;

                break;
            }

            map.stepTime();
        }

        assertNotNull(firstAttacker);

        return firstAttacker;
    }

    static Projectile waitForCatapultToThrowProjectile(Catapult catapult) throws InvalidUserActionException {
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

    static void waitForProjectileToReachTarget(Projectile projectile, GameMap map) throws InvalidUserActionException {

        for (int i = 0; i < 1000; i++) {

            if (projectile.isArrived()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(projectile.isArrived());
    }

    static WildAnimal waitForAnimalToAppear(GameMap map) throws InvalidUserActionException {

        for (int i = 0; i < 2000; i++) {

            if (!map.getWildAnimals().isEmpty()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.getWildAnimals().isEmpty());

        return map.getWildAnimals().get(0);
    }

    static WildAnimal waitForWildAnimalCloseToPoint(Point point, GameMap map) throws InvalidUserActionException {
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

    static void waitForActorsToGetClose(Hunter hunter, WildAnimal animal, int distance, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {
            if (hunter.getPosition().distance(animal.getPosition()) <= distance) {
                break;
            }

            map.stepTime();
        }

        assertTrue(hunter.getPosition().distance(animal.getPosition()) <= distance);
    }

    static void fastForwardUntilWorkerCarriesNoCargo(GameMap map, Worker worker) throws InvalidUserActionException {

        for (int j = 0; j < 2000; j++) {
            if (worker.getCargo() == null) {
                break;
            }

            map.stepTime();
        }

        assertNull(worker.getCargo());
    }

    static void waitForCargoToReachTarget(GameMap map, Cargo cargo) throws InvalidUserActionException {

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

    static void waitUntilAmountIs(Building building, Material material, int amount) throws InvalidUserActionException {
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

    static Cargo fastForwardUntilWorkerCarriesCargo(GameMap map, Worker worker) throws InvalidUserActionException {

        for (int i = 0; i < 2000; i++) {

            if (worker.getCargo() != null) {
                return worker.getCargo();
            }

            map.stepTime();
        }

        fail();

        return null;
    }

    static void waitForCropToGetReady(GameMap map, Crop crop) throws InvalidUserActionException {

        for (int i = 0; i < 1000; i++) {

            if (crop.getGrowthState() == FULL_GROWN) {
                break;
            }

            map.stepTime();
        }

        assertEquals(crop.getGrowthState(), FULL_GROWN);
    }

    static Crop waitForFarmerToPlantCrop(GameMap map, Farmer farmer0) throws InvalidUserActionException {

        waitForFarmerToStartPlanting(map, farmer0);

        Point position = farmer0.getPosition();

        assertFalse(map.isCropAtPoint(position));

        waitForFarmerToStopPlanting(map, farmer0);

        assertTrue(map.isCropAtPoint(position));

        return map.getCropAtPoint(position);
    }

    private static void waitForFarmerToStopPlanting(GameMap map, Farmer farmer) throws InvalidUserActionException {

        for (int i = 0; i < 10000; i++) {

            if (!farmer.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(farmer.isPlanting());
    }

    private static void waitForFarmerToStartPlanting(GameMap map, Farmer farmer) throws InvalidUserActionException {

        for (int i = 0; i < 10000; i++) {

            if (farmer.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
    }

    static void waitForCropToGetHarvested(GameMap map, Crop crop) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
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

    static Building waitForBuildingToGetUpgraded(Building building) throws InvalidUserActionException {
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

    static void waitForBuildingToBurnDown(Building building) throws InvalidUserActionException {

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

    static void waitForTreeToGetPlanted(GameMap map, Point point) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (map.isTreeAtPoint(point)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.isTreeAtPoint(point));

    }

    static void surroundPointWithVegetation(Point point, DetailedVegetation vegetation, GameMap map) {
        map.surroundWithVegetation(point, vegetation);

        assertEquals(map.getDetailedVegetationUpLeft(point), vegetation);
        assertEquals(map.getDetailedVegetationAbove(point), vegetation);
        assertEquals(map.getDetailedVegetationUpRight(point), vegetation);
        assertEquals(map.getDetailedVegetationDownRight(point), vegetation);
        assertEquals(map.getDetailedVegetationBelow(point), vegetation);
        assertEquals(map.getDetailedVegetationDownLeft(point), vegetation);
    }

    public static void verifyWorkerStaysAtHome(Worker worker, GameMap map) throws InvalidUserActionException {

        for (int i = 0; i < 1000; i++) {
            assertEquals(worker.getHome().getPosition(), worker.getPosition());

            map.stepTime();
        }
    }

    public static void plantTreesOnPlayersLand(Player player) {

        for (Point point: player.getLandInPoints()) {
            try {
                player.getMap().placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }
    }

    public static void plantTreesOnPoints(List<Point> pointsWithinRadius, GameMap map) {
        for (Point point : pointsWithinRadius) {
            if (map.isTreeAtPoint(point)) {
                continue;
            }

            try {
                map.placeTree(point, Tree.TreeType.PINE, TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }
    }

    public static void putStonesOnPoints(List<Point> points, GameMap map) {
        for (Point point : points) {
            if (map.isStoneAtPoint(point)) {
                continue;
            }

            try {
                map.placeStone(point, StoneType.STONE_1, 7);
            } catch (Exception e) {}
        }
    }

    public static void putStoneOnOnePoint(List<Point> points, GameMap map) {
        for (Point point : points) {
            if (map.isStoneAtPoint(point)) {
                continue;
            }

            try {
                map.placeStone(point, StoneType.STONE_1, 7);
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

    public static void fillMapWithVegetation(GameMap map, DetailedVegetation vegetation) {
        map.fillMapWithVegetation(vegetation);
    }

    public static Courier waitForRoadToGetAssignedCourier(GameMap map, Road road0) throws InvalidUserActionException {
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

    public static Worker waitForNonMilitaryBuildingToGetPopulated(Building building) throws InvalidUserActionException {

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

    public static Cargo waitForFlagToHaveCargoWaiting(GameMap map, Flag flag) throws InvalidUserActionException {
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

    public static Cargo waitForFlagToHaveCargoWaiting(GameMap map, Flag flag, Material material) throws InvalidUserActionException {
        Cargo cargo = null;

        for (int i = 0; i < 1000; i++) {

            for (Cargo cargoCandidate : flag.getStackedCargo()) {
                if (cargoCandidate.getMaterial() == material) {
                    cargo = cargoCandidate;

                    break;
                }
            }

            if (cargo != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(cargo);

        return cargo;
    }

    public static Set<Courier> waitForRoadsToGetAssignedCouriers(GameMap map, Road... roads) throws InvalidUserActionException {
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

    public static void waitForNewMessage(Player player0) throws InvalidUserActionException {
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

    public static void waitForBuildingToBeConstructed(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();

        for (int i = 0; i < 10000; i++) {
            if (building.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static void waitForStonemasonToStartGettingStone(GameMap map, Stonemason stonemason) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (stonemason.isGettingStone()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());
    }

    public static void waitForStonemasonToFinishGettingStone(GameMap map, Stonemason stonemason) throws InvalidUserActionException {
        assertTrue(stonemason.isGettingStone());

        for (int i = 0; i < 1000; i++) {
            if (!stonemason.isGettingStone()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(stonemason.isGettingStone());
    }

    public static void waitForSignToDisappear(GameMap map, Sign sign0) throws InvalidUserActionException {
        assertTrue(map.isSignAtPoint(sign0.getPosition()));

        for (int i = 0; i < 5000; i++) {
            if (!map.isSignAtPoint(sign0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isSignAtPoint(sign0.getPosition()));
    }

    public static void waitForFarmerToHarvestCrop(GameMap map, Farmer farmer, Crop crop0) throws InvalidUserActionException {
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

    public static void waitForWorkerToGoToPoint(GameMap map, Worker worker, Point point) throws InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {
            if (worker.isExactlyAtPoint() && worker.getPosition().equals(point)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isExactlyAtPoint());
        assertEquals(worker.getPosition(), point);
    }

    public static void waitForWorkerToSetTarget(GameMap map, Worker worker, Point point) throws InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {
            if (point.equals(worker.getTarget())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getTarget(), point);
    }

    public static void waitForHarvestedCropToDisappear(GameMap map, Crop crop0) throws InvalidUserActionException {
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

    public static void waitForTreeToGetCutDown(Tree tree0, GameMap map) throws InvalidUserActionException {
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

    public static void waitForWorkerToBeOutside(Worker worker, GameMap map) throws InvalidUserActionException {
        assertTrue(worker.isInsideBuilding());

        for (int i = 0; i < 1000; i++) {

            if (!worker.isInsideBuilding()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(worker.isInsideBuilding());
    }

    public static void waitForUpgradeToFinish(Building building) throws InvalidUserActionException {
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

    public static void waitForTreeConservationProgramToActivate(Player player0) throws InvalidUserActionException {
        GameMap map = player0.getMap();

        for (int i = 0; i < 1000; i++) {

            if (player0.isTreeConservationProgramActive()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(player0.isTreeConservationProgramActive());
    }

    public static void waitForStonesToDisappear(GameMap map, Stone... stones) throws InvalidUserActionException {

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

    public static Barracks placeAndOccupyBarracks(Player player, Point point) throws InvalidUserActionException {
        GameMap map = player.getMap();
        Barracks barracks = map.placeBuilding(new Barracks(player), point);

        /* Finish construction of barracks */
        constructHouse(barracks);

        /* Occupy barracks */
        occupyMilitaryBuilding(PRIVATE_RANK, barracks);

        return barracks;
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
    public static void waitForCouriersToBeIdle(GameMap map, Courier... couriers) throws InvalidUserActionException {

        List<Courier> listOfCouriers = new ArrayList<>(Arrays.asList(couriers));

        waitForCouriersToBeIdle(map, listOfCouriers);
    }

    public static void waitForCouriersToBeIdle(GameMap map, Collection<Courier> couriers) throws InvalidUserActionException {

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

    public static Cargo placeCargo(GameMap map, Material material, Flag flag, Building building) {
        Cargo cargo = new Cargo(material, map);

        cargo.setPosition(flag.getPosition());
        cargo.setTarget(building);

        flag.promiseCargo(cargo);
        flag.putCargo(cargo);

        return cargo;
    }

    public static void placeCargos(GameMap map, Material material, int amount, Flag flag, Building building) {
        for (int i = 0; i < amount; i++) {
            placeCargo(map, material, flag, building);
        }
    }

    public static void waitForBuildingToGetAmountOfMaterial(Building building, Material material, int targetAmount) throws InvalidUserActionException {
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

    public static void waitForFlagToGetStackedCargo(GameMap map, Flag flag, int amount) throws InvalidUserActionException {
        for (int i = 0; i < 20000; i++) {
            if (flag.getStackedCargo().size() == amount) {
                break;
            }

            map.stepTime();
        }

        assertEquals(flag.getStackedCargo().size(), amount);
    }

    public static void waitForBuildingsToBeConstructed(Building... buildings) throws InvalidUserActionException {
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

    public static void waitForNonMilitaryBuildingsToGetPopulated(Building... buildings) throws InvalidUserActionException {
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

    public static void verifyWorkerWalksToTargetOnRoads(GameMap map, Worker worker, Point point) throws InvalidUserActionException {
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

    public static void waitForWorkerToBeInside(Worker worker, GameMap map) throws InvalidUserActionException {
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

    public static void waitForWorkersToDisappearFromMap(List<Worker> workers, GameMap map) throws InvalidUserActionException {

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

    public static void waitForFishermanToStopFishing(Fisherman fisherman, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (!fisherman.isFishing()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(fisherman.isFishing());
    }

    public static void waitForPointToBeNext(GameMap map, Courier courier, Point position) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {

            if (!courier.isExactlyAtPoint() && courier.getNextPoint().equals(position)) {
                break;
            }

            map.stepTime();
        }

        assertFalse(courier.isExactlyAtPoint());
        assertEquals(courier.getNextPoint(), position);
    }

    public static void waitForMilitaryBuildingsToGetPopulated(Building... buildings) throws InvalidUserActionException {
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

    public static void constructHouses(Building... buildings) throws InvalidUserActionException {
        GameMap map = buildings[0].getMap();

        /* Place builders */
        List<Worker> builders = new ArrayList<>();
        for (Building building : buildings) {
            Player player = building.getPlayer();

            Builder builder = new Builder(player, map);
            map.placeWorker(builder, building.getFlag());

            building.promiseBuilder(builder);
            builder.setTargetBuilding(building);

            builders.add(builder);
        }

        /* Make the builders reach the buildings */
        fastForwardUntilWorkersReachTarget(map, builders);

        /* Wait for the buildings to get constructed */
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
            if (Arrays.stream(buildings).allMatch(Building::isReady)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(buildings).allMatch(Building::isReady));
    }

    public static void waitForWorkerToBeExactlyOnPoint(Worker worker, GameMap map) throws InvalidUserActionException {

        for (int i = 0; i < 500; i++) {
            if (worker.isExactlyAtPoint()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isExactlyAtPoint());
    }

    public static void verifyWorkerWalksOnPath(GameMap map, Worker worker, Point... points) throws InvalidUserActionException {

        for (Point point : points) {

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

    public static void verifyWorkerDoesNotMove(GameMap map, Courier courier, int time) throws InvalidUserActionException {
        Point point = courier.getPosition();

        for (int i = 0; i < time; i++) {
            assertTrue(courier.isExactlyAtPoint());

            map.stepTime();
        }

        assertEquals(point, courier.getPosition());
    }

    public static void fastForwardUntilWorkersCarryCargo(GameMap map, Material material, Worker... workers) throws InvalidUserActionException {
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

    public static void verifyWorkersDoNotMove(GameMap map, Worker... workers) throws InvalidUserActionException {
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

    public static void waitForCouriersToGetBlocked(GameMap map, Courier... couriers) throws InvalidUserActionException {
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

    public static void fastForwardUntilWorkersCarryCargo(GameMap map, Worker... workers) throws InvalidUserActionException {
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

    public static void waitForMilitaryToStopFighting(GameMap map, Soldier soldier) throws InvalidUserActionException {
        for (int i = 0; i < 2000; i++) {
            if (!soldier.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(soldier.isFighting());
    }

    public static void waitForMilitaryToStartFighting(GameMap map, Soldier soldier) throws InvalidUserActionException {
        for (int i = 0; i < 2000; i++) {
            if (soldier.isFighting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(soldier.isFighting());
    }

    public static void verifyWorkerWalksToTarget(GameMap map, Worker worker, Point point) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {

            if (worker.isExactlyAtPoint() && worker.getPosition().equals(point)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isExactlyAtPoint());
        assertEquals(worker.getPosition(), point);
    }

    public static void printMaxMinAdjacentToX(Set<Point> discoveredLand, int x) {
        Point pointMin0 = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointMin1 = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointMin2 = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point pointMax0 = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Point pointMax1 = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Point pointMax2 = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (Point point : discoveredLand) {
            if (pointMin0.y > point.y && point.x == x - 1) {
                pointMin0 = point;
            }

            if (pointMin1.y > point.y && point.x == x) {
                pointMin1 = point;
            }

            if (pointMin2.y > point.y && point.x == x + 1) {
                pointMin2 = point;
            }

            if (pointMax0.y < point.y && point.x == x - 1) {
                pointMax0 = point;
            }

            if (pointMax1.y < point.y && point.x == x) {
                pointMax1 = point;
            }

            if (pointMax2.y < point.y && point.x == x + 1) {
                pointMax2 = point;
            }
        }

        System.out.println("Max: " + pointMax0 + ", " + pointMax1 + ", " + pointMax2);
        System.out.println("Min: " + pointMin0 + ", " + pointMin1 + ", " + pointMin2);
    }

    public static void surroundPointWithDetailedVegetation(Point point, DetailedVegetation detailedVegetation, GameMap map) {
        map.setDetailedVegetationUpLeft(point, detailedVegetation);
        map.setDetailedVegetationAbove(point, detailedVegetation);
        map.setDetailedVegetationUpRight(point, detailedVegetation);
        map.setDetailedVegetationDownRight(point, detailedVegetation);
        map.setDetailedVegetationBelow(point, detailedVegetation);
        map.setDetailedVegetationDownLeft(point, detailedVegetation);
    }

    public static void waitForWorkerToHaveTargetSet(Worker worker, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {
            if (worker.getTarget() != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getTarget());
    }

    public static void verifyBuilderHammersInPlaceForDuration(GameMap map, Builder builder0, int time) throws InvalidUserActionException {
        Point point = builder0.getPosition();

        for (int i = 0; i < time; i++) {
            assertEquals(point, builder0.getPosition());
            assertTrue(builder0.isHammering());

            map.stepTime();
        }
    }

    public static void assignBuilder(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();
        Builder builder = new Builder(building.getPlayer(), map);

        map.placeWorker(builder, building.getFlag());
        builder.setTargetBuilding(building);
        building.promiseBuilder(builder);

        assertEquals(builder.getTarget(), building.getPosition());

        fastForwardUntilWorkerReachesPoint(map, builder, building.getPosition());
    }

    public static Builder waitForBuilderToGetAssignedToBuilding(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();
        Builder builder = null;

        for (int i = 0; i < 10000; i++) {
            for (Worker worker : map.getWorkers()) {
                if (! (worker instanceof Builder)) {
                    continue;
                }

                if (!building.equals(worker.getTargetBuilding())) {
                    continue;
                }

                builder = (Builder) worker;

                break;
            }

            if (builder != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(builder);
        assertEquals(builder.getTargetBuilding(), building);

        return builder;
    }

    public static void waitForShipToGetBuilt(GameMap map, Ship ship) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (ship.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(ship.isReady());
    }

    public static void waitForNewShipToBeBuilt(GameMap map) throws InvalidUserActionException {
        Set<Ship> shipsBefore = new HashSet<>(map.getShips());

        for (int i = 0; i < 10000; i++) {
            Set<Ship> currentShips = new HashSet<>(map.getShips());

            currentShips.removeAll(shipsBefore);

            boolean foundNewConstructedShip = false;

            if (currentShips.size() > 0) {
                for (Ship ship : currentShips) {
                    if (ship.isReady()) {
                        foundNewConstructedShip = true;

                        break;
                    }
                }
            }

            if (foundNewConstructedShip) {
                break;
            }

            map.stepTime();
        }

        Set<Ship> currentShips = new HashSet<>(map.getShips());

        currentShips.removeAll(shipsBefore);

        assertNotEquals(currentShips.size(), 0);

        boolean foundNewConstructedShip = false;

        for (Ship ship : currentShips) {
            if (ship.isReady()) {
                foundNewConstructedShip = true;

                break;
            }
        }

        assertTrue(foundNewConstructedShip);
    }

    public static Set<Ship> waitForNewShipToStartConstruction(GameMap map) throws InvalidUserActionException {
        Set<Ship> shipsBefore = new HashSet<>(map.getShips());

        for (int i = 0; i < 10000; i++) {
            Set<Ship> currentShips = new HashSet<>(map.getShips());

            currentShips.removeAll(shipsBefore);

            boolean foundNewUnderConstructionShip = false;

            if (currentShips.size() > 0) {
                for (Ship ship : currentShips) {
                    if (ship.isUnderConstruction()) {
                        foundNewUnderConstructionShip = true;

                        break;
                    }
                }
            }

            if (foundNewUnderConstructionShip) {
                break;
            }

            map.stepTime();
        }

        Set<Ship> currentShips = new HashSet<>(map.getShips());

        currentShips.removeAll(shipsBefore);

        assertNotEquals(currentShips.size(), 0);

        boolean foundNewUnderConstructionShip = false;

        for (Ship ship : currentShips) {
            if (ship.isUnderConstruction()) {
                foundNewUnderConstructionShip = true;

                break;
            }
        }

        assertTrue(foundNewUnderConstructionShip);

        return currentShips;
    }

    public static void waitForBuildingToGetBuilder(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();

        for (int i = 0; i < 2000; i++) {

            if (building.getBuilder() != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(building.getBuilder());
    }

    public static void waitForBuildingToBeUnderConstruction(Building building) throws InvalidUserActionException {
        GameMap map = building.getMap();

        for (int i = 0; i < 2000; i++) {

            if (building.isUnderConstruction()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(building.isUnderConstruction());
    }

    public static void waitForShipToBeReadyForExpedition(Ship ship, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 5000; i++) {

            if (ship.isReadyToStartExpedition()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(ship.isReadyToStartExpedition());
    }

    public static void waitForWorkerToHaveNoTargetSet(Ship ship, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (ship.getTarget() == null) {
                break;
            }

            map.stepTime();
        }

        assertNull(ship.getTarget());
    }

    public static void waitForNumberItems(List<Ship> ships, int amount, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (ships.size() == amount) {
                break;
            }

            map.stepTime();
        }

        assertEquals(ships.size(), amount);
    }

    public static void waitForForesterToBePlantingTree(Forester forester, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(forester.isPlanting());
    }

    public static void waitForForesterToStopPlantingTree(Forester forester, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (!forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(forester.isPlanting());

    }

    public static Iterable<? extends Point> getAllPointsOnMap(GameMap map) {
        List<Point> pointsOnMap = new ArrayList<>();

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if ((x + y) % 2 != 0) {
                    continue;
                }

                pointsOnMap.add(new Point(x, y));
            }
        }

        return pointsOnMap;
    }

    public static void waitForCourierToChewGum(Courier courier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (courier.isChewingGum()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isChewingGum());
    }

    public static void waitForCourierToReadPaper(Courier courier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 20000; i++) {
            if (courier.isReadingPaper()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isReadingPaper());
    }

    public static void waitForCourierToTouchNose(Courier courier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (courier.isTouchingNose()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isTouchingNose());
    }

    public static void waitForCourierToJumpSkipRope(Courier courier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (courier.isJumpingSkipRope()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isJumpingSkipRope());
    }

    public static void waitForCourierToSitDown(Courier courier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (courier.isSittingDown()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isSittingDown());
    }

    public static void waitForCourierToBeIdle(Courier courier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (courier.isIdle()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isIdle());
    }

    public static int countMonitoredEventsForDecoration(Point point, GameViewMonitor monitor) {
        int count = 0;

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            for (Point decoratedPoint : gameChangesList.getRemovedDecorations()) {
                if (Objects.equals(point, decoratedPoint)) {
                    count = count + 1;
                }
            }
        }

        return count;
    }

    public static int countMonitoredEventsForNewDecoration(Point point, GameViewMonitor monitor) {
        int count = 0;

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            for (Map.Entry<Point, DecorationType> entry : gameChangesList.getNewDecorations().entrySet()) {
                Point decoratedPoint = entry.getKey();
                DecorationType decorationType = entry.getValue();

                if (Objects.equals(point, decoratedPoint)) {
                    count = count + 1;
                }
            }
        }

        return count;

    }

    public static void waitForNoWorkerOutsideBuilding(Class<? extends Worker> workerClass, Player player) throws InvalidUserActionException {
        GameMap map = player.getMap();

        while (true) {
            if (map.getWorkers().stream().noneMatch(worker -> worker.getClass().equals(workerClass) && !worker.isInsideBuilding())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.getWorkers().stream().noneMatch(worker -> worker.getClass().equals(workerClass) && !worker.isInsideBuilding()));
    }

    public static void removeAllSoldiersFromStorage(Storehouse storehouse) {
        adjustInventoryTo(storehouse, PRIVATE, 0);
        adjustInventoryTo(storehouse, PRIVATE_FIRST_CLASS, 0);
        adjustInventoryTo(storehouse, SERGEANT, 0);
        adjustInventoryTo(storehouse, OFFICER, 0);
        adjustInventoryTo(storehouse, GENERAL, 0);
    }

    public static void waitUntilBuildingDoesntNeedMaterial(Building building, Material material) throws InvalidUserActionException {
        for (int i = 0; i < 20000; i++) {
            if (!building.needsMaterial(material)) {
                break;
            }

            building.getMap().stepTime();
        }

        assertFalse(building.needsMaterial(material));
    }

    public static void clearInventory(Storehouse storehouse, Material... materials) {
        for (var material : materials) {
            adjustInventoryTo(storehouse, material, 0);
        }
    }

    public static void deliverCargos(Building building, Material... materials) {
        for (var material : materials) {
            deliverCargo(building, material);
        }
    }

    public static void deliverMaxCargos(Building building, Material material) {
        for (int i = 0; i < 10; i++) {
            if (!building.needsMaterial(material)) {
                break;
            }

            deliverCargo(building, material);
        }

        assertEquals(building.getAmount(material), building.getCanHoldAmount(material));
        assertFalse(building.needsMaterial(material));
    }

    public static Worker fastForwardUntilOneOfWorkersCarriesCargo(GameMap map, Cargo cargo, Worker... workers) throws InvalidUserActionException {
/*        return fastForwardUntilOneOfWorkersCarriesCargo(map, cargo, workers);
    }
        public static Worker fastForwardUntilOneOfWorkersCarriesCargo(GameMap map, Cargo cargo, Worker[] workers) throws InvalidUserActionException {
  */      Worker workerWithCargo = null;

        for (int j = 0; j < 20000; j++) {
            for (Worker worker : workers) {
                if (worker == null) {
                    continue;
                }

                if (Objects.equals(worker.getCargo(), cargo)) {
                    workerWithCargo = worker;

                    break;
                }
            }

            if (workerWithCargo != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(workerWithCargo);
        assertNotNull(workerWithCargo.getCargo());
        assertEquals(workerWithCargo.getCargo(), cargo);

        return workerWithCargo;
    }

    public static void waitForOneOfSoldiersToAttack(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (Arrays.stream(soldiers).anyMatch(Soldier::isAttacking)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(soldiers).anyMatch(Soldier::isAttacking));
    }

    public static void waitForOneOfSoldiersToHit(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (Arrays.stream(soldiers).anyMatch(Soldier::isHitting)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(soldiers).anyMatch(Soldier::isHitting));
    }

    public static void waitForOneOfSoldiersToJumpBack(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (Arrays.stream(soldiers).anyMatch(Soldier::isJumpingBack)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(soldiers).anyMatch(Soldier::isJumpingBack));
    }

    public static void waitForOneOfSoldiersToGetHit(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (Arrays.stream(soldiers).anyMatch(Soldier::isGettingHit)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(soldiers).anyMatch(Soldier::isGettingHit));
    }

    public static void waitForOneOfSoldiersToStandAside(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        for (int i = 0; i < 10000; i++) {
            if (Arrays.stream(soldiers).anyMatch(Soldier::isStandingAside)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(soldiers).anyMatch(Soldier::isStandingAside));
    }

    public static void waitForFightToEnd(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (Arrays.stream(soldiers).anyMatch(soldier -> !soldier.isFighting())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(Arrays.stream(soldiers).anyMatch(soldier -> !soldier.isFighting()));
    }

    public static Soldier waitForSoldierToBeDying(GameMap map, Soldier... soldiers) throws InvalidUserActionException {
        Soldier dyingSoldier = null;

        for (int i = 0; i < 2000; i++) {
            if (Arrays.stream(soldiers).anyMatch(Soldier::isDying)) {
                dyingSoldier = Arrays.stream(soldiers).filter(Soldier::isDying).findFirst().get();

                break;
            }

            map.stepTime();
        }

        assertNotNull(dyingSoldier);

        return dyingSoldier;
    }

    public static void waitForSoldierToBeDying(Soldier soldier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (soldier.isDying()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(soldier.isDying());
    }

    public static void waitForWorkerToDie(GameMap map, Worker worker) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (worker.isDead()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(worker.isDead());
    }

    public static void waitForWorkerToHaveTarget(GameMap map, Worker worker, Point point) throws InvalidUserActionException {
        for (int i = 0; i < 2000; i++) {
            if (Objects.equals(worker.getTarget(), point)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getTarget(), point);
    }

    public static Soldier waitForSoldierNotDyingOutsideBuilding(Player player) throws InvalidUserActionException {
        GameMap map = player.getMap();

        for (int i = 0; i < 1000; i++) {
            if (map.getWorkers().stream()
                    .anyMatch(soldier -> soldier.isSoldier() &&
                            soldier.getPlayer().equals(player) &&
                            !((Soldier)soldier).isDying() &&
                            !soldier.isInsideBuilding())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.getWorkers().stream()
                .anyMatch(soldier -> soldier.isSoldier() &&
                        soldier.getPlayer().equals(player) &&
                        !((Soldier)soldier).isDying() &&
                        !soldier.isInsideBuilding()));

        return (Soldier) map.getWorkers().stream()
                .filter(soldier -> soldier.isSoldier() &&
                        soldier.getPlayer().equals(player) &&
                        !((Soldier)soldier).isDying() &&
                        !soldier.isInsideBuilding())
                .findFirst()
                .get();
    }

    public static void waitForSoldierToBeCloseToDying(Soldier soldier, GameMap map) throws InvalidUserActionException {
        for (int i = 0; i < 1000; i++) {
            if (soldier.getHealth() < 10) {
                break;
            }

            map.stepTime();
        }

        assertTrue(soldier.getHealth() < 10);
    }

    public static void waitForSoldiersToReachTargets(GameMap map, List<Soldier> soldiers) throws InvalidUserActionException {
        assertNotNull(map);
        assertFalse(soldiers.isEmpty());
        assertTrue(soldiers.stream().allMatch(Worker::isTraveling));

        for (int i = 0; i < 1000; i++) {

            if (soldiers.stream().allMatch(Worker::isArrived)) {
                break;
            }

            map.stepTime();
        }
    }

    public static List<Soldier> findSoldiersOutsideWithHome(Player player, Building building) {
        List<Soldier> soldiers = building.getMap().getWorkers()
                .stream()
                .filter(worker -> worker.getPlayer().equals(player))
                .filter(worker -> Objects.equals(worker.getHome(), building))
                .filter(worker -> !worker.isInsideBuilding())
                .filter(Worker::isSoldier)
                .map(worker -> (Soldier) worker)
                .collect(Collectors.toList());

        soldiers.forEach(soldier -> assertEquals(soldier.getHome(), building));

        return soldiers;
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

            GameChangesList copiedGameChangesList = Utils.copyGameChangesList(gameChangesList);

            gameChanges.add(copiedGameChangesList);

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
            if (gameChangesEvent == null) {
                return gameChanges;
            }

            if (gameChangesEvent.equals(getLastEvent())) {
                return new ArrayList<>();
            }

            int index = gameChanges.indexOf(gameChangesEvent);

            return gameChanges.subList(index + 1, gameChanges.size());
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
                Point point = entry.getKey();

                if (entry.getValue().getAvailableBuilding() == NO_BUILDING_POSSIBLE) {
                    assertFalse(availableBuildingsOnMap.containsKey(point));
                    assertNull(map.isAvailableHousePoint(player0, point));
                }

                if (entry.getValue().getAvailableBuilding() == LARGE_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(point), LARGE);
                    assertEquals(map.isAvailableHousePoint(player0, point), LARGE);
                }

                if (entry.getValue().getAvailableBuilding() == MEDIUM_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(point), MEDIUM);
                    assertEquals(map.isAvailableHousePoint(player0, point), MEDIUM);
                }

                if (entry.getValue().getAvailableBuilding() == SMALL_POSSIBLE) {
                    assertEquals(availableBuildingsOnMap.get(point), SMALL);
                    assertEquals(map.isAvailableHousePoint(player0, point), SMALL);
                }

                if (entry.getValue().getAvailableFlag() == FLAG_POSSIBLE) {
                    assertTrue(availableFlagsOnMap.contains(point));
                    assertTrue(map.isAvailableFlagPoint(player0, point));
                } else {
                    assertFalse(availableFlagsOnMap.contains(point));
                    assertFalse(map.isAvailableFlagPoint(player0, point));
                }

                if (entry.getValue().getAvailableBuilding() == MINE_POSSIBLE) {
                    assertTrue(availableMinesOnMap.contains(point));
                    assertTrue(map.isAvailableMinePoint(player0, point));
                } else {
                    assertFalse(availableMinesOnMap.contains(point));
                    assertFalse(map.isAvailableMinePoint(player0, point));
                }
            }

            /* Run real against monitored */
            for (Point point : player0.getDiscoveredLand()) {
                Size availableHouse = map.isAvailableHousePoint(player0, point);
                boolean availableMine = map.isAvailableMinePoint(player0, point);
                boolean availableFlag = map.isAvailableFlagPoint(player0, point);

                if (availableHouse == LARGE) {
                    assertEquals(availableConstruction.get(point).getAvailableBuilding(), LARGE_POSSIBLE);
                }

                if (availableHouse == MEDIUM) {
                    assertEquals(availableConstruction.get(point).getAvailableBuilding(), MEDIUM_POSSIBLE);
                }

                if (availableHouse == SMALL) {
                    assertEquals(availableConstruction.get(point).getAvailableBuilding(), SMALL_POSSIBLE);
                }

                if (availableHouse == null) {
                    assertTrue(
                            !availableConstruction.containsKey(point) ||
                                    availableConstruction.get(point).getAvailableBuilding() == NO_BUILDING_POSSIBLE
                    );
                }

                if (availableMine) {
                    assertEquals(availableConstruction.get(point).getAvailableBuilding(), MINE_POSSIBLE);
                } else {
                    assertTrue(
                            !availableConstruction.containsKey(point) ||
                                    availableConstruction.get(point).getAvailableBuilding() != MINE_POSSIBLE
                    );
                }

                if (availableFlag) {
                    assertEquals(availableConstruction.get(point).getAvailableFlag(), FLAG_POSSIBLE);
                } else {
                    assertTrue(
                            !availableConstruction.containsKey(point) ||
                                    availableConstruction.get(point).getAvailableFlag() == NO_FLAG_POSSIBLE
                    );
                }
            }
        }

        public void clearEvents() {
            this.gameChanges.clear();
        }
    }

    private static GameChangesList copyGameChangesList(GameChangesList gameChangesList) {
        GameChangesList copy = new GameChangesList(gameChangesList.getTime(),
                new ArrayList<>(gameChangesList.getWorkersWithNewTargets()),
                new ArrayList<>(gameChangesList.getNewFlags()),
                new ArrayList<>(gameChangesList.getRemovedFlags()),
                new ArrayList<>(gameChangesList.getNewBuildings()),
                new ArrayList<>(gameChangesList.getChangedBuildings()),
                new ArrayList<>(gameChangesList.getRemovedBuildings()),
                new ArrayList<>(gameChangesList.getNewRoads()),
                new ArrayList<>(gameChangesList.getRemovedRoads()),
                new ArrayList<>(gameChangesList.getRemovedWorkers()),
                new ArrayList<>(gameChangesList.getNewTrees()),
                new ArrayList<>(gameChangesList.getRemovedTrees()),
                new ArrayList<>(gameChangesList.getRemovedStones()),
                new ArrayList<>(gameChangesList.getNewSigns()),
                new ArrayList<>(gameChangesList.getRemovedSigns()),
                new ArrayList<>(gameChangesList.getNewCrops()),
                new ArrayList<>(gameChangesList.getRemovedCrops()),
                new ArrayList<>(gameChangesList.getNewDiscoveredLand()),
                new ArrayList<>(gameChangesList.getChangedBorders()),
                new ArrayList<>(gameChangesList.getNewStones()),
                new ArrayList<>(gameChangesList.getNewWorkers()),
                new ArrayList<>(gameChangesList.getChangedAvailableConstruction()),
                new ArrayList<>(gameChangesList.getNewGameMessages()),
                new ArrayList<>(gameChangesList.getPromotedRoads()),
                new ArrayList<>(gameChangesList.getChangedFlags()),
                new ArrayList<>(gameChangesList.getRemovedDeadTrees()),
                new ArrayList<>(gameChangesList.getDiscoveredDeadTrees()),
                new ArrayList<>(gameChangesList.getHarvestedCrops()),
                new ArrayList<>(gameChangesList.getNewShips()),
                new ArrayList<>(gameChangesList.getFinishedShips()),
                new ArrayList<>(gameChangesList.getShipsWithNewTargets()),
                new HashMap<>(gameChangesList.getWorkersWithStartedActions()),
                new ArrayList<>(gameChangesList.getRemovedDecorations()),
                new HashMap<>(gameChangesList.getNewDecorations()),
                new ArrayList<>(gameChangesList.getUpgradedBuildings()),
                new ArrayList<>(gameChangesList.getRemovedMessages()),
                new ArrayList<>(gameChangesList.getChangedStones()));

        return copy;
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

    static void fillMapWithCrops(GameMap map) throws InvalidUserActionException {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {

                if ((x + y) % 2 != 0) {
                    continue;
                }

                Point point = new Point(x, y);

                if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point)) {
                    continue;
                }

                Crop crop = map.placeCrop(point, CropType.TYPE_1);
            }
        }
    }

    static List<WorkerAction> getMonitoredWorkerActionsForWorker(Worker worker, GameViewMonitor monitor) {
        return monitor.getEvents().stream()
                .filter(gameChangesList -> gameChangesList.getWorkersWithStartedActions().containsKey(worker))
                .map(gameChangesList -> gameChangesList.getWorkersWithStartedActions().get(worker))
                .collect(Collectors.toList());
    }

    static int countMonitoredWorkerActionForWorker(Worker worker, WorkerAction workerAction, GameViewMonitor monitor) {
        int count = 0;

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            for (Map.Entry<Worker, WorkerAction> entry : gameChangesList.getWorkersWithStartedActions().entrySet()) {
                Worker worker1 = entry.getKey();
                WorkerAction workerAction1 = entry.getValue();

                if (worker.equals(worker1) && workerAction.equals(workerAction1)) {
                    count = count + 1;
                }
            }
        }

        return count;
    }

    public static <T extends Building> T verifyPlayerPlacesOnlyBuilding(ComputerPlayer computerPlayer, Class<T> aClass) throws Exception {
        Player player = computerPlayer.getControlledPlayer();
        GameMap map = player.getMap();
        int amount    = player.getBuildings().size();

        T building = waitForComputerPlayerToPlaceBuilding(computerPlayer, aClass);

        assertEquals(player.getBuildings().size(), amount + 1);

        return building;
    }

    public static <T extends Building>
    T waitForComputerPlayerToPlaceBuilding(ComputerPlayer computerPlayer, Class<T> aClass) throws Exception {
        Player player = computerPlayer.getControlledPlayer();
        GameMap map = player.getMap();
        T found = null;

        Set<Building> buildingsBefore = new HashSet<>(player.getBuildings());

        for (int i = 0; i < 10000; i++) {
            for (Building building : player.getBuildings()) {
                if (building.getClass().equals(aClass) && !buildingsBefore.contains(building)) {
                    found = (T)building;

                    break;
                }
            }

            if (found != null) {
                break;
            }

            computerPlayer.turn();

            map.stepTime();
        }

        assertNotNull(found);

        return found;
    }

    public static void verifyPlayersBuildingsContain(Player player0, Class<? extends Building> aClass) {
        boolean found = false;
        for (Building b : player0.getBuildings()) {
            if (b.getClass().equals(aClass)) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    public static void waitForBuildingToGetCapturedByPlayer(Building building, Player player) throws InvalidUserActionException {
        GameMap map = player.getMap();

        for (int i = 0; i < 10000; i++) {
            if (building.getPlayer().equals(player)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(building.getPlayer(), player);
    }

    public static void waitForBuildingToGetOccupied(ComputerPlayer computerPlayer, Building building) throws Exception {
        GameMap map = computerPlayer.getControlledPlayer().getMap();

        for (int i = 0; i < 10000; i++) {
            if (building.isOccupied()) {
                break;
            }

            computerPlayer.turn();

            map.stepTime();
        }

        assertTrue(building.isOccupied());
    }

    public static <T extends Building> void waitForBuildingToGetConstructedWithComputerPlayer(ComputerPlayer computerPlayer, T building) throws Exception {
        GameMap map = computerPlayer.getControlledPlayer().getMap();

        for (int i = 0; i < 1000; i++) {
            if (building.isReady()) {
                break;
            }

            computerPlayer.turn();

            map.stepTime();
        }

        assertTrue(building.isReady());
    }

    public static double distanceToKnownBorder(Barracks barracks, Player player) {

        /* Check how close the barracks is to the enemy's border */
        double distance = Double.MAX_VALUE;

        for (Point p : player.getBorderPoints()) {
            double tmpDistance = barracks.getPosition().distance(p);

            if (barracks.getPlayer().getDiscoveredLand().contains(p) &&
                    tmpDistance < distance) {
                distance = tmpDistance;
            }
        }

        return distance;
    }

    public static void waitForStoneToRunOut(ComputerPlayer computerPlayer, Stone stone) throws Exception {
        GameMap map = computerPlayer.getControlledPlayer().getMap();

        for (int i = 0; i < 20000; i++) {

            computerPlayer.turn();

            if (!map.isStoneAtPoint(stone.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isStoneAtPoint(stone.getPosition()));
    }

    public static <T extends Building> void waitForBuildingToGetTornDown(ComputerPlayer computerPlayer, T quarry) throws Exception {
        GameMap map = computerPlayer.getControlledPlayer().getMap();

        for (int i = 0; i < 1000; i++) {

            computerPlayer.turn();

            if (quarry.isBurningDown()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(quarry.isBurningDown());
    }
}
