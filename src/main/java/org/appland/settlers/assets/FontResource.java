package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.PlayerBitmap;

import java.util.Map;

public class FontResource extends GameResource {
    private final Map<String, PlayerBitmap> letterMap;

    public FontResource(Map<String, PlayerBitmap> letterMap) {
        this.letterMap = letterMap;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.FONT_RESOURCE;
    }

    public Map<String, PlayerBitmap> getLetterMap() {
        return letterMap;
    }
}
