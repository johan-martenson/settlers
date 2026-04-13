package org.appland.settlers.maps;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.maps.utils.GeometryMapping;
import org.appland.settlers.maps.utils.MapFileUtils;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.utils.StreamReader;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.appland.settlers.model.DecorationType.*;

/**
 * Loads a map binary file into a MapFile instance
 *
 */
public class MapLoader {
    private static final int NUMBER_OF_STARTING_POSITIONS = 7;
    private static final short UNLIMITED_PLAY_ENABLED = 0;
    private static final int NUMBER_OF_PLAYER_FACES = 7;
    private static final int NUMBER_OF_UNIQUE_MASSES = 250;
    private static final int LONG_TITLE = 24;
    private static final int SHORT_TITLE = 20;

    @Option(name = "--file", usage = "Map file to load")
    String filename;

    @Option(name = "--debug", usage = "Print debug information")
    boolean debugFlag = false;

    @Option(name = "--info", usage = "Print information about the map")
    boolean printInfo = false;


    public static final Set<DecorationType> SUPPORTED_DECORATIONS = EnumSet.of(
            MINI_BROWN_MUSHROOM,
            TOADSTOOL,
            MINI_STONE,
            SMALL_STONE,
            STONE,
            ANIMAL_SKELETON_1,
            ANIMAL_SKELETON_2,
            FLOWERS,
            LARGE_BUSH_1,
            PILE_OF_STONES,
            CATTAIL_1,
            CATTAIL_2,
            LARGE_BUSH_2,
            BUSH_3,
            SMALL_BUSH,
            CATTAIL_3,
            CATTAIL_4,
            BROWN_MUSHROOM,
            MINI_STONE_WITH_GRASS,
            SMALL_STONE_WITH_GRASS,
            SOME_SMALL_STONES_1,
            SOME_SMALL_STONES_2,
            SOME_SMALL_STONES_3,
            SPARSE_BUSH,
            SOME_WATER,
            LITTLE_GRASS,
            CACTUS_1,
            CACTUS_2,
            SNOWMAN,
            PORTAL,
            SHINING_PORTAL
    );

