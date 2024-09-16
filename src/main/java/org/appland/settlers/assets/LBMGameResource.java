package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.AnimatedLBMFile;

public class LBMGameResource extends GameResource {
    private final AnimatedLBMFile lbmFile;

    public LBMGameResource(AnimatedLBMFile lbmFile) {
        this.lbmFile = lbmFile;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.LBM_RESOURCE;
    }

    public AnimatedLBMFile getLbmFile() {
        return lbmFile;
    }
}
