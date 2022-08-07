package org.appland.settlers.rest.resource;

import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.BuildingCapturedMessage;
import org.appland.settlers.model.BuildingLostMessage;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GeologistFindMessage;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.LandDataPoint;
import org.appland.settlers.model.LandStatistics;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Message;
import org.appland.settlers.model.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.NoMoreResourcesMessage;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.ProductionDataPoint;
import org.appland.settlers.model.ProductionDataSeries;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.StatisticsManager;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.StoreHouseIsReadyMessage;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.UnderAttackMessage;
import org.appland.settlers.model.WildAnimal;
import org.appland.settlers.model.Worker;
import org.appland.settlers.rest.GameTicker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Message.MessageType.BUILDING_CAPTURED;
import static org.appland.settlers.model.Message.MessageType.BUILDING_LOST;
import static org.appland.settlers.model.Message.MessageType.GEOLOGIST_FIND;
import static org.appland.settlers.model.Message.MessageType.MILITARY_BUILDING_OCCUPIED;
import static org.appland.settlers.model.Message.MessageType.MILITARY_BUILDING_READY;
import static org.appland.settlers.model.Message.MessageType.NO_MORE_RESOURCES;
import static org.appland.settlers.model.Message.MessageType.STORE_HOUSE_IS_READY;
import static org.appland.settlers.model.Message.MessageType.UNDER_ATTACK;

