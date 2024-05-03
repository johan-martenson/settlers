package org.appland.settlers.rest.resource;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.LookoutTower;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.messages.MilitaryBuildingCausedLostLandMessage;
import org.appland.settlers.model.messages.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.messages.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.messages.StoreHouseIsReadyMessage;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.messages.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.messages.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.messages.UnderAttackMessage;
import org.appland.settlers.model.buildings.WatchTower;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.WildAnimal;
import org.appland.settlers.model.actors.Worker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.appland.settlers.model.messages.Message.MessageType.*;

class Utils {

    private final IdManager idManager;

    Utils(IdManager idManager) {
        this.idManager = idManager;
    }

    public static JSONObject decorationToJson(DecorationType decorationType, Point point) {
        JSONObject jsonDecoration = new JSONObject();

        jsonDecoration.put("x", point.x);
        jsonDecoration.put("y", point.y);
        jsonDecoration.put("decoration", decorationType.name().toUpperCase());

        return jsonDecoration;
    }

    public static JSONObject messageJsonToReplyJson(JSONObject jsonBody) {
        JSONObject jsonResponse = new JSONObject();

        long requestId = (Long) jsonBody.get("requestId");

        jsonResponse.put("requestId", requestId);

        return jsonResponse;
    }

    JSONArray gamesToJson(List<GameMap> games, GameResource gameResource) {
        JSONArray jsonGames = new JSONArray();

        for (GameMap map : games) {
            JSONObject jsonGame = gameToJson(map, gameResource);

            jsonGames.add(jsonGame);
        }

        return jsonGames;
    }

    JSONObject gameToJson(GameMap map, GameResource gameResource) {
        JSONObject jsonGame = new JSONObject();

        String id = idManager.getId(map);

        jsonGame.put("id", id);
        jsonGame.put("players", playersToJson(map.getPlayers(), gameResource));

        /* Set the status to STARTED because this is an instance of GameMap */
        jsonGame.put("status", "STARTED");

        return jsonGame;
    }

    JSONArray playersToJson(Collection<Player> players, GameResource gameResource) {
        JSONArray jsonPlayers = new JSONArray();

        for (Player player : players) {
            JSONObject jsonPlayer = playerToJson(player, idManager.getId(player), gameResource);

            jsonPlayers.add(jsonPlayer);
        }

        return jsonPlayers;
    }

    JSONObject playerToJson(Player player, String playerId, GameResource gameResource) {
        JSONObject jsonPlayer = new JSONObject();

        jsonPlayer.put("name", player.getName());
        jsonPlayer.put("color", player.getColor().name().toUpperCase());
        jsonPlayer.put("id", playerId);
        jsonPlayer.put("nation", player.getNation().name());

        if (gameResource.isComputerPlayer(player)) {
            jsonPlayer.put("type", "COMPUTER");
        } else {
            jsonPlayer.put("type", "HUMAN");
        }

        /* Get the player's "center spot" */
        for (Building building : player.getBuildings()) {
            if (building instanceof Headquarter) {
                jsonPlayer.put("centerPoint", pointToJson(building.getPosition()));

                break;
            }
        }

        /* Fill in the points the player has discovered */
        JSONArray jsonDiscoveredPoints = new JSONArray();
        jsonPlayer.put("discoveredPoints", jsonDiscoveredPoints);

        for (Point point : player.getDiscoveredLand()) {
            jsonDiscoveredPoints.add(pointToJson(point));
        }

        return jsonPlayer;
    }

    JSONArray pointsToJson(Collection<Point> points) {
        JSONArray jsonPoints = new JSONArray();

        for (Point point : points) {
            jsonPoints.add(pointToJson(point));
        }

        return jsonPoints;
    }

    JSONObject pointToJson(Point point) {
        JSONObject jsonPoint = new JSONObject();

        jsonPoint.put("x", point.x);
        jsonPoint.put("y", point.y);

        return jsonPoint;
    }


    private String colorToHexString(Color c) {

        StringBuilder hex = new StringBuilder(Integer.toHexString(c.getRGB() & 0xffffff));

        while (hex.length() < 6) {
            hex.insert(0, "0");
        }

        hex.insert(0, "#");

        return hex.toString();
    }

    List<Player> jsonToPlayers(JSONArray jsonPlayers) {
        List<Player> players = new ArrayList<>();

        if (jsonPlayers != null) {

            for (Object jsonPlayer : jsonPlayers) {
                players.add(jsonToPlayer((JSONObject) jsonPlayer));
            }
        }

        return players;
    }

    Player jsonToPlayer(JSONObject jsonPlayer) {
        String name = (String) jsonPlayer.get("name");
        PlayerColor color = jsonToColor((String) jsonPlayer.get("color"));

        String nationString = (String) jsonPlayer.get("nation");
        Nation nation = Nation.valueOf(nationString);

        Player player = new Player(name, color);

        player.setNation(nation);

        return player;
    }

    private PlayerColor jsonToColor(String colorName) {
        return PlayerColor.valueOf(colorName);
    }

    JSONObject terrainToJson(GameMap map) {
        JSONObject jsonTerrain = new JSONObject();

        JSONArray jsonTrianglesBelow = new JSONArray();
        JSONArray jsonTrianglesBelowRight = new JSONArray();
        JSONArray jsonHeights = new JSONArray();

        jsonTerrain.put("straightBelow", jsonTrianglesBelow);
        jsonTerrain.put("belowToTheRight", jsonTrianglesBelowRight);
        jsonTerrain.put("heights", jsonHeights);

        int start = 1;

        jsonTerrain.put("width", map.getWidth());
        jsonTerrain.put("height", map.getHeight());

        for (int y = 1; y < map.getHeight(); y++) {
            for (int x = start; x + 1 < map.getWidth(); x += 2) {
                Point point = new Point(x, y);

                DetailedVegetation below = map.getDetailedVegetationBelow(point);
                DetailedVegetation downRight = map.getDetailedVegetationDownRight(point);

                jsonTrianglesBelow.add(vegetationToJson(below));
                jsonTrianglesBelowRight.add(vegetationToJson(downRight));
                jsonHeights.add(map.getHeightAtPoint(point));
            }

            if (start == 1) {
                start = 2;
            } else {
                start = 1;
            }
        }

        return jsonTerrain;
    }

    private String vegetationToJson(DetailedVegetation vegetation) {
        switch (vegetation) {
            case SAVANNAH:           return "SA";
            case MOUNTAIN_1:         return "MO1";
            case SNOW:               return "SN";
            case SWAMP:              return "SW";
            case DESERT_1:           return "D1";
            case WATER:              return "W1";
            case BUILDABLE_WATER:    return "B";
            case DESERT_2:           return "D2";
            case MEADOW_1:           return "ME1";
            case MEADOW_2:           return "ME2";
            case MEADOW_3:           return "ME3";
            case MOUNTAIN_2:         return "MO2";
            case MOUNTAIN_3:         return "MO3";
            case MOUNTAIN_4:         return "MO4";
            case STEPPE:             return "ST";
            case FLOWER_MEADOW:      return "FM";
            case LAVA:               return "L1";
            case MAGENTA:            return "MA";
            case MOUNTAIN_MEADOW:    return "MM";
            case WATER_2:            return "W2";
            case LAVA_2:             return "L2";
            case LAVA_3:             return "L3";
            case LAVA_4:             return "L4";
            case BUILDABLE_MOUNTAIN: return "BM";

            default:
                System.out.println("Cannot handle this vegetation " + vegetation);
                System.exit(1);
        }

        return ""; // Should never be reached but the compiler complains
    }

