package org.appland.settlers.assets.gamefiles;

import org.appland.settlers.assets.WorkerDetails;

import static org.appland.settlers.assets.BodyType.FAT;
import static org.appland.settlers.assets.BodyType.THIN;


/**
 * JOBS.BOB
 * SLIM GUY (no head)
 * 0-7   - Walk east
 * 8-15  - Walk north-east
 * 16-23 - Walk south-west
 * 24-31 - Walk west
 * 32-39 - Walk north-west
 * 40-47 - Walk east (south-east?)
 *
 * FAT GUY (no head)
 * 48-55 - Walk east
 * 56-63 - Walk north-east (?)
 * 64-71 - Walk south-west
 * 72-79 - Walk west
 * 80-87 - Walk north-west
 * 88-95 - Walk south-east
 *
 * HEAD 1
 * 96 - East
 * 97 - South-east (?)
 * 98 - South-west
 * 99 - West
 * 100 - North-west
 * 101 - North-east
 *
 * HEAD 2
 * 102 - East
 * 103 - South-east
 * 104 - South-west
 * 105 - West
 * 106 - North-west
 * 107 - North-east
 *
 * HEAD 3
 * 108 - East
 * 109 - South-east
 * 110 - South-west
 * 111 - West
 * 112 - North-west
 * 113 - North-east
 *
 * HEAD 4
 * 114 - East
 * 115 - South-east
 * 116 - South-west
 * 117 - West
 * 118 - North-west
 * 119 - North-east
 *
 * HEAD 5
 * 120 - East
 * 121 - South-east
 * 122 - South-west
 * 123 - West
 * 124 - North-west
 * 125 - North-east
 *
 * HEAD 6
 * 126 - East
 * 127 - South-east
 * 128 - South-west
 * 129 - West
 * 130 - North-west
 * 131 - North-east
 *
 * HEAD 7
 * 132-137 - E, SE, SW, W, NW, NE
 *
 * HEAD 8
 * 138-142 - E, SE, SW, (W missing) NW, NE
 *
 * HEAD 9
 * 143-148 - E, SE, SW, W, NW, NE
 *
 * HEAD 10
 * 149-152 - E, SE, W, NE
 *
 * HEAD 11
 * 153-156 - E, SE, W, NE
 *
 * HEAD 12
 * 157-158 - E, NE
 *
 * HEAD 13
 * 159-162 - E, SE, NW, NE
 *
 * HEAD 14
 * 163-167 - E, SE, SW, NW, NE
 *
 * HEAD 15
 * 168-172 - E, SE, W, NW, NE
 *
 * HEAD 16
 * 173-178 - E, SE, SW, W, NW, NE
 *
 * HEAD 17
 * 179-184 - E, SE, SW, W, NW, NE
 *
 * VERY MINOR DETAIL (overlay?)
 * 185-189
 *
 * HEAD 18
 * 190-195 - E, SE, SW, W, NW, NE
 *
 * HEAD 19
 * 196-201 - E, SE, SW, W, NW, NE
 *
 * HEAD 20
 * 202-207 - E, SE, SW, W, NW, NE
 *
 * HEAD 21
 * 208-213 - E, SE, SW, W, NW, NE
 *
 * ... more heads ...
 *
 * HEAD WITH AXE OR HAMMER
 * 278-283 - E, SE, SW, W, NW, NE
 *
 * ...
 *
 * WOODCUTTER HEAD
 * 310-315 - E, SE, SW, W, NW, NE
 * 316-17 - W - animation(?)
 *
 * ... more heads and sometimes a bit of body ...
 *
 * MILITARY
 * 859-906 - Roman private (?)
 * 907-1098 - Other roman soldiers
 * 1099-1338 - Viking soldiers
 * 1339-1626 - Japanese soldiers
 * 1627-1962 - African soldiers (?)
 *
 *
 */
public class JobsBob {
    public static final String FILENAME = "DATA/BOBS/JOBS.BOB";

