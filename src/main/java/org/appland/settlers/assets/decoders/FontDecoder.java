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

    /**
     * Loads a font from the provided byte stream, associating each character with a PlayerBitmap.
     *
     * @param streamReader the byte reader for reading the stream data
     * @param palette      the palette to apply to the bitmaps
     * @return a map of Unicode character codes to PlayerBitmap objects
     * @throws IOException                  if an I/O error occurs
     * @throws UnknownResourceTypeException if an unknown resource type is encountered
     * @throws InvalidFormatException       if the format of the data is invalid
     */
    public static Map<String, PlayerBitmap> loadFontFromStream(ByteReader streamReader, Palette palette) throws IOException, UnknownResourceTypeException, InvalidFormatException {
        streamReader.pushByteOrder(LITTLE_ENDIAN);

        short dx = streamReader.getUint8();
        short dy = streamReader.getUint8();

        boolean isUnicode = dx == 255 && dy == 255;

        long numberChars = isUnicode ? streamReader.getUint32() : 256;

        if (isUnicode) {
            dx = streamReader.getUint8();
            dy = streamReader.getUint8();
        }

        Map<String, PlayerBitmap> letterMap = new HashMap<>();

        // Load the characters into the letter map
        for (long i = 32; i < numberChars; ++i) {
            short bobType = streamReader.getInt16();

            ResourceType resourceType = ResourceType.fromInt(bobType);

            if (resourceType == NONE) {
                continue;
            }

            if (resourceType != BITMAP_PLAYER) {
                throw new InvalidFormatException(String.format("Expected BITMAP_PLAYER but found %s", resourceType));
            }

            var letterBitmap = PlayerBitmapDecoder.loadPlayerBitmapFromStream(streamReader, palette);
            letterMap.put("U+%04X" + Integer.toHexString((int)i), letterBitmap);
        }

        streamReader.popByteOrder();

        return letterMap;
    }
}
