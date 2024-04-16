package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flag implements EndPoint {

    private static final int MAX_NUMBER_OF_STACKED_CARGO = 8;

    private final List<Cargo> stackedCargo;
    private final Set<Cargo> promisedCargo;

    private Point  position;
    private int    geologistsCalled;
    private int    scoutsCalled;
    private Player player;
    private FlagType flagType;
    private boolean fightIsHappening;

    public Flag(Point point) {
        position         = point;
        stackedCargo     = new ArrayList<>();
        geologistsCalled = 0;
        scoutsCalled     = 0;
        promisedCargo    = new HashSet<>();
        fightIsHappening = false;

        /* Default flag type is normal */
        flagType = FlagType.NORMAL;
    }

    Flag(Player player, Point point) {
        this(point);

        this.player = player;
    }

    public List<Cargo> getStackedCargo() {
        return stackedCargo;
    }

    @Override
    public void putCargo(Cargo cargo) {

        cargo.setPosition(getPosition());
        stackedCargo.add(cargo);

        /* Give the cargo a chance to re-plan */
        cargo.rerouteIfNeeded();

        /* Remove the promise for this cargo */
        promisedCargo.remove(cargo);

        /* Report that the flag has changed */
        if (player != null) {
            GameMap map = player.getMap();

            if (map != null) {
                player.getMap().reportChangedFlag(this);
            }
        }
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point point) {
        position = point;
    }

    @Override
    public String toString() {
        if (stackedCargo.isEmpty()) {
            return "Flag " + position;
        } else {
            StringBuilder stringBuilder = new StringBuilder("Flag " + position + " (stacked cargo:");

            for (Cargo cargo : stackedCargo) {
                stringBuilder.append(" ").append(cargo.getMaterial().name());
            }

            stringBuilder.append(")");

            return stringBuilder.toString();
        }
    }

    public Cargo retrieveCargo(Cargo cargo) {

        if (stackedCargo.contains(cargo)) {

            stackedCargo.remove(cargo);

            if (player != null) {
                GameMap map = player.getMap();

                if (map != null) {
                    player.getMap().reportChangedFlag(this);
                }
            }

            return cargo;
        }

        return null;
    }

    public void callGeologist() {
        geologistsCalled++;
    }

    public void geologistSent() {
        geologistsCalled--;
    }

    public boolean needsGeologist() {
        return geologistsCalled > 0;
    }

    public void callScout() {
        scoutsCalled++;
    }

    public void scoutSent() {
        scoutsCalled--;
    }

    public boolean needsScout() {
        return scoutsCalled > 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void promiseCargo(Cargo cargo) {
        promisedCargo.add(cargo);
    }

    public boolean hasPlaceForMoreCargo() {
        return stackedCargo.size() + promisedCargo.size() < MAX_NUMBER_OF_STACKED_CARGO;
    }

    public void onRemove() {

        /* Break delivery promises for any stacked cargo */
        for (Cargo cargo : stackedCargo) {

            if (!cargo.isPickupPromised()) {
                continue;
            }

            Building building = cargo.getTarget();

            building.cancelPromisedDelivery(cargo);
        }
    }

    public FlagType getType() {
        return flagType;
    }

    public void setType(FlagType flagType) {
        this.flagType = flagType;
    }

    public void setFightIsTakingPlace() {
        fightIsHappening = true;
    }

    public boolean isFightingAtFlag() {
        return fightIsHappening;
    }

    public enum FlagType {
        NORMAL, MAIN, MARINE
    }
}
