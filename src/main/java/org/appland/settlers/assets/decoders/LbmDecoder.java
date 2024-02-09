package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.LBMGameResource;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.LBMFile;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PaletteAnim;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class LbmDecoder {
    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static GameResource loadLBMFile(String filename, Palette defaultPalette) throws IOException, InvalidFormatException {
        StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.BIG_ENDIAN);

        String header = streamReader.getUint8ArrayAsString(4);
        long length = streamReader.getUint32();
        String pbm = streamReader.getUint8ArrayAsString(4);

        /* Check that the header is valid */
        if (!header.equals("FORM") || !pbm.equals("PBM ")) {
            throw new InvalidFormatException("Must match 'FORM' or 'PBM'. Not " + header);
        }

        int width = 0;
        int height = 0;
        int transClr = 0;
        short compression = 0;
        short mask = 0;
        boolean headerRead = false;

        LBMFile lbmFile = null;
        Palette palette = null;

        /* Read sections of the file until it's done */
        List<PaletteAnim> paletteAnimList = new ArrayList<>();

        while (!streamReader.isEof()) {

            /* Read next chunk */
            String chunkId = streamReader.getUint8ArrayAsString(4);
            long chunkLength = streamReader.getUint32();

            if ((chunkLength & 1) == 1) {
                chunkLength = chunkLength + 1;
            }

            /* Read bitmap header */
            switch (chunkId) {
                case "BMHD" -> {

                    if (headerRead) { // || !bitmap
                        throw new InvalidFormatException("Must read header");
                    }

                    width = streamReader.getUint16();
                    height = streamReader.getUint16();
                    int xOrig = streamReader.getUint16();
                    int yOrig = streamReader.getUint16();
                    short numPlanes = streamReader.getUint8();
                    mask = streamReader.getUint8();
                    compression = streamReader.getUint8();
                    short pad = streamReader.getUint8();
                    transClr = streamReader.getUint16();
                    short xAspect = streamReader.getUint8();
                    short yAspect = streamReader.getUint8();
                    int pageW = streamReader.getUint16();
                    int pageH = streamReader.getUint16();

                    if (numPlanes != 8 || (mask != 0 && mask != 2)) {
                        throw new InvalidFormatException("Planes and maks combination is not valid. Number planes: " + numPlanes + ", mask: " + mask);
                    }

                    if (compression > 1) {
                        throw new InvalidFormatException("Compression must not be greater than 1. Is " + compression);
                    }

                    lbmFile = new LBMFile(width, height, defaultPalette, TextureFormat.BGRA);

                    headerRead = true;
                }
                case "CRNG" -> {
                    PaletteAnim paletteAnim = PaletteAnim.load(streamReader);

                    paletteAnimList.add(paletteAnim);
                }
                case "CMAP" -> {

                    if (chunkLength != 256 * 3) {
                        throw new InvalidFormatException("Chunk length must be 256 * 3. Not " + chunkLength);
                    }

                    palette = Palette.load(streamReader, false);

                    if (mask == 2 && transClr < 256) {
                        palette.setTransparentIndex(transClr);
                    } else {
                        palette.setTransparentIndex(0);
                    }
                }
                case "BODY" -> {

                    if (!headerRead) {
                        throw new InvalidFormatException("Header is not read.");
                    }

                    // if bitmap == null, invalid format palette missing

                    if (compression == 0) {
                        if (chunkLength != (long) width * height) {
                            throw new InvalidFormatException("Length must match width x height (" + width + "x" + height + ". Not " + chunkLength);
                        }

                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                short color = streamReader.getUint8();

                                lbmFile.setPixelByColorIndex(x, y, color);
                            }
                        }
                    } else {
                        int x = 0;
                        int y = 0;

                        while (chunkLength > 0) {
                            byte compressionType = streamReader.getInt8();

                            chunkLength = chunkLength - 1;

                            if (chunkLength == 0) {
                                continue;
                            }

                            if (compressionType > 0) {
                                int count = 1 + compressionType;

                                for (short j = 0; j < count; j++) {
                                    short color = streamReader.getUint8();

                                    chunkLength = chunkLength - 1;

                                    lbmFile.setPixelByColorIndex(x++, y, color);

                                    if (x >= width) {
                                        y = y + 1;
                                        x = 0;
                                    }
                                }
                            } else {
                                int count = 1 - compressionType;

                                short color = streamReader.getUint8();

                                chunkLength = chunkLength - 1;

                                for (int j = 0; j < count; j++) {
                                    lbmFile.setPixelByColorIndex(x++, y, color);

                                    if (x >= width) {
                                        y = y + 1;
                                        x = 0;
                                    }
                                }
                            }
                        }
                    }
                }
                default -> streamReader.skip((int) chunkLength);
            }
        }

        lbmFile.setAnimPalettes(paletteAnimList);
        lbmFile.setLength(length);

        if (palette != null) {
            lbmFile.setPalette(palette);
        }

        return new LBMGameResource(lbmFile);
    }
}
