package org.appland.settlers.assets.test;

import org.appland.settlers.assets.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        AssetManager assetManager = new AssetManager();

        assetManager.setWantedTextureFormat(TextureFormat.BGRA);

        BitmapFile bitmap = assetManager.loadBitmapFile(TEST_BITMAP_PAL, palette);

        assertEquals(bitmap.getWidth(), 94);
        assertEquals(bitmap.getHeight(), 63);
        assertEquals(bitmap.getSourceBitsPerPixel(), 8);
        assertEquals(bitmap.getBytesPerPixel(), 4);
        assertEquals(bitmap.getColorUsed(), 256);
        assertEquals(bitmap.getColorImp(), 0);
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
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        BitmapFile bitmap = assetManager.loadBitmapFile(TEST_BITMAP_LOGO, palette);

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
        assertEquals(bitmap.getColorUsed(), 16777216);
        assertEquals(bitmap.getColorImp(), 0);
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
        AssetManager assetManager = new AssetManager();
        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        assertNotNull(palette);
        assertEquals(palette.getName(), "pal5.act(0)");
        assertEquals(palette.getTransparentIndex(), 0);
        assertEquals(palette.getNumberColors(), 256);
    }

    @Test
    public void testLoadBbmPalette() throws IOException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();
        List<GameResource> gameResourceList = assetManager.loadBbmFile(TEST_BITMAP_PAL_BBM);

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
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        GameResource gameResource = assetManager.loadLBMFile(TEST_BITMAP_LBM, palette);

        LBMGameResource lbmGameResource = (LBMGameResource) gameResource;

        LBMFile lbmFile = lbmGameResource.getLbmFile();

        assertNotNull(lbmFile.getBitmap());

        Bitmap bitmap = lbmFile.getBitmap();

        assertEquals(bitmap.getWidth(), 94);
        assertEquals(bitmap.getHeight(), 63);
        assertEquals(bitmap.getBytesPerPixel(), 4);
        assertEquals(bitmap.getFormat(), TextureFormat.BGRA);

        assertNotNull(lbmFile.getPalette());
        assertNotNull(lbmFile.getPaletteAnimList());

        List<PaletteAnim> paletteAnimList = lbmFile.getPaletteAnimList();

        assertEquals(paletteAnimList.size(), 0);
    }

    @Test
    public void testLoadBitmapShadow() throws IOException, InvalidHeaderException, UnknownResourceTypeException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResources = assetManager.loadLstFile(TEST_BITMAP_SHADOW, palette);

        assertEquals(gameResources.size(), 1);

        BitmapResource playerBitmapResource = (BitmapResource) gameResources.get(0);
        Bitmap bitmap = playerBitmapResource.getBitmap();

        assertNotNull(bitmap);
        assertEquals(bitmap.getWidth(), 57);
        assertEquals(bitmap.getHeight(), 66);
    }

    @Test
    public void testLoadPlayerBitmap() throws IOException, InvalidHeaderException, UnknownResourceTypeException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResources = assetManager.loadLstFile(TEST_PLAYER_BITMAP, palette);

        assertEquals(gameResources.size(), 1);

        PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResources.get(0);
        PlayerBitmap playerBitmap = playerBitmapResource.getBitmap();

        assertNotNull(playerBitmap);
        assertEquals(playerBitmap.getWidth(), 40);
        assertEquals(playerBitmap.getHeight(), 21);
    }

    @Test
    public void testLoadBitmapRLE() throws IOException, UnknownResourceTypeException, InvalidHeaderException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);
        List<GameResource> resources = assetManager.loadLstFile(TEST_BITMAP_RLE, palette);

        assertEquals(resources.size(), 1);

        BitmapRLEResource bitmapRLEResource = (BitmapRLEResource) resources.get(0);

        assertNotNull(bitmapRLEResource);

        BitmapRLE bitmapRLE = bitmapRLEResource.getBitmap();

        assertNotNull(bitmapRLE);
        assertEquals(bitmapRLE.getWidth(), 20);
        assertEquals(bitmapRLE.getHeight(), 20);
    }

    @Test
    public void testLoadBitmapRaw() throws IOException, UnknownResourceTypeException, InvalidHeaderException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);
        List<GameResource> resources = assetManager.loadLstFile(TEST_BITMAP_RAW, palette);

        assertEquals(resources.size(), 1);

        BitmapRawResource bitmapRawResource = (BitmapRawResource) resources.get(0);

        assertNotNull(bitmapRawResource);

        BitmapRaw bitmapRaw = bitmapRawResource.getBitmap();

        assertNotNull(bitmapRaw);
        assertEquals(bitmapRaw.getWidth(), 50);
        assertEquals(bitmapRaw.getHeight(), 54);
        assertEquals(bitmapRaw.getLength(), 2700);
    }

    @Test
    public void testLoadLbmTex() throws IOException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        GameResource gameResource = assetManager.loadLBMFile(TEST_TEX_LBM, palette);

        assertNotNull(gameResource);

        LBMGameResource lbmGameResource = (LBMGameResource) gameResource;

        LBMFile lbmFile = lbmGameResource.getLbmFile();

        assertNotNull(lbmFile.getBitmap());

        Bitmap bitmap = lbmFile.getBitmap();

        assertEquals(bitmap.getWidth(), 256);
        assertEquals(bitmap.getHeight(), 256);
        assertEquals(bitmap.getBytesPerPixel(), 4);
        assertEquals(bitmap.getFormat(), TextureFormat.BGRA);

        assertNotNull(lbmFile.getPalette());
        assertNotNull(lbmFile.getPaletteAnimList());

        List<PaletteAnim> paletteAnimList = lbmFile.getPaletteAnimList();

        assertEquals(paletteAnimList.size(), 16);
    }

    @Test
    public void testLoadDatIdx() throws IOException, UnknownResourceTypeException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();

        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResourceList = assetManager.loadDatFile(TEST_DAT, palette);

        assertEquals(gameResourceList.size(), 1);

        GameResource gameResource = gameResourceList.get(0);

        FontGameResource fontGameResource = (FontGameResource) gameResource;

        Map<String, PlayerBitmap> letterMap = fontGameResource.getLetterMap();

        assertEquals(letterMap.size(), 123);
        assertTrue(letterMap.containsKey("U+8e"));
        assertNotNull(letterMap.get("U+83"));

        PlayerBitmap playerBitmap = letterMap.get("U+8e");

        assertEquals(playerBitmap.getWidth(), 11);
        assertEquals(playerBitmap.getHeight(), 11);
        assertEquals(playerBitmap.getLength(), 105);
    }
}
