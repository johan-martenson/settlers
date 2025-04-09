package org.appland.settlers.assets.gamefiles;

import org.appland.settlers.assets.utils.GameFiles;

public class RomZLst {

    public static final String FILENAME = "DATA/MBOB/ROM_Z.LST";

    public static final int PALETTE = 0;
    public static final int NORMAL_FLAG_ANIMATION = 4;
    public static final int NORMAL_FLAG_SHADOW_ANIMATION = 12;
    public static final int MAIN_FLAG_ANIMATION = 20;
    public static final int MAIN_FLAG_SHADOW_ANIMATION = 28;
    public static final int MARINE_FLAG_ANIMATION = 36;
    public static final int MARINE_FLAG_SHADOW_ANIMATION = 44;


    public static final GameFiles.House HEADQUARTER = GameFiles.House.make("Headquarter", 60, GameFiles.Missing.NO_UNDER_CONSTRUCTION);
    public static final GameFiles.House BARRACKS = new GameFiles.House("Barracks", 63);
    public static final GameFiles.House GUARDHOUSE = new GameFiles.House("GuardHouse", 68);
    public static final GameFiles.House WATCHTOWER = new GameFiles.House("WatchTower", 73);
    public static final GameFiles.House FORTRESS = new GameFiles.House("Fortress", 78);
    public static final GameFiles.House GRANITE_MINE = GameFiles.House.make("GraniteMine", 83, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House COAL_MINE = GameFiles.House.make("CoalMine", 87, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House IRON_MINE = GameFiles.House.make("IronMine", 91, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House GOLD_MINE = GameFiles.House.make("GoldMine", 95, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House LOOKOUT_TOWER = new GameFiles.House("LookoutTower", 99);
    public static final GameFiles.House CATAPULT = GameFiles.House.make("Catapult", 104, GameFiles.Missing.NO_UNDER_CONSTRUCTION_SHADOW);
    public static final GameFiles.House WOODCUTTER = new GameFiles.House("Woodcutter", 108);
    public static final GameFiles.House FISHERY = new GameFiles.House("Fishery", 113);
    public static final GameFiles.House QUARRY = new GameFiles.House("Quarry", 118);
    public static final GameFiles.House FORESTER_HUT = new GameFiles.House("ForesterHut", 123);
    public static final GameFiles.House SLAUGHTER_HOUSE = new GameFiles.House("SlaughterHouse", 128);
    public static final GameFiles.House HUNTER_HUT = new GameFiles.House("HunterHut", 133);
    public static final GameFiles.House BREWERY = new GameFiles.House("Brewery", 138);
    public static final GameFiles.House ARMORY = new GameFiles.House("Armory", 143);
    public static final GameFiles.House METALWORKS = new GameFiles.House("Metalworks", 148);
    public static final GameFiles.House IRON_SMELTER = new GameFiles.House("IronSmelter", 153);
    public static final GameFiles.House PIG_FARM = new GameFiles.House("PigFarm", 158);
    public static final GameFiles.House STOREHOUSE = new GameFiles.House("Storehouse", 163);
    public static final GameFiles.House MILL = new GameFiles.House("Mill", 168);
    public static final GameFiles.House BAKERY = new GameFiles.House("Bakery", 173);
    public static final GameFiles.House SAWMILL = new GameFiles.House("Sawmill", 178);
    public static final GameFiles.House MINT = new GameFiles.House("Mint", 183);
    public static final GameFiles.House WELL = new GameFiles.House("Well", 188);
    public static final GameFiles.House SHIPYARD = new GameFiles.House("Shipyard", 193);
    public static final GameFiles.House FARM = new GameFiles.House("Farm", 198);
    public static final GameFiles.House DONKEY_BREEDER = new GameFiles.House("DonkeyFarm", 203);
    public static final GameFiles.House HARBOR = GameFiles.House.make("Harbor", 208, GameFiles.Missing.NO_OPEN_DOOR);

    public static final int CONSTRUCTION_PLANNED = 212;
    public static final int CONSTRUCTION_PLANNED_SHADOW = 213;
    public static final int CONSTRUCTION_JUST_STARTED_INDEX = 214;
    public static final int CONSTRUCTION_JUST_STARTED_SHADOW = 215;
    public static final int MILL_SAIL_ANIMATION = 216;
    public static final int HARBOR_ANIMATION = 232;
}
