package org.appland.settlers.assets.gamefiles;

/**
     * Layout of data in MAPBOBS.LST
     *
     * 0     Palette
     * 1     Selected point
     * 2     Building road?? Maybe when clicked down on step in road when building?
     * 3     Hover point
     * 4     ?? - some sort of selector
     * 5     Hover over available flag point
     * 6     Hover over available mine point
     * 7     Hover over available small building point
     * 8     Hover over available medium building point
     * 9     Hover over available large building point
     * 10    Hover over available harbor point
     * 11    Available flag
     * 12    Available small building
     * 13    Available medium building
     * 14    Available large building
     * 15    Available mine
     * 16    Available harbor
     * 17    ?? - skull - when to use?
     * 18    ?? - green bar - road building?
     * 19    ?? - green bar - road building?
     * 20    ?? - yellow bar - road building?
     * 21    ?? - red bar - road building?
     * 22    ?? - green bar - road building?
     * 23    ?? - yellow bar - road building?
     * 24    ?? - red bar - road building?
     * 25    ?? - revert arrow - used when?
     * 26-33 Tree animation type 1
     * 34    Tree mini type 1
     * 35    Tree small type 1
     * 36    Tree medium type 1
     * 37-40 Tree falling animation type 1
     * 41-48 Tree animation type 2
     * 49    Tree mini type 2
     * 50    Tree small type 2
     * 51    Tree medium type 2
     * 52-55 Tree falling animation type 2
     * 56-63 Tree animation type 3
     * 64    Tree mini type 3
     * 65    Tree small type 3
     * 66    Tree medium type 3
     * 67-70 Tree falling animation type 3
     * 71-78 Tree animation type 4
     * 79    Tree mini type 4
     * 80    Tree small type 4
     * 81      Tree medium type 4
     * 82-85   Tree falling animation type 4
     * 86-93   Tree animation type 5
     * 94      Tree mini type 5
     * 95      Tree small type 5
     * 96      Tree medium type 5
     * 97-100  Tree falling animation type 5
     * 101-108 Tree animation type 6 (cannot be cut down?)
     * 109-116 Tree animation type 7
     * 117     Tree mini type 7
     * 118     Tree small type 7
     * 119     Tree medium type 7
     * 120-123 Tree falling animation type 7
     * 124-131 Tree animation type 8
     * 132     Tree mini type 8
     * 133     Tree small type 8
     * 134     Tree medium type 8
     * 135-138 Tree falling type 8
     * 139-146 Tree animation type 9
     * 147     Tree mini type 9
     * 148     Tree small type 9
     * 149     Tree medium type 9
     * 150-153 Tree falling animation type 9
     * 154-161 Tree shadow type ? (lying or standing?)
     * 162     Tree mini shadow type ?
     * 163     Tree small shadow type ?
     * 164     Tree medium shadow type ?
     * 165-168 Tree falling animation shadow type ?
     *
     * 169-281 More tree shadows -- figure out which ones belong to which tree
     *
     * 282     ??
     * 283     Mushroom
     * 284     Mini decorative stone
     * 285     Mini decorative stones
     * 286     Small decorative stone
     * 287     Fallen dead decorative tree
     * 288     Standing dead decorative tree
     * 289     Skeleton decorative
     * 290     Mini decorative skeleton
     * 291     Flowers decorative
     * 292     Bush decorative
     * 293     Larger set of stones (can be extracted?)
     * 294     Cactus decorative
     * 295     Cactus decorative
     * 296     Beach grass decorative
     * 297     Small grass decorative
     * 298     Minish stones left over (decorative?)
     * 299     Minish stones left over (decorative?)
     * 300     Mini stones left over type 1
     * 301     Smallish stones left over type 1
     * 302     Medium stones left over type 1
     * 303     large stones left over type 1
     * 304     Mini stones left over type 2
     * 305     More stones left over type 2
     * 306     More stones left over type 2
     * 307     More stones left over type 2
     * 308     More stones left over type 2
     * 309     Maximum stones left over type 2
     * 310     Fallen tree left over
     * 311-314 Crops - newly planted to fully grown - type 1
     * 315     Crops - just harvested
     * 316-319 Crops newly planted to fully grown - type 2
     * 320     Crops - just harvested
     * 321     Bush - decorative
     * 322     Bush - decorative
     * 323     Bush - decorative
     * 324     Grass - decorative
     * 325     Grass - decorative
     * 326     Small skeleton
     * 327     Smaller skeleton
     *
     * 328-332 ???
     * 333-371 Unknown shadows???
     *
     * 372-374     Iron sign, small-medium-large, up-right
     * 375-377     Gold sign, small-medium-large, up-right
     * 378-380     Coal sign, small-medium-large, up-right
     * 381-383     Granite sign, small-medium-large, up-right
     * 384         Water sign, large(?), up-right
     * 385         Nothing found sign, up-right
     *
     * 386-418     Unknown shadows???
     *
     * 419-426     Small fire animation
     *
     * 475-482     Ice bear walking animation, right
     * 483-490     Ice bear walking animation, down-right
     * 491-498     Ice bear walking animation, down-left
     * 499-506     Ice bear walking animation, left
     * 507-514     Ice bear walking animation, up-left
     * 515-522     Ice bear walking animation, up-right
     *
     * 523         Dead animal???
     *
     * 524         ??
     *
     * 525-536     Rabbit running animation, right
     * 537-548     Rabbit running animation, left
     * 549-554     Rabbit running animation, up-left
     * 555-561     Rabbit running animation, up-right
     *
     * 562-600     More rabbits??
     *
     * 601-606     Fox running animation, right
     * 607-612     Fox running animation, down-right
     * 613-618     Fox running animation, down-left
     * 619-624     Fox running animation, left
     * 625-630     Fox running animation, up-left
     * 631-636     Fox running animation, up-right
     * 637         Fox killed
     * 638-643     Fox shadows
     *
     * 644-651     Deer running animation, right
     * 652-659     Deer running animation, down-right
     * 660-667     Deer running animation, down-left
     * 668-675     Deer running animation, left
     * 676-683     Deer running animation, up-left
     * 684-691     Deer running animation, up-right
     * 692         Deer killed
     * 693-698     Deer shadows
     *
     * 699-706     Rain deer running animation, right
     * 707-714     Rain deer running animation, down-right
     * 715-722     Rain deer running animation, down-left
     * 723-730     Rain deer running animation, left
     * 731-738     Rain deer running animation, up-left
     * 739-746     Rain deer running animation, up-right
     * 747         Rain deer killed
     * 748-753     Rain deer shadows
     *
     * 754-        Duck, east, south-east, south-west, west, north-west, north-east,
     *
     * 761-808     Horse animated ...
     *
     * 812-817     Donkey bags ...
     *
     * 818-829     Sheep animated ...
     *
     * 832-843     Pig animated ...
     *
     * 844-891     Smaller pig (?) animated
     *
     * ...
     *
     * 895-927     Tools and materials - small for workers to cary around
     *
     * 928-964     Tools and materials - for drawing in house information views - needs/has/produces
     *
     * 965-994     Tools and materials for headquarter/storehouse inventory view
     *
     * 995-1030    Very small tools and materials. For what??
     *
     * 1031-1038   Small building fire
     * 1039-1046   Medium building fire
     * 1047-1054   Large building fire
     * 1055        Small building fire done
     * 1056        Medium building fire done
     * 1056        Large building fire done
     * 1057-1081   Fire shadows
     * 1082-1087   Catapult shot landing
     *
     */

