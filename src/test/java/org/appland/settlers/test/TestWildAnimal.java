/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WildAnimal;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Vegetation.BUILDABLE_MOUNTAIN;
import static org.appland.settlers.model.Vegetation.DEEP_WATER;
import static org.appland.settlers.model.Vegetation.DESERT;
import static org.appland.settlers.model.Vegetation.GRASS;
import static org.appland.settlers.model.Vegetation.LAVA;
import static org.appland.settlers.model.Vegetation.MOUNTAIN;
import static org.appland.settlers.model.Vegetation.MOUNTAIN_MEADOW;
import static org.appland.settlers.model.Vegetation.SAVANNAH;
import static org.appland.settlers.model.Vegetation.SNOW;
import static org.appland.settlers.model.Vegetation.STEPPE;
import static org.appland.settlers.model.Vegetation.SWAMP;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author johan
 */
public class TestWildAnimal {

    @Test
    public void testWildAnimalsAppearSpontaneously() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that wild animals appear spontaneously */
        int animalsAtStart = map.getWildAnimals().size();

        for (int i = 0; i < 500; i++) {

            if (map.getWildAnimals().size() > animalsAtStart) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.getWildAnimals().size() > animalsAtStart);
    }

    @Test
    public void testWildAnimalMovesSometimes() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Wait for an animal to appear */
        WildAnimal animal = Utils.waitForAnimalToAppear(map);

        /* Verify that the animal moves sometimes */
        Point origin = animal.getPosition();

        for (int i = 0; i < 100; i++) {

            /* Break if the animal has moved */
            if (!origin.equals(animal.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotEquals(animal.getPosition(), origin);
    }

    @Test
    public void testWildAnimalDoesNotGetStuck() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Wait for an animal to appear */
        WildAnimal animal = Utils.waitForAnimalToAppear(map);

        /* Verify that the animal moves sometimes */
        Point oldPlace = animal.getPosition();
        int timeInSamePlace = 0;

        for (int i = 0; i < 1000; i++) {

            /* Check if the animal has moved */
            if (!oldPlace.equals(animal.getPosition())) {
                timeInSamePlace = 0;
                oldPlace = animal.getPosition();
            } else {
                timeInSamePlace++;
            }

            assertTrue(timeInSamePlace < 80);

            map.stepTime();
        }
    }

    @Test
    public void testWildAnimalsAreCreatedAcrossTheMap() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Verify that animals are created across the map */
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        Set<WildAnimal> currentAnimals = new HashSet<>();

        for (int i = 0; i < 5000; i++) {

            /* Find min and max of newly created animals */
            for (WildAnimal worker : map.getWildAnimals()) {

                /* Filter already handled animals */
                if (currentAnimals.contains(worker)) {
                    continue;
                }

                Point point = worker.getPosition();

                /* Update min and max */
                if (point.x < minX) {
                    minX = point.x;
                }

                if (point.x > maxX) {
                    maxX = point.x;
                }

                if (point.y < minY) {
                    minY = point.y;
                }

                if (point.y > maxY) {
                    maxY = point.y;
                }

                /* Remember that this animal has been handled */
                currentAnimals.add(worker);
            }

            map.stepTime();
        }

        assertTrue(maxX - minX > 20);
        assertTrue(maxY - minY > 20);

        assertFalse(map.getWildAnimals().isEmpty());
    }

    @Test
    public void testWildAnimalsStayAliveUnlessKilled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Wait for a wild animal to appear */
        WildAnimal animal = Utils.waitForAnimalToAppear(map);

        /* Verify that the animal stays alive */
        for (int i = 0; i < 20000; i++) {

            /* Find min and max of newly created animals */
            assertTrue(map.getWildAnimals().contains(animal));

            map.stepTime();
        }
    }

    @Test
    public void testWildAnimalWithNowhereToGoStandsStill() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Fill the map with stones except for one single point */
        Point point0 = new Point(20, 20);
        Point point1 = new Point(23, 23);

        for (int i = 1; i < 40; i++) {
            for (int j = 1; j < 40; j++) {

                if ((i + j) % 2 != 0) {
                    continue;
                }
                Point pointCurrent = new Point(i, j);

                if (point0.equals(pointCurrent) || point1.equals(pointCurrent)) {
                    continue;
                }

                map.placeStone(pointCurrent);
            }
        }

        /* Wait for a wild animal to appear in the single available spot */
        for (int i = 0; i < 1000; i++) {

            if (!map.getWildAnimals().isEmpty() &&
                 map.getWildAnimals().get(0).getPosition().equals(point0)) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.getWildAnimals().size() > 0);

        /* Verify that the wild animal stays in the spot because it has nowhere
          to go
        */
        WildAnimal wildAnimal0 = map.getWildAnimals().get(0);

        assertNotNull(wildAnimal0);
        assertEquals(wildAnimal0.getPosition(), point0);

        for (int i = 0; i < 500; i++) {

            assertEquals(wildAnimal0.getPosition(), point0);

            map.stepTime();
        }
    }

    @Test
    public void testCannotPlaceWildAnimalOnWater() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to water */
        Utils.fillMapWithVegetation(map, WATER);

        /* Verify that no wild animals appear */
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            assertTrue(map.getWildAnimals().isEmpty());
        }
    }

    @Test
    public void testCanPlaceWildAnimalOnGrass() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to grass */
        Utils.fillMapWithVegetation(map, GRASS);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnSwamp() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to swamp */
        Utils.fillMapWithVegetation(map, SWAMP);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to mountain */
        Utils.fillMapWithVegetation(map, MOUNTAIN);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnSavannah() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to savannah */
        Utils.fillMapWithVegetation(map, SAVANNAH);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnSnow() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to snow */
        Utils.fillMapWithVegetation(map, SNOW);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnDesert() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to desert */
        Utils.fillMapWithVegetation(map, DESERT);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnSteppe() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to steppe */
        Utils.fillMapWithVegetation(map, STEPPE);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnMountainMeadow() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to mountain meadow */
        Utils.fillMapWithVegetation(map, MOUNTAIN_MEADOW);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCanPlaceWildAnimalOnBuildableMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to buildable mountain */
        Utils.fillMapWithVegetation(map, BUILDABLE_MOUNTAIN);

        /* Verify that wild animals appear */
        boolean sawWildAnimal = false;
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            if (!map.getWildAnimals().isEmpty()) {
                sawWildAnimal = true;
            }
        }

        assertTrue(sawWildAnimal);
    }

    @Test
    public void testCannotPlaceWildAnimalOnDeepWater() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to deep water */
        Utils.fillMapWithVegetation(map, DEEP_WATER);

        /* Verify that no wild animals appear */
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            assertTrue(map.getWildAnimals().isEmpty());
        }
    }

    @Test
    public void testCannotPlaceWildAnimalOnLava() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Set the whole map to lava */
        Utils.fillMapWithVegetation(map, LAVA);

        /* Verify that no wild animals appear */
        for (int i = 0; i < 3000; i++) {
            map.stepTime();

            assertTrue(map.getWildAnimals().isEmpty());
        }
    }

    /*
     * This tests that wild animals are not placed on any points that are so close to the edge of the map that one of
     * the tiles next to the point they are placed on is missing
     *
     */
    @Test
    public void testWildAnimalsDoesNotAppearTooCloseToTheEdge() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Fill all points that are on the map and surrounded by valid tiles with stones */
        for (int x = 1; x < 20; x++) {
            for (int y = 1; y < 20; y++) {

                /* Filter invalid points */
                if ((x + y) % 2 != 0) {

                    continue;
                }

                Point point = new Point(x, y);

                /* Filter points that are next to at least one invalid/undefined tile */
                if (map.getTerrain().getSurroundingTiles(point).size() != 6) {
                    continue;
                }

                /* Place stone because this is a regular point */
                map.placeStone(point);
            }
        }

        /* Verify that no wild animals appear because there are no suitable points */
        for (int i = 0; i < 1000; i++) {

            assertEquals(map.getWildAnimals().size(), 0);

            map.stepTime();
        }
    }
}
