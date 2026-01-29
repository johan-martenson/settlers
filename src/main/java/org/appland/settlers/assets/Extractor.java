package org.appland.settlers.assets;

import org.appland.settlers.assets.collectors.BorderImageCollector;
import org.appland.settlers.assets.collectors.CargoImageCollection;
import org.appland.settlers.assets.collectors.CropImageCollection;
import org.appland.settlers.assets.collectors.DecorativeImageCollection;
import org.appland.settlers.assets.collectors.FireImageCollection;
import org.appland.settlers.assets.collectors.InventoryImageCollection;
import org.appland.settlers.assets.collectors.RoadBuildingImageCollection;
import org.appland.settlers.assets.collectors.SignImageCollection;
import org.appland.settlers.assets.collectors.StonesImageCollection;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.decoders.PaletteDecoder;
import org.appland.settlers.assets.extractors.AnimalsExtractor;
import org.appland.settlers.assets.extractors.AudioExtractor;
import org.appland.settlers.assets.extractors.BackgroundImageExtractor;
import org.appland.settlers.assets.extractors.BuildingsExtractor;
import org.appland.settlers.assets.extractors.CursorExtractor;
import org.appland.settlers.assets.extractors.FlagExtractor;
import org.appland.settlers.assets.extractors.IconsExtractor;
import org.appland.settlers.assets.extractors.ShipExtractor;
import org.appland.settlers.assets.extractors.TreeExtractor;
import org.appland.settlers.assets.extractors.WorkersExtractor;
import org.appland.settlers.assets.gamefiles.Map0ZLst;
import org.appland.settlers.assets.gamefiles.MapBobs0Lst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.gamefiles.MbobAfrBobsLst;
import org.appland.settlers.assets.gamefiles.MbobJapBobsLst;
import org.appland.settlers.assets.gamefiles.MbobRomBobsLst;
import org.appland.settlers.assets.gamefiles.MbobVikBobsLst;
import org.appland.settlers.assets.gamefiles.Tex5Lbm;
import org.appland.settlers.assets.gamefiles.Tex7Lbm;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.SmokeType;
import org.appland.settlers.model.StoneAmount;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.*;
import static org.appland.settlers.model.Stone.StoneType.STONE_1;
import static org.appland.settlers.model.Stone.StoneType.STONE_2;

public class Extractor {
    private static final String FALLBACK_PALETTE = "/Users/s0001386/projects/settlers/src/main/resources/default-palette.act";
    private static final String UI_ELEMENTS_DIRECTORY = "ui-elements";
    private static final String NATURE_DIRECTORY = "nature";
    private static final String SIGNS_DIRECTORY = "signs";
    private static final String TERRAIN_SUB_DIRECTORY = "terrain";
    private static final String GREENLAND_DIRECTORY = "greenland";
    private static final String WINTER_DIRECTORY = "winter";

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

        // Load the palettes
        extractor.loadPalettes(fromDir);

        // Create output directories
        Utils.createDirectory(toDir + "/" + UI_ELEMENTS_DIRECTORY);
        Utils.createDirectory(toDir + "/" + NATURE_DIRECTORY);
        Utils.createDirectory(toDir + "/" + NATURE_DIRECTORY + "/animals");
        Utils.createDirectory(toDir + "/" + SIGNS_DIRECTORY);
        Utils.createDirectory(toDir + "/" + NATURE_DIRECTORY + "/" + TERRAIN_SUB_DIRECTORY);
        Utils.createDirectory(toDir + "/" + NATURE_DIRECTORY + "/" + TERRAIN_SUB_DIRECTORY + "/" + GREENLAND_DIRECTORY);
        Utils.createDirectory(toDir + "/" + NATURE_DIRECTORY + "/" + TERRAIN_SUB_DIRECTORY + "/" + WINTER_DIRECTORY);


        // Extract assets
        extractor.populateNatureAndUIElements(fromDir, toDir);