public class MapBobsLst {
    public static final String FILENAME = "DATA/MAPBOBS.LST";

    public static final int BEER_CARGO = 894;
    public static final int TONG_CARGO = 895;
    public static final int AXE_CARGO = 896;
    public static final int SAW_CARGO = 898;
    public static final int PICK_AXE_CARGO = 899;
    public static final int SHOVEL_CARGO = 900;
    public static final int CRUCIBLE_CARGO = 901;
    public static final int FISHING_ROD_CARGO = 902;
    public static final int SCYTHE_CARGO = 903;
    public static final int EMPTY_BUCKET_CARGO = 904;
    public static final int WATER_BUCKET_CARGO = 905;
    public static final int CLEAVER_CARGO = 906;
    public static final int ROLLING_PIN_CARGO = 907;
    public static final int BOW_CARGO = 908;
    public static final int BOAT_CARGO = 909;
    public static final int SWORD_CARGO = 910;
    public static final int ANVIL_CARGO = 911;
    public static final int FLOUR_CARGO = 912;
    public static final int FISH_CARGO = 913;
    public static final int BREAD_CARGO = 914;
    public static final int ROMAN_SHIELD_CARGO = 915;
    public static final int WOOD_CARGO = 916;
    public static final int PLANK_CARGO = 917;
    public static final int STONE_CARGO = 918;
    public static final int VIKING_SHIELD_CARGO = 919;
    public static final int AFRICAN_SHIELD_CARGO = 920;
    public static final int WHEAT_CARGO = 921;
    public static final int COIN_CARGO = 922;
    public static final int GOLD_CARGO = 923;
    public static final int IRON_CARGO = 924;
    public static final int COAL_CARGO = 925;
    public static final int MEAT_CARGO = 926;
    public static final int PIG_CARGO = 927;
    public static final int JAPANESE_SHIELD_CARGO = 928;

