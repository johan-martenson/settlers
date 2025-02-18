/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * AttackPlayer is a computer-controlled player that attacks enemy buildings.
 */
public class AttackPlayer implements ComputerPlayer {
    private final Player         player;
    private final List<Building> recentlyWonBuildings = new ArrayList<>();

    private GameMap map;
    private State state = State.INITIAL_STATE;
    private Building buildingUnderAttack;

    /**
     * States defining the behavior of the AttackPlayer.
     */
    enum State {
        INITIAL_STATE,
        LOOK_FOR_BUILDINGS_TO_ATTACK,
        ATTACKING,
        WAITING_FOR_ATTACK_TO_START
    }

    /**
     * Constructs an AttackPlayer controlling the given player on the specified map.
     *
     * @param player The player this AI controls
     * @param map    The map on which this player operates
     */
    public AttackPlayer(Player player, GameMap map) {
        this.player = player;
        this.map    = map;
    }

    /**
     * Executes a turn for the AttackPlayer.
     *
     * @throws Exception If an issue occurs during the turn
     */
    @Override
    public void turn() throws Exception {

        // Update the building under attack if upgraded
        if (buildingUnderAttack != null) {
            buildingUnderAttack = map.getBuildingAtPoint(buildingUnderAttack.getPosition());
        }

        switch (state) {
            case INITIAL_STATE -> state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;
            case LOOK_FOR_BUILDINGS_TO_ATTACK -> {
                List<Building> visibleOpponentBuildings = GamePlayUtils.findVisibleOpponentBuildings(map, player);

                Building buildingToAttack = findBuildingToAttack(visibleOpponentBuildings);

                if (buildingToAttack != null) {
                    // Attack and transition to waiting for attack to start
                    state = State.WAITING_FOR_ATTACK_TO_START;
                    buildingUnderAttack = buildingToAttack;
                    player.attack(buildingToAttack, player.getAvailableAttackersForBuilding(buildingToAttack), AttackStrength.STRONG);
                    System.out.printf(" - Attacking %s, owned by %s%n", buildingToAttack, buildingToAttack.getPlayer().getName());
                }
            }
            case WAITING_FOR_ATTACK_TO_START -> {
                if (buildingUnderAttack == null) {
                    System.out.println(" - Building under attack is gone");
                    state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;
                } else if (buildingUnderAttack.isUnderAttack()) {
                    System.out.println(" - Attack has started");
                    state = State.ATTACKING;
                }
            }
            case ATTACKING -> {
                if (!buildingUnderAttack.isUnderAttack()) {
                    if (buildingUnderAttack.getPlayer().equals(player)) {
                        System.out.printf(" - Attack player: Adding won building at %s to list%n", buildingUnderAttack.getPosition());
                        recentlyWonBuildings.add(buildingUnderAttack);
                    }

                    state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;
                    buildingUnderAttack = null;
                }
            }
        }
    }

    /**
     * Sets the map for this AttackPlayer.
     *
     * @param map The new map to be set
     */
    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    /**
     * Gets the player controlled by this AI.
     *
     * @return The player controlled by this AI
     */
    @Override
    public Player getControlledPlayer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Finds a building to attack from the visible opponent buildings.
     *
     * @param visibleOpponentBuildings The list of visible opponent buildings
     * @return The building selected to attack or null if none found
     */
    private Building findBuildingToAttack(List<Building> visibleOpponentBuildings) {
        return visibleOpponentBuildings.stream()
                .filter(Building::isMilitaryBuilding) // Only military buildings
                .filter(Building::isReady)
                .filter(building -> {
                    try {
                        return player.getAvailableAttackersForBuilding(building) > 0;
                    } catch (InvalidUserActionException e) {
                        throw new RuntimeException(e);
                    }
                }) // Can be attacked
                .findFirst()
                .orElse(null);
    }


    /**
     * Checks if the player has won any buildings.
     *
     * @return true if any buildings were won; otherwise, false
     */
    boolean hasWonBuildings() {
        return !recentlyWonBuildings.isEmpty();
    }

    /**
     * Clears the list of recently won buildings.
     */
    void clearWonBuildings() {
        recentlyWonBuildings.clear();
    }

    /**
     * Gets the list of recently won buildings.
     *
     * @return The list of won buildings
     */
    public List<Building> getWonBuildings() {
        return recentlyWonBuildings;
    }

    /**
     * Checks if the player is currently attacking.
     *
     * @return true if attacking; otherwise, false
     */
    boolean isAttacking() {
        return state == State.ATTACKING || state == State.WAITING_FOR_ATTACK_TO_START;
    }
}