    public static void main(String[] args) {
        var mapLoader = new MapLoader();
        var parser = new CmdLineParser(mapLoader);

        try {
            parser.parseArgument(args);

            var mapFile = mapLoader.loadMapFromFile(mapLoader.filename);
            var gameMap = mapLoader.convertMapFileToGameMap(mapFile);

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
        System.out.println("   -- As map files points: ");
        mapFile.getStartingPoints().forEach(point -> System.out.printf("    - %d, %d%n", point.x, point.y));
        System.out.println("   -- As game points: ");
        mapFile.getStartingPoints().forEach(point -> System.out.printf("    - %s%n", GeometryMapping.mapFilePointToGamePoint(point, mapFile.getHeight())));

        System.out.println(" - Player types:");
        mapFile.getPlayerFaces().forEach(face ->
                System.out.printf("    - %s%n", face.name().toLowerCase())
        );

        System.out.printf(" - Width x height: %d x %d%n", mapFile.getWidth(), mapFile.getHeight());
        System.out.printf(" - Terrain type: %s%n", mapFile.getTerrainType().name().toLowerCase());
        System.out.printf(" - Unlimited play: %b%n", mapFile.isUnlimitedPlayEnabled());
        System.out.printf(" - Title type: %s%n", mapFile.getTitleType());
        System.out.printf(" - File header: %s%n", mapFile.getHeaderType().name().toLowerCase());
    }


    void debug(Object object) {
        if (debugFlag) {
            System.out.println(object);
        }
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
        debug(format("Loading: %s", mapFilename));

        try (var fileInputStream = Files.newInputStream(Paths.get(mapFilename))) {
            return loadMapFromStream(fileInputStream);
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
        var streamReader = new StreamReader(inputStream, ByteOrder.LITTLE_ENDIAN);
        var mapFile = new MapFile();

        // Read file header
        var fileHeader = streamReader.getUint8ArrayAsString(10);
        debug(format(" -- File header: %s", fileHeader));
        mapFile.setHeader(fileHeader);

        // Read title and dimensions.
        // Next 24 bytes are either 20 byte title + 2 byte width + 2 byte height, or 24 bytes title
        var titleAndMaybeWidthAndHeight = streamReader.getUint8ArrayAsByteArray(24);
        int maybeWidth = titleAndMaybeWidthAndHeight.getUint16(20);
        int maybeHeight = titleAndMaybeWidthAndHeight.getUint16(22);
        debug(format(" -- Maybe width x height: %d x %d", maybeWidth, maybeHeight));

        // Read terrain type
        mapFile.setTerrainType(TerrainType.fromUint8(streamReader.getUint8()));
        debug(format(" -- Terrain type: %s", mapFile.getTerrainType()));

        // Read number of players
        mapFile.setMaxNumberOfPlayers(streamReader.getUint8());
        debug(format(" -- Number of players: %d", mapFile.getMaxNumberOfPlayers()));

        if (mapFile.getMaxNumberOfPlayers() < 1) {
            throw new InvalidMapException("The map must contain at least one player");
        }

        // Read author
        mapFile.setAuthor(streamReader.getUint8ArrayAsNullTerminatedString(20));
        debug(format(" -- Author: %s", mapFile.getAuthor()));

        // Read starting positions
        var startingPositions = new ArrayList<Point>();

        for (int i = 0; i < NUMBER_OF_STARTING_POSITIONS; i++) {
            int x = streamReader.getUint16();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                startingPositions.add(new Point(x, 0));
            }
        }

        for (int i = 0; i < NUMBER_OF_STARTING_POSITIONS; i++) {
            int y = streamReader.getUint16();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                startingPositions.get(i).y = y;
                mapFile.addStartingPoint(startingPositions.get(i));
            }
        }

        debug(format(" -- Starting positions: %s", startingPositions));

        // Determine if the map is intended for unlimited play
        if (streamReader.getUint8() == UNLIMITED_PLAY_ENABLED) {
            mapFile.enableUnlimitedPlay();
        } else {
            mapFile.disableUnlimitedPlay();
        }
        debug(format(" -- Unlimited play: %b", mapFile.isUnlimitedPlayEnabled()));

        // Read player faces
        var playerFaces = new ArrayList<PlayerFace>();
        for (int i = 0; i < NUMBER_OF_PLAYER_FACES; i++) {
            short faceType = streamReader.getUint8();
            if (i < mapFile.getMaxNumberOfPlayers()) {
                playerFaces.add(PlayerFace.playerFaceFromShort(faceType));
            }
        }
        mapFile.setPlayerFaces(playerFaces);
        playerFaces.forEach(face -> debug(format(" -- Player: %s", face.name())));

        // Read starting points for unique water and land masses
        var masses = new ArrayList<UniqueMass>();
        for (int i = 0; i < NUMBER_OF_UNIQUE_MASSES; i++) {
            var type = MassType.massTypeFromInt(streamReader.getUint8());
            int x = streamReader.getUint16();
            int y = streamReader.getUint16();
            var position = new Point(x, y);
            long totalMass = streamReader.getUint32();

            masses.add(new UniqueMass(type, position, totalMass));

            if (!position.equals(new Point(0, 0))) {
                mapFile.addMassStartingPoint(position);
            }
        }
        debug(" -- Loaded starting points for water and land masses");


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

        // Extra 01 00 bytes may appear here but no files seen so far have this -- might be bug in one map
        // TODO: implement using streamReader

        // Read actual width and height, as used by map loaders
        int newWidth = streamReader.getUint16();
        int newHeight = streamReader.getUint16();
        String title;

        if (newWidth != maybeWidth || newHeight != maybeHeight) {
            title = titleAndMaybeWidthAndHeight.getNullTerminatedString(LONG_TITLE);
            mapFile.setTitleType(MapTitleType.LONG);
        } else {
            title = titleAndMaybeWidthAndHeight.getNullTerminatedString(SHORT_TITLE);
            mapFile.setTitleType(MapTitleType.SHORT);
        }

        mapFile.setWidth(newWidth);
        mapFile.setHeight(newHeight);
        mapFile.setTitle(title);
        debug(format(" -- Title type is: %s", mapFile.getTitleType()));
        debug(format(" -- Title is: %s", title));
        debug(format(" -- Width x height: %d x %d", newWidth, newHeight));

        // Read height block header and data
        var heightBlockHeader = readBlockHeaderFromStream(streamReader);
        debug(format(" -- Height block header: %s", heightBlockHeader));

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

            throw new SettlersMapLoadingException(format("Mismatch in dimensions. Was %s, %s but saw %s, %s",
                    mapFile.getWidth(),
                    mapFile.getHeight(),
                    heightBlockHeader.getWidth(),
                    heightBlockHeader.getHeight()));
        }

        // TODO: Handle fixed 01 00 if they appear

