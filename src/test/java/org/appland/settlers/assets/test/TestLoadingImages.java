package org.appland.settlers.assets.test;

import org.appland.settlers.assets.*;
import org.appland.settlers.assets.decoders.BbmDecoder;
import org.appland.settlers.assets.decoders.BitmapDecoder;
import org.appland.settlers.assets.decoders.DatDecoder;
import org.appland.settlers.assets.decoders.LbmDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.decoders.PaletteDecoder;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.BitmapFile;
import org.appland.settlers.assets.resources.BitmapRLE;
import org.appland.settlers.assets.resources.BitmapRaw;
import org.appland.settlers.assets.resources.AnimatedLBMFile;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.AnimatedPalette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestLoadingImages {

    private static final String TEST_PLAYER_BITMAP = "src/test/resources/bmpPlayer.lst";
    private static final String TEST_BITMAP_RAW = "src/test/resources/bmpRaw.lst";
    private static final String TEST_PALETTE = "src/test/resources/pal5.act";
    private static final String TEST_BITMAP_RLE = "src/test/resources/bmpRLE.lst";
    private static final String TEST_BITMAP_SHADOW = "src/test/resources/bmpShadow.lst";
    private static final String TEST_BITMAP_LOGO = "src/test/resources/logo.bmp";
    private static final String TEST_BITMAP_PAL = "src/test/resources/pal.bmp";
    private static final String TEST_BITMAP_LBM = "src/test/resources/test.lbm";
    private static final String TEST_TEX_LBM = "src/test/resources/TEX5.LBM";
    private static final String TEST_BITMAP_PAL_BBM = "src/test/resources/pal.bbm";
    private static final String TEST_DAT = "src/test/resources/EDITRES.DAT";
    private static final String TEST_IDX = "src/test/resources/EDITRES.IDX";

    @Test
    public void loadPalBmpFile() throws IOException, InvalidFormatException {
        Palette palette = createFakePalette();

        BitmapFile bitmap = BitmapDecoder.loadBitmapFile(TEST_BITMAP_PAL, palette, Optional.empty());

        assertEquals(bitmap.getWidth(), 94);
        assertEquals(bitmap.getHeight(), 63);
        assertEquals(bitmap.getSourceBitsPerPixel(), 8);
        assertEquals(bitmap.getBytesPerPixel(), 4);
        assertEquals(bitmap.getColorsUsed(), 256);
        assertEquals(bitmap.getImportantColors(), 0);
        assertEquals(bitmap.getPlanes(), 1);
        assertEquals(bitmap.getXPixelsPerM(), 2834);
        assertEquals(bitmap.getYPixelsPerM(), 2834);
        assertEquals(bitmap.getCompression(), 0);
        assertEquals(bitmap.getFileSize(), 7126);
        assertEquals(bitmap.getHeaderSize(), 40);
        assertEquals(bitmap.getPixelOffset(), 1078);
        assertEquals(bitmap.getImageData().length, 23688);
    }

    @Test
    public void loadLogoFile() throws InvalidFormatException, IOException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        BitmapFile bitmap = BitmapDecoder.loadBitmapFile(TEST_BITMAP_LOGO, palette, Optional.empty());

        assertNotNull(bitmap);
        assertEquals(bitmap.getWidth(), 50);
        assertEquals(bitmap.getHeight(), 54);
        assertEquals(bitmap.getFileSize(), 8262);
        assertEquals(bitmap.getReserved(), 0);
        assertEquals(bitmap.getPixelOffset(), 54);
        assertEquals(bitmap.getHeaderSize(), 40);
        assertEquals(bitmap.getPlanes(), 1);
        assertEquals(bitmap.getSourceBitsPerPixel(), 24);
        assertEquals(bitmap.getCompression(), 0);
        assertEquals(bitmap.getSize(), 0);
        assertEquals(bitmap.getXPixelsPerM(), 2834);
        assertEquals(bitmap.getYPixelsPerM(), 2834);
        assertEquals(bitmap.getColorsUsed(), 16777216);
        assertEquals(bitmap.getImportantColors(), 0);
    }

    private Palette createFakePalette() {
        byte[] colors = new byte[3 * 256];

        for (int i = 0; i < 256; i++) {
            int offset = i * 3;

            colors[offset] = (byte)(i & 0xFF);
            colors[offset + 1] = (byte)(i & 0xFF);
            colors[offset + 2] = (byte)(i & 0xFF);
        }

        return new Palette(colors);
    }

    @Test
    public void testLoadPalette() throws IOException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        assertNotNull(palette);
        assertEquals(palette.getName(), "pal5.act(0)");
        assertEquals(palette.getTransparentIndex(), 0);
        assertEquals(palette.getNumberColors(), 256);
    }

    @Test
    public void testLoadBbmPalette() throws IOException, InvalidFormatException {
        List<GameResource> gameResourceList = BbmDecoder.loadBbmFile(TEST_BITMAP_PAL_BBM);

        assertEquals(gameResourceList.size(), 2);

        PaletteResource paletteResource1 = (PaletteResource) gameResourceList.get(0);
        PaletteResource paletteResource2 = (PaletteResource) gameResourceList.get(1);

        Palette palette1 = paletteResource1.getPalette();
        Palette palette2 = paletteResource2.getPalette();

        assertEquals(palette1.getName(), "pal.bbm(0)");
        assertEquals(palette2.getName(), "pal.bbm(1)");
        assertEquals(palette1.getTransparentIndex(), 0);
        assertEquals(palette2.getTransparentIndex(), 0);
        assertEquals(palette1.getNumberColors(), 256);
        assertEquals(palette2.getNumberColors(), 256);
    }

    @Test
    public void testLoadBitmapLbm() throws IOException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        GameResource gameResource = LbmDecoder.loadLBMFile(TEST_BITMAP_LBM, palette);

        LBMGameResource lbmGameResource = (LBMGameResource) gameResource;

        AnimatedLBMFile lbmFile = lbmGameResource.getLbmFile();

        assertNotNull(lbmFile);

        assertEquals(lbmFile.getWidth(), 94);
        assertEquals(lbmFile.getHeight(), 63);
        assertEquals(lbmFile.getBytesPerPixel(), 4);
        assertEquals(lbmFile.getFormat(), TextureFormat.BGRA);

        assertNotNull(lbmFile.getPalette());
        assertNotNull(lbmFile.getPaletteAnimations());

        List<AnimatedPalette> animatedPaletteList = lbmFile.getPaletteAnimations();

        assertEquals(animatedPaletteList.size(), 0);
    }

    @Test
    public void testLoadBitmapShadow() throws IOException, UnknownResourceTypeException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResources = LstDecoder.loadLstFile(TEST_BITMAP_SHADOW, palette);

        assertEquals(gameResources.size(), 1);

        BitmapResource playerBitmapResource = (BitmapResource) gameResources.getFirst();
        Bitmap bitmap = playerBitmapResource.getBitmap();

        assertNotNull(bitmap);
        assertEquals(bitmap.getWidth(), 57);
        assertEquals(bitmap.getHeight(), 66);
    }

    @Test
    public void testLoadPlayerBitmap() throws IOException, UnknownResourceTypeException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResources = LstDecoder.loadLstFile(TEST_PLAYER_BITMAP, palette);

        assertEquals(gameResources.size(), 1);

        PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResources.getFirst();
        PlayerBitmap playerBitmap = playerBitmapResource.getBitmap();

        assertNotNull(playerBitmap);
        assertEquals(playerBitmap.getWidth(), 40);
        assertEquals(playerBitmap.getHeight(), 21);
    }

    @Test
    public void testLoadBitmapRLE() throws IOException, UnknownResourceTypeException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);
        List<GameResource> resources = LstDecoder.loadLstFile(TEST_BITMAP_RLE, palette);

        assertEquals(resources.size(), 1);

        BitmapRLEResource bitmapRLEResource = (BitmapRLEResource) resources.getFirst();

        assertNotNull(bitmapRLEResource);

        BitmapRLE bitmapRLE = bitmapRLEResource.getBitmap();

        assertNotNull(bitmapRLE);
        assertEquals(bitmapRLE.getWidth(), 20);
        assertEquals(bitmapRLE.getHeight(), 20);
    }

    @Test
    public void testLoadBitmapRaw() throws IOException, UnknownResourceTypeException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);
        List<GameResource> resources = LstDecoder.loadLstFile(TEST_BITMAP_RAW, palette);

        assertEquals(resources.size(), 1);

        BitmapRawResource bitmapRawResource = (BitmapRawResource) resources.getFirst();

        assertNotNull(bitmapRawResource);

        BitmapRaw bitmapRaw = bitmapRawResource.getBitmap();

        assertNotNull(bitmapRaw);
        assertEquals(bitmapRaw.getWidth(), 50);
        assertEquals(bitmapRaw.getHeight(), 54);
        assertEquals(bitmapRaw.getLength(), 2700);
    }

    @Test
    public void testLoadLbmTex() throws IOException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        GameResource gameResource = LbmDecoder.loadLBMFile(TEST_TEX_LBM, palette);

        assertNotNull(gameResource);

        LBMGameResource lbmGameResource = (LBMGameResource) gameResource;

        AnimatedLBMFile lbmFile = lbmGameResource.getLbmFile();

        assertEquals(lbmFile.getWidth(), 256);
        assertEquals(lbmFile.getHeight(), 256);
        assertEquals(lbmFile.getBytesPerPixel(), 4);
        assertEquals(lbmFile.getFormat(), TextureFormat.BGRA);

        assertNotNull(lbmFile.getPalette());
        assertNotNull(lbmFile.getPaletteAnimations());

        List<AnimatedPalette> animatedPaletteList = lbmFile.getPaletteAnimations();

        assertEquals(animatedPaletteList.size(), 16);
    }

    @Test
    public void testLoadDatIdx() throws IOException, UnknownResourceTypeException, InvalidFormatException {
        Palette palette = PaletteDecoder.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResourceList = DatDecoder.loadDatFile(TEST_DAT, palette);

        assertEquals(gameResourceList.size(), 1);

        GameResource gameResource = gameResourceList.getFirst();

        FontResource fontResource = (FontResource) gameResource;

        Map<String, PlayerBitmap> letterMap = fontResource.getLetterMap();

        assertEquals(letterMap.size(), 123);
        assertTrue(letterMap.containsKey("U+8e"));
        assertNotNull(letterMap.get("U+83"));

        PlayerBitmap playerBitmap = letterMap.get("U+8e");

        assertEquals(playerBitmap.getWidth(), 11);
        assertEquals(playerBitmap.getHeight(), 11);
        assertEquals(playerBitmap.getLength(), 105);
    }
}
