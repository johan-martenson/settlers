/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.PigFarm;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.SlaughterHouse;
import org.appland.settlers.model.Storehouse;
import org.appland.settlers.model.actors.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestPigFarm {

    @Test
    public void testPigFarmOnlyNeedsThreePlanksAndThreeStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place pig farm */
        Point point22 = new Point(6, 12);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point22);

        /* Deliver three plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(pigFarm0);

        /* Verify that this is enough to construct the pig farm */
        for (int i = 0; i < 200; i++) {
            assertTrue(pigFarm0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(pigFarm0.isReady());
    }

    @Test
    public void testPigFarmCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place pig farm */
        Point point22 = new Point(6, 12);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point22);

        /* Deliver two plank and three stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(pigFarm0);

        /* Verify that this is not enough to construct the pig farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(pigFarm0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(pigFarm0.isReady());
    }

    @Test
    public void testPigFarmCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place pig farm */
        Point point22 = new Point(6, 12);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point22);

        /* Deliver three planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(plankCargo);
        pigFarm0.putCargo(stoneCargo);
        pigFarm0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(pigFarm0);

        /* Verify that this is not enough to construct the pig farm */
        for (int i = 0; i < 500; i++) {
            assertTrue(pigFarm0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(pigFarm0.isReady());
    }

    @Test
    public void testUnfinishedPigFarmNeedsNoPigBreeder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 11);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point1);

        assertTrue(farm.isPlanned());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedPigFarmNeedsPigBreeder() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point1);

        Utils.constructHouse(farm);

        assertTrue(farm.isReady());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testPigBreederIsAssignedToFinishedPigFarm() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Finish the pig farm */
        Utils.constructHouse(farm);

        /* Run game logic twice, once to place courier and once to place pig breeder */
        Utils.fastForward(2, map);

        /* Verify that there was a pig breeder added */
        assertTrue(map.getWorkers().size() >= 3);

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), PigBreeder.class);
    }

    @Test
    public void testPigBreederIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Finish the pig farm */
        Utils.constructHouse(farm);

        /* Run game logic twice, once to place courier and once to place pig breeder */
        Utils.fastForward(2, map);

        /* Wait for a pig breeder to walk out */
        PigBreeder pigBreeder0 = Utils.waitForWorkerOutsideBuilding(PigBreeder.class, player0);

        /* Verify that the pig breeder is not a soldier */
        assertFalse(pigBreeder0.isSoldier());
    }

    @Test
    public void testPigBreederRestsInPigFarmThenLeaves() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);
        Worker pigBreeder = Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Run the game logic 99 times and make sure the pig breeder stays in the pig farm */
        for (int i = 0; i < 99; i++) {
            assertTrue(pigBreeder.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and make sure the pig breeder goes out of the pig farm */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederFeedsThePigsWhenItHasResources() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);
        PigBreeder pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Deliver wheat and pig to the farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm.putCargo(wheatCargo);
        pigFarm.putCargo(waterCargo);

        /* Let the pig breeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and make sure the pig breeder goes out of the farm */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pig breeder reach the spot and start to feed the pigs */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        for (int i = 0; i < 19; i++) {
            assertTrue(pigBreeder.isFeeding());
            map.stepTime();
        }

        assertTrue(pigBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();

        /* Verify that the pig breeder stopped feeding */
        assertFalse(pigBreeder.isFeeding());
        assertNull(pigBreeder.getCargo());
    }

    @Test
    public void testPigBreederReturnsAfterFeeding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);
        PigBreeder pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Wait for the pig breeder to rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once to let the pig breeder go out to plant */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pig breeder reach the intended spot and start to feed */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        /* Wait for the pig breeder to feed */
        Utils.fastForward(19, map);

        assertTrue(pigBreeder.isFeeding());

        map.stepTime();

        /* Verify that the pig breeder stopped feeding and is walking back to the farm */
        assertFalse(pigBreeder.isFeeding());
        assertTrue(pigBreeder.isTraveling());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertTrue(pigBreeder.getPlannedPath().contains(pigFarm.getFlag().getPosition()));

        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigBreederDeliversPigToFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);
        PigBreeder pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Let the pig breeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and to let the pig breeder go out to feed */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pig breeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        /* Wait for the pig breeder to feed the pigs */
        Utils.fastForward(19, map);

        assertTrue(pigBreeder.isFeeding());

        map.stepTime();

        /* Pig breeder is walking back to farm without carrying a cargo */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());
        assertNull(pigBreeder.getCargo());

        /* Let the pig breeder reach the farm */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isInsideBuilding());

        /* Wait for the pig breeder to prepare the pig */
        for (int i = 0; i < 20; i++) {
            assertNull(pigBreeder.getCargo());
            map.stepTime();
        }

        /* Pig breeder leaves the building to place the cargo at the flag */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());
        assertTrue(pigFarm.getFlag().getStackedCargo().isEmpty());
        assertNotNull(pigBreeder.getCargo());
        assertEquals(pigBreeder.getCargo().getMaterial(), PIG);
        assertEquals(pigBreeder.getTarget(), pigFarm.getFlag().getPosition());

        /* Let the pig breeder reach the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, pigFarm.getFlag().getPosition());

        assertFalse(pigFarm.getFlag().getStackedCargo().isEmpty());
        assertNull(pigBreeder.getCargo());

        /* The pig breeder goes back to the building */
        assertEquals(pigBreeder.getTarget(), pigFarm.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isInsideBuilding());
    }

    @Test
    public void testPigCargoIsDeliveredToGuardHouseUnderConstructionWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Remove all pigs from the headquarters */
        Utils.adjustInventoryTo(headquarter, PIG, 0);

        /* Place slaughter house */
        Point point4 = new Point(10, 4);
        SlaughterHouse slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point4);

        /* Connect the slaughter house to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Place the pig farm */
        Point point1 = new Point(14, 4);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm with the slaughter house */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), slaughterHouse.getFlag());

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);

        Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        /* Wait for the courier on the road between the guard house and the pig farm hut to have a pig cargo */
        Utils.deliverCargo(pigFarm, WATER);
        Utils.deliverCargo(pigFarm, WHEAT);

        Utils.waitForFlagToGetStackedCargo(map, pigFarm.getFlag(), 1);

        assertEquals(pigFarm.getFlag().getStackedCargo().get(0).getMaterial(), PIG);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the slaughter house (and not the headquarters) */
        assertEquals(pigFarm.getAmount(PIG), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), slaughterHouse.getPosition());

        assertEquals(slaughterHouse.getAmount(PIG), 1);
    }

    @Test
    public void testPigIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no stones, planks, or pigs */
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 0);
        Utils.adjustInventoryTo(headquarter, PIG, 0);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the pig farm */
        Point point1 = new Point(14, 4);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the pig farm */
        Utils.deliverCargos(pigFarm, PLANK, 3);
        Utils.deliverCargos(pigFarm, STONE, 3);

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);

        Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        /* Wait for the courier on the road between the storehouse and the pig farm to have a pig cargo */
        Utils.deliverCargo(pigFarm, WATER);
        Utils.deliverCargo(pigFarm, WHEAT);

        Utils.waitForFlagToGetStackedCargo(map, pigFarm.getFlag(), 1);

        assertEquals(pigFarm.getFlag().getStackedCargo().get(0).getMaterial(), PIG);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(PIG), 0);
        assertEquals(pigFarm.getAmount(PIG), 0);
        assertFalse(storehouse.needsMaterial(PIG));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().get(0).getMaterial().equals(PIG));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testPigIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are no pigs, wheat, or water */
        Utils.adjustInventoryTo(headquarter, PIG, 0);
        Utils.adjustInventoryTo(headquarter, WATER, 0);
        Utils.adjustInventoryTo(headquarter, WHEAT, 0);

        /* Place slaughter house */
        Point point4 = new Point(10, 4);
        SlaughterHouse slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point4);

        /* Connect the slaughter house to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        /* Wait for the slaughter house to get constructed */
        Utils.waitForBuildingToBeConstructed(slaughterHouse);

        /* Stop production in the slaughter house to prevent it from consuming its resources */
        slaughterHouse.stopProduction();

        /* Place the pig farm */
        Point point1 = new Point(14, 4);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm with the slaughter house */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), slaughterHouse.getFlag());

        /* Deliver the needed material to construct the pig farm */
        Utils.deliverCargos(pigFarm, PLANK, 3);
        Utils.deliverCargos(pigFarm, STONE, 3);

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm);

        Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        /* Wait for the flag on the road between the slaughter house and the pig farm to have a stone cargo */
        Utils.deliverCargo(pigFarm, WHEAT);
        Utils.deliverCargo(pigFarm, WATER);

        Utils.waitForFlagToGetStackedCargo(map, pigFarm.getFlag(), 1);

        assertEquals(pigFarm.getFlag().getStackedCargo().get(0).getMaterial(), PIG);
        assertEquals(pigFarm.getAmount(WHEAT), 0);
        assertEquals(pigFarm.getAmount(WATER), 0);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no stone is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, PIG, 1);

        assertEquals(slaughterHouse.getCanHoldAmount(PIG) - slaughterHouse.getAmount(PIG), 1);
        assertFalse(slaughterHouse.needsMaterial(PIG));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(PIG), 1);
    }

    @Test
    public void testPigFarmWithoutPigBreederProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new PigFarm(player0), point3);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Construct the house */
        Utils.constructHouse(farm);

        /* Verify that the farm does not produce any wheat */
        for (int i = 0; i < 200; i++) {
            assertTrue(farm.getFlag().getStackedCargo().isEmpty());

            map.stepTime();
        }

        assertTrue(farm.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testPigFarmWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Deliver material to the pig farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        /* Let the pig breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce a new meat cargo */
        Worker worker = pigFarm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (worker.getCargo() != null && worker.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());

        /* Verify that the pig breeder puts the meat cargo at the flag */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(pigFarm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(pigFarm0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the pig farm */
        assertEquals(worker.getTarget(), pigFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        for (int i = 0; i < 1000; i++) {
            if (worker.getCargo() != null && worker.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(pigFarm0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Deliver material to the pig farm */
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Let the pig breeder rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce a new meat cargo */
        Worker worker = pigFarm0.getWorker();

        for (int i = 0; i < 1000; i++) {
            if (worker.getCargo() != null && worker.getPosition().equals(pigFarm0.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(worker.getCargo());

        /* Verify that the pig breeder puts the meat cargo at the flag */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(pigFarm0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(pigFarm0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = pigFarm0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), pigFarm0.getFlag().getPosition());

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), pigFarm0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PIG), amount + 1);
    }

    @Test
    public void testPigBreederGoesBackToStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Destroy the pig farm */
        Worker worker = pigFarm0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederGoesBackOnToStorageOnRoadsIfPossibleWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Destroy the pig farm */
        Worker worker = pigFarm0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : worker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testPigBreederWithoutResourcesProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Utils.constructHouse(pigFarm);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pig breeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Verify that the pig breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertNull(pigBreeder.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testPigBreederWithoutResourcesStaysInHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Utils.constructHouse(pigFarm);

        /* Occupy the pig farm with a pig breeder */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pig breeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Verify that the pig breeder doesn't produce anything */
        for (int i = 0; i < 300; i++) {
            assertTrue(pigBreeder.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testPigBreederFeedsPigsWithWaterAndWheat() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point3 = new Point(10, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        Utils.constructHouse(pigFarm);

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm.putCargo(waterCargo);
        pigFarm.putCargo(wheatCargo);

        /* Assign a pig breeder to the farm */
        PigBreeder pigBreeder = new PigBreeder(player0, map);

        Utils.occupyBuilding(pigBreeder, pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Let the pig breeder rest */
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        /* Step once and to let the pig breeder go out to feed */
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());

        Point point = pigBreeder.getTarget();

        assertTrue(pigBreeder.isTraveling());

        /* Let the pig breeder reach the intended spot */
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isArrived());
        assertTrue(pigBreeder.isAt(point));
        assertTrue(pigBreeder.isFeeding());

        /* Wait for the pig breeder to feed the pigs */
        Utils.fastForward(19, map);

        assertTrue(pigBreeder.isFeeding());

        map.stepTime();

        /* Verify that the pig breeder is done feeding and has consumed the water and wheat */
        assertFalse(pigBreeder.isFeeding());
        assertEquals(pigFarm.getAmount(WATER), 0);
        assertEquals(pigFarm.getAmount(WHEAT), 0);
    }

    @Test
    public void testDestroyedPigFarmIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Destroy the pig farm */
        pigFarm0.tearDown();

        assertTrue(pigFarm0.isBurningDown());

        /* Wait for the pig farm to stop burning */
        Utils.fastForward(50, map);

        assertTrue(pigFarm0.isDestroyed());

        /* Wait for the pig farm to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), pigFarm0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(pigFarm0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));

        map.removeFlag(pigFarm0.getFlag());

        assertNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(8, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));

        pigFarm0.tearDown();

        assertNull(map.getRoad(pigFarm0.getPosition(), pigFarm0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInPigFarmCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(12, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter.getFlag());

        /* Finish the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Assign a worker to the pig farm */
        PigBreeder worker = new PigBreeder(player0, map);

        Utils.occupyBuilding(worker, pigFarm0);

        assertTrue(worker.isInsideBuilding());

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(wheatCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), PIG);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        /* Stop production and verify that no pig is produced */
        pigFarm0.stopProduction();

        assertFalse(pigFarm0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInPigFarmCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(12, 8);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter.getFlag());

        /* Finish the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Deliver resources to the pig farm */
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        pigFarm0.putCargo(waterCargo);
        pigFarm0.putCargo(waterCargo);

        pigFarm0.putCargo(wheatCargo);
        pigFarm0.putCargo(wheatCargo);

        /* Assign a worker to the pig farm */
        PigBreeder worker = new PigBreeder(player0, map);

        Utils.occupyBuilding(worker, pigFarm0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the pig breeder to produce pig */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), PIG);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), pigFarm0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        /* Stop production */
        pigFarm0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the pig farm produces pig again */
        pigFarm0.resumeProduction();

        assertTrue(pigFarm0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedPigBreederHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(20, 14);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm0.getFlag());

        /* Wait for pig breeder to get assigned and leave the headquarter */
        List<PigBreeder> workers = Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        PigBreeder worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place pig farm close to the new border */
        Point point4 = new Point(28, 18);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point4);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        PigBreeder worker = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testPigBreederReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place pig farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new PigFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the pig breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0);

        PigBreeder pigBreeder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof PigBreeder) {
                pigBreeder = (PigBreeder) worker;
            }
        }

        assertNotNull(pigBreeder);
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the pig breeder has started walking */
        assertFalse(pigBreeder.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the pig breeder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, flag0.getPosition());

        assertEquals(pigBreeder.getPosition(), flag0.getPosition());

        /* Verify that the pig breeder returns to the headquarter when it reaches the flag */
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());
    }

    @Test
    public void testPigBreederContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place pig farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new PigFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the pig breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0);

        PigBreeder pigBreeder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof PigBreeder) {
                pigBreeder = (PigBreeder) worker;
            }
        }

        assertNotNull(pigBreeder);
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the farmer has started walking */
        assertFalse(pigBreeder.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the pig breeder continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, flag0.getPosition());

        assertEquals(pigBreeder.getPosition(), flag0.getPosition());

        /* Verify that the pig breeder continues to the final flag */
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, farm0.getFlag().getPosition());

        /* Verify that the pig breeder goes out to pig farm instead of going directly back */
        assertNotEquals(pigBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testPigBreederReturnsToStorageIfPigFarmIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place pig farm */
        Point point2 = new Point(14, 4);
        Building farm0 = map.placeBuilding(new PigFarm(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, farm0.getFlag());

        /* Wait for the pig breeder to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0);

        PigBreeder pigBreeder = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof PigBreeder) {
                pigBreeder = (PigBreeder) worker;
            }
        }

        assertNotNull(pigBreeder);
        assertEquals(pigBreeder.getTarget(), farm0.getPosition());

        /* Wait for the pig breeder to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, flag0.getPosition());

        map.stepTime();

        /* See that the pig breeder has started walking */
        assertFalse(pigBreeder.isExactlyAtPoint());

        /* Tear down the pig farm */
        farm0.tearDown();

        /* Verify that the pig breeder continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, farm0.getFlag().getPosition());

        assertEquals(pigBreeder.getPosition(), farm0.getFlag().getPosition());

        /* Verify that the pig breeder goes back to storage */
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testPigBreederGoesOffroadBackToClosestStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(17, 17);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, storehouse0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederReturnsOffroadAndAvoidsBurningStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(17, 17);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederReturnsOffroadAndAvoidsDestroyedStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(17, 17);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testPigBreederReturnsOffroadAndAvoidsUnfinishedStorageWhenPigFarmIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(17, 17);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Occupy the pig farm */
        Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Place a second storage closer to the pig farm */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the pig farm */
        Worker pigBreeder = pigFarm0.getWorker();

        assertTrue(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getPosition(), pigFarm0.getPosition());

        pigFarm0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(pigBreeder.isInsideBuilding());
        assertEquals(pigBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PIG_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getPosition());

        /* Verify that the pig breeder is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(PIG_BREEDER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place pig farm */
        Point point26 = new Point(17, 17);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point26);

        /* Place road to connect the headquarter and the pig farm */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm0.getFlag());

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(PigBreeder.class, 1, player0).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        /* Tear down the building */
        pigFarm0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), pigFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testPigFarmWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Verify that the productivity is 0% when the pig farm doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(pigFarm.getFlag().getStackedCargo().isEmpty());
            assertNull(pigBreeder0.getCargo());
            assertEquals(pigFarm.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testPigFarmWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm.getFlag());

        /* Make the pig farm produce some pigs with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(pigFarm.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }

            assertEquals(pigFarm.getProductivity(), 100);
        }
    }

    @Test
    public void testPigFarmLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm.getFlag());

        /* Make the pig farm produce some pigs with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(pigFarm.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(pigFarm.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedPigFarmHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm);

        /* Verify that the unoccupied pig farm is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(pigFarm.getProductivity(), 0);

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }

            map.stepTime();
        }
    }

    @Test
    public void testPigFarmCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(10, 10);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm0);

        /* Verify that the pig farm can produce */
        assertTrue(pigFarm0.canProduce());
    }

    @Test
    public void testPigFarmReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(6, 12);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Construct the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Verify that the reported output is correct */
        assertEquals(pigFarm0.getProducedMaterial().length, 1);
        assertEquals(pigFarm0.getProducedMaterial()[0], PIG);
    }

    @Test
    public void testPigFarmReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(6, 12);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(pigFarm0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(pigFarm0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(pigFarm0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(pigFarm0.getCanHoldAmount(PLANK), 3);
        assertEquals(pigFarm0.getCanHoldAmount(STONE), 3);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(pigFarm0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testPigFarmReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(6, 12);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Construct the pig farm */
        Utils.constructHouse(pigFarm0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(pigFarm0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(pigFarm0.getTypesOfMaterialNeeded().contains(WATER));
        assertTrue(pigFarm0.getTypesOfMaterialNeeded().contains(WHEAT));
        assertEquals(pigFarm0.getCanHoldAmount(WATER), 6);
        assertEquals(pigFarm0.getCanHoldAmount(WHEAT), 6);

        for (Material material : Material.values()) {
            if (material == WATER || material == WHEAT) {
                continue;
            }

            assertEquals(pigFarm0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testPigFarmWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(16, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the pig farm to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(pigFarm);
        Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        /* Give material to the pig farm */
        Utils.putCargoToBuilding(pigFarm, WHEAT);
        Utils.putCargoToBuilding(pigFarm, WATER);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, pigFarm.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the pig farm waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(pigFarm.getFlag().getStackedCargo().size(), 8);
            assertNull(pigFarm.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the pig farm with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(pigFarm.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(pigFarm.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(pigFarm.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, pigFarm.getWorker(), PIG);
    }

    @Test
    public void testPigFarmDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(16, 6);
        PigFarm pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the pig farm to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(pigFarm);
        Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        /* Give material to the pig farm */
        Utils.putCargoToBuilding(pigFarm, WHEAT);
        Utils.putCargoToBuilding(pigFarm, WHEAT);
        Utils.putCargoToBuilding(pigFarm, WATER);
        Utils.putCargoToBuilding(pigFarm, WATER);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, pigFarm.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The pig farm waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(pigFarm.getFlag().getStackedCargo().size(), 8);
            assertNull(pigFarm.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the pig farm with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(pigFarm.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(pigFarm.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(pigFarm.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, pigFarm.getWorker(), PIG);

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(pigFarm.getWorker().getTarget(), pigFarm.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigFarm.getWorker(), pigFarm.getFlag().getPosition());

        assertEquals(pigFarm.getFlag().getStackedCargo().size(), 8);

        /* Verify that the pig farm doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(pigFarm.getFlag().getStackedCargo().size(), 8);
            assertNull(pigFarm.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenPigDeliveryAreBlockedPigFarmFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Place road to connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Wait for the pig farm to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(pigFarm0);

        Worker pigBreeder0 = Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm0);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm0);
        assertEquals(pigFarm0.getWorker(), pigBreeder0);

        /* Add a lot of material to the headquarter for the pig farm to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Block storage of pigs */
        headquarter0.blockDeliveryOfMaterial(PIG);

        /* Verify that the pig farm puts eight pigs on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, pigFarm0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder0, pigFarm0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(pigFarm0.getFlag().getStackedCargo().size(), 8);

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), PIG);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndPigFarmIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place pig farm */
        Point point2 = new Point(18, 6);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the pig farm */
        Road road1 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the pig farm and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, pigFarm0);

        /* Add a lot of material to the headquarter for the pig farm to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Wait for the pig farm and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, pigFarm0);

        Worker pigBreeder0 = pigFarm0.getWorker();

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm0);
        assertEquals(pigFarm0.getWorker(), pigBreeder0);

        /* Verify that the worker goes to the storage when the pig farm is torn down */
        headquarter0.blockDeliveryOfMaterial(PIG_BREEDER);

        pigFarm0.tearDown();

        map.stepTime();

        assertFalse(pigBreeder0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder0, pigFarm0.getFlag().getPosition());

        assertEquals(pigBreeder0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, pigBreeder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(pigBreeder0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndPigFarmIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place pig farm */
        Point point2 = new Point(18, 6);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the pig farm */
        Road road1 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the pig farm and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, pigFarm0);

        /* Add a lot of material to the headquarter for the pig farm to consume */
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        /* Wait for the pig farm and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, pigFarm0);

        Worker pigBreeder0 = pigFarm0.getWorker();

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm0);
        assertEquals(pigFarm0.getWorker(), pigBreeder0);

        /* Verify that the worker goes to the storage off-road when the pig farm is torn down */
        headquarter0.blockDeliveryOfMaterial(PIG_BREEDER);

        pigFarm0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(pigBreeder0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder0, pigFarm0.getFlag().getPosition());

        assertEquals(pigBreeder0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(pigBreeder0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, PIG_BREEDER, 1);

        assertEquals(headquarter0.getAmount(PIG_BREEDER), 1);

        headquarter0.pushOutAll(PIG_BREEDER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(PigBreeder.class, player0);

            assertEquals(headquarter0.getAmount(PIG_BREEDER), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, PIG_BREEDER, 1);

        headquarter0.blockDeliveryOfMaterial(PIG_BREEDER);
        headquarter0.pushOutAll(PIG_BREEDER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(PigBreeder.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Place road to connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the pig farm to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(pigFarm0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(PIG_BREEDER);

        Worker worker = pigFarm0.getWorker();

        pigFarm0.tearDown();

        assertEquals(worker.getPosition(), pigFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, pigFarm0.getFlag().getPosition());

        assertEquals(worker.getPosition(), pigFarm0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), pigFarm0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        PigFarm pigFarm0 = map.placeBuilding(new PigFarm(player0), point1);

        /* Place road to connect the pig farm with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, pigFarm0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the pig farm to get constructed */
        Utils.waitForBuildingToBeConstructed(pigFarm0);

        /* Wait for a pig breeder to start walking to the pig farm */
        PigBreeder pigBreeder = Utils.waitForWorkerOutsideBuilding(PigBreeder.class, player0);

        /* Wait for the pig breeder to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the pig breeder goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(pigBreeder.getTarget(), pigFarm0.getPosition());

        headquarter0.blockDeliveryOfMaterial(PIG_BREEDER);

        pigFarm0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, pigFarm0.getFlag().getPosition());

        assertEquals(pigBreeder.getPosition(), pigFarm0.getFlag().getPosition());
        assertNotEquals(pigBreeder.getTarget(), headquarter0.getPosition());
        assertFalse(pigBreeder.isInsideBuilding());
        assertNull(pigFarm0.getWorker());
        assertNotNull(pigBreeder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, pigBreeder, pigBreeder.getTarget());

        Point point = pigBreeder.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(pigBreeder.isDead());
            assertEquals(pigBreeder.getPosition(), point);
            assertTrue(map.getWorkers().contains(pigBreeder));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(pigBreeder));
    }
}
