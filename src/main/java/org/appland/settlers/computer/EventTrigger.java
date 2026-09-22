package org.appland.settlers.computer;

import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerGameViewMonitor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.appland.settlers.computer.GamePlayEvent.*;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.actors.Rank.GENERAL_RANK;

public class EventTrigger implements PlayerGameViewMonitor {
    private final Player player;
    private final Set<Player> discoveredPlayers = new HashSet<>();
    private final Set<Point> ownedLand = new HashSet<>();
    private final Set<GamePlayEventListener> listeners;

    private GameMap map;
    private boolean firstTurn = true;
    private int amountOfGenerals = 0;
    private boolean noGeneralPromoted = true;
    private boolean planksAndStonesReadyReported = false;

    public EventTrigger(Player player) {
        this.player = player;
        this.map = player.getMap();

        listeners = new HashSet<>();
    }

    public void turn() {
        map = player.getMap();

        if (map == null) {
            return;
        }

        if (ownedLand.isEmpty()) {
            ownedLand.addAll(player.getOwnedLand());
        }

        // Handle initialization and trigger first-run event
        // STARTING_GAME
        if (firstTurn) {
            firstTurn = false;

            listeners.forEach(listener -> listener.onEvent(GamePlayEvent.STARTING_GAME));
            player.monitorGameView(this);
            amountOfGenerals = countGenerals();
            this.map = player.getMap();
        }

        // PROMOTED_FIRST_GENERAL
        if (noGeneralPromoted) {
            var newAmountOfGenerals = countGenerals();

            if (newAmountOfGenerals != amountOfGenerals) {
                amountOfGenerals = newAmountOfGenerals;
                noGeneralPromoted = false;
            }
        }
    }

    @Override
    public void onViewChangesForPlayer(Player player, GameChangesList changes) {

//        // Game lifecycle
//                RESPOND_TO_OTHER_PLAYERS_CHAT
//                IDLE_CHAT
//
//                // Economy


//                DISCOVERED_SEA
//                NO_MORE_TREES
//                ECONOMY_STALLED
//
//                // Expansion
//                EXPANDED_TERRITORY
//
//                // Military
//                BUILT_FORTRESS
//                ATTACKING
//                BEING_ATTACKED
//                WON_BATTLE
//                LOST_BATTLE


//
//                // Personality
//                DOMINATING
//                FALLING_BEHIND

        // PLANKS_AND_STONES_READY
        if (!planksAndStonesReadyReported) {
            var readyHouses = map.getBuildings().stream()
                    .filter(house -> Objects.equals(house.getPlayer(), player))
                    .filter(Building::isReady)
                    .toList();

            if (readyHouses.stream().anyMatch(house -> house instanceof Quarry) &&
                readyHouses.stream().anyMatch(house -> house instanceof Sawmill) &&
                readyHouses.stream().anyMatch(house -> house instanceof Woodcutter)) {
                listeners.forEach(listener -> listener.onEvent(PLANKS_AND_STONES_READY));
                planksAndStonesReadyReported = true;
            }
        }

        // DISCOVERED_OTHER_PLAYER
        for (var point: changes.newDiscoveredLand()) {
            for (var newPlayer : map.getPlayers()) {
                if (discoveredPlayers.contains(newPlayer)) {
                    continue;
                }

                if (newPlayer.getOwnedLand().contains(point)) {
                    discoveredPlayers.add(newPlayer);
                    listeners.forEach(listener -> listener.onEvent(DISCOVERED_OTHER_PLAYER));

                    break;
                }
            }
        }

        // DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE
        // FOUND_GOLD
        for (var sign : changes.newSigns()) {
            if (sign.getType() == null) {
                continue;
            }

            if (sign.getType() == GOLD) {
                listeners.forEach(listener -> listener.onEvent(FOUND_GOLD));
            } else {
                listeners.forEach(listener -> listener.onEvent(DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE));
            }
        }

        // NO_MORE_STONE
        // CAPTURED_BUILDING
        // LOST_TERRITORY
        for (var message : changes.newMessages()) {
            if (message instanceof NoMoreResourcesMessage noMoreResourcesMessage && noMoreResourcesMessage.building() instanceof Quarry) {
                listeners.forEach(listener -> listener.onEvent(NO_MORE_STONE));
            } else if (message instanceof BuildingCapturedMessage buildingCapturedMessage) {
                listeners.forEach(listener -> listener.onEvent(CAPTURED_BUILDING));
            } else if (message instanceof BuildingLostMessage buildingLostMessage) {
                listeners.forEach(listener -> listener.onEvent(LOST_TERRITORY));
            }
        }

        // EXPANDED_TERRITORY
        if (!ownedLand.isEmpty() && player.getOwnedLand().stream().anyMatch(point -> !ownedLand.contains(point))) {
            listeners.forEach(listener -> listener.onEvent(EXPANDED_TERRITORY));
        }

        ownedLand.addAll(player.getOwnedLand());
    }

    private int countGenerals() {
        return map.getBuildings().stream()
                .filter(Building::isMilitaryBuilding)
                .filter(Building::isReady)
                .filter(building -> Objects.equals(building.getPlayer(), player))
                .mapToInt(building -> building.getHostedSoldiersWithRank(GENERAL_RANK))
                .sum();
    }

    public void report(GamePlayEvent event, Player player) {
        System.out.println("ET: got event %s from player %s".formatted(event, player));

        listeners.forEach(listener -> listener.onEvent(event));
    }

    public void addListener(GamePlayEventListener listener) {
        listeners.add(listener);
    }
}
