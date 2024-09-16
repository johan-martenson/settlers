package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.LBMGameResource;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.resources.AnimatedLBMFile;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.AnimatedPalette;
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

    /**
     * Loads an LBM file from the given filename and palette.
     *
     * @param filename       the name of the LBM file
     * @param defaultPalette the default palette to use
     * @return a GameResource representing the LBM file
     * @throws IOException            if an I/O error occurs
     * @throws InvalidFormatException if the file format is invalid
     */
    public static GameResource loadLBMFile(String filename, Palette defaultPalette) throws IOException, InvalidFormatException {
        try (StreamReader streamReader = new StreamReader(new FileInputStream(filename), ByteOrder.BIG_ENDIAN)) {
            String header = streamReader.getUint8ArrayAsString(4);
            long length = streamReader.getUint32();
            String pbm = streamReader.getUint8ArrayAsString(4);

            // Check that the header is valid
            if (!header.equals("FORM") || !pbm.equals("PBM ")) {
                throw new InvalidFormatException(String.format("Must match 'FORM' or 'PBM'. Not %s", header));
            }

            int width = 0;
            int height = 0;
            int transClr = 0;
            short compression = 0;
            short mask = 0;
            boolean headerRead = false;

            AnimatedLBMFile lbmFile = null;
            Palette palette = null;

            // Read sections of the file until done
            List<AnimatedPalette> animatedPaletteList = new ArrayList<>();

            while (!streamReader.isEof()) {
                String chunkId = streamReader.getUint8ArrayAsString(4);
                long chunkLength = streamReader.getUint32();

                if ((chunkLength & 1) == 1) {
                    chunkLength++;
                }

                switch (chunkId) {
                    case "BMHD" -> {

                        if (headerRead) {
                            throw new InvalidFormatException("Should only read the header once.");
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
                            throw new InvalidFormatException(String.format("Invalid planes and mask combination. Planes: %d, Mask: %d", numPlanes, mask));
                        }

                        if (compression > 1) {
                            throw new InvalidFormatException(String.format("Invalid compression: %d", compression));
                        }

                        lbmFile = new AnimatedLBMFile(width, height, defaultPalette, TextureFormat.BGRA);
                        headerRead = true;
                    }
                    case "CRNG" -> animatedPaletteList.add(AnimatedPalette.load(streamReader));
                    case "CMAP" -> {
                        if (chunkLength != 256 * 3) {
                            throw new InvalidFormatException(String.format("Invalid chunk length: %d", chunkLength));
                        }

                        palette = Palette.loadPalette(streamReader, false);
                        palette.setTransparentIndex(mask == 2 && transClr < 256 ? transClr : 0);
                    }
                    case "BODY" -> {
                        if (!headerRead) {
                            throw new InvalidFormatException("Header is not read.");
                        }

                        if (compression == 0) {
                            if (chunkLength != (long) width * height) {
                                throw new InvalidFormatException(String.format("Invalid length: %d", chunkLength));
                            }

                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    lbmFile.setPixelByColorIndex(x, y, streamReader.getUint8());
                                }
                            }
                        } else {
                            int x = 0;
                            int y = 0;

                            while (chunkLength > 0) {
                                byte compressionType = streamReader.getInt8();
                                chunkLength--;

                                if (chunkLength == 0) {
                                    continue;
                                }

                                if (compressionType > 0) {
                                    int count = 1 + compressionType;

                                    for (short j = 0; j < count; j++) {
                                        short color = streamReader.getUint8();
                                        chunkLength--;

                                        lbmFile.setPixelByColorIndex(x++, y, color);

                                        if (x >= width) {
                                            y = y + 1;
                                            x = 0;
                                        }
                                    }
                                } else {
                                    int count = 1 - compressionType;
                                    short color = streamReader.getUint8();
                                    chunkLength--;

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

            lbmFile.setPaletteAnimations(animatedPaletteList);
            lbmFile.setLength(length);

            if (palette != null) {
                lbmFile.setPalette(palette);
            }

            return new LBMGameResource(lbmFile);
        }
    }
}
