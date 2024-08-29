package org.appland.settlers.maps;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.utils.StreamReader;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.appland.settlers.maps.Utils.isEven;

/**
 * Loads a map binary file into a MapFile instance
 *
 */
public class MapLoader {

    @Option(name = "--file", usage = "Map file to load")
    String filename;

    @Option(name = "--debug", usage = "Print debug information")
    boolean debug = false;

    @Option(name = "--info", usage = "Print information about the map")
    boolean printInfo = false;

    public static void main(String[] args) {
        MapLoader mapLoader = new MapLoader();
        CmdLineParser parser = new CmdLineParser(mapLoader);

        try {
            parser.parseArgument(args);

            MapFile mapFile = mapLoader.loadMapFromFile(mapLoader.filename);
            GameMap gameMap = mapLoader.convertMapFileToGameMap(mapFile);

            if (mapLoader.printInfo) {
                mapLoader.printMapInformation(mapFile);
            }
        } catch (Exception ex) {
            Logger.getLogger(MapLoader.class.getName()).log(Level.SEVERE, null, ex);

            System.exit(1);
        }
    }

    /**
     * Prints information about the loaded map.
     *
     * @param mapFile the MapFile instance to print information about
     */
    private void printMapInformation(MapFile mapFile) {
        System.out.printf(" - Title: %s%n", mapFile.getTitle());
        System.out.printf(" - Author: %s%n", mapFile.getAuthor());
        System.out.printf(" - Max number of players: %d%n", mapFile.getMaxNumberOfPlayers());

        System.out.println(" - Starting points:");
        mapFile.getGamePointStartingPoints().forEach(point ->
                System.out.printf("    - %d, %d%n", point.x, point.y)
        );

        System.out.println(" - Player types:");
        mapFile.getPlayerFaces().forEach(face ->
                System.out.printf("    - %s%n", face.name().toLowerCase())
        );

        System.out.printf(" - Width x height: %d x %d%n", mapFile.getWidth(), mapFile.getHeight());
        System.out.printf(" - Terrain type: %s%n", mapFile.getTerrainType().name().toLowerCase());
        System.out.printf(" - Unlimited play: %b%n", mapFile.isUnlimitedPlay());
        System.out.printf(" - Title type: %s%n", mapFile.getTitleType());
        System.out.printf(" - File header: %s%n", mapFile.getHeaderType().name().toLowerCase());
    }

    /**
     * Loads a map file from the specified filename.
     *
     * @param mapFilename the filename of the map to load
     * @return the loaded MapFile instance
     * @throws SettlersMapLoadingException if there is an error loading the map
     * @throws IOException                 if there is an I/O error
     * @throws InvalidMapException         if the map is invalid
     */
    public MapFile loadMapFromFile(String mapFilename) throws SettlersMapLoadingException, IOException, InvalidMapException {
        printlnIfDebug(String.format("Loading: %s", mapFilename));

        try (InputStream fileInputStream = Files.newInputStream(Paths.get(mapFilename))) {
            return loadMapFromStream(fileInputStream);
        }
    }

    private void printlnIfDebug() {
        if (debug) {
            System.out.println();
        }
    }

    private void printlnIfDebug(Object message) {
        if (debug) {
            System.out.println(message);
        }
    }

    private void printIfDebug(String message) {
        if (debug) {
            System.out.print(message);
        }
    }

