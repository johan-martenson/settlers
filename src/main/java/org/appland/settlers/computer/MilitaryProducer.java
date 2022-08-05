package org.appland.settlers.computer;

import java.util.ArrayList;
import java.util.List;

import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Brewery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Farm;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Well;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.IronSmelter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import static org.appland.settlers.model.Size.LARGE;

import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
public class MilitaryProducer implements ComputerPlayer {

    private final Player            controlledPlayer;
    private final List<IronSmelter> ironSmelters;
    private final List<Armory>      armories;
    private final List<Brewery>     breweries;
    private final List<Farm>        farms;
    private final List<Well>        wells;

    private GameMap  map;
    private State    state;
    private Building headquarter;

    private enum State {
        INITIALIZING,
        NEEDS_IRON_SMELTER,
        NEEDS_ARMORY,
        NEEDS_FARM,
        NEEDS_WELL,
        NEEDS_BREWERY,
        WAITING_FOR_IRON_SMELTER,
        WAITING_FOR_ARMORY,
        WAITING_FOR_FARM,
        WAITING_FOR_WELL,
        WAITING_FOR_BREWERY,
        DONE,
    }

    public MilitaryProducer(Player player, GameMap m) {
        controlledPlayer = player;
        map              = m;

        ironSmelters = new ArrayList<>();
        armories     = new ArrayList<>();
        breweries    = new ArrayList<>();
        farms        = new ArrayList<>();
        wells        = new ArrayList<>();

        state = State.INITIALIZING;
    }