    public static final int HELPER_BOB_ID = 0;
    public static final int WOODCUTTER_BOB_ID = 5;
    public static final int FISHERMAN_BOB_ID = 12;
    public static final int FORESTER_BOB_ID = 8;
    public static final int CARPENTER_BOB_ID = 6;
    public static final int STONEMASON_BOB_ID = 7;
    public static final int HUNTER_BOB_ID = 20;
    public static final int FARMER_BOB_ID = 13;
    public static final int MILLER_BOB_ID = 16;
    public static final int BAKER_BOB_ID = 17;
    public static final int BUTCHER_BOB_ID = 15;
    public static final int MINER_BOB_ID = 10;
    public static final int BREWER_BOB_ID = 3;
    public static final int PIG_BREEDER_BOB_ID = 14;
    public static final int DONKEY_BREEDER_BOB_ID = 24;
    public static final int IRON_FOUNDER_BOB_ID = 11;
    public static final int MINTER_BOB_ID = 9;
    public static final int METALWORKER_BOB_ID = 18;
    public static final int ARMORER_BOB_ID = 4;
    public static final int BUILDER_BOB_ID = 23;
    public static final int PLANER_BOB_ID = 22;
    public static final int PRIVATE_BOB_ID = -30;
    public static final int PRIVATE_FIRST_CLASS_BOB_ID = -31;
    public static final int SERGEANT_BOB_ID = -32;
    public static final int OFFICER_BOB_ID = -33;
    public static final int GENERAL_BOB_ID = -34;
    public static final int GEOLOGIST_BOB_ID = 26;
    public static final int SHIP_WRIGHT_BOB_ID = 25;
    public static final int SCOUT_BOB_ID = -35;
    public static final int PACK_DONKEY_BOB_ID = 37;
    public static final int BOAT_CARRIER_BOB_ID = 37;
    public static final int CHAR_BURNER_BOB_ID = 37;

    public static final int WOODCUTTER_WITH_WOOD_CARGO_BOB_ID = 61;
    public static final int CARPENTER_WITH_PLANK_BOB_ID = 62;
    public static final int STONEMASON_WITH_STONE_CARGO_BOB_ID = 63;
    public static final int MINTER_WITH_COIN_CARGO_BOB_ID = 64;
    public static final int MINER_WITH_GOLD_CARGO_BOB_ID = 65;
    public static final int MINER_WITH_IRON_CARGO_BOB_ID = 66;
    public static final int MINER_WITH_COAL_CARGO_BOB_ID = 67;
    public static final int MINER_WITH_STONE_CARGO_BOB_ID = 68;
    public static final int FISHERMAN_WITH_FISH_CARGO_BOB_ID = 70;
    public static final int FARMER_WITH_WHEAT_CARGO_BOB_ID = 71;
    public static final int PIG_BREEDER_WITH_PIG_CARGO_BOB_ID = 73;
    public static final int MILLER_WITH_FLOUR_CARGO_BOB_ID = 75;
    public static final int BAKER_WITH_BREAD_CARGO_BOB_ID = 76;
    public static final int METAL_WORKER_WITH_TONGS_CARGO_BOB_ID = 78;
    public static final int METAL_WORKER_WITH_HAMMER_CARGO_BOB_ID = 79;
    public static final int METAL_WORKER_WITH_AXE_CARGO_BOB_ID = 80;
    public static final int METAL_WORKER_WITH_PICK_AXE_CARGO_BOB_ID = 81;
    public static final int METAL_WORKER_WITH_SHOVEL_CARGO_BOB_ID = 82;
    public static final int METAL_WORKER_WITH_CRUCIBLE_CARGO_BOB_ID = 83;
    public static final int METAL_WORKER_WITH_FISHING_ROD_CARGO_BOB_ID = 84;
    public static final int METAL_WORKER_WITH_SCYTHE_CARGO_BOB_ID = 85;
    public static final int METAL_WORKER_WITH_CLEAVER_CARGO_BOB_ID = 87;
    public static final int METAL_WORKER_WITH_ROLLING_PIN_CARGO_BOB_ID = 88;
    public static final int HUNTER_WITH_MEAT_CARGO_BOB_ID = 89;
    public static final int SHIPWRIGHT_WITH_BOAT_CARGO_BOB_ID = 90;
    public static final int METAL_WORKER_WITH_SAW_CARGO_BOB_ID = 91;
    public static final int SHIPWRIGHT_WITH_PLANK_CARGO_BOB_ID = 92;

