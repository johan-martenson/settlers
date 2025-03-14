package org.appland.settlers.assets;

import org.appland.settlers.assets.decoders.BbmDecoder;
import org.appland.settlers.assets.decoders.DatDecoder;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.decoders.PaletteDecoder;
import org.appland.settlers.assets.decoders.TextDecoder;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.BitmapRLE;
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.resources.WaveFile;
import org.appland.settlers.model.PlayerColor;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Reader class for handling loading of game assets and writing them to files.
 */
public class Reader {
    private static final String DEFAULT_PALETTE = "/Users/s0001386/projects/settlers/src/main/resources/default-palette.act";
    private static final int NUMBER_LINKS_PER_OVERLAY = 8 * 2 * 6;

    @Option(name = "--dir", usage = "Asset directory to load")
    static String assetDir = "";

    @Option(name = "--file", usage = "Asset file to load")
    static String assetFilename;

    @Option(name = "--to-dir", usage = "Folder to write to")
    static String dirToWrite;

    @Option(name = "--info", usage = "Print information about the assets")
    private static boolean printInfoSelected = false;

    @Option(name = "--type", usage = "File type to load within the given directory")
    private static String type;

    private Palette palette;

    public static void main(String[] args) throws CmdLineException, IOException, InvalidFormatException {
        Reader reader = new Reader();
        CmdLineParser parser = new CmdLineParser(reader);

        parser.parseArgument(args);

        // Load the default palette
        reader.loadDefaultPalette();

        Map<String, List<GameResource>> gameResourceMap = new HashMap<>();

        // Load file if specified
        if (!Objects.equals(assetFilename, "")) {
            gameResourceMap.put(assetFilename, reader.loadFile(assetFilename));

            System.out.println("Read file " + assetFilename);
            System.out.println("Loaded " + gameResourceMap.size() + " resources");
        }

        // Load directory if specified
        if (!Objects.equals(assetDir, "")) {
            gameResourceMap.putAll(reader.loadDirectory(assetDir, type));

            System.out.println("Read directory: " + assetDir);
            System.out.println(gameResourceMap);
        }

        // Print information if requested
        if (printInfoSelected) {
            printInformation(gameResourceMap);
        }

        // Write resources to file if specified
        if (!Objects.equals(dirToWrite, "")) {
            writeToDirectory(gameResourceMap, dirToWrite);

            System.out.println("Wrote: " + dirToWrite);
        }
    }

    /**
     * Loads all files from a directory based on the specified type.
     *
     * @param assetDir the directory to load from
     * @param type the type of files to load
     * @return a map of filenames and their corresponding resources
     * @throws IOException if there's an error during file operations
     * @throws InvalidFormatException if the file format is invalid
     */
    private Map<String, List<GameResource>> loadDirectory(String assetDir, String type) throws IOException, InvalidFormatException {
        Map<String, List<GameResource>> gameResourceMap = new HashMap<>();

        /* List all files */
        List<Path> paths = Files.find(
                Paths.get(assetDir),
                Integer.MAX_VALUE,
                (path, basicFileAttributes) -> path.toFile().getName().matches(".*." + type)
        ).toList();

        for (Path path : paths) {
            if (!Files.isDirectory(path)) {
                String filename = path.toString();
                gameResourceMap.put(filename, loadFile(filename));
            }
        }

        return gameResourceMap;
    }

