package org.appland.settlers.computer.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.appland.settlers.computer.util.PathFinding.findWayToConnectToBuilding;
import static org.appland.settlers.computer.util.Placement.placeRemainingRoads;
import static org.appland.settlers.test.Utils.printPlayersLand;
import static org.junit.Assert.*;

public class TestGamePlayUtil {

    @Test
    public void testConnectQuarryToHeadquarters() throws InvalidUserActionException {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 101);

        // Place headquarters
        var point0 = new Point(20, 20);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(23, 17);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Place road to connect
        var road0 = map.placeRoad(
                player0,
                List.of(
                        new Point(21, 19),
                        new Point(20, 18),
                        new Point(21, 17),
                        new Point(22, 16),
                        new Point(24, 16)
                        ));

        // Place flag on the road
        var flag0 = map.placeFlag(player0, new Point(21, 17));

        // Place sawmill
        var point2 = new Point(19, 13);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point2);

        // Place road
        var road1 = map.placeRoad(
                player0,
                List.of(
                        new Point(20, 12),
                        new Point(21, 13),
                        new Point(22, 14),
                        new Point(23, 15),
                        new Point(24, 16)
                )
        );

        // Place flag
        var flag1 = map.placeFlag(player0, new Point(22, 14));

        // Wait for the sawmill and the woodcutter to get constructed
        Utils.waitForBuildingsToBeConstructed(woodcutter0, sawmill0);

        // Place quarry
        var point3 = new Point(25, 13);
        var quarry0 = map.placeBuilding(new Quarry(player0), point3);

        // Add trees
        printPlayersLand(map.getPlayers(), List.of());
        var point4 = new Point(25, 15);
        var point5 = new Point(26, 16);
        var point6 = new Point(26, 14);
        map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        map.placeTree(point5, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        map.placeTree(point6, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Verify that an automatically selected road can be placed from the quarry to the headquarters
        printPlayersLand(map.getPlayers(), List.of());

        var path = findWayToConnectToBuilding(quarry0.getFlag().getPosition(), headquarter0.getPosition(), player0, 1);

        assertNotNull(path);
        assertEquals(9, path.size());
        assertFalse(map.isRoadAtPoint(new Point(23, 13)));
        System.out.println(path);
        printPlayersLand(map.getPlayers(), path);

        var newRoads = placeRemainingRoads(path, player0);
    }
}
