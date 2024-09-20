/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

import org.appland.settlers.model.Point;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.maps.Utils.isEven;

/**
 * Class representing a map file used in the game. Handles map properties and translation between file points and game points.
 */
public class MapFile {

    private static final String FILE_HEADER_V1 = "WORLD_V1.0";

    private final List<Point> startingPositions = new ArrayList<>();
    private final List<PlayerFace> playerFaces = new ArrayList<>();
    private final List<UniqueMass> masses = new ArrayList<>();
    private final List<MapFilePoint> mapFilePoints = new ArrayList<>();
    private final List<java.awt.Point> fileStartingPoints = new ArrayList<>();
    private final Map<Point, MapFilePoint> gamePointToMapFilePointMap = new HashMap<>();
    private final Map<java.awt.Point, MapFilePoint> mapFilePointToGamePointMap = new HashMap<>();

    private int width = -1;
    private int height = -1;
    private int maxNumberOfPlayers = -1;
    private TerrainType terrainType;
    private String author;
    private boolean isUnlimitedPlayEnabled;
    private String title;
    private MapTitleType mapTitleType;
    private HeaderType headerType;

    /**
     * Sets the title of the map.
     *
     * @param title the title to set
     */
    void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the title of the map.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the terrain type of the map.
     *
     * @param terrainType the terrain type to set
     */
    void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    /**
     * Sets the maximum number of players for the map.
     *
     * @param numberOfPlayers the maximum number of players
     */
    void setMaxNumberOfPlayers(int numberOfPlayers) {
        this.maxNumberOfPlayers = numberOfPlayers;
    }

    /**
     * Sets the author of the map.
     *
     * @param author the author's name
     */
    void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Adds a starting position in the map file.
     *
     * @param startingPoint the starting position
     */
    void addStartingPosition(java.awt.Point startingPoint) {
        fileStartingPoints.add(startingPoint);
    }

    /**
     * Enables unlimited play for the map.
     */
    void enableUnlimitedPlay() {
        isUnlimitedPlayEnabled = true;
    }

    /**
     * Disables unlimited play for the map.
     */
    void disableUnlimitedPlay() {
        isUnlimitedPlayEnabled = false;
    }

    /**
     * Sets player faces for the map.
     *
     * @param playerFaces list of player faces
     */
    void setPlayerFaces(List<PlayerFace> playerFaces) {
        this.playerFaces.clear();
        this.playerFaces.addAll(playerFaces);
    }

    /**
     * Sets the width of the map.
     *
     * @param width the width of the map
     */
    void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the width of the map.
     *
     * @return the map width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the height of the map.
     *
     * @param height the height of the map
     */
    void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the height of the map.
     *
     * @return the map height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Adds a map point to the list of points.
     *
     * @param mapFilePoint the map point to add
     */
    void addMapFilePoint(MapFilePoint mapFilePoint) {
        mapFilePoints.add(mapFilePoint);
    }

    void addMassStartingPoint(java.awt.Point position) {
        // Ignore for now
    }

    /**
     * Returns the list of all points in the map file.
     *
     * @return the list of points
     */
    public List<MapFilePoint> getMapFilePoints() {
        return mapFilePoints;
    }

    /**
     * Returns the list of game starting points after converting from map file points.
     *
     * @return the list of game starting points
     */
    public List<Point> getGamePointStartingPoints() {
        return startingPositions;
    }

    /**
     * Returns the maximum number of players allowed for the map.
     *
     * @return maximum number of players
     */
    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    /**
     * Returns the terrain type of the map.
     *
     * @return terrain type
     */
    public TerrainType getTerrainType() {
        return terrainType;
    }

    /**
     * Returns the author of the map.
     *
     * @return author's name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the list of player faces used in the map.
     *
     * @return list of player faces
     */
    public List<PlayerFace> getPlayerFaces() {
        return playerFaces;
    }

    /**
     * Returns a MapFile point by its index in the list.
     *
     * @param i the index of the MapFile point
     * @return the map point
     */
    public MapFilePoint getMapFilePoint(int i) {
        return mapFilePoints.get(i);
    }

