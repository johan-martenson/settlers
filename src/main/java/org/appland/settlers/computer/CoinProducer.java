package org.appland.settlers.computer;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Mint;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Size.SMALL;

/**
 * The CoinProducer class manages the coin production process for a player, including building mints and ensuring gold is available.
 */
public class CoinProducer implements ComputerPlayer {
    private final Player controlledPlayer;
    private final List<Mint> mints = new ArrayList<>();

    private GameMap map;
    private Building headquarter;
    private State state = State.INITIALIZING;

    private enum State {
        INITIALIZING,
        NEEDS_GOLD,
        MINT_NEEDED,
        WAITING_FOR_MINT,
        DONE
    }

    /**
     * Constructs a CoinProducer for the specified player and game map.
     *
     * @param player The player to be controlled.
     * @param map    The game map.
     */
    public CoinProducer(Player player, GameMap map) {
        this.controlledPlayer = player;
        this.map = map;
    }

    /**
     * Executes the actions needed in the current game turn.
     *
     * @throws Exception If there is an error during the turn.
     */
    @Override
    public void turn() throws Exception {
        switch (state) {
            case INITIALIZING -> {
                headquarter = controlledPlayer.getBuildings().stream()
                        .filter(building -> building instanceof Headquarter)
                        .findFirst()
                        .orElse(null);

                if (headquarter != null) {
                    state = State.NEEDS_GOLD;
                }
            }
            case NEEDS_GOLD -> {
                // Start building a mint if there is gold in storage
                if (headquarter.getAmount(GOLD) > 0 && mints.isEmpty()) {
                    state = State.MINT_NEEDED;
                }
            }
            case MINT_NEEDED -> {
                // Find a point to build a mint
                Point pointForMint = findPointForMint();

                if (pointForMint != null) {
                    // Build the mint
                    Mint mint = map.placeBuilding(new Mint(controlledPlayer), pointForMint);
                    mints.add(mint);

                    // Connect the mint with the headquarter
                    Road road = GamePlayUtils.connectPointToBuilding(controlledPlayer, map, mint.getFlag().getPosition(), headquarter);
                    GamePlayUtils.fillRoadWithFlags(map, road);

                    state = State.WAITING_FOR_MINT;
                }
            }
            case WAITING_FOR_MINT -> {
                // Wait for all mints to finish building
                boolean buildingsDone = mints.stream().allMatch(Mint::isReady);

                if (buildingsDone) {
                    state = State.DONE;
                }
            }
            case DONE -> {
                // Coin production completed
            }
        }
    }

    /**
     * Sets the game map for the coin producer.
     *
     * @param map The game map.
     */
    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    /**
     * Returns the player being controlled by this coin producer.
     *
     * @return The controlled player.
     */
    @Override
    public Player getControlledPlayer() {
        return controlledPlayer;
    }

    /**
     * Finds a suitable point to build a mint near the headquarters.
     *
     * @return A point where the mint can be placed, or null if no suitable point is found.
     */
    private Point findPointForMint() {
        return controlledPlayer.getLandInPoints().stream()
                .filter(point -> map.isAvailableHousePoint(controlledPlayer, point) != null && map.isAvailableHousePoint(controlledPlayer, point) != SMALL)
                .min((p1, p2) -> Double.compare(p1.distance(headquarter.getPosition()), p2.distance(headquarter.getPosition())))
                .orElse(null);
    }

    /**
     * Checks if coin production is complete.
     *
     * @return true if at least one mint is ready, false otherwise.
     */
    boolean coinProductionDone() {
        return GamePlayUtils.listContainsAtLeastOneReadyBuilding(mints);
    }
}
