package org.appland.settlers.maps;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.rest.resource.IdManager;
import org.appland.settlers.utils.JsonUtils;
import org.appland.settlers.utils.PrintUtils;
import org.jline.terminal.TerminalBuilder;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Inspector class to analyze and extract information about maps and other game data.
 */
public class Inspector {

    @Option(name = "--info", usage = "Print information about the map")
    private boolean printInfo = false;

    @Option(name = "--info-around-type", usage = "Print information about points surrounding the type. (dead-tree)")
    String informationAroundType = null;

    @Option(name = "--include-negative", usage = "Use together with --info-around-type to also print information for non-matching points")
    private boolean includeNegative = false;

    @Option(name = "--dir", usage = "Folder to load all maps from")
    String dir = null;

    @Option(name = "--debug", usage = "Print debug information")
    boolean debug = false;

    @Option(name = "--file", usage = "Map file to load")
    static String mapFilename;

    @Option(name = "--override-width", usage = "Override the detected width of the console")
    int widthOverride = -1;

    @Option(name = "--override-height", usage = "Override the detected height of the console")
    int heightOverride = -1;

    @Option(name = "--compare-available-buildings", usage = "Compares the available buildings between the map file and the game map")
    boolean compareAvailableBuildings;

    @Option(name = "--point-info", handler=SettlersPointHandler.class, usage = "Prints detailed information about the given point from the file and from the game map")
    java.awt.Point infoPoint = null;

    @Option(name = "--print-starting-points", usage = "Prints a list of the starting points")
    private boolean printStartingPoints = false;

    @Option(name = "--filter-unreliable-comparisons", usage = "When selected points that are close to the border or the headquarter are hidden")
    private boolean filterUnreliableComparisons = false;

    @Option(name = "--render-map-file", usage = "Renders an ascii representation of the map file")
    private boolean renderMapFile = false;

    @Option(name = "--print-points", usage = "Prints a list of all the points in the map file")
    private boolean printPoints = false;

    @Option(name = "--harbors", usage = "Prints information about harbor points")
    private boolean harborInfo = false;

    @Option(name = "--to-json", usage = "Writes a json file with information about the map")
    private String toJson = null;

    @Option(name = "--place-players", usage = "When used, a headquarters will be placed on each starting point")
    private boolean placePlayers = false;

    private static final List<PlayerColor> COLORS = Arrays.stream(PlayerColor.values()).toList();

    private final MapLoader  mapLoader = new MapLoader();
    private final JsonUtils  jsonUtils;
    private final PrintUtils printUtils = new PrintUtils();

    private MapFile   mapFile;
    private int       consoleHeight;
    private int       consoleWidth;
    private GameMap   map;

    /**
     * Main method to run the inspector with command line arguments.
     */
    public static void main(String[] args) throws Exception {
        var inspector = new Inspector();
        var parser = new CmdLineParser(inspector);
        parser.parseArgument(args);

        if (inspector.isFileSelected()) {
            inspector.loadMapFile(mapFilename, inspector.placePlayers);
        }

        if (inspector.isPointsSurroundingTypeSelected()) {
            InformationType informationType = InformationType.fromString(inspector.informationAroundType);

            if (inspector.isFileSelected()) {
                inspector.printPointsSurroundingPointTypeInFile(informationType);
            } else if (inspector.isDirSelected()) {
                inspector.printPointsSurroundingPointTypeInAllFiles(inspector.dir, informationType);
            }
        }

        if (inspector.isPrintInfoSelected()) {
            inspector.printUtils.printMapInfo(inspector.mapFile);
        }

        if (inspector.isPrintMapFromFileChosen()) {
            inspector.printUtils.printMapFile(inspector.mapFile, inspector.consoleWidth, inspector.debug);
        }

        if (inspector.isCompareAvailableBuildingsChosen()) {
            inspector.compareAvailableBuildingPoints();
        }

        if (inspector.isPrintStartingPointsChosen()) {
            inspector.printUtils.printStartingPointsFromGameMap(inspector.mapLoader.convertMapFileToGameMap(inspector.mapFile));
        }

        if (inspector.isPrintPointInformationChosen()) {
            inspector.printPointInformation(new Point(inspector.infoPoint));
        }

        if (inspector.isDumpSpotsChosen()) {
            inspector.printUtils.printMapFilePointList(inspector.mapFile);
        }

        if (inspector.isToJsonChosen()) {
            inspector.writeToJson();
        }

        if (inspector.harborInfo) {
            inspector.printInfoOnHarborPoints();
        }
    }

