package org.appland.settlers.rest.resource;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.chat.ChatManager;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Soldier;
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
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.rest.GameTicker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.appland.settlers.rest.resource.GameResources.GAME_RESOURCES;

@ServerEndpoint(value = "/ws/monitor/games")
public class WebsocketMonitor implements PlayerGameViewMonitor,
        GameResources.GameListListener,
        GameResource.GameResourceListener,
        ChatManager.ChatListener {
    private final Map<Player, Session> playerToSession;
    private final Utils utils;
    private final JSONParser parser;
    private final IdManager idManager = IdManager.idManager;
    private final GameTicker gameTicker = GameTicker.GAME_TICKER;
    private final Collection<Session> gameListListeners = new HashSet<>();
    private final Map<GameResource, Collection<Session>> gameInfoListeners = new HashMap<>();
    private final Map<String, Collection<Session>> chatRoomListeners = new HashMap<>();

    public WebsocketMonitor() {
        System.out.println("CREATED NEW WEBSOCKET MONITOR");

        utils = new Utils(idManager);
        parser = new JSONParser();
        playerToSession = new HashMap<>();

        GAME_RESOURCES.addAddedAndRemovedGamesListener(this);
    }

    @Override
    public void newMessageForPlayer(ChatManager.ChatMessage chatMessage, Player player) {
        System.out.println("ON NEW MESSAGE FOR PLAYER");

        sendToPlayer(new JSONObject(Map.of(
                        "type", "NEW_CHAT_MESSAGES",
                        "chatMessage", utils.chatMessageToPlayerToJson(chatMessage, player)
                )),
                player);
    }

    @Override
    public void newMessageForRoom(ChatManager.ChatMessage chatMessage, String roomId) {
        System.out.println("ON NEW MESSAGE FOR ROOM");

        System.out.println(chatRoomListeners.get(roomId));

        chatRoomListeners.get(roomId).forEach(session -> sendToSession(session,
                new JSONObject(Map.of(
                        "type", "NEW_CHAT_MESSAGES",
                        "chatMessage", utils.chatMessageToRoomToJson(chatMessage, roomId)
                ))));
    }

    @Override
    public void onGameResourceChanged(GameResource gameResource) {
        System.out.println();
        System.out.println("ON GAME RESOURCE CHANGED");

        if (gameInfoListeners.containsKey(gameResource)) {
            gameInfoListeners.get(gameResource).forEach(session -> sendToSession(session,
                    new JSONObject(Map.of(
                            "type", "GAME_INFO_CHANGED",
                            "gameInformation", utils.gameToJson(gameResource)
                    ))));
        }

        gameListListeners.forEach(session -> sendToSession(session,
                new JSONObject(Map.of(
                        "type", "GAME_LIST_CHANGED",
                        "games", utils.gamesToJson(GAME_RESOURCES.getGames())
                ))));
    }

    @Override
    public void onGameListChanged(Collection<GameResource> games) {
        System.out.println();
        System.out.println("ON GAME LIST CHANGED");

        gameListListeners.forEach(session -> sendToSession(session,
                new JSONObject(Map.of(
                        "type", "GAME_LIST_CHANGED",
                        "games", utils.gamesToJson(games)
                ))));

        if (!gameListListeners.isEmpty()) {
            games.forEach(gameResource -> gameResource.addChangeListener(this));
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("\nON MESSAGE: " + message);

        var player = (Player) session.getUserProperties().get("PLAYER");
        var game = (GameResource) session.getUserProperties().get("GAME");
        var map = player == null ? null : player.getMap();

        JSONObject jsonBody;

        try {
            jsonBody = (JSONObject) parser.parse(message);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Command command = Command.valueOf((String) jsonBody.get("command"));

        switch (command) {
            case GET_CHAT_HISTORY_FOR_ROOM -> {
                var roomId = (String) jsonBody.get("roomId");

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "chatHistory", utils.chatMessagesToRoomToJson(ChatManager.getChatHistoryForRoom(roomId), roomId)
                        )));
            }
            case LISTEN_TO_CHAT_MESSAGES -> {
                if (jsonBody.containsKey("playerId")) {
                    ChatManager.addMessageListenerForPlayer((Player) idManager.getObject((String) jsonBody.get("playerId")), this);
                }

                if (jsonBody.containsKey("roomIds")) {
                    ((JSONArray) jsonBody.get("roomIds"))
                            .forEach(roomId -> {
                                ChatManager.addMessageListenerForRoom((String) roomId, this);

                                if (!chatRoomListeners.containsKey(roomId)) {
                                    chatRoomListeners.put((String) roomId, new HashSet<>());
                                }

                                chatRoomListeners.get((String) roomId).add(session);
                            });
                }
            }
            case SEND_CHAT_MESSAGE_TO_ROOM -> {
                ChatManager.sendChatToRoom(
                        (String) jsonBody.get("roomId"),
                        (String) jsonBody.get("text"),
                        (Player) idManager.getObject((String) jsonBody.get("from"))
                );
            }
            case SET_GAME -> {
                var gameToSet = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                session.getUserProperties().put("GAME", gameToSet);

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "gameInformation", utils.gameToJson(gameToSet)
                        )));
            }
            case SET_SELF_PLAYER -> {
                var playerToSet = (Player) idManager.getObject((String) jsonBody.get("playerId"));

                session.getUserProperties().put("PLAYER", playerToSet);

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "playerInformation", utils.playerToJson(playerToSet)
                        )));
            }
            case LISTEN_TO_GAME_LIST -> {
                gameListListeners.add(session);

                if (gameListListeners.size() == 1) {
                    GAME_RESOURCES.addAddedAndRemovedGamesListener(this);
                }

                GAME_RESOURCES.getGames().forEach(gameResource -> gameResource.addChangeListener(this));
            }
            case STOP_LISTENING_TO_GAME_LIST -> {
                gameListListeners.remove(session);

                if (gameListListeners.isEmpty()) {
                    GAME_RESOURCES.removeAddedAndRemovedGamesListener(this);

                    GAME_RESOURCES.getGames().stream()
                            .filter(gameResource -> !gameInfoListeners.containsKey(gameResource))
                            .forEach(gameResource -> gameResource.removeChangeListener(this));
                }
            }
            case LISTEN_TO_GAME_INFO -> {
                if (!gameInfoListeners.containsKey(game)) {
                    gameInfoListeners.put(game, new HashSet<>());
                }

                gameInfoListeners.get(game).add(session);

                game.addChangeListener(this);

                session.getUserProperties().put("GAME", game);

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "gameInformation", utils.gameToJson(game)
                        )));
            }
            case START_MONITORING_GAME -> {
                player.monitorGameView(this);

                playerToSession.put(player, session);

                if (map != null) {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "playerView", utils.playerViewToJson(map, player, game)
                            )));
                } else {
                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId")
                            )));
                }
            }
            case STOP_LISTENING_TO_GAME_INFO -> {
                gameInfoListeners.get(game).remove(session);

                if (gameInfoListeners.get(game).isEmpty()) {
                    game.removeChangeListener(this);
                }
            }
            case CREATE_GAME -> {
                var newGame = new GameResource(utils);

                if (jsonBody.containsKey("players")) {
                    newGame.setPlayers(
                            utils.jsonToPlayers((JSONArray) jsonBody.get("players"))
                    );
                }

                if (jsonBody.containsKey("name")) {
                    newGame.setName((String) jsonBody.get("name"));
                }

                session.getUserProperties().put("GAME", newGame);
                GAME_RESOURCES.addGame(newGame);

                sendToSession(session, new JSONObject(Map.of(
                        "requestId", jsonBody.get("requestId"),
                        "gameInformation", utils.gameToJson(newGame)
                )));
            }
            case GET_MAPS -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "maps", utils.mapFilesToJson(MapsResource.mapsResource.getMaps())
                        )));
            }
            case GET_GAMES -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "games", utils.gamesToJson(GAME_RESOURCES.getGames())
                        )));
            }
            case UPDATE_PLAYER -> {
                var playerId = (String) jsonBody.get("playerId");
                var playerToUpdate = (Player) idManager.getObject(playerId);
                var name = (String) jsonBody.get("name");
                var playerColor = (PlayerColor) PlayerColor.valueOf((String) jsonBody.get("color"));
                var nation = (Nation) Nation.valueOf((String) jsonBody.get("nation"));

                synchronized (playerToUpdate) {
                    playerToUpdate.setName(name);
                    playerToUpdate.setPlayerColor(playerColor);
                    playerToUpdate.setNation(nation);

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "playerInformation", utils.playerToJson(playerToUpdate))
                            ));
                }
            }
            case REMOVE_PLAYER -> {
                var playerId = (String) jsonBody.get("playerId");
                var playerToRemove = (Player) idManager.getObject(playerId);

                synchronized (game) {
                    game.removePlayer(playerToRemove);
                }
            }
            case CREATE_PLAYER -> {
                var name = (String) jsonBody.get("name");
                var playerColor = PlayerColor.valueOf((String) jsonBody.get("color"));
                var nation = Nation.valueOf((String) jsonBody.get("nation"));
                var playerType = PlayerType.valueOf((String) jsonBody.get("type"));

                var newPlayer = new Player(name, playerColor);

                newPlayer.setNation(nation);
                newPlayer.setPlayerType(playerType);

                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "playerInformation", utils.playerToJson(newPlayer))
                        ));
            }
            case ADD_PLAYER_TO_GAME -> {
                var playerToAdd = (Player) idManager.getObject((String) jsonBody.get("playerId"));
                var gameToAddPlayerTo = (GameResource) idManager.getObject((String) jsonBody.get("gameId"));

                synchronized (gameToAddPlayerTo) {
                    if (playerToAdd.getPlayerType() == PlayerType.COMPUTER) {
                        gameToAddPlayerTo.addComputerPlayer(playerToAdd);
                    } else {
                        gameToAddPlayerTo.addHumanPlayer(playerToAdd);
                    }

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "gameInformation", utils.gameToJson(gameToAddPlayerTo)
                            )));
                }
            }

            case SET_OTHERS_CAN_JOIN -> {
                var othersCanJoin = (Boolean) jsonBody.get("othersCanJoin");

                synchronized (game) {
                    game.setOthersCanJoin(othersCanJoin);

                    sendToSession(session,
                            new JSONObject(Map.of(
                                    "requestId", jsonBody.get("requestId"),
                                    "gameInformation", utils.gameToJson(game)
                            )));
                }
            }
            case START_GAME -> {
                synchronized (game) {
                    utils.startGame(game, gameTicker);
                }
            }
            case SET_MAP -> {
                synchronized (game) {
                    game.setMap((MapFile) idManager.getObject((String) jsonBody.get("mapId")));
                }
            }
            case SET_INITIAL_RESOURCES -> {
                ResourceLevel resourceLevel = ResourceLevel.valueOf((String) jsonBody.get("resources"));

                synchronized (game) {
                    game.setResource(resourceLevel);
                }
            }
            case SET_GAME_NAME -> {
                synchronized (game) {
                    game.setName((String) jsonBody.get("name"));
                }
            }
            case GET_GAME_INFORMATION -> {
                sendToSession(session,
                        new JSONObject(Map.of(
                                "requestId", jsonBody.get("requestId"),
                                "gameInformation", utils.gameToJson(game)
                        )));
            }
            case UPGRADE -> {
                Building building = (Building) idManager.getObject((String) jsonBody.get("houseId"));

                synchronized (map) {
                    building.upgrade();
                }
            }
            case FLAG_DEBUG_INFORMATION -> {
                String flagId = (String) jsonBody.get("flagId");
                Flag flag = (Flag) idManager.getObject(flagId);

                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "flag", utils.flagToDebugJson(flag)
                    )));
                }
            }
            case GET_SOLDIERS_AVAILABLE_FOR_ATTACK -> {
                synchronized (map) {
                    int amount = player.getAmountOfSoldiersAvailableForAttack();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case GET_POPULATE_MILITARY_FAR_FROM_BORDER -> {
                synchronized (map) {
                    int amount = player.getAmountOfSoldiersWhenPopulatingFarFromBorder();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case GET_POPULATE_MILITARY_CLOSER_TO_BORDER -> {
                synchronized (map) {
                    int amount = player.getAmountOfSoldiersWhenPopulatingAwayFromBorder();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case GET_POPULATE_MILITARY_CLOSE_TO_BORDER -> {
                synchronized (map) {
                    int amount = player.getAmountOfSoldiersWhenPopulatingCloseToBorder();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case SET_SOLDIERS_AVAILABLE_FOR_ATTACK -> {
                var amount = ((Long) jsonBody.get("amount")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersAvailableForAttack(amount);
                }
            }
            case SET_MILITARY_POPULATION_CLOSE_TO_BORDER -> {
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingCloseToBorder(amount);
                }
            }
            case SET_MILITARY_POPULATION_CLOSER_TO_BORDER -> {
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingAwayFromBorder(amount);
                }
            }
            case SET_MILITARY_POPULATION_FAR_FROM_BORDER -> {
                var amount = ((Long) jsonBody.get("population")).intValue();

                synchronized (map) {
                    player.setAmountOfSoldiersWhenPopulatingFarFromBorder(amount);
                }
            }
            case SET_GAME_SPEED -> {
                GameSpeed gameSpeed = GameSpeed.valueOf((String) jsonBody.get("speed"));

                game.setGameSpeed(gameSpeed);

                int tick = switch (gameSpeed) {
                    case FAST -> 100;
                    case NORMAL -> 200;
                    case SLOW -> 400;
                };

                // TODO: should be handled through the regular game listener somehow?
                var jsonNewTick = new JSONObject(Map.of(
                        "tick", tick,
                        "gameSpeed", gameSpeed.name().toUpperCase()
                ));

                game.getPlayers().forEach(receiver -> {
                    if (playerToSession.containsKey(receiver)) {
                        sendToPlayer(jsonNewTick, receiver);
                    }
                });
            }
            case GET_MILITARY_SETTINGS -> {
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
                synchronized (map) {
                    int amount = player.getDefenseFromSurroundingBuildings();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case SET_DEFENSE_FROM_SURROUNDING_BUILDINGS -> {
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setDefenseFromSurroundingBuildings(strength);
                }
            }
            case GET_DEFENSE_STRENGTH -> {
                synchronized (map) {
                    int amount = player.getDefenseStrength();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case SET_DEFENSE_STRENGTH -> {
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setDefenseStrength(strength);
                }
            }
            case GET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING -> {
                synchronized (map) {
                    int amount = player.getStrengthOfSoldiersPopulatingBuildings();

                    sendAmountReplyToPlayer(amount, player, jsonBody);
                }
            }
            case SET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING -> {
                int strength = ((Long) jsonBody.get("strength")).intValue();

                synchronized (map) {
                    player.setStrengthOfSoldiersPopulatingBuildings(strength);
                }
            }
            case PAUSE_GAME -> {
                synchronized (game) {
                    game.setStatus(GameStatus.PAUSED);
                }
            }
            case RESUME_GAME -> {
                synchronized (game) {
                    game.setStatus(GameStatus.STARTED);
                }
            }
            case SET_IRON_BAR_QUOTAS -> {
                Long armoryAmount = (Long) jsonBody.get("armory");
                Long metalworksAmount = (Long) jsonBody.get("metalworks");

                synchronized (map) {
                    player.setIronBarQuota(Armory.class, armoryAmount.intValue());
                    player.setIronBarQuota(Metalworks.class, metalworksAmount.intValue());
                }
            }
            case GET_IRON_BAR_QUOTAS -> {
                synchronized (map) {
                    sendToSession(session, new JSONObject(Map.of(
                            "requestId", jsonBody.get("requestId"),
                            "armory", player.getIronBarQuota(Armory.class),
                            "metalworks", player.getIronBarQuota(Metalworks.class)
                    )));
                }
            }
            case GET_WATER_QUOTAS -> {
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
                Long donkeyFarmAmount = (Long) jsonBody.get("donkeyFarm");
                Long pigFarmAmount = (Long) jsonBody.get("pigFarm");
                Long bakeryAmount = (Long) jsonBody.get("bakery");
                Long breweryAmount = (Long) jsonBody.get("brewery");

                synchronized (map) {
                    player.setWaterQuota(DonkeyFarm.class, donkeyFarmAmount.intValue());
                    player.setWaterQuota(PigFarm.class, pigFarmAmount.intValue());
                    player.setWaterQuota(Bakery.class, bakeryAmount.intValue());
                    player.setWaterQuota(Brewery.class, breweryAmount.intValue());
                }
            }
            case SET_WHEAT_QUOTAS -> {
                Long donkeyFarmAmount = (Long) jsonBody.get("donkeyFarm");
                Long pigFarmAmount = (Long) jsonBody.get("pigFarm");
                Long millAmount = (Long) jsonBody.get("mill");
                Long breweryAmount = (Long) jsonBody.get("brewery");

                synchronized (map) {
                    player.setWheatQuota(DonkeyFarm.class, donkeyFarmAmount.intValue());
                    player.setWheatQuota(PigFarm.class, pigFarmAmount.intValue());
                    player.setWheatQuota(Mill.class, millAmount.intValue());
                    player.setWheatQuota(Brewery.class, breweryAmount.intValue());
                }
            }
            case GET_FOOD_QUOTAS -> {
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
                Long ironMineAmount = (Long) jsonBody.get("ironMine");
                Long coalMineAmount = (Long) jsonBody.get("coalMine");
                Long goldMineAmount = (Long) jsonBody.get("goldMine");
                Long graniteMineAmount = (Long) jsonBody.get("graniteMine");

                synchronized (map) {
                    player.setFoodQuota(IronMine.class, ironMineAmount.intValue());
                    player.setFoodQuota(CoalMine.class, coalMineAmount.intValue());
                    player.setFoodQuota(GoldMine.class, goldMineAmount.intValue());
                    player.setFoodQuota(GraniteMine.class, graniteMineAmount.intValue());
                }
            }
            case SET_COAL_QUOTAS -> {
                Long mintAmount = (Long) jsonBody.get("mint");
                Long armoryAmount = (Long) jsonBody.get("armory");
                Long ironSmelterAmount = (Long) jsonBody.get("ironSmelter");

                synchronized (map) {
                    player.setCoalQuota(Mint.class, mintAmount.intValue());
                    player.setCoalQuota(Armory.class, armoryAmount.intValue());
                    player.setCoalQuota(IronSmelter.class, ironSmelterAmount.intValue());
                }
            }
            case REMOVE_MESSAGES -> {
                synchronized (player.getMap()) {
                    for (var messageId : (JSONArray) jsonBody.get("messageIds")) {
                        Message gameMessage = (Message) idManager.getObject((String) messageId);

                        player.removeMessage(gameMessage);
                    }
                }
            }
            case REMOVE_MESSAGE -> {
                String messageId = (String) jsonBody.get("messageId");

                Message gameMessage = (Message) idManager.getObject(messageId);

                synchronized (player.getMap()) {
                    player.removeMessage(gameMessage);
                }
            }
            case START_DETAILED_MONITORING -> {
                String id = (String) jsonBody.get("id");
                Object object = idManager.getObject(id);

                var jsonUpdate = new JSONObject();

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
            case STOP_DETAILED_MONITORING -> {
                String buildingId = (String) jsonBody.get("buildingId");
                Building building = (Building) idManager.getObject(buildingId);

                synchronized (map) {
                    player.removeDetailedMonitoring(building);
                }
            }
            case SET_RESERVED_IN_HEADQUARTERS -> {
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
            }
            case INFORMATION_ON_POINTS -> {
                JSONArray jsonPointsInformation = new JSONArray();

                List<Point> points = utils.jsonToPoints((JSONArray) jsonBody.get("points"));

                synchronized (map) {
                    for (Point point : points) {
                        jsonPointsInformation.add(utils.pointToDetailedJson(point, player, map));
                    }
                }

                sendToSession(session, new JSONObject(Map.of(
                        "requestId", jsonBody.get("requestId"),
                        "pointsWithInformation", jsonPointsInformation
                )));
            }
            case FULL_SYNC -> {
                switch (game.status) {
                    case STARTED, PAUSED -> {
                        synchronized (map) {
                            sendToSession(session,
                                    new JSONObject(Map.of(
                                            "requestId", jsonBody.get("requestId"),
                                            "gameInformation", utils.gameToJson(game),
                                            "playerView", utils.playerViewToJson(map, player, game)
                                    )));
                        }
                    }
                    case NOT_STARTED -> {
                        synchronized (game) {
                            sendToSession(session,
                                    new JSONObject(Map.of(
                                            "requestId", jsonBody.get("requestId"),
                                            "gameInformation", utils.gameToJson(game)
                                    )));
                        }
                    }
                }
            }
            case CALL_SCOUT -> {
                JSONObject jsonPoint = (JSONObject) jsonBody.get("point");
                Point point = utils.jsonToPoint(jsonPoint);

                synchronized (map) {
                    Flag flag = map.getFlagAtPoint(point);

                    flag.callScout();
                }
            }
            case CALL_GEOLOGIST -> {
                JSONObject jsonPoint = (JSONObject) jsonBody.get("point");
                Point point = utils.jsonToPoint(jsonPoint);

                synchronized (map) {
                    Flag flag = map.getFlagAtPoint(point);

                    flag.callGeologist();
                }
            }
            case PLACE_BUILDING -> {
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
            case PLACE_ROAD -> {
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
            case PLACE_FLAG -> {
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
            case PLACE_FLAG_AND_ROAD -> {
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
            case REMOVE_ROAD -> {
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
            case REMOVE_FLAG -> {
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
            case REMOVE_BUILDING -> {
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
            default -> throw new RuntimeException("Message contains unknown command: " + message);
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

        /* Remove the closed session */
        gameListListeners.remove(session);
        gameInfoListeners.forEach((game, listeners) -> listeners.remove(session));
        chatRoomListeners.forEach((chatRoom, listeners) -> listeners.remove(session));

        Player player = (Player) session.getUserProperties().get("PLAYER");

        if (player != null) {
            System.out.println("Removing session for player: " + player);
            playerToSession.remove(player);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("ON ERROR: " + throwable);
        System.out.println(throwable.getCause());
        System.out.println(throwable.getMessage());
        System.out.println(Arrays.toString(throwable.getStackTrace()));

        /* Remove the error session */
        gameListListeners.remove(session);
        gameInfoListeners.forEach((game, listeners) -> listeners.remove(session));
        chatRoomListeners.forEach((chatRoom, listeners) -> listeners.remove(session));

        Player player = (Player) session.getUserProperties().get("PLAYER");

        if (player != null) {
            System.out.println("Removing session for player: " + player);
            playerToSession.remove(player);
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        System.out.println();
        System.out.println("Websocket session opened");
    }

    void sendToSession(Session session, JSONObject jsonObject) {
        session.getAsyncRemote().sendText(jsonObject.toJSONString());
    }

    void sendToSession(Session session, JSONArray jsonArray) {
        session.getAsyncRemote().sendText(jsonArray.toJSONString());
    }

    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {
        try {
            Session session = playerToSession.get(player);

            if (session != null) {
                sendToSession(session, new JSONObject(Map.of(
                        "type", "PLAYER_VIEW_CHANGED",
                        "playerViewChanges", utils.gameMonitoringEventsToJson(gameChangesList, player)
                )));
            }
        } catch (Exception e) {
            System.out.println("Exception while sending updates to frontend: " + e);
            e.printStackTrace();
        }
    }
}