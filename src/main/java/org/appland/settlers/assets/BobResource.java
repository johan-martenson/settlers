package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bob;

/**
 * Represents a game resource containing a Bob object.
 */
public class BobResource extends GameResource {
    private final Bob bob;

    public BobResource(Bob bob) {
        this.bob = bob;
    }

    /**
     * Retrieves the type of the game resource.
     *
     * @return The resource type, which is {@link GameResourceType#BOB_RESOURCE}.
     */
    @Override
    public GameResourceType getType() {
        return GameResourceType.BOB_RESOURCE;
    }

    /**
     * Retrieves the Bob object.
     *
     * @return The {@link Bob} object representing the Bob resource.
     */
    public Bob getBob() {
        return bob;
    }
}
