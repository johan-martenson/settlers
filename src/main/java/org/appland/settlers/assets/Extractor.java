package org.appland.settlers.assets;

import org.appland.settlers.assets.collectors.AnimalImageCollection;
import org.appland.settlers.assets.collectors.BorderImageCollector;
import org.appland.settlers.assets.collectors.BuildingsImageCollection;
import org.appland.settlers.assets.collectors.CargoImageCollection;
import org.appland.settlers.assets.collectors.CropImageCollection;
import org.appland.settlers.assets.collectors.DecorativeImageCollection;
import org.appland.settlers.assets.collectors.FireImageCollection;
import org.appland.settlers.assets.collectors.FlagImageCollection;
import org.appland.settlers.assets.collectors.RoadBuildingImageCollection;
import org.appland.settlers.assets.collectors.ShipImageCollection;
import org.appland.settlers.assets.collectors.SignImageCollection;
import org.appland.settlers.assets.collectors.StonesImageCollection;
import org.appland.settlers.assets.collectors.TreeImageCollection;
import org.appland.settlers.assets.collectors.UIElementsImageCollection;
import org.appland.settlers.assets.collectors.WorkerImageCollection;
import org.appland.settlers.assets.gamefiles.AfrZLst;
import org.appland.settlers.assets.gamefiles.BootBobsLst;
import org.appland.settlers.assets.gamefiles.CarrierBob;
import org.appland.settlers.assets.gamefiles.JapZLst;
import org.appland.settlers.assets.gamefiles.JobsBob;
import org.appland.settlers.assets.gamefiles.Map0ZLst;
import org.appland.settlers.assets.gamefiles.MapBobs0Lst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.gamefiles.RomBobsLst;
import org.appland.settlers.assets.gamefiles.RomYLst;
import org.appland.settlers.assets.gamefiles.RomZLst;
import org.appland.settlers.assets.gamefiles.SoundLst;
import org.appland.settlers.assets.gamefiles.Tex5Lbm;
import org.appland.settlers.assets.gamefiles.Tex7Lbm;
import org.appland.settlers.assets.gamefiles.VikZLst;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.FlagType;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.WorkerAction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.appland.settlers.assets.BodyType.FAT;
import static org.appland.settlers.assets.BodyType.THIN;
import static org.appland.settlers.assets.CompassDirection.EAST;
import static org.appland.settlers.assets.CompassDirection.NORTH_EAST;
import static org.appland.settlers.assets.CompassDirection.NORTH_WEST;
import static org.appland.settlers.assets.CompassDirection.SOUTH_EAST;
import static org.appland.settlers.assets.CompassDirection.SOUTH_WEST;
import static org.appland.settlers.assets.CompassDirection.WEST;
import static org.appland.settlers.assets.Nation.AFRICANS;
import static org.appland.settlers.assets.Nation.JAPANESE;
import static org.appland.settlers.assets.Nation.ROMANS;
import static org.appland.settlers.assets.Nation.VIKINGS;
import static org.appland.settlers.model.Material.AXE;
import static org.appland.settlers.model.Material.BEER;
import static org.appland.settlers.model.Material.BOAT;
import static org.appland.settlers.model.Material.BOW;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.CLEAVER;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.CRUCIBLE;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.FISHING_ROD;
import static org.appland.settlers.model.Material.FLOUR;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.HAMMER;
import static org.appland.settlers.model.Material.IRON;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PICK_AXE;
import static org.appland.settlers.model.Material.PIG;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.ROLLING_PIN;
import static org.appland.settlers.model.Material.SAW;
import static org.appland.settlers.model.Material.SCYTHE;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.SHOVEL;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.TONGS;
import static org.appland.settlers.model.Material.WATER;
import static org.appland.settlers.model.Material.WHEAT;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.WorkerAction.CHEW_GUM;
import static org.appland.settlers.model.WorkerAction.JUMP_SKIP_ROPE;
import static org.appland.settlers.model.WorkerAction.READ_NEWSPAPER;
import static org.appland.settlers.model.WorkerAction.SIT_DOWN;
import static org.appland.settlers.model.WorkerAction.TOUCH_NOSE;

public class Extractor {

    private static final String DEFAULT_PALETTE = "/home/johan/projects/settlers/src/main/resources/default-palette.act";

    private static final String ROMAN_BUILDINGS_DIRECTORY = "roman-buildings";
    private static final String UI_ELEMENTS_DIRECTORY = "ui-elements";
    private static final String NATURE_DIRECTORY = "nature";
    private static final String SIGNS_DIRECTORY = "signs";
    private static final String TERRAIN_SUB_DIRECTORY = "terrain";
    private static final String GREENLAND_DIRECTORY = "greenland";
    private static final String WINTER_DIRECTORY = "winter";
    private static final String SONG_0_FILENAME = "audio/song0.mp3";
    private static final String SONG_1_FILENAME = "audio/song1.mp3";
    private static final String SONG_0_TITLE = "Song 1";
    private static final String SONG_1_TITLE = "Song 2";

    @Option(name = "--from-dir", usage = "Asset directory to load from")
    static String fromDir;

    @Option(name = "--to-dir", usage = "Directory to extract assets into")
    static String toDir;

    private final AssetManager assetManager;
    private Palette defaultPalette;

    public static void main(String[] args) throws IOException, InvalidHeaderException, InvalidFormatException, UnknownResourceTypeException, CmdLineException {

        Extractor extractor = new Extractor();

        CmdLineParser parser = new CmdLineParser(extractor);

        parser.parseArgument(args);

        if (!Utils.isDirectory(toDir) || !Utils.isEmptyDirectory(toDir)) {
            System.out.println("Must specify an empty directory to extract assets into: " + toDir);
        }

        /* Get the default palette */
        extractor.loadDefaultPalette();

        /* Extract assets */
        extractor.populateRomanBuildings(fromDir, toDir);

        extractor.populateNatureAndUIElements(fromDir, toDir);

        extractor.populateWorkers(fromDir, toDir);

        extractor.populateFlags(fromDir, toDir);

        extractor.populateBorders(fromDir, toDir);

        extractor.populateShips(fromDir, toDir);

        extractor.populateAudio(fromDir, toDir);
    }

    private void populateAudio(String fromDir, String toDir) throws IOException, InvalidFormatException, UnknownResourceTypeException, InvalidHeaderException {

        // Write the music atlas
        JSONArray jsonSongs = new JSONArray();

        JSONObject jsonSong0 = new JSONObject();
        JSONObject jsonSong1 = new JSONObject();

        jsonSong0.put("path", toDir + "/" + SONG_0_FILENAME);
        jsonSong0.put("title", SONG_0_TITLE);
        jsonSong1.put("path", toDir + "/" + SONG_1_FILENAME);
        jsonSong1.put("title", SONG_1_TITLE);

        jsonSongs.add(jsonSong0);
        jsonSongs.add(jsonSong1);

        // Write the audio atlas for the music
        Files.writeString(Paths.get(toDir, "audio-atlas-music.json"), jsonSongs.toJSONString());

        Files.createDirectory(Paths.get(toDir, "audio"));

        // The music files are converted from XMI to MP3s outside this tool. Tell the user where to place them:
        System.out.println("ACTION: place the music files in audio/song[0-9].mp3");

        // Extract each individual sound
        assetManager.debug = true;

        List<GameResource> gameResources = assetManager.loadLstFile(fromDir + "/" + SoundLst.FILENAME, defaultPalette);

        for (GameResource gameResource : gameResources) {
            System.out.println(gameResource);
        }

        for (int i = 0; i < gameResources.size(); i++) {
            GameResource gameResource = gameResources.get(i);

            if (gameResource.getType() == GameResourceType.WAVE_SOUND) {
                WaveGameResource waveGameResource = (WaveGameResource) gameResource;
                WaveFile waveFile1 = waveGameResource.getWaveFile();

                waveFile1.writeToFile(toDir + "/wavefile-" + i + ".wave");
            }
        }

        // Write sounds
        Map<Integer, String> sounds = new HashMap<>();

        sounds.put(SoundLst.DUCK_QUACK, toDir + "/audio-duck-quack.wave");
        sounds.put(SoundLst.GEOLOGIST_FOUND_ORE, toDir + "/audio-geologist-finding.wave");
        sounds.put(SoundLst.MILITARY_BUILDING_OCCUPIED, toDir + "/audio-new-message.wave");
        sounds.put(SoundLst.NEW_MESSAGE, toDir + "/audio-new-message.wave");


        for (Entry<Integer, String> entry : sounds.entrySet()) {
            int index = entry.getKey();
            String path = entry.getValue();

            ((WaveGameResource) gameResources.get(index)).getWaveFile().writeToFile(path);
        }
    }