@Path("/settlers/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SettlersAPI {

    public final static String MAP_FILE_LIST = "mapFileList";
    public static final String GAME_TICKER = "gameTicker";

    public static final List<Material> PRODUCTION_STATISTICS_MATERIALS = Arrays.asList(
            WOOD,
            STONE,
            PLANK,
            COIN,
            GOLD,
            SWORD,
            SHIELD
    );

    @Context
    ServletContext context;

    private final IdManager idManager = IdManager.idManager;
    private final Utils utils;
    private final List<GameResource> startedGames;
    private final JSONParser parser;
    private final List<GameResource> gameResources;

    public SettlersAPI() {
        startedGames = new ArrayList<>();

        utils = new Utils(idManager);

        parser = new JSONParser();
        gameResources = new ArrayList<>();
    }

    @GET
    @Path("/maps")
    public Response getMaps() {

        /* Get the list of map files from the servlet context */
        List<MapFile> mapFiles = (List<MapFile>) context.getAttribute(MAP_FILE_LIST);

        /* Return the list of map files as JSON documents */
        return Response.status(200).entity(utils.mapFilesToJson(mapFiles).toJSONString()).build();
    }

    @GET
    @Path("/maps/{mapId}")
    public Response getMap(@PathParam("mapId") String mapId) {
        MapFile mapFile = (MapFile) idManager.getObject(mapId);

        if (mapFile == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No map with id %s exists", mapId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        return Response.status(200).entity(utils.mapFileToJson(mapFile).toJSONString()).build();
    }

    @GET
    @Path("/maps/{mapId}/terrain")
    public Response getTerrainForMap(@PathParam("mapId") String mapId) throws Exception {
        MapFile mapFile = (MapFile)idManager.getObject(mapId);

        if (mapFile == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No map with id %s exists", mapId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        return Response.status(200).entity(utils.mapFileTerrainToJson(mapFile).toJSONString()).build();
    }

    @GET
    @Path("/games")
    public Response getStartedGames() {
        List<GameMap> startedGameMaps = new ArrayList<>();
        List<GameResource> notStartedGames = new ArrayList<>();

        for (GameResource gameResource : gameResources) {

            if (gameResource.isStarted()) {
                startedGameMaps.add(gameResource.getMap());
            } else {
                notStartedGames.add(gameResource);
            }
        }

        JSONArray jsonGames = utils.gamesToJson(startedGameMaps);

        jsonGames.addAll(utils.gamePlaceholdersToJson(notStartedGames));

        return Response.status(200).entity(jsonGames.toJSONString()).build();
    }

    @DELETE
    @Path("/maps/{mapId}")
    public Response deleteMap(@PathParam("mapId") String mapId) {

        JSONObject message = new JSONObject();

        message.put("status", "Error");
        message.put("message", "Cannot delete maps.");

        return Response.status(405).entity(message.toJSONString()).build();
    }

    @GET
    @Path("/games/{id}")
    public Response getGame(@PathParam("id") String gameId) {

        Object gameObject = idManager.getObject(gameId);

        /* Return 404 if the game doesn't exist */

        if (gameObject == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Return the game as a JSON document */
        if (gameObject instanceof GameMap) {
            JSONObject jsonGame = utils.gameToJson((GameMap)gameObject);

            return Response.status(200).entity(jsonGame.toJSONString()).build();
        } else {
            JSONObject jsonGame = utils.gamePlaceholderToJson((GameResource)gameObject);

            return Response.status(200).entity(jsonGame.toJSONString()).build();
        }
    }

    @POST
    @Path("/games")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGame(String body) throws Exception {

        /* Return 400 (bad request) if the body is empty */
        if (body.equals("")) {
            return Response.status(400).build();
        }

        JSONObject jsonGame = (JSONObject) parser.parse(body);

        /* Create a real game instance if the game is immediately started */
        if (jsonGame.containsKey("status") && jsonGame.get("status").equals("STARTED")) {

            return null;
        } else {

            /* Create a placeholder if there are missing attributes */
            GameResource gameResource = new GameResource(utils);

            if (jsonGame.containsKey("name")) {
                gameResource.setName((String) jsonGame.get("name"));
            }

            if (jsonGame.containsKey("players")) {
                gameResource.setPlayers(utils.jsonToPlayers((JSONArray) jsonGame.get("players")));
            }

            if (jsonGame.containsKey("mapId")) {
                String mapId = (String) jsonGame.get("mapId");

                gameResource.setMap((MapFile) idManager.getObject(Integer.parseInt(mapId)));
            }

            gameResources.add(gameResource);

            return Response.status(201).entity(utils.gamePlaceholderToJson(gameResource).toJSONString()).build();
        }
    }

    @PATCH
    @Path("/games/{gameId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyGame(@PathParam("gameId") String gameId, String body) throws Exception {
        Object gameObject = idManager.getObject(gameId);

        if (gameObject == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonUpdates = (JSONObject) parser.parse(body);

        if (jsonUpdates.containsKey("mapId") && gameObject instanceof GameResource) {
            String updatedMapFileId = (String) jsonUpdates.get("mapId");

            MapFile updatedMapFile = (MapFile) idManager.getObject(updatedMapFileId);

            GameResource gamePlaceholder = (GameResource) gameObject;

            gamePlaceholder.setMap(updatedMapFile);

            return Response.status(200).entity(utils.gamePlaceholderToJson(gamePlaceholder).toJSONString()).build();
        }

        if (jsonUpdates.containsKey("status") && gameObject instanceof GameResource) {
            String updatedStatus = (String) jsonUpdates.get("status");

            GameResource gameResource = (GameResource) gameObject;

            if (updatedStatus.equals("STARTED")) {

                /* Create the game map */
                gameResource.createGameMap();
                GameMap map = gameResource.getMap();

                startedGames.add(gameResource);

                idManager.updateObject(gameObject, gameResource.getMap());

                /* Limit the amount of wild animals to make performance bearable -- temporary! */
                List<WildAnimal> wildAnimals = map.getWildAnimals();
                List<WildAnimal> reducedWildAnimals = new ArrayList<>(wildAnimals);

                if (reducedWildAnimals.size() > 10) {
                    reducedWildAnimals = reducedWildAnimals.subList(0, 10);
                }

                wildAnimals.clear();

                wildAnimals.addAll(reducedWildAnimals);

                /* Place a headquarter for each player */
                List<Player> players = map.getPlayers();
                List<Point> startingPoints = map.getStartingPoints();

                for (int i = 0; i < startingPoints.size(); i++) {

                    if (i == players.size()) {
                        break;
                    }

                    map.placeBuilding(new Headquarter(players.get(i)), startingPoints.get(i));

                    try {
                        map.placeDeadTree(startingPoints.get(i).right().right());
                    } catch (Throwable t) {

                    }
                }

                /* Adjust the initial set of resources */
                utils.adjustResources(map, gameResource.getResources());

                /* Start the time for the game by adding it to the game ticker */
                GameTicker gameTicker = (GameTicker) context.getAttribute(GAME_TICKER);

                gameTicker.startGame(gameResource);

                return Response.status(200).entity(utils.gameToJson(map).toJSONString()).build();
            }

            return Response.status(400).build();  // Add a bad request message
        }

        if (jsonUpdates.containsKey("resources") && gameObject instanceof GameResource) {
            ResourceLevel level = ResourceLevel.valueOf((String) jsonUpdates.get("resources"));

            GameResource gamePlaceholder = (GameResource) gameObject;

            gamePlaceholder.setResource(level);

            return Response.status(200).entity(utils.gamePlaceholderToJson(gamePlaceholder).toJSONString()).build();
        }

        /* Return bad request (400) if there is no mapFileId included */
        return Response.status(400).build(); // The scope of this is all changes, not only mapId
    }

    @DELETE
    @Path("/games/{gameId}")
    public Response deleteGame(@PathParam("gameId") String gameId) {
        Object gameObject = idManager.getObject(gameId);


        if (gameObject == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (gameObject instanceof GameResource) {
            gameResources.remove(gameObject);
        } else {
            startedGames.remove(gameObject); // TODO: type is wrong - startedGames is GameResources, gameObject is GameMap
        }

        /* Free up the id */
        idManager.remove(gameObject);

        return Response.status(200).build();
    }

    @GET
    @Path("/games/{gameId}/players")
    public Response getPlayersForGame(@PathParam("gameId") String gameId) {
        Object gameObject = idManager.getObject(gameId);

        if (gameObject == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (gameObject instanceof GameResource) {
            GameResource gamePlaceholder = (GameResource) gameObject;

            return Response.status(200).entity(utils.playersToJson(gamePlaceholder.getPlayers()).toJSONString()).build();
        } else {
            GameMap map = (GameMap) gameObject;

            JSONArray jsonPlayers = utils.playersToJson(map.getPlayers());

            return Response.status(200).entity(jsonPlayers.toJSONString()).build();
        }
    }

    @POST
    @Path("/games/{gameId}/players")
    public Response addPlayerToGame(@PathParam("gameId") String gameId, String playerBody) throws ParseException {
        Object gameObject = idManager.getObject(gameId);
        JSONObject jsonPlayer = (JSONObject) parser.parse(playerBody);

        if (gameObject == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        boolean isComputer = false;

        if (jsonPlayer.getOrDefault("type", "").equals("COMPUTER_PLAYER")) {
            isComputer = true;
        }

        Player player = utils.jsonToPlayer(jsonPlayer);

        if (gameObject instanceof GameResource) {
            GameResource gameResource = (GameResource) gameObject;

            if (isComputer) {
                gameResource.addComputerPlayer(player);
            } else {
                gameResource.addHumanPlayer(player);
            }
        }

        return Response.status(201).entity(utils.playerToJson(player).toJSONString()).build();
    }

    @PATCH
    @Path("/games/{gameId}/players/{playerId}")
    public Response updatePlayerInGame(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String body) throws ParseException {
        GameResource gamePlaceholder = (GameResource) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        JSONObject jsonUpdates = (JSONObject) parser.parse(body);

        if (gamePlaceholder == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!gamePlaceholder.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (jsonUpdates.containsKey("name")) {
            player.setName((String)jsonUpdates.get("name"));
        }

        if (jsonUpdates.containsKey("color")) {
            player.setColor(Color.decode((String)jsonUpdates.get("color")));
        }

        return Response.status(200).entity(utils.playerToJson(player).toJSONString()).build();
    }

    @DELETE
    @Path("/games/{gameId}/players/{playerId}")
    public Response removePlayerFromGame(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        if (gameResource == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!gameResource.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        gameResource.removePlayer(player);

        return Response.status(200).entity(utils.playerToJson(player).toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}")
    public Response getPlayerForGame(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {
        Player player = (Player) idManager.getObject(playerId);
        Object gameObject = idManager.getObject(gameId);

        if (gameObject == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (gameObject instanceof GameResource) {
            GameResource gameResource = (GameResource) gameObject;

            if (!gameResource.getPlayers().contains(player)) {
                JSONObject message = new JSONObject();

                message.put("status", "Error");
                message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

                return Response.status(404).entity(message.toJSONString()).build();
            }
        }

        if (gameObject instanceof GameMap) {
            GameMap map = (GameMap) gameObject;

            if (!map.getPlayers().contains(player)) {
                JSONObject message = new JSONObject();

                message.put("status", "Error");
                message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

                return Response.status(404).entity(message.toJSONString()).build();
            }
        }

        /* Return the player */
        return Response.status(200).entity(utils.playerToJson(player).toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/map/terrain")
    public Response getTerrainForMapInGame(@PathParam("gameId") String gameId) {
        GameMap map = (GameMap)idManager.getObject(gameId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject terrain;

        synchronized (map) {
            terrain = utils.terrainToJson(map);
        }

        return Response.status(200).entity(terrain.toJSONString()).build();
    }

    @PUT
    @Path("/games/{gameId}/map/points")
    public Response putPoint(@PathParam("gameId") String gameId, @QueryParam("x") int x, @QueryParam("y") int y, String body) throws Exception {
        Point point = new Point(x, y);
        GameMap map = (GameMap)idManager.getObject(gameId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonBody = (JSONObject) parser.parse(body);
        JSONObject response = new JSONObject();

        if (jsonBody.containsKey("geologistNeeded") &&
                (Boolean)jsonBody.get("geologistNeeded")) {

            synchronized (map) {
                map.getFlagAtPoint(point).callGeologist();
            }

            response.put("message", "Called geologist to " + point);
        } else if (jsonBody.containsKey("scoutNeeded") &&
                (Boolean)jsonBody.get("scoutNeeded")) {

            synchronized (map) {
                map.getFlagAtPoint(point).callScout();
            }

            response.put("message", "Called scout to " + point);
        }

        return Response.status(200).entity(response.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/map/points")
    public Response getPoint(@PathParam("gameId") String gameId, @QueryParam("playerId") String playerId, @QueryParam("x") int x, @QueryParam("y") int y) {
        Point point = new Point(x, y);
        GameMap map = (GameMap)idManager.getObject(gameId);
        Player player = (Player)idManager.getObject(playerId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonPoint = utils.pointToDetailedJson(point, player, map);

        return Response.status(200).entity(jsonPoint.toJSONString()).build();
    }

    @DELETE
    @Path("/games/{gameId}/players/{playerId}/flags/{flagId}")
    public Response removeFlag(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("flagId") int flagId) throws Exception {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        Flag flag = (Flag) idManager.getObject(flagId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!map.getFlags().contains(flag)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No flag with id %s exists in game with id %s", flagId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonResponse = new JSONObject();

        if (!player.equals(flag.getPlayer())) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", "Cannot remove flag from other player");

            return Response.status(404).entity(message.toJSONString()).build();
        }

        synchronized (map) {
            map.removeFlag(flag);
        }

        jsonResponse.put("message", "Flag removed");

        return Response.status(200).entity(jsonResponse.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/houses/{houseId}")
    public Response getHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("houseId") String houseId, @QueryParam("askingPlayerId") String askingPlayerId) {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        Building building = (Building) idManager.getObject(houseId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!map.getBuildings().contains(building)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No building with id %s exists in game with id %s", houseId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!player.getBuildings().contains(building)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No building with id %s belongs to the player with id %s", houseId, playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        Player askingPlayer = null;

        if (askingPlayerId != null) {
            askingPlayer = (Player) idManager.getObject(askingPlayerId);
        }

        JSONObject jsonHouse;

        synchronized (building.getMap()) {

            jsonHouse = utils.houseToJson(building);

            if (askingPlayer != null) {
                try {
                    int maxAttackers = askingPlayer.getAvailableAttackersForBuilding(building);

                    if (maxAttackers > 0) {

                        jsonHouse.put("maxAttackers", maxAttackers);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return Response.status(200).entity(jsonHouse.toJSONString()).build();
    }

    @DELETE
    @Path("/games/{gameId}/players/{playerId}/houses/{houseId}")
    public Response removeHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("houseId") String houseId) throws Exception {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        Building building = (Building) idManager.getObject(houseId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!map.getBuildings().contains(building)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No building with id %s exists in game with id %s", houseId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!player.getBuildings().contains(building)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No building with id %s belongs to the player with id %s", houseId, playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonResponse = new JSONObject();

        if (building.getPlayer().equals(player)) {

            synchronized (map) {
                building.tearDown();
            }

            jsonResponse.put("message", "Tore down building");
        }

        return Response.status(200).entity(jsonResponse.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/houses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getHouses(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        return Response.status(200).entity(utils.housesToJson(player.getBuildings()).toJSONString()).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/houses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String body) throws Exception {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        utils.printTimestamp("Entered createHouse on server");

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonHouse = (JSONObject) parser.parse(body);

        Point point = utils.jsonToPoint(jsonHouse);

        Building building = utils.buildingFactory(jsonHouse, player);

        synchronized (map) {
            utils.printTimestamp("About to place building");

            map.placeBuilding(building, point);

            utils.printTimestamp("Placed building");
        }

        return Response.status(201).entity(utils.houseToJson(building).toJSONString()).build();
    }

    @PATCH
    @Path("/games/{gameId}/players/{playerId}/houses/{houseId}")
    public Response updateHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("houseId") String houseId, String body) throws Exception {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Building building = (Building) idManager.getObject(houseId);
        Player player = (Player) idManager.getObject(playerId);

        System.out.println("PUT house " + building);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!map.getBuildings().contains(building)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No building with id %s exists in game with id %s", houseId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (!player.getBuildings().contains(building)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No building with id %s belongs to the player with id %s", houseId, playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonHouseModification = (JSONObject) parser.parse(body);

        JSONObject jsonResponse = new JSONObject();

        boolean doEvacuationChange = jsonHouseModification.containsKey("evacuate");
        boolean doPromotionsChange = jsonHouseModification.containsKey("promotionsEnabled");
        boolean doUpgrade = jsonHouseModification.containsKey("upgrade");

        if (doEvacuationChange || doPromotionsChange || doUpgrade) {

            boolean evacuate = (boolean)jsonHouseModification.getOrDefault("evacuate", false);
            boolean promotionsEnabled = (boolean)jsonHouseModification.getOrDefault("promotionsEnabled", false);

            if (!building.isMilitaryBuilding()) {
                jsonResponse.put("message", "Cannot evacuate non-military building");
            } else if (!(building.isOccupied() || building.isReady())) {
                jsonResponse.put("message", "Cannot evacuate a building in this state");
            } else {

                synchronized (player.getMap()) {

                    if (doEvacuationChange) {
                        if (evacuate) {
                            building.evacuate();
                        } else {
                            building.cancelEvacuation();
                        }
                    }

                    if (doPromotionsChange) {
                        if (promotionsEnabled) {
                            building.enablePromotions();
                        } else {
                            building.disablePromotions();
                        }
                    }

                    if (doUpgrade) {
                        building.upgrade();
                    }
                }

                jsonResponse = utils.houseToJson(building);
            }
        }

        if (jsonHouseModification.containsKey("attacked")) {

            System.out.println("Attacking");

            System.out.println("Player is " + player);
            System.out.println("Player of building is " + building.getPlayer());

            JSONObject jsonAttackInformation = (JSONObject) jsonHouseModification.get("attacked");

            String attackingPlayerId = (String) jsonAttackInformation.get("attackingPlayerId");
            Player attackingPlayer = (Player) idManager.getObject(attackingPlayerId);

            if (!building.getPlayer().equals(attackingPlayer)) {

                System.out.println();

                synchronized (player.getMap()) {
                    attackingPlayer.attack(building, 1);
                }

                jsonResponse.put("message", "Attacking building");
            } else {
                jsonResponse.put("message", "Cannot attack own building");
            }
        }

        return Response.status(200).entity(jsonResponse.toJSONString()).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/roads")
    public Response createRoad(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String bodyRoad) throws Exception {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonRoad = (JSONObject) parser.parse(bodyRoad);

        JSONArray jsonPoints = (JSONArray) jsonRoad.get("points");

        List<Point> points = utils.jsonToPoints(jsonPoints);

        Road road;

        if (points.size() == 2) {

            synchronized (map) {
                road = map.placeAutoSelectedRoad(player, points.get(0), points.get(1));
            }
        } else {

            synchronized (map) {
                road = map.placeRoad(player, points);
            }
        }

        return Response.status(201).entity(utils.roadToJson(road).toJSONString()).build();
    }

    @DELETE
    @Path("/games/{gameId}/players/{playerId}/roads/{roadId}")
    public Response removeRoad(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("roadId") String roadId) {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        Road road = (Road) idManager.getObject(roadId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (road == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No road with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the road belongs to the given player */
        if (!road.getPlayer().equals(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("The road with id %s does not belong to the player with id %s", roadId, playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject message = new JSONObject();

        try {
            map.removeRoad(road);

            message.put("result", "OK");
        } catch (Exception e) {
            message.put("result", "Failed");
        }

        return Response.status(200).entity(message.toJSONString()).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/flags")
    public Response createFlag(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String bodyFlag) throws Exception {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        JSONObject jsonPoint = (JSONObject) parser.parse(bodyFlag);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        Point point = utils.jsonToPoint(jsonPoint);

        Flag flag;

        synchronized (map) {
            flag = map.placeFlag(player, point);
        }

        return Response.status(201).entity(utils.flagToJson(flag).toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/flags/{flagId}")
    public Response getFlag(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("flagId") String flagId) {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        Flag flag = (Flag) idManager.getObject(flagId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (flag == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No flag with id %s exists", flagId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!flag.getPlayer().equals(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("The flag with id %s does not belong to the player with id %s", flagId, playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        return Response.status(200).entity(utils.flagToJson(flag).toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/view")
    public Response getViewForPlayer(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {

        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Create instances outside the synchronized block when possible */
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

        /* Protect access to the map to avoid interference */
        synchronized (map) {
            Set<Point> discoveredLand = player.getDiscoveredLand();

            /* Fill in houses */
            for (Building building : map.getBuildings()) {

                if (!discoveredLand.contains(building.getPosition())) {
                    continue;
                }

                jsonHouses.add(utils.houseToJson(building));
            }

            /* Fill in trees */
            for (Tree tree : map.getTrees()) {
                if (!discoveredLand.contains(tree.getPosition())) {
                    continue;
                }

                trees.add(utils.treeToJson(tree));
            }

            /* Fill in stones */
            for (Stone stone : map.getStones()) {

                if (!discoveredLand.contains(stone.getPosition())) {
                    continue;
                }

                jsonStones.add(utils.stoneToJson(stone));
            }

            /* Fill in workers */
            for (Worker worker : map.getWorkers()) {

                if (!discoveredLand.contains(worker.getPosition())) {
                    continue;
                }

                if (worker.isInsideBuilding()) {
                    continue;
                }

                jsonWorkers.add(utils.workerToJson(worker));
            }

            /* Fill in flags */
            for (Flag flag : map.getFlags()) {

                if (!discoveredLand.contains(flag.getPosition())) {
                    continue;
                }

                jsonFlags.add(utils.flagToJson(flag));
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

                jsonRoads.add(utils.roadToJson(road));
            }

            /* Fill in the points the player has discovered */
            for (Point point : discoveredLand) {
                jsonDiscoveredPoints.add(utils.pointToJson(point));
            }

            jsonBorders.add(utils.borderToJson(player, playerId));

            /* Fill in the signs */
            for (Sign sign : map.getSigns()) {

                if (!discoveredLand.contains(sign.getPosition())) {
                    continue;
                }

                jsonSigns.add(utils.signToJson(sign));
            }

            /* Fill in wild animals */
            for (WildAnimal animal : map.getWildAnimals()) {

                if (!discoveredLand.contains(animal.getPosition())) {
                    continue;
                }

                /* Animal is an extension of worker so the same method is used */
                jsonWildAnimals.add(utils.wildAnimalToJson(animal));
            }

            /* Fill in crops */
            for (Crop crop : map.getCrops()) {

                if (!discoveredLand.contains(crop.getPosition())) {
                    continue;
                }

                jsonCrops.add(utils.cropToJson(crop));
            }

            /* Fill in dead trees */
            for (Point point : map.getDeadTrees()) {
                jsonDeadTrees.add(utils.pointToJson(point));
            }

            /* Fill in available construction */
            for (Point point : map.getAvailableFlagPoints(player)) {

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                String key = "" + point.x + "," + point.y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add("flag");
            }

            for (Map.Entry<Point, Size> site : map.getAvailableHousePoints(player).entrySet()) {

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(site.getKey())) {
                    continue;
                }

                String key = "" + site.getKey().x + "," + site.getKey().y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add("" + site.getValue().toString().toLowerCase());
            }

            for (Point point : map.getAvailableMinePoints(player)) {

                /* Filter points not discovered yet */
                if (!player.getDiscoveredLand().contains(point)) {
                    continue;
                }

                String key = "" + point.x + "," + point.y;

                jsonAvailableConstruction.putIfAbsent(key, new JSONArray());

                ((JSONArray)jsonAvailableConstruction.get(key)).add("mine");
            }
        }

        for (Map.Entry<Point, DecorationType> entry : map.getDecorations().entrySet()) {
            Point point = entry.getKey();
            DecorationType decorationType = entry.getValue();

            /* Filter points not discovered yet */
            if (!player.getDiscoveredLand().contains(point)) {
                continue;
            }

            jsonDecorations.add(Utils.decorationToJson(decorationType, point));
        }

        return Response.status(200).entity(jsonView.toJSONString()).build();
    }

    @POST
    @Path("/rpc/games/{gameId}/players/{playerId}/find-new-road")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findNewRoad(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String bodyFindNewRoad) throws ParseException {
        GameMap map = (GameMap)idManager.getObject(gameId);
        Player player = (Player)idManager.getObject(playerId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonNewRoadParameters = (JSONObject) parser.parse(bodyFindNewRoad);

        Point start = utils.jsonToPoint((JSONObject) jsonNewRoadParameters.get("from"));
        Point goal = utils.jsonToPoint((JSONObject) jsonNewRoadParameters.get("to"));
        Set<Point> avoid = null;

        if (jsonNewRoadParameters.containsKey("avoid")) {
            avoid = utils.jsonToPointsSet((JSONArray) jsonNewRoadParameters.get("avoid"));
        }

        List<Point> possibleRoad;

        synchronized (map) {
            possibleRoad = map.findAutoSelectedRoad(player, start, goal, avoid);
        }

        JSONObject findNewRoadResponse = new JSONObject();

        findNewRoadResponse.put("roadIsPossible", true);
        findNewRoadResponse.put("possibleRoad", utils.pointsToJson(possibleRoad));

        if (map.isFlagAtPoint(goal) || (map.isRoadAtPoint(goal) && map.isAvailableFlagPoint(player, goal))) {
            findNewRoadResponse.put("closesRoad", true);
        } else {
            findNewRoadResponse.put("closesRoad", false);
        }

        return Response.status(200).entity(findNewRoadResponse.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/statistics/land")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLandStatistics(@PathParam("gameId") String gameId) {
        GameMap map = (GameMap)idManager.getObject(gameId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonResponse = new JSONObject();

        /*

        {'players': [...]    -- jsonPlayers
         'currentTime': 12345,
         'landStatistics': [ -- jsonLandStatisticsDataSeries
            {'time': 23,     -- jsonLandMeasurement
             'values': [2, 3, 4, 5]
            },
         ...
         ]
        }
         */

        /* Add the players */
        JSONArray jsonPlayers = utils.playersToShortJson(map.getPlayers());

        /* Add the land statistics array to the response */
        JSONArray jsonLandStatisticsDataSeries = new JSONArray();

        synchronized (map) {
            LandStatistics landStatistics = map.getStatisticsManager().getLandStatistics();

            for (LandDataPoint landDataPoint : landStatistics.getDataPoints()) {
                JSONObject jsonLandMeasurement = new JSONObject();

                jsonLandMeasurement.put("time", landDataPoint.getTime());

                JSONArray jsonLandValues = new JSONArray();
                for (int landSize : landDataPoint.getValues()) {
                    jsonLandValues.add(landSize);
                }

                jsonLandMeasurement.put("values", jsonLandValues);

                jsonLandStatisticsDataSeries.add(jsonLandMeasurement);
            }
        }

        jsonResponse.put("players", jsonPlayers);
        jsonResponse.put("landStatistics", jsonLandStatisticsDataSeries);
        jsonResponse.put("currentTime", map.getCurrentTime());

        return Response.status(200).entity(jsonResponse.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/statistics/production")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMaterialStatistics(@PathParam("gameId") String gameId) {
        GameMap map = (GameMap)idManager.getObject(gameId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonResponse = new JSONObject();

        jsonResponse.put("players", utils.playersToShortJson(map.getPlayers()));

        JSONArray jsonProductionStatisticsForAllMaterials = new JSONArray();
        StatisticsManager statisticsManager = map.getStatisticsManager();

        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {

            synchronized (map) {
                ProductionDataSeries materialProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(material);

                /* Set the meta data for the report for this material */
                JSONObject jsonMaterialStatisticsDataAndMeta = new JSONObject();
                JSONArray jsonMaterialStatisticsDataSeries = new JSONArray();

                jsonMaterialStatisticsDataAndMeta.put("material", material.name().toLowerCase());
                jsonMaterialStatisticsDataAndMeta.put("materialStatistics", jsonMaterialStatisticsDataSeries);

                /* Add the statistics for this material to the array */
                jsonProductionStatisticsForAllMaterials.add(jsonMaterialStatisticsDataAndMeta);

                for (ProductionDataPoint dataPoint : materialProductionDataSeries.getProductionDataPoints()) {

                    JSONObject jsonMaterialMeasurementPoint = new JSONObject();

                    /* Set measurement 0 */
                    jsonMaterialMeasurementPoint.put("time", dataPoint.getTime());

                    JSONArray jsonMaterialMeasurementPointValues = new JSONArray();

                    int[] values = dataPoint.getValues();

                    for (int value : values) {
                        jsonMaterialMeasurementPointValues.add(value);
                    }

                    jsonMaterialMeasurementPoint.put("values", jsonMaterialMeasurementPointValues);

                    /* Add the data point to the data series */
                    jsonMaterialStatisticsDataSeries.add(jsonMaterialMeasurementPoint);
                }
            }
        }

        /* Add the production statistics array to the response */
        jsonResponse.put("materialStatistics", jsonProductionStatisticsForAllMaterials);

        return Response.status(200).entity(jsonResponse.toJSONString()).build();
    }

    @PATCH
    @Path("/games/{gameId}/players/{playerId}/transportPriority")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setTransportPriority(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String body) throws ParseException, InvalidUserActionException {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        JSONObject jsonBody = (JSONObject) parser.parse(body);

        TransportCategory category = jsonToTransportCategory((String)jsonBody.get("material"));
        int priority = ((Long) jsonBody.get("priority")).intValue();

        System.out.println("Material: " + category);
        System.out.println("Priority: " + priority);

        synchronized (map) {
            player.setTransportPriority(priority, category);
        }

        JSONArray jsonTransportPriority = utils.transportPriorityToJson(player.getTransportPriorities());

        return Response.status(200).entity(jsonTransportPriority.toJSONString()).build();
    }

    private TransportCategory jsonToTransportCategory(String category) {
        return TransportCategory.valueOf(category.toUpperCase());
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/transportPriority")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransportPriority(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {
        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        JSONArray jsonTransportPriority = utils.transportPriorityToJson(player.getTransportPriorities());

        return Response.status(200).entity(jsonTransportPriority.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/gameMessages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameMessagesForPlayer(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {

        GameMap map = (GameMap) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (player == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists", playerId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Check that the player belongs to the given game */
        if (!map.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /*
        * [
        *     {'type': 'MILITARY_BUILDING_READY',
        *       'houseId': '123'
        *     },
        *
        *     {'type': 'NO_MORE_RESOURCES',
        *        'houseId': '123'
        *     },
        *
        *     {'type': 'BORDER_EXPANDED',
        *        'point': {x: 4, y: 8}
        *     }
        *
        *     {'type': 'UNDER_ATTACK',
        *        'houseId': '1234'
        *     }
        *
        *     {'type': 'GEOLOGIST_FIND',
        *        'point': {...}
        *        'material': 'IRON' | 'WATER' | 'COAL' | 'STONE'
        *     }
        * ]
        * */

        JSONArray jsonGameMessages = new JSONArray();

        for (Message message : player.getMessages()) {
            if (message.getMessageType() == MILITARY_BUILDING_READY) {
                JSONObject jsonMilitaryBuildingOccupiedMessage = utils.militaryBuildingReadyMessageToJson((MilitaryBuildingReadyMessage) message);

                jsonGameMessages.add(jsonMilitaryBuildingOccupiedMessage);
            } else if (message.getMessageType() == NO_MORE_RESOURCES) {
                JSONObject jsonNoMoreResourcesMessage = utils.noMoreResourcesMessageToJson((NoMoreResourcesMessage) message);

                jsonGameMessages.add(jsonNoMoreResourcesMessage);
            } else if (message.getMessageType() == MILITARY_BUILDING_OCCUPIED) {
                JSONObject jsonBorderExpandedMessage = utils.militaryBuildingOccupiedMessageToJson((MilitaryBuildingOccupiedMessage) message);

                jsonGameMessages.add(jsonBorderExpandedMessage);
            } else if (message.getMessageType() == UNDER_ATTACK) {
                JSONObject jsonUnderAttackMessage = utils.underAttackMessageToJson((UnderAttackMessage) message);

                jsonGameMessages.add(jsonUnderAttackMessage);
            } else if (message.getMessageType() == GEOLOGIST_FIND) {
                JSONObject jsonGeologistFindMessage = utils.geologistFindMessageToJson((GeologistFindMessage) message);

                jsonGameMessages.add(jsonGeologistFindMessage);
            } else if (message.getMessageType() == BUILDING_LOST) {
                JSONObject jsonBuildingLostMessage = utils.buildingLostMessageToJson((BuildingLostMessage) message);

                jsonGameMessages.add(jsonBuildingLostMessage);
            } else if (message.getMessageType() == BUILDING_CAPTURED) {
                JSONObject jsonBuildingCaptured = utils.buildingCapturedMessageToJson((BuildingCapturedMessage) message);

                jsonGameMessages.add(jsonBuildingCaptured);
            } else if (message.getMessageType() == STORE_HOUSE_IS_READY) {
                JSONObject jsonStoreHouseIsReady = utils.jsonStoreHouseIsReadyMessageToJson((StoreHouseIsReadyMessage) message);

                jsonGameMessages.add(jsonStoreHouseIsReady);
            }
        }

        return Response.status(200).entity(jsonGameMessages.toJSONString()).build();
    }
}