    public static final WorkerDetails HUNTER_BOB = new WorkerDetails(THIN, JobsBob.HUNTER_BOB_ID);
    public static final WorkerDetails SHIPWRIGHT_BOB = new WorkerDetails(THIN, JobsBob.SHIP_WRIGHT_BOB_ID);
    public static final WorkerDetails METAL_WORKER_BOB = new WorkerDetails(THIN, JobsBob.METALWORKER_BOB_ID);
    public static final WorkerDetails BAKER_BOB = new WorkerDetails(FAT, JobsBob.BAKER_BOB_ID);
    public static final WorkerDetails MILLER_BOB = new WorkerDetails(FAT, JobsBob.MILLER_BOB_ID);
    public static final WorkerDetails PIG_BREEDER_BOB = new WorkerDetails(THIN, JobsBob.PIG_BREEDER_BOB_ID);
    public static final WorkerDetails FARMER_BOB = new WorkerDetails(THIN, JobsBob.FARMER_BOB_ID);
    public static final WorkerDetails FISHERMAN_BOB = new WorkerDetails(THIN, JobsBob.FISHERMAN_BOB_ID);
    public static final WorkerDetails MINER_BOB = new WorkerDetails(THIN, JobsBob.MINER_BOB_ID);
    public static final WorkerDetails MINTER_BOB = new WorkerDetails(THIN, JobsBob.MINTER_BOB_ID);
    public static final WorkerDetails STONEMASON_BOB = new WorkerDetails(THIN, JobsBob.STONEMASON_BOB_ID);
    public static final WorkerDetails CARPENTER_BOB = new WorkerDetails(THIN, JobsBob.CARPENTER_BOB_ID);
    public static final WorkerDetails WOODCUTTER_BOB = new WorkerDetails(THIN, JobsBob.WOODCUTTER_BOB_ID);

    public static final int WOODCUTTER_CARGO_EAST = 2059;
    public static final int WOODCUTTER_CARGO_SOUTH_EAST = 2060;
    public static final int WOODCUTTER_CARGO_SOUTH_WEST = 2061;
    public static final int WOODCUTTER_CARGO_WEST = 2062;
    public static final int WOODCUTTER_CARGO_NORTH_WEST = 2063;
    public static final int WOODCUTTER_CARGO_NORTH_EAST = 2064;

    public static final int FISHERMAN_CARGO_EAST = 2086;
    public static final int FISHERMAN_CARGO_SOUTH_EAST = 2087;
    public static final int FISHERMAN_CARGO_SOUTH_WEST = 2088;
    public static final int FISHERMAN_CARGO_WEST = 2089;
    public static final int FISHERMAN_CARGO_NORTH_WEST = 2090;
    public static final int FISHERMAN_CARGO_NORTH_EAST = 2091;

    public static final int CARPENTER_CARGO_SOUTH_EAST = 2066;
    public static final int CARPENTER_CARGO_NORTH_WEST = 2067;

    public static final int STONEMASON_CARGO_EAST = 2068;
    public static final int STONEMASON_CARGO_SOUTH_EAST = 2069;
    public static final int STONEMASON_CARGO_SOUTH_WEST = 2070;
    public static final int STONEMASON_CARGO_WEST = 2071;
    public static final int STONEMASON_CARGO_NORTH_WEST = 2072;
    public static final int STONEMASON_CARGO_NORTH_EAST = 2073;

    public static final int MINTER_CARGO_SOUTH_EAST = 2074;
    public static final int MINTER_CARGO_NORTH_WEST = 2075;

