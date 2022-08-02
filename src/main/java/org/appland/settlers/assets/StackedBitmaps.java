package org.appland.settlers.assets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class StackedBitmaps {
    private final List<PlayerBitmap> bitmaps;

    public StackedBitmaps() {
        bitmaps = new ArrayList<>();
    }

    public void add(PlayerBitmap body) {
        bitmaps.add(body);
    }

    public Bitmap getMergedBitmap() {

        /* Calculate the dimensions */
        Point origin = new Point(0, 0);
        Dimension size = new Dimension(0, 0);

        for (PlayerBitmap playerBitmap : bitmaps) {
            //origin.x = Math.max(origin.x, playerBitmap.g)
        }

        return null;
    }

    public List<PlayerBitmap> getBitmaps() {
        return bitmaps;
    }
}
