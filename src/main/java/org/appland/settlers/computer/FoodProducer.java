package org.appland.settlers.computer;

import org.appland.settlers.computer.util.GamePlay;
import org.appland.settlers.computer.util.Placement;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Well;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.computer.util.GamePlay.connectToBuildingByRoad;
import static org.appland.settlers.computer.util.Placement.findBestPointForBuilding;
import static org.appland.settlers.model.Crop.GrowthState.HARVESTED;

/**
 *
 * @author johan
 */
public class FoodProducer extends BasePlayer implements PlayerGameViewMonitor {
    private static final int RANGE_FISHERY_TO_WATER = 5;

    private final List<Fishery>   fisheries  = new ArrayList<>();
    private final List<HunterHut> hunterHuts = new ArrayList<>();

    private State       state = State.INITIALIZING;
    private Headquarter headquarter;
    private Farm        farm       = null;
    private Well        well       = null;
    private Mill        mill       = null;
    private Bakery      bakery     = null;
    private boolean     noPlaceForFishery = false;

    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList gameChangesList) {

    }

    private enum State {
        INITIALIZING,
        NEEDS_FOOD,
        BUILD_FISHERY,
        WAITING_FOR_FISHERY,
        BUILDING_FISHERY_FAILED,
        BUILD_HUNTER_HUT,
        WAITING_FOR_HUNTER_HUT,
        NEEDS_BREAD
    }

    public FoodProducer(Player player, GameMap map) {
        super(map, player);
    }

    @Override
    public void turn() throws Exception {
        switch (state) {
            case INITIALIZING -> {

                // Ensure we have access to the map
                map = map != null ? map : player.getMap();

                if (map == null) {
                    return;
                }

                // Find the headquarters
                headquarter = (Headquarter) map.getBuildings().stream()
                        .filter(building -> building instanceof Headquarter)
                        .filter(building -> Objects.equals(building.getPlayer(), this.player))
                        .findFirst()
                        .orElse(null);

                if (headquarter != null) {
                    state = State.NEEDS_FOOD;
                }

                // Listen to changes to the player
                player.monitorGameView(this);
            }

            case NEEDS_FOOD, BUILDING_FISHERY_FAILED -> {

                // Try to build a fishery if there isn't already one placed
                if (fisheries.isEmpty() && state != State.BUILDING_FISHERY_FAILED) {
                    state = State.BUILD_FISHERY;
                } else if (hunterHuts.isEmpty()) {
                    state = State.BUILD_HUNTER_HUT;
                }
            }

            case BUILD_FISHERY -> {

                // Find a spot to build a fishery on
                var pointForFishery = findPointForFishery();

                if (pointForFishery == null) {
                    System.out.println(" -- No place available for fishery");

                    state = State.BUILDING_FISHERY_FAILED;
                    noPlaceForFishery = true;

                    return;
                }

                // Build the fishery
                var fishery = map.placeBuilding(new Fishery(player), pointForFishery);

                fisheries.add(fishery);

                // Connect the fishery with the headquarters
                connectToBuildingByRoad(fishery.getFlag(), headquarter);

                state = State.WAITING_FOR_FISHERY;
            }

            case BUILD_HUNTER_HUT -> {

                // Find a spot to build a hunter hut on
                var pointForHunterHut = findPointForHunterHut();

                if (pointForHunterHut == null) {
                    return;
                }

                // Build the hunter hut
                var hunterHut = map.placeBuilding(new HunterHut(player), pointForHunterHut);

                hunterHuts.add(hunterHut);

                // Connect the hunter hut with the headquarters
                connectToBuildingByRoad(hunterHut.getFlag(), headquarter);

                state = State.WAITING_FOR_HUNTER_HUT;
            }

            case WAITING_FOR_FISHERY -> {
                if (fisheries.stream().allMatch(Building::isReady)) {
                    state = State.NEEDS_FOOD;
                }
            }

            case WAITING_FOR_HUNTER_HUT -> {
                if (hunterHuts.stream().allMatch(Building::isReady)) {
                    state = State.NEEDS_BREAD;
                }
            }

            case NEEDS_BREAD -> {
                if (!GamePlay.buildingInPlace(farm)) {

                    // Place a farm
                    var pointForFarm = Placement.findBestPointForBuilding(
                            Farm.class,
                            player,
                            Set.of(
                                    new Placement.PlacementHeuristic(
                                            100,
                                            this::countPlantablePointsAround
                                    )
                            ),
                            Set.of(
                                    p -> ConstructionPreparationPlayer.canConnectPredicate(
                                            p.downRight(),
                                            List.of(p),
                                            headquarter
                                    )
                            )
                    );

                    if (pointForFarm != null) {
                        farm = map.placeBuilding(new Farm(player), pointForFarm);
                    }
                } else if (GamePlay.buildingDone(well) && !GamePlay.buildingInPlace(mill)) {

                    // Place a mill
                    var anchorBuilding = Placement.findAnchorFor(Mill.class, player);
                    mill = map.placeBuilding(new Mill(player), anchorBuilding.getPosition());

                    connectToBuildingByRoad(mill.getFlag(), headquarter);
                } else if (GamePlay.buildingDone(mill) && !GamePlay.buildingInPlace(bakery)) {

                    // Place bakery
                    var anchorBuilding = Placement.findAnchorFor(Bakery.class, player);
                    bakery = map.placeBuilding(new Bakery(player), anchorBuilding.getPosition());

                    connectToBuildingByRoad(bakery.getFlag(), headquarter);
                } else if (GamePlay.buildingDone(farm) && !GamePlay.buildingInPlace(well)) {

                    // Place a well
                    var anchorBuilding = Placement.findAnchorFor(Well.class, player);
                    well = map.placeBuilding(new Well(player), anchorBuilding.getPosition());

                    connectToBuildingByRoad(well.getFlag(), headquarter);
                }
            }
        }
    }

    private Point findPointForFishery() {
        return findBestPointForBuilding(
                Fishery.class,
                player,
                Set.of(
                        new Placement.PlacementHeuristic(
                                100,
                                point -> (int) map.getPointsWithinRadius(point, RANGE_FISHERY_TO_WATER)
                                        .stream()
                                        .filter(map::isInWater)
                                        .count()
                        )
                ),
                Set.of(
                        p -> ConstructionPreparationPlayer.canConnectPredicate(
                                p.downRight(),
                                List.of(p),
                                headquarter
                        )
                )
        );
    }

    private Point findPointForHunterHut() {
        return findBestPointForBuilding(
                HunterHut.class,
                player,
                Set.of(
                        new Placement.PlacementHeuristic(
                                100,
                                point -> GamePlay.countTreesNearby(point, map, 6)
                        )
                ),
                Set.of(
                        p -> ConstructionPreparationPlayer.canConnectPredicate(
                                p.downRight(),
                                List.of(p),
                                headquarter
                        )
                )
        );
    }

    boolean basicFoodProductionDone() {
        return (GamePlay.listContainsAtLeastOneReadyBuilding(fisheries) || noPlaceForFishery) &&
                GamePlay.listContainsAtLeastOneReadyBuilding(hunterHuts);
    }

    boolean fullFoodProductionDone() {
        return basicFoodProductionDone() &&
               GamePlay.buildingDone(farm) &&
               GamePlay.buildingDone(mill) &&
               GamePlay.buildingDone(well) &&
               GamePlay.buildingDone(bakery);
    }

    void scanForNewLakes() {
        noPlaceForFishery = false;
    }

    private int countPlantablePointsAround(Point farmPoint) {
        var possibleSpotsToPlant = new HashSet<Point>();

        possibleSpotsToPlant.addAll(Arrays.asList(farmPoint.getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(farmPoint.upLeft().getAdjacentPoints()));
        possibleSpotsToPlant.addAll(Arrays.asList(farmPoint.upRight().getAdjacentPoints()));

        possibleSpotsToPlant.remove(farmPoint);
        possibleSpotsToPlant.remove(farmPoint.upLeft());
        possibleSpotsToPlant.remove(farmPoint.upRight());

        return (int) possibleSpotsToPlant.stream()
                .filter(point -> {
                    var mapPoint = map.getMapPoint(point);

                    if (mapPoint.isBuilding() ||
                            mapPoint.isFlag() ||
                            mapPoint.isRoad() ||
                            mapPoint.isTree() ||
                            mapPoint.isStone()) {
                        return false;
                    }

                    if (mapPoint.isCrop()) {
                        var crop = map.getCropAtPoint(point);

                        if (crop.getGrowthState() != HARVESTED) {
                            return false;
                        }
                    }

                    return map.findWayOffroad(farmPoint, point, null) != null;
                })
                .count();
    }
}
