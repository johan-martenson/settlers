package org.appland.settlers.assets.gamefiles;

import org.appland.settlers.assets.utils.GameFiles;

public class VikYLst {
    public static final String FILENAME = "DATA/MBOB/VIK_Y.LST";

    public static final GameFiles.House HEADQUARTER = GameFiles.House.make("Headquarter", 60, GameFiles.Missing.NO_UNDER_CONSTRUCTION);
    public static final GameFiles.House BARRACKS = new GameFiles.House("Barracks", 63);
    public static final GameFiles.House GUARDHOUSE = new GameFiles.House("GuardHouse", 68);
    public static final GameFiles.House WATCHTOWER = new GameFiles.House("WatchTower", 73);
    public static final GameFiles.House FORTRESS = new GameFiles.House("Fortress", 78);
    public static final GameFiles.House GRANITE_MINE = GameFiles.House.make("GraniteMine", 83, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House COAL_MINE = GameFiles.House.make("CoalMine", 87, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House IRON_MINE = GameFiles.House.make("IronMine", 91, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House GOLD_MINE = GameFiles.House.make("GoldMine", 95, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House LOOKOUT_TOWER = GameFiles.House.make("LookoutTower", 99, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House CATAPULT = GameFiles.House.make("Catapult", 103, GameFiles.Missing.NO_UNDER_CONSTRUCTION_SHADOW);
    public static final GameFiles.House WOODCUTTER = new GameFiles.House("Woodcutter", 107);
    public static final GameFiles.House FISHERY = new GameFiles.House("Fishery", 112);
    public static final GameFiles.House QUARRY = new GameFiles.House("Quarry", 117);
    public static final GameFiles.House FORESTER_HUT = new GameFiles.House("ForesterHut", 122);
    public static final GameFiles.House SLAUGHTER_HOUSE = new GameFiles.House("SlaughterHouse", 127);
    public static final GameFiles.House HUNTER_HUT = new GameFiles.House("HunterHut", 132);
    public static final GameFiles.House BREWERY = new GameFiles.House("Brewery", 137);
    public static final GameFiles.House ARMORY = new GameFiles.House("Armory", 142);
    public static final GameFiles.House METALWORKS = new GameFiles.House("Metalworks", 147);
    public static final GameFiles.House IRON_SMELTER = new GameFiles.House("IronSmelter", 152);
    public static final GameFiles.House PIG_FARM = new GameFiles.House("PigFarm", 157);
    public static final GameFiles.House STOREHOUSE = new GameFiles.House("Storehouse", 162);
    public static final GameFiles.House MILL = new GameFiles.House("Mill", 167);
    public static final GameFiles.House BAKERY = new GameFiles.House("Bakery", 172);
    public static final GameFiles.House SAWMILL = new GameFiles.House("Sawmill", 177);
    public static final GameFiles.House MINT = new GameFiles.House("Mint", 182);
    public static final GameFiles.House WELL = GameFiles.House.make("Well", 187, GameFiles.Missing.NO_OPEN_DOOR);
    public static final GameFiles.House SHIPYARD = new GameFiles.House("Shipyard", 191);
    public static final GameFiles.House FARM = new GameFiles.House("Farm", 196);
    public static final GameFiles.House DONKEY_BREEDER = new GameFiles.House("DonkeyBreeder", 201);
    public static final GameFiles.House HARBOR = GameFiles.House.make("Harbor", 206, GameFiles.Missing.NO_OPEN_DOOR);

    public static final int CONSTRUCTION_PLANNED = 210;
    public static final int CONSTRUCTION_PLANNED_SHADOW = 211;
    public static final int CONSTRUCTION_JUST_STARTED_INDEX = 212;
    public static final int CONSTRUCTION_JUST_STARTED_SHADOW = 213;
}
