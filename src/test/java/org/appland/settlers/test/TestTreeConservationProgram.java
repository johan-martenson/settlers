package org.appland.settlers.test;

import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTreeConservationProgram {

    @Test
    public void testTreeConservationProgramIsActivatedWhenAmountOfPlanksIsLow() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that a message is sent */
        assertTrue(player0.getMessages().isEmpty());
        assertFalse(player0.isTreeConservationProgramActive());

        Utils.waitForTreeConservationProgramToActivate(player0);

        assertTrue(player0.isTreeConservationProgramActive());
    }

    @Test
    public void testTreeConservationProgramIsDeactivatedWhenThereAreEnoughPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertFalse(player0.isTreeConservationProgramActive());

        Utils.waitForTreeConservationProgramToActivate(player0);

        assertTrue(player0.isTreeConservationProgramActive());

        /* Verify that the tree conservation program is deactivated when more planks are added to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);

        map.stepTime();

        assertFalse(player0.isTreeConservationProgramActive());
    }

    @Test
    public void testTreeConservationProgramIsNotActivatedWhenASecondStorehouseHasEnoughPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Storehouse storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Finish construction of the storehouse */
        Utils.constructHouse(storage0);

        /* Set the amount of planks to the limit for the tree conservation program in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Put enough planks into the second storehouse to prevent tree conservation */
        Utils.adjustInventoryTo(storage0, PLANK, 50);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the tree conservation program is not activated */
        for (int i = 0; i < 500; i++) {
            assertFalse(player0.isTreeConservationProgramActive());

            if (armory0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(player0.isTreeConservationProgramActive());
    }
}