    private void populateShips(String fromDir, String toDir) throws UnknownResourceTypeException, IOException, InvalidHeaderException, InvalidFormatException {
        List<GameResource> bootBobsLst = assetManager.loadLstFile(fromDir + "/" + BootBobsLst.FILENAME, defaultPalette);

        ShipImageCollection shipImageCollection = new ShipImageCollection();

        shipImageCollection.addShipImageWithShadow(
                EAST,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_EAST),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_EAST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                SOUTH_EAST,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_SOUTH_EAST),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_SOUTH_EAST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                SOUTH_WEST,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_SOUTH_WEST),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_SOUTH_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                WEST,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_WEST),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                NORTH_WEST,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_NORTH_WEST),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_NORTH_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                NORTH_EAST,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_NORTH_EAST),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_NORTH_EAST_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.JUST_STARTED,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_JUST_STARTED),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_JUST_STARTED_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.HALF_WAY,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_HALF_WAY),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_HALF_WAY_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.ALMOST_DONE,
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_ALMOST_DONE),
                getImageFromResourceLocation(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_ALMOST_DONE_SHADOW)
        );

        shipImageCollection.writeImageAtlas(toDir, defaultPalette);
    }

    private void populateBorders(String fromDir, String toDir) throws UnknownResourceTypeException, IOException, InvalidHeaderException, InvalidFormatException {
        List<GameResource> afrBobsLst = assetManager.loadLstFile(fromDir + "/DATA/MBOB/AFR_BOBS.LST", defaultPalette);
        List<GameResource> japBobsLst = assetManager.loadLstFile(fromDir + "/DATA/MBOB/JAP_BOBS.LST", defaultPalette);
        List<GameResource> romBobsLst = assetManager.loadLstFile(fromDir + "/DATA/MBOB/ROM_BOBS.LST", defaultPalette);
        List<GameResource> vikBobsLst = assetManager.loadLstFile(fromDir + "/DATA/MBOB/VIK_BOBS.LST", defaultPalette);

        BorderImageCollector borderImageCollector = new BorderImageCollector();

        borderImageCollector.addLandBorderImage(AFRICANS, getImageFromResourceLocation(afrBobsLst, RomBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(AFRICANS, getImageFromResourceLocation(afrBobsLst, RomBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addLandBorderImage(JAPANESE, getImageFromResourceLocation(japBobsLst, RomBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(JAPANESE, getImageFromResourceLocation(japBobsLst, RomBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addLandBorderImage(ROMANS, getImageFromResourceLocation(romBobsLst, RomBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(ROMANS, getImageFromResourceLocation(romBobsLst, RomBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addLandBorderImage(VIKINGS, getImageFromResourceLocation(vikBobsLst, RomBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(VIKINGS, getImageFromResourceLocation(vikBobsLst, RomBobsLst.COAST_BORDER_ICON));

        borderImageCollector.writeImageAtlas(toDir, defaultPalette);
    }

    private void populateFlags(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, InvalidHeaderException, IOException {

        List<GameResource> afrZLst = assetManager.loadLstFile(fromDir + "/" + AfrZLst.FILENAME, defaultPalette);
        List<GameResource> japZLst = assetManager.loadLstFile(fromDir + "/" + JapZLst.FILENAME, defaultPalette);
        List<GameResource> romZLst = assetManager.loadLstFile(fromDir + "/" + RomZLst.FILENAME, defaultPalette);
        List<GameResource> vikZLst = assetManager.loadLstFile(fromDir + "/" + VikZLst.FILENAME, defaultPalette);

        FlagImageCollection flagImageCollection = new FlagImageCollection();

        // Africans
        flagImageCollection.addImagesForFlag(AFRICANS, FlagType.NORMAL, getImagesFromGameResource(afrZLst, AfrZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, FlagType.NORMAL, getImagesFromGameResource(afrZLst, AfrZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(AFRICANS, FlagType.MAIN, getImagesFromGameResource(afrZLst, AfrZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, FlagType.MAIN, getImagesFromGameResource(afrZLst, AfrZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(AFRICANS, FlagType.MARINE, getImagesFromGameResource(afrZLst, AfrZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, FlagType.MARINE, getImagesFromGameResource(afrZLst, AfrZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Japanese
        flagImageCollection.addImagesForFlag(JAPANESE, FlagType.NORMAL, getImagesFromGameResource(japZLst, JapZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, FlagType.NORMAL, getImagesFromGameResource(japZLst, JapZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(JAPANESE, FlagType.MAIN, getImagesFromGameResource(japZLst, JapZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, FlagType.MAIN, getImagesFromGameResource(japZLst, JapZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(JAPANESE, FlagType.MARINE, getImagesFromGameResource(japZLst, JapZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, FlagType.MARINE, getImagesFromGameResource(japZLst, JapZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Romans
        flagImageCollection.addImagesForFlag(ROMANS, FlagType.NORMAL, getImagesFromGameResource(romZLst, RomZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, FlagType.NORMAL, getImagesFromGameResource(romZLst, RomZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(ROMANS, FlagType.MAIN, getImagesFromGameResource(romZLst, RomZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, FlagType.MAIN, getImagesFromGameResource(romZLst, RomZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(ROMANS, FlagType.MARINE, getImagesFromGameResource(romZLst, RomZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, FlagType.MARINE, getImagesFromGameResource(romZLst, RomZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Vikings
        flagImageCollection.addImagesForFlag(VIKINGS, FlagType.NORMAL, getImagesFromGameResource(vikZLst, VikZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, FlagType.NORMAL, getImagesFromGameResource(vikZLst, VikZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(VIKINGS, FlagType.MAIN, getImagesFromGameResource(vikZLst, VikZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, FlagType.MAIN, getImagesFromGameResource(vikZLst, VikZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(VIKINGS, FlagType.MARINE, getImagesFromGameResource(vikZLst, VikZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, FlagType.MARINE, getImagesFromGameResource(vikZLst, VikZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Write the image atlas to file
        flagImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
    }

    private void populateWorkers(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, InvalidHeaderException, IOException {

        /* Load worker image parts */
        List<GameResource> jobsBobList = assetManager.loadLstFile(fromDir + "/" + JobsBob.FILENAME, defaultPalette);
        List<GameResource> map0ZLst = assetManager.loadLstFile(fromDir + "/" + Map0ZLst.FILENAME, defaultPalette);
        List<GameResource> romBobsLst = assetManager.loadLstFile(fromDir + "/" + RomBobsLst.FILENAME, defaultPalette);

        if (jobsBobList.size() != 1) {
            throw new RuntimeException("Wrong size of game resources in bob file. Must be 1, but was: " + jobsBobList.size());
        }

        if (! (jobsBobList.get(0) instanceof BobGameResource)) {
            throw new RuntimeException("Element must be Bob game resource. Was: " + jobsBobList.get(0).getClass().getName());
        }

        BobGameResource jobsBobGameResource = (BobGameResource) jobsBobList.get(0);

        /* Construct the worker details map */
        Map<JobType, WorkerDetails> workerDetailsMap = new EnumMap<>(JobType.class);

        // FIXME: assume RANGER == FORESTER

        /*
        * Translate ids:
        *  - 0 (Africans) -> 3
        *  - 1 (Japanese) -> 2
        *  - 2 (Romans)   -> 0
        *  - 3 (Vikings)  -> 1
        * */

        workerDetailsMap.put(JobType.HELPER, new WorkerDetails(false, JobsBob.HELPER_BOB_ID));
        workerDetailsMap.put(JobType.WOODCUTTER, JobsBob.WOODCUTTER_BOB);
        workerDetailsMap.put(JobType.FISHER, new WorkerDetails(false, JobsBob.FISHERMAN_BOB_ID));
        workerDetailsMap.put(JobType.FORESTER, new WorkerDetails(false, JobsBob.FORESTER_BOB_ID));
        workerDetailsMap.put(JobType.CARPENTER, new WorkerDetails(false, JobsBob.CARPENTER_BOB_ID));
        workerDetailsMap.put(JobType.STONEMASON, new WorkerDetails(false, JobsBob.STONEMASON_BOB_ID));
        workerDetailsMap.put(JobType.HUNTER, new WorkerDetails(false, JobsBob.HUNTER_BOB_ID));
        workerDetailsMap.put(JobType.FARMER, new WorkerDetails(false, JobsBob.FARMER_BOB_ID));
        workerDetailsMap.put(JobType.MILLER, new WorkerDetails(true, JobsBob.MILLER_BOB_ID));
        workerDetailsMap.put(JobType.BAKER, new WorkerDetails(true, JobsBob.BAKER_BOB_ID));
        workerDetailsMap.put(JobType.BUTCHER, new WorkerDetails(false, JobsBob.BUTCHER_BOB_ID));
        workerDetailsMap.put(JobType.MINER, new WorkerDetails(false, JobsBob.MINER_BOB_ID));
        workerDetailsMap.put(JobType.BREWER, new WorkerDetails(true, JobsBob.BREWER_BOB_ID));
        workerDetailsMap.put(JobType.PIG_BREEDER, new WorkerDetails(false, JobsBob.PIG_BREEDER_BOB_ID));
        workerDetailsMap.put(JobType.DONKEY_BREEDER, new WorkerDetails(false, JobsBob.DONKEY_BREEDER_BOB_ID));
        workerDetailsMap.put(JobType.IRON_FOUNDER, new WorkerDetails(false, JobsBob.IRON_FOUNDER_BOB_ID));
        workerDetailsMap.put(JobType.MINTER, new WorkerDetails(false, JobsBob.MINTER_BOB_ID));
        workerDetailsMap.put(JobType.METALWORKER, new WorkerDetails(false, JobsBob.METALWORKER_BOB_ID));
        workerDetailsMap.put(JobType.ARMORER, new WorkerDetails(true, JobsBob.ARMORER_BOB_ID));
        workerDetailsMap.put(JobType.BUILDER, new WorkerDetails(false, JobsBob.BUILDER_BOB_ID));
        workerDetailsMap.put(JobType.PLANER, new WorkerDetails(false, JobsBob.PLANER_BOB_ID));
        workerDetailsMap.put(JobType.PRIVATE, new WorkerDetails(false, JobsBob.PRIVATE_BOB_ID));
        workerDetailsMap.put(JobType.PRIVATE_FIRST_CLASS, new WorkerDetails(false, JobsBob.PRIVATE_FIRST_CLASS_BOB_ID));
        workerDetailsMap.put(JobType.SERGEANT, new WorkerDetails(false, JobsBob.SERGEANT_BOB_ID));
        workerDetailsMap.put(JobType.OFFICER, new WorkerDetails(false, JobsBob.OFFICER_BOB_ID));
        workerDetailsMap.put(JobType.GENERAL, new WorkerDetails(false, JobsBob.GENERAL_BOB_ID));
        workerDetailsMap.put(JobType.GEOLOGIST, new WorkerDetails(false, JobsBob.GEOLOGIST_BOB_ID));
        workerDetailsMap.put(JobType.SHIP_WRIGHT, new WorkerDetails(false, JobsBob.SHIP_WRIGHT_BOB_ID));
        workerDetailsMap.put(JobType.SCOUT, new WorkerDetails(false, JobsBob.SCOUT_BOB_ID));
        workerDetailsMap.put(JobType.PACK_DONKEY, new WorkerDetails(false, JobsBob.PACK_DONKEY_BOB_ID));
        workerDetailsMap.put(JobType.BOAT_CARRIER, new WorkerDetails(false, JobsBob.BOAT_CARRIER_BOB_ID));
        workerDetailsMap.put(JobType.CHAR_BURNER, new WorkerDetails(false, JobsBob.CHAR_BURNER_BOB_ID));

        /* Composite the worker images and animations */
        Map<JobType, RenderedWorker> renderedWorkers = assetManager.renderWorkerImages(jobsBobGameResource.getBob(), workerDetailsMap);
        Map<JobType, WorkerImageCollection> workerImageCollectors = new EnumMap<>(JobType.class);

        for (JobType jobType : JobType.values()) {
            RenderedWorker renderedWorker = renderedWorkers.get(jobType);

            WorkerImageCollection workerImageCollection = new WorkerImageCollection(jobType.name().toLowerCase());

            for (Nation nation : Nation.values()) {
                for (CompassDirection compassDirection : CompassDirection.values()) {

                    StackedBitmaps[] stackedBitmaps = renderedWorker.getAnimation(nation, compassDirection);

                    if (stackedBitmaps == null) {
                        System.out.println("Stacked bitmaps is null");
                        System.out.println(jobType);
                        System.out.println(nation);
                        System.out.println(compassDirection);
                    }

                    for (StackedBitmaps frame : stackedBitmaps) {
                        PlayerBitmap body = frame.getBitmaps().get(0);
                        PlayerBitmap head = frame.getBitmaps().get(1);

                        /* Calculate the dimension */
                        Point maxOrigin = new Point(0, 0);

                        if (!frame.getBitmaps().isEmpty()) {

                            maxOrigin.x = Integer.MIN_VALUE;
                            maxOrigin.y = Integer.MIN_VALUE;

                            Point maxPosition = maxOrigin;

                            boolean hasPlayer = false;

                            for (Bitmap bitmap : frame.getBitmaps()) {

                                if (bitmap instanceof PlayerBitmap) {
                                    hasPlayer = true;
                                }

                                Area bitmapVisibleArea = bitmap.getVisibleArea();
                                Point bitmapOrigin = bitmap.getOrigin();

                                maxOrigin.x = Math.max(maxOrigin.x, bitmapOrigin.x);
                                maxOrigin.y = Math.max(maxOrigin.y, bitmapOrigin.y);

                                maxPosition.x = Math.max(maxPosition.x, bitmapVisibleArea.width - bitmapOrigin.x);
                                maxPosition.y = Math.max(maxPosition.y, bitmapVisibleArea.height - bitmapOrigin.y);
                            }

                            /* Create a bitmap to merge both body and head into */
                            Bitmap merged = new Bitmap(maxOrigin.x + maxPosition.x, maxOrigin.y + maxPosition.y, defaultPalette, TextureFormat.BGRA);

                            merged.setNx(maxOrigin.x);
                            merged.setNy(maxOrigin.y);

                            /* Draw the body */
                            Area bodyVisibleArea = body.getVisibleArea();

                            Point bodyToUpperleft = new Point(maxOrigin.x - body.getOrigin().x, maxOrigin.y - body.getOrigin().y);
                            Point bodyFromUpperLeft = bodyVisibleArea.getUpperLeftCoordinate();

                            merged.copyNonTransparentPixels(body, bodyToUpperleft, bodyFromUpperLeft, bodyVisibleArea.getDimension());

                            /* Draw the head */
                            Area headVisibleArea = head.getVisibleArea();

                            Point headToUpperLeft = new Point(maxOrigin.x - head.getOrigin().x, maxOrigin.y - head.getOrigin().y);
                            Point headFromUpperLeft = headVisibleArea.getUpperLeftCoordinate();

                            merged.copyNonTransparentPixels(head, headToUpperLeft, headFromUpperLeft, headVisibleArea.getDimension());

                            /* Store the image in the worker image collection */
                            workerImageCollection.addNationSpecificFullImage(nation, compassDirection, merged);
                        }
                    }
                }
            }

            workerImageCollection.addShadowImages(EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(SOUTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(SOUTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(NORTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(NORTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

            // Store the worker image collector
            workerImageCollectors.put(jobType, workerImageCollection);
        }

        // Add cargo carrying images
        WorkerImageCollection woodcutterImageCollector = workerImageCollectors.get(JobType.WOODCUTTER);
        WorkerImageCollection carpenterImageCollector = workerImageCollectors.get(JobType.CARPENTER);
        WorkerImageCollection fishermanImageCollector = workerImageCollectors.get(JobType.FISHER);
        WorkerImageCollection stonemasonImageCollector = workerImageCollectors.get(JobType.STONEMASON);
        WorkerImageCollection minterImageCollector = workerImageCollectors.get(JobType.MINTER);
        WorkerImageCollection minerImageCollector = workerImageCollectors.get(JobType.MINER);
        WorkerImageCollection farmerImageCollector = workerImageCollectors.get(JobType.FARMER);
        WorkerImageCollection pigBreederImageCollector = workerImageCollectors.get(JobType.PIG_BREEDER);
        WorkerImageCollection millerImageCollector = workerImageCollectors.get(JobType.MILLER);
        WorkerImageCollection bakerImageCollector = workerImageCollectors.get(JobType.BAKER);
        WorkerImageCollection metalWorkerImageCollector = workerImageCollectors.get(JobType.METALWORKER);
        WorkerImageCollection hunterWorkerImageCollector = workerImageCollectors.get(JobType.HUNTER);
        WorkerImageCollection shipwrightWorkerImageCollector = workerImageCollectors.get(JobType.SHIP_WRIGHT);
        WorkerImageCollection brewerWorkerImageCollector = workerImageCollectors.get(JobType.BREWER);
        WorkerImageCollection foresterWorkerImageCollector = workerImageCollectors.get(JobType.FORESTER);
        WorkerImageCollection planerWorkerImageCollector = workerImageCollectors.get(JobType.PLANER);
        WorkerImageCollection geologistWorkerImageCollector = workerImageCollectors.get(JobType.GEOLOGIST);
        WorkerImageCollection builderWorkerImageCollector = workerImageCollectors.get(JobType.BUILDER);

        Bob bob = ((BobGameResource) jobsBobList.get(0)).getBob();

        woodcutterImageCollector.readCargoImagesFromBob(
                WOOD,
                JobsBob.WOODCUTTER_BOB.getBodyType(),
                JobsBob.WOODCUTTER_WITH_WOOD_CARGO_BOB_ID,
                bob
        );

        woodcutterImageCollector.addWorkAnimation(WorkerAction.CUTTING, getImagesFromGameResource(romBobsLst, RomBobsLst.CUTTING, 8));

        carpenterImageCollector.readCargoImagesFromBob(
                PLANK,
                JobsBob.CARPENTER_BOB.getBodyType(),
                JobsBob.CARPENTER_WITH_PLANK_BOB_ID,
                bob
        );

        carpenterImageCollector.addWorkAnimation(WorkerAction.SAWING, getImagesFromGameResource(romBobsLst, RomBobsLst.SAWING, 6));

        stonemasonImageCollector.readCargoImagesFromBob(
                STONE,
                JobsBob.STONEMASON_BOB.getBodyType(),
                JobsBob.STONEMASON_WITH_STONE_CARGO_BOB_ID,
                bob
        );

        stonemasonImageCollector.addWorkAnimation(WorkerAction.HACKING_STONE, getImagesFromGameResource(romBobsLst, RomBobsLst.HACKING_STONE, 8));

        foresterWorkerImageCollector.addWorkAnimation(WorkerAction.PLANTING_TREE, getImagesFromGameResource(romBobsLst, RomBobsLst.DIGGING_AND_PLANTING, 36));

        planerWorkerImageCollector.addWorkAnimation(WorkerAction.DIGGING_AND_STOMPING, getImagesFromGameResource(romBobsLst, RomBobsLst.DIGGING_AND_STOMPING, 26));

        geologistWorkerImageCollector.addWorkAnimation(WorkerAction.INVESTIGATING, getImagesFromGameResource(romBobsLst, RomBobsLst.INVESTIGATING, 16));

        builderWorkerImageCollector.addWorkAnimation(WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW, getImagesFromGameResource(romBobsLst, RomBobsLst.HAMMERING_HOUSE_HIGH_AND_LOW, 8));
        builderWorkerImageCollector.addWorkAnimation(WorkerAction.INSPECTING_HOUSE_CONSTRUCTION, getImagesFromGameResource(romBobsLst, RomBobsLst.INSPECTING_HOUSE_CONSTRUCTION, 4));

        minterImageCollector.readCargoImagesFromBob(
                COIN,
                JobsBob.MINTER_BOB.getBodyType(),
                JobsBob.MINTER_WITH_COIN_CARGO_BOB_ID,
                bob
        );

        // TODO: add work animation for minter

        minerImageCollector.readCargoImagesFromBob(
                GOLD,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_GOLD_CARGO_BOB_ID,
                bob
        );

        minerImageCollector.readCargoImagesFromBob(
                IRON,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_IRON_CARGO_BOB_ID,
                bob
        );

        minerImageCollector.readCargoImagesFromBob(
                COAL,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_COAL_CARGO_BOB_ID,
                bob
        );

        minerImageCollector.readCargoImagesFromBob(
                STONE,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_STONE_CARGO_BOB_ID,
                bob
        );

        // TODO: add work animation for miner

        // TODO: job id 69 == carrying crucible/anvil?

        fishermanImageCollector.readCargoImagesFromBob(
                FISH,
                JobsBob.FISHERMAN_BOB.getBodyType(),
                JobsBob.FISHERMAN_WITH_FISH_CARGO_BOB_ID,
                bob
        );

        // Lower fishing rod
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                EAST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.LOWERING_FISHING_ROD_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                SOUTH_EAST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.LOWERING_FISHING_ROD_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                SOUTH_WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.LOWERING_FISHING_ROD_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.LOWERING_FISHING_ROD_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.LOWERING_FISHING_ROD_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.LOWERING_FISHING_ROD_NORTH_EAST, 8));

        // Keep fishing
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                EAST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.FISHING_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                SOUTH_EAST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.FISHING_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                SOUTH_WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.FISHING_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.FISHING_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.FISHING_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.FISHING_NORTH_EAST, 8));

        // Pull up fish
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                EAST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.PULL_UP_FISH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                SOUTH_EAST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.PULL_UP_FISH_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                SOUTH_WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.PULL_UP_FISH_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.PULL_UP_FISH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.PULL_UP_FISH_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesFromGameResource(romBobsLst, RomBobsLst.PULL_UP_FISH_NORTH_EAST, 8));

        farmerImageCollector.readCargoImagesFromBob(
                WHEAT,
                JobsBob.FARMER_BOB.getBodyType(),
                JobsBob.FARMER_WITH_WHEAT_CARGO_BOB_ID,
                bob
        );

        farmerImageCollector.addWorkAnimation(WorkerAction.PLANTING_WHEAT, getImagesFromGameResource(romBobsLst, RomBobsLst.SOWING, 8));
        farmerImageCollector.addWorkAnimation(WorkerAction.HARVESTING, getImagesFromGameResource(romBobsLst, RomBobsLst.HARVESTING, 8));

        pigBreederImageCollector.readCargoImagesFromBob(
                PIG,
                JobsBob.PIG_BREEDER_BOB.getBodyType(),
                JobsBob.PIG_BREEDER_WITH_PIG_CARGO_BOB_ID,
                bob
        );

        millerImageCollector.readCargoImagesFromBob(
                FLOUR,
                JobsBob.MILLER_BOB.getBodyType(),
                JobsBob.MILLER_WITH_FLOUR_CARGO_BOB_ID,
                bob
        );

        bakerImageCollector.readCargoImagesFromBob(
                BREAD,
                JobsBob.BAKER_BOB.getBodyType(),
                JobsBob.BAKER_WITH_BREAD_CARGO_BOB_ID,
                bob
        );

        bakerImageCollector.addWorkAnimation(WorkerAction.BAKING, getImagesFromGameResource(romBobsLst, RomBobsLst.BAKING, 8));

        // TODO: Handle brewer and/or well worker

        brewerWorkerImageCollector.addWorkAnimation(WorkerAction.DRINKING_BEER, getImagesFromGameResource(romBobsLst, RomBobsLst.DRINKING_BEER, 8));

        // TODO: Handle metalworker carrying "shift gear". Assume it's tongs

        metalWorkerImageCollector.readCargoImagesFromBob(
                TONGS,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_TONGS_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                HAMMER,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_HAMMER_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                AXE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_AXE_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                PICK_AXE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_PICK_AXE_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                SHOVEL,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_SHOVEL_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                CRUCIBLE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_CRUCIBLE_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                FISHING_ROD,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_FISHING_ROD_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                SCYTHE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_SCYTHE_CARGO_BOB_ID,
                bob
        );

        // TODO: bucket

        metalWorkerImageCollector.readCargoImagesFromBob(
                CLEAVER,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_CLEAVER_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                ROLLING_PIN,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_ROLLING_PIN_CARGO_BOB_ID,
                bob
        );

        // TODO: Is 2330-2335 a saw or a bow?
        metalWorkerImageCollector.readCargoImagesFromBob(
                SAW,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_SAW_CARGO_BOB_ID,
                bob
        );

        hunterWorkerImageCollector.readCargoImagesFromBob(
                MEAT,
                JobsBob.HUNTER_BOB.getBodyType(),
                JobsBob.HUNTER_WITH_MEAT_CARGO_BOB_ID,
                bob
        );

        hunterWorkerImageCollector.addWorkAnimation(WorkerAction.SHOOTING, getImagesFromGameResource(romBobsLst, RomBobsLst.HUNTING, 13));
        hunterWorkerImageCollector.addWorkAnimation(WorkerAction.PICKING_UP_MEAT, getImagesFromGameResource(romBobsLst, RomBobsLst.PICKING_UP_MEAT, 12));

        shipwrightWorkerImageCollector.readCargoImagesFromBob(
                BOAT,
                JobsBob.SHIPWRIGHT_BOB.getBodyType(),
                JobsBob.SHIPWRIGHT_WITH_BOAT_CARGO_BOB_ID,
                bob
        );

        shipwrightWorkerImageCollector.readCargoImagesFromBob(
                PLANK,
                JobsBob.SHIPWRIGHT_BOB.getBodyType(),
                JobsBob.SHIPWRIGHT_WITH_PLANK_CARGO_BOB_ID,
                bob
        );

        // Write each worker image collection to file
        for (WorkerImageCollection workerImageCollection : workerImageCollectors.values()) {
            workerImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
        }

        // Extract couriers
        Bob jobsBob = jobsBobGameResource.getBob();
        Bob carrierBob = assetManager.loadBobFile(fromDir + "/" + CarrierBob.FILENAME, defaultPalette);

        WorkerImageCollection thinCarrier = new WorkerImageCollection("thin-carrier-no-cargo");
        WorkerImageCollection fatCarrier = new WorkerImageCollection("fat-carrier-no-cargo");
        WorkerImageCollection thinCarrierWithCargo = new WorkerImageCollection("thin-carrier-with-cargo");
        WorkerImageCollection fatCarrierWithCargo = new WorkerImageCollection("fat-carrier-with-cargo");

        // Read body images
        thinCarrier.readBodyImagesFromBob(THIN, jobsBob);
        fatCarrier.readBodyImagesFromBob(FAT, jobsBob);
        thinCarrierWithCargo.readBodyImagesFromBob(THIN, carrierBob);
        fatCarrierWithCargo.readBodyImagesFromBob(FAT, carrierBob);

        // Read walking images without cargo
        thinCarrier.readHeadImagesWithoutCargoFromBob(THIN, JobsBob.HELPER_BOB_ID, jobsBob);
        fatCarrier.readHeadImagesWithoutCargoFromBob(FAT, JobsBob.HELPER_BOB_ID, jobsBob);

        thinCarrier.mergeBodyAndHeadImages(defaultPalette);
        fatCarrier.mergeBodyAndHeadImages(defaultPalette);

        // Read walking animation for each type of cargo
        for (CarrierCargo carrierCargo : CarrierCargo.values()) {
            Material material = CarrierBob.CARGO_BOB_ID_TO_MATERIAL_MAP.get(carrierCargo.ordinal());

            if (material == null) {
                continue;
            }

            thinCarrierWithCargo.readCargoImagesFromBob(material, THIN, carrierCargo.ordinal(), carrierBob);
            fatCarrierWithCargo.readCargoImagesFromBob(material, FAT, carrierCargo.ordinal(), carrierBob);
        }

        thinCarrier.addShadowImages(EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(SOUTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(SOUTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(NORTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(NORTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        fatCarrier.addShadowImages(EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(SOUTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(SOUTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(NORTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(NORTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        thinCarrierWithCargo.addShadowImages(EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(SOUTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(SOUTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(NORTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(NORTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        fatCarrierWithCargo.addShadowImages(EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(SOUTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(SOUTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(NORTH_WEST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(NORTH_EAST, getImagesFromGameResource(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        // Add animations for when the couriers are bored
        fatCarrier.addWorkAnimation(CHEW_GUM, getImagesFromGameResource(romBobsLst, RomBobsLst.CHEW_GUM, 8));
        fatCarrier.addWorkAnimation(SIT_DOWN, getImagesFromGameResource(romBobsLst, RomBobsLst.SIT_DOWN, 5));
        thinCarrier.addWorkAnimation(READ_NEWSPAPER, getImagesFromGameResource(romBobsLst, RomBobsLst.READ_NEWSPAPER, 7));
        thinCarrier.addWorkAnimation(TOUCH_NOSE, getImagesFromGameResource(romBobsLst, RomBobsLst.TOUCH_NOSE, 5));
        thinCarrier.addWorkAnimation(JUMP_SKIP_ROPE, getImagesFromGameResource(romBobsLst, RomBobsLst.JUMP_SKIP_ROPE, 7));

        // Write the image atlases to files
        thinCarrier.writeImageAtlas(toDir + "/", defaultPalette);
        fatCarrier.writeImageAtlas(toDir + "/", defaultPalette);
        thinCarrierWithCargo.writeImageAtlas(toDir + "/", defaultPalette);
        fatCarrierWithCargo.writeImageAtlas(toDir + "/", defaultPalette);
    }

    /**
     * TEX5.LBM -- contains vegetation textures
     *
     * @param fromDir
     * @param toDir
     */
    private void populateNatureAndUIElements(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, InvalidHeaderException, IOException {

        /* Load from the map asset file */
        List<GameResource> mapBobsLst = assetManager.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);
        List<GameResource> mapBobs0Lst = assetManager.loadLstFile(fromDir + "/" + MapBobs0Lst.FILENAME, defaultPalette);

        /* Create the out directories */
        String uiDir = toDir + "/" + UI_ELEMENTS_DIRECTORY;
        String natureDir = toDir + "/" + NATURE_DIRECTORY;
        String signDir = toDir + "/" + SIGNS_DIRECTORY;
        String terrainDir = natureDir + "/" + TERRAIN_SUB_DIRECTORY;
        String greenlandDir = terrainDir + "/" + GREENLAND_DIRECTORY;
        String winterDir = terrainDir + "/" + WINTER_DIRECTORY;

        Utils.createDirectory(uiDir);
        Utils.createDirectory(natureDir);
        Utils.createDirectory(natureDir + "/animals");
        Utils.createDirectory(signDir);
        Utils.createDirectory(terrainDir);
        Utils.createDirectory(greenlandDir);
        Utils.createDirectory(winterDir);

        /* Extract the terrains */
        LBMGameResource greenlandGameResource = (LBMGameResource) assetManager.loadLBMFile(fromDir + "/" + Tex5Lbm.FILENAME, defaultPalette);
        LBMGameResource winterGameResource = (LBMGameResource) assetManager.loadLBMFile(fromDir + "/" + Tex7Lbm.FILENAME, defaultPalette);

        Bitmap greenlandTextureBitmap = greenlandGameResource.getLbmFile().getBitmap();
        Bitmap winterTextureBitmap = winterGameResource.getLbmFile().getBitmap();

        greenlandTextureBitmap.writeToFile(greenlandDir + "/greenland-texture.png");
        winterTextureBitmap.writeToFile(winterDir + "/winter-texture.png");

        /* Extract the stones */
        StonesImageCollection stonesImageCollection = new StonesImageCollection();

        stonesImageCollection.addImage(StoneType.TYPE_1, StoneAmount.MINI, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_MINI));
        stonesImageCollection.addShadowImage(StoneType.TYPE_1, StoneAmount.MINI, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_MINI_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_1, StoneAmount.LITTLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE));
        stonesImageCollection.addShadowImage(StoneType.TYPE_1, StoneAmount.LITTLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_1, StoneAmount.LITTLE_MORE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE_MORE));
        stonesImageCollection.addShadowImage(StoneType.TYPE_1, StoneAmount.LITTLE_MORE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE_MORE_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_1, StoneAmount.MIDDLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_MIDDLE));
        stonesImageCollection.addShadowImage(StoneType.TYPE_1, StoneAmount.MIDDLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_MIDDLE_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_1, StoneAmount.ALMOST_FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_ALMOST_FULL));
        stonesImageCollection.addShadowImage(StoneType.TYPE_1, StoneAmount.ALMOST_FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_ALMOST_FULL_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_1, StoneAmount.FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_FULL));
        stonesImageCollection.addShadowImage(StoneType.TYPE_1, StoneAmount.FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_1_FULL_SHADOW));

        stonesImageCollection.addImage(StoneType.TYPE_2, StoneAmount.MINI, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_MINI));
        stonesImageCollection.addShadowImage(StoneType.TYPE_2, StoneAmount.MINI, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_MINI_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_2, StoneAmount.LITTLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE));
        stonesImageCollection.addShadowImage(StoneType.TYPE_2, StoneAmount.LITTLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_2, StoneAmount.LITTLE_MORE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE_MORE));
        stonesImageCollection.addShadowImage(StoneType.TYPE_2, StoneAmount.LITTLE_MORE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE_MORE_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_2, StoneAmount.MIDDLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_MIDDLE));
        stonesImageCollection.addShadowImage(StoneType.TYPE_2, StoneAmount.MIDDLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_MIDDLE_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_2, StoneAmount.ALMOST_FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_ALMOST_FULL));
        stonesImageCollection.addShadowImage(StoneType.TYPE_2, StoneAmount.ALMOST_FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_ALMOST_FULL_SHADOW));
        stonesImageCollection.addImage(StoneType.TYPE_2, StoneAmount.FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_FULL));
        stonesImageCollection.addShadowImage(StoneType.TYPE_2, StoneAmount.FULL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_TYPE_2_FULL_SHADOW));

        stonesImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract UI elements */
        UIElementsImageCollection uiElementsImageCollection = new UIElementsImageCollection();

        uiElementsImageCollection.addSelectedPointImage(getImageFromResourceLocation(mapBobsLst, MapBobsLst.SELECTED_POINT));
        uiElementsImageCollection.addHoverPoint(getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_POINT));
        uiElementsImageCollection.addHoverAvailableFlag(getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_FLAG));
        uiElementsImageCollection.addHoverAvailableMine(getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MINE));
        uiElementsImageCollection.addHoverAvailableBuilding(SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_SMALL_BUILDING));
        uiElementsImageCollection.addHoverAvailableBuilding(MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MEDIUM_BUILDING));
        uiElementsImageCollection.addHoverAvailableBuilding(LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_LARGE_BUILDING));
        uiElementsImageCollection.addHoverAvailableHarbor(getImageFromResourceLocation(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_HARBOR));
        uiElementsImageCollection.addAvailableFlag(getImageFromResourceLocation(mapBobsLst, MapBobsLst.AVAILABLE_FLAG));
        uiElementsImageCollection.addAvailableMine(getImageFromResourceLocation(mapBobsLst, MapBobsLst.AVAILABLE_MINE));
        uiElementsImageCollection.addAvailableBuilding(SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.AVAILABLE_SMALL_BUILDING));
        uiElementsImageCollection.addAvailableBuilding(MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.AVAILABLE_MEDIUM_BUILDING));
        uiElementsImageCollection.addAvailableBuilding(LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.AVAILABLE_LARGE_BUILDING));
        uiElementsImageCollection.addAvailableHarbor(getImageFromResourceLocation(mapBobsLst, MapBobsLst.AVAILABLE_HARBOR));

        uiElementsImageCollection.writeImageAtlas(toDir, defaultPalette);

        /*  Extract the crops */
        CropImageCollection cropImageCollection = new CropImageCollection();

        cropImageCollection.addImage(CropType.TYPE_1, Crop.GrowthState.JUST_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_NEWLY_PLANTED));
        cropImageCollection.addImage(CropType.TYPE_1, Crop.GrowthState.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_LITTLE_GROWTH));
        cropImageCollection.addImage(CropType.TYPE_1, Crop.GrowthState.ALMOST_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_MORE_GROWTH));
        cropImageCollection.addImage(CropType.TYPE_1, Crop.GrowthState.FULL_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_FULLY_GROWN));
        cropImageCollection.addImage(CropType.TYPE_1, Crop.GrowthState.HARVESTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_JUST_HARVESTED));

        cropImageCollection.addImage(CropType.TYPE_2, Crop.GrowthState.JUST_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_NEWLY_PLANTED));
        cropImageCollection.addImage(CropType.TYPE_2, Crop.GrowthState.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_LITTLE_GROWTH));
        cropImageCollection.addImage(CropType.TYPE_2, Crop.GrowthState.ALMOST_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_MORE_GROWTH));
        cropImageCollection.addImage(CropType.TYPE_2, Crop.GrowthState.FULL_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_FULLY_GROWN));
        cropImageCollection.addImage(CropType.TYPE_2, Crop.GrowthState.HARVESTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_JUST_HARVESTED));

        cropImageCollection.addShadowImage(CropType.TYPE_1, Crop.GrowthState.JUST_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_NEWLY_PLANTED_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_1, Crop.GrowthState.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_LITTLE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_1, Crop.GrowthState.ALMOST_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_MORE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_1, Crop.GrowthState.FULL_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_FULLY_GROWN_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_1, Crop.GrowthState.HARVESTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_1_JUST_HARVESTED_SHADOW));

        cropImageCollection.addShadowImage(CropType.TYPE_2, Crop.GrowthState.JUST_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_NEWLY_PLANTED_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_2, Crop.GrowthState.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_LITTLE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_2, Crop.GrowthState.ALMOST_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_MORE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_2, Crop.GrowthState.FULL_GROWN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_FULLY_GROWN_SHADOW));
        cropImageCollection.addShadowImage(CropType.TYPE_2, Crop.GrowthState.HARVESTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CROP_TYPE_2_JUST_HARVESTED_SHADOW));

        cropImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract the cargo images that workers carry */
        CargoImageCollection cargoImageCollection = new CargoImageCollection();

        cargoImageCollection.addCargoImage(BEER, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BEER_CARGO));
        cargoImageCollection.addCargoImage(TONGS, getImageFromResourceLocation(mapBobsLst, MapBobsLst.TONG_CARGO));
        cargoImageCollection.addCargoImage(AXE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.AXE_CARGO));
        cargoImageCollection.addCargoImage(SAW, getImageFromResourceLocation(mapBobsLst, MapBobsLst.SAW_CARGO));
        cargoImageCollection.addCargoImage(PICK_AXE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PICK_AXE_CARGO));
        cargoImageCollection.addCargoImage(SHOVEL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.SHOVEL_CARGO));
        cargoImageCollection.addCargoImage(CRUCIBLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CRUCIBLE_CARGO)); //???
        cargoImageCollection.addCargoImage(FISHING_ROD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FISHING_ROD_CARGO));
        cargoImageCollection.addCargoImage(SCYTHE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.SCYTHE_CARGO));
        // - empty bucket at 904

        cargoImageCollection.addCargoImage(WATER, getImageFromResourceLocation(mapBobsLst, MapBobsLst.WATER_BUCKET_CARGO));
        cargoImageCollection.addCargoImage(CLEAVER, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CLEAVER_CARGO));
        cargoImageCollection.addCargoImage(ROLLING_PIN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROLLING_PIN_CARGO));
        cargoImageCollection.addCargoImage(BOW, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BOW_CARGO));
        cargoImageCollection.addCargoImage(BOAT, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BOAT_CARGO));
        cargoImageCollection.addCargoImage(SWORD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.SWORD_CARGO));
        // - anvil at 911

        cargoImageCollection.addCargoImage(FLOUR, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FLOUR_CARGO));
        cargoImageCollection.addCargoImage(FISH, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FISH_CARGO));
        cargoImageCollection.addCargoImage(BREAD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BREAD_CARGO));
        cargoImageCollection.addCargoImageForNation(ROMANS, SHIELD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROMAN_SHIELD_CARGO));
        cargoImageCollection.addCargoImage(WOOD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.WOOD_CARGO));
        cargoImageCollection.addCargoImage(PLANK, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PLANK_CARGO));
        cargoImageCollection.addCargoImage(STONE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STONE_CARGO));
        cargoImageCollection.addCargoImageForNation(VIKINGS, SHIELD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.VIKING_SHIELD_CARGO));
        cargoImageCollection.addCargoImageForNation(AFRICANS, SHIELD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.AFRICAN_SHIELD_CARGO));
        cargoImageCollection.addCargoImage(WHEAT, getImageFromResourceLocation(mapBobsLst, MapBobsLst.WHEAT_CARGO));
        cargoImageCollection.addCargoImage(COIN, getImageFromResourceLocation(mapBobsLst, MapBobsLst.COIN_CARGO));
        cargoImageCollection.addCargoImage(GOLD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GOLD_CARGO));
        cargoImageCollection.addCargoImage(IRON, getImageFromResourceLocation(mapBobsLst, MapBobsLst.IRON_CARGO));
        cargoImageCollection.addCargoImage(COAL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.COAL_CARGO));
        cargoImageCollection.addCargoImage(MEAT, getImageFromResourceLocation(mapBobsLst, MapBobsLst.MEAT_CARGO));
        cargoImageCollection.addCargoImage(PIG, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PIG_CARGO));
        cargoImageCollection.addCargoImageForNation(JAPANESE, SHIELD, getImageFromResourceLocation(mapBobsLst, MapBobsLst.JAPANESE_SHIELD_CARGO));

        cargoImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract signs */
        SignImageCollection signImageCollection = new SignImageCollection();

        signImageCollection.addImage(SignType.IRON, SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.IRON_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.IRON, MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.IRON_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.IRON, LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.IRON_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.COAL, SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.COAL_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.COAL, MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.COAL_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.COAL, LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.COAL_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.STONE, SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GRANITE_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.STONE, MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GRANITE_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.STONE, LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GRANITE_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.GOLD, SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GOLD_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.GOLD, MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GOLD_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.GOLD, LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.GOLD_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.WATER, LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.WATER_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.NOTHING, LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.NOTHING_SIGN_UP_RIGHT));

        signImageCollection.addShadowImage(getImageFromResourceLocation(mapBobsLst, MapBobsLst.SIGN_SHADOW));

        signImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract road building icons */
        RoadBuildingImageCollection roadBuildingImageCollection = new RoadBuildingImageCollection();

        roadBuildingImageCollection.addStartPointImage(getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_START_POINT));
        roadBuildingImageCollection.addSameLevelConnectionImage(getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_SAME_LEVEL_CONNECTION));

        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.LITTLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_LITTLE_HIGHER_CONNECTION));
        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_MEDIUM_HIGHER_CONNECTION));
        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.HIGH, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_MUCH_HIGHER_CONNECTION));

        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.LITTLE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_LITTLE_LOWER_CONNECTION));
        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_MEDIUM_LOWER_CONNECTION));
        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.HIGH, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ROAD_BUILDING_MUCH_LOWER_CONNECTION));

        roadBuildingImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract fire animation */
        FireImageCollection fireImageCollection = new FireImageCollection();

        fireImageCollection.addImagesForFire(FireSize.MINI, getImagesFromGameResource(mapBobsLst, MapBobsLst.MINI_FIRE_ANIMATION, 8));
        fireImageCollection.addImagesForFireWithShadow(
                FireSize.SMALL,
                getImagesFromGameResource(mapBobsLst, MapBobsLst.SMALL_FIRE_ANIMATION, 8),
                getImagesFromGameResource(mapBobsLst, MapBobsLst.SMALL_FIRE_SHADOW_ANIMATION, 8)
        );
        fireImageCollection.addImagesForFireWithShadow(
                FireSize.MEDIUM,
                getImagesFromGameResource(mapBobsLst, MapBobsLst.MEDIUM_FIRE_ANIMATION, 8),
                getImagesFromGameResource(mapBobsLst, MapBobsLst.MEDIUM_FIRE_SHADOW_ANIMATION, 8)
        );
        fireImageCollection.addImagesForFireWithShadow(
                FireSize.LARGE,
                getImagesFromGameResource(mapBobsLst, MapBobsLst.LARGE_FIRE_ANIMATION, 8),
                getImagesFromGameResource(mapBobsLst, MapBobsLst.LARGE_FIRE_SHADOW_ANIMATION, 8)
        );

        fireImageCollection.addBurntDownImage(SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.SMALL_BURNT_DOWN));
        fireImageCollection.addBurntDownImage(MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.MEDIUM_BURNT_DOWN));
        fireImageCollection.addBurntDownImage(LARGE, getImageFromResourceLocation(mapBobsLst, MapBobsLst.LARGE_BURNT_DOWN));

        fireImageCollection.writeImageAtlas(toDir, defaultPalette);

        // Collect tree images
        TreeImageCollection treeImageCollection = new TreeImageCollection("trees");

        /* Extract animation for tree type 1 in wind -- cypress (?) */
        treeImageCollection.addImagesForTree(Tree.TreeType.CYPRESS, getImagesFromGameResource(mapBobsLst, MapBobsLst.CYPRESS_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.CYPRESS, getImagesFromGameResource(mapBobsLst, MapBobsLst.CYPRESS_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.CYPRESS, getImagesFromGameResource(mapBobsLst, MapBobsLst.CYPRESS_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.CYPRESS, getImagesFromGameResource(mapBobsLst, MapBobsLst.CYPRESS_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CYPRESS_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CYPRESS_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CYPRESS_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_MEDIUM));

        /* Extract animation for tree type 2 in wind -- birch, for sure */
        treeImageCollection.addImagesForTree(Tree.TreeType.BIRCH, getImagesFromGameResource(mapBobsLst, MapBobsLst.BIRCH_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.BIRCH, getImagesFromGameResource(mapBobsLst, MapBobsLst.BIRCH_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.BIRCH, getImagesFromGameResource(mapBobsLst, MapBobsLst.BIRCH_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.BIRCH, getImagesFromGameResource(mapBobsLst, MapBobsLst.BIRCH_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BIRCH_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BIRCH_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BIRCH_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BIRCH_SHADOW_MEDIUM));

        /* Extract animation for tree type 3 in wind -- oak */
        treeImageCollection.addImagesForTree(Tree.TreeType.OAK, getImagesFromGameResource(mapBobsLst, MapBobsLst.OAK_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.OAK, getImagesFromGameResource(mapBobsLst, MapBobsLst.OAK_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.OAK, getImagesFromGameResource(mapBobsLst, MapBobsLst.OAK_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.OAK, getImagesFromGameResource(mapBobsLst, MapBobsLst.OAK_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.OAK_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.OAK_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.OAK_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.OAK_SHADOW_MEDIUM));

        /* Extract animation for tree type 4 in wind -- short palm */
        treeImageCollection.addImagesForTree(Tree.TreeType.PALM_1, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_1_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PALM_1, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_1_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PALM_1, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_1_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PALM_1, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_1_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_1_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_1_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_1_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_1_SHADOW_ALMOST_GROWN));

        /* Extract animation for tree type 5 in wind -- tall palm */
        treeImageCollection.addImagesForTree(Tree.TreeType.PALM_2, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_2_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PALM_2, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_2_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PALM_2, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_2_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PALM_2, getImagesFromGameResource(mapBobsLst, MapBobsLst.PALM_2_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_2_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_2_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_2_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PALM_2_SHADOW_ALMOST_GROWN));

        /* Extract animation for tree type 6 in wind -- fat palm - pine apple */
        treeImageCollection.addImagesForTree(Tree.TreeType.PINE_APPLE, getImagesFromGameResource(mapBobsLst, MapBobsLst.PINE_APPLE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PINE_APPLE, getImagesFromGameResource(mapBobsLst, MapBobsLst.PINE_APPLE_SHADOW_ANIMATION, 8));

        /* Extract animation for tree type 7 in wind -- pine */
        treeImageCollection.addImagesForTree(Tree.TreeType.PINE, getImagesFromGameResource(mapBobsLst, MapBobsLst.PINE_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PINE, getImagesFromGameResource(mapBobsLst, MapBobsLst.PINE_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PINE, getImagesFromGameResource(mapBobsLst, MapBobsLst.PINE_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PINE, getImagesFromGameResource(mapBobsLst, MapBobsLst.PINE_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PINE_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PINE_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PINE_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PINE_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN_SHADOW));

        /* Extract animation for tree type 8 in wind -- cherry */
        treeImageCollection.addImagesForTree(Tree.TreeType.CHERRY, getImagesFromGameResource(mapBobsLst, MapBobsLst.CHERRY_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.CHERRY, getImagesFromGameResource(mapBobsLst, MapBobsLst.CHERRY_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.CHERRY, getImagesFromGameResource(mapBobsLst, MapBobsLst.CHERRY_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.CHERRY, getImagesFromGameResource(mapBobsLst, MapBobsLst.CHERRY_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CHERRY_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CHERRY_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CHERRY_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CHERRY_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN_SHADOW));

        /* Extract animation for tree type 9 in wind -- fir (?) */
        treeImageCollection.addImagesForTree(Tree.TreeType.FIR, getImagesFromGameResource(mapBobsLst, MapBobsLst.FIR_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.FIR, getImagesFromGameResource(mapBobsLst, MapBobsLst.FIR_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.FIR, getImagesFromGameResource(mapBobsLst, MapBobsLst.FIR_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.FIR, getImagesFromGameResource(mapBobsLst, MapBobsLst.FIR_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FIR_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FIR_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, TreeSize.NEWLY_PLANTED, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FIR_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, TreeSize.SMALL, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FIR_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, TreeSize.MEDIUM, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN_SHADOW));

        treeImageCollection.writeImageAtlas(natureDir, defaultPalette);

        /* Extract animal animations */
        AnimalImageCollection iceBearImageCollection = new AnimalImageCollection("ice-bear");
        AnimalImageCollection foxImageCollection = new AnimalImageCollection("fox");
        AnimalImageCollection rabbitImageCollection = new AnimalImageCollection("rabbit");
        AnimalImageCollection stagImageCollection = new AnimalImageCollection("stag");
        AnimalImageCollection deerImageCollection = new AnimalImageCollection("deer");
        AnimalImageCollection sheepImageCollection = new AnimalImageCollection("sheep");
        AnimalImageCollection deer2ImageCollection = new AnimalImageCollection("deer2");
        AnimalImageCollection duckImageCollection = new AnimalImageCollection("duck");

        /* Ice bear */
        iceBearImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_WEST_ANIMATION, 6));
        iceBearImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_WEST_ANIMATION, 6));
        iceBearImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_WEST_ANIMATION, 6));

        /* Fox */
        foxImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_EAST_ANIMATION, 6));
        foxImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.FOX_WALKING_EAST_ANIMATION, 6));
        foxImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_EAST_ANIMATION, 6));
        foxImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_WEST_ANIMATION, 6));
        foxImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.FOX_WALKING_WEST_ANIMATION, 6));
        foxImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_WEST_ANIMATION, 6));

        foxImageCollection.addShadowImage(EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FOX_SHADOW_EAST));
        foxImageCollection.addShadowImage(SOUTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_EAST));
        foxImageCollection.addShadowImage(SOUTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_WEST));
        foxImageCollection.addShadowImage(WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FOX_SHADOW_WEST));
        foxImageCollection.addShadowImage(NORTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_WEST));
        foxImageCollection.addShadowImage(NORTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_EAST));

        /* Rabbit */
        rabbitImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.RABBIT_WALKING_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_WEST_ANIMATION, 6));
        rabbitImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.RABBIT_WALKING_WEST_ANIMATION, 6));
        rabbitImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_WEST_ANIMATION, 6));

        /* Stag */
        stagImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_EAST_ANIMATION, 8));
        stagImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.STAG_WALKING_EAST_ANIMATION, 8));
        stagImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_EAST_ANIMATION, 8));
        stagImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_WEST_ANIMATION, 8));
        stagImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.STAG_WALKING_WEST_ANIMATION, 8));
        stagImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_WEST_ANIMATION, 8));

        stagImageCollection.addShadowImage(EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STAG_SHADOW_EAST));
        stagImageCollection.addShadowImage(SOUTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_EAST));
        stagImageCollection.addShadowImage(SOUTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_WEST));
        stagImageCollection.addShadowImage(WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STAG_SHADOW_WEST));
        stagImageCollection.addShadowImage(NORTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_WEST));
        stagImageCollection.addShadowImage(NORTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_EAST));

        /* Deer */
        deerImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_EAST_ANIMATION, 8));
        deerImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_WALKING_EAST_ANIMATION, 8));
        deerImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_EAST_ANIMATION, 8));
        deerImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_WEST_ANIMATION, 8));
        deerImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_WALKING_WEST_ANIMATION, 8));
        deerImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_WEST_ANIMATION, 8));

        deerImageCollection.addShadowImage(EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_SHADOW_EAST));
        deerImageCollection.addShadowImage(SOUTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_EAST));
        deerImageCollection.addShadowImage(SOUTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_WEST));
        deerImageCollection.addShadowImage(WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_SHADOW_WEST));
        deerImageCollection.addShadowImage(NORTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_WEST));
        deerImageCollection.addShadowImage(NORTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_EAST));

        /* Sheep */
        sheepImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.SHEEP_WALKING_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_WEST_ANIMATION, 2));
        sheepImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.SHEEP_WALKING_WEST_ANIMATION, 2));
        sheepImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_WEST_ANIMATION, 2));

        /* Deer 2 (horse?) */
        deer2ImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_2_WALKING_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_WEST_ANIMATION, 8));
        deer2ImageCollection.addImages(WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_2_WALKING_WEST_ANIMATION, 8));
        deer2ImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_WEST_ANIMATION, 8));

        deer2ImageCollection.addShadowImage(EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_2_SHADOW_EAST));
        deer2ImageCollection.addShadowImage(SOUTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_EAST));
        deer2ImageCollection.addShadowImage(SOUTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_WEST));

        /* Extract duck */
        duckImageCollection.addImage(EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_EAST));
        duckImageCollection.addImage(SOUTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_EAST + 1));
        duckImageCollection.addImage(SOUTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_EAST + 2));
        duckImageCollection.addImage(WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_EAST + 3));
        duckImageCollection.addImage(NORTH_WEST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_EAST + 4));
        duckImageCollection.addImage(NORTH_EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_EAST + 5));

        duckImageCollection.addShadowImage(EAST, getImageFromResourceLocation(mapBobsLst, MapBobsLst.DUCK_SHADOW));

        iceBearImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        foxImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        rabbitImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        stagImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        deerImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        sheepImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        deer2ImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);
        duckImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);

        /* Extract the donkey */
        AnimalImageCollection donkeyImageCollection = new AnimalImageCollection("donkey");

        donkeyImageCollection.addImages(EAST, getImagesFromGameResource(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_ANIMATION, 8));
        donkeyImageCollection.addImages(SOUTH_EAST, getImagesFromGameResource(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_ANIMATION, 8));
        donkeyImageCollection.addImages(SOUTH_WEST, getImagesFromGameResource(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(WEST, getImagesFromGameResource(mapBobs0Lst, MapBobs0Lst.DONKEY_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(NORTH_WEST, getImagesFromGameResource(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(NORTH_EAST, getImagesFromGameResource(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_EAST_ANIMATION, 8));

        donkeyImageCollection.addShadowImage(EAST, getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_SHADOW));
        donkeyImageCollection.addShadowImage(SOUTH_EAST, getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_SHADOW));
        donkeyImageCollection.addShadowImage(SOUTH_WEST, getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_SHADOW));

        donkeyImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);

        /*  Extract decorative elements */
        DecorativeImageCollection decorativeImageCollection = new DecorativeImageCollection();

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_BROWN_MUSHROOM,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.MINI_BROWN_MUSHROOM),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.MINI_BROWN_MUSHROOM_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.TOADSTOOL,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MUSHROOM),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MUSHROOM_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_STONE,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONE),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONE_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_STONE,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONES),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.STONE,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_STONE),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_STONE_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.DEAD_TREE_LYING_DOWN,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_FALLEN_TREE),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_FALLEN_TREE_SHADOW));
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.DEAD_TREE,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_STANDING_DEAD_TREE),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_STANDING_DEAD_TREE_SHADOW)
                );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SKELETON,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_SKELETON),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_SKELETON_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_SKELETON,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_SKELETON),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_SKELETON_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.FLOWERS,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_FLOWERS),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_FLOWERS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.LARGE_BUSH,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_LARGE_BUSH),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_LARGE_BUSH_SHADOW)
        );

        // pile of stones

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.LARGER_STONES,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_LARGER_STONES),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_LARGER_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.CACTUS_1,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_1),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_1_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.CACTUS_2,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_2),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_2_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.CATTAIL,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_BEACH_GRASS),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_BEACH_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.GRASS_1,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_GRASS),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.BUSH,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_BUSH),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_BUSH,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_BUSH),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_BUSH,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_BUSH),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.GRASS_2,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_GRASS_2),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_GRASS_2_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_GRASS,
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_GRASS),
                getImageFromResourceLocation(mapBobsLst, MapBobsLst.DECORATIVE_MINI_GRASS_SHADOW)
        );

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.PORTAL,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.PORTAL),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.PORTAL_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SHINING_PORTAL,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SHINING_PORTAL),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SHINING_PORTAL_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.BROWN_MUSHROOM,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.BROWN_MUSHROOM),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.BROWN_MUSHROOM_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_STONE_WITH_GRASS,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.MINI_STONE_WITH_GRASS),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.MINI_STONE_WITH_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_STONE_WITH_GRASS,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SMALL_STONE_WITH_GRASS),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SMALL_STONE_WITH_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SOME_SMALL_STONES,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SOME_SMALL_STONES),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SOME_SMALL_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SOME_SMALLER_STONES,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SOME_SMALLER_STONES),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SOME_SMALLER_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.FEW_SMALL_STONES,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.FEW_SMALL_STONES),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.FEW_SMALL_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SPARSE_BUSH,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SPARSE_BUSH),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SPARSE_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SOME_WATER,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SOME_WATER),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SOME_WATER_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.LITTLE_GRASS,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.LITTLE_GRASS),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.LITTLE_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SNOWMAN,
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SNOWMAN),
                getImageFromResourceLocation(mapBobs0Lst, MapBobs0Lst.SNOWMAN_SHADOW)
        );

        decorativeImageCollection.writeImageAtlas(toDir, defaultPalette);
    }

    private void loadDefaultPalette() throws IOException {
        defaultPalette = assetManager.loadPaletteFromFile(DEFAULT_PALETTE);
    }

    Extractor() {
        assetManager = new AssetManager();
    }

    /**
     * Layout of data in ROM_Y.LST
     *
     *
     * 0 ...
     * 4-11        Flag animation
     * 12-19       Corresponding shadows
     * 20-27       Main road flag animation
     * 28-35       Corresponding shadows
     * 36-43       Sea flag animation
     * 44-51       Corresponding flag animation
     * 51-59       ??
     * 60          Headquarter
     * 61          ??
     * 62          Headquarter open door
     * 63          Barracks
     * 64          ??
     * 65          Barracks under construction
     * 66          ??
     * 67          Barracks open door
     * 68          Guardhouse
     * 69          ??
     * 70          Guardhouse under construction
     * 71          ??
     * 72          Guardhouse open door
     * 73          Watch tower
     * 74          ??
     * 75          Watch tower under construction
     * 76          Watch tower under construction shadow (??)
     * 77          Watch tower open door
     * 78          Fortress
     * 79          ??
     * 80          Fortress under construction
     * 81          Fortress under construction shadow (??)
     * 82          Fortress open door
     * 83          Granite mine
     * 84          Granite mine shadow (??)
     * 85          Granite mine under construction
     * 86          Granite mine under construction shadow (??)
     * 87          Coal mine
     * 88          Coal mine shadow (??)
     * 89          Coal mine under construction
     * 90          Coal mine under construction shadow (??)
     * 91          Iron mine
     * 92          Iron mine shadow (??)
     * 93          Iron mine under construction
     * 94          Iron mine under construction shadow (??)
     * 95          Gold mine
     * 96          Gold mine shadow (??)
     * 97          Gold mine under construction
     * 98          Gold mine under construction shadow (??)
     * 99          Lookout tower
     * 100         Lookout tower shadow (??)
     * 101         Lookout tower under construction
     * 102         Lookout tower under construction shadow (??)
     * 103         Lookout tower open door
     * 104         Catapult
     * 105         Catapult shadow (??)
     * 106         Catapult under construction
     * 107         Catapult open door
     * 108         Woodcutter
     * 109         Woodcutter shadow (??)
     * 110         Woodcutter under construction
     * 111         Woodcutter under construction shadow (??)
     * 112         Woodcutter open door
     * 113         Fishery
     * 114         Fishery shadow
     * 115         Fishery under construction
     * 116         Fishery under construction shadow
     * 117         Fishery open door
     * 118         Quarry
     * 119         Quarry shadow
     * 120         Quarry under construction
     * 121         Quarry under construction shadow
     * 122         Quarry open door
     * 123         Forester hut
     * 124         Forester hut shadow
     * 125         Forester hut under construction
     * 126         Forester hut under construction shadow
     * 127         Forester hut open door
     * 128         Slaughter house
     * 129         Slaughter house shadow
     * 130         Slaughter house under construction
     * 131         Slaughter house under construction shadow
     * 132         Slaughter house open door
     * 133         Hunter hut
     * 134         Hunter hut shadow
     * 135         Hunter hut under construction
     * 136         Hunter hut under construction shadow
     * 137         Hunter hut open door
     * 138         Brewery
     * 139         Brewery shadow
     * 140         Brewery under construction
     * 141         Brewery under construction shadow
     * 142         Brewery open door
     * 143         Armory
     * 144         Armory shadow
     * 145         Armory under construction
     * 146         Armory under construction shadow
     * 147         Armory open door
     * 148         Metalworks
     * 149         Metalworks shadow
     * 150         Metalworks under construction
     * 151         Metalworks under construction shadow
     * 152         Metalworks open door
     * 153         Iron Smelter
     * 154         Iron Smelter shadow
     * 155         Iron Smelter under construction
     * 156         Iron Smelter under construction shadow
     * 157         Iron Smelter open door
     * 158         Pig farm
     * 159         Pig farm shadow
     * 160         Pig farm under construction
     * 161         Pig farm under construction shadow
     * 162         Pig farm open door
     * 163         Store house
     * 164         Store house shadow
     * 165         Store house under construction
     * 166         Store house under construction shadow
     * 167         Store house open door
     * 168         Mill - no fan
     * 169         Mill - no fan shadow
     * 170         Mill - no fan under construction
     * 171         Mill - no fan under construction shadow
     * 172         Mill - open door
     * 173         Bakery
     * 174         Bakery shadow
     * 175         Bakery under construction
     * 176         Bakery under construction shadow
     * 177         Bakery open door
     * 178         Sawmill
     * 179         Sawmill shadow
     * 180         Sawmill under construction
     * 181         Sawmill under construction shadow
     * 182         Sawmill open door
     * 183         Mint
     * 184         Mint shadow
     * 185         Mint under construction
     * 186         Mint under construction shadow
     * 187         Mint open door
     * 188         Well
     * 189         Well shadow
     * 190         Well under construction
     * 191         Well under construction shadow
     * 192         Well open door
     * 193         Shipyard
     * 194         Shipyard shadow
     * 195         Shipyard under construction
     * 196         Shipyard under construction shadow
     * 197         Shipyard open door
     * 198         Farm
     * 199         Farm shadow
     * 200         Farm under construction
     * 201         Farm under construction shadow
     * 202         Farm open door
     * 203         Donkey breeder
     * 204         Donkey breeder shadow
     * 205         Donkey breeder under construction
     * 206         Donkey breeder under construction shadow
     * 207         Donkey breeder open door
     * 208         Harbor
     * 209         Harbor shadow
     * 210         Harbor under construction
     * 211         Harbor under construction shadow
     * 212         Construction planned sign
     * 213         Construction planned sign shadow
     * 214         Construction just started
     * 215         Construction just started shadow
     * 216         Mill fan not spinning
     * 217-223     Pairs of mill fan+shadow
     * 224-227     Unknown fire
     *
     *
     *
     *
     * @param fromDir
     * @param toDir
     * @throws InvalidFormatException
     * @throws UnknownResourceTypeException
     * @throws InvalidHeaderException
     * @throws IOException
     */
    private void populateRomanBuildings(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, InvalidHeaderException, IOException {

        /* Load from the roman asset file */
        List<GameResource> romYLst = assetManager.loadLstFile(fromDir + "/" + RomYLst.FILENAME, defaultPalette);

        /* Create the roman buildings directory */
        Utils.createDirectory(toDir + "/" + ROMAN_BUILDINGS_DIRECTORY);

        Map<Integer, String> imagesToFileMap = new HashMap<>();

        String buildingsDir = toDir + "/" + ROMAN_BUILDINGS_DIRECTORY;

        /* Write the buildings to the out directory */
        imagesToFileMap.put(RomYLst.HEADQUARTER, buildingsDir + "/headquarter.png");
        imagesToFileMap.put(RomYLst.BARRACKS, buildingsDir + "/barracks.png");
        imagesToFileMap.put(RomYLst.BARRACKS + 2, buildingsDir + "/barracks-under-construction.png");
        imagesToFileMap.put(RomYLst.GUARDHOUSE, buildingsDir + "/guardhouse.png");
        imagesToFileMap.put(RomYLst.GUARDHOUSE + 2, buildingsDir + "/guardhouse-under-construction.png");
        imagesToFileMap.put(RomYLst.WATCHTOWER, buildingsDir + "/watchtower.png");
        imagesToFileMap.put(RomYLst.WATCHTOWER + 2, buildingsDir + "/watchtower-under-construction.png");
        imagesToFileMap.put(RomYLst.FORTRESS, buildingsDir + "/fortress.png");
        imagesToFileMap.put(RomYLst.FORTRESS + 2, buildingsDir + "/fortress-under-construction.png");
        imagesToFileMap.put(RomYLst.GRANITE_MINE, buildingsDir + "/granite-mine.png");
        imagesToFileMap.put(RomYLst.GRANITE_MINE + 2, buildingsDir + "/granite-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.COAL_MINE, buildingsDir + "/coal-mine.png");
        imagesToFileMap.put(RomYLst.COAL_MINE + 2, buildingsDir + "/coal-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.IRON_MINE_RESOURCE, buildingsDir + "/iron-mine.png");
        imagesToFileMap.put(RomYLst.IRON_MINE_RESOURCE + 2, buildingsDir + "/iron-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.GOLD_MINE, buildingsDir + "/gold-mine.png");
        imagesToFileMap.put(RomYLst.GOLD_MINE + 2, buildingsDir + "/gold-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.LOOKOUT_TOWER, buildingsDir + "/lookout-tower.png");
        imagesToFileMap.put(RomYLst.LOOKOUT_TOWER + 2, buildingsDir + "/lookout-tower-under-construction.png");
        imagesToFileMap.put(RomYLst.CATAPULT, buildingsDir + "/catapult.png");
        imagesToFileMap.put(RomYLst.CATAPULT + 2, buildingsDir + "/catapult-under-construction.png");
        imagesToFileMap.put(RomYLst.WOODCUTTER, buildingsDir + "/woodcutter.png");
        imagesToFileMap.put(RomYLst.WOODCUTTER + 2, buildingsDir + "/woodcutter-under-construction.png");
        imagesToFileMap.put(RomYLst.FISHERY, buildingsDir + "/fishery.png");
        imagesToFileMap.put(RomYLst.FISHERY + 2, buildingsDir + "/fishery-under-construction.png");
        imagesToFileMap.put(RomYLst.QUARRY, buildingsDir + "/quarry.png");
        imagesToFileMap.put(RomYLst.QUARRY + 2, buildingsDir + "/quarry-under-construction.png");
        imagesToFileMap.put(RomYLst.FORESTER_HUT, buildingsDir + "/forester-hut.png");
        imagesToFileMap.put(RomYLst.FORESTER_HUT + 2, buildingsDir + "/forester-hut-under-construction.png");
        imagesToFileMap.put(RomYLst.SLAUGHTER_HOUSE, buildingsDir + "/slaughter-house.png");
        imagesToFileMap.put(RomYLst.SLAUGHTER_HOUSE + 2, buildingsDir + "/slaughter-house-under-construction.png");
        imagesToFileMap.put(RomYLst.HUNTER_HUT, buildingsDir + "/hunter-hut.png");
        imagesToFileMap.put(RomYLst.HUNTER_HUT + 2, buildingsDir + "/hunter-hut-under-construction.png");
        imagesToFileMap.put(RomYLst.BREWERY, buildingsDir + "/brewery.png");
        imagesToFileMap.put(RomYLst.BREWERY + 2, buildingsDir + "/brewery-under-construction.png");
        imagesToFileMap.put(RomYLst.ARMORY, buildingsDir + "/armory.png");
        imagesToFileMap.put(RomYLst.ARMORY + 2, buildingsDir + "/armory-under-construction.png");
        imagesToFileMap.put(RomYLst.METALWORKS, buildingsDir + "/metalworks.png");
        imagesToFileMap.put(RomYLst.METALWORKS + 2, buildingsDir + "/metalworks-under-construction.png");
        imagesToFileMap.put(RomYLst.IRON_SMELTER, buildingsDir + "/iron-smelter.png");
        imagesToFileMap.put(RomYLst.IRON_SMELTER + 2, buildingsDir + "/iron-smelter-under-construction.png");
        imagesToFileMap.put(RomYLst.PIG_FARM, buildingsDir + "/pig-farm.png");
        imagesToFileMap.put(RomYLst.PIG_FARM + 2, buildingsDir + "/pig-farm-under-construction.png");
        imagesToFileMap.put(RomYLst.STOREHOUSE, buildingsDir + "/storehouse.png");
        imagesToFileMap.put(RomYLst.STOREHOUSE + 2, buildingsDir + "/storehouse-under-construction.png");
        imagesToFileMap.put(RomYLst.MILL_NO, buildingsDir + "/mill-no-fan.png");
        imagesToFileMap.put(RomYLst.MILL_NO + 2, buildingsDir + "/mill-no-fan-under-construction.png");
        imagesToFileMap.put(RomYLst.BAKERY, buildingsDir + "/bakery.png");
        imagesToFileMap.put(RomYLst.BAKERY + 2, buildingsDir + "/bakery-under-construction.png");
        imagesToFileMap.put(RomYLst.SAWMILL, buildingsDir + "/sawmill.png");
        imagesToFileMap.put(RomYLst.SAWMILL + 2, buildingsDir + "/sawmill-under-construction.png");
        imagesToFileMap.put(RomYLst.MINT, buildingsDir + "/mint.png");
        imagesToFileMap.put(RomYLst.MINT + 2, buildingsDir + "/mint-under-construction.png");
        imagesToFileMap.put(RomYLst.WELL, buildingsDir + "/well.png");
        imagesToFileMap.put(RomYLst.WELL + 2, buildingsDir + "/well-under-construction.png");
        imagesToFileMap.put(RomYLst.SHIPYARD, buildingsDir + "/shipyard.png");
        imagesToFileMap.put(RomYLst.SHIPYARD + 2, buildingsDir + "/shipyard-under-construction.png");
        imagesToFileMap.put(RomYLst.FARM, buildingsDir + "/farm.png");
        imagesToFileMap.put(RomYLst.FARM + 2, buildingsDir + "/farm-under-construction.png");
        imagesToFileMap.put(RomYLst.DONKEY_BREEDER, buildingsDir + "/donkey-breeder.png");
        imagesToFileMap.put(RomYLst.DONKEY_BREEDER + 2, buildingsDir + "/donkey-breeder-under-construction.png");
        imagesToFileMap.put(RomYLst.HARBOR, buildingsDir + "/harbor.png");
        imagesToFileMap.put(RomYLst.HARBOR + 2, buildingsDir + "/harbor-under-construction.png");
        imagesToFileMap.put(RomYLst.CONSTRUCTION_PLANNED, buildingsDir + "/construction-planned-sign.png");
        imagesToFileMap.put(RomYLst.CONSTRUCTION_JUST_STARTED_INDEX, buildingsDir + "/construction-started-sign.png");

        writeFilesFromMap(romYLst, imagesToFileMap);

        // Create the image atlas
        Map<Nation, String> nationsAndBobFiles = new EnumMap<>(Nation.class);

        nationsAndBobFiles.put(ROMANS, "DATA/MBOB/ROM_Y.LST");
        nationsAndBobFiles.put(JAPANESE, "DATA/MBOB/JAP_Y.LST");
        nationsAndBobFiles.put(AFRICANS, "DATA/MBOB/AFR_Y.LST");
        nationsAndBobFiles.put(VIKINGS, "DATA/MBOB/VIK_Y.LST");

        BuildingsImageCollection buildingsImageCollection = new BuildingsImageCollection();

        for (Entry<Nation, String> entry : nationsAndBobFiles.entrySet()) {
            Nation nation = entry.getKey();
            String filename = fromDir + "/" + entry.getValue();

            List<GameResource> nationResourceList = assetManager.loadLstFile(filename, defaultPalette);

            buildingsImageCollection.addBuildingForNation(nation, "Headquarter", getImageFromResourceLocation(nationResourceList, RomYLst.HEADQUARTER));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Headquarter", getImageFromResourceLocation(nationResourceList, RomYLst.HEADQUARTER_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Barracks", getImageFromResourceLocation(nationResourceList, RomYLst.BARRACKS));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Barracks", getImageFromResourceLocation(nationResourceList, RomYLst.BARRACKS_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Barracks", getImageFromResourceLocation(nationResourceList, RomYLst.BARRACKS + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Barracks", getImageFromResourceLocation(nationResourceList, RomYLst.BARRACKS_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "GuardHouse", getImageFromResourceLocation(nationResourceList, RomYLst.GUARDHOUSE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "GuardHouse", getImageFromResourceLocation(nationResourceList, RomYLst.GUARDHOUSE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "GuardHouse", getImageFromResourceLocation(nationResourceList, RomYLst.GUARDHOUSE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "GuardHouse", getImageFromResourceLocation(nationResourceList, RomYLst.GUARDHOUSE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "WatchTower", getImageFromResourceLocation(nationResourceList, RomYLst.WATCHTOWER));
            buildingsImageCollection.addBuildingShadowForNation(nation, "WatchTower", getImageFromResourceLocation(nationResourceList, RomYLst.WATCHTOWER_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "WatchTower", getImageFromResourceLocation(nationResourceList, RomYLst.WATCHTOWER + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "WatchTower", getImageFromResourceLocation(nationResourceList, RomYLst.WATCHTOWER_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Fortress", getImageFromResourceLocation(nationResourceList, RomYLst.FORTRESS));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Fortress", getImageFromResourceLocation(nationResourceList, RomYLst.FORTRESS_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Fortress", getImageFromResourceLocation(nationResourceList, RomYLst.FORTRESS + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Fortress", getImageFromResourceLocation(nationResourceList, RomYLst.FORTRESS_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "GraniteMine", getImageFromResourceLocation(nationResourceList, RomYLst.GRANITE_MINE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "GraniteMine", getImageFromResourceLocation(nationResourceList, RomYLst.GRANITE_MINE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "GraniteMine", getImageFromResourceLocation(nationResourceList, RomYLst.GRANITE_MINE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "GraniteMine", getImageFromResourceLocation(nationResourceList, RomYLst.GRANITE_MINE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "CoalMine", getImageFromResourceLocation(nationResourceList, RomYLst.COAL_MINE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "CoalMine", getImageFromResourceLocation(nationResourceList, RomYLst.COAL_MINE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "CoalMine", getImageFromResourceLocation(nationResourceList, RomYLst.COAL_MINE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "CoalMine", getImageFromResourceLocation(nationResourceList, RomYLst.COAL_MINE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "IronMine", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_MINE_RESOURCE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "IronMine", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_MINE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "IronMine", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_MINE_RESOURCE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "IronMine", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_MINE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "GoldMine", getImageFromResourceLocation(nationResourceList, RomYLst.GOLD_MINE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "GoldMine", getImageFromResourceLocation(nationResourceList, RomYLst.GOLD_MINE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "GoldMine", getImageFromResourceLocation(nationResourceList, RomYLst.GOLD_MINE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "GoldMine", getImageFromResourceLocation(nationResourceList, RomYLst.GOLD_MINE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "LookoutTower", getImageFromResourceLocation(nationResourceList, RomYLst.LOOKOUT_TOWER));
            buildingsImageCollection.addBuildingShadowForNation(nation, "LookoutTower", getImageFromResourceLocation(nationResourceList, RomYLst.LOOKOUT_TOWER_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "LookoutTower", getImageFromResourceLocation(nationResourceList, RomYLst.LOOKOUT_TOWER + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "LookoutTower", getImageFromResourceLocation(nationResourceList, RomYLst.LOOKOUT_TOWER_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Catapult", getImageFromResourceLocation(nationResourceList, RomYLst.CATAPULT));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Catapult", getImageFromResourceLocation(nationResourceList, RomYLst.CATAPULT_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Catapult", getImageFromResourceLocation(nationResourceList, RomYLst.CATAPULT + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Catapult", getImageFromResourceLocation(nationResourceList, RomYLst.CATAPULT_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Woodcutter", getImageFromResourceLocation(nationResourceList, RomYLst.WOODCUTTER));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Woodcutter", getImageFromResourceLocation(nationResourceList, RomYLst.WOODCUTTER_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Woodcutter", getImageFromResourceLocation(nationResourceList, RomYLst.WOODCUTTER + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Woodcutter", getImageFromResourceLocation(nationResourceList, RomYLst.WOODCUTTER_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Fishery", getImageFromResourceLocation(nationResourceList, RomYLst.FISHERY));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Fishery", getImageFromResourceLocation(nationResourceList, RomYLst.FISHERY_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Fishery", getImageFromResourceLocation(nationResourceList, RomYLst.FISHERY + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Fishery", getImageFromResourceLocation(nationResourceList, RomYLst.FISHERY_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Quarry", getImageFromResourceLocation(nationResourceList, RomYLst.QUARRY));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Quarry", getImageFromResourceLocation(nationResourceList, RomYLst.QUARRY_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Quarry", getImageFromResourceLocation(nationResourceList, RomYLst.QUARRY + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Quarry", getImageFromResourceLocation(nationResourceList, RomYLst.QUARRY_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "ForesterHut", getImageFromResourceLocation(nationResourceList, RomYLst.FORESTER_HUT));
            buildingsImageCollection.addBuildingShadowForNation(nation, "ForesterHut", getImageFromResourceLocation(nationResourceList, RomYLst.FORESTER_HUT_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "ForesterHut", getImageFromResourceLocation(nationResourceList, RomYLst.FORESTER_HUT + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "ForesterHut", getImageFromResourceLocation(nationResourceList, RomYLst.FORESTER_HUT_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "SlaughterHouse", getImageFromResourceLocation(nationResourceList, RomYLst.SLAUGHTER_HOUSE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "SlaughterHouse", getImageFromResourceLocation(nationResourceList, RomYLst.SLAUGHTER_HOUSE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "SlaughterHouse", getImageFromResourceLocation(nationResourceList, RomYLst.SLAUGHTER_HOUSE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "SlaughterHouse", getImageFromResourceLocation(nationResourceList, RomYLst.SLAUGHTER_HOUSE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "HunterHut", getImageFromResourceLocation(nationResourceList, RomYLst.HUNTER_HUT));
            buildingsImageCollection.addBuildingShadowForNation(nation, "HunterHut", getImageFromResourceLocation(nationResourceList, RomYLst.HUNTER_HUT_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "HunterHut", getImageFromResourceLocation(nationResourceList, RomYLst.HUNTER_HUT + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "HunterHut", getImageFromResourceLocation(nationResourceList, RomYLst.HUNTER_HUT_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Brewery", getImageFromResourceLocation(nationResourceList, RomYLst.BREWERY));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Brewery", getImageFromResourceLocation(nationResourceList, RomYLst.BREWERY_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Brewery", getImageFromResourceLocation(nationResourceList, RomYLst.BREWERY + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Brewery", getImageFromResourceLocation(nationResourceList, RomYLst.BREWERY_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Armory", getImageFromResourceLocation(nationResourceList, RomYLst.ARMORY));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Armory", getImageFromResourceLocation(nationResourceList, RomYLst.ARMORY_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Armory", getImageFromResourceLocation(nationResourceList, RomYLst.ARMORY + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Armory", getImageFromResourceLocation(nationResourceList, RomYLst.ARMORY_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Metalworks", getImageFromResourceLocation(nationResourceList, RomYLst.METALWORKS));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Metalworks", getImageFromResourceLocation(nationResourceList, RomYLst.METALWORKS_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Metalworks", getImageFromResourceLocation(nationResourceList, RomYLst.METALWORKS + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Metalworks", getImageFromResourceLocation(nationResourceList, RomYLst.METALWORKS_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "IronSmelter", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_SMELTER));
            buildingsImageCollection.addBuildingShadowForNation(nation, "IronSmelter", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_SMELTER_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "IronSmelter", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_SMELTER + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "IronSmelter", getImageFromResourceLocation(nationResourceList, RomYLst.IRON_SMELTER_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "PigFarm", getImageFromResourceLocation(nationResourceList, RomYLst.PIG_FARM));
            buildingsImageCollection.addBuildingShadowForNation(nation, "PigFarm", getImageFromResourceLocation(nationResourceList, RomYLst.PIG_FARM_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "PigFarm", getImageFromResourceLocation(nationResourceList, RomYLst.PIG_FARM + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "PigFarm", getImageFromResourceLocation(nationResourceList, RomYLst.PIG_FARM_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Storehouse", getImageFromResourceLocation(nationResourceList, RomYLst.STOREHOUSE));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Storehouse", getImageFromResourceLocation(nationResourceList, RomYLst.STOREHOUSE_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Storehouse", getImageFromResourceLocation(nationResourceList, RomYLst.STOREHOUSE + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Storehouse", getImageFromResourceLocation(nationResourceList, RomYLst.STOREHOUSE_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Mill", getImageFromResourceLocation(nationResourceList, RomYLst.MILL_NO));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Mill", getImageFromResourceLocation(nationResourceList, RomYLst.MILL_NO_FAN_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Mill", getImageFromResourceLocation(nationResourceList, RomYLst.MILL_NO + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Mill", getImageFromResourceLocation(nationResourceList, RomYLst.MILL_NO_FAN_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Bakery", getImageFromResourceLocation(nationResourceList, RomYLst.BAKERY));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Bakery", getImageFromResourceLocation(nationResourceList, RomYLst.BAKERY_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Bakery", getImageFromResourceLocation(nationResourceList, RomYLst.BAKERY + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Bakery", getImageFromResourceLocation(nationResourceList, RomYLst.BAKERY_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Sawmill", getImageFromResourceLocation(nationResourceList, RomYLst.SAWMILL));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Sawmill", getImageFromResourceLocation(nationResourceList, RomYLst.SAWMILL_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Sawmill", getImageFromResourceLocation(nationResourceList, RomYLst.SAWMILL + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Sawmill", getImageFromResourceLocation(nationResourceList, RomYLst.SAWMILL_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Mint", getImageFromResourceLocation(nationResourceList, RomYLst.MINT));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Mint", getImageFromResourceLocation(nationResourceList, RomYLst.MINT_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Mint", getImageFromResourceLocation(nationResourceList, RomYLst.MINT + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Mint", getImageFromResourceLocation(nationResourceList, RomYLst.MINT_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Well", getImageFromResourceLocation(nationResourceList, RomYLst.WELL));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Well", getImageFromResourceLocation(nationResourceList, RomYLst.WELL_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Well", getImageFromResourceLocation(nationResourceList, RomYLst.WELL + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Well", getImageFromResourceLocation(nationResourceList, RomYLst.WELL_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Shipyard", getImageFromResourceLocation(nationResourceList, RomYLst.SHIPYARD));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Shipyard", getImageFromResourceLocation(nationResourceList, RomYLst.SHIPYARD_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Shipyard", getImageFromResourceLocation(nationResourceList, RomYLst.SHIPYARD + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Shipyard", getImageFromResourceLocation(nationResourceList, RomYLst.SHIPYARD_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Farm", getImageFromResourceLocation(nationResourceList, RomYLst.FARM));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Farm", getImageFromResourceLocation(nationResourceList, RomYLst.FARM_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Farm", getImageFromResourceLocation(nationResourceList, RomYLst.FARM + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Farm", getImageFromResourceLocation(nationResourceList, RomYLst.FARM_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "DonkeyBreeder", getImageFromResourceLocation(nationResourceList, RomYLst.DONKEY_BREEDER));
            buildingsImageCollection.addBuildingShadowForNation(nation, "DonkeyBreeder", getImageFromResourceLocation(nationResourceList, RomYLst.DONKEY_BREEDER_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "DonkeyBreeder", getImageFromResourceLocation(nationResourceList, RomYLst.DONKEY_BREEDER + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "DonkeyBreeder", getImageFromResourceLocation(nationResourceList, RomYLst.DONKEY_BREEDER_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addBuildingForNation(nation, "Harbor", getImageFromResourceLocation(nationResourceList, RomYLst.HARBOR));
            buildingsImageCollection.addBuildingShadowForNation(nation, "Harbor", getImageFromResourceLocation(nationResourceList, RomYLst.HARBOR_SHADOW));
            buildingsImageCollection.addBuildingUnderConstructionForNation(nation, "Harbor", getImageFromResourceLocation(nationResourceList, RomYLst.HARBOR + 2));
            buildingsImageCollection.addBuildingUnderConstructionShadowForNation(nation, "Harbor", getImageFromResourceLocation(nationResourceList, RomYLst.HARBOR_UNDER_CONSTRUCTION_SHADOW));

            buildingsImageCollection.addConstructionPlanned(nation, getImageFromResourceLocation(nationResourceList, RomYLst.CONSTRUCTION_PLANNED));
            buildingsImageCollection.addConstructionPlannedShadow(nation, getImageFromResourceLocation(nationResourceList, RomYLst.CONSTRUCTION_PLANNED_SHADOW));
            buildingsImageCollection.addConstructionJustStarted(nation, getImageFromResourceLocation(nationResourceList, RomYLst.CONSTRUCTION_JUST_STARTED_INDEX));
            buildingsImageCollection.addConstructionJustStartedShadow(nation, getImageFromResourceLocation(nationResourceList, RomYLst.CONSTRUCTION_JUST_STARTED_SHADOW));
        }

        buildingsImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
    }

    private List<Bitmap> getImagesFromGameResource(List<GameResource> gameResourceList, int startLocation, int amount) {
        List<Bitmap> images = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            images.add(getImageFromResourceLocation(gameResourceList, startLocation + i));
        }

        return images;
    }

    private Bitmap getImageFromResourceLocation(List<GameResource> gameResourceList, int location) {
        GameResource gameResource = gameResourceList.get(location);

        switch (gameResource.getType()) {
            case BITMAP_RLE:
                BitmapRLEResource headquarterRLEBitmapResource = (BitmapRLEResource) gameResource;
                return headquarterRLEBitmapResource.getBitmap();

            case PLAYER_BITMAP_RESOURCE:
                PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResource;
                return playerBitmapResource.getBitmap();

            case BITMAP_RESOURCE:
                BitmapResource bitmapResource = (BitmapResource) gameResource;
                return bitmapResource.getBitmap();

            default:
                throw new RuntimeException("CANNOT HANDLE " + gameResource.getClass());
        }
    }

    private void writeFilesFromMap(List<GameResource> gameResourceList, Map<Integer, String> imagesToFileMap) throws IOException {
        for (Entry<Integer, String> entry : imagesToFileMap.entrySet()) {
            GameResource gameResource = gameResourceList.get(entry.getKey());
            String outFilename = entry.getValue();

            switch (gameResource.getType()) {
                case BITMAP_RLE:
                    BitmapRLEResource headquarterRLEBitmapResource = (BitmapRLEResource) gameResource;
                    headquarterRLEBitmapResource.getBitmap().writeToFile(outFilename);
                    break;

                case PLAYER_BITMAP_RESOURCE:
                    PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResource;
                    playerBitmapResource.getBitmap().writeToFile(outFilename);
                    break;

                case BITMAP_RESOURCE:
                    BitmapResource bitmapResource = (BitmapResource) gameResource;
                    bitmapResource.getBitmap().writeToFile(outFilename);
                    break;

                default:
                    throw new RuntimeException("CANNOT HANDLE " + gameResource.getClass());
            }
        }
    }
}