    /**
     * Prints detailed information about the loaded game resources.
     *
     * @param gameResourceMap the map containing filenames and resources
     */
    private static void printInformation(Map<String, List<GameResource>> gameResourceMap) {
        System.out.println();

        gameResourceMap.forEach((inputFile, gameResourceList) -> {
            System.out.println();
            System.out.printf(" - File: %s", inputFile);

            for (GameResource gameResource : gameResourceList) {
                switch (gameResource.getType()) {
                    case PLAYER_BITMAP_RESOURCE -> {
                        PlayerBitmap playerBitmap = ((PlayerBitmapResource) gameResource).getBitmap();
                        System.out.printf("""
                             + Player bitmap
                                - Width: %d
                                - Height: %d
                                - NX: %d
                                - NY: %d
                                - Length: %d
                             """, playerBitmap.getWidth(), playerBitmap.getHeight(), playerBitmap.getNx(), playerBitmap.getNy(), playerBitmap.getLength());
                    }
                    case PALETTE_RESOURCE -> {
                        Palette palette = ((PaletteResource) gameResource).getPalette();
                        RGBColor rgbColor = palette.getColorForIndex(palette.getTransparentIndex());
                        System.out.printf("""
                             + Palette
                                - Number colors: %d
                                - Transparency index: %d
                                - Transparency color: %d (red), %d (green), %d (blue)
                             """, palette.getNumberColors(), palette.getTransparentIndex(), rgbColor.red(), rgbColor.green(), rgbColor.blue());
                    }
                    case BITMAP_RLE -> {
                        BitmapRLE bitmapRLE = ((BitmapRLEResource) gameResource).getBitmap();
                        System.out.printf("""
                             + RLE bitmap
                                - Width: %d
                                - Height: %d
                             """, bitmapRLE.getWidth(), bitmapRLE.getHeight());
                    }
                    case BITMAP_RESOURCE -> {
                        Bitmap bitmap = ((BitmapResource) gameResource).getBitmap();
                        System.out.printf("""
                             + Bitmap
                                - Width: %d
                                - Height: %d
                                - Bits per pixel: %d
                                - Format: %s
                             """, bitmap.getWidth(), bitmap.getHeight(), bitmap.getBytesPerPixel(), bitmap.getFormat());
                    }
                    case WAVE_SOUND -> {
                        WaveFile waveFile = ((WaveGameResource) gameResource).getWaveFile();
                        System.out.printf("""
                             + Wave
                                - Format id: %s
                                - Format size: %d
                                - Format tag: %d
                                - Number channels: %d
                                - Samples per second: %d
                                - Bytes per second: %d
                                - Frame size: %d
                                - Bits per sample: %d
                                - Data id: %s
                                - Data size: %d
                             """, waveFile.getFormatId(), waveFile.getFormatSize(), waveFile.getFormatTag(), waveFile.getNumberChannels(),
                                waveFile.getSamplesPerSecond(), waveFile.getBytesPerSecond(), waveFile.getFrameSize(), waveFile.getBitsPerSample(),
                                waveFile.getDataId(), waveFile.getDataSize());
                    }
                    case BITMAP_RAW -> {
                        Bitmap bitmapRaw = ((BitmapRawResource) gameResource).getBitmap();
                        System.out.printf("""
                             + Raw bitmap
                                - Width: %d
                                - Height: %d
                                - Format: %s
                                - Bits per pixel: %d
                             """, bitmapRaw.getWidth(), bitmapRaw.getHeight(), bitmapRaw.getFormat(), bitmapRaw.getBytesPerPixel());
                    }
                    case BOB_RESOURCE -> {
                        Bob bob = ((BobResource) gameResource).getBob();
                        System.out.printf("""
                             + Bob
                                - Number of body images: %d
                                - Number of overlay images: %d
                             """, bob.getNumberBodyImages(), bob.getNumberOverlayImages());
                    }
                    case FONT_RESOURCE -> {
                        FontResource fontResource = (FontResource) gameResource;
                        System.out.printf(" + Font%n     - Number of letters: %d%n", fontResource.getLetterMap().size());
                    }
                    case TEXT_RESOURCE -> {
                        TextResource textResource = (TextResource) gameResource;
                        System.out.println(" + Text");
                        textResource.getStrings().forEach(text -> System.out.println("     - " + text));
                    }
                    default -> throw new RuntimeException(format("Not implemented yet for %s", gameResource.getType()));
                }
            }
        });
    }

