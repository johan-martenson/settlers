package org.appland.settlers.assets;

import java.util.List;

import static org.appland.settlers.assets.GameResourceType.TEXT_RESOURCE;

public class TextResource extends GameResource {
    private final List<String> strings;

    public TextResource(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public GameResourceType getType() {
        return TEXT_RESOURCE;
    }

    public List<String> getStrings() {
        return strings;
    }
}
