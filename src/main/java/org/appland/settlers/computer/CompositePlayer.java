/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.Countdown;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.utils.Duration;
import org.appland.settlers.utils.Group;
import org.appland.settlers.utils.Stats;

import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.IRON_BAR;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;

/**
 *
 * @author johan
 */
public class CompositePlayer implements ComputerPlayer {
    private final Player player;
    private final ConstructionPreparationPlayer constructionPlayer;
    private final SearchForMineralsPlayer       mineralsPlayer;
    private final FoodProducer                  foodPlayer;
    private final CoinProducer                  coinPlayer;
    private final MilitaryProducer              militaryProducer;
    private final ExpandLandPlayer              expandingPlayer;
    private final AttackPlayer                  attackingPlayer;
    private final Countdown                     countdown;
    private final Group collectEachStepTimeGroup;
    private final Stats stats;

    private GameMap map;
    private ComputerPlayer previousPlayer;
    private ComputerPlayer currentPlayer;
    private int counter;
    private static final int PERIODIC_ENEMY_SCAN = 100;
    private static final int PERIODIC_SCAN_FOR_NEW_MINERALS = 30;
    private static final int PERIODIC_TRANSPORT_PRIORITY_REVIEW = 200;
    private static final int COUNTER_MAX         = 1000;
    private static final int ATTACK_FOLLOW_UP    = 20;
    private static final int TIME_TO_WAIT_FOR_PROMOTED_SOLDIERS = 200;
    private static final int PERIODIC_LAKE_SCAN = 40;
    private static final String AGGREGATED_EACH_STEP_TIME_GROUP = "COLLECT_EACH_TURN";

    public CompositePlayer(Player player, GameMap map) {
        this.player = player;
        this.map    = map;
        counter     = 0;
        countdown   = new Countdown();

        /* Set up statistics collection */
        stats = new Stats();

        collectEachStepTimeGroup = stats.createVariableGroupIfAbsent(AGGREGATED_EACH_STEP_TIME_GROUP);

        /* Instantiate each computer player */
        constructionPlayer = new ConstructionPreparationPlayer(player, map);
        mineralsPlayer     = new SearchForMineralsPlayer(player, map);
        foodPlayer         = new FoodProducer(player, map);
        coinPlayer         = new CoinProducer(player, map);
        militaryProducer   = new MilitaryProducer(player, map);
        expandingPlayer    = new ExpandLandPlayer(player, map, stats);
        attackingPlayer    = new AttackPlayer(player, map);

        /* Configure the players */
        expandingPlayer.setExpandTowardEnemies(true);
        expandingPlayer.waitForBuildingsToGetCompletelyOccupied(true);
    }

    public Stats getStats() {
        return stats;
    }