    // Inventory icons
    public static final int BEER_ICON = 929;
    public static final int PLIER_ICON = 930;
    public static final int HAMMER_ICON = 931;
    public static final int AXE_ICON = 932;
    public static final int SAW_ICON = 933;
    public static final int PICK_AXE_ICON = 934;
    public static final int SHOVEL_ICON = 935;
    public static final int CRUCIBLE_ICON = 936;
    public static final int FISHING_HOOK_ICON = 937;
    public static final int SCYTHE_ICON = 938;
    public static final int EMPTY_BUCKET_ICON = 939;
    public static final int BUCKET_WITH_WATER_ICON = 940;
    public static final int CLEAVER_ICON = 941;
    public static final int ROLLING_PIN_ICON = 942;
    public static final int SPEAR_ICON = 943;
    public static final int BOAT_ICON = 944;
    public static final int GOLD_SWORD_ICON = 945;
    public static final int IRON_BAR_ICON = 946;
    public static final int FLOUR_BAG_ICON = 947;
    public static final int FISH_ICON = 948;
    public static final int BREAD_ICON = 949;
    public static final int ROMAN_SHIELD_ICON = 950;
    public static final int WOOD_ICON = 951;
    public static final int PLANKS_ICON = 952;
    public static final int STONE_ICON = 953;
    public static final int VIKING_SHIELD_ICON = 954;
    public static final int AFRICAN_SHIELD_ICON = 955;
    public static final int WHEAT_ICON = 956;
    public static final int COIN_ICON = 957;
    public static final int GOLD_ICON = 958;
    public static final int IRON_ICON = 959;
    public static final int COAL_ICON = 960;
    public static final int MEAT_ICON = 961;
    public static final int PIG_ICON = 962;
    public static final int JAPANESE_SHIELD_ICON = 963;
    public static final int BACKGROUND_UNKNOWN_1 = 964;
    public static final int BACKGROUND_UNKNOWN_2 = 965;
    public static final int CARRIER_ICON = 966;
    public static final int WOODCUTTER_ICON = 967;
    public static final int FISHERMAN_ICON = 968;
    public static final int FORESTER_ICON = 969;
    public static final int SAWMILL_WORKER_ICON = 970;
    public static final int STONEMASON_ICON = 971;
    public static final int HUNTER_ICON = 972;
    public static final int FARMER_ICON = 973;
    public static final int MILLER_ICON = 974;
    public static final int BAKER_ICON = 975;
    public static final int BUTCHER_ICON = 976;
    public static final int MINER_ICON = 977;
    public static final int BREWER_ICON = 978;
    public static final int PIG_BREEDER_ICON = 979;
    public static final int DONKEY_BREEDER_ICON = 980;
    public static final int IRON_MELTER_ICON = 981;
    public static final int MINTER_ICON = 982;
    public static final int TOOL_MAKER_ICON = 983;
    public static final int SMITH_ICON = 984;
    public static final int BUILDER_ICON = 985;
    public static final int PLANER_ICON = 986;
    public static final int PRIVATE_SOLDIER_ICON = 987;
    public static final int PRIVATE_FIRST_RANK_SOLDIER_ICON = 988;
    public static final int SERGEANT_SOLDIER_ICON = 989;
    public static final int OFFICER_SOLDIER_ICON = 990;
    public static final int GENERAL_SOLDIER_ICON = 991;
    public static final int GEOLOGIST_ICON = 992;
    public static final int SHIP_ICON = 993;
    public static final int FUR_HAT_UNKNOWN_ICON = 994;
    public static final int ICON_BACKGROUND = 995;


