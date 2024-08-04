package org.appland.settlers.assets.gamefiles;

import org.appland.settlers.assets.utils.GameFiles;

/*
    Layout of data in ROM_Y.LST:
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
        */
public class RomYLst {
    public static final String FILENAME = "DATA/MBOB/ROM_Y.LST";

    public static final GameFiles.House HEADQUARTER = GameFiles.House.make("Headquarter", 60, GameFiles.Missing.NO_UNDER_CONSTRUCTION);
    public static final GameFiles.House BARRACKS = new GameFiles.House("Barracks", 63);
    public static final GameFiles.House GUARDHOUSE = new GameFiles.House("Guardhouse", 68);
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
    public static final GameFiles.House DONKEY_BREEDER = new GameFiles.House("DonkeyBreeder", 203);
    public static final GameFiles.House HARBOR = new GameFiles.House("Harbor", 208);

    public static final int CONSTRUCTION_PLANNED = 212;
    public static final int CONSTRUCTION_PLANNED_SHADOW = 213;
    public static final int CONSTRUCTION_JUST_STARTED_INDEX = 214;
    public static final int CONSTRUCTION_JUST_STARTED_SHADOW = 215;
}
