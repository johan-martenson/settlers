package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UiIcon;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.UIElementsImageCollection;
import org.appland.settlers.assets.decoders.DatDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.IoDat;
import org.appland.settlers.assets.gamefiles.IoLst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.io.IOException;

import static org.appland.settlers.assets.Utils.getImageAt;
import static org.appland.settlers.model.Size.*;

public class IconsExtractor {

    public static void extractIcons(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);
        var ioLst = LstDecoder.loadLstFile(fromDir + "/" + IoLst.FILENAME, defaultPalette);
        var ioDat = DatDecoder.loadDatFile(fromDir + "/" + IoDat.FILENAME, defaultPalette);

        var collector = new UIElementsImageCollection();

        collector.addSelectedPointImage(getImageAt(mapBobsLst, MapBobsLst.SELECTED_POINT));
        collector.addHoverPoint(getImageAt(mapBobsLst, MapBobsLst.HOVER_POINT));
        collector.addHoverAvailableFlag(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_FLAG));
        collector.addHoverAvailableMine(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MINE));
        collector.addHoverAvailableBuilding(SMALL, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_SMALL_BUILDING));
        collector.addHoverAvailableBuilding(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MEDIUM_BUILDING));
        collector.addHoverAvailableBuilding(LARGE, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_LARGE_BUILDING));
        collector.addHoverAvailableHarbor(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_HARBOR));
        collector.addAvailableFlag(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_FLAG));
        collector.addAvailableMine(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_MINE));
        collector.addAvailableBuilding(SMALL, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_SMALL_BUILDING));
        collector.addAvailableBuilding(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_MEDIUM_BUILDING));
        collector.addAvailableBuilding(LARGE, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_LARGE_BUILDING));
        collector.addAvailableHarbor(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_HARBOR));

        collector.addUiElement(UiIcon.DESTROY_BUILDING, getImageAt(ioLst, IoLst.BURNING_HOUSE_ICON));
        collector.addUiElement(UiIcon.ATTACK, getImageAt(ioLst, IoLst.ATTACK_ICON));
        collector.addUiElement(UiIcon.SCISSORS, getImageAt(ioLst, IoLst.SCISSORS));
        collector.addUiElement(UiIcon.INFORMATION, getImageAt(ioLst, IoLst.INFORMATION));
        collector.addUiElement(UiIcon.GEOLOGIST, getImageAt(ioLst, IoLst.GEOLOGIST_ICON));

        for (var icon : IoDat.values()) {
            collector.addIcon(icon, ImageUtils.getBitmapFromResource(ioDat.get(icon.index)));
        }

        try (var in = IconsExtractor.class.getResourceAsStream("/pause.png")) {
            if (in == null) {
                throw new IOException("Resource not found: /pause.png");
            }
            var img = ImageIO.read(in);

            collector.addUiElement(UiIcon.PAUSE, ImageUtils.toBitmap(img));
        }

        try (var in = IconsExtractor.class.getResourceAsStream("/play.png")) {
            if (in == null) {
                throw new IOException("Resource not found: /play.png");
            }

            var img = ImageIO.read(in);

            collector.addUiElement(UiIcon.PLAY, ImageUtils.toBitmap(img));
        }

        collector.writeImageAtlas(toDir, defaultPalette);
    }
}
