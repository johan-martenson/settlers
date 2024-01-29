package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.PlayerBitmap;

import static org.appland.settlers.assets.GameResourceType.PLAYER_BITMAP_RESOURCE;

public class PlayerBitmapResource implements GameResource {
    private final PlayerBitmap playerBitmap;

    public PlayerBitmapResource(PlayerBitmap playerBitmap) {
        this.playerBitmap = playerBitmap;
    }

    @Override
    public GameResourceType getType() {
        return PLAYER_BITMAP_RESOURCE;
    }

    public PlayerBitmap getBitmap() {
        return playerBitmap;
    }
}
