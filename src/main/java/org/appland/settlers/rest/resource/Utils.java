package org.appland.settlers.rest.resource;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.chat.ChatManager;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.model.BorderChange;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Ship;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.WildAnimal;
import org.appland.settlers.model.actors.Worker;
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
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.LookoutTower;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.WatchTower;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.messages.BombardedByCatapultMessage;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.GameEndedMessage;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.messages.HarborIsFinishedMessage;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.messages.MilitaryBuildingCausedLostLandMessage;
import org.appland.settlers.model.messages.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.messages.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;
import org.appland.settlers.model.messages.ShipHasReachedDestinationMessage;
import org.appland.settlers.model.messages.ShipReadyForExpeditionMessage;
import org.appland.settlers.model.messages.StoreHouseIsReadyMessage;
import org.appland.settlers.model.messages.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.messages.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.messages.UnderAttackMessage;
import org.appland.settlers.rest.GameTicker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.appland.settlers.model.messages.Message.MessageType.*;

class Utils {
    private final IdManager idManager;

    Utils(IdManager idManager) {
        this.idManager = idManager;
    }

    public static JSONObject decorationToJson(DecorationType decorationType, Point point) {
        return new JSONObject(Map.of(
                "x", point.x,
                "y", point.y,
                "decoration", decorationType.name().toUpperCase()
        ));
    }

    public static JSONObject messageJsonToReplyJson(JSONObject jsonBody) {
        return new JSONObject(Map.of(
                "requestId", (Long) jsonBody.get("requestId")
        ));
    }

    JSONArray gamesToJson(Collection<GameResource> games) {
        return toJsonArray(games, this::gameToJson);
    }

    JSONArray chatMessagesToRoomToJson(Collection<ChatManager.ChatMessage> chatMessages, String roomId) {
        return toJsonArray(chatMessages, chatMessage -> new JSONObject(Map.of(
                "id", idManager.getId(chatMessage),
                "from", chatMessage.from().getName(),
                "toRoomId", roomId,
                "text", chatMessage.text(),
                "time", simpleTimeToJson(chatMessage.time())
        )));
    }

    JSONObject simpleTimeToJson(ChatManager.SimpleTime time) {
        return new JSONObject(Map.of(
                "hours", time.hours(),
                "minutes", time.minutes(),
                "seconds", time.seconds()
        ));
    }

    JSONObject gameToJson(GameResource gameResource) {
        var jsonGame = new JSONObject(Map.of(
                "id", idManager.getId(gameResource),
                "name", gameResource.getName(),
                "players", playersToJson(gameResource.getPlayers(), gameResource),
                "status", gameResource.status.name().toUpperCase(),
                "initialResources", gameResource.getResources().name().toUpperCase(),
                "othersCanJoin", gameResource.getOthersCanJoin()
        ));

        MapFile mapFile = gameResource.getMapFile();

        if (mapFile != null) {
            jsonGame.put("mapId", idManager.getId(mapFile));
            jsonGame.put("map", mapFileToJson(mapFile));
        }

        if (gameResource.isStarted()) {
            var gameSpeed = gameResource.getGameSpeed();

            int tick = switch (gameSpeed) {
                case FAST -> 100;
                case NORMAL -> 200;
                case SLOW -> 400;
            };

            jsonGame.put("gameSpeed", gameSpeed.name().toUpperCase());
            jsonGame.put("tick", tick);
        }

        return jsonGame;
    }

    JSONArray playersToJson(Collection<Player> players, GameResource gameResource) {
        return toJsonArray(players, player -> playerToJson(player, idManager.getId(player), gameResource));
    }

    JSONObject playerToJson(Player player, String playerId, GameResource gameResource) {
        JSONArray jsonDiscoveredPoints = new JSONArray();

        for (Point point : player.getDiscoveredLand()) {
            jsonDiscoveredPoints.add(pointToJson(point));
        }

        var jsonPlayer = new JSONObject(Map.of(
                "id", playerId,
                "name", player.getName(),
                "color", player.getColor().name().toUpperCase(),
                "nation", player.getNation().name(),
                "type", gameResource.isComputerPlayer(player) ? "COMPUTER" : "HUMAN",
                "discoveredPoints", jsonDiscoveredPoints
        ));

        /* Get the player's "center spot" */
        for (Building building : player.getBuildings()) {
            if (building instanceof Headquarter) {
                jsonPlayer.put("centerPoint", pointToJson(building.getPosition()));

                break;
            }
        }

        return jsonPlayer;
    }

    JSONArray pointsToJson(Collection<Point> points) {
        return toJsonArray(points, this::pointToJson);
    }

    JSONObject pointToJson(Point point) {
        return new JSONObject(Map.of(
                "x", point.x,
                "y", point.y
        ));
    }

    List<Player> jsonToPlayers(JSONArray jsonPlayers) {
        List<Player> players = new ArrayList<>();

        if (jsonPlayers != null) {
            for (var jsonPlayer : jsonPlayers) {
                players.add(jsonToPlayer((JSONObject) jsonPlayer));
            }
        }

        return players;
    }

    Player jsonToPlayer(JSONObject jsonPlayer) {
        if (jsonPlayer.containsKey("id")) {
            return (Player) idManager.getObject((String) jsonPlayer.get("id"));
        }

        String name = (String) jsonPlayer.get("name");
        PlayerColor color = jsonToColor((String) jsonPlayer.get("color"));

        String nationString = (String) jsonPlayer.get("nation");
        Nation nation = Nation.valueOf(nationString);

        return new Player(name, color, nation, PlayerType.HUMAN);
    }

    private PlayerColor jsonToColor(String colorName) {
        return PlayerColor.valueOf(colorName);
    }

