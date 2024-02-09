package org.appland.settlers.maps;

import org.appland.settlers.model.Building;
import org.appland.settlers.model.DetailedVegetation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.jline.terminal.TerminalBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import static java.awt.Color.PINK;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.lang.String.format;

public class Inspector {

    /* Define command line options */
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

    /* Regular fields */
    private static final List<Color> COLORS = new ArrayList<>();

    static {
        COLORS.add(BLUE);
        COLORS.add(RED);
        COLORS.add(WHITE);
        COLORS.add(GREEN);
        COLORS.add(YELLOW);
        COLORS.add(BLACK);
        COLORS.add(GRAY);
        COLORS.add(PINK);
    }

    private final MapLoader mapLoader;

    private MapFile mapFile;
    private int     consoleHeight;
    private int     consoleWidth;
    private GameMap map;

    /**
     * Run the inspector
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Inspector inspector = new Inspector();
        CmdLineParser parser = new CmdLineParser(inspector);

        parser.parseArgument(args);

        /* Load the map directly if a map filename is given */
        if (inspector.isFileSelected()) {
            inspector.loadMapFile(mapFilename, inspector.placePlayers);
        }

        /* Print information about points surrounding points of type if chosen */
        if (inspector.isPointsSurroundingTypeSelected()) {
            InformationType informationType = InformationType.fromString(inspector.informationAroundType);

            if (inspector.isFileSelected()) {
                inspector.printPointsSurroundingPointTypeInFile(informationType);
            } else if (inspector.isDirSelected()) {
                inspector.printPointsSurroundingPointTypeInAllFiles(inspector.dir, informationType);
            }
        }

        /* Print map info if selected */
        if (inspector.isPrintInfoSelected()) {
            inspector.printMapInfo();
        }

        /* Print the map from the file */
        if (inspector.isPrintMapFromFileChosen()) {
            inspector.printMapFile();
        }

        /* Compare the available buildings */
        if (inspector.isCompareAvailableBuildingsChosen()) {
            inspector.compareAvailableBuildingPoints();
        }

        /* Print the starting points if selected */
        if (inspector.isPrintStartingPointsChosen()) {
            inspector.printStartingPointsFromGameMap();
        }

        /* Print detailed information about a given point if selected */
        if (inspector.isPrintPointInformationChosen()) {
            inspector.printPointInformation(new Point(inspector.infoPoint));
        }

        /* Dump spots */
        if (inspector.isDumpSpotsChosen()) {
            inspector.printSpotList();
        }

        /* Write to json */
        if (inspector.isToJsonChosen()) {
            inspector.writeToJson();
        }

