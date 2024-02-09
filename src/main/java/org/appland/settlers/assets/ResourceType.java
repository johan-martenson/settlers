package org.appland.settlers.assets;

public enum ResourceType {

    SOUND,
    BITMAP_RLE,
    FONT,
    BITMAP_PLAYER,
    PALETTE,
    BOB,
    BITMAP_SHADOW,
    MAP,
    TEXT,
    RAW,
    MAP_HEADER,
    PALETTE_ANIM,
    NONE,
    BITMAP,
    UNSET,
    INI;

    public static ResourceType fromInt(int type) throws UnknownResourceTypeException {
        return switch (type) {
            case 0 -> NONE;
            case 1 -> SOUND;
            case 2 -> BITMAP_RLE;
            case 3 -> FONT;
            case 4 -> BITMAP_PLAYER;
            case 5 -> PALETTE;
            case 6 -> BOB;
            case 7 -> BITMAP_SHADOW;
            case 8 -> MAP;
            case 9 -> TEXT;
            case 10 -> RAW;
            case 11 -> MAP_HEADER;
            case 12 -> INI;
            case 13 -> UNSET;
            case 14 -> BITMAP;
            case 15 -> PALETTE_ANIM;
            default -> throw new UnknownResourceTypeException("Unknown resource type: " + type);
        };
    }
}
