package org.appland.settlers.rest.resource;

import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.json.simple.JSONObject;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/ws/monitor/games/{gameId}/players/{playerId}")

public class WebsocketMonitor implements PlayerGameViewMonitor {

    private final Map<Player, Session> sessions;
    private final Utils utils;

    private final IdManager idManager = IdManager.idManager;

    public WebsocketMonitor() {
        sessions = new HashMap<>();

        System.out.println("CREATED NEW WEBSOCKET MONITOR");
        utils = new Utils(idManager);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("ON MESSAGE: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("ON CLOSE");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("ON ERROR: " + throwable);

        /* Remove the error session */
        Player player = null;
        for (Map.Entry<Player, Session> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                player = entry.getKey();

                break;
            }
        }

        if (player != null) {
            System.out.println("Removing session for player: " + player);
            sessions.remove(player);
        }
    }

    @OnOpen
    public void onOpen(Session session, @javax.websocket.server.PathParam("gameId") String gameId, @javax.websocket.server.PathParam("playerId") String playerId, EndpointConfig config) {

        System.out.println("Websocket opened");

        /* Subscribe to changes */
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        System.out.println("Storing session");
        this.sessions.put(player, session);

        System.out.println("Starting to monitor");
        player.monitorGameView(this);
    }

    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {
        try {

            Session session = sessions.get(player);

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