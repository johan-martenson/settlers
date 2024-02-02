package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.BitmapRLEResource;
import org.appland.settlers.assets.BitmapResource;
import org.appland.settlers.assets.BobResource;
import org.appland.settlers.assets.FontResource;
import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.PaletteResource;
import org.appland.settlers.assets.PlayerBitmapResource;
import org.appland.settlers.assets.ResourceType;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.WaveGameResource;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.utils.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class DatDecoder {

    private static boolean debug = true;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    public static List<GameResource> loadDatFile(String filename, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {

        System.out.println("Loading DAT file");

        String baseFile = filename.substring(0, filename.length() - 4);

        debugPrint(baseFile);

        String datFilename = baseFile + ".DAT";
        String idxFilename = baseFile + ".IDX";

        // Load only from the DAT file if the IDX file is missing
        if (!Files.exists(Paths.get(idxFilename))) {
            StreamReader datReader = new StreamReader(new FileInputStream(datFilename), LITTLE_ENDIAN);
            short datBobtype = datReader.getInt16();

            // Load as wave data without header
            if (datBobtype == 20294) {

                // Chunks x n
                // Chunk (integers are big endian)
                //  - Type ID
                //  - i32 data size
                //  - data

                // TODO: add decoding of song (?), e.g. "SNG_0010.DAT"
                return new ArrayList<>();
            } else if (datBobtype == 21111) {
                // TODO: add decoding of animation, e.g. "ROM_ANIM.DAT"
                return new ArrayList<>();
            } else if (datBobtype == -28416) {
                // TODO: add decoding of texture, e.g. "GOURAUD0.DAT"
                return new ArrayList<>();
            } else if (datBobtype == -6144) {
                // TODO: add decoding of texture, e.g. "GOU5.DAT"
                return new ArrayList<>();
            } else if (datBobtype == -5888) {
                // TODO: add decoding of texture, e.g. "GOU6.DAT"
                return new ArrayList<>();
            }

            ResourceType resourceType = ResourceType.fromInt(datBobtype);

            GameResource gameResource = loadType(datReader, resourceType, defaultPalette);

            List<GameResource> result = new ArrayList<>();

            result.add(gameResource);

            return result;
        }

        // Also read the IDX file if it exists
        StreamReader datReader = new StreamReader(new FileInputStream(datFilename), LITTLE_ENDIAN);
        StreamReader idxReader = new StreamReader(new FileInputStream(idxFilename), LITTLE_ENDIAN);

        long count = idxReader.getUint32();

        List<GameResource> gameResourceList = new ArrayList<>();

        for (long i = 0; i < count; i++) {
            String name = idxReader.getUint8ArrayAsString(16);
            long offset = idxReader.getUint32();
            String unknown = idxReader.getUint8ArrayAsString(6);
            int idxBobType = idxReader.getInt16();

            datReader.setPosition(offset);

            short datBobtype = datReader.getInt16();

            if (idxBobType != datBobtype) {

                debugPrint("Seems like an invalid item??");
                debugPrint(idxFilename);
                debugPrint(datFilename);
                debugPrint("" + i);

                continue;
            }

            ResourceType resourceType = ResourceType.fromInt(datBobtype);

            var gameResource = loadType(datReader, resourceType, defaultPalette);

            gameResource.setName(name);

            gameResourceList.add(gameResource);
        }

        return gameResourceList;
    }

    public static GameResource loadType(StreamReader streamReader, ResourceType resourceType, Palette palette) throws IOException, InvalidFormatException, UnknownResourceTypeException {

        return switch (resourceType) {
            case BITMAP_PLAYER -> new PlayerBitmapResource(PlayerBitmapDecoder.loadPlayerBitmapFromStream(streamReader, palette));
            case FONT -> new FontResource(FontDecoder.loadFontFromStream(streamReader, palette));
            case BITMAP -> new BitmapResource(RawBitmapDecoder.loadRawBitmapFromStream(streamReader, palette, Optional.empty()));
            case BITMAP_RLE -> new BitmapRLEResource(BitmapRleDecoder.loadBitmapRLEFromStream(streamReader, palette));
            case SOUND -> new WaveGameResource(WaveDecoder.loadWaveSoundFromStream(streamReader, 5, false));
            case PALETTE -> new PaletteResource(PaletteDecoder.loadPaletteFromStream(streamReader, true));
            case BOB -> new BobResource(BobDecoder.loadBobFromStream(streamReader, palette));
            default -> throw new RuntimeException("Not implemented yet. " + resourceType);
        };
    }
}