    public static final int SMALL_BURNT_DOWN = 1055;
    public static final int MEDIUM_BURNT_DOWN = 1056;
    public static final int LARGE_BURNT_DOWN = 1057;
    public static final int CYPRESS_SMALLEST = 34;
    public static final int CYPRESS_SMALL = 35;
    public static final int CYPRESS_ALMOST_GROWN = 36;
    public static final int BIRCH_SMALLEST = 49;
    public static final int BIRCH_SMALL = 50;
    public static final int BIRCH_ALMOST_GROWN = 51;
    public static final int OAK_SMALLEST = 64;
    public static final int OAK_SMALL = 65;
    public static final int OAK_ALMOST_GROWN = 66;
    public static final int PALM_1_SMALLEST = 79;
    public static final int PALM_1_SMALL = 80;
    public static final int PALM_1_ALMOST_GROWN = 81;
    public static final int PALM_2_SMALLEST = 94;
    public static final int PALM_2_SMALL = 95;
    public static final int PALM_2_ALMOST_GROWN = 96;
    public static final int PINE_SMALLEST = 117;
    public static final int PINE_SMALL = 118;
    public static final int PINE_ALMOST_GROWN = 119;
    public static final int CHERRY_SMALLEST = 132;
    public static final int CHERRY_SMALL = 133;
    public static final int CHERRY_ALMOST_GROWN = 134;
    public static final int FIR_SMALLEST = 147;
    public static final int FIR_SMALL = 148;
    public static final int FIR_ALMOST_GROWN = 149;
    public static final int CYPRESS_FALLING = 37;
    public static final int BIRCH_FALLING = 52;
    public static final int OAK_FALLING = 67;
    public static final int PALM_1_FALLING = 82;
    public static final int PALM_2_FALLING = 97;
    public static final int PINE_FALLING = 120;
    public static final int CHERRY_FALLING = 135;
    public static final int FIR_FALLING = 150;
    public static final int CYPRESS_TREE_SHADOW_ANIMATION = 154;
    public static final int CYPRESS_SHADOW_SMALLEST = 162;
    public static final int CYPRESS_SHADOW_SMALL = 163;
    public static final int CYPRESS_SHADOW_MEDIUM = 164;
    public static final int CYPRESS_FALLING_SHADOW = 165;
    public static final int BIRCH_TREE_SHADOW_ANIMATION = 169;
    public static final int BIRCH_FALLING_SHADOW = 180;
    public static final int BIRCH_SHADOW_SMALLEST = 177;
    public static final int BIRCH_SHADOW_SMALL = 178;
    public static final int BIRCH_SHADOW_MEDIUM = 179;
    public static final int OAK_TREE_SHADOW_ANIMATION = 184;
    public static final int OAK_SHADOW_SMALLEST = 192;
    public static final int OAK_SHADOW_SMALL = 193;
    public static final int OAK_SHADOW_MEDIUM = 194;
    public static final int OAK_FALLING_SHADOW = 195;
    public static final int PALM_1_TREE_SHADOW_ANIMATION = 199;
    public static final int PALM_1_SHADOW_SMALLEST = 207;
    public static final int PALM_1_SHADOW_SMALL = 208;
    public static final int PALM_1_SHADOW_ALMOST_GROWN = 209;
    public static final int PALM_1_FALLING_SHADOW = 210;
    public static final int PALM_2_TREE_SHADOW_ANIMATION = 214;
    public static final int PALM_2_SHADOW_SMALLEST = 222;
    public static final int PALM_2_SHADOW_SMALL = 223;
    public static final int PALM_2_SHADOW_ALMOST_GROWN = 224;
    public static final int PALM_2_FALLING_SHADOW = 225;
    public static final int PINE_APPLE_SHADOW_ANIMATION = 229;
    public static final int PINE_TREE_SHADOW_ANIMATION = 237;
    public static final int PINE_SMALLEST_SHADOW = 245;
    public static final int PINE_SMALL_SHADOW = 246;
    public static final int PINE_ALMOST_GROWN_SHADOW = 247;
    public static final int PINE_FALLING_SHADOW = 248;
    public static final int CHERRY_TREE_SHADOW_ANIMATION = 252;
    public static final int CHERRY_SMALLEST_SHADOW = 260;
    public static final int CHERRY_SMALL_SHADOW = 261;
    public static final int CHERRY_ALMOST_GROWN_SHADOW = 262;
    public static final int CHERRY_FALLING_SHADOW = 263;
    public static final int FIR_TREE_SHADOW_ANIMATION = 267;
    public static final int FIR_SMALLEST_SHADOW = 275;
    public static final int FIR_SMALL_SHADOW = 276;
    public static final int FIR_ALMOST_GROWN_SHADOW = 277;
    public static final int FIR_FALLING_SHADOW = 278;
    public static final int ICE_BEAR_WALKING_SOUTH_EAST_ANIMATION = 483;
    public static final int ICE_BEAR_WALKING_SOUTH_WEST_ANIMATION = 491;
    public static final int ICE_BEAR_WALKING_WEST_ANIMATION = 499;
    public static final int ICE_BEAR_WALKING_NORTH_WEST_ANIMATION = 507;
    public static final int ICE_BEAR_WALKING_NORTH_EAST_ANIMATION = 515;
    public static final int FOX_WALKING_EAST_ANIMATION = 601;
    public static final int FOX_WALKING_SOUTH_EAST_ANIMATION = 607;
    public static final int FOX_WALKING_SOUTH_WEST_ANIMATION = 613;
    public static final int FOX_WALKING_WEST_ANIMATION = 619;
    public static final int FOX_WALKING_NORTH_WEST_ANIMATION = 625;
    public static final int FOX_WALKING_NORTH_EAST_ANIMATION = 631;
    public static final int RABBIT_WALKING_SOUTH_EAST_ANIMATION = 531;
    public static final int RABBIT_WALKING_SOUTH_WEST_ANIMATION = 537;
    public static final int RABBIT_WALKING_WEST_ANIMATION = 543;
    public static final int RABBIT_WALKING_NORTH_WEST_ANIMATION = 549;
    public static final int RABBIT_WALKING_NORTH_EAST_ANIMATION = 555;
    public static final int STAG_WALKING_SOUTH_EAST_ANIMATION = 652;
    public static final int STAG_WALKING_SOUTH_WEST_ANIMATION = 660;
    public static final int STAG_WALKING_WEST_ANIMATION = 668;
    public static final int STAG_WALKING_NORTH_WEST_ANIMATION = 676;
    public static final int STAG_WALKING_NORTH_EAST_ANIMATION = 684;
    public static final int DEER_WALKING_EAST_ANIMATION = 699;
    public static final int DEER_WALKING_SOUTH_EAST_ANIMATION = 707;
    public static final int DEER_WALKING_SOUTH_WEST_ANIMATION = 715;
    public static final int DEER_WALKING_WEST_ANIMATION = 723;
    public static final int DEER_WALKING_NORTH_WEST_ANIMATION = 731;
    public static final int DEER_WALKING_NORTH_EAST_ANIMATION = 739;
    public static final int SHEEP_WALKING_SOUTH_EAST_ANIMATION = 820;
    public static final int SHEEP_WALKING_SOUTH_WEST_ANIMATION = 822;
    public static final int SHEEP_WALKING_WEST_ANIMATION = 824;
    public static final int SHEEP_WALKING_NORTH_WEST_ANIMATION = 826;
    public static final int SHEEP_WALKING_NORTH_EAST_ANIMATION = 828;
    public static final int DEER_2_WALKING_SOUTH_EAST_ANIMATION = 769;
    public static final int DEER_2_WALKING_SOUTH_WEST_ANIMATION = 777;
    public static final int DEER_2_WALKING_WEST_ANIMATION = 785;
    public static final int DEER_2_WALKING_NORTH_WEST_ANIMATION = 793;
    public static final int DEER_2_WALKING_NORTH_EAST_ANIMATION = 801;
    public static final int CROP_TYPE_1_NEWLY_PLANTED_SHADOW = 357;
    public static final int CROP_TYPE_1_LITTLE_GROWTH_SHADOW = 358;
    public static final int CROP_TYPE_1_MORE_GROWTH_SHADOW = 359;
    public static final int CROP_TYPE_1_FULLY_GROWN_SHADOW = 360;
    public static final int CROP_TYPE_1_JUST_HARVESTED_SHADOW = 361;
    public static final int CROP_TYPE_2_NEWLY_PLANTED_SHADOW = 362;
    public static final int CROP_TYPE_2_LITTLE_GROWTH_SHADOW = 363;
    public static final int CROP_TYPE_2_MORE_GROWTH_SHADOW = 364;
    public static final int CROP_TYPE_2_FULLY_GROWN_SHADOW = 365;
    public static final int CROP_TYPE_2_JUST_HARVESTED_SHADOW = 366;

