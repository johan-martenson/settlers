package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;

import java.io.IOException;
import java.util.List;

public class BreedingDonkeyCollection {
    private List<Bitmap> animation;

    public void addAnimation(List<Bitmap> animation) {
        this.animation = animation;
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        var imageBoard = new ImageBoard();

        imageBoard.placeImageSeriesBottom(animation, "animation");

        imageBoard.writeBoard(directory, "image-atlas-breeding-donkey", palette);
    }
}