    private void writeToJson() throws IOException, InvalidMapException, SettlersMapLoadingException {
        var mapFile = mapLoader.loadMapFromFile(mapFilename);
        Files.writeString(Path.of(toJson), jsonUtils.mapFileToDetailedJson(mapFile).toJSONString());
    }

    private void printInfoOnHarborPoints() {
        mapFile.getMapFilePoints().stream()
                .filter(MapFilePoint::isPossiblePlaceForHarbor)
                .forEach(mapFilePoint -> {
                    var point = mapFilePoint.getGamePointPosition();
                    var closeTiles = new HashSet<>(map.getSurroundingTiles(point));
                    var closeToFlagTiles = new HashSet<>(map.getSurroundingTiles(point.downRight()));

                    var oneStepAwayTiles = new HashSet<>(List.of(
                            map.getVegetationUpLeft(point.left()),
                            map.getVegetationAbove(point.left()),
                            map.getVegetationUpLeft(point.upLeft()),
                            map.getVegetationAbove(point.upLeft()),
                            map.getVegetationUpLeft(point.upRight()),
                            map.getVegetationAbove(point.upRight()),
                            map.getVegetationUpRight(point.upRight()),
                            map.getVegetationDownRight(point.upRight()),
                            map.getVegetationUpRight(point.right()),
                            map.getVegetationDownRight(point.right()),
                            map.getVegetationBelow(point.right()),
                            map.getVegetationDownRight(point.downRight()),
                            map.getVegetationBelow(point.downRight()),
                            map.getVegetationDownRight(point.downLeft()),
                            map.getVegetationBelow(point.downLeft()),
                            map.getVegetationDownLeft(point.downLeft()),
                            map.getVegetationUpLeft(point.downLeft()),
                            map.getVegetationDownLeft(point.left())
                    ));

                    System.out.println(" +");
                    System.out.printf("Close tiles: %s%n", closeTiles);
                    System.out.printf("Close to flag tiles: %s%n", closeToFlagTiles);
                    System.out.printf("One step away tiles: %s%n", oneStepAwayTiles);
                });
    }

    private boolean isToJsonChosen() {
        return toJson != null;
    }

    private void printPointsSurroundingPointTypeInAllFiles(String dir, InformationType informationType) throws IOException, InvalidMapException, SettlersMapLoadingException {
        var mapFiles = new ArrayList<MapFile>();

        var paths = Files.find(Paths.get(dir), Integer.MAX_VALUE,
                        (path, basicFileAttributes) -> path.toFile().getName().matches(".*.SWD") || path.toFile().getName().matches(".*.WLD"))
                .toList();

        for (var path : paths) {
            if (!Files.isDirectory(path)) {
                mapFiles.add(mapLoader.loadMapFromFile(path.toString()));
            }
        }

        printPointsSurroundingPointTypeInMapFiles(mapFiles, informationType);
    }

    private boolean isDirSelected() {
        return dir != null;
    }

    private void printPointsSurroundingPointTypeInFile(InformationType informationType) {
        printPointsSurroundingPointTypeInMapFiles(List.of(this.mapFile), informationType);
    }