        if (inspector.harborInfo) {
            inspector.printInfoOnHarborPoints();
        }
    }

    private void printInfoOnHarborPoints() {
        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {
            if (mapFilePoint.isPossiblePlaceForHarbor()) {
                Point point = mapFilePoint.getGamePointPosition();

                Set<DetailedVegetation> closeTiles = new HashSet<>(map.getSurroundingTiles(point));
                Set<DetailedVegetation> closeToFlagTiles = new HashSet<>(map.getSurroundingTiles(point.downRight()));
                Set<DetailedVegetation> oneStepAwayTiles = new HashSet<>();

                Point pointLeft = point.left();
                Point pointUpLeft = point.upLeft();
                Point pointUpRight = point.upRight();
                Point pointRight = point.right();
                Point pointDownRight = point.downRight();
                Point pointDownLeft = point.downLeft();

                oneStepAwayTiles.add(map.getDetailedVegetationUpLeft(pointLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationAbove(pointLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationUpLeft(pointUpLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationAbove(pointUpLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationUpLeft(pointUpRight));
                oneStepAwayTiles.add(map.getDetailedVegetationAbove(pointUpRight));
                oneStepAwayTiles.add(map.getDetailedVegetationUpRight(pointUpRight));
                oneStepAwayTiles.add(map.getDetailedVegetationDownRight(pointUpRight));
                oneStepAwayTiles.add(map.getDetailedVegetationUpRight(pointRight));
                oneStepAwayTiles.add(map.getDetailedVegetationDownRight(pointRight));
                oneStepAwayTiles.add(map.getDetailedVegetationBelow(pointRight));
                oneStepAwayTiles.add(map.getDetailedVegetationDownRight(pointDownRight));
                oneStepAwayTiles.add(map.getDetailedVegetationBelow(pointDownRight));
                oneStepAwayTiles.add(map.getDetailedVegetationDownRight(pointDownLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationBelow(pointDownLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationDownLeft(pointDownLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationUpLeft(pointDownLeft));
                oneStepAwayTiles.add(map.getDetailedVegetationDownLeft(pointLeft));

                System.out.println(" +");
                System.out.printf("Close tiles: %s%n", closeTiles);
                System.out.printf("Close to flag tiles: %s%n", closeToFlagTiles);
                System.out.printf("One step away tiles: %s%n", oneStepAwayTiles);
            }
        }
    }

    private void writeToJson() throws IOException, InvalidMapException, SettlersMapLoadingException {
        MapFile mapFile = mapLoader.loadMapFromFile(mapFilename);

        JSONObject jsonMap = new JSONObject();

        jsonMap.put("title", mapFile.getTitle());
        jsonMap.put("author", mapFile.getAuthor());
        jsonMap.put("width", mapFile.getWidth());
        jsonMap.put("height", mapFile.getHeight());
        jsonMap.put("maxNumberPlayers", mapFile.getMaxNumberOfPlayers());
        jsonMap.put("terrain", mapFile.getTerrainType().name().toUpperCase());

        JSONArray jsonStartingPoints = new JSONArray();

        jsonMap.put("startingPoints", jsonStartingPoints);

        for (Point point : mapFile.getGamePointStartingPoints()) {
            JSONObject jsonPoint = new JSONObject();

            jsonPoint.put("x", point.x);
            jsonPoint.put("y", point.y);

            jsonStartingPoints.add(jsonPoint);
        }

        JSONArray jsonMapPoints = new JSONArray();

        jsonMap.put("points", jsonMapPoints);

        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {
            JSONObject jsonMapFilePoint = new JSONObject();

            Point gamePoint = mapFilePoint.getGamePointPosition();

            jsonMapFilePoint.put("x", gamePoint.x);
            jsonMapFilePoint.put("y", gamePoint.y);
            jsonMapFilePoint.put("height", mapFilePoint.getHeight());

            jsonMapFilePoint.put("vegetationBelow", mapFilePoint.getVegetationBelow().name().toUpperCase());
            jsonMapFilePoint.put("vegetationDownRight", mapFilePoint.getVegetationDownRight().name().toUpperCase());

            jsonMapFilePoint.put("vegetationBelowAsInt", mapFilePoint.getVegetationBelow().ordinal());
            jsonMapFilePoint.put("vegetationDownRightAsInt", mapFilePoint.getVegetationDownRight().ordinal());

            if (mapFilePoint.hasStone()) {
                jsonMapFilePoint.put("stone", mapFilePoint.getStoneAmount());
            }

            if (mapFilePoint.hasTree()) {
                jsonMapFilePoint.put("tree", mapFilePoint.getTreeType().name().toUpperCase());
                jsonMapFilePoint.put("treeAsInt", mapFilePoint.getTreeType().ordinal());
            }

            if (mapFilePoint.getBuildableSite() != null) {
                jsonMapFilePoint.put("canBuild", mapFilePoint.getBuildableSite().name().toUpperCase());
                jsonMapFilePoint.put("canBuildAsInt", mapFilePoint.getBuildableSite().ordinal());
            }

            jsonMapPoints.add(jsonMapFilePoint);
        }

        Files.writeString(Paths.get(toJson), jsonMap.toJSONString());
    }

    private boolean isToJsonChosen() {
        return toJson != null;
    }

    private void printPointsSurroundingPointTypeInAllFiles(String dir, InformationType informationType) throws IOException, InvalidMapException, SettlersMapLoadingException {

        List<MapFile> mapFiles = new ArrayList<>();

        /* List all maps */
        List<Path> paths = Files.find(Paths.get(dir),
                Integer.MAX_VALUE,
                (path, basicFileAttributes) -> path.toFile().getName().matches(".*.SWD") ||
                        path.toFile().getName().matches(".*.WLD")
        ).toList();

        /* Print information for points surrounding points of the selected type */
        for (Path path : paths) {

            if (Files.isDirectory(path)) {
                continue;
            }

            String filename = path.toString();

            MapFile mapFile = mapLoader.loadMapFromFile(filename);

            mapFiles.add(mapFile);
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
        Map<BuildableSite, Integer> availableConstructionCenter = new EnumMap<>(BuildableSite.class);
        Map<BuildableSite, Integer> availableConstructionLeft = new EnumMap<>(BuildableSite.class);
        Map<BuildableSite, Integer> availableConstructionUpLeft = new EnumMap<>(BuildableSite.class);
        Map<BuildableSite, Integer> availableConstructionUpRight = new EnumMap<>(BuildableSite.class);
        Map<BuildableSite, Integer> availableConstructionRight = new EnumMap<>(BuildableSite.class);
        Map<BuildableSite, Integer> availableConstructionDownRight = new EnumMap<>(BuildableSite.class);
        Map<BuildableSite, Integer> availableConstructionDownLeft = new EnumMap<>(BuildableSite.class);

        Map<Texture, Integer> vegetationUpLeft = new EnumMap<>(Texture.class);
        Map<Texture, Integer> vegetationAbove = new EnumMap<>(Texture.class);
        Map<Texture, Integer> vegetationUpRight = new EnumMap<>(Texture.class);
        Map<Texture, Integer> vegetationDownRight = new EnumMap<>(Texture.class);
        Map<Texture, Integer> vegetationBelow = new EnumMap<>(Texture.class);
        Map<Texture, Integer> vegetationDownLeft = new EnumMap<>(Texture.class);

        int measuredPoints = 0;

        for (MapFile mapFile : mapFiles) {

            for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {

                /* Filter points that don't match the requested information */
                if (informationType == InformationType.DEAD_TREE && !mapFilePoint.hasDeadTree()) {
                    continue;
                }

                Point point = mapFilePoint.getGamePointPosition();

                MapFilePoint mapFilePointLeft = mapFile.getMapFilePoint(point.left());
                MapFilePoint mapFilePointUpLeft = mapFile.getMapFilePoint(point.upLeft());
                MapFilePoint mapFilePointUpRight = mapFile.getMapFilePoint(point.upRight());
                MapFilePoint mapFilePointRight = mapFile.getMapFilePoint(point.right());
                MapFilePoint mapFilePointDownRight = mapFile.getMapFilePoint(point.downRight());
                MapFilePoint mapFilePointDownLeft = mapFile.getMapFilePoint(point.downLeft());

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

                measuredPoints = measuredPoints + 1;
            }
        }

        System.out.println();
        System.out.println("Surrounding available construction");

        System.out.println(" - Center: " + availableConstructionCenter);
        System.out.println(" - Left: " + availableConstructionLeft);
        System.out.println(" - Up-left: " + availableConstructionUpLeft);
        System.out.println(" - Up-right: " + availableConstructionUpRight);
        System.out.println(" - Right: " + availableConstructionRight);
        System.out.println(" - Down-right: " + availableConstructionDownRight);
        System.out.println(" - Down-left: " + availableConstructionDownLeft);

        System.out.println();
        System.out.println("Surrounding vegetation");

        System.out.println(" - Up-left: " + vegetationUpLeft);
        System.out.println(" - Above: " + vegetationAbove);
        System.out.println(" - Up-right: " + vegetationUpRight);
        System.out.println(" - Down-rRight: " + vegetationDownRight);
        System.out.println(" - Below: " + vegetationBelow);
        System.out.println(" - Down-left: " + vegetationDownLeft);

        System.out.println("(" + measuredPoints + " points measured)");
    }

    private boolean isFileSelected() {
        return mapFilename != null;
    }

    private boolean isPointsSurroundingTypeSelected() {
        return this.informationAroundType != null && (this.informationAroundType.equals("dead-tree") || this.informationAroundType.equals("possible-shipyard"));
    }

    private void printMapInfo() {
        System.out.println();
        System.out.println("About the map:");
        System.out.println(" - Title: " + mapFile.getTitle());
        System.out.println(" - Author: " + mapFile.getAuthor());
        System.out.println(" - Width: " + mapFile.getWidth());
        System.out.println(" - Height: " + mapFile.getHeight());
        System.out.println(" - Max number of players: " + mapFile.getMaxNumberOfPlayers());
    }

    private boolean isPrintInfoSelected() {
        return printInfo;
    }

    private void printSpotList() {

        System.out.println();
        System.out.println("All spots in the map file");

        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {
            Point point = mapFilePoint.getGamePointPosition();

            MapFilePoint spotLeft = mapFile.getMapFilePoint(point.left());
            MapFilePoint spotUpLeft = mapFile.getMapFilePoint(point.upLeft());
            MapFilePoint spotDownLeft = mapFile.getMapFilePoint(point.downLeft());
            MapFilePoint spotRight = mapFile.getMapFilePoint(point.right());
            MapFilePoint spotUpRight = mapFile.getMapFilePoint(point.upRight());
            MapFilePoint spotDownRight = mapFile.getMapFilePoint(point.downRight());

            System.out.print(" - " + point + " available: " + mapFilePoint.getBuildableSite() +
                    ", height: " + mapFilePoint.getHeight() +
                    ", height differences:");

            if (spotLeft != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotLeft.getHeight()));
            }

            if (spotUpLeft != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotUpLeft.getHeight()));
            }

            if (spotDownLeft!= null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotDownLeft.getHeight()));
            }

            if (spotRight != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotRight.getHeight()));
            }

            if (spotUpRight != null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotUpRight.getHeight()));
            }

            if (spotDownRight!= null) {
                System.out.print(" " + (mapFilePoint.getHeight() - spotDownRight.getHeight()));
            }

            System.out.println();
        }
    }

    private boolean isDumpSpotsChosen() {
        return printPoints;
    }

    private boolean isPrintMapFromFileChosen() {
        return renderMapFile;
    }

    /**
     * Returns true if the user has chosen to compare available buildings
     *
     * @return
     */
    private boolean isCompareAvailableBuildingsChosen() {
        return compareAvailableBuildings;
    }

    /**
     * Returns true if the user has chosen to print the starting points
     *
     * @return
     */
    private boolean isPrintStartingPointsChosen() {
        return printStartingPoints;
    }

    /**
     * Returns true if the user has chosen to print detailed information about a selected point
     *
     * @return
     */
    private boolean isPrintPointInformationChosen() {
        return infoPoint != null;
    }

    /**
     * Creates a new Inspector instance
     *
     * @throws IOException
     */
    public Inspector() throws IOException {
        mapLoader = new MapLoader();

        mapLoader.debug = debug;

        consoleWidth = TerminalBuilder.terminal().getWidth();
        consoleHeight = TerminalBuilder.terminal().getHeight();

        if (debug) {
            System.out.println("Detected console dimensions: " + consoleWidth + "x" + consoleHeight);
        }

        if (widthOverride != -1) {
            consoleWidth = widthOverride;
        }

        if (heightOverride != -1) {
            consoleHeight = heightOverride;
        }

        if (debug) {
            System.out.println("Using dimensions: " + consoleWidth + "x" + consoleHeight);
        }
    }

    /**
     * Loads the given file, creates a MapFile instance based on it, and converts it to a GameMap instance
     *
     * @param mapFilename
     * @throws Exception
     */
    private void loadMapFile(String mapFilename, boolean placePlayers) throws Exception {
        mapFile = mapLoader.loadMapFromFile(mapFilename);
        map = mapLoader.convertMapFileToGameMap(mapFile);

        System.out.println("Max players: " + mapFile.getMaxNumberOfPlayers());
        System.out.println("Starting positions: " + mapFile.getGamePointStartingPoints());

        List<Point> startingPoints = map.getStartingPoints();
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < startingPoints.size(); i++) {
            players.add(new Player("" + i, COLORS.get(i)));
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
                        System.out.printf("%s: Not OK (exception is %s)%n", point, e);
                    }
                }
            }
        }
    }

    /**
     * Renders the map file to stdout with the starting points highlighted
     */
    private void printMapFile() {

        String[][] mapFileRender = renderMapFileToStringArray(mapFile, mapFile.getGamePointStartingPoints());

        /* Print the render of the map file */
        StringBuilder sb = new StringBuilder();
        for (String[] row : mapFileRender) {

            int index = 0;
            for (String character : row) {

                if (index == consoleWidth) {
                    break;
                }

                index++;

                if (character == null) {
                    sb.append(" ");
                } else {
                    sb.append(character);
                }
            }
        }

        System.out.println(sb);
    }

    /**
     * Prints the starting points from the GameMap instance
     */
    private void printStartingPointsFromGameMap() {

        System.out.println();
        System.out.println("Starting points: ");

        for (Point point : map.getStartingPoints()) {
            System.out.println(" - " + point);
        }
    }

    /**
     * Prints detailed information about the given point
     *
     * @param infoPoint
     */
    private void printPointInformation(Point infoPoint) {

        System.out.println();
        System.out.println("Detailed information about " + infoPoint);

        /* Print information about the point read from the MapFile */
        MapFilePoint spot = mapFile.getMapFilePoint(infoPoint);
        System.out.println();
        System.out.println("Map file");

        if (spot.hasTree()) {
            System.out.println(" - Tree");
        }

        if (spot.hasStone()) {
            System.out.println(" - Stone");
        }

        if (spot.getBuildableSite() != null) {
            System.out.println(" - Can build: " + spot.getBuildableSite());
        }

        System.out.println(" - Surrounding terrain:");
        System.out.println("   -- Above 1: " +
                mapFile.getMapFilePoint(infoPoint.upLeft().upLeft()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.upLeft().upLeft()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.up()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.up()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.upRight().upRight()).getVegetationBelow());
        System.out.println("   -- Above 2: " +
                mapFile.getMapFilePoint(infoPoint.upLeft().left()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.upLeft()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.upLeft()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.upRight()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.upRight()).getVegetationDownRight());
        System.out.println("   -- Below 1: " +
                mapFile.getMapFilePoint(infoPoint.left()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.left()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.right()).getVegetationBelow());
        System.out.println("   -- Below 2: " +
                mapFile.getMapFilePoint(infoPoint.left().downLeft()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.downLeft()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.downLeft()).getVegetationDownRight() + " " +
                mapFile.getMapFilePoint(infoPoint.downRight()).getVegetationBelow() + " " +
                mapFile.getMapFilePoint(infoPoint.downRight()).getVegetationDownRight());

        System.out.println(" - Surrounding available buildings:");
        System.out.println("   -- Above 1: " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.upLeft().upLeft()).getBuildableSite()) + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.up()).getBuildableSite()) + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.upRight().upRight()).getBuildableSite()));
        System.out.println("   -- Above 2:           " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.upLeft()).getBuildableSite()) + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.upRight()).getBuildableSite()));
        System.out.println("   -- Same:    " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.left()).getBuildableSite()) + " " +
                format("%-20s", "POINT") + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.right()).getBuildableSite()));
        System.out.println("   -- Below 1:           " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.downLeft()).getBuildableSite()) + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.downRight()).getBuildableSite()));
        System.out.println("   -- Below 2: " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.downLeft().downLeft()).getBuildableSite()) + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.down()).getBuildableSite()) + " " +
                format("%-20s", mapFile.getMapFilePoint(infoPoint.downRight().downRight()).getBuildableSite()));

        System.out.println(" - Surrounding stones and trees:");
        System.out.println("   -- Above 1: " +
                treeOrStoneOrNoneString(mapFile, infoPoint.upLeft().upLeft()) + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.up()) + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.upRight().upRight()));
        System.out.println("   -- Above 2:    " +
                treeOrStoneOrNoneString(mapFile, infoPoint.upLeft()) + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.upRight()));
        System.out.println("   -- Same:    " +
                treeOrStoneOrNoneString(mapFile, infoPoint.left()) + " " +
                "POINT" + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.right()));
        System.out.println("   -- Below 1:    " +
                treeOrStoneOrNoneString(mapFile, infoPoint.downLeft()) + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.downRight()));
        System.out.println("   -- Below 2: " +
                treeOrStoneOrNoneString(mapFile, infoPoint.downLeft().downLeft()) + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.down()) + " " +
                treeOrStoneOrNoneString(mapFile, infoPoint.downRight().downRight()));

        System.out.println(" - Surrounding heights:");
        System.out.println("   -- Above 1: " +
                mapFile.getMapFilePoint(infoPoint.upLeft().upLeft()).getHeight() + " " +
                        mapFile.getMapFilePoint(infoPoint.up()).getHeight() + " " +
                        mapFile.getMapFilePoint(infoPoint.upRight().upRight()).getHeight());
        System.out.println("   -- Above 2:   " +
                mapFile.getMapFilePoint(infoPoint.upLeft()).getHeight() + " " +
                mapFile.getMapFilePoint(infoPoint.upRight()).getHeight());
        System.out.println("   -- Same:    " +
                mapFile.getMapFilePoint(infoPoint.left()).getHeight() + " " +
                mapFile.getMapFilePoint(infoPoint).getHeight() + " " +
                mapFile.getMapFilePoint(infoPoint.right()).getHeight());
        System.out.println("   -- Below 1:   " +
                mapFile.getMapFilePoint(infoPoint.downLeft()).getHeight() + " " +
                mapFile.getMapFilePoint(infoPoint.downRight()).getHeight());
        System.out.println("   -- Below 2: " +
                mapFile.getMapFilePoint(infoPoint.downLeft().downLeft()).getHeight() + " " +
                mapFile.getMapFilePoint(infoPoint.down()).getHeight() + " " +
                mapFile.getMapFilePoint(infoPoint.downRight().downRight()).getHeight());

        System.out.println();


        /* Print the information about the point read from the GameMap */
        System.out.println();
        System.out.println("Game map");
        if (map.isTreeAtPoint(infoPoint)) {
            System.out.println(" - Tree");
        }

        if (spot.hasStone()) {
            System.out.println(" - Stone");
        }

        Player player = null;

        for (Player p : map.getPlayers()) {
            if (p.getLandInPoints().contains(infoPoint)) {
                player = p;

                break;
            }
        }

        Map<Point, Size> availableHousePoints = map.getAvailableHousePoints(player);
        if (player != null) {
            System.out.println(" - Can build: " + map.getAvailableHousePoints(player).get(infoPoint));
        }

        System.out.println(" - Surrounding terrain:");
        System.out.println("   -- Above 1: " +
                map.getDetailedVegetationUpLeft(infoPoint.up()) + " " +
                map.getDetailedVegetationAbove(infoPoint.up()) + " " +
                map.getDetailedVegetationUpRight(infoPoint.up()));
        System.out.println("   -- Above 2: " +
                map.getDetailedVegetationDownLeft(infoPoint.up()) + " " +
                map.getDetailedVegetationBelow(infoPoint.up()) + " " +
                map.getDetailedVegetationDownRight(infoPoint.up()));
        System.out.println("   -- Above 3: " +
                map.getDetailedVegetationUpLeft(infoPoint) + " " +
                map.getDetailedVegetationAbove(infoPoint) + " " +
                map.getDetailedVegetationUpRight(infoPoint));
        System.out.println("   -- Below 1: " +
                map.getDetailedVegetationDownLeft(infoPoint) + " " +
                map.getDetailedVegetationBelow(infoPoint) + " " +
                map.getDetailedVegetationDownRight(infoPoint));
        System.out.println("   -- Below 2: " +
                map.getDetailedVegetationUpLeft(infoPoint.down()) + " " +
                map.getDetailedVegetationAbove(infoPoint.down()) + " " +
                map.getDetailedVegetationUpRight(infoPoint.down()));
        System.out.println("   -- Below 3: " +
                map.getDetailedVegetationDownLeft(infoPoint.down()) + " " +
                map.getDetailedVegetationBelow(infoPoint.down()) + " " +
                map.getDetailedVegetationDownRight(infoPoint.down()));

        System.out.println(" - Surrounding available buildings:");
        System.out.println("   -- Above 1: " +
                format("%-20s", availableHousePoints.get(infoPoint.upLeft().upLeft())) + " " +
                format("%-20s", availableHousePoints.get(infoPoint.up())) + " " +
                format("%-20s", availableHousePoints.get(infoPoint.upRight().upRight())));
        System.out.println("   -- Above 2:           " +
                format("%-20s", availableHousePoints.get(infoPoint.upLeft())) + " " +
                format("%-20s", availableHousePoints.get(infoPoint.upRight())));
        System.out.println("   -- Same:    " +
                format("%-20s", availableHousePoints.get(infoPoint.left())) + " " +
                format("%-20s", "POINT") + " " +
                format("%-20s", availableHousePoints.get(infoPoint.right())));
        System.out.println("   -- Below 1:           " +
                format("%-20s", availableHousePoints.get(infoPoint.downLeft())) + " " +
                format("%-20s", availableHousePoints.get(infoPoint.downRight())));
        System.out.println("   -- Below 2: " +
                format("%-20s", availableHousePoints.get(infoPoint.downLeft().downLeft())) + " " +
                format("%-20s", availableHousePoints.get(infoPoint.down())) + " " +
                format("%-20s", availableHousePoints.get(infoPoint.downRight().downRight())));

        /* Print the closest border point */
        int distance = getDistanceToBorder(infoPoint, player);

        System.out.println(" - Distance to border: " + distance);

        /* Print the distance to the headquarters */
        System.out.println(" - Distance headquarter: " + distanceInGame(infoPoint, getHeadquarterForPlayer(player).getPosition()));

        System.out.println();
    }

    private String treeOrStoneOrNoneString(MapFile mapFile, Point point) {
        MapFilePoint mapFilePoint = mapFile.getMapFilePoint(point);

        if (mapFilePoint.hasTree()) {
            return "tree ";
        } else if (mapFilePoint.hasStone()) {
            return "stone";
        } else {
            return "  x  ";
        }
    }

    /**
     * Returns the distance to the closest border point for the given point
     *
     * @param infoPoint
     * @param player
     * @return
     */
    private int getDistanceToBorder(Point infoPoint, Player player) {
        int distance = Integer.MAX_VALUE;
        for (Point point : player.getBorderPoints()) {

            int tmpDistance = distanceInGame(point, infoPoint);

            if (tmpDistance < distance) {
                distance = tmpDistance;
            }
        }

        return distance;
    }

    /**
     * Returns the headquarters for the given player
     *
     * @param player
     * @return
     */
    private Headquarter getHeadquarterForPlayer(Player player) {
        for (Building building : player.getBuildings()) {
            if (building instanceof Headquarter) {
                return (Headquarter) building;
            }
        }

        return null;
    }

    /**
     * Returns the distance when traveling between the two points following in-game rules
     *
     * @param point
     * @param infoPoint
     * @return
     */
    private int distanceInGame(Point point, Point infoPoint) {
        int distanceY = Math.abs(infoPoint.y - point.y);
        int distanceX = Math.abs(infoPoint.x - point.x);

        int distance = distanceY;

        if (distanceX > distanceY) {
            distance += distanceX - distanceY;
        }

        return distance;
    }

    /**
     * Compared the available points to build on in a map file and what the game calculates
     *
     * @return
     * @throws Exception
     */
    private void compareAvailableBuildingPoints() throws Exception {

        System.out.println("Available starting points in MapFile and in GameMap");
        System.out.println();

        /* Use the first player to compare building points for */
        Player player = map.getPlayers().getFirst();

        if (debug) {
            System.out.println("Starting point for player from MapFile: " + new Point(mapFile.getGamePointStartingPoints().getFirst()));
            System.out.println("Starting point for player from GameMap: " + map.getStartingPoints().getFirst());
        }

        /* Place a headquarters for the player to get the game to calculate available buildings points within the
        * border
        * */
        map.placeBuilding(new Headquarter(player), map.getStartingPoints().getFirst());

        /* Compare the available building points calculated in the game with the corresponding points in the MapFile */
        Map<Point, Size> availablePoints = map.getAvailableHousePoints(player);

        Map<Point, AvailableBuildingComparison> matched    = new HashMap<>();
        Map<Point, AvailableBuildingComparison> mismatched = new HashMap<>();

        for (Map.Entry<Point, Size> entry : availablePoints.entrySet()) {
            Point point = entry.getKey();
            Size size = entry.getValue();

            MapFilePoint mapFilePoint = mapFile.getMapFilePoint(point);

            if (!availablePoints.containsKey(point)) {
                continue;
            }

            AvailableBuildingComparison comparison = new AvailableBuildingComparison(
                    size,
                    map.isAvailableFlagPoint(player, point),
                    mapFilePoint.getBuildableSite());

            /* Collect matches and mismatches */
            if (comparison.matches()) {
                matched.put(point, comparison);
            } else {
                mismatched.put(point, comparison);
            }

        }

        /* Print the matches */
        System.out.println();
        System.out.println("Matches: ");

        for (Map.Entry<Point, AvailableBuildingComparison> pointAndComparison : matched.entrySet()) {
            Point point = pointAndComparison.getKey();
            AvailableBuildingComparison comparison = pointAndComparison.getValue();

            System.out.println(" - " + point + ": " + comparison.getAvailableInMap() + ", " + comparison.getAvailableInFile());
        }

        /* Print the point that didn't match */
        Headquarter headquarter = getHeadquarterForPlayer(player);

        System.out.println();
        System.out.println("Mismatched: ");

        int filtered = 0;

        for (Map.Entry<Point, AvailableBuildingComparison> pointAndComparison : mismatched.entrySet()) {
            Point point = pointAndComparison.getKey();
            AvailableBuildingComparison comparison = pointAndComparison.getValue();

            int distanceToBorder = getDistanceToBorder(point, player);
            int distanceToHeadquarter = distanceInGame(point, headquarter.getPosition());

            /* Filter comparisons where the point is too close to the border or the headquarters */
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

            System.out.println(" - " + point + " - game: " + comparison.availableInGame + ", file: " + comparison.getAvailableInFile() +
                    ", distance to border: " + distanceToBorder +
                    ", distance to headquarter: " + distanceToHeadquarter +
                    ", height differences: " + (spot.getHeight() - spotLeft.getHeight()) +
                    ", " + (spot.getHeight() - spotUpLeft.getHeight()) +
                    ", " + (spot.getHeight() - spotUpRight.getHeight()) +
                    ", " + (spot.getHeight() - spotRight.getHeight()) +
                    ", " + (spot.getHeight() - spotDownRight.getHeight()) +
                    ", " + (spot.getHeight() - spotDownLeft.getHeight()));

            if (comparison.getAvailableInFile() == BuildableSite.OCCUPIED_BY_TREE) {

                if (!map.isTreeAtPoint(point)) {
                    System.out.println("   -- Tree in file but not on map: " + point);
                } else {
                    System.out.println("   -- Tree availableInGame on map: " /*+ map.getTreeAtPoint(point)*/);
                }
            }
        }

        System.out.println();
        System.out.println("Comparison summary:");
        System.out.println(" - Matching: " + matched.size());
        System.out.println(" - Mismatched: " + mismatched.size());
        System.out.println(" - Filtered: " + filtered);
    }

    private boolean isComparisonUnreliable(int distanceToBorder, int distanceToHeadquarter) {
        return distanceToBorder < 4 || distanceToHeadquarter < 4;
    }

    /**
     * Renders the MapFile instance to a String array. If highlights are included they will be shown on top of the
     * rendered map
     *
     * @param mapFile
     * @param highlights
     * @return
     */
    private String[][] renderMapFileToStringArray(MapFile mapFile, List<Point> highlights) {
        int maxWidth = mapFile.getWidth() * 2 + 2;
        int maxHeight = mapFile.getHeight() + 1;

        if (debug) {
            System.out.println("Width: " + maxWidth + ", height: " + maxHeight);
        }

        String[][] bfr = new String[maxHeight][maxWidth * 2];

        for (MapFilePoint mapFilePoint : mapFile.getMapFilePoints()) {

            int x = mapFilePoint.getGamePointPosition().x;
            int y = mapFilePoint.getGamePointPosition().y;

            /* Skip points that will not appear on screen */
            if (x >= maxWidth || y >= maxHeight) {
                continue;
            }

            /* Draw water */
            if (Texture.isWater(mapFilePoint.getVegetationBelow()) && y > 0) {
                bfr[y - 1][x] = " ";
            } else if (y > 0 && bfr[y - 1][x] == null) {
                bfr[y - 1][x] = ".";
            }

            if (y > 0 && x < maxWidth - 2 && Texture.isWater(mapFilePoint.getVegetationDownRight())) {
                bfr[y - 1][x + 1] = " ";
            } else if (y > 0 && x < maxWidth - 2 && bfr[y - 1][x + 1] == null) {
                bfr[y - 1][x + 1] = ".";
            }

            /* Place stones */
            if (mapFilePoint.hasStone()) {
                bfr[y][x] = "O";
            }

            /* Place trees */
            if (mapFilePoint.hasTree()) {
                bfr[y][x] = "T";
            }
        }

        if (highlights != null) {
            for (java.awt.Point highlight : highlights) {
                bfr[highlight.y - 1][highlight.x - 1] = "*";
                bfr[highlight.y - 1][highlight.x + 1] = "*";
                bfr[highlight.y + 1][highlight.x - 1] = "*";
                bfr[highlight.y + 1][highlight.x + 1] = "*";
                bfr[highlight.y + 1][highlight.x] = "*";
                bfr[highlight.y - 1][highlight.x] = "*";
                bfr[highlight.y][highlight.x - 1] = "*";
                bfr[highlight.y][highlight.x + 1] = "*";

                bfr[highlight.y][highlight.x] = "X";
            }
        }

        return bfr;
    }

    private static class AvailableBuildingComparison {
        private final BuildableSite availableInFile;
        private final Size          availableInGame;
        private final boolean       availableFlagInGame;

        public AvailableBuildingComparison(Size availableInGame, boolean availableFlagInGame, BuildableSite availableInFile) {
            this.availableInGame     = availableInGame;
            this.availableFlagInGame = availableFlagInGame;
            this.availableInFile     = availableInFile;
        }

        public boolean matches() {
            return (availableInGame == Size.LARGE  && availableInFile == BuildableSite.CASTLE) ||
                   (availableInGame == Size.MEDIUM && availableInFile == BuildableSite.HOUSE)  ||
                   (availableInGame == Size.SMALL  && availableInFile == BuildableSite.HUT)    ||
                   (availableFlagInGame && availableInGame == null &&
                            (availableInFile == BuildableSite.FLAG ||
                             availableInFile == BuildableSite.FLAG_NEXT_TO_INACCESSIBLE_TERRAIN));
        }

        public BuildableSite getAvailableInFile() {
            return availableInFile;
        }

        public Size getAvailableInMap() {
            return availableInGame;
        }
    }

    private <T> void incrementInMap(Map<T, Integer> map, T item) {
        int amount = map.getOrDefault(item, 0);

        map.put(item, amount + 1);
    }

    private enum InformationType {
        POSSIBLE_SHIPYARD, DEAD_TREE;

        public static InformationType fromString(String informationAroundType) {
            if (informationAroundType.equals("dead-tree")) {
                return InformationType.DEAD_TREE;
            }

            return null;
        }
    }
}
