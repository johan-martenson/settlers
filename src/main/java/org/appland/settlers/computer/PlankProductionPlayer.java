/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;

import java.util.List;

import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
public class PlankProductionPlayer implements ComputerPlayer {
    private ForesterHut foresterHut;
    private Woodcutter  woodcutter;
    private Headquarter headquarter;
    private Sawmill     sawmill;
    private GameMap     map;

    private enum State {
        NO_CONSTRUCTION,
        WAITING_FOR_FORESTER,
        FORESTER_CONSTRUCTED,
        WOODCUTTER_CONSTRUCTED,
        WAITING_FOR_WOODCUTTER,
        WAITING_FOR_SAWMILL
    }

    private final Player  player;

    private State state;

    public PlankProductionPlayer(Player p, GameMap m) {
        player = p;
        map    = m;

        /* Set the initial state */
        state = State.NO_CONSTRUCTION;
    }

    @Override
    public void turn() throws Exception {
        State stateBefore = state;

        /* Construct a forester */
        if (state == State.NO_CONSTRUCTION) {

            /* Find headquarter */
            headquarter = GamePlayUtils.findHeadquarter(player);

            /* Find a site for the forester hut */
            Point site = findSpotForForesterHut();

            /* Place forester hut */
            foresterHut = map.placeBuilding(new ForesterHut(player), site);

            /* Connect the forester hut with the headquarter */
            Road road = map.placeAutoSelectedRoad(player, foresterHut.getFlag(), headquarter.getFlag());

            /* Place flags where possible */
            GamePlayUtils.fillRoadWithFlags(map, road);

            /* Change state to wait for the forester to be ready */
            state = State.WAITING_FOR_FORESTER;
        } else if (state == State.WAITING_FOR_FORESTER) {

            /* Check if the forester hut is constructed */
            if (foresterHut.isReady()) {
                state = State.FORESTER_CONSTRUCTED;
            }
        } else if (state == State.FORESTER_CONSTRUCTED) {

            /* Find a site for the woodcutter close to the forester hut */
            Point site = findSpotForWoodcutterNextToForesterHut(foresterHut);

            /* Place the woodcutter */
            woodcutter = map.placeBuilding(new Woodcutter(player), site);

            /* Connect the forester hut with the headquarter */
            Road road = map.placeAutoSelectedRoad(player, foresterHut.getFlag(), woodcutter.getFlag());

            /* Place flags where possible */
            GamePlayUtils.fillRoadWithFlags(map, road);

            /* Change state to wait for the woodcutter */
            state = State.WAITING_FOR_WOODCUTTER;
        } else if (state == State.WAITING_FOR_WOODCUTTER) {

            /* Check if the woodcutter is constructed */
            if (woodcutter.isReady()) {
                state = State.WOODCUTTER_CONSTRUCTED;
            }
        } else if (state == State.WOODCUTTER_CONSTRUCTED) {

            /* Find a site for the sawmill close to the headquarter */
            Point site = findSpotForSawmill(headquarter);

            /* Place the sawmill */
            sawmill = map.placeBuilding(new Sawmill(player), site);

            /* Connect the sawmill with the headquarter */
            Road road = map.placeAutoSelectedRoad(player, sawmill.getFlag(), headquarter.getFlag());

            /* Place flags where possible */
            GamePlayUtils.fillRoadWithFlags(map, road);

            /* Change state to wait for the woodcutter */
            state = State.WAITING_FOR_SAWMILL;
        }
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    private Point findSpotForForesterHut() {
        return GamePlayUtils.findAvailableSpotForBuilding(map, player);
    }

    private Point findSpotForWoodcutterNextToForesterHut(ForesterHut foresterHut) {

        /* Find available spots close to the forester */
        List<Point> spots = GamePlayUtils.findAvailableHousePointsWithinRadius(map, player, foresterHut.getPosition(), SMALL, 4);

        /* Return null if there are no available spots */
        if (spots.isEmpty()) {
            return null;
        }

        /* Return any point from the available ones */
        return spots.getFirst();
    }

    private Point findSpotForSawmill(Headquarter headquarter) {

        /* Find available spots close to the forester */
        List<Point> spots = GamePlayUtils.findAvailableHousePointsWithinRadius(map, player, headquarter.getPosition(), MEDIUM, 4);

        /* Return null if there are no available spots */
        if (spots.isEmpty()) {
            return null;
        }

        /* Return any point from the available ones */
        return spots.getFirst();
    }

    @Override
    public Player getControlledPlayer() {
        return player;
    }
}