    /**
     * Maps file points to game points based on the map layout and dimensions.
     *
     * The spots in the map file are saved according to the pattern:
     *
     *   00  01  02  03
     * 04  05  06  07
     *   08  09  0A  0B
     * 0C  0D  0E  0F
     *
     * While points in the game are structured as:
     *
     *
     * 0,2       2, 2
     *      1,1       3,1
     * 0,0       2,0
     *
     * With cropping:
     *  - Every second row starts at x = -1
     *  - Width in gamemap becomes width_in_file x 2 - 2
     *  - Height in gamemap depends on if cropping is needed
     *      - If height is even - no cropping is needed
     *      - If height is odd - need to crop last row. height_in_game = height_in_file - 1
     *
     */
    public void mapFilePointsToGamePoints() {
        int rowLength = width;
        int rowIndex = 1;
        int mapFileY = 1;
        int mapFileX = 1;
        int gamePointY = isEven(height) ? height : height - 1;
        int gamePointX = isEven(gamePointY) ? 0 : 1;

        for (MapFilePoint mapFilePoint : mapFilePoints) {
            Point gamePoint = new Point(gamePointX, gamePointY);
            java.awt.Point mapFilePosition = new java.awt.Point(mapFileX, mapFileY);

            gamePointToMapFilePointMap.put(gamePoint, mapFilePoint);
            mapFilePointToGamePointMap.put(mapFilePosition, mapFilePoint);

            mapFilePoint.setPositionAsGamePoint(gamePoint);

            if (rowIndex == rowLength) {
                rowIndex = 1;
                mapFileY++;
                gamePointY--;
                mapFileX = 1;
                gamePointX = isEven(gamePointY) ? 0 : 1;
            } else {
                rowIndex++;
                mapFileX++;
                gamePointX += 2;
            }
        }
    }

    /**
     * Translates file starting points to game points.
     *
     * @throws InvalidMapException if a starting point is outside the map
     */
    void translateFileStartingPointsToGamePoints() throws InvalidMapException {
        for (var point : fileStartingPoints) {
            if (!mapFilePointToGamePointMap.containsKey(point)) {
                continue;
            }

            if (point == null || mapFilePointToGamePointMap.get(point) == null) {
                System.out.println(point);
                System.out.println(startingPositions);
                System.out.println(mapFilePointToGamePointMap);
                System.out.println(mapFilePointToGamePointMap.get(point));
            }

            MapFilePoint mapFilePoint = mapFilePointToGamePointMap.get(point);
            if (mapFilePoint == null) {
                throw new InvalidMapException(String.format("The starting point %s is outside of the map.", point));
            }

            startingPositions.add(new Point(mapFilePoint.getGamePointPosition()));
        }
    }

    /**
     * Returns the map point corresponding to a given game point.
     *
     * @param point the game point
     * @return the corresponding map point
     */
    public MapFilePoint getMapFilePoint(Point point) {
        return gamePointToMapFilePointMap.get(point);
    }

    /**
     * Sets the map title type.
     *
     * @param titleType the title type to set
     */
    public void setTitleType(MapTitleType titleType) {
        this.mapTitleType = titleType;
    }

    /**
     * Returns the map title type.
     *
     * @return the map title type as a string
     */
    public String getTitleType() {
        return mapTitleType.name();
    }

    /**
     * Returns whether unlimited play is enabled.
     *
     * @return true if unlimited play is enabled, false otherwise
     */
    public boolean isUnlimitedPlayEnabled() {
        return isUnlimitedPlayEnabled;
    }

    /**
     * Returns the header type of the map.
     *
     * @return the header type
     */
    public HeaderType getHeaderType() {
        return headerType;
    }

    /**
     * Sets the header type based on the file header string.
     *
     * @param fileHeader the file header
     * @throws RuntimeException if the header type is not supported
     */
    public void setHeader(String fileHeader) {
        headerType = switch (fileHeader) {
            case FILE_HEADER_V1 -> HeaderType.V1;
            default -> throw new RuntimeException("Can't handle header type: " + fileHeader);
        };
    }

    /**
     * Returns the list of starting points from the file.
     *
     * @return list of file starting points
     */
    public List<java.awt.Point> getStartingPoints() {
        return fileStartingPoints;
    }

    /**
     * Returns the dimensions of the map.
     *
     * @return the map's dimensions as a Dimension object
     */
    public Dimension getDimension() {
        return new Dimension(width, height);
    }
}
