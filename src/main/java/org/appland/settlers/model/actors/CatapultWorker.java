/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Projectile;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Storehouse;

import static org.appland.settlers.model.Material.STONE;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class CatapultWorker extends Worker {
    private final Countdown countdown = new Countdown();

    private static final int RESTING_TIME = 99;
    private static final int MAX_RANGE = 15;

    private State state = State.WALKING_TO_TARGET;

    private enum State {
        WALKING_TO_TARGET,
        RESTING_IN_HOUSE,
        RETURNING_TO_STORAGE
    }

    public CatapultWorker(Player player, GameMap map) {
        super(player, map);
    }

    @Override
    protected void onEnterBuilding(Building building) {
        state = State.RESTING_IN_HOUSE;
        countdown.countFrom(RESTING_TIME);
    }

    @Override
    protected void onIdle() {
        if (state == State.RESTING_IN_HOUSE) {

            // Countdown if there are stones available
            if (home.getAmount(STONE) > 0) {

                if (countdown.hasReachedZero()) {
                    var target = findReachableTarget();

                    // Fire a projectile if there was a suitable target
                    if (target != null) {
                        var projectile = new Projectile((Catapult)home, target, map);

                        map.placeProjectile(projectile);

                        // Consume the stone
                        home.consumeOne(STONE);

                        map.getStatisticsManager().stoneThrown((Catapult) home, map.getTime());

                        // Rest again
                        countdown.countFrom(RESTING_TIME);
                    }
                } else {
                    countdown.step();
                }
            }
        }
    }

    @Override
    protected void onArrival() {
        if (state == State.RETURNING_TO_STORAGE) {
            var storehouse = (Storehouse)map.getBuildingAtPoint(position);
            storehouse.depositWorker(this);
        }
    }

    @Override
    public String toString() {
        if (isExactlyAtPoint()) {
            return "Catapult worker %s".formatted(position);
        } else {
            return "Catapult worker %s - %s".formatted(position, getNextPoint());
        }
    }

    @Override
    protected void onReturnToStorage() {
        var storage = GameUtils.getClosestStorageConnectedByRoads(position, player);

        if (storage != null) {
            state = State.RETURNING_TO_STORAGE;
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroad(player, position);

            if (storage != null) {
                state = State.RETURNING_TO_STORAGE;
                setOffroadTarget(storage.getPosition());
            }
        }
    }

    private Building findReachableTarget() {
        for (var point : map.getPointsWithinRadius(position, MAX_RANGE)) {
            var mapPoint = map.getMapPoint(point);

            // Filter points without a building
            if (!mapPoint.isBuilding()) {
                continue;
            }

            var building = mapPoint.getBuilding();

            // Filter buildings belonging to the same player
            if (building.getPlayer().equals(player)) {
                continue;
            }

            // Filter non-military buildings
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            // Filer buildings that are not ready
            if (!building.isReady()) {
                continue;
            }

            return building;
        }

        return null;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() {

        // Return to storage if the planned path no longer exists
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(position) &&
            !map.arePointsConnectedByRoads(position, target)) {

            // Don't try to enter the catapult upon arrival
            clearTargetBuilding();

            // Go back to the storage
            returnToStorage();
        }
    }
}
