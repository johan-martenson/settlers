/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.appland.settlers.model.Crop.GrowthState.FULL_GROWN;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;
import static org.appland.settlers.model.Farmer.States.GOING_BACK_TO_HOUSE;
import static org.appland.settlers.model.Farmer.States.GOING_BACK_TO_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.Farmer.States.GOING_OUT_TO_HARVEST;
import static org.appland.settlers.model.Farmer.States.GOING_OUT_TO_PLANT;
import static org.appland.settlers.model.Farmer.States.GOING_OUT_TO_PUT_CARGO;
import static org.appland.settlers.model.Farmer.States.HARVESTING;
import static org.appland.settlers.model.Farmer.States.IN_HOUSE_WITH_CARGO;
import static org.appland.settlers.model.Farmer.States.PLANTING;
import static org.appland.settlers.model.Farmer.States.RESTING_IN_HOUSE;
import static org.appland.settlers.model.Farmer.States.RETURNING_TO_STORAGE;
import static org.appland.settlers.model.Farmer.States.WALKING_TO_TARGET;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
@Walker(speed = 10)
public class Farmer extends Worker {
    private final static int TIME_TO_REST    = 99;
    private final static int TIME_TO_PLANT   = 19;
    private final static int TIME_TO_HARVEST = 19;
    
    private final Countdown countdown;
    private States state;

    private Iterable<Point> getSurroundingSpotsForCrops() {
        Point hutPoint = getHome().getPosition();
        
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
                map.isFlagAtPoint(p)     ||
                map.isRoadAtPoint(p)     ||
                map.isTreeAtPoint(p)) {
                continue;
            }

            if (map.isCropAtPoint(p)) {
                Crop crop = map.getCropAtPoint(p);
                
                if (crop.getGrowthState() != HARVESTED) {
                    continue;
                }
            }

            chosenPoint = p;
            break;
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

    protected enum States {
        WALKING_TO_TARGET, 
        RESTING_IN_HOUSE, 
        GOING_OUT_TO_PLANT, 
        PLANTING, 
        GOING_BACK_TO_HOUSE, 
        GOING_OUT_TO_HARVEST, 
        HARVESTING,
        GOING_BACK_TO_HOUSE_WITH_CARGO,
        GOING_OUT_TO_PUT_CARGO,
        IN_HOUSE_WITH_CARGO,
        RETURNING_TO_STORAGE
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
        } else if (b instanceof Farm) {
            setHome(b);
        }
        
        state = RESTING_IN_HOUSE;
        
        countdown.countFrom(TIME_TO_REST);
    }
    
    @Override
    protected void onIdle() throws Exception {
        if (state == RESTING_IN_HOUSE) {
            if (countdown.reachedZero() && getHome().isProductionEnabled()) {
                Crop cropToHarvest = findCropToHarvest();

                if (cropToHarvest != null) {                    
                    state = GOING_OUT_TO_HARVEST;
                    
                    setOffroadTarget(cropToHarvest.getPosition());
                } else if (getSurroundingNonHarvestedCrops().size() < 5) {
                    Point p = getFreeSpotToPlant();

                    if (p == null) {
                        return;
                    }

                    setOffroadTarget(p);

                    state = GOING_OUT_TO_PLANT;
                }
            } else if (getHome().isProductionEnabled()) {
                countdown.step();
            }
        } else if (state == PLANTING) {
            if (countdown.reachedZero()) {
                Crop crop = map.placeCrop(getPosition());
                    
                state = GOING_BACK_TO_HOUSE;
                    
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == HARVESTING) {
            if (countdown.reachedZero()) {

                Crop crop = map.getCropAtPoint(getPosition());
                crop.harvest();
                
                /* Create a crop cargo to make sure the map is set correctly */
                setCargo(new Cargo(WHEAT, map));
                
                state = GOING_BACK_TO_HOUSE_WITH_CARGO;
                
                returnHomeOffroad();
            } else {
                countdown.step();
            }
        } else if (state == IN_HOUSE_WITH_CARGO) {
            setTarget(getHome().getFlag().getPosition());

            state = GOING_OUT_TO_PUT_CARGO;
        }
    }

    private Collection<Crop> getSurroundingNonHarvestedCrops() {
        List<Crop> result = new ArrayList<>();
        
        for (Point p : getSurroundingSpotsForCrops()) {
            if (map.isCropAtPoint(p)) {
                Crop crop = map.getCropAtPoint(p);
                
                if (crop.getGrowthState() != HARVESTED) {
                    result.add(map.getCropAtPoint(p));
                }
            }
        }

        return result;
    }

    @Override
    public void onArrival() throws Exception {
        if (state == GOING_OUT_TO_PUT_CARGO) {
            Cargo cargo = getCargo();

            cargo.setPosition(getPosition());
            cargo.transportToStorage();
            getHome().getFlag().putCargo(cargo);

            setCargo(null);

            state = GOING_BACK_TO_HOUSE;

            setTarget(getHome().getPosition());
        } else if (state == GOING_BACK_TO_HOUSE) {
            state = RESTING_IN_HOUSE;
            
            enterBuilding(getHome());
            
            countdown.countFrom(TIME_TO_REST);
        } else if (state == GOING_OUT_TO_PLANT) {
            state = PLANTING;
            
            countdown.countFrom(TIME_TO_PLANT);
        } else if (state == GOING_OUT_TO_HARVEST) {
            state = HARVESTING;
            
            countdown.countFrom(TIME_TO_HARVEST);
        } else if (state == GOING_BACK_TO_HOUSE_WITH_CARGO) {
            enterBuilding(getHome());
                
            state = IN_HOUSE_WITH_CARGO;
        } else if (state == RETURNING_TO_STORAGE) {
            Storage storage = (Storage)map.getBuildingAtPoint(getPosition());
        
            storage.depositWorker(this);
        }
    }

    @Override
    protected void onReturnToStorage() throws Exception {
        Building storage = map.getClosestStorage(getPosition());
    
        if (storage != null) {
            state = RETURNING_TO_STORAGE;
            
            setTarget(storage.getPosition());
        } else {
            for (Building b : map.getBuildings()) {
                if (b instanceof Storage) {
                    state = RETURNING_TO_STORAGE;

                    setOffroadTarget(b.getPosition());

                    break;
                }
            }
        }
    }
}