    @Override
    public void turn() throws Exception {

        Duration duration = stats.measureOneShotDuration("CompositePlayer.turn");

        /* Keep track of how many times the method is run to support periodic tasks */
        if (counter > COUNTER_MAX) {
            counter = 0;
        } else {
            counter++;
        }

        /* Remember the previous player to detect player changes */
        previousPlayer = currentPlayer;

        /* Tweak transport priority regularly */
        if (counter % PERIODIC_TRANSPORT_PRIORITY_REVIEW == 0) {

            player.setFoodQuota(CoalMine.class, 1);
            player.setFoodQuota(GoldMine.class, 1);
            player.setFoodQuota(IronMine.class, 1);
            player.setFoodQuota(GraniteMine.class, 1);

            if (player.getInventory().get(COAL) < 5) {
                player.setFoodQuota(CoalMine.class, 10);
            }

            if (player.getInventory().get(GOLD) < 5) {
                player.setFoodQuota(GoldMine.class, 10);
            }

            if (player.getInventory().get(IRON) < 5) {
                player.setFoodQuota(IronMine.class, 10);
            }

            /* Change transport priorities if needed */
            tuneTransportPriorities();

            duration.after("Tune transport priorities");
        }

        /* Scan for new potential mines periodically */
        if (counter % PERIODIC_SCAN_FOR_NEW_MINERALS == 0 ) {
            mineralsPlayer.scanForNewMinerals();

            duration.after("Scan for new minerals");
        }

        /* Scan for lakes periodically */
        if (counter % PERIODIC_LAKE_SCAN == 0) {
            foodPlayer.scanForNewLakes();

            duration.after("Scan for new lakes");
        }

        /* Ensure plank production is active */
        if (!constructionPlayer.plankProductionWorking()) {
            constructionPlayer.turn();

            currentPlayer = constructionPlayer;

            duration.after("Construction player turn");

        /* Ensure stones are collected. Explore land directly if there are no stones available */
        } else if (!constructionPlayer.stoneProductionWorking()) {

            if (constructionPlayer.hasAccessToStone()) {
                constructionPlayer.turn();

                currentPlayer = constructionPlayer;

                duration.after("Construction player turn");
            } else {
                expandingPlayer.turn();

                currentPlayer = expandingPlayer;

                duration.after("Expanding land player turn");
            }

        /* Scan for minerals if there are unknown areas and re-scan periodically */
        } else if (!mineralsPlayer.allCurrentMineralsKnown()) {
            mineralsPlayer.turn();

            currentPlayer = mineralsPlayer;

            duration.after("Minerals player turn");

        /* Build first level of food production if it's missing and there are mines needing it */
        } else if (mineralsPlayer.hasMines() && !foodPlayer.basicFoodProductionDone()) {
            foodPlayer.turn();

            currentPlayer = foodPlayer;

            duration.after("Food player turn");

        /* Build up coin production if gold is available */
        } else if (player.getInventory().get(GOLD) > 0 && !coinPlayer.coinProductionDone()) {
            coinPlayer.turn();

            currentPlayer = coinPlayer;

            duration.after("Coin player turn");

        /* Build up full food production after the coin production is available */
        } else if (mineralsPlayer.hasCoalMine() &&
                   mineralsPlayer.hasIronMine() &&
                   !foodPlayer.fullFoodProductionDone()) {
            foodPlayer.turn();

            currentPlayer = foodPlayer;

            duration.after("Food player turn");

        /* Build up military production when full food production is done */
        } else if (mineralsPlayer.hasCoalMine() &&
                   mineralsPlayer.hasIronMine() &&
                   !militaryProducer.productionDone()){

            militaryProducer.turn();

            currentPlayer = militaryProducer;

            duration.after("Military producer turn");

        /* Handle ongoing attacks */
        } else if (attackingPlayer.isAttacking() && counter % ATTACK_FOLLOW_UP == 0) {

            attackingPlayer.turn();

            currentPlayer = attackingPlayer;

            duration.after("Attacking player turn");

        /* Handle the case where an ongoing attack has been won */
        } else if (attackingPlayer.hasWonBuildings()) {
            System.out.println("\nComposite player: Has won building\n");
            System.out.println("  " + attackingPlayer.getWonBuildings());

            /* Notify the expanding player about newly acquired enemy buildings */
            expandingPlayer.registerBuildings(attackingPlayer.getWonBuildings());
            attackingPlayer.clearWonBuildings();

            duration.after("Won buildings");

        /* Look for enemies to attack */
        } else if (expandingPlayer.hasNewBuildings() || counter % PERIODIC_ENEMY_SCAN == 0) {

            expandingPlayer.clearNewBuildings();

            /* Wait with attack if there is gold available but not enough promotions yet */
            if (mineralsPlayer.hasGoldMine()) {

                /* Wait to get a chance to get promoted soldiers before attacking */
                if (countdown.isActive()) {
                    if (!countdown.hasReachedZero()) {
                        countdown.step();

                        return;
                    }
                } else {
                    countdown.countFrom(TIME_TO_WAIT_FOR_PROMOTED_SOLDIERS);

                    return;
                }
            }

            /* Look for enemies close by to attack */
            Building enemyBuilding = Utils.getCloseEnemyBuilding(player);

            duration.after("Look for enemy buildings to attack");

            if (enemyBuilding == null) {
                System.out.println("Composite player: No close enemy to attack");
                return;
            }

            /* Attack if possible */
            if (player.getAvailableAttackersForBuilding(enemyBuilding) > 0) {
                System.out.println("Composite player: Can attack");
                attackingPlayer.turn();

                currentPlayer = attackingPlayer;

                duration.after("Attacking player turn");

            } else {
                System.out.println("Composite player: Cannot attack enemy at " + enemyBuilding.getPosition());
            }

        /* Expand the land if there is nothing else to do */
        } else {

            expandingPlayer.turn();

            currentPlayer = expandingPlayer;

            duration.after("Expanding player turn");
        }

        if (previousPlayer != currentPlayer) {
            System.out.println(" -- Switched to " + currentPlayer.getClass().getSimpleName() + " from " + previousPlayer);
        }

        duration.reportStats(stats);

        collectEachStepTimeGroup.collectionPeriodDone();
    }