    private void printPointsSurroundingPointTypeInMapFiles(List<MapFile> mapFiles, InformationType informationType) {
        var availableConstructionCenter = new EnumMap<BuildableSite, Integer>(BuildableSite.class);
        var availableConstructionLeft = new EnumMap<BuildableSite, Integer>(BuildableSite.class);
        var availableConstructionUpLeft = new EnumMap<BuildableSite, Integer>(BuildableSite.class);
        var availableConstructionUpRight = new EnumMap<BuildableSite, Integer>(BuildableSite.class);
        var availableConstructionRight = new EnumMap<BuildableSite, Integer>(BuildableSite.class);
        var availableConstructionDownRight = new EnumMap<BuildableSite, Integer>(BuildableSite.class);
        var availableConstructionDownLeft = new EnumMap<BuildableSite, Integer>(BuildableSite.class);

        var vegetationUpLeft = new EnumMap<Texture, Integer>(Texture.class);
        var vegetationAbove = new EnumMap<Texture, Integer>(Texture.class);
        var vegetationUpRight = new EnumMap<Texture, Integer>(Texture.class);
        var vegetationDownRight = new EnumMap<Texture, Integer>(Texture.class);
        var vegetationBelow = new EnumMap<Texture, Integer>(Texture.class);
        var vegetationDownLeft = new EnumMap<Texture, Integer>(Texture.class);

        int measuredPoints = 0;

        for (var mapFile : mapFiles) {
            for (var mapFilePoint : mapFile.getMapFilePoints()) {
                if (informationType == InformationType.DEAD_TREE && !mapFilePoint.hasDeadTree()) {
                    continue;
                }

                var point = mapFilePoint.getGamePointPosition();
                var mapFilePointLeft = mapFile.getMapFilePoint(point.left());
                var mapFilePointUpLeft = mapFile.getMapFilePoint(point.upLeft());
                var mapFilePointUpRight = mapFile.getMapFilePoint(point.upRight());
                var mapFilePointRight = mapFile.getMapFilePoint(point.right());
                var mapFilePointDownRight = mapFile.getMapFilePoint(point.downRight());
                var mapFilePointDownLeft = mapFile.getMapFilePoint(point.downLeft());

                incrementInMap(availableConstructionCenter, mapFilePoint.getBuildableSite());
                incrementInMap(availableConstructionLeft, mapFilePointLeft.getBuildableSite());
                incrementInMap(availableConstructionUpLeft, mapFilePointUpLeft.getBuildableSite());
                incrementInMap(availableConstructionUpRight, mapFilePointUpRight.getBuildableSite());
                incrementInMap(availableConstructionRight, mapFilePointRight.getBuildableSite());
                incrementInMap(availableConstructionDownRight, mapFilePointDownRight.getBuildableSite());
                incrementInMap(availableConstructionDownLeft, mapFilePointDownLeft.getBuildableSite());

                incrementInMap(vegetationUpLeft, mapFilePointUpLeft.getVegetationBelow());
                incrementInMap(vegetationAbove, mapFilePointUpLeft.getVegetationDownRight());
                incrementInMap(vegetationUpRight, mapFilePointUpRight.getVegetationBelow());
                incrementInMap(vegetationDownRight, mapFilePoint.getVegetationDownRight());
                incrementInMap(vegetationBelow, mapFilePoint.getVegetationBelow());
                incrementInMap(vegetationDownLeft, mapFilePointLeft.getVegetationDownRight());

                measuredPoints++;
            }
        }

        System.out.println();
        System.out.println("Surrounding available construction");

        System.out.printf(" - Center: %s%n", availableConstructionCenter);
        System.out.printf(" - Left: %s%n", availableConstructionLeft);
        System.out.printf(" - Up-left: %s%n", availableConstructionUpLeft);
        System.out.printf(" - Up-right: %s%n", availableConstructionUpRight);
        System.out.printf(" - Right: %s%n", availableConstructionRight);
        System.out.printf(" - Down-right: %s%n", availableConstructionDownRight);
        System.out.printf(" - Down-left: %s%n", availableConstructionDownLeft);

        System.out.println();
        System.out.println("Surrounding vegetation");
        System.out.printf(" - Up-left: %s%n", vegetationUpLeft);
        System.out.printf(" - Above: %s%n", vegetationAbove);
        System.out.printf(" - Up-right: %s%n", vegetationUpRight);
        System.out.printf(" - Down-rRight: %s%n", vegetationDownRight);
        System.out.printf(" - Below: %s%n", vegetationBelow);
        System.out.printf(" - Down-left: %s%n", vegetationDownLeft);
        System.out.printf("(%d points measured)%n", measuredPoints);
    }

    private boolean isFileSelected() {
        return mapFilename != null;
    }

    private boolean isPointsSurroundingTypeSelected() {
        return this.informationAroundType != null && (this.informationAroundType.equals("dead-tree") || this.informationAroundType.equals("possible-shipyard"));
    }

    private boolean isPrintInfoSelected() {
        return printInfo;
    }

    private boolean isDumpSpotsChosen() {
        return printPoints;
    }

    private boolean isPrintMapFromFileChosen() {
        return renderMapFile;
    }

    private boolean isCompareAvailableBuildingsChosen() {
        return compareAvailableBuildings;
    }

    private boolean isPrintStartingPointsChosen() {
        return printStartingPoints;
    }

    private boolean isPrintPointInformationChosen() {
        return infoPoint != null;
    }

    public Inspector() throws IOException {
        mapLoader.debug = debug;

        consoleWidth = TerminalBuilder.terminal().getWidth();
        consoleHeight = TerminalBuilder.terminal().getHeight();

        if (debug) {
            System.out.printf("Detected console dimensions: %dx%d%n", consoleWidth, consoleHeight);
        }

        if (widthOverride != -1) {
            consoleWidth = widthOverride;
        }

        if (heightOverride != -1) {
            consoleHeight = heightOverride;
        }

        if (debug) {
            System.out.printf("Using dimensions: %dx%d%n", consoleWidth, consoleHeight);
        }

        jsonUtils = new JsonUtils(new IdManager());
    }

