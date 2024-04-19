package org.appland.settlers.assets;

import org.appland.settlers.assets.collectors.AnimalImageCollection;
import org.appland.settlers.assets.collectors.BorderImageCollector;
import org.appland.settlers.assets.collectors.CargoImageCollection;
import org.appland.settlers.assets.collectors.CropImageCollection;
import org.appland.settlers.assets.collectors.DecorativeImageCollection;
import org.appland.settlers.assets.collectors.FireImageCollection;
import org.appland.settlers.assets.collectors.FlagImageCollection;
import org.appland.settlers.assets.collectors.InventoryImageCollection;
import org.appland.settlers.assets.collectors.RoadBuildingImageCollection;
import org.appland.settlers.assets.collectors.ShipImageCollection;
import org.appland.settlers.assets.collectors.SignImageCollection;
import org.appland.settlers.assets.collectors.StonesImageCollection;
import org.appland.settlers.assets.collectors.TreeImageCollection;
import org.appland.settlers.assets.collectors.UIElementsImageCollection;
import org.appland.settlers.assets.collectors.WorkerImageCollection;
import org.appland.settlers.assets.decoders.BobDecoder;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.decoders.PaletteDecoder;
import org.appland.settlers.assets.extractors.BuildingsExtractor;
import org.appland.settlers.assets.gamefiles.AfrZLst;
import org.appland.settlers.assets.gamefiles.BootBobsLst;
import org.appland.settlers.assets.gamefiles.CarrierBob;
import org.appland.settlers.assets.gamefiles.CbobRomBobsLst;
import org.appland.settlers.assets.gamefiles.IoLst;
import org.appland.settlers.assets.gamefiles.JapZLst;
import org.appland.settlers.assets.gamefiles.JobsBob;
import org.appland.settlers.assets.gamefiles.Map0ZLst;
import org.appland.settlers.assets.gamefiles.MapBobs0Lst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.gamefiles.MbobAfrBobsLst;
import org.appland.settlers.assets.gamefiles.MbobJapBobsLst;
import org.appland.settlers.assets.gamefiles.MbobRomBobsLst;
import org.appland.settlers.assets.gamefiles.MbobVikBobsLst;
import org.appland.settlers.assets.gamefiles.RomYLst;
import org.appland.settlers.assets.gamefiles.RomZLst;
import org.appland.settlers.assets.gamefiles.SoundLst;
import org.appland.settlers.assets.gamefiles.Tex5Lbm;
import org.appland.settlers.assets.gamefiles.Tex7Lbm;
import org.appland.settlers.assets.gamefiles.VikZLst;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.resources.WaveFile;
import org.appland.settlers.assets.utils.ImageTransformer;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Tree;
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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.*;
import static org.appland.settlers.model.Stone.StoneType.STONE_1;
import static org.appland.settlers.model.Stone.StoneType.STONE_2;
import static org.appland.settlers.model.WorkerAction.*;
import static org.appland.settlers.model.actors.Courier.BodyType.FAT;
import static org.appland.settlers.model.actors.Courier.BodyType.THIN;

public class Extractor {
    private Palette fallbackPalette;

    private record TitleAndFilename(String title, String filename) { }

    private static final String FALLBACK_PALETTE = "/Users/s0001386/projects/settlers/src/main/resources/default-palette.act";
    private static final String ROMAN_BUILDINGS_DIRECTORY = "roman-buildings";
    private static final String UI_ELEMENTS_DIRECTORY = "ui-elements";
    private static final String NATURE_DIRECTORY = "nature";
    private static final String SIGNS_DIRECTORY = "signs";
    private static final String TERRAIN_SUB_DIRECTORY = "terrain";
    private static final String GREENLAND_DIRECTORY = "greenland";
    private static final String WINTER_DIRECTORY = "winter";

    private static final List<TitleAndFilename> MUSIC_TITLE_AND_FILENAMES = new ArrayList<>(List.of(
            new TitleAndFilename("Track 1", "audio/01_-_Track_01.mp3"),
            new TitleAndFilename("Track 2", "audio/02_-_Track_02.mp3"),
            new TitleAndFilename("Track 3", "audio/03_-_Track_03.mp3"),
            new TitleAndFilename("Track 4", "audio/04_-_Track_04.mp3"),
            new TitleAndFilename("Track 5", "audio/05_-_Track_05.mp3"),
            new TitleAndFilename("Track 6", "audio/06_-_Track_06.mp3"),
            new TitleAndFilename("Track 7", "audio/07_-_Track_07.mp3"),
            new TitleAndFilename("Track 8", "audio/08_-_Track_08.mp3")
    ));

    @Option(name = "--from-dir", usage = "Asset directory to load from")
    static String fromDir;

    @Option(name = "--to-dir", usage = "Directory to extract assets into")
    static String toDir;

    private Palette defaultPalette;

    public static void main(String[] args) throws IOException, InvalidFormatException, UnknownResourceTypeException, CmdLineException {
        Extractor extractor = new Extractor();

        CmdLineParser parser = new CmdLineParser(extractor);

        parser.parseArgument(args);

        if (!Utils.isDirectory(toDir) || !Utils.isEmptyDirectory(toDir)) {
            System.out.println("Must specify an empty directory to extract assets into: " + toDir);
        }

        /* Load the palettes */
        extractor.loadPalettes(fromDir);

        /* Extract assets */
        extractor.populateRomanBuildings(fromDir, toDir);

        extractor.populateNatureAndUIElements(fromDir, toDir);

        extractor.populateWorkers(fromDir, toDir);

        extractor.populateFlags(fromDir, toDir);

        extractor.populateBorders(fromDir, toDir);

        extractor.populateShips(fromDir, toDir);

        extractor.populateAudio(fromDir, toDir);
    }

    // TODO: extract icons from IO.DAT

    private void populateAudio(String fromDir, String toDir) throws IOException, InvalidFormatException, UnknownResourceTypeException {

        // Write the music atlas
        JSONArray jsonSongs = new JSONArray();

        for (TitleAndFilename titleAndFilename : MUSIC_TITLE_AND_FILENAMES) {
            JSONObject jsonSong = new JSONObject();

            jsonSong.put("path", toDir + "/" + titleAndFilename.filename);
            jsonSong.put("title", titleAndFilename.title);

            jsonSongs.add(jsonSong);
        }

        // Write the audio atlas for the music
        Files.writeString(Paths.get(toDir, "audio-atlas-music.json"), jsonSongs.toJSONString());

        Files.createDirectory(Paths.get(toDir, "audio"));

        // The music files are converted from XMI to MP3s outside this tool. Tell the user where to place them:
        System.out.println("ACTION: place the music files in audio/song[0-9].mp3");

        // Extract each individual sound
        List<GameResource> gameResources = LstDecoder.loadLstFile(fromDir + "/" + SoundLst.FILENAME, defaultPalette);

        // Save each wave file, so we can try to figure out when they are used
        for (int i = 0; i < gameResources.size(); i++) {
            GameResource gameResource = gameResources.get(i);

            if (gameResource.getType() == GameResourceType.WAVE_SOUND) {
                WaveGameResource waveGameResource = (WaveGameResource) gameResource;
                WaveFile waveFile1 = waveGameResource.getWaveFile();

                waveFile1.writeToFile(toDir + "/wavefile-" + i + ".wave");
            }
        }

        // Write sounds
        Map<String, Integer> sounds = new HashMap<>();

        sounds.put(toDir + "/audio/fire.wave", SoundLst.FIRE);
        sounds.put(toDir + "/audio/fighting-hit-0.wave", SoundLst.FIGHTING_HIT_0);
        sounds.put(toDir + "/audio/fighting-unknown.wave", SoundLst.FIGHTING_UNKNOWN);
        sounds.put(toDir + "/audio/fighting-soldier-died.wave", SoundLst.FIGHTING_SOLDIER_DIED);
        sounds.put(toDir + "/audio/fighting-hit-1.wave", SoundLst.FIGHTING_HIT_1);
        sounds.put(toDir + "/audio/woodcutter-0.wave", SoundLst.SAWMILL_WORKER_0);
        sounds.put(toDir + "/audio/woodcutter-1.wave", SoundLst.SAWMILL_WORKER_1);
        sounds.put(toDir + "/audio/pig-breeder-0.wave", SoundLst.PIG_BREEDER_0);
        sounds.put(toDir + "/audio/pig-breeder-1.wave", SoundLst.PIG_BREEDER_1);
        sounds.put(toDir + "/audio/shipwright-0.wave", SoundLst.SHIPWRIGHT_0);
        sounds.put(toDir + "/audio/shipwright-1.wave", SoundLst.SHIPWRIGHT_1);
        sounds.put(toDir + "/audio/forester-0.wave", SoundLst.FORESTER_0);
        sounds.put(toDir + "/audio/forester-1.wave", SoundLst.FORESTER_1);
        sounds.put(toDir + "/audio/iron-founder-and-brewer.wave", SoundLst.IRON_FOUNDER_AND_BREWER);
        sounds.put(toDir + "/audio/farmer.wave", SoundLst.FARMER);
        sounds.put(toDir + "/audio/miner.wave", SoundLst.MINER);
        sounds.put(toDir + "/audio/geologist-digging-0.wave", SoundLst.GEOLOGIST_DIGGING_0);
        sounds.put(toDir + "/audio/geologist-digging-1.wave", SoundLst.GEOLOGIST_DIGGING_1);

        // TODO: brewer is at 51 which maps where?

        sounds.put(toDir + "/audio/builder-kneeling-hammering.wave", SoundLst.BUILDER_KNEELING_HAMMERING);
        sounds.put(toDir + "/audio/builder-standing-hammering.wave", SoundLst.BUILDER_STANDING_HAMMERING);
        sounds.put(toDir + "/audio/stonemason-hacking.wave", SoundLst.STONEMASON_HACKING);
        sounds.put(toDir + "/audio/miller.wave", SoundLst.MILLER);
        sounds.put(toDir + "/audio/butcher.wave", SoundLst.BUTCHER);
        sounds.put(toDir + "/audio/fisherman.wave", SoundLst.FISHERMAN);
        sounds.put(toDir + "/audio/planer-0.wave", SoundLst.PLANER_0);
        sounds.put(toDir + "/audio/planer-1.wave", SoundLst.PLANER_1);
        sounds.put(toDir + "/audio/woodcutter-cutting.wave", SoundLst.WOODCUTTER_CUTTING);
        sounds.put(toDir + "/audio/falling-tree.wave", SoundLst.FALLING_TREE);
        sounds.put(toDir + "/audio/baker-baking.wave", SoundLst.BAKER);
        sounds.put(toDir + "/audio/hunter.wave", SoundLst.HUNTER);
        sounds.put(toDir + "/audio/metalworker-0.wave", SoundLst.METALWORKER_0);
        sounds.put(toDir + "/audio/metalworker-1.wave", SoundLst.METALWORKER_1);
        sounds.put(toDir + "/audio/metalworker-2.wave", SoundLst.METALWORKER_2);
        sounds.put(toDir + "/audio/armorer.wave", SoundLst.ARMORER);
        sounds.put(toDir + "/audio/courier-with-boat.wave", SoundLst.COURIER_WITH_BOAT);
        sounds.put(toDir + "/audio/well-worker.wave", SoundLst.WELL_WORKER);

        sounds.put(toDir + "/audio/duck-quack.wave", SoundLst.DUCK_QUACK);
        sounds.put(toDir + "/audio/geologist-finding.wave", SoundLst.GEOLOGIST_FOUND_ORE);
        sounds.put(toDir + "/audio/new-message.wave", SoundLst.MILITARY_BUILDING_OCCUPIED);
        sounds.put(toDir + "/audio/new-message.wave", SoundLst.NEW_MESSAGE);

        // TODO: add additional sounds
        // TODO: verify that the offset remains correct. Brewer has sound 51?
        sounds.forEach((path, value) -> ((WaveGameResource) gameResources.get(value)).getWaveFile().writeToFile(path));
    }

