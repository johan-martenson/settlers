package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bob;

public class BobResource extends GameResource {
    private final Bob bob;

    public BobResource(Bob bob) {
        this.bob = bob;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.BOB_RESOURCE;
    }

    public Bob getBob() {
        return bob;
    }
}
