package org.appland.settlers.assets.test;

import org.appland.settlers.assets.*;
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBob {

    private static final String TEST_PALETTE = "src/test/resources/pal5.act";
    private static final String TEST_BOB_FILE = "src/test/resources/CARRIER.BOB";
    @Test
    public void testLoadBob() throws IOException, UnknownResourceTypeException, InvalidHeaderException, InvalidFormatException {
        AssetManager assetManager = new AssetManager();
        Palette palette = assetManager.loadPaletteFromFile(TEST_PALETTE);

        List<GameResource> gameResourceList = assetManager.loadLstFile(TEST_BOB_FILE, palette);

        assertEquals(gameResourceList.size(), 1);

        Bob bob = ((BobGameResource)gameResourceList.get(0)).getBob();

        assertEquals(bob.getNumberOverlayImages(), 602);
        assertEquals(bob.getNumberLinks(), 3264);
        assertEquals(bob.getAllBitmaps().size(), 698);

        for (PlayerBitmap bitmap : bob.getAllBitmaps()) {
            assertEquals(bitmap.getNx(), 16);
            assertEquals(bitmap.getWidth(), 32);
            assertTrue(bitmap.getHeight() > 0);
        }

        PlayerBitmap playerBitmap = bob.getAllBitmaps().get(0);

        assertEquals(playerBitmap.getNy(), 12);
        assertEquals(playerBitmap.getHeight(), 13);

        playerBitmap = bob.getAllBitmaps().get(1);

        assertEquals(playerBitmap.getNy(), 13);
        assertEquals(playerBitmap.getHeight(), 14);

        playerBitmap = bob.getAllBitmaps().get(697);

        assertEquals(playerBitmap.getNy(), 18);
        assertEquals(playerBitmap.getHeight(), 12);
    }
}
