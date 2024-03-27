/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.computer;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
public class AttackPlayer implements ComputerPlayer {
    private final Player         player;
    private final List<Building> recentlyWonBuildings;

    private GameMap        map;
    private State          state;
    private Headquarter    headquarter;
    private Building       buildingUnderAttack;

    enum State {
        INITIAL_STATE,
        LOOK_FOR_BUILDINGS_TO_ATTACK,
        ATTACKING,
        WAITING_FOR_ATTACK_TO_START
    }

    public AttackPlayer(Player player, GameMap map) {
        this.player = player;
        this.map    = map;

        state = State.INITIAL_STATE;

        buildingUnderAttack = null;
        recentlyWonBuildings = new ArrayList<>();
    }

    @Override
    public void turn() throws Exception {

        /* Record the state before the turn */
        State stateBefore = state;

        /* Update the building under attack in case it was upgraded */
        if (buildingUnderAttack != null) {
            buildingUnderAttack = map.getBuildingAtPoint(buildingUnderAttack.getPosition());
        }

        /* Start with finding the headquarters */
        if (state == State.INITIAL_STATE) {

            /* Find headquarter */
            headquarter = Utils.findHeadquarter(player);

            /* Change the state to ready to build */
            state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;
        } else if (state == State.LOOK_FOR_BUILDINGS_TO_ATTACK) {

            /* Find opponents' buildings within field of view */
            List<Building> visibleOpponentBuildings = Utils.findVisibleOpponentBuildings(map, player);

            /* Find a building to attack */
            Building buildingToAttack = findBuildingToAttack(visibleOpponentBuildings);

            /* Keep looking for buildings to attack if there is no building now */
            if (buildingToAttack == null) {
                return;
            }

            /* Change state to attacking */
            state = State.WAITING_FOR_ATTACK_TO_START;
            buildingUnderAttack = buildingToAttack;

            /* Attack the identified building */
            player.attack(buildingToAttack, player.getAvailableAttackersForBuilding(buildingToAttack), AttackStrength.STRONG);
            System.out.println(" - Attacking " + buildingToAttack + ", owned by " + buildingToAttack.getPlayer().getName());
        } else if (state == State.WAITING_FOR_ATTACK_TO_START) {

            if (buildingUnderAttack == null) {
                System.out.println(" - Building under attack is gone");

                state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;

                return;
            }

            if (buildingUnderAttack.isUnderAttack()) {
                System.out.println(" - Attack has started");
                state = State.ATTACKING;
            }
        } else if (state == State.ATTACKING) {

            /* Check if the attack is finished */
            if (!buildingUnderAttack.isUnderAttack()) {
                state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;

                if (buildingUnderAttack.getPlayer().equals(player)) {

                    System.out.println(" - Attack player: Adding won building at " + buildingUnderAttack.getPosition() + " to list");

                    recentlyWonBuildings.add(buildingUnderAttack);
                }

                state = State.LOOK_FOR_BUILDINGS_TO_ATTACK;

                buildingUnderAttack = null;
            }
        }
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public Player getControlledPlayer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Building findBuildingToAttack(List<Building> visibleOpponentBuildings) throws Exception {

        for (Building building : visibleOpponentBuildings) {

            /* Filter out non-military buildings */
            if (!building.isMilitaryBuilding()) {
                continue;
            }

            int availableAttackers = player.getAvailableAttackersForBuilding(building);

            /* Filter out buildings that cannot be attacked */
            if (availableAttackers == 0) {
                continue;
            }

            /* Choose the first building that can be attacked */
            return building;
        }

        return null;
    }


    boolean hasWonBuildings() {
        return !recentlyWonBuildings.isEmpty();
    }

    void clearWonBuildings() {
        recentlyWonBuildings.clear();
    }

    public List<Building> getWonBuildings() {
        return recentlyWonBuildings;
    }

    boolean isAttacking() {
        return state == State.ATTACKING || state == State.WAITING_FOR_ATTACK_TO_START;
    }
}
