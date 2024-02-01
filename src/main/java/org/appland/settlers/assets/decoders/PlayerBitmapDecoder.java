package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;

public class PlayerBitmapDecoder {

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static PlayerBitmap loadPlayerBitmapFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        /* Read header */
        int nx = streamReader.getInt16();
        int ny = streamReader.getInt16();

        long unknown1 = streamReader.getUint32();

        int width = streamReader.getUint16();
        int height = streamReader.getUint16();

        int unknown2 = streamReader.getUint16();

        long length = streamReader.getUint32();

        /* Verify that length is not too short */
        if (length <= (height * 2L)) {
            throw new InvalidFormatException("Length (" + length + ") must be larger than height (" + height + ") * 2");
        }

        debugPrint("    - Width: " + width);
        debugPrint("    - Height: " + height);
        debugPrint("    - Length: " + length);
        debugPrint("    - Height * 2: " + height * 2);
        debugPrint("    - Length - height * 2: " + (int) (length - height * 2));

        int[] starts = streamReader.getUint16ArrayAsInts(height);
        byte[] imageData = streamReader.getUint8ArrayAsBytes((int)(length - height * 2));

        debugPrint("    - Number starts: " + starts.length);
        debugPrint("    - Size of image data: " + imageData.length);
        debugPrint("    - Image dimensions are: " + width + "x" + height);
        debugPrint("    - Multiplied: " + width * height);

        PlayerBitmap playerBitmap = new PlayerBitmap(width, height, palette, TextureFormat.BGRA);

        playerBitmap.setNx(nx);
        playerBitmap.setNy(ny);
        playerBitmap.setLength(length);

        debugPrint(" Loading from image data");

        playerBitmap.loadImageFromData(imageData, starts, false);

        debugPrint(" Loaded from image data");

        return playerBitmap;
    }
}