        long subBlockSize = heightBlockHeader.getMultiplier() * heightBlockHeader.getBlockLength();
        debug(format(" -- Data size: %d", (int) subBlockSize));

        // Read heights block
        for (int i = 0; i < subBlockSize; i++) {
            var heightAtPoint = streamReader.getUint8();

            var spot = new MapFilePoint();
            spot.setHeight(heightAtPoint);
            mapFile.addHeight(heightAtPoint);
            mapFile.addMapFilePoint(spot);
        }
        debug(" -- Loaded heights");

        // Now that the MapFilePoints are added, set the native position for each one, as used in the original game
        int x = 0;
        int y = 0;
        for (int i = 0; i < subBlockSize; i++) {
            var mapFilePoint = mapFile.getMapFilePoints().get(i);
            mapFilePoint.setPosition(new Point(x, y));

            x += 1;

            if (x == mapFile.getWidth()) {
                x = 0;
                y += 1;
            }
        }

        // Read textures below
        debug("Texture block 1: ");

        // Read the header and verify that it matches the first header
        var texturesBelowBlockHeader = readBlockHeaderFromStream(streamReader);

        if (!texturesBelowBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for upward triangles doesn't match. Exiting.");
        } else {
            debug("Header for textures below matches");
        }

        // Read the below texture for each point on the map
        for (int i = 0; i < subBlockSize; i++) {
            var mapFilePoint = mapFile.getMapFilePoint(i);
            short vegetationBelowUint8 = streamReader.getUint8();
            var vegetationBelow = MapFileUtils.vegetationFromUint8(vegetationBelowUint8);
            mapFilePoint.setVegetationBelow(vegetationBelow);
            mapFile.addTileBelow(vegetationBelow);

            // Set the possible harbors
            if ((vegetationBelowUint8 & 0x40) != 0) {
                mapFilePoint.setPossibleHarbor();
            }
        }

        // Read textures for down-pointing triangles
        debug("Texture block 2: ");

