/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestDefaultPlayer {

    @Test
    public void testNameOfDefaultPlayer() throws Exception {
        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Verify that the name of the default player is correct */
        assertEquals(map.getPlayers().get(0).getName(), "Mai Thi Van Anh");
    }
    
    @Test
    public void testGameMapHasDefaultPlayer() throws Exception {

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Verify that there is one default player */
        assertEquals(map.getPlayers().size(), 1);
    }
    
    @Test
    public void testHouseGetsDefaultPlayerWhenPlaced() throws Exception {

        /* Create house belonging to no player */
        Woodcutter woodcutter0 = new Woodcutter();

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 4);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Place woodcutter */
        Point point1 = new Point(5, 5);
        map.placeBuilding(woodcutter0, point1);

        /* Verify that the buildings have the default player set */
        assertNotNull(headquarter0.getPlayer());
        assertNotNull(woodcutter0.getPlayer());

        assertEquals(headquarter0.getPlayer(), map.getPlayers().get(0));
        assertEquals(woodcutter0.getPlayer(), map.getPlayers().get(0));
    }

    @Test
    public void testFlagGetsDefaultPlayerWhenPlaced() throws Exception {

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 4);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Place flag */
        Point point1 = new Point(5, 5);
        Flag flag0 = map.placeFlag(point1);

        /* Verify that the buildings have the default player set */
        assertNotNull(flag0.getPlayer());

        assertEquals(flag0.getPlayer(), map.getPlayers().get(0));
    }

    @Test
    public void testRoadGetsDefaultPlayerWhenPlaced() throws Exception {

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(4, 6);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Place flag */
        Point point2 = new Point(9, 5);
        Flag flag1 = map.placeFlag(point2);

        /* Place road */
        Point point3 = new Point(7, 5);
        Road road0 = map.placeRoad(headquarter0.getFlag().getPosition(), point3, point2);

        /* Verify that the buildings have the same default player set */
        assertNotNull(road0.getPlayer());
        assertEquals(map.getPlayers().get(0), road0.getPlayer());
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceUnregisteredPlayersHouseOnDefaultPlayersLand() throws Exception {

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 4);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Create house belonging to another player */
        Player player1 = new Player("Player 1");
        Woodcutter woodcutter0 = new Woodcutter(player1);

        /* Place woodcutter */
        Point point1 = new Point(5, 5);
        map.placeBuilding(woodcutter0, point1);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceUnregisteredPlayersRoadOnDefaultPlayersLand() throws Exception {

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 4);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Place flag */
        Point point1 = new Point(15, 3);
        map.placeFlag(point1);

        /* Create other player */
        Player player1 = new Player("Player 1");

        /* Create road belonging to another player */
        Point point2 = new Point(11, 3);
        Point point3 = new Point(13, 3);
        map.placeRoad(player1, point2, point3, point1);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceUnregisteredPlayersFlagOnDefaultPlayersLand() throws Exception {

        /* Create map */
        GameMap map = new GameMap(20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 4);
        Building headquarter0 = map.placeBuilding(new Headquarter(), point0);

        /* Create other player */
        Player player1 = new Player("Player 1");

        /* Place flag */
        Point point1 = new Point(15, 3);
        map.placeFlag(player1, point1);
    }

    @Test
    public void testNoDefaultPlayerIsCreatedWhenExplicitPlayersAreChosen() throws Exception {

        /* Create player list */
        List<Player> players = new LinkedList<>();
        Player player0       = new Player("Player 0");
        players.add(player0);

        /* Create map */
        GameMap map = new GameMap(players, 20, 20);

        /* Verify that there is only one player */
        assertEquals(map.getPlayers().size(), 1);

        /* Verify that the player is correct */
        assertEquals(map.getPlayers().get(0), player0);
    }
}
