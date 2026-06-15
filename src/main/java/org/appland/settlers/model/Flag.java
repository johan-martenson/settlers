package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flag implements EndPoint {

    private static final int MAX_NUMBER_OF_STACKED_CARGO = 8;

    private final List<Cargo> stackedCargo = new ArrayList<>();
    private final Set<Cargo> promisedCargo = new HashSet<>();

    private Point    position;
    private int      geologistsCalled = 0;
    private int      scoutsCalled = 0;
    private Player   player;
    private FlagType flagType = FlagType.NORMAL;
    private boolean  fightIsHappening = false;

    public Flag(Point point) {
        position = point;
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
        cargo.setPosition(position);
        stackedCargo.add(cargo);

        // Give the cargo a chance to re-plan
        cargo.rerouteIfNeeded();

        // Remove the promise for this cargo
        promisedCargo.remove(cargo);

        // Report that the flag has changed
        if (player != null) {
            var map = player.getMap();

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
            var stringBuilder = new StringBuilder("Flag " + position + " (stacked cargo:");

            for (var cargo : stackedCargo) {
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
                var map = player.getMap();

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

        // Break delivery promises for any stacked cargo
        for (var cargo : stackedCargo) {
            if (!cargo.isPickupPromised()) {
                continue;
            }

            var building = cargo.getTarget();
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

    public void setNoFightAtFlag() {
        fightIsHappening = false;
    }

    public enum FlagType {
        NORMAL, MAIN, MARINE
    }
}
