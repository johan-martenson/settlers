package org.appland.settlers.rest.resource;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    public void onMessage(Session session, String message) {
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
            case INFORMATION_ON_POINTS:

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