    public static final int FARMER_CARGO_EAST = 2093;
    public static final int FARMER_CARGO_SOUTH_EAST = 2094;
    public static final int FARMER_CARGO_SOUTH_WEST = 2095;
    public static final int FARMER_CARGO_WEST = 2096;
    public static final int FARMER_CARGO_NORTH_WEST = 2097;
    public static final int FARMER_CARGO_NORTH_EAST = 2098;
    public static final int PIG_BREEDER_CARGO_SOUTH_EAST_ANIMATION_0 = 2100;
    public static final int PIG_BREEDER_CARGO_SOUTH_EAST_ANIMATION_1 = 2102;
    public static final int PIG_BREEDER_CARGO_SOUTH_EAST_ANIMATION_2 = 2104;
    public static final int PIG_BREEDER_CARGO_SOUTH_EAST_ANIMATION_3 = 2108;
    public static final int PIG_BREEDER_CARGO_SOUTH_EAST_ANIMATION_4 = 2110;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_0 = 2101;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_1 = 2103;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_2 = 2105;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_3 = 2106;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_4 = 2107;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_5 = 2109;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_6 = 2111;
    public static final int PIG_BREEDER_CARGO_NORTH_WEST_ANIMATION_7 = 2112;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_0 = 2122;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_1 = 2124;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_2 = 2126;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_3 = 2128;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_4 = 2130;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_5 = 2132;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_6 = 2134;
    public static final int MILLER_CARGO_SOUTH_EAST_ANIMATION_7 = 2135;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_0 = 2123;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_1 = 2125;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_2 = 2127;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_3 = 2129;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_4 = 2131;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_5 = 2133;
    public static final int MILLER_CARGO_NORTH_WEST_ANIMATION_6 = 2136;
    public static final int BAKER_CARGO_EAST_ANIMATION_0 = 2137;
    public static final int BAKER_CARGO_EAST_ANIMATION_1 = 2141;
    public static final int BAKER_CARGO_EAST_ANIMATION_2 = 2148;
    public static final int BAKER_CARGO_EAST_ANIMATION_3 = 2152;
    public static final int BAKER_CARGO_EAST_ANIMATION_4 = 2162;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_0 = 2138;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_1 = 2142;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_2 = 2145;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_3 = 2149;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_4 = 2153;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_5 = 2156;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_6 = 2159;
    public static final int BAKER_CARGO_SOUTH_EAST_ANIMATION_7 = 2163;
    public static final int BAKER_CARGO_WEST_ANIMATION_0 = 2139;
    public static final int BAKER_CARGO_WEST_ANIMATION_1 = 2143;
    public static final int BAKER_CARGO_WEST_ANIMATION_2 = 2146;
    public static final int BAKER_CARGO_WEST_ANIMATION_3 = 2150;
    public static final int BAKER_CARGO_WEST_ANIMATION_4 = 2154;
    public static final int BAKER_CARGO_WEST_ANIMATION_5 = 2157;
    public static final int BAKER_CARGO_WEST_ANIMATION_6 = 2160;
    public static final int BAKER_CARGO_WEST_ANIMATION_7 = 2164;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_0 = 2140;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_1 = 2144;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_2 = 2147;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_3 = 2151;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_4 = 2155;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_5 = 2158;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_6 = 2161;
    public static final int BAKER_CARGO_NORTH_WEST_ANIMATION_7 = 2165;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_0 = 2187;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_1 = 2189;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_2 = 2190;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_3 = 2192;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_4 = 2193;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_5 = 2194;
    public static final int METAL_WORKER_HAMMER_CARGO_SOUTH_EAST_ANIMATION_6 = 2196;
    public static final int METAL_WORKER_HAMMER_CARGO_NORTH_WEST_ANIMATION_0 = 2188;
    public static final int METAL_WORKER_HAMMER_CARGO_NORTH_WEST_ANIMATION_1 = 2191;
    public static final int METAL_WORKER_HAMMER_CARGO_NORTH_WEST_ANIMATION_2 = 2195;
    public static final int METAL_WORKER_HAMMER_CARGO_NORTH_WEST_ANIMATION_3 = 2197;
    public static final int METAL_WORKER_AXE_CARGO_SOUTH_EAST_ANIMATION_0 = 2198;
    public static final int METAL_WORKER_AXE_CARGO_SOUTH_EAST_ANIMATION_1 = 2200;
    public static final int METAL_WORKER_AXE_CARGO_SOUTH_EAST_ANIMATION_2 = 2201;
    public static final int METAL_WORKER_AXE_CARGO_SOUTH_EAST_ANIMATION_3 = 2205;
    public static final int METAL_WORKER_AXE_CARGO_SOUTH_EAST_ANIMATION_4 = 2206;
    public static final int METAL_WORKER_AXE_CARGO_NORTH_WEST_ANIMATION_0 = 2199;
    public static final int METAL_WORKER_AXE_CARGO_NORTH_WEST_ANIMATION_1 = 2202;
    public static final int METAL_WORKER_AXE_CARGO_NORTH_WEST_ANIMATION_2 = 2203;
    public static final int METAL_WORKER_AXE_CARGO_NORTH_WEST_ANIMATION_3 = 2204;
    public static final int METAL_WORKER_AXE_CARGO_NORTH_WEST_ANIMATION_4 = 2207;
    public static final int METAL_WORKER_AXE_CARGO_NORTH_WEST_ANIMATION_5 = 2208;
    public static final int METAL_WORKER_PICK_AXE_CARGO_SOUTH_EAST_ANIMATION_0 = 2209;
    public static final int METAL_WORKER_PICK_AXE_CARGO_SOUTH_EAST_ANIMATION_1 = 2211;
    public static final int METAL_WORKER_PICK_AXE_CARGO_SOUTH_EAST_ANIMATION_2 = 2213;
    public static final int METAL_WORKER_PICK_AXE_CARGO_SOUTH_EAST_ANIMATION_3 = 2216;
    public static final int METAL_WORKER_PICK_AXE_CARGO_SOUTH_EAST_ANIMATION_4 = 2217;
    public static final int METAL_WORKER_PICK_AXE_CARGO_SOUTH_EAST_ANIMATION_5 = 2219;
    public static final int METAL_WORKER_PICK_AXE_CARGO_NORTH_WEST_ANIMATION_0 = 2210;
    public static final int METAL_WORKER_PICK_AXE_CARGO_NORTH_WEST_ANIMATION_1 = 2212;
    public static final int METAL_WORKER_PICK_AXE_CARGO_NORTH_WEST_ANIMATION_2 = 2214;
    public static final int METAL_WORKER_PICK_AXE_CARGO_NORTH_WEST_ANIMATION_3 = 2215;
    public static final int METAL_WORKER_PICK_AXE_CARGO_NORTH_WEST_ANIMATION_4 = 2218;
    public static final int METAL_WORKER_PICK_AXE_CARGO_NORTH_WEST_ANIMATION_5 = 2220;
    public static final int METAL_WORKER_SHOVEL_CARGO_SOUTH_EAST_ANIMATION_0 = 2221;
    public static final int METAL_WORKER_SHOVEL_CARGO_SOUTH_EAST_ANIMATION_1 = 2223;
    public static final int METAL_WORKER_SHOVEL_CARGO_SOUTH_EAST_ANIMATION_2 = 2224;
    public static final int METAL_WORKER_SHOVEL_CARGO_SOUTH_EAST_ANIMATION_3 = 2227;
    public static final int METAL_WORKER_SHOVEL_CARGO_SOUTH_EAST_ANIMATION_4 = 2228;
    public static final int METAL_WORKER_SHOVEL_CARGO_NORTH_WEST_ANIMATION_0 = 2222;
    public static final int METAL_WORKER_SHOVEL_CARGO_NORTH_WEST_ANIMATION_1 = 2225;
    public static final int METAL_WORKER_SHOVEL_CARGO_NORTH_WEST_ANIMATION_2 = 2226;
    public static final int METAL_WORKER_SHOVEL_CARGO_NORTH_WEST_ANIMATION_3 = 2229;
    public static final int METAL_WORKER_SHOVEL_CARGO_NORTH_WEST_ANIMATION_4 = 2230;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_SOUTH_EAST_ANIMATION_0 = 2231;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_SOUTH_EAST_ANIMATION_1 = 2233;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_SOUTH_EAST_ANIMATION_2 = 2234;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_SOUTH_EAST_ANIMATION_3 = 2237;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_SOUTH_EAST_ANIMATION_4 = 2238;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_NORTH_WEST_ANIMATION_0 = 2232;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_NORTH_WEST_ANIMATION_1 = 2235;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_NORTH_WEST_ANIMATION_2 = 2236;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_NORTH_WEST_ANIMATION_3 = 2239;
    public static final int METAL_WORKER_CRUCIBLE_CARGO_NORTH_WEST_ANIMATION_4 = 2240;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_SOUTH_EAST_ANIMATION_0 = 2241;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_SOUTH_EAST_ANIMATION_1 = 2243;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_SOUTH_EAST_ANIMATION_2 = 2044;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_SOUTH_EAST_ANIMATION_3 = 2247;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_SOUTH_EAST_ANIMATION_4 = 2248;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_SOUTH_EAST_ANIMATION_5 = 2250;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_NORTH_WEST_ANIMATION_0 = 2242;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_NORTH_WEST_ANIMATION_1 = 2245;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_NORTH_WEST_ANIMATION_2 = 2246;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_NORTH_WEST_ANIMATION_3 = 2249;
    public static final int METAL_WORKER_FISHING_ROD_CARGO_NORTH_WEST_ANIMATION_4 = 2251;
    public static final int METAL_WORKER_SCYTHE_CARGO_SOUTH_EAST_ANIMATION_0 = 2252;
    public static final int METAL_WORKER_SCYTHE_CARGO_SOUTH_EAST_ANIMATION_1 = 2254;
    public static final int METAL_WORKER_SCYTHE_CARGO_SOUTH_EAST_ANIMATION_2 = 2255;
    public static final int METAL_WORKER_SCYTHE_CARGO_SOUTH_EAST_ANIMATION_3 = 2258;
    public static final int METAL_WORKER_SCYTHE_CARGO_SOUTH_EAST_ANIMATION_4 = 2259;
    public static final int METAL_WORKER_SCYTHE_CARGO_SOUTH_EAST_ANIMATION_5 = 2261;
    public static final int METAL_WORKER_SCYTHE_CARGO_NORTH_WEST_ANIMATION_0 = 2253;
    public static final int METAL_WORKER_SCYTHE_CARGO_NORTH_WEST_ANIMATION_1 = 2256;
    public static final int METAL_WORKER_SCYTHE_CARGO_NORTH_WEST_ANIMATION_2 = 2257;
    public static final int METAL_WORKER_SCYTHE_CARGO_NORTH_WEST_ANIMATION_3 = 2260;
    public static final int METAL_WORKER_SCYTHE_CARGO_NORTH_WEST_ANIMATION_4 = 2262;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_0 = 2176;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_1 = 2178;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_2 = 2179;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_3 = 2181;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_4 = 2182;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_5 = 2183;
    public static final int METAL_WORKER_TONGS_CARGO_SOUTH_EAST_ANIMATION_6 = 2184;
    public static final int METAL_WORKER_TONGS_CARGO_NORTH_WEST_ANIMATION_0 = 2177;
    public static final int METAL_WORKER_TONGS_CARGO_NORTH_WEST_ANIMATION_1 = 2180;
    public static final int METAL_WORKER_TONGS_CARGO_NORTH_WEST_ANIMATION_2 = 2184;
    public static final int METAL_WORKER_TONGS_CARGO_NORTH_WEST_ANIMATION_3 = 2186;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_SOUTH_EAST_ANIMATION_0 = 2284;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_SOUTH_EAST_ANIMATION_1 = 2286;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_SOUTH_EAST_ANIMATION_2 = 2287;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_SOUTH_EAST_ANIMATION_3 = 2290;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_SOUTH_EAST_ANIMATION_4 = 2291;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_NORTH_WEST_ANIMATION_0 = 2285;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_NORTH_WEST_ANIMATION_1 = 2288;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_NORTH_WEST_ANIMATION_2 = 2289;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_NORTH_WEST_ANIMATION_3 = 2292;
    public static final int METAL_WORKER_ROLLING_PIN_CARGO_NORTH_WEST_ANIMATION_4 = 2293;
    public static final int METAL_WORKER_CLEAVER_CARGO_SOUTH_EAST_ANIMATION_0 = 2274;
    public static final int METAL_WORKER_CLEAVER_CARGO_SOUTH_EAST_ANIMATION_1 = 2276;
    public static final int METAL_WORKER_CLEAVER_CARGO_SOUTH_EAST_ANIMATION_2 = 2277;
    public static final int METAL_WORKER_CLEAVER_CARGO_SOUTH_EAST_ANIMATION_3 = 2280;
    public static final int METAL_WORKER_CLEAVER_CARGO_SOUTH_EAST_ANIMATION_4 = 2281;
    public static final int METAL_WORKER_CLEAVER_CARGO_NORTH_WEST_ANIMATION_0 = 2275;
    public static final int METAL_WORKER_CLEAVER_CARGO_NORTH_WEST_ANIMATION_1 = 2278;
    public static final int METAL_WORKER_CLEAVER_CARGO_NORTH_WEST_ANIMATION_2 = 2279;
    public static final int METAL_WORKER_CLEAVER_CARGO_NORTH_WEST_ANIMATION_3 = 2282;
    public static final int METAL_WORKER_CLEAVER_CARGO_NORTH_WEST_ANIMATION_4 = 2283;
    public static final int METAL_WORKER_SAW_CARGO_SOUTH_EAST = 2330;
    public static final int METAL_WORKER_SAW_CARGO_NORTH_WEST_ANIMATION_0 = 2331;
    public static final int METAL_WORKER_SAW_CARGO_NORTH_WEST_ANIMATION_1 = 2332;
    public static final int METAL_WORKER_SAW_CARGO_NORTH_WEST_ANIMATION_2 = 2333;
    public static final int METAL_WORKER_SAW_CARGO_NORTH_WEST_ANIMATION_3 = 2334;
    public static final int METAL_WORKER_SAW_CARGO_NORTH_WEST_ANIMATION_4 = 2335;
}
