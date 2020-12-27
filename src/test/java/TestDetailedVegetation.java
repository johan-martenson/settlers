import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Vegetation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.junit.Assert.assertEquals;

public class TestDetailedVegetation {

    @Test
    public void testSetAndGetDetailedVegetationAroundPoint() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that each detailed vegetation can be set and read */
        Point point0 = new Point(10, 20);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_1);
        map.setDetailedVegetationAbove(point0, DetailedVegetation.MOUNTAIN_2);
        map.setDetailedVegetationUpRight(point0, DetailedVegetation.MOUNTAIN_3);
        map.setDetailedVegetationDownRight(point0, DetailedVegetation.MOUNTAIN_4);
        map.setDetailedVegetationBelow(point0, DetailedVegetation.MEADOW_1);
        map.setDetailedVegetationDownLeft(point0, DetailedVegetation.MEADOW_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_1);
        assertEquals(map.getDetailedVegetationAbove(point0), DetailedVegetation.MOUNTAIN_2);
        assertEquals(map.getDetailedVegetationUpRight(point0), DetailedVegetation.MOUNTAIN_3);
        assertEquals(map.getDetailedVegetationDownRight(point0), DetailedVegetation.MOUNTAIN_4);
        assertEquals(map.getDetailedVegetationBelow(point0), DetailedVegetation.MEADOW_1);
        assertEquals(map.getDetailedVegetationDownLeft(point0), DetailedVegetation.MEADOW_2);
    }

    @Test
    public void testSetAndGetEachDetailedVegetationType() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that each detailed vegetation can be set and read */
        Point point0 = new Point(10, 20);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SAVANNAH);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SAVANNAH);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_1);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_1);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SNOW);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SNOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SWAMP);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SWAMP);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.DESERT_1);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.DESERT_1);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.WATER);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.BUILDABLE_WATER);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.BUILDABLE_WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.DESERT_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.DESERT_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_1);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_1);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_3);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_3);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_3);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_3);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_4);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_4);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.STEPPE);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.STEPPE);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.FLOWER_MEADOW);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.FLOWER_MEADOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MAGENTA);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MAGENTA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_MEADOW);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_MEADOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.WATER_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.WATER_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_2);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA_2);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_3);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA_3);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_4);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA_4);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.BUILDABLE_MOUNTAIN);

        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.BUILDABLE_MOUNTAIN);
    }

    @Test
    public void testSetDetailedVegetationTypeAndGetSimpleVegetationType() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that each detailed vegetation can be set and read */
        Point point0 = new Point(10, 20);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SAVANNAH);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SAVANNAH);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_1);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SNOW);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SNOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.SWAMP);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SWAMP);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.DESERT_1);

        assertEquals(map.getTileUpLeft(point0), Vegetation.DESERT);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.WATER);

        assertEquals(map.getTileUpLeft(point0), Vegetation.WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.BUILDABLE_WATER);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SHALLOW_WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.DESERT_2);

        assertEquals(map.getTileUpLeft(point0), Vegetation.DESERT);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_1);

        assertEquals(map.getTileUpLeft(point0), Vegetation.GRASS);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_2);

        assertEquals(map.getTileUpLeft(point0), Vegetation.GRASS);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MEADOW_3);

        assertEquals(map.getTileUpLeft(point0), Vegetation.GRASS);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_2);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_3);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_4);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.STEPPE);

        assertEquals(map.getTileUpLeft(point0), Vegetation.STEPPE);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.FLOWER_MEADOW);

        assertEquals(map.getTileUpLeft(point0), Vegetation.GRASS);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA);

        assertEquals(map.getTileUpLeft(point0), Vegetation.LAVA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MAGENTA);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MAGENTA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.MOUNTAIN_MEADOW);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN_MEADOW);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.WATER_2);

        assertEquals(map.getTileUpLeft(point0), Vegetation.DEEP_WATER);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_2);

        assertEquals(map.getTileUpLeft(point0), Vegetation.LAVA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_3);

        assertEquals(map.getTileUpLeft(point0), Vegetation.LAVA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.LAVA_4);

        assertEquals(map.getTileUpLeft(point0), Vegetation.LAVA);

        map.setDetailedVegetationUpLeft(point0, DetailedVegetation.BUILDABLE_MOUNTAIN);

        assertEquals(map.getTileUpLeft(point0), Vegetation.BUILDABLE_MOUNTAIN);
    }

    @Test
    public void testSetSimpleVegetationTypeAndGetDetailedVegetationType() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Verify that each detailed vegetation can be set and read */
        Point point0 = new Point(10, 20);

        map.setTileUpLeft(point0, Vegetation.WATER);

        assertEquals(map.getTileUpLeft(point0), Vegetation.WATER);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.WATER);

        map.setTileUpLeft(point0, Vegetation.GRASS);

        assertEquals(map.getTileUpLeft(point0), Vegetation.GRASS);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MEADOW_1);

        map.setTileUpLeft(point0, Vegetation.SWAMP);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SWAMP);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SWAMP);

        map.setTileUpLeft(point0, Vegetation.MOUNTAIN);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_1);

        map.setTileUpLeft(point0, Vegetation.SAVANNAH);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SAVANNAH);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SAVANNAH);

        map.setTileUpLeft(point0, Vegetation.SNOW);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SNOW);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.SNOW);

        map.setTileUpLeft(point0, Vegetation.DESERT);

        assertEquals(map.getTileUpLeft(point0), Vegetation.DESERT);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.DESERT_1);

        map.setTileUpLeft(point0, Vegetation.DEEP_WATER);

        assertEquals(map.getTileUpLeft(point0), Vegetation.DEEP_WATER);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.WATER_2);

        map.setTileUpLeft(point0, Vegetation.SHALLOW_WATER);

        assertEquals(map.getTileUpLeft(point0), Vegetation.SHALLOW_WATER);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.BUILDABLE_WATER);

        map.setTileUpLeft(point0, Vegetation.STEPPE);

        assertEquals(map.getTileUpLeft(point0), Vegetation.STEPPE);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.STEPPE);

        map.setTileUpLeft(point0, Vegetation.LAVA);

        assertEquals(map.getTileUpLeft(point0), Vegetation.LAVA);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.LAVA);

        map.setTileUpLeft(point0, Vegetation.MAGENTA);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MAGENTA);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MAGENTA);

        map.setTileUpLeft(point0, Vegetation.MOUNTAIN_MEADOW);

        assertEquals(map.getTileUpLeft(point0), Vegetation.MOUNTAIN_MEADOW);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.MOUNTAIN_MEADOW);

        map.setTileUpLeft(point0, Vegetation.BUILDABLE_MOUNTAIN);

        assertEquals(map.getTileUpLeft(point0), Vegetation.BUILDABLE_MOUNTAIN);
        assertEquals(map.getDetailedVegetationUpLeft(point0), DetailedVegetation.BUILDABLE_MOUNTAIN);
    }
}
