/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model.actors;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.MapPoint;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.WorkerAction;

import java.util.List;
import java.util.Random;

import static org.appland.settlers.model.DetailedVegetation.CAN_USE_WELL;
import static org.appland.settlers.model.DetailedVegetation.MINABLE_MOUNTAIN;
import static org.appland.settlers.model.GameUtils.areAllOneOf;
import static org.appland.settlers.model.Material.GEOLOGIST;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Size.*;
import static org.appland.settlers.model.actors.Geologist.State.*;

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

    private static final int TIME_TO_INVESTIGATE   = 19;
    private static final int RADIUS_TO_INVESTIGATE = 7;
    private static final Random RANDOM = new Random(1);

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
    protected void onIdle() {
        if (state == INVESTIGATING) {
            if (countdown.hasReachedZero()) {
                Material foundMaterial = placeSignWithResult(getPosition());

                nrSitesInvestigated++;

                /* Report the find */
                if (foundMaterial != null) {
                    getPlayer().reportGeologicalFinding(getPosition(), foundMaterial);
                }

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
    protected void onArrival() {
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

            map.reportWorkerStartedAction(this, WorkerAction.INVESTIGATING);

            countdown.countFrom(TIME_TO_INVESTIGATE);
        } else if (state == RETURNING_TO_FLAG) {
            state = RETURNING_TO_STORAGE;

            /* Try to go to the storage on roads */
            Building storage = GameUtils.getClosestStorageConnectedByRoads(flagPoint, getPlayer());

            if (storage != null) {
                setTarget(storage.getPosition());

            /* Go back off-road if the flag has been removed */
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

    private Material placeSignWithResult(Point point) {
        boolean placedSign = false;
        Material foundMaterial = null;

        List<DetailedVegetation> surroundingVegetation = map.getSurroundingTiles(point);

        if (areAllOneOf(surroundingVegetation, CAN_USE_WELL)) {
            map.placeSign(WATER, LARGE, point);
            placedSign = true;

            foundMaterial = WATER;
        } else if (areAllOneOf(surroundingVegetation, MINABLE_MOUNTAIN)) {
            for (Material mineral: Material.getMinerals()) {
                int amount = map.getAmountOfMineralAtPoint(mineral, point);

                if (amount > 0) {
                    foundMaterial = mineral;
                }

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

        return foundMaterial;
    }

    private Point findSiteToExamine() {
        List<Point> points = map.getPointsWithinRadius(flagPoint, RADIUS_TO_INVESTIGATE);

        int offset = RANDOM.nextInt(points.size());

        for (int i = 0; i < points.size(); i++) {
            int indexWithOffset = (i + offset) % points.size();

            Point point = points.get(indexWithOffset);

            if (point.equals(getPosition())) {
                continue;
            }

            MapPoint mapPoint = map.getMapPoint(point);

            if (mapPoint.isSign()) {
                continue;
            }

            if (mapPoint.isTree()) {
                continue;
            }

            if (mapPoint.isStone()) {
                continue;
            }

            if (mapPoint.isFlag()) {
                continue;
            }

            if (mapPoint.isBuilding()) {
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
    protected void onWalkingAndAtFixedPoint() {

        /* Return to storage if the planned path no longer exists */
        if (state == WALKING_TO_TARGET &&
            map.isFlagAtPoint(getPosition()) &&
            !map.arePointsConnectedByRoads(getPosition(), getTarget())) {
            returnToStorage();
        }
    }

    @Override
    protected void onReturnToStorage() {
        Building storage = getPlayer().getClosestStorage(getPosition(), getHome());

        state = RETURNING_TO_STORAGE;

        if (storage != null) {
            setTarget(storage.getPosition());
        } else {
            storage = GameUtils.getClosestStorageOffroad(getPlayer(), getPosition());

            setOffroadTarget(storage.getPosition());
        }
    }
}
