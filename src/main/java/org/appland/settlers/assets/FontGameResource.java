package org.appland.settlers.assets;

import java.util.Map;

public class FontGameResource implements GameResource {
    private final Map<String, PlayerBitmap> letterMap;

    public FontGameResource(Map<String, PlayerBitmap> letterMap) {
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
