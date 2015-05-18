/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WildAnimal;
import static org.junit.Assert.assertFalse;
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

            assertTrue(timeInSamePlace < 60);

            map.stepTime();
        }
    }
}
