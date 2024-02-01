package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.BitmapRLE;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;

public class BitmapRleDecoder {

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static BitmapRLE loadBitmapRLEFromStream(ByteReader streamReader, Palette palette) throws IOException, InvalidFormatException {

        /* Read header */
        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int unknown2 = streamReader.getUint16();
        long length = streamReader.getUint32();

        debugPrint(" - nx: " + nx);
        debugPrint(" - ny: " + ny);
        debugPrint(" - Unknown 1: " + unknown1);
        debugPrint(" - Width: " + width);
        debugPrint(" - Height: " + height);
        debugPrint(" - Unknown 2: " + unknown2);
        debugPrint(" - Length: " + length);

        // Load the image data
        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        BitmapRLE bitmap = new BitmapRLE(width, height, data, palette, length, TextureFormat.BGRA);

        bitmap.setNx(nx);
        bitmap.setNy(ny);

        return bitmap;
    }
}
