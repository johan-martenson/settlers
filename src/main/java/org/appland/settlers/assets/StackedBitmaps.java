package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.PlayerBitmap;

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

    public List<PlayerBitmap> getPlayerBitmaps() {
        return bitmaps;
    }
}
