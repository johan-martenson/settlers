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
 *
 * @author johan
 */
public class MapFile {

    private static final String FILE_HEADER_V1 = "WORLD_V1.0";
    /* Map properties */
    private final List<Point> startingPositions;
    private final List<PlayerFace> playerFaces;
    private final List<UniqueMass> masses;
    private final List<MapFilePoint> pointList;
    private final List<java.awt.Point> fileStartingPoints;
    private final Map<Point, MapFilePoint> gamePointToMapFilePointMap;
    private final Map<java.awt.Point, MapFilePoint> mapFilePointToGamePointMap;

    int         width;
    int         height;
    int         maxNumberOfPlayers;
    TerrainType terrainType;
    String      author;
    boolean     unlimitedPlay;
    private     String title;
    private     MapTitleType mapTitleType;
    private HeaderType headerType;

    public MapFile() {
        width                      = -1;
        height                     = -1;
        maxNumberOfPlayers         = -1;
        startingPositions          = new ArrayList<>();
        playerFaces                = new ArrayList<>();
        masses                     = new ArrayList<>();
        pointList                  = new ArrayList<>();
        fileStartingPoints         = new ArrayList<>();
        mapFilePointToGamePointMap = new HashMap<>();
        gamePointToMapFilePointMap = new HashMap<>();
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    void setMaxNumberOfPlayers(int numberOfPlayers) {
        this.maxNumberOfPlayers = numberOfPlayers;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    void addStartingPosition(java.awt.Point startingPoint) {
        fileStartingPoints.add(startingPoint);
    }

    void enableUnlimitedPlay() {
        unlimitedPlay = true;
    }

    void disableUnlimitedPlay() {
        unlimitedPlay = false;
    }

    void setPlayerFaces(List<PlayerFace> playerFaces) {
        this.playerFaces.clear();

        this.playerFaces.addAll(playerFaces);
    }

    void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    void addSpot(MapFilePoint spot) {
        pointList.add(spot);
    }

    void addMassStartingPoint(java.awt.Point position) {
        // Ignore for now
    }

    List<MapFilePoint> getMapFilePoints() {
        return pointList;
    }

    public List<Point> getGamePointStartingPoints() {
        return startingPositions;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    TerrainType getTerrainType() {
        return terrainType;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isPlayUnlimited() {
        return unlimitedPlay;
    }

    public List<PlayerFace> getPlayerFaces() {
        return playerFaces;
    }

    public MapFilePoint getSpot(int i) {
        return pointList.get(i);
    }

    /**
     * Map file positions to game points
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

        /* Set initial values */
        int rowIndex = 1;
        int mapFileY = 1; // FIXME: are mapfile coordinates starting at 1 or 0?
        int mapFileX = 1;
        int gamePointX;
        int gamePointY;

        /* Start game point x at 0 if height is even, otherwise 1 */
        if (isEven(height)) {
            gamePointY = height;
            gamePointX = 0;
        } else {
            gamePointY = height - 1;
            gamePointX = 1;
        }

        /* Go through each map file point and calculate its coordinates in mapfile coordinates and game points */
        for (MapFilePoint mapFilePoint : pointList) {

            /* Store mapping from game point and map file coordinates to the map file point */
            Point gamePoint = new Point(gamePointX, gamePointY);
            java.awt.Point mapFilePosition = new java.awt.Point(mapFileX, mapFileY);

            gamePointToMapFilePointMap.put(gamePoint, mapFilePoint);
            mapFilePointToGamePointMap.put(mapFilePosition, mapFilePoint);

            mapFilePoint.setPositionAsGamePoint(gamePoint);

            /* Go to next row if the current row is done */
            if (rowIndex == rowLength) {
                rowIndex = 1;

                mapFileY = mapFileY + 1;
                gamePointY = gamePointY - 1;

                mapFileX = 1;

                if (isEven(gamePointY)) {
                    gamePointX = 0;
                } else {
                    gamePointX = 1;
                }

            /* Go to next place in the row */
            } else {
                rowIndex = rowIndex + 1;

                mapFileX = mapFileX + 1;
                gamePointX = gamePointX + 2;
            }
        }
    }

    void translateFileStartingPointsToGamePoints() throws InvalidMapException {

        for (java.awt.Point point : fileStartingPoints) {

            /* Filter invalid starting points - this can exist e.g. on mission maps */
            if (!mapFilePointToGamePointMap.containsKey(point)) {
                continue;
            }

            if (point == null || mapFilePointToGamePointMap.get(point) == null) {
                System.out.println(point);
                System.out.println(startingPositions);
                System.out.println(mapFilePointToGamePointMap);
                System.out.println(mapFilePointToGamePointMap.get(point));
            }

            MapFilePoint spot = mapFilePointToGamePointMap.get(point);

            if (spot == null) {
                throw new InvalidMapException("The starting point " + point + " is outside of the map.");
            }

            startingPositions.add(new Point(spot.getGamePointPosition()));
        }
    }

    public MapFilePoint getMapFilePoint(Point point) {
        return gamePointToMapFilePointMap.get(point);
    }

    public void setTitleType(MapTitleType titleType) {
        this.mapTitleType = titleType;
    }

    public String getTitleType() {
        return mapTitleType.name();
    }

    public boolean isUnlimitedPlay() {
        return unlimitedPlay;
    }

    public HeaderType getHeaderType() {
        return headerType;
    }

    public void setHeader(String fileHeader) {
        if (FILE_HEADER_V1.equals(fileHeader)) {
            headerType = HeaderType.V1;
        } else {
            throw new RuntimeException("Can't handle header type: " + fileHeader);
        }
    }

    public List<java.awt.Point> getStartingPoints() {
        return fileStartingPoints;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }
}
