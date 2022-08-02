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
        switch (type) {
            case 0:
                return NONE;
            case 1:
                return SOUND;
            case 2:
                return BITMAP_RLE;
            case 3:
                return FONT;
            case 4:
                return BITMAP_PLAYER;
            case 5:
                return PALETTE;
            case 6:
                return BOB;
            case 7:
                return BITMAP_SHADOW;
            case 8:
                return MAP;
            case 9:
                return TEXT;
            case 10:
                return RAW;
            case 11:
                return MAP_HEADER;
            case 12:
                return INI;
            case 13:
                return UNSET;
            case 14:
                return BITMAP;
            case 15:
                return PALETTE_ANIM;
            default:
                throw new UnknownResourceTypeException("Unknown resource type: " + type);
        }
    }
}
