package org.appland.settlers.rest;

import org.appland.settlers.computer.CompositePlayer;
import org.appland.settlers.computer.ComputerPlayer;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.rest.resource.GameResource;
import org.appland.settlers.rest.resource.GameSpeed;
import org.appland.settlers.utils.CumulativeDuration;
import org.appland.settlers.utils.Group;
import org.appland.settlers.utils.Stats;
import org.appland.settlers.utils.Variable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameTicker {

    private static final int MIN_GAME_TICK_LENGTH = 50;
    private static final int COMPUTER_PLAYER_FREQUENCY = 20;
    private static final String FULL_TICK_TIME = "GameTicker.tick.total";
    public static final GameTicker GAME_TICKER = new GameTicker();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Set<GameResource> games = new HashSet<>();
    private final Stats stats = new Stats();
    ScheduledFuture<?> handle;
    private int counter = 0;

    GameTicker() {
        stats.setUpperThreshold(FULL_TICK_TIME, 150);
    }

    void deactivate() {
        scheduler.shutdown();
    }

    void activate() {
        handle = scheduler.scheduleAtFixedRate(() -> {
            boolean runComputers = false;

            Group group = null;
            CumulativeDuration duration = null;
            Variable computerPlayerTurns = null;

            try {
                group = stats.createVariableGroupIfAbsent("GameTickGroup");
                duration = stats.measureCumulativeDuration("GameTicker.tick", group);
                computerPlayerTurns = stats.addIncrementingVariableIfAbsent("ComputerPlayerTurns");
            } catch (Throwable e) {
                System.out.println(e);
                e.printStackTrace();
            }

            if (counter == COMPUTER_PLAYER_FREQUENCY) {
                runComputers = true;
            }

            for (var game : games) {
                if (game.isPaused()) {
                    continue;
                }

                if (game.getGameSpeed() == GameSpeed.FAST && counter % 2 != 0) {
                    continue;
                }

                if (game.getGameSpeed() == GameSpeed.NORMAL && counter % 4 != 0) {
                    continue;
                }

                if (game.getGameSpeed() == GameSpeed.SLOW && counter % 8 != 0) {
                    continue;
                }

                GameMap map = game.getGameMap();

                Collection<ComputerPlayer> computerPlayers = game.getComputerPlayers();

                synchronized (map) {
                    try {
                        map.stepTime();
                    } catch (Throwable e) {
                        System.out.println("Exception during game loop: " + e);
                        e.printStackTrace();
                        System.out.println(e.getCause());
                        System.out.println(map);

                        System.exit(1);
                    }

                    duration.after("Map.stepTime");

                    if (runComputers) {
                        for (ComputerPlayer computerPlayer : computerPlayers) {
                            synchronized (map) {

                                try {
                                    computerPlayer.turn();

                                    Stats computerPlayerStats = ((CompositePlayer)computerPlayer).getStats();

                                    computerPlayerTurns.reportValue(1);

                                    Variable totalTurn = computerPlayerStats.getVariable("CompositePlayer.turn.total");

                                    if (totalTurn.isLatestValueHighest()) {
                                        stats.printVariablesAsTable();
                                        computerPlayerStats.printVariablesAsTable();
                                    }

                                } catch (Throwable e) {
                                    System.out.println("Exception during computer player turn");
                                    e.printStackTrace();
                                }
                            }

                            duration.after("ComputerPlayer.turn");
                        }
                    }
                }

                try {
                    duration.report();

                    group.collectionPeriodDone();

                    boolean printStats = false;

                    Variable mapStepTime = stats.getVariable("GameTicker.tick.Map.stepTime");
                    Variable computerPlayerTurn = stats.getVariable("GameTicker.tick.ComputerPlayer.turn");
                    Variable fullTick = stats.getVariable("GameTicker.tick.total");

                    if (mapStepTime.isLatestValueHighest()) {
                        System.out.println("\nNew highest time for map.stepTime(): " + mapStepTime.getHighestValue() + " (ms)");

                        System.out.println("Upper threshold is: " + mapStepTime.getUpperThreshold());

                        printStats = true;

                    }

                    if (runComputers && computerPlayerTurn != null && computerPlayerTurn.isLatestValueHighest()) {
                        System.out.println("\nNew highest time for computer players: " + computerPlayerTurn.getHighestValue() + " (ms)");

                        System.out.println("Average: " + computerPlayerTurn.getAverage() + " (ms)");



                        printStats = true;
                    }

                    if (fullTick.isLatestValueHighest()) {
                        System.out.println("\nNew highest time for full tick: " + fullTick.getHighestValue() + " (ms)");

                        System.out.println("Average: " + fullTick.getAverage() + " (ms)");

                        printStats = true;
                    }

                    if (fullTick.isUpperThresholdExceeded()) {
                        System.out.println("\nMap step time exceeded threshold");

                        printStats = true;
                    }

                    if (printStats) {
                        stats.printVariablesAsTable();
                        map.getStats().printVariablesAsTable();
                    }
                } catch (Throwable t) {
                    System.out.println(t);
                    t.printStackTrace();
                }
            }

            if (runComputers) {
                counter = 0;
            } else {
                counter = counter + 1;
            }
        },
        MIN_GAME_TICK_LENGTH,MIN_GAME_TICK_LENGTH, TimeUnit.MILLISECONDS);
    }

    public void startGame(GameResource gameResource) {
        games.add(gameResource);

        GameMap map = gameResource.getGameMap();

        for (Building building : map.getBuildings()) {
            if (building instanceof Headquarter headquarter) {

                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
                headquarter.depositWorker(new Scout(headquarter.getPlayer(), map));
            }
        }
    }
}
