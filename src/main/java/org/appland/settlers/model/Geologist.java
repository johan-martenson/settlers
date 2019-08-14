/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.List;
import java.util.Random;
import static org.appland.settlers.model.Geologist.State.GOING_TO_NEXT_SITE;
import static org.appland.settlers.model.Geologist.State.INVESTIGATING;
import static org.appland.settlers.model.Geologist.State.RETURNING_TO_FLAG;
import static org.appland.settlers.model.Geologist.State.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Geologist.State.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Geologist extends Worker {

    protected enum State {
        WALKING_TO_TARGET,
        GOING_TO_NEXT_SITE,
        INVESTIGATING,
        RETURNING_TO_FLAG,
        RETURNING_TO_STORAGE
    }

    private final static int TIME_TO_INVESTIGATE   = 19;
    private final static int RADIUS_TO_INVESTIGATE = 7;
    private final static Random RANDOM = new Random(1);

    private final Countdown countdown;

    private State state;
    private int   nrSitesInvestigated;
    private Point flagPoint;

    public Geologist(Player player, GameMap map) {
        super(player, map);

        countdown           = new Countdown();
        nrSitesInvestigated = 0;

        state = WALKING_TO_TARGET;
    }

    public boolean isInvestigating() {
        return state == INVESTIGATING;
    }

    @Override
    protected void onIdle() throws Exception {
        if (state == INVESTIGATING) {
            if (countdown.reachedZero()) {
                placeSignWithResult(getPosition());

                nrSitesInvestigated++;

                /* Return after investigating five sites */
                if (nrSitesInvestigated == 10) {
                    state = RETURNING_TO_FLAG;

                    setOffroadTarget(flagPoint);

                    return;
                }

                Point nextSite = findSiteToExamine();

                if (nextSite == null) {
                    state = RETURNING_TO_FLAG;

                    setOffroadTarget(flagPoint);
                } else {
                    state = GOING_TO_NEXT_SITE;

                    setOffroadTarget(nextSite);
                }
            } else {
                countdown.step();
            }
        }
    }

    @Override
    protected void onArrival() throws Exception {
        if (state == WALKING_TO_TARGET) {
            flagPoint = getPosition();

            Point point = findSiteToExamine();

            if (point == null) {
                state = RETURNING_TO_STORAGE;

                setTarget(GameUtils.getClosestStorageConnectedByRoads(flagPoint, getPlayer()).getPosition(), flagPoint);
            } else {
                state = GOING_TO_NEXT_SITE;

                setOffroadTarget(point);
            }
        } else if (state == GOING_TO_NEXT_SITE) {
            state = INVESTIGATING;

            countdown.countFrom(TIME_TO_INVESTIGATE);
        } else if (state == RETURNING_TO_FLAG) {
            state = RETURNING_TO_STORAGE;

            /* Try to go to the storage on roads */
            Building storage = GameUtils.getClosestStorageConnectedByRoads(flagPoint, getPlayer());

            if (storage != null) {
                setTarget(storage.getPosition());

            /* Go back offroad if the flag has been removed */
            } else {
                storage = getPlayer().getClosestStorageOffroad(flagPoint);

                setOffroadTarget(storage.getPosition());
            }
        } else if (state == RETURNING_TO_STORAGE) {
            Building storage = map.getBuildingAtPoint(getPosition());

            storage.putCargo(new Cargo(GEOLOGIST, map));

            enterBuilding(storage);
        }
    }

    private void placeSignWithResult(Point point) {
        Terrain terrain = map.getTerrain();
        boolean placedSign = false;

        if (terrain.isOnGrass(point)) {
            map.placeSign(WATER, LARGE, point);
            placedSign = true;
        } else if (terrain.isOnMountain(point)) {
            for (Material mineral: Material.getMinerals()) {
                int amount = map.getAmountOfMineralAtPoint(mineral, point);

                if (amount > 10) {
                    map.placeSign(mineral, LARGE, point);
                    placedSign = true;
                    break;
                } else if (amount > 5) {
                    map.placeSign(mineral, MEDIUM, point);
                    placedSign = true;
                    break;
                } else if (amount > 0) {
                    map.placeSign(mineral, SMALL, point);
                    placedSign = true;
                    break;
                }
            }
        }

        if (!placedSign) {
            map.placeEmptySign(point);
        }
    }

    private Point findSiteToExamine() {
        List<Point> points = map.getPointsWithinRadius(flagPoint, RADIUS_TO_INVESTIGATE);

        points.addAll(points);

        int startIndex = RANDOM.nextInt(points.size() / 2);

        for (Point point : points.subList(startIndex, points.size() - 1)) {
            if (point.equals(getPosition())) {
                continue;
            }

            if (map.isSignAtPoint(point)) {
                continue;
            }

            if (map.isTreeAtPoint(point)) {
                continue;
            }

            if (map.isStoneAtPoint(point)) {
                continue;
            }

            if (map.isFlagAtPoint(point)) {
                continue;
            }

            if (map.isBuildingAtPoint(point)) {
                continue;
            }

            if (map.findWayOffroad(getPosition(), point, null) == null) {
                continue;
            }

            return point;
        }

        return null;
    }

    @Override
    protected void onWalkingAndAtFixedPoint() throws Exception {

        /* Return to storage if the planned path no longer exists */
        if (state == State.WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {
            returnToStorage();
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = getPlayer().getClosestStorage(getPosition(), getHome());

        state = State.RETURNING_TO_STORAGE;

        if (storage != null) {
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            setOffroadTarget(storage.getPosition());
        }
    }
}
