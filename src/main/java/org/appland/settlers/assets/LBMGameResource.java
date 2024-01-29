package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.LBMFile;

public class LBMGameResource implements GameResource {
    private final LBMFile lbmFile;

    public LBMGameResource(LBMFile lbmFile) {
        this.lbmFile = lbmFile;
    }

    @Override
    public GameResourceType getType() {
        return GameResourceType.LBM_RESOURCE;
    }

    public LBMFile getLbmFile() {
        return lbmFile;
    }
}
