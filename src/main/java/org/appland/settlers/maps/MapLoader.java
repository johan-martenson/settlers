package org.appland.settlers.maps;

import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.utils.StreamReader;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

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

    private boolean doCropping = true;

    public static void main(String[] args) {

        /* Parse command line and start */
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

    private void printMapInformation(MapFile mapFile) {
        System.out.println(format(" - Title: %s", mapFile.getTitle()));
        System.out.println(format(" - Author: %s", mapFile.getAuthor()));
        System.out.println(format(" - Max number of players: %d", mapFile.getMaxNumberOfPlayers()));
        System.out.println(" - Starting points:");

        mapFile.getStartingPoints().forEach(point -> System.out.println(format("    - %d, %d", point.x, point.y)));

        System.out.println(" - Player types:");

        mapFile.getPlayerFaces().forEach(face -> System.out.println(format("    - %s", face.name().toLowerCase())));

        System.out.println(format(" - Width x height: %d x %d", mapFile.getWidth(), mapFile.getHeight()));
        System.out.println(format(" - Terrain type: %s", mapFile.getTerrainType().name().toLowerCase()));
        System.out.println(format(" - Unlimited play: %b", mapFile.isUnlimitedPlay()));
        System.out.println(format(" - Title type: %s", mapFile.getTitleType()));
        System.out.println(format(" - File header: %s", mapFile.getHeaderType().name().toLowerCase()));
    }

    public MapFile loadMapFromFile(String mapFilename) throws SettlersMapLoadingException, IOException, InvalidMapException {
        printlnIfDebug();
        printlnIfDebug("Loading: " + mapFilename);

        InputStream fileInputStream = Files.newInputStream(Paths.get(mapFilename));

        MapFile mapFile = loadMapFromStream(fileInputStream);

        fileInputStream.close();

        return mapFile;
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

    public MapFile loadMapFromStream(InputStream inputStream) throws SettlersMapLoadingException, IOException, InvalidMapException {

        StreamReader streamReader = new StreamReader(inputStream, ByteOrder.LITTLE_ENDIAN);

        MapFile mapFile = new MapFile();

        /* Read file header */
        String fileHeader = streamReader.getUint8ArrayAsString(10);

        printlnIfDebug(" -- File header: " + fileHeader);

        mapFile.setHeader(fileHeader);

        /* Read title and potentially width & height.
        *   - Next 24 bytes are either 20 byte title + 2 byte width + 2 byte height, or
        *     24 bytes title
        *  */

        ByteArray titleAndMaybeWidthAndHeight = streamReader.getUint8ArrayAsByteArray(24);

        int maybeWidth = titleAndMaybeWidthAndHeight.getUint16(20);
        int maybeHeight = titleAndMaybeWidthAndHeight.getUint16(22);

        printlnIfDebug(" -- Maybe width x height: " + maybeWidth + " x " + maybeHeight);

        /* Read the terrain type */
        mapFile.setTerrainType(TerrainType.fromUint8(streamReader.getUint8()));

        printlnIfDebug(" -- Terrain type: " + mapFile.getTerrainType());

        /* Read number of players */
        mapFile.setMaxNumberOfPlayers(streamReader.getUint8());

        printlnIfDebug(" -- Number of players: " + mapFile.getMaxNumberOfPlayers());

        if (mapFile.getMaxNumberOfPlayers() < 1) {
            throw new InvalidMapException("The map must contain at least one player");
        }

        /* Read the author */
        mapFile.setAuthor(streamReader.getUint8ArrayAsNullTerminatedString(20));

        printlnIfDebug(" -- Author: " + mapFile.getAuthor());

        /* Go through x coordinates for starting positions */
        List<Point> tmpStartingPositions = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            int x = streamReader.getUint16();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                tmpStartingPositions.add(new Point(x, 0));
            }
        }

        /* Go through y coordinates for starting positions */
        for (int i = 0; i < 7; i++) {
            int y = streamReader.getUint16();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                tmpStartingPositions.get(i).y = y;

                mapFile.addStartingPosition(tmpStartingPositions.get(i));
            }
        }

        printlnIfDebug(" -- Starting positions: " + tmpStartingPositions);

        /* Determine if the map is intended for unlimited play */
        if (streamReader.getUint8() == 0) {
            mapFile.enableUnlimitedPlay();
        } else {
            mapFile.disableUnlimitedPlay();
        }

        printlnIfDebug(" -- Unlimited play: " + mapFile.isPlayUnlimited());

        /* Read player faces */
        List<PlayerFace> playerFaces = new ArrayList<>();
        for (int i = 0; i < 7; i++) {

            short faceType = streamReader.getUint8();

            if (i < mapFile.getMaxNumberOfPlayers()) {
                PlayerFace playerFace = PlayerFace.playerFaceFromShort(faceType);

                playerFaces.add(playerFace);
            }
        }

        mapFile.setPlayerFaces(playerFaces);

        for (PlayerFace face : mapFile.getPlayerFaces()) {
            printlnIfDebug(" -- Player: " + face.name());
        }

        /* Read starting points for each unique water and land mass */
        List<UniqueMass> masses = new ArrayList<>();

        for (int i = 0; i < 250; i++) {
            MassType type = MassType.massTypeFromInt(streamReader.getUint8());

            int x = streamReader.getUint16();
            int y = streamReader.getUint16();

            Point position = new Point(x, y);

            long totalMass = streamReader.getUint32();
            UniqueMass mass = new UniqueMass(type, position, totalMass);

            masses.add(mass);

            if (!position.equals(new Point(0, 0))) {
                mapFile.addMassStartingPoint(position);
            }
        }

        printlnIfDebug(" -- Loaded starting points for water and land masses");

        /* Read map file identification */
        byte[] fileIdBytes = streamReader.getUint8ArrayAsBytes(2);

        /* Verify file id */
        if (fileIdBytes[0] != 0x11 || fileIdBytes[1] != 0x27) {
            System.out.println("Warning: Invalid file id " + Utils.getHex(fileIdBytes) + " (must be 0x1127). Exiting.");

            //throw new SettlersMapLoadingException("Invalid file id " + Utils.getHex(fileIdBytes) + " (must be 0x1127). Exiting.");
        }

        /* Skip four un-used bytes */
        byte[] unusedBytes = streamReader.getUint8ArrayAsBytes(4);

        if (unusedBytes[0] != 0 || unusedBytes[1] != 0 ||
            unusedBytes[2] != 0 || unusedBytes[3] != 0) {
            System.out.println("Warning: Not zeros although mandatory. Are instead " +
                                unusedBytes[0] + " " +
                    unusedBytes[0] + " " +
                    unusedBytes[0] + " " +
                    unusedBytes[0] + " ");
        }

        /* Extra 01 00 bytes may appear here but no files seen so far have this -- might be bug in one map */
        // TODO: implement using streamReader

        /* Read actual width and height, as used by map loaders */
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

        printlnIfDebug(" -- Title type is: " + mapFile.getTitleType());
        printlnIfDebug(" -- Title is: " + title);
        printlnIfDebug(" -- Width x height: " + newWidth + " x " + newHeight);

        /* Read first sub block fileHeader with data about heights */
        BlockHeader heightBlockHeader = readBlockHeaderFromStream(streamReader);

        printlnIfDebug(" -- Height block header: " + heightBlockHeader);

        /* Verify that the coming six bytes are: 0x 10 27 00 00 00 00 */
        if (!heightBlockHeader.isValid()) {
            System.out.println("Height block header is invalid: " + heightBlockHeader);
        }

        /* Handle fixed 01 00 if they appear */
        // TODO: implement this using streamReader

        /* Verify that the dimensions remain */
        if (mapFile.getWidth() != heightBlockHeader.getWidth() || mapFile.getHeight() != heightBlockHeader.getHeight()) {
            System.out.println("Mismatch in dimensions. Was "
                    + mapFile.getWidth() + " x " + mapFile.getHeight() + " but saw "
                    + heightBlockHeader.getWidth() + " x " + heightBlockHeader.getHeight());

            throw new SettlersMapLoadingException("Mismatch in dimensions. Was "
                    + mapFile.getWidth() + " x " + mapFile.getHeight() + " but saw "
                    + heightBlockHeader.getWidth() + " x " + heightBlockHeader.getHeight());
        }

        /* Handle fixed 01 00 if they appear */
        // TODO: implement using streamReader

        long subBlockSize = heightBlockHeader.getMultiplier() * heightBlockHeader.getBlockLength();

        printlnIfDebug(" -- Data size: " + (int)subBlockSize);

        /* Read heights block */
        for (int i = 0; i < subBlockSize; i++) {
            MapFilePoint spot = new MapFilePoint();

            spot.setHeight(streamReader.getUint8());

            mapFile.addSpot(spot);
        }

        printlnIfDebug(" -- Loaded heights");

        /* Read textures below */
        printlnIfDebug();
        printIfDebug("Texture block 1: ");

        /* Read the header and verify that it matches the first header */
        BlockHeader texturesBelowBlockHeader = readBlockHeaderFromStream(streamReader);

        if (!texturesBelowBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for upward triangles doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + texturesBelowBlockHeader);

            throw new SettlersMapLoadingException("Header of block for upward triangles doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for textures below matches");
        }

        /* Read the below texture for each point on the map */
        for (int i = 0; i < subBlockSize; i++) {

            MapFilePoint mapFilePoint = mapFile.getSpot(i);

            /* Set the textures */
            short belowTextureShort = streamReader.getUint8();
            Texture texture = Texture.textureFromUint8(belowTextureShort);

            mapFilePoint.setVegetationBelow(texture);

            /* Set the possible harbors */
            if ((belowTextureShort & 0x40) != 0) {
                mapFilePoint.setPossibleHarbor();
            }
        }

        /* Read textures for down-pointing triangles */
        printIfDebug("Texture block 2: ");

        /* Read down-right textures header */
        BlockHeader texturesDownRightBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!texturesDownRightBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for downward triangles doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + texturesDownRightBlockHeader);

            throw new SettlersMapLoadingException("Header of block for downward triangles doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for textures down right matches");
        }

        /* Read textures */
        for (int i = 0; i < subBlockSize; i++) {
            Texture texture = Texture.textureFromUint8(streamReader.getUint8());

            mapFile.getSpot(i).setVegetationDownRight(texture);
        }

        /* Read the fourth sub block fileHeader with roads */
        printIfDebug("Road block: ");

        /* Read road block -- ignore for now */
        BlockHeader roadBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!roadBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of road block doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + roadBlockHeader);

            throw new SettlersMapLoadingException("Header of block for roads doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for road block matches");
        }

        /* Read roads -- ignore this block for now*/
        streamReader.skip((int) subBlockSize);

        /* Read block with object properties */
        printIfDebug("Object property block: ");

        /* Read the block header */
        BlockHeader objectPropertiesBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!objectPropertiesBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for object properties doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + objectPropertiesBlockHeader);

            throw new SettlersMapLoadingException("Header of block for object properties doesn't match. Exiting.");
        }

        /* Read object properties */
        for (int i = 0; i < subBlockSize; i++) {
            mapFile.getSpot(i).setObjectProperties(streamReader.getUint8());
        }

        /* Read object types */
        printIfDebug("Object type block: ");

        /* Read object types */
        BlockHeader objectTypesBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!objectTypesBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for object types doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + objectTypesBlockHeader);

            throw new SettlersMapLoadingException("Header of block for object types doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for object types matches");
        }

        /* Read object types*/
        for (int i = 0; i < subBlockSize; i++) {
            mapFile.getSpot(i).setObjectType(streamReader.getUint8());
        }

        /* Read animals */
        printIfDebug("Animals block: ");

        /* Read block header */
        BlockHeader wildAnimalsBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!wildAnimalsBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for wild animals doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + wildAnimalsBlockHeader);

            throw new SettlersMapLoadingException("Header of block for wild animals doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for wild animals matches");
        }

        /* Read animals */
        for (int i = 0; i < subBlockSize; i++) {
            Animal animal = Animal.animalFromInt(streamReader.getUint8());

            if (animal.isWildAnimal()) {
                mapFile.getSpot(i).setAnimal(animal);
            }
        }

        /* Skip block with unknown data */
        printIfDebug("Unknown block: ");

        /* Read block header */
        BlockHeader ignoredBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!ignoredBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for ignored block doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + ignoredBlockHeader);

            throw new SettlersMapLoadingException("Header of ignored block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for ignored block matches");
        }

        /* Skip the block */
        streamReader.skip((int)subBlockSize);

        /* Read buildable sites */
        printIfDebug("Buildable sites block: ");

        /* Read block header */
        BlockHeader buildableSitesBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Verify that the block header matches the previous ones */
        if (!buildableSitesBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for buildable sites doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + buildableSitesBlockHeader);

            throw new SettlersMapLoadingException("Header of buildable sites doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for buildable sites matches");
        }

        /* Read the buildable sites */
        for (int i = 0; i < subBlockSize; i++) {
            BuildableSite buildableSite = BuildableSite.buildableSiteFromInt(streamReader.getUint8());

            mapFile.getSpot(i).setBuildableSite(buildableSite);
        }

        /* Skip tenth block with unknown data */
        printIfDebug("Second unknown block: ");

        /* Read block header */
        BlockHeader unknownBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Verify that the block header matches previous block headers */
        if (!unknownBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of unknown block doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + unknownBlockHeader);

            throw new SettlersMapLoadingException("Header of unknown block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for unknown block matches");
        }

        /* Skip the block */
        streamReader.skip((int)subBlockSize);

        /* Skip the next block with map editor cursor position */
        printIfDebug("Map editor cursor position: ");

        /* Get block header */
        BlockHeader cursorPositionsBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block header doesn't match the first header */
        if (!cursorPositionsBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for cursor positions doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + cursorPositionsBlockHeader);

            throw new SettlersMapLoadingException("Header of cursor positions doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for cursor positions matches");
        }

        /* Skip the block */
        streamReader.skip((int)subBlockSize);

        /* Read the resources block */
        printIfDebug("Resource block: ");

        /* Get block header */
        BlockHeader resourcesBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!resourcesBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of block for resources block doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + resourcesBlockHeader);

            throw new SettlersMapLoadingException("Header of resources block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for resources block matches");
        }

        /* Read the resources block */
        for (int i = 0; i < subBlockSize; i++) {
            Resource resource = Resource.resourceFromInt(streamReader.getUint8());

            mapFile.getSpot(i).setResource(resource);
        }

        /* Ignore gouraud shading block */

        /* Get the block header */
        BlockHeader gouraudBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!gouraudBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of gouraud block doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + gouraudBlockHeader);

            throw new SettlersMapLoadingException("Header of gouraud block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for gouraud block matches");
        }

        /* Skip gouraud block */
        streamReader.skip((int)subBlockSize);

        /* Ignore passable areas block */

        /* Get the block header */
        BlockHeader passableAreasBlockHeader = readBlockHeaderFromStream(streamReader);

        /* Exit if the block fileHeader doesn't match the first fileHeader */
        if (!gouraudBlockHeader.equals(heightBlockHeader)) {
            System.out.println("Header of passable areas block doesn't match. Exiting.");
            System.out.println("First header: " + heightBlockHeader);
            System.out.println("Current header: " + passableAreasBlockHeader);

            throw new SettlersMapLoadingException("Header of passable areas block doesn't match. Exiting.");
        } else {
            printlnIfDebug("Header for passable areas block matches");
        }

        /* Skip passable areas block */
        streamReader.skip((int)subBlockSize);

        /* Footer, always 0xFF */

        /* Post process the map file */
        mapFile.mapFilePointsToGamePoints();
        mapFile.translateFileStartingPointsToGamePoints();

        return mapFile;
    }

    private BlockHeader readBlockHeaderFromStream(StreamReader streamReader) throws IOException {
        int id = streamReader.getUint16();
        long unknown = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int multiplier = streamReader.getUint16();
        long blockLength = streamReader.getUint32();

        return new BlockHeader(id, unknown, width, height, multiplier, blockLength);
    }

    public GameMap convertMapFileToGameMap(MapFile mapFile) throws Exception {

        /* Generate list of players */
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < mapFile.maxNumberOfPlayers; i++) {
            players.add(new Player("Player " + i, new Color(i*20, i*20, i*20)));
        }

        /* Create initial game map with correct dimensions */
        GameMap gameMap = new GameMap(players, mapFile.getWidth() * 2 + 2, mapFile.getHeight() + 3);

        /* Set up the terrain */
        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {

            org.appland.settlers.model.Point point = mapFilePoint.getGamePointPosition();

            /* Filter points that have been cropped out */ // TODO: support cropping on/off
            if (doCropping && (point.x < 1 || point.x >= gameMap.getWidth() || point.y < 1 || point.y >= gameMap.getHeight())) {
                continue;
            }

            /* Assign textures */
            gameMap.setDetailedVegetationBelow(point, Utils.convertTextureToVegetation(mapFilePoint.getVegetationBelow()));
            gameMap.setDetailedVegetationDownRight(point, Utils.convertTextureToVegetation(mapFilePoint.getVegetationDownRight()));

            /* Set mineral quantities */
            if (mapFilePoint.hasMineral()) {
                Material mineral = Utils.resourceTypeToMaterial(mapFilePoint.getMineralType());

                gameMap.setMineralAmount(point, mineral, mapFilePoint.getMineralQuantity());
            }

            /* Place stones */
            if (mapFilePoint.hasStone()) {
                gameMap.placeStone(point, mapFilePoint.getStoneType(), mapFilePoint.getStoneAmount());
            }

            /* Place trees */
            if (mapFilePoint.hasTree()) {
                Tree.TreeType treeType = mapFilePoint.getTreeType();
                TreeSize treeSize = mapFilePoint.getTreeSize();

                gameMap.placeTree(point, treeType, treeSize);
            }

            /* Place dead trees */
            if (mapFilePoint.hasDeadTree()) {
                gameMap.placeDeadTree(point);
            }

            /* Handle remaining nature decorations */
            if (mapFilePoint.isNatureDecoration()) {
                DecorationType decorationType = mapFilePoint.getNatureDecorationType();

                if (DecorationType.NO_IMPACT_ON_GAME.contains(decorationType)) {
                    gameMap.placeDecoration(point, decorationType);
                }
            }

            /* Place wild animals */
            if (mapFilePoint.hasWildAnimal()) {
                gameMap.placeWildAnimal(point);
            }

            /* Set available harbor */
            if (mapFilePoint.isPossiblePlaceForHarbor()) {
                gameMap.setPossiblePlaceForHarbor(point);
            }

            /* Set the height */
            gameMap.setHeightAtPoint(point, mapFilePoint.getHeight());
        }

        /* Set starting points */
        gameMap.setStartingPoints(mapFile.getStartingPoints());

        if (debug) {
            printIfDebug(" -- Starting positions: ");
        }

        if (debug) {
            for (Point point : mapFile.getStartingPoints()) {
                printIfDebug("(" + point.x + ", " + point.y + ") ");
            }
        }

        return gameMap;
    }
}