    /**
     * Writes the game resources to files.
     *
     * @param gameResourceMap the map of resources to write
     * @param dirToWrite the directory to write to
     * @throws IOException if there's an error during file operations
     */
    private static void writeToDirectory(Map<String, List<GameResource>> gameResourceMap, String dirToWrite) throws IOException {
        for (var entry : gameResourceMap.entrySet()) {
            String inputFilename = entry.getKey();
            List<GameResource> gameResourceList = entry.getValue();

            int i = 0;

            String filenameWithoutPath = inputFilename.substring(inputFilename.lastIndexOf("/") + 1);

            for (GameResource gameResource : gameResourceList) {
                String outFile;

                if (gameResource.isNameSet()) {
                    String utf8EncodedString = new String(gameResource.getName().strip().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                    outFile = format("%s/%s-%s-%d.png", dirToWrite, filenameWithoutPath, utf8EncodedString, i);
                } else {
                    outFile = format("%s/%s-%d.png", dirToWrite, filenameWithoutPath, i);
                }

                String outSoundFile = format("%s/%s-%d.wav", dirToWrite, filenameWithoutPath, i);
                i++;

                switch (gameResource.getType()) {
                    case BITMAP_RAW -> ((BitmapRawResource) gameResource).getBitmap().writeToFile(outFile);
                    case BITMAP_RESOURCE -> ((BitmapResource) gameResource).getBitmap().writeToFile(outFile);
                    case BITMAP_RLE -> ((BitmapRLEResource) gameResource).getBitmap().writeToFile(outFile);
                    case PALETTE_RESOURCE -> createBitmapFromPalette(((PaletteResource) gameResource).getPalette()).writeToFile(outFile);
                    case PLAYER_BITMAP_RESOURCE -> {
                        PlayerBitmap playerBitmap = ((PlayerBitmapResource) gameResource).getBitmap();

                        // Write the underlay only
                        playerBitmap.writeToFile(outFile);

                        // Write a version of the combined image once per player color
                        for (var playerColor : PlayerColor.values()) {
                            playerBitmap.getBitmapForPlayer(playerColor).writeToFile(
                                    format("%s/%s-%d (%s).png", dirToWrite, filenameWithoutPath, i, playerColor.name())
                            );
                        }

                        // Write the mask
                        playerBitmap.getTextureBitmap().writeToFile(format("%s/%s-%d (mask).png", dirToWrite, filenameWithoutPath, i));
                    }
                    case WAVE_SOUND -> ((WaveGameResource) gameResource).getWaveFile().writeToFile(outSoundFile);
                    case BOB_RESOURCE -> {
                        Bob bob = ((BobResource) gameResource).getBob();
                        int j = 0;
                        for (PlayerBitmap playerBitmap1 : bob.getAllBitmaps()) {
                            playerBitmap1.writeToFile(format("%s/%d-%s-%d.png", dirToWrite, j++, filenameWithoutPath, i));
                        }

                        StringBuilder linksAsStr = new StringBuilder();
                        int[] links = bob.getLinks();
                        for (int linkIndex = 0; linkIndex < links.length; linkIndex++) {
                            if (linkIndex % NUMBER_LINKS_PER_OVERLAY == 0) {
                                linksAsStr.append("# Job ID ").append(linkIndex / NUMBER_LINKS_PER_OVERLAY).append("\n");
                            }
                            linksAsStr.append(linkIndex).append(", ").append(links[linkIndex]).append("\n");
                        }
                        Files.writeString(Paths.get(dirToWrite, "links.txt"), linksAsStr.toString());
                    }
                    case LBM_RESOURCE -> ((LBMGameResource) gameResource).getLbmFile().writeToFile(outFile);
                    case FONT_RESOURCE -> createBitmapFromLetterMap(((FontResource) gameResource).getLetterMap()).writeToFile(outFile);
                    case TEXT_RESOURCE -> writeTextFile(format("%s/-%s-%d.txt", dirToWrite, filenameWithoutPath, i), ((TextResource) gameResource).getStrings());
                    default -> System.out.printf("Can't write this type to file: %s%n", gameResource.getType());
                }
            }
        }
    }

    /**
     * Writes the provided list of strings to a text file.
     *
     * @param outTextFile the file to write
     * @param strings the list of strings to write
     * @throws IOException if there's an error during file operations
     */
    private static void writeTextFile(String outTextFile, List<String> strings) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(outTextFile));

        for (String text : strings) {
            printWriter.println(text);
        }

