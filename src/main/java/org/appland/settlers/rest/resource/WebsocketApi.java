package org.appland.settlers.rest.resource;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.chat.ChatManager;
import org.appland.settlers.computer.util.GamePlay;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerChangeListener;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.ResourceLevel;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Rank;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.statistics.StatisticsListener;
import org.appland.settlers.rest.GameTicker;
import org.appland.settlers.utils.JsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.rest.resource.GameResources.GAME_RESOURCES;
import static org.appland.settlers.rest.resource.GameUtils.startGame;

/**
 * WebSocket API endpoint for interacting with the game server.
 *
 * <h2>Concurrency and Synchronization Model</h2>
 * <p>
 * This class is accessed concurrently by multiple threads:
 * <ul>
 *   <li>WebSocket container threads invoking {@code @OnMessage}, {@code @OnOpen}, {@code @OnClose}, {@code @OnError}</li>
 *   <li>Game simulation threads (e.g. {@link org.appland.settlers.rest.GameTicker}) invoking listeners such as
 *       {@link org.appland.settlers.model.PlayerGameViewMonitor}, {@link org.appland.settlers.model.statistics.StatisticsListener},
 *       and {@link org.appland.settlers.model.PlayerChangeListener}</li>
 * </ul>
 *
 * <p>Because of this, explicit synchronization is used. The following rules define the intended synchronization scheme.</p>
 *
 * <h3>1. Game State Synchronization</h3>
 *
 * <ul>
 *   <li>The {@link GameMap} instance is the primary lock for all game-world state.</li>
 *   <li>All mutations and reads of game state (buildings, roads, players, statistics, etc.) must be performed inside:
 *       <pre>{@code synchronized (map) { ... }}</pre>
 *   </li>
 *   <li>Objects belonging to a map (e.g. {@link Building}) must be synchronized via their map:
 *       <pre>{@code synchronized (building.getMap()) { ... }}</pre>
 *   </li>
 * </ul>
 *
 * <h3>2. Player Synchronization</h3>
 *
 * <ul>
 *   <li>If a change to a {@link Player} affects gameplay (e.g. resources, units, statistics, map interaction),
 *       the {@link GameMap} must be locked:
 *       <pre>{@code synchronized (player.getMap()) { ... }}</pre>
 *   </li>
 *   <li>If a change only affects player metadata (e.g. name, color, nation), the {@link Player} instance itself must be locked:
 *       <pre>{@code synchronized (player) { ... }}</pre>
 *   </li>
 *   <li>Code must not rely on both locks being held at the same time.</li>
 * </ul>
 *
 * <h3>3. GameResource Synchronization</h3>
 *
 * <ul>
 *   <li>{@link GameResource} instances are synchronized on themselves:
 *       <pre>{@code synchronized (game) { ... }}</pre>
 *   </li>
 *   <li>This applies to lobby-level state such as players in a game, game name, map selection, etc.</li>
 * </ul>
 *
 * <h3>4. Listener Collections</h3>
 * <p>
 * The following collections are mutable and accessed from multiple threads:
 *
 * <ul>
 *   <li>{@code gameListListeners}</li>
 *   <li>{@code gameInfoListeners}</li>
 *   <li>{@code chatRoomListeners}</li>
 *   <li>{@code statisticsListeners}</li>
 *   <li>{@code playerListeners}</li>
 *   <li>{@code playerToSession}</li>
 * </ul>
 *
 * <p>Access rules:</p>
 * <ul>
 *   <li>All modifications (add/remove) must be performed inside a synchronized block on the collection itself.</li>
 *   <li>Iteration should also be performed under the same lock or using a defensive copy.</li>
 *   <li>Callers must tolerate missing entries (i.e. {@code get(...)} may return {@code null}).</li>
 * </ul>
 *
 * <h3>5. Listener Callbacks</h3>
 *
 * <ul>
 *   <li>Callbacks such as {@link #onViewChangesForPlayer(Player, GameChangesList)} and
 *       {@link #buildingStatisticsChanged(Building)} may be invoked while the game thread holds the map lock.</li>
 *   <li>These methods must <b>not</b> attempt to acquire locks that could lead to deadlocks.</li>
 *   <li>In particular, no synchronization on {@link GameMap} may occur inside these callbacks unless explicitly safe.</li>
 * </ul>
 *
 * <h3>6. Session Access</h3>
 *
 * <ul>
 *   <li>{@link Session} objects are not synchronized explicitly.</li>
 *   <li>All writes use {@code session.getAsyncRemote().sendText(...)} which is thread-safe per WebSocket spec.</li>
 *   <li>Callers must handle the possibility that a session has been closed or removed concurrently.</li>
 *   <li>Sending to a session must not be done with locks held.</li>
 * </ul>
 *
 * <h3>7. Error Handling and Robustness</h3>
 *
 * <ul>
 *   <li>All listener maps may be concurrently modified; null checks are required when accessing them.</li>
 *   <li>Client input is not trusted; lookups via {@link IdManager} may fail or return unexpected types.</li>
 * </ul>
 *
 * <h3>Summary</h3>
 *
 * <ul>
 *   <li>{@link GameMap} is the primary lock for simulation state.</li>
 *   <li>{@link Player} uses dual locking depending on whether the change affects gameplay or metadata.</li>
 *   <li>{@link GameResource} protects lobby state.</li>
 *   <li>Listener collections must be explicitly synchronized.</li>
 *   <li>Listener callbacks must avoid introducing new locks or blocking operations.</li>
 * </ul>
 */
