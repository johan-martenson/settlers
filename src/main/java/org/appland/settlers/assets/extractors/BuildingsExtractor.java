package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.BuildingsImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.AfrZLst;
import org.appland.settlers.assets.gamefiles.CbobRomBobsLst;
import org.appland.settlers.assets.gamefiles.JapZLst;
import org.appland.settlers.assets.gamefiles.RomZLst;
import org.appland.settlers.assets.gamefiles.VikZLst;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;

import static java.lang.String.format;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.getImageAt;
import static org.appland.settlers.assets.utils.ImageUtils.composeBuildingAnimation;

public class BuildingsExtractor {

    /**
     * Extracts buildings and their construction states for different nations and writes them into an image atlas.
     *
     * @param fromDir The directory containing the source LST files
     * @param toDir The directory to write the resulting image atlas
     * @param palette The color palette to apply to the images
     * @throws UnknownResourceTypeException If an unknown resource type is encountered
     * @throws IOException If an I/O error occurs during file operations
     * @throws InvalidFormatException If the format of the LST files is invalid
     */
    public static void extractBuildingAssets(String fromDir, String toDir, Palette palette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var buildingsImageCollection = new BuildingsImageCollection();

        var romZLst = LstDecoder.loadLstFile(String.format("%s/%s", fromDir, RomZLst.FILENAME), palette);
        var japZLst = LstDecoder.loadLstFile(String.format("%s/%s", fromDir, JapZLst.FILENAME), palette);
        var afrZLst = LstDecoder.loadLstFile(String.format("%s/%s", fromDir, AfrZLst.FILENAME), palette);
        var vikZLst = LstDecoder.loadLstFile(String.format("%s/%s", fromDir, VikZLst.FILENAME), palette);

        var cbobRomBobsLst = LstDecoder.loadLstFile(format("%s/%s", fromDir, CbobRomBobsLst.FILENAME), palette);

        // Load roman buildings
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.HUNTER_HUT);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.MINT);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.WELL);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.FARM);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(romZLst, ROMANS, RomZLst.HARBOR);

        // Compose animations
        buildingsImageCollection.addBuildingWorkingAnimation(
                ROMANS,
                RomZLst.HARBOR.name(),
                composeBuildingAnimation(romZLst, RomZLst.HARBOR.index(), RomZLst.HARBOR_ANIMATION, 8, 1));
        buildingsImageCollection.addBuildingWorkingAnimation(
                ROMANS,
                RomZLst.ARMORY.name(),
                composeBuildingAnimation(romZLst, RomZLst.ARMORY.index(), cbobRomBobsLst, CbobRomBobsLst.HAMMERING_ARMOR.index(), CbobRomBobsLst.HAMMERING_ARMOR.length(), 1, -12, 15));
        buildingsImageCollection.addBuildingWorkingAnimation(
                ROMANS,
                RomZLst.IRON_SMELTER.name(),
                composeBuildingAnimation(romZLst, RomZLst.IRON_SMELTER.index(), cbobRomBobsLst, CbobRomBobsLst.IRON_FOUNDER_WORKING.index(), CbobRomBobsLst.IRON_FOUNDER_WORKING.length(),  1, -19, 8));
        buildingsImageCollection.addBuildingWorkingAnimation(
                ROMANS,
                RomZLst.MINT.name(),
                composeBuildingAnimation(romZLst, RomZLst.MINT.index(), cbobRomBobsLst, CbobRomBobsLst.MINTING_COIN.index(), CbobRomBobsLst.MINTING_COIN.length(), 1, 22, -12));

        // Compose the mine animations
        var romanCoalMineAnimation = composeBuildingAnimation(romZLst, RomZLst.COAL_MINE.index(), cbobRomBobsLst, CbobRomBobsLst.DIGGING_FOR_ORE.index(), CbobRomBobsLst.DIGGING_FOR_ORE.length(), 1, 9, 5);
        var romanIronMineAnimation = composeBuildingAnimation(romZLst, RomZLst.IRON_MINE.index(), cbobRomBobsLst, CbobRomBobsLst.DIGGING_FOR_ORE.index(), CbobRomBobsLst.DIGGING_FOR_ORE.length(), 1, 9, 5);
        var romanGoldMineAnimation = composeBuildingAnimation(romZLst, RomZLst.GOLD_MINE.index(), cbobRomBobsLst, CbobRomBobsLst.DIGGING_FOR_ORE.index(), CbobRomBobsLst.DIGGING_FOR_ORE.length(), 1, 9, 5);
        var romanGraniteMineAnimation = composeBuildingAnimation(romZLst, RomZLst.GRANITE_MINE.index(), cbobRomBobsLst, CbobRomBobsLst.DIGGING_FOR_ORE.index(), CbobRomBobsLst.DIGGING_FOR_ORE.length(), 1, 9, 5);

        buildingsImageCollection.addBuildingWorkingAnimation(ROMANS, RomZLst.COAL_MINE.name(), romanCoalMineAnimation);
        buildingsImageCollection.addBuildingWorkingAnimation(ROMANS, RomZLst.IRON_MINE.name(), romanIronMineAnimation);
        buildingsImageCollection.addBuildingWorkingAnimation(ROMANS, RomZLst.GOLD_MINE.name(), romanGoldMineAnimation);
        buildingsImageCollection.addBuildingWorkingAnimation(ROMANS, RomZLst.GRANITE_MINE.name(), romanGraniteMineAnimation);

        // Compose the mill animation
        var romanMillAnimation = composeBuildingAnimation(romZLst, RomZLst.MILL.index(), RomZLst.MILL_SAIL_ANIMATION, 8, 2);
        var romanMillAnimationShadow = composeBuildingAnimation(romZLst, RomZLst.MILL.index() + 1, RomZLst.MILL_SAIL_ANIMATION + 1, 8, 2);

        // Add the mill images to the collector
        buildingsImageCollection.addBuildingForNation(ROMANS, RomZLst.MILL.name(), romanMillAnimation.getFirst());
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, RomZLst.MILL.name(), romanMillAnimationShadow.getFirst());
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, RomZLst.MILL.name(), getImageAt(romZLst, RomZLst.MILL.index() + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, RomZLst.MILL.name(), getImageAt(romZLst, RomZLst.MILL.index() + 3));
        buildingsImageCollection.addOpenDoorForBuilding(ROMANS, RomZLst.MILL.name(), getImageAt(romZLst, RomZLst.MILL.index() + 4));

        buildingsImageCollection.addBuildingWorkingAnimationWithShadow(ROMANS, RomZLst.MILL.name(), romanMillAnimation, romanMillAnimationShadow);

        buildingsImageCollection.addConstructionPlanned(ROMANS, getImageAt(romZLst, RomZLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(ROMANS, getImageAt(romZLst, RomZLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(ROMANS, getImageAt(romZLst, RomZLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(ROMANS, getImageAt(romZLst, RomZLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        // Load japanese buildings
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.HUNTER_HUT);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.MINT);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.WELL);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.FARM);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(japZLst, JAPANESE, JapZLst.HARBOR);

        // Compose the harbor animation -- it has no shadow!
        var japaneseHarborAnimation = composeBuildingAnimation(japZLst, JapZLst.HARBOR.index(), JapZLst.HARBOR_ANIMATION, 8, 1);

        buildingsImageCollection.addBuildingWorkingAnimation(JAPANESE, JapZLst.HARBOR.name(), japaneseHarborAnimation);

        // Compose the mill animation
        var japaneseMillAnimation = composeBuildingAnimation(japZLst, JapZLst.MILL.index(), JapZLst.MILL_SAIL_ANIMATION, 8, 2);
        var japaneseMillAnimationShadow = composeBuildingAnimation(japZLst, JapZLst.MILL.index() + 1, JapZLst.MILL_SAIL_ANIMATION + 1, 8, 2);

        // Add the mill images to the collector
        buildingsImageCollection.addBuildingForNation(JAPANESE, JapZLst.MILL.name(), japaneseMillAnimation.getFirst());
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, JapZLst.MILL.name(), japaneseMillAnimationShadow.getFirst());
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, JapZLst.MILL.name(), getImageAt(japZLst, JapZLst.MILL.index() + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, JapZLst.MILL.name(), getImageAt(japZLst, JapZLst.MILL.index() + 3));
        buildingsImageCollection.addOpenDoorForBuilding(JAPANESE, JapZLst.MILL.name(), getImageAt(japZLst, JapZLst.MILL.index() + 4));

        buildingsImageCollection.addBuildingWorkingAnimationWithShadow(JAPANESE, RomZLst.MILL.name(), japaneseMillAnimation, japaneseMillAnimationShadow);

        buildingsImageCollection.addConstructionPlanned(JAPANESE, getImageAt(japZLst, JapZLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(JAPANESE, getImageAt(japZLst, JapZLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(JAPANESE, getImageAt(japZLst, JapZLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(JAPANESE, getImageAt(japZLst, JapZLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        // Load african buildings
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.HUNTER_HUT);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.MINT);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.WELL);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.FARM);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(afrZLst, AFRICANS, AfrZLst.HARBOR);

        // Compose the mill animation
        var africanMillAnimation = composeBuildingAnimation(afrZLst, AfrZLst.MILL.index(), AfrZLst.MILL_SAIL_ANIMATION, 8, 2);
        var africanMillAnimationShadow = composeBuildingAnimation(afrZLst, AfrZLst.MILL.index() + 1, AfrZLst.MILL_SAIL_ANIMATION + 1, 8, 2);

        // Add the mill images to the collector
        buildingsImageCollection.addBuildingForNation(AFRICANS, AfrZLst.MILL.name(), africanMillAnimation.getFirst());
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, AfrZLst.MILL.name(), africanMillAnimationShadow.getFirst());
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, AfrZLst.MILL.name(), getImageAt(afrZLst, AfrZLst.MILL.index() + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, AfrZLst.MILL.name(), getImageAt(afrZLst, AfrZLst.MILL.index() + 3));
        buildingsImageCollection.addOpenDoorForBuilding(AFRICANS, AfrZLst.MILL.name(), getImageAt(afrZLst, AfrZLst.MILL.index() + 4));

        buildingsImageCollection.addBuildingWorkingAnimationWithShadow(AFRICANS, AfrZLst.MILL.name(), africanMillAnimation, africanMillAnimationShadow);

        buildingsImageCollection.addConstructionPlanned(AFRICANS, getImageAt(afrZLst, AfrZLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(AFRICANS, getImageAt(afrZLst, AfrZLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(AFRICANS, getImageAt(afrZLst, AfrZLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(AFRICANS, getImageAt(afrZLst, AfrZLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        // Load viking buildings
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.HUNTER_HUT);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.MINT);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.WELL);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.FARM);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(vikZLst, VIKINGS, VikZLst.HARBOR);

        // Compose the mill animation
        var vikingMillAnimation = composeBuildingAnimation(vikZLst, VikZLst.MILL.index(), VikZLst.MILL_SAIL_ANIMATION, 8, 2);
        var vikingMillAnimationShadow = composeBuildingAnimation(vikZLst, VikZLst.MILL.index() + 1, VikZLst.MILL_SAIL_ANIMATION + 1, 8, 2);

        // Add the mill images to the collector
        buildingsImageCollection.addBuildingForNation(VIKINGS, VikZLst.MILL.name(), vikingMillAnimation.getFirst());
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, VikZLst.MILL.name(), vikingMillAnimationShadow.getFirst());
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, VikZLst.MILL.name(), getImageAt(vikZLst, VikZLst.MILL.index() + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, VikZLst.MILL.name(), getImageAt(vikZLst, VikZLst.MILL.index() + 3));
        buildingsImageCollection.addOpenDoorForBuilding(VIKINGS, VikZLst.MILL.name(), getImageAt(vikZLst, VikZLst.MILL.index() + 4));

        buildingsImageCollection.addBuildingWorkingAnimationWithShadow(VIKINGS, VikZLst.MILL.name(), vikingMillAnimation, vikingMillAnimationShadow);

        buildingsImageCollection.addConstructionPlanned(VIKINGS, getImageAt(vikZLst, VikZLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(VIKINGS, getImageAt(vikZLst, VikZLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(VIKINGS, getImageAt(vikZLst, VikZLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(VIKINGS, getImageAt(vikZLst, VikZLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        buildingsImageCollection.writeImageAtlas(toDir + "/", palette);
    }
}
