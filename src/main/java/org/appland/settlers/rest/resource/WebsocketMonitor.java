package org.appland.settlers.rest.resource;

import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Brewery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.CoalMine;
import org.appland.settlers.model.DonkeyFarm;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GoldMine;
import org.appland.settlers.model.GraniteMine;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.IronMine;
import org.appland.settlers.model.IronSmelter;
import org.appland.settlers.model.Message;
import org.appland.settlers.model.Metalworks;
import org.appland.settlers.model.Soldier;
import org.appland.settlers.model.Mill;
import org.appland.settlers.model.Mint;
import org.appland.settlers.model.PigFarm;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ServerEndpoint(value = "/ws/monitor/games/{gameId}/players/{playerId}")

public class WebsocketMonitor implements PlayerGameViewMonitor {

    private final Map<Player, Session> playerToSession;
    private final Utils utils;
    private final JSONParser parser;

    @Context
    ServletContext context;

    private final IdManager idManager = IdManager.idManager;

    public WebsocketMonitor() {

        System.out.println("CREATED NEW WEBSOCKET MONITOR");
        utils = new Utils(idManager);
        parser = new JSONParser();
        playerToSession = new HashMap<>();
    }

    @OnMessage
    public void onMessage(Session session, String message) throws InvalidUserActionException {
        System.out.println("\nON MESSAGE: " + message);

        Player player = (Player) session.getUserProperties().get("PLAYER");
        GameMap map = player.getMap();

        JSONObject jsonBody;
        try {
            jsonBody = (JSONObject) parser.parse(message);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Command command = Command.valueOf((String) jsonBody.get("command"));

        switch (command) {
            case GET_SOLDIERS_AVAILABLE_FOR_ATTACK: {
                int amount;

                synchronized (map) {
                    amount = player.getAmountOfSoldiersAvailableForAttack();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case GET_POPULATE_MILITARY_FAR_FROM_BORDER: {
                int amount;

                synchronized (map) {
                    amount = player.getAmountOfSoldiersWhenPopulatingFarFromBorder();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case GET_POPULATE_MILITARY_CLOSER_TO_BORDER: {
                int amount;

                synchronized (map) {
                    amount = player.getAmountOfSoldiersWhenPopulatingAwayFromBorder();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case GET_POPULATE_MILITARY_CLOSE_TO_BORDER: {
                int amount;

                synchronized (map) {
                    amount = player.getAmountOfSoldiersWhenPopulatingCloseToBorder();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case SET_SOLDIERS_AVAILABLE_FOR_ATTACK: {
                var amount = ((Long) jsonBody.get("amount")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersAvailableForAttack(amount);
                }
            }
            break;

            case SET_MILITARY_POPULATION_CLOSE_TO_BORDER: {
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingCloseToBorder(amount);
                }
            }
            break;

            case SET_MILITARY_POPULATION_CLOSER_TO_BORDER: {
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingAwayFromBorder(amount);
                }
            }
            break;

            case SET_MILITARY_POPULATION_FAR_FROM_BORDER: {
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingFarFromBorder(amount);
                }
            }
            break;

            case SET_GAME_SPEED: {
                GameSpeed gameSpeed = GameSpeed.valueOf((String) jsonBody.get("speed"));

                GameResource game = (GameResource) session.getUserProperties().get("GAME");

                game.setGameSpeed(gameSpeed);

                JSONObject jsonNewTick = new JSONObject();

                int tick = switch (gameSpeed) {
                    case FAST -> 100;
                    case NORMAL -> 200;
                    case SLOW -> 400;
                };

                jsonNewTick.put("tick", tick);

                game.getPlayers().forEach(receiver -> {
                    if (playerToSession.containsKey(receiver)) {
                        sendToPlayer(jsonNewTick, receiver);
                    }
                });
            }
            break;

            case GET_DEFENSE_FROM_SURROUNDING_BUILDINGS: {
                int amount;

                synchronized (map) {
                    amount = player.getDefenseFromSurroundingBuildings();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case SET_DEFENSE_FROM_SURROUNDING_BUILDINGS: {
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setDefenseFromSurroundingBuildings(strength);
                }
            }
            break;

            case GET_DEFENSE_STRENGTH: {
                int amount;

                synchronized (map) {
                    amount = player.getDefenseStrength();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case SET_DEFENSE_STRENGTH: {
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setDefenseStrength(strength);
                }
            }
            break;

            case GET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING: {
                int amount;

                synchronized (map) {
                    amount = player.getStrengthOfSoldiersPopulatingBuildings();
                }

                sendAmountReplyToPlayer(amount, player, jsonBody);
            }
            break;

            case SET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING: {
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setStrengthOfSoldiersPopulatingBuildings(strength);
                }
            }
            break;

            case PAUSE_GAME: {
                GameResource game = (GameResource) session.getUserProperties().get("GAME");

                game.status = GameStatus.PAUSED;

                // Tell all subscribed players that the game is paused
                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put("gameState", "PAUSED");

                game.getGameMap().getPlayers().forEach(player1 -> {
                    var playerSession = playerToSession.get(player1);

                    if (playerSession != null) {
                        playerSession.getAsyncRemote().sendText(jsonResponse.toJSONString());
                    }
                });
            }
            break;

            case RESUME_GAME: {
                GameResource game = (GameResource) session.getUserProperties().get("GAME");

                game.status = GameStatus.STARTED;

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put("gameState", "STARTED");

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case SET_IRON_BAR_QUOTAS: {
                Long armoryAmount = (Long) jsonBody.get("armory");
                Long metalworksAmount = (Long) jsonBody.get("metalworks");

                player.setIronBarQuota(Armory.class, armoryAmount.intValue());
                player.setIronBarQuota(Metalworks.class, metalworksAmount.intValue());
            }
            break;

            case GET_IRON_BAR_QUOTAS: {
                JSONObject jsonResponse = Utils.messageJsonToReplyJson(jsonBody);

                jsonResponse.put("armory", player.getIronBarQuota(Armory.class));
                jsonResponse.put("metalworks", player.getIronBarQuota(Metalworks.class));

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case GET_WATER_QUOTAS: {
                JSONObject jsonResponse = Utils.messageJsonToReplyJson(jsonBody);
                jsonResponse.put("donkeyFarm", player.getWaterQuota(DonkeyFarm.class));
                jsonResponse.put("pigFarm", player.getWaterQuota(PigFarm.class));
                jsonResponse.put("bakery", player.getWaterQuota(Bakery.class));
                jsonResponse.put("brewery", player.getWaterQuota(Brewery.class));

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case GET_WHEAT_QUOTAS: {
                JSONObject jsonResponse = Utils.messageJsonToReplyJson(jsonBody);

                jsonResponse.put("donkeyFarm", player.getWheatQuota(DonkeyFarm.class));
                jsonResponse.put("pigFarm", player.getWheatQuota(PigFarm.class));
                jsonResponse.put("mill", player.getWheatQuota(Mill.class));
                jsonResponse.put("brewery", player.getWheatQuota(Brewery.class));

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case SET_WATER_QUOTAS: {
                Long donkeyFarmAmount = (Long) jsonBody.get("donkeyFarm");
                Long pigFarmAmount = (Long) jsonBody.get("pigFarm");
                Long bakeryAmount = (Long) jsonBody.get("bakery");
                Long breweryAmount = (Long) jsonBody.get("brewery");

                player.setWaterQuota(DonkeyFarm.class, donkeyFarmAmount.intValue());
                player.setWaterQuota(PigFarm.class, pigFarmAmount.intValue());
                player.setWaterQuota(Bakery.class, bakeryAmount.intValue());
                player.setWaterQuota(Brewery.class, breweryAmount.intValue());
            }
            break;

            case SET_WHEAT_QUOTAS: {
                Long donkeyFarmAmount = (Long) jsonBody.get("donkeyFarm");
                Long pigFarmAmount = (Long) jsonBody.get("pigFarm");
                Long millAmount = (Long) jsonBody.get("mill");
                Long breweryAmount = (Long) jsonBody.get("brewery");

                player.setWheatQuota(DonkeyFarm.class, donkeyFarmAmount.intValue());
                player.setWheatQuota(PigFarm.class, pigFarmAmount.intValue());
                player.setWheatQuota(Mill.class, millAmount.intValue());
                player.setWheatQuota(Brewery.class, breweryAmount.intValue());
            }
            break;

            case GET_FOOD_QUOTAS: {
                JSONObject jsonResponse = Utils.messageJsonToReplyJson(jsonBody);

                jsonResponse.put("ironMine", player.getFoodQuota(IronMine.class));
                jsonResponse.put("coalMine", player.getFoodQuota(CoalMine.class));
                jsonResponse.put("goldMine", player.getFoodQuota(GoldMine.class));
                jsonResponse.put("graniteMine", player.getFoodQuota(GraniteMine.class));

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case GET_COAL_QUOTAS: {
                JSONObject jsonResponse = Utils.messageJsonToReplyJson(jsonBody);

                jsonResponse.put("mint", player.getCoalQuota(Mint.class));
                jsonResponse.put("armory", player.getCoalQuota(Armory.class));
                jsonResponse.put("ironSmelter", player.getCoalQuota(IronSmelter.class));

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case SET_FOOD_QUOTAS: {
                Long ironMineAmount = (Long) jsonBody.get("ironMine");
                Long coalMineAmount = (Long) jsonBody.get("coalMine");
                Long goldMineAmount = (Long) jsonBody.get("goldMine");
                Long graniteMineAmount = (Long) jsonBody.get("graniteMine");

                player.setCoalQuota(IronMine.class, ironMineAmount.intValue());
                player.setCoalQuota(CoalMine.class, coalMineAmount.intValue());
                player.setCoalQuota(GoldMine.class, goldMineAmount.intValue());
                player.setCoalQuota(GraniteMine.class, graniteMineAmount.intValue());
            }
                break;

            case SET_COAL_QUOTAS: {
                Long mintAmount = (Long) jsonBody.get("mint");
                Long armoryAmount = (Long) jsonBody.get("armory");
                Long ironSmelterAmount = (Long) jsonBody.get("ironSmelter");

                player.setCoalQuota(Mint.class, mintAmount.intValue());
                player.setCoalQuota(Armory.class, armoryAmount.intValue());
                player.setCoalQuota(IronSmelter.class, ironSmelterAmount.intValue());
            }
                break;

            case REMOVE_MESSAGE: {
                String messageId = (String) jsonBody.get("messageId");

                Message gameMessage = (Message) idManager.getObject(messageId);

                player.removeMessage(gameMessage);
            }
                break;

            case START_DETAILED_MONITORING: {
                String id = (String) jsonBody.get("id");

                Object object = idManager.getObject(id);

                JSONObject jsonUpdate = new JSONObject();

                if (object instanceof Building building) {
                    synchronized (map) {
                        player.addDetailedMonitoring(building);

                        JSONArray jsonUpdatedBuildings = new JSONArray();

                        jsonUpdate.put("changedBuildings", jsonUpdatedBuildings);

                        jsonUpdatedBuildings.add(utils.houseToJson(building, player));
                    }
                } else if (object instanceof Flag flag) {
                    synchronized (map) {
                        player.addDetailedMonitoring(flag);

                        JSONArray jsonUpdatedFlags = new JSONArray();

                        jsonUpdate.put("changedFlags", jsonUpdatedFlags);

                        jsonUpdatedFlags.add(utils.flagToJson(flag));
                    }
                }

                session.getAsyncRemote().sendText(jsonUpdate.toJSONString());
            }

                break;

            case STOP_DETAILED_MONITORING: {
                String buildingId = (String) jsonBody.get("buildingId");

                Building building = (Building) idManager.getObject(buildingId);

                synchronized (map) {
                    player.removeDetailedMonitoring(building);
                }
            }

                break;

            case SET_RESERVED_IN_HEADQUARTERS:
                synchronized (map) {
                    Optional<Building> optionalHeadquarter = player.getHeadquarter();

                    if (optionalHeadquarter.isPresent()) {
                        Headquarter headquarter = (Headquarter) optionalHeadquarter.get();

                        Arrays.stream(Soldier.Rank.values()).iterator().forEachRemaining(
                                rank -> {
                                    if (jsonBody.containsKey(rank.name().toUpperCase())) {
                                        Long amountLong = (Long) jsonBody.get(rank.name().toUpperCase());
                                        int amount = amountLong.intValue();
                                        headquarter.setReservedSoldiers(rank, amount);
                                    }
                                }
                        );
                    } else {
                        System.out.println("Can't find headquarters for the player!");
                    }
                }

                break;

            case INFORMATION_ON_POINTS: {
                JSONObject jsonResponse = Utils.messageJsonToReplyJson(jsonBody);
                JSONArray jsonPointsInformation = new JSONArray();

                List<Point> points = utils.jsonToPoints((JSONArray) jsonBody.get("points"));

                jsonResponse.put("pointsWithInformation", jsonPointsInformation);

                synchronized (map) {
                    for (Point point : points) {
                        jsonPointsInformation.add(utils.pointToDetailedJson(point, player, map));
                    }
                }

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
                break;

            case FULL_SYNC: {
                synchronized (map) {
                    String playerId = (String) jsonBody.get("playerId");

                    GameResource gameResource = (GameResource) session.getUserProperties().get("GAME");

                    JSONObject jsonFullSync = new JSONObject();

                    jsonFullSync.put("playerView", utils.playerViewToJson(playerId, map, player, gameResource));

                    System.out.println("Replying with full sync message");

                    session.getAsyncRemote().sendText(jsonFullSync.toJSONString());
                }
            }
            break;

            case CALL_SCOUT: {
                JSONObject jsonPoint = (JSONObject) jsonBody.get("point");
                Point point = utils.jsonToPoint(jsonPoint);

                synchronized (map) {
                    Flag flag = map.getFlagAtPoint(point);

                    flag.callScout();
                }
            }
            break;

            case CALL_GEOLOGIST: {
                JSONObject jsonPoint = (JSONObject) jsonBody.get("point");
                Point point = utils.jsonToPoint(jsonPoint);

                synchronized (map) {
                    Flag flag = map.getFlagAtPoint(point);

                    flag.callGeologist();
                }
            }
            break;

            case PLACE_BUILDING: {
                Point point = utils.jsonToPoint(jsonBody);

                Building building = utils.buildingFactory(jsonBody, player);

                synchronized (map) {
                    try {
                        map.placeBuilding(building, point);
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            break;

            case PLACE_ROAD: {
                JSONArray jsonRoadPoints = (JSONArray) jsonBody.get("road");

                List<Point> roadPoints = utils.jsonToPoints(jsonRoadPoints);

                synchronized (map) {
                    try {
                        Road road = map.placeRoad(player, roadPoints);
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            break;

            case PLACE_FLAG: {
                JSONObject jsonFlag = (JSONObject) jsonBody.get("flag");

                Point flagPoint = utils.jsonToPoint(jsonFlag);

                synchronized (map) {
                    try {
                        Flag flag = map.placeFlag(player, flagPoint);
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            break;

            case PLACE_FLAG_AND_ROAD: {
                // TODO: handle case where the flag already exists

                JSONObject jsonFlag = (JSONObject) jsonBody.get("flag");
                JSONArray jsonRoadPoints = (JSONArray) jsonBody.get("road");

                Point flagPoint = utils.jsonToPoint(jsonFlag);

                List<Point> roadPoints = utils.jsonToPoints(jsonRoadPoints);

                // Handle the case where the last point overlaps with the flag point
                Point lastPoint = roadPoints.getLast();

                if (lastPoint.equals(flagPoint)) {
                    Point secondLastPoint = roadPoints.get(roadPoints.size() - 2);
                    int gapX = Math.abs(lastPoint.x - secondLastPoint.x);
                    int gapY = Math.abs(lastPoint.y - secondLastPoint.y);

                    // Is the gap between the last point and the one before too long? Then remove it and let the code
                    // downstream fill the gap
                    if (!((gapX == 2 && gapY == 0) || gapY == 1 && gapX == 1)) {
                        roadPoints.removeLast();
                    }

                    // As long as there as each step is allowed, the following code can handle that last point of the
                    // road overlaps with the flag point
                }

                synchronized (map) {
                    try {
                        Flag flag = map.placeFlag(player, flagPoint);

                        Point lastPointInRoad = roadPoints.getLast();

                        if (flagPoint.distance(lastPointInRoad) > 2) {
                            List<Point> additionalRoad = map.findAutoSelectedRoad(
                                    player,
                                    lastPointInRoad,
                                    flagPoint,
                                    new HashSet<>(roadPoints)
                            );

                            // Remove the first point in the extended list because it overlaps with the given road points
                            additionalRoad.removeFirst();

                            roadPoints.addAll(additionalRoad);
                        }

                        if (map.isFlagAtPoint(flagPoint)) {
                            Road road = map.placeRoad(player, roadPoints);
                        }
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            break;

            case REMOVE_ROAD: {
                String roadId = (String) jsonBody.get("id");

                Road road = (Road) idManager.getObject(roadId);

                try {
                    synchronized (map) {
                        map.removeRoad(road);
                    }
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            }

            break;

            case REMOVE_FLAG: {
                String flagId = (String) jsonBody.get("id");

                Flag flag = (Flag) idManager.getObject(flagId);

                try {
                    synchronized (map) {
                        map.removeFlag(flag);
                    }
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            }
            break;

            case REMOVE_BUILDING: {
                String buildingId = (String) jsonBody.get("id");

                Building building = (Building) idManager.getObject(buildingId);

                try {
                    synchronized (map) {
                        building.tearDown();
                    }
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            }
            break;

            default:
                throw new RuntimeException("Message contains unknown command: " + message);
        }
    }

    private void sendAmountReplyToPlayer(int amount, Player player, JSONObject jsonMessage) {
        long requestId = (Long) jsonMessage.get("requestId");

        JSONObject jsonResponse = new JSONObject();

        jsonResponse.put("amount", amount);
        jsonResponse.put("requestId", requestId);

        sendToPlayer(jsonResponse, player);
    }

    private void sendToPlayer(JSONObject jsonMessage, Player player) {
        playerToSession.get(player).getAsyncRemote().sendText(jsonMessage.toJSONString());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("ON CLOSE");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("ON ERROR: " + throwable);

        /* Remove the error session */
        Player player = (Player) session.getUserProperties().get("PLAYER");

        if (player != null) {
            System.out.println("Removing session for player: " + player);
            playerToSession.remove(player);
        }
    }

    @OnOpen
    public void onOpen(Session session, @javax.websocket.server.PathParam("gameId") String gameId, @javax.websocket.server.PathParam("playerId") String playerId, EndpointConfig config) throws IOException {

        System.out.println("Websocket opened");

        /* Subscribe to changes */
        Player player = (Player) idManager.getObject(playerId);

        if (player == null || idManager.getObject(gameId) == null) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "The player or game doesn't exist"));
        } else {

            session.getUserProperties().put("PLAYER", player);
            session.getUserProperties().put("GAME", idManager.getObject(gameId));

            System.out.println("Storing session");
            this.playerToSession.put(player, session);

            System.out.println("Starting to monitor");
            player.monitorGameView(this);
        }
    }

    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {
        try {
            Session session = playerToSession.get(player);

            if (session != null) {
                JSONObject jsonGameMonitoringEvent = utils.gameMonitoringEventsToJson(gameChangesList, player);

                session.getAsyncRemote().sendText(jsonGameMonitoringEvent.toJSONString());
            }

        } catch (Exception e) {
            System.out.println("Exception while sending updates to frontend: " + e);
            e.printStackTrace();
        }
    }
}