package org.appland.settlers.rest.resource;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.actors.WildAnimal;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.messages.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.messages.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;
import org.appland.settlers.model.messages.StoreHouseIsReadyMessage;
import org.appland.settlers.model.messages.UnderAttackMessage;
import org.appland.settlers.model.statistics.LandDataPoint;
import org.appland.settlers.model.statistics.LandStatistics;
import org.appland.settlers.model.statistics.ProductionDataPoint;
import org.appland.settlers.model.statistics.ProductionDataSeries;
import org.appland.settlers.model.statistics.StatisticsManager;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.messages.Message.MessageType.*;
import static org.appland.settlers.rest.resource.GameStatus.NOT_STARTED;
import static org.appland.settlers.rest.resource.GameStatus.STARTED;

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
    private final JSONParser parser;
    private final List<GameResource> gameResources;

    public SettlersAPI() {
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
    public Response getGames() {
        JSONArray jsonGameResources = new JSONArray();

        for (GameResource gameResource : gameResources) {
            if (gameResource.status == STARTED) {
                synchronized (gameResource.getGameMap()) {
                    jsonGameResources.add(utils.gameResourceToJson(gameResource));
                }
            } else {
                jsonGameResources.add(utils.gameResourceToJson(gameResource));
            }
        }

        return Response.status(200).entity(jsonGameResources.toJSONString()).build();
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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);

        /* Return 404 if the game doesn't exist */
        if (gameResource == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        /* Return the game as a JSON document */
        JSONObject jsonGameResource = utils.gameResourceToJson(gameResource);

        System.out.println(jsonGameResource.toJSONString());

        return Response.status(200).entity(jsonGameResource.toJSONString()).build();
    }

    @POST
    @Path("/games")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGame(String body) throws Exception {

        /* Return 400 (bad request) if the body is empty */
        if (body.isEmpty()) {
            return Response.status(400).build();
        }

        JSONObject jsonGame = (JSONObject) parser.parse(body);

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

        if (jsonGame.get("status") != null && jsonGame.get("status").equals("STARTED")) {
            gameResource.setStatus(STARTED);
        } else {
            gameResource.setStatus(NOT_STARTED);
        }

        gameResources.add(gameResource);

        return Response.status(201).entity(utils.gameResourceToJson(gameResource).toJSONString()).build();
    }

    @PATCH
    @Path("/games/{gameId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyGame(@PathParam("gameId") String gameId, String body) throws Exception {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);

        if (gameResource == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonUpdates = (JSONObject) parser.parse(body);

        if (gameResource.isStarted()) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("The game %s is already started and can't be modified.", gameId));

            return Response.status(409).entity(message.toJSONString()).build();
        }

        if (jsonUpdates.containsKey("mapId")) {
            String updatedMapFileId = (String) jsonUpdates.get("mapId");

            MapFile updatedMapFile = (MapFile) idManager.getObject(updatedMapFileId);

            gameResource.setMap(updatedMapFile);

            return Response.status(200).entity(utils.gameResourceToJson(gameResource).toJSONString()).build();
        }

        if (jsonUpdates.containsKey("status")) {
            String updatedStatus = (String) jsonUpdates.get("status");

            if (updatedStatus.equals("STARTED")) {
                startGame(gameResource);

                gameResource.setStatus(STARTED);

                return Response.status(200).entity(utils.gameToJson(gameResource.getGameMap(), gameResource).toJSONString()).build();
            }

            return Response.status(400).build();  // Add a bad request message
        }

        if (jsonUpdates.containsKey("resources")) {
            ResourceLevel level = ResourceLevel.valueOf((String) jsonUpdates.get("resources"));

            gameResource.setResource(level);

            return Response.status(200).entity(utils.gameResourceToJson(gameResource).toJSONString()).build();
        }

        if (jsonUpdates.containsKey("othersCanJoin")) {
            System.out.println(jsonUpdates.get("othersCanJoin"));

            gameResource.setOthersCanJoin((Boolean) jsonUpdates.get("othersCanJoin"));

            return Response.status(200).entity(utils.gameResourceToJson(gameResource).toJSONString()).build();
        }

        /* Return bad request (400) if there is no mapFileId included */
        return Response.status(400).build(); // The scope of this is all changes, not only mapId
    }

    private void startGame(GameResource gameResource) throws Exception {

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
    }

    @DELETE
    @Path("/games/{gameId}")
    public Response deleteGame(@PathParam("gameId") String gameId) {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);

        if (gameResource == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        gameResources.remove(gameResource);

        /* Free up the id */
        idManager.remove(gameResource);

        return Response.status(200).build();
    }

    @GET
    @Path("/games/{gameId}/players")
    public Response getPlayersForGame(@PathParam("gameId") String gameId) {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);

        if (gameResource == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONArray jsonPlayers = new JSONArray();

        for (Player player : gameResource.getPlayers()) {
            JSONObject jsonPlayer = utils.playerToJson(player, idManager.getId(player), gameResource);

            if (gameResource.isComputerPlayer(player)) {
                jsonPlayer.put("type", "COMPUTER");
            } else {
                jsonPlayer.put("type", "HUMAN");
            }

            jsonPlayers.add(jsonPlayer);
        }

        return Response.status(200).entity(jsonPlayers.toJSONString()).build();
    }

    @POST
    @Path("/games/{gameId}/players")
    public Response addPlayerToGame(@PathParam("gameId") String gameId, String playerBody) throws ParseException {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        JSONObject jsonPlayer = (JSONObject) parser.parse(playerBody);

        if (gameResource == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        boolean isComputer = false;

        if (jsonPlayer.getOrDefault("type", "").equals("COMPUTER")) {
            isComputer = true;
        }

        Player player = utils.jsonToPlayer(jsonPlayer);

        if (isComputer) {
            gameResource.addComputerPlayer(player);
        } else {
            gameResource.addHumanPlayer(player);
        }

        JSONObject jsonPlayerResponse = utils.playerToJson(player);

        if (isComputer) {
            jsonPlayerResponse.put("type", "COMPUTER");
        } else {
            jsonPlayerResponse.put("type", "HUMAN");
        }

        return Response.status(201).entity(jsonPlayerResponse.toJSONString()).build();
    }

    @PATCH
    @Path("/games/{gameId}/players/{playerId}")
    public Response updatePlayerInGame(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String body) throws ParseException {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        Player player = (Player) idManager.getObject(playerId);
        JSONObject jsonUpdates = (JSONObject) parser.parse(body);

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

        if (jsonUpdates.containsKey("name")) {
            player.setName((String)jsonUpdates.get("name"));
        }

        if (jsonUpdates.containsKey("nation")) {
            Nation nation = Nation.valueOf((String) jsonUpdates.get("nation"));

            player.setNation(nation);
        }

        if (jsonUpdates.containsKey("color")) {
            player.setColor(PlayerColor.valueOf((String)jsonUpdates.get("color")));
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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);

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

        /* Check that the player belongs to the given game */
        if (!gameResource.getPlayers().contains(player)) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No player with id %s exists in game with id %s", playerId, gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        if (gameResource.isStarted()) {
            GameMap map = gameResource.getGameMap();

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();


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
    public Response getPoint(@PathParam("gameId") String gameId, @QueryParam("playerId") String playerId, @QueryParam("x") int x, @QueryParam("y") int y) throws InvalidUserActionException {
        Point point = new Point(x, y);
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
    public Response getHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("houseId") String houseId, @QueryParam("askingPlayerId") String askingPlayerId) throws InvalidUserActionException {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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

            jsonHouse = utils.houseToJson(building, player);

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
    public Response getHouses(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) throws InvalidUserActionException {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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

        return Response.status(200).entity(utils.housesToJson(player.getBuildings(), player).toJSONString()).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/houses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String body) throws Exception {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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

        return Response.status(201).entity(utils.houseToJson(building, player).toJSONString()).build();
    }

    @PATCH
    @Path("/games/{gameId}/players/{playerId}/houses/{houseId}")
    public Response updateHouse(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("houseId") String houseId, String body) throws Exception {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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

                jsonResponse = utils.houseToJson(building, player);
            }
        }

        if (jsonHouseModification.containsKey("attacked")) {

            JSONObject jsonAttackInformation = (JSONObject) jsonHouseModification.get("attacked");

            String attackingPlayerId = (String) jsonAttackInformation.get("attackingPlayerId");
            Player attackingPlayer = (Player) idManager.getObject(attackingPlayerId);
            int attackers = ((Long)jsonAttackInformation.get("attackers")).intValue();

            System.out.println("Attacking");
            System.out.println(jsonAttackInformation);

            AttackStrength attackStrength = AttackStrength.valueOf((String)jsonAttackInformation.get("attackType"));

            if (!building.getPlayer().equals(attackingPlayer)) {
                synchronized (player.getMap()) {
                    attackingPlayer.attack(building, attackers, attackStrength);
                }

                jsonResponse.put("message", "Attacking building");
            } else {
                jsonResponse.put("message", "Cannot attack own building");
            }
        }

        if (jsonHouseModification.containsKey("production")) {
            if (jsonHouseModification.get("production").equals("PAUSED")) {
                synchronized (player.getMap()) {
                    building.stopProduction();
                }
            } else {
                synchronized (player.getMap()) {
                    building.resumeProduction();
                }
            }
        }

        return Response.status(200).entity(jsonResponse.toJSONString()).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/roads")
    public Response createRoad(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String bodyRoad) throws Exception {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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

        Flag flag = null;

        if (jsonRoad.containsKey("flag")) {
            JSONObject jsonFlag = (JSONObject) jsonRoad.get("flag");

            Point point = utils.jsonToPoint(jsonFlag);

            synchronized (map) {
                flag = map.placeFlag(player, point);
            }
        }

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

        JSONObject jsonResponse = utils.roadToJson(road);

        if (flag != null) {
            jsonResponse.put("flag", utils.flagToJson(flag));
        }

        return Response.status(201).entity(jsonResponse.toJSONString()).build();
    }

    @DELETE
    @Path("/games/{gameId}/players/{playerId}/roads/{roadId}")
    public Response removeRoad(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, @PathParam("roadId") String roadId) {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();
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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();
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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();
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
    public Response getViewForPlayer(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) throws InvalidUserActionException {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
        JSONObject jsonView = utils.playerViewToJson(playerId, map, player, gameResource);

        return Response.status(200).entity(jsonView.toJSONString()).build();
    }

    @POST
    @Path("/rpc/games/{gameId}/players/{playerId}/find-new-road")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findNewRoad(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId, String bodyFindNewRoad) throws ParseException {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

        if (map == null) {
            JSONObject message = new JSONObject();

            message.put("status", "Error");
            message.put("message", format("No game with id %s exists", gameId));

            return Response.status(404).entity(message.toJSONString()).build();
        }

        JSONObject jsonResponse = new JSONObject();

        jsonResponse.put("players", utils.playersToShortJson(map.getPlayers()));

        JSONObject jsonProductionStatisticsForAllMaterials = new JSONObject();
        StatisticsManager statisticsManager = map.getStatisticsManager();

        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {

            synchronized (map) {
                ProductionDataSeries materialProductionDataSeries = statisticsManager.getProductionStatisticsForMaterial(material);

                /* Set the meta data for the report for this material */
                JSONArray jsonMaterialStatisticsDataSeries = new JSONArray();

                /* Add the statistics for this material to the array */
                jsonProductionStatisticsForAllMaterials.put(material.name().toUpperCase(), jsonMaterialStatisticsDataSeries);

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
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
        return TransportCategory.valueOf(category);
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/transportPriority")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransportPriority(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

        Player player = (Player) idManager.getObject(playerId);

        JSONArray jsonTransportPriority = utils.transportPriorityToJson(player.getTransportPriorities());

        return Response.status(200).entity(jsonTransportPriority.toJSONString()).build();
    }

    @GET
    @Path("/games/{gameId}/players/{playerId}/gameMessages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameMessagesForPlayer(@PathParam("gameId") String gameId, @PathParam("playerId") String playerId) {
        GameResource gameResource = (GameResource) idManager.getObject(gameId);
        GameMap map = gameResource.getGameMap();

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