    private void populateShips(String fromDir, String toDir) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        List<GameResource> bootBobsLst = LstDecoder.loadLstFile(fromDir + "/" + BootBobsLst.FILENAME, defaultPalette);

        ShipImageCollection shipImageCollection = new ShipImageCollection();

        shipImageCollection.addShipImageWithShadow(
                EAST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_EAST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_EAST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                SOUTH_EAST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_EAST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_EAST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                SOUTH_WEST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_WEST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                WEST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_WEST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                NORTH_WEST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_WEST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                NORTH_EAST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_EAST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_EAST_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.JUST_STARTED,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_JUST_STARTED),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_JUST_STARTED_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.HALF_WAY,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_HALF_WAY),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_HALF_WAY_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.ALMOST_DONE,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_ALMOST_DONE),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_ALMOST_DONE_SHADOW)
        );

        shipImageCollection.writeImageAtlas(toDir, defaultPalette);
    }

    private void populateBorders(String fromDir, String toDir) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        List<GameResource> mbobAfrBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobAfrBobsLst.FILENAME, defaultPalette);
        List<GameResource> mbobJapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobJapBobsLst.FILENAME, defaultPalette);
        List<GameResource> mbobRomBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobRomBobsLst.FILENAME, defaultPalette);
        List<GameResource> mbobVikBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobVikBobsLst.FILENAME, defaultPalette);

        BorderImageCollector borderImageCollector = new BorderImageCollector();

        borderImageCollector.addLandBorderImage(AFRICANS, getPlayerImageAt(mbobAfrBobsLst, MbobAfrBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(AFRICANS, getPlayerImageAt(mbobAfrBobsLst, MbobAfrBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addLandBorderImage(JAPANESE, getPlayerImageAt(mbobJapBobsLst, MbobJapBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(JAPANESE, getPlayerImageAt(mbobJapBobsLst, MbobJapBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addLandBorderImage(ROMANS, getPlayerImageAt(mbobRomBobsLst, MbobRomBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(ROMANS, getPlayerImageAt(mbobRomBobsLst, MbobRomBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addLandBorderImage(VIKINGS, getPlayerImageAt(mbobVikBobsLst, MbobVikBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWaterBorderImage(VIKINGS, getPlayerImageAt(mbobVikBobsLst, MbobVikBobsLst.COAST_BORDER_ICON));

        borderImageCollector.writeImageAtlas(toDir, defaultPalette);
    }

    private void populateFlags(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, IOException {

        List<GameResource> afrZLst = LstDecoder.loadLstFile(fromDir + "/" + AfrZLst.FILENAME, defaultPalette);
        List<GameResource> japZLst = LstDecoder.loadLstFile(fromDir + "/" + JapZLst.FILENAME, defaultPalette);
        List<GameResource> romZLst = LstDecoder.loadLstFile(fromDir + "/" + RomZLst.FILENAME, defaultPalette);
        List<GameResource> vikZLst = LstDecoder.loadLstFile(fromDir + "/" + VikZLst.FILENAME, defaultPalette);

        FlagImageCollection flagImageCollection = new FlagImageCollection();

        // Africans
        flagImageCollection.addImagesForFlag(AFRICANS, Flag.FlagType.NORMAL, getPlayerImagesAt(afrZLst, AfrZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, Flag.FlagType.NORMAL, getImagesAt(afrZLst, AfrZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(AFRICANS, Flag.FlagType.MAIN, getPlayerImagesAt(afrZLst, AfrZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, Flag.FlagType.MAIN, getImagesAt(afrZLst, AfrZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(AFRICANS, Flag.FlagType.MARINE, getPlayerImagesAt(afrZLst, AfrZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, Flag.FlagType.MARINE, getImagesAt(afrZLst, AfrZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Japanese
        flagImageCollection.addImagesForFlag(JAPANESE, Flag.FlagType.NORMAL, getPlayerImagesAt(japZLst, JapZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, Flag.FlagType.NORMAL, getImagesAt(japZLst, JapZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(JAPANESE, Flag.FlagType.MAIN, getPlayerImagesAt(japZLst, JapZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, Flag.FlagType.MAIN, getImagesAt(japZLst, JapZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(JAPANESE, Flag.FlagType.MARINE, getPlayerImagesAt(japZLst, JapZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, Flag.FlagType.MARINE, getImagesAt(japZLst, JapZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Romans
        flagImageCollection.addImagesForFlag(ROMANS, Flag.FlagType.NORMAL, getPlayerImagesAt(romZLst, RomZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, Flag.FlagType.NORMAL, getImagesAt(romZLst, RomZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(ROMANS, Flag.FlagType.MAIN, getPlayerImagesAt(romZLst, RomZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, Flag.FlagType.MAIN, getImagesAt(romZLst, RomZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(ROMANS, Flag.FlagType.MARINE, getPlayerImagesAt(romZLst, RomZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, Flag.FlagType.MARINE, getImagesAt(romZLst, RomZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Vikings
        flagImageCollection.addImagesForFlag(VIKINGS, Flag.FlagType.NORMAL, getPlayerImagesAt(vikZLst, VikZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, Flag.FlagType.NORMAL, getImagesAt(vikZLst, VikZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(VIKINGS, Flag.FlagType.MAIN, getPlayerImagesAt(vikZLst, VikZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, Flag.FlagType.MAIN, getImagesAt(vikZLst, VikZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(VIKINGS, Flag.FlagType.MARINE, getPlayerImagesAt(vikZLst, VikZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, Flag.FlagType.MARINE, getImagesAt(vikZLst, VikZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Write the image atlas to file
        flagImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
    }

    private void populateWorkers(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, IOException {

        /* Load worker image parts */
        List<GameResource> jobsBobList = LstDecoder.loadLstFile(fromDir + "/" + JobsBob.FILENAME, defaultPalette);
        List<GameResource> map0ZLst = LstDecoder.loadLstFile(fromDir + "/" + Map0ZLst.FILENAME, defaultPalette);
        List<GameResource> cbobRomBobsLst = LstDecoder.loadLstFile(fromDir + "/" + CbobRomBobsLst.FILENAME, defaultPalette);

        if (jobsBobList.size() != 1) {
            throw new RuntimeException("Wrong size of game resources in bob file. Must be 1, but was: " + jobsBobList.size());
        }

        if (! (jobsBobList.getFirst() instanceof BobResource jobsBobResource)) {
            throw new RuntimeException("Element must be Bob game resource. Was: " + jobsBobList.getFirst().getClass().getName());
        }

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
        Map<JobType, RenderedWorker> renderedWorkers = BobDecoder.renderWorkerImages(jobsBobResource.getBob(), workerDetailsMap);
        Map<JobType, WorkerImageCollection> workerImageCollectors = new EnumMap<>(JobType.class);

        var nationSpecificWorkers = List.of(
                JobType.PRIVATE,
                JobType.PRIVATE_FIRST_CLASS,
                JobType.SERGEANT,
                JobType.OFFICER,
                JobType.GENERAL);

        for (JobType jobType : JobType.values()) {
            RenderedWorker renderedWorker = renderedWorkers.get(jobType);

            WorkerImageCollection workerImageCollection = new WorkerImageCollection(jobType.name().toLowerCase());

            for (Nation nation : Nation.values()) {
                for (CompassDirection direction : CompassDirection.values()) {
                    StackedBitmaps[] stackedBitmaps = renderedWorker.getAnimation(nation, direction);

                    if (stackedBitmaps == null) {
                        System.out.println("Stacked bitmaps is null");
                        System.out.println(jobType);
                        System.out.println(nation);
                        System.out.println(direction);
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

                            boolean hasPlayerColor = false;

                            for (Bitmap bitmap : frame.getBitmaps()) {
                                if (bitmap instanceof PlayerBitmap) {
                                    hasPlayerColor = true;
                                }

                                Area bitmapVisibleArea = bitmap.getVisibleArea();
                                Point bitmapOrigin = bitmap.getOrigin();

                                maxOrigin.x = Math.max(maxOrigin.x, bitmapOrigin.x);
                                maxOrigin.y = Math.max(maxOrigin.y, bitmapOrigin.y);

                                maxPosition.x = Math.max(maxPosition.x, bitmapVisibleArea.width - bitmapOrigin.x);
                                maxPosition.y = Math.max(maxPosition.y, bitmapVisibleArea.height - bitmapOrigin.y);
                            }

                            for (var playerColor : PlayerColor.values()) {

                                /* Create a bitmap to merge both body and head into */
                                Bitmap merged = new Bitmap(maxOrigin.x + maxPosition.x, maxOrigin.y + maxPosition.y, defaultPalette, TextureFormat.BGRA);

                                merged.setNx(maxOrigin.x);
                                merged.setNy(maxOrigin.y);

                                /* Draw the body */
                                Area bodyVisibleArea = body.getVisibleArea();

                                Point bodyToUpperLeft = new Point(maxOrigin.x - body.getOrigin().x, maxOrigin.y - body.getOrigin().y);
                                Point bodyFromUpperLeft = bodyVisibleArea.getUpperLeftCoordinate();

                                if (hasPlayerColor) {
                                    merged.copyNonTransparentPixels(
                                            body.getBitmapForPlayer(playerColor),
                                            bodyToUpperLeft,
                                            bodyFromUpperLeft,
                                            bodyVisibleArea.getDimension());
                                } else {
                                    merged.copyNonTransparentPixels(
                                            body,
                                            bodyToUpperLeft,
                                            bodyFromUpperLeft,
                                            bodyVisibleArea.getDimension());
                                }

                                /* Draw the head */
                                Area headVisibleArea = head.getVisibleArea();

                                Point headToUpperLeft = new Point(maxOrigin.x - head.getOrigin().x, maxOrigin.y - head.getOrigin().y);
                                Point headFromUpperLeft = headVisibleArea.getUpperLeftCoordinate();

                                merged.copyNonTransparentPixels(head, headToUpperLeft, headFromUpperLeft, headVisibleArea.getDimension());

                                /* Store the image in the worker image collection */
                                if (nationSpecificWorkers.contains(jobType)) {
                                    if (hasPlayerColor) {
                                        workerImageCollection.addNationSpecificImageWithPlayerColor(nation, playerColor, direction, merged);
                                    } else {
                                        workerImageCollection.addNationSpecificImage(nation, direction, merged);
                                    }
                                } else {
                                    if (hasPlayerColor) {
                                        workerImageCollection.addImageWithPlayerColor(playerColor, direction, merged);
                                    } else {
                                        workerImageCollection.addImage(direction, merged);
                                    }
                                }

                                if (!hasPlayerColor) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!nationSpecificWorkers.contains(jobType)) {
                    break;
                }
            }

            workerImageCollection.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

            // Store the worker image collector
            workerImageCollectors.put(jobType, workerImageCollection);
        }

        // Add cargo carrying images and animations
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
        WorkerImageCollection privateWorkerImageCollector = workerImageCollectors.get(JobType.PRIVATE);
        WorkerImageCollection privateFirstClassWorkerImageCollector = workerImageCollectors.get(JobType.PRIVATE_FIRST_CLASS);
        WorkerImageCollection sergeantWorkerImageCollector = workerImageCollectors.get(JobType.SERGEANT);
        WorkerImageCollection officerWorkerImageCollector = workerImageCollectors.get(JobType.OFFICER);
        WorkerImageCollection generalWorkerImageCollector = workerImageCollectors.get(JobType.GENERAL);

        Bob bob = ((BobResource) jobsBobList.getFirst()).getBob();

        woodcutterImageCollector.readCargoImagesFromBob(
                WOOD,
                JobsBob.WOODCUTTER_BOB.getBodyType(),
                JobsBob.WOODCUTTER_WITH_WOOD_CARGO_BOB_ID,
                bob
        );

        woodcutterImageCollector.addAnimation(WorkerAction.CUTTING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.CUTTING, 8));

        // Add roman military attacking
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_ATTACKING_EAST, 8));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_ATTACKING_WEST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_ATTACKING_EAST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_ATTACKING_WEST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_ATTACKING_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_ATTACKING_WEST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_ATTACKING_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_ATTACKING_WEST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_ATTACKING_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_ATTACKING_WEST, 8));

        // Add roman military getting hit
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_SHIELD_UP_EAST, 8));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_SHIELD_UP_WEST, 6));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_STAND_ASIDE_EAST, 6));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_STAND_ASIDE_WEST, 7));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, SHIELD_UP, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_JUMP_BACK_EAST, 7));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, SHIELD_UP, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_JUMP_BACK_WEST, 7));

        Arrays.stream(PlayerColor.values()).forEach(
                playerColor -> {
                    privateWorkerImageCollector.addNationSpecificAnimationInDirectionWithPlayerColor(
                            ROMANS,
                            WEST,
                            GET_HIT,
                            ImageTransformer.makeGetHitAnimation(
                                    getPlayerImageAt(
                                            cbobRomBobsLst,
                                            CbobRomBobsLst.PRIVATE_SHIELD_UP_WEST).getBitmapForPlayer(playerColor)));

                    privateWorkerImageCollector.addNationSpecificAnimationInDirectionWithPlayerColor(
                            ROMANS,
                            EAST,
                            GET_HIT,
                            ImageTransformer.makeGetHitAnimation(
                                    getPlayerImageAt(
                                            cbobRomBobsLst,
                                            CbobRomBobsLst.PRIVATE_SHIELD_UP_EAST).getBitmapForPlayer(playerColor)));
                }
        );


        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_AVOIDING_HIT_EAST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_AVOIDING_HIT_WEST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_FLINCH_HIT_EAST, 6));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_FLINCH_HIT_WEST, 6));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_GETTING_HIT_EAST, 7));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PRIVATE_FIRST_CLASS_GETTING_HIT_WEST, 7));

        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_AVOIDING_HIT_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_AVOIDING_HIT_WEST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_FLINCH_HIT_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_FLINCH_HIT_WEST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_GETTING_HIT_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SERGEANT_GETTING_HIT_WEST, 8));

        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_AVOIDING_HIT_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_AVOIDING_HIT_WEST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_FLINCH_HIT_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_FLINCH_HIT_WEST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_GETTING_HIT_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.OFFICER_GETTING_HIT_WEST, 7));

        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_AVOIDING_HIT_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_AVOIDING_HIT_WEST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_FLINCH_HIT_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_FLINCH_HIT_WEST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_GETTING_HIT_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.GENERAL_GETTING_HIT_WEST, 8));

        privateWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        privateFirstClassWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        sergeantWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        officerWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        generalWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));

        carpenterImageCollector.readCargoImagesFromBob(
                PLANK,
                JobsBob.CARPENTER_BOB.getBodyType(),
                JobsBob.CARPENTER_WITH_PLANK_BOB_ID,
                bob
        );

        carpenterImageCollector.addAnimation(WorkerAction.SAWING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SAWING, 6));

        stonemasonImageCollector.readCargoImagesFromBob(
                STONE,
                JobsBob.STONEMASON_BOB.getBodyType(),
                JobsBob.STONEMASON_WITH_STONE_CARGO_BOB_ID,
                bob
        );

        stonemasonImageCollector.addAnimation(WorkerAction.HACKING_STONE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HACKING_STONE, 8));

        foresterWorkerImageCollector.addAnimation(WorkerAction.PLANTING_TREE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.DIGGING_AND_PLANTING, 36));

        planerWorkerImageCollector.addAnimation(WorkerAction.DIGGING_AND_STOMPING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.DIGGING_AND_STOMPING, 26));

        geologistWorkerImageCollector.addAnimation(WorkerAction.INVESTIGATING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.INVESTIGATING, 16));

        builderWorkerImageCollector.addAnimation(WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HAMMERING_HOUSE_HIGH_AND_LOW, 8));
        builderWorkerImageCollector.addAnimation(WorkerAction.INSPECTING_HOUSE_CONSTRUCTION, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.INSPECTING_HOUSE_CONSTRUCTION, 4));

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
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                SOUTH_EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                SOUTH_WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_NORTH_EAST, 8));

        // Keep fishing
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                SOUTH_EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                SOUTH_WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_NORTH_EAST, 8));

        // Pull up fish
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                SOUTH_EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                SOUTH_WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_NORTH_EAST, 8));

        farmerImageCollector.readCargoImagesFromBob(
                WHEAT,
                JobsBob.FARMER_BOB.getBodyType(),
                JobsBob.FARMER_WITH_WHEAT_CARGO_BOB_ID,
                bob
        );

        farmerImageCollector.addAnimation(WorkerAction.PLANTING_WHEAT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOWING, 8));
        farmerImageCollector.addAnimation(WorkerAction.HARVESTING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HARVESTING, 8));

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

        bakerImageCollector.addAnimation(WorkerAction.BAKING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.BAKING, 8));

        // TODO: Handle brewer and/or well worker

        brewerWorkerImageCollector.addAnimation(WorkerAction.DRINKING_BEER, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.DRINKING_BEER, 8));

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

        hunterWorkerImageCollector.addAnimation(WorkerAction.SHOOTING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HUNTING, 13));
        hunterWorkerImageCollector.addAnimation(WorkerAction.PICKING_UP_MEAT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PICKING_UP_MEAT, 12));

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
        Bob jobsBob = jobsBobResource.getBob();
        Bob carrierBob = BobDecoder.loadBobFile(fromDir + "/" + CarrierBob.FILENAME, defaultPalette);

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

        thinCarrier.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        fatCarrier.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        thinCarrierWithCargo.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        fatCarrierWithCargo.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        // Add animations for when the couriers are bored
        fatCarrier.addAnimation(CHEW_GUM, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.CHEW_GUM, 8));
        fatCarrier.addAnimation(SIT_DOWN, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SIT_DOWN, 5));
        thinCarrier.addAnimation(READ_NEWSPAPER, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.READ_NEWSPAPER, 7));
        thinCarrier.addAnimation(TOUCH_NOSE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.TOUCH_NOSE, 5));
        thinCarrier.addAnimation(JUMP_SKIP_ROPE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.JUMP_SKIP_ROPE, 7));

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
    private void populateNatureAndUIElements(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, IOException {

        /* Load from the map asset file */
        List<GameResource> mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);
        List<GameResource> mapBobs0Lst = LstDecoder.loadLstFile(fromDir + "/" + MapBobs0Lst.FILENAME, defaultPalette);
        List<GameResource> map0ZLst = LstDecoder.loadLstFile(fromDir + "/" + Map0ZLst.FILENAME, defaultPalette);
        List<GameResource> ioLst = LstDecoder.loadLstFile(fromDir + "/" + IoLst.FILENAME, fallbackPalette);

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
        LBMGameResource greenlandGameResource = (LBMGameResource) LbmDecoder.loadLBMFile(fromDir + "/" + Tex5Lbm.FILENAME, defaultPalette);
        LBMGameResource winterGameResource = (LBMGameResource) LbmDecoder.loadLBMFile(fromDir + "/" + Tex7Lbm.FILENAME, defaultPalette);

        Bitmap greenlandTextureBitmap = greenlandGameResource.getLbmFile();
        Bitmap winterTextureBitmap = winterGameResource.getLbmFile();

        greenlandTextureBitmap.writeToFile(greenlandDir + "/greenland-texture.png");
        winterTextureBitmap.writeToFile(winterDir + "/winter-texture.png");

        /* Extract the stones */
        StonesImageCollection stonesImageCollection = new StonesImageCollection();

        stonesImageCollection.addImage(STONE_1, StoneAmount.MINI, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_MINI));
        stonesImageCollection.addShadowImage(STONE_1, StoneAmount.MINI, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_MINI_SHADOW));
        stonesImageCollection.addImage(STONE_1, StoneAmount.LITTLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE));
        stonesImageCollection.addShadowImage(STONE_1, StoneAmount.LITTLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE_SHADOW));
        stonesImageCollection.addImage(STONE_1, StoneAmount.LITTLE_MORE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE_MORE));
        stonesImageCollection.addShadowImage(STONE_1, StoneAmount.LITTLE_MORE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_LITTLE_MORE_SHADOW));
        stonesImageCollection.addImage(STONE_1, StoneAmount.MIDDLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_MIDDLE));
        stonesImageCollection.addShadowImage(STONE_1, StoneAmount.MIDDLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_MIDDLE_SHADOW));
        stonesImageCollection.addImage(STONE_1, StoneAmount.ALMOST_FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_ALMOST_FULL));
        stonesImageCollection.addShadowImage(STONE_1, StoneAmount.ALMOST_FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_ALMOST_FULL_SHADOW));
        stonesImageCollection.addImage(STONE_1, StoneAmount.FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_FULL));
        stonesImageCollection.addShadowImage(STONE_1, StoneAmount.FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_FULL_SHADOW));

        stonesImageCollection.addImage(STONE_2, StoneAmount.MINI, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_MINI));
        stonesImageCollection.addShadowImage(STONE_2, StoneAmount.MINI, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_MINI_SHADOW));
        stonesImageCollection.addImage(STONE_2, StoneAmount.LITTLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE));
        stonesImageCollection.addShadowImage(STONE_2, StoneAmount.LITTLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE_SHADOW));
        stonesImageCollection.addImage(STONE_2, StoneAmount.LITTLE_MORE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE_MORE));
        stonesImageCollection.addShadowImage(STONE_2, StoneAmount.LITTLE_MORE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_LITTLE_MORE_SHADOW));
        stonesImageCollection.addImage(STONE_2, StoneAmount.MIDDLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_MIDDLE));
        stonesImageCollection.addShadowImage(STONE_2, StoneAmount.MIDDLE, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_MIDDLE_SHADOW));
        stonesImageCollection.addImage(STONE_2, StoneAmount.ALMOST_FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_ALMOST_FULL));
        stonesImageCollection.addShadowImage(STONE_2, StoneAmount.ALMOST_FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_ALMOST_FULL_SHADOW));
        stonesImageCollection.addImage(STONE_2, StoneAmount.FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_FULL));
        stonesImageCollection.addShadowImage(STONE_2, StoneAmount.FULL, getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_FULL_SHADOW));

        stonesImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract UI elements */
        UIElementsImageCollection uiElementsImageCollection = new UIElementsImageCollection();

        uiElementsImageCollection.addSelectedPointImage(getImageAt(mapBobsLst, MapBobsLst.SELECTED_POINT));
        uiElementsImageCollection.addHoverPoint(getImageAt(mapBobsLst, MapBobsLst.HOVER_POINT));
        uiElementsImageCollection.addHoverAvailableFlag(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_FLAG));
        uiElementsImageCollection.addHoverAvailableMine(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MINE));
        uiElementsImageCollection.addHoverAvailableBuilding(SMALL, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_SMALL_BUILDING));
        uiElementsImageCollection.addHoverAvailableBuilding(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MEDIUM_BUILDING));
        uiElementsImageCollection.addHoverAvailableBuilding(LARGE, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_LARGE_BUILDING));
        uiElementsImageCollection.addHoverAvailableHarbor(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_HARBOR));
        uiElementsImageCollection.addAvailableFlag(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_FLAG));
        uiElementsImageCollection.addAvailableMine(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_MINE));
        uiElementsImageCollection.addAvailableBuilding(SMALL, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_SMALL_BUILDING));
        uiElementsImageCollection.addAvailableBuilding(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_MEDIUM_BUILDING));
        uiElementsImageCollection.addAvailableBuilding(LARGE, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_LARGE_BUILDING));
        uiElementsImageCollection.addAvailableHarbor(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_HARBOR));

        uiElementsImageCollection.addUiElement(UiIcon.DESTROY_BUILDING, getImageAt(ioLst, IoLst.BURNING_HOUSE_ICON));
        uiElementsImageCollection.addUiElement(UiIcon.ATTACK, getImageAt(ioLst, IoLst.ATTACK_ICON));
        uiElementsImageCollection.addUiElement(UiIcon.SCISSORS, getImageAt(ioLst, IoLst.SCISSORS));
        uiElementsImageCollection.addUiElement(UiIcon.INFORMATION, getImageAt(ioLst, IoLst.INFORMATION));
        uiElementsImageCollection.addUiElement(UiIcon.GEOLOGIST, getImageAt(ioLst, IoLst.GEOLOGIST_ICON));

        uiElementsImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract the inventory icons */
        InventoryImageCollection inventoryImageCollection = new InventoryImageCollection();

        inventoryImageCollection.addIcon(BEER, getImageAt(mapBobsLst, MapBobsLst.BEER_ICON));
        inventoryImageCollection.addIcon(TONGS, getImageAt(mapBobsLst, MapBobsLst.PLIER_ICON));
        inventoryImageCollection.addIcon(HAMMER, getImageAt(mapBobsLst, MapBobsLst.HAMMER_ICON));
        inventoryImageCollection.addIcon(AXE, getImageAt(mapBobsLst, MapBobsLst.AXE_ICON));
        inventoryImageCollection.addIcon(SAW, getImageAt(mapBobsLst, MapBobsLst.SAW_ICON));
        inventoryImageCollection.addIcon(PICK_AXE, getImageAt(mapBobsLst, MapBobsLst.PICK_AXE_ICON));
        inventoryImageCollection.addIcon(SHOVEL, getImageAt(mapBobsLst, MapBobsLst.SHOVEL_ICON));
        inventoryImageCollection.addIcon(CRUCIBLE, getImageAt(mapBobsLst, MapBobsLst.CRUCIBLE_ICON));
        inventoryImageCollection.addIcon(FISHING_ROD, getImageAt(mapBobsLst, MapBobsLst.FISHING_HOOK_ICON));
        inventoryImageCollection.addIcon(SCYTHE, getImageAt(mapBobsLst, MapBobsLst.SCYTHE_ICON));
        //inventoryImageCollection.addIcon(BUCKET, getImageFromResourceLocation(mapBobsLst, MapBobsLst.EMPTY_BUCKET_ICON));
        inventoryImageCollection.addIcon(WATER, getImageAt(mapBobsLst, MapBobsLst.BUCKET_WITH_WATER_ICON));
        inventoryImageCollection.addIcon(CLEAVER, getImageAt(mapBobsLst, MapBobsLst.CLEAVER_ICON));
        inventoryImageCollection.addIcon(ROLLING_PIN, getImageAt(mapBobsLst, MapBobsLst.ROLLING_PIN_ICON));
        inventoryImageCollection.addIcon(BOW, getImageAt(mapBobsLst, MapBobsLst.SPEAR_ICON));
        inventoryImageCollection.addIcon(BOAT, getImageAt(mapBobsLst, MapBobsLst.BOAT_ICON));
        inventoryImageCollection.addIcon(SWORD, getImageAt(mapBobsLst, MapBobsLst.GOLD_SWORD_ICON));
        inventoryImageCollection.addIcon(IRON_BAR, getImageAt(mapBobsLst, MapBobsLst.IRON_BAR_ICON));
        inventoryImageCollection.addIcon(FLOUR, getImageAt(mapBobsLst, MapBobsLst.FLOUR_BAG_ICON));
        inventoryImageCollection.addIcon(FISH, getImageAt(mapBobsLst, MapBobsLst.FISH_ICON));
        inventoryImageCollection.addIcon(BREAD, getImageAt(mapBobsLst, MapBobsLst.BREAD_ICON));
        inventoryImageCollection.addNationSpecificIcon(SHIELD, ROMANS, getImageAt(mapBobsLst, MapBobsLst.ROMAN_SHIELD_ICON));
        inventoryImageCollection.addIcon(WOOD, getImageAt(mapBobsLst, MapBobsLst.WOOD_ICON));
        inventoryImageCollection.addIcon(PLANK, getImageAt(mapBobsLst, MapBobsLst.PLANKS_ICON));
        inventoryImageCollection.addIcon(STONE, getImageAt(mapBobsLst, MapBobsLst.STONE_ICON));
        inventoryImageCollection.addNationSpecificIcon(SHIELD, VIKINGS, getImageAt(mapBobsLst, MapBobsLst.VIKING_SHIELD_ICON));
        inventoryImageCollection.addNationSpecificIcon(SHIELD, AFRICANS, getImageAt(mapBobsLst, MapBobsLst.AFRICAN_SHIELD_ICON));
        inventoryImageCollection.addIcon(WHEAT, getImageAt(mapBobsLst, MapBobsLst.WHEAT_ICON));
        inventoryImageCollection.addIcon(COIN, getImageAt(mapBobsLst, MapBobsLst.COIN_ICON));
        inventoryImageCollection.addIcon(GOLD, getImageAt(mapBobsLst, MapBobsLst.GOLD_ICON));
        inventoryImageCollection.addIcon(IRON, getImageAt(mapBobsLst, MapBobsLst.IRON_ICON));
        inventoryImageCollection.addIcon(COAL, getImageAt(mapBobsLst, MapBobsLst.COAL_ICON));
        inventoryImageCollection.addIcon(MEAT, getImageAt(mapBobsLst, MapBobsLst.MEAT_ICON));
        inventoryImageCollection.addIcon(PIG, getImageAt(mapBobsLst, MapBobsLst.PIG_ICON));
        inventoryImageCollection.addNationSpecificIcon(SHIELD, JAPANESE, getImageAt(mapBobsLst, MapBobsLst.JAPANESE_SHIELD_ICON));
        //inventoryImageCollection.addIcon(, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BACKGROUND_UNKNOWN_1);
        //inventoryImageCollection.addIcon(, getImageFromResourceLocation(mapBobsLst, MapBobsLst.BACKGROUND_UNKNOWN_2);
        inventoryImageCollection.addIcon(COURIER, getImageAt(mapBobsLst, MapBobsLst.CARRIER_ICON));
        inventoryImageCollection.addIcon(WOODCUTTER_WORKER, getImageAt(mapBobsLst, MapBobsLst.WOODCUTTER_ICON));
        inventoryImageCollection.addIcon(FISHERMAN, getImageAt(mapBobsLst, MapBobsLst.FISHERMAN_ICON));
        inventoryImageCollection.addIcon(FORESTER, getImageAt(mapBobsLst, MapBobsLst.FORESTER_ICON));
        inventoryImageCollection.addIcon(SAWMILL_WORKER, getImageAt(mapBobsLst, MapBobsLst.SAWMILL_WORKER_ICON));
        inventoryImageCollection.addIcon(STONEMASON, getImageAt(mapBobsLst, MapBobsLst.STONEMASON_ICON));
        inventoryImageCollection.addIcon(HUNTER, getImageAt(mapBobsLst, MapBobsLst.HUNTER_ICON));
        inventoryImageCollection.addIcon(FARMER, getImageAt(mapBobsLst, MapBobsLst.FARMER_ICON));
        inventoryImageCollection.addIcon(MILLER, getImageAt(mapBobsLst, MapBobsLst.MILLER_ICON));
        inventoryImageCollection.addIcon(BAKER, getImageAt(mapBobsLst, MapBobsLst.BAKER_ICON));
        inventoryImageCollection.addIcon(BUTCHER, getImageAt(mapBobsLst, MapBobsLst.BUTCHER_ICON));
        inventoryImageCollection.addIcon(MINER, getImageAt(mapBobsLst, MapBobsLst.MINER_ICON));
        inventoryImageCollection.addIcon(BREWER, getImageAt(mapBobsLst, MapBobsLst.BREWER_ICON));
        inventoryImageCollection.addIcon(PIG_BREEDER, getImageAt(mapBobsLst, MapBobsLst.PIG_BREEDER_ICON));
        inventoryImageCollection.addIcon(DONKEY_BREEDER, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_BREEDER_ICON));
        inventoryImageCollection.addIcon(IRON_FOUNDER, getImageAt(mapBobsLst, MapBobsLst.IRON_MELTER_ICON));
        inventoryImageCollection.addIcon(MINTER, getImageAt(mapBobsLst, MapBobsLst.MINTER_ICON));
        inventoryImageCollection.addIcon(METALWORKER, getImageAt(mapBobsLst, MapBobsLst.TOOL_MAKER_ICON));
        inventoryImageCollection.addIcon(ARMORER, getImageAt(mapBobsLst, MapBobsLst.SMITH_ICON));
        inventoryImageCollection.addIcon(BUILDER, getImageAt(mapBobsLst, MapBobsLst.BUILDER_ICON));
        inventoryImageCollection.addIcon(PLANER, getImageAt(mapBobsLst, MapBobsLst.PLANER_ICON));
        inventoryImageCollection.addIcon(PRIVATE, getImageAt(mapBobsLst, MapBobsLst.PRIVATE_SOLDIER_ICON));
        inventoryImageCollection.addIcon(PRIVATE_FIRST_CLASS, getImageAt(mapBobsLst, MapBobsLst.PRIVATE_FIRST_RANK_SOLDIER_ICON));
        inventoryImageCollection.addIcon(SERGEANT, getImageAt(mapBobsLst, MapBobsLst.SERGEANT_SOLDIER_ICON));
        inventoryImageCollection.addIcon(OFFICER, getImageAt(mapBobsLst, MapBobsLst.OFFICER_SOLDIER_ICON));
        inventoryImageCollection.addIcon(GENERAL, getImageAt(mapBobsLst, MapBobsLst.GENERAL_SOLDIER_ICON));
        inventoryImageCollection.addIcon(GEOLOGIST, getImageAt(mapBobsLst, MapBobsLst.GEOLOGIST_ICON));
        inventoryImageCollection.addIcon(SHIPWRIGHT, getImageAt(mapBobsLst, MapBobsLst.SHIP_ICON));
        inventoryImageCollection.addIcon(SCOUT, getImageAt(mapBobsLst, MapBobsLst.FUR_HAT_UNKNOWN_ICON));

        inventoryImageCollection.addIcon(DONKEY, getImageAt(mapBobs0Lst, 1027));
        //        inventoryImageCollection.addIcon(, getImageFromResourceLocation(mapBobsLst, MapBobsLst.ICON_BACKGROUND);

        inventoryImageCollection.writeImageAtlas(toDir, defaultPalette);

        /*  Extract the crops */
        CropImageCollection cropImageCollection = new CropImageCollection();

        cropImageCollection.addImage(Crop.CropType.TYPE_1, Crop.GrowthState.JUST_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_NEWLY_PLANTED));
        cropImageCollection.addImage(Crop.CropType.TYPE_1, Crop.GrowthState.SMALL, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_LITTLE_GROWTH));
        cropImageCollection.addImage(Crop.CropType.TYPE_1, Crop.GrowthState.ALMOST_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_MORE_GROWTH));
        cropImageCollection.addImage(Crop.CropType.TYPE_1, Crop.GrowthState.FULL_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_FULLY_GROWN));
        cropImageCollection.addImage(Crop.CropType.TYPE_1, Crop.GrowthState.HARVESTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_JUST_HARVESTED));

        cropImageCollection.addImage(Crop.CropType.TYPE_2, Crop.GrowthState.JUST_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_NEWLY_PLANTED));
        cropImageCollection.addImage(Crop.CropType.TYPE_2, Crop.GrowthState.SMALL, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_LITTLE_GROWTH));
        cropImageCollection.addImage(Crop.CropType.TYPE_2, Crop.GrowthState.ALMOST_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_MORE_GROWTH));
        cropImageCollection.addImage(Crop.CropType.TYPE_2, Crop.GrowthState.FULL_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_FULLY_GROWN));
        cropImageCollection.addImage(Crop.CropType.TYPE_2, Crop.GrowthState.HARVESTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_JUST_HARVESTED));

        cropImageCollection.addShadowImage(Crop.CropType.TYPE_1, Crop.GrowthState.JUST_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_NEWLY_PLANTED_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_1, Crop.GrowthState.SMALL, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_LITTLE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_1, Crop.GrowthState.ALMOST_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_MORE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_1, Crop.GrowthState.FULL_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_FULLY_GROWN_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_1, Crop.GrowthState.HARVESTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_1_JUST_HARVESTED_SHADOW));

        cropImageCollection.addShadowImage(Crop.CropType.TYPE_2, Crop.GrowthState.JUST_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_NEWLY_PLANTED_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_2, Crop.GrowthState.SMALL, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_LITTLE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_2, Crop.GrowthState.ALMOST_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_MORE_GROWTH_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_2, Crop.GrowthState.FULL_GROWN, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_FULLY_GROWN_SHADOW));
        cropImageCollection.addShadowImage(Crop.CropType.TYPE_2, Crop.GrowthState.HARVESTED, getImageAt(mapBobsLst, MapBobsLst.CROP_TYPE_2_JUST_HARVESTED_SHADOW));

        cropImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract the cargo images that workers carry */
        CargoImageCollection cargoImageCollection = new CargoImageCollection();

        cargoImageCollection.addCargoImage(BEER, getImageAt(mapBobsLst, MapBobsLst.BEER_CARGO));
        cargoImageCollection.addCargoImage(TONGS, getImageAt(mapBobsLst, MapBobsLst.TONG_CARGO));
        cargoImageCollection.addCargoImage(AXE, getImageAt(mapBobsLst, MapBobsLst.AXE_CARGO));
        cargoImageCollection.addCargoImage(SAW, getImageAt(mapBobsLst, MapBobsLst.SAW_CARGO));
        cargoImageCollection.addCargoImage(PICK_AXE, getImageAt(mapBobsLst, MapBobsLst.PICK_AXE_CARGO));
        cargoImageCollection.addCargoImage(SHOVEL, getImageAt(mapBobsLst, MapBobsLst.SHOVEL_CARGO));
        cargoImageCollection.addCargoImage(CRUCIBLE, getImageAt(mapBobsLst, MapBobsLst.CRUCIBLE_CARGO)); //???
        cargoImageCollection.addCargoImage(FISHING_ROD, getImageAt(mapBobsLst, MapBobsLst.FISHING_ROD_CARGO));
        cargoImageCollection.addCargoImage(SCYTHE, getImageAt(mapBobsLst, MapBobsLst.SCYTHE_CARGO));
        // - empty bucket at 904

        cargoImageCollection.addCargoImage(WATER, getImageAt(mapBobsLst, MapBobsLst.WATER_BUCKET_CARGO));
        cargoImageCollection.addCargoImage(CLEAVER, getImageAt(mapBobsLst, MapBobsLst.CLEAVER_CARGO));
        cargoImageCollection.addCargoImage(ROLLING_PIN, getImageAt(mapBobsLst, MapBobsLst.ROLLING_PIN_CARGO));
        cargoImageCollection.addCargoImage(BOW, getImageAt(mapBobsLst, MapBobsLst.BOW_CARGO));
        cargoImageCollection.addCargoImage(BOAT, getImageAt(mapBobsLst, MapBobsLst.BOAT_CARGO));
        cargoImageCollection.addCargoImage(SWORD, getImageAt(mapBobsLst, MapBobsLst.SWORD_CARGO));
        // - anvil at 911

        cargoImageCollection.addCargoImage(FLOUR, getImageAt(mapBobsLst, MapBobsLst.FLOUR_CARGO));
        cargoImageCollection.addCargoImage(FISH, getImageAt(mapBobsLst, MapBobsLst.FISH_CARGO));
        cargoImageCollection.addCargoImage(BREAD, getImageAt(mapBobsLst, MapBobsLst.BREAD_CARGO));
        cargoImageCollection.addCargoImageForNation(ROMANS, SHIELD, getImageAt(mapBobsLst, MapBobsLst.ROMAN_SHIELD_CARGO));
        cargoImageCollection.addCargoImage(WOOD, getImageAt(mapBobsLst, MapBobsLst.WOOD_CARGO));
        cargoImageCollection.addCargoImage(PLANK, getImageAt(mapBobsLst, MapBobsLst.PLANK_CARGO));
        cargoImageCollection.addCargoImage(STONE, getImageAt(mapBobsLst, MapBobsLst.STONE_CARGO));
        cargoImageCollection.addCargoImageForNation(VIKINGS, SHIELD, getImageAt(mapBobsLst, MapBobsLst.VIKING_SHIELD_CARGO));
        cargoImageCollection.addCargoImageForNation(AFRICANS, SHIELD, getImageAt(mapBobsLst, MapBobsLst.AFRICAN_SHIELD_CARGO));
        cargoImageCollection.addCargoImage(WHEAT, getImageAt(mapBobsLst, MapBobsLst.WHEAT_CARGO));
        cargoImageCollection.addCargoImage(COIN, getImageAt(mapBobsLst, MapBobsLst.COIN_CARGO));
        cargoImageCollection.addCargoImage(GOLD, getImageAt(mapBobsLst, MapBobsLst.GOLD_CARGO));
        cargoImageCollection.addCargoImage(IRON, getImageAt(mapBobsLst, MapBobsLst.IRON_CARGO));
        cargoImageCollection.addCargoImage(COAL, getImageAt(mapBobsLst, MapBobsLst.COAL_CARGO));
        cargoImageCollection.addCargoImage(MEAT, getImageAt(mapBobsLst, MapBobsLst.MEAT_CARGO));
        cargoImageCollection.addCargoImage(PIG, getImageAt(mapBobsLst, MapBobsLst.PIG_CARGO));
        cargoImageCollection.addCargoImageForNation(JAPANESE, SHIELD, getImageAt(mapBobsLst, MapBobsLst.JAPANESE_SHIELD_CARGO));

        cargoImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract signs */
        SignImageCollection signImageCollection = new SignImageCollection();

        signImageCollection.addImage(SignType.IRON, SMALL, getImageAt(mapBobsLst, MapBobsLst.IRON_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.IRON, MEDIUM, getImageAt(mapBobsLst, MapBobsLst.IRON_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.IRON, LARGE, getImageAt(mapBobsLst, MapBobsLst.IRON_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.COAL, SMALL, getImageAt(mapBobsLst, MapBobsLst.COAL_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.COAL, MEDIUM, getImageAt(mapBobsLst, MapBobsLst.COAL_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.COAL, LARGE, getImageAt(mapBobsLst, MapBobsLst.COAL_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.STONE, SMALL, getImageAt(mapBobsLst, MapBobsLst.GRANITE_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.STONE, MEDIUM, getImageAt(mapBobsLst, MapBobsLst.GRANITE_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.STONE, LARGE, getImageAt(mapBobsLst, MapBobsLst.GRANITE_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.GOLD, SMALL, getImageAt(mapBobsLst, MapBobsLst.GOLD_SIGN_SMALL_UP_RIGHT));
        signImageCollection.addImage(SignType.GOLD, MEDIUM, getImageAt(mapBobsLst, MapBobsLst.GOLD_SIGN_MEDIUM_UP_RIGHT));
        signImageCollection.addImage(SignType.GOLD, LARGE, getImageAt(mapBobsLst, MapBobsLst.GOLD_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.WATER, LARGE, getImageAt(mapBobsLst, MapBobsLst.WATER_SIGN_LARGE_UP_RIGHT));

        signImageCollection.addImage(SignType.NOTHING, LARGE, getImageAt(mapBobsLst, MapBobsLst.NOTHING_SIGN_UP_RIGHT));

        signImageCollection.addShadowImage(getImageAt(mapBobsLst, MapBobsLst.SIGN_SHADOW));

        signImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract road building icons */
        RoadBuildingImageCollection roadBuildingImageCollection = new RoadBuildingImageCollection();

        roadBuildingImageCollection.addStartPointImage(getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_START_POINT));
        roadBuildingImageCollection.addSameLevelConnectionImage(getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_SAME_LEVEL_CONNECTION));

        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.LITTLE, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_LITTLE_HIGHER_CONNECTION));
        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MEDIUM_HIGHER_CONNECTION));
        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.HIGH, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MUCH_HIGHER_CONNECTION));

        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.LITTLE, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_LITTLE_LOWER_CONNECTION));
        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MEDIUM_LOWER_CONNECTION));
        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.HIGH, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MUCH_LOWER_CONNECTION));

        roadBuildingImageCollection.writeImageAtlas(toDir, defaultPalette);

        /* Extract fire animation */
        FireImageCollection fireImageCollection = new FireImageCollection();

        fireImageCollection.addImagesForFire(FireSize.MINI, getImagesAt(mapBobsLst, MapBobsLst.MINI_FIRE_ANIMATION, 8));
        fireImageCollection.addImagesForFireWithShadow(
                FireSize.SMALL,
                getImagesAt(mapBobsLst, MapBobsLst.SMALL_FIRE_ANIMATION, 8),
                getImagesAt(mapBobsLst, MapBobsLst.SMALL_FIRE_SHADOW_ANIMATION, 8)
        );
        fireImageCollection.addImagesForFireWithShadow(
                FireSize.MEDIUM,
                getImagesAt(mapBobsLst, MapBobsLst.MEDIUM_FIRE_ANIMATION, 8),
                getImagesAt(mapBobsLst, MapBobsLst.MEDIUM_FIRE_SHADOW_ANIMATION, 8)
        );
        fireImageCollection.addImagesForFireWithShadow(
                FireSize.LARGE,
                getImagesAt(mapBobsLst, MapBobsLst.LARGE_FIRE_ANIMATION, 8),
                getImagesAt(mapBobsLst, MapBobsLst.LARGE_FIRE_SHADOW_ANIMATION, 8)
        );

        fireImageCollection.addBurntDownImage(SMALL, getImageAt(mapBobsLst, MapBobsLst.SMALL_BURNT_DOWN));
        fireImageCollection.addBurntDownImage(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.MEDIUM_BURNT_DOWN));
        fireImageCollection.addBurntDownImage(LARGE, getImageAt(mapBobsLst, MapBobsLst.LARGE_BURNT_DOWN));

        fireImageCollection.writeImageAtlas(toDir, defaultPalette);

        // Collect tree images
        TreeImageCollection treeImageCollection = new TreeImageCollection("trees");

        /* Extract animation for tree type 1 in wind -- cypress (?) */
        treeImageCollection.addImagesForTree(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.CYPRESS, getImagesAt(mapBobsLst, MapBobsLst.CYPRESS_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CYPRESS, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CYPRESS, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CYPRESS_SHADOW_MEDIUM));

        /* Extract animation for tree type 2 in wind -- birch, for sure */
        treeImageCollection.addImagesForTree(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.BIRCH, getImagesAt(mapBobsLst, MapBobsLst.BIRCH_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.BIRCH, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.BIRCH_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.BIRCH, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.BIRCH_SHADOW_MEDIUM));

        /* Extract animation for tree type 3 in wind -- oak */
        treeImageCollection.addImagesForTree(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.OAK, getImagesAt(mapBobsLst, MapBobsLst.OAK_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.OAK_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.OAK_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.OAK, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.OAK_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.OAK, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.OAK_SHADOW_MEDIUM));

        /* Extract animation for tree type 4 in wind -- short palm */
        treeImageCollection.addImagesForTree(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PALM_1, getImagesAt(mapBobsLst, MapBobsLst.PALM_1_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_1, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_1_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_1, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_1_SHADOW_ALMOST_GROWN));

        /* Extract animation for tree type 5 in wind -- tall palm */
        treeImageCollection.addImagesForTree(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PALM_2, getImagesAt(mapBobsLst, MapBobsLst.PALM_2_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PALM_2, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_2_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALLEST));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_SMALL));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PALM_2, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PALM_2_SHADOW_ALMOST_GROWN));

        /* Extract animation for tree type 6 in wind -- fat palm - pineapple */
        treeImageCollection.addImagesForTree(Tree.TreeType.PINE_APPLE, getImagesAt(mapBobsLst, MapBobsLst.PINE_APPLE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PINE_APPLE, getImagesAt(mapBobsLst, MapBobsLst.PINE_APPLE_SHADOW_ANIMATION, 8));

        /* Extract animation for tree type 7 in wind -- pine */
        treeImageCollection.addImagesForTree(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.PINE, getImagesAt(mapBobsLst, MapBobsLst.PINE_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.PINE, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.PINE_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.PINE, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.PINE_ALMOST_GROWN_SHADOW));

        /* Extract animation for tree type 8 in wind -- cherry */
        treeImageCollection.addImagesForTree(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.CHERRY, getImagesAt(mapBobsLst, MapBobsLst.CHERRY_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.CHERRY, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.CHERRY_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.CHERRY, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.CHERRY_ALMOST_GROWN_SHADOW));

        /* Extract animation for tree type 9 in wind -- fir (?) */
        treeImageCollection.addImagesForTree(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_TREE_ANIMATION, 8));
        treeImageCollection.addImagesForTreeShadow(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_TREE_SHADOW_ANIMATION, 8));

        treeImageCollection.addImagesForTreeFalling(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_FALLING, 4));
        treeImageCollection.addImagesForTreeFallingShadow(Tree.TreeType.FIR, getImagesAt(mapBobsLst, MapBobsLst.FIR_FALLING_SHADOW, 4));

        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALLEST));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALL));
        treeImageCollection.addImageForGrowingTree(Tree.TreeType.FIR, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN));

        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.NEWLY_PLANTED, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALLEST_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.SMALL, getImageAt(mapBobsLst, MapBobsLst.FIR_SMALL_SHADOW));
        treeImageCollection.addImageForGrowingTreeShadow(Tree.TreeType.FIR, Tree.TreeSize.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.FIR_ALMOST_GROWN_SHADOW));

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
        iceBearImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_WEST_ANIMATION, 6));
        iceBearImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_WEST_ANIMATION, 6));
        iceBearImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_WEST_ANIMATION, 6));

        /* Fox */
        foxImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_EAST_ANIMATION, 6));
        foxImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_EAST_ANIMATION, 6));
        foxImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_EAST_ANIMATION, 6));
        foxImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_WEST_ANIMATION, 6));
        foxImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_WEST_ANIMATION, 6));
        foxImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_WEST_ANIMATION, 6));

        foxImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_EAST));
        foxImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_EAST));
        foxImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_WEST));
        foxImageCollection.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_WEST));
        foxImageCollection.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_WEST));
        foxImageCollection.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_EAST));

        /* Rabbit */
        rabbitImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_WEST_ANIMATION, 6));
        rabbitImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_WEST_ANIMATION, 6));
        rabbitImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_WEST_ANIMATION, 6));

        /* Stag */
        stagImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_EAST_ANIMATION, 8));
        stagImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_EAST_ANIMATION, 8));
        stagImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_EAST_ANIMATION, 8));
        stagImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_WEST_ANIMATION, 8));
        stagImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_WEST_ANIMATION, 8));
        stagImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_WEST_ANIMATION, 8));

        stagImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_EAST));
        stagImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_EAST));
        stagImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_WEST));
        stagImageCollection.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_WEST));
        stagImageCollection.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_WEST));
        stagImageCollection.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_EAST));

        /* Deer */
        deerImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_EAST_ANIMATION, 8));
        deerImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_EAST_ANIMATION, 8));
        deerImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_EAST_ANIMATION, 8));
        deerImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_WEST_ANIMATION, 8));
        deerImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_WEST_ANIMATION, 8));
        deerImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_WEST_ANIMATION, 8));

        deerImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_EAST));
        deerImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_EAST));
        deerImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_WEST));
        deerImageCollection.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_WEST));
        deerImageCollection.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_WEST));
        deerImageCollection.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_EAST));

        /* Sheep */
        sheepImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_WEST_ANIMATION, 2));
        sheepImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_WEST_ANIMATION, 2));
        sheepImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_WEST_ANIMATION, 2));

        /* Deer 2 (horse?) */
        deer2ImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_WEST_ANIMATION, 8));
        deer2ImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_WEST_ANIMATION, 8));
        deer2ImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_WEST_ANIMATION, 8));

        deer2ImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_EAST));
        deer2ImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_EAST));
        deer2ImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_WEST));

        /* Extract duck */
        duckImageCollection.addImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST));
        duckImageCollection.addImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 1));
        duckImageCollection.addImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 2));
        duckImageCollection.addImage(WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 3));
        duckImageCollection.addImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 4));
        duckImageCollection.addImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 5));

        duckImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_SHADOW));

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

        donkeyImageCollection.addImages(EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_ANIMATION, 8));
        donkeyImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_ANIMATION, 8));
        donkeyImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_EAST_ANIMATION, 8));

        donkeyImageCollection.addShadowImage(EAST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_SHADOW));
        donkeyImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_SHADOW));
        donkeyImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_SHADOW));

        donkeyImageCollection.addCargoImage(BEER, getImageAt(map0ZLst, Map0ZLst.DONKEY_BEER));
        donkeyImageCollection.addCargoImage(TONGS, getImageAt(map0ZLst, Map0ZLst.DONKEY_TONGS));
        donkeyImageCollection.addCargoImage(HAMMER, getImageAt(map0ZLst, Map0ZLst.DONKEY_HAMMER));
        donkeyImageCollection.addCargoImage(AXE, getImageAt(map0ZLst, Map0ZLst.DONKEY_AXE));
        donkeyImageCollection.addCargoImage(SAW, getImageAt(map0ZLst, Map0ZLst.DONKEY_SAW));
        donkeyImageCollection.addCargoImage(PICK_AXE, getImageAt(map0ZLst, Map0ZLst.DONKEY_PICK_AXE));
        donkeyImageCollection.addCargoImage(SHOVEL, getImageAt(map0ZLst, Map0ZLst.DONKEY_SHOVEL));
        donkeyImageCollection.addCargoImage(CRUCIBLE, getImageAt(map0ZLst, Map0ZLst.DONKEY_CRUCIBLE));
        donkeyImageCollection.addCargoImage(FISHING_ROD, getImageAt(map0ZLst, Map0ZLst.DONKEY_FISHING_ROD));
        donkeyImageCollection.addCargoImage(SCYTHE, getImageAt(map0ZLst, Map0ZLst.DONKEY_SCYTHE));
        //donkeyImageCollection.addCargoImage(, getImageFromResourceLocation(map0ZLst, Map0ZLst.DONKEY_EMPTY_BARREL));
        donkeyImageCollection.addCargoImage(WATER, getImageAt(map0ZLst, Map0ZLst.DONKEY_WATER));
        donkeyImageCollection.addCargoImage(CLEAVER, getImageAt(map0ZLst, Map0ZLst.DONKEY_CLEAVER));
        donkeyImageCollection.addCargoImage(ROLLING_PIN, getImageAt(map0ZLst, Map0ZLst.DONKEY_ROLLING_PIN));
        donkeyImageCollection.addCargoImage(BOW, getImageAt(map0ZLst, Map0ZLst.DONKEY_BOW));
        donkeyImageCollection.addCargoImage(BOAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_BOAT));
        donkeyImageCollection.addCargoImage(SWORD, getImageAt(map0ZLst, Map0ZLst.DONKEY_SWORD));
        donkeyImageCollection.addCargoImage(IRON_BAR, getImageAt(map0ZLst, Map0ZLst.DONKEY_IRON_BAR));
        donkeyImageCollection.addCargoImage(FLOUR, getImageAt(map0ZLst, Map0ZLst.DONKEY_FLOUR));
        donkeyImageCollection.addCargoImage(FISH, getImageAt(map0ZLst, Map0ZLst.DONKEY_FISH));
        donkeyImageCollection.addCargoImage(BREAD, getImageAt(map0ZLst, Map0ZLst.DONKEY_BREAD));
        donkeyImageCollection.addNationSpecificCargoImage(ROMANS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_ROMAN_SHIELD));
        donkeyImageCollection.addCargoImage(WOOD, getImageAt(map0ZLst, Map0ZLst.DONKEY_WOOD));
        donkeyImageCollection.addCargoImage(PLANK, getImageAt(map0ZLst, Map0ZLst.DONKEY_PLANK));
        donkeyImageCollection.addCargoImage(STONE, getImageAt(map0ZLst, Map0ZLst.DONKEY_STONE));
        donkeyImageCollection.addNationSpecificCargoImage(VIKINGS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_VIKING_SHIELD));
        donkeyImageCollection.addNationSpecificCargoImage(AFRICANS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_AFRICAN_SHIELD));
        donkeyImageCollection.addCargoImage(WHEAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_WHEAT));
        donkeyImageCollection.addCargoImage(COIN, getImageAt(map0ZLst, Map0ZLst.DONKEY_COIN));
        donkeyImageCollection.addCargoImage(GOLD, getImageAt(map0ZLst, Map0ZLst.DONKEY_GOLD));
        donkeyImageCollection.addCargoImage(IRON, getImageAt(map0ZLst, Map0ZLst.DONKEY_IRON));
        donkeyImageCollection.addCargoImage(COAL, getImageAt(map0ZLst, Map0ZLst.DONKEY_COAL));
        donkeyImageCollection.addCargoImage(MEAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_MEAT));
        donkeyImageCollection.addCargoImage(PIG, getImageAt(map0ZLst, Map0ZLst.DONKEY_PIG));
        donkeyImageCollection.addNationSpecificCargoImage(JAPANESE, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_JAPANESE_SHIELD));

        donkeyImageCollection.writeImageAtlas(natureDir + "/animals/", defaultPalette);

        /*  Extract decorative elements */
        DecorativeImageCollection decorativeImageCollection = new DecorativeImageCollection();

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_BROWN_MUSHROOM,
                getImageAt(mapBobs0Lst, MapBobs0Lst.MINI_BROWN_MUSHROOM),
                getImageAt(mapBobs0Lst, MapBobs0Lst.MINI_BROWN_MUSHROOM_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.TOADSTOOL,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MUSHROOM),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MUSHROOM_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_STONE,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONE),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONE_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_STONE,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONES),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.STONE,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_STONE),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_STONE_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.DEAD_TREE_LYING_DOWN,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_FALLEN_TREE),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_FALLEN_TREE_SHADOW));
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.DEAD_TREE,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_STANDING_DEAD_TREE),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_STANDING_DEAD_TREE_SHADOW)
                );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.ANIMAL_SKELETON_1,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_ANIMAL_SKELETON_1),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_ANIMAL_SKELETON_1_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.ANIMAL_SKELETON_2,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_ANIMAL_SKELETON_2),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_ANIMAL_SKELETON_2_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.FLOWERS,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_FLOWERS),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_FLOWERS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.LARGE_BUSH,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_LARGE_BUSH),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_LARGE_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.PILE_OF_STONES,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_LARGER_STONES),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_LARGER_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.CACTUS_1,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_1),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_1_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.CACTUS_2,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_2),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_CACTUS_2_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.CATTAIL,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_BEACH_GRASS),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_BEACH_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.STONE_REMAINING_STYLE_1,
                getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_MINI),
                getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_1_MINI_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.STONE_REMAINING_STYLE_2,
                getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_MINI),
                getImageAt(mapBobsLst, MapBobsLst.STONE_TYPE_2_MINI_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.GRASS_1,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_GRASS),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationTypeImage(
                DecorationType.TREE_STUB,
                getImageAt(mapBobsLst, MapBobsLst.TREE_STUB)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.BUSH,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_BUSH),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_BUSH,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_BUSH),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_SMALL_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_BUSH,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_BUSH),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.GRASS_2,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_GRASS_2),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_GRASS_2_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_GRASS,
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_GRASS),
                getImageAt(mapBobsLst, MapBobsLst.DECORATIVE_MINI_GRASS_SHADOW)
        );

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.HUMAN_SKELETON_1,
                getImageAt(mapBobs0Lst, MapBobs0Lst.HUMAN_SKELETON_1),
                getImageAt(mapBobs0Lst, MapBobs0Lst.HUMAN_SKELETON_1_SHADOW)
        );

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.HUMAN_SKELETON_2,
                getImageAt(mapBobs0Lst, MapBobs0Lst.HUMAN_SKELETON_2),
                getImageAt(mapBobs0Lst, MapBobs0Lst.HUMAN_SKELETON_2_SHADOW)
        );

        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.PORTAL,
                getImageAt(mapBobs0Lst, MapBobs0Lst.PORTAL),
                getImageAt(mapBobs0Lst, MapBobs0Lst.PORTAL_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SHINING_PORTAL,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SHINING_PORTAL),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SHINING_PORTAL_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.BROWN_MUSHROOM,
                getImageAt(mapBobs0Lst, MapBobs0Lst.BROWN_MUSHROOM),
                getImageAt(mapBobs0Lst, MapBobs0Lst.BROWN_MUSHROOM_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.MINI_STONE_WITH_GRASS,
                getImageAt(mapBobs0Lst, MapBobs0Lst.MINI_STONE_WITH_GRASS),
                getImageAt(mapBobs0Lst, MapBobs0Lst.MINI_STONE_WITH_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SMALL_STONE_WITH_GRASS,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SMALL_STONE_WITH_GRASS),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SMALL_STONE_WITH_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SOME_SMALL_STONES,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SOME_SMALL_STONES),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SOME_SMALL_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SOME_SMALLER_STONES,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SOME_SMALLER_STONES),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SOME_SMALLER_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.FEW_SMALL_STONES,
                getImageAt(mapBobs0Lst, MapBobs0Lst.FEW_SMALL_STONES),
                getImageAt(mapBobs0Lst, MapBobs0Lst.FEW_SMALL_STONES_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SPARSE_BUSH,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SPARSE_BUSH),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SPARSE_BUSH_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SOME_WATER,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SOME_WATER),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SOME_WATER_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.LITTLE_GRASS,
                getImageAt(mapBobs0Lst, MapBobs0Lst.LITTLE_GRASS),
                getImageAt(mapBobs0Lst, MapBobs0Lst.LITTLE_GRASS_SHADOW)
        );
        decorativeImageCollection.addDecorationImageWithShadow(
                DecorationType.SNOWMAN,
                getImageAt(mapBobs0Lst, MapBobs0Lst.SNOWMAN),
                getImageAt(mapBobs0Lst, MapBobs0Lst.SNOWMAN_SHADOW)
        );

        decorativeImageCollection.writeImageAtlas(toDir, defaultPalette);
    }

    private void loadPalettes(String fromDir) throws IOException {
        defaultPalette = PaletteDecoder.loadPaletteFromFile(FALLBACK_PALETTE);

        fallbackPalette = PaletteDecoder.loadPaletteFromFile(FALLBACK_PALETTE);
    }

    /**
     *
     * @param fromDir
     * @param toDir
     * @throws InvalidFormatException
     * @throws UnknownResourceTypeException
     * @throws IOException
     */
    private void populateRomanBuildings(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, IOException {

        /* Load from the roman asset file */
        List<GameResource> romYLst = LstDecoder.loadLstFile(fromDir + "/" + RomYLst.FILENAME, defaultPalette);

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

        BuildingsExtractor.extract(fromDir, toDir, defaultPalette);
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
