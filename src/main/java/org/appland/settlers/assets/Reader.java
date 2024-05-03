package org.appland.settlers.assets;

import org.appland.settlers.assets.decoders.BbmDecoder;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.decoders.PaletteDecoder;
import org.appland.settlers.assets.decoders.TextDecoder;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.BitmapRLE;
import org.appland.settlers.assets.resources.BitmapRaw;
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.LBMFile;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.resources.WaveFile;
import org.appland.settlers.assets.utils.SoundLoader;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.utils.ByteArrayReader;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

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

    public static void main(String[] args) throws CmdLineException, IOException, UnknownResourceTypeException, InvalidFormatException {
        Reader reader = new Reader();
        CmdLineParser parser = new CmdLineParser(reader);

        parser.parseArgument(args);

        /* Get the default palette */
        reader.loadDefaultPalette();

        /* Read the file */
        Map<String, List<GameResource>> gameResourceMap = new HashMap<>();
        if (!Objects.equals(assetFilename, "")) {
            gameResourceMap.put(assetFilename, reader.loadFile(assetFilename));

            System.out.println("Read file " + assetFilename);
            System.out.println("Loaded " + gameResourceMap.size() + " resources");
        }

        /* Read the directory */
        if (!Objects.equals(assetDir, "")) {
            gameResourceMap.putAll(reader.loadDirectory(assetDir, type));

            System.out.println("Read directory: " + assetDir);
            System.out.println(gameResourceMap);
        }

        /* Print information */
        if (printInfoSelected) {
            printInformation(gameResourceMap);
        }

        /* Write the resources to file */
        if (!Objects.equals(dirToWrite, "")) {
            writeToDirectory(gameResourceMap, dirToWrite);

            System.out.println("Wrote: " + dirToWrite);
        }
    }

    private Map<String, List<GameResource>> loadDirectory(String assetDir, String type) throws IOException, InvalidFormatException {

        Map<String, List<GameResource>> gameResourceMap = new HashMap<>();

        /* List all files */
        List<Path> paths = Files.find(Paths.get(assetDir),
                Integer.MAX_VALUE,
                (path, basicFileAttributes) -> path.toFile().getName().matches(".*." + type)
        ).toList();

        for (Path path : paths) {
            if (Files.isDirectory(path)) {
                continue;
            }

            String filename = path.toString();

            gameResourceMap.put(filename, loadFile(filename));
        }

        return gameResourceMap;
    }

    private static void printInformation(Map<String, List<GameResource>> gameResourceMap) {
        System.out.println();

        for (Map.Entry<String, List<GameResource>> entry : gameResourceMap.entrySet()) {
            String inputFile = entry.getKey();
            List<GameResource> gameResourceList = entry.getValue();

            System.out.println();
            System.out.println(" - File: " + inputFile);

            for (GameResource gameResource : gameResourceList) {

                switch (gameResource.getType()) {
                    case PLAYER_BITMAP_RESOURCE:
                        PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResource;
                        PlayerBitmap playerBitmap = playerBitmapResource.getBitmap();

                        System.out.println();
                        System.out.println(" + Player bitmap");
                        System.out.println("    - Width: " + playerBitmap.getWidth());
                        System.out.println("    - Height: " + playerBitmap.getHeight());
                        System.out.println("    - NX: " + playerBitmap.getNx());
                        System.out.println("    - NY: " + playerBitmap.getNy());
                        System.out.println("    - Length: " + playerBitmap.getLength());

                        break;
                    case PALETTE_RESOURCE:
                        PaletteResource paletteResource = (PaletteResource) gameResource;
                        Palette palette = paletteResource.getPalette();

                        System.out.println();
                        System.out.println(" + Palette");
                        System.out.println("    - Number colors: " + palette.getNumberColors());
                        System.out.println("    - Transparency index: " + palette.getTransparentIndex());

                        RGBColor rgbColor = palette.getColorForIndex(palette.getTransparentIndex());

                        System.out.println("    - Transparency color: " + rgbColor.red() + " (red), " + rgbColor.green() + " (green), " + rgbColor.blue() + " (blue");

                        break;
                    case BITMAP_RLE:
                        BitmapRLEResource bitmapRLEResource = (BitmapRLEResource) gameResource;
                        BitmapRLE bitmapRLE = bitmapRLEResource.getBitmap();

                        System.out.println();
                        System.out.println(" + RLE bitmap");
                        System.out.println("    - Width: " + bitmapRLE.getWidth());
                        System.out.println("    - Height: " + bitmapRLE.getHeight());

                        break;
                    case BITMAP_RESOURCE:
                        BitmapResource bitmapResource = (BitmapResource) gameResource;
                        Bitmap bitmap = bitmapResource.getBitmap();

                        System.out.println();
                        System.out.println(" + Bitmap");
                        System.out.println("    - Width: " + bitmap.getWidth());
                        System.out.println("    - Height: " + bitmap.getHeight());
                        System.out.println("    - Bits per pixel: " + bitmap.getBytesPerPixel());
                        System.out.println("    - Format: " + bitmap.getFormat());

                        break;

                    case WAVE_SOUND:
                        WaveGameResource waveGameResource = (WaveGameResource) gameResource;
                        WaveFile waveFile = waveGameResource.getWaveFile();

                        System.out.println();
                        System.out.println(" + Wave");
                        System.out.println("    - Format id: " + waveFile.getFormatId());
                        System.out.println("    - Format size: " + waveFile.getFormatSize());
                        System.out.println("    - Format tag: " + waveFile.getFormatTag());
                        System.out.println("    - Number channels: " + waveFile.getNumberChannels());
                        System.out.println("    - Samples per second: " + waveFile.getSamplesPerSecond());
                        System.out.println("    - Bytes per second: " + waveFile.getBytesPerSecond());
                        System.out.println("    - Frame size: " + waveFile.getFrameSize());
                        System.out.println("    - Bits per sample: " + waveFile.getBitsPerSample());
                        System.out.println("    - Data id: " + waveFile.getDataId());
                        System.out.println("    - Data size: " + waveFile.getDataSize());

                        break;

                    case BITMAP_RAW:
                        BitmapRawResource bitmapRawResource = (BitmapRawResource) gameResource;
                        Bitmap bitmapRaw = bitmapRawResource.getBitmap();

                        System.out.println();
                        System.out.println(" + Raw bitmap");
                        System.out.println("    - Width: " + bitmapRaw.getWidth());
                        System.out.println("    - Height: " + bitmapRaw.getHeight());
                        System.out.println("    - Format: " + bitmapRaw.getFormat());
                        System.out.println("    - Bits per pixel: " + bitmapRaw.getBytesPerPixel());

                        break;

                    case BOB_RESOURCE:
                        BobResource bobResource = (BobResource) gameResource;

                        Bob bob = bobResource.getBob();
                        System.out.println();
                        System.out.println(" + Bob");
                        System.out.println("    - Number of body images: " + bob.getNumberBodyImages());
                        System.out.println("    - Number of overlay images: " + bob.getNumberOverlayImages());

                        break;

                    case FONT_RESOURCE:

                        FontResource fontResource = (FontResource) gameResource;

                        Map<String, PlayerBitmap> letterMap = fontResource.getLetterMap();

                        System.out.println();
                        System.out.println(" + Font");
                        System.out.println("     - Number of letters: " + letterMap.size());

                        break;

                    case TEXT_RESOURCE:

                        TextResource textResource = (TextResource) gameResource;

                        System.out.println();
                        System.out.println(" + Text");

                        for (String text : textResource.getStrings()) {
                            System.out.println("     - " + text);
                        }

                        break;

                    default:
                        throw new RuntimeException("Not implemented yet for " + gameResource.getType());
                }
            }
        }
    }

    private static void writeToDirectory(Map<String, List<GameResource>> gameResourceMap, String dirToWrite) throws IOException {

        for (Map.Entry<String, List<GameResource>> entry : gameResourceMap.entrySet()) {
            String inputFilename = entry.getKey();
            List<GameResource> gameResourceList = entry.getValue();

            int i = 0;

            int lastSeparator = inputFilename.lastIndexOf("/");
            String filenameWithoutPath = inputFilename.substring(lastSeparator + 1);

            for (GameResource gameResource : gameResourceList) {

                String outFile;

                if (gameResource.isNameSet()) {
                    String rawString = gameResource.getName();
                    byte[] bytes = rawString.strip().getBytes(StandardCharsets.UTF_8);

                    String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
                    outFile = format("%s/%s-%s-%d.png", dirToWrite, filenameWithoutPath, utf8EncodedString, i);
                } else {
                    outFile = format("%s/%s-%d.png", dirToWrite, filenameWithoutPath, i);
                }

                String outSoundFile = format("%s/%s-%d.wav", dirToWrite, filenameWithoutPath, i);

                i = i + 1;

                switch (gameResource.getType()) {
                    case BITMAP_RAW:
                        BitmapRawResource bitmapRawResource = (BitmapRawResource) gameResource;
                        BitmapRaw bitmapRaw = bitmapRawResource.getBitmap();
                        bitmapRaw.writeToFile(outFile);
                        break;

                    case BITMAP_RESOURCE:
                        BitmapResource bitmapResource = (BitmapResource) gameResource;
                        Bitmap bitmap = bitmapResource.getBitmap();
                        bitmap.writeToFile(outFile);
                        break;

                    case BITMAP_RLE:
                        BitmapRLEResource bitmapRLEResource = (BitmapRLEResource) gameResource;
                        Bitmap bitmapRLE = bitmapRLEResource.getBitmap();
                        bitmapRLE.writeToFile(outFile);
                        break;

                    case PALETTE_RESOURCE:
                        PaletteResource paletteResource = (PaletteResource) gameResource;
                        Bitmap paletteBitmap = createBitmapFromPalette(paletteResource.getPalette());

                        paletteBitmap.writeToFile(outFile);

                        break;

                    case PLAYER_BITMAP_RESOURCE:
                        PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResource;
                        PlayerBitmap playerBitmap = playerBitmapResource.getBitmap();
                        playerBitmap.writeToFile(outFile);

                        for (var playerColor : PlayerColor.values()) {
                            playerBitmap.getBitmapForPlayer(playerColor).writeToFile(
                                    format("%s/%s-%d (%s).png", dirToWrite, filenameWithoutPath, i, playerColor.name())
                            );
                        }

                        playerBitmap.getTextureBitmap().writeToFile(
                                format("%s/%s-%d (mask).png", dirToWrite, filenameWithoutPath, i)
                        );

                        break;

                    case WAVE_SOUND:
                        WaveGameResource waveGameResource = (WaveGameResource) gameResource;
                        WaveFile waveFile = waveGameResource.getWaveFile();
                        waveFile.writeToFile(outSoundFile);

                        break;

                    case BOB_RESOURCE:
                        BobResource bobResource = (BobResource) gameResource;
                        Bob bob = bobResource.getBob();

                        int j = 0;

                        for (PlayerBitmap playerBitmap1 : bob.getAllBitmaps()) {
                            String outFile2 = dirToWrite + "/" + j + "-" + filenameWithoutPath + "-" + i + ".png";

                            playerBitmap1.writeToFile(outFile2);

                            j = j + 1;
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

                        break;

                    case LBM_RESOURCE:
                        LBMGameResource lbmGameResource = (LBMGameResource) gameResource;
                        LBMFile lbmFile = lbmGameResource.getLbmFile();

                        lbmFile.writeToFile(outFile);

                        break;

                    case FONT_RESOURCE:
                        FontResource fontResource = (FontResource) gameResource;
                        Bitmap fontBitmap = createBitmapFromLetterMap(fontResource.getLetterMap());

                        fontBitmap.writeToFile(outFile);

                        break;

                    case TEXT_RESOURCE:
                        TextResource textResource = (TextResource) gameResource;

                        String outTextFile = dirToWrite + "/" + "-" + filenameWithoutPath + "-" + i + ".txt";

                        writeTextFile(outTextFile, textResource.getStrings());

                        break;

                    default:
                        System.out.println("Can't write this type to file: " + gameResource.getType());
                }
            }
        }
    }

    private static void writeTextFile(String outTextFile, List<String> strings) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(outTextFile));

        for (String text : strings) {
            printWriter.println(text);
        }

        printWriter.flush();
        printWriter.close();
    }

    private static Bitmap createBitmapFromLetterMap(Map<String, PlayerBitmap> letterMap) {
        int width = 0;
        int height = 0;

        /* Summarize the width and find the max height */
        for (PlayerBitmap letterBitmap : letterMap.values()) {
            width = width + letterBitmap.getWidth();

            if (letterBitmap.getHeight() > height) {
                height = letterBitmap.getHeight();
            }
        }

        /* Create a BGRA picture */
        byte[] data = new byte[width * height * 4];

        int offset = 0;

        for (PlayerBitmap bitmap : letterMap.values()) {

            for (int y = 0; y < bitmap.getHeight(); y++) {
                for (int x = 0; x < bitmap.getWidth(); x++) {
                    data[(y * width + offset + x) * 4] = bitmap.getBlueAsByte(x, y);
                    data[(y * width + offset + x) * 4 + 1] = bitmap.getGreenAsByte(x, y);
                    data[(y * width + offset + x) * 4 + 2] = bitmap.getRedAsByte(x, y);
                    data[(y * width + offset + x) * 4 + 3] = bitmap.getAlphaAsByte(x, y);
                }
            }

            offset = offset + bitmap.getWidth();
        }

        Bitmap bitmap = new Bitmap(width, height, null, TextureFormat.BGRA);

        bitmap.setImageDataFromBuffer(data);

        return bitmap;
    }

    private static Bitmap createBitmapFromPalette(Palette palette) {

        /* Create a BGRA picture of dimensions #colors x 10 */
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

    private List<GameResource> loadFile(String filename) throws IOException, InvalidFormatException {

        int lastSeparator = filename.lastIndexOf("/");
        String filenameWithoutPath = filename.substring(lastSeparator + 1);

        int lastPeriod = filenameWithoutPath.lastIndexOf(".");
        String fileSuffix = filenameWithoutPath.substring(lastPeriod + 1);

        System.out.println("Asset filename and path: " + filename);
        System.out.println("Asset filename: " + filenameWithoutPath);
        System.out.println("File type: " + fileSuffix);

        switch (fileSuffix) {
            case "LST", "BOB" -> {
                try {
                    return LstDecoder.loadLstFile(filename, palette);
                } catch (Throwable t) {
                    System.out.println("Failed to load " + filename);
                    t.printStackTrace();
                }
            }
            case "LBM" -> {
                try {
                    GameResource gameResource = LbmDecoder.loadLBMFile(filename, palette);

                    List<GameResource> gameResourcesLbmList = new ArrayList<>();

                    gameResourcesLbmList.add(gameResource);

                    return gameResourcesLbmList;
                } catch (Throwable t) {
                    System.out.println("Failed to load " + filename);
                    t.printStackTrace();
                }
            }
            case "DAT" -> {

                try {
                    System.out.println("Loading as sound stream");

                    byte[] bytes = Files.newInputStream(Paths.get(filename)).readAllBytes();

                    GameResource gameResource = SoundLoader.loadSoundFromStream(new ByteArrayReader(bytes, ByteOrder.LITTLE_ENDIAN));

                    System.out.println(gameResource);

                    List<GameResource> result = new ArrayList<>();

                    result.add(gameResource);

                    return result;

                } catch (Throwable t) {
                    System.out.println("Failed to load " + filename);
                    t.printStackTrace();
                }
            }
            case "BBM" -> {

                System.out.println("Loading BBM file");
                List<GameResource> gameResourceList = BbmDecoder.loadBbmFile(filename);
                System.out.println("Got " + gameResourceList);

                return gameResourceList;
            }
            case "GER", "ENG" -> {

                List<String> strings = TextDecoder.loadTextFile(filename);

                List<GameResource> stringResourceList = new ArrayList<>();

                stringResourceList.add(new TextResource(strings));

                return stringResourceList;
            }
            default -> throw new RuntimeException("Not supporting " + type);
        }

        return null;
    }
}
