/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Woodcutter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestPlayer {

    @Test
    public void testPlayerIsSetWhenBuildingIsCreated() {

        /* Create player */
        Player player0 = new Player("Player 0");

        /* Create building with the player set */
        Woodcutter woodcutter0 = new Woodcutter(player0);

        /* Verify that the player is set in the building */
        assertNotNull(woodcutter0.getPlayer());
        assertEquals(woodcutter0.getPlayer(), player0);
    }

    @Test
    public void testNameIsSetInPlayer() {

        /* Create player */
        Player player0 = new Player("Player 0");

        /* Verify that the name is set */
        assertEquals(player0.getName(), "Player 0");
    }

    @Test
    public void testCreatePlayer() {
        
        /* Create player 'player one' */
        Player p = new Player("Player one");
    }
    
    @Test
    public void testCreateHouseWithPlayer() {

        /* Create player 'player one' */
        Player p = new Player("Player one");
        
        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(p);
    }

    @Test
    public void testPlayerIsAlsoSetInBuildingsFlag() {

        /* Create player 'player one' */
        Player p = new Player("Player one");
        
        /* Create house belonging to player one */
        Woodcutter woodcutter0 = new Woodcutter(p);
        
        /* Verify that the building's flag has the player set correctly */
        assertEquals(woodcutter0.getFlag().getPlayer(), p);
    }

    @Test (expected = Exception.class)
    public void testPlayerCanOnlyCreateOneHeadquarter() throws Exception {

        /* Create player 'player one' */
        Player p = new Player("Player one");

        /* Create headquarter belonging to player one */
        Building headquarter0 = new Headquarter(p);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(p);
        GameMap map = new GameMap(players, 50, 50);

        /* Place first headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);

        /* Create second headquarter belonging to player one */
        Building headquarter1 = new Headquarter(p);

        /* Place second headquarter */
        Point point1 = new Point(15, 15);
        map.placeBuilding(headquarter1, point1);
    }

    @Test (expected = Exception.class)
    public void testCannotPlaceFlagWithoutPlayerWhenPlayersAreDefined() throws Exception {

        /* Create player 'player one' */
        Player p = new Player("Player one");

        /* Create headquarter belonging to player one */
        Building headquarter0 = new Headquarter(p);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(p);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);
    
        /* Verify that a flag cannot be placed without specifying the player*/
        Point point1 = new Point(9, 5);
        Flag flag0 = map.placeFlag(point1);
    }

    @Test
    public void testCannotCreateRoadWithoutPlayerWhenPlayersAreDefined() throws Exception {

        /* Create player 'player one' */
        Player player = new Player("Player one");

        /* Create headquarter belonging to player one */
        Building headquarter0 = new Headquarter(player);

        /* Create game map with one player */
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        map.placeBuilding(headquarter0, point0);
    
        /* Place flag */
        Point point1 = new Point(9, 5);
        Flag flag0 = map.placeFlag(player, point1);
    
        /* Verify that roads cannot be placed without specifying the player */
        int roads = map.getRoads().size();

        try {
            map.placeAutoSelectedRoad(headquarter0.getFlag(), flag0);
            assertFalse(true);
        } catch (Exception e) {}
    
        assertEquals(map.getRoads().size(), roads);

        try {
            map.placeAutoSelectedRoad(headquarter0.getFlag().getPosition(), point1);
            assertFalse(true);
        } catch (Exception e) {}
    
        assertEquals(map.getRoads().size(), roads);

        try {
            Point point2 = new Point(7, 5);
            
            map.placeRoad(headquarter0.getFlag().getPosition(), point2, flag0.getPosition());
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(map.getRoads().size(), roads);

        try {
            Point point2 = new Point(7, 5);
            
            List<Point> points = new ArrayList<>();
            
            points.add(headquarter0.getFlag().getPosition());
            points.add(point2);
            points.add(flag0.getPosition());
            map.placeRoad(points);
            assertFalse(true);
        } catch (Exception e) {}

        assertEquals(map.getRoads().size(), roads);
    }
}