    JSONObject pointToDetailedJson(Point point, Player player, GameMap map) throws InvalidUserActionException {

        JSONObject jsonPointInfo = pointToJson(point);

        if (player.getDiscoveredLand().contains(point)) {

            if (map.isBuildingAtPoint(point)) {
                Building building = map.getBuildingAtPoint(point);
                jsonPointInfo.put("building", houseToJson(building, player));
                jsonPointInfo.put("is", "building");
                jsonPointInfo.put("buildingId", idManager.getId(building));
            } else if (map.isFlagAtPoint(point)) {
                jsonPointInfo.put("is", "flag");
                jsonPointInfo.put("flagId", idManager.getId(map.getFlagAtPoint(point)));
            } else if (map.isRoadAtPoint(point)) {
                jsonPointInfo.put("is", "road");
                jsonPointInfo.put("roadId", idManager.getId(map.getRoadAtPoint(point)));
            }

            JSONArray canBuild = new JSONArray();
            jsonPointInfo.put("canBuild", canBuild);

            try {
                if (map.isAvailableFlagPoint(player, point)) {
                    canBuild.add("flag");
                }

                if (map.isAvailableMinePoint(player, point)) {
                    canBuild.add("mine");
                }

                Size size = map.isAvailableHousePoint(player, point);

                if (size != null) {
                    if (size == Size.LARGE) {
                        canBuild.add("large");
                        canBuild.add("medium");
                        canBuild.add("small");
                    } else if (size == Size.MEDIUM) {
                        canBuild.add("medium");
                        canBuild.add("small");
                    } else if (size == Size.SMALL) {
                        canBuild.add("small");
                    }
                }

                /* Fill in available connections for a new road */
                JSONArray jsonPossibleConnections = new JSONArray();
                jsonPointInfo.put("possibleRoadConnections", jsonPossibleConnections);
                for (Point possibleConnection : map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, point)) {
                    jsonPossibleConnections.add(pointToJson(possibleConnection));
                }
            } catch (Exception ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return jsonPointInfo;
    }

    JSONObject houseToJson(Building building, Player player) throws InvalidUserActionException {
        JSONObject jsonHouse = pointToJson(building.getPosition());

        jsonHouse.put("id", idManager.getId(building));
        jsonHouse.put("playerId", idManager.getId(building.getPlayer()));
        jsonHouse.put("type", building.getClass().getSimpleName());
        jsonHouse.put("nation", building.getPlayer().getNation().name().toUpperCase());

        if (building.canProduce()) {
            JSONArray jsonProduces = new JSONArray();

            jsonHouse.put("productivity", building.getProductivity());
            jsonHouse.put("produces", jsonProduces);

            for (Material material : building.getProducedMaterial()) {
                jsonProduces.add(material.name().toUpperCase());
            }

            jsonHouse.put("productionEnabled", building.isProductionEnabled());
        }

        JSONObject jsonResources = new JSONObject();

        for (Material material : Material.values()) {
            int amountCanHold = building.getCanHoldAmount(material);
            int amountAvailable = building.getAmount(material);

            if (amountAvailable > 0 || amountCanHold > 0) {
                JSONObject jsonResource = new JSONObject();

                jsonResource.put("has", amountAvailable);

                if (amountCanHold > 0) {
                    jsonResource.put("canHold", amountCanHold);
                }

                jsonResources.put(material.name().toUpperCase(), jsonResource);
            }
        }

        jsonHouse.put("resources", jsonResources);

        if (building.isPlanned()) {
            jsonHouse.put("state", "PLANNED");
        } else if (building.isUnderConstruction()) {
            jsonHouse.put("state", "UNFINISHED");
        } else if (building.isReady() && !building.isOccupied()) {
            jsonHouse.put("state", "UNOCCUPIED");
        } else if (building.isReady() && building.isOccupied()) {
            jsonHouse.put("state", "OCCUPIED");
        } else if (building.isBurningDown()) {
            jsonHouse.put("state", "BURNING");
        } else if (building.isDestroyed()) {
            jsonHouse.put("state", "DESTROYED");
        }

        if (building.isUnderConstruction()) {
            jsonHouse.put("constructionProgress", building.getConstructionProgress());
        } else if (building.isReady()) {
            jsonHouse.put("door", building.isDoorClosed() ? "CLOSED" : "OPEN");
        }

        /* Add amount of hosted soldiers for military buildings */
        if (building.isMilitaryBuilding() && building.isReady()) {
            JSONArray jsonSoldiers = new JSONArray();

            for (Soldier military : building.getHostedSoldiers()) {
                jsonSoldiers.add(military.getRank().name().toUpperCase());
            }

            jsonHouse.put("soldiers", jsonSoldiers);
            jsonHouse.put("maxSoldiers", building.getMaxHostedSoldiers());
            jsonHouse.put("evacuated", building.isEvacuated());
            jsonHouse.put("promotionsEnabled", building.isPromotionEnabled());

            if (building.isUpgrading()) {
                jsonHouse.put("upgrading", true);
            }
        }

        if (building instanceof Headquarter headquarter) {
            JSONObject jsonReserved = new JSONObject();
            JSONObject jsonInReserve = new JSONObject();

            jsonHouse.put("reserved", jsonReserved);
            jsonHouse.put("inReserve", jsonInReserve);

            Arrays.stream(Soldier.Rank.values()).iterator().forEachRemaining(
                    rank -> {
                        jsonReserved.put(rank.name().toUpperCase(), headquarter.getReservedSoldiers(rank));
                        jsonInReserve.put(rank.name().toUpperCase(), headquarter.getActualReservedSoldiers().get(rank));
                    });
        }

        if (!building.getPlayer().equals(player) && building.isMilitaryBuilding() && building.isOccupied()) {
            int availableAttackers = player.getAvailableAttackersForBuilding(building);

            jsonHouse.put("availableAttackers", availableAttackers);
        }

        return jsonHouse;
    }


    List<Point> jsonToPoints(JSONArray jsonPoints) {
        List<Point> points = new ArrayList<>();

        for (Object point : jsonPoints) {
            points.add(jsonToPoint((JSONObject) point));
        }

        return points;
    }

    Point jsonToPoint(JSONObject point) {
        int x;
        int y;

        Object xObject = point.get("x");
        Object yObject = point.get("y");

        if (xObject instanceof String) {
            x = Integer.parseInt((String) xObject);
        } else if (xObject instanceof Integer) {
            x = (Integer) xObject;
        } else {
            x = ((Long) xObject).intValue();
        }

        if (yObject instanceof String) {
            y = Integer.parseInt((String) yObject);
        } else if (yObject instanceof Integer) {
            y = (Integer) yObject;
        } else {
            y = ((Long) yObject).intValue();
        }

        return new Point(x, y);
    }

    public Building buildingFactory(JSONObject jsonHouse, Player player) {
        String buildingType = (String)jsonHouse.get("type");

        return buildingFactory(buildingType, player);
    }

    public Building buildingFactory(String buildingType, Player player) {
        Building building = switch (buildingType) {
            case "ForesterHut" -> new ForesterHut(player);
            case "Woodcutter" -> new Woodcutter(player);
            case "Quarry" -> new Quarry(player);
            case "Headquarter" -> new Headquarter(player);
            case "Sawmill" -> new Sawmill(player);
            case "Farm" -> new Farm(player);
            case "Barracks" -> new Barracks(player);
            case "Well" -> new Well(player);
            case "Mill" -> new Mill(player);
            case "Bakery" -> new Bakery(player);
            case "Fishery" -> new Fishery(player);
            case "GoldMine" -> new GoldMine(player);
            case "IronMine" -> new IronMine(player);
            case "CoalMine" -> new CoalMine(player);
            case "GraniteMine" -> new GraniteMine(player);
            case "PigFarm" -> new PigFarm(player);
            case "Mint" -> new Mint(player);
            case "SlaughterHouse" -> new SlaughterHouse(player);
            case "DonkeyFarm" -> new DonkeyFarm(player);
            case "GuardHouse" -> new GuardHouse(player);
            case "WatchTower" -> new WatchTower(player);
            case "Fortress" -> new Fortress(player);
            case "Catapult" -> new Catapult(player);
            case "HunterHut" -> new HunterHut(player);
            case "IronSmelter" -> new IronSmelter(player);
            case "Armory" -> new Armory(player);
            case "Brewery" -> new Brewery(player);
            case "Storehouse" -> new Storehouse(player);
            case "LookoutTower" -> new LookoutTower(player);
            case "Metalworks" -> new Metalworks(player);
            case "Shipyard" -> new Shipyard(player);
            default -> {
                System.out.println("DON'T KNOW HOW TO CREATE BUILDING " + buildingType);
                System.exit(1);
                yield null;
            }
        };

        return building;
    }

    JSONObject treeToJson(Tree tree) {
        JSONObject jsonTree = pointToJson(tree.getPosition());

        jsonTree.put("id", idManager.getId(tree));
        jsonTree.put("type", tree.getTreeType().name().toUpperCase());
        jsonTree.put("size", tree.getSize().name().toUpperCase());

        return jsonTree;
    }

    JSONObject stoneToJson(Stone stone) {
        JSONObject jsonStone = pointToJson(stone.getPosition());

        jsonStone.put("id", idManager.getId(stone));
        jsonStone.put("type", stone.getStoneType().name().toUpperCase());
        jsonStone.put("amount", stone.getStoneAmount().name().toUpperCase());

        return jsonStone;
    }

    JSONObject workerToJson(Worker worker) {
        JSONObject jsonWorker = pointToJson(worker.getPosition());

        jsonWorker.put("id", idManager.getId(worker));
        jsonWorker.put("type", workerTypeToJson(worker));
        jsonWorker.put("inside", worker.isInsideBuilding());
        jsonWorker.put("betweenPoints", !worker.isExactlyAtPoint());
        jsonWorker.put("direction", worker.getDirection().name().toUpperCase());
        jsonWorker.put("color", worker.getPlayer().getColor().name().toUpperCase());
        jsonWorker.put("nation", worker.getPlayer().getNation().name().toUpperCase());

        if (!worker.isExactlyAtPoint()) {
            jsonWorker.put("previous", pointToJson(worker.getLastPoint()));
            jsonWorker.put("next", pointToJson(worker.getNextPoint()));
            jsonWorker.put("percentageTraveled", worker.getPercentageOfDistanceTraveled());
            jsonWorker.put("plannedPath", pointsToJson(worker.getPlannedPath()));
        } else {
            jsonWorker.put("percentageTraveled", 0);
        }

        if (worker instanceof Courier courier) {

            jsonWorker.put("bodyType", courier.getBodyType().name().toUpperCase());
        }

        if (worker.getCargo() != null) {
            jsonWorker.put("cargo", worker.getCargo().getMaterial().getSimpleName().toUpperCase());
        }

        return jsonWorker;
    }

    private String rankToTypeString(Soldier soldier) {
        String nameAndRank = soldier.getRank().name().toLowerCase();

        int underscorePosition = nameAndRank.indexOf("_");

        return nameAndRank.substring(0, 1).toUpperCase() + nameAndRank.substring(1, underscorePosition);
    }

    JSONObject flagToJson(Flag flag) {
        JSONObject jsonFlag = pointToJson(flag.getPosition());

        jsonFlag.put("id", idManager.getId(flag));
        jsonFlag.put("playerId", idManager.getId(flag.getPlayer()));
        jsonFlag.put("type", flag.getType().name());
        jsonFlag.put("nation", flag.getPlayer().getNation().name().toUpperCase());
        jsonFlag.put("color", flag.getPlayer().getColor().name().toUpperCase());

        if (!flag.getStackedCargo().isEmpty()) {
            jsonFlag.put("stackedCargo", cargosToMaterialJson(flag.getStackedCargo()));
        }

        return jsonFlag;
    }

    private JSONArray cargosToMaterialJson(Collection<Cargo> cargos) {
        JSONArray jsonMaterial = new JSONArray() ;

        for (Cargo cargo : cargos) {
            Material material = cargo.getMaterial();

            jsonMaterial.add(material.getSimpleName().toUpperCase());
        }

        return jsonMaterial;
    }

    JSONObject roadToJson(Road road) {
        JSONObject jsonRoad = new JSONObject();

        JSONArray jsonPoints = new JSONArray();

        for (Point point : road.getWayPoints()) {
            JSONObject jsonPoint = pointToJson(point);

            jsonPoints.add(jsonPoint);
        }

        jsonRoad.put("points", jsonPoints);
        jsonRoad.put("id", idManager.getId(road));

        if (road.isMainRoad()) {
            jsonRoad.put("type", "MAIN");
        } else {
            jsonRoad.put("type", "NORMAL");
        }

        return jsonRoad;
    }

    JSONObject borderToJson(Player player, String playerId) {

        /* Fill in borders */
        JSONObject jsonBorder = new JSONObject();
        jsonBorder.put("playerId", playerId);

        JSONArray jsonBorderPoints = new JSONArray();
        jsonBorder.put("points", jsonBorderPoints);

        for (Point point : player.getBorderPoints()) {
            jsonBorderPoints.add(pointToJson(point));
        }

        return jsonBorder;
    }

    JSONObject signToJson(Sign sign) {
        JSONObject jsonSign = new JSONObject();

        if (sign.isEmpty()) {
            jsonSign.put("type", null);
        } else {
            switch (sign.getType()) {
                case GOLD:
                    jsonSign.put("type", "GOLD");
                    break;
                case IRON:
                    jsonSign.put("type", "IRON");
                    break;
                case COAL:
                    jsonSign.put("type", "COAL");
                    break;
                case STONE:
                    jsonSign.put("type", "STONE");
                    break;
                case WATER:
                    jsonSign.put("type", "WATER");
                    break;
                default:
                    System.out.println("Cannot have sign of type " + sign.getType());
                    System.exit(1);
            }
        }

        Point point = sign.getPosition();

        jsonSign.put("x", point.x);
        jsonSign.put("y", point.y);

        jsonSign.put("id", idManager.getId(sign));

        if (sign.getSize() != null) {
            jsonSign.put("amount", sign.getSize().toString().toUpperCase());
        }

        return jsonSign;
    }

    Object cropToJson(Crop crop) {
        JSONObject jsonCrop = pointToJson(crop.getPosition());

        jsonCrop.put("id", idManager.getId(crop));
        jsonCrop.put("state", "" + crop.getGrowthState());
        jsonCrop.put("type", crop.getType().name().toUpperCase());

        return jsonCrop;
    }

    JSONObject playerToJson(Player player) {
        JSONObject jsonPlayer = new JSONObject();

        jsonPlayer.put("id", idManager.getId(player));
        jsonPlayer.put("name", player.getName());
        jsonPlayer.put("color", player.getColor().name().toUpperCase());
        jsonPlayer.put("nation", player.getNation().name());

        return jsonPlayer;
    }

    JSONArray mapFilesToJson(List<MapFile> mapFiles) {
        JSONArray jsonMapFiles = new JSONArray();

        for (MapFile mapFile : mapFiles) {
            jsonMapFiles.add(mapFileToJson(mapFile));
        }

        return jsonMapFiles;
    }

    JSONObject mapFileToJson(MapFile mapFile) {
        JSONObject jsonMapFile = new JSONObject();

        jsonMapFile.put("title", mapFile.getTitle());
        jsonMapFile.put("author", mapFile.getAuthor());
        jsonMapFile.put("width", mapFile.getWidth());
        jsonMapFile.put("height", mapFile.getHeight());
        jsonMapFile.put("maxPlayers", mapFile.getMaxNumberOfPlayers());
        jsonMapFile.put("id", idManager.getId(mapFile));
        jsonMapFile.put("startingPoints", pointsToJson(mapFile.getGamePointStartingPoints()));

        return jsonMapFile;
    }

    GameMap gamePlaceholderToGame(GameResource gamePlaceholder) throws Exception {

        /* Create a GameMap instance from the map file */
        MapLoader mapLoader = new MapLoader();
        GameMap map = mapLoader.convertMapFileToGameMap(gamePlaceholder.getMapFile());

        /* Assign the players */
        map.setPlayers(gamePlaceholder.getPlayers());

        return map;
    }

    void adjustResources(GameMap map, ResourceLevel resources) {

        for (Player player : map.getPlayers()) {

            Headquarter headquarter = (Headquarter)player.getBuildings().getFirst();

            if (resources == ResourceLevel.LOW) {

                headquarter.retrieve(Material.STONE);
                headquarter.retrieve(Material.STONE);
                headquarter.retrieve(Material.STONE);

                headquarter.retrieve(Material.PLANK);
                headquarter.retrieve(Material.PLANK);
                headquarter.retrieve(Material.PLANK);

                headquarter.retrieve(Material.WOOD);
                headquarter.retrieve(Material.WOOD);
                headquarter.retrieve(Material.WOOD);
            } else if (resources == ResourceLevel.HIGH) {

                deliver(Material.STONE, 3, headquarter);
                deliver(Material.PLANK, 3, headquarter);
                deliver(Material.WOOD, 3, headquarter);
            }
        }
    }

    private void deliver(Material material, int amount, Headquarter headquarter) {

        for (int i = 0; i < amount; i++) {
            headquarter.promiseDelivery(material);
            headquarter.putCargo(new Cargo(material, headquarter.getMap()));
        }
    }

    JSONArray housesToJson(Collection<Building> buildings, Player player) throws InvalidUserActionException {
        JSONArray jsonHouses = new JSONArray();

        for (Building building : buildings) {
            jsonHouses.add(houseToJson(building, player));
        }

        return jsonHouses;
    }

    JSONObject mapFileTerrainToJson(MapFile mapFile) throws Exception {
        MapLoader mapLoader = new MapLoader();

        GameMap map = mapLoader.convertMapFileToGameMap(mapFile);

        return terrainToJson(map);
    }

    public JSONArray playersToShortJson(List<Player> players) {
        JSONArray jsonPlayers = new JSONArray();

        for (Player player : players) {
            JSONObject jsonPlayer = new JSONObject();

            jsonPlayer.put("name", player.getName());
            jsonPlayer.put("color", player.getColor().name().toUpperCase());
            jsonPlayer.put("id", idManager.getId(player));

            jsonPlayers.add(jsonPlayer);
        }

        return jsonPlayers;
    }

    public JSONObject buildingLostMessageToJson(BuildingLostMessage buildingLostMessage) {
        JSONObject jsonBuildingLostMessage = new JSONObject();

        Building building = buildingLostMessage.building();

        jsonBuildingLostMessage.put("id", idManager.getId(buildingLostMessage));
        jsonBuildingLostMessage.put("type", "BUILDING_LOST");
        jsonBuildingLostMessage.put("houseId", idManager.getId(buildingLostMessage.building()));
        jsonBuildingLostMessage.put("houseType", building.getSimpleName());
        jsonBuildingLostMessage.put("point", buildingToPoint(building));

        return jsonBuildingLostMessage;
    }

    public JSONObject buildingCapturedMessageToJson(BuildingCapturedMessage buildingCapturedMessage) {
        JSONObject jsonBuildingCapturedMessage = new JSONObject();

        Building building = buildingCapturedMessage.building();

        jsonBuildingCapturedMessage.put("id", idManager.getId(buildingCapturedMessage));
        jsonBuildingCapturedMessage.put("type", "BUILDING_CAPTURED");
        jsonBuildingCapturedMessage.put("houseId", idManager.getId(buildingCapturedMessage.building()));
        jsonBuildingCapturedMessage.put("houseType", building.getSimpleName());
        jsonBuildingCapturedMessage.put("point", buildingToPoint(building));

        return jsonBuildingCapturedMessage;
    }

    public JSONObject jsonStoreHouseIsReadyMessageToJson(StoreHouseIsReadyMessage storeHouseIsReadyMessage) {
        JSONObject jsonStoreHouseIsReadyMessage = new JSONObject();

        Building building = storeHouseIsReadyMessage.building();

        jsonStoreHouseIsReadyMessage.put("id", idManager.getId(storeHouseIsReadyMessage));
        jsonStoreHouseIsReadyMessage.put("type", "STORE_HOUSE_IS_READY");
        jsonStoreHouseIsReadyMessage.put("houseId", idManager.getId(storeHouseIsReadyMessage.building()));
        jsonStoreHouseIsReadyMessage.put("houseType", building.getSimpleName());
        jsonStoreHouseIsReadyMessage.put("point", buildingToPoint(building));

        return jsonStoreHouseIsReadyMessage;
    }

    JSONObject militaryBuildingReadyMessageToJson(MilitaryBuildingReadyMessage militaryBuildingReadyMessage) {
        JSONObject jsonMilitaryBuildingOccupiedMessage = new JSONObject();

        Building building = militaryBuildingReadyMessage.building();

        jsonMilitaryBuildingOccupiedMessage.put("id", idManager.getId(militaryBuildingReadyMessage));
        jsonMilitaryBuildingOccupiedMessage.put("type", MILITARY_BUILDING_READY.toString());
        jsonMilitaryBuildingOccupiedMessage.put("houseId", idManager.getId(militaryBuildingReadyMessage.building()));
        jsonMilitaryBuildingOccupiedMessage.put("houseType", building.getSimpleName());
        jsonMilitaryBuildingOccupiedMessage.put("point", buildingToPoint(building));

        return jsonMilitaryBuildingOccupiedMessage;
    }

    JSONObject noMoreResourcesMessageToJson(NoMoreResourcesMessage noMoreResourcesMessage) {
        JSONObject jsonNoMoreResourcesMessage = new JSONObject();

        Building building = noMoreResourcesMessage.building();

        jsonNoMoreResourcesMessage.put("id", idManager.getId(noMoreResourcesMessage));
        jsonNoMoreResourcesMessage.put("type", NO_MORE_RESOURCES.toString());
        jsonNoMoreResourcesMessage.put("houseId", idManager.getId(noMoreResourcesMessage.building()));
        jsonNoMoreResourcesMessage.put("houseType", building.getSimpleName());
        jsonNoMoreResourcesMessage.put("point", buildingToPoint(building));

        return jsonNoMoreResourcesMessage;
    }

    JSONObject militaryBuildingOccupiedMessageToJson(MilitaryBuildingOccupiedMessage militaryBuildingOccupiedMessage) {
        JSONObject jsonMilitaryBuildingOccupiedMessage = new JSONObject();

        Building building = militaryBuildingOccupiedMessage.building();

        jsonMilitaryBuildingOccupiedMessage.put("id", idManager.getId(militaryBuildingOccupiedMessage));
        jsonMilitaryBuildingOccupiedMessage.put("type", MILITARY_BUILDING_OCCUPIED.toString());
        jsonMilitaryBuildingOccupiedMessage.put("houseId", idManager.getId(building));
        jsonMilitaryBuildingOccupiedMessage.put("houseType", building.getSimpleName());
        jsonMilitaryBuildingOccupiedMessage.put("point", buildingToPoint(building));

        return jsonMilitaryBuildingOccupiedMessage;
    }

    JSONObject underAttackMessageToJson(UnderAttackMessage underAttackMessage) {
        JSONObject jsonUnderAttackMessage;
        jsonUnderAttackMessage = new JSONObject();

        Building building = underAttackMessage.building();

        jsonUnderAttackMessage.put("id", idManager.getId(underAttackMessage));
        jsonUnderAttackMessage.put("type", UNDER_ATTACK.toString());
        jsonUnderAttackMessage.put("houseId", idManager.getId(underAttackMessage.building()));
        jsonUnderAttackMessage.put("houseType", building.getSimpleName());
        jsonUnderAttackMessage.put("point", buildingToPoint(building));

        return jsonUnderAttackMessage;
    }

    JSONObject geologistFindMessageToJson(GeologistFindMessage geologistFindMessage) {
        JSONObject jsonGeologistFindMessage = new JSONObject();

        JSONObject jsonGeologistFindPoint = new JSONObject();

        jsonGeologistFindPoint.put("x", geologistFindMessage.point().x);
        jsonGeologistFindPoint.put("y", geologistFindMessage.point().y);

        jsonGeologistFindMessage.put("id", idManager.getId(geologistFindMessage));
        jsonGeologistFindMessage.put("type", GEOLOGIST_FIND.toString());
        jsonGeologistFindMessage.put("point", jsonGeologistFindPoint);

        jsonGeologistFindMessage.put("material", geologistFindMessage.material().toString());

        return jsonGeologistFindMessage;
    }

    public JSONObject gameMonitoringEventsToJson(GameChangesList gameChangesList, Player player) throws InvalidUserActionException {
        JSONObject jsonMonitoringEvents = new JSONObject();

        jsonMonitoringEvents.put("time", gameChangesList.getTime());

        Set<Building> allChangedBuildings = new HashSet<>(gameChangesList.getChangedBuildings());

        if (!gameChangesList.getNewFallingTrees().isEmpty()) {
            jsonMonitoringEvents.put("newFallingTrees", treesToJson(gameChangesList.getNewFallingTrees()));
        }

        if (!gameChangesList.getPromotedRoads().isEmpty()) {
            jsonMonitoringEvents.put("changedRoads", roadsToJson(gameChangesList.getPromotedRoads()));
        }

        if (!gameChangesList.getNewStones().isEmpty()) {
            jsonMonitoringEvents.put("newStones", newStonesToJson(gameChangesList.getNewStones()));
        }

        if (!gameChangesList.getChangedStones().isEmpty()) {
            jsonMonitoringEvents.put("changedStones", newStonesToJson(gameChangesList.getChangedStones()));
        }

        gameChangesList.getUpgradedBuildings().forEach(newAndOldBuilding -> {
            // Move the id to the new building
            System.out.println("Old building: " + newAndOldBuilding.oldBuilding);
            System.out.println("Id: " + idManager.getId(newAndOldBuilding.oldBuilding));
            System.out.println("New building: " + newAndOldBuilding.newBuilding);

            idManager.updateObject(newAndOldBuilding.oldBuilding, newAndOldBuilding.newBuilding);

            // Tell the frontend that the house has changed
            allChangedBuildings.add(newAndOldBuilding.newBuilding);
        });

        if (!gameChangesList.getWorkersWithNewTargets().isEmpty()) {
            jsonMonitoringEvents.put("workersWithNewTargets", workersWithNewTargetsToJson(gameChangesList.getWorkersWithNewTargets()));

            jsonMonitoringEvents.put("wildAnimalsWithNewTargets", wildAnimalsWithNewTargetsToJson(gameChangesList.getWorkersWithNewTargets()));

            jsonMonitoringEvents.put("shipsWithNewTargets", shipWithNewTargetsToJson(gameChangesList.getWorkersWithNewTargets()));
        }

        if (!gameChangesList.getWorkersWithStartedActions().isEmpty()) {
            jsonMonitoringEvents.put("workersWithStartedActions", workersAndActionsToJson(gameChangesList.getWorkersWithStartedActions()));
        }

        if (!gameChangesList.getNewShips().isEmpty()) {
            jsonMonitoringEvents.put("newShips", shipsToJson(gameChangesList.getNewShips()));
        }

        if (!gameChangesList.getFinishedShips().isEmpty()) {
            jsonMonitoringEvents.put("finishedShips", shipsToJson(gameChangesList.getFinishedShips()));
        }

        if (!gameChangesList.getNewBuildings().isEmpty()) {
            jsonMonitoringEvents.put("newBuildings", newBuildingsToJson(gameChangesList.getNewBuildings(), player));
        }

        if (!gameChangesList.getNewFlags().isEmpty()) {
            jsonMonitoringEvents.put("newFlags", flagsToJson(gameChangesList.getNewFlags()));
        }

        if (!gameChangesList.getNewRoads().isEmpty()) {
            jsonMonitoringEvents.put("newRoads", roadsToJson(gameChangesList.getNewRoads()));
        }

        if (!gameChangesList.getNewTrees().isEmpty()) {
            jsonMonitoringEvents.put("newTrees", newTreesToJson(gameChangesList.getNewTrees()));
        }

        if (!gameChangesList.getDiscoveredDeadTrees().isEmpty()) {
            jsonMonitoringEvents.put("discoveredTrees", pointsToJson(gameChangesList.getDiscoveredDeadTrees()));
        }

        if (!gameChangesList.getNewDiscoveredLand().isEmpty()) {
            jsonMonitoringEvents.put("newDiscoveredLand", newDiscoveredLandToJson(gameChangesList.getNewDiscoveredLand()));
        }

        if (!gameChangesList.getNewCrops().isEmpty()) {
            jsonMonitoringEvents.put("newCrops", newCropsToJson(gameChangesList.getNewCrops()));
        }

        if (!gameChangesList.getNewSigns().isEmpty()) {
            jsonMonitoringEvents.put("newSigns", newSignsToJson(gameChangesList.getNewSigns()));
        }

        if (!allChangedBuildings.isEmpty()) {
            jsonMonitoringEvents.put("changedBuildings", changedBuildingsToJson(allChangedBuildings, player));
        }

        if (!gameChangesList.getChangedFlags().isEmpty()) {
            jsonMonitoringEvents.put("changedFlags", flagsToJson(gameChangesList.getChangedFlags()));
        }

        if (!gameChangesList.getNewDecorations().isEmpty()) {
            jsonMonitoringEvents.put("newDecorations", pointsAndDecorationsToJson(gameChangesList.getNewDecorations()));
        }

        if (!gameChangesList.getRemovedDecorations().isEmpty()) {
            jsonMonitoringEvents.put("removedDecorations", pointsToJson(gameChangesList.getRemovedDecorations()));
        }

        if (!gameChangesList.getRemovedWorkers().isEmpty()) {
            jsonMonitoringEvents.put("removedWorkers", removedWorkersToJson(gameChangesList.getRemovedWorkers()));

            jsonMonitoringEvents.put("removedWildAnimals", removedWildAnimalsToJson(gameChangesList.getRemovedWorkers()));
        }

        if (!gameChangesList.getRemovedBuildings().isEmpty()) {
            jsonMonitoringEvents.put("removedBuildings", removedBuildingsToJson(gameChangesList.getRemovedBuildings()));
        }

        if (!gameChangesList.getRemovedFlags().isEmpty()){
            jsonMonitoringEvents.put("removedFlags", removedFlagsToJson(gameChangesList.getRemovedFlags()));
        }

        if (!gameChangesList.getRemovedRoads().isEmpty()) {
            jsonMonitoringEvents.put("removedRoads", removedRoadsToJson(gameChangesList.getRemovedRoads()));
        }

        if (!gameChangesList.getRemovedTrees().isEmpty()) {
            jsonMonitoringEvents.put("removedTrees", removedTreesToJson(gameChangesList.getRemovedTrees()));
        }

        if (!gameChangesList.getRemovedDeadTrees().isEmpty()) {
            jsonMonitoringEvents.put("removedDeadTrees", pointsToJson(gameChangesList.getRemovedDeadTrees()));
        }

        if (!gameChangesList.getChangedBorders().isEmpty()) {
            jsonMonitoringEvents.put("changedBorders", borderChangesToJson(gameChangesList.getChangedBorders()));
        }

        if (!gameChangesList.getChangedAvailableConstruction().isEmpty()) {
            jsonMonitoringEvents.put(
                    "changedAvailableConstruction",
                    availableConstructionChangesToJson(gameChangesList.getChangedAvailableConstruction(), player)
            );
        }

        if (!gameChangesList.getRemovedCrops().isEmpty()) {
            jsonMonitoringEvents.put("removedCrops", cropsToIdJson(gameChangesList.getRemovedCrops()));
        }

        if (!gameChangesList.getHarvestedCrops().isEmpty()) {
            jsonMonitoringEvents.put("harvestedCrops", cropsToIdJson(gameChangesList.getHarvestedCrops()));
        }

        if (!gameChangesList.getRemovedSigns().isEmpty()) {
            jsonMonitoringEvents.put("removedSigns", removedSignsToJson(gameChangesList.getRemovedSigns()));
        }

        if (!gameChangesList.getRemovedStones().isEmpty()) {
            jsonMonitoringEvents.put("removedStones", removedStonesToJson(gameChangesList.getRemovedStones()));
        }

        if (!gameChangesList.getNewGameMessages().isEmpty()) {
            jsonMonitoringEvents.put("newMessages", messagesToJson(gameChangesList.getNewGameMessages()));
        }

        if (!gameChangesList.getRemovedMessages().isEmpty()) {
            jsonMonitoringEvents.put("removedMessages", removedMessagesToJson(gameChangesList.getRemovedMessages()));
        }

        return jsonMonitoringEvents;
    }

    private JSONArray removedMessagesToJson(Collection<Message> removedMessages) {
        JSONArray jsonRemovedMessages = new JSONArray();

        removedMessages.forEach(message -> jsonRemovedMessages.add(idManager.getId(message)));

        return jsonRemovedMessages;
    }

    private JSONArray pointsAndDecorationsToJson(Map<Point, DecorationType> pointsAndDecorations) {
        JSONArray jsonPointsAndDecorations = new JSONArray();

        for (Map.Entry<Point, DecorationType> entry : pointsAndDecorations.entrySet()) {
            Point point = entry.getKey();
            DecorationType decorationType = entry.getValue();

            JSONObject pointAndDecorationJson = new JSONObject();

            pointAndDecorationJson.put("x", point.x);
            pointAndDecorationJson.put("y", point.y);
            pointAndDecorationJson.put("decoration", decorationType.name().toUpperCase());

            jsonPointsAndDecorations.add(pointAndDecorationJson);
        }

        return jsonPointsAndDecorations;
    }

    private JSONArray workersAndActionsToJson(Map<Worker, WorkerAction> workersWithStartedActions) {
        JSONArray workersAndActionsJson = new JSONArray();

        workersWithStartedActions.forEach((worker, action) -> {
            Point position = worker.getPosition();

            JSONObject jsonWorker = new JSONObject();

            jsonWorker.put("id", idManager.getId(worker));
            jsonWorker.put("x", position.x);
            jsonWorker.put("y", position.y);
            jsonWorker.put("direction", worker.getDirection().name().toUpperCase());
            jsonWorker.put("startedAction", action.name().toUpperCase());

            workersAndActionsJson.add(jsonWorker);
        });

        return workersAndActionsJson;
    }

    private JSONArray shipWithNewTargetsToJson(List<Worker> workers) {
        JSONArray jsonWorkers = new JSONArray();

        workers.forEach(worker -> {
            if (worker instanceof Ship ship) {

                jsonWorkers.add(shipToJson(ship));
            }
        });

        return jsonWorkers;
    }

    private JSONObject shipToJson(Ship ship) {
        JSONObject jsonShip = new JSONObject();

        if (ship.isUnderConstruction()) {
            jsonShip.put("state", "UNDER_CONSTRUCTION");
        } else if (ship.isReady()) {
            jsonShip.put("state", "READY");
        }

        jsonShip.put("x", ship.getPosition().x);
        jsonShip.put("y", ship.getPosition().y);

        jsonShip.put("direction", ship.getDirection().name().toUpperCase());

        JSONObject jsonCargos = new JSONObject();
        Map<Material, Integer> cargos = new EnumMap<>(Material.class);

        for (Cargo cargo : ship.getCargos()) {
            Material material = cargo.getMaterial();
            int amount = cargos.getOrDefault(cargo.getMaterial(), 0);

            cargos.put(material, amount + 1);
        }

        cargos.forEach((material, amount) -> jsonCargos.put(material.name().toUpperCase(), amount));

        jsonShip.put("cargo", jsonCargos);

        return jsonShip;
    }

    private JSONArray shipsToJson(List<Ship> ships) {
        JSONArray jsonShips = new JSONArray();

        ships.forEach(ship -> jsonShips.add(shipToJson(ship)));

        return jsonShips;
    }

    private JSONArray removedWildAnimalsToJson(List<Worker> removedWorkers) {
        JSONArray jsonRemovedWildAnimalIds = new JSONArray();

        for (Worker worker : removedWorkers) {
            if (! (worker instanceof WildAnimal)) {
                continue;
            }

            jsonRemovedWildAnimalIds.add(idManager.getId(worker));
        }

        return jsonRemovedWildAnimalIds;
    }

    private JSONArray wildAnimalsWithNewTargetsToJson(List<Worker> workersWithNewTargets) {
        JSONArray jsonWildAnimals = new JSONArray();

        for (Worker worker : workersWithNewTargets) {
            if (! (worker instanceof WildAnimal wildAnimal)) {
                continue;
            }

            jsonWildAnimals.add(wildAnimalToJson(wildAnimal));
        }

        return jsonWildAnimals;
    }

    private JSONArray newStonesToJson(Collection<Stone> newStones) {
        JSONArray jsonNewStones = new JSONArray();

        for (Stone stone : newStones) {
            jsonNewStones.add(stoneToJson(stone));
        }

        return jsonNewStones;
    }

    private JSONArray messagesToJson(List<Message> newGameMessages) {
        JSONArray jsonMessages = new JSONArray();

        for (Message message : newGameMessages) {
            if (message.getMessageType() == MILITARY_BUILDING_OCCUPIED) {
                jsonMessages.add(militaryBuildingOccupiedMessageToJson((MilitaryBuildingOccupiedMessage) message));
            } else if (message.getMessageType() == GEOLOGIST_FIND) {
                jsonMessages.add(geologistFindMessageToJson((GeologistFindMessage) message));
            } else if (message.getMessageType() == MILITARY_BUILDING_READY) {
                jsonMessages.add(militaryBuildingReadyMessageToJson((MilitaryBuildingReadyMessage) message));
            } else if (message.getMessageType() == NO_MORE_RESOURCES) {
                jsonMessages.add(noMoreResourcesMessageToJson((NoMoreResourcesMessage) message));
            } else if (message.getMessageType() == UNDER_ATTACK) {
                jsonMessages.add(underAttackMessageToJson((UnderAttackMessage) message));
            } else if (message.getMessageType() == BUILDING_CAPTURED) {
                jsonMessages.add(buildingCapturedMessageToJson((BuildingCapturedMessage) message));
            } else if (message.getMessageType() == BUILDING_LOST) {
                jsonMessages.add(buildingLostMessageToJson((BuildingLostMessage) message));
            } else if (message.getMessageType() == STORE_HOUSE_IS_READY) {
                jsonMessages.add(jsonStoreHouseIsReadyMessageToJson((StoreHouseIsReadyMessage) message));
            } else if (message.getMessageType() == TREE_CONSERVATION_PROGRAM_ACTIVATED) {
                jsonMessages.add(treeConservationProgramActivatedMessageToJson((TreeConservationProgramActivatedMessage) message));
            } else if (message.getMessageType() == TREE_CONSERVATION_PROGRAM_DEACTIVATED) {
                jsonMessages.add(treeConservationProgramDeactivatedMessageToJson((TreeConservationProgramDeactivatedMessage) message));
            } else if (message.getMessageType() == MILITARY_BUILDING_CAUSED_LOST_LAND) {
                jsonMessages.add(militaryBuildingCausedLostLandMessageToJson((MilitaryBuildingCausedLostLandMessage) message));
            }
        }

        return jsonMessages;
    }

    private JSONObject militaryBuildingCausedLostLandMessageToJson(MilitaryBuildingCausedLostLandMessage message) {
        JSONObject jsonMilitaryBuildingCausedLostLandMessage = new JSONObject();

        Building building = message.building();

        jsonMilitaryBuildingCausedLostLandMessage.put("type", MILITARY_BUILDING_CAUSED_LOST_LAND.toString());
        jsonMilitaryBuildingCausedLostLandMessage.put("houseId", idManager.getId(building));
        jsonMilitaryBuildingCausedLostLandMessage.put("point", buildingToPoint(building));

        return jsonMilitaryBuildingCausedLostLandMessage;
    }

    private JSONObject buildingToPoint(Building building) {
        JSONObject jsonPoint = new JSONObject();

        jsonPoint.put("x", building.getPosition().x);
        jsonPoint.put("y", building.getPosition().y);

        return jsonPoint;
    }

    private JSONObject treeConservationProgramDeactivatedMessageToJson(TreeConservationProgramDeactivatedMessage message) {
        JSONObject jsonTreeConservationProgramDeactivated = new JSONObject();

        jsonTreeConservationProgramDeactivated.put("type", TREE_CONSERVATION_PROGRAM_DEACTIVATED.toString());

        return jsonTreeConservationProgramDeactivated;
    }

    private JSONObject treeConservationProgramActivatedMessageToJson(TreeConservationProgramActivatedMessage message) {
        JSONObject jsonTreeConservationProgramActivated = new JSONObject();

        jsonTreeConservationProgramActivated.put("type", TREE_CONSERVATION_PROGRAM_ACTIVATED.toString());

        return jsonTreeConservationProgramActivated;
    }

    private JSONArray availableConstructionChangesToJson(Collection<Point> changedAvailableConstruction, Player player) {
        GameMap map = player.getMap();

        JSONArray jsonChangedAvailableConstruction = new JSONArray();

        synchronized (map) {
            for (Point point : changedAvailableConstruction) {
                JSONObject jsonPointAndAvailableConstruction = new JSONObject();
                JSONArray jsonAvailableConstruction = new JSONArray();

                if (map.isAvailableFlagPoint(player, point)) {
                    jsonAvailableConstruction.add("flag");
                }

                Size size = map.isAvailableHousePoint(player, point);

                if (size != null) {
                    jsonAvailableConstruction.add(size.name().toLowerCase());
                }

                if (map.isAvailableMinePoint(player, point)) {
                    jsonAvailableConstruction.add("mine");
                }

                jsonPointAndAvailableConstruction.put("available", jsonAvailableConstruction);
                jsonPointAndAvailableConstruction.put("x", point.x);
                jsonPointAndAvailableConstruction.put("y", point.y);

                jsonChangedAvailableConstruction.add(jsonPointAndAvailableConstruction);
            }
        }

        return jsonChangedAvailableConstruction;
    }

    private JSONArray borderChangesToJson(List<BorderChange> changedBorders) {
        JSONArray jsonBorderChanges = new JSONArray();

        for (BorderChange borderChange : changedBorders) {
            JSONObject jsonBorderChange = new JSONObject();

            jsonBorderChange.put("playerId", idManager.getId(borderChange.getPlayer()));
            jsonBorderChange.put("newBorder", pointsToJson(borderChange.getNewBorder()));
            jsonBorderChange.put("removedBorder", pointsToJson(borderChange.getRemovedBorder()));

            jsonBorderChanges.add(jsonBorderChange);
        }

        return jsonBorderChanges;
    }

    private JSONArray removedStonesToJson(List<Stone> removedStones) {
        JSONArray jsonRemovedStones = new JSONArray();

        for (Stone stone : removedStones) {
            jsonRemovedStones.add(idManager.getId(removedStones));
        }

        return jsonRemovedStones;
    }

    private JSONArray removedSignsToJson(List<Sign> removedSigns) {
        return objectsToJsonIdArray(removedSigns);
    }

    private JSONArray newSignsToJson(List<Sign> newSigns) {
        JSONArray jsonSigns = new JSONArray();

        for (Sign sign : newSigns) {
            jsonSigns.add(signToJson(sign));
        }

        return jsonSigns;
    }

    private JSONArray cropsToIdJson(List<Crop> removedCrops) {
        JSONArray jsonRemovedCrops = new JSONArray();

        for (Crop crop : removedCrops) {
            jsonRemovedCrops.add(idManager.getId(crop));
        }

        return jsonRemovedCrops;
    }

    private JSONArray newCropsToJson(List<Crop> newCrops) {
        return cropsToJson(newCrops);
    }

    private JSONArray cropsToJson(List<Crop> newCrops) {
        JSONArray jsonCrops = new JSONArray();

        for (Crop crop : newCrops) {
            jsonCrops.add(cropToJson(crop));
        }

        return jsonCrops;
    }

    private JSONArray newDiscoveredLandToJson(Collection<Point> newDiscoveredLand) {
        return pointsToJson(newDiscoveredLand);
    }

    private JSONArray removedTreesToJson(List<Tree> removedTrees) {
        JSONArray jsonRemovedTrees = new JSONArray();

        for (Tree tree : removedTrees) {
            jsonRemovedTrees.add(idManager.getId(tree));
        }

        return jsonRemovedTrees;
    }

    private JSONArray newTreesToJson(List<Tree> newTrees) {
        return treesToJson(newTrees);
    }

    private JSONArray treesToJson(Collection<Tree> newTrees) {
        JSONArray jsonTrees = new JSONArray();

        for (Tree tree : newTrees) {
            jsonTrees.add(treeToJson(tree));
        }

        return jsonTrees;
    }

    private JSONArray changedBuildingsToJson(Collection<Building> changedBuildings, Player player) throws InvalidUserActionException {
        return housesToJson(changedBuildings, player);
    }

    private JSONArray removedRoadsToJson(List<Road> removedRoads) {
        return objectsToJsonIdArray(removedRoads);
    }

    private JSONArray removedFlagsToJson(List<Flag> removedFlags) {
        return objectsToJsonIdArray(removedFlags);
    }

    private JSONArray removedBuildingsToJson(List<Building> removedBuildings) {
        return objectsToJsonIdArray(removedBuildings);
    }

    private JSONArray removedWorkersToJson(List<Worker> removedWorkers) {
        JSONArray jsonIdArray = new JSONArray();

        for (Worker worker : removedWorkers) {
            if (worker instanceof WildAnimal) {
                continue;
            }

            jsonIdArray.add(idManager.getId(worker));
        }

        return jsonIdArray;
    }

    private JSONArray objectsToJsonIdArray(List<?> gameObjects) {
        JSONArray jsonIdArray = new JSONArray();

        for (Object gameObject : gameObjects) {
            jsonIdArray.add(idManager.getId(gameObject));
        }

        return jsonIdArray;
    }

    private JSONArray roadsToJson(List<Road> newRoads) {
        JSONArray jsonRoads = new JSONArray();

        for (Road road : newRoads) {
            jsonRoads.add(roadToJson(road));
        }

        return jsonRoads;
    }

    private JSONArray flagsToJson(Collection<Flag> flags) {
        JSONArray jsonFlags = new JSONArray();

        for (Flag flag : flags) {
            jsonFlags.add(flagToJson(flag));
        }

        return jsonFlags;
    }

    private JSONArray newBuildingsToJson(List<Building> newBuildings, Player player) throws InvalidUserActionException {
        JSONArray jsonNewBuildings = new JSONArray();

        for (Building building : newBuildings) {
            jsonNewBuildings.add(houseToJson(building, player));
        }

        return jsonNewBuildings;
    }

    private JSONArray workersWithNewTargetsToJson(List<Worker> workersWithNewTargets) {
        JSONArray jsonWorkersWithNewTarget = new JSONArray();

        for (Worker worker : workersWithNewTargets) {
            JSONObject jsonWorkerWithNewTarget = new JSONObject();

            if (worker instanceof WildAnimal) {
                continue;
            }

            jsonWorkerWithNewTarget.put("id", idManager.getId(worker));
            jsonWorkerWithNewTarget.put("path", pointsToJson(worker.getPlannedPath()));

            jsonWorkerWithNewTarget.put("x", worker.getPosition().x);
            jsonWorkerWithNewTarget.put("y", worker.getPosition().y);

            jsonWorkerWithNewTarget.put("type", workerTypeToJson(worker));
            jsonWorkerWithNewTarget.put("color", worker.getPlayer().getColor().name().toUpperCase());
            jsonWorkerWithNewTarget.put("nation", worker.getPlayer().getNation().name().toUpperCase());

            jsonWorkerWithNewTarget.put("direction", worker.getDirection().name().toUpperCase());

            if (worker instanceof Courier courier) {
                jsonWorkerWithNewTarget.put("bodyType", courier.getBodyType().name().toUpperCase());
            }

            if (worker.getCargo() != null) {
                jsonWorkerWithNewTarget.put("cargo", worker.getCargo().getMaterial().getSimpleName().toUpperCase());
            }

            jsonWorkersWithNewTarget.add(jsonWorkerWithNewTarget);
        }

        return jsonWorkersWithNewTarget;
    }

    private String workerTypeToJson(Worker worker) {

        if (worker.isSoldier()) {
            Soldier soldier = (Soldier) worker;

            return rankToTypeString(soldier);
        } else {
            return worker.getClass().getSimpleName();
        }
    }

    public JSONArray transportPriorityToJson(List<TransportCategory> transportPriorityList) {
        JSONArray jsonTransportPriority = new JSONArray();

        for (TransportCategory category : transportPriorityList) {
            jsonTransportPriority.add(category.name().toUpperCase());
        }

        return jsonTransportPriority;
    }

    public Set<Point> jsonToPointsSet(JSONArray avoid) {
        Set<Point> pointsSet = new HashSet<>();

        for (Object jsonPoint : avoid) {
            Point point = jsonToPoint((JSONObject) jsonPoint);

            pointsSet.add(point);
        }

        return pointsSet;
    }

    public void printTimestamp(String message) {

        Date date = new Date();
        long timeMilli = date.getTime();
        System.out.println(message + ": " + timeMilli);
    }

    public JSONObject wildAnimalToJson(WildAnimal wildAnimal) {
        JSONObject jsonWildAnimal = pointToJson(wildAnimal.getPosition());

        jsonWildAnimal.put("type", wildAnimal.getType().name());
        jsonWildAnimal.put("id", idManager.getId(wildAnimal));
        jsonWildAnimal.put("betweenPoints", !wildAnimal.isExactlyAtPoint());
        jsonWildAnimal.put("direction", wildAnimal.getDirection().name().toUpperCase());

        if (wildAnimal.getPlannedPath() != null && wildAnimal.getPlannedPath().isEmpty()) {
            jsonWildAnimal.put("path", pointsToJson(wildAnimal.getPlannedPath()));
        }

        if (!wildAnimal.isExactlyAtPoint()) {
            jsonWildAnimal.put("previous", pointToJson(wildAnimal.getLastPoint()));
            jsonWildAnimal.put("next", pointToJson(wildAnimal.getNextPoint()));
            jsonWildAnimal.put("percentageTraveled", wildAnimal.getPercentageOfDistanceTraveled());
        } else {
            jsonWildAnimal.put("percentageTraveled", 0);
        }

        return jsonWildAnimal;
    }

    public JSONObject playerViewToJson(String playerId, GameMap map, Player player, GameResource gameResource) throws InvalidUserActionException {
        JSONObject jsonView = new JSONObject();

        JSONArray  jsonHouses                = new JSONArray();
        JSONArray  trees                     = new JSONArray();
        JSONArray  jsonStones                = new JSONArray();
        JSONArray  jsonWorkers               = new JSONArray();
        JSONArray  jsonWildAnimals           = new JSONArray();
        JSONArray  jsonFlags                 = new JSONArray();
        JSONArray  jsonRoads                 = new JSONArray();
        JSONArray  jsonDiscoveredPoints      = new JSONArray();
        JSONArray  jsonBorders               = new JSONArray();
        JSONArray  jsonSigns                 = new JSONArray();
        JSONArray  jsonCrops                 = new JSONArray();
        JSONObject jsonAvailableConstruction = new JSONObject();
        JSONArray  jsonDeadTrees             = new JSONArray();
        JSONArray  jsonDecorations           = new JSONArray();

        jsonView.put("trees", trees);
        jsonView.put("houses", jsonHouses);
        jsonView.put("stones", jsonStones);
        jsonView.put("workers", jsonWorkers);
        jsonView.put("wildAnimals", jsonWildAnimals);
        jsonView.put("flags", jsonFlags);
        jsonView.put("roads", jsonRoads);
        jsonView.put("discoveredPoints", jsonDiscoveredPoints);
        jsonView.put("borders", jsonBorders);
        jsonView.put("signs", jsonSigns);
        jsonView.put("crops", jsonCrops);
        jsonView.put("availableConstruction", jsonAvailableConstruction);
        jsonView.put("deadTrees", jsonDeadTrees);
        jsonView.put("decorations", jsonDecorations);

        /* Put the game status */
        jsonView.put("gameState", gameResource.status.name().toUpperCase());

        /* Protect access to the map to avoid interference */
        synchronized (map) {
            Set<Point> discoveredLand = player.getDiscoveredLand();

            /* Fill in houses */
            for (Building building : map.getBuildings()) {

                if (!discoveredLand.contains(building.getPosition())) {
                    continue;
                }

                jsonHouses.add(houseToJson(building, player));
            }

            /* Fill in trees */
            for (Tree tree : map.getTrees()) {
                if (!discoveredLand.contains(tree.getPosition())) {
                    continue;
                }

                trees.add(treeToJson(tree));
            }

            /* Fill in stones */
            for (Stone stone : map.getStones()) {

                if (!discoveredLand.contains(stone.getPosition())) {
                    continue;
                }

                jsonStones.add(stoneToJson(stone));
            }

            /* Fill in workers */
            for (Worker worker : map.getWorkers()) {

                if (!discoveredLand.contains(worker.getPosition())) {
                    continue;
                }

                if (worker.isInsideBuilding()) {
                    continue;
                }

                jsonWorkers.add(workerToJson(worker));
            }

            /* Fill in flags */
            for (Flag flag : map.getFlags()) {

                if (!discoveredLand.contains(flag.getPosition())) {
                    continue;
                }

                jsonFlags.add(flagToJson(flag));
            }

            /* Fill in roads */
            for (Road road : map.getRoads()) {

                boolean inside = false;

                /* Filter roads the player cannot see */
                for (Point p : road.getWayPoints()) {
                    if (discoveredLand.contains(p)) {
                        inside = true;

                        break;
                    }
                }

                if (!inside) {
                    continue;
                }

                jsonRoads.add(roadToJson(road));
            }

            /* Fill in the points the player has discovered */
            for (Point point : discoveredLand) {
                jsonDiscoveredPoints.add(pointToJson(point));
            }

            jsonBorders.add(borderToJson(player, playerId));

            /* Fill in the signs */
            for (Sign sign : map.getSigns()) {

                if (!discoveredLand.contains(sign.getPosition())) {
                    continue;
                }

                jsonSigns.add(signToJson(sign));
            }

            /* Fill in wild animals */
            for (WildAnimal animal : map.getWildAnimals()) {

                if (!discoveredLand.contains(animal.getPosition())) {
                    continue;
                }

                /* Animal is an extension of worker so the same method is used */
                jsonWildAnimals.add(wildAnimalToJson(animal));
            }

            /* Fill in crops */
            for (Crop crop : map.getCrops()) {

                if (!discoveredLand.contains(crop.getPosition())) {
                    continue;
                }

                jsonCrops.add(cropToJson(crop));
            }

            /* Fill in dead trees */
            for (Point point : map.getDeadTrees()) {
                jsonDeadTrees.add(pointToJson(point));
            }

            /* Fill in available construction */
            for (Point point : map.getAvailableFlagPoints(player)) {

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                String key = point.x + "," + point.y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add("flag");
            }

            for (Map.Entry<Point, Size> site : map.getAvailableHousePoints(player).entrySet()) {

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(site.getKey())) {
                    continue;
                }

                String key = site.getKey().x + "," + site.getKey().y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add(site.getValue().toString().toLowerCase());
            }

            for (Point point : map.getAvailableMinePoints(player)) {

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                String key = point.x + "," + point.y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add("mine");
            }

            /* Fill in decorations */
            for (Map.Entry<Point, DecorationType> entry : map.getDecorations().entrySet()) {
                Point point = entry.getKey();
                DecorationType decorationType = entry.getValue();

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                jsonDecorations.add(decorationToJson(decorationType, point));
            }

            /* Add the messages */
            jsonView.put("messages", messagesToJson(player.getMessages()));
        }

        return jsonView;
    }

    public JSONObject gameResourceToJson(GameResource gameResource) {
        JSONObject jsonGameResource = new JSONObject();

        if (gameResource.getPlayers() != null) {
            jsonGameResource.put("players", playersToJson(gameResource.getPlayers(), gameResource));
        } else {
            jsonGameResource.put("players", Collections.emptyList());
        }

        MapFile mapFile = gameResource.getMapFile();

        if (mapFile != null) {
            jsonGameResource.put("mapId", idManager.getId(mapFile));

            jsonGameResource.put("map", mapFileToJson(mapFile));
        }

        if (gameResource.isNameSet()) {
            jsonGameResource.put("name", gameResource.getName());
        } else {
            jsonGameResource.put("name", "");
        }

        jsonGameResource.put("id", idManager.getId(gameResource));
        jsonGameResource.put("status", gameResource.status.name());
        jsonGameResource.put("resources", gameResource.getResources().name());
        jsonGameResource.put("othersCanJoin", gameResource.getOthersCanJoin());


        return jsonGameResource;
    }
}
