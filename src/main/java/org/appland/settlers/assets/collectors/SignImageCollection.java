package org.appland.settlers.assets.collectors;

import org.appland.settlers.assets.SignType;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageBoard;
import org.appland.settlers.model.Size;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class SignImageCollection {
    private final Map<SignType, Map<Size, Bitmap>> signTypeToImageMap;

    private Bitmap shadowImage;

    public SignImageCollection() {
        signTypeToImageMap = new EnumMap<>(SignType.class);

        Arrays.asList(SignType.values()).forEach(signType -> this.signTypeToImageMap.put(signType, new EnumMap<>(Size.class)));
    }

    public void addImage(SignType signType, Size size, Bitmap image) {
        this.signTypeToImageMap.get(signType).put(size, image);
    }

    public void writeImageAtlas(String directory, Palette palette) throws IOException {
        ImageBoard imageBoard = new ImageBoard();

        signTypeToImageMap
                .forEach((signType, sizeBitmapMap) -> sizeBitmapMap
                        .forEach((size, image) -> imageBoard.placeImageRightOf(
                                image,
                                signType.name().toUpperCase(),
                                size.name().toUpperCase())));

        imageBoard.placeImageBottom(shadowImage, "shadowImage");

        imageBoard.writeBoard(directory, "image-atlas-signs", palette);
    }

    public void addShadowImage(Bitmap shadowImage) {
        this.shadowImage = shadowImage;
    }
}
