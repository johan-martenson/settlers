package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bob;

public class BobGameResource implements GameResource {
    private final Bob bob;

    public BobGameResource(Bob bob) {
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