    JSONObject terrainToJson(GameMap map) {
        JSONArray jsonTrianglesBelow = new JSONArray();
        JSONArray jsonTrianglesBelowRight = new JSONArray();
        JSONArray jsonHeights = new JSONArray();

        int start = 1;

        for (int y = 1; y < map.getHeight(); y++) {
            for (int x = start; x + 1 < map.getWidth(); x += 2) {
                Point point = new Point(x, y);

                Vegetation below = map.getVegetationBelow(point);
                Vegetation downRight = map.getVegetationDownRight(point);

                jsonTrianglesBelow.add(below.toInt());
                jsonTrianglesBelowRight.add(downRight.toInt());
                jsonHeights.add(map.getHeightAtPoint(point));
            }

            if (start == 1) {
                start = 2;
            } else {
                start = 1;
            }
        }

        return new JSONObject(Map.of(
                "straightBelow", jsonTrianglesBelow,
                "belowToTheRight", jsonTrianglesBelowRight,
                "heights", jsonHeights,
                "width", map.getWidth(),
                "height", map.getHeight()
        ));
    }

    private String vegetationToJson(Vegetation vegetation) {
        return switch (vegetation) {
            case SAVANNAH -> "SA";
            case MOUNTAIN_1 -> "MO1";
            case SNOW -> "SN";
            case SWAMP -> "SW";
            case DESERT_1 -> "D1";
            case WATER -> "W1";
            case BUILDABLE_WATER -> "B";
            case DESERT_2 -> "D2";
            case MEADOW_1 -> "ME1";
            case MEADOW_2 -> "ME2";
            case MEADOW_3 -> "ME3";
            case MOUNTAIN_2 -> "MO2";
            case MOUNTAIN_3 -> "MO3";
            case MOUNTAIN_4 -> "MO4";
            case STEPPE -> "ST";
            case FLOWER_MEADOW -> "FM";
            case LAVA_1 -> "L1";
            case MAGENTA -> "MA";
            case MOUNTAIN_MEADOW -> "MM";
            case WATER_2 -> "W2";
            case LAVA_2 -> "L2";
            case LAVA_3 -> "L3";
            case LAVA_4 -> "L4";
            case BUILDABLE_MOUNTAIN -> "BM";
        };
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

    private String buildingStateToString(Building building) {
        if (building.isPlanned()) {
            return "PLANNED";
        } else if (building.isUnderConstruction()) {
            return "UNFINISHED";
        } else if (building.isReady() && !building.isOccupied()) {
            return "UNOCCUPIED";
        } else if (building.isReady() && building.isOccupied()) {
            return "OCCUPIED";
        } else if (building.isBurningDown()) {
            return "BURNING";
        } else if (building.isDestroyed()) {
            return "DESTROYED";
        }

        System.out.println("Can't translate state to string for building: " + building);

        System.exit(1);

        return "";
    }

    JSONObject houseToJson(Building building, Player player) throws InvalidUserActionException {
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

        var jsonHouse = new JSONObject(Map.of(
                "id", idManager.getId(building),
                "x", building.getPosition().x,
                "y", building.getPosition().y,
                "playerId", idManager.getId(building.getPlayer()),
                "type", building.getClass().getSimpleName(),
                "nation", building.getPlayer().getNation().name().toUpperCase(),
                "resources", jsonResources,
                "state", buildingStateToString(building)
        ));

        if (building.canProduce()) {
            JSONArray jsonProduces = new JSONArray();

            jsonHouse.put("productivity", building.getProductivity());
            jsonHouse.put("produces", jsonProduces);

            for (Material material : building.getProducedMaterial()) {
                jsonProduces.add(material.name().toUpperCase());
            }

            jsonHouse.put("productionEnabled", building.isProductionEnabled());
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
        return switch (buildingType) {
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
    }

    JSONObject treeToJson(Tree tree) {
        return new JSONObject(Map.of(
                "id", idManager.getId(tree),
                "x", tree.getPosition().x,
                "y", tree.getPosition().y,
                "type", tree.getTreeType().name().toUpperCase(),
                "size", tree.getSize().name().toUpperCase()
        ));
    }

    JSONObject stoneToJson(Stone stone) {
        return new JSONObject(Map.of(
                "id", idManager.getId(stone),
                "x", stone.getPosition().x,
                "y", stone.getPosition().y,
                "type", stone.getStoneType().name().toUpperCase(),
                "amount", stone.getStoneAmount().name().toUpperCase()
        ));
    }

    JSONObject workerToJson(Worker worker) {
        var jsonWorker = new JSONObject(Map.of(
                "id", idManager.getId(worker),
                "x", worker.getPosition().x,
                "y", worker.getPosition().y,
                "type", workerTypeToJson(worker),
                "inside", worker.isInsideBuilding(),
                "betweenPoints", !worker.isExactlyAtPoint(),
                "direction", worker.getDirection().name().toUpperCase(),
                "color", worker.getPlayer().getColor().name().toUpperCase(),
                "nation", worker.getPlayer().getNation().name().toUpperCase()
        ));

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
        var jsonFlag = new JSONObject(Map.of(
                "id", idManager.getId(flag),
                "x", flag.getPosition().x,
                "y", flag.getPosition().y,
                "playerId", idManager.getId(flag.getPlayer()),
                "type", flag.getType().name(),
                "nation", flag.getPlayer().getNation().name().toUpperCase(),
                "color", flag.getPlayer().getColor().name().toUpperCase()
        ));

        if (!flag.getStackedCargo().isEmpty()) {
            jsonFlag.put("stackedCargo", cargosToMaterialJson(flag.getStackedCargo()));
        }

        return jsonFlag;
    }

    private JSONArray cargosToMaterialJson(Collection<Cargo> cargos) {
        return toJsonArray(cargos, cargo -> cargo.getMaterial().getSimpleName().toUpperCase());
    }

    JSONObject roadToJson(Road road) {
        var jsonPoints = toJsonArray(road.getWayPoints(), this::pointToJson);

        return new JSONObject(Map.of(
                "id", idManager.getId(road),
                "points", jsonPoints,
                "type", road.isMainRoad() ? "MAIN" : "NORMAL"
        ));
    }

    JSONObject borderToJson(Player player, String playerId) {
        var jsonBorderPoints = new JSONArray();

        player.getBorderPoints().stream()
                .map(this::pointToJson)
                .forEach(jsonBorderPoints::add);

        return new JSONObject(Map.of(
                "playerId", playerId,
                "points", jsonBorderPoints
        ));
    }

    JSONObject signToJson(Sign sign) {
        var jsonSign = new JSONObject(Map.of(
                "id", idManager.getId(sign),
                "x", sign.getPosition().x,
                "y", sign.getPosition().y
        ));

        if (sign.isEmpty()) {
            jsonSign.put("type", null);
        } else {
            switch (sign.getType()) {
                case GOLD -> jsonSign.put("type", "GOLD");
                case IRON -> jsonSign.put("type", "IRON");
                case COAL -> jsonSign.put("type", "COAL");
                case STONE -> jsonSign.put("type", "STONE");
                case WATER -> jsonSign.put("type", "WATER");
                default -> {
                    System.out.println("Cannot have sign of type " + sign.getType());
                    System.exit(1);
                }
            }
        }

        if (sign.getSize() != null) {
            jsonSign.put("amount", sign.getSize().toString().toUpperCase());
        }

        return jsonSign;
    }

    JSONObject cropToJson(Crop crop) {
        return new JSONObject(Map.of(
                "id", idManager.getId(crop),
                "state", crop.getGrowthState().name().toUpperCase(),
                "type", crop.getType().name().toUpperCase()
        ));
    }

    JSONObject playerToJson(Player player) {
        return new JSONObject(Map.of(
                "id", idManager.getId(player),
                "name", player.getName(),
                "color", player.getColor().name().toUpperCase(),
                "nation", player.getNation().name().toUpperCase()
        ));
    }

    JSONArray mapFilesToJson(Collection<MapFile> mapFiles) {
        return toJsonArray(mapFiles, this::mapFileToJson);
    }

    JSONObject mapFileToJson(MapFile mapFile) {
        return new JSONObject(Map.of(
                "id", idManager.getId(mapFile),
                "name", mapFile.getTitle(),
                "author", mapFile.getAuthor(),
                "width", mapFile.getWidth(),
                "height", mapFile.getHeight(),
                "maxPlayers", mapFile.getMaxNumberOfPlayers(),
                "startingPoints", pointsToJson(mapFile.getGamePointStartingPoints())
        ));
    }

    GameMap gamePlaceholderToGame(GameResource gamePlaceholder) throws Exception {
        MapLoader mapLoader = new MapLoader();
        GameMap map = mapLoader.convertMapFileToGameMap(gamePlaceholder.getMapFile());

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
        return toJsonArray(players, player -> new JSONObject(Map.of(
                "id", idManager.getId(player),
                "name", player.getName(),
                "color", player.getColor().name().toUpperCase(),
                "nation", player.getNation().name().toUpperCase()
        )));
    }

    public JSONObject buildingLostMessageToJson(BuildingLostMessage buildingLostMessage) {
        Building building = buildingLostMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(buildingLostMessage),
                "type", "BUILDING_LOST",
                "houseId", idManager.getId(building),
                "houseType", building.getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    public JSONObject buildingCapturedMessageToJson(BuildingCapturedMessage buildingCapturedMessage) {
        Building building = buildingCapturedMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(buildingCapturedMessage),
                "type", "BUILDING_CAPTURED",
                "houseId", idManager.getId(building),
                "houseType", building.getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    public JSONObject storeHouseIsReadyMessageToJson(StoreHouseIsReadyMessage storeHouseIsReadyMessage) {
        Building building = storeHouseIsReadyMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(storeHouseIsReadyMessage),
                "type", "STORE_HOUSE_IS_READY",
                "houseId", idManager.getId(building),
                "houseType", building.getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    JSONObject militaryBuildingReadyMessageToJson(MilitaryBuildingReadyMessage militaryBuildingReadyMessage) {
        Building building = militaryBuildingReadyMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(militaryBuildingReadyMessage),
                "type", MILITARY_BUILDING_READY.toString(),
                "houseId", idManager.getId(building),
                "houseType", building.getClass().getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    JSONObject noMoreResourcesMessageToJson(NoMoreResourcesMessage noMoreResourcesMessage) {
        Building building = noMoreResourcesMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(noMoreResourcesMessage),
                "type", NO_MORE_RESOURCES.toString(),
                "houseId", idManager.getId(building),
                "houseType", building.getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    JSONObject militaryBuildingOccupiedMessageToJson(MilitaryBuildingOccupiedMessage militaryBuildingOccupiedMessage) {
        Building building = militaryBuildingOccupiedMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(militaryBuildingOccupiedMessage),
                "type", MILITARY_BUILDING_OCCUPIED.toString(),
                "houseId", idManager.getId(building),
                "houseType", building.getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    JSONObject underAttackMessageToJson(UnderAttackMessage underAttackMessage) {
        Building building = underAttackMessage.building();

        return new JSONObject(Map.of(
                "id", idManager.getId(underAttackMessage),
                "type", UNDER_ATTACK.toString(),
                "houseId", idManager.getId(building),
                "houseType", building.getSimpleName(),
                "point", buildingToPoint(building)
        ));
    }

    JSONObject geologistFindMessageToJson(GeologistFindMessage geologistFindMessage) {
        JSONObject jsonGeologistFindPoint = new JSONObject(Map.of(
                "x", geologistFindMessage.point().x,
                "y", geologistFindMessage.point().y
        ));

        return new JSONObject(Map.of(
                "id", idManager.getId(geologistFindMessage),
                "type", GEOLOGIST_FIND.toString(),
                "point", jsonGeologistFindPoint,
                "material", geologistFindMessage.material().toString()
        ));
    }

    public JSONObject gameMonitoringEventsToJson(GameChangesList gameChangesList, Player player) throws InvalidUserActionException {
        JSONObject jsonMonitoringEvents = new JSONObject();

        jsonMonitoringEvents.put("time", gameChangesList.time());

        Set<Building> allChangedBuildings = new HashSet<>(gameChangesList.changedBuildings());

        if (gameChangesList.transportPriorityChanged()) {
            jsonMonitoringEvents.put("transportPriority", transportPriorityToJson(player.getTransportPriorities()));
        }

        if (!gameChangesList.newFallingTrees().isEmpty()) {
            jsonMonitoringEvents.put("newFallingTrees", treesToJson(gameChangesList.newFallingTrees()));
        }

        if (!gameChangesList.promotedRoads().isEmpty()) {
            jsonMonitoringEvents.put("changedRoads", roadsToJson(gameChangesList.promotedRoads()));
        }

        if (!gameChangesList.newStones().isEmpty()) {
            jsonMonitoringEvents.put("newStones", newStonesToJson(gameChangesList.newStones()));
        }

        if (!gameChangesList.changedStones().isEmpty()) {
            jsonMonitoringEvents.put("changedStones", newStonesToJson(gameChangesList.changedStones()));
        }

        gameChangesList.upgradedBuildings().forEach(newAndOldBuilding -> {
            // Move the id to the new building
            System.out.println("Old building: " + newAndOldBuilding.oldBuilding);
            System.out.println("Id: " + idManager.getId(newAndOldBuilding.oldBuilding));
            System.out.println("New building: " + newAndOldBuilding.newBuilding);

            idManager.updateObject(newAndOldBuilding.oldBuilding, newAndOldBuilding.newBuilding);

            // Tell the frontend that the house has changed
            allChangedBuildings.add(newAndOldBuilding.newBuilding);
        });

        if (!gameChangesList.workersWithNewTargets().isEmpty()) {
            jsonMonitoringEvents.put("workersWithNewTargets", workersWithNewTargetsToJson(gameChangesList.workersWithNewTargets()));

            jsonMonitoringEvents.put("wildAnimalsWithNewTargets", wildAnimalsWithNewTargetsToJson(gameChangesList.workersWithNewTargets()));

            jsonMonitoringEvents.put("shipsWithNewTargets", shipWithNewTargetsToJson(gameChangesList.workersWithNewTargets()));
        }

        if (!gameChangesList.workersWithStartedActions().isEmpty()) {
            jsonMonitoringEvents.put("workersWithStartedActions", workersAndActionsToJson(gameChangesList.workersWithStartedActions()));
        }

        if (!gameChangesList.newShips().isEmpty()) {
            jsonMonitoringEvents.put("newShips", shipsToJson(gameChangesList.newShips()));
        }

        if (!gameChangesList.finishedShips().isEmpty()) {
            jsonMonitoringEvents.put("finishedShips", shipsToJson(gameChangesList.finishedShips()));
        }

        if (!gameChangesList.newBuildings().isEmpty()) {
            jsonMonitoringEvents.put("newBuildings", newBuildingsToJson(gameChangesList.newBuildings(), player));
        }

        if (!gameChangesList.newFlags().isEmpty()) {
            jsonMonitoringEvents.put("newFlags", flagsToJson(gameChangesList.newFlags()));
        }

        if (!gameChangesList.newRoads().isEmpty()) {
            jsonMonitoringEvents.put("newRoads", roadsToJson(gameChangesList.newRoads()));
        }

        if (!gameChangesList.newTrees().isEmpty()) {
            jsonMonitoringEvents.put("newTrees", newTreesToJson(gameChangesList.newTrees()));
        }

        if (!gameChangesList.discoveredDeadTrees().isEmpty()) {
            jsonMonitoringEvents.put("discoveredTrees", pointsToJson(gameChangesList.discoveredDeadTrees()));
        }

        if (!gameChangesList.newDiscoveredLand().isEmpty()) {
            jsonMonitoringEvents.put("newDiscoveredLand", newDiscoveredLandToJson(gameChangesList.newDiscoveredLand()));
        }

        if (!gameChangesList.newCrops().isEmpty()) {
            jsonMonitoringEvents.put("newCrops", newCropsToJson(gameChangesList.newCrops()));
        }

        if (!gameChangesList.newSigns().isEmpty()) {
            jsonMonitoringEvents.put("newSigns", newSignsToJson(gameChangesList.newSigns()));
        }

        if (!allChangedBuildings.isEmpty()) {
            jsonMonitoringEvents.put("changedBuildings", changedBuildingsToJson(allChangedBuildings, player));
        }

        if (!gameChangesList.changedFlags().isEmpty()) {
            jsonMonitoringEvents.put("changedFlags", flagsToJson(gameChangesList.changedFlags()));
        }

        if (!gameChangesList.newDecorations().isEmpty()) {
            jsonMonitoringEvents.put("newDecorations", pointsAndDecorationsToJson(gameChangesList.newDecorations()));
        }

        if (!gameChangesList.removedDecorations().isEmpty()) {
            jsonMonitoringEvents.put("removedDecorations", pointsToJson(gameChangesList.removedDecorations()));
        }

        if (!gameChangesList.removedWorkers().isEmpty()) {
            jsonMonitoringEvents.put("removedWorkers", removedWorkersToJson(gameChangesList.removedWorkers()));

            jsonMonitoringEvents.put("removedWildAnimals", removedWildAnimalsToJson(gameChangesList.removedWorkers()));
        }

        if (!gameChangesList.removedBuildings().isEmpty()) {
            jsonMonitoringEvents.put("removedBuildings", removedBuildingsToJson(gameChangesList.removedBuildings()));
        }

        if (!gameChangesList.removedFlags().isEmpty()){
            jsonMonitoringEvents.put("removedFlags", removedFlagsToJson(gameChangesList.removedFlags()));
        }

        if (!gameChangesList.removedRoads().isEmpty()) {
            jsonMonitoringEvents.put("removedRoads", removedRoadsToJson(gameChangesList.removedRoads()));
        }

        if (!gameChangesList.removedTrees().isEmpty()) {
            jsonMonitoringEvents.put("removedTrees", removedTreesToJson(gameChangesList.removedTrees()));
        }

        if (!gameChangesList.removedDeadTrees().isEmpty()) {
            jsonMonitoringEvents.put("removedDeadTrees", pointsToJson(gameChangesList.removedDeadTrees()));
        }

        if (!gameChangesList.changedBorders().isEmpty()) {
            jsonMonitoringEvents.put("changedBorders", borderChangesToJson(gameChangesList.changedBorders()));
        }

        if (!gameChangesList.changedAvailableConstruction().isEmpty()) {
            jsonMonitoringEvents.put(
                    "changedAvailableConstruction",
                    availableConstructionChangesToJson(gameChangesList.changedAvailableConstruction(), player)
            );
        }

        if (!gameChangesList.removedCrops().isEmpty()) {
            jsonMonitoringEvents.put("removedCrops", cropsToIdJson(gameChangesList.removedCrops()));
        }

        if (!gameChangesList.harvestedCrops().isEmpty()) {
            jsonMonitoringEvents.put("harvestedCrops", cropsToIdJson(gameChangesList.harvestedCrops()));
        }

        if (!gameChangesList.removedSigns().isEmpty()) {
            jsonMonitoringEvents.put("removedSigns", removedSignsToJson(gameChangesList.removedSigns()));
        }

        if (!gameChangesList.removedStones().isEmpty()) {
            jsonMonitoringEvents.put("removedStones", removedStonesToJson(gameChangesList.removedStones()));
        }

        if (!gameChangesList.newMessages().isEmpty()) {
            jsonMonitoringEvents.put("newMessages", messagesToJson(gameChangesList.newMessages()));
        }

        if (!gameChangesList.removedMessages().isEmpty()) {
            jsonMonitoringEvents.put("removedMessages", removedMessagesToJson(gameChangesList.removedMessages()));
        }

        return jsonMonitoringEvents;
    }

    private JSONArray removedMessagesToJson(Collection<Message> removedMessages) {
        return toJsonArray(removedMessages, idManager::getId);
    }

    private JSONArray pointsAndDecorationsToJson(Map<Point, DecorationType> pointsAndDecorations) {
        JSONArray jsonPointsAndDecorations = new JSONArray();

        for (Map.Entry<Point, DecorationType> entry : pointsAndDecorations.entrySet()) {
            Point point = entry.getKey();
            DecorationType decorationType = entry.getValue();

            jsonPointsAndDecorations.add(new JSONObject(Map.of(
                    "x", point.x,
                    "y", point.y,
                    "decoration", decorationType.name().toUpperCase()
            )));
        }

        return jsonPointsAndDecorations;
    }

    private JSONArray workersAndActionsToJson(Map<Worker, WorkerAction> workersWithStartedActions) {
        JSONArray workersAndActionsJson = new JSONArray();

        workersWithStartedActions.forEach((worker, action) -> {
            Point position = worker.getPosition();

            workersAndActionsJson.add(new JSONObject(Map.of(
                    "id", idManager.getId(worker),
                    "x", position.x,
                    "y", position.y,
                    "direction", worker.getDirection().name().toUpperCase(),
                    "startedAction", action.name().toUpperCase()
            )));
        });

        return workersAndActionsJson;
    }

    private JSONArray shipWithNewTargetsToJson(List<Worker> workers) {
        return toJsonArrayWithFilter(workers, worker -> shipToJson((Ship) worker), worker -> worker instanceof Ship);
    }

    private JSONObject shipToJson(Ship ship) {
        JSONObject jsonCargos = new JSONObject();
        Map<Material, Integer> cargos = new EnumMap<>(Material.class);

        for (Cargo cargo : ship.getCargos()) {
            Material material = cargo.getMaterial();
            int amount = cargos.getOrDefault(cargo.getMaterial(), 0);

            cargos.put(material, amount + 1);
        }

        cargos.forEach((material, amount) -> jsonCargos.put(material.name().toUpperCase(), amount));

        return new JSONObject(Map.of(
                "state", ship.isUnderConstruction() ? "UNDER_CONSTRUCTION" : "READY",
                "x", ship.getPosition().x,
                "y", ship.getPosition().y,
                "direction", ship.getDirection().name().toUpperCase(),
                "cargo", jsonCargos
        ));
    }

    private JSONArray shipsToJson(List<Ship> ships) {
        return toJsonArray(ships, this::shipToJson);
    }

    private JSONArray removedWildAnimalsToJson(List<Worker> removedWorkers) {
        return toJsonArrayWithFilter(
                removedWorkers,
                idManager::getId,
                worker -> worker instanceof WildAnimal
        );
    }

    private JSONArray wildAnimalsWithNewTargetsToJson(List<Worker> workersWithNewTargets) {
        return toJsonArrayWithFilter(
                workersWithNewTargets,
                worker -> wildAnimalToJson((WildAnimal) worker),
                worker -> worker instanceof WildAnimal
        );
    }

    private JSONArray newStonesToJson(Collection<Stone> newStones) {
        return toJsonArray(newStones, this::stoneToJson);
    }

    private JSONArray messagesToJson(List<Message> newGameMessages) {
        JSONArray jsonMessages = new JSONArray();

        newGameMessages.forEach(message -> {
            var jsonMessage = switch (message.getMessageType()) {
                case MILITARY_BUILDING_OCCUPIED -> militaryBuildingOccupiedMessageToJson((MilitaryBuildingOccupiedMessage) message);
                case GEOLOGIST_FIND -> geologistFindMessageToJson((GeologistFindMessage) message);
                case MILITARY_BUILDING_READY -> militaryBuildingReadyMessageToJson((MilitaryBuildingReadyMessage) message);
                case NO_MORE_RESOURCES -> noMoreResourcesMessageToJson((NoMoreResourcesMessage) message);
                case UNDER_ATTACK -> underAttackMessageToJson((UnderAttackMessage) message);
                case BUILDING_CAPTURED -> buildingCapturedMessageToJson((BuildingCapturedMessage) message);
                case BUILDING_LOST -> buildingLostMessageToJson((BuildingLostMessage) message);
                case STORE_HOUSE_IS_READY -> storeHouseIsReadyMessageToJson((StoreHouseIsReadyMessage) message);
                case TREE_CONSERVATION_PROGRAM_ACTIVATED -> treeConservationProgramActivatedMessageToJson((TreeConservationProgramActivatedMessage) message);
                case TREE_CONSERVATION_PROGRAM_DEACTIVATED -> treeConservationProgramDeactivatedMessageToJson((TreeConservationProgramDeactivatedMessage) message);
                case MILITARY_BUILDING_CAUSED_LOST_LAND -> militaryBuildingCausedLostLandMessageToJson((MilitaryBuildingCausedLostLandMessage) message);
                case BOMBARDED_BY_CATAPULT -> bombardedByCatapultMessageToJson((BombardedByCatapultMessage) message);
                case HARBOR_IS_FINISHED -> harborFinishedMessageToJson((HarborIsFinishedMessage) message);
                case SHIP_READY_FOR_EXPEDITION -> shipReadyForExpeditionMessageToJson((ShipReadyForExpeditionMessage) message);
                case SHIP_HAS_REACHED_DESTINATION -> shipHasReachedDestinationMessageToJson((ShipHasReachedDestinationMessage) message);
                case GAME_ENDED -> gameEndedMessageToJson((GameEndedMessage) message);
            };

            jsonMessages.add(jsonMessage);
        });

        return jsonMessages;
    }

    private JSONObject gameEndedMessageToJson(GameEndedMessage message) {
        return new JSONObject(Map.of(
                "type", GAME_ENDED.name().toUpperCase(),
                "winnerPlayerId", idManager.getId(message.winner())
        ));
    }

    private JSONObject shipHasReachedDestinationMessageToJson(ShipHasReachedDestinationMessage message) {
        var ship = message.ship();

        return new JSONObject(Map.of(
                "type", SHIP_HAS_REACHED_DESTINATION.name().toUpperCase(),
                "shipId", idManager.getId(ship),
                "point", pointToJson(ship.getPosition())
        ));
    }

    private JSONObject shipReadyForExpeditionMessageToJson(ShipReadyForExpeditionMessage message) {
        var ship = message.ship();

        return new JSONObject(Map.of(
                "type", SHIP_READY_FOR_EXPEDITION.name().toUpperCase(),
                "shipId", idManager.getId(ship),
                "point", pointToJson(ship.getPosition())
        ));
    }

    private JSONObject bombardedByCatapultMessageToJson(BombardedByCatapultMessage message) {
        Building building = message.building();

        return new JSONObject(Map.of(
                "type", BOMBARDED_BY_CATAPULT.name().toUpperCase(),
                "houseType", building.getSimpleName().toUpperCase(),
                "houseId", idManager.getId(building),
                "point", buildingToPoint(building)
        ));
    }

    private JSONObject harborFinishedMessageToJson(HarborIsFinishedMessage message) {
        Building harbor = message.harbor();

        return new JSONObject(Map.of(
                "type", HARBOR_IS_FINISHED.name().toUpperCase(),
                "houseId", idManager.getId(harbor),
                "point", buildingToPoint(harbor)
        ));
    }

    private JSONObject militaryBuildingCausedLostLandMessageToJson(MilitaryBuildingCausedLostLandMessage message) {
        Building building = message.building();

        return new JSONObject(Map.of(
                "type", MILITARY_BUILDING_CAUSED_LOST_LAND.toString(),
                "houseId", idManager.getId(building),
                "point", buildingToPoint(building)
        ));
    }

    private JSONObject buildingToPoint(Building building) {
        return new JSONObject(Map.of(
                "x", building.getPosition().x,
                "y", building.getPosition().y
        ));
    }

    private JSONObject treeConservationProgramDeactivatedMessageToJson(TreeConservationProgramDeactivatedMessage message) {
        return new JSONObject(Map.of(
                "type", TREE_CONSERVATION_PROGRAM_DEACTIVATED.toString()
        ));
    }

    private JSONObject treeConservationProgramActivatedMessageToJson(TreeConservationProgramActivatedMessage message) {
        return new JSONObject(Map.of(
                "type", TREE_CONSERVATION_PROGRAM_ACTIVATED.toString()
        ));
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
        return toJsonArray(
                changedBorders,
                borderChange -> new JSONObject(Map.of(
                        "playerId", idManager.getId(borderChange.getPlayer()),
                        "newBorder", pointsToJson(borderChange.getNewBorder()),
                        "removedBorder", pointsToJson(borderChange.getRemovedBorder())
                ))
        );
    }

    private JSONArray removedStonesToJson(List<Stone> removedStones) {
        return toJsonArray(removedStones, idManager::getId);
    }

    private JSONArray removedSignsToJson(List<Sign> removedSigns) {
        return objectsToJsonIdArray(removedSigns);
    }

    private JSONArray newSignsToJson(List<Sign> signs) {
        return toJsonArray(signs, this::signToJson);
    }

    private JSONArray cropsToIdJson(List<Crop> crops) {
        return toJsonArray(crops, idManager::getId);
    }

    private JSONArray newCropsToJson(List<Crop> newCrops) {
        return cropsToJson(newCrops);
    }

    private JSONArray cropsToJson(List<Crop> crops) {
        return toJsonArray(crops, this::cropToJson);
    }

    private JSONArray newDiscoveredLandToJson(Collection<Point> newDiscoveredLand) {
        return pointsToJson(newDiscoveredLand);
    }

    private JSONArray removedTreesToJson(List<Tree> removedTrees) {
        return toJsonArray(removedTrees, idManager::getId);
    }

    private JSONArray newTreesToJson(List<Tree> newTrees) {
        return treesToJson(newTrees);
    }

    private JSONArray treesToJson(Collection<Tree> trees) {
        return toJsonArray(trees, this::treeToJson);
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
        return toJsonArrayWithFilter(removedWorkers, idManager::getId, worker -> !(worker instanceof WildAnimal));
    }

    private JSONArray objectsToJsonIdArray(List<?> gameObjects) {
        return toJsonArray(gameObjects, idManager::getId);
    }

    private JSONArray roadsToJson(List<Road> roads) {
        return toJsonArray(roads, this::roadToJson);
    }

    private JSONArray flagsToJson(Collection<Flag> flags) {
        return toJsonArray(flags, this::flagToJson);
    }

    private <T> JSONArray toJsonArrayWithFilter(Collection<T> fromList, Function<T, Object> mapFunction, Function<T, Boolean> filterFunction) {
        JSONArray jsonResult = new JSONArray();

        if (fromList == null) {
            return jsonResult;
        }

        for (T t : fromList) {
            if (filterFunction.apply(t)) {
                jsonResult.add(mapFunction.apply(t));
            }
        }

        return jsonResult;
    }

    private <T> JSONArray toJsonArray(Collection<T> fromList, Function<T, Object> mapFunction) {
        JSONArray jsonResult = new JSONArray();

        if (fromList == null) {
            return jsonResult;
        }

        for (T t : fromList) {
            jsonResult.add(mapFunction.apply(t));
        }

        return jsonResult;
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
        return worker.isSoldier()
                ? rankToTypeString((Soldier) worker)
                : worker.getClass().getSimpleName();
    }

    public JSONArray transportPriorityToJson(List<TransportCategory> transportPriorityList) {
        return toJsonArray(transportPriorityList, prio -> prio.name().toUpperCase());
    }

    public Set<Point> jsonToPointsSet(JSONArray jsonPoints) {
        Set<Point> pointsSet = new HashSet<>();

        for (Object jsonPoint : jsonPoints) {
            Point point = jsonToPoint((JSONObject) jsonPoint);

            pointsSet.add(point);
        }

        return pointsSet;
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

    public JSONObject playerViewToJson(GameMap map, Player player, GameResource gameResource) throws InvalidUserActionException {
        JSONArray  jsonHouses                = new JSONArray();
        JSONArray  trees;
        JSONArray  jsonStones;
        JSONArray  jsonWorkers;
        JSONArray  jsonWildAnimals;
        JSONArray  jsonFlags;
        JSONArray  jsonRoads                 = new JSONArray();
        JSONArray  jsonDiscoveredPoints;
        JSONArray  jsonBorders               = new JSONArray();
        JSONArray  jsonSigns;
        JSONArray  jsonCrops                 = new JSONArray();
        JSONObject jsonAvailableConstruction = new JSONObject();
        JSONArray  jsonDeadTrees;
        JSONArray  jsonDecorations           = new JSONArray();

        JSONArray jsonMessages = messagesToJson(player.getMessages());

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
            trees = toJsonArrayWithFilter(map.getTrees(),
                    this::treeToJson,
                    tree -> discoveredLand.contains(tree.getPosition()));

            /* Fill in stones */
            jsonStones = toJsonArrayWithFilter(
                    map.getStones(),
                    this::stoneToJson,
                    stone -> discoveredLand.contains(stone.getPosition()));


            /* Fill in workers */
            jsonWorkers = toJsonArrayWithFilter(
                    map.getWorkers(),
                    this::workerToJson,
                    worker -> discoveredLand.contains(worker.getPosition()) && !worker.isInsideBuilding());

            /* Fill in flags */
            jsonFlags = toJsonArrayWithFilter(
                    map.getFlags(),
                    this::flagToJson,
                    flag -> discoveredLand.contains(flag.getPosition()));

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
            jsonDiscoveredPoints = pointsToJson(discoveredLand);

            jsonBorders.add(borderToJson(player, idManager.getId(player)));

            /* Fill in the signs */
            jsonSigns = toJsonArrayWithFilter(
                    map.getSigns(),
                    this::signToJson,
                    sign -> discoveredLand.contains(sign.getPosition()));

            /* Fill in wild animals */
            jsonWildAnimals = toJsonArrayWithFilter(
                    map.getWildAnimals(),
                    this::wildAnimalToJson,
                    animal -> discoveredLand.contains(animal.getPosition()));

            /* Fill in crops */
            for (Crop crop : map.getCrops()) {
                if (!discoveredLand.contains(crop.getPosition())) {
                    continue;
                }

                jsonCrops.add(cropToJson(crop));
            }

            /* Fill in dead trees */
            jsonDeadTrees = toJsonArrayWithFilter(
                    map.getDeadTrees(),
                    this::pointToJson,
                    discoveredLand::contains);

            /* Fill in available construction */
            for (Point point : map.getAvailableFlagPoints(player)) {
                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                String key = point.x + "," + point.y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add("flag");
            }

            for (Map.Entry<Point, Size> site : map.getAvailableHousePoints(player).entrySet()) {
                if (!player.getDiscoveredLand().contains(site.getKey())) {
                    continue;
                }

                String key = site.getKey().x + "," + site.getKey().y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add(site.getValue().toString().toLowerCase());
            }

            for (Point point : map.getAvailableMinePoints(player)) {
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

                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                jsonDecorations.add(decorationToJson(decorationType, point));
            }
        }

        // Add terrain information
        JSONArray jsonTrianglesBelow = new JSONArray();
        JSONArray jsonTrianglesBelowRight = new JSONArray();
        JSONArray jsonHeights = new JSONArray();

        int start = 1;

        for (int y = 1; y < map.getHeight(); y++) {
            for (int x = start; x + 1 < map.getWidth(); x += 2) {
                Point point = new Point(x, y);

                Vegetation below = map.getVegetationBelow(point);
                Vegetation downRight = map.getVegetationDownRight(point);

                jsonTrianglesBelow.add(below.toInt());
                jsonTrianglesBelowRight.add(downRight.toInt());
                jsonHeights.add(map.getHeightAtPoint(point));
            }

            if (start == 1) {
                start = 2;
            } else {
                start = 1;
            }
        }

        JSONObject jsonView = new JSONObject();
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
        jsonView.put("players", playersToJson(map.getPlayers(), gameResource));
        jsonView.put("gameState", gameResource.status.name().toUpperCase());
        jsonView.put("messages", jsonMessages);
        jsonView.put("straightBelow", jsonTrianglesBelow);
        jsonView.put("belowToTheRight", jsonTrianglesBelowRight);
        jsonView.put("heights", jsonHeights);
        jsonView.put("width", map.getWidth());
        jsonView.put("height", map.getHeight());
        jsonView.put("transportPriority", transportPriorityToJson(player.getTransportPriorities()));

        return jsonView;
    }

    public JSONObject cargoToJson(Cargo cargo) {
        return new JSONObject(Map.of(
                "material", cargo.getMaterial().name().toUpperCase(),
                "target", pointToJson(cargo.getTarget().getPosition()),
                "targetType", cargo.getTarget().getClass().getSimpleName()
        ));
    }

    void startGame(GameResource gameResource, GameTicker gameTicker) throws Exception {

        /* Create the game map */
        gameResource.createGameMap();
        GameMap map = gameResource.getGameMap();

        /* Limit the amount of wild animals to make performance bearable -- temporary! */
        List<WildAnimal> wildAnimals = map.getWildAnimals();
        List<WildAnimal> reducedWildAnimals = new ArrayList<>(wildAnimals);

        if (reducedWildAnimals.size() > 10) {
            reducedWildAnimals = reducedWildAnimals.subList(0, 10);
        }

        wildAnimals.clear();

        wildAnimals.addAll(reducedWildAnimals);

        /* Place a headquarters for each player */
        List<Player> players = map.getPlayers();
        List<Point> startingPoints = map.getStartingPoints();

        for (int i = 0; i < startingPoints.size(); i++) {
            if (i == players.size()) {
                break;
            }

            map.placeBuilding(new Headquarter(players.get(i)), startingPoints.get(i));
        }

        /* Adjust the initial set of resources */
        adjustResources(map, gameResource.getResources());

        /* Start the time for the game by adding it to the game ticker */
        gameTicker.startGame(gameResource);

        gameResource.setStatus(GameStatus.STARTED);
    }

    public JSONObject chatMessageToPlayerToJson(ChatManager.ChatMessage chatMessage, Player player) {
        return new JSONObject(Map.of(
                "from", idManager.getId(chatMessage.from()),
                "text", chatMessage.text(),
                "toPlayerId", idManager.getId(player),
                "time", timeToJson(chatMessage.time())
        ));
    }

    private JSONObject timeToJson(ChatManager.SimpleTime time) {
        return new JSONObject(Map.of(
                "hours", time.hours(),
                "minutes", time.minutes(),
                "seconds", time.seconds()
        ));
    }

    public JSONObject chatMessageToRoomToJson(ChatManager.ChatMessage chatMessage, String roomId) {
        return new JSONObject(Map.of(
                "fromPlayerId", idManager.getId(chatMessage.from()),
                "fromName", chatMessage.from().getName(),
                "text", chatMessage.text(),
                "toRoomId", roomId,
                "time", timeToJson(chatMessage.time())
        ));
    }

    public JSONObject flagToDebugJson(Flag flag) {
        var jsonFlag = flagToJson(flag);

        jsonFlag.put("cargos", toJsonArray(flag.getStackedCargo(), this::cargoToJson));

        return jsonFlag;
    }

    public TransportCategory jsonToTransportCategory(String material) {
        return TransportCategory.valueOf(material);
    }
}
