package org.appland.settlers.rest.resource;

import org.appland.settlers.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint(value = "/ws/monitor/games/{gameId}/players/{playerId}")

public class WebsocketMonitor implements PlayerGameViewMonitor {

    private final Map<Player, Session> playerToSession;
    private final Utils utils;
    private final JSONParser parser;


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
            case GET_WHEAT_QUOTAS: {
                JSONObject jsonResponse = new JSONObject();

                long requestId = (Long) jsonBody.get("requestId");

                jsonResponse.put("donkeyFarm", player.getWheatQuota(DonkeyFarm.class));
                jsonResponse.put("pigFarm", player.getWheatQuota(PigFarm.class));
                jsonResponse.put("mill", player.getWheatQuota(Mill.class));
                jsonResponse.put("brewery", player.getWheatQuota(Brewery.class));

                jsonResponse.put("requestId", requestId);

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
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
                JSONObject jsonResponse = new JSONObject();

                long requestId = (Long) jsonBody.get("requestId");

                jsonResponse.put("ironMine", player.getFoodQuota(IronMine.class));
                jsonResponse.put("coalMine", player.getFoodQuota(CoalMine.class));
                jsonResponse.put("goldMine", player.getFoodQuota(GoldMine.class));
                jsonResponse.put("graniteMine", player.getFoodQuota(GraniteMine.class));

                jsonResponse.put("requestId", requestId);

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case GET_COAL_QUOTAS: {
                JSONObject jsonResponse = new JSONObject();

                long requestId = (Long) jsonBody.get("requestId");

                jsonResponse.put("mint", player.getCoalQuota(Mint.class));
                jsonResponse.put("armory", player.getCoalQuota(Armory.class));
                jsonResponse.put("ironSmelter", player.getCoalQuota(IronSmelter.class));

                jsonResponse.put("requestId", requestId);

                System.out.println(jsonResponse.toJSONString());

                session.getAsyncRemote().sendText(jsonResponse.toJSONString());
            }
            break;

            case SET_FOOD_QUOTAS:
                Long ironMineAmount = (Long) jsonBody.get("ironMine");
                Long coalMineAmount = (Long) jsonBody.get("coalMine");
                Long goldMineAmount = (Long) jsonBody.get("goldMine");
                Long graniteMineAmount = (Long) jsonBody.get("graniteMine");

                player.setCoalQuota(IronMine.class, ironMineAmount.intValue());
                player.setCoalQuota(CoalMine.class, coalMineAmount.intValue());
                player.setCoalQuota(GoldMine.class, goldMineAmount.intValue());
                player.setCoalQuota(GraniteMine.class, graniteMineAmount.intValue());

                break;

            case SET_COAL_QUOTAS:
                Long mintAmount = (Long) jsonBody.get("mint");
                Long armoryAmount = (Long) jsonBody.get("armory");
                Long ironSmelterAmount = (Long) jsonBody.get("ironSmelter");

                player.setCoalQuota(Mint.class, mintAmount.intValue());
                player.setCoalQuota(Armory.class, armoryAmount.intValue());
                player.setCoalQuota(IronSmelter.class, ironSmelterAmount.intValue());

                break;

            case REMOVE_MESSAGE:
                String messageId = (String) jsonBody.get("messageId");

                Message gameMessage = (Message) idManager.getObject(messageId);

                player.removeMessage(gameMessage);

                break;

            case START_DETAILED_MONITORING: {
                String id = (String) jsonBody.get("id");

                Object object = idManager.getObject(id);

                if (object instanceof Building building) {
                    synchronized (map) {
                        player.addDetailedMonitoring(building);
                    }
                } else if (object instanceof Flag flag) {
                    synchronized (map) {
                        player.addDetailedMonitoring(flag);
                    }
                }
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

                        Arrays.stream(Military.Rank.values()).iterator().forEachRemaining(
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
                JSONObject jsonResponse = new JSONObject();
                JSONArray jsonPointsInformation = new JSONArray();

                List<Point> points = utils.jsonToPoints((JSONArray) jsonBody.get("points"));
                long requestId = (long) jsonBody.get("requestId");

                jsonResponse.put("requestId", requestId);
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

                    JSONObject jsonFullSync = new JSONObject();

                    jsonFullSync.put("playerView", utils.playerViewToJson(playerId, map, player));

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

                        Point lastPointInRoad = roadPoints.get(roadPoints.size() - 1);

                        if (flagPoint.distance(lastPointInRoad) > 2) {
                            List<Point> additionalRoad = map.findAutoSelectedRoad(
                                    player,
                                    lastPointInRoad,
                                    flagPoint,
                                    new HashSet<>(roadPoints)
                            );

                            // Remove the first point in the extended list because it overlaps with the given road points
                            additionalRoad.remove(0);

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

        if (player == null && idManager.getObject(gameId) == null) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "The player or game doesn't exist"));
        } else {

            session.getUserProperties().put("PLAYER", player);

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