    @Override
    public void turn() throws Exception {

        if (state == State.INITIALIZING) {

            for (Building building : controlledPlayer.getBuildings()) {
                if (building instanceof Headquarter) {
                    headquarter = building;

                    break;
                }
            }

            if (headquarter != null) {
                state = State.NEEDS_IRON_SMELTER;
            }
        } else if (state == State.NEEDS_IRON_SMELTER) {

            /* Determine if there already are iron smelters built */
            if (Utils.buildingTypeExists(controlledPlayer.getBuildings(), IronSmelter.class)) {
        	    ironSmelters.addAll(Utils.getBuildingsOfType(controlledPlayer.getBuildings(), IronSmelter.class));

        	    state = State.WAITING_FOR_IRON_SMELTER;
            } else {

                /* Find a spot for the iron smelter */
            	Point ironSmelterPoint = Utils.findPointForBuildingCloseToPoint(headquarter.getPosition(), MEDIUM, controlledPlayer, map);

            	if (ironSmelterPoint == null) {
            		return;
            	}

            	/* Build the iron smelter */
            	IronSmelter ironSmelter = map.placeBuilding(new IronSmelter(controlledPlayer), ironSmelterPoint);

            	ironSmelters.add(ironSmelter);

            	/* Connect the iron smelter with the headquarter */
                Road road = Utils.connectPointToBuilding(controlledPlayer, map, ironSmelter.getFlag().getPosition(), headquarter);

                /* Fill the road with flags */
                Utils.fillRoadWithFlags(map, road);

                state = State.WAITING_FOR_IRON_SMELTER;
            }
        } else if (state == State.WAITING_FOR_IRON_SMELTER) {

        	if (Utils.buildingsAreReady(ironSmelters)) {
        		state = State.NEEDS_ARMORY;
        	}
        } else if (state == State.NEEDS_ARMORY) {

            /* Determine if there already are armories built */
            if (Utils.buildingTypeExists(controlledPlayer.getBuildings(), Armory.class)) {
        	    armories.addAll(Utils.getBuildingsOfType(controlledPlayer.getBuildings(), Armory.class));

        	    state = State.WAITING_FOR_IRON_SMELTER;
            } else {

                /* Find a spot for the armory */
            	Point armoryPoint = Utils.findPointForBuildingCloseToPoint(headquarter.getPosition(), MEDIUM, controlledPlayer, map);

            	if (armoryPoint == null) {
            		return;
            	}

            	/* Build the armory */
            	Armory armory = map.placeBuilding(new Armory(controlledPlayer), armoryPoint);

            	armories.add(armory);

            	/* Connect the armory with the headquarter */
                Road road = Utils.connectPointToBuilding(controlledPlayer, map, armory.getFlag().getPosition(), headquarter);

                /* Fill the road with flags */
                Utils.fillRoadWithFlags(map, road);

                state = State.WAITING_FOR_ARMORY;
            }
        } else if (state == State.WAITING_FOR_ARMORY) {

        	if (Utils.buildingsAreReady(armories)) {
        		state = State.NEEDS_FARM;
        	}
        } else if (state == State.NEEDS_FARM) {

            /* Determine if there are already existing farm */
            if (Utils.buildingTypeExists(controlledPlayer.getBuildings(), Farm.class)) {
                farms.addAll(Utils.getBuildingsOfType(controlledPlayer.getBuildings(), Farm.class));

                state = State.WAITING_FOR_FARM;
            } else {

                /* Find a spot for the farm */
                Point farmSpot = Utils.findPointForBuildingCloseToPoint(headquarter.getPosition(), LARGE, controlledPlayer, map);

                if (farmSpot == null) {
                    return;
                }

                /* Build the farm */
                Farm farm = map.placeBuilding(new Farm(controlledPlayer), farmSpot);

                farms.add(farm);

                /* Connect the farm with the headquarter */
                Road road = Utils.connectPointToBuilding(controlledPlayer, map, farm.getFlag().getPosition(), headquarter);

                /* Fill the road with flags */
                Utils.fillRoadWithFlags(map, road);

                state = State.WAITING_FOR_FARM;
            }
        } else if (state == State.WAITING_FOR_FARM) {
            if (Utils.buildingsAreReady(farms)) {
                state = State.NEEDS_WELL;
            }
        } else if (state == State.NEEDS_WELL) {

            /* Determine if there already are wells built */
            if (Utils.buildingTypeExists(controlledPlayer.getBuildings(), Well.class)) {
                wells.addAll(Utils.getBuildingsOfType(controlledPlayer.getBuildings(), Well.class));

                state = State.WAITING_FOR_WELL;
            } else {

                /* Find a spot for the brewery */
                Point wellPoint = Utils.findPointForBuildingCloseToPoint(headquarter.getPosition(), SMALL, controlledPlayer, map);

                if (wellPoint == null) {
                    return;
                }

                /* Build the well */
                Well well = map.placeBuilding(new Well(controlledPlayer), wellPoint);

                wells.add(well);

                /* Connect the well with the headquarter */
                Road road = Utils.connectPointToBuilding(controlledPlayer, map, well.getFlag().getPosition(), headquarter);

                /* Fill the road with flags */
                Utils.fillRoadWithFlags(map, road);

                state = State.WAITING_FOR_WELL;
            }
        } else if (state == State.WAITING_FOR_WELL) {
            if (Utils.buildingsAreReady(wells)) {
                state = State.NEEDS_BREWERY;
            }
        } else if (state == State.NEEDS_BREWERY) {

            /* Determine if there already are breweries built */
            if (Utils.buildingTypeExists(controlledPlayer.getBuildings(), Brewery.class)) {
        	    breweries.addAll(Utils.getBuildingsOfType(controlledPlayer.getBuildings(), Brewery.class));

        	    state = State.WAITING_FOR_BREWERY;
            } else {

                /* Find a spot for the brewery */
            	Point breweryPoint = Utils.findPointForBuildingCloseToPoint(headquarter.getPosition(), MEDIUM, controlledPlayer, map);

            	if (breweryPoint == null) {
                    return;
            	}

            	/* Build the brewery */
            	Brewery brewery = map.placeBuilding(new Brewery(controlledPlayer), breweryPoint);

            	breweries.add(brewery);

            	/* Connect the brewery with the headquarter */
                Road road = Utils.connectPointToBuilding(controlledPlayer, map, brewery.getFlag().getPosition(), headquarter);

                /* Fill the road with flags */
                Utils.fillRoadWithFlags(map, road);

                state = State.WAITING_FOR_BREWERY;
            }
        } else if (state == State.WAITING_FOR_BREWERY) {
            if (Utils.buildingsAreReady(breweries)) {
            	state = State.DONE;
            }
        }
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public Player getControlledPlayer() {
        return controlledPlayer;
    }

    boolean productionDone() {
        return (Utils.listContainsAtLeastOneReadyBuilding(ironSmelters) &&
                Utils.listContainsAtLeastOneReadyBuilding(armories)     &&
                Utils.listContainsAtLeastOneReadyBuilding(breweries)    &&
                Utils.listContainsAtLeastOneReadyBuilding(farms)        &&
                Utils.listContainsAtLeastOneReadyBuilding(wells));
    }
}
