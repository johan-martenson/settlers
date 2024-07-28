package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTreeConservationProgram {

    @Test
    public void testTreeConservationProgramIsEnabledByDefault() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that the tree conservation program is enabled by default */
        assertTrue(player0.isTreeConservationProgramEnabled());
    }

    @Test
    public void testCanEnableTreeConservationProgram() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that the tree conservation program can be enabled */
        player0.enableTreeConservationProgram();

        assertTrue(player0.isTreeConservationProgramEnabled());
    }

    @Test
    public void testCanDisableTreeConservationProgram() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that the tree conservation program can be enabled */
        player0.disableTreeConservationProgram();

        assertFalse(player0.isTreeConservationProgramEnabled());
    }

    @Test
    public void testTreeConservationProgramIsActivatedWhenAmountOfPlanksIsLow() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
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
        assertTrue(player0.isTreeConservationProgramEnabled());
        assertTrue(player0.getMessages().isEmpty());
        assertFalse(player0.isTreeConservationProgramActive());

        Utils.waitForTreeConservationProgramToActivate(player0);

        assertTrue(player0.isTreeConservationProgramActive());
    }

    @Test
    public void testTreeConservationProgramDoesNotActivateIfItIsDisabled() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Set the amount of planks to just above the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 11);
        Utils.adjustInventoryTo(headquarter0, STONE, 5);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that the tree conservation program does not activate if it's disabled */
        player0.disableTreeConservationProgram();

        assertFalse(player0.isTreeConservationProgramEnabled());
        assertFalse(player0.isTreeConservationProgramActive());

        Utils.waitForBuildingToBeConstructed(armory0);

        assertTrue(armory0.isReady());
        assertTrue(headquarter0.getAmount(PLANK) < 10);
        assertFalse(player0.isTreeConservationProgramActive());
    }

    @Test
    public void testTreeConservationProgramIsDeactivatedWhenThereAreEnoughPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
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
