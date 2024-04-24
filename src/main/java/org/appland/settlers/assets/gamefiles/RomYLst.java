package org.appland.settlers.assets.gamefiles;

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

    public static final int HEADQUARTER = 60;
    public static final int HEADQUARTER_SHADOW = 61;
    public static final int BARRACKS = 63;
    public static final int BARRACKS_SHADOW = 64;
    public static final int BARRACKS_UNDER_CONSTRUCTION_SHADOW = 66;
    public static final int GUARDHOUSE = 68;
    public static final int GUARDHOUSE_SHADOW = 69;
    public static final int GUARDHOUSE_UNDER_CONSTRUCTION_SHADOW = 71;
    public static final int WATCHTOWER = 73;
    public static final int WATCHTOWER_SHADOW = 74;
    public static final int WATCHTOWER_UNDER_CONSTRUCTION_SHADOW = 76;
    public static final int FORTRESS = 78;
    public static final int FORTRESS_SHADOW = 79;
    public static final int FORTRESS_UNDER_CONSTRUCTION_SHADOW = 81;
    public static final int GRANITE_MINE = 83;
    public static final int GRANITE_MINE_SHADOW = 84;
    public static final int GRANITE_MINE_UNDER_CONSTRUCTION_SHADOW = 86;
    public static final int COAL_MINE = 87;
    public static final int COAL_MINE_SHADOW = 88;
    public static final int COAL_MINE_UNDER_CONSTRUCTION_SHADOW = 90;
    public static final int IRON_MINE_RESOURCE = 91;
    public static final int IRON_MINE_SHADOW = 92;
    public static final int IRON_MINE_UNDER_CONSTRUCTION_SHADOW = 94;
    public static final int GOLD_MINE = 95;
    public static final int GOLD_MINE_SHADOW = 96;
    public static final int GOLD_MINE_UNDER_CONSTRUCTION_SHADOW = 98;
    public static final int LOOKOUT_TOWER = 99;
    public static final int LOOKOUT_TOWER_SHADOW = 100;
    public static final int LOOKOUT_TOWER_UNDER_CONSTRUCTION_SHADOW = 102;
    public static final int CATAPULT = 104;
    public static final int CATAPULT_SHADOW = 105;
    public static final int CATAPULT_UNDER_CONSTRUCTION_SHADOW = 107;
    public static final int WOODCUTTER = 108;
    public static final int WOODCUTTER_SHADOW = 109;
    public static final int WOODCUTTER_UNDER_CONSTRUCTION_SHADOW = 111;
    public static final int WOODCUTTER_OPEN_DOOR = 112;
    public static final int FISHERY = 113;
    public static final int FISHERY_SHADOW = 114;
    public static final int FISHERY_UNDER_CONSTRUCTION_SHADOW = 116;
    public static final int QUARRY = 118;
    public static final int QUARRY_SHADOW = 119;
    public static final int QUARRY_UNDER_CONSTRUCTION_SHADOW = 121;
    public static final int FORESTER_HUT = 123;
    public static final int FORESTER_HUT_SHADOW = 124;
    public static final int FORESTER_HUT_UNDER_CONSTRUCTION_SHADOW = 126;
    public static final int SLAUGHTER_HOUSE = 128;
    public static final int SLAUGHTER_HOUSE_SHADOW = 129;
    public static final int SLAUGHTER_HOUSE_UNDER_CONSTRUCTION_SHADOW = 131;
    public static final int HUNTER_HUT = 133;
    public static final int HUNTER_HUT_SHADOW = 134;
    public static final int HUNTER_HUT_UNDER_CONSTRUCTION_SHADOW = 136;
    public static final int BREWERY = 138;
    public static final int BREWERY_SHADOW = 139;
    public static final int BREWERY_UNDER_CONSTRUCTION_SHADOW = 141;
    public static final int ARMORY = 143;
    public static final int ARMORY_SHADOW = 144;
    public static final int ARMORY_UNDER_CONSTRUCTION_SHADOW = 146;
    public static final int METALWORKS = 148;
    public static final int METALWORKS_SHADOW = 149;
    public static final int METALWORKS_UNDER_CONSTRUCTION_SHADOW = 151;
    public static final int IRON_SMELTER = 153;
    public static final int IRON_SMELTER_SHADOW = 154;
    public static final int IRON_SMELTER_UNDER_CONSTRUCTION_SHADOW = 156;
    public static final int PIG_FARM = 158;
    public static final int PIG_FARM_SHADOW = 159;
    public static final int PIG_FARM_UNDER_CONSTRUCTION_SHADOW = 161;
    public static final int STOREHOUSE = 163;
    public static final int STOREHOUSE_SHADOW = 164;
    public static final int STOREHOUSE_UNDER_CONSTRUCTION_SHADOW = 166;
    public static final int MILL_NO = 168;
    public static final int MILL_NO_FAN_SHADOW = 169;
    public static final int MILL_NO_FAN_UNDER_CONSTRUCTION_SHADOW = 171;
    public static final int BAKERY = 173;
    public static final int BAKERY_SHADOW = 174;
    public static final int BAKERY_UNDER_CONSTRUCTION_SHADOW = 176;
    public static final int SAWMILL = 178;
    public static final int SAWMILL_SHADOW = 179;
    public static final int SAWMILL_UNDER_CONSTRUCTION_SHADOW = 181;
    public static final int MINT = 183;
    public static final int MINT_SHADOW = 184;
    public static final int MINT_UNDER_CONSTRUCTION_SHADOW = 186;
    public static final int WELL = 188;
    public static final int WELL_SHADOW = 189;
    public static final int WELL_UNDER_CONSTRUCTION_SHADOW = 191;
    public static final int SHIPYARD = 193;
    public static final int SHIPYARD_SHADOW = 194;
    public static final int SHIPYARD_UNDER_CONSTRUCTION_SHADOW = 196;
    public static final int FARM = 198;
    public static final int FARM_SHADOW = 199;
    public static final int FARM_UNDER_CONSTRUCTION_SHADOW = 201;
    public static final int DONKEY_BREEDER = 203;
    public static final int DONKEY_BREEDER_SHADOW = 204;
    public static final int DONKEY_BREEDER_UNDER_CONSTRUCTION_SHADOW = 206;
    public static final int HARBOR = 208;
    public static final int HARBOR_SHADOW = 209;
    public static final int HARBOR_UNDER_CONSTRUCTION_SHADOW = 211;
    public static final int CONSTRUCTION_PLANNED = 212;
    public static final int CONSTRUCTION_PLANNED_SHADOW = 213;
    public static final int CONSTRUCTION_JUST_STARTED_INDEX = 214;
    public static final int CONSTRUCTION_JUST_STARTED_SHADOW = 215;
}
