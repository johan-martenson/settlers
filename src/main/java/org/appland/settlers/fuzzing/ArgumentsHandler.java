package org.appland.settlers.fuzzing;

import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ArgumentsHandler {

    private final InputStream inputStream;

    public ArgumentsHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    int getIntFor3Chars() throws SettlersModelDriverException, IOException {

        byte[] bytes = new byte[3];

        int result = inputStream.read(bytes, 0, 3);

        if (result == -1) {
            throw new EOFException();
        }

        String remaining = new String(bytes, StandardCharsets.ISO_8859_1);

        if (remaining.length() < 3) {
            throw new SettlersModelDriverException();
        }

        return Integer.parseInt(remaining.substring(0, 3));
    }

    int getIntFor2Chars() throws SettlersModelDriverException, IOException {

        byte[] bytes = new byte[3];

        int result = inputStream.read(bytes, 0, 2);

        if (result == -1) {
            throw new EOFException();
        }

        String remaining = new String(bytes, StandardCharsets.ISO_8859_1);

        if (remaining.length() < 2) {
            throw new SettlersModelDriverException();
        }

        return Integer.parseInt(remaining.substring(0, 2));
    }

    int getUnsignedIntFor1Chars() throws IOException {
        byte[] bytes = new byte[1];

        int result = inputStream.read(bytes, 0, 1);

        if (result == -1) {
            throw new EOFException();
        }

        return Math.abs(bytes[0]);
    }

    public String getChar() throws IOException {
        byte[] bytes = new byte[1];

        int result = inputStream.read(bytes, 0, 1);

        if (result == -1) {
            throw new EOFException();
        }

        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    public Point getPointForChars() throws IOException {
        return new Point(
            getUnsignedIntFor1Chars(),
            getUnsignedIntFor1Chars()
        );
    }

    public Player getPlayerFromChar(GameMap map) throws IOException {
        int playerIndex = getUnsignedIntFor1Chars();

        List<Player> players = map.getPlayers();

        return players.get(playerIndex % players.size());
    }

    public Building getBuildingFromChar(GameMap map) throws IOException {
        int index = getUnsignedIntFor1Chars();

        return map.getBuildings().get(index % map.getBuildings().size());
    }

    public Flag getFlagFromChar(GameMap map) throws IOException {
        int index = getUnsignedIntFor1Chars();

        return map.getFlags().get(index % map.getFlags().size());
    }

    public Road getRoadFromChar(GameMap map) throws IOException {
        int index = getUnsignedIntFor1Chars();

        return map.getRoads().get(index % map.getRoads().size());
    }

    public Material getMaterialFromChar() throws IOException {
        int index = getUnsignedIntFor1Chars();

        return Material.values()[index % Material.values().length];
    }

    public Building getBuildingFromCharByPoint(GameMap map) throws IOException {
        Point point = getPointForChars();

        return map.getBuildingAtPoint(point);
    }

    public Point getPointByIndex(GameMap map) throws IOException {
        int pointsPerRow = map.getWidth() / 2;

        int largeNumber = getUnsignedIntFor1Chars() * getUnsignedIntFor1Chars();
        int amountOfPointsInMap = (pointsPerRow) * map.getHeight();
        int index = largeNumber % amountOfPointsInMap;

        int y = (int) Math.round((double)index / (double)pointsPerRow);
        int x = largeNumber % pointsPerRow;

        if (y % 2 == 0) {
            return new Point(x * 2, y);
        }

        return new Point(x * 2 + 1, y);
    }
}