    public static final int STONE_TYPE_1_MINI = 298;
    public static final int STONE_TYPE_1_LITTLE = 299;
    public static final int STONE_TYPE_1_LITTLE_MORE = 300;
    public static final int STONE_TYPE_1_MIDDLE = 301;
    public static final int STONE_TYPE_1_ALMOST_FULL = 302;
    public static final int STONE_TYPE_1_FULL = 303;
    public static final int STONE_TYPE_2_MINI = 304;
    public static final int STONE_TYPE_2_LITTLE = 305;
    public static final int STONE_TYPE_2_LITTLE_MORE = 306;
    public static final int STONE_TYPE_2_MIDDLE = 307;
    public static final int STONE_TYPE_2_ALMOST_FULL = 308;
    public static final int STONE_TYPE_2_FULL = 309;
    public static final int SELECTED_POINT = 1;
    public static final int HOVER_POINT = 3;
    public static final int HOVER_AVAILABLE_FLAG = 5;
    public static final int HOVER_AVAILABLE_MINE = 6;
    public static final int HOVER_AVAILABLE_SMALL_BUILDING = 7;
    public static final int HOVER_AVAILABLE_MEDIUM_BUILDING = 8;
    public static final int HOVER_AVAILABLE_LARGE_BUILDING = 9;
    public static final int HOVER_AVAILABLE_HARBOR = 10;
    public static final int AVAILABLE_FLAG = 11;
    public static final int AVAILABLE_SMALL_BUILDING = 12;
    public static final int AVAILABLE_MEDIUM_BUILDING = 13;
    public static final int AVAILABLE_LARGE_BUILDING = 14;
    public static final int AVAILABLE_MINE = 15;
    public static final int AVAILABLE_HARBOR = 16;
    public static final int CROP_TYPE_1_NEWLY_PLANTED = 311;
    public static final int CROP_TYPE_1_LITTLE_GROWTH = 312;
    public static final int CROP_TYPE_1_MORE_GROWTH = 313;
    public static final int CROP_TYPE_1_FULLY_GROWN = 314;
    public static final int CROP_TYPE_1_JUST_HARVESTED = 315;
    public static final int CROP_TYPE_2_NEWLY_PLANTED = 316;
    public static final int CROP_TYPE_2_LITTLE_GROWTH = 317;
    public static final int CROP_TYPE_2_MORE_GROWTH = 318;
    public static final int CROP_TYPE_2_FULLY_GROWN = 319;
    public static final int CROP_TYPE_2_JUST_HARVESTED = 320;
    public static final int IRON_SIGN_SMALL_UP_RIGHT = 372;
    public static final int IRON_SIGN_MEDIUM_UP_RIGHT = 373;
    public static final int IRON_SIGN_LARGE_UP_RIGHT = 374;
    public static final int GOLD_SIGN_SMALL_UP_RIGHT = 375;
    public static final int GOLD_SIGN_MEDIUM_UP_RIGHT = 376;
    public static final int GOLD_SIGN_LARGE_UP_RIGHT = 377;
    public static final int COAL_SIGN_SMALL_UP_RIGHT = 378;
    public static final int COAL_SIGN_MEDIUM_UP_RIGHT = 379;
    public static final int COAL_SIGN_LARGE_UP_RIGHT = 380;
    public static final int GRANITE_SIGN_SMALL_UP_RIGHT = 381;
    public static final int GRANITE_SIGN_MEDIUM_UP_RIGHT = 382;
    public static final int GRANITE_SIGN_LARGE_UP_RIGHT = 383;
    public static final int WATER_SIGN_LARGE_UP_RIGHT = 384;
    public static final int NOTHING_SIGN_UP_RIGHT = 385;
    public static final int ROAD_BUILDING_START_POINT = 2;
    public static final int ROAD_BUILDING_SAME_LEVEL_CONNECTION = 18;
    public static final int ROAD_BUILDING_LITTLE_HIGHER_CONNECTION = 19;
    public static final int ROAD_BUILDING_MEDIUM_HIGHER_CONNECTION = 20;
    public static final int ROAD_BUILDING_MUCH_HIGHER_CONNECTION = 21;
    public static final int ROAD_BUILDING_LITTLE_LOWER_CONNECTION = 22;
    public static final int ROAD_BUILDING_MEDIUM_LOWER_CONNECTION = 23;
    public static final int ROAD_BUILDING_MUCH_LOWER_CONNECTION = 24;
    public static final int MINI_FIRE_ANIMATION = 419;
    public static final int SMALL_FIRE_ANIMATION = 1031;
    public static final int MEDIUM_FIRE_ANIMATION = 1039;
    public static final int LARGE_FIRE_ANIMATION = 1047;
    public static final int CYPRESS_TREE_ANIMATION = 26;
    public static final int BIRCH_TREE_ANIMATION = 41;
    public static final int OAK_TREE_ANIMATION = 56;
    public static final int PALM_1_TREE_ANIMATION = 71;
    public static final int PALM_2_TREE_ANIMATION = 86;
    public static final int PINE_APPLE_ANIMATION = 101;
    public static final int PINE_TREE_ANIMATION = 109;
    public static final int CHERRY_TREE_ANIMATION = 124;
    public static final int FIR_TREE_ANIMATION = 139;
    public static final int DECORATIVE_STANDING_DEAD_TREE_SHADOW = 334;
    public static final int DECORATIVE_FALLEN_TREE_SHADOW = 333;
    public static final int DECORATIVE_MUSHROOM_SHADOW = 329;
    public static final int DECORATIVE_MINI_STONE_SHADOW = 330;
    public static final int DECORATIVE_MINI_STONES_SHADOW = 331;
    public static final int DECORATIVE_STONE_SHADOW = 332;
    public static final int DECORATIVE_SKELETON_SHADOW = 335;
    public static final int DECORATIVE_MINI_SKELETON_SHADOW = 336;
    public static final int DECORATIVE_FLOWERS_SHADOW = 337;
    public static final int DECORATIVE_LARGE_BUSH_SHADOW = 338;
    public static final int DECORATIVE_LARGER_STONES_SHADOW = 339;
    public static final int DECORATIVE_CACTUS_1_SHADOW = 340;
    public static final int DECORATIVE_CACTUS_2_SHADOW = 341;
    public static final int DECORATIVE_BEACH_GRASS_SHADOW = 342;
    public static final int DECORATIVE_SMALL_GRASS_SHADOW = 343;
    public static final int RABBIT_WALKING_EAST_ANIMATION = 525;
    public static final int STAG_WALKING_EAST_ANIMATION = 644;
    public static final int SHEEP_WALKING_EAST_ANIMATION = 818;
    public static final int ICE_BEAR_WALKING_EAST_ANIMATION = 475;
    public static final int DEER_2_WALKING_EAST_ANIMATION = 761;
    public static final int DUCK_EAST = 754;
    public static final int DECORATIVE_MUSHROOM = 283;
    public static final int DECORATIVE_MINI_STONE = 284;
    public static final int DECORATIVE_MINI_STONES = 285;
    public static final int DECORATIVE_STONE = 286;
    public static final int DECORATIVE_FALLEN_TREE = 287;
    public static final int DECORATIVE_STANDING_DEAD_TREE = 288;
    public static final int DECORATIVE_SKELETON = 289;
    public static final int DECORATIVE_MINI_SKELETON = 290;
    public static final int DECORATIVE_FLOWERS = 291;
    public static final int DECORATIVE_LARGE_BUSH = 292;
    public static final int DECORATIVE_LARGER_STONES = 293;
    public static final int DECORATIVE_CACTUS_1 = 294;
    public static final int DECORATIVE_CACTUS_2 = 295;
    public static final int DECORATIVE_BEACH_GRASS = 296;
    public static final int DECORATIVE_SMALL_GRASS = 297;
    public static final int DECORATIVE_BUSH = 321;
    public static final int DECORATIVE_SMALL_BUSH = 322;
    public static final int DECORATIVE_MINI_BUSH = 323;
    public static final int SIGN_SHADOW = 386;
    public static final int STONE_TYPE_1_MINI_SHADOW = 344;
    public static final int STONE_TYPE_1_LITTLE_SHADOW = 345;
    public static final int STONE_TYPE_1_LITTLE_MORE_SHADOW = 346;
    public static final int STONE_TYPE_1_MIDDLE_SHADOW = 347;
    public static final int STONE_TYPE_1_ALMOST_FULL_SHADOW = 348;
    public static final int STONE_TYPE_1_FULL_SHADOW = 349;
    public static final int STONE_TYPE_2_MINI_SHADOW = 350;
    public static final int STONE_TYPE_2_LITTLE_SHADOW = 351;
    public static final int STONE_TYPE_2_LITTLE_MORE_SHADOW = 352;
    public static final int STONE_TYPE_2_MIDDLE_SHADOW = 353;
    public static final int STONE_TYPE_2_ALMOST_FULL_SHADOW = 354;
    public static final int STONE_TYPE_2_FULL_SHADOW = 355;
    public static final int DECORATIVE_BUSH_SHADOW = 367;
    public static final int DECORATIVE_SMALL_BUSH_SHADOW = 368;
    public static final int DECORATIVE_MINI_BUSH_SHADOW = 369;
    public static final int FOX_SHADOW_EAST = 638;
    public static final int FOX_SHADOW_SOUTH_EAST = 639;
    public static final int FOX_SHADOW_SOUTH_WEST = 640;
    public static final int FOX_SHADOW_WEST = 641;
    public static final int FOX_SHADOW_NORTH_WEST = 642;
    public static final int FOX_SHADOW_NORTH_EAST = 643;
    public static final int STAG_SHADOW_EAST = 693;
    public static final int STAG_SHADOW_SOUTH_EAST = 694;
    public static final int STAG_SHADOW_SOUTH_WEST = 695;
    public static final int STAG_SHADOW_WEST = 696;
    public static final int STAG_SHADOW_NORTH_WEST = 697;
    public static final int STAG_SHADOW_NORTH_EAST = 698;
    public static final int DEER_SHADOW_EAST = 748;
    public static final int DEER_SHADOW_SOUTH_EAST = 749;
    public static final int DEER_SHADOW_SOUTH_WEST = 750;
    public static final int DEER_SHADOW_WEST = 751;
    public static final int DEER_SHADOW_NORTH_WEST = 752;
    public static final int DEER_SHADOW_NORTH_EAST = 753;
    public static final int DUCK_SHADOW = 760;
    public static final int DEER_2_SHADOW_EAST = 809;
    public static final int DEER_2_SHADOW_SOUTH_EAST = 810;
    public static final int DEER_2_SHADOW_SOUTH_WEST = 811;
    public static final int SMALL_FIRE_SHADOW_ANIMATION = 1058;
    public static final int MEDIUM_FIRE_SHADOW_ANIMATION = 1066;
    public static final int LARGE_FIRE_SHADOW_ANIMATION = 1074;
    public static final int DECORATIVE_GRASS_2 = 324;
    public static final int DECORATIVE_MINI_GRASS = 325;
    public static final int DECORATIVE_GRASS_2_SHADOW = 370;
    public static final int DECORATIVE_MINI_GRASS_SHADOW = 371;
}
