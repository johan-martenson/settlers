/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.computer.util.GamePlay;
import org.appland.settlers.computer.util.Placement;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;

import java.util.List;
import java.util.Set;

import static org.appland.settlers.computer.util.Placement.*;
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

        // Set the initial state
        state = State.NO_CONSTRUCTION;
    }

    @Override
    public void turn() throws Exception {
        var stateBefore = state;

        // Construct a forester
        if (state == State.NO_CONSTRUCTION) {
            headquarter = GamePlay.findHeadquarter(player);

            // Place woodcutter directly if there's a point with lots of trees
            woodcutter = new Woodcutter(player);
            var pointForWoodcutter = findBestPointForBuilding(Woodcutter.class, player, Set.of(
                    new PlacementHeuristic(100, point -> GamePlay.countTreesNearby(point, map, 6))
            ), Set.of());

            System.out.println("Best place for woodcutter: " + pointForWoodcutter);

            if (pointForWoodcutter != null) {
                System.out.println("Placing woodcutter.");
                woodcutter = map.placeBuilding(woodcutter, pointForWoodcutter);
                map.placeAutoSelectedRoad(player, woodcutter.getFlag(), headquarter.getFlag());

                state = State.WAITING_FOR_WOODCUTTER;
            } else {
                System.out.println("Placing forester.");
                woodcutter = null;

                var site = findSpotForForesterHut();

                foresterHut = map.placeBuilding(new ForesterHut(player), site);
                map.placeAutoSelectedRoad(player, foresterHut.getFlag(), headquarter.getFlag());

                state = State.WAITING_FOR_FORESTER;
            }
        } else if (state == State.WAITING_FOR_FORESTER) {

            // Check if the forester hut is constructed
            if (foresterHut.isReady()) {
                state = State.FORESTER_CONSTRUCTED;
            }
        } else if (state == State.FORESTER_CONSTRUCTED) {

            // Find a site for the woodcutter close to the forester hut
            var anchor = findAnchorFor(Woodcutter.class, player);
            var heuristics = List.of(
                    new PlacementHeuristic(100, point -> GamePlay.countTreesNearby(point, map, 6)),
                    new PlacementHeuristic(-1, point -> GameUtils.distanceInGameSteps(anchor.getPosition(), point))
            );

            var woodcutter = new Woodcutter(getControlledPlayer());
            var site = Placement.findBestPointForBuildingCloseToPoint(
                    Woodcutter.class,
                    anchor.getPosition(),
                    6,
                    player,
                    heuristics,
                    Set.of());

            // Place the woodcutter
            map.placeBuilding(woodcutter, site);

            // Connect the forester hut with the headquarters
            var road = map.placeAutoSelectedRoad(player, foresterHut.getFlag(), woodcutter.getFlag());

            // Place flags where possible
            GamePlay.fillRoadWithFlags(map, road);

            // Change state to wait for the woodcutter
            state = State.WAITING_FOR_WOODCUTTER;
        } else if (state == State.WAITING_FOR_WOODCUTTER) {

            // Check if the woodcutter is constructed
            if (woodcutter.isReady()) {
                state = State.WOODCUTTER_CONSTRUCTED;
            }
        } else if (state == State.WOODCUTTER_CONSTRUCTED) {

            // Find a site for the sawmill close to the headquarters
            var site = findSpotForSawmill(headquarter);

            // Place the sawmill
            sawmill = map.placeBuilding(new Sawmill(player), site);

            // Connect the sawmill with the headquarter
            var road = map.placeAutoSelectedRoad(player, sawmill.getFlag(), headquarter.getFlag());

            // Place flags where possible
            GamePlay.fillRoadWithFlags(map, road);

            // Change state to wait for the woodcutter
            state = State.WAITING_FOR_SAWMILL;
        }
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    private Point findSpotForForesterHut() {
        return GamePlay.findAvailableSpotForBuilding(map, player);
    }

    private Point findSpotForWoodcutterNextToForesterHut(ForesterHut foresterHut) {

        // Find available spots close to the forester
        var spots = GamePlay.findAvailableHousePointsWithinRadius(map, player, foresterHut.getPosition(), SMALL, 4);

        // Return null if there are no available spots
        if (spots.isEmpty()) {
            return null;
        }

        // Return any point from the available ones
        return spots.getFirst();
    }

    private Point findSpotForSawmill(Headquarter headquarter) {

        // Find available spots close to the forester
        var spots = GamePlay.findAvailableHousePointsWithinRadius(map, player, headquarter.getPosition(), MEDIUM, 4);

        // Return null if there are no available spots
        if (spots.isEmpty()) {
            return null;
        }

        // Return any point from the available ones
        return spots.getFirst();
    }

    @Override
    public Player getControlledPlayer() {
        return player;
    }
}
