/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Farmer.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Farmer.States.GOING_OUT_TO_HARVEST;
import static org.appland.settlers.model.Farmer.States.HARVESTING;
import static org.appland.settlers.model.Farmer.States.PLANTING;
import static org.appland.settlers.model.Farmer.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Farmer.States.WALKING_TO_TARGET;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Farmer extends Worker {
    private States state;
    private Countdown countdown;
    private Farm hut;
    private Cargo cropCargo;

    private Iterable<Point> getSurroundingSpotsForCrops() {
        Point hutPoint = hut.getPosition();
        
        Set<Point> possibleSpotsToPlant = new HashSet<>();
        
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.upLeft().getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(hutPoint.upRight().getAdjacentPoints()));
        
        possibleSpotsToPlant.remove(hutPoint);
        possibleSpotsToPlant.remove(hutPoint.upLeft());
        possibleSpotsToPlant.remove(hutPoint.upRight());

        return possibleSpotsToPlant;
    }
    
    private Point getFreeSpotToPlant() {
        Point chosenPoint = null;
        
        for (Point p : getSurroundingSpotsForCrops()) {
            if (map.isBuildingAtPoint(p) || 
                map.isCropAtPoint(p)     || 
                map.isFlagAtPoint(p)     ||
                map.isRoadAtPoint(p)     ||
                map.isTreeAtPoint(p)) {
                chosenPoint = p;
                break;
            }
        }

        return chosenPoint;
    }

    private Crop findCropToHarvest() {
        for (Point p : getSurroundingSpotsForCrops()) {
            if (map.isCropAtPoint(p)) {
                Crop crop = map.getCropAtPoint(p);
                
                if (crop.getGrowthState() == FULL_GROWN) {
                    return crop;
                }
            }
        }

        return null;
    }

    enum States {
        WALKING_TO_TARGET, RESTING_IN_HOUSE, GOING_OUT_TO_PLANT, PLANTING, GOING_BACK_TO_HOUSE, GOING_OUT_TO_HARVEST, HARVESTING
    }

    public Farmer() {
        this(null);
    }
    
    public Farmer(GameMap map) {
        super(map);

        state = WALKING_TO_TARGET;
        countdown = new Countdown();
    }

    public boolean isHarvesting() {
        return state == HARVESTING;
    }
    
    public boolean isPlanting() {
        return state == PLANTING;
    }

    @Override
    protected void onEnterBuilding(Building b) {
        if (b instanceof Storage) {
            return;
        }
        
        hut = (Farm)b;
        
        state = States.RESTING_IN_HOUSE;
        
        countdown.countFrom(99);
    }
    
    @Override
    protected void onIdle() {
        if (state == States.RESTING_IN_HOUSE) {
            
            if (countdown.reachedZero()) {
                Crop cropToHarvest = findCropToHarvest();

                if (cropToHarvest != null) {                    
                    state = States.GOING_OUT_TO_HARVEST;
                    
                    setOffroadTarget(cropToHarvest.getPosition());
                } else {
                    Point p = getFreeSpotToPlant();

                    if (p == null) {
                        return;
                    }

                    setOffroadTarget(p);

                    state = States.GOING_OUT_TO_PLANT;
                }
            } else {
                countdown.step();
            }
        } else if (state == PLANTING) {
            if (countdown.reachedZero()) {
                
                Crop crop = map.placeCrop(getPosition());
                
                state = States.GOING_BACK_TO_HOUSE;
                
                setOffroadTarget(hut.getFlag().getPosition());
            } else {
                countdown.step();
            }
        } else if (state == States.GOING_OUT_TO_PLANT) {
            state = PLANTING;
            
            countdown.countFrom(19);
        } else if (state == States.GOING_BACK_TO_HOUSE) {            
            if (cropCargo != null) {
                hut.putProducedCargoForDelivery(cropCargo);
            }
            
            enterBuilding(hut);
            
            state = RESTING_IN_HOUSE;
            
            countdown.countFrom(99);
        } else if (state == GOING_OUT_TO_HARVEST) {
            state = HARVESTING;
            
            countdown.countFrom(19);
        } else if (state == HARVESTING) {
            if (countdown.reachedZero()) {

                Crop crop = map.getCropAtPoint(getPosition());
                cropCargo = crop.harvest();
                
                state = GOING_BACK_TO_HOUSE;
                
                setOffroadTarget(hut.getFlag().getPosition());
            } else {
                countdown.step();
            }
        }
    }
}
