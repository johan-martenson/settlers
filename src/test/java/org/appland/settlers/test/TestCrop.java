package org.appland.settlers.test;

import org.appland.settlers.assets.CropType;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.awt.Color.BLUE;
import static org.junit.Assert.assertTrue;

public class TestCrop {

    @Test
    public void testFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvest() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Wait for the farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(farm);

        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to plant a crop */
        Utils.waitForFarmerToPlantCrop(map, farmer);

        assertTrue(map.isCropAtPoint(farmer.getPosition()));

        Crop crop = map.getCropAtPoint(farmer.getPosition());

        /* Verify that the crop is of type 1 or type 2 */
        assertTrue(crop.getType() == CropType.TYPE_1 || crop.getType() == CropType.TYPE_2);
    }

    @Test
    public void testFarmerPlantsBothTypesOfCrops() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Wait for the farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(farm);

        Farmer farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to plant 20 crops */
        var crops = new HashMap<CropType, Integer>();

        for (int i = 0; i < 20; i++) {
            Utils.waitForFarmerToPlantCrop(map, farmer);

            assertTrue(map.isCropAtPoint(farmer.getPosition()));

            Crop crop = map.getCropAtPoint(farmer.getPosition());

            int amount = crops.getOrDefault(crop.getType(), 0);

            crops.put(crop.getType(), amount + 1);
        }

        /* Verify that the farmer planted both types of crops */
        assertTrue(crops.getOrDefault(CropType.TYPE_1, 0) > 0);
        assertTrue(crops.getOrDefault(CropType.TYPE_2, 0) > 0);
    }

    // TODO: test that the farmer plants a mix of type 1 and type 2
}
