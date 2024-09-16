package org.appland.settlers.assets.decoders;

import org.appland.settlers.assets.BitmapRLEResource;
import org.appland.settlers.assets.BitmapRawResource;
import org.appland.settlers.assets.BitmapResource;
import org.appland.settlers.assets.BobResource;
import org.appland.settlers.assets.FontResource;
import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.GameResourceType;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.PaletteResource;
import org.appland.settlers.assets.PlayerBitmapResource;
import org.appland.settlers.assets.ResourceType;
import org.appland.settlers.assets.TextResource;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.SoundLoader;
import org.appland.settlers.utils.ByteArrayReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class LstDecoder {

    private static boolean debug = false;

    private static void debugPrint(String debugString) {
        if (debug) {
            System.out.println(debugString);
        }
    }

    /**
     * Loads the LST file and decodes the resources within it.
     *
     * @param filename       the LST filename
     * @param defaultPalette the default palette used for images
     * @return a list of decoded game resources
     * @throws IOException                  if an I/O error occurs
     * @throws UnknownResourceTypeException if the resource type is unknown
     * @throws InvalidFormatException       if the file format is invalid
     */
    public static List<GameResource> loadLstFile(String filename, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {
        List<GameResource> gameResources = new ArrayList<>();

        ByteArrayReader streamReader = new ByteArrayReader(Files.newInputStream(Paths.get(filename)).readAllBytes(), LITTLE_ENDIAN);

        Palette palette = defaultPalette;
        int header = streamReader.getUint16();

        debugPrint(format("Header is: %s", Integer.toHexString(header)));

        switch (header) {
            case 0xFDE7 -> // Load text file
                    gameResources.add(new TextResource(TextDecoder.loadTextFromStream(streamReader)));
            case 0x01F6 -> // Load BOB file
                    gameResources.add(new BobResource(BobDecoder.loadBobFromStream(streamReader, palette)));
            case 0x4E20 -> { // Load LST BOB file
                debugPrint(" - Header is valid for LST bob file");

                long numberItems = streamReader.getUint32();

                debugPrint(" - Contains number of items: " + numberItems);

                // Loop through and read each item
                int hits = 0;

                for (long i = 0; i < numberItems; i++) {
                    short used = streamReader.getInt16();

                    // Filter unused items
                    if (used != 1) {
                        debugPrint(" - Filter un-used item");

                        continue;
                    }

                    debugPrint(format("HIT: %d - %d", i, hits));
                    hits = hits + 1;

                    // Load the resource type
                    int type = streamReader.getInt16();
                    ResourceType resourceType = ResourceType.fromInt(type);

                    debugPrint(" - Resource type number: " + type);
                    debugPrint(" - Resource type: " + resourceType);

                    // Load the resource based on the type
                    var gameResource = switch (resourceType) {
                        case SOUND -> SoundLoader.loadSoundFromStream(streamReader);
                        case BITMAP_RLE -> new BitmapRLEResource(BitmapRleDecoder.loadBitmapRLEFromStream(streamReader, palette));
                        case FONT -> new FontResource(FontDecoder.loadFontFromStream(streamReader, defaultPalette));
                        case BITMAP_PLAYER -> new PlayerBitmapResource(PlayerBitmapDecoder.loadPlayerBitmapFromStream(streamReader, palette));
                        case PALETTE -> new PaletteResource(PaletteDecoder.loadPaletteFromStream(streamReader, true));
                        case BOB -> new BobResource(BobDecoder.loadBobFromStream(streamReader, palette));
                        case BITMAP_SHADOW -> new BitmapResource(ShadowBitmapDecoder.loadBitmapShadowFromStream(streamReader, palette));
                        case MAP -> throw new RuntimeException("TODO: use MapManager to load the map");
                        case BITMAP, RAW -> new BitmapRawResource(RawBitmapDecoder.loadRawBitmapFromStream(streamReader, palette, Optional.empty()));
                        case PALETTE_ANIM -> throw new RuntimeException("Todo: implement loading of animated palettes");
                        default -> throw new UnknownResourceTypeException("Can't handle resource type " + resourceType);
                    };

                    if (gameResource.getType() == GameResourceType.PALETTE_RESOURCE) {
                        palette = ((PaletteResource) gameResource).getPalette();
                    }

                    gameResources.add(gameResource);
                }
            }
            default -> throw new RuntimeException("Can't handle unknown header type: " + Integer.toHexString(header));
        }

        return gameResources;
    }
}
