package org.appland.settlers.fuzzing;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.WatchTower;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.utils.TestCaseGenerator;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class SettlersModelDriver {

    private static final String MAP_FILENAME = "/home/johan/projects/settlers-map-manager/maps/WORLDS/WELT01.SWD";
    private static final MapLoader mapLoader = new MapLoader();
    private static MapFile mapFile = null;

    static {
        try {
            mapFile = mapLoader.loadMapFromFile(MAP_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Map<Integer, Class<? extends Building>> buildingClassMap = new HashMap<>();
    private static final Map<Integer, Vegetation> vegetationMap = new HashMap<>();
    private static final TestCaseGenerator testCaseGenerator = new TestCaseGenerator();
    private static final int NUMBER_COMMANDS = 43;

    public static void main(String[] args) throws Exception {

        final String filename = args[0];

        System.out.println("Reading: " + filename);

        File file = new File(filename);

        FileInputStream inputStream = new FileInputStream(file);

        /* Set up the building class map */
        buildingClassMap.put(0, Armory.class);
        buildingClassMap.put(1, Bakery.class);
        buildingClassMap.put(2, Barracks.class);
        buildingClassMap.put(3, Brewery.class);
        buildingClassMap.put(4, Catapult.class);
        buildingClassMap.put(5, CoalMine.class);
        buildingClassMap.put(6, DonkeyFarm.class);
        buildingClassMap.put(7, Farm.class);
        buildingClassMap.put(8, Fishery.class);
        buildingClassMap.put(9, ForesterHut.class);
        buildingClassMap.put(10, Fortress.class);
        buildingClassMap.put(11, GoldMine.class);
        buildingClassMap.put(12, GraniteMine.class);
        buildingClassMap.put(13, GuardHouse.class);
        buildingClassMap.put(14, Headquarter.class);
        buildingClassMap.put(15, HunterHut.class);
        buildingClassMap.put(16, IronMine.class);
        buildingClassMap.put(17, Mill.class);
        buildingClassMap.put(18, Mint.class);
        buildingClassMap.put(19, PigFarm.class);
        buildingClassMap.put(20, Quarry.class);
        buildingClassMap.put(21, Sawmill.class);
        buildingClassMap.put(22, SlaughterHouse.class);
        buildingClassMap.put(23, Storehouse.class);
        buildingClassMap.put(24, WatchTower.class);
        buildingClassMap.put(25, Well.class);
        buildingClassMap.put(26, Woodcutter.class);

        /* Set up the vegetation map */
        for (int i = 0; i < Vegetation.values().length; i++) {
            vegetationMap.put(i, Vegetation.values()[i]);
        }

        /* Create game map */
        List<Player> players = new ArrayList<>();

        //MapLoader mapLoader = new MapLoader();
        //MapFile mapFile = null;
        //try {
            //mapFile = mapLoader.loadMapFromFile(MAP_FILENAME);
        //} catch (InvalidMapException e) {
            //e.printStackTrace();
        //}

        for (int i = 0; i < mapFile.getStartingPoints().size(); i++) {
            players.add(new Player("Player " + i, PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN));
        }

        GameMap map = mapLoader.convertMapFileToGameMap(mapFile);

        map.setPlayers(players);

        /* Start monitoring for each player */
        for (Player player : players) {
            player.monitorGameView((player1, gameChangesList) -> {

            });
        }

        /* Execute the commands from the input file */
        ArgumentsHandler arguments = new ArgumentsHandler(inputStream);

        while (true) {

            try {
                int commandIndex = Math.abs(arguments.getUnsignedIntFor1Chars() % NUMBER_COMMANDS);

                switch (commandIndex) {
                    case 0: // BUILD_ROAD_AUTO PLAYER_ID START.X START.Y END.X END.Y
                        buildRoadAutomatic(map, arguments);
                        break;
                    case 1: // DELETE_ROAD POINT.X POINT.Y
                        deleteRoadAtPoint(map, arguments);
                        break;
                    case 2: // DELETE ROAD BY INDEX
                        deleteRoadByIndex(map, arguments);
                        break;
                    case 3: // RAISE_FLAG PLAYER_ID POINT.X POINT.Y
                        raiseFlag(map, arguments);
                        break;
                    case 4: // DELETE_FLAG
                        deleteFlagAtPoint(map, arguments);
                        break;
                    case 5: // DELETE FLAG BY INDEX
                        deleteFlagByIndex(map, arguments);
                        break;
                    case 6: // FAST_FORWARD
                        fastForward(map, arguments);
                        break;
                    case 7: // RAISE_BUILDING PLAYER_ID TYPE POINT.X POINT.Y
                        raiseBuilding(map, arguments);
                        break;
                    case 8: // DELETE_BUILDING_AT_POINT POINT.X POINT.Y
                        deleteBuilding(map, arguments);
                        break;
                    case 9: // DELETE BUILDING BY INDEX
                        tearDownBuildingByIndex(map, arguments);
                        break;
                    case 10: // SET_VEGETATION_BELOW TYPE POINT.X POINT.Y
                        setVegetationBelow(map, arguments);
                        break;
                    case 11: // SET_VEGETATION_DOWN_RIGHT TYPE POINT.X POINT.Y
                        setVegetationDownRight(map, arguments);
                        break;
                    case 12: // START_PRODUCTION IN HOUSE BY INDEX
                        startProductionInHouseByIndex(map, arguments);
                        break;
                    case 13: // STOP_PRODUCTION HOUSE_INDEX
                        stopProduction(map, arguments);
                        break;
                    case 14: // CALL_SCOUT POINT.X POINT.Y
                        callScout(map, arguments);
                        break;
                    case 15: // CALL SCOUT IN FLAG BY INDEX
                        callScoutAtFlagByIndex(map, arguments);
                        break;
                    case 16: // CALL_GEOLOGIST POINT.X POINT.Y
                        callGeologist(map, arguments);
                        break;
                    case 17: // CALL GEOLOGIST AT FLAG BY INDEX
                        callGeologistAtFlagByIndex(map, arguments);
                        break;
                    case 18: // CHANGE_TRANSPORTATION_PRIORITY
                        changeTransportationPriority(map, arguments);
                        break;
                    case 19: // CHANGE_COAL_ALLOCATION
                        changeCoalAllocation(map, arguments);
                        break;
                    case 20: // CHANGE_FOOD_ALLOCATION
                        changeFoodAllocation(map, arguments);
                        break;
                    case 21: // ATTACK BUILDING BY INDEX
                        attackByIndex(map, arguments);
                        break;
                    case 22: // ATTACK
                        attack(map, arguments);
                        break;
                    case 23: // GET AVAILABLE BUILDINGS
                        getAvailableBuildings(map, arguments);
                        break;
                    case 24: // PLACE MANUAL ROAD
                        placeManualRoad(map, arguments);
                        break;
                    case 25: // GET POSSIBLE ADJACENT ROAD POINTS
                        getPossibleAdjacentRoadPoints(map, arguments);
                        break;
                    case 26: // GET AVAILABLE FLAGS
                        getAvailableFlags(map, arguments);
                        break;
                    case 27: // GET AVAILABLE MINES
                        getAvailableMines(map, arguments);
                        break;
                    case 28: // EVACUATE BUILDING
                        evacuateBuilding(map, arguments);
                        break;
                    case 29: // EVACUATE BUILDING BY INDEX
                        evacuateBuildingByIndex(map, arguments);
                        break;
                    case 30: // CANCEL EVACUATION
                        cancelEvacuation(map, arguments);
                        break;
                    case 31: // CANCEL EVACUATION BY INDEX
                        cancelEvacuationByIndex(map, arguments);
                    case 32: // STOP RECEIVING COINS
                        stopReceivingCoins(map, arguments);
                        break;
                    case 33: // STOP RECEIVING COINS IN HOUSE BY INDEX
                        stopReceivingCoinsByIndex(map, arguments);
                        break;
                    case 34: // START RECEIVING COINS
                        startReceivingCoins(map, arguments);
                        break;
                    case 35: // START RECEIVING COINS IN HOUSE BY INDEX
                        startReceivingCoinsByIndex(map, arguments);
                        break;
                    case 36: // UPGRADE BUILDING
                        upgradeBuilding(map, arguments);
                        break;
                    case 37: // UPGRADE BUILDING BY INDEX
                        upgradeBuildingByIndex(map, arguments);
                        break;
                    case 38: // PUSH OUT MATERIAL BY INDEX
                        pushOutMaterialByIndex(map, arguments);
                        break;
                    case 39: // PUSH OUT MATERIAL
                        pushOutMaterial(map, arguments);
                        break;
                    case 40: // STOP STORAGE OF MATERIAL BY INDEX
                        stopStorageOfMaterialByIndex(map, arguments);
                        break;
                    case 41: // STOP STORAGE OF MATERIAL
                        stopStorageOfMaterial(map, arguments);
                        break;
                    case 42: // BUILD ROAD AUTOMATIC BY INDEX
                        buildRoadAutomaticByIndex(map, arguments);
                        break;
                    default:
                        System.out.println("CAN'T HANDLE: '" + commandIndex + "'");
                        return;
                }
            } catch (SettlersModelDriverException | InvalidUserActionException e) {
                System.out.println(e);
            } catch (EOFException e) {
                return;
            }
        }
    }

    private static void buildRoadAutomaticByIndex(GameMap map, ArgumentsHandler arguments) throws InvalidUserActionException, SettlersModelDriverException {
        Player player;
        Point start;
        Point end;

        try {
            player = arguments.getPlayerFromChar(map);

            start = arguments.getPointByIndex(map);
            end = arguments.getPointByIndex(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        map.placeAutoSelectedRoad(player, start, end);
    }

    private static void stopStorageOfMaterial(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;
        Material material;

        try {
            building = arguments.getBuildingFromCharByPoint(map);
            material = arguments.getMaterialFromChar();

            if (! (building instanceof Storehouse)) {
                throw new SettlersModelDriverException();
            }

        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordStopStorageOfMaterial(building, material);

        ((Storehouse) building).blockDeliveryOfMaterial(material);
    }

    private static void stopStorageOfMaterialByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;
        Material material;

        try {
            building = arguments.getBuildingFromChar(map);
            material = arguments.getMaterialFromChar();

            if (! (building instanceof Storehouse)) {
                throw new SettlersModelDriverException();
            }

        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordStopStorageOfMaterial(building, material);

        ((Storehouse) building).blockDeliveryOfMaterial(material);
    }

    private static void pushOutMaterial(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;
        Material material;

        try {
            building = arguments.getBuildingFromCharByPoint(map);
            material = arguments.getMaterialFromChar();

            if (! (building instanceof Storehouse)) {
                throw new SettlersModelDriverException();
            }

        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordPushOutMaterial(building, material);

        ((Storehouse) building).pushOutAll(material);
    }

    private static void pushOutMaterialByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;
        Material material;

        try {
            building = arguments.getBuildingFromChar(map);
            material = arguments.getMaterialFromChar();

            if (! (building instanceof Storehouse)) {
                throw new SettlersModelDriverException();
            }

        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordPushOutMaterial(building, material);

        ((Storehouse) building).pushOutAll(material);
    }

    private static void upgradeBuildingByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordUpgradeBuilding(building);

        building.upgrade();
    }

    private static void startReceivingCoinsByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordEnablePromotions(building);

        building.enablePromotions();
    }

    private static void stopReceivingCoinsByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordDisablePromotions(building);

        building.disablePromotions();
    }

    private static void cancelEvacuationByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordCancelEvacuation(building);

        building.cancelEvacuation();
    }

    private static void evacuateBuildingByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordEvacuate(building);

        building.evacuate();
    }

    private static void attackByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Building building;
        Player player;
        int attackers;

        try {
            building = arguments.getBuildingFromChar(map);
            player = arguments.getPlayerFromChar(map);
            attackers = arguments.getUnsignedIntFor1Chars();
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordAttack(player, building, attackers);

        player.attack(building, attackers, AttackStrength.STRONG);
    }

    private static void callGeologistAtFlagByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Flag flag;

        try {
            flag = arguments.getFlagFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordCallGeologist(flag);

        flag.callGeologist();
    }

    private static void callScoutAtFlagByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Flag flag;

        try {
            flag = arguments.getFlagFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordCallScout(flag);

        flag.callScout();
    }

    private static void tearDownBuildingByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordTearDownBuilding(building);

        building.tearDown();
    }

    private static void deleteFlagByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Flag flag;

        try {
            flag = arguments.getFlagFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordRemoveFlag(flag);

        map.removeFlag(flag);
    }

    private static void deleteRoadByIndex(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Road road;

        try {
            road = arguments.getRoadFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordRemoveRoad(road);

        map.removeRoad(road);
    }

    private static void upgradeBuilding(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Building building;

        try {
            Point point = arguments.getPointForChars();

            building = map.getBuildingAtPoint(point);

            if (building == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordUpgradeBuilding(building);

        building.upgrade();
    }

    private static void startReceivingCoins(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            Point point = arguments.getPointForChars();

            building = map.getBuildingAtPoint(point);

            if (building == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordEnablePromotions(building);

        building.enablePromotions();
    }

    private static void stopReceivingCoins(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            Point point = arguments.getPointForChars();

            building = map.getBuildingAtPoint(point);

            if (building == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordDisablePromotions(building);

        building.disablePromotions();
    }

    private static void cancelEvacuation(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Building building;

        try {
            Point point = arguments.getPointForChars();

            building = map.getBuildingAtPoint(point);

            if (building == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordCancelEvacuation(building);

        building.cancelEvacuation();
    }

    private static void evacuateBuilding(GameMap map, ArgumentsHandler arguments) throws Exception {
        Building building;

        try {
            Point point = arguments.getPointForChars();

            building = map.getBuildingAtPoint(point);

            if (building == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordEvacuate(building);

        building.evacuate();
    }

    private static void getAvailableMines(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Player player;

        try {
            player = arguments.getPlayerFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordGetAvailableMines(player);

        map.getAvailableMinePoints(player);
    }

    private static void getAvailableFlags(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Player player;

        try {
            player = arguments.getPlayerFromChar(map);
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordGetAvailableFlags(player);

        map.getAvailableFlagPoints(player);
    }

    private static void getPossibleAdjacentRoadPoints(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Player player;
        Point point;

        try {
            player = arguments.getPlayerFromChar(map);

            point = arguments.getPointForChars();
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordGetPossibleAdjacentRoadPoints(point, player);

        map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, point);
    }

    private static void placeManualRoad(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Player player;
        List<Point> points = new ArrayList<>();

        try {
            player = arguments.getPlayerFromChar(map);

            int numberOfPoints = arguments.getUnsignedIntFor1Chars();

            for (int i = 0; i < numberOfPoints; i++) {
                points.add(arguments.getPointForChars());
            }

        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordPlaceRoad(player, points);

        map.placeRoad(player, points);
    }

    private static void getAvailableBuildings(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Player player;

        try {
            player = arguments.getPlayerFromChar(map);

            if (player == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordGetAvailableHousePoints(player);

        map.getAvailableHousePoints(player);
    }

    private static void attack(GameMap map, ArgumentsHandler arguments) throws Exception {
        Player attacker;
        Building building;
        int attackers = 0;

        try {
            attacker = arguments.getPlayerFromChar(map);

            building = map.getBuildingAtPoint(arguments.getPointForChars());

            attackers = arguments.getUnsignedIntFor1Chars();

            if (building == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            throw new SettlersModelDriverException();
        }

        testCaseGenerator.recordAttack(attacker, building, attackers);

        attacker.attack(building, attackers, AttackStrength.STRONG);
    }

    private static void changeFoodAllocation(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Player player;
        Class<? extends Building> buildingClass;
        int quota = 0;

        try {
            player = arguments.getPlayerFromChar(map);

            int type = arguments.getUnsignedIntFor1Chars();

            type = type % 4;

            if (type == 0) {
                buildingClass = CoalMine.class;
            } else if (type == 1) {
                buildingClass = GraniteMine.class;
            } else if (type == 2) {
                buildingClass = IronMine.class;
            } else if (type == 3) {
                buildingClass = GoldMine.class;
            } else {
                throw new SettlersModelDriverException();
            }

            quota = arguments.getUnsignedIntFor1Chars();
        } catch (Throwable t) {
            System.out.println("CHANGE FOOD ALLOCATION - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("CHANGE FOOD ALLOCATION");
        System.out.println(" - Building: " + buildingClass);
        System.out.println(" - Quota: " + quota);

        testCaseGenerator.recordSetFoodQuota(player, buildingClass, quota);

        player.setFoodQuota(buildingClass, quota);
    }

    private static void changeCoalAllocation(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Player player;
        Class<? extends Building> buildingClass;
        int quota = 0;

        try {
            player = arguments.getPlayerFromChar(map);
            buildingClass = buildingClassMap.get(arguments.getUnsignedIntFor1Chars() % buildingClassMap.size());
            quota = arguments.getUnsignedIntFor1Chars();

            if (player == null || buildingClass == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            System.out.println("CHANGE COAL ALLOCATION - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("CHANGE COAL ALLOCATION");
        System.out.println(" - Building: " + buildingClass);
        System.out.println(" - Quota: "  + quota);

        testCaseGenerator.recordSetCoalQuota(player, buildingClass, quota);

        player.setCoalQuota(buildingClass, quota);
    }

    private static void changeTransportationPriority(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException, InvalidUserActionException {
        Player player;
        int priority = 0;
        TransportCategory transportCategory;

        try {
            player = arguments.getPlayerFromChar(map);

            transportCategory = TransportCategory.values()[arguments.getUnsignedIntFor1Chars() % TransportCategory.values().length];

            priority = arguments.getUnsignedIntFor1Chars();

            if (player == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            System.out.println("CHANGE TRANSPORTATION PRIORITY - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("CHANGE TRANSPORTATION PRIORITY");
        System.out.println(" - Priority: " + priority);
        System.out.println(" - Material: " + transportCategory);

        testCaseGenerator.recordSetTransportPriority(player, priority, transportCategory);

        player.setTransportPriority(priority, transportCategory);
    }

    private static void callScout(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Flag flag;

        try {
            Point point = arguments.getPointForChars();

            flag = map.getFlagAtPoint(point);

            if (flag == null) {
                throw new Exception();
            }
        } catch (Throwable t) {
            System.out.println("CALL SCOUT - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("CALL SCOUT");
        System.out.println(" - Flag: " + flag);

        testCaseGenerator.recordCallScout(flag);

        flag.callScout();
    }

    private static void callGeologist(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Flag flag;

        try {
            Point point = arguments.getPointForChars();

            flag = map.getFlagAtPoint(point);

            if (flag == null) {
                throw new Exception();
            }
        } catch (Throwable t) {
            System.out.println("CALL GEOLOGIST - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("CALL GEOLOGIST");
        System.out.println(" - Flag: " + flag);

        testCaseGenerator.recordCallGeologist(flag);

        flag.callGeologist();
    }

    private static void stopProduction(GameMap map, ArgumentsHandler arguments) throws Exception {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            System.out.println("STOP PRODUCTION - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("STOP PRODUCTION");
        System.out.println(" - Building: " + building);

        testCaseGenerator.recordStopProduction(building);

        building.stopProduction();
    }

    private static void startProductionInHouseByIndex(GameMap map, ArgumentsHandler arguments) throws Exception {
        Building building;

        try {
            building = arguments.getBuildingFromChar(map);
        } catch (Throwable t) {
            System.out.println("START PRODUCTION - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("START PRODUCTION");
        System.out.println(" - Building: " + building);

        testCaseGenerator.recordResumeProduction(building);

        building.resumeProduction();
    }

    private static void setVegetationDownRight(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Vegetation vegetation;
        Point point;

        try {

            int vegetationInt = arguments.getUnsignedIntFor1Chars();

            if (!vegetationMap.containsKey(vegetationInt)) {
                throw new SettlersModelDriverException();
            }

            vegetation = vegetationMap.get(vegetationInt);

            point = new Point(arguments.getIntFor3Chars(), arguments.getIntFor3Chars());
        } catch (Throwable t) {
            System.out.println("SET VEGETATION DOWN RIGHT - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("SET VEGETATION DOWN RIGHT");
        System.out.println(" - Point: " + point);
        System.out.println(" - Vegetation: " + vegetation);

        testCaseGenerator.recordSetVegetationDownRight(point, vegetation);

        map.setDetailedVegetationDownRight(point, vegetation);
    }

    private static void setVegetationBelow(GameMap map, ArgumentsHandler arguments) throws SettlersModelDriverException {
        Vegetation vegetation;
        Point point;

        try {
            int vegetationInt = arguments.getUnsignedIntFor1Chars();

            if (!vegetationMap.containsKey(vegetationInt)) {
                throw new SettlersModelDriverException();
            }

            vegetation = Vegetation.values()[vegetationInt];

            point = arguments.getPointForChars();
        } catch (Throwable t) {
            System.out.println("SET VEGETATION BELOW - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("SET VEGETATION BELOW");
        System.out.println(" - Point: " + point);
        System.out.println(" - Vegetation: " + vegetation);

        testCaseGenerator.recordSetVegetationBelow(point, vegetation);

        map.setDetailedVegetationBelow(point, vegetation);
    }

    /**
     *
     * DELETE_BUILDING_AT_POINT POINT.X POINT.Y
     *
     * @param map
     * @param arguments
     * @throws Exception
     */
    private static void deleteBuilding(GameMap map, ArgumentsHandler arguments) throws Exception {
        Building building;

        try {
            Point point = arguments.getPointForChars();
            building = map.getBuildingAtPoint(point);

            if (building == null) {
                throw new Exception();
            }

        } catch (Throwable t) {
            System.out.println("DELETE_BUILDING_AT_POINT - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("DELETE_BUILDING_AT_POINT");
        System.out.println(" - Building: " + building);

        testCaseGenerator.recordTearDownBuilding(building);

        building.tearDown();
    }

    /**
     *
     * RAISE_BUILDING PLAYER_ID TYPE POINT.X POINT.Y
     *                    1      2     3       3
     * @param map
     * @param arguments
     */
    private static void raiseBuilding(GameMap map, ArgumentsHandler arguments) throws Exception {
        Building building;
        Point point;

        try {
            Player player = arguments.getPlayerFromChar(map);
            building = createBuilding(player, arguments.getUnsignedIntFor1Chars() % buildingClassMap.size());
            point = arguments.getPointForChars();
        } catch (Throwable t) {
            System.out.println("RAISE BUILDING - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("RAISE BUILDING");
        System.out.println(" - Building: " + building);
        System.out.println(" - Point: " + point);

        testCaseGenerator.recordPlaceBuilding(building, point);

        map.placeBuilding(building, point);
    }

    private static Building createBuilding(Player player, int parseInt) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends Building> buildingClass = buildingClassMap.get(parseInt);

        Constructor<?> constructor = buildingClass.getConstructor(Player.class);

        Building building = (Building)constructor.newInstance(player);

        return building;
    }

    private static void fastForward(GameMap map, ArgumentsHandler arguments) throws Exception {

        int iterations;

        try {
            int arg1 = arguments.getUnsignedIntFor1Chars();
            int arg2 = arguments.getUnsignedIntFor1Chars();

            iterations = arg1 * arg2;

            if (iterations < 1 || iterations > 2000) {
                return;
            }
        } catch (Throwable t) {
            System.out.println("FAST FORWARD - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("FAST FORWARD");
        System.out.println(" - Iterations: " + iterations);

        testCaseGenerator.recordFastForward(iterations, map);

        for (int i = 0; i < iterations; i++) {

            map.stepTime();
        }
    }

    private static void deleteFlagAtPoint(GameMap map, ArgumentsHandler arguments) throws Exception {
        Flag flag;

        try {
            Point point = arguments.getPointForChars();

            flag = map.getFlagAtPoint(point);
        } catch (Throwable t) {
            System.out.println("DELETE FLAG AT POINT - FAILED");

            throw  new SettlersModelDriverException();
        }

        System.out.println("DELETE FLAG AT POINT");
        System.out.println(" - Flag: " + flag);

        testCaseGenerator.recordRemoveFlag(flag);

        map.removeFlag(flag);
    }

    /**
     *
     * RAISE_FLAG PLAYER_ID POINT.X POINT.Y
     *
     * @param map
     * @param arguments
     * @throws Exception
     */
    private static void raiseFlag(GameMap map, ArgumentsHandler arguments) throws Exception {
        Player player;
        Point point;

        try {
            player = arguments.getPlayerFromChar(map);

            point = arguments.getPointForChars();
        } catch (Throwable t) {
            System.out.println("RAISE FLAG - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("RAISE FLAG");
        System.out.println(" - Point: " + point);

        testCaseGenerator.recordPlaceFlag(player, point);

        map.placeFlag(player, point);
    }

    /**
     *
     * DELETE_ROAD ROAD_ID
     * @param map
     * @param arguments
     */
    private static void deleteRoadAtPoint(GameMap map, ArgumentsHandler arguments) throws Exception {

        Road road;

        try {
            Point point = arguments.getPointForChars();

            road = map.getRoadAtPoint(point);

            if (road == null) {
                throw new SettlersModelDriverException();
            }
        } catch (Throwable t) {
            System.out.println("DELETE ROAD AT POINT - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("DELETE ROAD AT POINT");
        System.out.println(" - Road: " + road);

        testCaseGenerator.recordRemoveRoad(road);

        map.removeRoad(road);
    }

    /**
     *
     * BUILD_ROAD_AUTO PLAYER_ID START.X START.Y END.X END.Y
     *                      ^      ^       ^       ^    ^
     *                      |      |       |       |    |
     *                      |      |       |       |    --- 3 char int
     *                      |      |       |        --- 3 char int
     *                      |      |        --- 3 char int
     *                      |       --- 3 char int
     *                       -- 1 char int
     *
     * @param map
     * @param arguments
     */
    private static void buildRoadAutomatic(GameMap map, ArgumentsHandler arguments) throws InvalidUserActionException, SettlersModelDriverException {

        Point start;
        Point end;
        Player player;

        try {
            player = arguments.getPlayerFromChar(map);

            start = arguments.getPointForChars();
            end = arguments.getPointForChars();

        } catch (Throwable t) {
            System.out.println("PLACE ROAD AUTOMATICALLY - FAILED");

            throw new SettlersModelDriverException();
        }

        System.out.println("PLACE ROAD AUTOMATICALLY");
        System.out.println(" - Start: " + start);
        System.out.println(" - End: " + end);
        System.out.println(" - Player: " + player);

        testCaseGenerator.recordPlaceRoadAutomatically(player, start, end);

        map.placeAutoSelectedRoad(player, start, end);
    }
}