        // Read down-right textures header
        var texturesDownRightBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!texturesDownRightBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for downward triangles doesn't match. Exiting.");
        } else {
            debug("Header for textures down right matches");
        }

        // Read textures
        for (int i = 0; i < subBlockSize; i++) {
            var vegetation = MapFileUtils.vegetationFromUint8(streamReader.getUint8());
            mapFile.getMapFilePoint(i).setVegetationDownRight(vegetation);
            mapFile.addTileDownRight(vegetation);
        }

        // Read the fourth sub block fileHeader with roads
        debug("Road block: ");

        // Read road block -- ignore for now
        var roadBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!roadBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for roads doesn't match. Exiting.");
        } else {
            debug("Header for road block matches");
        }

        // Read roads -- ignore this block for now
        streamReader.skip((int) subBlockSize);

        // Read block with object properties
        debug("Object property block: ");

        // Read the block header
        var objectPropertiesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!objectPropertiesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for object properties doesn't match. Exiting.");
        }

        // Read object properties
        for (int i = 0; i < subBlockSize; i++) {
            mapFile.getMapFilePoint(i).setObjectProperties(streamReader.getUint8());
        }

        // Read object types
        debug("Object type block: ");

        // Read object types
        var objectTypesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!objectTypesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for object types doesn't match. Exiting.");
        } else {
            debug("Header for object types matches");
        }

        // Read object types
        for (int i = 0; i < subBlockSize; i++) {
            mapFile.getMapFilePoint(i).setObjectType(streamReader.getUint8());
        }

        // Read animals
        debug("Animals block: ");

        // Read block header
        var wildAnimalsBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!wildAnimalsBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for wild animals doesn't match. Exiting.");
        } else {
            debug("Header for wild animals matches");
        }

        // Read animals
        for (int i = 0; i < subBlockSize; i++) {
            var animal = Animal.animalFromInt(streamReader.getUint8());
            if (animal.isWildAnimal()) {
                mapFile.getMapFilePoint(i).setAnimal(animal);
            }
        }

        // Skip block with unknown data
        debug("Unknown block: ");

        // Read block header
        var ignoredBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!ignoredBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for ignored block doesn't match. Exiting.");
        } else {
            debug("Header for ignored block matches");
        }

        // Skip the block
        streamReader.skip((int) subBlockSize);

        // Read buildable sites
        debug("Buildable sites block: ");

        // Read block header
        var buildableSitesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Verify that the block header matches the previous ones
        if (!buildableSitesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for buildable sites doesn't match. Exiting.");
        } else {
            debug("Header for buildable sites matches");
        }

        // Read the buildable sites
        for (int i = 0; i < subBlockSize; i++) {
            var buildableSite = BuildableSite.buildableSiteFromInt(streamReader.getUint8());
            mapFile.getMapFilePoint(i).setBuildableSite(buildableSite);
        }

        // Skip tenth block with unknown data
        debug("Second unknown block: ");

        // Read block header
        var unknownBlockHeader = readBlockHeaderFromStream(streamReader);

        // Verify that the block header matches previous block headers
        if (!unknownBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of unknown block doesn't match. Exiting.");
        } else {
            debug("Header for unknown block matches");
        }

        // Skip the block
        streamReader.skip((int) subBlockSize);

        // Skip the next block with map editor cursor position
        debug("Map editor cursor position: ");

        // Get block header
        var cursorPositionsBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block header doesn't match the first header
        if (!cursorPositionsBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of block for cursor positions doesn't match. Exiting.");
        } else {
            debug("Header for cursor positions matches");
        }

        // Skip the block
        streamReader.skip((int) subBlockSize);

        // Read the resources block
        debug("Resource block: ");

        // Get block header
        var resourcesBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!resourcesBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of resources block doesn't match. Exiting.");
        } else {
            debug("Header for resources block matches");
        }

        // Read the resources block
        for (int i = 0; i < subBlockSize; i++) {
            var resource = Resource.resourceFromInt(streamReader.getUint8());
            mapFile.getMapFilePoint(i).setResource(resource);
        }

        // Skip gouraud shading block
        var gouraudBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!gouraudBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of gouraud block doesn't match. Exiting.");
        } else {
            debug("Header for gouraud block matches");
        }

        // Skip gouraud block
        streamReader.skip((int) subBlockSize);

        // Skip passable areas block
        var passableAreasBlockHeader = readBlockHeaderFromStream(streamReader);

        // Exit if the block fileHeader doesn't match the first fileHeader
        if (!passableAreasBlockHeader.equals(heightBlockHeader)) {
            throw new SettlersMapLoadingException("Header of passable areas block doesn't match. Exiting.");
        } else {
            debug("Header for passable areas block matches");
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
        var players = new ArrayList<Player>();
        var colors = Arrays.stream(PlayerColor.values()).toList();

        for (int i = 0; i < mapFile.getMaxNumberOfPlayers(); i++) {
            players.add(new Player(format("Player %d", i), colors.get(i), Nation.ROMANS, PlayerType.HUMAN));
        }

        var gameDimension = GeometryMapping.mapFileDimensionsToGameDimensions(mapFile.getDimension());
        var gameMap = new GameMap(players, gameDimension.width, gameDimension.height);

        var unsupportedDecorations = new HashSet<DecorationType>();
        for (var mapFilePoint : mapFile.getMapFilePoints()) {
            var point = GeometryMapping.mapFilePointToGamePoint(mapFilePoint.getPosition(), mapFile.getDimension().height);

            if (mapFilePoint.getVegetationBelow() != null) {
                gameMap.setVegetationBelow(point, mapFilePoint.getVegetationBelow());
            }

            if (mapFilePoint.getVegetationDownRight() != null) {
                gameMap.setVegetationDownRight(point, mapFilePoint.getVegetationDownRight());
            }

            if (mapFilePoint.hasMineral()) {
                var mineral = Utils.resourceTypeToMaterial(mapFilePoint.getMineralType());
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
                var decorationType = mapFilePoint.getNatureDecorationType();

                if (SUPPORTED_DECORATIONS.contains(decorationType)) {
                    gameMap.placeDecoration(point, decorationType);
                } else {
                    unsupportedDecorations.add(decorationType);
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

        System.out.println("UNSUPPORTED DECORATIONS: " + unsupportedDecorations);

        var gamePointStartingPoints = mapFile.getStartingPoints().stream()
                .map(mapFilePoint -> GeometryMapping.mapFilePointToGamePoint(mapFilePoint, mapFile.getHeight()))
                .collect(Collectors.toList());

        System.out.println("Map file height: " + mapFile.getHeight());
        System.out.println("Map file starting points: " + mapFile.getStartingPoints());
        System.out.println("Game map starting points: " + gamePointStartingPoints);

        gameMap.setStartingPoints(gamePointStartingPoints);

        if (debugFlag) {
            System.out.println(" -- Starting positions: ");
            System.out.println("     - As map file points: " + mapFile.getStartingPoints());
            System.out.println("     - As game points: " + gamePointStartingPoints);
        }

        return gameMap;
    }
}
