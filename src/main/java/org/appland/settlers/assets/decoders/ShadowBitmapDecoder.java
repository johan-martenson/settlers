package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.ByteReader;

import java.io.IOException;

import static java.lang.String.format;

public class ShadowBitmapDecoder {

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static Bitmap loadBitmapShadowFromStream(ByteReader streamReader, Palette palette) throws IOException {

        /* Read header */
        short nx = streamReader.getInt16();
        short ny = streamReader.getInt16();
        long unknown1 = streamReader.getUint32();
        int width = streamReader.getUint16();
        int height = streamReader.getUint16();
        int unknown2 = streamReader.getUint16();
        long length = streamReader.getUint32();

        if (unknown1 != 0 || unknown2 != 1) {
            throw new RuntimeException(format("Invalid format. Unknown 1 must be 0, was: %d. Unknown 2 must be 1, was: %d.", unknown1, unknown2));
        }

        byte[] data = streamReader.getUint8ArrayAsBytes((int)length);

        short grayIndex = palette.getIndexForColor(255, 255, 255);

        debugPrint(" - Width: " + width);
        debugPrint(" - Height: " + height);
        debugPrint(" - Length: " + length);

        if (length == 0) {
            throw new RuntimeException("Not implemented support for empty images");
        }

        long position = height * 2L;
        Bitmap bitmap = new Bitmap(width, height, palette, TextureFormat.BGRA);

        bitmap.setNx(nx);
        bitmap.setNy(ny);

        for (int y = 0; y < height; y++) {
            int x = 0;

            while (x < width && position + 2 < data.length) {
                int count = data[(int)position++];

                for (int i = 0; i < count; i++, x++) {
                    bitmap.setPixelByColorIndex(x, y, grayIndex);
                }

                count = data[(int)position++];

                x = x + count;
            }

            if (position >= data.length) {
                throw new RuntimeException("Exceeded data size");
            }

            position = position + 1;
        }

        return bitmap;
    }
}