@ServerEndpoint(value = "/ws/api")
public class WebsocketApi implements PlayerGameViewMonitor,
        GameResources.GameListListener,
        GameResource.GameResourceListener,
        ChatManager.ChatListener,
        StatisticsListener, PlayerChangeListener {

    private final Map<Player, Set<Session>> playerToSessions = new HashMap<>();
    private final Map<Session, Player> sessionToPlayer = new HashMap<>();
    private final JsonUtils jsonUtils = new JsonUtils(IdManager.idManager);
    private final JSONParser parser = new JSONParser();
    private final IdManager idManager = IdManager.idManager;
    private final GameTicker gameTicker = GameTicker.GAME_TICKER;
    private final Collection<Session> gameListListeners = new HashSet<>();
    private final Map<GameResource, Collection<Session>> gameInfoListeners = new HashMap<>();
    private final Map<String, Collection<Session>> chatRoomListeners = new HashMap<>();
    private final Map<GameMap, Set<Session>> statisticsListeners = new HashMap<>();
    private final Map<Player, Set<Session>> playerListeners = new HashMap<>();
    private final Map<Session, Set<String>> detailedMonitoringIds = new HashMap<>();
    private final Map<Session, Set<Player>> chatPlayerListeners = new HashMap<>();

    public WebsocketApi() {
        System.out.println("CREATED NEW WEBSOCKET MONITOR");
    }

    /**
     * Called when the given player receives a new chat message
     * <p>
     * LOCKS HELD: ChatManager.class
     *
     * @param chatMessage
     * @param player
     */
    @Override
    public void newMessageForPlayer(ChatManager.ChatMessage chatMessage, Player player) {
        System.out.println("ON NEW MESSAGE FOR PLAYER");

        sendToPlayer(new JSONObject(Map.of(
                        "type", "NEW_CHAT_MESSAGE",
                        "chatMessage", jsonUtils.chatMessageToPlayerToJson(chatMessage, player)
                )),
                player);
    }

    /**
     * Called when the given chat room receives a new chat message
     * <p>
     * LOCKS HELD: ChatManager.class
     *
     * @param chatMessage
     * @param roomId
     */
    @Override
    public void newMessageForRoom(ChatManager.ChatMessage chatMessage, String roomId) {
        System.out.println("ON NEW MESSAGE FOR ROOM");

        synchronized (chatRoomListeners) {
            chatRoomListeners.get(roomId).forEach(session -> sendToSession(session,
                    new JSONObject(Map.of(
                            "type", "NEW_CHAT_MESSAGE",
                            "chatMessage", jsonUtils.chatMessageToRoomToJson(chatMessage, roomId)
                    ))));
        }
    }

    /**
     * Called when the game receives a new chat message. Translates the GameMap instance to a room id and sends the
     * message on to listening frontend instances.
     * <p>
     * LOCKS HELD: ChatManager.class
     *
     * @param chatMessage
     * @param game
     */
    @Override
    public void newMessageForGame(ChatManager.ChatMessage chatMessage, GameMap game) {
        System.out.println("NEW MESSAGE FOR GAME");

        var roomId = "game-" + idManager.getId(GAME_RESOURCES.getGameResource(game));

        synchronized (chatRoomListeners) {
            chatRoomListeners.get(roomId).forEach(session -> sendToSession(session,
                    new JSONObject(Map.of(
                            "type", "NEW_CHAT_MESSAGE",
                            "chatMessage", jsonUtils.chatMessageToRoomToJson(chatMessage, roomId)
                    ))));
        }
    }

    /**
     * Called when the game resource changes. E.g. allow/disallow cheating
     * <p>
     * LOCKS HELD: gameResource
     *
     * @param gameResource
     */
    @Override
    public void onGameResourceChanged(GameResource gameResource) {
        System.out.println();
        System.out.println("ON GAME RESOURCE CHANGED");

        synchronized (gameInfoListeners) {
            if (gameInfoListeners.containsKey(gameResource)) {
                gameInfoListeners.get(gameResource).forEach(session -> sendToSession(session,
                        new JSONObject(Map.of(
                                "type", "GAME_INFO_CHANGED",
                                "gameInformation", jsonUtils.gameToJson(gameResource)
                        ))));
            }
        }

        synchronized (gameListListeners) {
            gameListListeners.forEach(session -> sendToSession(session,
                    new JSONObject(Map.of(
                            "type", "GAME_LIST_CHANGED",
                            "games", jsonUtils.gamesToJson(GAME_RESOURCES.getGames())
                    ))));
        }
    }

    /**
     * Called when the full list of games changes.
     * <p>
     * LOCKS HELD: GAME_RESOURCES
     *
     * @param games
     */
    @Override
    public void onGameListChanged(Collection<GameResource> games) {
        System.out.println();
        System.out.println("ON GAME LIST CHANGED");

        synchronized (gameListListeners) {
            gameListListeners.forEach(session -> sendToSession(session,
                    new JSONObject(Map.of(
                            "type", "GAME_LIST_CHANGED",
                            "games", jsonUtils.gamesToJson(games)
                    ))));

            if (!gameListListeners.isEmpty()) {
                games.forEach(gameResource -> gameResource.addChangeListener(this));
            }
        }
    }

    @Override
    public void onGameRemoved(GameResource game) {
        synchronized (gameInfoListeners) {
            if (!gameInfoListeners.containsKey(game)) {
                game.removeChangeListener(this);
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("\nON MESSAGE: " + message);

        JSONObject jsonBody;

        try {
            jsonBody = (JSONObject) parser.parse(message);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        var command = Command.valueOf((String) jsonBody.get("command"));

        switch (command) {
            case BLOCK_MATERIAL -> {
                var house = (Storehouse) idManager.getObject((String) jsonBody.get("houseId"));
                var material = jsonUtils.jsonToMaterial((String) jsonBody.get("material"));

                synchronized (house.getMap()) {
                    house.blockDeliveryOfMaterial(material);
                }
            }

            case ALLOW_MATERIAL -> {
                var house = (Storehouse) idManager.getObject((String) jsonBody.get("houseId"));
                var material = jsonUtils.jsonToMaterial((String) jsonBody.get("material"));

                synchronized (house.getMap()) {
                    house.allowDeliveryOfMaterial(material);
                }
            }

            case SEND_OUT_MATERIAL -> {
                var house = (Storehouse) idManager.getObject((String) jsonBody.get("houseId"));
                var material = jsonUtils.jsonToMaterial((String) jsonBody.get("material"));

                synchronized (house.getMap()) {
                    house.pushOutAll(material);
                }
            }

            case STOP_SENDING_OUT_MATERIAL -> {
                var house = (Storehouse) idManager.getObject((String) jsonBody.get("houseId"));
                var material = jsonUtils.jsonToMaterial((String) jsonBody.get("material"));

                synchronized (house.getMap()) {
                    house.stopPushingOut(material);
                }
            }

            case LISTEN_TO_PLAYER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                synchronized (playerListeners) {
                    if (!playerListeners.containsKey(player)) {
                        playerListeners.put(player, new HashSet<>());
                    }

                    if (playerListeners.get(player).isEmpty()) {
                        player.addPlayerChangeListener(this);
                    }

                    playerListeners.get(player).add(session);
                }
            }

            case STOP_LISTENING_TO_PLAYER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                synchronized (playerListeners) {
                    var listeners = playerListeners.get(player);

                    if (listeners != null) {
                        listeners.remove(session);

                        if (listeners.isEmpty()) {
                            playerListeners.remove(player);
                            player.removePlayerChangeListener(this);
                        }
                    }
                }
            }

            case GET_TRANSPORT_PRIORITY -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                synchronized (player.getMap()) {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "priority", jsonUtils.transportPriorityToJson(player.getTransportPriorities())
                            )));
                }
            }

            case LISTEN_TO_STATISTICS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    if (statisticsListeners.getOrDefault(map, Set.of()).isEmpty()) {
                        map.getStatisticsManager().addListener(this);
                    }
                }

                synchronized (statisticsListeners) {
                    statisticsListeners.computeIfAbsent(map, k -> new HashSet<>()).add(session);
                }
            }

            case STOP_LISTENING_TO_STATISTICS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    var listeners = statisticsListeners.get(map);

                    if (listeners != null) {
                        listeners.remove(session);

                        if (listeners.isEmpty()) {
                            statisticsListeners.remove(map);
                            map.getStatisticsManager().removeListener(this);
                        }
                    }
                }
            }

            case GET_STATISTICS -> {
                var gameResource = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = gameResource.getGameMap();

                synchronized (map) {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "statistics", jsonUtils.statisticsToJson(
                                            map.getTime(),
                                            player,
                                            map.getPlayers(),
                                            map.getStatisticsManager()
                                    )
                            )));
                }
            }

            case GET_TERRAIN -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "terrain", jsonUtils.mapFileTerrainToJson((MapFile) idManager.getObject((String) jsonBody.get("mapId")))
                        )));
            }

            case SET_TRANSPORT_PRIORITY -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var category = jsonUtils.jsonToTransportCategory((String) jsonBody.get("category"));
                int priority = ((Long) jsonBody.get("priority")).intValue();

                synchronized (map) {
                    player.setTransportPriority(priority, category);
                }
            }

            case CANCEL_EVACUATION -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (house.getMap()) {
                    house.cancelEvacuation();
                }
            }

            case DISABLE_PROMOTIONS -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (house.getMap()) {
                    house.disablePromotions();
                }
            }

            case ENABLE_PROMOTIONS -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (house.getMap()) {
                    house.enablePromotions();
                }
            }

            case PAUSE_PRODUCTION -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (house.getMap()) {
                    house.stopProduction();
                }
            }

            case RESUME_PRODUCTION -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (house.getMap()) {
                    house.resumeProduction();
                }
            }

            case DELETE_GAME -> {
                var gameResource = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                GAME_RESOURCES.removeGame(gameResource);
            }

            case FIND_NEW_ROAD -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var start = jsonUtils.jsonToPoint((JSONObject) jsonBody.get("from"));
                var goal = jsonUtils.jsonToPoint((JSONObject) jsonBody.get("to"));
                var map = player.getMap();
                var avoid = (Set<Point>) null;

                if (jsonBody.containsKey("avoid")) {
                    avoid = jsonUtils.jsonToPointsSet((JSONArray) jsonBody.get("avoid"));
                }

                synchronized (map) {
                    var possibleRoad = map.findAutoSelectedRoad(player, start, goal, avoid);

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "roadIsPossible", true,
                                    "possibleRoad", jsonUtils.pointsToJson(possibleRoad),
                                    "closesRoad", map.isFlagAtPoint(goal) || (map.isRoadAtPoint(goal) && map.isAvailableFlagPoint(player, goal))
                            )));
                }
            }

            case EVACUATE_HOUSE -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (house.getMap()) {
                    house.evacuate();
                }
            }

            case ATTACK_HOUSE -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));
                var attackers = ((Long) jsonBody.get("attackers")).intValue();
                var attackStrength = AttackStrength.valueOf((String) jsonBody.get("attackType"));

                synchronized (house.getMap()) {
                    player.attack(house, attackers, attackStrength);
                }
            }

            case SET_TOOL_PRODUCTION_PRIORITY -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var tool = Material.valueOf((String) jsonBody.get("tool"));
                var prio = ((Long) jsonBody.get("priority")).intValue();

                synchronized (player.getMap()) {
                    player.setProductionQuotaForTool(tool, prio);
                }
            }

            case GET_TOOL_PRODUCTION_PRIORITIES -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                synchronized (player.getMap()) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "toolPriorities", jsonUtils.toolQuotasToJson(player)
                    )));
                }
            }

            case GET_CHAT_HISTORY_FOR_ROOMS -> {
                var roomIds = (JSONArray) jsonBody.get("roomIds");

                for (var roomIdObj : roomIds) {
                    var roomId = (String) roomIdObj;

                    if (roomId.startsWith("game-")) {
                        var map = ((GameResource) idManager.getObject(roomId.substring("game-".length()))).getGameMap();

                        synchronized (ChatManager.class) {
                            var msg = new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "chatHistory", jsonUtils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForGame(map), roomId)
                            ));

                            sendToSession(session,
                                    new JSONObject(Map.of(
                                            "requestId", jsonBody.get("requestId"),
                                            "chatHistory", jsonUtils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForGame(map), roomId)
                                    )));
                        }
                    } else {
                        synchronized (ChatManager.class) {
                            sendToSession(session,
                                    new JSONObject(Map.of(
                                            "requestId", jsonBody.get("requestId"),
                                            "chatHistory", jsonUtils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForRoom(roomId), roomId)
                                    )));
                        }
                    }
                }
            }

            case GET_CHAT_HISTORY_FOR_ROOM -> {
                var roomId = (String) jsonBody.get("roomId");

                if (roomId.startsWith("game-")) {
                    var map = ((GameResource) idManager.getObject(roomId.substring("game-".length()))).getGameMap();

                    synchronized (ChatManager.class) {
                        var msg = new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "chatHistory", jsonUtils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForGame(map), roomId)
                        ));

                        sendToSession(session,
                                new JSONObject(Map.of(
                                        "requestId", jsonBody.get("requestId"),
                                        "chatHistory", jsonUtils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForGame(map), roomId)
                                )));
                    }
                } else {
                    synchronized (ChatManager.class) {
                        sendToSession(session,
                                new JSONObject(Map.of(
                                        "requestId", jsonBody.get("requestId"),
                                        "chatHistory", jsonUtils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForRoom(roomId), roomId)
                                )));
                    }
                }
            }

            case LISTEN_TO_CHAT_MESSAGES -> {
                if (jsonBody.containsKey("playerId")) {
                    var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                    synchronized (ChatManager.class) {
                        ChatManager.addMessageListenerForPlayer(player, this);
                    }

                    synchronized (chatPlayerListeners) {
                        chatPlayerListeners
                                .computeIfAbsent(session, k -> new HashSet<>())
                                .add(player);
                    }
                }

                if (jsonBody.containsKey("roomIds")) {
                    ((JSONArray) jsonBody.get("roomIds"))
                            .forEach(roomId -> {
                                synchronized (ChatManager.class) {
                                    var roomIdString = (String) roomId;

                                    if (roomIdString.startsWith("game-")) {
                                        var gameResource = (GameResource) idManager.getObject(roomIdString.substring(5));
                                        ChatManager.addMessageListenerForGame(gameResource.getGameMap(), this);
                                    } else {
                                        ChatManager.addMessageListenerForRoom((String) roomId, this);
                                    }
                                }

                                synchronized (chatRoomListeners) {
                                    chatRoomListeners
                                            .computeIfAbsent((String) roomId, k -> new HashSet<>())
                                            .add(session);
                                }
                            });
                }
            }

            case SEND_CHAT_MESSAGE_TO_ROOM -> {
                var roomId = (String) jsonBody.get("roomId");
                var chatMessage = (String) jsonBody.get("text");
                var player = (Player) idManager.getObject((String) jsonBody.get("from"));

                // Sending to a game uses one API while sending to another room uses another API
                // Messages to a game starts with "game-".

                if (roomId.startsWith("game-")) {
                    var game = (GameResource) idManager.getObject(roomId.substring(5));

                    synchronized (ChatManager.class) {
                        ChatManager.sendChatToGame(chatMessage, player, game.getGameMap());
                    }
                } else {
                    synchronized (ChatManager.class) {
                        ChatManager.sendChatToRoom(
                                roomId,
                                chatMessage,
                                (Player) idManager.getObject((String) jsonBody.get("from"))
                        );
                    }
                }
            }

            case SET_CHEATING_ON_OFF -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    game.setCheatingEnabled((Boolean) jsonBody.get("cheatingEnabled"));
                }
            }

            case LISTEN_TO_GAME_LIST -> {
                synchronized (gameListListeners) {
                    gameListListeners.add(session);

                    if (gameListListeners.size() == 1) {
                        GAME_RESOURCES.addAddedAndRemovedGamesListener(this);
                    }

                    synchronized (gameInfoListeners) {
                        GAME_RESOURCES.getGames().forEach(gameResource -> {
                            if (!gameInfoListeners.containsKey(gameResource)) {
                                gameResource.addChangeListener(this);
                            }
                        });
                    }
                }
            }

            case STOP_LISTENING_TO_GAME_LIST -> {
                synchronized (gameListListeners) {
                    gameListListeners.remove(session);

                    if (gameListListeners.isEmpty()) {
                        GAME_RESOURCES.removeAddedAndRemovedGamesListener(this);

                        synchronized (gameInfoListeners) {
                            GAME_RESOURCES.getGames().stream()
                                    .filter(gameResource -> !gameInfoListeners.containsKey(gameResource))
                                    .forEach(gameResource -> gameResource.removeChangeListener(this));
                        }
                    }
                }
            }

            case LISTEN_TO_GAME_INFO -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (gameInfoListeners) {
                    if (!gameInfoListeners.containsKey(game)) {
                        gameInfoListeners.put(game, new HashSet<>());
                    }

                    gameInfoListeners.get(game).add(session);

                    game.addChangeListener(this);

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "gameInformation", jsonUtils.gameToJson(game)
                            )));
                }
            }

            case START_MONITORING_GAME -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                var previousPlayer = (Player) null;

                synchronized (sessionToPlayer) {
                    previousPlayer = sessionToPlayer.get(session);

                    sessionToPlayer.put(session, player);
                }

                var startMonitoring = false;

                synchronized (playerToSessions) {

                    // Clean up previous sessions for the previous player (if any)
                    var sessionsForPreviousPlayer = playerToSessions.get(previousPlayer);

                    if (sessionsForPreviousPlayer != null) {
                        sessionsForPreviousPlayer.remove(session);

                        if (sessionsForPreviousPlayer.isEmpty()) {
                            playerToSessions.remove(previousPlayer);
                            previousPlayer.stopMonitoringGameView(this);
                        }
                    }

                    // Handle sessions for the new player
                    var sessions = playerToSessions.computeIfAbsent(player, k -> new HashSet<>());

                    if (sessions.isEmpty()) {
                        startMonitoring = true;
                    }

                    sessions.add(session);
                }

                if (startMonitoring) {
                    player.monitorGameView(this);
                }

                if (map != null) {
                    synchronized (map) {
                        sendToSession(session,
                                new JSONObject(Map.of(
                                        "requestId", jsonBody.get("requestId"),
                                        "playerView", jsonUtils.playerViewToJson(map, player, game)
                                )));
                    }
                } else {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId")
                            )));
                }
            }

            case STOP_LISTENING_TO_GAME_INFO -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (gameInfoListeners) {
                    var listeners = gameInfoListeners.get(game);

                    if (listeners != null) {
                        listeners.remove(session);

                        if (listeners.isEmpty()) {
                            gameInfoListeners.remove(game);

                            if (gameListListeners.isEmpty()) {
                                game.removeChangeListener(this);
                            }
                        }
                    }
                }
            }

            case CREATE_GAME -> {
                var gameResource = new GameResource(jsonUtils);

                if (jsonBody.containsKey("players")) {
                    gameResource.setPlayers(jsonUtils.jsonToPlayers((JSONArray) jsonBody.get("players")));
                }

                if (jsonBody.containsKey("name")) {
                    gameResource.setName((String) jsonBody.get("name"));
                }

                GAME_RESOURCES.addGame(gameResource);

                sendToSession(session, new JSONObject(Map.of(
                        "requestId", jsonBody.get("requestId"),
                        "gameInformation", jsonUtils.gameToJson(gameResource)
                )));
            }

            case GET_MAP -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "map", jsonUtils.mapFileToJson((MapFile) idManager.getObject((String) jsonBody.get("mapId")))
                        )));
            }

            case GET_MAPS -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "maps", jsonUtils.toJsonArray(MapsResource.mapsResource.getMaps(), jsonUtils::mapFileToJson)
                        )));
            }

            case GET_MAP_WITH_TERRAIN -> {
                var mapFile = (MapFile) idManager.getObject((String) jsonBody.get("mapId"));
                var jsonMapFile = jsonUtils.mapFileToJson(mapFile);

                jsonMapFile.put("terrain", jsonUtils.mapFileTerrainToJson(mapFile));

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "map", jsonMapFile
                        )));
            }

            case GET_MAPS_WITH_TERRAIN -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "maps", jsonUtils.toJsonArray(
                                        MapsResource.mapsResource.getMaps(),
                                        mapFile -> {
                                            var jsonMapFile = jsonUtils.mapFileToJson(mapFile);

                                            jsonMapFile.put("terrain", jsonUtils.mapFileTerrainToJson(mapFile));

                                            return jsonMapFile;
                                        }
                                ))));
            }

            case GET_GAMES -> {
                synchronized (GAME_RESOURCES) {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "games", jsonUtils.gamesToJson(GAME_RESOURCES.getGames())
                            )));
                }
            }

            case UPDATE_PLAYER -> {
                var playerId = (String) jsonBody.get("playerId");
                var player = (Player) idManager.getObject(playerId);
                var name = (String) jsonBody.get("name");
                var color = PlayerColor.valueOf((String) jsonBody.get("color"));
                var nation = Nation.valueOf((String) jsonBody.get("nation"));

                synchronized (player) {
                    player.update(name, nation, color);

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "playerInformation", jsonUtils.playerToJson(player))
                            ));
                }
            }

            case REMOVE_PLAYER -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                synchronized (game) {
                    game.removePlayer(player);
                }
            }

            case CREATE_PLAYER -> {
                var name = (String) jsonBody.get("name");
                var playerColor = PlayerColor.valueOf((String) jsonBody.get("color"));
                var nation = Nation.valueOf((String) jsonBody.get("nation"));
                var playerType = PlayerType.valueOf((String) jsonBody.get("type"));
                var player = new Player(name, playerColor, nation, playerType);

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "playerInformation", jsonUtils.playerToJson(player))
                        ));
            }

            case ADD_PLAYER_TO_GAME -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    if (player.getPlayerType() == PlayerType.COMPUTER) {
                        game.addComputerPlayer(player);
                    } else {
                        game.addHumanPlayer(player);
                    }

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "gameInformation", jsonUtils.gameToJson(game)
                            )));
                }
            }

            case SET_OTHERS_CAN_JOIN -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var othersCanJoin = (Boolean) jsonBody.get("othersCanJoin");

                synchronized (game) {
                    game.setOthersCanJoin(othersCanJoin);

                    // FIXME: this results in sending the updated player twice
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "gameInformation", jsonUtils.gameToJson(game)
                            )));
                }
            }

            case START_GAME -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    startGame(game, gameTicker);
                }
            }

            case SET_MAP -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    game.setMap((MapFile) idManager.getObject((String) jsonBody.get("mapId")));
                }
            }

            case SET_INITIAL_RESOURCES -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var resources = ResourceLevel.valueOf((String) jsonBody.get("resources"));

                synchronized (game) {
                    game.setResourceLevel(resources);
                }
            }

            case SET_GAME_NAME -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    game.setName((String) jsonBody.get("name"));
                }
            }

            case GET_GAME_INFORMATION -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                if (game != null) {
                    synchronized (game) {
                        sendToSession(session,
                                new JSONObject(Map.of(
                                        "requestId", jsonBody.get("requestId"),
                                        "gameInformation", jsonUtils.gameToJson(game)
                                )));
                    }
                } else {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "error", "The game doesn't exist on the server."
                            )));
                }
            }

            case UPGRADE -> {
                var house = (Building) idManager.getObject((String) jsonBody.get("houseId"));
                var map = house.getMap();

                synchronized (map) {
                    house.upgrade();
                }
            }

            case FLAG_DEBUG_INFORMATION -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var map = game.getGameMap();
                var flag = (Flag) idManager.getObject((String) jsonBody.get("flagId"));

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "flag", jsonUtils.flagToDebugJson(flag)
                    )));
                }
            }

            case GET_SOLDIERS_AVAILABLE_FOR_ATTACK -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getAmountOfSoldiersAvailableForAttack();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case GET_POPULATE_MILITARY_FAR_FROM_BORDER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getAmountOfSoldiersWhenPopulatingFarFromBorder();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case GET_POPULATE_MILITARY_CLOSER_TO_BORDER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getAmountOfSoldiersWhenPopulatingAwayFromBorder();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case GET_POPULATE_MILITARY_CLOSE_TO_BORDER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getAmountOfSoldiersWhenPopulatingCloseToBorder();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case SET_SOLDIERS_AVAILABLE_FOR_ATTACK -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var amount = ((Long) jsonBody.get("amount")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersAvailableForAttack(amount);
                }
            }

            case SET_MILITARY_POPULATION_CLOSE_TO_BORDER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingCloseToBorder(amount);
                }
            }

            case SET_MILITARY_POPULATION_CLOSER_TO_BORDER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingAwayFromBorder(amount);
                }
            }

            case SET_MILITARY_POPULATION_FAR_FROM_BORDER -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingFarFromBorder(amount);
                }
            }

            case SET_GAME_SPEED -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var speed = GameSpeed.valueOf((String) jsonBody.get("speed"));

                synchronized (game) {
                    game.setGameSpeed(speed);
                }
            }

            case GET_MILITARY_SETTINGS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "defenseStrength", player.getDefenseStrength(),
                            "defenseFromSurroundingBuildings", player.getDefenseFromSurroundingBuildings(),
                            "soldierAmountWhenPopulatingCloseToBorder", player.getAmountOfSoldiersWhenPopulatingCloseToBorder(),
                            "soldierAmountWhenPopulatingAwayFromBorder", player.getAmountOfSoldiersWhenPopulatingAwayFromBorder(),
                            "soldierAmountWhenPopulatingFarFromBorder", player.getAmountOfSoldiersWhenPopulatingFarFromBorder(),
                            "soldierStrengthWhenPopulatingBuildings", player.getStrengthOfSoldiersPopulatingBuildings(),
                            "soldierAmountsAvailableForAttack", player.getAmountOfSoldiersAvailableForAttack()
                    )));
                }
            }

            case GET_DEFENSE_FROM_SURROUNDING_BUILDINGS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getDefenseFromSurroundingBuildings();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case SET_DEFENSE_FROM_SURROUNDING_BUILDINGS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setDefenseFromSurroundingBuildings(strength);
                }
            }

            case GET_DEFENSE_STRENGTH -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getDefenseStrength();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case SET_DEFENSE_STRENGTH -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setDefenseStrength(strength);
                }
            }

            case GET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    int amount = player.getStrengthOfSoldiersPopulatingBuildings();
                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }

            case SET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setStrengthOfSoldiersPopulatingBuildings(strength);
                }
            }

            case PAUSE_GAME -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    game.setStatus(GameStatus.PAUSED);
                }
            }

            case RESUME_GAME -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (game) {
                    game.setStatus(GameStatus.STARTED);
                }
            }

            case SET_IRON_BAR_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var armoryAmount = (Long) jsonBody.get("armory");
                var metalworksAmount = (Long) jsonBody.get("metalworks");

                synchronized (map) {
                    player.setIronBarQuota(Armory.class, armoryAmount.intValue());
                    player.setIronBarQuota(Metalworks.class, metalworksAmount.intValue());
                }
            }

            case SET_PLANK_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var constructionAmount = (Long) jsonBody.get("construction");
                var shipyardAmount = (Long) jsonBody.get("shipyard");
                var metalworksAmount = (Long) jsonBody.get("metalworks");

                synchronized (map) {
                    player.setConstructionPlankQuota(constructionAmount.intValue());
                    player.setShipyardPlankQuota(shipyardAmount.intValue());
                    player.setMetalworksPlankQuota(metalworksAmount.intValue());
                }
            }

            case GET_IRON_BAR_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "armory", player.getIronBarQuota(Armory.class),
                            "metalworks", player.getIronBarQuota(Metalworks.class)
                    )));
                }
            }

            case GET_WATER_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "donkeyFarm", player.getWaterQuota(DonkeyFarm.class),
                            "pigFarm", player.getWaterQuota(PigFarm.class),
                            "bakery", player.getWaterQuota(Bakery.class),
                            "brewery", player.getWaterQuota(Brewery.class)
                    )));
                }
            }

            case GET_WHEAT_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "donkeyFarm", player.getWheatQuota(DonkeyFarm.class),
                            "pigFarm", player.getWheatQuota(PigFarm.class),
                            "mill", player.getWheatQuota(Mill.class),
                            "brewery", player.getWheatQuota(Brewery.class)
                    )));
                }
            }

            case SET_WATER_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var donkeyFarmAmount = (Long) jsonBody.get("donkeyFarm");
                var pigFarmAmount = (Long) jsonBody.get("pigFarm");
                var bakeryAmount = (Long) jsonBody.get("bakery");
                var breweryAmount = (Long) jsonBody.get("brewery");

                synchronized (map) {
                    player.setWaterQuota(DonkeyFarm.class, donkeyFarmAmount.intValue());
                    player.setWaterQuota(PigFarm.class, pigFarmAmount.intValue());
                    player.setWaterQuota(Bakery.class, bakeryAmount.intValue());
                    player.setWaterQuota(Brewery.class, breweryAmount.intValue());
                }
            }

            case SET_WHEAT_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var donkeyFarmAmount = (Long) jsonBody.get("donkeyFarm");
                var pigFarmAmount = (Long) jsonBody.get("pigFarm");
                var millAmount = (Long) jsonBody.get("mill");
                var breweryAmount = (Long) jsonBody.get("brewery");

                synchronized (map) {
                    player.setWheatQuota(DonkeyFarm.class, donkeyFarmAmount.intValue());
                    player.setWheatQuota(PigFarm.class, pigFarmAmount.intValue());
                    player.setWheatQuota(Mill.class, millAmount.intValue());
                    player.setWheatQuota(Brewery.class, breweryAmount.intValue());
                }
            }

            case GET_FOOD_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "ironMine", player.getFoodQuota(IronMine.class),
                            "coalMine", player.getFoodQuota(CoalMine.class),
                            "goldMine", player.getFoodQuota(GoldMine.class),
                            "graniteMine", player.getFoodQuota(GraniteMine.class)
                    )));
                }
            }

            case GET_COAL_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "mint", player.getCoalQuota(Mint.class),
                            "armory", player.getCoalQuota(Armory.class),
                            "ironSmelter", player.getCoalQuota(IronSmelter.class)
                    )));
                }
            }

            case SET_FOOD_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var ironMineAmount = (Long) jsonBody.get("ironMine");
                var coalMineAmount = (Long) jsonBody.get("coalMine");
                var goldMineAmount = (Long) jsonBody.get("goldMine");
                var graniteMineAmount = (Long) jsonBody.get("graniteMine");

                synchronized (map) {
                    player.setFoodQuota(IronMine.class, ironMineAmount.intValue());
                    player.setFoodQuota(CoalMine.class, coalMineAmount.intValue());
                    player.setFoodQuota(GoldMine.class, goldMineAmount.intValue());
                    player.setFoodQuota(GraniteMine.class, graniteMineAmount.intValue());
                }
            }

            case SET_COAL_QUOTAS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var mintAmount = (Long) jsonBody.get("mint");
                var armoryAmount = (Long) jsonBody.get("armory");
                var ironSmelterAmount = (Long) jsonBody.get("ironSmelter");

                synchronized (map) {
                    player.setCoalQuota(Mint.class, mintAmount.intValue());
                    player.setCoalQuota(Armory.class, armoryAmount.intValue());
                    player.setCoalQuota(IronSmelter.class, ironSmelterAmount.intValue());
                }
            }

            case REMOVE_MESSAGES -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    for (var messageId : (JSONArray) jsonBody.get("messageIds")) {
                        var gameMessage = (Message) idManager.getObject((String) messageId);
                        player.removeMessage(gameMessage);
                    }
                }
            }

            case REMOVE_MESSAGE -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var gameMessage = (Message) idManager.getObject((String) jsonBody.get("messageId"));

                synchronized (map) {
                    player.removeMessage(gameMessage);
                }
            }

            case START_DETAILED_MONITORING -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var id = (String) jsonBody.get("id");
                var object = idManager.getObject(id);
                var jsonPlayerViewChanges = new JSONObject();

                var jsonUpdate = new JSONObject(Map.of(
                        "type", "PLAYER_VIEW_CHANGED",
                        "playerViewChanges", jsonPlayerViewChanges
                ));

                synchronized (detailedMonitoringIds) {
                    detailedMonitoringIds.computeIfAbsent(session, k -> new HashSet<>()).add(id);
                }

                if (object instanceof Building building) {
                    synchronized (map) {
                        player.addDetailedMonitoring(building);

                        var jsonUpdatedBuildings = new JSONArray();

                        jsonPlayerViewChanges.put("changedBuildings", jsonUpdatedBuildings);
                        jsonUpdatedBuildings.add(jsonUtils.houseToJson(building, player));
                    }
                } else if (object instanceof Flag flag) {
                    synchronized (map) {
                        player.addDetailedMonitoring(flag);

                        var jsonUpdatedFlags = new JSONArray();

                        jsonPlayerViewChanges.put("changedFlags", jsonUpdatedFlags);
                        jsonUpdatedFlags.add(jsonUtils.flagToJson(flag));
                    }
                }

                session.getAsyncRemote().sendText(jsonUpdate.toJSONString());
            }

            case STOP_DETAILED_MONITORING -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var id = (String) jsonBody.get("id");
                var monitoredObject = idManager.getObject(id);

                // FIXME: if two sessions monitor the same building, the first session closing will remove the building for both

                synchronized (detailedMonitoringIds) {
                    var ids = detailedMonitoringIds.get(session);

                    if (ids != null) {
                        ids.remove(id);

                        if (ids.isEmpty()) {
                            detailedMonitoringIds.remove(session);
                        }
                    }
                }

                synchronized (map) {
                    if (monitoredObject instanceof Building building) {
                        player.removeDetailedMonitoring(building);
                    } else if (monitoredObject instanceof Flag flag) {
                        player.removeDetailedMonitoring(flag);
                    }
                }
            }

            case SET_RESERVED_IN_HEADQUARTERS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    var optionalHeadquarter = player.getHeadquarter();

                    if (optionalHeadquarter.isPresent()) {
                        var headquarter = (Headquarter) optionalHeadquarter.get();

                        Arrays.stream(Rank.values()).iterator().forEachRemaining(
                                rank -> {
                                    if (jsonBody.containsKey(rank.name().toUpperCase())) {
                                        var amountLong = (Long) jsonBody.get(rank.name().toUpperCase());
                                        int amount = amountLong.intValue();
                                        headquarter.setReservedSoldiers(rank, amount);
                                    }
                                }
                        );
                    } else {
                        System.out.println("Can't find headquarters for the player!");
                    }
                }
            }

            case INFORMATION_ON_POINTS -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var jsonPointsInformation = new JSONArray();
                var points = jsonUtils.jsonToPoints((JSONArray) jsonBody.get("points"));

                synchronized (map) {
                    for (var point : points) {
                        jsonPointsInformation.add(jsonUtils.pointToDetailedJson(point, player, map));
                    }
                }

                sendToSession(session, new JSONObject(Map.of(
                        "requestId", jsonBody.get("requestId"),
                        "pointsWithInformation", jsonPointsInformation
                )));
            }

            case FULL_SYNC -> {
                var game = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = game.getGameMap();

                // FIXME: should synchronize before accessing game.status
                switch (game.status) {
                    case STARTED, PAUSED -> {
                        synchronized (map) {
                            sendToSession(session,
                                    new JSONObject(Map.of(
                                            "requestId", jsonBody.get("requestId"),
                                            "gameInformation", jsonUtils.gameToJson(game),
                                            "playerView", jsonUtils.playerViewToJson(map, player, game)
                                    )));
                        }
                    }
                    case NOT_STARTED -> {
                        synchronized (game) {
                            sendToSession(session,
                                    new JSONObject(Map.of(
                                            "requestId", jsonBody.get("requestId"),
                                            "gameInformation", jsonUtils.gameToJson(game)
                                    )));
                        }
                    }
                }
            }

            case CALL_SCOUT -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var point = jsonUtils.jsonToPoint((JSONObject) jsonBody.get("point"));

                synchronized (map) {
                    var flag = map.getFlagAtPoint(point);
                    flag.callScout();
                }
            }

            case CALL_GEOLOGIST -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var point = jsonUtils.jsonToPoint((JSONObject) jsonBody.get("point"));

                synchronized (map) {
                    var flag = map.getFlagAtPoint(point);
                    flag.callGeologist();
                }
            }

            case PLACE_BUILDING -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var point = jsonUtils.jsonToPoint(jsonBody);
                var building = jsonUtils.buildingFactory(jsonBody, player);

                synchronized (map) {
                    try {
                        map.placeBuilding(building, point);
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case PLACE_ROAD -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var roadPoints = jsonUtils.jsonToPoints((JSONArray) jsonBody.get("road"));

                synchronized (map) {
                    try {
                        map.placeRoad(player, roadPoints);
                    } catch (InvalidUserActionException e) {
                        System.out.printf("Refusing to place invalid road: %s", roadPoints);
                    }
                }
            }

            case PLACE_CONNECTION -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var roadPoints = jsonUtils.jsonToPoints((JSONArray) jsonBody.get("points"));

                synchronized (map) {
                    try {
                        var road = new ArrayList<Point>();

                        for (var point : roadPoints) {
                            if (road.isEmpty()) {
                                road.add(point);

                                continue;
                            }

                            var lastPoint = road.getLast();

                            // Is there more than one step between the current and the previous point?
                            if (Math.abs(lastPoint.x - point.x) > 2 || Math.abs(lastPoint.y - point.y) > 1) {

                                // Place the road before the gap
                                if (road.size() > 2) {
                                    map.placeRoad(player, road);
                                }

                                road.clear();

                                // Fill in the gap between the last point and the current point
                                GamePlay.connectToPointByRoad(lastPoint, point, player, 0.5);
                            }

                            road.add(point);
                        }
                    } catch (InvalidUserActionException e) {
                        System.out.printf("Refusing to place invalid road: %s", roadPoints);
                    }
                }
            }

            case PLACE_FLAG -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var jsonFlag = (JSONObject) jsonBody.get("flag");
                var flagPoint = jsonUtils.jsonToPoint(jsonFlag);

                synchronized (map) {
                    try {
                        map.placeFlag(player, flagPoint);
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case PLACE_FLAG_AND_ROAD -> {
                // TODO: handle case where the flag already exists

                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var jsonFlag = (JSONObject) jsonBody.get("flag");
                var jsonRoadPoints = (JSONArray) jsonBody.get("road");
                var flagPoint = jsonUtils.jsonToPoint(jsonFlag);
                var roadPoints = jsonUtils.jsonToPoints(jsonRoadPoints);

                // Handle the case where the last point overlaps with the flag point
                var lastPoint = roadPoints.getLast();

                if (lastPoint.equals(flagPoint)) {
                    var secondLastPoint = roadPoints.get(roadPoints.size() - 2);
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
                        var flag = map.placeFlag(player, flagPoint);
                        var lastPointInRoad = roadPoints.getLast();

                        if (flagPoint.distance(lastPointInRoad) > 2) {
                            var additionalRoad = map.findAutoSelectedRoad(
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
                            var road = map.placeRoad(player, roadPoints);
                        }
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case REMOVE_ROAD -> {
                var road = (Road) idManager.getObject((String) jsonBody.get("id"));
                var map = road.getPlayer().getMap();

                try {
                    synchronized (map) {
                        map.removeRoad(road);
                    }
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            }

            case REMOVE_FLAG -> {
                var flag = (Flag) idManager.getObject((String) jsonBody.get("id"));
                var player = flag.getPlayer();
                var map = player.getMap();

                try {
                    synchronized (map) {
                        map.removeFlag(flag);
                    }
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            }

            case REMOVE_BUILDING -> {
                var building = (Building) idManager.getObject((String) jsonBody.get("id"));
                var player = building.getPlayer();
                var map = player.getMap();

                try {
                    synchronized (map) {
                        building.tearDown();
                    }
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            }

            case MARK_GAME_MESSAGES_READ -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();

                synchronized (map) {
                    ((JSONArray) jsonBody.get("messageIds")).stream()
                            .map(messageId -> idManager.getObject((String) messageId))
                            .forEach(readMessage -> player.markMessageAsRead((Message) readMessage));
                }
            }

            case CHEAT -> {
                var player = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var map = player.getMap();
                var cheatCode = (String) jsonBody.get("cheatCode");

                if (cheatCode.equals("GIVE_ME_SOME_MORE")) {
                    synchronized (map) {
                        var headquarters = map.getBuildings().stream()
                                .filter(building -> Objects.equals(building.getPlayer(), player))
                                .filter(building -> building instanceof Headquarter)
                                .findFirst();

                        if (headquarters.isPresent()) {
                            for (var material : Material.values()) {
                                GameUtils.deliver(material, 10, (Headquarter) headquarters.get());
                            }
                        }
                    }
                } else if (cheatCode.equals("SHOW_ME_THE_WORLD")) {
                    synchronized (map) {
                        org.appland.settlers.model.GameUtils.discoverFullMap(player);
                    }
                }
            }

            default -> throw new RuntimeException("Message contains unknown command: " + message);
        }
    }

    private void sendAmountReplyToPlayer(int amount, Player player, JSONObject jsonMessage) {
        long requestId = (Long) jsonMessage.get("requestId");

        sendToPlayer(new JSONObject(Map.of(
                "requestId", requestId,
                "amount", amount
        )), player);
    }

    private void sendToPlayer(JSONObject jsonMessage, Player player) {
        synchronized (playerToSessions) {
            playerToSessions.get(player).forEach(session -> session.getAsyncRemote().sendText(jsonMessage.toString()));
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(">> WEBSOCKET SESSION CLOSED.");

        // Remove the closed session
        var stopListeningToGameList = false;
        synchronized (gameListListeners) {
            gameListListeners.remove(session);

            if (gameListListeners.isEmpty()) {
                GAME_RESOURCES.removeAddedAndRemovedGamesListener(this);
                stopListeningToGameList = true;
            }
        }

        synchronized (gameInfoListeners) {
            var gamesToStopListeningTo = new ArrayList<GameResource>();

            gameInfoListeners.forEach((game, listeners) -> {
                listeners.remove(session);

                if (listeners.isEmpty()) {
                    gamesToStopListeningTo.add(game);
                }
            });

            gamesToStopListeningTo.forEach(game -> {
                gameInfoListeners.remove(game);

                if (gameListListeners.isEmpty()) {
                    game.removeChangeListener(this);
                }
            });

            if (stopListeningToGameList) {
                GAME_RESOURCES.getGames().stream()
                        .filter(game -> !gameInfoListeners.containsKey(game))
                        .forEach(game -> game.removeChangeListener(this));
            }
        }

        synchronized (ChatManager.class) {
            synchronized (chatRoomListeners) {
                var roomsToStopListeningTo = new ArrayList<String>();

                chatRoomListeners.forEach((chatRoom, listeners) -> {
                    listeners.remove(session);

                    if (listeners.isEmpty()) {
                        roomsToStopListeningTo.add(chatRoom);
                    }
                });

                roomsToStopListeningTo.forEach(roomId -> {
                    ChatManager.removeMessageListenerForRoom(roomId, this);

                    if (roomId.startsWith("game-")) {
                        var gameResource = (GameResource) idManager.getObject(roomId.substring(5));

                        if (gameResource != null) {
                            ChatManager.removeMessageListenerForGame(gameResource.getGameMap(), this);
                        }
                    }
                });
            }
        }

        synchronized (playerListeners) {
            var playersToRemove = new ArrayList<Player>();

            playerListeners.forEach((player, listeners) -> {
                listeners.remove(session);

                if (listeners.isEmpty()) {
                    playersToRemove.add(player);
                }
            });

            playersToRemove.forEach(player -> {
                playerListeners.remove(player);
                player.removePlayerChangeListener(this);
            });
        }

        synchronized (statisticsListeners) {
            var mapsToRemove = new ArrayList<GameMap>();

            statisticsListeners.forEach((map, listeners) -> {
                listeners.remove(session);

                if (listeners.isEmpty()) {
                    mapsToRemove.add(map);
                }
            });

            mapsToRemove.forEach(map -> {
                statisticsListeners.remove(map);
                map.getStatisticsManager().removeListener(this);
            });
        }

        synchronized (detailedMonitoringIds) {
            var ids = detailedMonitoringIds.remove(session);

            if (ids != null) {
                ids.forEach(id -> {
                    var object = idManager.getObject(id);

                    switch (object) {
                        case Building building -> building.getPlayer().removeDetailedMonitoring(building);
                        case Flag flag -> flag.getPlayer().removeDetailedMonitoring(flag);
                        default -> {
                        }
                    }
                });
            }
        }

        var player = (Player) null;
        synchronized (sessionToPlayer) {
            player = sessionToPlayer.remove(session);
        }

        Set<Player> playersToStopListeningTo;

        synchronized (chatPlayerListeners) {
            playersToStopListeningTo = chatPlayerListeners.remove(session);
        }

        if (playersToStopListeningTo != null) {
            synchronized (ChatManager.class) {
                playersToStopListeningTo.forEach(
                        playerToStopListeningTo ->
                                ChatManager.removeMessageListenerForPlayer(playerToStopListeningTo, this)
                );
            }
        }

        synchronized (playerToSessions) {
            if (player != null) {
                var sessions = playerToSessions.get(player);

                if (sessions != null) {
                    sessions.remove(session);

                    if (sessions.isEmpty()) {
                        playerToSessions.remove(player);

                        player.stopMonitoringGameView(this);
                    }
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println(">> ERROR IN WEBSOCKET: " + throwable);
        System.out.println(throwable.getCause());
        System.out.println(Arrays.asList(throwable.getCause().getStackTrace()));
        System.out.println(throwable.getMessage());
        System.out.println(Arrays.toString(throwable.getStackTrace()));

        // Cleaning up the session is handling in onClose
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println();
        System.out.println(">> WEBSOCKET SESSION OPENED.");
    }

    void sendToSession(Session session, JSONObject jsonObject) {
        session.getAsyncRemote().sendText(jsonObject.toJSONString());
    }

    void sendToSession(Session session, JSONArray jsonArray) {
        session.getAsyncRemote().sendText(jsonArray.toJSONString());
    }

    /**
     * Called when the view of the game is changed for the given player.
     * <p>
     * LOCKS HELD: map
     *
     * @param player
     * @param gameChangesList
     */
    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {
        // Note: This will be called when the gameTicker runs map.stepTime() and synchronizes on the map.
        //       No part of gameMonitoringEventsToJson can use synchronization - this will cause a deadlock.

        synchronized (playerToSessions) {
            playerToSessions.get(player).forEach(session -> {
                try {
                    sendToSession(session, new JSONObject(Map.of(
                            "type", "PLAYER_VIEW_CHANGED",
                            "playerViewChanges", jsonUtils.gameMonitoringEventToJson(gameChangesList, player)
                    )));
                } catch (InvalidUserActionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Called when the statistics are changed for the given building.
     * <p>
     * LOCKS HELD: map
     *
     * @param building
     */
    @Override
    public void buildingStatisticsChanged(Building building) {
        System.out.println(" >> BUILDING STATISTICS CHANGED");

        var map = building.getMap();
        var statisticsManager = map.getStatisticsManager();

        synchronized (statisticsListeners) {
            statisticsListeners.get(map).forEach(session -> sendToSession(
                    session,
                    new JSONObject(Map.of(
                            "type", "STATISTICS_CHANGED",
                            "statistics", jsonUtils.statisticsToJson(map.getTime(), building.getPlayer(), map.getPlayers(), statisticsManager)
                    ))
            ));
        }
    }

    /**
     * Called when general statistics change.
     * <p>
     * LOCKS HELD: map
     *
     * @param player
     */
    @Override
    public void generalStatisticsChanged(Player player) {
        System.out.println(" >> GENERAL STATISTICS CHANGED");

        var map = player.getMap();
        var statisticsManager = map.getStatisticsManager();

        synchronized (statisticsListeners) {
            statisticsListeners.get(map).forEach(session -> sendToSession(
                    session,
                    new JSONObject(Map.of(
                            "type", "STATISTICS_CHANGED",
                            "statistics", jsonUtils.statisticsToJson(map.getTime(), player, map.getPlayers(), statisticsManager)
                    ))));
        }
    }

    /**
     * Called when the player information changes. E.g. color, name, etc.
     * <p>
     * LOCKS HELD: TBD
     * <p>
     * TODO: define locking rule for changing player settings. Should they be protected by lock on map or player?
     *
     * @param player
     */
    @Override
    public void onPlayerChanged(Player player) {
        System.out.println(" >> PLAYER_CHANGED");

        synchronized (playerListeners) {
            playerListeners.get(player).forEach(session -> sendToSession(
                    session,
                    new JSONObject(Map.of(
                            "type", "PLAYER_CHANGED",
                            "player", jsonUtils.playerToJson(player)
                    ))));
        }
    }
}
