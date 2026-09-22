package org.appland.settlers.test.pigfarm;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Pig;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPigs {

    @Test
    public void testUnoccupiedPigFarmHasNoPig() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        var road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        // Remove the road to the pig farm to keep it unoccupied
        map.removeRoad(road0);

        // Verify that the unoccupied pig farm has no pigs
        Utils.fastForward(200, map, () -> {
            assertTrue(pigFarm.isUnoccupied());
            assertEquals(0, pigFarm.getPigs().size());
        });
    }

    @Test
    public void testPigFarmShowsOnePigAt0To20PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Fill the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        // Let the pig breeder work for the first productivity measurement
        Utils.fastForward(160, map);

        // The first 16-second measurement is averaged with six zero measurements
        assertTrue(pigFarm.getProductivity() >= 0);
        assertTrue(pigFarm.getProductivity() < 20);

        // Verify that one large pig is always visible at low productivity
        assertEquals(1, pigFarm.getPigs().size());
        assertEquals(PigFarm.StyeSlot.SLOT_1, pigFarm.getPigs().getFirst().slot());
        assertEquals(Pig.PigAge.ADULT, pigFarm.getPigs().getFirst().getAge());
    }

    @Test
    public void testPigFarmShowsTwoPigsAt20To40PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Fill the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        // Let the pig breeder complete two productivity measurements
        Utils.fastForwardUntil(map, () -> pigFarm.getProductivity() >= 20);

        // The rolling average is now (100 + 100) / 7 = 28%
        assertTrue(pigFarm.getProductivity() >= 20);
        assertTrue(pigFarm.getProductivity() < 40);

        // Verify that 20-39% productivity shows one small pig plus the large pig
        assertEquals(2, pigFarm.getPigs().size());
        assertEquals(PigFarm.StyeSlot.SLOT_1, pigFarm.getPigs().getFirst().slot());
        assertEquals(Pig.PigAge.ADULT, pigFarm.getPigs().getFirst().getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_2, pigFarm.getPigs().get(1).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(1).getAge());
    }

    @Test
    public void testPigFarmShowsThreePigsAt40To60PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Fill the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        // Let the pig breeder complete three productivity measurements
        Utils.fastForwardUntil(map, () -> pigFarm.getProductivity() >= 40);

        // The rolling average is now (100 + 100 + 100) / 7 = 42%
        System.out.println(pigFarm.getProductivity());
        assertTrue(pigFarm.getProductivity() >= 40);
        assertTrue(pigFarm.getProductivity() < 60);

        // Verify that 40-59% productivity shows two small pigs plus the large pig
        assertEquals(3, pigFarm.getPigs().size());
        assertEquals(PigFarm.StyeSlot.SLOT_1, pigFarm.getPigs().getFirst().slot());
        assertEquals(Pig.PigAge.ADULT, pigFarm.getPigs().getFirst().getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_2, pigFarm.getPigs().get(1).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(1).getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_3, pigFarm.getPigs().get(2).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(2).getAge());
    }

    @Test
    public void testPigFarmShowsFourPigsAt60To80PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Fill the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        // Let the pig breeder complete four productivity measurements
        Utils.fastForward(800, map);

        // The rolling average is now (100 + 100 + 100 + 100) / 7 = 57%
        System.out.println(pigFarm.getProductivity());
        assertTrue(pigFarm.getProductivity() >= 60);
        assertTrue(pigFarm.getProductivity() < 80);

        // Verify that 40-59% productivity shows three small pigs plus the large pig
        assertEquals(4, pigFarm.getPigs().size());
        assertEquals(PigFarm.StyeSlot.SLOT_1, pigFarm.getPigs().getFirst().slot());
        assertEquals(Pig.PigAge.ADULT, pigFarm.getPigs().getFirst().getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_2, pigFarm.getPigs().get(1).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(1).getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_3, pigFarm.getPigs().get(2).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(2).getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_4, pigFarm.getPigs().get(3).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(3).getAge());
    }

    @Test
    public void testPigFarmShowsFivePigsAt80To100PercentProductivity() throws Exception {

        // Create new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), new Point(5, 5));

        // Place pig farm and wait for it to get constructed and occupied
        var pigFarm = map.placeBuilding(new PigFarm(player0), new Point(10, 6));
        map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Fill the inventory of the pig farm
        Utils.deliverCargos(pigFarm, WATER, 6);
        Utils.deliverCargos(pigFarm, WHEAT, 6);

        // Let the pig breeder complete five productivity measurements
        Utils.fastForwardUntil(map, () -> pigFarm.getProductivity() >= 80);

        // The rolling average is now (100 + 100 + 100 + 100 + 100) / 7 = 71%
        System.out.println(pigFarm.getProductivity());
        assertTrue(pigFarm.getProductivity() >= 80);
        assertTrue(pigFarm.getProductivity() <= 100);


        // 60-79% productivity shows four small pigs plus the large pig
        assertEquals(5, pigFarm.getPigs().size());
        assertEquals(PigFarm.StyeSlot.SLOT_1, pigFarm.getPigs().getFirst().slot());
        assertEquals(Pig.PigAge.ADULT, pigFarm.getPigs().getFirst().getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_2, pigFarm.getPigs().get(1).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(1).getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_3, pigFarm.getPigs().get(2).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(2).getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_4, pigFarm.getPigs().get(3).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(3).getAge());
        assertEquals(PigFarm.StyeSlot.SLOT_5, pigFarm.getPigs().get(4).slot());
        assertEquals(Pig.PigAge.PIGLET, pigFarm.getPigs().get(4).getAge());
    }
}

