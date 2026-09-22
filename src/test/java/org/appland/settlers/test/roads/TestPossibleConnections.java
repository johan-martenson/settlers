package org.appland.settlers.test.roads;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestPossibleConnections {

    @Test
    public void testMiscPossibleConnections() throws Exception {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 80, 81);

        // Place headquarters
        var point0 = new Point(30, 30);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place trees
        var point1 = new Point(24, 24);
        var point2 = new Point(23, 25);
        var point3 = new Point(23, 23);
        var point4 = new Point(21, 23);
        var point5 = new Point(19, 25);
        map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        map.placeTree(point3, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        map.placeTree(point5, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Place woodcutter
        var point6 = new Point(21, 25);
        Utils.printPlayersLand(map.getPlayers(), List.of(point6));

        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point6);

        // Verify that there is only one place to build a road towards from the woodcutter's flag
        var possibleRoadConnections0 = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, woodcutter0.getFlag().getPosition());

        assertEquals(1, possibleRoadConnections0.size());
        assertEquals(woodcutter0.getPosition().downLeft(), possibleRoadConnections0.getFirst());
        assertFalse(map.arePointsConnectedByRoads(woodcutter0.getPosition().downRight(), headquarter.getFlag().getPosition()));

        // Verify that there is only one possible point to build a road towards from that point (which is back)
        Utils.printPlayersLand(map.getPlayers(), List.of(woodcutter0.getPosition().downLeft()));
        var possibleRoadConnections1 = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, woodcutter0.getPosition().downLeft());

        assertEquals(1, possibleRoadConnections1.size());
        assertEquals(woodcutter0.getPosition().downRight(), possibleRoadConnections1.getFirst());
        assertFalse(map.arePointsConnectedByRoads(woodcutter0.getPosition().downLeft(), headquarter.getFlag().getPosition()));
    }
}
