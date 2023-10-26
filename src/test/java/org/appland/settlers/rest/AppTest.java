package org.appland.settlers.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import junit.framework.TestCase;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AppTest extends TestCase {

    private static final String APPLICATION_PATH = "/*";
    private static final String CONTEXT_ROOT = "/settlers";

    @BeforeClass
    public static void startFrontendServer() throws Exception {

        final int port = 8080;
        final Server server = new Server(port);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(server, CONTEXT_ROOT);

        // Register the lifecycle listener
        context.addEventListener(new DeploymentListener());

        // Setup RESTEasy's HttpServletDispatcher at "/api/*".
        final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", APPLICATION_PATH);
        //restEasyServlet.setInitParameter("javax.ws.rs.Application","org.appland.settlers.rest.FatJarApplication");
        restEasyServlet.setInitParameter("javax.ws.rs.Application", FatJarApplication.class.getName());
        //context.addServlet(restEasyServlet, APPLICATION_PATH + "/*");
        context.addServlet(restEasyServlet, APPLICATION_PATH);

        // Set the DefaultServlet at "/".

        server.setStopAtShutdown(true);
        server.start();
        //server.join();

        /* Set the standard URL to avoid repeating it in each test */
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/settlers/api/";
    }

    @Test
    public void testAddGame() {

        /* Create a map as the base for the JSON body */
        Map<String,String> newGame = new HashMap<>();

        /* Add the game */
        Response response = given().contentType("application/json").body(newGame)
                .when().post("/games").then()

                /* Verify the status code */
                .statusCode(201)

                /* Verify that the players attribute is an empty list */
                .body("players", equalTo(Collections.emptyList()))

                .extract().response();

        /* Verify that the reply contains all the required attributes */
        Map<String, ?> jsonResponse = response.jsonPath().getMap("");

        assertTrue(jsonResponse.containsKey("id"));
        assertTrue(jsonResponse.containsKey("players"));
        assertTrue(jsonResponse.containsKey("status"));
        assertTrue(jsonResponse.containsKey("resources"));
    }

    @Test
    public void testCreateGameWithMapAndPlayersReturnsGame() {

        /* Get the id of the first map */
        String mapId = getIdOfFirstMap();

        /* Create the map body */
        Map<String, Object> game = new HashMap<>();

        List<Map<String, String>> players = new ArrayList<>();

        Map<String, String> player0 = new HashMap<>();

        player0.put("name", "Player 0");
        player0.put("color", "#000000");
        player0.put("nation", "ROMANS");

        players.add(player0);

        game.put("mapId", mapId);
        game.put("players", players);

        /* Create the game */
        String gameId = given().contentType(ContentType.JSON).body(game).when()
                .post("/games").then()
                .statusCode(201)

                .body("players[0].name", equalTo("Player 0"))
                .body("players[0].color", equalTo("#000000"))
                .body("mapId", equalTo(mapId))

                .extract().jsonPath().getString("id");

        assertNotNull(gameId);
    }

    @Test
    public void testCreateGameWithName() {

        /* Create a map as the base for the JSON body */
        Map<String,String> game = new HashMap<>();

        game.put("name", "Name of my game");

        /* Add the game */
        Response response = given().contentType("application/json").body(game)
                .when().post("/games").then()

                /* Verify the status code */
                .statusCode(201)

                /* Verify that the players attribute is an empty list */
                .body("players", equalTo(Collections.emptyList()))

                /* Verify that tha name is set correctly */
                .body("name", equalTo("Name of my game"))

                .extract().response();

        String gameId = response.jsonPath().getString("id");
        String name = response.jsonPath().getString("name");

        /* Verify that the name is correct in the returned body */
        assertEquals(name, "Name of my game");

        /* Verify that the name is correct when retrieving a single game */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()

                .statusCode(200)

                .body("name", equalTo("Name of my game"));

        /* Verify that the name is correct when getting all games */
        given().contentType(ContentType.JSON).when()
                .get("/games").then()
                .statusCode(200)
                .body("name", hasItem("Name of my game"));
    }

    @Test
    public void testAddGameWithoutPlayers() {

        /* Create a map as the base for the JSON body */
        Map<String,String> newGame = new HashMap<>();

        /* Add the game */
        Response response = given().contentType("application/json").body(newGame)
                .when().post("/games").then()

                /* Verify the status code */
                .statusCode(201)

                /* Verify that the players attribute is an empty list */
                .body("players", equalTo(Collections.emptyList()))

                .extract().response();

        /* Verify that the reply contains all the required attributes */
        Map<String, ?> jsonResponse = response.jsonPath().getMap("");

        assertTrue(jsonResponse.containsKey("id"));
        assertTrue(jsonResponse.containsKey("players"));
        assertTrue(jsonResponse.containsKey("status"));
        assertTrue(jsonResponse.containsKey("resources"));

        /* Verify that the game exists */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", jsonResponse.get("id")).then()
                .statusCode(200)
                .body("players", equalTo(Collections.emptyList()));

        /* Verify that the game exists in the list of games */
        given().contentType(ContentType.JSON).when()
                .get("/games").then()
                .statusCode(200)
                .body("id", hasItem(jsonResponse.get("id")));

    }

    @Test
    public void testAddedGameIsNotStarted() {

        /* Get the id of the first map */
        String mapId = getIdOfFirstMap();

        /* Create the game body */
        Map<String, Object> game = new HashMap<>();

        List<Map<String, String>> players = new ArrayList<>();

        Map<String, String> player0 = new HashMap<>();

        player0.put("name", "Player 0");
        player0.put("color", "#000000");
        player0.put("nation", "ROMANS");

        players.add(player0);

        game.put("mapId", mapId);
        game.put("players", players);

        /* Create the game */
        String gameId = given().contentType(ContentType.JSON).body(game).when()
                .post("/games").then()
                .statusCode(201)

                /* Verify that the game is not started */
                .body("status", equalTo("NOT_STARTED"))

                .extract().jsonPath().getString("id");

        assertNotNull(gameId);

    }

    @Test
    public void testDefaultResourceLevelIsMedium() {

        /* Create a game with one player */
        String gameId = createOnePlayerGame();

        /* Verify that the resource level is medium */
        String resourceLevel = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .extract().jsonPath().getString("resources");

        assertEquals(resourceLevel, "MEDIUM");
    }

    @Test
    public void testSetResourcesToLow() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Set the resource level to LOW */
        Map<String, String> modifiedGame = new HashMap<>();

        modifiedGame.put("resources", "LOW");

        given().contentType(ContentType.JSON).body(modifiedGame).when()
                .patch("/games/{gameId}", gameId).then()
                .statusCode(200)
                .body("resources", equalTo("LOW"));
    }

    @Test
    public void testSettingResourcesToLowReducesResourcesAvailable() {

        /* Create a game */
        String gameId0 = createOnePlayerGame();

        /* Create a second game as a reference */
        String gameId1 = createOnePlayerGame();

        /* Set the resource level to LOW for the first game*/
        setResourceLevelForGame(gameId0, "LOW");

        /* Start both games */
        startGame(gameId0);
        startGame(gameId1);

        /* Get the amount of stones for the player in each game */
        String playerId0 = getPlayerIds(gameId0).get(0);

        String playerId1 = getPlayerIds(gameId1).get(0);

        String houseId0 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId0, playerId0).then()
                .statusCode(200)
                .extract().jsonPath().getString("[0].id");

        String houseId1 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId1, playerId1).then()
                .statusCode(200)
                .extract().jsonPath().getString("[0].id");

        Map resources0 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId0, playerId0, houseId0).then()
                .statusCode(200)
                .extract().jsonPath().getMap("resources");

        Map resources1 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId1, playerId1, houseId1).then()
                .statusCode(200)
                .extract().jsonPath().getMap("resources");

        Map stone0 = (Map)resources0.get("stone");
        Map stone1 = (Map)resources1.get("stone");

        int stoneAmount0 = (int)stone0.get("has");
        int stoneAmount1 = (int)stone1.get("has");

        assertTrue(stoneAmount0 < stoneAmount1);
    }

    @Test
    public void testSettingResourcesToHighReducesResourcesAvailable() {

        /* Create a game */
        String gameId0 = createOnePlayerGame();

        /* Create a second game as a reference */
        String gameId1 = createOnePlayerGame();

        /* Set the resource level to HIGH for the first game */
        String resourceLevel = "HIGH";
        setResourceLevelForGame(gameId0, resourceLevel);

        /* Start both games */
        startGame(gameId0);
        startGame(gameId1);

        /* Get the amount of stones for the player in each game */
        String playerId0 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId0).then()
                .statusCode(200)
                .extract().jsonPath().getString("players[0].id");

        String playerId1 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId1).then()
                .statusCode(200)
                .extract().jsonPath().getString("players[0].id");

        String houseId0 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId0, playerId0).then()
                .statusCode(200)
                .extract().jsonPath().getString("[0].id");

        String houseId1 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId1, playerId1).then()
                .statusCode(200)
                .extract().jsonPath().getString("[0].id");

        Map resources0 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId0, playerId0, houseId0).then()
                .statusCode(200)
                .extract().jsonPath().getMap("resources");

        Map resources1 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId1, playerId1, houseId1).then()
                .statusCode(200)
                .extract().jsonPath().getMap("resources");

        Map stone0 = (Map)resources0.get("stone");
        Map stone1 = (Map)resources1.get("stone");

        int stoneAmount0 = (int)stone0.get("has");
        int stoneAmount1 = (int)stone1.get("has");

        assertTrue(stoneAmount0 > stoneAmount1);
    }

    @Test
    public void testSetResourcesToMedium() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Set the resource level to medium */
        setResourceLevelForGame(gameId, "MEDIUM");
    }

    @Test
    public void testSetResourcesToHigh() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Set the resource level to high */
        setResourceLevelForGame(gameId, "HIGH");
    }

    @Test
    public void testNotStartedGameHasNoDiscoveredPoints() {

        /* Get the id of the first map */
        String mapId = getIdOfFirstMap();

        /* Create the map body */
        Map<String, Object> game = new HashMap<>();

        List<Map<String, String>> players = new ArrayList<>();

        Map<String, String> player0 = new HashMap<>();

        player0.put("name", "Player 0");
        player0.put("color", "#000000");
        player0.put("nation", "ROMANS");

        players.add(player0);

        game.put("mapId", mapId);
        game.put("players", players);

        /* Create the game */
        given().contentType(ContentType.JSON).body(game).when()
                .post("/games").then()
                .statusCode(201)
                .body("players[0].discoveredPoints", equalTo(Collections.emptyList()));

    }

    @Test
    public void testStartedGameHasDiscoveredPointsAndHeadquarter() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the player as a map */
        Map<String, Object> jsonPlayer = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("players[0]");

        /* Verify that the player has no discovered points */
        assertTrue(jsonPlayer.containsKey("discoveredPoints"));

        /* Verify that the player has a headquarter */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/view", gameId, playerId).then()
                .statusCode(200)
                .body("houses[0].type", equalTo("Headquarter"));
    }

    @Test
    public void testGettingPlayersAfterStartingGame() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the list of players */
        List playerId = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getList("");
    }

    @Test
    public void testStartGame() {

        /* Get the id of the first map */
        String mapId = getIdOfFirstMap();

        /* Create the map body */
        Map<String, Object> game = new HashMap<>();

        List<Map<String, String>> players = new ArrayList<>();

        Map<String, String> player0 = new HashMap<>();

        player0.put("name", "Player 0");
        player0.put("color", "#000000");
        player0.put("nation", "ROMANS");

        players.add(player0);

        game.put("mapId", mapId);
        game.put("players", players);

        /* Create the game */
        String gameId = given().contentType(ContentType.JSON).body(game).when()
                .post("/games").then()
                .statusCode(201)

                /* Verify that the game is not started */
                .body("status", equalTo("NOT_STARTED"))

                .extract().jsonPath().getString("id");

        /* Start the game */
        Map<String, String> modifiedGame = new HashMap<>();

        modifiedGame.put("status", "STARTED");

        given().contentType(ContentType.JSON).body(modifiedGame).when()
                .patch("/games/{gameId}", gameId).then()
                .statusCode(200)
                .body("status", equalTo("STARTED"));

        /* Get the game and verify that it's started */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .body("status", equalTo("STARTED"));
    }

    @Test
    public void testCannotCreateGameWithEmptyBody() {

        /* Verify that it's not possible to create a game with an empty body. At least "{}" is required */
        given().contentType("application/json")
                .when().post("/games").then()

                /* Verify the returned status code is 400 (bad request) */
                .statusCode(400);
    }

    @Test
    public void testDeleteGame() {

        /* Add the game */
        String gameId = createOnePlayerGame();

        /* Verify that the game exists */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200)
                .body("id", equalTo(gameId));

        /* Verify that the game can be deleted */
        given().contentType(ContentType.JSON).when()
                .delete("/games/{gameId}", gameId).then()

                /* Verify that the status code is 200 */
                .statusCode(200);

        /* Verify that the game is gone */
        given().contentType(ContentType.JSON).when()
                .delete("/games/{gameId}", gameId).then()
                .statusCode(404);
    }

    @Test
    public void testAddedGameCanBeRetrieved() {

        /* Create game and store the id */
        Map<String, String> newGame = new HashMap<>();

        String id = given().contentType("application/json").body(newGame)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Verify that the game can be retrieved */
        Response response = given().contentType(ContentType.JSON)
                .when().get("/games/{id}", id).then()
                .statusCode(200)

                /* Verify that the players attribute is an empty list */
                .body("players", equalTo(Collections.emptyList()))

                /* Verify that the id is correct */
                .body("id", equalTo(id))

                .extract().response();

        /* Verify that the reply contains all the required attributes */
        Map<String, ?> jsonResponse = response.jsonPath().getMap("");

        assertTrue(jsonResponse.containsKey("id"));
        assertTrue(jsonResponse.containsKey("players"));
        assertTrue(jsonResponse.containsKey("status"));
        assertTrue(jsonResponse.containsKey("resources"));
    }

    @Test
    public void testCannotGetNonexistentGame() {

        String NON_EXISTING_GAME = "123123123";

        /* Make sure the game is non-existing */
        given().contentType(ContentType.JSON).when()
                .delete("/games/{gameId}", NON_EXISTING_GAME);

        /* Verify that getting the non-existing game returns 404 */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", NON_EXISTING_GAME).then()
                .statusCode(404);
    }

    @Test
    public void testMapEndpointExistsAndReturnsList() {

        /* Verify that the map can be retrieved */
        given().contentType(ContentType.JSON)
                .when().get("/maps").then()
                .statusCode(200)
                .extract().jsonPath().getList("");
    }

    @Test
    public void testReferenceMapIsCorrect() {

        /* Verify that the map is correct when read as part of the list of maps can be retrieved */
        String mapId = given().contentType(ContentType.JSON)
                .when().get("/maps").then()
                .statusCode(200)

                /* Verify that the attributes of the map are correct */
                .body("[0].title", equalTo("Drei Vulkane"))
                .body("[0].author", equalTo("J�rgen"))
                .body("[0].width", equalTo(96))
                .body("[0].height", equalTo(96))
                .body("[0].maxPlayers", equalTo(3))
                .extract().jsonPath().getString("[0].id");

        assertNotNull(mapId);
        assertNotEquals(mapId, "");

        /* Verify that the reference map is correct when read as a single instance */
        given().contentType(ContentType.JSON)
                .when().get("/maps/{mapId}", mapId).then()
                .statusCode(200)

                /* Verify that the attributes of the map are correct */
                .body("title", equalTo("Drei Vulkane"))
                .body("author", equalTo("J�rgen"))
                .body("width", equalTo(96))
                .body("height", equalTo(96))
                .body("maxPlayers", equalTo(3));
    }

    @Test
    public void testGetTerrainForMap() {

        /* Get the map id */
        String mapId = getIdOfFirstMap();

        assertNotNull(mapId);
        assertNotEquals(mapId, "");

        /* Verify that the terrain can be retrieved */
        List straightBelow = given().contentType(ContentType.JSON).when()
                .get("/maps/{mapId}/terrain", mapId).then()
                .statusCode(200)

                /* Extract the terrain */
        .extract().jsonPath().getJsonObject("straightBelow");

        assertNotNull(straightBelow);

        List belowToTheRight = given().contentType(ContentType.JSON).when()
                .get("/maps/{mapId}/terrain", mapId).then()
                .statusCode(200)

                /* Extract the terrain */
                .extract().jsonPath().getJsonObject("belowToTheRight");

        assertNotNull(belowToTheRight);
    }

    @Test
    public void testTerrainContainsHeight() {

        /* Get the map id */
        String mapId = getIdOfFirstMap();

        assertNotNull(mapId);
        assertNotEquals(mapId, "");

        /* Verify that the terrain can be retrieved */
        List<Integer> heights = given().contentType(ContentType.JSON).when()
                .get("/maps/{mapId}/terrain", mapId).then()
                .statusCode(200)

                /* Extract the terrain */
                .extract().jsonPath().getJsonObject("heights");

        assertNotNull(heights);
        assertFalse(heights.isEmpty());
    }

    @Test
    public void testGetPlayerStartingPointsForMap() {

        /* Get the map id */
        String mapId = getIdOfFirstMap();

        assertNotNull(mapId);
        assertNotEquals(mapId, "");

        /* Verify that the players are correct */
        List startingPoints = given().contentType(ContentType.JSON).when()
                .get("/maps/{mapId}", mapId).then()
                .statusCode(200)

                /* Get the starting points */
                .extract().jsonPath().getList("startingPoints");

        Point[] points = new Point[] {
                new Point(64, 38),
                new Point(120, 72),
                new Point(146, 30)
        };

        Set<Point> hits = new HashSet<>();

        assertEquals(startingPoints.size(), 3);

        for (Object startingPointObject : startingPoints) {
            Map startingPointMap = (Map)startingPointObject;

            for (Point point : points) {
                if (point.x == (Integer)startingPointMap.get("x") &&
                    point.y == (Integer)startingPointMap.get("y")) {
                    hits.add(point);

                    break;
                }
            }
        }

        assertEquals(hits.size(), 3);
    }

    @Test
    public void testCannotDeleteMap() {

        /* Verify that the game can be retrieved */
        String mapId = given().contentType(ContentType.JSON)
                .when().get("/maps").then()
                .statusCode(200)

                /* Verify that the attributes of the map are correct */
                .body("[0].title", equalTo("Drei Vulkane"))
                .body("[0].author", equalTo("J�rgen"))
                .body("[0].width", equalTo(96))
                .body("[0].height", equalTo(96))
                .body("[0].maxPlayers", equalTo(3))
                .extract().jsonPath().getString("[0].id");

        assertNotNull(mapId);
        assertNotEquals(mapId, "");

        /* Verify that the map cannot be deleted */
        given().contentType(ContentType.JSON).when()
                .delete("/maps/{mapId}", mapId).then()
                .statusCode(405);

        /* Verify that the map still exists */
        given().contentType(ContentType.JSON).when()
                .get("/maps/{mapId}", mapId).then()
                .statusCode(200)
                .body("id", equalTo(mapId));
    }

    @Test
    public void testAddPlayerToGame() {

        /* Create game without players */
        Map<String,String> newGame = new HashMap<>();
        newGame.put("width", "100");
        newGame.put("height", "100");

        String gameId = given().contentType("application/json").body(newGame)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Verify that a player can be added to the game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Name");
        player.put("color", "#121212");
        player.put("nation", "ROMANS");

        given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId).then()

                /* Verify that the creation returned status 201 */
                .statusCode(201)

                /* Verify that the player is returned in the body */
                .body("name", equalTo("Some Name"))
                .body("color", equalTo("#121212"))

                /* Store the id */
                .extract().jsonPath().getString("id");
    }

    @Test
    public void testPlayerBelongsToOnlyOneGame() {

        /* Create game without players */
        Map<String,String> newGame0 = new HashMap<>();
        newGame0.put("width", "100");
        newGame0.put("height", "100");

        String gameId0 = given().contentType("application/json").body(newGame0)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Create a second game without players */
        Map<String,String> newGame1 = new HashMap<>();
        newGame1.put("width", "100");
        newGame1.put("height", "100");

        String gameId1 = given().contentType("application/json").body(newGame1)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Add a player to the first game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Other Name");
        player.put("color", "#343434");
        player.put("nation", "ROMANS");

        String playerId = given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId0).then()

                /* Store the id */
                .extract().jsonPath().getString("id");

        /* Verify that the player belongs to the first game */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}", gameId0, playerId).then()

                .statusCode(200)

                .body("name", equalTo("Some Other Name"));

        /* Verify that the player doesn't belong to the second game */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}", gameId1, playerId).then()

                /* Verify that the status code is 404 */
                .statusCode(404);
    }

    @Test
    public void testCannotAccessValidPlayerInNonExistingGame() {

        String NON_EXISTING_ID = "123123123";

        /* Create game without players */
        Map<String,String> newGame0 = new HashMap<>();
        newGame0.put("width", "100");
        newGame0.put("height", "100");

        String gameId0 = given().contentType("application/json").body(newGame0)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Add a player to the game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Other Name");
        player.put("color", "#343434");
        player.put("nation", "ROMANS");

        String playerId = given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId0).then()

                /* Store the id */
                .extract().jsonPath().getString("id");

        /* Verify that the second game is non-existing */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", NON_EXISTING_ID)
                .then()
                .statusCode(404);

        /* Verify that the player cannot be accessed through a non-existing game */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}", NON_EXISTING_ID, playerId).then()

                .statusCode(404);
    }

    @Test
    public void testCreateGameAndThenAssignMapFileToGame() {

        /* Get the id of the map file */
        String mapFileId = given().contentType(ContentType.JSON).when()
                .get("/maps").then()
                .extract().jsonPath().getString("[0].id");

        /* Create a game */
        Map<String, String> mapGame = new HashMap<>();



        String gameId = given().contentType(ContentType.JSON).body(mapGame).when()
                .post("/games").then()
                .statusCode(201).extract().jsonPath().getString("id");

        /* Verify that the game exists */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200);

        /* Assign the map file */
        Map<String, String> assignMapFileMap = new HashMap<>();

        assignMapFileMap.put("mapId", mapFileId);

        given().contentType(ContentType.JSON).body(assignMapFileMap).when()
                .patch("/games/{gameId}", gameId).then()
                .statusCode(200);

        /* Verify that the game map is set */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200)
                .body("mapId", equalTo(mapFileId));
    }

    @Test
    public void testPlayerAddedToGameCanBeRetrieved() {

        /* Create game without players */
        Map<String,String> newGame = new HashMap<>();
        newGame.put("width", "100");
        newGame.put("height", "100");

        String gameId = given().contentType("application/json").body(newGame)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Add a player to the game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Other Name");
        player.put("color", "#343434");
        player.put("nation", "ROMANS");

        String playerId = given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId).then()

                /* Store the id */
                .extract().jsonPath().getString("id");

        /* Verify that the id is set for the player */
        assertNotNull(playerId);

        /* Verify that the player added can be retrieved again */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}", gameId, playerId).then()

                /* Verify that the status code is 200 */
                .statusCode(200)

                /* Verify the body */
                .body("name", equalTo("Some Other Name"))
                .body("color", equalTo("#343434"))
                .body("id", equalTo(playerId));
    }

    @Test
    public void testUpdatePlayerNameAndColor() {

        /* Create game without players */
        Map<String,String> newGame = new HashMap<>();
        newGame.put("width", "100");
        newGame.put("height", "100");

        String gameId = given().contentType("application/json").body(newGame)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Add a player to the game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Other Name");
        player.put("color", "#343434");
        player.put("nation", "ROMANS");

        String playerId = given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId).then()

                /* Store the id */
                .extract().jsonPath().getString("id");

        /* Verify that the id is set for the player */
        assertNotNull(playerId);

        /* Update the player's name and color */
        player.put("name", "Changed name");
        player.put("color", "#AABBCC");

        given().contentType(ContentType.JSON).body(player).when()
                .patch("/games/{gameId}/players/{playerId}", gameId, playerId).then()

                .body("name", equalTo("Changed name"))
                .body("color", equalTo("#aabbcc"));

        /* Get the player again and verify that it's correct */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}", gameId, playerId).then()

                .body("name", equalTo("Changed name"))
                .body("color", equalTo("#aabbcc"));
    }

    @Test
    public void testPlayerRemovePlayerFromGame() {

        /* Create game without players */
        Map<String,String> newGame = new HashMap<>();
        newGame.put("width", "100");
        newGame.put("height", "100");

        String gameId = given().contentType("application/json").body(newGame)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Add a player to the game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Other Name");
        player.put("color", "#343434");
        player.put("nation", "ROMANS");

        String playerId = given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId).then()

                /* Store the id */
                .extract().jsonPath().getString("id");

        /* Verify that the id is set for the player */
        assertNotNull(playerId);

        /* Remove the player */
        given().contentType(ContentType.JSON).when()
                .delete("/games/{gameId}/players/{playerId}", gameId, playerId).then()

                .body("name", equalTo("Some Other Name"))
                .body("color", equalTo("#343434"))
                .body("id", equalTo(playerId));

        /* Verify that the player cannot can be retrieved again */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}", gameId, playerId).then()

                /* Verify that the status code is 200 */
                .statusCode(404);
    }

    @Test
    public void testCanGetPlayersFromGameThatHasNotStarted() {

        /* Create game without players */
        Map<String,String> newGame = new HashMap<>();
        newGame.put("width", "100");
        newGame.put("height", "100");

        String gameId = given().contentType("application/json").body(newGame)
                .when().post("/games").then()
                .extract().jsonPath().getString("id");

        /* Add a player to the game */
        Map<String, String> player = new HashMap<>();

        player.put("name", "Some Other Name");
        player.put("color", "#343434");
        player.put("nation", "ROMANS");

        String playerId = given().contentType(ContentType.JSON).body(player)
                .when().post("/games/{id}/players", gameId).then()

                /* Store the id */
                .extract().jsonPath().getString("id");

        /* Verify that the id is set for the player */
        assertNotNull(playerId);

        /* Verify that the game isn't started */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .body("status", equalTo("NOT_STARTED"));

        /* Verify that the players can be retrieved */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players", gameId).then()

                /* Verify that the status code is 200 */
                .statusCode(200)

                /* Verify the body */
                .body("[0].name", equalTo("Some Other Name"))
                .body("[0].color", equalTo("#343434"))
                .body("[0].id", equalTo(playerId));
    }

    @Test
    public void testMapIsCorrectInGameAfterAssignment() {

        /* Get the id of the map file */
        String mapId = given().contentType(ContentType.JSON).when()
                .get("/maps").then()
                .extract().jsonPath().getString("[0].id");

        /* Create a game */
        Map<String, String> mapGame = new HashMap<>();

        String gameId = given().contentType(ContentType.JSON).body(mapGame).when()
                .post("/games").then()
                .statusCode(201).extract().jsonPath().getString("id");

        /* Verify that the game exists */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200);

        /* Assign the map file */
        Map<String, String> assignMapFileMap = new HashMap<>();

        assignMapFileMap.put("mapId", mapId);

        given().contentType(ContentType.JSON).body(assignMapFileMap).when()
                .patch("/games/{gameId}", gameId).then()
                .statusCode(200);

        /* Verify that the game map is set */
        Map<String, String> gameMap = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200)
                .body("mapId", equalTo(mapId))
                .extract().jsonPath().getMap("map");

        /* Verify that the height and width are correct */
        assertTrue(gameMap.containsKey("width"));
        assertTrue(gameMap.containsKey("height"));

        /* Verify that the id is correct */
        assertEquals(gameMap.get("id"), mapId);
    }

    @Test
    public void testCreateHouseReturnsCreatedHouse() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the location of the headquarters */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("[0]");

        /* Create a house and verify that the house is returned in the response */
        Map<String, Object> house = new HashMap<>();

        int x = (Integer)headquarter.get("x") - 4;
        int y = (Integer)headquarter.get("y");

        house.put("type", "Woodcutter");
        house.put("x", x);
        house.put("y", y);
        house.put("playerId", playerId);

        /* Verify that the house can be created and that it returns the house in the body */
        String houseId = given().contentType(ContentType.JSON).body(house).when()
                .post("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(201)
                .body("type", equalTo("Woodcutter"))
                .body("x", equalTo(x))
                .body("y", equalTo(y))
                .extract().jsonPath().getString("id");

        assertNotNull(houseId);

        /* Verify that the house is correct when retrieved as a single instance */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId, playerId, houseId).then()
                .statusCode(200)
                .body("type", equalTo("Woodcutter"))
                .body("x", equalTo(x))
                .body("y", equalTo(y))
                .body("id", equalTo(houseId));
    }

    @Ignore
    @Test
    public void testMilitaryBuildingCanBeEvacuated() throws InterruptedException {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the location of the headquarter */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .log().all()
                .extract().jsonPath().getMap("[0]");

        /* Place barracks */
        Point point01 = jsonToPoint(headquarter);
        Point point02 = new Point(point01.x - 8, point01.y);
        int x = (Integer)headquarter.get("x") - 8;
        int y = (Integer)headquarter.get("y");

        String buildingType = "Barracks";

        String houseId = placeBuilding(gameId, playerId, point02.x, point02.y, buildingType);

        /* Connect the barracks to the headquarter */
        Map<String, Object> jsonRoadBody = new HashMap<>();
        List<Map<String, Integer>> points = new ArrayList<>();

        points.add(pointToJson(point01.downRight()));
        points.add(pointToJson(point02.downRight()));

        jsonRoadBody.put("points", points);

        given().contentType(ContentType.JSON).body(jsonRoadBody).when()
                .post("/games/{gameId}/players/{playerId}/roads", gameId, playerId).then()
                .statusCode(200).log().all();

        /* Wait for the barracks to get constructed */
        for (int i = 0; i < 100; i++) {

            Map<String, Object> house = given().contentType(ContentType.JSON).when()
                    .get("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId, playerId, houseId).then()
                    .log().all().extract().jsonPath().getMap("");

            if (house.get("state").equals("UNOCCUPIED")) {
                break;
            }

            System.out.println("Sleeping " + i);
            Thread.sleep(1000);
        }

        /* Verify that the barracks can be evacuated */
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("evacuate", true);

        given().contentType(ContentType.JSON).body(parameters).log().all().when()
                .put("/games/{gameId}/players/{playerId}/houses/{houseId}", gameId, playerId, houseId).then()
                .statusCode(200).log().all()
                .body("type", equalTo("Barracks"))
                .body("evacuate", equalTo(true))
                .body("x", equalTo(x))
                .body("y", equalTo(y))
                .body("id", equalTo(Integer.parseInt(houseId)));
    }

    private Point jsonToPoint(Map<String, Object> jsonPoint) {
        return new Point(
                (Integer)jsonPoint.get("x"),
                (Integer)jsonPoint.get("y")
        );
    }

    private Map<String, Integer> pointToJson(Point point) {
        Map<String, Integer> jsonPoint = new HashMap<>();

        jsonPoint.put("x", point.x);
        jsonPoint.put("y", point.y);

        return jsonPoint;
    }

    private String placeBuilding(String gameId, String playerId, int x, int y, String buildingType) {
        Map<String, Object> house = new HashMap<>();

        house.put("type", buildingType);
        house.put("x", x);
        house.put("y", y);
        house.put("playerId", playerId);

        /* Place the house */
        String houseId = given().contentType(ContentType.JSON).body(house).when()
                .post("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .body("type", equalTo(buildingType))
                .body("x", equalTo(x))
                .body("y", equalTo(y))
                .extract().jsonPath().getString("id");

        assertNotNull(houseId);
        return houseId;
    }

    @Test
    public void testCreateFlagReturnsCreatedFlag() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the location of the headquarter */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("[0]");

        /* Create a flag and verify that the flag is returned in the response */
        Map<String, Object> flag = new HashMap<>();

        int x = (Integer)headquarter.get("x") - 8;
        int y = (Integer)headquarter.get("y");

        flag.put("x", x);
        flag.put("y", y);

        /* Verify that the flag can be created and that it returns the flag in the body */
        String flagId = given().contentType(ContentType.JSON).body(flag).when()
                .post("/games/{gameId}/players/{playerId}/flags", gameId, playerId).then()
                .statusCode(201)
                .body("x", equalTo(x))
                .body("y", equalTo(y))
                .extract().jsonPath().getString("id");

        assertNotNull(flagId);

        /* Verify that the flag is correct when retrieved as a single instance */
        given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/flags/{flagId}", gameId, playerId, flagId).then()
                .statusCode(200)
                .body("x", equalTo(x))
                .body("y", equalTo(y))
                .body("id", equalTo(flagId));
    }

    @Ignore
    @Test
    public void testCourierWalks() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getString("players[0].id");

        /* Get the location of the headquarter */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("[0]");

        /* Place a flag */
        int x = (Integer)headquarter.get("x") - 3;
        int y = (Integer)headquarter.get("y") - 1;

        String flagId = placeFlag(gameId, playerId, x, y);

        /* Place a road from the headquarter to the flag */
        Map<String, Object> road = new HashMap<>();
        List<Map<String, Integer>> points = new ArrayList<>();

        Map<String, Integer> point0 = new HashMap<>();
        Map<String, Integer> point2 = new HashMap<>();

        point0.put("x", x);
        point0.put("y", y);

        point2.put("x", x + 4);
        point2.put("y", y);

        points.add(point0);
        points.add(point2);

        road.put("points", points);

        /* Get the number of workers seen by the player */
        List<Object> workers = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/view", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getList("workers");

        int numberOfWorkers = workers.size();

        /* Place the road */
        String roadId = given().contentType(ContentType.JSON).body(road).when()
                .post("/games/{gameId}/players/{playerId}/roads", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getString("id");

        int newNumberOfWorkers = numberOfWorkers;
        int tries = 0;
        while (tries < 2000) {

            tries++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            workers = given().contentType(ContentType.JSON).when()
                    .get("/games/{gameId}/players/{playerId}/view", gameId, playerId).then()
                    .statusCode(200)
                    .extract().jsonPath().getList("workers");

            newNumberOfWorkers = workers.size();

            if (newNumberOfWorkers > numberOfWorkers) {
                break;
            }
        }

        assertNotEquals(numberOfWorkers, newNumberOfWorkers);
    }

    @Test
    public void testFlagIdIsString() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the location of the headquarter */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("[0]");

        /* Place a flag */
        int x = (Integer) headquarter.get("x") - 3;
        int y = (Integer) headquarter.get("y") - 1;

        String flagId = placeFlag(gameId, playerId, x, y);

        /* Verify that the id is a string when getting the view for a player */
        String otherFlagId = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/view", gameId, playerId).then()
                .extract().jsonPath().getString("flags[0].id");

        assertNotNull(otherFlagId);
    }

    @Test
    public void testPlayerIdInFlagIsString() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the location of the headquarter */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("[0]");

        /* Place a flag */
        Map<String, Object> flag = new HashMap<>();

        int x = (Integer) headquarter.get("x") - 3;
        int y = (Integer) headquarter.get("y") - 1;

        flag.put("x", x);
        flag.put("y", y);

        /* Create the flag */
        String playerId1 = given().contentType(ContentType.JSON).body(flag).when()
                .post("/games/{gameId}/players/{playerId}/flags", gameId, playerId).then()
                .statusCode(201)
                .extract().jsonPath().getString("playerId");

        assertNotNull(playerId1);
        assertEquals(playerId, playerId1);

        /* Verify that the id is a string when getting the view for a player */
        String playerId2 = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/view", gameId, playerId).then()
                .extract().jsonPath().getString("flags[0].playerId");

        assertNotNull(playerId2);
        assertEquals(playerId, playerId2);
    }

    @Test
    public void testFindPossibleRoadToFlag() {

        /* Create the game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get the id of the player */
        String playerId = getPlayerIds(gameId).get(0);

        /* Get the location of the headquarter */
        Map<String, Object> headquarter = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players/{playerId}/houses", gameId, playerId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("[0]");

        /* Verify that we can find a possible way to the headquarter's flag */
        Map findPossibleWayParameters = new HashMap();

        Point point0 = Utils.getPositionForBuildingMap(headquarter).downRight().right().right();
        Point point1 = Utils.getPositionForBuildingMap(headquarter).downRight();

        findPossibleWayParameters.put("from", Utils.pointToMap(point0));
        findPossibleWayParameters.put("to", Utils.pointToMap(point1));

        List points = given().contentType(ContentType.JSON).body(findPossibleWayParameters).when()
                .post("/rpc/games/{gameId}/players/{playerId}/find-new-road", gameId, playerId).then()
                .statusCode(200)
                .body("roadIsPossible", equalTo(true))
                .extract().jsonPath().getList("possibleRoad");

        Point point2 = Utils.mapToPoint((Map)points.get(0));
        Point point3 = Utils.mapToPoint((Map)points.get(1));
        Point point4 = Utils.mapToPoint((Map)points.get(2));

        assertEquals(points.size(), 3);
        assertEquals(point2.x, point0.x);
        assertEquals(point2.y, point0.y);
        assertEquals(point3.x, point0.left().x);
        assertEquals(point3.y, point0.left().y);
        assertEquals(point4.x, point1.x);
        assertEquals(point4.y, point1.y);
    }

    @Test
    public void testGetMaterialStatisticsOnStart() {

        /* Create a game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Verify that it's possible to get material statistics for the game */
        Map statistics = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/statistics/production", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("");

        assertTrue(statistics.containsKey("players"));
        assertTrue(statistics.containsKey("materialStatistics"));

        /* Verify that all players are listed */
        List<String> playerIds = getPlayerIds(gameId);

        List playerObjects = (List)statistics.get("players");
        for (String playerId : playerIds) {
            boolean found = false;

            for (Object playerObject : playerObjects) {
                Map playerMap = (Map)playerObject;

                if (playerId.equals(playerMap.get("id"))) {
                    found = true;

                    break;
                }
            }

            assertTrue(found);
        }

        /* Verify that wood statistics are available */
        List<Map> statisticsArray = (List<Map>)statistics.get("materialStatistics");

        boolean woodFound = false;
        for (Map materialStatistics : statisticsArray) {
            if ("wood".equals(materialStatistics.get("material"))) {
                woodFound = true;

                break;
            }
        }

        assertTrue(woodFound);

        /* Verify that all production statistics are 0 in the beginning */
        for (Map productionStatistics : statisticsArray) {
            List<Map> materialProductionStatistics = (List<Map>)productionStatistics.get("materialStatistics");

            boolean timeZeroFound = false;

            for (Map measurement : materialProductionStatistics) {

                if ((int)measurement.get("time") == 0) {
                    timeZeroFound = true;

                    for (Integer value : (List<Integer>)measurement.get("values")) {
                        assertEquals(0, (int)value);
                    }
                }
            }

            assertTrue(timeZeroFound);
        }
    }

    @Test
    public void testProductionStatisticsContainsRightMaterials() {

        /* Create a game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Verify that it's possible to get material statistics for the game */
        Map statistics = getProductionStatisticsForGame(gameId);

        /* Verify that statistics for the required materials are available */
        List<Material> requiredMaterials = Arrays.asList(WOOD, PLANK, STONE, GOLD, SWORD, SHIELD, COIN);
        List<Map> statisticsArray = (List<Map>)statistics.get("materialStatistics");

        for (Material material : requiredMaterials) {
            boolean materialFound = false;

            for (Map materialStatistics : statisticsArray) {

                if (material.name().toLowerCase().equals(materialStatistics.get("material"))) {
                    materialFound = true;

                    break;
                }
            }

            assertTrue(materialFound);
        }
    }

    @Test
    public void testGetLandStatisticsFromGame() {

        /* Create a game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Verify that it's possible to get land statistics for the game */
        Map landStatistics = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/statistics/land", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("");

        assertTrue(landStatistics.containsKey("players"));
        assertTrue(landStatistics.containsKey("landStatistics"));
        assertTrue(landStatistics.containsKey("currentTime"));
    }

    @Test
    public void testGetLandStatisticsFromGameHasInitialDataPoint() {

        /* Create a game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get land statistics for the game */
        Map landStatistics = getLandStatistics(gameId);

        /* Verify that statistics for each player is available */
        assertTrue(landStatistics.containsKey("players"));
        assertTrue(landStatistics.containsKey("landStatistics"));

        List<Map> statisticsArray = (List<Map>) landStatistics.get("landStatistics");

        assertTrue(statisticsArray.size() >= 1);
        assertTrue(statisticsArray.get(0).containsKey("time"));
        assertTrue(statisticsArray.get(0).containsKey("values"));

        List<Integer> values = (List<Integer>) statisticsArray.get(0).get("values");

        assertEquals(values.size(), 1);
    }

    @Test
    public void testPlayersAreCorrectInLandStatistics() {

        /* Create a game */
        String gameId = createOnePlayerGame();

        /* Start the game */
        startGame(gameId);

        /* Get land statistics for the game */
        Map landStatistics = getLandStatistics(gameId);

        /* Verify that the players field is correct */
        List<Map> jsonPlayers = (List<Map>)landStatistics.get("players");

        assertEquals(jsonPlayers.size(), 1);

        Map jsonPlayer = jsonPlayers.get(0);

        assertTrue(jsonPlayer.containsKey("name"));
        assertTrue(jsonPlayer.containsKey("color"));
        assertEquals(jsonPlayer.get("name"), "Player 0");
        assertEquals(jsonPlayer.get("color"), "#000000");

    }

    private Map getLandStatistics(String gameId) {
        Map landStatistics = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/statistics/land", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("");

        assertTrue(landStatistics.containsKey("players"));
        assertTrue(landStatistics.containsKey("landStatistics"));

        return landStatistics;
    }

    private Map<Object, Object> getProductionStatisticsForGame(String gameId) {
        return given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/statistics/production", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getMap("");
    }

    private List<String> getPlayerIds(String gameId) {
        List<String> playerIds = new ArrayList<>();

        List players = given().contentType(ContentType.JSON).when()
                .get("/games/{gameId}/players", gameId).then()
                .statusCode(200)
                .extract().jsonPath().getList("");

        for (Object playerObject : players) {
            Map playerMap = (Map)playerObject;

            String playerId = (String)playerMap.get("id");

            playerIds.add(playerId);
        }

        return playerIds;
    }

    private void startGame(String gameId) {
        Map<String, String> modifiedGame = new HashMap<>();

        modifiedGame.put("status", "STARTED");

        given().contentType(ContentType.JSON).body(modifiedGame).when()
                .patch("/games/{gameId}", gameId).then()
                .statusCode(200)
                .body("status", equalTo("STARTED"));
    }

    private String createOnePlayerGame() {
        String gameId;

        /* Get the id of the first map */
        String mapId = getIdOfFirstMap();

        /* Create the map body */
        Map<String, Object> game = new HashMap<>();

        List<Map<String, String>> players = new ArrayList<>();

        Map<String, String> player0 = new HashMap<>();

        player0.put("name", "Player 0");
        player0.put("color", "#000000");
        player0.put("nation", "ROMANS");

        players.add(player0);

        game.put("mapId", mapId);
        game.put("players", players);

        /* Create the game */
        gameId = given().contentType(ContentType.JSON).body(game).when()
                .post("/games").then()
                .statusCode(201)

                .extract().jsonPath().getString("id");

        return gameId;
    }

    private String getIdOfFirstMap() {
        return given().contentType(ContentType.JSON)
                .when().get("/maps").then()
                .statusCode(200)

                .extract().jsonPath().getString("[0].id");
    }

    private void setResourceLevelForGame(String gameId0, String resourceLevel) {
        Map<String, String> resourceModification = new HashMap<>();

        resourceModification.put("resources", resourceLevel);

        given().contentType(ContentType.JSON).body(resourceModification).when()
                .patch("/games/{gameId}", gameId0).then()
                .statusCode(200)
                .body("resources", equalTo(resourceLevel));
    }

    private String placeFlag(String gameId, String playerId, int x, int y) {
        String flagId;
        Map<String, Object> flag = new HashMap<>();

        flag.put("x", x);
        flag.put("y", y);

        /* Create the flag */
        flagId = given().contentType(ContentType.JSON).body(flag).when()
                .post("/games/{gameId}/players/{playerId}/flags", gameId, playerId).then()
                .statusCode(201)
                .extract().jsonPath().getString("id");

        assertNotNull(flagId);

        return flagId;
    }
}

