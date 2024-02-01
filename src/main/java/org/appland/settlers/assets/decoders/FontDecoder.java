package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.ResourceType;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.appland.settlers.assets.ResourceType.BITMAP_PLAYER;
import static org.appland.settlers.assets.ResourceType.NONE;

public class FontDecoder {

    public static Map<String, PlayerBitmap> loadFontFromStream(ByteReader streamReader, Palette palette) throws IOException, UnknownResourceTypeException, InvalidFormatException {
        streamReader.pushByteOrder(LITTLE_ENDIAN);

        short dx = streamReader.getUint8();
        short dy = streamReader.getUint8();

        boolean isUnicode = dx == 255 && dy == 255;

        long numberChars;

        if (isUnicode) {
            numberChars = streamReader.getUint32();
            dx = streamReader.getUint8();
            dy = streamReader.getUint8();
        } else {
            numberChars = 256;
        }

        /* Read the letters */
        Map<String, PlayerBitmap> letterMap = new HashMap<>();
        for (long i = 32; i < numberChars; ++i) {
            short bobType = streamReader.getInt16();

            ResourceType resourceType1 = ResourceType.fromInt(bobType);

            if (resourceType1 == NONE) {
                continue;
            }

            if (resourceType1 != BITMAP_PLAYER) {
                throw new InvalidFormatException("Can only read player bitmap for fonts. Not " + resourceType1);
            }

            var letterBitmap = PlayerBitmapDecoder.loadPlayerBitmapFromStream(streamReader, palette);

            letterMap.put("U+" + Integer.toHexString((int)i), letterBitmap);
        }

        streamReader.popByteOrder();

        return letterMap;
    }
}
