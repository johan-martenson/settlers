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
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.decoders.PaletteDecoder;
import org.appland.settlers.assets.extractors.BackgroundImageExtractor;
import org.appland.settlers.assets.extractors.BuildingsExtractor;
import org.appland.settlers.assets.extractors.CursorExtractor;
import org.appland.settlers.assets.extractors.IconsExtractor;
import org.appland.settlers.assets.extractors.WorkersExtractor;
import org.appland.settlers.assets.gamefiles.AfrZLst;
import org.appland.settlers.assets.gamefiles.BootBobsLst;
import org.appland.settlers.assets.gamefiles.IoLst;
import org.appland.settlers.assets.gamefiles.JapZLst;
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
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.WaveFile;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.StoneAmount;
import org.appland.settlers.model.Tree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.*;
import static org.appland.settlers.assets.utils.ImageUtils.writeImageResourceToFile;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.*;
import static org.appland.settlers.model.Stone.StoneType.STONE_1;
import static org.appland.settlers.model.Stone.StoneType.STONE_2;

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
        var extractor = new Extractor();
        var parser = new CmdLineParser(extractor);

        parser.parseArgument(args);

        if (!Utils.isDirectory(toDir) || !Utils.isEmptyDirectory(toDir)) {
            System.out.println("Must specify an empty directory to extract assets into: " + toDir);
        }

        /* Load the palettes */
        extractor.loadPalettes(fromDir);

        /* Extract assets */
        extractor.populateRomanBuildings(fromDir, toDir);

        extractor.populateNatureAndUIElements(fromDir, toDir);

        WorkersExtractor.extractWorkerAssets(fromDir, toDir, extractor.defaultPalette);

        extractor.populateFlags(fromDir, toDir);

        extractor.populateBorders(fromDir, toDir);

        extractor.populateShips(fromDir, toDir);

        extractor.populateAudio(fromDir, toDir);

        CursorExtractor.extractCursors(fromDir, toDir, extractor.defaultPalette);

        BackgroundImageExtractor.extractBackgroundImages(fromDir, toDir, extractor.defaultPalette);

        IconsExtractor.extractIcons(fromDir, toDir, extractor.defaultPalette);
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

        borderImageCollector.addSummerBorderImage(AFRICANS, getPlayerImageAt(mbobAfrBobsLst, MbobAfrBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWinterBorderImage(AFRICANS, getPlayerImageAt(mbobAfrBobsLst, MbobAfrBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addSummerBorderImage(JAPANESE, getPlayerImageAt(mbobJapBobsLst, MbobJapBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWinterBorderImage(JAPANESE, getPlayerImageAt(mbobJapBobsLst, MbobJapBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addSummerBorderImage(ROMANS, getPlayerImageAt(mbobRomBobsLst, MbobRomBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWinterBorderImage(ROMANS, getPlayerImageAt(mbobRomBobsLst, MbobRomBobsLst.COAST_BORDER_ICON));

        borderImageCollector.addSummerBorderImage(VIKINGS, getPlayerImageAt(mbobVikBobsLst, MbobVikBobsLst.LAND_BORDER_ICON));
        borderImageCollector.addWinterBorderImage(VIKINGS, getPlayerImageAt(mbobVikBobsLst, MbobVikBobsLst.COAST_BORDER_ICON));

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
        inventoryImageCollection.addIcon(CARPENTER, getImageAt(mapBobsLst, MapBobsLst.SAWMILL_WORKER_ICON));
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

        // Extract decorative elements
        var decorationsWithShadows = Map.ofEntries(
                Map.entry(DecorationType.MINI_BROWN_MUSHROOM, MapBobs0Lst.MINI_BROWN_MUSHROOM_AND_SHADOW),
                Map.entry(DecorationType.TOADSTOOL, MapBobs0Lst.TOADSTOOL_AND_SHADOW),
                Map.entry(DecorationType.MINI_STONE, MapBobs0Lst.MINI_STONE_AND_SHADOW),
                Map.entry(DecorationType.SMALL_STONE, MapBobs0Lst.SMALL_STONE_AND_SHADOW),
                Map.entry(DecorationType.STONE, MapBobs0Lst.STONE_AND_SHADOW),
                Map.entry(DecorationType.DEAD_TREE_LYING_DOWN, MapBobs0Lst.DEAD_TREE_LYING_DOWN_AND_SHADOW),
                Map.entry(DecorationType.DEAD_TREE, MapBobs0Lst.DEAD_TREE_AND_SHADOW),
                Map.entry(DecorationType.ANIMAL_SKELETON_1, MapBobs0Lst.ANIMAL_SKELETON_1_AND_SHADOW),
                Map.entry(DecorationType.ANIMAL_SKELETON_2, MapBobs0Lst.ANIMAL_SKELETON_2_AND_SHADOW),
                Map.entry(DecorationType.FLOWERS, MapBobs0Lst.FLOWERS_AND_SHADOW),
                Map.entry(DecorationType.LARGE_BUSH_1, MapBobs0Lst.LARGE_BUSH_1_AND_SHADOW),
                Map.entry(DecorationType.PILE_OF_STONES, MapBobs0Lst.PILE_OF_STONES_AND_SHADOW),
                Map.entry(DecorationType.CACTUS_1, MapBobs0Lst.CACTUS_1_AND_SHADOW),
                Map.entry(DecorationType.CACTUS_2, MapBobs0Lst.CACTUS_2_AND_SHADOW),
                Map.entry(DecorationType.CATTAIL_1, MapBobs0Lst.CATTAIL_1_AND_SHADOW),
                Map.entry(DecorationType.CATTAIL_2, MapBobs0Lst.CATTAIL_2_AND_SHADOW),
                Map.entry(DecorationType.LARGE_BUSH_2, MapBobs0Lst.LARGE_BUSH_2_AND_SHADOW),
                Map.entry(DecorationType.BUSH_3, MapBobs0Lst.BUSH_3_AND_SHADOW),
                Map.entry(DecorationType.SMALL_BUSH, MapBobs0Lst.SMALL_BUSH_AND_SHADOW),
                Map.entry(DecorationType.CATTAIL_3, MapBobs0Lst.CATTAIL_3_AND_SHADOW),
                Map.entry(DecorationType.CATTAIL_4, MapBobs0Lst.CATTAIL_4_AND_SHADOW),
                Map.entry(DecorationType.PORTAL, MapBobs0Lst.PORTAL_AND_SHADOW),
                Map.entry(DecorationType.SHINING_PORTAL, MapBobs0Lst.SHINING_PORTAL_AND_SHADOW),
                Map.entry(DecorationType.BROWN_MUSHROOM, MapBobs0Lst.BROWN_MUSHROOM_AND_SHADOW),
                Map.entry(DecorationType.SMALL_STONE_WITH_GRASS, MapBobs0Lst.SMALL_STONE_WITH_GRASS_AND_SHADOW),
                Map.entry(DecorationType.SOME_SMALL_STONES_1, MapBobs0Lst.SOME_SMALL_STONES_1_AND_SHADOW),
                Map.entry(DecorationType.SOME_SMALL_STONES_2, MapBobs0Lst.SOME_SMALL_STONES_2_AND_SHADOW),
                Map.entry(DecorationType.SOME_SMALL_STONES_3, MapBobs0Lst.SOME_SMALL_STONES_3_AND_SHADOW),
                Map.entry(DecorationType.SPARSE_BUSH, MapBobs0Lst.SPARSE_BUSH_AND_SHADOW),
                Map.entry(DecorationType.SOME_WATER, MapBobs0Lst.SOME_WATER_AND_SHADOW),
                Map.entry(DecorationType.LITTLE_GRASS, MapBobs0Lst.LITTLE_GRASS_AND_SHADOW),
                Map.entry(DecorationType.SNOWMAN, MapBobs0Lst.SNOWMAN_AND_SHADOW),
                Map.entry(DecorationType.TREE_STUB, MapBobs0Lst.TREE_STUB_AND_SHADOW)
        );

        var decorationsWithoutShadows = Map.of(
                DecorationType.MINI_STONE_WITH_GRASS, MapBobs0Lst.MINI_STONE_WITH_GRASS_AND_SHADOW
        );

        DecorativeImageCollection decorativeImageCollection = new DecorativeImageCollection();

        decorationsWithShadows.forEach((decorationType, imageAndShadowIndex) -> {
            var image = getImageAt(mapBobs0Lst, imageAndShadowIndex.image());
            var shadow = getImageAt(mapBobs0Lst, imageAndShadowIndex.shadow());
            decorativeImageCollection.addDecorationImageWithShadow(decorationType, image, shadow);
        });

        decorationsWithoutShadows.forEach((decoration, imageIndex) -> {
            var image = getImageAt(mapBobs0Lst, imageIndex.image());
            decorativeImageCollection.addDecorationImage(decoration, image);
        });

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
        imagesToFileMap.put(RomYLst.HEADQUARTER.index(), buildingsDir + "/headquarter.png");
        imagesToFileMap.put(RomYLst.BARRACKS.index(), buildingsDir + "/barracks.png");
        imagesToFileMap.put(RomYLst.BARRACKS.index() + 2, buildingsDir + "/barracks-under-construction.png");
        imagesToFileMap.put(RomYLst.GUARDHOUSE.index(), buildingsDir + "/guardhouse.png");
        imagesToFileMap.put(RomYLst.GUARDHOUSE.index() + 2, buildingsDir + "/guardhouse-under-construction.png");
        imagesToFileMap.put(RomYLst.WATCHTOWER.index(), buildingsDir + "/watchtower.png");
        imagesToFileMap.put(RomYLst.WATCHTOWER.index() + 2, buildingsDir + "/watchtower-under-construction.png");
        imagesToFileMap.put(RomYLst.FORTRESS.index(), buildingsDir + "/fortress.png");
        imagesToFileMap.put(RomYLst.FORTRESS.index() + 2, buildingsDir + "/fortress-under-construction.png");
        imagesToFileMap.put(RomYLst.GRANITE_MINE.index(), buildingsDir + "/granite-mine.png");
        imagesToFileMap.put(RomYLst.GRANITE_MINE.index() + 2, buildingsDir + "/granite-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.COAL_MINE.index(), buildingsDir + "/coal-mine.png");
        imagesToFileMap.put(RomYLst.COAL_MINE.index() + 2, buildingsDir + "/coal-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.IRON_MINE.index(), buildingsDir + "/iron-mine.png");
        imagesToFileMap.put(RomYLst.IRON_MINE.index() + 2, buildingsDir + "/iron-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.GOLD_MINE.index(), buildingsDir + "/gold-mine.png");
        imagesToFileMap.put(RomYLst.GOLD_MINE.index() + 2, buildingsDir + "/gold-mine-under-construction.png");
        imagesToFileMap.put(RomYLst.LOOKOUT_TOWER.index(), buildingsDir + "/lookout-tower.png");
        imagesToFileMap.put(RomYLst.LOOKOUT_TOWER.index() + 2, buildingsDir + "/lookout-tower-under-construction.png");
        imagesToFileMap.put(RomYLst.CATAPULT.index(), buildingsDir + "/catapult.png");
        imagesToFileMap.put(RomYLst.CATAPULT.index() + 2, buildingsDir + "/catapult-under-construction.png");
        imagesToFileMap.put(RomYLst.WOODCUTTER.index(), buildingsDir + "/woodcutter.png");
        imagesToFileMap.put(RomYLst.WOODCUTTER.index() + 2, buildingsDir + "/woodcutter-under-construction.png");
        imagesToFileMap.put(RomYLst.FISHERY.index(), buildingsDir + "/fishery.png");
        imagesToFileMap.put(RomYLst.FISHERY.index() + 2, buildingsDir + "/fishery-under-construction.png");
        imagesToFileMap.put(RomYLst.QUARRY.index(), buildingsDir + "/quarry.png");
        imagesToFileMap.put(RomYLst.QUARRY.index() + 2, buildingsDir + "/quarry-under-construction.png");
        imagesToFileMap.put(RomYLst.FORESTER_HUT.index(), buildingsDir + "/forester-hut.png");
        imagesToFileMap.put(RomYLst.FORESTER_HUT.index() + 2, buildingsDir + "/forester-hut-under-construction.png");
        imagesToFileMap.put(RomYLst.SLAUGHTER_HOUSE.index(), buildingsDir + "/slaughter-house.png");
        imagesToFileMap.put(RomYLst.SLAUGHTER_HOUSE.index() + 2, buildingsDir + "/slaughter-house-under-construction.png");
        imagesToFileMap.put(RomYLst.HUNTER_HUT.index(), buildingsDir + "/hunter-hut.png");
        imagesToFileMap.put(RomYLst.HUNTER_HUT.index() + 2, buildingsDir + "/hunter-hut-under-construction.png");
        imagesToFileMap.put(RomYLst.BREWERY.index(), buildingsDir + "/brewery.png");
        imagesToFileMap.put(RomYLst.BREWERY.index() + 2, buildingsDir + "/brewery-under-construction.png");
        imagesToFileMap.put(RomYLst.ARMORY.index(), buildingsDir + "/armory.png");
        imagesToFileMap.put(RomYLst.ARMORY.index() + 2, buildingsDir + "/armory-under-construction.png");
        imagesToFileMap.put(RomYLst.METALWORKS.index(), buildingsDir + "/metalworks.png");
        imagesToFileMap.put(RomYLst.METALWORKS.index() + 2, buildingsDir + "/metalworks-under-construction.png");
        imagesToFileMap.put(RomYLst.IRON_SMELTER.index(), buildingsDir + "/iron-smelter.png");
        imagesToFileMap.put(RomYLst.IRON_SMELTER.index() + 2, buildingsDir + "/iron-smelter-under-construction.png");
        imagesToFileMap.put(RomYLst.PIG_FARM.index(), buildingsDir + "/pig-farm.png");
        imagesToFileMap.put(RomYLst.PIG_FARM.index() + 2, buildingsDir + "/pig-farm-under-construction.png");
        imagesToFileMap.put(RomYLst.STOREHOUSE.index(), buildingsDir + "/storehouse.png");
        imagesToFileMap.put(RomYLst.STOREHOUSE.index() + 2, buildingsDir + "/storehouse-under-construction.png");
        imagesToFileMap.put(RomYLst.MILL.index(), buildingsDir + "/mill-no-fan.png");
        imagesToFileMap.put(RomYLst.MILL.index() + 2, buildingsDir + "/mill-no-fan-under-construction.png");
        imagesToFileMap.put(RomYLst.BAKERY.index(), buildingsDir + "/bakery.png");
        imagesToFileMap.put(RomYLst.BAKERY.index() + 2, buildingsDir + "/bakery-under-construction.png");
        imagesToFileMap.put(RomYLst.SAWMILL.index(), buildingsDir + "/sawmill.png");
        imagesToFileMap.put(RomYLst.SAWMILL.index() + 2, buildingsDir + "/sawmill-under-construction.png");
        imagesToFileMap.put(RomYLst.MINT.index(), buildingsDir + "/mint.png");
        imagesToFileMap.put(RomYLst.MINT.index() + 2, buildingsDir + "/mint-under-construction.png");
        imagesToFileMap.put(RomYLst.WELL.index(), buildingsDir + "/well.png");
        imagesToFileMap.put(RomYLst.WELL.index() + 2, buildingsDir + "/well-under-construction.png");
        imagesToFileMap.put(RomYLst.SHIPYARD.index(), buildingsDir + "/shipyard.png");
        imagesToFileMap.put(RomYLst.SHIPYARD.index() + 2, buildingsDir + "/shipyard-under-construction.png");
        imagesToFileMap.put(RomYLst.FARM.index(), buildingsDir + "/farm.png");
        imagesToFileMap.put(RomYLst.FARM.index() + 2, buildingsDir + "/farm-under-construction.png");
        imagesToFileMap.put(RomYLst.DONKEY_BREEDER.index(), buildingsDir + "/donkey-breeder.png");
        imagesToFileMap.put(RomYLst.DONKEY_BREEDER.index() + 2, buildingsDir + "/donkey-breeder-under-construction.png");
        imagesToFileMap.put(RomYLst.HARBOR.index(), buildingsDir + "/harbor.png");
        imagesToFileMap.put(RomYLst.HARBOR.index() + 2, buildingsDir + "/harbor-under-construction.png");
        imagesToFileMap.put(RomYLst.CONSTRUCTION_PLANNED, buildingsDir + "/construction-planned-sign.png");
        imagesToFileMap.put(RomYLst.CONSTRUCTION_JUST_STARTED_INDEX, buildingsDir + "/construction-started-sign.png");

        writeFilesFromMap(romYLst, imagesToFileMap);

        BuildingsExtractor.extractBuildingAssets(fromDir, toDir, defaultPalette);
    }

    private void writeFilesFromMap(List<GameResource> gameResourceList, Map<Integer, String> imagesToFileMap) throws IOException {
        for (Entry<Integer, String> entry : imagesToFileMap.entrySet()) {
            GameResource gameResource = gameResourceList.get(entry.getKey());
            String outFilename = entry.getValue();

            writeImageResourceToFile(gameResource, outFilename);
        }
    }
}
