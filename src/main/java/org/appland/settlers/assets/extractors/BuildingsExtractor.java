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
        buildingsImageCollection.addBuildingForNation(ROMANS, "Headquarter", getImageAt(romYLst, RomYLst.HEADQUARTER));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Headquarter", getImageAt(romYLst, RomYLst.HEADQUARTER_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Barracks", getImageAt(romYLst, RomYLst.BARRACKS));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Barracks", getImageAt(romYLst, RomYLst.BARRACKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Barracks", getImageAt(romYLst, RomYLst.BARRACKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Barracks", getImageAt(romYLst, RomYLst.BARRACKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "GuardHouse", getImageAt(romYLst, RomYLst.GUARDHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "GuardHouse", getImageAt(romYLst, RomYLst.GUARDHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "GuardHouse", getImageAt(romYLst, RomYLst.GUARDHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "GuardHouse", getImageAt(romYLst, RomYLst.GUARDHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "WatchTower", getImageAt(romYLst, RomYLst.WATCHTOWER));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "WatchTower", getImageAt(romYLst, RomYLst.WATCHTOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "WatchTower", getImageAt(romYLst, RomYLst.WATCHTOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "WatchTower", getImageAt(romYLst, RomYLst.WATCHTOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Fortress", getImageAt(romYLst, RomYLst.FORTRESS));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Fortress", getImageAt(romYLst, RomYLst.FORTRESS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Fortress", getImageAt(romYLst, RomYLst.FORTRESS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Fortress", getImageAt(romYLst, RomYLst.FORTRESS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "GraniteMine", getImageAt(romYLst, RomYLst.GRANITE_MINE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "GraniteMine", getImageAt(romYLst, RomYLst.GRANITE_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "GraniteMine", getImageAt(romYLst, RomYLst.GRANITE_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "GraniteMine", getImageAt(romYLst, RomYLst.GRANITE_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "CoalMine", getImageAt(romYLst, RomYLst.COAL_MINE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "CoalMine", getImageAt(romYLst, RomYLst.COAL_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "CoalMine", getImageAt(romYLst, RomYLst.COAL_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "CoalMine", getImageAt(romYLst, RomYLst.COAL_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "IronMine", getImageAt(romYLst, RomYLst.IRON_MINE_RESOURCE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "IronMine", getImageAt(romYLst, RomYLst.IRON_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "IronMine", getImageAt(romYLst, RomYLst.IRON_MINE_RESOURCE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "IronMine", getImageAt(romYLst, RomYLst.IRON_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "GoldMine", getImageAt(romYLst, RomYLst.GOLD_MINE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "GoldMine", getImageAt(romYLst, RomYLst.GOLD_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "GoldMine", getImageAt(romYLst, RomYLst.GOLD_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "GoldMine", getImageAt(romYLst, RomYLst.GOLD_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "LookoutTower", getImageAt(romYLst, RomYLst.LOOKOUT_TOWER));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "LookoutTower", getImageAt(romYLst, RomYLst.LOOKOUT_TOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "LookoutTower", getImageAt(romYLst, RomYLst.LOOKOUT_TOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "LookoutTower", getImageAt(romYLst, RomYLst.LOOKOUT_TOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Catapult", getImageAt(romYLst, RomYLst.CATAPULT));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Catapult", getImageAt(romYLst, RomYLst.CATAPULT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Catapult", getImageAt(romYLst, RomYLst.CATAPULT + 2));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Woodcutter", getImageAt(romYLst, RomYLst.WOODCUTTER));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Woodcutter", getImageAt(romYLst, RomYLst.WOODCUTTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Woodcutter", getImageAt(romYLst, RomYLst.WOODCUTTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Woodcutter", getImageAt(romYLst, RomYLst.WOODCUTTER_UNDER_CONSTRUCTION_SHADOW));
        buildingsImageCollection.addOpenDoorForBuilding(ROMANS, "Woodcutter", getImageAt(romYLst, RomYLst.WOODCUTTER_OPEN_DOOR));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Fishery", getImageAt(romYLst, RomYLst.FISHERY));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Fishery", getImageAt(romYLst, RomYLst.FISHERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Fishery", getImageAt(romYLst, RomYLst.FISHERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Fishery", getImageAt(romYLst, RomYLst.FISHERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Quarry", getImageAt(romYLst, RomYLst.QUARRY));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Quarry", getImageAt(romYLst, RomYLst.QUARRY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Quarry", getImageAt(romYLst, RomYLst.QUARRY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Quarry", getImageAt(romYLst, RomYLst.QUARRY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "ForesterHut", getImageAt(romYLst, RomYLst.FORESTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "ForesterHut", getImageAt(romYLst, RomYLst.FORESTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "ForesterHut", getImageAt(romYLst, RomYLst.FORESTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "ForesterHut", getImageAt(romYLst, RomYLst.FORESTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "SlaughterHouse", getImageAt(romYLst, RomYLst.SLAUGHTER_HOUSE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "SlaughterHouse", getImageAt(romYLst, RomYLst.SLAUGHTER_HOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "SlaughterHouse", getImageAt(romYLst, RomYLst.SLAUGHTER_HOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "SlaughterHouse", getImageAt(romYLst, RomYLst.SLAUGHTER_HOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "HunterHut", getImageAt(romYLst, RomYLst.HUNTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "HunterHut", getImageAt(romYLst, RomYLst.HUNTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "HunterHut", getImageAt(romYLst, RomYLst.HUNTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "HunterHut", getImageAt(romYLst, RomYLst.HUNTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Brewery", getImageAt(romYLst, RomYLst.BREWERY));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Brewery", getImageAt(romYLst, RomYLst.BREWERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Brewery", getImageAt(romYLst, RomYLst.BREWERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Brewery", getImageAt(romYLst, RomYLst.BREWERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Armory", getImageAt(romYLst, RomYLst.ARMORY));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Armory", getImageAt(romYLst, RomYLst.ARMORY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Armory", getImageAt(romYLst, RomYLst.ARMORY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Armory", getImageAt(romYLst, RomYLst.ARMORY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Metalworks", getImageAt(romYLst, RomYLst.METALWORKS));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Metalworks", getImageAt(romYLst, RomYLst.METALWORKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Metalworks", getImageAt(romYLst, RomYLst.METALWORKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Metalworks", getImageAt(romYLst, RomYLst.METALWORKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "IronSmelter", getImageAt(romYLst, RomYLst.IRON_SMELTER));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "IronSmelter", getImageAt(romYLst, RomYLst.IRON_SMELTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "IronSmelter", getImageAt(romYLst, RomYLst.IRON_SMELTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "IronSmelter", getImageAt(romYLst, RomYLst.IRON_SMELTER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "PigFarm", getImageAt(romYLst, RomYLst.PIG_FARM));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "PigFarm", getImageAt(romYLst, RomYLst.PIG_FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "PigFarm", getImageAt(romYLst, RomYLst.PIG_FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "PigFarm", getImageAt(romYLst, RomYLst.PIG_FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Storehouse", getImageAt(romYLst, RomYLst.STOREHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Storehouse", getImageAt(romYLst, RomYLst.STOREHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Storehouse", getImageAt(romYLst, RomYLst.STOREHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Storehouse", getImageAt(romYLst, RomYLst.STOREHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Mill", getImageAt(romYLst, RomYLst.MILL_NO));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Mill", getImageAt(romYLst, RomYLst.MILL_NO_FAN_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Mill", getImageAt(romYLst, RomYLst.MILL_NO + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Mill", getImageAt(romYLst, RomYLst.MILL_NO_FAN_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Bakery", getImageAt(romYLst, RomYLst.BAKERY));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Bakery", getImageAt(romYLst, RomYLst.BAKERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Bakery", getImageAt(romYLst, RomYLst.BAKERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Bakery", getImageAt(romYLst, RomYLst.BAKERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Sawmill", getImageAt(romYLst, RomYLst.SAWMILL));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Sawmill", getImageAt(romYLst, RomYLst.SAWMILL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Sawmill", getImageAt(romYLst, RomYLst.SAWMILL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Sawmill", getImageAt(romYLst, RomYLst.SAWMILL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Mint", getImageAt(romYLst, RomYLst.MINT));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Mint", getImageAt(romYLst, RomYLst.MINT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Mint", getImageAt(romYLst, RomYLst.MINT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Mint", getImageAt(romYLst, RomYLst.MINT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Well", getImageAt(romYLst, RomYLst.WELL));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Well", getImageAt(romYLst, RomYLst.WELL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Well", getImageAt(romYLst, RomYLst.WELL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Well", getImageAt(romYLst, RomYLst.WELL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Shipyard", getImageAt(romYLst, RomYLst.SHIPYARD));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Shipyard", getImageAt(romYLst, RomYLst.SHIPYARD_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Shipyard", getImageAt(romYLst, RomYLst.SHIPYARD + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Shipyard", getImageAt(romYLst, RomYLst.SHIPYARD_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Farm", getImageAt(romYLst, RomYLst.FARM));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Farm", getImageAt(romYLst, RomYLst.FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Farm", getImageAt(romYLst, RomYLst.FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Farm", getImageAt(romYLst, RomYLst.FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "DonkeyFarm", getImageAt(romYLst, RomYLst.DONKEY_BREEDER));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "DonkeyFarm", getImageAt(romYLst, RomYLst.DONKEY_BREEDER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "DonkeyFarm", getImageAt(romYLst, RomYLst.DONKEY_BREEDER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "DonkeyFarm", getImageAt(romYLst, RomYLst.DONKEY_BREEDER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(ROMANS, "Harbor", getImageAt(romYLst, RomYLst.HARBOR));
        buildingsImageCollection.addBuildingShadowForNation(ROMANS, "Harbor", getImageAt(romYLst, RomYLst.HARBOR_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(ROMANS, "Harbor", getImageAt(romYLst, RomYLst.HARBOR + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(ROMANS, "Harbor", getImageAt(romYLst, RomYLst.HARBOR_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addConstructionPlanned(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(ROMANS, getImageAt(romYLst, RomYLst.CONSTRUCTION_JUST_STARTED_SHADOW));


        // Load japanese buildings
        buildingsImageCollection.addBuildingForNation(JAPANESE, "Headquarter", getImageAt(japYLst, JapYLst.HEADQUARTER));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Headquarter", getImageAt(japYLst, JapYLst.HEADQUARTER_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Barracks", getImageAt(japYLst, JapYLst.BARRACKS));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Barracks", getImageAt(japYLst, JapYLst.BARRACKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Barracks", getImageAt(japYLst, JapYLst.BARRACKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Barracks", getImageAt(japYLst, JapYLst.BARRACKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "GuardHouse", getImageAt(japYLst, JapYLst.GUARDHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "GuardHouse", getImageAt(japYLst, JapYLst.GUARDHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "GuardHouse", getImageAt(japYLst, JapYLst.GUARDHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "GuardHouse", getImageAt(japYLst, JapYLst.GUARDHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "WatchTower", getImageAt(japYLst, JapYLst.WATCHTOWER));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "WatchTower", getImageAt(japYLst, JapYLst.WATCHTOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "WatchTower", getImageAt(japYLst, JapYLst.WATCHTOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "WatchTower", getImageAt(japYLst, JapYLst.WATCHTOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Fortress", getImageAt(japYLst, JapYLst.FORTRESS));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Fortress", getImageAt(japYLst, JapYLst.FORTRESS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Fortress", getImageAt(japYLst, JapYLst.FORTRESS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Fortress", getImageAt(japYLst, JapYLst.FORTRESS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "GraniteMine", getImageAt(japYLst, JapYLst.GRANITE_MINE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "GraniteMine", getImageAt(japYLst, JapYLst.GRANITE_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "GraniteMine", getImageAt(japYLst, JapYLst.GRANITE_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "GraniteMine", getImageAt(japYLst, JapYLst.GRANITE_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "CoalMine", getImageAt(japYLst, JapYLst.COAL_MINE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "CoalMine", getImageAt(japYLst, JapYLst.COAL_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "CoalMine", getImageAt(japYLst, JapYLst.COAL_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "CoalMine", getImageAt(japYLst, JapYLst.COAL_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "IronMine", getImageAt(japYLst, JapYLst.IRON_MINE_RESOURCE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "IronMine", getImageAt(japYLst, JapYLst.IRON_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "IronMine", getImageAt(japYLst, JapYLst.IRON_MINE_RESOURCE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "IronMine", getImageAt(japYLst, JapYLst.IRON_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "GoldMine", getImageAt(japYLst, JapYLst.GOLD_MINE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "GoldMine", getImageAt(japYLst, JapYLst.GOLD_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "GoldMine", getImageAt(japYLst, JapYLst.GOLD_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "GoldMine", getImageAt(japYLst, JapYLst.GOLD_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "LookoutTower", getImageAt(japYLst, JapYLst.LOOKOUT_TOWER));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "LookoutTower", getImageAt(japYLst, JapYLst.LOOKOUT_TOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "LookoutTower", getImageAt(japYLst, JapYLst.LOOKOUT_TOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "LookoutTower", getImageAt(japYLst, JapYLst.LOOKOUT_TOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Catapult", getImageAt(japYLst, JapYLst.CATAPULT));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Catapult", getImageAt(japYLst, JapYLst.CATAPULT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Catapult", getImageAt(japYLst, JapYLst.CATAPULT + 2));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Woodcutter", getImageAt(japYLst, JapYLst.WOODCUTTER));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Woodcutter", getImageAt(japYLst, JapYLst.WOODCUTTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Woodcutter", getImageAt(japYLst, JapYLst.WOODCUTTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Woodcutter", getImageAt(japYLst, JapYLst.WOODCUTTER_UNDER_CONSTRUCTION_SHADOW));
        buildingsImageCollection.addOpenDoorForBuilding(JAPANESE, "Woodcutter", getImageAt(japYLst, JapYLst.WOODCUTTER_OPEN_DOOR));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Fishery", getImageAt(japYLst, JapYLst.FISHERY));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Fishery", getImageAt(japYLst, JapYLst.FISHERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Fishery", getImageAt(japYLst, JapYLst.FISHERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Fishery", getImageAt(japYLst, JapYLst.FISHERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Quarry", getImageAt(japYLst, JapYLst.QUARRY));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Quarry", getImageAt(japYLst, JapYLst.QUARRY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Quarry", getImageAt(japYLst, JapYLst.QUARRY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Quarry", getImageAt(japYLst, JapYLst.QUARRY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "ForesterHut", getImageAt(japYLst, JapYLst.FORESTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "ForesterHut", getImageAt(japYLst, JapYLst.FORESTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "ForesterHut", getImageAt(japYLst, JapYLst.FORESTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "ForesterHut", getImageAt(japYLst, JapYLst.FORESTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "SlaughterHouse", getImageAt(japYLst, JapYLst.SLAUGHTER_HOUSE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "SlaughterHouse", getImageAt(japYLst, JapYLst.SLAUGHTER_HOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "SlaughterHouse", getImageAt(japYLst, JapYLst.SLAUGHTER_HOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "SlaughterHouse", getImageAt(japYLst, JapYLst.SLAUGHTER_HOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "HunterHut", getImageAt(japYLst, JapYLst.HUNTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "HunterHut", getImageAt(japYLst, JapYLst.HUNTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "HunterHut", getImageAt(japYLst, JapYLst.HUNTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "HunterHut", getImageAt(japYLst, JapYLst.HUNTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Brewery", getImageAt(japYLst, JapYLst.BREWERY));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Brewery", getImageAt(japYLst, JapYLst.BREWERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Brewery", getImageAt(japYLst, JapYLst.BREWERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Brewery", getImageAt(japYLst, JapYLst.BREWERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Armory", getImageAt(japYLst, JapYLst.ARMORY));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Armory", getImageAt(japYLst, JapYLst.ARMORY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Armory", getImageAt(japYLst, JapYLst.ARMORY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Armory", getImageAt(japYLst, JapYLst.ARMORY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Metalworks", getImageAt(japYLst, JapYLst.METALWORKS));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Metalworks", getImageAt(japYLst, JapYLst.METALWORKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Metalworks", getImageAt(japYLst, JapYLst.METALWORKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Metalworks", getImageAt(japYLst, JapYLst.METALWORKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "IronSmelter", getImageAt(japYLst, JapYLst.IRON_SMELTER));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "IronSmelter", getImageAt(japYLst, JapYLst.IRON_SMELTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "IronSmelter", getImageAt(japYLst, JapYLst.IRON_SMELTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "IronSmelter", getImageAt(japYLst, JapYLst.IRON_SMELTER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "PigFarm", getImageAt(japYLst, JapYLst.PIG_FARM));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "PigFarm", getImageAt(japYLst, JapYLst.PIG_FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "PigFarm", getImageAt(japYLst, JapYLst.PIG_FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "PigFarm", getImageAt(japYLst, JapYLst.PIG_FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Storehouse", getImageAt(japYLst, JapYLst.STOREHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Storehouse", getImageAt(japYLst, JapYLst.STOREHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Storehouse", getImageAt(japYLst, JapYLst.STOREHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Storehouse", getImageAt(japYLst, JapYLst.STOREHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Mill", getImageAt(japYLst, JapYLst.MILL_NO_FAN));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Mill", getImageAt(japYLst, JapYLst.MILL_NO_FAN_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Mill", getImageAt(japYLst, JapYLst.MILL_NO_FAN + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Mill", getImageAt(japYLst, JapYLst.MILL_NO_FAN_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Bakery", getImageAt(japYLst, JapYLst.BAKERY));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Bakery", getImageAt(japYLst, JapYLst.BAKERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Bakery", getImageAt(japYLst, JapYLst.BAKERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Bakery", getImageAt(japYLst, JapYLst.BAKERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Sawmill", getImageAt(japYLst, JapYLst.SAWMILL));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Sawmill", getImageAt(japYLst, JapYLst.SAWMILL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Sawmill", getImageAt(japYLst, JapYLst.SAWMILL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Sawmill", getImageAt(japYLst, JapYLst.SAWMILL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Mint", getImageAt(japYLst, JapYLst.MINT));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Mint", getImageAt(japYLst, JapYLst.MINT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Mint", getImageAt(japYLst, JapYLst.MINT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Mint", getImageAt(japYLst, JapYLst.MINT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Well", getImageAt(japYLst, JapYLst.WELL));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Well", getImageAt(japYLst, JapYLst.WELL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Well", getImageAt(japYLst, JapYLst.WELL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Well", getImageAt(japYLst, JapYLst.WELL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Shipyard", getImageAt(japYLst, JapYLst.SHIPYARD));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Shipyard", getImageAt(japYLst, JapYLst.SHIPYARD_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Shipyard", getImageAt(japYLst, JapYLst.SHIPYARD + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Shipyard", getImageAt(japYLst, JapYLst.SHIPYARD_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Farm", getImageAt(japYLst, JapYLst.FARM));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Farm", getImageAt(japYLst, JapYLst.FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Farm", getImageAt(japYLst, JapYLst.FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Farm", getImageAt(japYLst, JapYLst.FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "DonkeyFarm", getImageAt(japYLst, JapYLst.DONKEY_BREEDER));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "DonkeyFarm", getImageAt(japYLst, JapYLst.DONKEY_BREEDER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "DonkeyFarm", getImageAt(japYLst, JapYLst.DONKEY_BREEDER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "DonkeyFarm", getImageAt(japYLst, JapYLst.DONKEY_BREEDER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(JAPANESE, "Harbor", getImageAt(japYLst, JapYLst.HARBOR));
        buildingsImageCollection.addBuildingShadowForNation(JAPANESE, "Harbor", getImageAt(japYLst, JapYLst.HARBOR_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(JAPANESE, "Harbor", getImageAt(japYLst, JapYLst.HARBOR + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(JAPANESE, "Harbor", getImageAt(japYLst, JapYLst.HARBOR_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addConstructionPlanned(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(JAPANESE, getImageAt(japYLst, JapYLst.CONSTRUCTION_JUST_STARTED_SHADOW));



        // Load african buildings
        buildingsImageCollection.addBuildingForNation(AFRICANS, "Headquarter", getImageAt(afrYLst, AfrYLst.HEADQUARTER));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Headquarter", getImageAt(afrYLst, AfrYLst.HEADQUARTER_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Barracks", getImageAt(afrYLst, AfrYLst.BARRACKS));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Barracks", getImageAt(afrYLst, AfrYLst.BARRACKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Barracks", getImageAt(afrYLst, AfrYLst.BARRACKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Barracks", getImageAt(afrYLst, AfrYLst.BARRACKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "GuardHouse", getImageAt(afrYLst, AfrYLst.GUARDHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "GuardHouse", getImageAt(afrYLst, AfrYLst.GUARDHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "GuardHouse", getImageAt(afrYLst, AfrYLst.GUARDHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "GuardHouse", getImageAt(afrYLst, AfrYLst.GUARDHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "WatchTower", getImageAt(afrYLst, AfrYLst.WATCHTOWER));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "WatchTower", getImageAt(afrYLst, AfrYLst.WATCHTOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "WatchTower", getImageAt(afrYLst, AfrYLst.WATCHTOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "WatchTower", getImageAt(afrYLst, AfrYLst.WATCHTOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Fortress", getImageAt(afrYLst, AfrYLst.FORTRESS));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Fortress", getImageAt(afrYLst, AfrYLst.FORTRESS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Fortress", getImageAt(afrYLst, AfrYLst.FORTRESS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Fortress", getImageAt(afrYLst, AfrYLst.FORTRESS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "GraniteMine", getImageAt(afrYLst, AfrYLst.GRANITE_MINE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "GraniteMine", getImageAt(afrYLst, AfrYLst.GRANITE_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "GraniteMine", getImageAt(afrYLst, AfrYLst.GRANITE_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "GraniteMine", getImageAt(afrYLst, AfrYLst.GRANITE_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "CoalMine", getImageAt(afrYLst, AfrYLst.COAL_MINE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "CoalMine", getImageAt(afrYLst, AfrYLst.COAL_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "CoalMine", getImageAt(afrYLst, AfrYLst.COAL_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "CoalMine", getImageAt(afrYLst, AfrYLst.COAL_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "IronMine", getImageAt(afrYLst, AfrYLst.IRON_MINE_RESOURCE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "IronMine", getImageAt(afrYLst, AfrYLst.IRON_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "IronMine", getImageAt(afrYLst, AfrYLst.IRON_MINE_RESOURCE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "IronMine", getImageAt(afrYLst, AfrYLst.IRON_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "GoldMine", getImageAt(afrYLst, AfrYLst.GOLD_MINE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "GoldMine", getImageAt(afrYLst, AfrYLst.GOLD_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "GoldMine", getImageAt(afrYLst, AfrYLst.GOLD_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "GoldMine", getImageAt(afrYLst, AfrYLst.GOLD_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "LookoutTower", getImageAt(afrYLst, AfrYLst.LOOKOUT_TOWER));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "LookoutTower", getImageAt(afrYLst, AfrYLst.LOOKOUT_TOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "LookoutTower", getImageAt(afrYLst, AfrYLst.LOOKOUT_TOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "LookoutTower", getImageAt(afrYLst, AfrYLst.LOOKOUT_TOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Catapult", getImageAt(afrYLst, AfrYLst.CATAPULT));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Catapult", getImageAt(afrYLst, AfrYLst.CATAPULT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Catapult", getImageAt(afrYLst, AfrYLst.CATAPULT + 2));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Woodcutter", getImageAt(afrYLst, AfrYLst.WOODCUTTER));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Woodcutter", getImageAt(afrYLst, AfrYLst.WOODCUTTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Woodcutter", getImageAt(afrYLst, AfrYLst.WOODCUTTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Woodcutter", getImageAt(afrYLst, AfrYLst.WOODCUTTER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Fishery", getImageAt(afrYLst, AfrYLst.FISHERY));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Fishery", getImageAt(afrYLst, AfrYLst.FISHERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Fishery", getImageAt(afrYLst, AfrYLst.FISHERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Fishery", getImageAt(afrYLst, AfrYLst.FISHERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Quarry", getImageAt(afrYLst, AfrYLst.QUARRY));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Quarry", getImageAt(afrYLst, AfrYLst.QUARRY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Quarry", getImageAt(afrYLst, AfrYLst.QUARRY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Quarry", getImageAt(afrYLst, AfrYLst.QUARRY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "ForesterHut", getImageAt(afrYLst, AfrYLst.FORESTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "ForesterHut", getImageAt(afrYLst, AfrYLst.FORESTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "ForesterHut", getImageAt(afrYLst, AfrYLst.FORESTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "ForesterHut", getImageAt(afrYLst, AfrYLst.FORESTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "SlaughterHouse", getImageAt(afrYLst, AfrYLst.SLAUGHTER_HOUSE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "SlaughterHouse", getImageAt(afrYLst, AfrYLst.SLAUGHTER_HOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "SlaughterHouse", getImageAt(afrYLst, AfrYLst.SLAUGHTER_HOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "SlaughterHouse", getImageAt(afrYLst, AfrYLst.SLAUGHTER_HOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "HunterHut", getImageAt(afrYLst, AfrYLst.HUNTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "HunterHut", getImageAt(afrYLst, AfrYLst.HUNTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "HunterHut", getImageAt(afrYLst, AfrYLst.HUNTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "HunterHut", getImageAt(afrYLst, AfrYLst.HUNTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Brewery", getImageAt(afrYLst, AfrYLst.BREWERY));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Brewery", getImageAt(afrYLst, AfrYLst.BREWERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Brewery", getImageAt(afrYLst, AfrYLst.BREWERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Brewery", getImageAt(afrYLst, AfrYLst.BREWERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Armory", getImageAt(afrYLst, AfrYLst.ARMORY));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Armory", getImageAt(afrYLst, AfrYLst.ARMORY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Armory", getImageAt(afrYLst, AfrYLst.ARMORY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Armory", getImageAt(afrYLst, AfrYLst.ARMORY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Metalworks", getImageAt(afrYLst, AfrYLst.METALWORKS));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Metalworks", getImageAt(afrYLst, AfrYLst.METALWORKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Metalworks", getImageAt(afrYLst, AfrYLst.METALWORKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Metalworks", getImageAt(afrYLst, AfrYLst.METALWORKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "IronSmelter", getImageAt(afrYLst, AfrYLst.IRON_SMELTER));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "IronSmelter", getImageAt(afrYLst, AfrYLst.IRON_SMELTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "IronSmelter", getImageAt(afrYLst, AfrYLst.IRON_SMELTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "IronSmelter", getImageAt(afrYLst, AfrYLst.IRON_SMELTER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "PigFarm", getImageAt(afrYLst, AfrYLst.PIG_FARM));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "PigFarm", getImageAt(afrYLst, AfrYLst.PIG_FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "PigFarm", getImageAt(afrYLst, AfrYLst.PIG_FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "PigFarm", getImageAt(afrYLst, AfrYLst.PIG_FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Storehouse", getImageAt(afrYLst, AfrYLst.STOREHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Storehouse", getImageAt(afrYLst, AfrYLst.STOREHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Storehouse", getImageAt(afrYLst, AfrYLst.STOREHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Storehouse", getImageAt(afrYLst, AfrYLst.STOREHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Mill", getImageAt(afrYLst, AfrYLst.MILL_NO));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Mill", getImageAt(afrYLst, AfrYLst.MILL_NO_FAN_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Mill", getImageAt(afrYLst, AfrYLst.MILL_NO + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Mill", getImageAt(afrYLst, AfrYLst.MILL_NO_FAN_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Bakery", getImageAt(afrYLst, AfrYLst.BAKERY));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Bakery", getImageAt(afrYLst, AfrYLst.BAKERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Bakery", getImageAt(afrYLst, AfrYLst.BAKERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Bakery", getImageAt(afrYLst, AfrYLst.BAKERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Sawmill", getImageAt(afrYLst, AfrYLst.SAWMILL));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Sawmill", getImageAt(afrYLst, AfrYLst.SAWMILL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Sawmill", getImageAt(afrYLst, AfrYLst.SAWMILL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Sawmill", getImageAt(afrYLst, AfrYLst.SAWMILL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Mint", getImageAt(afrYLst, AfrYLst.MINT));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Mint", getImageAt(afrYLst, AfrYLst.MINT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Mint", getImageAt(afrYLst, AfrYLst.MINT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Mint", getImageAt(afrYLst, AfrYLst.MINT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Well", getImageAt(afrYLst, AfrYLst.WELL));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Well", getImageAt(afrYLst, AfrYLst.WELL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Well", getImageAt(afrYLst, AfrYLst.WELL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Well", getImageAt(afrYLst, AfrYLst.WELL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Shipyard", getImageAt(afrYLst, AfrYLst.SHIPYARD));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Shipyard", getImageAt(afrYLst, AfrYLst.SHIPYARD_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Shipyard", getImageAt(afrYLst, AfrYLst.SHIPYARD + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Shipyard", getImageAt(afrYLst, AfrYLst.SHIPYARD_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Farm", getImageAt(afrYLst, AfrYLst.FARM));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Farm", getImageAt(afrYLst, AfrYLst.FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Farm", getImageAt(afrYLst, AfrYLst.FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Farm", getImageAt(afrYLst, AfrYLst.FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "DonkeyFarm", getImageAt(afrYLst, AfrYLst.DONKEY_BREEDER));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "DonkeyFarm", getImageAt(afrYLst, AfrYLst.DONKEY_BREEDER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "DonkeyFarm", getImageAt(afrYLst, AfrYLst.DONKEY_BREEDER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "DonkeyFarm", getImageAt(afrYLst, AfrYLst.DONKEY_BREEDER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(AFRICANS, "Harbor", getImageAt(afrYLst, AfrYLst.HARBOR));
        buildingsImageCollection.addBuildingShadowForNation(AFRICANS, "Harbor", getImageAt(afrYLst, AfrYLst.HARBOR_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(AFRICANS, "Harbor", getImageAt(afrYLst, AfrYLst.HARBOR + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(AFRICANS, "Harbor", getImageAt(afrYLst, AfrYLst.HARBOR_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addConstructionPlanned(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(AFRICANS, getImageAt(afrYLst, AfrYLst.CONSTRUCTION_JUST_STARTED_SHADOW));


        // Load viking buildings
        buildingsImageCollection.addBuildingForNation(VIKINGS, "Headquarter", getImageAt(vikYLst, VikYLst.HEADQUARTER));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Headquarter", getImageAt(vikYLst, VikYLst.HEADQUARTER_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Barracks", getImageAt(vikYLst, VikYLst.BARRACKS));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Barracks", getImageAt(vikYLst, VikYLst.BARRACKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Barracks", getImageAt(vikYLst, VikYLst.BARRACKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Barracks", getImageAt(vikYLst, VikYLst.BARRACKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "GuardHouse", getImageAt(vikYLst, VikYLst.GUARDHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "GuardHouse", getImageAt(vikYLst, VikYLst.GUARDHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "GuardHouse", getImageAt(vikYLst, VikYLst.GUARDHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "GuardHouse", getImageAt(vikYLst, VikYLst.GUARDHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "WatchTower", getImageAt(vikYLst, VikYLst.WATCHTOWER));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "WatchTower", getImageAt(vikYLst, VikYLst.WATCHTOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "WatchTower", getImageAt(vikYLst, VikYLst.WATCHTOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "WatchTower", getImageAt(vikYLst, VikYLst.WATCHTOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Fortress", getImageAt(vikYLst, VikYLst.FORTRESS));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Fortress", getImageAt(vikYLst, VikYLst.FORTRESS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Fortress", getImageAt(vikYLst, VikYLst.FORTRESS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Fortress", getImageAt(vikYLst, VikYLst.FORTRESS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "GraniteMine", getImageAt(vikYLst, VikYLst.GRANITE_MINE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "GraniteMine", getImageAt(vikYLst, VikYLst.GRANITE_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "GraniteMine", getImageAt(vikYLst, VikYLst.GRANITE_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "GraniteMine", getImageAt(vikYLst, VikYLst.GRANITE_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "CoalMine", getImageAt(vikYLst, VikYLst.COAL_MINE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "CoalMine", getImageAt(vikYLst, VikYLst.COAL_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "CoalMine", getImageAt(vikYLst, VikYLst.COAL_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "CoalMine", getImageAt(vikYLst, VikYLst.COAL_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "IronMine", getImageAt(vikYLst, VikYLst.IRON_MINE_RESOURCE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "IronMine", getImageAt(vikYLst, VikYLst.IRON_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "IronMine", getImageAt(vikYLst, VikYLst.IRON_MINE_RESOURCE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "IronMine", getImageAt(vikYLst, VikYLst.IRON_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "GoldMine", getImageAt(vikYLst, VikYLst.GOLD_MINE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "GoldMine", getImageAt(vikYLst, VikYLst.GOLD_MINE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "GoldMine", getImageAt(vikYLst, VikYLst.GOLD_MINE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "GoldMine", getImageAt(vikYLst, VikYLst.GOLD_MINE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "LookoutTower", getImageAt(vikYLst, VikYLst.LOOKOUT_TOWER));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "LookoutTower", getImageAt(vikYLst, VikYLst.LOOKOUT_TOWER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "LookoutTower", getImageAt(vikYLst, VikYLst.LOOKOUT_TOWER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "LookoutTower", getImageAt(vikYLst, VikYLst.LOOKOUT_TOWER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Catapult", getImageAt(vikYLst, VikYLst.CATAPULT));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Catapult", getImageAt(vikYLst, VikYLst.CATAPULT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Catapult", getImageAt(vikYLst, VikYLst.CATAPULT + 2));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Woodcutter", getImageAt(vikYLst, VikYLst.WOODCUTTER));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Woodcutter", getImageAt(vikYLst, VikYLst.WOODCUTTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Woodcutter", getImageAt(vikYLst, VikYLst.WOODCUTTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Woodcutter", getImageAt(vikYLst, VikYLst.WOODCUTTER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Fishery", getImageAt(vikYLst, VikYLst.FISHERY));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Fishery", getImageAt(vikYLst, VikYLst.FISHERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Fishery", getImageAt(vikYLst, VikYLst.FISHERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Fishery", getImageAt(vikYLst, VikYLst.FISHERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Quarry", getImageAt(vikYLst, VikYLst.QUARRY));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Quarry", getImageAt(vikYLst, VikYLst.QUARRY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Quarry", getImageAt(vikYLst, VikYLst.QUARRY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Quarry", getImageAt(vikYLst, VikYLst.QUARRY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "ForesterHut", getImageAt(vikYLst, VikYLst.FORESTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "ForesterHut", getImageAt(vikYLst, VikYLst.FORESTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "ForesterHut", getImageAt(vikYLst, VikYLst.FORESTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "ForesterHut", getImageAt(vikYLst, VikYLst.FORESTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "SlaughterHouse", getImageAt(vikYLst, VikYLst.SLAUGHTER_HOUSE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "SlaughterHouse", getImageAt(vikYLst, VikYLst.SLAUGHTER_HOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "SlaughterHouse", getImageAt(vikYLst, VikYLst.SLAUGHTER_HOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "SlaughterHouse", getImageAt(vikYLst, VikYLst.SLAUGHTER_HOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "HunterHut", getImageAt(vikYLst, VikYLst.HUNTER_HUT));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "HunterHut", getImageAt(vikYLst, VikYLst.HUNTER_HUT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "HunterHut", getImageAt(vikYLst, VikYLst.HUNTER_HUT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "HunterHut", getImageAt(vikYLst, VikYLst.HUNTER_HUT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Brewery", getImageAt(vikYLst, VikYLst.BREWERY));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Brewery", getImageAt(vikYLst, VikYLst.BREWERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Brewery", getImageAt(vikYLst, VikYLst.BREWERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Brewery", getImageAt(vikYLst, VikYLst.BREWERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Armory", getImageAt(vikYLst, VikYLst.ARMORY));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Armory", getImageAt(vikYLst, VikYLst.ARMORY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Armory", getImageAt(vikYLst, VikYLst.ARMORY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Armory", getImageAt(vikYLst, VikYLst.ARMORY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Metalworks", getImageAt(vikYLst, VikYLst.METALWORKS));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Metalworks", getImageAt(vikYLst, VikYLst.METALWORKS_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Metalworks", getImageAt(vikYLst, VikYLst.METALWORKS + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Metalworks", getImageAt(vikYLst, VikYLst.METALWORKS_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "IronSmelter", getImageAt(vikYLst, VikYLst.IRON_SMELTER));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "IronSmelter", getImageAt(vikYLst, VikYLst.IRON_SMELTER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "IronSmelter", getImageAt(vikYLst, VikYLst.IRON_SMELTER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "IronSmelter", getImageAt(vikYLst, VikYLst.IRON_SMELTER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "PigFarm", getImageAt(vikYLst, VikYLst.PIG_FARM));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "PigFarm", getImageAt(vikYLst, VikYLst.PIG_FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "PigFarm", getImageAt(vikYLst, VikYLst.PIG_FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "PigFarm", getImageAt(vikYLst, VikYLst.PIG_FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Storehouse", getImageAt(vikYLst, VikYLst.STOREHOUSE));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Storehouse", getImageAt(vikYLst, VikYLst.STOREHOUSE_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Storehouse", getImageAt(vikYLst, VikYLst.STOREHOUSE + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Storehouse", getImageAt(vikYLst, VikYLst.STOREHOUSE_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Mill", getImageAt(vikYLst, VikYLst.MILL_NO));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Mill", getImageAt(vikYLst, VikYLst.MILL_NO_FAN_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Mill", getImageAt(vikYLst, VikYLst.MILL_NO + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Mill", getImageAt(vikYLst, VikYLst.MILL_NO_FAN_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Bakery", getImageAt(vikYLst, VikYLst.BAKERY));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Bakery", getImageAt(vikYLst, VikYLst.BAKERY_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Bakery", getImageAt(vikYLst, VikYLst.BAKERY + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Bakery", getImageAt(vikYLst, VikYLst.BAKERY_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Sawmill", getImageAt(vikYLst, VikYLst.SAWMILL));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Sawmill", getImageAt(vikYLst, VikYLst.SAWMILL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Sawmill", getImageAt(vikYLst, VikYLst.SAWMILL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Sawmill", getImageAt(vikYLst, VikYLst.SAWMILL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Mint", getImageAt(vikYLst, VikYLst.MINT));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Mint", getImageAt(vikYLst, VikYLst.MINT_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Mint", getImageAt(vikYLst, VikYLst.MINT + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Mint", getImageAt(vikYLst, VikYLst.MINT_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Well", getImageAt(vikYLst, VikYLst.WELL));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Well", getImageAt(vikYLst, VikYLst.WELL_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Well", getImageAt(vikYLst, VikYLst.WELL + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Well", getImageAt(vikYLst, VikYLst.WELL_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Shipyard", getImageAt(vikYLst, VikYLst.SHIPYARD));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Shipyard", getImageAt(vikYLst, VikYLst.SHIPYARD_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Shipyard", getImageAt(vikYLst, VikYLst.SHIPYARD + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Shipyard", getImageAt(vikYLst, VikYLst.SHIPYARD_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Farm", getImageAt(vikYLst, VikYLst.FARM));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Farm", getImageAt(vikYLst, VikYLst.FARM_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Farm", getImageAt(vikYLst, VikYLst.FARM + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Farm", getImageAt(vikYLst, VikYLst.FARM_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "DonkeyFarm", getImageAt(vikYLst, VikYLst.DONKEY_BREEDER));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "DonkeyFarm", getImageAt(vikYLst, VikYLst.DONKEY_BREEDER_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "DonkeyFarm", getImageAt(vikYLst, VikYLst.DONKEY_BREEDER + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "DonkeyFarm", getImageAt(vikYLst, VikYLst.DONKEY_BREEDER_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addBuildingForNation(VIKINGS, "Harbor", getImageAt(vikYLst, VikYLst.HARBOR));
        buildingsImageCollection.addBuildingShadowForNation(VIKINGS, "Harbor", getImageAt(vikYLst, VikYLst.HARBOR_SHADOW));
        buildingsImageCollection.addBuildingUnderConstructionForNation(VIKINGS, "Harbor", getImageAt(vikYLst, VikYLst.HARBOR + 2));
        buildingsImageCollection.addBuildingUnderConstructionShadowForNation(VIKINGS, "Harbor", getImageAt(vikYLst, VikYLst.HARBOR_UNDER_CONSTRUCTION_SHADOW));

        buildingsImageCollection.addConstructionPlanned(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_PLANNED));
        buildingsImageCollection.addConstructionPlannedShadow(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_PLANNED_SHADOW));
        buildingsImageCollection.addConstructionJustStarted(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_JUST_STARTED_INDEX));
        buildingsImageCollection.addConstructionJustStartedShadow(VIKINGS, getImageAt(vikYLst, VikYLst.CONSTRUCTION_JUST_STARTED_SHADOW));

        buildingsImageCollection.writeImageAtlas(toDir + "/", palette);
    }
}