        WorkersExtractor.extractWorkerAssets(fromDir, toDir, extractor.defaultPalette);
        extractor.populateBorders(fromDir, toDir);
        CursorExtractor.extractCursors(fromDir, toDir, extractor.defaultPalette);
        BackgroundImageExtractor.extractBackgroundImages(fromDir, toDir, extractor.defaultPalette);
        IconsExtractor.extractIcons(fromDir, toDir, extractor.defaultPalette);
        FlagExtractor.extractFlags(fromDir, toDir, extractor.defaultPalette);
        TreeExtractor.extractTrees(fromDir, toDir + "/" + NATURE_DIRECTORY, extractor.defaultPalette);
        AnimalsExtractor.extractAnimals(fromDir, toDir + "/" + NATURE_DIRECTORY, extractor.defaultPalette);
        BuildingsExtractor.extractBuildingAssets(fromDir, toDir, extractor.defaultPalette);
        AudioExtractor.extractAudioAssets(fromDir, toDir, extractor.defaultPalette);
        ShipExtractor.extractShips(fromDir, toDir, extractor.defaultPalette);
    }

    private void populateBorders(String fromDir, String toDir) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var mbobAfrBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobAfrBobsLst.FILENAME, defaultPalette);
        var mbobJapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobJapBobsLst.FILENAME, defaultPalette);
        var mbobRomBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobRomBobsLst.FILENAME, defaultPalette);
        var mbobVikBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MbobVikBobsLst.FILENAME, defaultPalette);

        var borderImageCollector = new BorderImageCollector();

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

    /**
     * TEX5.LBM -- contains vegetation textures
     *
     */
    private void populateNatureAndUIElements(String fromDir, String toDir) throws InvalidFormatException, UnknownResourceTypeException, IOException {

        // Load from the map asset file
        var mapBobsLst = LstDecoder.loadLstFile(format("%s/%s", fromDir, MapBobsLst.FILENAME), defaultPalette);
        var mapBobs0Lst = LstDecoder.loadLstFile(format("%s/%s", fromDir, MapBobs0Lst.FILENAME), defaultPalette);
        var map0ZLst = LstDecoder.loadLstFile(format("%s/%s", fromDir, Map0ZLst.FILENAME), defaultPalette);

        // Extract the terrains
        var greenlandGameResource = (LBMGameResource) LbmDecoder.loadLBMFile(format("%s/%s", fromDir, Tex5Lbm.FILENAME), defaultPalette);
        var winterGameResource = (LBMGameResource) LbmDecoder.loadLBMFile(format("%s/%s", fromDir, Tex7Lbm.FILENAME), defaultPalette);

        var greenlandTextureBitmap = greenlandGameResource.getLbmFile();
        var winterTextureBitmap = winterGameResource.getLbmFile();

        greenlandTextureBitmap.writeToFile(toDir + "/" + NATURE_DIRECTORY + "/" + TERRAIN_SUB_DIRECTORY + "/" + GREENLAND_DIRECTORY + "/greenland-texture.png");
        winterTextureBitmap.writeToFile(toDir + "/" + NATURE_DIRECTORY + "/" + TERRAIN_SUB_DIRECTORY + "/" + WINTER_DIRECTORY + "/winter-texture.png");

        // Extract the stones
        var stonesImageCollection = new StonesImageCollection();

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


        // Extract the inventory icons
        var inventoryImageCollection = new InventoryImageCollection();

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

        //  Extract the crops
        var cropImageCollection = new CropImageCollection();

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

        // Extract the cargo images that workers carry
        var cargoImageCollection = new CargoImageCollection();

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
        cargoImageCollection.addCargoImage(IRON_BAR, getImageAt(mapBobsLst, MapBobsLst.IRON_BAR_CARGO));

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

        // Extract signs
        var signImageCollection = new SignImageCollection();

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

        // Extract road building icons
        var roadBuildingImageCollection = new RoadBuildingImageCollection();

        roadBuildingImageCollection.addStartPointImage(getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_START_POINT));
        roadBuildingImageCollection.addSameLevelConnectionImage(getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_SAME_LEVEL_CONNECTION));

        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.LITTLE, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_LITTLE_HIGHER_CONNECTION));
        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MEDIUM_HIGHER_CONNECTION));
        roadBuildingImageCollection.addUpwardsConnectionImage(RoadConnectionDifference.HIGH, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MUCH_HIGHER_CONNECTION));

        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.LITTLE, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_LITTLE_LOWER_CONNECTION));
        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.MEDIUM, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MEDIUM_LOWER_CONNECTION));
        roadBuildingImageCollection.addDownwardsConnectionImage(RoadConnectionDifference.HIGH, getImageAt(mapBobsLst, MapBobsLst.ROAD_BUILDING_MUCH_LOWER_CONNECTION));

        roadBuildingImageCollection.writeImageAtlas(toDir, defaultPalette);

        // Extract fire animation
        var fireImageCollection = new FireImageCollection();

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

        fireImageCollection.addSmokeAnimation(SmokeType.SMOKE_TYPE_1, getImagesAt(map0ZLst, Map0ZLst.SMALL_SMOKE_1));
        fireImageCollection.addSmokeAnimation(SmokeType.SMOKE_TYPE_2, getImagesAt(map0ZLst, Map0ZLst.SMALL_SMOKE_2));
        fireImageCollection.addSmokeAnimation(SmokeType.SMOKE_TYPE_3, getImagesAt(map0ZLst, Map0ZLst.MEDIUM_SMOKE));
        fireImageCollection.addSmokeAnimation(SmokeType.SMOKE_TYPE_4, getImagesAt(map0ZLst, Map0ZLst.LARGE_SMOKE));

        fireImageCollection.writeImageAtlas(toDir, defaultPalette);

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

        var decorativeImageCollection = new DecorativeImageCollection();

        decorationsWithShadows.forEach((decorationType, imageAndShadowIndex) ->
            decorativeImageCollection.addDecorationImageWithShadow(
                    decorationType,
                    getImageAt(mapBobs0Lst, imageAndShadowIndex.image()),
                    getImageAt(mapBobs0Lst, imageAndShadowIndex.shadow())));

        decorationsWithoutShadows.forEach((decoration, imageIndex) ->
            decorativeImageCollection.addDecorationImage(decoration, getImageAt(mapBobs0Lst, imageIndex.image())));

        decorativeImageCollection.writeImageAtlas(toDir, defaultPalette);
    }

    private void loadPalettes(String fromDir) throws IOException {
        defaultPalette = PaletteDecoder.loadPaletteFromFile(FALLBACK_PALETTE);
    }
}
