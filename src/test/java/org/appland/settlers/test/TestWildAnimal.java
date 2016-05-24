/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WildAnimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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

        assertFalse(animal.getPosition().equals(origin));
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
            for (WildAnimal w : map.getWildAnimals()) {

                /* Filter already handled animals */
                if (currentAnimals.contains(w)) {
                    continue;
                }

                Point p = w.getPosition();

                /* Update min and max */
                if (p.x < minX) {
                    minX = p.x;
                }

                if (p.x > maxX) {
                    maxX = p.x;
                }

                if (p.y < minY) {
                    minY = p.y;
                }

                if (p.y > maxY) {
                    maxY = p.y;
                }

                /* Remember that this animal has been handled */
                currentAnimals.add(w);
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
}