    /**
     * Loads the given file, creates a MapFile instance based on it, and converts it to a GameMap instance
     *
     * @param mapFilename  The filename of the map to load
     * @param placePlayers If true players will also be placed
     * @throws Exception If map loading fails
     */
    private void loadMapFile(String mapFilename, boolean placePlayers) throws Exception {
        mapFile = mapLoader.loadMapFromFile(mapFilename);
        map = mapLoader.convertMapFileToGameMap(mapFile);

        System.out.printf("Max players: %d%n", mapFile.getMaxNumberOfPlayers());
        System.out.printf("Starting positions: %s%n", mapFile.getGamePointStartingPoints());

        List<Point> startingPoints = map.getStartingPoints();
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < startingPoints.size(); i++) {
            players.add(new Player("" + i, COLORS.get(i), Nation.ROMANS, PlayerType.HUMAN));
        }
        map.setPlayers(players);

        if (placePlayers) {
            if (mapFile.getGamePointStartingPoints().isEmpty()) {
                System.out.println("No starting points found in map file");
            } else {
                for (int i = 0; i < startingPoints.size(); i++) {
                    Point point = startingPoints.get(i);
                    Player player = players.get(i);

                    try {
                        map.placeBuilding(new Headquarter(player), point);

                        System.out.println(point + ": OK");
                    } catch (InvalidUserActionException e) {
                        System.out.printf("%s: Not OK (exception is %s) %n", point, e);
                    }
                }
            }
        }
    }

    /**
     * Prints detailed information about the given point
     *
     * @param point The point to print information about
     */
    private void printPointInformation(Point point) {
        System.out.println();
        System.out.printf("Detailed information about %s%n", infoPoint);

        MapFilePoint mapFilePoint = mapFile.getMapFilePoint(point);

        System.out.println();
        System.out.println("Map file point:");
        printUtils.printMapFilePoint(mapFilePoint);
        printUtils.printSurroundingTerrainFromMapFile(point, mapFile);
        printUtils.printSurroundingAvailableBuildingsFromMapFile(point, mapFile);
        printUtils.printSurroundingStonesAndTreesFromMapFile(point, mapFile);
        printUtils.printSurroundingHeightsFromMapFile(point, mapFile);

        System.out.println();
        System.out.println("Game map");
        printUtils.printGameMapPoint(point, map);

        var player = map.getPlayers().stream()
                .filter(p -> p.getLandInPoints().contains(point))
                .findFirst()
                .orElse(null);

        printUtils.printSurroundingTerrainFromGameMap(point, map);

        if (player != null) {
            var availableHousePoints = map.getAvailableHousePoints(player);
            System.out.println(" - Can build: " + map.getAvailableHousePoints(player).get(point));
            printUtils.printSurroundingAvailableBuildingsFromGameMap(point, availableHousePoints);
            System.out.println(" - Distance to border: " + GameUtils.getDistanceToBorder(point, player));
        }

        System.out.println(" - Distance headquarter: " + GameUtils.distanceInGameSteps(point, GameUtils.getHeadquarterForPlayer(player).getPosition()));

        System.out.println();
    }

    /**
     * Compares the available building points in the map file and what the game calculates.
     */
    private void compareAvailableBuildingPoints() throws Exception {
        System.out.println("Available starting points in MapFile and in GameMap");
        System.out.println();

        var player = map.getPlayers().getFirst();

        if (debug) {
            System.out.printf("Starting point for player from MapFile: %s%n", new Point(mapFile.getGamePointStartingPoints().getFirst()));
            System.out.printf("Starting point for player from GameMap: %s%n", map.getStartingPoints().getFirst());
        }

        map.placeBuilding(new Headquarter(player), map.getStartingPoints().getFirst());

        // Compare the available building points calculated in the game with the corresponding points in the MapFile
        var availablePoints = map.getAvailableHousePoints(player);

        var matched = new HashMap<Point, AvailableBuildingComparison>();
        var mismatched = new HashMap<Point, AvailableBuildingComparison>();

        availablePoints.forEach((point, size) -> {
            var mapFilePoint = mapFile.getMapFilePoint(point);

            if (!availablePoints.containsKey(point)) {
                return;
            }

            var comparison = new AvailableBuildingComparison(size, map.isAvailableFlagPoint(player, point), mapFilePoint.getBuildableSite());

            if (comparison.matches()) {
                matched.put(point, comparison);
            } else {
                mismatched.put(point, comparison);
            }
        });

        // Print the matches
        System.out.println();
        System.out.println("Matches: ");
        matched.forEach((point, comparison) ->
                System.out.printf(" - %s: %s, %s%n", point, comparison.getAvailableInMap(), comparison.availableInFile()));

        // Print the points that didn't match
        var headquarter = GameUtils.getHeadquarterForPlayer(player);

        System.out.println();
        System.out.println("Mismatched: ");

        int filtered = 0;

        for (Map.Entry<Point, AvailableBuildingComparison> pointAndComparison : mismatched.entrySet()) {
            Point point = pointAndComparison.getKey();
            AvailableBuildingComparison comparison = pointAndComparison.getValue();

            int distanceToBorder = GameUtils.getDistanceToBorder(point, player);
            int distanceToHeadquarter = GameUtils.distanceInGameSteps(point, headquarter.getPosition());

            // Filter comparisons where the point is too close to the border or the headquarters
            if (filterUnreliableComparisons && isComparisonUnreliable(distanceToBorder, distanceToHeadquarter)) {
                filtered++;

                continue;
            }

            MapFilePoint spot = mapFile.getMapFilePoint(point);
            MapFilePoint spotLeft = mapFile.getMapFilePoint(point.left());
            MapFilePoint spotUpLeft = mapFile.getMapFilePoint(point.upLeft());
            MapFilePoint spotDownLeft = mapFile.getMapFilePoint(point.downLeft());
            MapFilePoint spotRight = mapFile.getMapFilePoint(point.right());
            MapFilePoint spotUpRight = mapFile.getMapFilePoint(point.upRight());
            MapFilePoint spotDownRight = mapFile.getMapFilePoint(point.downRight());

            System.out.printf("- %s - game: %s, file: %s, distance to border: %d, distance to headquarter: %d, height differences: %d, %d, %d, %d, %d, %d",
                    point,
                    comparison.availableInGame(),
                    comparison.availableInFile(),
                    distanceToBorder,
                    distanceToHeadquarter,
                    (spot.getHeight() - spotLeft.getHeight()),
                    (spot.getHeight() - spotUpLeft.getHeight()),
                    (spot.getHeight() - spotUpRight.getHeight()),
                    (spot.getHeight() - spotRight.getHeight()),
                    (spot.getHeight() - spotDownRight.getHeight()),
                    (spot.getHeight() - spotDownLeft.getHeight())
            );

            if (comparison.availableInFile() == BuildableSite.OCCUPIED_BY_TREE) {
                if (!map.isTreeAtPoint(point)) {
                    System.out.println("   -- Tree in file but not on map: " + point);
                } else {
                    System.out.println("   -- Tree available in game on map: " + map.getTreeAtPoint(point));
                }
            }
        }

        System.out.println();
        System.out.printf("""
        Comparison summary:
         - Matching: %d
         - Mismatched: %d
         - Filtered: %d
        """,
                matched.size(),
                mismatched.size(),
                filtered
        );
    }

    private boolean isComparisonUnreliable(int distanceToBorder, int distanceToHeadquarter) {
        return distanceToBorder < 4 || distanceToHeadquarter < 4;
    }

    private record AvailableBuildingComparison(Size availableInGame, boolean availableFlagInGame, BuildableSite availableInFile) {
        public boolean matches() {
            return switch (availableInGame) {
                case LARGE -> availableInFile == BuildableSite.CASTLE;
                case MEDIUM -> availableInFile == BuildableSite.HOUSE;
                case SMALL -> availableInFile == BuildableSite.HUT;
                case null -> availableFlagInGame && (availableInFile == BuildableSite.FLAG || availableInFile == BuildableSite.FLAG_NEXT_TO_INACCESSIBLE_TERRAIN);
            };
        }

        public Size getAvailableInMap() {
            return availableInGame;
        }
    }

    private <T> void incrementInMap(Map<T, Integer> map, T item) {
        map.put(item, map.getOrDefault(item, 0) + 1);
    }

    private enum InformationType {
        POSSIBLE_SHIPYARD, DEAD_TREE;

        public static InformationType fromString(String type) {
            return switch (type) {
                case "dead-tree" -> DEAD_TREE;
                default -> null;
            };
        }
    }
}