    @Override
    public void setMap(GameMap map) {
        expandingPlayer.setMap(map);
        constructionPlayer.setMap(map);
        attackingPlayer.setMap(map);
        mineralsPlayer.setMap(map);
        coinPlayer.setMap(map);
        foodPlayer.setMap(map);
        militaryProducer.setMap(map);

        this.map = map;
    }

    @Override
    public Player getControlledPlayer() {
        return player;
    }

    private void tuneTransportPriorities() throws InvalidUserActionException {

        /* Create a baseline for materials that tend to overflow */

        player.setTransportPriority(0, TransportCategory.GOLD);
        player.setTransportPriority(1, TransportCategory.WEAPONS);
        player.setTransportPriority(2, TransportCategory.IRON_BAR);
        player.setTransportPriority(3, TransportCategory.COAL);
        player.setTransportPriority(4, TransportCategory.IRON);
        player.setTransportPriority(5, TransportCategory.FOOD);
        player.setTransportPriority(6, TransportCategory.FLOUR);
        player.setTransportPriority(7, TransportCategory.WHEAT);
        player.setTransportPriority(8, TransportCategory.STONE);
        player.setTransportPriority(9, TransportCategory.PLANK);
        player.setTransportPriority(10, TransportCategory.BEER);
        player.setTransportPriority(11, TransportCategory.WOOD);
        player.setTransportPriority(12, TransportCategory.WATER);

        /* Main priority: GOLD, PRIVATE, PLANKS, STONES
           Handle backwards to get the priority right
        */

        /* First stones */
        if (player.getInventory().get(STONE) < 20) {
            player.setTransportPriority(0, TransportCategory.STONE);
        }

        /* Then planks */
        if (player.getInventory().get(PLANK) < 20) {
            player.setTransportPriority(0, TransportCategory.PLANK);
            player.setTransportPriority(1, TransportCategory.WOOD);
        }

        /* Then privates - handle beer */
        if (player.getInventory().get(BEER) < 5) {
            player.setTransportPriority(0, TransportCategory.BEER);

            if (player.getInventory().get(WATER) < 10) {
                player.setTransportPriority(1, TransportCategory.WATER);
            }

            if (player.getInventory().get(WHEAT) < 10) {
                player.setTransportPriority(2, TransportCategory.WHEAT);
            }
        }

        /* Then privates - handle weapons */
        if (player.getInventory().get(BEER) > 10) {
            player.setTransportPriority(0, TransportCategory.WEAPONS);

            player.setTransportPriority(1, TransportCategory.IRON_BAR);
            player.setTransportPriority(2, TransportCategory.COAL);

            if (player.getInventory().get(IRON_BAR) < 5) {
                player.setTransportPriority(3, TransportCategory.IRON);

                if (player.getInventory().get(COAL) < 5 ||
                    player.getInventory().get(IRON) < 5) {
                    player.setTransportPriority(4, TransportCategory.FOOD);

                    if (player.getInventory().get(BREAD) < 5) {
                        player.setTransportPriority(5, TransportCategory.FLOUR);
                        player.setTransportPriority(6, TransportCategory.WATER);

                        if (player.getInventory().get(FLOUR) < 5) {
                            player.setTransportPriority(7, TransportCategory.WHEAT);
                        }
                    }
                }
            }
        }

        /* Then gold */
        if (player.getInventory().get(COIN) < 5) {
            player.setTransportPriority(0, TransportCategory.COIN);
            player.setTransportPriority(1, TransportCategory.COAL);
            player.setTransportPriority(2, TransportCategory.GOLD);

            if (player.getInventory().get(COAL) < 5 ||
                player.getInventory().get(GOLD) < 5) {
                player.setTransportPriority(3, TransportCategory.FOOD);

                if (player.getInventory().get(BREAD) < 5) {
                    player.setTransportPriority(4, TransportCategory.FLOUR);
                    player.setTransportPriority(5, TransportCategory.WATER);

                    if (player.getInventory().get(FLOUR) < 5) {
                        player.setTransportPriority(6, TransportCategory.WHEAT);
                    }
                }
            }
        }
    }
}