    /**
     * Loads a map from the provided InputStream.
     *
     * @param inputStream the InputStream to load the map from
     * @return the loaded MapFile instance
     * @throws SettlersMapLoadingException if there is an error loading the map
     * @throws IOException                 if there is an I/O error
     * @throws InvalidMapException         if the map is invalid
     */
    public MapFile loadMapFromStream(InputStream inputStream) throws SettlersMapLoadingException, IOException, InvalidMapException {
        StreamReader streamReader = new StreamReader(inputStream, ByteOrder.LITTLE_ENDIAN);
        MapFile mapFile = new MapFile();

        // Read file header
        String fileHeader = streamReader.getUint8ArrayAsString(10);
        printlnIfDebug(String.format(" -- File header: %s", fileHeader));
        mapFile.setHeader(fileHeader);

        // Read title and dimensions.
        // Next 24 bytes are either 20 byte title + 2 byte width + 2 byte height, or 24 bytes title
        ByteArray titleAndMaybeWidthAndHeight = streamReader.getUint8ArrayAsByteArray(24);
        int maybeWidth = titleAndMaybeWidthAndHeight.getUint16(20);
        int maybeHeight = titleAndMaybeWidthAndHeight.getUint16(22);
        printlnIfDebug(String.format(" -- Maybe width x height: %d x %d", maybeWidth, maybeHeight));

        // Read terrain type
        mapFile.setTerrainType(TerrainType.fromUint8(streamReader.getUint8()));
        printlnIfDebug(String.format(" -- Terrain type: %s", mapFile.getTerrainType()));

        // Read number of players
        mapFile.setMaxNumberOfPlayers(streamReader.getUint8());
        printlnIfDebug(String.format(" -- Number of players: %d", mapFile.getMaxNumberOfPlayers()));

        if (mapFile.getMaxNumberOfPlayers() < 1) {
            throw new InvalidMapException("The map must contain at least one player");
        }

        // Read author
        mapFile.setAuthor(streamReader.getUint8ArrayAsNullTerminatedString(20));
        printlnIfDebug(String.format(" -- Author: %s", mapFile.getAuthor()));

        // Read starting positions
        List<Point> tmpStartingPositions = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            int x = streamReader.getUint16();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                tmpStartingPositions.add(new Point(x, 0));
            }
        }

        for (int i = 0; i < 7; i++) {
            int y = streamReader.getUint16();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                tmpStartingPositions.get(i).y = y;
                mapFile.addStartingPosition(tmpStartingPositions.get(i));
            }
        }

        printlnIfDebug(String.format(" -- Starting positions: %s", tmpStartingPositions));

        // Determine if the map is intended for unlimited play
        if (streamReader.getUint8() == 0) {
            mapFile.enableUnlimitedPlay();
        } else {
            mapFile.disableUnlimitedPlay();
        }
        printlnIfDebug(String.format(" -- Unlimited play: %b", mapFile.isPlayUnlimited()));

        // Read player faces
        List<PlayerFace> playerFaces = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            short faceType = streamReader.getUint8();
            if (i < mapFile.getMaxNumberOfPlayers()) {
                playerFaces.add(PlayerFace.playerFaceFromShort(faceType));
            }
        }
        mapFile.setPlayerFaces(playerFaces);
        playerFaces.forEach(face -> printlnIfDebug(String.format(" -- Player: %s", face.name())));

        // Read starting points for unique water and land masses
        List<UniqueMass> masses = new ArrayList<>();
        for (int i = 0; i < 250; i++) {
            MassType type = MassType.massTypeFromInt(streamReader.getUint8());
            int x = streamReader.getUint16();
            int y = streamReader.getUint16();
            Point position = new Point(x, y);
            long totalMass = streamReader.getUint32();

            masses.add(new UniqueMass(type, position, totalMass));

            if (!position.equals(new Point(0, 0))) {
                mapFile.addMassStartingPoint(position);
            }
        }
        printlnIfDebug(" -- Loaded starting points for water and land masses");


        // Read map file identification
        byte[] fileIdBytes = streamReader.getUint8ArrayAsBytes(2);
        if (fileIdBytes[0] != 0x11 || fileIdBytes[1] != 0x27) {
            System.out.printf("Warning: Invalid file id %s (must be 0x1127). Exiting.%n", Utils.getHex(fileIdBytes));
        }

        // Skip unused bytes
        byte[] unusedBytes = streamReader.getUint8ArrayAsBytes(4);
        if (!Arrays.equals(unusedBytes, new byte[]{0, 0, 0, 0})) {
            System.out.printf("Warning: Not zeros although mandatory. Are instead %s%n", Arrays.toString(unusedBytes));
        }

        /* Extra 01 00 bytes may appear here but no files seen so far have this -- might be bug in one map */
        // TODO: implement using streamReader

        // Read actual width and height, as used by map loaders
        int newWidth = streamReader.getUint16();
        int newHeight = streamReader.getUint16();
        String title;

        if (newWidth != maybeWidth || newHeight != maybeHeight) {
            title = titleAndMaybeWidthAndHeight.getNullTerminatedString(24);
            mapFile.setTitleType(MapTitleType.LONG);
        } else {
            title = titleAndMaybeWidthAndHeight.getNullTerminatedString(20);
            mapFile.setTitleType(MapTitleType.SHORT);
        }

        mapFile.setWidth(newWidth);
        mapFile.setHeight(newHeight);
        mapFile.setTitle(title);
        printlnIfDebug(String.format(" -- Title type is: %s", mapFile.getTitleType()));
        printlnIfDebug(String.format(" -- Title is: %s", title));
        printlnIfDebug(String.format(" -- Width x height: %d x %d", newWidth, newHeight));

        // Read height block header and data
        BlockHeader heightBlockHeader = readBlockHeaderFromStream(streamReader);
        printlnIfDebug(String.format(" -- Height block header: %s", heightBlockHeader));

        if (!heightBlockHeader.isValid()) {
            System.out.printf("Height block header is invalid: %s", heightBlockHeader);
        }

        // TODO: Handle fixed 01 00 if they appear

        // Verify that the dimension remains the same
        if (mapFile.getWidth() != heightBlockHeader.getWidth() || mapFile.getHeight() != heightBlockHeader.getHeight()) {
            System.out.printf("Mismatch in dimensions. Was %s, %s but saw %s, %s",
                     mapFile.getWidth(),
                    mapFile.getHeight(),
                    heightBlockHeader.getWidth(),
                    heightBlockHeader.getHeight());

            throw new SettlersMapLoadingException(String.format("Mismatch in dimensions. Was %s, %s but saw %s, %s",
                    mapFile.getWidth(),
                    mapFile.getHeight(),
                    heightBlockHeader.getWidth(),
                    heightBlockHeader.getHeight()));
        }

        // TODO: Handle fixed 01 00 if they appear

        long subBlockSize = heightBlockHeader.getMultiplier() * heightBlockHeader.getBlockLength();
        printlnIfDebug(String.format(" -- Data size: %d", (int) subBlockSize));

        // Read heights block
        for (int i = 0; i < subBlockSize; i++) {
            MapFilePoint spot = new MapFilePoint();
            spot.setHeight(streamReader.getUint8());
            mapFile.addSpot(spot);
        }
        printlnIfDebug(" -- Loaded heights");

        // Now that the MapFilePoints are added, set the native position for each one, as used in the original game
        int x = 0;
        int y = 0;
        for (int i = 0; i < subBlockSize; i++) {
            MapFilePoint mapFilePoint = mapFile.getMapFilePoints().get(i);
            mapFilePoint.setPosition(new Point(x, y));
            x += 1;

            if (x == mapFile.getWidth()) {
                x = 0;
                y += 1;
            }
        }

        // Read textures below
        printlnIfDebug();
        printIfDebug("Texture block 1: ");

        // Read the header and verify that it matches the first header
        BlockHeader texturesBelowBlockHeader = readBlockHeaderFromStream(streamReader);

        if (!texturesBelowBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for upward triangles doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for textures below matches");
        }

        // Read the below texture for each point on the map
        for (int i = 0; i < subBlockSize; i++) {
            MapFilePoint mapFilePoint = mapFile.getSpot(i);
            short belowTextureShort = streamReader.getUint8();
            Texture texture = Texture.textureFromUint8(belowTextureShort);
            mapFilePoint.setVegetationBelow(texture);

            // Set the possible harbors
            if ((belowTextureShort & 0x40) != 0) {
                mapFilePoint.setPossibleHarbor();
            }
        }

        // Read textures for down-pointing triangles
        printIfDebug("Texture block 2: ");

        // Read down-right textures header
        BlockHeader texturesDownRightBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!texturesDownRightBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for downward triangles doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for textures down right matches");
        }

        // Read textures
        for (int i = 0; i < subBlockSize; i++) {
            Texture texture = Texture.textureFromUint8(streamReader.getUint8());
            mapFile.getSpot(i).setVegetationDownRight(texture);
        }

        // Read the fourth sub block fileHeader with roads
        printIfDebug("Road block: ");

        // Read road block -- ignore for now
        BlockHeader roadBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!roadBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for roads doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for road block matches");
        }

        // Read roads -- ignore this block for now
        streamReader.skip((int) subBlockSize);

        // Read block with object properties
        printIfDebug("Object property block: ");

        // Read the block header
        BlockHeader objectPropertiesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!objectPropertiesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for object properties doesn't match. Exiting.");
        }

        // Read object properties
        for (int i = 0; i < subBlockSize; i++) {
            mapFile.getSpot(i).setObjectProperties(streamReader.getUint8());
        }

        // Read object types
        printIfDebug("Object type block: ");

        // Read object types
        BlockHeader objectTypesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!objectTypesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for object types doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for object types matches");
        }

        // Read object types
        for (int i = 0; i < subBlockSize; i++) {
            mapFile.getSpot(i).setObjectType(streamReader.getUint8());
        }

        // Read animals
        printIfDebug("Animals block: ");

        // Read block header
        BlockHeader wildAnimalsBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!wildAnimalsBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for wild animals doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for wild animals matches");
        }

        // Read animals
        for (int i = 0; i < subBlockSize; i++) {
            Animal animal = Animal.animalFromInt(streamReader.getUint8());
            if (animal.isWildAnimal()) {
                mapFile.getSpot(i).setAnimal(animal);
            }
        }

        // Skip block with unknown data
        printIfDebug("Unknown block: ");

        // Read block header
        BlockHeader ignoredBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!ignoredBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for ignored block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for ignored block matches");
        }

        // Skip the block
        streamReader.skip((int) subBlockSize);

        // Read buildable sites
        printIfDebug("Buildable sites block: ");

        // Read block header
        BlockHeader buildableSitesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Verify that the block header matches the previous ones
        if (!buildableSitesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for buildable sites doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for buildable sites matches");
        }

        // Read the buildable sites
        for (int i = 0; i < subBlockSize; i++) {
            BuildableSite buildableSite = BuildableSite.buildableSiteFromInt(streamReader.getUint8());
            mapFile.getSpot(i).setBuildableSite(buildableSite);
        }

        // Skip tenth block with unknown data
        printIfDebug("Second unknown block: ");

        // Read block header
        BlockHeader unknownBlockHeader = readBlockHeaderFromStream(streamReader);

        // Verify that the block header matches previous block headers
        if (!unknownBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of unknown block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for unknown block matches");
        }

        // Skip the block
        streamReader.skip((int) subBlockSize);

        // Skip the next block with map editor cursor position
        printIfDebug("Map editor cursor position: ");

        // Get block header
        BlockHeader cursorPositionsBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block header doesn't match the first header
        if (!cursorPositionsBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for cursor positions doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for cursor positions matches");
        }

        // Skip the block
        streamReader.skip((int) subBlockSize);

        // Read the resources block
        printIfDebug("Resource block: ");

        // Get block header
        BlockHeader resourcesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!resourcesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of resources block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for resources block matches");
        }

        // Read the resources block
        for (int i = 0; i < subBlockSize; i++) {
            Resource resource = Resource.resourceFromInt(streamReader.getUint8());
            mapFile.getSpot(i).setResource(resource);
        }

        // Skip gouraud shading block
        BlockHeader gouraudBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!gouraudBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of gouraud block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for gouraud block matches");
        }

        // Skip gouraud block
        streamReader.skip((int) subBlockSize);

        // Skip passable areas block
        BlockHeader passableAreasBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!passableAreasBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of passable areas block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for passable areas block matches");
        }

        // Skip passable areas block
        streamReader.skip((int) subBlockSize);

        // Footer, always 0xFF

        // Post process the map file
        mapFile.mapFilePointsToGamePoints();
        mapFile.translateFileStartingPointsToGamePoints();

        return mapFile;
    }

    private BlockHeader readBlockHeaderFromStream(StreamReader streamReader) throws IOException {
        return new BlockHeader(
                streamReader.getUint16(), // id
                streamReader.getUint32(), // unknown
                streamReader.getUint16(), // width
                streamReader.getUint16(), // height
                streamReader.getUint16(), // multiplier
                streamReader.getUint32()  // block length
        );
    }

    /**
     * Converts a MapFile to a GameMap.
     *
     * @param mapFile the MapFile to convert.
     * @return the converted GameMap instance.
     * @throws Exception if there is an error during conversion.
     */
    public GameMap convertMapFileToGameMap(MapFile mapFile) throws Exception {
        List<Player> players = new ArrayList<>();
        List<PlayerColor> colors = Arrays.stream(PlayerColor.values()).toList();

        for (int i = 0; i < mapFile.maxNumberOfPlayers; i++) {
            players.add(new Player(String.format("Player %d", i), colors.get(i), Nation.ROMANS, PlayerType.HUMAN));
        }

        Dimension gamePointDimension = toGamePointDimension(mapFile.getDimension());
        GameMap gameMap = new GameMap(players, gamePointDimension.width + 2, gamePointDimension.height + 2);

        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {
            Point mapFilePosition = mapFilePoint.getPosition();
            org.appland.settlers.model.Point point = mapFilePositionToGamePoint(mapFilePosition, mapFile.getDimension());

            if (mapFilePoint.getVegetationBelow() != null) {
                gameMap.setVegetationBelow(point, Utils.convertTextureToVegetation(mapFilePoint.getVegetationBelow()));
            }

            if (mapFilePoint.getVegetationDownRight() != null) {
                gameMap.setVegetationDownRight(point, Utils.convertTextureToVegetation(mapFilePoint.getVegetationDownRight()));
            }

            if (mapFilePoint.hasMineral()) {
                Material mineral = Utils.resourceTypeToMaterial(mapFilePoint.getMineralType());
                gameMap.setMineralAmount(point, mineral, mapFilePoint.getMineralQuantity());
            }

            if (mapFilePoint.hasStone()) {
                gameMap.placeStone(point, mapFilePoint.getStoneType(), Math.min(mapFilePoint.getStoneAmount(), 6));
            }

            if (mapFilePoint.hasTree()) {
                gameMap.placeTree(point, mapFilePoint.getTreeType(), mapFilePoint.getTreeSize());
            }

            if (mapFilePoint.hasDeadTree()) {
                gameMap.placeDeadTree(point);
            }

            if (mapFilePoint.hasDecoration()) {
                DecorationType decorationType = mapFilePoint.getNatureDecorationType();

                if (DecorationType.NO_IMPACT_ON_GAME.contains(decorationType)) {
                    gameMap.placeDecoration(point, decorationType);
                }
            }

            if (mapFilePoint.hasWildAnimal()) {
                gameMap.placeWildAnimal(point);
            }

            if (mapFilePoint.isPossiblePlaceForHarbor()) {
                gameMap.setPossiblePlaceForHarbor(point);
            }

            gameMap.setHeightAtPoint(point, mapFilePoint.getHeight());
        }

        List<org.appland.settlers.model.Point> gamePointStartingPoints = mapFile.getStartingPoints().stream()
                .map(point -> mapFilePositionToGamePoint(point, mapFile.getDimension()))
                .collect(Collectors.toList());

        gameMap.setStartingPoints(gamePointStartingPoints);

        if (debug) {
            System.out.println(" -- Starting positions: ");
            mapFile.getGamePointStartingPoints().forEach(point ->
                    printIfDebug(String.format("(%d, %d) ", point.x, point.y))
            );
        }

        return gameMap;
    }

    private Dimension toGamePointDimension(Dimension dimension) {
        return isEven(dimension.height) ?
                new Dimension(2 * dimension.width - 2, dimension.height - 1) :
                new Dimension(2 * dimension.width - 2, dimension.height);
    }

    public org.appland.settlers.model.Point mapFilePositionToGamePoint(Point mapFilePosition, Dimension dimension) {
        int xMultiplier = 2 * mapFilePosition.x;
        int yAdjusted = dimension.height - mapFilePosition.y;

        return isEven(dimension.height) ?
                new org.appland.settlers.model.Point(isEven(mapFilePosition.y) ? xMultiplier - 1 : xMultiplier, yAdjusted - 1) :
                new org.appland.settlers.model.Point(isEven(mapFilePosition.y) ? xMultiplier - 1 : xMultiplier, yAdjusted);
    }
}