        printWriter.flush();
        printWriter.close();
    }

    /**
     * Creates a bitmap from the provided letter map.
     *
     * @param letterMap the map of letter bitmaps
     * @return the created bitmap
     */
    private static Bitmap createBitmapFromLetterMap(Map<String, PlayerBitmap> letterMap) {
        int width = letterMap.values().stream().mapToInt(PlayerBitmap::getWidth).sum();
        int height = letterMap.values().stream().mapToInt(PlayerBitmap::getHeight).max().orElse(0);

        byte[] data = new byte[width * height * 4]; // Create a BGRA picture

        int offset = 0;
        for (PlayerBitmap bitmap : letterMap.values()) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                for (int x = 0; x < bitmap.getWidth(); x++) {
                    int pixelOffset = (y * width + offset + x) * 4;
                    data[pixelOffset] = bitmap.getBlueAsByte(x, y);
                    data[pixelOffset + 1] = bitmap.getGreenAsByte(x, y);
                    data[pixelOffset + 2] = bitmap.getRedAsByte(x, y);
                    data[pixelOffset + 3] = bitmap.getAlphaAsByte(x, y);
                }
            }

            offset += bitmap.getWidth();
        }

        Bitmap bitmap = new Bitmap(width, height, null, TextureFormat.BGRA);
        bitmap.setImageDataFromBuffer(data);

        return bitmap;
    }

    /**
     * Creates a bitmap from the given palette.
     *
     * @param palette the palette to create the bitmap from
     * @return the created bitmap
     */
    private static Bitmap createBitmapFromPalette(Palette palette) {
        byte[] data = new byte[palette.getNumberColors() * 4 * 10];

        for (int i = 0; i < 10; i++) {
            for (int x = 0; x < palette.getNumberColors(); x++) {
                RGBColor rgbColor = palette.getColorForIndex(x);

                data[(i * palette.getNumberColors() + x) * 4] = rgbColor.blue();
                data[(i * palette.getNumberColors() + x) * 4 + 1] = rgbColor.green();
                data[(i * palette.getNumberColors() + x) * 4 + 2] = rgbColor.red();
                data[(i * palette.getNumberColors() + x) * 4 + 3] = (byte)0xFF;
            }
        }

        Bitmap bitmap = new Bitmap(palette.getNumberColors(), 10, palette, TextureFormat.BGRA);
        bitmap.setImageDataFromBuffer(data);

        return bitmap;
    }

    private void loadDefaultPalette() throws IOException {
        this.palette = PaletteDecoder.loadPaletteFromFile(DEFAULT_PALETTE);
    }

    /**
     * Loads a file based on its type.
     *
     * @param filename the name of the file to load
     * @return a list of game resources from the file
     * @throws IOException if an error occurs while reading the file
     * @throws InvalidFormatException if the file format is invalid
     */
    private List<GameResource> loadFile(String filename) throws IOException, InvalidFormatException {
        int lastSeparator = filename.lastIndexOf("/");
        String filenameWithoutPath = filename.substring(lastSeparator + 1);

        String fileSuffix = filename.substring(filename.lastIndexOf('.') + 1);

        System.out.printf("Asset filename and path: %s", filename);
        System.out.printf("Asset filename: %s", filenameWithoutPath);
        System.out.printf("File type: %s", fileSuffix);

        switch (fileSuffix) {
            case "LST", "BOB" -> {
                try {
                    return LstDecoder.loadLstFile(filename, palette);
                } catch (Throwable t) {
                    System.out.printf("Failed to load %s", filename);
                    t.printStackTrace();
                }
            }
            case "LBM" -> {
                try {
                    return List.of(LbmDecoder.loadLBMFile(filename, palette));
                } catch (Throwable t) {
                    System.out.printf("Failed to load %s", filename);
                    t.printStackTrace();
                }
            }
            case "DAT" -> {
                try {
                    System.out.println("Loading dat file");
                    return DatDecoder.loadDatFile(filename, palette);
                } catch (Throwable t) {
                    System.out.printf("Failed to load %s", filename);
                    t.printStackTrace();
                }
            }
            case "BBM" -> {
                System.out.println("Loading BBM file");
                return BbmDecoder.loadBbmFile(filename);
            }
            case "GER", "ENG" -> {
                List<String> strings = TextDecoder.loadTextFromFile(filename);
                return List.of(new TextResource(strings));
            }
            default -> throw new RuntimeException(format("Not supporting %s", type));
        }

        return Collections.emptyList();
    }
}
