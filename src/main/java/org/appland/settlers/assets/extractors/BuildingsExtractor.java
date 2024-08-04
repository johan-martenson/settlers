package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.BuildingsImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.AfrYLst;
import org.appland.settlers.assets.gamefiles.JapYLst;
import org.appland.settlers.assets.gamefiles.RomYLst;
import org.appland.settlers.assets.gamefiles.VikYLst;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;

import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.getImageAt;

public class BuildingsExtractor {
    public static void extract(String fromDir, String toDir, Palette palette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        BuildingsImageCollection buildingsImageCollection = new BuildingsImageCollection();

        var romYLst = LstDecoder.loadLstFile(fromDir + "/" + RomYLst.FILENAME, palette);
        var japYLst = LstDecoder.loadLstFile(fromDir + "/" + JapYLst.FILENAME, palette);
        var afrYLst = LstDecoder.loadLstFile(fromDir + "/" + AfrYLst.FILENAME, palette);
        var vikYLst = LstDecoder.loadLstFile(fromDir + "/" + VikYLst.FILENAME, palette);

        // Load roman buildings
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.MILL);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.MINT);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.WELL);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.FARM);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(romYLst, ROMANS, RomYLst.HARBOR);

        buildingsImageCollection.addConstructionPlanned(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        // Load japanese buildings
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.MILL);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.MINT);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.WELL);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.FARM);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(japYLst, JAPANESE, JapYLst.HARBOR);

        buildingsImageCollection.addConstructionPlanned(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        // Load african buildings
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.MILL);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.MINT);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.WELL);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.FARM);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(afrYLst, AFRICANS, AfrYLst.HARBOR);

        buildingsImageCollection.addConstructionPlanned(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        // Load viking buildings
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.HEADQUARTER);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.BARRACKS);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.GUARDHOUSE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.WATCHTOWER);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.FORTRESS);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.GRANITE_MINE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.COAL_MINE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.IRON_MINE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.GOLD_MINE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.LOOKOUT_TOWER);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.CATAPULT);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.WOODCUTTER);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.FISHERY);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.QUARRY);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.FORESTER_HUT);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.SLAUGHTER_HOUSE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.BREWERY);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.ARMORY);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.METALWORKS);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.IRON_SMELTER);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.PIG_FARM);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.STOREHOUSE);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.MILL);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.BAKERY);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.SAWMILL);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.MINT);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.WELL);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.SHIPYARD);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.FARM);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.DONKEY_BREEDER);
        buildingsImageCollection.addImagesForBuilding(vikYLst, VIKINGS, VikYLst.HARBOR);

        buildingsImageCollection.addConstructionPlanned(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        buildingsImageCollection.writeImageAtlas(toDir + "/", palette);
    }
}
